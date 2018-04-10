package com.minardwu.yiyue.http.result;

/**
 * Created by wumingyuan on 2018/4/10.
 */

public class FailResult {

    private int resultCode;
    private String exception;

    public FailResult(int resultCode, String exception) {
        this.resultCode = resultCode;
        this.exception = exception;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
