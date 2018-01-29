package com.minardwu.yiyue.model;

/**
 * Created by MinardWu on 2017/12/29.
 */

public class DrawerItemBean {

    private int type;
    private int imgId;
    private String title;
    private String info;
    private boolean state;

    public DrawerItemBean(int type, int imgId, String title, String info, boolean state) {
        this.type = type;
        this.imgId = imgId;
        this.title = title;
        this.info = info;
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
