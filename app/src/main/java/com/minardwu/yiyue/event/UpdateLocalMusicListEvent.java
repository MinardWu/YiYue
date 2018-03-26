package com.minardwu.yiyue.event;

import com.minardwu.yiyue.model.MusicBean;

/**
 * Created by MinardWu on 2018/1/9.
 */

public class UpdateLocalMusicListEvent {

    private int update;

    public UpdateLocalMusicListEvent(int update) {
        this.update = update;
    }

    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
    }
}
