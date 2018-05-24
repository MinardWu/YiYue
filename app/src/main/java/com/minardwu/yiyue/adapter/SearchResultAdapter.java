package com.minardwu.yiyue.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
 * @author MinardWu
 * @date : 2018/2/1
 */

public class SearchResultAdapter extends BaseAdapter {

    private List<MusicBean> musicList = new ArrayList<MusicBean>();
    private PlayOnlineMusicService playOnlineMusicService;


    public SearchResultAdapter(List<MusicBean> list) {
        this.musicList = list;
        playOnlineMusicService = AppCache.getPlayOnlineMusicService();
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object getItem(int i) {
        return musicList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        MyViewHolder viewHolder;
        if(convertView==null){
            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_search_result,null);
            viewHolder = new MyViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (MyViewHolder) convertView.getTag();
        }

        MusicBean music = musicList.get(position);
        viewHolder.tv_Title.setText(music.getTitle());
        String artist = FileUtils.getArtistAndAlbum(music.getArtistName(), music.getAlbum());
        viewHolder.tv_Artist.setText(artist);
        viewHolder.v_Divider.setVisibility(position != musicList.size() ? View.VISIBLE : View.GONE);
        if ((playOnlineMusicService.getPlayingMusicId()==musicList.get(position).getId())) {
            viewHolder.tv_Title.setTextColor(YiYueApplication.getAppContext().getResources().getColor(R.color.green_deep));
            viewHolder.tv_Artist.setTextColor(YiYueApplication.getAppContext().getResources().getColor(R.color.green_deep));
        } else {
            viewHolder.tv_Title.setTextColor(YiYueApplication.getAppContext().getResources().getColor(R.color.black_80));
            viewHolder.tv_Artist.setTextColor(YiYueApplication.getAppContext().getResources().getColor(R.color.grey));
        }

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
        return convertView;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
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

    public interface OnSearchResultViewClickListener {
        void onItemClick(View view, int position);
        void onMoreClick(View view, int position);
    }

    private OnSearchResultViewClickListener listener;

    public void setOnSearchResultViewClickListener(OnSearchResultViewClickListener listener){
        this.listener = listener;
    }
}
