package com.minardwu.yiyue.service;

import android.app.Service;
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
import com.minardwu.yiyue.utils.Preferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 音乐播放后台服务
 */
public class PlayService extends Service implements MediaPlayer.OnCompletionListener {
    private static final String TAG = "Service";
    private static final long TIME_UPDATE = 300L;

    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PLAYING = 2;
    private static final int STATE_PAUSE = 3;

    private final NoisyAudioStreamReceiver noisyReceiver = new NoisyAudioStreamReceiver();
    private final IntentFilter noisyfilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private final Handler handler = new Handler();

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private AudioFocusManager audioFocusManager;//音乐焦点管理
    private MediaSessionManager mediaSessionManager;//媒体播放时界面和服务通讯
    private OnPlayerEventListener onPlayerEventListener;

    private MusicBean playingMusic;//正在播放的歌曲[本地|网络]
    private int playingPosition = 0;//正在播放的本地歌曲的序号
    private int playState = STATE_IDLE;//状态

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: " + getClass().getSimpleName());
        EventBus.getDefault().register(this);
        audioFocusManager = new AudioFocusManager(this);
        mediaSessionManager = new MediaSessionManager(this);
        mediaPlayer.setOnCompletionListener(this);
        playingPosition = Preferences.getCurrentSongPosition();//初始化时获取上次最后播放的位置
//        Notifier.init(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    public static void doCommand(Context context, String action) {
        Intent intent = new Intent(context, PlayService.class);
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
            }
        }
        return START_NOT_STICKY;
    }

    /**
     * 扫描音乐
     */
    public void updateMusicList(final EventCallback<Void> callback) {
        new AsyncTask<Void, Void, List<MusicBean>>() {
            @Override
            protected List<MusicBean> doInBackground(Void... params) {
                return MusicUtils.scanMusic(PlayService.this);
            }

            @Override
            protected void onPostExecute(List<MusicBean> musicList) {
                AppCache.getLocalMusicList().clear();
                AppCache.getLocalMusicList().addAll(musicList);

                if (!AppCache.getLocalMusicList().isEmpty()) {
                    updatePlayingPosition();
                    playingMusic = AppCache.getLocalMusicList().get(playingPosition);
                }

                if (onPlayerEventListener != null) {
                    onPlayerEventListener.onMusicListUpdate();
                }

                if (callback != null) {
                    callback.onEvent(null);//扫描后执行后续操作，具体操作由执行函数式传入的EventCallback参数决定
                }
            }
        }.execute();
    }

    public OnPlayerEventListener getOnPlayEventListener() {
        return onPlayerEventListener;
    }

    public void setOnPlayEventListener(OnPlayerEventListener listener) {
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
        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getPlayMode());
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
        Preferences.saveCurrentSongId(music.getId());//保存当前播放的音乐id和位置
        Preferences.saveCurrentSongPosition(playingPosition);
        Preferences.saveCurrentSongTitle(music.getTitle());
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
            mediaPlayer.setOnBufferingUpdateListener(bufferingUpdateListener);
            if (onPlayerEventListener != null) {
                onPlayerEventListener.onChangeMusic(music);
            }
//            Notifier.showPlay(music);
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

    private MediaPlayer.OnBufferingUpdateListener bufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (onPlayerEventListener != null) {
                onPlayerEventListener.onBufferingUpdate(percent);
            }
        }
    };

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
//            Notifier.showPlay(playingMusic);
            mediaSessionManager.updateMetaData(playingMusic);
            mediaSessionManager.updatePlaybackState();
            registerReceiver(noisyReceiver, noisyfilter);//注册耳机拔出监听广播
            if (onPlayerEventListener != null) {
                onPlayerEventListener.onPlayerStart();
            }
        }
    }

    void pause() {
        if (!isPlaying()) {
            return;
        }
        mediaPlayer.pause();
        playState = STATE_PAUSE;
        handler.removeCallbacks(publishRunnable);
//        Notifier.showPause(playingMusic);
        mediaSessionManager.updatePlaybackState();
        unregisterReceiver(noisyReceiver);//注销耳机拔出监听广播
        if (onPlayerEventListener != null) {
            onPlayerEventListener.onPlayerPause();
        }
    }

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
    public void next() {
        if (AppCache.getLocalMusicList().isEmpty()) {
            return;
        }
        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getPlayMode());
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
    }

    public void prev() {
        if (AppCache.getLocalMusicList().isEmpty()) {
            return;
        }
        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getPlayMode());
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
    public void seekTo(int msec) {
        if (isPlaying() || isPausing()) {
            mediaPlayer.seekTo(msec);
            mediaSessionManager.updatePlaybackState();
            if (onPlayerEventListener != null) {
                onPlayerEventListener.onPublish(msec);
            }
        }
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
//        Notifier.cancelAll();
        AppCache.setPlayService(null);
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
        public PlayService getService() {
            return PlayService.this;
        }
    }
}
