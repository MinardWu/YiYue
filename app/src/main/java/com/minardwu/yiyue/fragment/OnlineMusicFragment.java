package com.minardwu.yiyue.fragment;

import android.content.Intent;
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
import com.minardwu.yiyue.activity.ArtistActivity;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.http.GetOnlineSong;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.OnPlayOnlineMusicListener;
import com.minardwu.yiyue.service.PlayOnlineMusicService;
import com.minardwu.yiyue.utils.Notifier;
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

    private MusicBean playingMusic;
    private boolean isLoveSong;

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
        tv_online_music_artist.setOnClickListener(this);
        iv_onlinemusic_play.setSelected(false);
        playingMusic = MyDatabaseHelper.init(getContext()).getFMHistoryLastSong();
        if(playingMusic==null){
            new GetOnlineSong() {
                @Override
                public void onSuccess(MusicBean musicBean) {
                    playingMusic = musicBean;
                    changeMusicImp(playingMusic);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            changeIconState(1);
                        }
                    });
                    Notifier.showPause(playingMusic);
                }

                @Override
                public void onFail(String string) {

                }
            }.exectue(100861);
        }else {
            changeMusicImp(playingMusic);
            changeIconState(1);
            Notifier.showPause(playingMusic);
        }

    }

    public void changeMusicImp(final MusicBean music) {
        playingMusic = music;
        isLoveSong = MyDatabaseHelper.init(getContext()).isLoveSong(playingMusic);
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                loadCoverByUrl(music.getCoverPath());
                tv_online_music_title.setText(music.getTitle());
                tv_online_music_artist.setText(music.getArtist());
                String lrc = music.getLrc();
                changeIconState(1);
                if(lrc!=null){
                    Log.e("lrc",lrc);
                    if(lrc.equals("1")){
                        lrc_onlinelmusic.setLabel("尚无歌词");
                    }else if(lrc.equals("2")){
                        lrc_onlinelmusic.setLabel("纯音乐，请欣赏");
                    }else{
                        lrc_onlinelmusic.loadLrc(lrc);
                    }
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
    public void onUpdatePosition(int position,String artistId) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_onlinemusic_download:
                isLoveSong = MyDatabaseHelper.init(getContext()).isLoveSong(playingMusic);
                if(isLoveSong){
                    iv_onlinemusic_download.setSelected(false);
                    MyDatabaseHelper.init(getContext()).deleteLoveSong(playingMusic);
                }else {
                    iv_onlinemusic_download.setSelected(true);
                    MyDatabaseHelper.init(getContext()).addLoveSong(playingMusic);
                }
                break;
            case R.id.iv_onlinemusic_play:
                getPlayOnlineMusicService().playOrPause((int) playingMusic.getId());
                break;
            case R.id.iv_onlinemusic_next:
                changeIconState(0);
                getPlayOnlineMusicService().next();
                break;
            case R.id.tv_online_music_artist:
                Intent intent = new Intent(getActivity(), ArtistActivity.class);
//                intent.putExtra("type",0);
                intent.putExtra("type",1);
                intent.putExtra("artistName",tv_online_music_artist.getText().toString());
                intent.putExtra("artistId",playingMusic.getArtistId());
                startActivity(intent);
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
                    //刷新notification的封面
                    playingMusic.setOnlineMusicCover(finalBitmap);
                    if (getPlayOnlineMusicService().isPlaying()){
                        Notifier.showPlay(playingMusic);
                    }else {
                        Notifier.showPause(playingMusic);
                    }
                    //刷新播放界面封面
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

    private void changeIconState(int state){
        if(state==0){
            iv_onlinemusic_download.setEnabled(false);
            iv_onlinemusic_play.setEnabled(false);
            iv_onlinemusic_next.setEnabled(false);
        }else if(state==1){
            iv_onlinemusic_download.setSelected(isLoveSong?true:false);
            iv_onlinemusic_download.setEnabled(true);
            iv_onlinemusic_play.setEnabled(true);
            iv_onlinemusic_next.setEnabled(true);
        }

    }

}
