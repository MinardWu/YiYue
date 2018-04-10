package com.minardwu.yiyue.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.minardwu.yiyue.R;
import com.minardwu.yiyue.model.AlbumBean;
import com.minardwu.yiyue.model.ArtistBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wumingyuan on 2018/4/2.
 */

public class CollectedAlbumAdapter extends RecyclerView.Adapter {


    private Context context;
    private List<AlbumBean> albumBeanList;
    public CollectedAlbumAdapter(Context context, List<AlbumBean> albumBeanList) {
        this.context = context;
        this.albumBeanList = albumBeanList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_collected_artist_or_album,null);
        CollectedArtistHolder holder = new CollectedArtistHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final AlbumBean bean = albumBeanList.get(position);
        final CollectedArtistHolder collectedArtistHolder = (CollectedArtistHolder) holder;
        collectedArtistHolder.sdv_artist.setImageURI(bean.getPicUrl());
        collectedArtistHolder.tv_artist_name.setText(bean.getAlbumName());
        collectedArtistHolder.tv_artist_info.setText(context.getResources().getString(R.string.collection_album_music_size,bean.getSize()));
        collectedArtistHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnItemClick(view,bean,position);
            }
        });
        collectedArtistHolder.iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnMoreClick(view,bean,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumBeanList.size();
    }


    class CollectedArtistHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item)
         ConstraintLayout item;
         @BindView(R.id.sdv_artist)
         SimpleDraweeView sdv_artist;
         @BindView(R.id.tv_artist_name)
         TextView tv_artist_name;
         @BindView(R.id.tv_artist_info)
         TextView tv_artist_info;
         @BindView(R.id.iv_more)
         ImageView iv_more;

         public CollectedArtistHolder(View itemView) {
             super(itemView);
             ButterKnife.bind(this,itemView);
         }
    }

    public interface CollectedAlbumAdapterClickListener {
        void OnItemClick(View view, AlbumBean albumBean, int position);
        void OnMoreClick(View view, AlbumBean albumBean, int position);
    }

    private CollectedAlbumAdapterClickListener listener;

    public void setOnClickListener(CollectedAlbumAdapterClickListener listener) {
        this.listener = listener;
    }
}
