package com.minardwu.yiyue.http.album;

import android.util.Log;

import com.minardwu.yiyue.http.HttpCallback;
import com.minardwu.yiyue.http.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author wumingyuan
 * @date 2018/7/31.
 */

public class AlbumDataSourceRep implements AlbumDataSource{

    private AlbumApiService albumApiService;

    public AlbumDataSourceRep() {
        albumApiService = ServiceGenerator.getInstance().createService(AlbumApiService.class);
    }

    @Override
    public Call<AlbumDataBean> loadAlbumData(int albumId,HttpCallback<AlbumDataBean> callback){
        Call<AlbumDataBean> call = albumApiService.loadAlbumData("album",albumId);
        call.enqueue(new Callback<AlbumDataBean>() {
            @Override
            public void onResponse(Call<AlbumDataBean> call, Response<AlbumDataBean> response) {
                callback.onSuccess(response.body());
                Log.e("AlbumDataSourceRep",response.body().getAlbum().getArtist().getName());
            }

            @Override
            public void onFailure(Call<AlbumDataBean> call, Throwable t) {
                Log.e("AlbumDataSourceRep","error:"+t.getCause());
                try {
                    throw t;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            }
        });

        return call;
    }

}
