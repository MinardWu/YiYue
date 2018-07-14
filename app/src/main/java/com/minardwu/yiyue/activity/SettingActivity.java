package com.minardwu.yiyue.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.fragment.NestedSettingFragment;
import com.minardwu.yiyue.fragment.SettingFragment;

/**
 * @author MinardWu
 * @date : 2018/5/22
 */

public class SettingActivity extends SampleActivity implements SettingFragment.NestedScreenClickListener {

    private static final String TAG_NESTED = "TAG_NESTED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content,new SettingFragment())
                .commit();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_setting;
    }

    @Override
    protected void setToolbarTitle(TextView left, TextView mid, TextView right) {
        super.setToolbarTitle(left, mid, right);
        left.setText("设置");
        mid.setVisibility(View.GONE);
        right.setVisibility(View.GONE);
    }

    @Override
    public void onNestedScreenClick(String key) {
        getFragmentManager().beginTransaction()
                .replace(R.id.content, NestedSettingFragment.newInstance(key),TAG_NESTED).addToBackStack(TAG_NESTED).commit();
    }
}
