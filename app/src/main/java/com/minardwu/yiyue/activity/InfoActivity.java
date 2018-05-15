package com.minardwu.yiyue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.utils.SystemUtils;
import com.minardwu.yiyue.utils.UIUtils;

public class InfoActivity extends SampleActivity {

    private TextView tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv_version = findViewById(R.id.tv_version);
        tv_version.setText(SystemUtils.getLocalVersionName(this));
        tv_version.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startActivity(new Intent(InfoActivity.this,MockControllerActivity.class));
                return false;
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_info;
    }

    @Override
    protected void setToolbar(Toolbar toolbar) {
        super.setToolbar(toolbar);
        toolbar.setBackgroundColor(UIUtils.getColor(R.color.white));
    }

    @Override
    protected void setToolbarImage(ImageView imageView) {
        super.setToolbarImage(imageView);
        imageView.setImageResource(R.drawable.ic_back);
    }

    @Override
    protected void setToolbarTitle(TextView left, TextView mid, TextView right) {
        super.setToolbarTitle(left, mid, right);
        left.setVisibility(View.GONE);
        mid.setVisibility(View.GONE);
        right.setVisibility(View.GONE);
    }
}
