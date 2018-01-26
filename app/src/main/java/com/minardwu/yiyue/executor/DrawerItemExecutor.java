package com.minardwu.yiyue.executor;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.view.View;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.activity.InfoActivity;
import com.minardwu.yiyue.activity.MainActivity;
import com.minardwu.yiyue.activity.SampleActivity;
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
            case 1:
                StopTimeDialog stopTimeDialog = new StopTimeDialog(activity, R.style.StopTimeDialog);
                stopTimeDialog.show();
                break;
            case 4:
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
            case 5:
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
            case 6:
                activity.startActivity(new Intent(activity, InfoActivity.class));
                break;
        }
    }

}
