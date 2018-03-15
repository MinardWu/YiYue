package com.minardwu.yiyue.executor;

import android.app.Activity;
import android.content.Intent;

import com.minardwu.yiyue.activity.ArtistActivity;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.model.MusicBean;

/**
 * Created by MinardWu on 2018/3/15.
 */

public class MoreOptionOfLoveSongExecutor {
    public static void execute(Activity activity,int position, MusicBean musicBean,IView iView){
        switch (position){
            case 0:
                //position==0逻辑放在外面执行，不会跳到这里
                break;
            case 1:
                Intent intent = new Intent(activity, ArtistActivity.class);
                intent.putExtra("artistId",musicBean.getArtistId());
                intent.putExtra("artistName",musicBean.getArtist());
                activity.startActivity(intent);
                break;
            case 2:

                break;
            case 3:
                MyDatabaseHelper.init(activity).deleteLoveSong(musicBean);
                iView.updateView();
                break;
        }
    }
}
