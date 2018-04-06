package com.minardwu.yiyue.executor;

import android.app.Activity;
import android.content.Intent;

import com.minardwu.yiyue.activity.AlbumActivity;
import com.minardwu.yiyue.activity.ArtistActivity;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.model.ArtistBean;
import com.minardwu.yiyue.model.MusicBean;

/**
 * Created by MinardWu on 2018/3/15.
 */

public class MoreOptionOfCollectedArtistExecutor {
    public static void execute(Activity activity, int position, ArtistBean artistBean,IView iView){
        switch (position){
            case 0:
                MyDatabaseHelper.init(activity).unfollowArtist(artistBean.getId());
                iView.updateViewForExecutor();
                break;
            case 1:
                Intent artistIntent = new Intent(activity, ArtistActivity.class);
                artistIntent.putExtra("artistId",artistBean.getId());
                artistIntent.putExtra("artistName",artistBean.getName());
                activity.startActivity(artistIntent);
                break;
            case 2:

                break;
            case 3:

                break;
        }
    }
}
