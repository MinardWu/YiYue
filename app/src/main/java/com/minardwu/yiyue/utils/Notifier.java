package com.minardwu.yiyue.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.activity.MainActivity;
import com.minardwu.yiyue.constants.Extras;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.receiver.StatusBarReceiver;
import com.minardwu.yiyue.service.PlayLocalMusicService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wcy on 2017/4/18.
 */
public class Notifier {
    private static final int NOTIFICATION_ID = 0x111;
    private static PlayLocalMusicService playLocalMusicService;
    private static NotificationManager notificationManager;

    public static void init(PlayLocalMusicService playLocalMusicService) {
        Notifier.playLocalMusicService = playLocalMusicService;
        notificationManager = (NotificationManager) playLocalMusicService.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static void showPlay(MusicBean music) {
        playLocalMusicService.startForeground(NOTIFICATION_ID, buildNotification(playLocalMusicService, music, true));
    }

    public static void showPause(MusicBean music) {
//        playLocalMusicService.stopForeground(false);
        notificationManager.notify(NOTIFICATION_ID, buildNotification(playLocalMusicService, music, false));
    }

    public static void cancelAll() {
        playLocalMusicService.stopForeground(false);
        notificationManager.cancelAll();
    }

    private static Notification buildNotification(Context context, MusicBean music, boolean isPlaying) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Extras.EXTRA_NOTIFICATION,music.getType().toString());
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notification)
                .setCustomBigContentView(getRemoteViews(context, music, isPlaying));
        return builder.build();
    }

    private static RemoteViews getRemoteViews(Context context, MusicBean music, boolean isPlaying) {
        String title = music.getTitle();
        String subtitle = music.getArtist();
        Bitmap cover;
        if(music.getType()==MusicBean.Type.LOCAL){
            cover = CoverLoader.getInstance().loadThumbnail(music);
        }else{
            cover = music.getOnlineMusicCover();
        }

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);
        if (cover != null) {
            remoteViews.setImageViewBitmap(R.id.iv_icon, cover);
        } else {
            remoteViews.setImageViewResource(R.id.iv_icon, R.drawable.icon_logo);
        }
        remoteViews.setTextViewText(R.id.tv_title, title);
        remoteViews.setTextViewText(R.id.tv_subtitle, subtitle);

        boolean isLightNotificationTheme = isLightNotificationTheme(playLocalMusicService);

        Intent playIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        playIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_PLAY_PAUSE);
        playIntent.putExtra(StatusBarReceiver.MUSICTYPE, music.getType().toString());
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if(isPlaying){
            remoteViews.setImageViewResource(R.id.iv_play_pause,R.drawable.ic_notification_pause);
        }else {
            remoteViews.setImageViewResource(R.id.iv_play_pause,R.drawable.ic_notification_play);
        }
        remoteViews.setOnClickPendingIntent(R.id.iv_play_pause, playPendingIntent);

        Intent nextIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        nextIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_NEXT);
        nextIntent.putExtra(StatusBarReceiver.MUSICTYPE, music.getType().toString());
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 1, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_next, nextPendingIntent);

        Intent preIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        preIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_PRE);
        preIntent.putExtra(StatusBarReceiver.MUSICTYPE, music.getType().toString());
        PendingIntent prePendingIntent = PendingIntent.getBroadcast(context, 2, preIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_play_pre, prePendingIntent);

        Intent cancelIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        cancelIntent.putExtra(StatusBarReceiver.EXTRA,StatusBarReceiver.EXTRA_CANCEL);
        cancelIntent.putExtra(StatusBarReceiver.MUSICTYPE, music.getType().toString());
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(context,3,cancelIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.rl_cancel_all,cancelPendingIntent);
        return remoteViews;
    }

//    private static int getPlayIconRes(boolean isLightNotificationTheme, boolean isPlaying) {
//        if (isPlaying) {
//            return isLightNotificationTheme
//                    ? R.drawable.ic_status_bar_pause_dark_selector
//                    : R.drawable.ic_status_bar_pause_light_selector;
//        } else {
//            return isLightNotificationTheme
//                    ? R.drawable.ic_status_bar_play_dark_selector
//                    : R.drawable.ic_status_bar_play_light_selector;
//        }
//    }

//    private static int getNextIconRes(boolean isLightNotificationTheme) {
//        return isLightNotificationTheme
//                ? R.drawable.ic_status_bar_next_dark_selector
//                : R.drawable.ic_status_bar_next_light_selector;
//    }

    private static boolean isLightNotificationTheme(Context context) {
        int notificationTextColor = getNotificationTextColor(context);
        return isSimilarColor(Color.BLACK, notificationTextColor);
    }

    private static int getNotificationTextColor(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.build();
        RemoteViews remoteViews = notification.contentView;
        if (remoteViews == null) {
            return Color.BLACK;
        }
        int layoutId = remoteViews.getLayoutId();
        ViewGroup notificationLayout = (ViewGroup) LayoutInflater.from(context).inflate(layoutId, null);
        TextView title = (TextView) notificationLayout.findViewById(android.R.id.title);
        if (title != null) {
            return title.getCurrentTextColor();
        } else {
            return findTextColor(notificationLayout);
        }
    }

    /**
     * 如果通过 android.R.id.title 无法获得 title ，
     * 则通过遍历 notification 布局找到 textSize 最大的 TextView ，应该就是 title 了。
     */
    private static int findTextColor(ViewGroup notificationLayout) {
        List<TextView> textViewList = new ArrayList<>();
        findTextView(notificationLayout, textViewList);

        float maxTextSize = -1;
        TextView maxTextView = null;
        for (TextView textView : textViewList) {
            if (textView.getTextSize() > maxTextSize) {
                maxTextView = textView;
            }
        }

        if (maxTextView != null) {
            return maxTextView.getCurrentTextColor();
        }

        return Color.BLACK;
    }

    private static void findTextView(View view, List<TextView> textViewList) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                findTextView(viewGroup.getChildAt(i), textViewList);
            }
        } else if (view instanceof TextView) {
            textViewList.add((TextView) view);
        }
    }

    private static boolean isSimilarColor(int baseColor, int color) {
        int simpleBaseColor = baseColor | 0xff000000;
        int simpleColor = color | 0xff000000;
        int baseRed = Color.red(simpleBaseColor) - Color.red(simpleColor);
        int baseGreen = Color.green(simpleBaseColor) - Color.green(simpleColor);
        int baseBlue = Color.blue(simpleBaseColor) - Color.blue(simpleColor);
        double value = Math.sqrt(baseRed * baseRed + baseGreen * baseGreen + baseBlue * baseBlue);
        return value < 180.0;
    }
}
