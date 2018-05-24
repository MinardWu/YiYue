package com.minardwu.yiyue.http;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.YiYueApplication;
import com.minardwu.yiyue.constants.NetWorkType;
import com.minardwu.yiyue.http.result.FailResult;
import com.minardwu.yiyue.http.result.ResultCode;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.utils.FileUtils;
import com.minardwu.yiyue.utils.MusicUtils;
import com.minardwu.yiyue.utils.NetWorkUtils;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.utils.UIUtils;
import com.minardwu.yiyue.widget.dialog.YesOrNoDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * @author MinardWu
 * @date : 2018/5/22
 */

public class DownloadSong {

    private static final String GET_SONG_URL_BY_ID="https://api.imjad.cn/cloudmusic/?type=song&id=";

    public static void execute(final Context context, final MusicBean musicBean, final DownloadSongCallBack callBack){
        if(!Preferences.enableAllowMobileDownload()
                && NetWorkUtils.getNetWorkType()== NetWorkType.MOBILE){
            final YesOrNoDialog yesOrNoDialog = new YesOrNoDialog.Builder()
                    .context(context)
                    .title(R.string.is_open_mobile_download)
                    .yes(R.string.sure, new YesOrNoDialog.PositiveClickListener() {
                        @Override
                        public void OnClick(YesOrNoDialog dialog, View view) {
                            dialog.dismiss();
                            Preferences.saveAllowMobileDownload(true);
                            start(musicBean,callBack);
                        }
                    })
                    .yesTextColor(UIUtils.getColor(R.color.colorGreenLight))
                    .no(R.string.cancel, new YesOrNoDialog.NegativeClickListener() {
                        @Override
                        public void OnClick(YesOrNoDialog dialog, View view) {
                            dialog.dismiss();
                        }
                    })
                    .noTextColor(UIUtils.getColor(R.color.colorGreenLight))
                    .build();
            yesOrNoDialog.show();
        }else {
            start(musicBean,callBack);
        }
    }

    private static void start(final MusicBean musicBean, final DownloadSongCallBack callBack){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(GET_SONG_URL_BY_ID+musicBean.getId()).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFail(new FailResult(ResultCode.NETWORK_ERROR,e.toString()));
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject root = new JSONObject(response.body().string());
                    JSONObject data = root.getJSONArray("data").getJSONObject(0);
                    String url = data.getString("url");
                    if(TextUtils.isEmpty(url)){
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onFail(new FailResult(ResultCode.GET_SONG_NO_COPYRIGHT,"no copyright"));
                            }
                        });
                    }else {
                        //这里的成功是指获取到url，开始下载了
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onSuccess();
                            }
                        });
                        download(musicBean,url);
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onFail(new FailResult(ResultCode.GET_URL_ERROR,e.toString()));
                        }
                    });
                }
            }
        });
    }

    private static void download( MusicBean musicBean, String url){
        final DownloadManager downloadManager = (DownloadManager) YiYueApplication.getAppContext().getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalPublicDir("/YiYue/Music/",
                FileUtils.getFileName(musicBean)+".mp3");
        request.setTitle(FileUtils.getFileName(musicBean));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedNetworkTypes(Preferences.enableAllowMobileDownload()
                ? DownloadManager.Request.NETWORK_MOBILE
                : DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(Preferences.enableAllowMobileDownload());
        request.allowScanningByMediaScanner();
        final long downloadId = downloadManager.enqueue(request);
        final String filePath = FileUtils.getMusicDir()+FileUtils.getFileName(musicBean)+".mp3";
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkDownloadStatus(context,downloadManager,downloadId,filePath);
            }
        };
        YiYueApplication.getAppContext().registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private static void checkDownloadStatus(Context context,DownloadManager downloadManager,long id,String filepath) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    break;
                case DownloadManager.STATUS_PENDING:
                    break;
                case DownloadManager.STATUS_RUNNING:
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    ToastUtils.showShortToast(R.string.download_success);
                    MusicUtils.addToMediaStore(filepath);
                    break;
                case DownloadManager.STATUS_FAILED:
                    ToastUtils.showShortToast(R.string.download_fail);
                    break;
                default:
                    break;
            }
        }
    }



    public interface DownloadSongCallBack{
        /**
         * 获取音乐url成功
         */
        void onSuccess();

        /**
         * 下载失败
         * @param failResult
         */
        void onFail(FailResult failResult);
    }

}
