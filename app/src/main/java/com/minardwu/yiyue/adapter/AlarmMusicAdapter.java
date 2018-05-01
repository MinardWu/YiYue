package com.minardwu.yiyue.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.utils.CoverLoader;
import com.minardwu.yiyue.utils.FileUtils;
import com.minardwu.yiyue.utils.Preferences;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MinardWu on 2018/5/1.
 */

public class AlarmMusicAdapter extends RecyclerView.Adapter {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_alarm_music,null);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        final MusicBean music = AppCache.getLocalMusicList().get(position);
        viewHolder.tv_Title.setText(music.getTitle());
        String artist = FileUtils.getArtistAndAlbum(music.getArtistName(), music.getAlbum());
        viewHolder.tv_Artist.setText(artist);
        viewHolder.iv_choose.setVisibility(music.getId()== Preferences.getAlarmMusicId()
                ? View.VISIBLE
                : View.GONE);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarmMusicAdapterClickListener.onClick(view,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return AppCache.getLocalMusicList().size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_play)
        ImageView iv_play;
        @BindView(R.id.tv_title)
        TextView tv_Title;
        @BindView(R.id.tv_artist)
        TextView tv_Artist;
        @BindView(R.id.iv_choose)
        ImageView iv_choose;
        @BindView(R.id.v_divider)
        View v_Divider;
        @BindView(R.id.ll_item)
        LinearLayout item;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


    public interface AlarmMusicAdapterClickListener{
        void onClick(View view,int position);
    }

    private AlarmMusicAdapterClickListener alarmMusicAdapterClickListener;

    public void setAlarmMusicAdapterClickListener(AlarmMusicAdapterClickListener listener){
        this.alarmMusicAdapterClickListener = listener;
    }
}
