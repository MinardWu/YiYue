package com.minardwu.yiyue.application;

import android.app.Application;
import android.content.Context;

import com.minardwu.yiyue.utils.CoverLoader;
import com.minardwu.yiyue.utils.MusicUtils;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.utils.ToastUtils;

/**
 * Created by MinardWu on 2017/12/30.
 */

public class YiYueApplication extends Application{

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Preferences.init(getApplicationContext());
        ToastUtils.init(getApplicationContext());
        CoverLoader.getInstance().init(getApplicationContext());
        AppCache.getLocalMusicList().clear();
        AppCache.getLocalMusicList().addAll(MusicUtils.scanMusic(getApplicationContext()));
    }

    public static Context getAppContext(){
        return context;
    }
}
