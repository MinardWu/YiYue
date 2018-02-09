package com.minardwu.yiyue.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.minardwu.yiyue.utils.SystemUtils;


public class ButtonLayout extends ViewGroup {


    private final static String TAG = "ButtonLayout";

    private final static int VIEW_MARGIN_HORIZONTAL = 32;
    private final static int VIEW_MARGIN_VERTICAL = 24;

    public ButtonLayout(Context context) {
        super(context);
    }

    public ButtonLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ButtonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //用户精确定义时返回的宽高值
        int exactlyWidth = MeasureSpec.getSize(widthMeasureSpec);
        int exactlyHeight = MeasureSpec.getSize(heightMeasureSpec);
        //记录如果属性值为wrap_content时返回的宽高值，同自定义View一样，这也是重写onMeasure的目的
        int groupWidth;
        int groupHeight;
        //设置groupWidth
        if(widthMode==MeasureSpec.EXACTLY){
            groupWidth = exactlyWidth;
        }else {
            groupWidth = SystemUtils.getScreenWidth();
        }
        //设置groupHeight
        measureChildren(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);// 计算出所有的childView的宽和高,下面需要用到
        int count = getChildCount();
        int row = 1;
        int currentChildLeft = 0;
        int childTotalHeight = 0;
        for (int i = 0; i < count; i++) {
            final View child = this.getChildAt(i);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            currentChildLeft += childWidth + VIEW_MARGIN_HORIZONTAL;
            childTotalHeight = row * (childHeight + VIEW_MARGIN_VERTICAL);
            if (currentChildLeft > groupWidth) {
                row++;
                currentChildLeft = childWidth + VIEW_MARGIN_HORIZONTAL;//又从左边开始
                childTotalHeight = row * (childHeight + VIEW_MARGIN_VERTICAL);
            }
        }
        groupHeight = childTotalHeight;
        Log.e(TAG,groupWidth+"-----"+groupHeight+"----"+row);
        //如果是wrap_content设置为我们计算的值否则，直接设置为父容器计算的值
        setMeasuredDimension(
                (widthMode == MeasureSpec.EXACTLY) ? exactlyWidth : groupWidth,
                (heightMode == MeasureSpec.EXACTLY) ? exactlyHeight : groupHeight);
    }

    @Override
    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
        Log.e(TAG, " left = " + arg1 + " top = " + arg2+ " right = " + arg3 + " botom = " + arg4);
        arg2 = 0;
        int count = getChildCount();
        int row = 1;
        int layoutWidth = arg3-arg1;
        int currentChildLeft = arg1;
        int childTotalHeight = arg2;
        for (int i = 0; i < count; i++) {
            final View child = this.getChildAt(i);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            currentChildLeft += childWidth + VIEW_MARGIN_HORIZONTAL;
            childTotalHeight = row * (childHeight + VIEW_MARGIN_VERTICAL) + arg2;
            // if it can't drawing on a same line , skip to next line
            if (currentChildLeft > layoutWidth) {
                row++;
                currentChildLeft = childWidth + VIEW_MARGIN_HORIZONTAL + arg1;
                childTotalHeight = row * (childHeight + VIEW_MARGIN_VERTICAL) + arg2;
            }
            child.layout(currentChildLeft - childWidth, childTotalHeight - childHeight, currentChildLeft, childTotalHeight);
        }
        Log.e(TAG,"onLayout："+childTotalHeight+"----"+row);
    }
}
