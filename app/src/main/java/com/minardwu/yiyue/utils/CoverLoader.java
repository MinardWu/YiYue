package com.minardwu.yiyue.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.model.MusicBean;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 专辑封面图片加载器
 */
public class CoverLoader {
    public static final int THUMBNAIL_MAX_LENGTH = 500;
    private static final String KEY_NULL = "null";

    // 封面缓存
    private LruCache<String, Bitmap> coverCache;
    private Context context;

    private enum Type {
        THUMBNAIL(""),
        BLUR("#BLUR"),
        ROUND("#ROUND");

        private String value;
        Type(String value) {
            this.value = value;
        }
    }

    private CoverLoader() {
        // 获取当前进程的可用内存（单位KB）
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 缓存大小为当前进程可用内存的1/8
        int cacheSize = maxMemory / 8;
        coverCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    return bitmap.getAllocationByteCount() / 1024;
                } else {
                    return bitmap.getByteCount() / 1024;
                }
            }
        };
    }

    private static class SingletonHolder {
        private static CoverLoader instance = new CoverLoader();
    }

    public static CoverLoader getInstance() {
        return SingletonHolder.instance;
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
    }

    public Bitmap loadThumbnail(MusicBean music) {
        return loadCover(music, Type.THUMBNAIL);
    }

    public Bitmap loadBlur(MusicBean music) {
        return loadCover(music, Type.BLUR);
    }

    public Bitmap loadRound(MusicBean music) {
        return loadCover(music, Type.ROUND);
    }

    private Bitmap loadCover(MusicBean music, Type type) {
        Bitmap bitmap;
        String key = getKey(music, type);
        //若找不到缓存则载入默认图，若默认图已缓存则返回，若还没添加进缓存则添加进缓存后返回
        if (TextUtils.isEmpty(key)) {
            bitmap = coverCache.get(KEY_NULL.concat(type.value));
            if (bitmap != null) {
                return bitmap;
            }else {
                bitmap = getDefaultCover(type);
                coverCache.put(KEY_NULL.concat(type.value), bitmap);
                return bitmap;
            }
        }
        //找到缓存，如果Bitmap不为null则返回，若为null则从媒体库获取并加入缓存，最后返回
        bitmap = coverCache.get(key);
        if (bitmap != null) {
            return bitmap;
        }else {
            bitmap = loadCoverByType(music, type);
            if (bitmap != null) {
                coverCache.put(key, bitmap);
                return bitmap;
            }
        }

        return loadCover(null, type);
    }

    private String getKey(MusicBean music, Type type) {
        if (music == null) {
            return null;
        }

        if (music.getType() == MusicBean.Type.LOCAL && music.getAlbumId() > 0) {
            return String.valueOf(music.getAlbumId()).concat(type.value);
        } else if (music.getType() == MusicBean.Type.ONLINE && !TextUtils.isEmpty(music.getCoverPath())) {
            return music.getCoverPath().concat(type.value);
        } else {
            return null;
        }
    }

    private Bitmap getDefaultCover(Type type) {
        switch (type) {
            case BLUR:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.play_page_default_bg);
            case ROUND:
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.play_page_default_cover);
                bitmap = ImageUtils.resizeImage(bitmap, SystemUtils.getScreenWidth() / 2, SystemUtils.getScreenWidth() / 2);
                return bitmap;
            default:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.default_cover);
        }
    }

    private Bitmap loadCoverByType(MusicBean music, Type type) {
        Bitmap bitmap;
        if (music.getType() == MusicBean.Type.LOCAL) {
            bitmap = loadCoverFromMediaStore(music.getAlbumId());
        } else {
            bitmap = loadCoverFromFile(music.getCoverPath());
        }

        switch (type) {
            case BLUR:
                return ImageUtils.blur(bitmap);
            case ROUND:
                bitmap = ImageUtils.resizeImage(bitmap, SystemUtils.getScreenWidth() / 2, SystemUtils.getScreenWidth() / 2);
                return ImageUtils.createCircleImage(bitmap);
            default:
                return bitmap;
        }
    }

    /**
     * 从媒体库加载封面<br>
     * 本地音乐
     */
    private Bitmap loadCoverFromMediaStore(long albumId) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MusicUtils.getMediaStoreAlbumCoverUri(albumId);
        InputStream is;
        try {
            is = resolver.openInputStream(uri);
        } catch (FileNotFoundException ignored) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeStream(is, null, options);
    }

    /**
     * 从下载的图片加载封面<br>
     * 网络音乐
     */
    private Bitmap loadCoverFromFile(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(path, options);
    }
}
