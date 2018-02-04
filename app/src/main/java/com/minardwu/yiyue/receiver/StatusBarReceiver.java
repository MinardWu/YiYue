package com.minardwu.yiyue.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.service.PlayLocalMusicService;
import com.minardwu.yiyue.service.PlayOnlineMusicService;
import com.minardwu.yiyue.utils.Notifier;


/**
 * Created by wcy on 2017/4/18.
 */
public class StatusBarReceiver extends BroadcastReceiver {
    public static final String ACTION_STATUS_BAR = "com.minardwu.yiyue.receiver.StatusBarReceiver.ACTION";
    public static final String EXTRA = "extra";
    public static final String MUSICTYPE = "musictype";
    public static final String EXTRA_PRE = "pre";
    public static final String EXTRA_PLAY_PAUSE = "play_pause";
    public static final String EXTRA_NEXT = "next";
    public static final String EXTRA_CANCEL = "cancel";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        PlayLocalMusicService playLocalMusicService = AppCache.getPlayLocalMusicService();
        PlayOnlineMusicService playOnlineMusicService = AppCache.getPlayOnlineMusicService();
        String extra = intent.getStringExtra(EXTRA);
        String type = intent.getStringExtra(MUSICTYPE);
        if (TextUtils.equals(extra, EXTRA_NEXT)) {
            if (type.equals("LOCAL")){
                playLocalMusicService.next();
            }else if(type.equals("ONLINE")){
                playOnlineMusicService.next();
            }
        } else if (TextUtils.equals(extra, EXTRA_PLAY_PAUSE)) {
            if (type.equals("LOCAL")){
                playLocalMusicService.playOrPause();
            }else if(type.equals("ONLINE")){
                playOnlineMusicService.playOrPause();
            }
        } else if (TextUtils.equals(extra, EXTRA_PRE)) {
            if (type.equals("LOCAL")){
                playLocalMusicService.prev();
            }else if(type.equals("ONLINE")){
                playOnlineMusicService.next();
            }
        } else if (TextUtils.equals(extra, EXTRA_CANCEL)) {
            if (type.equals("LOCAL")){
                playLocalMusicService.pauseForHideNotifition();
            }else if(type.equals("ONLINE")){
                playOnlineMusicService.pauseForHideNotification();
            }
            Notifier.cancelAll();
        }
    }
}

