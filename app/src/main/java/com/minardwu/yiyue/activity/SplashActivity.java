package com.minardwu.yiyue.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.service.PlayOnlineMusicService;
import com.minardwu.yiyue.service.PlayService;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity implements ServiceConnection {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if(AppCache.getPlayService()!=null&&AppCache.getPlayOnlineMusicService()!=null){
            startMainActivity();
            finish();
        }else {
            //启动并绑定音乐播放服务
            startService(new Intent(this, PlayService.class));
            bindService(new Intent(this, PlayService.class),this,BIND_AUTO_CREATE);
            //启动并绑定音乐播放服务
            startService(new Intent(this, PlayOnlineMusicService.class));
            bindService(new Intent(this, PlayOnlineMusicService.class),this,BIND_AUTO_CREATE);
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
    }

    /**
     * 绑定后回调获取Service并赋值给AppCache方便后面使用
     */
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        String tag = iBinder.getClass()+"";
        Log.e("ServiceTag",tag);
        if(tag.equals("class com.minardwu.yiyue.service.PlayService$PlayBinder")){
            final PlayService playService = ((PlayService.PlayBinder) iBinder).getService();
            AppCache.setPlayService(playService);
        }else {
            PlayOnlineMusicService service = ((PlayOnlineMusicService.PlayBinder) iBinder).getService();
            AppCache.setPlayOnlineMusicService(service);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    private void startMainActivity() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.putExtras(getIntent());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(this);
    }
}
