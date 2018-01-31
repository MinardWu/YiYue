package com.minardwu.yiyue.service;

import com.minardwu.yiyue.model.MusicBean;

/**
 * Created by MinardWu on 2018/1/11.
 */

public interface OnPlayOnlineMusicListener {

    /**
     * 切换歌曲
     */
    void onChangeMusic(MusicBean music);

    /**
     * 继续播放
     */
    void onPlayerStart();

    /**
     * 暂停播放
     */
    void onPlayerPause();

    /**
     * 更新进度
     */
    void onPublish(float progress);

    /**
     * 缓冲百分比
     */
    void onBufferingUpdate(int percent);

    /**
     * 更新定时停止播放时间
     */
    void onTimer(long remain);


    /**
     * 更新歌手歌曲列表播放歌曲，加上artistId是保证更新的是当前播放歌手的列表
     */
    void onUpdatePosition(int position,String artistId);

}
