package com.minardwu.yiyue.utils;


import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.SimpleFormatter;

public class ParseUtils {

    public static long parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String formatTime(String pattern, long milli) {
        int m = (int) (milli / DateUtils.MINUTE_IN_MILLIS);
        int s = (int) ((milli / DateUtils.SECOND_IN_MILLIS) % 60);
        String minute = String.format(Locale.getDefault(), "%02d", m);
        String second = String.format(Locale.getDefault(), "%02d", s);
        return pattern.replace("mm", minute).replace("ss", second);
    }

    public static String formatTimeOfPattern(String pattern, long l) {
        Date date = new Date(l);
        String time = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return time;
    }

    /**
     * b转为Mb
     */
    public static float b2mb(int b) {
        String mb = String.format(Locale.getDefault(), "%.2f", (float) b / 1024 / 1024);
        return Float.valueOf(mb);
    }
}
