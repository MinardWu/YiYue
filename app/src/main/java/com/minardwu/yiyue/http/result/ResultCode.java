package com.minardwu.yiyue.http.result;

/**
 * Created by wumingyuan on 2018/4/10.
 */

public class ResultCode {

    public static final int NETWORK_ERROR = 7;
    public static final int PARSE_JSON_ERROR = 8;

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

    //歌词王获取歌词返回
    public static final int GCW_GET_LRC_ERROR = 501;
    public static final int GCW_LRC_NO_FOUND = 502;
    public static final int GCW_CREATE_LRC_FILE_ERROR = 503;

    /**
     * 通过url获取图片返回
     *  {@link com.minardwu.yiyue.utils.ImageUtils#getBitmapByUrl(java.lang.String, com.minardwu.yiyue.http.HttpCallback)}
     */
    public static final int GET_BITMAP_BY_URL_ERROR = 601;
}
