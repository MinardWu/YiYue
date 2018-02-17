package com.minardwu.yiyue.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.minardwu.yiyue.model.ArtistBean;
import com.minardwu.yiyue.model.MusicBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by MinardWu on 2018/1/31.
 */

public class GetOnlineArtist {


    private static final String TAG = "GetOnlineArtist";
    private static final String URL_GET_ARTIST_ID_BY_NAME = "https://api.imjad.cn/cloudmusic/?type=search&searchtype=100&s=";
    private static final String URL_GET_ARTIST_INFO_BY_ID = "https://api.imjad.cn/cloudmusic/?type=artist&id=";

    public static void getArtistIdByName(String name, final HttpCallback<ArtistBean> callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder().url(URL_GET_ARTIST_ID_BY_NAME +"\""+name+"\"").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFail(e.toString());
                Log.e(TAG,e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.e(TAG,string);
                try {
                    JSONObject root = new JSONObject(string);
                    JSONObject result = root.getJSONObject("result");
                    JSONArray songs = result.getJSONArray("songs");
                    JSONObject song = songs.getJSONObject(0);
                    JSONArray ars = song.getJSONArray("ar");
                    JSONObject ar = ars.getJSONObject(0);
                    String id = ar.getString("id");
                    getArtistInfoById(id,callback);
                    Log.e(TAG,id);
                } catch (JSONException e) {
                    Log.e(TAG,e.toString());
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getArtistInfoById(String id, final HttpCallback<ArtistBean> callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(URL_GET_ARTIST_INFO_BY_ID+id).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFail(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.e(TAG,string);
                try {
                    JSONObject root = new JSONObject(string);
                    String code = root.getString("code");
                    if(code.equals("200")){
                        JSONObject artist = root.getJSONObject("artist");
                        String picUrl = artist.getString("picUrl");
                        String info = artist.getString("briefDesc");
                        String name = artist.getString("name");
                        String id = artist.getString("id");

                        ArrayList<MusicBean> list = new ArrayList<MusicBean>();
                        JSONArray hotSongs = root.getJSONArray("hotSongs");
                        for (int i=0;i<hotSongs.length();i++){
                            JSONObject hotSong = hotSongs.getJSONObject(i);
                            String songId = hotSong.getString("id");
                            String songName = hotSong.getString("name");
                            String songAlbumName = hotSong.getJSONObject("al").getString("name");
                            String songAlbumId = hotSong.getJSONObject("al").getString("id");
                            MusicBean musicBean = new MusicBean();
                            musicBean.setArtist(name);
                            musicBean.setId(Long.parseLong(songId));
                            musicBean.setTitle(songName);
                            musicBean.setAlbum(songAlbumName);
                            musicBean.setAlbumId(Long.parseLong(songAlbumId));
                            //Log.e(TAG,songAlbumName);
                            list.add(musicBean);
                        }
                        ArtistBean artistBean = new ArtistBean();
                        artistBean.setId(id);
                        artistBean.setName(name);
                        artistBean.setInfo(info);
                        artistBean.setPicUrl(picUrl);
                        artistBean.setSongs(list);
                        callback.onSuccess(artistBean);
                    }else {
                        callback.onFail("未找到");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG,e.toString());
                }
            }
        });
    }

    public static void getArtistPicById(String id, final HttpCallback<Bitmap> getPicCallback){
        getArtistInfoById(id, new HttpCallback<ArtistBean>() {
            @Override
            public void onSuccess(ArtistBean artistBean) {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(artistBean.getPicUrl()).build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        getPicCallback.onFail("111111"+e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        InputStream is = response.body().byteStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        is.close();
                        getPicCallback.onSuccess(bitmap);
                    }
                });
            }

            @Override
            public void onFail(String e) {
                getPicCallback.onFail("2222222"+e.toString());
            }
        });

    }

}
