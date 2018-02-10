package com.minardwu.yiyue.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.minardwu.yiyue.utils.SystemUtils;
import com.minardwu.yiyue.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MinardWu on 2018/2/9.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private SQLiteDatabase sqLiteDatabase;

    private static final String TABLE_SEARCH_HISTORY = "search_history";
    private static final String TABLE_MY_ARTIST = "my_artist";
    private static final String TABLE_FM_HISTORY = "fm_history";

    private static final String CREATE_TABLE_SEARCH_HISTORY = "create table "+TABLE_SEARCH_HISTORY+"(" +
            "id integer primary key autoincrement," +
            "content text," +
            "time integer" +
            ")";

    private static final String CREATE_TABLE_MY_ARTIST = "create table "+TABLE_MY_ARTIST+"(" +
            "id integer primary key autoincrement," +
            "artistId text" +
            "name text" +
            ")";

    private static final String CREATE_TABLE_FM_HISTORY = "create table "+TABLE_FM_HISTORY+"(" +
            "id integer primary key autoincrement," +
            "songId integer" +
            "title text" +
            "artist text" +
            "album text" +
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
        ToastUtils.show("ok");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void setSQLiteDataBase(SQLiteDatabase sqLiteDataBase){
        this.sqLiteDatabase = sqLiteDataBase;
    }

    public List<String> queryHistory(){
        List<String> list = new ArrayList<String>();
        Cursor cursor = sqLiteDatabase.query(TABLE_SEARCH_HISTORY,null,null,null,null,null,"time desc");
        while (cursor.moveToNext()){
            list.add(cursor.getString(cursor.getColumnIndex("content")));
        }
        cursor.close();
        return list;
    }

    public void insertHistory(String content){
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM "+TABLE_SEARCH_HISTORY+" WHERE content = ?",new String[]{content});
        cursor.moveToFirst();
        Long count = cursor.getLong(0);
        if(count>0){
            updateHistory(content);
        }else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("content",content);
            contentValues.put("time", System.currentTimeMillis());
            sqLiteDatabase.insert(TABLE_SEARCH_HISTORY,null,contentValues);
        }

    }

    public void clearHistory(){
        sqLiteDatabase.delete(TABLE_SEARCH_HISTORY,null,null);
    }

    public void updateHistory(String content){
        ContentValues contentValues = new ContentValues();
        contentValues.put("time",System.currentTimeMillis());
        sqLiteDatabase.update(TABLE_SEARCH_HISTORY,contentValues,"content = ?",new String[]{content});
    }

}
