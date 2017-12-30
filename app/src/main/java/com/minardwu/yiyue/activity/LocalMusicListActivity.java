package com.minardwu.yiyue.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.LocalMusicListItemAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocalMusicListActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.lv_localmusic)
    ListView lv_localmusic;
    @BindView(R.id.tv_empty)
    TextView tv_empty;

    @OnClick(R.id.iv_back) void back(){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music_list);
        ButterKnife.bind(this);
        lv_localmusic.setAdapter(new LocalMusicListItemAdapter());
        lv_localmusic.setEmptyView(tv_empty);
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(0,R.anim.activity_close);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
