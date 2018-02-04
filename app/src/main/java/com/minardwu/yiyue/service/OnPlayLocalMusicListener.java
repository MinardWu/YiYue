package com.minardwu.yiyue.service;


import com.minardwu.yiyue.model.MusicBean;

/**
 * 播放进度监听器
 */
public interface OnPlayLocalMusicListener {

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
    void onPublish(int progress);

    /**
     * 缓冲百分比
     */
    void onBufferingUpdate(int percent);

    /**
     * 更新定时停止播放时间
     */
    void onTimer(long remain);

    /**
     * 更新本地音乐
     */
    void onMusicListUpdate();
}
