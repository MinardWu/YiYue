package com.minardwu.yiyue.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.OnlineMusicRecycleViewAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.db.MyDatabaseHelper;
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

import java.sql.SQLClientInfoException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.rl_artist_hot_songs) RecyclerView rl_artist_hot_songs;
    @BindView(R.id.collapsing_toolbar_layout) CollapsingToolbarLayout collapsing_toolbar_layout;
    @BindView(R.id.app_bar_layout) AppBarLayout app_bar_layout;
    @BindView(R.id.tv_artist_name) TextView tv_artist_name;
    @BindView(R.id.iv_bg) ImageView iv_bg;
    @BindView(R.id.iv_artist) ImageView iv_artist;
    @BindView(R.id.tv_artist_name_below_iv) TextView tv_artist_name_below_iv;
    @BindView(R.id.btn_follow_artist) Button btn_follow_artist;


    PlayOnlineMusicService playOnlineMusicService;
    List<MusicBean> hontSongs = new ArrayList<MusicBean>();
    OnlineMusicRecycleViewAdapter adapter = new OnlineMusicRecycleViewAdapter(hontSongs);
    LinearLayoutManager linearLayoutManager;
    MyDatabaseHelper myDatabaseHelper;

    private Intent intent;
    private int type;
    private int song_conut;
    private String artistId;
    private String artistName;
    private String artistPicUrl;


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
        initView();
        GetOnlineArtist.getArtistInfoById(artistId, new HttpCallback<ArtistBean>() {
            @Override
            public void onSuccess(final ArtistBean artistBean) {
                artistPicUrl = artistBean.getPicUrl();
                initListView(artistBean);
                ImageUtils.getBitmapByUrl(artistBean.getPicUrl(), new HttpCallback<Bitmap>() {
                    @Override
                    public void onSuccess(final Bitmap bitmap) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (myDatabaseHelper.isFollowArtist(artistId)){
                                    myDatabaseHelper.updateArtistPic(artistId,artistPicUrl);
                                }
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
    }

    private void initView(){
        setTitleToCollapsingToolbarLayout();
        tv_artist_name.setText(artistName);
        tv_artist_name_below_iv.setText(artistName);
        iv_artist.setImageBitmap(ImageUtils.createCircleImage(BitmapFactory.decodeResource(getResources(),R.drawable.default_cover)));
        myDatabaseHelper = new MyDatabaseHelper(this,getResources().getString(R.string.database_name),null,1);
        SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
        myDatabaseHelper.setSQLiteDataBase(sqLiteDatabase);
        btn_follow_artist.setText(myDatabaseHelper.isFollowArtist(artistId) ? "已关注":"关注");
        btn_follow_artist.setOnClickListener(this);
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

    private void initListView(final ArtistBean artistBean){
        song_conut = artistBean.getSongs().size();
        artistId = artistBean.getId();
        hontSongs = artistBean.getSongs();
        adapter = new OnlineMusicRecycleViewAdapter(hontSongs);
        linearLayoutManager = new LinearLayoutManager(this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rl_artist_hot_songs.setLayoutManager(linearLayoutManager);
                rl_artist_hot_songs.setAdapter(adapter);
                adapter.setHeaderText(artistBean.getId());
                adapter.updatePlayingMusicId(playOnlineMusicService);
                adapter.setOnRecycleViewClickListener(new OnlineMusicRecycleViewAdapter.OnRecycleViewClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(position==0) {
                            playOnlineMusicService.startOrStopLoop(artistBean.getId(), hontSongs);
                            adapter.setHeaderText(artistBean.getId());
                        }
                        //第一个条件是点击播放歌手歌单后进行判断用的
                        //第二个条件是刚点开歌手页进行判断的
                        else if(position==adapter.getPlayingMusicPosition()||
                                playOnlineMusicService.getPlayingMusic()!=null&&adapter.getMusicList().get(position-1).getId()==playOnlineMusicService.getPlayingMusic().getId()){
                            finish();
                        }else {
                            playOnlineMusicService.stop();
                            playOnlineMusicService.play((int) adapter.getMusicList().get(position-1).getId());
                            adapter.updatePlayingMusicPosition(position);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onMoreClick(View view, int position) {
                            ToastUtils.show("next");
                            playOnlineMusicService.next();
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
            //记得position加1(界面与musicList传递数据时总是加1或减1，加或减取决于谁是发送数据的一方了)
            adapter.updatePlayingMusicPosition(event.getPosition()+1);
            adapter.notifyDataSetChanged();
        }else {
            //当用户点开一个歌手页后如果不打算循坏该歌手的歌，但是又不退出歌手页，而这时fm切到下一首歌了（已经不是歌手页这个歌手了），则要把之前显示播放的那首歌设置为未播放的样式
            adapter.updatePlayingMusicPosition(-1);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_follow_artist:
                if(myDatabaseHelper.isFollowArtist(artistId)){
                    myDatabaseHelper.unfollowArtist(artistId);
                    btn_follow_artist.setText("关注");
                }else {
                    myDatabaseHelper.followArtist(artistId,artistName,artistPicUrl);
                    btn_follow_artist.setText("已关注");
                }
        }
    }
}
