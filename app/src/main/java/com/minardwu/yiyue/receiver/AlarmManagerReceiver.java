package com.minardwu.yiyue.receiver;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.minardwu.yiyue.activity.AlarmActivity;
import com.minardwu.yiyue.activity.AlarmClockActivity;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.utils.AlarmManagerUtil;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

public class AlarmManagerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int hour = intent.getIntExtra("hour",0);
        int minute = intent.getIntExtra("minute",0);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        ToastUtils.showLongToast("闹钟！"+hour+":"+minute);
        //如果闹钟是重复的话则应该检测是否在日期内，而且4.4版本后的要接着设置一次
        if(Preferences.enableAlarmClockRepeat()){
            if(MyDatabaseHelper.init(context).queryAlarmClockDate().contains(day)){
                Intent newIntent = new Intent();
                newIntent.setClass(context, AlarmActivity.class);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(newIntent);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                AlarmManagerUtil.setAlarm(context,hour,minute, AlarmClockActivity.ALARM_ID);
            }
        }else {
            if(MyDatabaseHelper.init(context).queryAlarmClockDate().contains(day)){
                Intent newIntent = new Intent();
                newIntent.setClass(context, AlarmActivity.class);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(newIntent);
            }
        }

    }
}
