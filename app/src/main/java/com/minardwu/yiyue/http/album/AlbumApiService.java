package com.minardwu.yiyue.http.album;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author wumingyuan
 * @date 2018/7/31.
 */

public interface AlbumApiService {

    /**
     * 加载专辑数据
     * @param type
     * @param id
     * @return
     */
    @GET("/cloudmusic")
    Call<AlbumDataBean> loadAlbumData(@Query("type") String type,
                                      @Query("id") int id);

}
