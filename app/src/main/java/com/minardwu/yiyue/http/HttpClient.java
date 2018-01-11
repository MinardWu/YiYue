package com.minardwu.yiyue.http;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by MinardWu on 2018/1/11.
 */

public class HttpClient {

    private static final String TAG="HttpClient";

    private HttpClient(){}

    private static HttpClient httpClient;

    public static HttpClient getInstance(){
        if(httpClient==null){
            synchronized (HttpClient.class){
                if(httpClient==null){
                    httpClient = new HttpClient();
                }
            }
        }
        return httpClient;
    }

    private static final String GET_SONG_URL_BY_ID="https://api.imjad.cn/cloudmusic/?type=song&id=";
    OkHttpClient okHttpClient = new OkHttpClient();

    public void getSongUrlById(final int id, final HttpCallback<String> callback){
        String url;
        Request request = new Request.Builder().url(GET_SONG_URL_BY_ID+id).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFail(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject root = new JSONObject(response.body().string());
                    JSONObject data = root.getJSONArray("data").getJSONObject(0);
                    String url = data.getString("url");
                    if(url.equals("")){
                        Log.e(TAG,"url为空，找不到歌曲");
                        getSongUrlById(id+1,callback);
                    }else {
                        callback.onSuccess(url);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onFail(e.toString());
                }
            }
        });
    }

}
