package com.minardwu.yiyue.model;

import java.util.List;

/**
 * Created by wumingyuan on 2018/3/23.
 */

public class AlbumBean {

    private String albumId;
    private String albumName;
    private String picUrl;
    private String info;
    private String company;
    private String subType;
    private int size;
    private long publishTime;
    private ArtistBean artist;
    private List<MusicBean> songs;

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public ArtistBean getArtist() {
        return artist;
    }

    public void setArtist(ArtistBean artist) {
        this.artist = artist;
    }

    public List<MusicBean> getSongs() {
        return songs;
    }

    public void setSongs(List<MusicBean> songs) {
        this.songs = songs;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }
}
