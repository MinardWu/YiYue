package com.minardwu.yiyue.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.widget.ButtonLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    @BindView(R.id.button_layout)
    ButtonLayout button_layout;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.tv_clear_history)
    TextView tv_clear_history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        String[] hg = new String[]{"00000000000",
                "dsf","dfsa","11111111","gtdhds",
                "dsf","dfsa","gtdhds","gtdhds",
                "dsf","dfsa","gtdhds","22222222222"};
        et_search.setOnEditorActionListener(this);
        for (String history:hg){
            Button button = new Button(this);
            button.setText(history);
            button.setMinHeight(0);
            button.setMinWidth(0);
            button.setMinimumHeight(0);//View中的方法 改变View中的mMinHeight
            button.setMinimumWidth(0);//View中的方法  改变View中的mMinWidth
            button.setBackgroundResource(R.drawable.btn_search_history);
            button.setOnClickListener(this);
            button_layout.addView(button);
        }
    }

    @Override
    public void onClick(View view) {
        Button btn = (Button) view;
        ToastUtils.show(btn.getText().toString());
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        ToastUtils.show("搜索");
        return false;
    }
}
