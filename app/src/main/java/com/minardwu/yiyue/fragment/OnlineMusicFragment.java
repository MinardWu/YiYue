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
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.activity.ArtistActivity;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.http.result.FailResult;
import com.minardwu.yiyue.http.GetOnlineSong;
import com.minardwu.yiyue.http.result.ResultCode;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.OnPlayOnlineMusicListener;
import com.minardwu.yiyue.service.PlayOnlineMusicService;
import com.minardwu.yiyue.utils.Notifier;
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.utils.UIUtils;
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

    private static final String TAG = "OnlineMusicFragment" ;
    private MusicBean playingMusic;
    private boolean isLoveSong;
    private AnimationSet unloveAnimation;
    private AnimationSet loveAnimation;

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
            changeIconState(0);
            setFirstInData();
        }else {
            changeIconState(1);
            changeMusicImp(playingMusic);
            Notifier.showPause(playingMusic);
        }
        unloveAnimation = (AnimationSet) AnimationUtils.loadAnimation(getContext(),R.anim.action_unlove);
        loveAnimation = (AnimationSet) AnimationUtils.loadAnimation(getContext(),R.anim.action_love);
    }

    public void changeMusicImp(final MusicBean music) {
        playingMusic = music;
        isLoveSong = MyDatabaseHelper.init(getContext()).isCollectedSong(playingMusic);
        iv_onlinemusic_download.setSelected(isLoveSong ? true:false);
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                loadCoverByUrl(music.getCoverPath());
                tv_online_music_title.setText(music.getTitle());
                tv_online_music_artist.setText(music.getArtistName());
                String lrc = music.getLrc();
                if(lrc!=null){
                    Log.v("lrc",lrc);
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
    public void onPrepareStart() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                changeIconState(0);
            }
        });
    }

    @Override
    public void onPrepareStop() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                changeIconState(1);
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
    public void onGetSongError(int resultCode) {
        switch (resultCode){
            case ResultCode.NETWORK_ERROR:
                ToastUtils.show(UIUtils.getString(getContext(),R.string.network_error));
                break;
            case ResultCode.GET_URL_ERROR:
            case ResultCode.GET_DETAIL_ERROR:
            case ResultCode.GET_LRC_ERROR:
                ToastUtils.show(UIUtils.getString(getContext(),R.string.server_error));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_onlinemusic_download:
                isLoveSong = MyDatabaseHelper.init(getContext()).isCollectedSong(playingMusic);
                if(isLoveSong){
                    iv_onlinemusic_download.startAnimation(unloveAnimation);
                    iv_onlinemusic_download.setSelected(false);
                    MyDatabaseHelper.init(getContext()).deleteCollectedSong(playingMusic);
                }else {
                    iv_onlinemusic_download.startAnimation(loveAnimation);
                    iv_onlinemusic_download.setSelected(true);
                    MyDatabaseHelper.init(getContext()).addCollectedSong(playingMusic);
                }
                break;
            case R.id.iv_onlinemusic_play:
                if(playingMusic==null){
                    getPlayOnlineMusicService().play(AppCache.defaultMusicId);
                }else {
                    getPlayOnlineMusicService().playOrPause((int)playingMusic.getId());
                }
                break;
            case R.id.iv_onlinemusic_next:
                getPlayOnlineMusicService().next();
                break;
            case R.id.tv_online_music_artist:
                Intent intent = new Intent(getActivity(), ArtistActivity.class);
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
            iv_onlinemusic_download.setSelected(isLoveSong?true:false);
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

    /**
     * 用户在播放当前歌曲时进入收藏界面取消收藏正在播放的音乐，出来后应该更新界面
     */
    @Override
    public void onResume() {
        super.onResume();
        if(playingMusic!=null){
            isLoveSong = MyDatabaseHelper.init(getContext()).isCollectedSong(playingMusic);
            iv_onlinemusic_download.setSelected(isLoveSong?true:false);
        }
    }

    private void setFirstInData(){
        new GetOnlineSong() {
            @Override
            public void onSuccess(MusicBean musicBean) {
                playingMusic = musicBean;
                changeMusicImp(playingMusic);
                changeIconState(1);
                Notifier.showPause(playingMusic);
            }

            @Override
            public void onFail(FailResult failResult) {
                //Log.e(TAG,string);
                changeIconState(1);
                ToastUtils.show("服务器出小差了");
            }
        }.exectue(AppCache.defaultMusicId);
    }

}
