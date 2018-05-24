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

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.application.YiYueApplication;
import com.minardwu.yiyue.model.MusicBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MusicUtils {

    /**
     *音乐过滤条件
     */
    private static final String MUSIC_FILTER = MediaStore.Audio.AudioColumns.SIZE + " >= ? AND "
            + MediaStore.Audio.AudioColumns.DURATION + " >= ?";

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
                MUSIC_FILTER,
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
            musicBeanList.add(musicBean);
        }
        cursor.close();
        Collections.sort(musicBeanList,new MusicComparator());
        return musicBeanList;
    }

    public static void deleteFromMediaStore(String filePath){
        String[] retCol = {MediaStore.Audio.Media._ID};
        //将单引号转化为两个单引号，避免sql执行时出错
        String handleFilePath = filePath.replace("'","''");
        Cursor cur = YiYueApplication.getAppContext().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                retCol,
                MediaStore.MediaColumns.DATA + "='" + handleFilePath + "'", null, null);
        if (cur.getCount() == 0) {
            return;
        }
        cur.moveToFirst();
        int id = cur.getInt(cur.getColumnIndex(MediaStore.MediaColumns._ID));
        cur.close();
        try {
            Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
            YiYueApplication.getAppContext().getContentResolver().delete(uri, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            scanMusic(YiYueApplication.getAppContext());
        }
    }

    public static void addToMediaStore(String filePath){
        FileUtils.scanFile(filePath);
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
                    result =  (int) (musicBean2.getAddTime()-musicBean1.getAddTime());
                    break;
                case Preferences.ORDER_BY_TITLE:
                    result = (int)musicBean1.getTitle().toLowerCase().charAt(0)-(int)musicBean2.getTitle().toLowerCase().charAt(0);
                    break;
                case Preferences.ORDER_BY_SINGER:
                    result = (int)musicBean1.getArtistName().toLowerCase().charAt(0)-(int)musicBean2.getArtistName().toLowerCase().charAt(0);
                    break;
                case Preferences.ORDER_BY_ALBUM:
                    result = (int)musicBean1.getAlbum().toLowerCase().charAt(0)-(int)musicBean2.getAlbum().toLowerCase().charAt(0);
                    break;
                default:
                    break;
            }
            return result;
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }
    }

    public static MusicBean getAlarmMusic(long id){
        for (int i =0;i < AppCache.getLocalMusicList().size();i++){
            if (AppCache.getLocalMusicList().get(i).getId() == id){
                return AppCache.getLocalMusicList().get(i);
            }
        }
        if (AppCache.getLocalMusicList().size() > 0) {
            return AppCache.getLocalMusicList().get(0);
        }
        return null;
    }

    public static MusicBean getDefaultMusic(){
        if (AppCache.getLocalMusicList().size() > 0) {
            return AppCache.getLocalMusicList().get(0);
        }
        return null;
    }

    public static ArrayList<MusicBean> searchLocalMusic(String content){
        //1.转为小写 2.去掉空格
        content = content.toLowerCase().replace(" ","");
        ArrayList<MusicBean> list = new ArrayList<MusicBean>();
        if(AppCache.getLocalMusicList().size()>0){
            for (MusicBean musicBean:AppCache.getLocalMusicList()){
                if(musicBean.getTitle().toLowerCase().replace(" ","").contains(content)
                        || musicBean.getArtistName().toLowerCase().replace(" ","").contains(content)
                        || musicBean.getAlbum().toLowerCase().replace(" ","").contains(content)){
                    list.add(musicBean);
                }
            }
        }
        return list;
    }

    public static int getLocalMusicPosition(long id){
        for (int i = 0;i<AppCache.getLocalMusicList().size();i++){
            if (AppCache.getLocalMusicList().get(i).getId() == id){
                return i;
            }
        }
        //若找不到则返回列表第一个
        if(AppCache.getLocalMusicList().size()>0){
            return 0;
        }
        return -1;
    }


    public static MusicBean getLocalMusicPlayingMusic(){
        //返回播放的音乐
        long id = Preferences.getCurrentSongId();
        for (int i = 0;i<AppCache.getLocalMusicList().size();i++){
            if (AppCache.getLocalMusicList().get(i).getId() == id){
                return AppCache.getLocalMusicList().get(i);
            }
        }
        //若找不到则返回列表第一个
        if(AppCache.getLocalMusicList().size()>0){
            return AppCache.getLocalMusicList().get(0);
        }
        return null;
    }

    public static int getLocalMusicPlayingPosition(){
        long id = Preferences.getCurrentSongId();
        for (int i = 0;i<AppCache.getLocalMusicList().size();i++){
            if (AppCache.getLocalMusicList().get(i).getId() == id){
                return i;
            }
        }
        return 0;
    }

    public static String getLocalMusicPlayingMusicTitle(){
        //返回播放的音乐
        if(getLocalMusicPlayingMusic()!=null){
            return getLocalMusicPlayingMusic().getTitle();
        }else {
            return UIUtils.getString(R.string.slogan);
        }
    }

    public static void removeMusic(long id){
        //返回播放的音乐
        for (int i=0;i<AppCache.getLocalMusicList().size();i++){
            if(AppCache.getLocalMusicList().get(i).getId()==id){
                AppCache.getLocalMusicList().remove(i);
            }
        }
    }
}
