package com.minardwu.yiyue.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.minardwu.yiyue.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class SampleActivity extends BaseActivity {

    @BindView(R.id.iv_toolbar_back) ImageView iv_toolbar_back;
    @BindView(R.id.tv_toolbar_mid_title) TextView tv_toolbar_mid_title;
    @BindView(R.id.tv_toolbar_left_title) TextView tv_toolbar_left_title;
    @BindView(R.id.tv_toolbar_right_title) TextView tv_toolbar_right_title;

    @OnClick(R.id.iv_toolbar_back) void back(){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        ViewGroup conten_view = findViewById(R.id.content_view);
        View view = View.inflate(this,getContentView(),null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        conten_view.addView(view,params);
        ButterKnife.bind(this);
        setToolbarImage(iv_toolbar_back);
        setToolbarTitle(tv_toolbar_left_title,tv_toolbar_mid_title,tv_toolbar_right_title);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected abstract int getContentView();

    protected void setToolbarTitle(TextView left,TextView mid,TextView right) {}

    protected void setToolbarImage(ImageView imageView) {}
}
