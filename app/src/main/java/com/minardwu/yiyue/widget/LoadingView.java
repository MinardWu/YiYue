package com.minardwu.yiyue.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.os.Handler;

import com.minardwu.yiyue.R;


/**
 * Created by MinardWu on 2018/2/17.
 */

public class LoadingView extends View {

    private static final long TIME_UPDATE = 30L;
    private static final float ROTATION_INCREASE = 8f;
    private static final float TEXT_MARGIN_LEFT = 16;

    private float rotation = 0.0f;
    private Matrix matrix = new Matrix();
    private Handler handler = new Handler();
    private String loadindText;
    private int loadindTextColor;
    private float loadindTextSize;
    private float loadindiconWidth;
    private boolean isRunning = false;

    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.LoadingView);
        loadindText = typedArray.getString(R.styleable.LoadingView_loading_text);
        if(loadindText==null){
            loadindText = "正在加载中...";
        }
        loadindTextColor = typedArray.getColor(R.styleable.LoadingView_loading_text_color,getResources().getColor(R.color.colorGreenLight));
        loadindTextSize = typedArray.getDimension(R.styleable.LoadingView_loading_text_size,25);
        loadindiconWidth = typedArray.getDimension(R.styleable.LoadingView_ic_loading_width,25);
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
            Paint textPaint = new Paint();
            textPaint.setColor(loadindTextColor);
            textPaint.setTextSize(loadindTextSize);
            Rect minRect = new Rect();
            textPaint.getTextBounds(loadindText,0,loadindText.length(),minRect);
            width = (int) (loadindiconWidth+TEXT_MARGIN_LEFT+(minRect.right-minRect.left))+2;//这里加2是为了你不float转为int的精度缺失
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (int) loadindiconWidth;
        }
        setMeasuredDimension(width, height);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画图标
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_loading_album);
        bitmap = Bitmap.createScaledBitmap(bitmap,(int)loadindiconWidth,(int)loadindiconWidth,true);
        matrix.setRotate(rotation,bitmap.getWidth()/2,bitmap.getHeight()/2);
        matrix.preTranslate(0,0);
        canvas.drawBitmap(bitmap,matrix,null);
        if(!isRunning){
            handler.post(rotationRunnable);
            isRunning = true;
        }
        //写字
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(loadindTextColor);
        textPaint.setTextSize(loadindTextSize);
        Paint.FontMetricsInt fm = textPaint.getFontMetricsInt();
        int top = fm.top;
        int bottom = fm.bottom;
        int height = bottom - top;
        canvas.drawText(loadindText,bitmap.getWidth()+TEXT_MARGIN_LEFT,bitmap.getHeight()/2+height/2-fm.bottom,textPaint);
    }

    private Runnable rotationRunnable = new Runnable() {
        @Override
        public void run() {
            rotation += ROTATION_INCREASE;
            if(rotation >= 360){
                rotation = 0.0f;
            }
            invalidate();
            handler.postDelayed(this,TIME_UPDATE);
        }

    };

    public void start(){
        handler.post(rotationRunnable);
    }
}
