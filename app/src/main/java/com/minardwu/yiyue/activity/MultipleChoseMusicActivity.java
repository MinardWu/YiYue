package com.minardwu.yiyue.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.MultipleChoseMusicAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.event.ChooseMusicCountEvent;
import com.minardwu.yiyue.model.MusicBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author MinardWu
 * @date : 2018/5/3
 */

public class MultipleChoseMusicActivity extends SampleActivity {

    @BindView(R.id.rv_multiple_chose_music)
    RecyclerView rv_multiple_chose_music;

    private TextView left;
    private TextView right;

    private ArrayList<MusicBean> list = new ArrayList<MusicBean>();
    private MultipleChoseMusicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        if (getIntent()!=null){
            list.clear();
            list = getIntent().getParcelableArrayListExtra("musicList");
        }
        adapter = new MultipleChoseMusicAdapter(this,list);
        rv_multiple_chose_music.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_multiple_chose_music.setAdapter(adapter);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_multiple_chose_music;
    }

    @Override
    protected void setToolbarTitle(TextView left, TextView mid, TextView right) {
        super.setToolbarTitle(left, mid, right);
        this.left = left;
        this.right = right;
        left.setText("选择音乐");
        mid.setVisibility(View.GONE);
        right.setText("加入列表");
        right.setTextSize(15);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCache.getPlayOnlineMusicService().appendMusicList(adapter.getChosenList());
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChooseMusicCountEvent(ChooseMusicCountEvent event){
        if (event.getCount()>0){
            left.setText(getContext().getString(R.string.select_music_count,event.getCount()));
        }else {
            left.setText("选择音乐");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
