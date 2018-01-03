package com.minardwu.yiyue.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.service.PlayService;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity implements ServiceConnection {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //启动并绑定音乐播放服务
        startService(new Intent(this, PlayService.class));
        bindService(new Intent(this, PlayService.class),this,BIND_AUTO_CREATE);
        //一秒后进入主页面
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        },1000);
    }

    /**
     * 绑定后回调获取Service并赋值给AppCache方便后面使用
     */
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        final PlayService playService = ((PlayService.PlayBinder) iBinder).getService();
        AppCache.setPlayService(playService);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }
}