package com.minardwu.yiyue.service;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Random;


import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.constants.Actions;
import com.minardwu.yiyue.enums.PlayModeEnum;
import com.minardwu.yiyue.event.StopPlayLocalMusicServiceEvent;
import com.minardwu.yiyue.event.StopPlayOnlineMusicServiceEvent;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.receiver.NoisyAudioStreamReceiver;
import com.minardwu.yiyue.utils.MusicUtils;
import com.minardwu.yiyue.utils.Notifier;
import com.minardwu.yiyue.utils.Preferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 音乐播放后台服务
 */
public class PlayLocalMusicService extends PlayService implements MediaPlayer.OnCompletionListener {
    private static final String TAG = "Service";
    private static final long TIME_UPDATE = 300L;

    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PLAYING = 2;
    private static final int STATE_PAUSE = 3;

    private final NoisyAudioStreamReceiver noisyReceiver = new NoisyAudioStreamReceiver();
    private final IntentFilter noisyFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private final Handler handler = new Handler();

    private MediaPlayer mediaPlayer = new MediaPlayer();
    //音乐焦点管理
    private AudioFocusManager audioFocusManager;
    //媒体播放时界面和服务通讯
    private MediaSessionManager mediaSessionManager;
    private OnPlayLocalMusicListener onPlayerEventListener;

    //正在播放的歌曲[本地|网络]
    private MusicBean playingMusic;
    //正在播放的本地歌曲的序号
    private int playingPosition = 0;
    //状态
    private int playState = STATE_IDLE;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: " + getClass().getSimpleName());
        EventBus.getDefault().register(this);
        audioFocusManager = new AudioFocusManager();
        mediaSessionManager = new MediaSessionManager();
        mediaPlayer.setOnCompletionListener(this);
        //初始化时获取上次最后播放的位置
        playingPosition = MusicUtils.getLocalMusicPlayingPosition();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    public static void doCommand(Context context, String action) {
        Intent intent = new Intent(context, PlayLocalMusicService.class);
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
                case Actions.ACTION_MEDIA_PREVIOUS:
                    prev();
                    break;
                default:
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    public void setOnPlayEventListener(OnPlayLocalMusicListener listener) {
        onPlayerEventListener = listener;
    }

    /**
     * 播放完自动切换下一首
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        onCompletionImpl(mp);
    }

    public void onCompletionImpl(MediaPlayer mp) {
        if (AppCache.getLocalMusicList().isEmpty()) {
            return;
        }
        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getLocalPlayMode());
        switch (mode) {
            case SHUFFLE:
                playingPosition = new Random().nextInt(AppCache.getLocalMusicList().size());
                play(playingPosition);
                break;
            case SINGLE:
                play(playingPosition);
                break;
            case LOOP:
            default:
                play(playingPosition + 1);
                break;
        }
    }

    /**
     * 确定要播放的Music
     */
    public void play(int position) {
        if (AppCache.getLocalMusicList().isEmpty()) {
            return;
        }
        //到尽头时重新播放列表尾或者列表头
        if (position < 0) {
            position = AppCache.getLocalMusicList().size() - 1;
        } else if (position >= AppCache.getLocalMusicList().size()) {
            position = 0;
        }

        playingPosition = position;
        MusicBean music = AppCache.getLocalMusicList().get(playingPosition);
        //保存当前播放的音乐id
        Preferences.saveCurrentSongId(music.getId());
        play(music);
    }

    /**
     * 准备开始播放，在preparedListener中调用start才开始真的播放
     */
    public void play(MusicBean music) {
        playingMusic = music;
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(music.getPath());
            mediaPlayer.prepareAsync();
            playState = STATE_PREPARING;
            mediaPlayer.setOnPreparedListener(preparedListener);
            if (onPlayerEventListener != null) {
                onPlayerEventListener.onChangeMusic(music);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (isPreparing()) {
                start();
            }
        }
    };

    @Override
    public void playOrPause() {
        if (isPreparing()) {
            stop();
        } else if (isPlaying()) {
            pause();
        } else if (isPausing()) {
            start();
        } else {
            play(getPlayingPosition());
        }
    }

    void start() {
        if (!isPreparing() && !isPausing()) {
            return;
        }
        //如果正在播放网络音乐则暂停
        if(AppCache.getPlayOnlineMusicService().isPlaying()){
            EventBus.getDefault().post(new StopPlayOnlineMusicServiceEvent(1));
        }
        if (audioFocusManager.requestAudioFocus()) {
            mediaPlayer.start();
            playState = STATE_PLAYING;
            handler.post(publishRunnable);
            Notifier.showPlay(playingMusic);
            AppCache.setCurrentService(this);
            mediaSessionManager = new MediaSessionManager();
            mediaSessionManager.updateMetaData(playingMusic);
            mediaSessionManager.updatePlaybackState();
            //注册耳机插拔监听广播
            registerReceiver(noisyReceiver, noisyFilter);
            if (onPlayerEventListener != null) {
                onPlayerEventListener.onPlayerStart();
            }
        }
    }

    @Override
    void pause() {
        if (!isPlaying()) {
            return;
        }
        mediaPlayer.pause();
        playState = STATE_PAUSE;
        handler.removeCallbacks(publishRunnable);
        Notifier.showPause(playingMusic);
        audioFocusManager.abandonAudioFocus();
        mediaSessionManager.updatePlaybackState();
        //注销耳机拔出监听广播
        unregisterReceiver(noisyReceiver);
        if (onPlayerEventListener != null) {
            onPlayerEventListener.onPlayerPause();
        }
    }

    public void pauseForHideNotifition(){
        if (!isPlaying()) {
            return;
        }
        mediaPlayer.pause();
        playState = STATE_PAUSE;
        handler.removeCallbacks(publishRunnable);
        mediaSessionManager.updatePlaybackState();
        //注销耳机拔出监听广播
        unregisterReceiver(noisyReceiver);
        if (onPlayerEventListener != null) {
            onPlayerEventListener.onPlayerPause();
        }
    }

    @Override
    public void stop() {
        if (isIdle()) {
            return;
        }
        pause();
        mediaPlayer.reset();
        playState = STATE_IDLE;
    }

    /**
     * 点击按钮切换下一首，与自动播放切换下一首有逻辑上的区别
     * 此处单曲循环模式应该与列表循环模式的处理逻辑一样，都是跳到下一首
     */
    @Override
    public void next() {
        if (AppCache.getLocalMusicList().isEmpty()) {
            return;
        }
        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getLocalPlayMode());
        switch (mode) {
            case SHUFFLE:
                playingPosition = new Random().nextInt(AppCache.getLocalMusicList().size());
                play(playingPosition);
                break;
            case SINGLE:
            case LOOP:
            default:
                play(playingPosition + 1);
                break;
        }
        mediaSessionManager.release();//不release再重新创建的话更新不了ui
    }

    @Override
    public void prev() {
        if (AppCache.getLocalMusicList().isEmpty()) {
            return;
        }
        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getLocalPlayMode());
        switch (mode) {
            case SHUFFLE:
                playingPosition = new Random().nextInt(AppCache.getLocalMusicList().size());
                play(playingPosition);
                break;
            case SINGLE:
            case LOOP:
            default:
                play(playingPosition - 1);
                break;
        }
        mediaSessionManager.release();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StopPlayLocalMusicServiceEvent event) {
        if(event.getFlag()==1){
            pause();
        }
    }

    /**
     * 跳转到指定的时间位置
     */
    @Override
    public void seekTo(int msec) {
        if (isPlaying() || isPausing()) {
            mediaPlayer.seekTo(msec);
            mediaSessionManager.updatePlaybackState();
            if (onPlayerEventListener != null) {
                onPlayerEventListener.onPublish(msec);
            }
        }
    }

    @Override
    public boolean isPlaying() {
        return playState == STATE_PLAYING;
    }

    @Override
    public boolean isPausing() {
        return playState == STATE_PAUSE;
    }

    @Override
    public boolean isPreparing() {
        return playState == STATE_PREPARING;
    }

    @Override
    public boolean isIdle() {
        return playState == STATE_IDLE;
    }


    /**
     * 获取正在播放的本地歌曲的序号
     */
    public void setPlayingPosition(int position) {
        this.playingPosition = position;
    }

    /**
     * 获取正在播放的本地歌曲的序号
     */
    public int getPlayingPosition() {
        return playingPosition;
    }

    /**
     * 获取正在播放的歌曲
     */
    public MusicBean getPlayingMusic() {
        return playingMusic;
    }

    /**
     * 删除或下载歌曲后刷新正在播放的本地歌曲的序号
     */
    public void updatePlayingPosition() {
        int position = 0;
        long id = Preferences.getCurrentSongId();
        //遍历找出与当前播放歌曲id相同的歌曲
        for (int i = 0; i < AppCache.getLocalMusicList().size(); i++) {
            if (AppCache.getLocalMusicList().get(i).getId() == id) {
                position = i;
                break;
            }
        }
        playingPosition = position;
        Preferences.saveCurrentSongId(AppCache.getLocalMusicList().get(playingPosition).getId());
    }

    public int getAudioSessionId() {
        return mediaPlayer.getAudioSessionId();
    }

    @Override
    public long getCurrentPosition() {
        if (isPlaying() || isPausing()) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    private Runnable publishRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying() && onPlayerEventListener != null) {
                onPlayerEventListener.onPublish(mediaPlayer.getCurrentPosition());
            }
            handler.postDelayed(this, TIME_UPDATE);
        }
    };

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
        AppCache.setPlayLocalMusicService(null);
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
                        onCompletionImpl(mediaPlayer);
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
                onCompletionImpl(mediaPlayer);
            }
        });
    }

    public class PlayBinder extends Binder {
        public PlayLocalMusicService getService() {
            return PlayLocalMusicService.this;
        }
    }
}
