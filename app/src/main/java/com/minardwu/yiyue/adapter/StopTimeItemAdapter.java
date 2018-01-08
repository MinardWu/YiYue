package com.minardwu.yiyue.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.minardwu.yiyue.R;

/**
 * Created by MinardWu on 2018/1/8.
 */

public class StopTimeItemAdapter extends BaseAdapter {

    Context mcontext;
    String[] items;
    int showImagePosition = 0;

    public StopTimeItemAdapter(Context context, int resId) {
        mcontext = context;
        items = mcontext.getResources().getStringArray(resId);
    }

    public void setShowImagePosition(int i){
        showImagePosition = i;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int i) {
        return items[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view==null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mcontext).inflate(R.layout.list_stoptime,null);
            viewHolder.textView = view.findViewById(R.id.tv_stop_time);
            viewHolder.imageView = view.findViewById(R.id.iv_stop_time);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.textView.setText(items[i]);
        if(i==showImagePosition){
            viewHolder.imageView.setVisibility(View.VISIBLE);
        }else {
            viewHolder.imageView.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    class ViewHolder{
        TextView textView;
        ImageView imageView;
    }
}
