package com.minardwu.yiyue.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.YiYueApplication;

/**
 * Created by MinardWu on 2018/3/25.
 */

public class UIUtils {

    /**
     * convert px to its equivalent dp
     *
     * 将px转换为与之相等的dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale =  context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * convert dp to its equivalent px
     *
     * 将dp转换为与之相等的px
     */
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    /**
     * convert px to its equivalent sp
     *
     * 将px转换为sp
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    /**
     * convert sp to its equivalent px
     *
     * 将sp转换为px
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int getToolbarHeight(Context context) {
        int actionBarHeight=100;
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, context.getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    public static int getColor(int id) {
        return YiYueApplication.getAppContext().getResources().getColor(id);
    }

    public static String getString(int id) {
        return YiYueApplication.getAppContext().getResources().getString(id);
    }

    public static Drawable getDrawable(int id) {
        return YiYueApplication.getAppContext().getResources().getDrawable(id);
    }
}
