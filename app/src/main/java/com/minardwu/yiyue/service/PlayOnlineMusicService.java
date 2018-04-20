package com.minardwu.yiyue.service;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.constants.Actions;
import com.minardwu.yiyue.constants.NetWorkType;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.event.StopPlayLocalMusicServiceEvent;
import com.minardwu.yiyue.event.StopPlayOnlineMusicServiceEvent;
import com.minardwu.yiyue.event.UpdateOnlineMusicListPositionEvent;
import com.minardwu.yiyue.http.result.FailResult;
import com.minardwu.yiyue.http.GetOnlineSong;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.receiver.NoisyAudioStreamReceiver;
import com.minardwu.yiyue.utils.NetWorkUtils;
import com.minardwu.yiyue.utils.Notifier;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 音乐播放后台服务
 */
public class PlayOnlineMusicService extends PlayService implements MediaPlayer.OnCompletionListener, OnPlayOnlineMusicListener {

    private static final String TAG = "PlayOnlineMusicService";
    private static final long TIME_UPDATE = 100L;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PLAYING = 2;
    private static final int STATE_PAUSE = 3;

    private int playState = STATE_IDLE;
    private boolean isPlayList = false;
    private Random random = new Random(System.currentTimeMillis());
    private List<MusicBean> onlineMusicPlayList;
    private int playPosition;
    private String playingMusicId="null";

    private final NoisyAudioStreamReceiver noisyReceiver = new NoisyAudioStreamReceiver();//广播在start的时候注册，pause的时候注销
    private final IntentFilter noisyFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private AudioFocusManager audioFocusManager;//音乐焦点管理
    private MediaSessionManager mediaSessionManager;//媒体播放时界面和服务通讯
    private MusicBean playingMusic;

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private OnPlayOnlineMusicListener playOnlineMusicListener;
    private final Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        setPlayOnlineMusicListener(this);
        mediaPlayer.setOnCompletionListener(this);
        EventBus.getDefault().register(this);
        onlineMusicPlayList = new ArrayList<MusicBean>();
        audioFocusManager = new AudioFocusManager();
        mediaSessionManager = new MediaSessionManager();
    }

    public void setPlayOnlineMusicListener(OnPlayOnlineMusicListener playOnlineMusicListener) {
        this.playOnlineMusicListener = playOnlineMusicListener;
    }

    public void playOrPause(int id) {
        if (isPreparing()) {
            stop();
        } else if (isPlaying()) {
            pause();
        } else if (isPausing()) {
            start();
        } else {
            play(id);
        }
    }

    public void playMusicList(int positon){
        stop();
        playPosition = positon;
        play((int)onlineMusicPlayList.get(positon).getId());
    }

    public void playMusicList(List<MusicBean> list){
        replaceMusicList(list);
        stop();
        playPosition = 0;
        play((int)list.get(0).getId());
    }

    public void replaceMusicList(List<MusicBean> list){
        isPlayList = true;
        Preferences.savePlayOnlineList(true);
        onlineMusicPlayList.clear();
        onlineMusicPlayList.addAll(list);
        playOnlineMusicListener.onUpdateOnlineMusicList(onlineMusicPlayList);
    }

    public void appendMusicList(List<MusicBean> list){
        isPlayList = true;
        Preferences.savePlayOnlineList(true);
        for(MusicBean musicBean:list){
            if(!onlineMusicPlayList.contains(musicBean)){
                onlineMusicPlayList.add(musicBean);
            }
        }
        playOnlineMusicListener.onUpdateOnlineMusicList(onlineMusicPlayList);
    }

    public void deleteMusic(MusicBean musicBean){
        for(int i=0;i<onlineMusicPlayList.size();i++){
            if (onlineMusicPlayList.get(i).getId() == musicBean.getId()){
                onlineMusicPlayList.remove(i);
            }
        }
        playOnlineMusicListener.onUpdateOnlineMusicList(onlineMusicPlayList);
    }

    public void clearMusicList(){
        isPlayList = false;
        onlineMusicPlayList.clear();
        playOnlineMusicListener.onUpdateOnlineMusicList(onlineMusicPlayList);
    }

    public List<MusicBean> getMusicList(){
        return onlineMusicPlayList;
    }

    public void updatePlayingMusicPosition(int position){
        this.playPosition = position;
    }

    public String getPlayingMusicId(){
        return playingMusicId;
    }

    public void play(int id){
        playOnlineMusicListener.onPrepareStart();
        if(Preferences.enablePlayWhenOnlyHaveWifi()){
            if(NetWorkUtils.getNetWorkType(this) != NetWorkType.WIFI){
                ToastUtils.showShortToast(UIUtils.getString(R.string.wifi_tips));
                playOnlineMusicListener.onPrepareStop();
                return;
            }
        }
        if(NetWorkUtils.getNetWorkType(this) == NetWorkType.NO_NET){
            ToastUtils.showShortToast(UIUtils.getString(R.string.network_error));
            playOnlineMusicListener.onPrepareStop();
            return;
        }
        Log.e(TAG,"id:"+id);
        playingMusicId = id+"";
        mediaPlayer.reset();
        new GetOnlineSong() {
            @Override
            public void onSuccess(MusicBean musicBean) {
                playOnlineMusicListener.onPrepareStop();
                playingMusic = musicBean;
                playOnlineMusicListener.onChangeMusic(musicBean);
                try {
                    mediaPlayer.setDataSource(musicBean.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.setOnPreparedListener(preparedListener);
                mediaPlayer.prepareAsync();//在线播放音频，使用prepareAsync()
                setPlayState(STATE_PREPARING);
            }

            @Override
            public void onFail(FailResult result) {
                playOnlineMusicListener.onPrepareStop();
                playOnlineMusicListener.onGetSongError(result.getResultCode());
                Log.e(TAG,result.getException());
            }
        }.exectue(id);//114533
    }

    MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer player) {
            start();
        }
    };

    public void next(){
        handler.removeCallbacks(updateProgressRunable);
        playOnlineMusicListener.onPublish(0);
        if(isPlayList){
            if(playPosition == onlineMusicPlayList.size()-1){
                playPosition = 0;
            }else {
                playPosition = playPosition +1;
            }
            play((int)onlineMusicPlayList.get(playPosition).getId());
            EventBus.getDefault().post(new UpdateOnlineMusicListPositionEvent());
        }else {
            play(random.nextInt(100000)+60000);
            EventBus.getDefault().post(new UpdateOnlineMusicListPositionEvent());
        }
        mediaSessionManager.release();//不release再重新创建的话更新不了ui
    }

    void start(){
        if (!isPreparing() && !isPausing()) {
            return;
        }
        //如果正在播放本地音乐则暂停
        if(AppCache.getPlayLocalMusicService().isPlaying()){
            EventBus.getDefault().post(new StopPlayLocalMusicServiceEvent(1));
        }
        if(audioFocusManager.requestAudioFocus()){
            mediaPlayer.start();
            setPlayState(STATE_PLAYING);
            handler.post(updateProgressRunable);
            playOnlineMusicListener.onPlayerStart();
            Notifier.showPlay(playingMusic);
            AppCache.setCurrentService(this);
            MyDatabaseHelper.init(this).addFMHistory(playingMusic);
            mediaSessionManager = new MediaSessionManager();
            mediaSessionManager.updateMetaData(playingMusic);
            mediaSessionManager.updatePlaybackState();
            registerReceiver(noisyReceiver, noisyFilter);
        }
    }

    void pause() {
        if (!isPlaying()) {
            return;
        }
        mediaSessionManager.updatePlaybackState();
        mediaPlayer.pause();
        setPlayState(STATE_PAUSE);
        handler.removeCallbacks(updateProgressRunable);
        audioFocusManager.abandonAudioFocus();
        Notifier.showPause(playingMusic);
        unregisterReceiver(noisyReceiver);
        if (playOnlineMusicListener != null) {
            playOnlineMusicListener.onPlayerPause();
        }
    }

    public void pauseForHideNotification(){
        if (!isPlaying()) {
            return;
        }
        mediaSessionManager.updatePlaybackState();
        mediaPlayer.pause();
        setPlayState(STATE_PAUSE);
        handler.removeCallbacks(updateProgressRunable);
        unregisterReceiver(noisyReceiver);
        if (playOnlineMusicListener != null) {
            playOnlineMusicListener.onPlayerPause();
        }
    }

    public void stop(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {//在播放状态才可以停止播放
            pause();
            mediaPlayer.reset();
            handler.removeCallbacks(updateProgressRunable);
            setPlayState(STATE_IDLE);
        }
    }

    public long getCurrentMusicDuration(){
        return mediaPlayer.getDuration();
    }

    public void setPlayState(int i){
        playState = i;
    }

    public boolean isPlaying() {
        return playState == STATE_PLAYING;
    }

    public boolean isPausing() {
        return playState == STATE_PAUSE;
    }

    public boolean isPreparing() {
        return playState == STATE_PREPARING;
    }

    public boolean isIdle() {
        return playState == STATE_IDLE;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    private float progress;
    private Runnable updateProgressRunable = new Runnable() {
        @Override
        public void run() {
            progress = (float) mediaPlayer.getCurrentPosition() /(float)mediaPlayer.getDuration();
            playOnlineMusicListener.onPublish(progress);
            handler.postDelayed(updateProgressRunable,TIME_UPDATE);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StopPlayOnlineMusicServiceEvent event) {
        if(event.getFlag()==1){
            pause();
        }
    }

    public static void doCommand(Context context, String action) {
        Intent intent = new Intent(context, PlayOnlineMusicService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case Actions.ACTION_MEDIA_PLAY_PAUSE:
                    playOrPause();
                    break;
                case Actions.ACTION_MEDIA_NEXT:
                    next();
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    /**
     * 播放完自动切换下一首
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    /**
     * 获取正在播放的歌曲
     */
    public MusicBean getPlayingMusic() {
        return playingMusic;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        audioFocusManager.abandonAudioFocus();
        mediaSessionManager.release();
        Notifier.cancelAll();
        AppCache.setPlayOnlineMusicService(null);
        Log.i(TAG, "onDestroy: " + getClass().getSimpleName());
    }

    /**
     * 定时停播时执行操作
     */
    public void quit() {
        pause();
        QuitTimer.getInstance().stop();
        Preferences.saveStopTime(0);
    }

    /**
     * 定时停播，且选择了播放完当前歌曲结束再退出时执行的操作
     */
    public void quitWhenSongEnd(){
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                quit();//利用onCompletion设置播放完后暂停，但是接下来要把onCompletion重新设置为默认
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        next();
                    }
                });
            }
        });
    }

    /**
     * 定时停播，且选择了播放完当前歌曲结束再退出的情况
     * 若此时计时已为0，但最后一首歌还没有结束，用户若点击了不启用，则重设onCompletion为默认
     */
    public void resetOnCompletion(){
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                next();
            }
        });
    }

    @Override
    public void onPrepareStart() {

    }

    @Override
    public void onPrepareStop() {

    }

    @Override
    public void onChangeMusic(MusicBean music) {

    }

    @Override
    public void onPlayerStart() {

    }

    @Override
    public void onPlayerPause() {

    }

    @Override
    public void onPublish(float progress) {

    }

    @Override
    public void onBufferingUpdate(int percent) {

    }

    @Override
    public void onTimer(long remain) {

    }

    @Override
    public void onUpdatePosition(int position,String artistId) {

    }

    @Override
    public void onGetSongError(int resultCode) {

    }

    @Override
    public void onUpdateOnlineMusicList(List<MusicBean> list) {

    }

    public class PlayBinder extends Binder {
        public PlayOnlineMusicService getService() {
            return PlayOnlineMusicService.this;
        }
    }
}
