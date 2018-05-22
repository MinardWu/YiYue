package com.minardwu.yiyue.executor;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.activity.AlbumActivity;
import com.minardwu.yiyue.activity.ArtistActivity;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.http.DownloadSong;
import com.minardwu.yiyue.http.result.FailResult;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.utils.UIUtils;
import com.minardwu.yiyue.widget.dialog.YesOrNoDialog;

/**
 * Created by MinardWu on 2018/3/15.
 */

public class MoreOptionOfCollectedSongExecutor {
    public static void execute(final Activity activity, int position, final MusicBean musicBean, final IView iView){
        switch (position){
            case 0:
                //position==0逻辑放在外面执行，不会跳到这里
                break;
            case 1:
                Intent artistIntent = new Intent(activity, ArtistActivity.class);
                artistIntent.putExtra(ArtistActivity.ARTIST_ID,musicBean.getArtistId());
                artistIntent.putExtra(ArtistActivity.ARTIST_NAME,musicBean.getArtistName());
                activity.startActivity(artistIntent);
                break;
            case 2:
                Intent albumIntent = new Intent(activity, AlbumActivity.class);
                albumIntent.putExtra(AlbumActivity.ALBUM_ID,musicBean.getAlbumId());
                albumIntent.putExtra(AlbumActivity.ALBUM_NAME,musicBean.getAlbum());
                activity.startActivity(albumIntent);
                break;
            case 3:
                DownloadSong.execute(activity, musicBean, new DownloadSong.DownloadSongCallBack() {
                    @Override
                    public void onSuccess() {
                        ToastUtils.showShortToast(R.string.download_begin);
                    }

                    @Override
                    public void onFail(FailResult failResult) {
                        ToastUtils.showShortToast(R.string.download_fail);
                    }
                });
                break;
            case 4:
                AppCache.getPlayOnlineMusicService().appendMusicList(musicBean);
                ToastUtils.showShortToast(R.string.append_online_music_list_success);
                break;
            case 5:
                YesOrNoDialog dialog = new YesOrNoDialog.Builder()
                        .context(activity)
                        .title(UIUtils.getString(R.string.is_delete_collected_song))
                        .yes(UIUtils.getString(R.string.sure), new YesOrNoDialog.PositiveClickListener() {
                            @Override
                            public void OnClick(YesOrNoDialog dialog1,View view) {
                                MyDatabaseHelper.init(activity).deleteCollectedSong(musicBean);
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
            default:
                break;
        }
    }
}
