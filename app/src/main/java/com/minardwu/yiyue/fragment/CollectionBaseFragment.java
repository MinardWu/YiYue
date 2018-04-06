package com.minardwu.yiyue.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.OnlineMusicRecycleViewAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.PlayOnlineMusicService;

import java.util.List;

public abstract class CollectionBaseFragment extends Fragment {

    protected View view;
    protected RecyclerView recyclerView;
    protected LinearLayout empty_view;
    protected PlayOnlineMusicService playOnlineMusicService;
    protected LinearLayoutManager linearLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playOnlineMusicService = AppCache.getPlayOnlineMusicService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_collection_base, container, false);
        recyclerView = view.findViewById(R.id.rv_collection_base);
        empty_view = view.findViewById(R.id.empty_view);
        recyclerView.setAdapter(getAdapter());
        return view;
    }

    protected abstract RecyclerView.Adapter getAdapter();

}
