package com.minardwu.yiyue.event;

/**
 * Created by MinardWu on 2018/1/31.
 */

public class UpdateOnlineMusicListPositionEvent {

    String artistId;
    int position;

    public UpdateOnlineMusicListPositionEvent(String artistId, int position) {
        this.artistId = artistId;
        this.position = position;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
