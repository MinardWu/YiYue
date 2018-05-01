package com.minardwu.yiyue.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.AlarmClockDateAdapter;
import com.minardwu.yiyue.event.CloseAlarmClockEvent;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.utils.AlarmManagerUtil;
import com.minardwu.yiyue.utils.MusicUtils;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

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
    @BindView(R.id.rv_date)
    RecyclerView rv_date;

    public static final int ALARM_ID = 101;
    private final int REQUEST_CODE = 1;
    private boolean isRepeatTurnOn = Preferences.enableAlarmClockRepeat();
    private boolean isAlarmClockTurnOn = Preferences.enableAlarmClock();
    private LinearLayoutManager layoutManager;
    private AlarmClockDateAdapter adapter;
    private int hour;
    private int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        rl_alarm_clock_time.setOnClickListener(this);
        rl_alarm_clock_song.setOnClickListener(this);
        sw_time.setOnClickListener(this);
        sw_repeat.setOnClickListener(this);
        sw_time.setChecked(isAlarmClockTurnOn);
        switchUI(isAlarmClockTurnOn);
        adapter = new AlarmClockDateAdapter(getContext());
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_date.setLayoutManager(layoutManager);
        rv_date.setAdapter(adapter);
        MusicBean alarmMusic = MusicUtils.getAlarmMusic(Preferences.getAlarmMusicId());
        if(alarmMusic != null){
            tv_alarm_clock_song.setText(alarmMusic.getTitle());
        }
        setTimeData(Preferences.getAlarmHour(),Preferences.getAlarmMinute());
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
                        hour = timePicker.getHour();
                        minute = timePicker.getMinute();
                        setTimeData(hour,minute);
                        Preferences.saveAlarmHour(hour);
                        Preferences.saveAlarmMinute(minute);
                        if (isAlarmClockTurnOn){
                            AlarmManagerUtil.setAlarm(getContext(),hour,minute,ALARM_ID);
                        }
                    }
                }, Preferences.getAlarmHour(), Preferences.getAlarmMinute(), true);
                tp.show();
                break;
            case R.id.rl_alarm_clock_song:
                Intent intent = new Intent(AlarmClockActivity.this,AlarmMusicChoseActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
                break;
            case R.id.sw_time:
                isAlarmClockTurnOn = sw_time.isChecked();
                Preferences.saveAlarmClock(isAlarmClockTurnOn);
                switchUI(isAlarmClockTurnOn);
                if(sw_time.isChecked()){
                    AlarmManagerUtil.setAlarm(getContext(),hour,minute,ALARM_ID);
                }else {
                    AlarmManagerUtil.cancelAlarm(getContext(),ALARM_ID);
                }
                break;
            case R.id.sw_repeat:
                isRepeatTurnOn = sw_repeat.isChecked();
                Preferences.saveAlarmClockRepeat(isRepeatTurnOn);
                showDate(isRepeatTurnOn);
                break;
        }
    }

    private void switchUI(boolean isAlarmClockTurnOn){
        tv_alarm_clock_ring.setTextColor(isAlarmClockTurnOn ? UIUtils.getColor(R.color.black) : UIUtils.getColor(R.color.grey));
        tv_alarm_clock_song.setTextColor(isAlarmClockTurnOn ? UIUtils.getColor(R.color.black) : UIUtils.getColor(R.color.grey));
        tv_alarm_clock_repeat.setTextColor(isAlarmClockTurnOn ? UIUtils.getColor(R.color.black) : UIUtils.getColor(R.color.grey));
        rl_alarm_clock_song.setClickable(isAlarmClockTurnOn);
        sw_repeat.setClickable(isAlarmClockTurnOn);
        sw_repeat.setChecked(isAlarmClockTurnOn ? isRepeatTurnOn : false);
        showDate(isAlarmClockTurnOn ? isRepeatTurnOn : false);
    }

    private void showDate(boolean isRepeatTurnOn){
        rv_date.setVisibility(isRepeatTurnOn ? View.VISIBLE : View.GONE);
    }

    private void setTimeData(int hour,int minute){
        String h = hour<10 ? "0"+hour : ""+hour;
        String m = minute<10 ? "0"+minute : ""+minute;
        tv_alarm_clock_time.setText(h+":"+m);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE){
            if (resultCode == 7){
                tv_alarm_clock_song.setText(data.getStringExtra("musicTitle"));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseAlarmClockEvent(CloseAlarmClockEvent event){
        sw_time.setChecked(Preferences.enableAlarmClock());
        switchUI(false);
    }

}
