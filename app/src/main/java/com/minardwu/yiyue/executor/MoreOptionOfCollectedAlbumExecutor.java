package com.minardwu.yiyue.executor;

import android.app.Activity;
import android.content.Intent;

import com.minardwu.yiyue.activity.ArtistActivity;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.model.AlbumBean;
import com.minardwu.yiyue.model.ArtistBean;

/**
 * Created by MinardWu on 2018/3/15.
 */

public class MoreOptionOfCollectedAlbumExecutor {
    public static void execute(Activity activity, int position, AlbumBean albumBean, IView iView){
        switch (position){
            case 0:
                MyDatabaseHelper.init(activity).deleteCollectedAlbum(albumBean);
                iView.updateViewForExecutor();
                break;
            case 1:
                Intent artistIntent = new Intent(activity, ArtistActivity.class);
                artistIntent.putExtra("artistId",albumBean.getArtist().getId());
                artistIntent.putExtra("artistName",albumBean.getArtist().getName());
                activity.startActivity(artistIntent);
                break;
            default:
                break;
        }
    }
}
