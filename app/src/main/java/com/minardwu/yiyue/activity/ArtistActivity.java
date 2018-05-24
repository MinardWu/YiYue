package com.minardwu.yiyue.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.ImageAndTextAdapter;
import com.minardwu.yiyue.adapter.OnlineMusicRecycleViewAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.event.PlayNewOnlineMusicEvent;
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
import com.minardwu.yiyue.utils.SystemUtils;
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.utils.UIUtils;
import com.minardwu.yiyue.widget.LoadingView;
import com.minardwu.yiyue.widget.dialog.YesOrNoDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author MinardWu
 * @date : 2018/1/31
 */

public class ArtistActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.rl_artist_hot_songs) RecyclerView rl_artist_hot_songs;
    @BindView(R.id.collapsing_toolbar_layout) CollapsingToolbarLayout collapsing_toolbar_layout;
    @BindView(R.id.app_bar_layout) AppBarLayout app_bar_layout;
    @BindView(R.id.tv_artist_name) TextView tv_artist_name;
    @BindView(R.id.iv_bg) ImageView iv_bg;
    @BindView(R.id.iv_back) ImageView iv_back;
    @BindView(R.id.btn_follow_artist) Button btn_follow_artist;
    @BindView(R.id.loading_view) LoadingView loading_view;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.ll_black_cover)
    LinearLayout ll_black_cover;

    public static final String ARTIST_ID = "artistId";
    public static final String ARTIST_NAME = "artistName";

    PlayOnlineMusicService playOnlineMusicService;
    ArrayList<MusicBean> hotSongs = new ArrayList<MusicBean>();
    OnlineMusicRecycleViewAdapter adapter = new OnlineMusicRecycleViewAdapter(ArtistActivity.this,hotSongs);
    LinearLayoutManager linearLayoutManager;
    MyDatabaseHelper myDatabaseHelper;
    private ArtistBean artist;

    private Intent intent;
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
        artistId = intent.getStringExtra(ARTIST_ID);
        artistName = intent.getStringExtra(ARTIST_NAME);
        initToolBarView();
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
                        iv_bg.setImageBitmap(bitmap);
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

    private void initToolBarView(){
        int toolbarPadding = (int) getResources().getDimension(R.dimen.toolbar_paddingTop_top);
        int toolbarOriginHeight = UIUtils.getToolbarHeight(this);
        CollapsingToolbarLayout.LayoutParams toolbarLayoutParams = new CollapsingToolbarLayout.LayoutParams(CollapsingToolbarLayout.LayoutParams.MATCH_PARENT, toolbarPadding+toolbarOriginHeight);
        toolbarLayoutParams.setCollapseMode(CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN);
        toolbar.setLayoutParams(toolbarLayoutParams);
        toolbar.setPadding(0,toolbarPadding,0,0);
        setTitleToCollapsingToolbarLayout();
    }

    private void initView(){
        tv_artist_name.setText(artistName);
        setCollectedState();
        btn_follow_artist.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    private void setTitleToCollapsingToolbarLayout() {
        app_bar_layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int offsetWhenCollapse = appBarLayout.getTotalScrollRange();
                int alpha = (int)((float)Math.abs(verticalOffset)/(float)offsetWhenCollapse*255);
                btn_follow_artist.setAlpha(1f-(float)Math.abs(verticalOffset)/(float)offsetWhenCollapse);
                if(alpha<20){
                    alpha = 20;
                }
                if (alpha>200){
                    alpha = 200;
                }
                ll_black_cover.setBackgroundColor(Color.argb(alpha,0,0,0));
//                if (Math.abs(verticalOffset) == offsetWhenCollapse) {
//                    //设置toolbar为自定义样式的时候，会覆盖掉collapsing_toolbar_layout的title，所以这时下面的方法不能用
//                    collapsing_toolbar_layout.setTitle(artistName);
//                    collapsing_toolbar_layout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorGreenLight));
//                    collapsing_toolbar_layout.setCollapsedTitleGravity(Gravity.CENTER);
//                } else {
//                    btn_follow_artist.setVisibility(View.VISIBLE);
//                    collapsing_toolbar_layout.setTitle("");
//                    collapsing_toolbar_layout.setExpandedTitleGravity(Gravity.CENTER);
//                    collapsing_toolbar_layout.setExpandedTitleColor(Color.TRANSPARENT);
//                }
            }
        });
    }

    private void initListView(final ArtistBean artistBean){
        artistId = artistBean.getId();
        hotSongs = artistBean.getSongs();
        adapter = new OnlineMusicRecycleViewAdapter(this,hotSongs);
        linearLayoutManager = new LinearLayoutManager(this);

        loading_view.setVisibility(View.GONE);
        rl_artist_hot_songs.setLayoutManager(linearLayoutManager);
        rl_artist_hot_songs.setAdapter(adapter);
        adapter.setOnRecycleViewClickListener(new OnlineMusicRecycleViewAdapter.OnRecycleViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position==0) {
                    playOnlineMusicService.playMusicList(hotSongs);
                    finish();
                    SystemUtils.startMainActivity(ArtistActivity.this,MainActivity.ONLINE);
                }else if(playOnlineMusicService.getPlayingMusicId()==hotSongs.get(position-1).getId()){
                    finish();
                    SystemUtils.startMainActivity(ArtistActivity.this,MainActivity.ONLINE);
                }else {
                    if(playOnlineMusicService.isPlayList()){
                        playOnlineMusicService.playOtherWhenPlayList(hotSongs.get(position-1));
                        adapter.notifyDataSetChanged();
                    }else {
                        playOnlineMusicService.stop();
                        playOnlineMusicService.play(adapter.getMusicList().get(position-1).getId());
                        adapter.notifyDataSetChanged();
                    }
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
                ToastUtils.showShortToast(UIUtils.getString(R.string.network_error));
                break;
            case ResultCode.GET_ARTIST_INFO_ERROR:
                ToastUtils.showShortToast(UIUtils.getString(R.string.server_error));
                break;
            case ResultCode.GET_ARTIST_NO_FOUND:
                ToastUtils.showShortToast(UIUtils.getString(R.string.artist_no_found));
                break;
            case ResultCode.GET_BITMAP_BY_URL_ERROR:
                //暂时不作处理
                break;
            default:
                break;
        }
        ToastUtils.showShortToast(result.getException());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayNewOnlineMusicEvent(PlayNewOnlineMusicEvent event){
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
                    YesOrNoDialog yesOrNoDialog = new YesOrNoDialog.Builder()
                            .context(getContext())
                            .title(UIUtils.getString(R.string.is_delete_collected_artist))
                            .titleTextColor(UIUtils.getColor(R.color.grey))
                            .yes(UIUtils.getString(R.string.sure), new YesOrNoDialog.PositiveClickListener() {
                                @Override
                                public void OnClick(YesOrNoDialog dialog, View view) {
                                    dialog.dismiss();
                                    myDatabaseHelper.unfollowArtist(artistId);
                                    setCollectedState();
                                    ToastUtils.showShortToast(R.string.delete_collected_artist_success);
                                }
                            })
                            .no(UIUtils.getString(R.string.cancel), new YesOrNoDialog.NegativeClickListener() {
                                @Override
                                public void OnClick(YesOrNoDialog dialog, View view) {
                                    dialog.dismiss();
                                }
                            })
                            .noTextColor(UIUtils.getColor(R.color.green_main))
                            .build();
                    yesOrNoDialog.show();
                }else {
                    myDatabaseHelper.followArtist(artist);
                    ToastUtils.showShortToast(R.string.collected_artist_success);
                    setCollectedState();
                }
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void setCollectedState(){
        Drawable drawable = UIUtils.getDrawable(myDatabaseHelper.isFollowArtist(artistId)
                ? R.drawable.ic_chosen_white
                : R.drawable.ic_add_white);
        drawable.setBounds(0,0,UIUtils.dp2px(getContext(),14),UIUtils.dp2px(getContext(),14));  //width即为你需要设置的图片宽度，height即为你设置的图片的高度
        btn_follow_artist.setCompoundDrawables(drawable,null,null,null);
        btn_follow_artist.setCompoundDrawablePadding(10);
        btn_follow_artist.setSelected(myDatabaseHelper.isFollowArtist(artistId));
        btn_follow_artist.setText(myDatabaseHelper.isFollowArtist(artistId) ? "已关注":"关注");
    }

}
