package com.minardwu.yiyue.http;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.minardwu.yiyue.http.mock.MockData;
import com.minardwu.yiyue.http.result.FailResult;
import com.minardwu.yiyue.http.result.ResultCode;
import com.minardwu.yiyue.model.ArtistBean;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.utils.Preferences;

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
        void onFail(FailResult result);
    }

    private static final String SEARCH_URL = "https://api.imjad.cn/cloudmusic/?type=search&searchtype=1&s=";
    private static final String TAG = "Search";

    public static void serach(String s, final SearchCallback searchCallback){
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder().url(SEARCH_URL+"\""+s+"\"").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        searchCallback.onFail(new FailResult(ResultCode.NETWORK_ERROR,e.toString()));
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final List<MusicBean> musicBeanList = new ArrayList<MusicBean>();
                String res = response.body().string();
                if(Preferences.enableUseMockSearchData()){
                    res = MockData.searchResult;
                }
                Log.e(TAG,res);
                try {
                    JSONObject root = new JSONObject(res);
                    JSONObject result = root.getJSONObject("result");
                    JSONArray songs = result.getJSONArray("songs");
                    for(int i=0;i<songs.length();i++){
                        MusicBean musicBean = new MusicBean();
                        final ArtistBean artistBean = new ArtistBean();

                        JSONObject song = songs.getJSONObject(i);
                        String songId = song.getString("id");
                        String songName = song.getString("name");
                        String songAlbumName = song.getJSONObject("al").getString("name");
                        String songAlbumId = song.getJSONObject("al").getString("id");
                        String songArtistName = song.getJSONArray("ar").getJSONObject(0).getString("name");
                        String songArtistId = song.getJSONArray("ar").getJSONObject(0).getString("id");
                        String artistId = songs.getJSONObject(0).getJSONArray("ar").getJSONObject(0).getString("id");
                        String artistName = songs.getJSONObject(0).getJSONArray("ar").getJSONObject(0).getString("name");

                        musicBean.setId(Long.parseLong(songId));
                        musicBean.setTitle(songName);
                        musicBean.setArtistName(songArtistName);
                        musicBean.setArtistId(songArtistId);
                        musicBean.setAlbum(songAlbumName);
                        musicBean.setAlbumId(songAlbumId);
                        musicBeanList.add(musicBean);
                        artistBean.setName(artistName);
                        artistBean.setId(artistId);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                searchCallback.onSuccess(musicBeanList,artistBean);
                            }
                        });
                    }
                } catch (final JSONException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            searchCallback.onFail(new FailResult(ResultCode.SEARCH_ERROR,e.toString()));
                        }
                    });
                }
            }
        });
    }




}
