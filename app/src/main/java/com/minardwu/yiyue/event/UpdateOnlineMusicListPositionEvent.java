package com.minardwu.yiyue.event;

/**
 * Created by MinardWu on 2018/1/31.
 */

public class UpdateOnlineMusicListPositionEvent {

    String listId;
    int position;

    public UpdateOnlineMusicListPositionEvent(String listId) {
        this.listId = listId;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

}
