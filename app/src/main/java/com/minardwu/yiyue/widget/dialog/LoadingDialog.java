package com.minardwu.yiyue.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.minardwu.yiyue.R;


/**
 * Created by MinardWu on 2016/3/8.
 */
public class LoadingDialog {

    public static Dialog createLoadingDialog(Context context) {

        View view =  LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll_progressDialog);
        ImageView iv = (ImageView) view.findViewById(R.id.iv_progressDialog);

        //设置控件信息
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.loading_dialog_rotate);
        iv.startAnimation(animation);

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        loadingDialog.setCancelable(false);//不可以用“返回键”取消
        loadingDialog.setContentView(ll, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));// 设置布局

        return loadingDialog;

    }

}
