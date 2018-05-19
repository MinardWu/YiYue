package com.minardwu.yiyue.service;

import com.minardwu.yiyue.model.MusicBean;

import java.util.List;

/**
 * Created by MinardWu on 2018/1/11.
 */

public interface OnPlayOnlineMusicListener {

    /**
     * 开始获取数据
     */
    void onPrepareStart();

    /**
     * 获取数据结束
     */
    void onPrepareStop();

    /**
     * 切换歌曲
     * @param music 切换的歌曲
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
     * @param progress 进度
     */
    void onPublish(float progress);

    /**
     * 缓冲百分比
     * @param percent 百分比
     */
    void onBufferingUpdate(int percent);

    /**
     * 加载歌曲失败
     * @param resultCode 错误码
     */
    void onGetSongError(int resultCode);

    /**
     * 更新在线歌曲列表
     * @param list 音乐列表
     */
    void onUpdateOnlineMusicList(List<MusicBean> list);

}
