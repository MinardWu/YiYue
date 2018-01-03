package com.minardwu.yiyue.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.activity.LocalMusicListActivity;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.application.YiYueApplication;
import com.minardwu.yiyue.enums.PlayModeEnum;
import com.minardwu.yiyue.http.DownloadLrc;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.OnPlayerEventListener;
import com.minardwu.yiyue.service.PlayService;
import com.minardwu.yiyue.utils.FileUtils;
import com.minardwu.yiyue.utils.ParseUtils;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.utils.ToastUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.wcy.lrcview.LrcView;


public class LocalMusicFragment extends Fragment implements View.OnClickListener,OnPlayerEventListener, SeekBar.OnSeekBarChangeListener, LrcView.OnPlayClickListener{

    private int lastProgress;
    private boolean isDraggingProgress;

    @BindView(R.id.iv_local_music_player_playmode) ImageView iv_local_music_player_playmode;
    @BindView(R.id.iv_local_music_player_pre) ImageView iv_local_music_player_pre;
    @BindView(R.id.iv_local_music_player_play) ImageView iv_local_music_player_play;
    @BindView(R.id.iv_local_music_player_next) ImageView iv_local_music_player_next;
    @BindView(R.id.iv_local_music_player_musiclist) ImageView iv_local_music_player_musiclist;
    @BindView(R.id.tv_local_music_current_time) TextView tv_current_time;
    @BindView(R.id.tv_local_music_total_time) TextView tv_total_time;
//    @BindView(R.id.tv_song)  private TextView tv_song;
//    @BindView(R.id.tv_singer)  private TextView tv_singer;
//    @BindView(R.id.ac_albumcover)  private AlbumCoverView ac_albumcover;
    @BindView(R.id.lrc_localmusic)  LrcView lrc_localmusic;
    @BindView(R.id.sb_local_music_progress)  SeekBar sb_progress;

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

        iv_local_music_player_playmode.setImageLevel(Preferences.getPlayMode());

        String lrcPath = FileUtils.getLrcFilePath(AppCache.getLocalMusicList().get(Preferences.getCurrentSongPosition()));
        if (lrcPath!=null) {
            loadLrc(lrcPath);
        }
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
        }
    }

    public PlayService getPlayService() {
        PlayService playService = AppCache.getPlayService();
        if (playService == null) {
            throw new NullPointerException("play service is null");
        }
        return playService;
    }

    @Override
    public void onChangeMusic(MusicBean music) {
        if (music == null) {
            return;
        }
//        tv_song.setText(music.getTitle());
//        tv_singer.setText(music.getArtist());
        sb_progress.setProgress((int) getPlayService().getCurrentPosition());
        sb_progress.setSecondaryProgress(0);
        sb_progress.setMax((int) music.getDuration());
        lastProgress = 0;
        tv_current_time.setText(R.string.play_time_start);
        tv_total_time.setText(ParseUtils.formatTime("mm:ss",music.getDuration()));
//        ac_albumcover.start();
//        setCoverAndBg(music);
        //如果是刚刚进入app而且没有点击过播放按钮或是上下一首，而且此时该文件已有歌词文件，则不需要加载歌词，因为在onCreate已经加载过了
        //其他情况就都要重新获取歌词
        if(YiYueApplication.isJustIntoAppAndNotPlay){
            String lrcPath = FileUtils.getLrcFilePath(AppCache.getLocalMusicList().get(Preferences.getCurrentSongPosition()));
            if(lrcPath!=null){
                //这种情况就不用加载了
            }else {
                setLrc(music);
            }
            YiYueApplication.isJustIntoAppAndNotPlay = false;
        }else {
            setLrc(music);
        }
//        if (getPlayService().isPlaying() || getPlayService().isPreparing()) {
//            iv_local_music_player_play.setSelected(true);
//            ac_albumcover.start();
//        } else {
//            iv_local_music_player_play.setSelected(false);
//            ac_albumcover.pause();
//        }
    }

    @Override
    public void onPlayerStart() {
        iv_local_music_player_play.setSelected(true);
//        ac_albumcover.start();
    }

    @Override
    public void onPlayerPause() {
        iv_local_music_player_play.setSelected(false);
//        ac_albumcover.pause();
    }

    @Override
    public void onPublish(int progress) {
        if (!isDraggingProgress) {
            sb_progress.setProgress(progress);
        }
        lrc_localmusic.updateTime(progress);
    }

    @Override
    public void onBufferingUpdate(int percent) {
        sb_progress.setSecondaryProgress(sb_progress.getMax() * 100 / percent);
    }

    @Override
    public void onTimer(long remain) {

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
        if (seekBar == sb_progress) {
            isDraggingProgress = true;
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == sb_progress) {
            isDraggingProgress = false;
            if (getPlayService().isPlaying() || getPlayService().isPausing()) {
                int progress = seekBar.getProgress();
                getPlayService().seekTo(progress);
                lrc_localmusic.updateTime(progress);
            } else {
                seekBar.setProgress(0);
            }
        }
    }

    private void switchPlayMode() {
        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getPlayMode());
        switch (mode) {
            case LOOP:
                mode = PlayModeEnum.SHUFFLE;
                ToastUtils.show(R.string.mode_shuffle);
                break;
            case SHUFFLE:
                mode = PlayModeEnum.SINGLE;
                ToastUtils.show(R.string.mode_one);
                break;
            case SINGLE:
                mode = PlayModeEnum.LOOP;
                ToastUtils.show(R.string.mode_loop);
                break;
        }
        Preferences.savePlayMode(mode.value());
        iv_local_music_player_playmode.setImageLevel(Preferences.getPlayMode());
    }

    private void setLrc(final MusicBean music) {
        if (music.getType() == MusicBean.Type.LOCAL) {
            final String lrcPath;
            lrcPath = FileUtils.getLrcFilePath(music);
            if (lrcPath!=null) {
                loadLrc(lrcPath);
            } else {
                loadLrc("");
                setLrcLabel("正在搜索歌词...");
                new DownloadLrc(music.getArtist(),music.getTitle()){
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
                    public void downloadLrcFail(String e) {
                        if(iv_local_music_player_play.getTag()!=music){//若已经切歌则不需要后续操作
                            return;
                        }
                        iv_local_music_player_play.setTag(null);//成功或失败后清除Tag
                        setLrcLabel("找不到歌词");
                    }
                }.execute();
            }
        } else {
            String lrcPath = FileUtils.getLrcDir() + FileUtils.getLrcFileName(music.getArtist(), music.getTitle());
            loadLrc(lrcPath);
        }
    }

    private void loadLrc(String path) {
        File file = new File(path);
        lrc_localmusic.loadLrc(file);
    }

    private void setLrcLabel(String label) {
        lrc_localmusic.setLabel(label);
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
