package com.minardwu.yiyue.activity;

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
import com.minardwu.yiyue.service.OnPlayOnlineMusicListener;
import com.minardwu.yiyue.service.OnPlayerEventListener;
import com.minardwu.yiyue.service.PlayOnlineMusicService;

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
        //playOnlineMusicService.setPlayOnlineMusicListener(this);
        GetOnlineArtist.getArtistIdByName("周杰伦", new HttpCallback<ArtistBean>() {
            @Override
            public void onSuccess(ArtistBean artistBean) {
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

            @Override
            public void onFail(String e) {

            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateOnlineMusicListPositionEvent(UpdateOnlineMusicListPositionEvent event){
        if (this.artistId.equals(event.getArtistId())){
            adapter.updatePlayingMusicPosition(event.getPosition());
            adapter.notifyDataSetChanged();
        }
    }
}
