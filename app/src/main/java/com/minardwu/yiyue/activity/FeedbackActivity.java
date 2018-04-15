package com.minardwu.yiyue.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.http.result.FailResult;
import com.minardwu.yiyue.http.HttpCallback;
import com.minardwu.yiyue.http.SendEmail;
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.utils.UIUtils;
import com.minardwu.yiyue.widget.dialog.LoadingDialog;
import com.minardwu.yiyue.widget.dialog.YesOrNoDialog;

import butterknife.BindView;

public class FeedbackActivity extends SampleActivity {

    @BindView(R.id.et_feedback)
    EditText et_feedback;
    @BindView(R.id.tv_thanks)
    TextView tv_thanks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv_thanks.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                if(et_feedback.getText().toString().trim().equals("")){
                    ToastUtils.showShortToast(UIUtils.getString(R.string.feedback_empty));
                }else{
                    final Dialog dialog = LoadingDialog.createLoadingDialog(FeedbackActivity.this);
                    dialog.show();
                    SendEmail.sendMail(et_feedback.getText().toString(), new HttpCallback() {
                        @Override
                        public void onSuccess(Object o) {
                            dialog.dismiss();
                            et_feedback.setText(null);
                            mid.setText("提交成功");
                            right.setVisibility(View.GONE);
                            et_feedback.setVisibility(View.GONE);
                            tv_thanks.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onFail(FailResult e) {
                            dialog.dismiss();
                            ToastUtils.showShortToast("发送失败");
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void setToolbarImage(ImageView imageView) {
        super.setToolbarImage(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        quit();
    }

    private void quit(){
        if (!et_feedback.getText().toString().trim().equals("")){
            YesOrNoDialog yesOrNoDialog = new YesOrNoDialog.Builder()
                    .context(getContext())
                    .title(UIUtils.getString(R.string.feedback_quit))
                    .titleTextColor(UIUtils.getColor(R.color.grey))
                    .yes(UIUtils.getString(R.string.sure), new YesOrNoDialog.PositiveClickListener() {
                        @Override
                        public void OnClick(YesOrNoDialog dialog, View view) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .no(UIUtils.getString(R.string.cancel), new YesOrNoDialog.NegativeClickListener() {
                        @Override
                        public void OnClick(YesOrNoDialog dialog, View view) {
                            dialog.dismiss();
                        }
                    })
                    .noTextColor(UIUtils.getColor(R.color.colorGreenLight))
                    .build();
            yesOrNoDialog.show();
        }else{
            finish();
        }
    }

}
