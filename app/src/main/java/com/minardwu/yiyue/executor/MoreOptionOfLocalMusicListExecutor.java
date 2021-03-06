package com.minardwu.yiyue.executor;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.activity.SearchActivity;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.utils.FileUtils;
import com.minardwu.yiyue.utils.MusicUtils;
import com.minardwu.yiyue.utils.SystemUtils;
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.utils.UIUtils;
import com.minardwu.yiyue.widget.dialog.YesOrNoDialog;

import java.io.File;

/**
 * Created by MinardWu on 2018/3/15.
 */

public class MoreOptionOfLocalMusicListExecutor {
    public static void execute(final Activity activity, int position, final MusicBean musicBean, final IView iView){
        switch (position){
            case 0:
                //position==0逻辑放在外面执行，不会跳到这里
                break;
            case 1:
                Intent searchArtistIntent = new Intent(activity, SearchActivity.class);
                searchArtistIntent.putExtra(SearchActivity.SEARCH_VALUE,musicBean.getArtistName());
                activity.startActivity(searchArtistIntent);
                break;
            case 2:
                Intent searchAlbumIntent = new Intent(activity, SearchActivity.class);
                searchAlbumIntent.putExtra(SearchActivity.SEARCH_VALUE,musicBean.getAlbum());
                activity.startActivity(searchAlbumIntent);
                break;
            case 3:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 判断是否有WRITE_SETTINGS权限
                    if(!Settings.System.canWrite(activity)) {
                        ToastUtils.showShortToast("请开通相关权限，否则无法正常使用该功能！");
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                                Uri.parse("package:" + activity.getPackageName()));
                        activity.startActivityForResult(intent, SystemUtils.REQUEST_WRITE_SETTING);
                    } else {
                        File music = new File(musicBean.getPath());
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.MediaColumns.DATA, music.getAbsolutePath());
                        values.put(MediaStore.MediaColumns.TITLE, music.getName());
                        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
                        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
                        values.put(MediaStore.Audio.Media.IS_ALARM, false);
                        values.put(MediaStore.Audio.Media.IS_MUSIC, true);
                        Uri uri = MediaStore.Audio.Media.getContentUriForPath(music.getAbsolutePath());
                        Uri newUri = activity.getContentResolver().insert(uri, values);
                        RingtoneManager.setActualDefaultRingtoneUri(activity, RingtoneManager.TYPE_RINGTONE, newUri);
                        Toast.makeText(activity,"设置来电铃声成功！", Toast.LENGTH_SHORT ).show();
                    }
                } else {

                }
                break;
            case 4:
                YesOrNoDialog dialog = new YesOrNoDialog.Builder()
                        .context(activity)
                        .subtitle(R.string.is_delete_local_music)
                        .yes(UIUtils.getString(R.string.sure), new YesOrNoDialog.PositiveClickListener() {
                            @Override
                            public void OnClick(YesOrNoDialog dialog1,View view) {
                                dialog1.dismiss();
                                if(FileUtils.deleteFile(musicBean.getPath())){
                                    ToastUtils.showShortToast(R.string.delete_success);
                                    if(musicBean.getId() == MusicUtils.getLocalMusicPlayingMusic().getId()){
                                        int tempPosition = MusicUtils.getLocalMusicPlayingPosition();
                                        MusicUtils.removeMusic(musicBean.getId());
                                        AppCache.getPlayLocalMusicService().setPlayingPosition(tempPosition);
                                        if(AppCache.getPlayLocalMusicService().isPlaying()){
                                            AppCache.getPlayLocalMusicService().play(tempPosition);
                                        }else {
                                            AppCache.getPlayLocalMusicService().stop();
                                            AppCache.getPlayLocalMusicService().updateLocalMusicFragment(tempPosition);
                                        }
                                    }else if(MusicUtils.getLocalMusicPosition(musicBean.getId())<MusicUtils.getLocalMusicPlayingPosition()){
                                        int position = MusicUtils.getLocalMusicPlayingPosition();
                                        MusicUtils.removeMusic(musicBean.getId());
                                        AppCache.getPlayLocalMusicService().setPlayingPosition(position-1);
                                    }else {
                                        MusicUtils.removeMusic(musicBean.getId());
                                    }
                                    iView.updateViewForExecutor();
                                }else {
                                    ToastUtils.showShortToast(R.string.delete_fail);
                                }
                            }
                        })
                        .no(UIUtils.getString(R.string.cancel), new YesOrNoDialog.NegativeClickListener() {
                            @Override
                            public void OnClick(YesOrNoDialog dialog1,View view) {
                                dialog1.dismiss();
                            }
                        })
                        .build();
                dialog.show();
                break;
            default:
                break;
        }
    }
}
