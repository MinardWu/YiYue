package com.minardwu.yiyue.event;

/**
 * Created by MinardWu on 2018/1/11.
 */

public class StopPlayOnlineMusicServiceEvent {
    int flag;

    public StopPlayOnlineMusicServiceEvent(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
