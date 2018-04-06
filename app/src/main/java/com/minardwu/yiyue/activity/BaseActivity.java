package com.minardwu.yiyue.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.os.Build;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.service.PlayLocalMusicService;
import com.minardwu.yiyue.service.PlayOnlineMusicService;

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
}
