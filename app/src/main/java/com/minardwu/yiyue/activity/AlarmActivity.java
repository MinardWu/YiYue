package com.minardwu.yiyue.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.event.CloseAlarmClockEvent;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.utils.CoverLoader;
import com.minardwu.yiyue.utils.MusicUtils;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmActivity extends BaseActivity implements MediaPlayer.OnPreparedListener {

    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_tips)
    TextView tv_tips;
    @BindView(R.id.iv_alarm_bg)
    ImageView iv_alarm_bg;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);
        if(getIntent() != null){
            MusicBean alarmMusic = MusicUtils.getAlarmMusic(Preferences.getAlarmMusicId());
            if (alarmMusic == null){
                alarmMusic = MusicUtils.getDefaultMusic();
            }
            if(alarmMusic != null){
                iv_alarm_bg.setImageBitmap(CoverLoader.getInstance().loadThumbnail(alarmMusic));
                tv_title.setText(alarmMusic.getTitle());
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(alarmMusic.getPath());
                    mediaPlayer.setOnPreparedListener(this);
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                iv_alarm_bg.setImageDrawable(UIUtils.getDrawable(R.drawable.default_cover));
                tv_title.setText("暂无本地音乐哦");
            }
            //如果闹钟不是重复的话，响过一次后就把它关掉
            if(!Preferences.enableAlarmClockRepeat()){
                Preferences.saveAlarmClock(false);
                EventBus.getDefault().post(new CloseAlarmClockEvent());//刷新闹钟设置界面ui
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}
