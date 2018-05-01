package com.minardwu.yiyue.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.utils.SystemUtils;
import com.minardwu.yiyue.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wumingyuan on 2018/4/13.
 */

public class AlarmClockDateAdapter extends RecyclerView.Adapter {

    private Context context;
    private String[] dates = new String[]{"一","二","三","四","五","六","七"};
    private int[] count = new int[]{2,3,4,5,6,7,1};
    private List<Integer> checkDates = new ArrayList<Integer>();

    public AlarmClockDateAdapter(Context context){
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_alarm_clock_date,null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(SystemUtils.getScreenWidth()/10,SystemUtils.getScreenWidth()/10);
        layoutParams.rightMargin = SystemUtils.getScreenWidth()*3/10/10;
        viewHolder.button.setLayoutParams(layoutParams);
        viewHolder.button.setText(dates[position]);
        checkDates = MyDatabaseHelper.init(context).queryAlarmClockDate();
        if(checkDates!=null){
            viewHolder.button.setSelected(checkDates.contains(position+1) ? true : false);
            viewHolder.button.setTextColor(checkDates.contains(position+1)
                    ? UIUtils.getColor(R.color.colorGreenLight)
                    : UIUtils.getColor(R.color.grey));
        }
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.button.setSelected(viewHolder.button.isSelected() ? false : true);
                viewHolder.button.setTextColor(viewHolder.button.isSelected()
                        ? UIUtils.getColor(R.color.colorGreenLight)
                        : UIUtils.getColor(R.color.grey));
                if (viewHolder.button.isSelected()){
                    MyDatabaseHelper.init(context).addAlarmClockDate(count[position]);
                }else {
                    MyDatabaseHelper.init(context).deleteAlarmClockDate(count[position]);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dates.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.btn_date)
        Button button;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
