package com.minardwu.yiyue.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.http.result.FailResult;
import com.minardwu.yiyue.http.HttpCallback;
import com.minardwu.yiyue.http.SendEmail;
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.widget.LoadingDialog;

public class FeedbackActivity extends SampleActivity {

    EditText et_feedback;
    TextView tv_thanks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        et_feedback = findViewById(R.id.et_feedback);
        tv_thanks = findViewById(R.id.tv_thanks);
        tv_thanks.setVisibility(View.GONE);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_feed_back;
    }

    @Override
    protected void setToolbarTitle(TextView left, final TextView mid, final TextView right) {
        super.setToolbarTitle(left, mid, right);
        left.setVisibility(View.GONE);
        mid.setText("反馈");
        right.setText("发送");
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = LoadingDialog.createLoadingDialog(FeedbackActivity.this);
                dialog.show();
                SendEmail.sendMail(et_feedback.getText().toString(), new HttpCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                mid.setText("提交成功");
                                right.setVisibility(View.GONE);
                                et_feedback.setVisibility(View.GONE);
                                tv_thanks.setVisibility(View.VISIBLE);
                            }
                        });

                    }

                    @Override
                    public void onFail(FailResult e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                ToastUtils.show("发送失败");
                            }
                        });
                    }
                });
            }
        });
    }
}
