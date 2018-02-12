package com.minardwu.yiyue.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.minardwu.yiyue.R;

public class MyArtistActivity extends SampleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_my_artist;
    }

    @Override
    protected void setToolbarTitle(TextView left, TextView mid, TextView right) {
        super.setToolbarTitle(left, mid, right);
        left.setVisibility(View.GONE);
        mid.setText("收藏");
        right.setVisibility(View.GONE);
    }
}
