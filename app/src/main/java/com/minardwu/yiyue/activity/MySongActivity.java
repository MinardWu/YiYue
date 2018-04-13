package com.minardwu.yiyue.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.ImageAndTextAdapter;
import com.minardwu.yiyue.adapter.OnlineMusicRecycleViewAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.event.UpdateOnlineMusicListPositionEvent;
import com.minardwu.yiyue.executor.IView;
import com.minardwu.yiyue.executor.MoreOptionOfCollectedSongExecutor;
import com.minardwu.yiyue.fragment.OptionDialogFragment;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.PlayOnlineMusicService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class MySongActivity extends SampleActivity implements IView{

    private RecyclerView rv_fm_history;
    private LinearLayout empty_view;
    private OnlineMusicRecycleViewAdapter adapter;
    private List<MusicBean> list;
    private PlayOnlineMusicService playOnlineMusicService;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        playOnlineMusicService = AppCache.getPlayOnlineMusicService();
        linearLayoutManager = new LinearLayoutManager(this);
        rv_fm_history = findViewById(R.id.rv_my_song);
        empty_view = findViewById(R.id.empty_view);
        list = MyDatabaseHelper.init(this).queryCollectedSong();
        rv_fm_history.setVisibility(list.size()>0?View.VISIBLE:View.GONE);
        empty_view.setVisibility(list.size()>0?View.GONE:View.VISIBLE);
        adapter = new OnlineMusicRecycleViewAdapter(list);
        rv_fm_history.setLayoutManager(linearLayoutManager);
        rv_fm_history.setAdapter(adapter);
        adapter.setOnRecycleViewClickListener(new OnlineMusicRecycleViewAdapter.OnRecycleViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position == 0){
                    playOnlineMusicService.playMusicList(list);
                }else if(playOnlineMusicService.getPlayingMusicId().equals(list.get(position-1).getId()+"")){
                    finish();
                }else {
                    playOnlineMusicService.stop();
                    playOnlineMusicService.play((int) adapter.getMusicList().get(position-1).getId());
                    playOnlineMusicService.updatePlayingMusicPosition(position-1);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onMoreClick(View view, final int musicPosition) {
                final OptionDialogFragment fragment = new OptionDialogFragment();
                fragment.setHeader_titile("歌曲：");
                fragment.setHeader_text(list.get(musicPosition-1).getTitle());
                fragment.setListViewAdapter(new ImageAndTextAdapter(MySongActivity.this,R.array.collected_song_more_img,R.array.collected_song_more_text));
                fragment.setOptionDialogFragmentClickListener(new OptionDialogFragment.OptionDialogFragmentClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        fragment.dismiss();
                        //点击播放的话直接使用上面的逻辑即可
                        if (position==0){
                            onItemClick(view,musicPosition);
                        }else{
                            MoreOptionOfCollectedSongExecutor.execute(MySongActivity.this,position,list.get(musicPosition-1),MySongActivity.this);
                        }
                    }
                });
                fragment.show(getSupportFragmentManager(), "OptionDialogFragment");
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_my_song;
    }

    @Override
    protected void setToolbarTitle(TextView left, TextView mid, TextView right) {
        super.setToolbarTitle(left, mid, right);
        left.setVisibility(View.GONE);
        mid.setText("我的红心");
        right.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateOnlineMusicListPositionEvent(UpdateOnlineMusicListPositionEvent event){
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void updateViewForExecutor() {
        onResume();
    }
}
