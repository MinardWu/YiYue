package com.minardwu.yiyue.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.event.UpdateLocalMusicListEvent;
import com.minardwu.yiyue.utils.MusicUtils;
import com.minardwu.yiyue.utils.Preferences;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;

/**
 * Created by MinardWu on 2018/1/23.
 */

public class MoreDialog extends Dialog {


    public MoreDialog(@NonNull Context context) {
        super(context);
    }

    public MoreDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected MoreDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public interface OnMoreDialogItemClickListener{
        void onClick(View view);
    }

    private OnMoreDialogItemClickListener listener;

    public void setOnMoreDialogItemClickListener(OnMoreDialogItemClickListener listener){
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_more,null);
        setContentView(view);
        findViewById(R.id.tv_sort).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                ChooseOptionDialog sortDialog = new ChooseOptionDialog(getContext(),R.style.StopTimeDialog);
                sortDialog.setTitle("排序方式");
                sortDialog.setItem(R.array.sort);
                sortDialog.setShowImagePosition(Preferences.getLocalMusicOrderType()-1);
                sortDialog.setOnDialogItemClickListener(new ChooseOptionDialog.onDialogItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Preferences.setLocalMusicOrderType(position+1);
                        Collections.sort(AppCache.getLocalMusicList(),new MusicUtils.MusicComparator());
                        EventBus.getDefault().post(new UpdateLocalMusicListEvent(1));
                    }
                });
                sortDialog.show();
            }
        });


        findViewById(R.id.tv_scan_music).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                listener.onClick(view);
            }
        });
    }
}
