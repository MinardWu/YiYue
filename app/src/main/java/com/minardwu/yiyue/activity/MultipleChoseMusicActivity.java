package com.minardwu.yiyue.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.MultipleChoseMusicAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.model.MusicBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MultipleChoseMusicActivity extends SampleActivity {

    @BindView(R.id.rv_multiple_chose_music)
    RecyclerView rv_multiple_chose_music;
    @BindView(R.id.tv_add_to_list)
    TextView tv_add_to_list;

    private ArrayList<MusicBean> list = new ArrayList<MusicBean>();
    private MultipleChoseMusicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        if (getIntent()!=null){
            list.clear();
            list = getIntent().getParcelableArrayListExtra("musicList");
        }
        adapter = new MultipleChoseMusicAdapter(this,list);
        rv_multiple_chose_music.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_multiple_chose_music.setAdapter(adapter);
        tv_add_to_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCache.getPlayOnlineMusicService().appendMusicList(adapter.getChosenList());
                finish();
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_multiple_chose_music;
    }

    @Override
    protected void setToolbarTitle(TextView left, TextView mid, TextView right) {
        super.setToolbarTitle(left, mid, right);
        left.setText("选择音乐");
        mid.setVisibility(View.GONE);
        right.setVisibility(View.GONE);
    }
}
