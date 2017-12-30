package com.minardwu.yiyue.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.activity.LocalMusicListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.wcy.lrcview.LrcView;


public class LocalMusicFragment extends Fragment implements View.OnClickListener {

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
        iv_local_music_player_playmode.setOnClickListener(this);
        iv_local_music_player_pre.setOnClickListener(this);
        iv_local_music_player_play.setOnClickListener(this);
        iv_local_music_player_next.setOnClickListener(this);
        iv_local_music_player_musiclist.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_local_music_player_playmode:

                break;
            case R.id.iv_local_music_player_pre:

                break;
            case R.id.iv_local_music_player_play:

                break;
            case R.id.iv_local_music_player_next:

                break;
            case R.id.iv_local_music_player_musiclist:
                getActivity().startActivity(new Intent(getContext(), LocalMusicListActivity.class));
                getActivity().overridePendingTransition(R.anim.activity_open,0);
                break;
        }
    }
}
