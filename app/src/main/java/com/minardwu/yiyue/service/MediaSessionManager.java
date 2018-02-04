package com.minardwu.yiyue.service;

import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;


import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.application.YiYueApplication;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.utils.CoverLoader;


public class MediaSessionManager {
    private static final String TAG = "MediaSessionManager";
    private static final long MEDIA_SESSION_ACTIONS = PlaybackStateCompat.ACTION_PLAY
            | PlaybackStateCompat.ACTION_PAUSE
            | PlaybackStateCompat.ACTION_PLAY_PAUSE
            | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            | PlaybackStateCompat.ACTION_STOP
            | PlaybackStateCompat.ACTION_SEEK_TO;

    private MediaSessionCompat mMediaSession;

    public MediaSessionManager() {
        setupMediaSession();
    }

    //初始化MediaSession
    private void setupMediaSession() {
        mMediaSession = new MediaSessionCompat(YiYueApplication.getAppContext(), TAG);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        mMediaSession.setCallback(callback);
        mMediaSession.setActive(true);
    }

    //更新播放状态
    public void updatePlaybackState() {
        PlayService playingService = AppCache.getCurrentService();
        int state = (playingService.isPlaying() || playingService.isPreparing()) ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
        mMediaSession.setPlaybackState(
                new PlaybackStateCompat.Builder()
                        .setActions(MEDIA_SESSION_ACTIONS)
                        .setState(state, playingService.getCurrentPosition(), 1)
                        .build());
    }

    //更新播放的歌曲
    public void updateMetaData(MusicBean music) {
        if (music == null) {
            mMediaSession.setMetadata(null);
            return;
        }

        MediaMetadataCompat.Builder metaData = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, music.getTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, music.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, music.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, music.getArtist())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, music.getDuration());

        if(music.getType()==MusicBean.Type.LOCAL){
            metaData.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, CoverLoader.getInstance().loadThumbnail(music));
        }else{
            metaData.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, music.getOnlineMusicCover());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            metaData.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, AppCache.getLocalMusicList().size());
        }

        mMediaSession.setMetadata(metaData.build());
    }

    //释放
    public void release() {
        mMediaSession.setCallback(null);
        mMediaSession.setActive(false);
        mMediaSession.release();
    }

    private MediaSessionCompat.Callback callback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            AppCache.getCurrentService().playOrPause();
        }

        @Override
        public void onPause() {
            AppCache.getCurrentService().playOrPause();
        }

        @Override
        public void onSkipToNext() {
            AppCache.getCurrentService().next();
        }

        @Override
        public void onSkipToPrevious() {
            if(AppCache.getCurrentService() instanceof PlayOnlineMusicService){
                AppCache.getCurrentService().next();
            }else {
                AppCache.getCurrentService().prev();
            }
        }

        @Override
        public void onStop() {
            AppCache.getCurrentService().stop();
        }

        @Override
        public void onSeekTo(long pos) {
            AppCache.getCurrentService().seekTo((int) pos);
        }
    };
}
