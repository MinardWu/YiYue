package com.minardwu.yiyue.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.minardwu.yiyue.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author MinardWu
 * @date : 2018/3/4
 */

public class ImageAndTextAdapter extends BaseAdapter {

    private Context mContext;
    private int[] images;
    private String[] strings;
    public ImageAndTextAdapter(Context context,int imgId, int textId){
        mContext = context;
        strings = context.getResources().getStringArray(textId);
        TypedArray typedArray = mContext.getResources().obtainTypedArray(imgId);
        images = new int[typedArray.length()];
        for (int i = 0; i < typedArray.length(); i++)
            images[i] = typedArray.getResourceId(i, 0);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_img_and_text,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.list_img_and_text_iv.setImageResource(images[i]);
        viewHolder.list_img_and_text_tv.setText(strings[i]);
        viewHolder.divider.setVisibility(i==getCount()-1 ? View.INVISIBLE:View.VISIBLE);
        return view;
    }

    class ViewHolder{
        @BindView(R.id.list_img_and_text_iv)
        ImageView list_img_and_text_iv;
        @BindView(R.id.list_img_and_text_tv)
        TextView list_img_and_text_tv;
        @BindView(R.id.v_divider)
        View divider;

        ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
}
