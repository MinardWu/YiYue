package com.minardwu.yiyue.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.ImageAndTextAdapter;
import com.minardwu.yiyue.adapter.OnlineMusicRecycleViewAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.event.PlayNewOnlineMusicEvent;
import com.minardwu.yiyue.executor.MoreOptionOfActAlbumExecutor;
import com.minardwu.yiyue.fragment.AlbumInfoFragment;
import com.minardwu.yiyue.fragment.OptionDialogFragment;
import com.minardwu.yiyue.http.result.FailResult;
import com.minardwu.yiyue.http.GetOnlineAlbum;
import com.minardwu.yiyue.http.HttpCallback;
import com.minardwu.yiyue.http.result.ResultCode;
import com.minardwu.yiyue.model.AlbumBean;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.PlayOnlineMusicService;
import com.minardwu.yiyue.utils.ImageUtils;
import com.minardwu.yiyue.utils.ParseUtils;
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
 * @date : 2018/3/23
 */

public class AlbumActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.app_bar_layout)
    AppBarLayout app_bar_layout;
    @BindView(R.id.rl_top)
    RelativeLayout rl_top;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_toolbar_album_name)
    TextView tv_toolbar_album_name;
    @BindView(R.id.rl_album_songs)
    RecyclerView rl_album_songs;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsing_toolbar_layout;
    @BindView(R.id.iv_album_cover)
    ImageView iv_album_cover;
    @BindView(R.id.tv_album_name)
    TextView tv_album_name;
    @BindView(R.id.tv_album_artist)
    TextView tv_album_artist;
    @BindView(R.id.tv_album_time)
    TextView tv_album_time;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.loading_view)
    LoadingView loading_view;
    @BindView(R.id.root)
    CoordinatorLayout root;
    @BindView(R.id.collected_layout)
    LinearLayout collected_layout;
    @BindView(R.id.ll_ic_album_collected)
    LinearLayout ll_ic_album_collected;
    @BindView(R.id.ll_ic_album_artist)
    LinearLayout ll_ic_album_artist;
    @BindView(R.id.ll_ic_album_info)
    LinearLayout ll_ic_album_info;
    @BindView(R.id.ll_ic_album_multiple_choose)
    LinearLayout ll_ic_album_multiple_choose;
    @BindView(R.id.iv_album_collected)
    ImageView iv_album_collected;
    @BindView(R.id.tv_album_collected)
    TextView tv_album_collected;

    private String albumId;
    private String albumName;
    private PlayOnlineMusicService playOnlineMusicService;
    private OnlineMusicRecycleViewAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private Bitmap coverBitmap;
    private Bitmap blurBitmap;
    private Bitmap toolbarBitmap;
    private AlbumBean albumBean;
    private ArrayList<MusicBean> list;

    public static final String ALBUM_ID = "albumId";
    public static final String ALBUM_NAME = "albumName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        playOnlineMusicService = AppCache.getPlayOnlineMusicService();
        albumId = getIntent().getStringExtra(ALBUM_ID);
        albumName = getIntent().getStringExtra(ALBUM_NAME);

        initToolBarView();
        GetOnlineAlbum.getOnlineAlbum(albumId, new HttpCallback<AlbumBean>() {
            @Override
            public void onSuccess(AlbumBean bean) {
                albumBean = bean;
                initView(bean);
            }

            @Override
            public void onFail(FailResult result) {
                handleError(result);
            }
        });
        initClickAction();
    }

    private void initToolBarView(){
        int toolbarPadding = (int) getResources().getDimension(R.dimen.toolbar_paddingTop_top);
        int toolbarOriginHeight = UIUtils.getToolbarHeight(this);
        CollapsingToolbarLayout.LayoutParams toolbarLayoutParams = new CollapsingToolbarLayout.LayoutParams(CollapsingToolbarLayout.LayoutParams.MATCH_PARENT, toolbarPadding+toolbarOriginHeight);
        toolbarLayoutParams.setCollapseMode(CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN);
        toolbar.setLayoutParams(toolbarLayoutParams);
        toolbar.setPadding(0,toolbarPadding,0,0);
        tv_toolbar_album_name.setText("专辑");
//        RelativeLayout.LayoutParams albumCoverLayoutParams = new RelativeLayout.LayoutParams(UIUtils.dp2px(this,120),UIUtils.dp2px(this,120));
//        albumCoverLayoutParams.setMargins(UIUtils.dp2px(this,18),toolbarPadding+toolbarOriginHeight+UIUtils.dp2px(this,8),0,0);
//        iv_album_cover.setLayoutParams(albumCoverLayoutParams);
        setTitleToCollapsingToolbarLayout();
    }

    private void initView(final AlbumBean albumBean){
        ImageUtils.getBitmapByUrl(albumBean.getPicUrl(), new HttpCallback<Bitmap>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onSuccess(final Bitmap bitmap) {
                coverBitmap = bitmap;
                blurBitmap = ImageUtils.getFullScreenBlurBitmap(bitmap);
                toolbarBitmap = Bitmap.createBitmap(blurBitmap,0,0,blurBitmap.getWidth(),blurBitmap.getHeight()/2);

                iv_album_cover.setImageBitmap(bitmap);
                root.setBackground(new BitmapDrawable(blurBitmap));
//                toolbar.setBackground(new BitmapDrawable(toolbarBitmap));
//                toolbar.getBackground().setAlpha(0);
            }

            @Override
            public void onFail(FailResult result) {

            }
        });
        list = albumBean.getSongs();
        adapter = new OnlineMusicRecycleViewAdapter(this,list);
        linearLayoutManager = new LinearLayoutManager(this);
        tv_album_name.setText(albumBean.getAlbumName());
        tv_album_artist.setText("歌手："+albumBean.getArtist().getName()+" >");
        tv_album_time.setText("发行时间："+ParseUtils.formatTimeOfPattern("yyyy-MM-dd",albumBean.getPublishTime()));
        loading_view.setVisibility(View.GONE);
        rl_album_songs.setLayoutManager(linearLayoutManager);
        rl_album_songs.setAdapter(adapter);
        setCollectedState(MyDatabaseHelper.init(getContext()).isCollectedAlbum(albumBean.getAlbumId()));
        adapter.setOnRecycleViewClickListener(new OnlineMusicRecycleViewAdapter.OnRecycleViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position==0) {
                    playOnlineMusicService.playMusicList(list);
                    finish();
                    SystemUtils.startMainActivity(AlbumActivity.this,MainActivity.ONLINE);
                }else if(playOnlineMusicService.getPlayingMusicId()==list.get(position-1).getId()){
                    finish();
                    SystemUtils.startMainActivity(AlbumActivity.this,MainActivity.ONLINE);
                }else {
                    if(playOnlineMusicService.isPlayList()){
                        playOnlineMusicService.playOtherWhenPlayList(list.get(position-1));
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
                fragment.setHeader_text(list.get(musicPosition-1).getTitle());
                fragment.setListViewAdapter(new ImageAndTextAdapter(AlbumActivity.this,R.array.activity_album_more_img,R.array.activity_album_more_text));
                fragment.setOptionDialogFragmentClickListener(new OptionDialogFragment.OptionDialogFragmentClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        fragment.dismiss();
                        if(position==0){
                            onItemClick(view,musicPosition);
                        }else {
                            MoreOptionOfActAlbumExecutor.execute(AlbumActivity.this,position,list.get(musicPosition-1));
                        }
                    }
                });
                fragment.show(getSupportFragmentManager(), "OptionDialogFragment");
            }
        });
    }

    private void initClickAction(){
        ll_ic_album_collected.setOnClickListener(this);
        ll_ic_album_artist.setOnClickListener(this);
        ll_ic_album_info.setOnClickListener(this);
        ll_ic_album_multiple_choose.setOnClickListener(this);
        iv_album_cover.setOnClickListener(this);
        tv_album_artist.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    private void setTitleToCollapsingToolbarLayout() {
        app_bar_layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int offsetWhenCollapse = appBarLayout.getTotalScrollRange();
                collected_layout.setAlpha(1f-(float)Math.abs(verticalOffset)/(float)offsetWhenCollapse);
                if (Math.abs(verticalOffset) == offsetWhenCollapse) {
                    tv_toolbar_album_name.setText(albumName);
                } else {
                    tv_toolbar_album_name.setText(UIUtils.getString(R.string.album));
                }
            }
        });
    }

    private void setCollectedState(boolean isCollected){
        if (isCollected){
            iv_album_collected.setSelected(true);
            tv_album_collected.setText("已收藏");
            //tv_album_collected.setTextColor(UIUtils.getColor(R.color.green_main));
        }else {
            iv_album_collected.setSelected(false);
            tv_album_collected.setText("收藏");
            //tv_album_collected.setTextColor(UIUtils.getColor(R.color.white));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_album_cover:
            case R.id.ll_ic_album_info:
                AlbumInfoFragment albumInfoFragment = AlbumInfoFragment.newInstance(albumBean,coverBitmap);
                albumInfoFragment.show(getFragmentManager(),"albumInfoFragment");
                break;
            case R.id.tv_album_artist:
            case R.id.ll_ic_album_artist:
                Intent artistIntent = new Intent(this,ArtistActivity.class);
                artistIntent.putExtra("artistName",albumBean.getArtist().getName());
                artistIntent.putExtra("artistId",albumBean.getArtist().getId());
                startActivity(artistIntent);
                break;
            case R.id.ll_ic_album_collected:
                if (MyDatabaseHelper.init(getContext()).isCollectedAlbum(albumBean.getAlbumId())){
                    YesOrNoDialog yesOrNoDialog = new YesOrNoDialog.Builder()
                            .context(getContext())
                            .title(UIUtils.getString(R.string.is_delete_collected_album))
                            .titleTextColor(UIUtils.getColor(R.color.grey))
                            .yes(UIUtils.getString(R.string.sure), new YesOrNoDialog.PositiveClickListener() {
                                @Override
                                public void OnClick(YesOrNoDialog dialog, View view) {
                                    dialog.dismiss();
                                    MyDatabaseHelper.init(getContext()).deleteCollectedAlbum(albumBean);
                                    setCollectedState(false);
                                    ToastUtils.showShortToast(R.string.delete_collected_album_success);
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
                    MyDatabaseHelper.init(getContext()).addCollectedAlbum(albumBean);
                    setCollectedState(true);
                    ToastUtils.showShortToast(R.string.collected_album_success);
                }
                break;
            case R.id.ll_ic_album_multiple_choose:
                Intent intent = new Intent(AlbumActivity.this, MultipleChoseMusicActivity.class);
                intent.putParcelableArrayListExtra("musicList",list);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void handleError(FailResult result){
        switch (result.getResultCode()){
            case ResultCode.NETWORK_ERROR:
                ToastUtils.showShortToast(UIUtils.getString(R.string.network_error));
                break;
            case ResultCode.GET_ALBUM_INFO_ERROR:
                ToastUtils.showShortToast(UIUtils.getString(R.string.server_error));
                break;
            case ResultCode.GET_BITMAP_BY_URL_ERROR:
                //暂时不作处理
                break;
            default:
                break;
        }
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
}
