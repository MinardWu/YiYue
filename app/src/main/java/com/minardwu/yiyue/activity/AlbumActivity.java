package com.minardwu.yiyue.activity;

import android.graphics.Bitmap;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.ImageAndTextAdapter;
import com.minardwu.yiyue.adapter.OnlineMusicRecycleViewAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.fragment.OptionDialogFragment;
import com.minardwu.yiyue.http.GetOnlineAlbum;
import com.minardwu.yiyue.http.HttpCallback;
import com.minardwu.yiyue.model.AlbumBean;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.PlayOnlineMusicService;
import com.minardwu.yiyue.utils.ImageUtils;
import com.minardwu.yiyue.utils.ParseUtils;
import com.minardwu.yiyue.widget.LoadingView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumActivity extends AppCompatActivity {

    @BindView(R.id.rl_album_songs)
    RecyclerView rl_album_songs;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsing_toolbar_layout;
    @BindView(R.id.app_bar_layout)
    AppBarLayout app_bar_layout;
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
    @BindView(R.id.iv_back) ImageView iv_back;
    @BindView(R.id.loading_view)
    LoadingView loading_view;

    private String albumId;
    private String albumName;
    private int song_conut;
    PlayOnlineMusicService playOnlineMusicService;
    OnlineMusicRecycleViewAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    MyDatabaseHelper myDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        ButterKnife.bind(this);
        playOnlineMusicService = AppCache.getPlayOnlineMusicService();
        albumId = getIntent().getStringExtra("albumId");
        albumName = getIntent().getStringExtra("albumName");
        GetOnlineAlbum.getOnlineAlbum(albumId, new HttpCallback<AlbumBean>() {
            @Override
            public void onSuccess(AlbumBean albumBean) {
                initView(albumBean);
            }

            @Override
            public void onFail(String e) {

            }
        });

    }

    private void initView(final AlbumBean albumBean){
        ImageUtils.getBitmapByUrl(albumBean.getPicUrl(), new HttpCallback<Bitmap>() {
            @Override
            public void onSuccess(final Bitmap bitmap) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv_album_cover.setImageBitmap(bitmap);
                        iv_bg.setImageBitmap(ImageUtils.blur(AlbumActivity.this,bitmap,0.01f,25));
                    }
                });
            }

            @Override
            public void onFail(String e) {

            }
        });
//        ImageUtils.getBlurBitmapByUrl(albumBean.getPicUrl(), new HttpCallback<Bitmap>() {
//            @Override
//            public void onSuccess(final Bitmap bitmap) {
//                iv_bg.setImageBitmap(bitmap);
//            }
//
//            @Override
//            public void onFail(String e) {
//
//            }
//        });
        final List<MusicBean> list = albumBean.getSongs();
        adapter = new OnlineMusicRecycleViewAdapter(list);
        linearLayoutManager = new LinearLayoutManager(this);
        tv_album_name.setText(albumBean.getAlbumName());
        tv_album_artist.setText("歌手："+albumBean.getArtist().getName()+" >");
        tv_album_time.setText("发行时间："+ParseUtils.formatTimeOfPattern("yyyy-MM-dd",albumBean.getPublishTime()));
        loading_view.setVisibility(View.GONE);
        rl_album_songs.setLayoutManager(linearLayoutManager);
        rl_album_songs.setAdapter(adapter);
        adapter.setHeaderText(albumId);
        adapter.setOnRecycleViewClickListener(new OnlineMusicRecycleViewAdapter.OnRecycleViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position==0) {
                    playOnlineMusicService.startOrStopLoop(albumId,list);
                    adapter.setHeaderText(albumId);
                }else if(playOnlineMusicService.getPlayingMusicId().equals(list.get(position-1).getId()+"")){
                    finish();
                }else {
                    playOnlineMusicService.stop();
                    playOnlineMusicService.play((int) adapter.getMusicList().get(position-1).getId());
                    playOnlineMusicService.updataPlayingMusicPosition(position-1);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onMoreClick(View view, int position) {
                OptionDialogFragment fragment = new OptionDialogFragment();
                fragment.setHeader_titile("歌曲：");
                fragment.setHeader_text(list.get(position-1).getTitle());
                fragment.setListViewAdapter(new ImageAndTextAdapter(AlbumActivity.this,R.array.artist_activity_more_img,R.array.artist_activity_more_text));
                fragment.setOptionDialogFragmentClickListener(new OptionDialogFragment.OptionDialogFragmentClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        Toast.makeText(AlbumActivity.this, position+"", Toast.LENGTH_SHORT).show();
                    }
                });
                fragment.show(getSupportFragmentManager(), "OptionDialogFragment");
            }
        });
    }

}
