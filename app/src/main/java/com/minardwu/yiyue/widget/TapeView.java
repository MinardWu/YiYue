package com.minardwu.yiyue.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.minardwu.yiyue.utils.SystemUtils;

/**
 * Created by MinardWu on 2018/2/20.
 */

public class TapeView extends View {

    private static final String TAG = "TapeView";
    private int screenWidth = SystemUtils.getScreenWidth();
    private int screenHeight = SystemUtils.getScreenHeight();
    private String song = "七里香";
    private String artist = "周杰伦";


    public TapeView(Context context) {
        this(context,null);
    }

    public TapeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TapeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画中间主要部分
        Paint centerAreaPaint = new Paint();
        centerAreaPaint.setAntiAlias(true);
        centerAreaPaint.setColor(Color.BLACK);
        centerAreaPaint.setStrokeWidth(5);
        centerAreaPaint.setStyle(Paint.Style.STROKE);
        Path centerAreaPath = new Path();
        float semicircleRadius = 125;//左右两个半圆的半径
        float semicircleDiameter = 250;//左右两个半圆的直径
        float gearMargin = 20;
        float gearRadius = semicircleRadius-gearMargin;
        float ellipseLineLength = 3.f/5.f*screenWidth-semicircleRadius*2;
        float centerOvalVerticalMargin = 20;
        float centerOvalhorizontalMargin = 80;
        MyPoint ellipseRelativeStart = new MyPoint(1.f/5.f*screenWidth,1.f/3.f*screenHeight);
        MyPoint leftGearCenterPoint = new MyPoint(1.f/5.f*screenWidth+gearMargin+gearRadius,1.f/3.f*screenHeight+gearMargin+gearRadius);
        MyPoint rightGearCenterPoint = new MyPoint(1.f/5.f*screenWidth+gearMargin+gearRadius+ellipseLineLength,1.f/3.f*screenHeight+gearMargin+gearRadius);
        {
            centerAreaPath.moveTo(1.f/20.f*screenWidth,3.f/32.f*screenHeight);
            centerAreaPath.lineTo(19.f/20.f*screenWidth,3.f/32.f*screenHeight);
            centerAreaPath.lineTo(19.f/20.f*screenWidth,23.f/32.f*screenHeight);
            centerAreaPath.lineTo(1.f/20.f*screenWidth,23.f/32.f*screenHeight);
            centerAreaPath.lineTo(1.f/20.f*screenWidth,3.f/32.f*screenHeight);
            //画中间椭圆部分
            centerAreaPath.moveTo(ellipseRelativeStart.getX(),ellipseRelativeStart.getY());//移动到椭圆左上点
            centerAreaPath.arcTo(ellipseRelativeStart.getX(),ellipseRelativeStart.getY(),ellipseRelativeStart.getX()+semicircleDiameter,ellipseRelativeStart.getY()+semicircleDiameter,270,-180,true);//从上往下画180度弧
            centerAreaPath.rLineTo(ellipseLineLength,0);//接着往右移画下面直线
            centerAreaPath.arcTo(ellipseRelativeStart.getX()+ellipseLineLength,ellipseRelativeStart.getY(),ellipseRelativeStart.getX()+semicircleDiameter+ellipseLineLength,ellipseRelativeStart.getY()+semicircleDiameter,90,-180,true);//从下往上画180度弧
            centerAreaPath.rLineTo(-ellipseLineLength,0);//接着往左移画上面直线
            //画齿轮部分
            centerAreaPath.addCircle(leftGearCenterPoint.getX(),leftGearCenterPoint.getY(),gearRadius, Path.Direction.CW);
            centerAreaPath.addCircle(rightGearCenterPoint.getX(),rightGearCenterPoint.getY(),gearRadius, Path.Direction.CW);
            centerAreaPath.addRect(leftGearCenterPoint.x+gearRadius+centerOvalhorizontalMargin,ellipseRelativeStart.y+centerOvalVerticalMargin,rightGearCenterPoint.x-gearRadius-centerOvalhorizontalMargin,ellipseRelativeStart.y+semicircleDiameter-centerOvalVerticalMargin, Path.Direction.CW);
        }
        canvas.drawPath(centerAreaPath,centerAreaPaint);
        centerAreaPaint.setStrokeWidth(20);
        for (int i=0;i<6;i++){
            canvas.save();
            canvas.rotate(i*60,leftGearCenterPoint.x,leftGearCenterPoint.y);
            canvas.drawLine(leftGearCenterPoint.x,leftGearCenterPoint.y-gearRadius,leftGearCenterPoint.x,leftGearCenterPoint.y-gearRadius+gearRadius/4,centerAreaPaint);
            canvas.restore();

            canvas.save();
            canvas.rotate(i*60,rightGearCenterPoint.x,rightGearCenterPoint.y);
            canvas.drawLine(rightGearCenterPoint.x,rightGearCenterPoint.y-gearRadius,rightGearCenterPoint.x,rightGearCenterPoint.y-gearRadius+gearRadius/4,centerAreaPaint);
            canvas.restore();
        }
        centerAreaPaint.setStrokeWidth(5);
        centerAreaPaint.setTextSize(60);
        centerAreaPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(song,1.f/2.f*screenWidth,1.f/6.f*screenHeight,centerAreaPaint);
        centerAreaPaint.setTextSize(50);
        canvas.drawText(artist,1.f/2.f*screenWidth,1.f/6.f*screenHeight+100,centerAreaPaint);

        //画四个钉子
        Paint nailPaint = new Paint();
        float nailRadius = 1.f/50.f*screenWidth;
        float nailMargin = 1.f/100.f*screenWidth;
        nailPaint.setAntiAlias(true);
        nailPaint.setColor(Color.BLACK);
        nailPaint.setStyle(Paint.Style.FILL);
        Path nailPath = new Path();
        {
            nailPath.addCircle(nailMargin+nailRadius,nailMargin+nailRadius,nailRadius, Path.Direction.CW);//左上
            nailPath.addCircle(nailMargin+nailRadius,screenHeight-nailMargin-nailRadius,nailRadius,Path.Direction.CW);//左下
            nailPath.addCircle(screenWidth-nailMargin-nailRadius,nailMargin+nailRadius,nailRadius,Path.Direction.CW);//右上
            nailPath.addCircle(screenWidth-nailMargin-nailRadius,screenHeight-nailMargin-nailRadius,nailRadius,Path.Direction.CW);//右下
        }
        canvas.drawPath(nailPath,nailPaint);


        Paint bottomPaint = new Paint();
        bottomPaint.setAntiAlias(true);
        bottomPaint.setColor(Color.BLACK);
        bottomPaint.setStyle(Paint.Style.FILL);
        Path bottomPath = new Path();
        {
            bottomPath.moveTo(1.f/6.f*screenWidth,screenHeight);
            bottomPath.lineTo((1.f/6.f+1.f/20.f)*screenWidth,3.f/4.f*screenHeight);
            bottomPath.lineTo((5.f/6.f-1.f/20.f)*screenWidth,3.f/4.f*screenHeight);
            bottomPath.lineTo(5.f/6.f*screenWidth,screenHeight);
            bottomPath.lineTo(1.f/6.f*screenWidth,screenHeight);
        }
        canvas.drawPath(bottomPath,bottomPaint);

    }

    private class MyPoint{
        public float x;
        public float y;

        public MyPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public MyPoint add(float x,float y){
            return  new MyPoint(this.x+x,this.y+y);
        }

        public MyPoint add(MyPoint point){
            return  new MyPoint(this.x+point.x,this.y+point.y);
        }
    }
}
