package com.minardwu.yiyue.service;

import android.app.Service;
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

import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.constants.Actions;
import com.minardwu.yiyue.constants.NetWorkType;
import com.minardwu.yiyue.event.StopPlayLocalMusicServiceEvent;
import com.minardwu.yiyue.event.StopPlayOnlineMusicServiceEvent;
import com.minardwu.yiyue.event.UpdateOnlineMusicListPositionEvent;
import com.minardwu.yiyue.http.GetOnlineSong;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.receiver.NoisyAudioStreamReceiver;
import com.minardwu.yiyue.utils.NetWorkUtils;
import com.minardwu.yiyue.utils.Notifier;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 音乐播放后台服务
 */
public class PlayOnlineMusicService extends Service implements MediaPlayer.OnCompletionListener, OnPlayOnlineMusicListener {
    private static final String TAG = "PlayOnlineMusicService";
    private static final long TIME_UPDATE = 100L;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PLAYING = 2;
    private static final int STATE_PAUSE = 3;
    private int playState = STATE_IDLE;//初始化状态
    private boolean randomPlay = true;//初始化状态
    private Random random = new Random(System.currentTimeMillis());
    private List<Integer> targetListIds = new ArrayList<Integer>();
    private int playPosintion;

    private final NoisyAudioStreamReceiver noisyReceiver = new NoisyAudioStreamReceiver();//广播在start的时候注册，pause的时候注销
    private final IntentFilter noisyfilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

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
//        audioFocusManager = new AudioFocusManager(this);
//        mediaSessionManager = new MediaSessionManager(this);
    }

    public void setPlayOnlineMusicListener(OnPlayOnlineMusicListener playOnlineMusicListener) {
        this.playOnlineMusicListener = playOnlineMusicListener;
    }

    public void playOrPause() {
        if (isPreparing()) {
            stop();
        } else if (isPlaying()) {
            pause();
        } else if (isPausing()) {
            start();
        } else {
            next();
        }
    }

    public void playTargetList(List<MusicBean> list,int position){
        for(MusicBean musicBean:list)
            targetListIds.add((int)musicBean.getId());
        randomPlay = false;
        play(targetListIds.get(position));
        playPosintion = position;
    }

    public void playRandom(){
        randomPlay = true;
        //play(random.nextInt(100000)+60000);
    }

    public void play(int id){
        if(Preferences.enablePlayWhenOnlyHaveWifi()){
            if(NetWorkUtils.getNetWorkType(this) != NetWorkType.WIFI){
                ToastUtils.show("当前无WiFi，若想播放请关闭开关");
                return;
            }
        }
        if(NetWorkUtils.getNetWorkType(this) == NetWorkType.NO_NET){
            ToastUtils.show("当前无网络，请检查网络连接");
            return;
        }
        Log.e(TAG,"playstart:"+id);
        mediaPlayer.reset();
        new GetOnlineSong() {
            @Override
            public void onSuccess(MusicBean musicBean) {
                playingMusic = musicBean;
                playOnlineMusicListener.onChangeMusic(musicBean);
                Log.e(TAG,"sucess");
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
            public void onFail(String string) {
                Log.e("GetOnlineSong","播放出错了"+string);
            }
        }.exectue(id);
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
        if(!randomPlay){
            if(playPosintion== targetListIds.size()-1){
                play(targetListIds.get(0));
                playPosintion = 0;
            }else {
                play(targetListIds.get(playPosintion+1));
                playPosintion = playPosintion+1;
            }
            EventBus.getDefault().post(new UpdateOnlineMusicListPositionEvent(getPlayingMusic().getArtistId(),playPosintion));
        }else {
            play(random.nextInt(100000)+60000);
        }
    }

    void start(){
        if (!isPreparing() && !isPausing()) {
            return;
        }
        //如果正在播放本地音乐则暂停
        if(AppCache.getPlayService().isPlaying()){
            EventBus.getDefault().post(new StopPlayLocalMusicServiceEvent(1));
        }
        mediaPlayer.start();
        setPlayState(STATE_PLAYING);
        handler.post(updateProgressRunable);
        playOnlineMusicListener.onPlayerStart();
        Notifier.showPlay(playingMusic);
        registerReceiver(noisyReceiver,noisyfilter);
    }

    void pause() {
        if (!isPlaying()) {
            return;
        }
        mediaPlayer.pause();
        setPlayState(STATE_PAUSE);
        handler.removeCallbacks(updateProgressRunable);
        Notifier.showPause(playingMusic);
//        mediaSessionManager.updatePlaybackState();
        unregisterReceiver(noisyReceiver);
        if (playOnlineMusicListener != null) {
            playOnlineMusicListener.onPlayerPause();
        }
    }

    public void pauseForHideNotifition(){
        if (!isPlaying()) {
            return;
        }
        mediaPlayer.pause();
        setPlayState(STATE_PAUSE);
        handler.removeCallbacks(updateProgressRunable);
//        mediaSessionManager.updatePlaybackState();
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
//        audioFocusManager.abandonAudioFocus();
//        mediaSessionManager.release();
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

    public class PlayBinder extends Binder {
        public PlayOnlineMusicService getService() {
            return PlayOnlineMusicService.this;
        }
    }
}
