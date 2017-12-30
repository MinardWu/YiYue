package com.minardwu.yiyue.model;

/**
 * Created by MinardWu on 2017/12/29.
 */

public class DrawerItemBean {

    private int resId;
    private String text;

    public DrawerItemBean(int resId, String text) {
        this.resId = resId;
        this.text = text;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
