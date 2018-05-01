package com.minardwu.yiyue.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.minardwu.yiyue.activity.AlarmActivity;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by MinardWu on 2018/5/1.
 */

public class AlarmManagerUtil {

    public static final String ALARM_ACTION = "com.minardwu.yiyue.receiver.AlarmManagerReceiver";

    public static void setAlarmTime(Context context, long timeInMillis, Intent intent) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(context, intent.getIntExtra("id", 0),
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        int interval = (int) intent.getLongExtra("intervalMillis", 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setWindow(AlarmManager.RTC_WAKEUP, timeInMillis, interval, sender);
        }
    }

    public static void cancelAlarm(Context context, int id) {
        Intent intent = new Intent(ALARM_ACTION);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


    public static void setAlarm(Context context,int hour,int minute ,int id) {
        cancelAlarm(context,id);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long intervalMillis = 1000*60*60*24;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if(System.currentTimeMillis() > calendar.getTimeInMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(ALARM_ACTION);
        intent.putExtra("hour",hour);
        intent.putExtra("minute",minute);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setWindow(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                    intervalMillis, pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    intervalMillis, pendingIntent);
        }
    }

}
