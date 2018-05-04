package com.minardwu.yiyue.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 单曲信息
 * Created by wcy on 2015/11/27.
 */
public class MusicBean implements Parcelable {
    //歌曲类型:本地/网络
    private Type type;

    //歌曲名
    private String title;

    //歌曲id
    private long id;

    //歌手名
    private String artistName;

    //歌手id
    private String artistId;

    //专辑名
    private String album;

    //专辑id
    private String albumId;

    //专辑封面路径
    private String coverPath;

    //歌曲时间
    private long duration;

    //音乐路径
    private String path;

    //文件名
    private String fileName;

    //文件大小
    private long fileSize;

    //歌词
    private String lrc;

    //网络歌曲封面
    private Bitmap onlineMusicCover;

    //添加时间
    private long addTime;

    public enum Type {
        LOCAL,
        ONLINE
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }

    public Bitmap getOnlineMusicCover() {
        return onlineMusicCover;
    }

    public void setOnlineMusicCover(Bitmap onlineMusicCover) {
        this.onlineMusicCover = onlineMusicCover;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    /**
     * 对比本地歌曲是否相同
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof MusicBean && this.getId() == ((MusicBean) o).getId();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.artistName);
        dest.writeString(this.artistId);
        dest.writeString(this.album);
        dest.writeString(this.albumId);
        dest.writeString(this.coverPath);
        dest.writeLong(this.duration);
        dest.writeString(this.path);
        dest.writeString(this.fileName);
        dest.writeLong(this.fileSize);
        dest.writeString(this.lrc);
        dest.writeParcelable(this.onlineMusicCover, flags);
        dest.writeLong(this.addTime);
    }

    public MusicBean() {
    }

    protected MusicBean(Parcel in) {
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : Type.values()[tmpType];
        this.id = in.readLong();
        this.title = in.readString();
        this.artistName = in.readString();
        this.artistId = in.readString();
        this.album = in.readString();
        this.albumId = in.readString();
        this.coverPath = in.readString();
        this.duration = in.readLong();
        this.path = in.readString();
        this.fileName = in.readString();
        this.fileSize = in.readLong();
        this.lrc = in.readString();
        this.onlineMusicCover = in.readParcelable(Bitmap.class.getClassLoader());
        this.addTime = in.readLong();
    }

    public static final Parcelable.Creator<MusicBean> CREATOR = new Parcelable.Creator<MusicBean>() {
        @Override
        public MusicBean createFromParcel(Parcel source) {
            return new MusicBean(source);
        }

        @Override
        public MusicBean[] newArray(int size) {
            return new MusicBean[size];
        }
    };
}
