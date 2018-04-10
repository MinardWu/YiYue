package com.minardwu.yiyue.http.result;

/**
 * Created by wumingyuan on 2018/4/10.
 */

public class ResultCode {

    public static final int NETWORK_ERROR = 7;

    //歌曲返回
    public static final int GET_URL_ERROR = 101;
    public static final int GET_DETAIL_ERROR = 102;
    public static final int GET_LRC_ERROR = 103;

    //歌手返回
    public static final int GET_ARTIST_INFO_ERROR = 201;
    public static final int GET_ARTIST_NO_FOUND = 202;
    public static final int GET_ARTIST_PIC_ERROR = 203;

    //歌手返回
    public static final int GET_ALBUM_INFO_ERROR = 301;

    //搜索返回
    public static final int SEARCH_ERROR = 401;

    //反馈返回
    public static final int SEND_EMAIL_ERROR = 501;

    /**
     * 通过url获取图片返回
     *  {@link com.minardwu.yiyue.utils.ImageUtils#getBitmapByUrl(java.lang.String, com.minardwu.yiyue.http.HttpCallback)}
     */
    public static final int GET_BITMAP_BY_URL_ERROR = 601;
}
