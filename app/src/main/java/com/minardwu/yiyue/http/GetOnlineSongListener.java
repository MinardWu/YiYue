package com.minardwu.yiyue.http;

import com.minardwu.yiyue.model.MusicBean;

/**
 * Created by MinardWu on 2018/1/14.
 */

public interface GetOnlineSongListener {

    void onSuccess(MusicBean musicBean);

    void onFail(int resultCode);

    void exectue(int id);
}
