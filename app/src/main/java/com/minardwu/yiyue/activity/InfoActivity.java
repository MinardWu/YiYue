package com.minardwu.yiyue.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.utils.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InfoActivity extends SampleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView tv_version = findViewById(R.id.tv_version);
        tv_version.setText(SystemUtils.getLocalVersionName(this));
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_info;
    }

    @Override
    protected void setToolbarTitle(TextView left, TextView mid, TextView right) {
        super.setToolbarTitle(left, mid, right);
        left.setVisibility(View.GONE);
        mid.setVisibility(View.GONE);
        right.setVisibility(View.GONE);
    }

}
