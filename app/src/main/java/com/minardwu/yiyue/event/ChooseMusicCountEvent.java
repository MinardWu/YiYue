package com.minardwu.yiyue.event;

import com.minardwu.yiyue.model.MusicBean;

/**
 * Created by MinardWu on 2018/1/9.
 */

public class ChooseMusicCountEvent {

    int count;
    boolean isSelectAll;

    public ChooseMusicCountEvent(int count, boolean isSelectAll) {
        this.count = count;
        this.isSelectAll = isSelectAll;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isSelectAll() {
        return isSelectAll;
    }

    public void setSelectAll(boolean selectAll) {
        isSelectAll = selectAll;
    }
}
