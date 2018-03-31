package com.minardwu.yiyue.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmClockActivity extends SampleActivity implements View.OnClickListener {

    @BindView(R.id.rl_alarm_clock_time)
    RelativeLayout rl_alarm_clock_time;
    @BindView(R.id.rl_alarm_clock_song)
    RelativeLayout rl_alarm_clock_song;
    @BindView(R.id.tv_alarm_clock_time)
    TextView tv_alarm_clock_time;
    @BindView(R.id.tv_alarm_clock_ring)
    TextView tv_alarm_clock_ring;
    @BindView(R.id.tv_alarm_clock_song)
    TextView tv_alarm_clock_song;
    @BindView(R.id.tv_alarm_clock_repeat)
    TextView tv_alarm_clock_repeat;
    @BindView(R.id.sw_time)
    Switch sw_time;
    @BindView(R.id.sw_repeat)
    Switch sw_repeat;

    private boolean isReportTuroOn = false;
    private boolean isAlarmClockTurnOn = Preferences.enableAlarmClock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rl_alarm_clock_time.setOnClickListener(this);
        rl_alarm_clock_song.setOnClickListener(this);
        sw_time.setOnClickListener(this);
        sw_repeat.setOnClickListener(this);
        sw_time.setChecked(isAlarmClockTurnOn);
        switchUI(isAlarmClockTurnOn);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_alarm_clock;
    }

    @Override
    protected void setToolbarTitle(TextView left, TextView mid, TextView right) {
        super.setToolbarTitle(left, mid, right);
        left.setText("音乐闹钟");
        mid.setVisibility(View.GONE);
        right.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_alarm_clock_time:
                TimePickerDialog tp = new TimePickerDialog(AlarmClockActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int iminute) {
                        int hour = timePicker.getHour();
                        int minute = timePicker.getMinute();
                        String h = hour<10 ? "0"+hour : ""+hour;
                        String m = minute<10 ? "0"+minute : ""+minute;
                        tv_alarm_clock_time.setText(h+":"+m);
                    }
                }, 12, 0, true);//12：钟表初始小时数，0：钟表初始分钟数
                tp.show();
                break;
            case R.id.rl_alarm_clock_song:
                break;
            case R.id.sw_time:
                isAlarmClockTurnOn = sw_time.isChecked();
                Preferences.saveAlarmClock(isAlarmClockTurnOn);
                switchUI(isAlarmClockTurnOn);
                break;
            case R.id.sw_repeat:
                isReportTuroOn = sw_repeat.isChecked();
                break;
        }
    }

    private void switchUI(boolean isAlarmClockTurnOn){
        tv_alarm_clock_ring.setTextColor(isAlarmClockTurnOn ? UIUtils.getColor(this,R.color.black) : UIUtils.getColor(this,R.color.grey));
        tv_alarm_clock_song.setTextColor(isAlarmClockTurnOn ? UIUtils.getColor(this,R.color.black) : UIUtils.getColor(this,R.color.grey));
        tv_alarm_clock_repeat.setTextColor(isAlarmClockTurnOn ? UIUtils.getColor(this,R.color.black) : UIUtils.getColor(this,R.color.grey));
        rl_alarm_clock_song.setClickable(false);
        sw_repeat.setClickable(isAlarmClockTurnOn);
        sw_repeat.setChecked(isAlarmClockTurnOn ?  isReportTuroOn : false);
    }
}
