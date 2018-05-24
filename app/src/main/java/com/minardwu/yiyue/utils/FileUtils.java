package com.minardwu.yiyue.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Build;
import android.text.TextUtils;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.application.YiYueApplication;
import com.minardwu.yiyue.model.MusicBean;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件工具类
 */
public class FileUtils {
    private static final String MP3 = ".mp3";
    private static final String LRC = ".lrc";

    private static String getAppDir() {
        return Environment.getExternalStorageDirectory() + "/YiYue";
    }

    public static String getMusicDir() {
        String dir = getAppDir() + "/Music/";
        return mkdirs(dir);
    }

    public static String getLrcDir() {
        String dir = getAppDir() + "/Lyric/";
        return mkdirs(dir);
    }

    public static String getAlbumDir() {
        String dir = getAppDir() + "/Album/";
        return mkdirs(dir);
    }

    public static String getLogDir() {
        String dir = getAppDir() + "/Log/";
        return mkdirs(dir);
    }

    public static String getRelativeMusicDir() {
        String dir = "PonyMusic/Music/";
        return mkdirs(dir);
    }

    public static String getSplashDir(Context context) {
        String dir = context.getFilesDir() + "/splash/";
        return mkdirs(dir);
    }

    public static String getCorpImagePath(Context context) {
        return context.getExternalCacheDir() + "/corp.jpg";
    }

    /**
     * 获取歌词路径,从已下载lrc文件夹中查找
     * @return 如果存在返回路径，否则返回null
     */
    public static String getLrcFilePath(MusicBean music) {
        if (music == null) {
            return null;
        }
        String lrcFilePath = getLrcDir() + getLrcFileName(music.getArtistName(), music.getTitle());
        if (!exists(lrcFilePath)) {
            lrcFilePath = null;
        }
        return lrcFilePath;

    }

    private static String mkdirs(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return dir;
    }

    private static boolean exists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static String getFileName(MusicBean musicBean){
        return getFileName(musicBean.getArtistName(),musicBean.getTitle());
    }

    public static String getFileName(String artist, String title) {
        artist = stringFilter(artist);
        title = stringFilter(title);
        if (TextUtils.isEmpty(artist)) {
            artist = YiYueApplication.getAppContext().getString(R.string.unknown);
        }
        if (TextUtils.isEmpty(title)) {
            title = YiYueApplication.getAppContext().getString(R.string.unknown);
        }
        return artist + " - " + title;
    }

    public static String getMp3FileName(String artist, String title) {
        return getFileName(artist, title) + MP3;
    }

    public static String getLrcFileName(String artist, String title) {
        return getFileName(artist, title) + LRC;
    }

    public static String getAlbumFileName(String artist, String title) {
        return getFileName(artist, title);
    }

    public static String getArtistAndAlbum(String artist, String album) {
        if (TextUtils.isEmpty(artist) && TextUtils.isEmpty(album)) {
            return "";
        } else if (!TextUtils.isEmpty(artist) && TextUtils.isEmpty(album)) {
            return artist;
        } else if (TextUtils.isEmpty(artist) && !TextUtils.isEmpty(album)) {
            return album;
        } else {
            return artist + " - " + album;
        }
    }

    /**
     * 过滤特殊字符(\/:*?"<>|)
     */
    private static String stringFilter(String str) {
        if (str == null) {
            return null;
        }
        String regEx = "[\\/:*?\"<>|]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }


    public static void saveLrcFile(String path, String content) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            bw.write(content);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteFile(String filePath){
        File file = new File(filePath);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                MusicUtils.deleteFromMediaStore(filePath);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static void scanAll() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] paths = new String[]{Environment.getExternalStorageDirectory().toString()};
            MediaScannerConnection.scanFile(YiYueApplication.getAppContext(), paths, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String s, Uri uri) {
                    MusicUtils.scanMusic(YiYueApplication.getAppContext());
                }
            });
        } else {
            final Intent intent;
            intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
            intent.setClassName("com.android.providers.media", "com.android.providers.media.MediaScannerReceiver");
            intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
            YiYueApplication.getAppContext().sendBroadcast(intent);
        }
    }

    public static void scanDir(String dirPath){
        File file = new File(dirPath);
        if(file.isDirectory()){
            File[] array = file.listFiles();
            for(int i=0;i<array.length;i++){
                File f = array[i];
                if(f.isFile()){
                    String name = f.getName();
                    if(name.contains(".mp3")){
                        scanFile(f.getAbsolutePath());
                    }
                } else {
                    scanDir(f.getAbsolutePath());
                }
            }
        }
    }

    public static void scanFile(String filePath){
//        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        scanIntent.setData(Uri.fromFile(new File(filePath)));
//        YiYueApplication.getAppContext().sendBroadcast(scanIntent);
        String[] paths = new String[]{filePath};
        MediaScannerConnection.scanFile(YiYueApplication.getAppContext(), paths, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String s, Uri uri) {
                AppCache.updateLocalMusicList();
            }
        });
    }
}
