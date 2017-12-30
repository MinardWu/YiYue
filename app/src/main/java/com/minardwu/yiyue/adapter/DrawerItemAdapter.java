package com.minardwu.yiyue.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.model.DrawerItemBean;

import java.util.List;

/**
 * Created by MinardWu on 2017/12/29.
 */

public class DrawerItemAdapter extends ArrayAdapter<DrawerItemBean> {

    private int resourceId;

    public DrawerItemAdapter(@NonNull Context context, int resource, @NonNull List<DrawerItemBean> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DrawerItemBean drawerItemBean = getItem(position);
        ViewHolder viewHolder;
        if(convertView==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder.imageView = convertView.findViewById(R.id.iv_list_drawer);
            viewHolder.textView = convertView.findViewById(R.id.tv_list_drawer);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.setImageResource(drawerItemBean.getResId());
        viewHolder.textView.setText(drawerItemBean.getText());

        return convertView;
    }

    class ViewHolder{
        ImageView imageView;
        TextView textView;
    }
}


