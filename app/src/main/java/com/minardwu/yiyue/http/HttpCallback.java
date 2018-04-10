package com.minardwu.yiyue.http;

import com.minardwu.yiyue.http.result.FailResult;

/**
 * Created by MinardWu on 2018/1/11.
 */

public interface HttpCallback<T> {

    void onSuccess(T t);

    void onFail(FailResult result);

}
