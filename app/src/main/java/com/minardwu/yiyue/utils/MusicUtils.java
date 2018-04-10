package com.minardwu.yiyue.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.minardwu.yiyue.model.MusicBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MusicUtils {

    //过滤条件
    private static final String FILTER = MediaStore.Audio.AudioColumns.SIZE + " >= ? AND " + MediaStore.Audio.AudioColumns.DURATION + " >= ?";

    /**
     * 扫描歌曲
     */
    @NonNull
    public static List<MusicBean> scanMusic(Context context) {
        List<MusicBean> musicBeanList = new ArrayList<>();
        long filterSize = Preferences.getFilterSize() * 1024;
        long filterTime = Preferences.getFilterTime() * 1000;
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        BaseColumns._ID,
                        MediaStore.Audio.AudioColumns.IS_MUSIC,
                        MediaStore.Audio.AudioColumns.TITLE,
                        MediaStore.Audio.AudioColumns.ARTIST,
                        MediaStore.Audio.AudioColumns.ALBUM,
                        MediaStore.Audio.AudioColumns.ALBUM_ID,
                        MediaStore.Audio.AudioColumns.DATA,
                        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                        MediaStore.Audio.AudioColumns.SIZE,
                        MediaStore.Audio.AudioColumns.DURATION,
                        MediaStore.Audio.AudioColumns.DATE_ADDED
                },
                FILTER,
                new String[]{
                        String.valueOf(filterSize),
                        String.valueOf(filterTime)
                },
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor == null) {
            return musicBeanList;
        }

        int i = 0;
        while (cursor.moveToNext()) {
            // 是否为音乐，魅族手机上始终为0
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC));
            if (!SystemUtils.isFlyme() && isMusic == 0) {
                continue;
            }

            long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
            String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)));
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
            String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)));
            long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            long addTime = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));

            MusicBean musicBean = new MusicBean();
            musicBean.setId(id);
            musicBean.setType(MusicBean.Type.LOCAL);
            musicBean.setTitle(title);
            musicBean.setArtistName(artist);
            musicBean.setAlbum(album);
            musicBean.setAlbumId(albumId+"");
            musicBean.setDuration(duration);
            musicBean.setPath(path);
            musicBean.setFileName(fileName);
            musicBean.setFileSize(fileSize);
            musicBean.setAddTime(addTime);
            if (++i <= 20) {
                // 只加载前20首的缩略图
//                CoverLoader.getInstance().loadThumbnail(musicBean);
            }
            musicBeanList.add(musicBean);
        }
        cursor.close();
//        Preferences.saveCurrentSongId(musicBeanList.get(0).getId());
//        Preferences.saveCurrentSongTitle(musicBeanList.get(0).getTitle());
//        Preferences.saveCurrentSongPosition(0);
        Collections.sort(musicBeanList,new MusicComparator());
        return musicBeanList;
    }

    /**
     * 根据id获取封面Uri
     */
    public static Uri getMediaStoreAlbumCoverUri(long albumId) {
        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(artworkUri, albumId);
    }


    /**
     * 判断能否启动一个音频效果控制面板UI。
     */
    public static boolean isAudioControlPanelAvailable(Context context) {
        return isIntentAvailable(context, new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL));
    }

    private static boolean isIntentAvailable(Context context, Intent intent) {
        return context.getPackageManager().resolveActivity(intent, PackageManager.GET_RESOLVED_FILTER) != null;
    }

    public static class MusicComparator implements Comparator<MusicBean> {

        @Override
        public int compare(MusicBean musicBean1, MusicBean musicBean2) {
            int type = Preferences.getLocalMusicOrderType();
            int result = 1;
            switch (type){
                case Preferences.ORDER_BY_TIME:
                    result =  (int) (musicBean1.getAddTime()-musicBean2.getAddTime());
                    break;
                case Preferences.ORDER_BY_TITLE:
                    result = (int)musicBean1.getTitle().toLowerCase().charAt(0)-(int)musicBean2.getTitle().toLowerCase().charAt(0);
                    break;
                case Preferences.ORDER_BY_SINGER:
                    result = (int)musicBean1.getArtistName().toLowerCase().charAt(0)-(int)musicBean2.getTitle().toLowerCase().charAt(0);
                    break;
                case Preferences.ORDER_BY_ALBUM:
                    result = (int)musicBean1.getAlbum().toLowerCase().charAt(0)-(int)musicBean2.getAlbum().toLowerCase().charAt(0);
                    break;
            }
            return result;
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }
    }
}
