package com.minardwu.yiyue.http;

/**
 * Created by MinardWu on 2018/1/11.
 */

public interface HttpCallback<T> {

    void onSuccess(T t);

    void onFail(String e);

}
