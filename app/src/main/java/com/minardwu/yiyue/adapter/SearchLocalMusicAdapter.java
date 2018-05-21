package com.minardwu.yiyue.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.activity.MainActivity;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.utils.FileUtils;
import com.minardwu.yiyue.utils.MusicUtils;
import com.minardwu.yiyue.utils.SystemUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author MinardWu
 * @date : 2018/5/2
 */

public class SearchLocalMusicAdapter extends RecyclerView.Adapter{

    private Context context;
    private ArrayList<MusicBean> list = new ArrayList<MusicBean>();

    public SearchLocalMusicAdapter(Context context,ArrayList<MusicBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_search_local_music,null);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        final MusicBean music = list.get(position);
        viewHolder.tv_Title.setText(music.getTitle());
        String artist = FileUtils.getArtistAndAlbum(music.getArtistName(), music.getAlbum());
        viewHolder.tv_Artist.setText(artist);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int p = MusicUtils.getLocalMusicPlayingPosition();
                AppCache.getPlayLocalMusicService().play(p);
                SystemUtils.startMainActivity((Activity) context, MainActivity.LOCAL);
            }
        });
    }

    public void updateList(ArrayList<MusicBean> list){
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_title)
        TextView tv_Title;
        @BindView(R.id.tv_artist)
        TextView tv_Artist;
        @BindView(R.id.v_divider)
        View v_Divider;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
