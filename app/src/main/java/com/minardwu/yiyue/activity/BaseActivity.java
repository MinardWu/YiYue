package com.minardwu.yiyue.activity;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.os.Build;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.application.YiYueApplication;
import com.minardwu.yiyue.service.PlayLocalMusicService;
import com.minardwu.yiyue.service.PlayOnlineMusicService;

/**
 * @author MinardWu
 * @date : 2017/12/30
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        //4.4以上支持沉浸式，进行设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }

    public Context getContext(){
        return this;
    }

    public Context getApplacationContext(){
        return YiYueApplication.getAppContext();
    }

    public PlayLocalMusicService getPlayLocalMusicService() {
        PlayLocalMusicService playLocalMusicService = AppCache.getPlayLocalMusicService();
        if (playLocalMusicService == null) {
            throw new NullPointerException("local service is null");
        }
        return playLocalMusicService;
    }

    public PlayOnlineMusicService getPlayOnlineMusicService() {
        PlayOnlineMusicService playOnlineMusicService = AppCache.getPlayOnlineMusicService();
        if (playOnlineMusicService == null) {
            throw new NullPointerException("online service is null");
        }
        return playOnlineMusicService;
    }

    public void setStatusBarDarkModeForM(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        }
    }

    protected void log(String string){
        Log.e(getClass().getName(),string);
    }
}
