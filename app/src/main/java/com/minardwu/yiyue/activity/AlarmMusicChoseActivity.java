package com.minardwu.yiyue.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.AlarmMusicAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.utils.Preferences;

import butterknife.BindView;

/**
 * @author MinardWu
 * @date : 2018/5/1
 */

public class AlarmMusicChoseActivity extends SampleActivity {

    @BindView(R.id.rv_alarm_music)
    RecyclerView rv_alarm_music;

    private AlarmMusicAdapter alarmMusicAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarmMusicAdapter = new AlarmMusicAdapter();
        rv_alarm_music.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_alarm_music.setAdapter(alarmMusicAdapter);
        alarmMusicAdapter.setAlarmMusicAdapterClickListener(new AlarmMusicAdapter.AlarmMusicAdapterClickListener() {
            @Override
            public void onClick(View view, int position) {
                MusicBean musicBean = AppCache.getLocalMusicList().get(position);
                Preferences.saveAlarmMusicId(musicBean.getId());
                alarmMusicAdapter.notifyDataSetChanged();
                Intent intent = new Intent();
                intent.putExtra("musicTitle",musicBean.getTitle());
                setResult(7,intent);
                finish();
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_alarm_music_chose;
    }

    @Override
    protected void setToolbarTitle(TextView left, TextView mid, TextView right) {
        super.setToolbarTitle(left, mid, right);
        left.setText("选择铃声");
        mid.setVisibility(View.GONE);
        right.setVisibility(View.GONE);
    }
}
