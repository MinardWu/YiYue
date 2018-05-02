package com.minardwu.yiyue.adapter;

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
import com.minardwu.yiyue.utils.FileUtils;
import com.minardwu.yiyue.utils.Preferences;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wumingyuan on 2018/5/2.
 */

public class SearchLocalMusicAdapter extends RecyclerView.Adapter{

    private ArrayList<MusicBean> list = new ArrayList<MusicBean>();

    public SearchLocalMusicAdapter(ArrayList<MusicBean> list) {
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_search_local_music,null);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        final MusicBean music = list.get(position);
        viewHolder.tv_Title.setText(music.getTitle());
        String artist = FileUtils.getArtistAndAlbum(music.getArtistName(), music.getAlbum());
        viewHolder.tv_Artist.setText(artist);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
