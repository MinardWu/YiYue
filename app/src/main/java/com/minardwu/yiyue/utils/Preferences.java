package com.minardwu.yiyue.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.minardwu.yiyue.R;


/**
 * SharedPreferences工具类
 */
public class Preferences {
    private static final String MUSIC_ID = "music_id";
    private static final String MUSIC_POSITION = "music_position";
    private static final String MUSIC_TITLE = "music_title";
    private static final String LOCAL_PLAY_MODE = "local_play_mode";
    private static final String ONLINE_PLAY_MODE = "online_play_mode";
    private static final String ONLINE_PLAY_POSITION = "online_play_position";
    private static final String SPLASH_URL = "splash_url";
    private static final String NIGHT_MODE = "night_mode";
    private static final String ALARM_CLOCK = "alarm_clock";
    private static final String ALARM_CLOCK_REPEAT = "alarm_clock_repeat";
    private static final String STOP_TIME = "stop_time";
    private static final String QUIT_TILL_SONG_END = "quit_till_song_end";
    private static final String FILTER_TIME_POSITION = "filter_time_position";
    private static final String FILTER_SIZE_POSITION = "filter_size_position";
    private static final String PLAY_ONLINE_LIST = "play_online_list";
    private static final String AUTO_REVERSAL = "auto_reversal";
    private static final String ALLOW_MOBILE_PLAY = "allow_mobile_play";
    private static final String ALLOW_MOBILE_DOWNLOAD = "allow_mobile_download";


    private static final String USE_MOCK_DATA = "use_mock_data";
    private static final String USE_MOCK_SEARCH_DATA = "use_mock_search_data";
    private static final String USE_MOCK_ARTIST_DATA = "use_mock_artist_data";
    private static final String USE_MOCK_ALBUM_DATA = "use_mock_album_data";

    private static final String ALARM_MUSIC_ID = "alarm_music_id";
    private static final String ALARM_HOUR = "alarm_hour";
    private static final String ALARM_MINUTE = "alarm_minute";

    private static final String LOCAL_MUSIC_ORDER_TYPE = "local_music_order_type";
    public static final int ORDER_BY_TIME = 0;
    public static final int ORDER_BY_TITLE = 1;
    public static final int ORDER_BY_SINGER = 2;
    public static final int ORDER_BY_ALBUM = 3;

    private static Context sContext;

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    private static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(sContext);
    }

    //对基本数据的操作调用自带接口
    private static boolean getBoolean(String key, boolean defValue) {
        return getPreferences().getBoolean(key, defValue);
    }

    private static void saveBoolean(String key, boolean value) {
        getPreferences().edit().putBoolean(key, value).apply();
    }

    private static int getInt(String key, int defValue) {
        return getPreferences().getInt(key, defValue);
    }

    private static void saveInt(String key, int value) {
        getPreferences().edit().putInt(key, value).apply();
    }

    private static long getLong(String key, long defValue) {
        return getPreferences().getLong(key, defValue);
    }

    private static void saveLong(String key, long value) {
        getPreferences().edit().putLong(key, value).apply();
    }

    private static String getString(String key, @Nullable String defValue) {
        return getPreferences().getString(key, defValue);
    }

    private static void saveString(String key, @Nullable String value) {
        getPreferences().edit().putString(key, value).apply();
    }


    //对于具体数据调用基本数据的操作
    public static int getLocalMusicOrderType() {
        return getInt(LOCAL_MUSIC_ORDER_TYPE, 0);
    }

    public static void setLocalMusicOrderType(int position) {
        saveInt(LOCAL_MUSIC_ORDER_TYPE, position);
    }

    public static long getAlarmMusicId() {
        return getLong(ALARM_MUSIC_ID, -1);
    }

    public static void saveAlarmMusicId(long id) {
        saveLong(ALARM_MUSIC_ID, id);
    }

    public static int getAlarmHour() {
        return getInt(ALARM_HOUR, 12);
    }

    public static void saveAlarmHour(int hour) {
        saveInt(ALARM_HOUR, hour);
    }

    public static int getAlarmMinute() {
        return getInt(ALARM_MINUTE, 0);
    }

    public static void saveAlarmMinute(int minute) {
        saveInt(ALARM_MINUTE, minute);
    }

    public static long getCurrentSongId() {
        return getLong(MUSIC_ID, -1);
    }

    public static void saveCurrentSongId(long id) {
        saveLong(MUSIC_ID, id);
    }

    public static int getCurrentSongPosition() {
        return getInt(MUSIC_POSITION, 0);
    }

    public static void saveOnlinePlayPosition(int position) {
        saveInt(ONLINE_PLAY_POSITION, position);
    }

    public static int getLocalPlayMode() {
        return getInt(LOCAL_PLAY_MODE, 0);
    }

    public static void saveLocalPlayMode(int mode) {
        saveInt(LOCAL_PLAY_MODE, mode);
    }

    public static int getOnlinePlayMode() {
        return getInt(ONLINE_PLAY_MODE, 0);
    }

    public static void saveOnlineMode(int mode) {
        saveInt(ONLINE_PLAY_MODE, mode);
    }

    public static int getStopTime() {
        return getInt(STOP_TIME, 0);
    }

    public static void saveStopTime(int time) {
        saveInt(STOP_TIME, time);
    }

    public static String getSplashUrl() {
        return getString(SPLASH_URL, "");
    }

    public static void saveSplashUrl(String url) {
        saveString(SPLASH_URL, url);
    }

    public static boolean enableUseMockData() {
        return getBoolean(USE_MOCK_DATA, false);
    }

    public static void saveUseMockData(boolean enable) {
        saveBoolean(USE_MOCK_DATA, enable);
    }

    public static boolean enableUseMockSearchData() {
        return getBoolean(USE_MOCK_SEARCH_DATA, false);
    }

    public static void saveUseMockSearchData(boolean enable) {
        saveBoolean(USE_MOCK_SEARCH_DATA, enable);
    }

    public static boolean enableUseMockArtistData() {
        return getBoolean(USE_MOCK_ARTIST_DATA, false);
    }

    public static void saveUseMockArtistData(boolean enable) {
        saveBoolean(USE_MOCK_ARTIST_DATA, enable);
    }

    public static boolean enableUseMockAlbumData() {
        return getBoolean(USE_MOCK_ALBUM_DATA, false);
    }

    public static void saveUseMockAlbumData(boolean enable) {
        saveBoolean(USE_MOCK_ALBUM_DATA, enable);
    }

    public static boolean enableAutoReversal() {
        return getBoolean(AUTO_REVERSAL, false);
    }

    public static void saveAutoReversal(boolean enable) {
        saveBoolean(AUTO_REVERSAL, enable);
    }

    public static boolean enableAllowMobilePlay() {
        return getBoolean(ALLOW_MOBILE_PLAY, false);
    }

    public static void saveAllowMobilePlay(boolean enable) {
        saveBoolean(ALLOW_MOBILE_PLAY, enable);
    }

    public static boolean enableAllowMobileDownload() {
        return getBoolean(ALLOW_MOBILE_DOWNLOAD, false);
    }

    public static void saveAllowMobileDownload(boolean enable) {
        saveBoolean(ALLOW_MOBILE_DOWNLOAD, enable);
    }

    public static boolean enableAlarmClockRepeat() {
        return getBoolean(ALARM_CLOCK_REPEAT, false);
    }

    public static void saveAlarmClockRepeat(boolean enable) {
        saveBoolean(ALARM_CLOCK_REPEAT, enable);
    }

    public static boolean enablePlayOnlineList() {
        return getBoolean(PLAY_ONLINE_LIST, false);
    }

    public static void savePlayOnlineList(boolean enable) {
        saveBoolean(PLAY_ONLINE_LIST, enable);
    }

    public static boolean enableAlarmClock() {
        return getBoolean(ALARM_CLOCK, false);
    }

    public static void saveAlarmClock(boolean enable) {
        saveBoolean(ALARM_CLOCK, enable);
    }

    public static boolean enableMobileNetworkDownload() {
        return getBoolean(sContext.getString(R.string.setting_key_mobile_network_download), false);
    }

    public static void setQuitTillSongEnd(boolean enable) {
        saveBoolean(QUIT_TILL_SONG_END, enable);
    }

    public static boolean getQuitTillSongEnd() {
        return getBoolean(QUIT_TILL_SONG_END, false);
    }

    public static boolean isNightMode() {
        return getBoolean(NIGHT_MODE, false);
    }

    public static void saveNightMode(boolean on) {
        saveBoolean(NIGHT_MODE, on);
    }

    public static long getFilterSize() {
        return getLong(sContext.getString(R.string.setting_key_filter_size), 0);
    }

    public static void saveFilterSize(long value) {
        saveLong(sContext.getString(R.string.setting_key_filter_size), value);
    }

    public static int getFilterSizePosition() {
        return getInt(FILTER_SIZE_POSITION, 0);
    }

    public static void saveFilterSizePosition(int value) {
        saveInt(FILTER_SIZE_POSITION, value);
    }

    public static long getFilterTime() {
        return getLong(sContext.getString(R.string.setting_key_filter_time), 0);
    }

    public static void saveFilterTime(long value) {
        saveLong(sContext.getString(R.string.setting_key_filter_time), value);
    }

    public static int getFilterTimePosition() {
        return getInt(FILTER_TIME_POSITION, 0);
    }

    public static void saveFilterTimePosition(int value) {
        saveInt(FILTER_TIME_POSITION, value);
    }

}
