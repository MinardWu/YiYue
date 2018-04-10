package com.minardwu.yiyue.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minardwu.yiyue.activity.AlbumActivity;
import com.minardwu.yiyue.activity.ArtistActivity;
import com.minardwu.yiyue.adapter.CollectedAlbumAdapter;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.model.AlbumBean;

import java.util.List;

public class CollectedAlbumFragment extends CollectionBaseFragment{

    private List<AlbumBean> albumBeanList;
    private CollectedAlbumAdapter albumAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setVisibility(albumBeanList.size()>0?View.VISIBLE:View.GONE);
        empty_view.setVisibility(albumBeanList.size()>0?View.GONE:View.VISIBLE);
        return view;
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        albumBeanList = MyDatabaseHelper.init(getContext()).queryCollectedAlbum();
        albumAdapter = new CollectedAlbumAdapter(getContext(),albumBeanList);
        albumAdapter.setOnClickListener(new CollectedAlbumAdapter.CollectedAlbumAdapterClickListener() {
            @Override
            public void OnItemClick(View view, AlbumBean albumBean, int position) {
                Intent intent = new Intent(getContext(), AlbumActivity.class);
                intent.putExtra("albumName", albumBean.getAlbumName());
                intent.putExtra("albumId", albumBean.getAlbumId());
                getActivity().startActivity(intent);
            }

            @Override
            public void OnMoreClick(View view, AlbumBean albumBean, int position) {

            }
        });
        return albumAdapter;
    }

}
