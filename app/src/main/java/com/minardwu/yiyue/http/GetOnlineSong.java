package com.minardwu.yiyue.http;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.minardwu.yiyue.constants.Lrc;
import com.minardwu.yiyue.http.result.FailResult;
import com.minardwu.yiyue.http.result.ResultCode;
import com.minardwu.yiyue.model.MusicBean;

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

public abstract class GetOnlineSong implements GetOnlineSongListener {

    private static final String TAG="GetOnlineSong";
    private static final String GET_SONG_URL_BY_ID="https://api.imjad.cn/cloudmusic/?type=song&id=";
    private static final String GET_SONG_DETAIL_BY_ID="https://api.imjad.cn/cloudmusic/?type=detail&id=";
    private static final String GET_SONG_LRC_BY_ID="https://api.imjad.cn/cloudmusic/?type=lyric&id=";

    OkHttpClient okHttpClient = new OkHttpClient();

    /**
     * @param id 歌曲id
     * @param isClick 用户是点击具体歌曲还是fm过来的
     */
    @Override
    public void execute(long id, boolean isClick) {
        getSongUrlById(id,isClick);
    }

    /**
     * 获取歌曲url
     * @param id 歌曲id
     */
    public void getSongUrlById(final long id, final boolean isClick){
        final MusicBean musicBean = new MusicBean();
        final long[] temp = new long[1];
        temp[0] = id;
        String url;
        Request request = new Request.Builder().url(GET_SONG_URL_BY_ID+id).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onFail(new FailResult(ResultCode.NETWORK_ERROR,e.toString()));
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject root = new JSONObject(response.body().string());
                    JSONObject data = root.getJSONArray("data").getJSONObject(0);
                    String url = data.getString("url");
                    if(url.equals("")){
                        //如果是点击过来的则报无版权，不会接着往下找
                        if(isClick){
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    onFail(new FailResult(ResultCode.GET_SONG_NO_COPYRIGHT,"no copyright"));
                                }
                            });
                        }else {
                            Log.e(TAG,"url为空，找不到歌曲");
                            temp[0] = id+10;
                            getSongUrlById(id+10,isClick);
                        }
                    }else {
                        musicBean.setPath(url);
                        getSongDetailById(temp[0],musicBean);
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            onFail(new FailResult(ResultCode.GET_URL_ERROR,e.toString()));
                        }
                    });
                }
            }
        });
    }

    /**
     * 获取歌曲名字，歌手，封面信息
     * @param id 歌曲id

     */
    public void getSongDetailById(final long id, final MusicBean musicBean){
        Request request = new Request.Builder().url(GET_SONG_DETAIL_BY_ID+id).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onFail(new FailResult(ResultCode.NETWORK_ERROR,e.toString()));
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String i = response.body().string();
                    //Log.e(TAG,i);
                    JSONObject root = new JSONObject(i);
                    JSONObject detail = root.getJSONArray("songs").getJSONObject(0);
                    JSONObject artistInfo = detail.getJSONArray("ar").getJSONObject(0);
                    JSONObject albumInfo = detail.getJSONObject("al");
                    String title = detail.getString("name");
                    String artist = artistInfo.getString("name");
                    String artistId = artistInfo.getString("id");
                    String picUrl = albumInfo.getString("picUrl");
                    String albumName = albumInfo.getString("name");
                    String albumId = albumInfo.getString("id");
                    musicBean.setId(id);
                    musicBean.setTitle(title);
                    musicBean.setArtistName(artist);
                    musicBean.setArtistId(artistId);
                    musicBean.setCoverPath(picUrl);
                    musicBean.setAlbum(albumName);
                    musicBean.setAlbumId(albumId);
                    musicBean.setType(MusicBean.Type.ONLINE);
                    getSongLrcById(id,musicBean);
                } catch (final JSONException e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            onFail(new FailResult(ResultCode.GET_DETAIL_ERROR,e.toString()));
                        }
                    });
                }
            }
        });
    }

    /**
     * 获取歌词
     * @param id 歌曲id
     * @param musicBean 歌曲实体，这个是为了获得上一个函数中或得到的music
     */
    public void getSongLrcById(final long id,final MusicBean musicBean){
        Request request = new Request.Builder().url(GET_SONG_LRC_BY_ID+id).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onFail(new FailResult(ResultCode.NETWORK_ERROR,e.toString()));
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject root = new JSONObject(response.body().string());
                    Log.e(TAG,root.toString());
                    if(root.has("uncollected")){//无歌词
                        musicBean.setLrc(Lrc.LRC_NO_EXIST);
                    }else if(root.has("nolyric")){//纯音乐无歌词
                        musicBean.setLrc(Lrc.LRC_PURE_MUSIC);
                    }else {
                        JSONObject lrc = root.getJSONObject("lrc");
                        String lyric = lrc.getString("lyric");
                        musicBean.setLrc(lyric);
                    }
                    Log.e(TAG,musicBean.getTitle());
                    Log.e(TAG,musicBean.getArtistName());
                    Log.e(TAG,musicBean.getCoverPath());
                    Log.e(TAG,musicBean.getPath());
                    Log.e(TAG,musicBean.getLrc());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            onSuccess(musicBean);
                        }
                    });
                } catch (final JSONException e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            //onFail(GET_LRC_ERROR);
                            //获取不到歌词依旧下发
                            onSuccess(musicBean);
                            Log.e(TAG,"getSongLrcById-onResponse："+e.toString());
                        }
                    });
                }
            }
        });
    }
}
