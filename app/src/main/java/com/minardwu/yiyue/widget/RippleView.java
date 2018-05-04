package com.minardwu.yiyue.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.utils.UIUtils;

import java.util.logging.Handler;

/**
 * Created by wumingyuan on 2018/5/4.
 * 闹钟界面水波纹
 */

public class RippleView extends View{

    private float desc = 0;
    private float d = 2;
    private boolean isRunning;
    private android.os.Handler handler = new android.os.Handler();
    private String text;
    private float radius;
    private float i = 2.5f;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            d -= 0.01;
            desc += d;
            if(desc > 50){
                desc = 0;
                d = 2;
            }
            invalidate();
            handler.postDelayed(runnable,50);
        }
    };

    public RippleView(Context context) {
        this(context,null);
    }

    public RippleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RippleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.RippleView);
        radius = typedArray.getFloat(R.styleable.RippleView_radios,150);
        text = typedArray.getString(R.styleable.RippleView_text);
        if(text==null){
            text = "Stop";
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getMode(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = (int) (radius*i*2);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (int) (radius*i*2);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!isRunning){
            start();
            isRunning = true;
        }
        Paint paint = new Paint();

        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);

        paint.setAlpha(255);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(UIUtils.getColor(R.color.white));
        canvas.drawCircle(radius*i,radius*i,radius,paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(UIUtils.getColor(R.color.white_30));
        canvas.drawCircle(radius*i,radius*i,radius,paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(UIUtils.getColor(R.color.grey));
        paint.setAlpha(255 - (int) (255*(desc/50)));
        canvas.drawCircle(radius*i,radius*i,radius*1.1f+desc,paint);
        canvas.drawCircle(radius*i,radius*i,radius*1.6f+desc,paint);
        canvas.drawCircle(radius*i,radius*i,radius*2.1f+desc,paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(255);
        paint.setColor(UIUtils.getColor(R.color.white));
        paint.setStrokeWidth(1);
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(text,radius*i,radius*i+15,paint);
    }

    private void start(){
        handler.post(runnable);
    }
}
