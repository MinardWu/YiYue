package com.minardwu.yiyue.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wumingyuan on 2018/3/23.
 */

public class AlbumBean implements Parcelable {

    private String albumId;
    private String albumName;
    private String picUrl;
    private String info;
    private String company;
    private String subType;
    private int size;
    private long publishTime;
    private ArtistBean artist;
    private ArrayList<MusicBean> songs;

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

    public ArrayList<MusicBean> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<MusicBean> songs) {
        this.songs = songs;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.albumId);
        dest.writeString(this.albumName);
        dest.writeString(this.picUrl);
        dest.writeString(this.info);
        dest.writeString(this.company);
        dest.writeString(this.subType);
        dest.writeInt(this.size);
        dest.writeLong(this.publishTime);
        dest.writeParcelable(this.artist, flags);
        dest.writeList(this.songs);
    }

    public AlbumBean() {
    }

    protected AlbumBean(Parcel in) {
        this.albumId = in.readString();
        this.albumName = in.readString();
        this.picUrl = in.readString();
        this.info = in.readString();
        this.company = in.readString();
        this.subType = in.readString();
        this.size = in.readInt();
        this.publishTime = in.readLong();
        this.artist = in.readParcelable(ArtistBean.class.getClassLoader());
        this.songs = new ArrayList<MusicBean>();
        in.readList(this.songs, MusicBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<AlbumBean> CREATOR = new Parcelable.Creator<AlbumBean>() {
        @Override
        public AlbumBean createFromParcel(Parcel source) {
            return new AlbumBean(source);
        }

        @Override
        public AlbumBean[] newArray(int size) {
            return new AlbumBean[size];
        }
    };
}
