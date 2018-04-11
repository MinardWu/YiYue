package com.minardwu.yiyue.http;

import com.minardwu.yiyue.http.result.FailResult;

/**
 * Created by MinardWu on 2018/1/3.
 */

public interface DownloadLrcListener {

    void execute();

    void downloadLrcPrepare();

    void downloadLrcSuccess();

    void downloadLrcFail(FailResult failResult);
}
