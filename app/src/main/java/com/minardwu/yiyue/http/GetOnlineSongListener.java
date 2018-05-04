package com.minardwu.yiyue.http;

import com.minardwu.yiyue.http.result.FailResult;
import com.minardwu.yiyue.model.MusicBean;

/**
 * Created by MinardWu on 2018/1/14.
 */

public interface GetOnlineSongListener {

    void onSuccess(MusicBean musicBean);

    void onFail(FailResult result);

    void execute(long id, boolean isClick);
}
