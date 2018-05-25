package com.minardwu.yiyue.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.YiYueApplication;
import com.minardwu.yiyue.http.mock.MockData;
import com.minardwu.yiyue.http.result.FailResult;
import com.minardwu.yiyue.http.result.ResultCode;
import com.minardwu.yiyue.model.ArtistBean;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.utils.UIUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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
            public void onFailure(Call call, final IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFail(new FailResult(ResultCode.NETWORK_ERROR,e.toString()));
                    }
                });
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
                } catch (final JSONException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFail(new FailResult(ResultCode.GET_ARTIST_INFO_ERROR,e.toString()));
                        }
                    });
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
            public void onFailure(Call call, final IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFail(new FailResult(ResultCode.NETWORK_ERROR,e.toString()));
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                if(Preferences.enableUseMockArtistData()){
                    string = MockData.artistInfo;
                }
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
                        int musicSize = artist.getInt("musicSize");
                        int albumSize = artist.getInt("albumSize");

                        ArrayList<MusicBean> list = new ArrayList<MusicBean>();
                        JSONArray hotSongs = root.getJSONArray("hotSongs");
                        for (int i=0;i<hotSongs.length();i++){
                            JSONObject hotSong = hotSongs.getJSONObject(i);
                            String songId = hotSong.getString("id");
                            String songName = hotSong.getString("name");
                            String songCoverUrl = hotSong.getJSONObject("al").getString("picUrl");
                            String songAlbumName = hotSong.getJSONObject("al").getString("name");
                            String songAlbumId = hotSong.getJSONObject("al").getString("id");
                            MusicBean musicBean = new MusicBean();
                            musicBean.setType(MusicBean.Type.ONLINE);
                            musicBean.setArtistId(id);
                            musicBean.setArtistName(name);
                            musicBean.setId(Long.parseLong(songId));
                            musicBean.setTitle(songName);
                            musicBean.setCoverPath(songCoverUrl);
                            musicBean.setAlbum(songAlbumName);
                            musicBean.setAlbumId(songAlbumId);
                            //Log.e(TAG,songAlbumName);
                            list.add(musicBean);
                        }
                        final ArtistBean artistBean = new ArtistBean();
                        artistBean.setId(id);
                        artistBean.setName(name);
                        artistBean.setInfo(info);
                        artistBean.setPicUrl(picUrl);
                        artistBean.setSongs(list);
                        artistBean.setMusicSize(musicSize);
                        artistBean.setAlbumSize(albumSize);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(artistBean);
                            }
                        });
                    }else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFail(new FailResult(ResultCode.GET_ARTIST_NO_FOUND,
                                        UIUtils.getString(R.string.artist_no_found)));
                            }
                        });
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFail(new FailResult(ResultCode.GET_ARTIST_INFO_ERROR,e.toString()));
                        }
                    });
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
                    public void onFailure(Call call, final IOException e) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                getPicCallback.onFail(new FailResult(ResultCode.NETWORK_ERROR,e.toString()));
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        InputStream is = response.body().byteStream();
                        final Bitmap bitmap = BitmapFactory.decodeStream(is);
                        is.close();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                getPicCallback.onSuccess(bitmap);
                            }
                        });
                    }
                });
            }

            @Override
            public void onFail(final FailResult result) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        getPicCallback.onFail(result);
                    }
                });
            }
        });

    }

}
