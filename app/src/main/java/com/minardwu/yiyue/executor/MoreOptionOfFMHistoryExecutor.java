package com.minardwu.yiyue.executor;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.minardwu.yiyue.activity.AlbumActivity;
import com.minardwu.yiyue.activity.ArtistActivity;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.http.GetOnlineAlbum;
import com.minardwu.yiyue.http.HttpCallback;
import com.minardwu.yiyue.model.AlbumBean;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.utils.ToastUtils;

/**
 * Created by MinardWu on 2018/3/15.
 */

public class MoreOptionOfFMHistoryExecutor {
    public static void execute(Activity activity,int position, MusicBean musicBean,IView iView){
        switch (position){
            case 0:
                //position==0逻辑放在外面执行，不会跳到这里
                break;
            case 1:
                Intent artistIntent = new Intent(activity, ArtistActivity.class);
                artistIntent.putExtra("artistName",musicBean.getArtist());
                artistIntent.putExtra("artistId",musicBean.getArtistId());
                activity.startActivity(artistIntent);
                break;
            case 2:
                Intent albumIntent = new Intent(activity, AlbumActivity.class);
                albumIntent.putExtra("albumId",musicBean.getAlbumId());
                albumIntent.putExtra("albumName",musicBean.getAlbum());
                activity.startActivity(albumIntent);
                break;
            case 3:
                MyDatabaseHelper.init(activity).deleteFMHistory(musicBean);
                iView.updateViewForExecutor();
                break;
        }
    }
}
