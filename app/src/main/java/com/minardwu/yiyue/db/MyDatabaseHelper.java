package com.minardwu.yiyue.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ListView;

import com.minardwu.yiyue.model.MusicBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MinardWu on 2018/2/9.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    public static SQLiteDatabase sqLiteDatabase;

    private static final String TABLE_SEARCH_HISTORY = "search_history";
    private static final String TABLE_MY_ARTIST = "my_artist";
    private static final String TABLE_FM_HISTORY = "fm_history";

    private static final String CREATE_TABLE_SEARCH_HISTORY = "create table " + TABLE_SEARCH_HISTORY + "(" +
            "id integer primary key autoincrement," +
            "content text," +
            "time integer" +
            ")";

    private static final String CREATE_TABLE_MY_ARTIST = "create table " + TABLE_MY_ARTIST + "(" +
            "id integer primary key autoincrement," +
            "artistId text," +
            "name text," +
            "picUrl text" +
            ")";

    private static final String CREATE_TABLE_FM_HISTORY = "create table " + TABLE_FM_HISTORY + "(" +
            "id integer primary key autoincrement," +
            "songId text," +
            "artistId text," +
            "title text," +
            "artist text," +
            "album text," +
            "time integer" +
            ")";

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_SEARCH_HISTORY);
        sqLiteDatabase.execSQL(CREATE_TABLE_MY_ARTIST);
        sqLiteDatabase.execSQL(CREATE_TABLE_FM_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

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

    public void followArtist(String artistId, String name,String picUrl) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("artistId", artistId);
        contentValues.put("name", name);
        contentValues.put("picUrl", picUrl);
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
            contentValues.put("artist", musicBean.getArtist());
            contentValues.put("album", musicBean.getAlbum());
            contentValues.put("time", System.currentTimeMillis());
            sqLiteDatabase.insert(TABLE_FM_HISTORY,null,contentValues);
            return;
        }

    }

    public List<MusicBean> queryFMHistory(){
        List<MusicBean> list = new ArrayList<MusicBean>();
        Cursor cursor = sqLiteDatabase.query(TABLE_FM_HISTORY,null,null,null,null,null,"time desc","50");
        while (cursor.moveToNext()){
            MusicBean musicBean = new MusicBean();
            musicBean.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex("songId"))));
            musicBean.setArtistId(cursor.getString(cursor.getColumnIndex("artistId")));
            musicBean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            musicBean.setArtist(cursor.getString(cursor.getColumnIndex("artist")));
            musicBean.setAlbum(cursor.getString(cursor.getColumnIndex("album")));
            list.add(musicBean);
        }
        return list;
    }

}