package com.minardwu.yiyue.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.minardwu.yiyue.R;

import butterknife.BindView;
import butterknife.OnClick;

public abstract class SampleActivity extends BaseActivity {

    @BindView(R.id.iv_toolbar_back) ImageView iv_toolbar_back;
    @OnClick(R.id.iv_toolbar_back) void back(){
        finish();
    }

    ViewGroup content_view;
    TextView tv_toolbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        content_view = findViewById(R.id.content_view);

        setToolbarTitle(tv_toolbar_title);
        content_view.addView(View.inflate(this,getContentView(),null));
    }

    protected abstract int getContentView();

    protected void setToolbarTitle(TextView textView) {}
}
