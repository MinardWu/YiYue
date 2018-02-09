package com.minardwu.yiyue.application;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.minardwu.yiyue.utils.CoverLoader;
import com.minardwu.yiyue.utils.MusicUtils;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.utils.ToastUtils;

import okhttp3.OkHttpClient;

/**
 * Created by MinardWu on 2017/12/30.
 */

public class YiYueApplication extends Application{

    private static Context context;
    public static boolean isJustIntoAppAndNotPlay;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Preferences.init(getApplicationContext());
        ToastUtils.init(getApplicationContext());
        CoverLoader.getInstance().init(getApplicationContext());
        AppCache.getLocalMusicList().clear();
        AppCache.getLocalMusicList().addAll(MusicUtils.scanMusic(getApplicationContext()));
        Stetho.initializeWithDefaults(this);
        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        isJustIntoAppAndNotPlay = true;
    }

    public static Context getAppContext(){
        return context;
    }
}
