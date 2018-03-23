package com.minardwu.yiyue.application;

import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.PlayOnlineMusicService;
import com.minardwu.yiyue.service.PlayLocalMusicService;
import com.minardwu.yiyue.service.PlayService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MinardWu on 2017/12/30.
 */

public class AppCache {

    private List<MusicBean> LocalMusicList = new ArrayList<MusicBean>();
    private static PlayService currentService;
    private PlayLocalMusicService playLocalMusicService;
    private PlayOnlineMusicService playOnlineMusicService;
    public static int defaultMusicId = 186016;

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

    public static PlayLocalMusicService getPlayLocalMusicService() {
        return getInstance().playLocalMusicService;
    }

    public static void setPlayLocalMusicService(PlayLocalMusicService service) {
        getInstance().playLocalMusicService = service;
    }

    public static PlayOnlineMusicService getPlayOnlineMusicService() {
        return getInstance().playOnlineMusicService;
    }

    public static void setPlayOnlineMusicService(PlayOnlineMusicService playOnlineMusicService) {
        getInstance().playOnlineMusicService = playOnlineMusicService;
    }

    public static PlayService getCurrentService(){
        return currentService;
    }

    public static void setCurrentService(PlayService service){
        currentService = service;
    }
}
