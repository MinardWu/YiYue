package com.minardwu.yiyue.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.OnlineMusicRecycleViewAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.event.UpdateOnlineMusicListPositionEvent;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.PlayOnlineMusicService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class MySongActivity extends SampleActivity {

    private RecyclerView rv_fm_history;
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
        list = MyDatabaseHelper.init(this,getResources().getString(R.string.database_name),null,1).queryMySong();
        adapter = new OnlineMusicRecycleViewAdapter(list);
        adapter.setHeaderText("LOVE");
        rv_fm_history.setLayoutManager(linearLayoutManager);
        rv_fm_history.setAdapter(adapter);
        adapter.setOnRecycleViewClickListener(new OnlineMusicRecycleViewAdapter.OnRecycleViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position == 0){
                    playOnlineMusicService.startOrStopLoop("LOVE", list);
                    adapter.setHeaderText("LOVE");
                }else if(playOnlineMusicService.getPlayingMusic()!=null&&adapter.getMusicList().get(position-1).getId()==playOnlineMusicService.getPlayingMusic().getId()){
                    finish();
                }else {
                    playOnlineMusicService.stop();
                    playOnlineMusicService.play((int) adapter.getMusicList().get(position-1).getId());
                    playOnlineMusicService.updataPlayingMusicPosition(position-1);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onMoreClick(View view, int position) {
                playOnlineMusicService.next();
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
}
