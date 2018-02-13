package com.minardwu.yiyue.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.OnlineMusicListItemAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.model.MusicBean;

import java.util.List;

public class MyFMHistoryActivity extends SampleActivity {

    private ListView lv_fm_history;
    private OnlineMusicListItemAdapter adapter;
    private List<MusicBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lv_fm_history = findViewById(R.id.lv_fm_history);
        list = MyDatabaseHelper.init(this,getResources().getString(R.string.database_name),null,1).queryFMHistory();
        adapter = new OnlineMusicListItemAdapter(list);
        lv_fm_history.setAdapter(adapter);
        lv_fm_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppCache.getPlayOnlineMusicService().stop();
                AppCache.getPlayOnlineMusicService().play((int) list.get(i).getId());
                adapter.updatePlayingMusicPosition(i);
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_my_fmhistory;
    }

    @Override
    protected void setToolbarTitle(TextView left, TextView mid, TextView right) {
        super.setToolbarTitle(left, mid, right);
        left.setVisibility(View.GONE);
        mid.setText("FM足迹");
        right.setVisibility(View.GONE);
    }

}
