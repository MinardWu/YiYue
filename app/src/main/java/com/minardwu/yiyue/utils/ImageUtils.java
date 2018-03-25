package com.minardwu.yiyue.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;



import com.minardwu.yiyue.application.YiYueApplication;
import com.minardwu.yiyue.constants.RequestCode;
import com.minardwu.yiyue.http.HttpCallback;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 图像工具类
 */
public class ImageUtils {
    private static final int BLUR_RADIUS = 500;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blur(Context context, Bitmap bitmap, float bitmap_scale, int blur_radius) {
        //先对图片进行压缩然后再blur
        Bitmap inputBitmap = Bitmap.createScaledBitmap(bitmap,
                Math.round(bitmap.getWidth() * bitmap_scale),
                Math.round(bitmap.getHeight() * bitmap_scale),
                false);
        //创建空的Bitmap用于输出
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);
        //①、初始化Renderscript
        RenderScript rs = RenderScript.create(context);
        //②、Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        //③、native层分配内存空间
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        //④、设置blur的半径然后进行blur
        theIntrinsic.setRadius(blur_radius);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        //⑤、拷贝blur后的数据到java缓冲区中
        tmpOut.copyTo(outputBitmap);
        //⑥、销毁Renderscript
        rs.destroy();
        //bitmap.recycle();
        return outputBitmap;
    }

    @Nullable
    public static Bitmap blur(Bitmap source) {
        if (source == null) {
            return null;
        }

        try {
            return blur(source, BLUR_RADIUS);
        } catch (Exception e) {
            e.printStackTrace();
            return source;
        }
    }

    private static Bitmap blur(Bitmap source, int radius) {
        Bitmap bitmap = source.copy(source.getConfig(), true);

        if (radius < 1) {
            return null;
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rSum, gSum, bSum, x, y, i, p, yp, yi, yw;
        int vMin[] = new int[Math.max(w, h)];

        int divSum = (div + 1) >> 1;
        divSum *= divSum;
        int dv[] = new int[256 * divSum];
        for (i = 0; i < 256 * divSum; i++) {
            dv[i] = (i / divSum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackPointer;
        int stackStart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int rOutSum, gOutSum, bOutSum;
        int rInSum, gInSum, bInSum;

        for (y = 0; y < h; y++) {
            rInSum = gInSum = bInSum = rOutSum = gOutSum = bOutSum = rSum = gSum = bSum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rSum += sir[0] * rbs;
                gSum += sir[1] * rbs;
                bSum += sir[2] * rbs;
                if (i > 0) {
                    rInSum += sir[0];
                    gInSum += sir[1];
                    bInSum += sir[2];
                } else {
                    rOutSum += sir[0];
                    gOutSum += sir[1];
                    bOutSum += sir[2];
                }
            }
            stackPointer = radius;

            for (x = 0; x < w; x++) {
                r[yi] = dv[rSum];
                g[yi] = dv[gSum];
                b[yi] = dv[bSum];

                rSum -= rOutSum;
                gSum -= gOutSum;
                bSum -= bOutSum;

                stackStart = stackPointer - radius + div;
                sir = stack[stackStart % div];

                rOutSum -= sir[0];
                gOutSum -= sir[1];
                bOutSum -= sir[2];

                if (y == 0) {
                    vMin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vMin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rInSum += sir[0];
                gInSum += sir[1];
                bInSum += sir[2];

                rSum += rInSum;
                gSum += gInSum;
                bSum += bInSum;

                stackPointer = (stackPointer + 1) % div;
                sir = stack[(stackPointer) % div];

                rOutSum += sir[0];
                gOutSum += sir[1];
                bOutSum += sir[2];

                rInSum -= sir[0];
                gInSum -= sir[1];
                bInSum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rInSum = gInSum = bInSum = rOutSum = gOutSum = bOutSum = rSum = gSum = bSum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rSum += r[yi] * rbs;
                gSum += g[yi] * rbs;
                bSum += b[yi] * rbs;

                if (i > 0) {
                    rInSum += sir[0];
                    gInSum += sir[1];
                    bInSum += sir[2];
                } else {
                    rOutSum += sir[0];
                    gOutSum += sir[1];
                    bOutSum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackPointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rSum] << 16) | (dv[gSum] << 8) | dv[bSum];

                rSum -= rOutSum;
                gSum -= gOutSum;
                bSum -= bOutSum;

                stackStart = stackPointer - radius + div;
                sir = stack[stackStart % div];

                rOutSum -= sir[0];
                gOutSum -= sir[1];
                bOutSum -= sir[2];

                if (x == 0) {
                    vMin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vMin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rInSum += sir[0];
                gInSum += sir[1];
                bInSum += sir[2];

                rSum += rInSum;
                gSum += gInSum;
                bSum += bInSum;

                stackPointer = (stackPointer + 1) % div;
                sir = stack[stackPointer];

                rOutSum += sir[0];
                gOutSum += sir[1];
                bOutSum += sir[2];

                rInSum -= sir[0];
                gInSum -= sir[1];
                bInSum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return bitmap;
    }

    /**
     * 将图片放大或缩小到指定尺寸
     */
    public static Bitmap resizeImage(Bitmap source, int dstWidth, int dstHeight) {
        if (source == null) {
            return null;
        }
        return Bitmap.createScaledBitmap(source, dstWidth, dstHeight, true);
    }

    /**
     * 将图片剪裁为圆形
     */
    public static Bitmap createCircleImage(Bitmap source) {
        if (source == null) {
            return null;
        }

        int length = Math.min(source.getWidth(), source.getHeight());
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(target.getWidth() / 2, target.getHeight() / 2, length / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        target = Bitmap.createBitmap(target,target.getWidth()/2-length/2,target.getHeight()/2-length/2,length,length);
        return target;
    }

    public static void getBitmapByUrl(final String url, final HttpCallback<Bitmap> callback) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                URL myFileUrl = null;
                Bitmap bitmap = null;
                try {
                    myFileUrl = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                    final Bitmap finalBitmap = bitmap;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(finalBitmap);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onFail(e.toString());
                }
            }
        }.start();
        return;
    }

    public static void getBlurBitmapByUrl(final String url, final HttpCallback<Bitmap> callback) {
        getBitmapByUrl(url, new HttpCallback<Bitmap>() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                final Bitmap blur = ImageUtils.blur(YiYueApplication.getAppContext(),bitmap,0.01f,25);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(blur);
                    }
                });
            }

            @Override
            public void onFail(String e) {
                callback.onFail(e);
            }
        });
    }

    public static void startAlbum(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, RequestCode.REQUEST_ALBUM);
    }

    public static void startCorp(Activity activity, Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", CoverLoader.THUMBNAIL_MAX_LENGTH);
        intent.putExtra("outputY", CoverLoader.THUMBNAIL_MAX_LENGTH);
        intent.putExtra("return-data", false);
        File outFile = new File(FileUtils.getCorpImagePath(activity));
        Uri outUri = Uri.fromFile(outFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // 取消人脸识别
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, RequestCode.REQUEST_CORP);
    }
}
