package com.minardwu.yiyue.http;

import android.os.Looper;
import android.util.Log;

import com.minardwu.yiyue.model.AlbumBean;
import com.minardwu.yiyue.model.ArtistBean;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wumingyuan on 2018/3/23.
 */

public class GetOnlineAlbum {

    private static final String URL_GET_ALBUM_BY_ID = "https://api.imjad.cn/cloudmusic/?type=album&id=" ;

    public static void getOnlineAlbum(String albumId, final HttpCallback<AlbumBean> callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder().url(URL_GET_ALBUM_BY_ID+albumId).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("GetOnlineAlbum",e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.e("GetOnlineAlbum",res);
                try {
                    JSONObject root = new JSONObject(res);
                    JSONArray songs = root.getJSONArray("songs");
                    JSONObject album = root.getJSONObject("album");
                    JSONObject artist = album.getJSONObject("artist");
                    String albumId = album.getString("id");
                    String albumName = album.getString("name");
                    String albumPicUrl = album.getString("picUrl");
                    String albumInfo = album.getString("description");
                    String albumSubType = album.getString("subType");
                    String albumCompany = album.getString("company");
                    String albumArtistName = artist.getString("name");
                    String albumArtistId = artist.getString("id");
                    String albumArtistPicUrl = artist.getString("picUrl");
                    long albumPublishTime = album.getLong("publishTime");
                    int size = album.getInt("size");

                    List<MusicBean> list = new ArrayList<MusicBean>();
                    for (int i=0;i<size;i++){
                        JSONObject song = songs.getJSONObject(i);
                        String songId = song.getString("id");
                        String songName = song.getString("name");
                        String songAlbumName = song.getJSONObject("al").getString("name");
                        String songAlbumId = song.getJSONObject("al").getString("id");
                        MusicBean musicBean = new MusicBean();
                        musicBean.setArtist(albumArtistName);
                        musicBean.setArtistId(albumArtistId);
                        musicBean.setId(Long.parseLong(songId));
                        musicBean.setTitle(songName);
                        musicBean.setAlbum(songAlbumName);
                        musicBean.setAlbumId(songAlbumId);
                        list.add(musicBean);
                    }

                    ArtistBean artistBean = new ArtistBean();
                    artistBean.setId(albumArtistId);
                    artistBean.setName(albumArtistName);
                    artistBean.setPicUrl(albumArtistPicUrl);

                    final AlbumBean albumBean = new AlbumBean();
                    albumBean.setArtist(artistBean);
                    albumBean.setSongs(list);
                    albumBean.setAlbumId(albumId);
                    albumBean.setAlbumName(albumName);
                    albumBean.setPicUrl(albumPicUrl);
                    albumBean.setCompany(albumCompany);
                    albumBean.setPublishTime(albumPublishTime);
                    albumBean.setInfo(albumInfo);
                    albumBean.setSubType(albumSubType);
                    albumBean.setSize(size);
                    new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(albumBean);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onFail(e.toString());
                }
            }
        });
    }

}
