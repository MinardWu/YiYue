package com.minardwu.yiyue.http.album;

import com.minardwu.yiyue.http.HttpCallback;

import retrofit2.Call;

/**
 * @author wumingyuan
 */

public interface AlbumDataSource {

    /**
     * 加载专辑数据
     * @param id
     * @param callback
     * @return
     */
    Call<AlbumDataBean> loadAlbumData(int id, HttpCallback<AlbumDataBean> callback);

}
