package com.minardwu.yiyue.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.PlayService;
import com.minardwu.yiyue.utils.CoverLoader;
import com.minardwu.yiyue.utils.FileUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MinardWu on 2017/12/30.
 */

public class LocalMusicListItemAdapter extends BaseAdapter {

//    private OnMoreClickListener mListener;
    private int mPlayingPosition;

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
    public View getView(int position, View view, ViewGroup viewGroup) {
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
        } else {
            viewHolder.v_Playing.setVisibility(View.INVISIBLE);
        }
        MusicBean music = AppCache.getLocalMusicList().get(position);
        Bitmap cover = CoverLoader.getInstance().loadThumbnail(music);
        viewHolder.iv_Cover.setImageBitmap(cover);
        viewHolder.tv_Title.setText(music.getTitle());
        String artist = FileUtils.getArtistAndAlbum(music.getArtist(), music.getAlbum());
        viewHolder.tv_Artist.setText(artist);
        viewHolder.iv_More.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mListener != null) {
//                    mListener.onMoreClick(position);
//                }
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
        @BindView(R.id.tv_title)
         TextView tv_Title;
        @BindView(R.id.tv_artist)
         TextView tv_Artist;
        @BindView(R.id.iv_more)
         ImageView iv_More;
        @BindView(R.id.v_divider)
         View v_Divider;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void updatePlayingPosition(PlayService playService) {
        if (playService.getPlayingMusic() != null && playService.getPlayingMusic().getType() == MusicBean.Type.LOCAL) {
            mPlayingPosition = playService.getPlayingPosition();
        } else {
            mPlayingPosition = -1;
        }
    }
}