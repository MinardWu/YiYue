package com.minardwu.yiyue.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.minardwu.yiyue.R;

public class InfoActivity extends SampleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_info;
    }

    @Override
    protected void setToolbarTitle(TextView textView) {
        super.setToolbarTitle(textView);
        textView.setText("关于");
    }

}
