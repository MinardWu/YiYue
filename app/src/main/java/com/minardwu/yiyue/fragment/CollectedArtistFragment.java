package com.minardwu.yiyue.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.activity.ArtistActivity;
import com.minardwu.yiyue.adapter.CollectedArtistAdapter;
import com.minardwu.yiyue.adapter.ImageAndTextAdapter;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.executor.IView;
import com.minardwu.yiyue.executor.MoreOptionOfArtistActExecutor;
import com.minardwu.yiyue.executor.MoreOptionOfCollectedArtistExecutor;
import com.minardwu.yiyue.model.ArtistBean;
import com.minardwu.yiyue.utils.ToastUtils;

import java.util.List;

public class CollectedArtistFragment extends CollectionBaseFragment implements IView{

    private List<ArtistBean> list;
    private CollectedArtistAdapter adapter;

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
        recyclerView.setVisibility(list.size()>0?View.VISIBLE:View.GONE);
        empty_view.setVisibility(list.size()>0?View.GONE:View.VISIBLE);
        return view;
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        list = MyDatabaseHelper.init(getActivity()).queryFollowedArtist();
        adapter = new CollectedArtistAdapter(getActivity(),list);
        adapter.setListener(new CollectedArtistAdapter.CollectedArtistAdapterClickListener() {
            @Override
            public void OnItemClick(View view, ArtistBean artistBean, int position) {
                Intent intent = new Intent(getContext(), ArtistActivity.class);
                intent.putExtra("artistName",artistBean.getName());
                intent.putExtra("artistId",artistBean.getId());
                getActivity().startActivity(intent);
            }

            @Override
            public void OnMoreClick(View view, final ArtistBean artistBean, final int artistPosition) {
                final OptionDialogFragment fragment = new OptionDialogFragment();
                fragment.setHeader_titile("歌手：");
                fragment.setHeader_text(artistBean.getName());
                fragment.setListViewAdapter(new ImageAndTextAdapter(getContext(), R.array.collection_artist_more_img,R.array.collection_artist_more_text));
                fragment.setOptionDialogFragmentClickListener(new OptionDialogFragment.OptionDialogFragmentClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        fragment.dismiss();
                        MoreOptionOfCollectedArtistExecutor.execute(getActivity(),position,list.get(artistPosition),CollectedArtistFragment.this);
                    }
                });
                fragment.show(getFragmentManager(), "OptionDialogFragment");
            }
        });
        return adapter;
    }

    @Override
    public void updateViewForExecutor() {
        list.clear();
        list.addAll(MyDatabaseHelper.init(getActivity()).queryFollowedArtist());
        recyclerView.setVisibility(list.size()>0?View.VISIBLE:View.GONE);
        empty_view.setVisibility(list.size()>0?View.GONE:View.VISIBLE);
        adapter.notifyDataSetChanged();
    }
}
