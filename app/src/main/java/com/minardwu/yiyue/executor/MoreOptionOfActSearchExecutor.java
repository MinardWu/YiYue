package com.minardwu.yiyue.executor;

import android.app.Activity;
import android.content.Intent;

import com.minardwu.yiyue.activity.AlbumActivity;
import com.minardwu.yiyue.activity.ArtistActivity;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.utils.ToastUtils;

/**
 * Created by MinardWu on 2018/3/15.
 */

public class MoreOptionOfActSearchExecutor {
    public static void execute(Activity activity,int position, MusicBean musicBean){
        switch (position){
            case 0:
                //position==0逻辑放在外面执行，不会跳到这里
                break;
            case 1:
                ToastUtils.showShortToast("id:"+musicBean.getArtistId());
                Intent artistIntent = new Intent(activity, ArtistActivity.class);
                artistIntent.putExtra("artistId",musicBean.getArtistId());
                artistIntent.putExtra("artistName",musicBean.getArtistName());
                activity.startActivity(artistIntent);
                break;
            case 2:
                Intent albumIntent = new Intent(activity, AlbumActivity.class);
                albumIntent.putExtra("albumId",musicBean.getAlbumId());
                albumIntent.putExtra("albumName",musicBean.getAlbum());
                activity.startActivity(albumIntent);
                break;
            case 3:
                AppCache.getPlayOnlineMusicService().appendMusicList(musicBean);
                break;
        }
    }
}
