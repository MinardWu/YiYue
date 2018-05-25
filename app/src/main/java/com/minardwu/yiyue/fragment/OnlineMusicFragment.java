package com.minardwu.yiyue.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.minardwu.yiyue.constants.Lrc;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.http.result.FailResult;
import com.minardwu.yiyue.http.GetOnlineSong;
import com.minardwu.yiyue.http.result.ResultCode;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.OnPlayOnlineMusicListener;
import com.minardwu.yiyue.service.PlayOnlineMusicService;
import com.minardwu.yiyue.utils.FileUtils;
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
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author MinardWu
 */

public class OnlineMusicFragment extends Fragment implements OnPlayOnlineMusicListener, View.OnClickListener {

    @BindView(R.id.online_music_cover) OnlineMusicCoverView online_music_cover;
    @BindView(R.id.tv_online_music_title) TextView tv_online_music_title;
    @BindView(R.id.tv_online_music_artist) TextView tv_online_music_artist;
    @BindView(R.id.lrc_onlinelmusic) LrcView lrc_onlinelmusic;
    @BindView(R.id.iv_onlinemusic_download) ImageView iv_onlinemusic_download;
    @BindView(R.id.iv_onlinemusic_play) ImageView iv_onlinemusic_play;
    @BindView(R.id.iv_onlinemusic_next) ImageView iv_onlinemusic_next;
    @BindView(R.id.iv_online_music_list) ImageView iv_online_music_list;

    private static final String TAG = "OnlineMusicFragment" ;
    private MusicBean playingMusic;
    private boolean isLoveSong;
    private AnimationSet unloveAnimation;
    private AnimationSet loveAnimation;
    private ArrayList<MusicBean> playList;
    private OnlineMusicListDialogFragment onlineMusicListDialogFragment;
    private boolean isDialogShow;

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
        lrc_onlinelmusic.setCanTouch(false);
        iv_onlinemusic_download.setOnClickListener(this);
        iv_onlinemusic_play.setOnClickListener(this);
        iv_onlinemusic_next.setOnClickListener(this);
        tv_online_music_artist.setOnClickListener(this);
        iv_online_music_list.setOnClickListener(this);
        iv_onlinemusic_play.setSelected(false);
        unloveAnimation = (AnimationSet) AnimationUtils.loadAnimation(getContext(),R.anim.action_unlove);
        loveAnimation = (AnimationSet) AnimationUtils.loadAnimation(getContext(),R.anim.action_love);

        playingMusic = MyDatabaseHelper.init(getContext()).getFMHistoryLastSong();
        playList = MyDatabaseHelper.init(getContext()).queryOnlineMusicList();
        onlineMusicListDialogFragment = OnlineMusicListDialogFragment.newInstance(playList);
        if(playList.size()!=0){
            getPlayOnlineMusicService().replaceMusicList(playList);
        }
        if(playingMusic==null){
            changeIconState(0);
            setFirstInData();
        }else {
            changeIconState(1);
            changeMusicImp(playingMusic);
            Notifier.showPause(playingMusic);
            getPlayOnlineMusicService().setPlayingMusic(playingMusic);
        }
        iv_online_music_list.setVisibility(playList.size() == 0 ? View.GONE :View.VISIBLE);
    }

    public void changeMusicImp(final MusicBean music) {
        playingMusic = music;
        isLoveSong = MyDatabaseHelper.init(getContext()).isCollectedSong(playingMusic);
        iv_onlinemusic_download.setSelected(isLoveSong);
        tv_online_music_title.setText(music.getTitle());
        tv_online_music_artist.setText(music.getArtistName());
        String lrc = music.getLrc();
        if(lrc!=null){
            if(lrc.equals(Lrc.LRC_NO_EXIST)){
                lrc_onlinelmusic.setLabel(UIUtils.getString(R.string.lrc_no_exist));
            }else if(lrc.equals(Lrc.LRC_PURE_MUSIC)){
                lrc_onlinelmusic.setLabel(UIUtils.getString(R.string.lrc_pure_music));
            }else{
                lrc_onlinelmusic.loadLrc(lrc);
                FileUtils.saveLrc(lrc,music);
            }
        }
        loadCoverByUrl(music.getCoverPath());
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
    public void onGetSongError(int resultCode) {
        handleError(resultCode);
    }

    @Override
    public void onUpdateOnlineMusicList(List<MusicBean> list) {
        playList.clear();
        playList.addAll(list);
        iv_online_music_list.setVisibility(playList.size() == 0 ? View.GONE :View.VISIBLE);
        onlineMusicListDialogFragment.updateMusicList(list);
        if(isDialogShow && list.size()==0){
            onlineMusicListDialogFragment.dismiss();
            isDialogShow = false;
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
                    ToastUtils.showShortToast(R.string.delete_collected_song_success);
                }else {
                    iv_onlinemusic_download.startAnimation(loveAnimation);
                    iv_onlinemusic_download.setSelected(true);
                    MyDatabaseHelper.init(getContext()).addCollectedSong(playingMusic);
                    ToastUtils.showShortToast(R.string.collected_song_success);
                }
                break;
            case R.id.iv_onlinemusic_play:
                if(playingMusic==null){
                    getPlayOnlineMusicService().play(AppCache.defaultMusicId);
                }else {
                    getPlayOnlineMusicService().playOrPause(playingMusic.getId());
                }
                break;
            case R.id.iv_onlinemusic_next:
                getPlayOnlineMusicService().next();
                break;
            case R.id.iv_online_music_list:
                isDialogShow = true;
                onlineMusicListDialogFragment = OnlineMusicListDialogFragment.newInstance(playList);
                onlineMusicListDialogFragment.show(getFragmentManager(), "OnlineMusicListDialogFragment");
                break;
            case R.id.tv_online_music_artist:
                Intent intent = new Intent(getActivity(), ArtistActivity.class);
                intent.putExtra(ArtistActivity.ARTIST_ID,playingMusic.getArtistId());
                intent.putExtra(ArtistActivity.ARTIST_NAME,tv_online_music_artist.getText().toString());
                startActivity(intent);
                break;
            default:
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
                if(myFileUrl!=null){
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
                        if(getActivity()!=null){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    online_music_cover.loadCover(finalBitmap);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void changeIconState(int state){
        if(state==0){
            iv_onlinemusic_download.setSelected(isLoveSong);
            iv_onlinemusic_download.setEnabled(false);
            iv_onlinemusic_play.setEnabled(false);
            iv_onlinemusic_next.setEnabled(false);
        }else if(state==1){
            iv_onlinemusic_download.setSelected(isLoveSong);
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
            iv_onlinemusic_download.setSelected(isLoveSong);
        }
    }

    private void setFirstInData(){
        new GetOnlineSong() {
            @Override
            public void onSuccess(MusicBean musicBean) {
                playingMusic = musicBean;
                changeMusicImp(playingMusic);
                getPlayOnlineMusicService().setPlayingMusic(playingMusic);
                MyDatabaseHelper.init(getContext()).addFMHistory(musicBean);
                changeIconState(1);
                Notifier.showPause(playingMusic);
            }

            @Override
            public void onFail(FailResult failResult) {
                handleError(failResult.getResultCode());
            }
        }.execute(AppCache.defaultMusicId,false);
    }

    private void handleError(int resultCode){
        changeIconState(1);
        switch (resultCode){
            case ResultCode.NETWORK_ERROR:
                ToastUtils.showShortToast(UIUtils.getString(R.string.network_error));
                break;
            case ResultCode.GET_URL_ERROR:
            case ResultCode.GET_DETAIL_ERROR:
            case ResultCode.GET_LRC_ERROR:
                ToastUtils.showShortToast(UIUtils.getString(R.string.server_error));
            default:
                ToastUtils.showShortToast(UIUtils.getString(R.string.server_error));
                break;
        }
    }

}
