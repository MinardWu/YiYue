package com.minardwu.yiyue.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.os.Handler;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.utils.SystemUtils;

/**
 * Created by MinardWu on 2018/2/20.
 */

public class TapeView extends View {

    private static final String TAG = "TapeView";
    private int screenWidth = SystemUtils.getScreenWidth();
    private int screenHeight = SystemUtils.getScreenHeight();
    private String song = "不能说的秘密";
    private String artist = "周杰伦";
    private boolean isRunning = false;
    private float rotationDegree = 0;
    private Handler handler = new Handler();

    public TapeView(Context context) {
        this(context,null);
    }

    public TapeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TapeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Runnable rotateRunable = new Runnable() {
        @Override
        public void run() {
            rotationDegree += 2.f;
            if(rotationDegree>=360){
                rotationDegree = 0.f;
            }
            invalidate();
            handler.postDelayed(this,50);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!isRunning){
            handler.post(rotateRunable);
            isRunning = true;
        }
        canvas.drawColor(getResources().getColor(R.color.tapeBlack));

        //画中间主要部分
        Paint centerAreaPaint = new Paint();
        centerAreaPaint.setAntiAlias(true);
        centerAreaPaint.setColor(getResources().getColor(R.color.tapeRed));
        centerAreaPaint.setStrokeWidth(5);
        centerAreaPaint.setStyle(Paint.Style.FILL);
        Path centerAreaPath = new Path();
        float semicircleRadius = 125;//左右两个半圆的半径
        float semicircleDiameter = 250;//左右两个半圆的直径
        float gearMargin = 20;
        float gearRadius = semicircleRadius-gearMargin;
        float smallCircleRadius = 1.f/2.f*gearRadius;
        float ellipseLineLength = 3.f/5.f*screenWidth-semicircleRadius*2;
        float centerOvalVerticalMargin = 20;
        float centerOvalHorizontalMargin = 80;
        float sideATextMargin = 80;
        MyPoint ellipseRelativeStart = new MyPoint(1.f/5.f*screenWidth,1.f/3.f*screenHeight);
        MyPoint leftGearCenterPoint = new MyPoint(1.f/5.f*screenWidth+gearMargin+gearRadius,1.f/3.f*screenHeight+gearMargin+gearRadius);
        MyPoint rightGearCenterPoint = new MyPoint(1.f/5.f*screenWidth+gearMargin+gearRadius+ellipseLineLength,1.f/3.f*screenHeight+gearMargin+gearRadius);
        float centerOvalWidth = rightGearCenterPoint.x-leftGearCenterPoint.x-gearRadius*2-centerOvalHorizontalMargin*2;
        float ovalStartX = leftGearCenterPoint.x+gearRadius+centerOvalHorizontalMargin;
        float ovalCenterY = leftGearCenterPoint.y;
        MyPoint ovalLeftTopPoint = new MyPoint(ovalStartX,ellipseRelativeStart.y+centerOvalVerticalMargin);
        MyPoint ovalRightTopPoint = new MyPoint(ovalStartX+centerOvalWidth,ellipseRelativeStart.y+centerOvalVerticalMargin);
        canvas.drawRoundRect(1.f/20.f*screenWidth,3.f/32.f*screenHeight,19.f/20.f*screenWidth,23.f/32.f*screenHeight,50,50,centerAreaPaint);
        centerAreaPaint.setColor(getResources().getColor(R.color.tapeBlack));
        {
//            centerAreaPath.moveTo(1.f/20.f*screenWidth,3.f/32.f*screenHeight);
//            centerAreaPath.lineTo(19.f/20.f*screenWidth,3.f/32.f*screenHeight);
//            centerAreaPath.lineTo(19.f/20.f*screenWidth,23.f/32.f*screenHeight);
//            centerAreaPath.lineTo(1.f/20.f*screenWidth,23.f/32.f*screenHeight);
//            centerAreaPath.lineTo(1.f/20.f*screenWidth,3.f/32.f*screenHeight);
            //画中间椭圆部分
            centerAreaPath.moveTo(ellipseRelativeStart.getX(),ellipseRelativeStart.getY());//移动到椭圆左上点
            centerAreaPath.arcTo(ellipseRelativeStart.getX(),ellipseRelativeStart.getY(),ellipseRelativeStart.getX()+semicircleDiameter,ellipseRelativeStart.getY()+semicircleDiameter,270,-180,true);//从上往下画180度弧
            centerAreaPath.rLineTo(ellipseLineLength,0);//接着往右移画下面直线
            centerAreaPath.arcTo(ellipseRelativeStart.getX()+ellipseLineLength,ellipseRelativeStart.getY(),ellipseRelativeStart.getX()+semicircleDiameter+ellipseLineLength,ellipseRelativeStart.getY()+semicircleDiameter,90,-180,true);//从下往上画180度弧
            centerAreaPath.rLineTo(-ellipseLineLength,0);//接着往左移画上面直线
            //画齿轮最外圆
            centerAreaPath.addCircle(leftGearCenterPoint.getX(),leftGearCenterPoint.getY(),gearRadius, Path.Direction.CW);
            centerAreaPath.addCircle(rightGearCenterPoint.getX(),rightGearCenterPoint.getY(),gearRadius, Path.Direction.CW);
        }
        canvas.drawPath(centerAreaPath,centerAreaPaint);
        //画长方形
        centerAreaPaint.setColor(Color.DKGRAY);
        centerAreaPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(leftGearCenterPoint.x+gearRadius+centerOvalHorizontalMargin,ellipseRelativeStart.y+centerOvalVerticalMargin,rightGearCenterPoint.x-gearRadius-centerOvalHorizontalMargin,ellipseRelativeStart.y+semicircleDiameter-centerOvalVerticalMargin,centerAreaPaint);
        //画左边唱片
        centerAreaPaint.setColor(getResources().getColor(R.color.tapeBlack));
        canvas.drawRect(ovalStartX,ellipseRelativeStart.y+centerOvalVerticalMargin,ovalStartX+160,ellipseRelativeStart.y+semicircleDiameter-centerOvalVerticalMargin,centerAreaPaint);//填充唱片背景
        centerAreaPaint.setStrokeWidth(3);
        centerAreaPaint.setStyle(Paint.Style.FILL);
        centerAreaPaint.setColor(getResources().getColor(R.color.tapeRed));
        drawLeftDisc(canvas,leftGearCenterPoint,ovalLeftTopPoint.add(0,20),centerAreaPaint);
        centerAreaPaint.setStyle(Paint.Style.STROKE);
        centerAreaPaint.setStrokeWidth(0.5f);
        centerAreaPaint.setColor(Color.LTGRAY);
        ovalLeftTopPoint.x += 50;
        drawLeftDisc(canvas,leftGearCenterPoint,ovalLeftTopPoint,centerAreaPaint);
        ovalLeftTopPoint.x += 30;
        drawLeftDisc(canvas,leftGearCenterPoint,ovalLeftTopPoint,centerAreaPaint);
        ovalLeftTopPoint.x += 50;
        drawLeftDisc(canvas,leftGearCenterPoint,ovalLeftTopPoint,centerAreaPaint);
        ovalLeftTopPoint.x += 30;
        centerAreaPaint.setStyle(Paint.Style.FILL);
        centerAreaPaint.setColor(getResources().getColor(R.color.tapeBlack));
        drawLeftDisc(canvas,leftGearCenterPoint,ovalLeftTopPoint,centerAreaPaint);
        //画右边唱片
        centerAreaPaint.setColor(getResources().getColor(R.color.tapeBlack));
        canvas.drawRect(ovalStartX+centerOvalWidth-130,ellipseRelativeStart.y+centerOvalVerticalMargin,ovalStartX+centerOvalWidth,ellipseRelativeStart.y+semicircleDiameter-centerOvalVerticalMargin,centerAreaPaint);//填充唱片背景
        centerAreaPaint.setStrokeWidth(3);
        centerAreaPaint.setStyle(Paint.Style.FILL);
        centerAreaPaint.setColor(getResources().getColor(R.color.tapeRed));
        drawRightDisc(canvas,rightGearCenterPoint,ovalRightTopPoint.add(0,20),centerAreaPaint);
        centerAreaPaint.setStyle(Paint.Style.STROKE);
        centerAreaPaint.setStrokeWidth(0.5f);
        centerAreaPaint.setColor(Color.LTGRAY);
        ovalRightTopPoint.x -= 50;
        drawRightDisc(canvas,rightGearCenterPoint,ovalRightTopPoint,centerAreaPaint);
        ovalRightTopPoint.x -= 30;
        drawRightDisc(canvas,rightGearCenterPoint,ovalRightTopPoint,centerAreaPaint);
        ovalRightTopPoint.x -= 50;
        centerAreaPaint.setStyle(Paint.Style.FILL);
        centerAreaPaint.setColor(getResources().getColor(R.color.tapeBlack));
        drawRightDisc(canvas,rightGearCenterPoint,ovalRightTopPoint,centerAreaPaint);
        //画长方形里面的点
        int count = 1;
        float[] points = new float[14];
        float decX = centerOvalWidth/8;
        for(int i=0;i<14;i+=2){
            points[i] = ovalStartX+count*decX;
            points[i+1] = ovalCenterY;
            count++;
        }
        centerAreaPaint.setColor(Color.WHITE);
        centerAreaPaint.setStrokeWidth(5);
        canvas.drawPoints(points,centerAreaPaint);
        //画长方形里面的两条短竖线
        canvas.drawLine(ovalStartX+centerOvalWidth/2,ovalCenterY-50,ovalStartX+centerOvalWidth/2,ovalCenterY-20,centerAreaPaint);
        canvas.drawLine(ovalStartX+centerOvalWidth/2,ovalCenterY+20,ovalStartX+centerOvalWidth/2,ovalCenterY+50,centerAreaPaint);
        //齿轮最外层两个圆
        centerAreaPaint.setStyle(Paint.Style.STROKE);
        centerAreaPaint.setStrokeWidth(10);
        centerAreaPaint.setColor(Color.WHITE);
        canvas.drawCircle(leftGearCenterPoint.getX(),leftGearCenterPoint.getY(),semicircleRadius-20f,centerAreaPaint);
        canvas.drawCircle(rightGearCenterPoint.getX(),rightGearCenterPoint.getY(),semicircleRadius-20f,centerAreaPaint);
        centerAreaPaint.setColor(Color.BLACK);
        canvas.drawCircle(leftGearCenterPoint.getX(),leftGearCenterPoint.getY(),semicircleRadius-30f,centerAreaPaint);
        canvas.drawCircle(rightGearCenterPoint.getX(),rightGearCenterPoint.getY(),semicircleRadius-30f,centerAreaPaint);
        //齿轮最里面两个圆
        centerAreaPaint.setColor(Color.BLACK);
        centerAreaPaint.setStrokeWidth(20);
        centerAreaPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(leftGearCenterPoint.getX(),leftGearCenterPoint.getY(),smallCircleRadius,centerAreaPaint);
        canvas.drawCircle(rightGearCenterPoint.getX(),rightGearCenterPoint.getY(),smallCircleRadius,centerAreaPaint);
        centerAreaPaint.setColor(Color.WHITE);
        canvas.drawCircle(leftGearCenterPoint.getX(),leftGearCenterPoint.getY(),smallCircleRadius/3,centerAreaPaint);
        canvas.drawCircle(rightGearCenterPoint.getX(),rightGearCenterPoint.getY(),smallCircleRadius/3,centerAreaPaint);
        //画齿轮的齿
        centerAreaPaint.setColor(Color.BLACK);
        centerAreaPaint.setStrokeWidth(20);
        for (int i=0;i<12;i++){
            canvas.save();
            canvas.rotate(i*30+rotationDegree,leftGearCenterPoint.x,leftGearCenterPoint.y);
            if(i%2==0){
                canvas.drawLine(leftGearCenterPoint.x,leftGearCenterPoint.y-gearRadius+15.f,leftGearCenterPoint.x,leftGearCenterPoint.y-gearRadius+15.f+gearRadius/6,centerAreaPaint);
            }else {
                canvas.drawLine(leftGearCenterPoint.x,leftGearCenterPoint.y-smallCircleRadius-gearRadius/6,leftGearCenterPoint.x,leftGearCenterPoint.y-smallCircleRadius,centerAreaPaint);
            }
            canvas.restore();

            canvas.save();
            canvas.rotate(i*30+rotationDegree,rightGearCenterPoint.x,rightGearCenterPoint.y);
            if(i%2==0){
                canvas.drawLine(rightGearCenterPoint.x,rightGearCenterPoint.y-gearRadius+15.f,rightGearCenterPoint.x,rightGearCenterPoint.y-gearRadius+gearRadius/6+15.f,centerAreaPaint);
            }else {
                canvas.drawLine(rightGearCenterPoint.x,rightGearCenterPoint.y-smallCircleRadius-gearRadius/6,rightGearCenterPoint.x,rightGearCenterPoint.y-smallCircleRadius,centerAreaPaint);
            }
            canvas.restore();
        }

        //写字
        centerAreaPaint.setStrokeWidth(5);
        centerAreaPaint.setTextSize(60);
        centerAreaPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(song,1.f/2.f*screenWidth,1.f/6.f*screenHeight,centerAreaPaint);
        centerAreaPaint.setTextSize(50);
        canvas.drawText(artist,1.f/2.f*screenWidth,1.f/6.f*screenHeight+100,centerAreaPaint);
        centerAreaPaint.setColor(getResources().getColor(R.color.tapeInfoTextColor));
        centerAreaPaint.setTextAlign(Paint.Align.RIGHT);
        centerAreaPaint.setTextSize(60);
        canvas.drawText("Side",leftGearCenterPoint.x-gearRadius-sideATextMargin,leftGearCenterPoint.y-20,centerAreaPaint);
        centerAreaPaint.setTextAlign(Paint.Align.CENTER);
        centerAreaPaint.setTextSize(80);
        canvas.drawText("A",leftGearCenterPoint.x-gearRadius-sideATextMargin-60,leftGearCenterPoint.y+80,centerAreaPaint);
        centerAreaPaint.setTextSize(40);
        canvas.drawText("一乐 Copyright ©2018 MinardWu",screenWidth/2,leftGearCenterPoint.y+gearRadius+120,centerAreaPaint);

//        canvas.save();
//        TextPaint textPaint = new TextPaint();
//        textPaint.setColor(Color.parseColor("#ffffff"));
//        textPaint.setTextSize(50.0F);
//        textPaint.setAntiAlias(true);
//        //StaticLayout layout = new StaticLayout("Side B", textPaint, 160, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
//        StaticLayout layout = new StaticLayout("Side B", 0,2,textPaint, 1000, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
//        canvas.translate(leftGearCenterPoint.x-gearRadius-sideATextMargin,leftGearCenterPoint.y+100);
//        layout.draw(canvas);
//        canvas.restore();

        //四个钉子
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

        //底部梯形
        Paint bottomAreaPaint = new Paint();
        bottomAreaPaint.setAntiAlias(true);
        bottomAreaPaint.setColor(getResources().getColor(R.color.tapeRed));
        bottomAreaPaint.setStyle(Paint.Style.STROKE);
        bottomAreaPaint.setStrokeWidth(8);
        Path bottomAreaPath = new Path();
        float bottomAreaHeight = 1.f/4.f*screenHeight;
        float bottomAreaTopLength = 2.f/3.f*screenWidth-2*1.f/20.f*screenWidth;
        float bottomAreaBottomLength = 2.f/3.f*screenWidth;
        MyPoint bottomLeftTopPoint = new MyPoint((1.f/6.f+1.f/20.f)*screenWidth,3.f/4.f*screenHeight);
        MyPoint bottomRightTopPoint = new MyPoint((5.f/6.f-1.f/20.f)*screenWidth,3.f/4.f*screenHeight);
        float tan = bottomAreaHeight/((1.f/20.f)*screenWidth);
        {
            bottomAreaPath.moveTo(1.f/6.f*screenWidth,screenHeight);
            bottomAreaPath.lineTo((1.f/6.f+1.f/20.f)*screenWidth,screenHeight-bottomAreaHeight);
            bottomAreaPath.lineTo((5.f/6.f-1.f/20.f)*screenWidth,screenHeight-bottomAreaHeight);
            bottomAreaPath.lineTo(5.f/6.f*screenWidth,screenHeight);
            bottomAreaPath.lineTo(1.f/6.f*screenWidth,screenHeight);
        }
        canvas.drawPath(bottomAreaPath,bottomAreaPaint);
        for(int i=0;i<8;i++){
            //画线
            float desY = bottomAreaHeight/8;
            float startX = bottomLeftTopPoint.x-i*(desY/tan);
            float startY = bottomLeftTopPoint.y+desY*i;
            float endX = bottomRightTopPoint.x+i*(desY/tan);
            float endY = bottomRightTopPoint.y+desY*i;
            canvas.drawLine(startX,startY,endX,endY,bottomAreaPaint);
        }
        //梯形中的五个孔
        bottomAreaPaint.setStyle(Paint.Style.FILL);
        bottomAreaPaint.setColor(Color.BLACK);
        canvas.drawCircle(screenWidth/2,screenHeight-bottomAreaHeight+bottomAreaHeight/6,bottomAreaHeight/10,bottomAreaPaint);
        canvas.drawCircle(screenWidth/2-bottomAreaBottomLength/3,screenHeight-bottomAreaHeight+bottomAreaHeight/6*5,bottomAreaHeight/8,bottomAreaPaint);
        canvas.drawCircle(screenWidth/2+bottomAreaBottomLength/3,screenHeight-bottomAreaHeight+bottomAreaHeight/6*5,bottomAreaHeight/8,bottomAreaPaint);
        canvas.drawRect(bottomLeftTopPoint.x+bottomAreaTopLength/4,screenHeight-bottomAreaHeight+bottomAreaHeight/2,bottomLeftTopPoint.x+bottomAreaTopLength/4+bottomAreaHeight/5,screenHeight-bottomAreaHeight+bottomAreaHeight/2+bottomAreaHeight/5,bottomAreaPaint);
        canvas.drawRect(bottomRightTopPoint.x-bottomAreaTopLength/4,screenHeight-bottomAreaHeight+bottomAreaHeight/2,bottomRightTopPoint.x-bottomAreaTopLength/4+bottomAreaHeight/5,screenHeight-bottomAreaHeight+bottomAreaHeight/2+bottomAreaHeight/5,bottomAreaPaint);
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawLeftDisc(Canvas canvas, MyPoint gearCenter, MyPoint point, Paint paint){
        float tan = (gearCenter.y-point.y)/(point.x-gearCenter.x);
        float radius = (float) Math.sqrt(Math.pow(point.y-gearCenter.y,2)+Math.pow(point.x-gearCenter.x,2));
        float angle = (float) Math.atan(tan)/(float) Math.PI*180.f;
        angle = angle;//这里+1是为了扩大弧，使弧与长方形更好的贴合
        canvas.drawArc(gearCenter.x-radius,gearCenter.y-radius,gearCenter.x+radius,gearCenter.y+radius,-angle,2*angle,false,paint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawRightDisc(Canvas canvas, MyPoint gearCenter, MyPoint point, Paint paint){
        float tan = (gearCenter.y-point.y)/(gearCenter.x-point.x);
        float raius = (float) Math.sqrt(Math.pow(gearCenter.y-point.y,2)+Math.pow(gearCenter.x-point.x,2));
        float angle = (float) Math.atan(tan)/(float) Math.PI*180.f;
        angle = angle;//这里+1是为了扩大弧，使弧与长方形更好的贴合
        canvas.drawArc(gearCenter.x-raius,gearCenter.y-raius,gearCenter.x+raius,gearCenter.y+raius,180-angle,2*angle,false,paint);
    }
}
