package com.minardwu.yiyue.executor;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.view.View;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.activity.AlarmClockActivity;
import com.minardwu.yiyue.activity.CollectionActivity;
import com.minardwu.yiyue.activity.FeedbackActivity;
import com.minardwu.yiyue.activity.InfoActivity;
import com.minardwu.yiyue.activity.MyArtistActivity;
import com.minardwu.yiyue.activity.MyFMHistoryActivity;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.utils.MusicUtils;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.widget.dialog.ChooseOptionDialog;
import com.minardwu.yiyue.widget.dialog.StopTimeDialog;

/**
 * Created by MinardWu on 2018/1/23.
 */

public class DrawerItemExecutor {

    public void execute(int position,String title,Activity activity){
        switch (title){
            case "音效调节":
                if (MusicUtils.isAudioControlPanelAvailable(activity)) {
                    Intent intent = new Intent();
                    String packageName = activity.getPackageName();
                    intent.setAction(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                    intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName);
                    intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                    intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, AppCache.getPlayLocalMusicService().getAudioSessionId());
                    try {
                        activity.startActivityForResult(intent, 1);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        ToastUtils.showShortToast(R.string.device_not_support);
                    }
                } else {
                    ToastUtils.showShortToast(R.string.device_not_support);
                }
                break;
            case "定时停止播放":
                StopTimeDialog stopTimeDialog = new StopTimeDialog(activity, R.style.StopTimeDialog);
                stopTimeDialog.show();
                break;
            case "我的收藏":
                activity.startActivity(new Intent(activity, CollectionActivity.class));
                break;
            case "我的歌手":
                activity.startActivity(new Intent(activity, MyArtistActivity.class));
                break;
            case "FM足迹":
                activity.startActivity(new Intent(activity, MyFMHistoryActivity.class));
                break;
            case "反馈":
                activity.startActivity(new Intent(activity, FeedbackActivity.class));
                break;
            case "关于":
                activity.startActivity(new Intent(activity, InfoActivity.class));
                break;
            case "音乐闹钟":
                activity.startActivity(new Intent(activity, AlarmClockActivity.class));
                break;
            default:
                break;
        }
    }

}
