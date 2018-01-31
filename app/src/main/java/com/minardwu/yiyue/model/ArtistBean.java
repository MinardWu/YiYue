package com.minardwu.yiyue.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MinardWu on 2018/1/31.
 */

public class ArtistBean {

    private String id;
    private String name;
    private String info;
    private String picUrl;
    private ArrayList<MusicBean> songs;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public ArrayList<MusicBean> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<MusicBean> songs) {
        this.songs = songs;
    }
}
