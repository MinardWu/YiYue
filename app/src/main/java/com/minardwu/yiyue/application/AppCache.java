package com.minardwu.yiyue.application;

import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.PlayService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MinardWu on 2017/12/30.
 */

public class AppCache {

    private List<MusicBean> LocalMusicList = new ArrayList<MusicBean>();
    private PlayService playService;

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

    public static PlayService getPlayService() {
        return getInstance().playService;
    }

    public static void setPlayService(PlayService service) {
        getInstance().playService = service;
    }
}
