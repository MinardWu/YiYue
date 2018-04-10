package com.minardwu.yiyue.executor;

import android.app.Activity;
import android.content.Intent;

import com.minardwu.yiyue.activity.ArtistActivity;
import com.minardwu.yiyue.model.MusicBean;

/**
 * Created by MinardWu on 2018/3/15.
 */

public class MoreOptionOfActAlbumExecutor {
    public static void execute(Activity activity,int position, MusicBean musicBean){
        switch (position){
            case 0:
                //position==0逻辑放在外面执行，不会跳到这里
                break;
            case 1:
                Intent artistIntent = new Intent(activity, ArtistActivity.class);
                artistIntent.putExtra("artistName",musicBean.getArtistName());
                artistIntent.putExtra("artistId",musicBean.getArtistId());
                activity.startActivity(artistIntent);
                break;
        }
    }
}
