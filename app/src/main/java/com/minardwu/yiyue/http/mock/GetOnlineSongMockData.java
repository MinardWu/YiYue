package com.minardwu.yiyue.http.mock;

import android.util.Log;

import com.minardwu.yiyue.http.GetOnlineSongListener;
import com.minardwu.yiyue.model.MusicBean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MinardWu on 2018/5/15.
 */

public abstract class GetOnlineSongMockData implements GetOnlineSongListener {

    private static final String TAG="GetOnlineSongMock";
    private static int index = 0;

    /**
     * @param id 歌曲id
     * @param isClick 用户是点击具体歌曲还是fm过来的
     */
    @Override
    public void execute(long id, boolean isClick) {
        getSongUrlById(index,isClick);
        index++;
        if(index==4){
            index = 0;
        }
    }

    /**
     * 获取歌曲url
     * @param id 歌曲id
     */
    public void getSongUrlById(final long id, final boolean isClick){
//        JSONObject root = null;
//        try {
//            root = new JSONObject(MockData.songUrlList.get((int)id));
//            JSONObject data = root.getJSONArray("data").getJSONObject(0);
//            String url = data.getString("url");
//            getSongDetailById(id,url);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        getSongDetailById(id,MockData.songUrlList.get((int)id));
    }

    /**
     * 获取歌曲名字，歌手，封面信息
     * @param id 歌曲id
     * @param musicUrl 这个主要是为了保存上一个函数中或得到的url，在本函数将其保存到musicbean中
     */
    public void getSongDetailById(final long id, final String musicUrl){
        final MusicBean musicBean = new MusicBean();
        try {
            String i = MockData.songDetailList.get((int)id);
            JSONObject root = new JSONObject(i);
            JSONObject detail = root.getJSONArray("songs").getJSONObject(0);
            JSONObject artistInfo = detail.getJSONArray("ar").getJSONObject(0);
            JSONObject albumInfo = detail.getJSONObject("al");
            String title = detail.getString("name");
            String artist = artistInfo.getString("name");
            String artistId = artistInfo.getString("id");
            String picUrl = albumInfo.getString("picUrl");
            String albumName = albumInfo.getString("name");
            String albumId = albumInfo.getString("id");
            musicBean.setId(id);
            musicBean.setTitle(title);
            musicBean.setArtistName(artist);
            musicBean.setArtistId(artistId);
            musicBean.setCoverPath(picUrl);
            musicBean.setAlbum(albumName);
            musicBean.setAlbumId(albumId);
            musicBean.setPath(musicUrl);
            musicBean.setType(MusicBean.Type.ONLINE);
            getSongLrcById(id,musicBean);
            Log.e(TAG,title);
            Log.e(TAG,artist);
            Log.e(TAG,picUrl);
            Log.e(TAG,musicUrl);
        } catch (final JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取歌词
     * @param id 歌曲id
     * @param musicBean 歌曲实体，这个是为了获得上一个函数中或得到的music
     */
    public void getSongLrcById(final long id,final MusicBean musicBean){
        try {
            JSONObject root = new JSONObject(MockData.songLrcList.get((int)id));
            JSONObject lrc = root.getJSONObject("lrc");
            String lyric = lrc.getString("lyric");
            musicBean.setLrc(lyric);
            onSuccess(musicBean);
        } catch (final JSONException e) {
            e.printStackTrace();
            onSuccess(musicBean);
            Log.e(TAG,"getSongLrcById-onResponse："+e.toString());

        }
    }
}
