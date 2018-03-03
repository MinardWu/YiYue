package com.minardwu.yiyue.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.application.YiYueApplication;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.PlayLocalMusicService;
import com.minardwu.yiyue.utils.CoverLoader;
import com.minardwu.yiyue.utils.FileUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MinardWu on 2017/12/30.
 */

public class LocalMusicListItemAdapter extends BaseAdapter {


    private int mPlayingPosition;

    public interface LocalMusicListItemAdapterLinster{
        void onItemClick(int position);
        void onMoreClick(int position);
    }

    private LocalMusicListItemAdapterLinster mListener;

    public void setLocalMusicListItemAdapterLinster(LocalMusicListItemAdapterLinster localMusicListItemAdapterLinster) {
        this.mListener = localMusicListItemAdapterLinster;
    }

    @Override
    public int getCount() {
        return AppCache.getLocalMusicList().size();
    }

    @Override
    public Object getItem(int i) {
        return AppCache.getLocalMusicList().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view==null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_localmusic,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (position == mPlayingPosition) {
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
        MusicBean music = AppCache.getLocalMusicList().get(position);
        Bitmap cover = CoverLoader.getInstance().loadThumbnail(music);
        viewHolder.iv_Cover.setImageBitmap(cover);
        viewHolder.tv_Title.setText(music.getTitle());
        viewHolder.tv_count.setText(position+1+"");
        String artist = FileUtils.getArtistAndAlbum(music.getArtist(), music.getAlbum());
        viewHolder.tv_Artist.setText(artist);
        viewHolder.iv_More.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onMoreClick(position);
                }
            }
        });
        viewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(position);
                }
            }
        });
        viewHolder.v_Divider.setVisibility(position != AppCache.getLocalMusicList().size() - 1 ? View.VISIBLE : View.GONE);
        return view;
    }

    static class ViewHolder {
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
        LinearLayout item;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void updatePlayingPosition(PlayLocalMusicService playLocalMusicService) {
        if (playLocalMusicService.getPlayingMusic() != null && playLocalMusicService.getPlayingMusic().getType() == MusicBean.Type.LOCAL) {
            mPlayingPosition = playLocalMusicService.getPlayingPosition();
        } else {
            mPlayingPosition = -1;
        }
    }
}
