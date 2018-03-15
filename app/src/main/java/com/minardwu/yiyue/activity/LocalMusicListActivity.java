package com.minardwu.yiyue.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.ImageAndTextAdapter;
import com.minardwu.yiyue.adapter.LocalMusicListItemAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.fragment.OptionDialogFragment;
import com.minardwu.yiyue.utils.MusicUtils;
import com.minardwu.yiyue.widget.MoreDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocalMusicListActivity extends BaseActivity {

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
        localMusicListItemAdapter.setLocalMusicListItemAdapterLinster(new LocalMusicListItemAdapter.LocalMusicListItemAdapterLinster() {
            @Override
            public void onItemClick(int position) {
                if(position== getPlayLocalMusicService().getPlayingPosition()){
                    finish();
                }else {
                    getPlayLocalMusicService().play(position);
                    updateView();
                }
            }

            @Override
            public void onMoreClick(int position) {
                OptionDialogFragment fragment = new OptionDialogFragment();
                fragment.setHeader_titile("歌曲：");
                fragment.setHeader_text(AppCache.getLocalMusicList().get(position).getTitle());
                fragment.setListViewAdapter(new ImageAndTextAdapter(LocalMusicListActivity.this,R.array.local_music_more_img,R.array.local_music_more_text));
                fragment.setOptionDialogFragmentClickListener(new OptionDialogFragment.OptionDialogFragmentClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        Toast.makeText(LocalMusicListActivity.this, position+"", Toast.LENGTH_SHORT).show();
                    }
                });
                fragment.show(getSupportFragmentManager(), "OptionDialogFragment");
            }
        });
        lv_localmusic.setAdapter(localMusicListItemAdapter);
        lv_localmusic.setEmptyView(tv_empty);
        //lv_localmusic.setOnItemClickListener(this);
        updateView();
    }

    private void updateView() {
        if (AppCache.getLocalMusicList().isEmpty()) {
            tv_empty.setVisibility(View.VISIBLE);
        } else {
            tv_empty.setVisibility(View.GONE);
        }
        tv_local_music_list_songnum.setText("("+AppCache.getLocalMusicList().size()+"首)");
        localMusicListItemAdapter.updatePlayingPosition(getPlayLocalMusicService());
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
//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        if(i==getPlayLocalMusicService().getPlayingPosition()){
//            finish();
//        }else {
//            getPlayLocalMusicService().play(i);
//            updateView();
//        }
//    }
}
