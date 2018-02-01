package com.minardwu.yiyue.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.OnlineMusicListItemAdapter;
import com.minardwu.yiyue.adapter.OnlineMusicRecycleViewAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.event.UpdateOnlineMusicListPositionEvent;
import com.minardwu.yiyue.http.GetOnlineArtist;
import com.minardwu.yiyue.http.HttpCallback;
import com.minardwu.yiyue.model.ArtistBean;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.PlayOnlineMusicService;
import com.minardwu.yiyue.utils.ImageUtils;
import com.minardwu.yiyue.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistActivity extends AppCompatActivity{

    @BindView(R.id.rl_artist_hot_songs) RecyclerView rl_artist_hot_songs;
    @BindView(R.id.collapsing_toolbar_layout) CollapsingToolbarLayout collapsing_toolbar_layout;
    @BindView(R.id.app_bar_layout) AppBarLayout app_bar_layout;
    @BindView(R.id.tv_artist_name) TextView tv_artist_name;
    @BindView(R.id.iv_bg) ImageView iv_bg;
    @BindView(R.id.iv_artist) ImageView iv_artist;
    @BindView(R.id.tv_artist_name_below_iv) TextView tv_artist_name_below_iv;

    PlayOnlineMusicService playOnlineMusicService;
    List<MusicBean> hontSongs = new ArrayList<MusicBean>();
    OnlineMusicRecycleViewAdapter adapter = new OnlineMusicRecycleViewAdapter(hontSongs);
    LinearLayoutManager linearLayoutManager;

    private Intent intent;
    private int type;
    private String artistId;
    private String artistName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        playOnlineMusicService = AppCache.getPlayOnlineMusicService();
        intent = getIntent();
        type = intent.getIntExtra("type",0);
        artistName = intent.getStringExtra("artistName");
        artistId = intent.getStringExtra("artistId");
        GetOnlineArtist.getArtistInfoById(artistId, new HttpCallback<ArtistBean>() {
            @Override
            public void onSuccess(ArtistBean artistBean) {
                initListView(artistBean);
                ImageUtils.getBitmapByUrl(artistBean.getPicUrl(), new HttpCallback<Bitmap>() {
                    @Override
                    public void onSuccess(final Bitmap bitmap) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_artist.setImageBitmap(ImageUtils.createCircleImage(bitmap));
                                iv_bg.setImageBitmap(bitmap);
                            }
                        });
                    }

                    @Override
                    public void onFail(String e) {
                        loadDataFail();
                    }
                });
            }

            @Override
            public void onFail(String e) {
                loadDataFail();
            }
        });
        initView();
    }

    private void initView(){
        setTitleToCollapsingToolbarLayout();
        tv_artist_name.setText(artistName);
        tv_artist_name_below_iv.setText(artistName);
        iv_artist.setImageBitmap(ImageUtils.createCircleImage(BitmapFactory.decodeResource(getResources(),R.drawable.default_cover)));
    }

    private void setTitleToCollapsingToolbarLayout() {
        app_bar_layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int offsetWhenCollapse = appBarLayout.getTotalScrollRange();
                if (Math.abs(verticalOffset) == offsetWhenCollapse) {
                    tv_artist_name.setVisibility(View.VISIBLE);
                    //设置toolbar为自定义样式的时候，会覆盖掉collapsing_toolbar_layout的title，所以这时下面的方法不能用
//                    collapsing_toolbar_layout.setTitle(artistName);
//                    collapsing_toolbar_layout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorGreenLight));
//                    collapsing_toolbar_layout.setCollapsedTitleGravity(Gravity.CENTER);
                } else {
                    tv_artist_name.setVisibility(View.INVISIBLE);
//                    collapsing_toolbar_layout.setTitle("");
//                    collapsing_toolbar_layout.setExpandedTitleGravity(Gravity.CENTER);
//                    collapsing_toolbar_layout.setExpandedTitleColor(Color.TRANSPARENT);
                }
            }
        });
    }


    private void initListView(ArtistBean artistBean){
        artistId = artistBean.getId();
        hontSongs = artistBean.getSongs();
        adapter = new OnlineMusicRecycleViewAdapter(hontSongs);
        linearLayoutManager = new LinearLayoutManager(this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rl_artist_hot_songs.setLayoutManager(linearLayoutManager);
                rl_artist_hot_songs.setAdapter(adapter);
                adapter.updatePlayingMusicId(playOnlineMusicService);
                adapter.setOnRecycleViewClickListener(new OnlineMusicRecycleViewAdapter.OnRecycleViewClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //第一个条件是点击播放歌手歌单后进行判断用的
                        //第二个条件是刚点开歌手页进行判断的
                        if(position==adapter.getPlayingMusicPosition()||adapter.getTargetList().get(position).getId()==playOnlineMusicService.getPlayingMusic().getId()){
                            finish();
                        }else {
                            playOnlineMusicService.stop();
                            playOnlineMusicService.playTargetList(hontSongs,position);
                            adapter.updatePlayingMusicPosition(position);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onMoreClick(View view, int position) {
                            ToastUtils.show("more");
                    }
                });
            }
        });
    }

    private void loadDataFail(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.show("加载失败");
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateOnlineMusicListPositionEvent(UpdateOnlineMusicListPositionEvent event){
        if (this.artistId.equals(event.getArtistId())){
            adapter.updatePlayingMusicPosition(event.getPosition());
            adapter.notifyDataSetChanged();
        }else {
            //当用户点开一个歌手页后如果不打算循坏该歌手的歌，但是又不退出歌手页，而这时fm切到下一首歌了，则要把之前显示播放的那首歌设置为未播放的样式
            adapter.updatePlayingMusicPosition(-1);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
