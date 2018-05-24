package com.minardwu.yiyue.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.minardwu.yiyue.application.YiYueApplication;
import com.minardwu.yiyue.constants.NetWorkType;

/**
 * Created by MinardWu on 2018/1/29.
 */

public class NetWorkUtils {

    public static int getNetWorkType() {
        int netWorkType = NetWorkType.NO_NET;
        ConnectivityManager connectivityManager = (ConnectivityManager) YiYueApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo==null){
            netWorkType = NetWorkType.NO_NET;
        }else if (networkInfo != null && networkInfo.isConnected()) {
            int type = networkInfo.getType();
            if (type == ConnectivityManager.TYPE_WIFI) {
                netWorkType = NetWorkType.WIFI;
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                netWorkType = NetWorkType.MOBILE;
            }
        }
        return netWorkType;
    }

}
