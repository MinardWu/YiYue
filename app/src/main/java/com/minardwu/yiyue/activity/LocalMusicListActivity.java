package com.minardwu.yiyue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.ImageAndTextAdapter;
import com.minardwu.yiyue.adapter.LocalMusicListItemAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.event.UpdateLocalMusicListEvent;
import com.minardwu.yiyue.executor.IView;
import com.minardwu.yiyue.executor.MoreOptionOfLocalMusicListExecutor;
import com.minardwu.yiyue.fragment.OptionDialogFragment;
import com.minardwu.yiyue.utils.MusicUtils;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.utils.SystemUtils;
import com.minardwu.yiyue.widget.CustomPopWindow;
import com.minardwu.yiyue.widget.dialog.ChooseOptionDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author MinardWu
 * @date : 2017/12/30
 */

public class LocalMusicListActivity extends BaseActivity implements IView{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.iv_search)
    ImageView iv_search;
    @BindView(R.id.iv_sort)
    ImageView iv_sort;
    @BindView(R.id.lv_local_music)
    ListView lv_local_music;
    @BindView(R.id.tv_empty)
    TextView tv_empty;
    @BindView(R.id.tv_local_music_list_songnum)
    TextView tv_local_music_list_songnum;

    @OnClick(R.id.iv_back) void back(){
        finish();
    }
    @OnClick(R.id.iv_search) void search(){
        startActivity(new Intent(LocalMusicListActivity.this,SearchLocalMusicActivity.class));
    }
    @OnClick(R.id.iv_sort) void more(){
//        CustomPopWindow customPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
//                .setView(R.layout.popup_window)//显示的布局，还可以通过设置一个View
//                .setOutsideTouchable(true)//是否PopupWindow 以外触摸dissmiss
//                .create()
//                .showAsDropDown(toolbar, SystemUtils.getScreenWidth(),0);
//        MoreDialog moreDialog = new MoreDialog(this,R.style.StopTimeDialog);
//        moreDialog.setOnMoreDialogItemClickListener(new MoreDialog.OnMoreDialogItemClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(view.getId()==R.id.tv_scan_music){
//                    AppCache.getLocalMusicList().clear();
//                    AppCache.getLocalMusicList().addAll(MusicUtils.scanMusic(LocalMusicListActivity.this));
//                    updateView();
//                }
//            }
//        });
//        moreDialog.showShortToast();
        ChooseOptionDialog sortDialog = new ChooseOptionDialog(getContext(),R.style.StopTimeDialog);
        sortDialog.setTitle("排序方式");
        sortDialog.setItem(R.array.sort);
        sortDialog.setShowImagePosition(Preferences.getLocalMusicOrderType());
        sortDialog.setOnDialogItemClickListener(new ChooseOptionDialog.onDialogItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Preferences.setLocalMusicOrderType(position);
                Collections.sort(AppCache.getLocalMusicList(),new MusicUtils.MusicComparator());
                EventBus.getDefault().post(new UpdateLocalMusicListEvent(1));
                getPlayLocalMusicService().updatePlayingPosition();
            }
        });
        sortDialog.show();
    }

    LocalMusicListItemAdapter localMusicListItemAdapter = new LocalMusicListItemAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music_list);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        localMusicListItemAdapter.setLocalMusicListItemAdapterLinster(new LocalMusicListItemAdapter.LocalMusicListItemAdapterLinster() {
            @Override
            public void onItemClick(int position) {
                if(AppCache.getLocalMusicList().get(position).getId() == MusicUtils.getLocalMusicPlayingMusic().getId()){
                    if(getPlayLocalMusicService().isPlaying()){
                        finish();
                    }else {
                        getPlayLocalMusicService().play(position);
                        finish();
                    }
                }else {
                    getPlayLocalMusicService().play(position);
                    updateView();
                }
            }

            @Override
            public void onMoreClick(final int musicPosition) {
                final OptionDialogFragment fragment = new OptionDialogFragment();
                fragment.setHeader_titile("歌曲：");
                fragment.setHeader_text(AppCache.getLocalMusicList().get(musicPosition).getTitle());
                fragment.setListViewAdapter(new ImageAndTextAdapter(LocalMusicListActivity.this,R.array.local_music_more_img,R.array.local_music_more_text));
                fragment.setOptionDialogFragmentClickListener(new OptionDialogFragment.OptionDialogFragmentClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        fragment.dismiss();
                        if (position==0){
                            onItemClick(musicPosition);
                        }else {
                            MoreOptionOfLocalMusicListExecutor.execute(LocalMusicListActivity.this,position,AppCache.getLocalMusicList().get(musicPosition),LocalMusicListActivity.this);
                        }
                    }
                });
                fragment.show(getSupportFragmentManager(), "OptionDialogFragment");
            }
        });
        lv_local_music.setAdapter(localMusicListItemAdapter);
        lv_local_music.setEmptyView(tv_empty);
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

    @Override
    public void updateViewForExecutor() {
        localMusicListItemAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateLocalMusicListEvent(UpdateLocalMusicListEvent event) {
        localMusicListItemAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
