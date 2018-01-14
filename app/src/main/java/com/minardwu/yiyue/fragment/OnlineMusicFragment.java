package com.minardwu.yiyue.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.OnPlayOnlineMusicListener;
import com.minardwu.yiyue.service.PlayOnlineMusicService;
import com.minardwu.yiyue.widget.LrcView;
import com.minardwu.yiyue.widget.OnlineMusicCoverView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OnlineMusicFragment extends Fragment implements OnPlayOnlineMusicListener, View.OnClickListener {


    @BindView(R.id.online_music_cover) OnlineMusicCoverView online_music_cover;
    @BindView(R.id.tv_online_music_title) TextView tv_online_music_title;
    @BindView(R.id.tv_online_music_artist) TextView tv_online_music_artist;
    @BindView(R.id.lrc_onlinelmusic) LrcView lrc_onlinelmusic;
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

    public void changeMusicImp(final MusicBean music) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                loadCoverByUrl(music.getCoverPath());
                tv_online_music_title.setText(music.getTitle());
                tv_online_music_artist.setText(music.getArtist());
                String lrc = music.getLrc();
                if(lrc.equals("1")){
                    lrc_onlinelmusic.setLabel("尚无歌词");
                }else if(lrc.equals("2")){
                    lrc_onlinelmusic.setLabel("纯音乐，请欣赏");
                }else{
                    lrc_onlinelmusic.loadLrc(lrc);
                }
            }
        });
    }

    @Override
    public void onChangeMusic(MusicBean music) {
        changeMusicImp(music);
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
        lrc_onlinelmusic.updateTime((long) (getPlayOnlineMusicService().getCurrentMusicDuration()*progress));
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

    public void loadCoverByUrl(final String url) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                URL myFileUrl = null;
                Bitmap bitmap = null;
                try {
                    myFileUrl = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                    final Bitmap finalBitmap = bitmap;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            online_music_cover.loadCover(finalBitmap);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return;
    }
}
