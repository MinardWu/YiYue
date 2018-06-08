package com.minardwu.yiyue.model;

import com.minardwu.yiyue.utils.UIUtils;

/**
 * Created by MinardWu on 2017/12/29.
 */

public class DrawerItemBean {

    /**
     * 0 普通
     * 1 开关
     * 2 间隔
     */
    private int type;
    private int imgId;
    private int titleId;
    private String info;
    private boolean state;

    public DrawerItemBean(int type, int imgId, int titleId, String info, boolean state) {
        this.type = type;
        this.imgId = imgId;
        this.titleId = titleId;
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

    public int getTitleId() {
        return titleId;
    }

    public void setTitleId(int titleId) {
        this.titleId = titleId;
    }

    public String getTitle() {
        return UIUtils.getString(titleId);
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
