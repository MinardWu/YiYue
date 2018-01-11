package com.minardwu.yiyue.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.OnPlayOnlineMusicListener;
import com.minardwu.yiyue.service.PlayOnlineMusicService;
import com.minardwu.yiyue.widget.OnlineMusicCoverView;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OnlineMusicFragment extends Fragment implements OnPlayOnlineMusicListener, View.OnClickListener {


    @BindView(R.id.online_music_cover) OnlineMusicCoverView online_music_cover;
    @BindView(R.id.iv_onlinemusic_download) ImageView iv_onlinemusic_download;
    @BindView(R.id.iv_onlinemusic_play) ImageView iv_onlinemusic_play;
    @BindView(R.id.iv_onlinemusic_next) ImageView iv_onlinemusic_next;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_online_music, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this,getView());
        AppCache.getPlayOnlineMusicService().setPlayOnlineMusicListener(this);
        iv_onlinemusic_download.setOnClickListener(this);
        iv_onlinemusic_play.setOnClickListener(this);
        iv_onlinemusic_next.setOnClickListener(this);
        iv_onlinemusic_play.setSelected(false);
    }

    @Override
    public void onChangeMusic(MusicBean music) {

    }

    @Override
    public void onPlayerStart() {
        iv_onlinemusic_play.setSelected(true);
    }

    @Override
    public void onPlayerPause() {
        iv_onlinemusic_play.setSelected(false);
    }

    @Override
    public void onPublish(float progress) {
        online_music_cover.update(progress);
    }

    @Override
    public void onBufferingUpdate(int percent) {

    }

    @Override
    public void onTimer(long remain) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_onlinemusic_download:

                break;
            case R.id.iv_onlinemusic_play:
                getPlayOnlineMusicService().playOrPause();
                break;
            case R.id.iv_onlinemusic_next:
                getPlayOnlineMusicService().next();
                break;
        }
    }

    private PlayOnlineMusicService getPlayOnlineMusicService(){
        return AppCache.getPlayOnlineMusicService();
    }
}
