package com.minardwu.yiyue.http;

import com.minardwu.yiyue.http.result.FailResult;
import com.minardwu.yiyue.model.MusicBean;

/**
 * Created by MinardWu on 2018/1/14.
 */

public interface GetOnlineSongListener {

    /**
     * 获取成功
     * @param musicBean
     */
    void onSuccess(MusicBean musicBean);

    /**
     * 获取失败
     * @param result
     */
    void onFail(FailResult result);

    /**
     * 发起获取歌曲的网络请求
     * @param id
     * @param isClick
     */
    void execute(long id, boolean isClick);
}
