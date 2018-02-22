package com.minardwu.yiyue.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.service.PlayLocalMusicService;
import com.minardwu.yiyue.service.PlayOnlineMusicService;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity implements ServiceConnection {

    @BindView(R.id.app_name)
    TextView app_name;
    @BindView(R.id.tv_copyright)
    TextView tv_copyright;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        //启动并绑定音乐播放服务
        startService(new Intent(this, PlayLocalMusicService.class));
        bindService(new Intent(this, PlayLocalMusicService.class),this,BIND_AUTO_CREATE);
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
        Animation fade_in = (AlphaAnimation) AnimationUtils.loadAnimation(this,R.anim.splash_textview_fade_in);
        app_name.startAnimation(fade_in);
        tv_copyright.startAnimation(fade_in);
    }

    /**
     * 绑定后回调获取Service并赋值给AppCache方便后面使用
     */
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        String tag = iBinder.getClass()+"";
        Log.e("ServiceTag",tag);
        if(tag.equals("class com.minardwu.yiyue.service.PlayLocalMusicService$PlayBinder")){
            final PlayLocalMusicService playLocalMusicService = ((PlayLocalMusicService.PlayBinder) iBinder).getService();
            AppCache.setPlayLocalMusicService(playLocalMusicService);
        }else {
            PlayOnlineMusicService service = ((PlayOnlineMusicService.PlayBinder) iBinder).getService();
            AppCache.setPlayOnlineMusicService(service);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }
}
