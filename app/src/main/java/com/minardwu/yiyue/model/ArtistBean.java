package com.minardwu.yiyue.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MinardWu on 2018/1/31.
 */

public class ArtistBean implements Parcelable {

    private String id;
    private String name;
    private String info;
    private String picUrl;
    private int musicSize;
    private int albumSize;
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

    public int getMusicSize() {
        return musicSize;
    }

    public void setMusicSize(int musicSize) {
        this.musicSize = musicSize;
    }

    public int getAlbumSize() {
        return albumSize;
    }

    public void setAlbumSize(int albumSize) {
        this.albumSize = albumSize;
    }

    public ArrayList<MusicBean> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<MusicBean> songs) {
        this.songs = songs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.info);
        dest.writeString(this.picUrl);
        dest.writeInt(this.musicSize);
        dest.writeInt(this.albumSize);
        dest.writeTypedList(this.songs);
    }

    public ArtistBean() {
    }

    protected ArtistBean(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.info = in.readString();
        this.picUrl = in.readString();
        this.musicSize = in.readInt();
        this.albumSize = in.readInt();
        this.songs = in.createTypedArrayList(MusicBean.CREATOR);
    }

    public static final Parcelable.Creator<ArtistBean> CREATOR = new Parcelable.Creator<ArtistBean>() {
        @Override
        public ArtistBean createFromParcel(Parcel source) {
            return new ArtistBean(source);
        }

        @Override
        public ArtistBean[] newArray(int size) {
            return new ArtistBean[size];
        }
    };
}
