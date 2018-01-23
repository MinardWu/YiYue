package com.minardwu.yiyue.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.LocalMusicListItemAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.utils.MusicUtils;
import com.minardwu.yiyue.widget.MoreDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocalMusicListActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.iv_search)
    ImageView iv_search;
    @BindView(R.id.iv_more)
    ImageView iv_more;
    @BindView(R.id.lv_localmusic)
    ListView lv_localmusic;
    @BindView(R.id.tv_empty)
    TextView tv_empty;
    @BindView(R.id.tv_local_music_list_songnum)
    TextView tv_local_music_list_songnum;

    @OnClick(R.id.iv_back) void back(){
        finish();
    }
    @OnClick(R.id.iv_more) void more(){
        MoreDialog moreDialog = new MoreDialog(this,R.style.StopTimeDialog);
        moreDialog.setOnMoreDialogItemClickListener(new MoreDialog.OnMoreDialogItemClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId()==R.id.tv_scan_music){
                    AppCache.getLocalMusicList().clear();
                    AppCache.getLocalMusicList().addAll(MusicUtils.scanMusic(LocalMusicListActivity.this));
                    updateView();
                }
            }
        });
        moreDialog.show();
    }

    LocalMusicListItemAdapter localMusicListItemAdapter = new LocalMusicListItemAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music_list);
        ButterKnife.bind(this);
        lv_localmusic.setAdapter(localMusicListItemAdapter);
        lv_localmusic.setEmptyView(tv_empty);
        lv_localmusic.setOnItemClickListener(this);
        updateView();
    }

    private void updateView() {
        if (AppCache.getLocalMusicList().isEmpty()) {
            tv_empty.setVisibility(View.VISIBLE);
        } else {
            tv_empty.setVisibility(View.GONE);
        }
        tv_local_music_list_songnum.setText("("+AppCache.getLocalMusicList().size()+"首)");
        localMusicListItemAdapter.updatePlayingPosition(getPlayService());
        localMusicListItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(0,R.anim.activity_close);
    }

    /**
     * 音乐列表点击事件，如果点击正在播放的音乐则回到播放界面
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(i==getPlayService().getPlayingPosition()){
            finish();
        }else {
            getPlayService().play(i);
            updateView();
        }
    }
}
