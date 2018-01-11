package com.minardwu.yiyue.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.constants.Actions;
import com.minardwu.yiyue.service.PlayOnlineMusicService;
import com.minardwu.yiyue.service.PlayService;


/**
 * 来电/耳机拔出时暂停播放
 */
public class NoisyAudioStreamReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(AppCache.getPlayService().isPlaying()){
            PlayService.doCommand(context, Actions.ACTION_MEDIA_PLAY_PAUSE);
        }else if(AppCache.getPlayOnlineMusicService().isPlaying()){
            PlayOnlineMusicService.doCommand(context, Actions.ACTION_MEDIA_PLAY_PAUSE);
        }
    }
}
