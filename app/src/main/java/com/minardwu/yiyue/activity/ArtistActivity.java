package com.minardwu.yiyue.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.ImageAndTextAdapter;
import com.minardwu.yiyue.adapter.OnlineMusicRecycleViewAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.event.UpdateOnlineMusicListPositionEvent;
import com.minardwu.yiyue.executor.MoreOptionOfActArtistExecutor;
import com.minardwu.yiyue.fragment.OptionDialogFragment;
import com.minardwu.yiyue.http.result.FailResult;
import com.minardwu.yiyue.http.GetOnlineArtist;
import com.minardwu.yiyue.http.HttpCallback;
import com.minardwu.yiyue.http.result.ResultCode;
import com.minardwu.yiyue.model.ArtistBean;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.PlayOnlineMusicService;
import com.minardwu.yiyue.utils.ImageUtils;
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.utils.UIUtils;
import com.minardwu.yiyue.widget.LoadingView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.rl_artist_hot_songs) RecyclerView rl_artist_hot_songs;
    @BindView(R.id.collapsing_toolbar_layout) CollapsingToolbarLayout collapsing_toolbar_layout;
    @BindView(R.id.app_bar_layout) AppBarLayout app_bar_layout;
    @BindView(R.id.tv_artist_name) TextView tv_artist_name;
    @BindView(R.id.iv_bg) ImageView iv_bg;
    @BindView(R.id.iv_back) ImageView iv_back;
    @BindView(R.id.iv_artist) ImageView iv_artist;
    @BindView(R.id.tv_artist_name_below_iv) TextView tv_artist_name_below_iv;
    @BindView(R.id.btn_follow_artist) Button btn_follow_artist;
    @BindView(R.id.loading_view) LoadingView loading_view;


    PlayOnlineMusicService playOnlineMusicService;
    List<MusicBean> hotSongs = new ArrayList<MusicBean>();
    OnlineMusicRecycleViewAdapter adapter = new OnlineMusicRecycleViewAdapter(hotSongs);
    LinearLayoutManager linearLayoutManager;
    MyDatabaseHelper myDatabaseHelper;
    private ArtistBean artist;

    private Intent intent;
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
        myDatabaseHelper = MyDatabaseHelper.init(this);
        intent = getIntent();
        artistName = intent.getStringExtra("artistName");
        artistId = intent.getStringExtra("artistId");
        initView();
        GetOnlineArtist.getArtistInfoById(artistId, new HttpCallback<ArtistBean>() {
            @Override
            public void onSuccess(final ArtistBean artistBean) {
                artist = artistBean;
                artistPicUrl = artistBean.getPicUrl();
                initListView(artistBean);
                ImageUtils.getBitmapByUrl(artistBean.getPicUrl(), new HttpCallback<Bitmap>() {
                    @Override
                    public void onSuccess(final Bitmap bitmap) {
                        if (myDatabaseHelper.isFollowArtist(artistId)){
                            myDatabaseHelper.updateArtistPic(artistId,artistPicUrl);
                        }
                        iv_artist.setImageBitmap(ImageUtils.createCircleImage(bitmap));
                        iv_bg.setImageBitmap(ImageUtils.blur(ArtistActivity.this,bitmap,0.1f,5));
                    }

                    @Override
                    public void onFail(FailResult result) {
                        loadDataFail(result);
                    }
                });
            }

            @Override
            public void onFail(FailResult result) {
                loadDataFail(result);
            }
        });
    }

    private void initView(){
        setTitleToCollapsingToolbarLayout();
        tv_artist_name.setText(artistName);
        tv_artist_name_below_iv.setText(artistName);
        iv_artist.setImageBitmap(ImageUtils.createCircleImage(BitmapFactory.decodeResource(getResources(),R.drawable.default_cover)));
        btn_follow_artist.setText(myDatabaseHelper.isFollowArtist(artistId) ? "已关注":"关注");
        btn_follow_artist.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    private void setTitleToCollapsingToolbarLayout() {
        app_bar_layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int offsetWhenCollapse = appBarLayout.getTotalScrollRange();
                if (Math.abs(verticalOffset) == offsetWhenCollapse) {
                    btn_follow_artist.setVisibility(View.INVISIBLE);//解决快速上拉时button会与标题重叠的问题
                    tv_artist_name_below_iv.setVisibility(View.INVISIBLE);//解决快速上拉时会与标题重叠的问题
                    tv_artist_name.setVisibility(View.VISIBLE);
                    //设置toolbar为自定义样式的时候，会覆盖掉collapsing_toolbar_layout的title，所以这时下面的方法不能用
//                    collapsing_toolbar_layout.setTitle(artistName);
//                    collapsing_toolbar_layout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorGreenLight));
//                    collapsing_toolbar_layout.setCollapsedTitleGravity(Gravity.CENTER);
                } else {
                    btn_follow_artist.setVisibility(View.VISIBLE);
                    tv_artist_name_below_iv.setVisibility(View.VISIBLE);
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
        hotSongs = artistBean.getSongs();
        adapter = new OnlineMusicRecycleViewAdapter(hotSongs);
        linearLayoutManager = new LinearLayoutManager(this);

        loading_view.setVisibility(View.GONE);
        rl_artist_hot_songs.setLayoutManager(linearLayoutManager);
        rl_artist_hot_songs.setAdapter(adapter);
        adapter.setHeaderText(artistBean.getId());
        adapter.setOnRecycleViewClickListener(new OnlineMusicRecycleViewAdapter.OnRecycleViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position==0) {
                    playOnlineMusicService.startOrStopLoop(artistBean.getId(), hotSongs);
                    adapter.setHeaderText(artistBean.getId());
                }else if(playOnlineMusicService.getPlayingMusicId().equals(hotSongs.get(position-1).getId()+"")){
                    finish();
                }else {
                    playOnlineMusicService.stop();
                    playOnlineMusicService.play((int) adapter.getMusicList().get(position-1).getId());
                    playOnlineMusicService.updataPlayingMusicPosition(position-1);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onMoreClick(View view, final int musicPosition) {
                final OptionDialogFragment fragment = new OptionDialogFragment();
                fragment.setHeader_titile("歌曲：");
                fragment.setHeader_text(hotSongs.get(musicPosition-1).getTitle());
                fragment.setListViewAdapter(new ImageAndTextAdapter(ArtistActivity.this,R.array.activity_artist_more_img,R.array.activity_artist_more_text));
                fragment.setOptionDialogFragmentClickListener(new OptionDialogFragment.OptionDialogFragmentClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        fragment.dismiss();
                        if(position==0){
                            onItemClick(view,musicPosition);
                        }else {
                            MoreOptionOfActArtistExecutor.execute(ArtistActivity.this,position,hotSongs.get(musicPosition-1));
                        }
                    }
                });
                fragment.show(getSupportFragmentManager(), "OptionDialogFragment");
            }
        });


    }

    private void loadDataFail(final FailResult result){
        switch (result.getResultCode()){
            case ResultCode.NETWORK_ERROR:
                ToastUtils.show(UIUtils.getString(ArtistActivity.this,R.string.network_error));
                break;
            case ResultCode.GET_ARTIST_INFO_ERROR:
                ToastUtils.show(UIUtils.getString(ArtistActivity.this,R.string.server_error));
                break;
            case ResultCode.GET_ARTIST_NO_FOUND:
                ToastUtils.show(UIUtils.getString(ArtistActivity.this,R.string.artist_no_found));
                break;
            case ResultCode.GET_BITMAP_BY_URL_ERROR:
                //暂时不作处理
                break;
            default:
                break;
        }
        ToastUtils.show(result.getException());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateOnlineMusicListPositionEvent(UpdateOnlineMusicListPositionEvent event){
        adapter.notifyDataSetChanged();
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
                    myDatabaseHelper.followArtist(artist);
                    btn_follow_artist.setText("已关注");
                }
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

}
