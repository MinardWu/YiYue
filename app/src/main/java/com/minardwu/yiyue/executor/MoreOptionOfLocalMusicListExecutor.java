package com.minardwu.yiyue.executor;

import android.app.Activity;
import android.util.Log;

import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.utils.FileUtils;
import com.minardwu.yiyue.utils.ToastUtils;

/**
 * Created by MinardWu on 2018/3/15.
 */

public class MoreOptionOfLocalMusicListExecutor {
    public static void execute(Activity activity,int position, MusicBean musicBean,IView iView){
        switch (position){
            case 0:
                //position==0逻辑放在外面执行，不会跳到这里
                break;
            case 1:
//                Intent intent = new Intent(activity, ArtistActivity.class);
//                intent.putExtra("artistName",musicBean.getArtist());
//                intent.putExtra("artistId",musicBean.getArtistId());
//                activity.startActivity(intent);
                break;
            case 2:

                break;
            case 3:

                break;
            case 4:
                if(FileUtils.deleteFile(musicBean.getPath())){
                    ToastUtils.show("删除成功");
                    for (int i=0;i<AppCache.getLocalMusicList().size();i++){
                        if(AppCache.getLocalMusicList().get(i).getId()==musicBean.getId()){
                            AppCache.getLocalMusicList().remove(i);
                        }
                    }
                    iView.updateViewForExecutor();
                }else {
                    ToastUtils.show("删除失败");
                }
                break;
        }
    }
}
