package com.minardwu.yiyue.service;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;

/**
 */
public class QuitTimer {
    private PlayService playService;
    private EventCallback<Long> timerCallback;
    private Handler handler;
    private long mTimerRemain;

    public static QuitTimer getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final QuitTimer sInstance = new QuitTimer();
    }

    private QuitTimer() {
    }

    public void init(@NonNull PlayService playService, @NonNull Handler handler, @NonNull EventCallback<Long> timerCallback) {
        this.playService = playService;
        this.handler = handler;
        this.timerCallback = timerCallback;
    }

    public void start(long milli) {
        stop();//每次开始都要想把之前开始的移除掉
        if (milli > 0) {
            mTimerRemain = milli + DateUtils.SECOND_IN_MILLIS;
            handler.post(quitRunnable);
        } else {
            mTimerRemain = 0;
            timerCallback.onEvent(mTimerRemain);
        }
    }

    public void stop() {
        handler.removeCallbacks(quitRunnable);
    }

    private Runnable quitRunnable = new Runnable() {
        @Override
        public void run() {
            mTimerRemain -= DateUtils.SECOND_IN_MILLIS;
            if (mTimerRemain > 0) {
                timerCallback.onEvent(mTimerRemain);//传递回PlayService中，然后利用OnTimer传回Activity更新UI
                handler.postDelayed(this, DateUtils.SECOND_IN_MILLIS);//下一秒继续计时
            } else {
                playService.quit();
            }
        }
    };
}
