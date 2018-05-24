package com.minardwu.yiyue.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.fragment.OnlineMusicListDialogFragment;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.PlayOnlineMusicService;
import com.minardwu.yiyue.utils.UIUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author MinardWu
 * @date : 2018/4/13
 */

public class OnlineMusicListAdapter extends RecyclerView.Adapter {

    private List<MusicBean> list;
    private Context context;
    private OnlineMusicListDialogFragment fragment;
    private PlayOnlineMusicService playOnlineMusicService;

    public OnlineMusicListAdapter(Context context, OnlineMusicListDialogFragment fragment,List<MusicBean> list) {
        this.list = list;
        this.fragment = fragment;
        this.context = context;
        playOnlineMusicService = AppCache.getPlayOnlineMusicService();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_online_music_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MusicBean musicBean = list.get(position);
        final boolean isPlayingMusic = playOnlineMusicService.getPlayingMusicId()==list.get(position).getId() ? true :false;
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.tv_title.setText(musicBean.getTitle());
        viewHolder.tv_artist.setText(context.getResources().getString(R.string.online_music_list_artist,musicBean.getArtistName()));
        viewHolder.tv_title.setTextColor(isPlayingMusic ? UIUtils.getColor(R.color.green_main) : UIUtils.getColor(R.color.black));
        viewHolder.tv_artist.setTextColor(isPlayingMusic ? UIUtils.getColor(R.color.green_main) : UIUtils.getColor(R.color.grey));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlayingMusic){
                    fragment.dismiss();
                }else {
                    playOnlineMusicService.playMusicList(position);
                    notifyDataSetChanged();
                }
            }
        });

        viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playOnlineMusicService.deleteMusic(musicBean);
                //删除的歌曲为当前选中播放的歌曲
                if(isPlayingMusic){
                    //删除的歌曲正在播放
                    if(playOnlineMusicService.isPlaying()){
                        //列表中删除后还有歌曲，则接着播放，负责停止播放，隐藏列表
                        if(playOnlineMusicService.getMusicList().size()!=0){
                            playOnlineMusicService.playMusicList(position);
                        }else {
                            playOnlineMusicService.pause();
                            fragment.dismiss();
                        }
                    }else {//删除歌曲不在播放则只更新到下首ui
                        if(playOnlineMusicService.getMusicList().size()!=0){
                            playOnlineMusicService.updateOnlineMusicFragment(playOnlineMusicService.getMusicList().get(position));
                        }else {
                            fragment.dismiss();
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_artist)
        TextView tv_artist;
        @BindView(R.id.iv_delete)
        ImageView iv_delete;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
