package com.minardwu.yiyue.activity;

import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.OrientationEventListener;
import android.view.WindowManager;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.OnPlayLocalMusicListener;
import com.minardwu.yiyue.utils.MusicUtils;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.widget.TapeView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TapeActivity extends BaseActivity implements OnPlayLocalMusicListener {

    @BindView(R.id.tape)
    TapeView tapeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tape);
        ButterKnife.bind(this);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getPlayLocalMusicService().setOnPlayEventListener(this);
        MusicBean music = getPlayLocalMusicService().getPlayingMusic();
        if(MusicUtils.getLocalMusicPlayingMusic()!=null){
            onChangeMusic(MusicUtils.getLocalMusicPlayingMusic());
        }
        if(getPlayLocalMusicService().isPlaying()){
            tapeView.startRotate();
        }else {
            tapeView.stopRotate();
        }
        tapeView.setOnGearClickListener(new TapeView.OnGearClickListener() {
            @Override
            public void leftGearClick() {
                tapeView.startAccelerate();
                getPlayLocalMusicService().next();
            }

            @Override
            public void rightGearClick() {
                tapeView.startAccelerate();
                getPlayLocalMusicService().prev();
            }

            @Override
            public void otherAreaClick() {
                getPlayLocalMusicService().playOrPause();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkOrientation();
    }

    private void checkOrientation(){
        OrientationEventListener mOrientationListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                //设置横屏、方向横屏
                if (orientation > 225 && orientation < 315) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else if (orientation > 45 && orientation < 135) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }

                if (orientation<45 || orientation>135&&orientation<225 || orientation>315){
                    finish();
                }
            }
        };

        if (mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        } else {
            mOrientationListener.disable();
        }
    }

    @Override
    public void onChangeMusic(MusicBean music) {
        tapeView.setTitle(music.getTitle());
        tapeView.setArtis(music.getArtistName());
    }

    @Override
    public void onPlayerStart() {
        tapeView.startRotate();
    }

    @Override
    public void onPlayerPause() {
        tapeView.stopRotate();
    }

    @Override
    public void onPublish(int progress) {

    }

    @Override
    public void onMusicListUpdate() {

    }

    @Override
    public void onBackPressed() {

    }
}
