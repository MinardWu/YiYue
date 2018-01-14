package com.minardwu.yiyue.http;

import android.util.Log;

import com.minardwu.yiyue.model.MusicBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by MinardWu on 2018/1/14.
 */

public abstract class GetOnlineSong implements HttpListener{

    private static final String TAG="GetOnlineSong";
    private static final String GET_SONG_URL_BY_ID="https://api.imjad.cn/cloudmusic/?type=song&id=";
    private static final String GET_SONG_DETAIL_BY_ID="https://api.imjad.cn/cloudmusic/?type=detail&id=";
    private static final String GET_SONG_LRC_BY_ID="https://api.imjad.cn/cloudmusic/?type=lyric&id=";

    OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    public void exectue(int id) {
        getSongUrlById(id);
    }

    public void getSongUrlById(final int id){
        final int[] temp = new int[1];
        temp[0] = id;
        String url;
        Request request = new Request.Builder().url(GET_SONG_URL_BY_ID+id).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onFail(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject root = new JSONObject(response.body().string());
                    JSONObject data = root.getJSONArray("data").getJSONObject(0);
                    String url = data.getString("url");
                    if(url.equals("")){
                        Log.e(TAG,"url为空，找不到歌曲");
                        temp[0] = id+1;
                        getSongUrlById(id+1);
                    }else {
                        getSongDetailById(temp[0],url);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    onFail(e.toString());
                }
            }
        });
    }

    public void getSongDetailById(final int id, final String musicUrl){
        final MusicBean musicBean = new MusicBean();
        Request request = new Request.Builder().url(GET_SONG_DETAIL_BY_ID+id).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onFail(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String i = response.body().string();
//                    Log.e(TAG,i);
                    JSONObject root = new JSONObject(i);
                    JSONObject detail = root.getJSONArray("songs").getJSONObject(0);
                    JSONObject artistInfo = detail.getJSONArray("ar").getJSONObject(0);
                    JSONObject albumInfo = detail.getJSONObject("al");
                    String title = detail.getString("name");
                    String artist = artistInfo.getString("name");
                    String picUrl = albumInfo.getString("picUrl");
                    Log.e(TAG,title);
                    Log.e(TAG,artist);
                    Log.e(TAG,picUrl);
                    musicBean.setTitle(title);
                    musicBean.setArtist(artist);
                    musicBean.setCoverPath(picUrl);
                    musicBean.setPath(musicUrl);
//                    onSuccess(musicBean);
                    getSongLrcById(id,musicBean);
                } catch (JSONException e) {
                    e.printStackTrace();
                    onFail(e.toString());
                }
            }
        });
    }

    public void getSongLrcById(final int id,final MusicBean musicBean){
        Request request = new Request.Builder().url(GET_SONG_LRC_BY_ID+id).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onFail(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject root = new JSONObject(response.body().string());
                    Log.e(TAG,root.toString());
                    if(root.has("uncollected")){//无歌词
                        musicBean.setLrc("1");
                    }else if(root.has("nolyric")){//纯音乐无歌词
                        musicBean.setLrc("2");
                    }else {
                        JSONObject lrc = root.getJSONObject("lrc");
                        String lyric = lrc.getString("lyric");
                        musicBean.setLrc(lyric);
                    }
                    onSuccess(musicBean);
                } catch (JSONException e) {
                    e.printStackTrace();
                    onFail(e.toString());
                }
            }
        });
    }
}
