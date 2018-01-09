package com.minardwu.yiyue.event;

import com.minardwu.yiyue.model.MusicBean;

/**
 * Created by MinardWu on 2018/1/9.
 */

public class ChageToolbarTextEvent {

    MusicBean musicBean;

    public ChageToolbarTextEvent(MusicBean musicBean) {
        this.musicBean = musicBean;
    }

    public MusicBean getMusicBean() {
        return musicBean;
    }

    public void setMusicBean(MusicBean musicBean) {
        this.musicBean = musicBean;
    }
}
