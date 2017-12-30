package com.minardwu.yiyue.application;

import android.app.Application;

import com.minardwu.yiyue.model.MusicBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MinardWu on 2017/12/30.
 */

public class AppCache {

    private List<MusicBean> LocalMusicList = new ArrayList<MusicBean>();

    private AppCache(){}

    private static class SingletonHolder {
        private static AppCache appCache = new AppCache();
    }

    private static AppCache getInstance() {
        return SingletonHolder.appCache;
    }

    public static List<MusicBean> getLocalMusicList() {
        return getInstance().LocalMusicList;
    }
}
