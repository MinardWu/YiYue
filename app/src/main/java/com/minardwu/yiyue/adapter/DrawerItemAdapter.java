package com.minardwu.yiyue.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.model.DrawerItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MinardWu
 * @date : 2017/12/29
 */

public class DrawerItemAdapter extends BaseAdapter {

    Context mContext;
    List<DrawerItemBean> drawerItemBeanList = new ArrayList<DrawerItemBean>();

    public DrawerItemAdapter(Context context){
        this.mContext = context;

        drawerItemBeanList.add(new DrawerItemBean(0, R.drawable.ic_love,R.string.drawer_item_collection,null,false));
        drawerItemBeanList.add(new DrawerItemBean(0, R.drawable.ic_foot,R.string.drawer_item_fm_history,null,false));
        drawerItemBeanList.add(new DrawerItemBean(2, 0,0,null,false));

        drawerItemBeanList.add(new DrawerItemBean(0, R.drawable.ic_balancer,R.string.drawer_item_sound_effect,null,false));
        drawerItemBeanList.add(new DrawerItemBean(0, R.drawable.ic_clock,R.string.drawer_item_stop_time,null,false));
        drawerItemBeanList.add(new DrawerItemBean(0, R.drawable.ic_filter,R.string.drawer_item_filter_time,null,false));
        drawerItemBeanList.add(new DrawerItemBean(0, R.drawable.ic_filter,R.string.drawer_item_filter_size,null,false));
        drawerItemBeanList.add(new DrawerItemBean(0, R.drawable.ic_alarm_clock,R.string.drawer_item_alarm_clock,null,false));
        drawerItemBeanList.add(new DrawerItemBean(2, 0,0,null,false));

        drawerItemBeanList.add(new DrawerItemBean(0, R.drawable.ic_feedback,R.string.drawer_item_feedback,"帮助我们",false));
        drawerItemBeanList.add(new DrawerItemBean(0, R.drawable.ic_info,R.string.drawer_item_info,null,false));
    }

//    void initData(int imgIdArray,int stringArray,int typeArray){
//        TypedArray typedArray = mContext.getResources().obtainTypedArray(imgIdArray);
//        int[] resIds = new int[typedArray.length()];
//        for (int i = 0; i < typedArray.length(); i++){
//            resIds[i] = typedArray.getResourceId(i, 0);
//        }
//        int[] types = mContext.getResources().getIntArray(typeArray);
//        String[] titles = mContext.getResources().getStringArray(stringArray);
//        for(int i=0;i<titles.length;i++){
//            drawerItemBeanList.add(new DrawerItemBean(types[i],resIds[i],titles[i],"",false));
//        }
//        setDrawerItemBeanState(0, Preferences.enablePlayWhenOnlyHaveWifi());
//        setDrawerItemBeanInfo(11,"帮助我们");
//    }

    public void setDrawerItemBeanInfo(int position,String info){
        drawerItemBeanList.get(position).setInfo(info);
        notifyDataSetChanged();
    }

    public void setDrawerItemBeanState(int position,boolean isCheck){
        drawerItemBeanList.get(position).setState(isCheck);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return drawerItemBeanList.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getCount() {
        return drawerItemBeanList.size();
    }

    @Override
    public Object getItem(int i) {
        return drawerItemBeanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        NormalViewHolder normalViewHolder;
        final SwitchViewHolder switchViewHolder;
        EmptyViewHolder emptyViewHolder;
        final DrawerItemBean drawerItemBean = drawerItemBeanList.get(position);
        switch (getItemViewType(position)){
            case 0:
                if(convertView==null){
                    normalViewHolder = new NormalViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.list_drawer_normal,null);
                    normalViewHolder.item = convertView.findViewById(R.id.rl_item);
                    normalViewHolder.imageView = convertView.findViewById(R.id.iv_list_drawer_normal);
                    normalViewHolder.title = convertView.findViewById(R.id.tv_list_drawer_normal_title);
                    normalViewHolder.info = convertView.findViewById(R.id.tv_list_drawer_normal_info);
                    convertView.setTag(normalViewHolder);
                }else {
                    normalViewHolder = (NormalViewHolder) convertView.getTag();
                }
                normalViewHolder.imageView.setImageResource(drawerItemBean.getImgId());
                normalViewHolder.title.setText(drawerItemBean.getTitle());
                normalViewHolder.info.setText(drawerItemBean.getInfo());
                normalViewHolder.item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        normalClickListener.onClick(position,drawerItemBean.getTitle());
                    }
                });
                break;
            case 1:
                if(convertView==null){
                    switchViewHolder = new SwitchViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.list_drawer_switch,null);
                    switchViewHolder.imageView = convertView.findViewById(R.id.iv_list_drawer_switch);
                    switchViewHolder.title = convertView.findViewById(R.id.tv_list_drawer_switch_title);
                    switchViewHolder.aSwitch = convertView.findViewById(R.id.sw_list_drawer_switch);
                    convertView.setTag(switchViewHolder);
                }else {
                    switchViewHolder = (SwitchViewHolder) convertView.getTag();
                }
                switchViewHolder.imageView.setImageResource(drawerItemBean.getImgId());
                switchViewHolder.title.setText(drawerItemBean.getTitle());
                switchViewHolder.aSwitch.setChecked(drawerItemBean.isState());
                switchViewHolder.aSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switchClickListener.onClick(position,switchViewHolder.aSwitch.isChecked());
                    }
                });
                break;
            case 2:
                if(convertView==null){
                    emptyViewHolder = new EmptyViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.list_drawer_empty,null);
                    convertView.setVisibility(View.INVISIBLE);
                    convertView.setTag(emptyViewHolder);
                }else {
                    emptyViewHolder = (EmptyViewHolder) convertView.getTag();
                }
                break;
            default:
                break;
        }
        return convertView;
    }

    class NormalViewHolder {
        RelativeLayout item;
        ImageView imageView;
        TextView title;
        TextView info;
    }

    class SwitchViewHolder {
        ImageView imageView;
        TextView title;
        Switch aSwitch;
    }

    class EmptyViewHolder {

    }

    public interface OnSwitchClickListener{
        void onClick(int position,boolean isCheck);
    }

    private OnSwitchClickListener switchClickListener;

    public void setSwitchClickListener(OnSwitchClickListener switchClickListener) {
        this.switchClickListener = switchClickListener;
    }

    public interface OnNormalClickListener{
        void onClick(int position,String title);
    }

    private OnNormalClickListener normalClickListener;

    public void setOnNormalClickListener(OnNormalClickListener onNormalClickListener) {
        this.normalClickListener = onNormalClickListener;
    }
}


