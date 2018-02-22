package com.minardwu.yiyue.executor;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.view.View;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.activity.ArtistActivity;
import com.minardwu.yiyue.activity.FeedbackActivity;
import com.minardwu.yiyue.activity.InfoActivity;
import com.minardwu.yiyue.activity.MyArtistActivity;
import com.minardwu.yiyue.activity.MyFMHistoryActivity;
import com.minardwu.yiyue.activity.MySongActivity;
import com.minardwu.yiyue.activity.TapeActivity;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.utils.MusicUtils;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.widget.ChooseOptionDialog;
import com.minardwu.yiyue.widget.StopTimeDialog;

/**
 * Created by MinardWu on 2018/1/23.
 */

public class DrawerItemExecutor {

    public void execute(int position,Activity activity){
        switch (position){
            case 3:
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
                        ToastUtils.show(R.string.device_not_support);
                    }
                } else {
                    ToastUtils.show(R.string.device_not_support);
                }
                break;
            case 4:
                StopTimeDialog stopTimeDialog = new StopTimeDialog(activity, R.style.StopTimeDialog);
                stopTimeDialog.show();
                break;
            case 5:
                final int second[]= activity.getResources().getIntArray(R.array.filter_time_num);
                ChooseOptionDialog timeFilterDialog = new ChooseOptionDialog(activity,R.style.StopTimeDialog);
                timeFilterDialog.setTitle("按时长过滤");
                timeFilterDialog.setItem(R.array.filter_time_title);
                timeFilterDialog.setShowImagePosition(Preferences.getFilterTimePosition());
                timeFilterDialog.setOnDialogItemClickListener(new ChooseOptionDialog.onDialogItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Preferences.saveFilterTimePosition(position);
                        Preferences.saveFilterTime(second[position]);
                        ToastUtils.show(second[position]+"");
                    }
                });
                timeFilterDialog.show();
                break;
            case 6:
                final int size[]= activity.getResources().getIntArray(R.array.filter_size_num);
                ChooseOptionDialog sizeFilterDialog = new ChooseOptionDialog(activity,R.style.StopTimeDialog);
                sizeFilterDialog.setTitle("按大小过滤");
                sizeFilterDialog.setItem(R.array.filter_size_title);
                sizeFilterDialog.setShowImagePosition(Preferences.getFilterSizePosition());
                sizeFilterDialog.setOnDialogItemClickListener(new ChooseOptionDialog.onDialogItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Preferences.saveFilterSizePosition(position);
                        Preferences.saveFilterSize(size[position]);
                        ToastUtils.show(size[position]+"");
                    }
                });
                sizeFilterDialog.show();
                break;
            case 8:
                activity.startActivity(new Intent(activity, MySongActivity.class));
                break;
            case 9:
                activity.startActivity(new Intent(activity, MyArtistActivity.class));
                break;
            case 10:
                activity.startActivity(new Intent(activity, MyFMHistoryActivity.class));
                break;
            case 12:
                activity.startActivity(new Intent(activity, FeedbackActivity.class));
                break;
            case 13:
                activity.startActivity(new Intent(activity, TapeActivity.class));
                break;
        }
    }

}
