package com.minardwu.yiyue.http;

import com.minardwu.yiyue.model.MusicBean;

/**
 * Created by MinardWu on 2018/1/14.
 */

public interface HttpListener {

    void onSuccess(MusicBean musicBean);

    void onFail(String string);

    void exectue(int id);
}
