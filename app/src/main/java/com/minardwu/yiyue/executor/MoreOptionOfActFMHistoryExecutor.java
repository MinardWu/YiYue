package com.minardwu.yiyue.executor;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.activity.AlbumActivity;
import com.minardwu.yiyue.activity.ArtistActivity;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.utils.UIUtils;
import com.minardwu.yiyue.widget.dialog.YesOrNoDialog;

/**
 * Created by MinardWu on 2018/3/15.
 */

public class MoreOptionOfActFMHistoryExecutor {
    public static void execute(final Activity activity, int position, final MusicBean musicBean, final IView iView){
        switch (position){
            case 0:
                //position==0逻辑放在外面执行，不会跳到这里
                break;
            case 1:
                Intent artistIntent = new Intent(activity, ArtistActivity.class);
                artistIntent.putExtra(ArtistActivity.ARTIST_NAME,musicBean.getArtistName());
                artistIntent.putExtra(ArtistActivity.ARTIST_ID,musicBean.getArtistId());
                activity.startActivity(artistIntent);
                break;
            case 2:
                Intent albumIntent = new Intent(activity, AlbumActivity.class);
                albumIntent.putExtra(AlbumActivity.ALBUM_ID,musicBean.getAlbumId());
                albumIntent.putExtra(AlbumActivity.ALBUM_NAME,musicBean.getAlbum());
                activity.startActivity(albumIntent);
                break;
            case 3:
                YesOrNoDialog dialog = new YesOrNoDialog.Builder()
                        .context(activity)
                        .title(UIUtils.getString(R.string.is_delete_fm_history))
                        .yes(UIUtils.getString(R.string.sure), new YesOrNoDialog.PositiveClickListener() {
                            @Override
                            public void OnClick(YesOrNoDialog dialog1,View view) {
                                MyDatabaseHelper.init(activity).deleteFMHistory(musicBean);
                                iView.updateViewForExecutor();
                                dialog1.dismiss();
                            }
                        })
                        .no(UIUtils.getString(R.string.cancel), new YesOrNoDialog.NegativeClickListener() {
                            @Override
                            public void OnClick(YesOrNoDialog dialog1,View view) {
                                dialog1.dismiss();
                            }
                        })
                        .noTextColor(UIUtils.getColor(R.color.colorGreenLight))
                        .build();
                dialog.show();
                break;
        }
    }
}
