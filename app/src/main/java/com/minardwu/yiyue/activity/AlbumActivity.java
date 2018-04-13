package com.minardwu.yiyue.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.ImageAndTextAdapter;
import com.minardwu.yiyue.adapter.OnlineMusicRecycleViewAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.executor.MoreOptionOfActAlbumExecutor;
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
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.utils.UIUtils;
import com.minardwu.yiyue.widget.LoadingView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    @BindView(R.id.iv_bg)
    ImageView iv_bg;
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
    @BindView(R.id.iv_more)
    ImageView iv_more;
    @BindView(R.id.loading_view)
    LoadingView loading_view;

    private String albumId;
    private String albumName;
    private int song_conut;
    private PlayOnlineMusicService playOnlineMusicService;
    private OnlineMusicRecycleViewAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private MyDatabaseHelper myDatabaseHelper;
    private Bitmap blurBitmap;
    private AlbumBean albumBean;

    public static final String ALBUM_ID = "albumId";
    public static final String ALBUM_NAME = "albumName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        ButterKnife.bind(this);
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

    }

    private void initToolBarView(){
        int toolbarPadding = (int) getResources().getDimension(R.dimen.toolbar_paddingTop_top);
        int toolbarOriginHeight = UIUtils.getToolbarHeight(this);
        CollapsingToolbarLayout.LayoutParams toolbarLayoutParams = new CollapsingToolbarLayout.LayoutParams(CollapsingToolbarLayout.LayoutParams.MATCH_PARENT, toolbarPadding+toolbarOriginHeight);
        toolbarLayoutParams.setCollapseMode(CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN);
        toolbar.setLayoutParams(toolbarLayoutParams);
        toolbar.setPadding(0,toolbarPadding,0,0);
        RelativeLayout.LayoutParams albumCoverLayoutParams = new RelativeLayout.LayoutParams(UIUtils.dp2px(this,120),UIUtils.dp2px(this,120));
        tv_toolbar_album_name.setText("专辑");
        albumCoverLayoutParams.setMargins(UIUtils.dp2px(this,18),toolbarPadding+toolbarOriginHeight+UIUtils.dp2px(this,8),0,0);
        iv_album_cover.setLayoutParams(albumCoverLayoutParams);
        setTitleToCollapsingToolbarLayout();
        iv_back.setOnClickListener(this);
        iv_more.setOnClickListener(this);
    }

    private void initView(final AlbumBean albumBean){
        ImageUtils.getBitmapByUrl(albumBean.getPicUrl(), new HttpCallback<Bitmap>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onSuccess(final Bitmap bitmap) {
                blurBitmap = ImageUtils.blur(AlbumActivity.this,bitmap,0.01f,25);
                toolbar.setBackground(new BitmapDrawable(blurBitmap));
                toolbar.getBackground().setAlpha(0);
                iv_bg.setImageBitmap(blurBitmap);
                iv_album_cover.setImageBitmap(bitmap);
            }

            @Override
            public void onFail(FailResult result) {

            }
        });
        final List<MusicBean> list = albumBean.getSongs();
        adapter = new OnlineMusicRecycleViewAdapter(list);
        linearLayoutManager = new LinearLayoutManager(this);
        tv_album_name.setText(albumBean.getAlbumName());
        tv_album_artist.setText("歌手："+albumBean.getArtist().getName()+" >");
        tv_album_time.setText("发行时间："+ParseUtils.formatTimeOfPattern("yyyy-MM-dd",albumBean.getPublishTime()));
        tv_album_artist.setOnClickListener(this);
        loading_view.setVisibility(View.GONE);
        rl_album_songs.setLayoutManager(linearLayoutManager);
        rl_album_songs.setAdapter(adapter);
        adapter.setOnRecycleViewClickListener(new OnlineMusicRecycleViewAdapter.OnRecycleViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position==0) {
                    playOnlineMusicService.playMusicList(list);
                }else if(playOnlineMusicService.getPlayingMusicId().equals(list.get(position-1).getId()+"")){
                    finish();
                }else {
                    playOnlineMusicService.stop();
                    playOnlineMusicService.play((int) adapter.getMusicList().get(position-1).getId());
                    playOnlineMusicService.updatePlayingMusicPosition(position-1);
                    adapter.notifyDataSetChanged();
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

    private void setTitleToCollapsingToolbarLayout() {
        app_bar_layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int offsetWhenCollapse = appBarLayout.getTotalScrollRange();
                toolbar.getBackground().setAlpha((int)((float)Math.abs(verticalOffset)/(float)offsetWhenCollapse*255));
                if (Math.abs(verticalOffset) == offsetWhenCollapse) {
                    tv_toolbar_album_name.setText(albumName);
                } else {
                    tv_toolbar_album_name.setText("专辑");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_album_artist:
                Intent artistIntent = new Intent(this,ArtistActivity.class);
                artistIntent.putExtra("artistName",albumBean.getArtist().getName());
                artistIntent.putExtra("artistId",albumBean.getArtist().getId());
                startActivity(artistIntent);
            case R.id.iv_more:
                MyDatabaseHelper.init(AlbumActivity.this).addCollectedAlbum(albumBean);
                ToastUtils.show("11");
        }
    }

    private void handleError(FailResult result){
        switch (result.getResultCode()){
            case ResultCode.NETWORK_ERROR:
                ToastUtils.show(UIUtils.getString(R.string.network_error));
                break;
            case ResultCode.GET_ALBUM_INFO_ERROR:
                ToastUtils.show(UIUtils.getString(R.string.server_error));
                break;
            case ResultCode.GET_BITMAP_BY_URL_ERROR:
                //暂时不作处理
                break;
            default:
                break;
        }
    }
}
