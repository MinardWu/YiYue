package com.minardwu.yiyue.http;

import android.os.Handler;
import android.os.Looper;

import com.minardwu.yiyue.http.result.FailResult;
import com.minardwu.yiyue.http.result.ResultCode;
import com.minardwu.yiyue.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by MinardWu on 2018/1/3.
 */

public abstract class DownloadLrc implements DownloadLrcListener {

    private String artist;
    private String title;

    public DownloadLrc(String artist, String title) {
        this.artist = artist;
        this.title = title;
    }

    @Override
    public void execute() {
        downloadLrcPrepare();
        getLrcUrl();
    }

    private void getLrcUrl() {
        final String getLrcUrl = "http://gecimi.com/api/lyric/" + title + "/" + artist;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(getLrcUrl).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        downloadLrcFail(new FailResult(ResultCode.NETWORK_ERROR, e.toString()));
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject root = new JSONObject(response.body().string());
                    if (root.getInt("count") == 0) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                downloadLrcFail(new FailResult(ResultCode.GCW_LRC_NO_FOUND, "0 result"));
                            }
                        });
                        return;
                    } else {
                        JSONArray jsonArray = root.getJSONArray("result");
                        String lrcUrl = jsonArray.getJSONObject(0).getString("lrc");
                        downloadLrc(lrcUrl, FileUtils.getLrcDir());
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            downloadLrcFail(new FailResult(ResultCode.GCW_GET_LRC_ERROR, e.toString()));
                        }
                    });
                }
            }
        });
    }


    private void downloadLrc(String lrcUrl, final String lrcDir) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(lrcUrl).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        downloadLrcFail(new FailResult(ResultCode.NETWORK_ERROR, e.toString()));
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file = new File(lrcDir, artist + " - " + title + ".lrc");
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            downloadLrcSuccess();
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            downloadLrcFail(new FailResult(ResultCode.GCW_CREATE_LRC_FILE_ERROR, e.toString()));
                        }
                    });
                } finally {
                    try {
                        if (is != null)
                            is.close();
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

}
