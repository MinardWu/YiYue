package com.minardwu.yiyue.service;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.constants.StopTimeAction;
import com.minardwu.yiyue.utils.Preferences;

/**
 */
public class QuitTimer {
    private PlayLocalMusicService playLocalMusicService;
    private PlayOnlineMusicService playOnlineMusicService;
    private EventCallback<Long> timerCallback;
    private Handler handler = new Handler();
    private long remainTime;

    public static QuitTimer getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final QuitTimer sInstance = new QuitTimer();
    }

    private QuitTimer() {
    }

    public void init(@NonNull EventCallback<Long> timerCallback) {
        this.timerCallback = timerCallback;
        this.playLocalMusicService = AppCache.getPlayLocalMusicService();
        this.playOnlineMusicService = AppCache.getPlayOnlineMusicService();
    }

    public void start(long milli) {
        stop();//每次开始都要想把之前开始的移除掉
        if (milli > 0) {
            remainTime = milli + DateUtils.SECOND_IN_MILLIS;
            handler.post(quitRunnable);
        } else {
            remainTime = 0;
            timerCallback.onEvent(remainTime);
        }
    }

    public void stop() {
        timerCallback.onEvent(StopTimeAction.CLEAR_INFO);
        handler.removeCallbacks(quitRunnable);
    }

    private Runnable quitRunnable = new Runnable() {
        @Override
        public void run() {
            remainTime -= DateUtils.SECOND_IN_MILLIS;
            if (remainTime > 0) {
                timerCallback.onEvent(remainTime);
                handler.postDelayed(this, DateUtils.SECOND_IN_MILLIS);//下一秒继续计时
            } else {
                //如果开启播完关闭则需要修改drawer定时ui
                if(Preferences.getQuitTillSongEnd()){
                    timerCallback.onEvent(StopTimeAction.UNTIL_SONG_END);
                }
                //更改播放状态
                if(playLocalMusicService.isPlaying()){
                    if(Preferences.getQuitTillSongEnd()){
                        playLocalMusicService.quitWhenSongEnd();
                    }else {
                        playLocalMusicService.quit();
                    }
                }else if(playOnlineMusicService.isPlaying()){
                    if(Preferences.getQuitTillSongEnd()){
                        playOnlineMusicService.quitWhenSongEnd();
                    }else {
                        playOnlineMusicService.quit();
                    }
                }
                Preferences.saveStopTime(0);
            }
        }
    };
}
