package com.minardwu.yiyue.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.minardwu.yiyue.model.AlbumBean;
import com.minardwu.yiyue.model.ArtistBean;
import com.minardwu.yiyue.model.MusicBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MinardWu on 2018/2/9.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    public static SQLiteDatabase sqLiteDatabase;

    private static final String DATABASE_NAME = "T10.db";
    private static final String TABLE_SEARCH_HISTORY = "search_history";
    private static final String TABLE_FM_HISTORY = "fm_history";
    private static final String TABLE_MY_ARTIST = "my_artist";
    private static final String TABLE_MY_SONG = "my_song";
    private static final String TABLE_MY_ALBUM = "my_album";
    private static final String TABLE_ALARM_CLOCK_DATE = "alarm_clock_date";
    private static final String TABLE_ONLINE_MUSIC_LIST = "online_music_list";

    private static final String CREATE_TABLE_SEARCH_HISTORY = "create table " + TABLE_SEARCH_HISTORY + "(" +
            "id integer primary key autoincrement," +
            "content text," +
            "time integer" +
            ")";

    private static final String CREATE_TABLE_FM_HISTORY = "create table " + TABLE_FM_HISTORY + "(" +
            "id integer primary key autoincrement," +
            "songId text," +
            "artistId text," +
            "title text," +
            "artist text," +
            "album text," +
            "albumId text," +
            "coverUrl text," +
            "time integer" +
            ")";

    private static final String CREATE_TABLE_MY_SONG = "create table " + TABLE_MY_SONG + "(" +
            "id integer primary key autoincrement," +
            "songId text," +
            "artistId text," +
            "title text," +
            "artist text," +
            "album text," +
            "albumId text," +
            "coverUrl text," +
            "time integer" +
            ")";

    private static final String CREATE_TABLE_MY_ARTIST = "create table " + TABLE_MY_ARTIST + "(" +
            "id integer primary key autoincrement," +
            "artistId text," +
            "name text," +
            "picUrl text," +
            "musicSize integer," +
            "albumSize integer" +
            ")";

    private static final String CREATE_TABLE_MY_ALBUM = "create table " + TABLE_MY_ALBUM + "(" +
            "id integer primary key autoincrement," +
            "albumId text," +
            "albumName text," +
            "picUrl text," +
            "artistId text," +
            "artistName text," +
            "artistPicUrl text," +
            "publishTime long," +
            "company text," +
            "info text," +
            "subType text," +
            "size integer"+
            ")";


    private static final String CREATE_TABLE_ALARM_CLOCK_DATE = "create table " + TABLE_ALARM_CLOCK_DATE + "(" +
            "id integer primary key autoincrement," +
            "date integer"+
            ")";

    private static final String CREATE_TABLE_ONLINE_MUSIC_LIST = "create table " + TABLE_ONLINE_MUSIC_LIST + "(" +
            "id integer primary key autoincrement," +
            "songId text," +
            "artistId text," +
            "title text," +
            "artist text," +
            "album text," +
            "albumId text," +
            "coverUrl text," +
            "time integer" +
            ")";

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_SEARCH_HISTORY);
        sqLiteDatabase.execSQL(CREATE_TABLE_FM_HISTORY);
        sqLiteDatabase.execSQL(CREATE_TABLE_MY_ARTIST);
        sqLiteDatabase.execSQL(CREATE_TABLE_MY_SONG);
        sqLiteDatabase.execSQL(CREATE_TABLE_MY_ALBUM);
        sqLiteDatabase.execSQL(CREATE_TABLE_ALARM_CLOCK_DATE);
        sqLiteDatabase.execSQL(CREATE_TABLE_ONLINE_MUSIC_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static MyDatabaseHelper init(Context context){
        return init(context,DATABASE_NAME,null,1);
    }

    public static MyDatabaseHelper init(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context,name,factory,version);
        sqLiteDatabase  = myDatabaseHelper.getWritableDatabase();
        return myDatabaseHelper;
    }

    public void setSQLiteDataBase(SQLiteDatabase sqLiteDataBase) {
        this.sqLiteDatabase = sqLiteDataBase;
    }

    public List<String> querySearchHistory() {
        List<String> list = new ArrayList<String>();
        Cursor cursor = sqLiteDatabase.query(TABLE_SEARCH_HISTORY, null, null, null, null, null, "time desc");
        while (cursor.moveToNext()) {
            list.add(cursor.getString(cursor.getColumnIndex("content")));
        }
        cursor.close();
        return list;
    }

    public void insertSearchHistory(String content) {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + TABLE_SEARCH_HISTORY + " WHERE content = ?", new String[]{content});
        cursor.moveToFirst();
        Long count = cursor.getLong(0);
        if (count > 0) {
            updateSearchHistory(content);
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("content", content);
            contentValues.put("time", System.currentTimeMillis());
            sqLiteDatabase.insert(TABLE_SEARCH_HISTORY, null, contentValues);
        }

    }

    public void clearSearchHistory() {
        sqLiteDatabase.delete(TABLE_SEARCH_HISTORY, null, null);
    }

    public void updateSearchHistory(String content) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("time", System.currentTimeMillis());
        sqLiteDatabase.update(TABLE_SEARCH_HISTORY, contentValues, "content = ?", new String[]{content});
    }

    public boolean isFollowArtist(String artistId) {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + TABLE_MY_ARTIST + " WHERE artistId = ?", new String[]{artistId});
        cursor.moveToFirst();
        Long count = cursor.getLong(0);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void followArtist(ArtistBean artistBean) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("artistId", artistBean.getId());
        contentValues.put("name", artistBean.getName());
        contentValues.put("picUrl", artistBean.getPicUrl());
        contentValues.put("musicSize", artistBean.getMusicSize());
        contentValues.put("albumSize", artistBean.getAlbumSize());
        sqLiteDatabase.insert(TABLE_MY_ARTIST, null, contentValues);
    }

    public void unfollowArtist(String artistId) {
        sqLiteDatabase.delete(TABLE_MY_ARTIST, "artistId = ?", new String[]{artistId});
    }

    public void updateArtistPic(String artistId,String picUrl) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("picUrl", picUrl);
        sqLiteDatabase.update(TABLE_MY_ARTIST,contentValues,"artistId = ?", new String[]{artistId});
    }

    public List<ArtistBean> queryFollowedArtist(){
        List<ArtistBean> list = new ArrayList<ArtistBean>();
        Cursor cursor = sqLiteDatabase.query(TABLE_MY_ARTIST,null,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            ArtistBean artistBean = new ArtistBean();
            artistBean.setId(cursor.getString(cursor.getColumnIndex("artistId")));
            artistBean.setName(cursor.getString(cursor.getColumnIndex("name")));
            artistBean.setPicUrl(cursor.getString(cursor.getColumnIndex("picUrl")));
            artistBean.setMusicSize(cursor.getInt(cursor.getColumnIndex("musicSize")));
            artistBean.setAlbumSize(cursor.getInt(cursor.getColumnIndex("albumSize")));
            list.add(artistBean);
        }
        return list;
    }

    public void addFMHistory(MusicBean musicBean){
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + TABLE_FM_HISTORY + " WHERE songId = ?", new String[]{musicBean.getId()+""});
        cursor.moveToFirst();
        Long count = cursor.getLong(0);
        if (count > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("time",System.currentTimeMillis());
            sqLiteDatabase.update(TABLE_FM_HISTORY,contentValues,"songId = ?",new String[]{musicBean.getId()+""});
            return;
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("songId", musicBean.getId()+"");
            contentValues.put("artistId", musicBean.getArtistId());
            contentValues.put("title", musicBean.getTitle());
            contentValues.put("artist", musicBean.getArtistName());
            contentValues.put("album", musicBean.getAlbum());
            contentValues.put("albumId", musicBean.getAlbumId());
            contentValues.put("coverUrl", musicBean.getCoverPath());
            contentValues.put("time", System.currentTimeMillis());
            sqLiteDatabase.insert(TABLE_FM_HISTORY,null,contentValues);
            return;
        }

    }

    public ArrayList<MusicBean> queryFMHistory(){
        ArrayList<MusicBean> list = new ArrayList<MusicBean>();
        Cursor cursor = sqLiteDatabase.query(TABLE_FM_HISTORY,null,null,null,null,null,"time desc","50");
        while (cursor.moveToNext()){
            MusicBean musicBean = new MusicBean();
            musicBean.setType(MusicBean.Type.ONLINE);
            musicBean.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex("songId"))));
            musicBean.setArtistId(cursor.getString(cursor.getColumnIndex("artistId")));
            musicBean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            musicBean.setArtistName(cursor.getString(cursor.getColumnIndex("artist")));
            musicBean.setAlbum(cursor.getString(cursor.getColumnIndex("album")));
            musicBean.setAlbumId(cursor.getString(cursor.getColumnIndex("albumId")));
            list.add(musicBean);
        }
        return list;
    }

    public MusicBean getFMHistoryLastSong(){
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + TABLE_FM_HISTORY,null);
        cursor.moveToFirst();
        Long count = cursor.getLong(0);
        final MusicBean[] lastSong = {new MusicBean()};
        if (count > 0) {
            Cursor cursor2 = sqLiteDatabase.query(TABLE_FM_HISTORY,null,null,null,null,null,"time desc","50");
            cursor2.moveToNext();
            lastSong[0].setId(Long.parseLong(cursor2.getString(cursor2.getColumnIndex("songId"))));
            lastSong[0].setArtistId(cursor2.getString(cursor2.getColumnIndex("artistId")));
            lastSong[0].setTitle(cursor2.getString(cursor2.getColumnIndex("title")));
            lastSong[0].setArtistName(cursor2.getString(cursor2.getColumnIndex("artist")));
            lastSong[0].setAlbum(cursor2.getString(cursor2.getColumnIndex("album")));
            lastSong[0].setAlbumId(cursor2.getString(cursor2.getColumnIndex("albumId")));
            lastSong[0].setCoverPath(cursor2.getString(cursor2.getColumnIndex("coverUrl")));
            lastSong[0].setType(MusicBean.Type.ONLINE);
            return lastSong[0];
        } else {
            return null;
        }
    }

    public void deleteFMHistory(MusicBean musicBean){
        sqLiteDatabase.delete(TABLE_FM_HISTORY,"songId = ?",new String[]{musicBean.getId()+""});
    }

    public void clearFMHistory(){
        sqLiteDatabase.delete(TABLE_FM_HISTORY,null,null);
    }

    public boolean isCollectedSong(MusicBean musicBean){
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + TABLE_MY_SONG + " WHERE songId = ?", new String[]{musicBean.getId()+""});
        cursor.moveToFirst();
        Long count = cursor.getLong(0);
        if (count > 0) {
            return true;
        }else {
            return false;
        }
    }

    public void addCollectedSong(MusicBean musicBean){
        ContentValues contentValues = new ContentValues();
        contentValues.put("songId", musicBean.getId()+"");
        contentValues.put("artistId", musicBean.getArtistId());
        contentValues.put("title", musicBean.getTitle());
        contentValues.put("artist", musicBean.getArtistName());
        contentValues.put("album", musicBean.getAlbum());
        contentValues.put("albumId", musicBean.getAlbumId());
        contentValues.put("coverUrl", musicBean.getCoverPath());
        contentValues.put("time", System.currentTimeMillis());
        sqLiteDatabase.insert(TABLE_MY_SONG,null,contentValues);
    }

    public void deleteCollectedSong(MusicBean musicBean){
        sqLiteDatabase.delete(TABLE_MY_SONG,"songId = ?",new String[]{musicBean.getId()+""});
    }

    public ArrayList<MusicBean> queryCollectedSong(){
        ArrayList<MusicBean> list = new ArrayList<MusicBean>();
        Cursor cursor = sqLiteDatabase.query(TABLE_MY_SONG,null,null,null,null,null,"time desc",null);
        while (cursor.moveToNext()){
            MusicBean musicBean = new MusicBean();
            musicBean.setType(MusicBean.Type.ONLINE);
            musicBean.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex("songId"))));
            musicBean.setArtistId(cursor.getString(cursor.getColumnIndex("artistId")));
            musicBean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            musicBean.setArtistName(cursor.getString(cursor.getColumnIndex("artist")));
            musicBean.setAlbum(cursor.getString(cursor.getColumnIndex("album")));
            musicBean.setAlbumId(cursor.getString(cursor.getColumnIndex("albumId")));
            musicBean.setCoverPath(cursor.getString(cursor.getColumnIndex("coverUrl")));
            list.add(musicBean);
        }
        return list;
    }


    public void addCollectedAlbum(AlbumBean albumBean){
        if(!isCollectedAlbum(albumBean.getAlbumId())){
            ContentValues contentValues = new ContentValues();
            contentValues.put("albumId", albumBean.getAlbumId());
            contentValues.put("albumName", albumBean.getAlbumName());
            contentValues.put("picUrl", albumBean.getPicUrl());
            contentValues.put("artistId", albumBean.getArtist().getId());
            contentValues.put("artistName", albumBean.getArtist().getName());
            contentValues.put("artistPicUrl", albumBean.getArtist().getPicUrl());
            contentValues.put("publishTime", albumBean.getPublishTime());
            contentValues.put("company", albumBean.getCompany());
            contentValues.put("info", albumBean.getInfo());
            contentValues.put("subType", albumBean.getSubType());
            contentValues.put("size", albumBean.getSize());
            sqLiteDatabase.insert(TABLE_MY_ALBUM,null,contentValues);
        }
    }

    public List<AlbumBean> queryCollectedAlbum(){
        List<AlbumBean> list = new ArrayList<AlbumBean>();
        Cursor cursor = sqLiteDatabase.query(TABLE_MY_ALBUM,null,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            AlbumBean albumBean = new AlbumBean();
            albumBean.setAlbumId(cursor.getString(cursor.getColumnIndex("albumId")));
            albumBean.setAlbumName(cursor.getString(cursor.getColumnIndex("albumName")));
            albumBean.setPicUrl(cursor.getString(cursor.getColumnIndex("picUrl")));
            albumBean.setPublishTime(cursor.getLong(cursor.getColumnIndex("publishTime")));
            albumBean.setCompany(cursor.getString(cursor.getColumnIndex("company")));
            albumBean.setInfo(cursor.getString(cursor.getColumnIndex("info")));
            albumBean.setSubType(cursor.getString(cursor.getColumnIndex("subType")));
            albumBean.setSize(cursor.getInt(cursor.getColumnIndex("size")));

            ArtistBean artistBean = new ArtistBean();
            artistBean.setName(cursor.getString(cursor.getColumnIndex("artistName")));
            artistBean.setId(cursor.getString(cursor.getColumnIndex("artistId")));
            artistBean.setPicUrl(cursor.getString(cursor.getColumnIndex("artistPicUrl")));
            albumBean.setArtist(artistBean);
            list.add(albumBean);
        }
        return list;
    }

    public void deleteCollectedAlbum(AlbumBean albumBean){
        sqLiteDatabase.delete(TABLE_MY_ALBUM,"albumId = ?",new String[]{albumBean.getAlbumId()});
    }

    public boolean isCollectedAlbum(String albumId) {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + TABLE_MY_ALBUM + " WHERE albumId = ?", new String[]{albumId});
        cursor.moveToFirst();
        Long count = cursor.getLong(0);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void addAlarmClockDate(int date){
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", date);
        sqLiteDatabase.insert(TABLE_ALARM_CLOCK_DATE,null,contentValues);
    }

    public void deleteAlarmClockDate(int date){
        sqLiteDatabase.delete(TABLE_ALARM_CLOCK_DATE,"date = ?",new String[]{Integer.toString(date)});
    }

    public List<Integer> queryAlarmClockDate(){
        List<Integer> list = new ArrayList<Integer>();
        Cursor cursor = sqLiteDatabase.query(TABLE_ALARM_CLOCK_DATE,null,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            int date = cursor.getInt(cursor.getColumnIndex("date"));
            list.add(date);
        }
        return list;
    }

    public void addOnlineMusic(MusicBean musicBean){
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + TABLE_ONLINE_MUSIC_LIST + " WHERE songId = ?", new String[]{musicBean.getId()+""});
        cursor.moveToFirst();
        Long count = cursor.getLong(0);
        if (count > 0) {
            return;
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("songId", musicBean.getId()+"");
            contentValues.put("artistId", musicBean.getArtistId());
            contentValues.put("title", musicBean.getTitle());
            contentValues.put("artist", musicBean.getArtistName());
            contentValues.put("album", musicBean.getAlbum());
            contentValues.put("albumId", musicBean.getAlbumId());
            contentValues.put("coverUrl", musicBean.getCoverPath());
            sqLiteDatabase.insert(TABLE_ONLINE_MUSIC_LIST,null,contentValues);
            return;
        }
    }

    public void deleteOnlineMusic(MusicBean musicBean){
        sqLiteDatabase.delete(TABLE_ONLINE_MUSIC_LIST,"songId = ?",new String[]{musicBean.getId()+""});
    }

    public void replaceOnlineMusicList(List<MusicBean> list){
        sqLiteDatabase.delete(TABLE_ONLINE_MUSIC_LIST, null, null);
        if(list!=null){
            for(int i=0;i<list.size();i++){
                addOnlineMusic(list.get(i));
            }
        }
    }

    public ArrayList<MusicBean> queryOnlineMusicList(){
        ArrayList<MusicBean> list = new ArrayList<MusicBean>();
        Cursor cursor = sqLiteDatabase.query(TABLE_ONLINE_MUSIC_LIST,null,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            MusicBean musicBean = new MusicBean();
            musicBean.setType(MusicBean.Type.ONLINE);
            musicBean.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex("songId"))));
            musicBean.setArtistId(cursor.getString(cursor.getColumnIndex("artistId")));
            musicBean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            musicBean.setArtistName(cursor.getString(cursor.getColumnIndex("artist")));
            musicBean.setAlbum(cursor.getString(cursor.getColumnIndex("album")));
            musicBean.setAlbumId(cursor.getString(cursor.getColumnIndex("albumId")));
            musicBean.setCoverPath(cursor.getString(cursor.getColumnIndex("coverUrl")));
            list.add(musicBean);
        }
        return list;
    }

    public void clearOnlineMusicList(){
        sqLiteDatabase.delete(TABLE_ONLINE_MUSIC_LIST,null,null);
    }
}