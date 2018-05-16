package com.minardwu.yiyue.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.activity.LocalMusicListActivity;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.enums.PlayModeEnum;
import com.minardwu.yiyue.event.ChageToolbarTextEvent;
import com.minardwu.yiyue.http.DownloadLrc;
import com.minardwu.yiyue.http.result.FailResult;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.OnPlayLocalMusicListener;
import com.minardwu.yiyue.service.PlayLocalMusicService;
import com.minardwu.yiyue.utils.CoverLoader;
import com.minardwu.yiyue.utils.FileUtils;
import com.minardwu.yiyue.utils.MusicUtils;
import com.minardwu.yiyue.utils.ParseUtils;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.utils.UIUtils;
import com.minardwu.yiyue.widget.LocalMusicCoverView;
import com.minardwu.yiyue.widget.LrcView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LocalMusicFragment extends Fragment implements View.OnClickListener,OnPlayLocalMusicListener, SeekBar.OnSeekBarChangeListener, LrcView.OnPlayClickListener{

    private int lastProgress;
    private boolean isDraggingProgress;
    private AlphaAnimation fade_in;
    private AlphaAnimation fade_out;

    @BindView(R.id.tv_local_music_artist) TextView tv_local_music_artist;
    @BindView(R.id.tv_local_music_current_time) TextView tv_current_time;
    @BindView(R.id.tv_local_music_total_time) TextView tv_total_time;
    @BindView(R.id.sb_local_music_progress)  SeekBar sb_progress;
    @BindView(R.id.iv_local_music_player_playmode) ImageView iv_local_music_player_playmode;
    @BindView(R.id.iv_local_music_player_pre) ImageView iv_local_music_player_pre;
    @BindView(R.id.iv_local_music_player_play) ImageView iv_local_music_player_play;
    @BindView(R.id.iv_local_music_player_next) ImageView iv_local_music_player_next;
    @BindView(R.id.iv_local_music_player_musiclist) ImageView iv_local_music_player_musiclist;
    @BindView(R.id.rl_lrc_and_cover) RelativeLayout rl_lrc_and_cover;
    @BindView(R.id.rl_cover_and_single_lrc)  RelativeLayout rl_cover_and_single_lrc;
    @BindView(R.id.ac_albumcover) LocalMusicCoverView ac_albumcover;
    @BindView(R.id.lrc_localmusic) LrcView lrc_localmusic;
    @BindView(R.id.lrc_localmusic_single) LrcView lrc_localmusic_single;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this,getView());
        getPlayService().setOnPlayEventListener(this);

        iv_local_music_player_playmode.setOnClickListener(this);
        iv_local_music_player_pre.setOnClickListener(this);
        iv_local_music_player_play.setOnClickListener(this);
        iv_local_music_player_next.setOnClickListener(this);
        iv_local_music_player_musiclist.setOnClickListener(this);

        sb_progress.setOnSeekBarChangeListener(this);
        lrc_localmusic.setOnPlayClickListener(this);

        iv_local_music_player_playmode.setImageLevel(Preferences.getLocalPlayMode());
        lrc_localmusic.setVisibility(View.GONE);
        rl_cover_and_single_lrc.setVisibility(View.VISIBLE);

        fade_in = (AlphaAnimation) AnimationUtils.loadAnimation(getContext(),R.anim.view_fade_in);
        fade_out = (AlphaAnimation) AnimationUtils.loadAnimation(getContext(),R.anim.view_fade_out);
        lrc_localmusic.setOnLrcViewForOutsideUseClickListener(new LrcView.OnLrcViewForOutsideUseClickListener() {
            @Override
            public void onClick(View view) {
                lrc_localmusic.startAnimation(fade_out);
                lrc_localmusic.setVisibility(View.GONE);
                rl_cover_and_single_lrc.startAnimation(fade_in);
                rl_cover_and_single_lrc.setVisibility(View.VISIBLE);
            }
        });

        rl_cover_and_single_lrc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_cover_and_single_lrc.startAnimation(fade_out);
                rl_cover_and_single_lrc.setVisibility(View.GONE);
                lrc_localmusic.startAnimation(fade_in);
                lrc_localmusic.setVisibility(View.VISIBLE);
            }
        });
        if(MusicUtils.getLocalMusicPlayingMusic()!=null){
            onChangeMusic(MusicUtils.getLocalMusicPlayingMusic());
        }
    }

    /**
     * 从TapeActivity返回重新设置
     */
    @Override
    public void onResume() {
        getPlayService().setOnPlayEventListener(this);
        if(MusicUtils.getLocalMusicPlayingMusic()!=null){
            onChangeMusic(MusicUtils.getLocalMusicPlayingMusic());
        }
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_local_music_player_playmode:
                switchPlayMode();
                break;
            case R.id.iv_local_music_player_pre:
                getPlayService().prev();
                break;
            case R.id.iv_local_music_player_play:
                getPlayService().playOrPause();
                break;
            case R.id.iv_local_music_player_next:
                getPlayService().next();
                break;
            case R.id.iv_local_music_player_musiclist:
                getActivity().startActivity(new Intent(getContext(), LocalMusicListActivity.class));
                getActivity().overridePendingTransition(R.anim.activity_open,0);
                break;
            default:
                break;
        }
    }

    public PlayLocalMusicService getPlayService() {
        PlayLocalMusicService playLocalMusicService = AppCache.getPlayLocalMusicService();
        if (playLocalMusicService == null) {
            throw new NullPointerException("play service is null");
        }
        return playLocalMusicService;
    }

    @Override
    public void onChangeMusic(MusicBean music) {
        if (music == null) {
            return;
        }
        //更新Toolbar的UI
        EventBus.getDefault().post(new ChageToolbarTextEvent(music));
        //更新播放界面UI
        tv_local_music_artist.setText(music.getArtistName());
        sb_progress.setMax((int) music.getDuration());
        sb_progress.setProgress((int) getPlayService().getCurrentPosition());
        sb_progress.setSecondaryProgress(0);
        lastProgress = 0;
        tv_current_time.setText(ParseUtils.formatTime("mm:ss",(int) getPlayService().getCurrentPosition()));
        tv_total_time.setText(ParseUtils.formatTime("mm:ss",music.getDuration()));
        ac_albumcover.setCoverBitmap(CoverLoader.getInstance().loadRound(music));
        ac_albumcover.start();
        setLrc(music);
        if (getPlayService().isPlaying() || getPlayService().isPreparing()) {
            iv_local_music_player_play.setSelected(true);
            ac_albumcover.start();
        } else {
            iv_local_music_player_play.setSelected(false);
            ac_albumcover.pause();
        }
    }

    @Override
    public void onPlayerStart() {
        iv_local_music_player_play.setSelected(true);
        ac_albumcover.start();
    }

    @Override
    public void onPlayerPause() {
        iv_local_music_player_play.setSelected(false);
        ac_albumcover.pause();
    }

    @Override
    public void onPublish(int progress) {
        if (!isDraggingProgress) {
            sb_progress.setProgress(progress);
        }
        lrc_localmusic.updateTime(progress);
        lrc_localmusic_single.updateTime(progress);
    }

    @Override
    public void onMusicListUpdate() {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == sb_progress) {
            if (Math.abs(progress - lastProgress) >= DateUtils.SECOND_IN_MILLIS) {
                tv_current_time.setText(ParseUtils.formatTime("mm:ss",progress));
                lastProgress = progress;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isDraggingProgress = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isDraggingProgress = false;
        if (getPlayService().isPlaying() || getPlayService().isPausing()) {
            int progress = seekBar.getProgress();
            getPlayService().seekTo(progress);
            lrc_localmusic.updateTime(progress);
            lrc_localmusic_single.updateTime(progress);
        } else {
            seekBar.setProgress(0);
        }
    }

    private void switchPlayMode() {
        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getLocalPlayMode());
        switch (mode) {
            case LOOP:
                mode = PlayModeEnum.SHUFFLE;
                ToastUtils.showShortToast(R.string.mode_shuffle);
                break;
            case SHUFFLE:
                mode = PlayModeEnum.SINGLE;
                ToastUtils.showShortToast(R.string.mode_one);
                break;
            case SINGLE:
                mode = PlayModeEnum.LOOP;
                ToastUtils.showShortToast(R.string.mode_loop);
                break;
            default:
                break;
        }
        Preferences.saveLocalPlayMode(mode.value());
        iv_local_music_player_playmode.setImageLevel(mode.value());
    }

    private void setLrc(final MusicBean music) {
        final String lrcPath;
        lrcPath = FileUtils.getLrcFilePath(music);
        if (lrcPath!=null) {
            loadLrc(lrcPath);
        } else {
            lrc_localmusic.setLabel(UIUtils.getString(R.string.lrc_searching));
            lrc_localmusic_single.setLabel(UIUtils.getString(R.string.lrc_searching));
            new DownloadLrc(music.getArtistName(),music.getTitle()){
                @Override
                public void downloadLrcPrepare() {
                    iv_local_music_player_play.setTag(music);//设置tag防止歌词下载完成后已切换歌曲
                }

                @Override
                public void downloadLrcSuccess() {
                    if(iv_local_music_player_play.getTag()!=music){//若已经切歌则不需要后续操作
                        return;
                    }
                    iv_local_music_player_play.setTag(null);//成功或失败后清除Tag
                    String lrc = FileUtils.getLrcFilePath(music);//下载成功后重新获取路径
                    loadLrc(lrc);
                }

                @Override
                public void downloadLrcFail(FailResult result) {
                    if(iv_local_music_player_play.getTag()!=music){//若已经切歌则不需要后续操作
                        return;
                    }
                    iv_local_music_player_play.setTag(null);//成功或失败后清除Tag
                    lrc_localmusic.setLabel(UIUtils.getString(R.string.lrc_can_not_find));
                    lrc_localmusic_single.setLabel(UIUtils.getString(R.string.lrc_can_not_find));
                }
            }.execute();
        }
    }

    private void loadLrc(String path) {
        File file = new File(path);
        lrc_localmusic.loadLrc(file);
        lrc_localmusic_single.loadLrc(file);
    }


    @Override
    public boolean onPlayClick(long time) {
        if (getPlayService().isPlaying() || getPlayService().isPausing()) {
            getPlayService().seekTo((int) time);
            if (getPlayService().isPausing()) {
                getPlayService().playOrPause();
            }
            return true;
        }
        return false;
    }

}
