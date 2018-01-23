package com.minardwu.yiyue.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.StopTimeItemAdapter;

/**
 * Created by MinardWu on 2018/1/23.
 */

public class FilterDialog extends Dialog {

    private TextView tv_dialog_title;
    private ListView listView;
    private StopTimeItemAdapter stopTimeItemAdapter;
    private String title;
    private int resId;
    private int showImagePosition;

    public FilterDialog(@NonNull Context context) {
        super(context);
    }

    public FilterDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected FilterDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public interface onDialogItemClickListener{
        void onClick(View view,int position);
    }

    private onDialogItemClickListener listener;

    public void setOnDialogItemClickListener(onDialogItemClickListener clickListener){
        this.listener = clickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_filter,null);
        setContentView(view);
        tv_dialog_title = findViewById(R.id.tv_dialog_title);
        tv_dialog_title.setText(title);
        listView = findViewById(R.id.lv_dialog);
        stopTimeItemAdapter = new StopTimeItemAdapter(getContext(),resId);
        stopTimeItemAdapter.setShowImagePosition(showImagePosition);
        listView.setAdapter(stopTimeItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                stopTimeItemAdapter.setShowImagePosition(i);
                listener.onClick(view,i);
            }
        });
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setItem(int resId){
        this.resId = resId;
    }

    public void setShowImagePosition(int i){
        this.showImagePosition = i;
    }

}
