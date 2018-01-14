package com.minardwu.yiyue.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.utils.SystemUtils;

/**
 * Created by MinardWu on 2018/1/9.
 */

public class OnlineMusicCoverView extends View{

    private int progress = 0;
    private int bitmapWidth = (int) (SystemUtils.getScreenWidth()*0.6);
    private float progressCircleWidth;
    private int progressCircleColor;
    private int progressCircleBackgroundColor;
    private Bitmap bitmap_cover;


    private Handler handler = new Handler();

    public OnlineMusicCoverView(Context context) {
        this(context, null);;
    }

    public OnlineMusicCoverView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public OnlineMusicCoverView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.OnlineCoverView);
        progressCircleWidth = typedArray.getDimension(R.styleable.OnlineCoverView_progressCircleWidth,25);
        progressCircleColor = typedArray.getColor(R.styleable.OnlineCoverView_progressCircleColor,getResources().getColor(R.color.colorGreenDeep));
        progressCircleBackgroundColor = typedArray.getColor(R.styleable.OnlineCoverView_progressCircleBackgroudColor,getResources().getColor(R.color.colorGreen));
        typedArray.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int lengthForExactly = Math.min(widthSize,heightSize);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = lengthForExactly;
            bitmapWidth = (int) (width - progressCircleWidth *2);
        } else {
            width = (int) (progressCircleWidth *2+bitmapWidth);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = lengthForExactly;
        } else {
            height = (int) (progressCircleWidth *2+bitmapWidth);
        }
        setMeasuredDimension(width, height);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();

        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(progressCircleBackgroundColor);
        paint.setStrokeWidth(progressCircleWidth);
        canvas.drawCircle(getWidth()/2,getWidth()/2,(getWidth()- progressCircleWidth)/2,paint);

        if(bitmap_cover==null){
            bitmap_cover = BitmapFactory.decodeResource(getResources(),R.drawable.default_cover);
        }
        bitmap_cover = Bitmap.createScaledBitmap(bitmap_cover, bitmapWidth, bitmapWidth, true);
        canvas.drawBitmap(getCircleBitmap(bitmap_cover), progressCircleWidth, progressCircleWidth,null);

        paint.setColor(progressCircleColor);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(progressCircleWidth);
        canvas.drawPoint(getWidth()/2, progressCircleWidth /2,paint);

        canvas.drawArc(progressCircleWidth /2, progressCircleWidth /2,getWidth()- progressCircleWidth /2,getHeight()- progressCircleWidth /2,-90,progress,false,paint);
    }

    public void loadCover(Bitmap bitmap){
        bitmap_cover = bitmap;
        invalidate();
    }


    private Bitmap getCircleBitmap(Bitmap source){
        if (source == null) {
            return null;
        }
        int width = source.getWidth();
        int height = source.getHeight();
        int length = Math.min(width,height);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(length,length, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(width/2,height/2,length/2,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source,0,0,paint);
        return target;
    }

    private Runnable updateProgressRunable = new Runnable() {
        @Override
        public void run() {
            progress = progress+1;
            if(progress == 360){
                progress = 0;
            }
            invalidate();
            handler.postDelayed(updateProgressRunable,100);
        }
    };

    public void start(){
        handler.post(updateProgressRunable);
    }

    public void pause(){
        handler.removeCallbacks(updateProgressRunable);
    }

    public void update(float percent){
        progress = (int) (360*percent);
        invalidate();
    }
}
