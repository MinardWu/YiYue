package com.minardwu.yiyue.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.OnlineMusicListItemAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.event.UpdateOnlineMusicListPositionEvent;
import com.minardwu.yiyue.http.GetOnlineArtist;
import com.minardwu.yiyue.http.HttpCallback;
import com.minardwu.yiyue.model.ArtistBean;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.PlayOnlineMusicService;
import com.minardwu.yiyue.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistActivity extends AppCompatActivity{

    @BindView(R.id.lv_artist_hot_songs) ListView listView;

    PlayOnlineMusicService playOnlineMusicService;
    List<MusicBean> hontSongs = new ArrayList<MusicBean>();
    OnlineMusicListItemAdapter adapter = new OnlineMusicListItemAdapter(hontSongs);
    private String artistId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        playOnlineMusicService = AppCache.getPlayOnlineMusicService();
        Intent intent = getIntent();
        int type = intent.getIntExtra("type",0);
        if (type==0){
            String artist = intent.getStringExtra("artistName");
            GetOnlineArtist.getArtistIdByName(artist, new HttpCallback<ArtistBean>() {
                @Override
                public void onSuccess(ArtistBean artistBean) {
                    initListView(artistBean);
                }

                @Override
                public void onFail(String e) {
                    loadDataFail();
                }
            });
        }else if(type==1){
            String id = intent.getStringExtra("artistId");
            GetOnlineArtist.getArtistInfoById(id, new HttpCallback<ArtistBean>() {
                @Override
                public void onSuccess(ArtistBean artistBean) {
                    initListView(artistBean);
                }

                @Override
                public void onFail(String e) {
                    loadDataFail();
                }
            });

        }

    }

    private void initListView(ArtistBean artistBean){
        artistId = artistBean.getId();
        hontSongs = artistBean.getSongs();
        adapter = new OnlineMusicListItemAdapter(hontSongs);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
                adapter.updatePlayingMusicId(playOnlineMusicService);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //第一个条件是点击播放歌手歌单后进行判断用的
                        //第二个条件是刚点开歌手页进行判断的
                        if(i==adapter.getPlayingMusicPosition()||adapter.getTargetList().get(i).getId()==playOnlineMusicService.getPlayingMusic().getId()){
                            finish();
                        }else {
                            playOnlineMusicService.stop();
                            playOnlineMusicService.playTargetList(hontSongs,i);
                            adapter.updatePlayingMusicPosition(i);
                            adapter.notifyDataSetChanged();
                        }
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
