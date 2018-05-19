package com.minardwu.yiyue.service;

import android.app.Service;

/**
 * Created by MinardWu on 2018/2/4.
 */

public abstract class PlayService extends Service{

    public void playOrPause(){}

    public void next(){}

    public void prev(){}

    public void stop(){}

    void pause(){}

    public void seekTo(int pos){}

    public long getCurrentPosition(){
        return 0;
    }

    public boolean isPlaying() {
        return false;
    }

    public boolean isPausing() {
        return false;
    }

    public boolean isPreparing() {
        return false;
    }

    public boolean isIdle() {
        return false;
    }

}
