package com.minardwu.yiyue.http;

import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

import com.minardwu.yiyue.model.ArtistBean;
import com.minardwu.yiyue.model.MusicBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by MinardWu on 2018/2/10.
 */

public class Search {

    public interface SearchCallback{
        void onSuccess(List<MusicBean> list, ArtistBean artistBean);
        void onFail(String e);
    }

    private static final String SEARCH_URL = "https://api.imjad.cn/cloudmusic/?type=search&searchtype=1&s=";
    private static final String TAG = "Search";

    public static void serach(String s, final SearchCallback searchCallback){
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder().url(SEARCH_URL+"\""+s+"\"").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                searchCallback.onFail(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                List<MusicBean> musicBeanList = new ArrayList<MusicBean>();
                String res = response.body().string();
                Log.e(TAG,res);
                try {
                    JSONObject root = new JSONObject(res);
                    JSONObject result = root.getJSONObject("result");
                    JSONArray songs = result.getJSONArray("songs");
                    for(int i=0;i<songs.length();i++){
                        MusicBean musicBean = new MusicBean();
                        ArtistBean artistBean = new ArtistBean();

                        JSONObject song = songs.getJSONObject(i);
                        String songId = song.getString("id");
                        String songName = song.getString("name");
                        String songAlbumName = song.getJSONObject("al").getString("name");
                        String songAlbumId = song.getJSONObject("al").getString("id");
                        String songArtist = song.getJSONArray("ar").getJSONObject(0).getString("name");
                        String artistId = songs.getJSONObject(0).getJSONArray("ar").getJSONObject(0).getString("id");
                        String artistName = songs.getJSONObject(0).getJSONArray("ar").getJSONObject(0).getString("name");

                        musicBean.setId(Long.parseLong(songId));
                        musicBean.setTitle(songName);
                        musicBean.setArtist(songArtist);
                        musicBean.setAlbum(songAlbumName);
                        musicBean.setAlbumId(songAlbumId);
                        musicBeanList.add(musicBean);
                        artistBean.setName(artistName);
                        artistBean.setId(artistId);
                        Log.e(TAG,songName+"="+artistName);
                        searchCallback.onSuccess(musicBeanList,artistBean);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }




}
