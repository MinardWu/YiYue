package com.minardwu.yiyue.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.application.YiYueApplication;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.PlayOnlineMusicService;
import com.minardwu.yiyue.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MinardWu on 2018/2/1.
 */

public class OnlineMusicRecycleViewAdapter extends RecyclerView.Adapter<OnlineMusicRecycleViewAdapter.MyViewHolder> {


    private long playingMusicId;
    private int playingMusicPosition = -1;
    private boolean justIn = true;
    private List<MusicBean> list = new ArrayList<MusicBean>();

    public OnlineMusicRecycleViewAdapter(List<MusicBean> list) {
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_localmusic,null);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, final int position) {
        if ((list.get(position).getId()==playingMusicId&&justIn)||position==playingMusicPosition) {
            viewHolder.v_Playing.setVisibility(View.VISIBLE);
            viewHolder.tv_count.setTextColor(YiYueApplication.getAppContext().getResources().getColor(R.color.colorGreenDeep));
            viewHolder.tv_Title.setTextColor(YiYueApplication.getAppContext().getResources().getColor(R.color.colorGreenDeep));
            viewHolder.tv_Artist.setTextColor(YiYueApplication.getAppContext().getResources().getColor(R.color.colorGreenDeep));
        } else {
            viewHolder.v_Playing.setVisibility(View.INVISIBLE);
            viewHolder.tv_count.setTextColor(YiYueApplication.getAppContext().getResources().getColor(R.color.grey));
            viewHolder.tv_Title.setTextColor(YiYueApplication.getAppContext().getResources().getColor(R.color.black_l));
            viewHolder.tv_Artist.setTextColor(YiYueApplication.getAppContext().getResources().getColor(R.color.grey));
        }
        MusicBean music = list.get(position);
        viewHolder.tv_Title.setText(music.getTitle());
        viewHolder.tv_count.setText(position+1+"");
        String artist = FileUtils.getArtistAndAlbum(music.getArtist(), music.getAlbum());
        viewHolder.tv_Artist.setText(artist);
        viewHolder.v_Divider.setVisibility(position != AppCache.getLocalMusicList().size() - 1 ? View.VISIBLE : View.GONE);
        viewHolder.iv_More.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onMoreClick(view,position);
                }
            }
        });
        viewHolder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener!=null){
                    listener.onItemClick(view,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.v_playing)
        View v_Playing;
        @BindView(R.id.iv_cover)
        ImageView iv_Cover;
        @BindView(R.id.tv_count)
        TextView tv_count;
        @BindView(R.id.tv_title)
        TextView tv_Title;
        @BindView(R.id.tv_artist)
        TextView tv_Artist;
        @BindView(R.id.iv_more)
        ImageView iv_More;
        @BindView(R.id.v_divider)
        View v_Divider;
        @BindView(R.id.ll_item)
        View ll_item;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void updatePlayingMusicId(PlayOnlineMusicService playOnlineMusicService) {
        if (playOnlineMusicService.getPlayingMusic() != null) {
            playingMusicId = playOnlineMusicService.getPlayingMusic().getId();
        } else {
            playingMusicId = -1;
        }
    }

    public void updatePlayingMusicPosition(int position) {
        justIn = false;
        playingMusicPosition = position;
    }

    public int getPlayingMusicPosition() {
        return playingMusicPosition;
    }

    public List<MusicBean> getTargetList() {
        return list;
    }

    public interface OnRecycleViewClickListener{
        void onItemClick(View view,int position);
        void onMoreClick(View view,int position);
    }

    private OnRecycleViewClickListener listener;

    public void setOnRecycleViewClickListener(OnRecycleViewClickListener listener){
        this.listener = listener;
    }
}
