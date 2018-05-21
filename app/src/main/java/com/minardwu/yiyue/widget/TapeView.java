package com.minardwu.yiyue.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.os.Handler;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.utils.SystemUtils;
import com.minardwu.yiyue.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MinardWu on 2018/2/20.
 */

public class TapeView extends View {

    private static final String TAG = "TapeView";
    private int screenWidth = SystemUtils.getScreenWidth();
    private int screenHeight = SystemUtils.getScreenHeight();
    private String title = UIUtils.getString(R.string.tape_view_title);
    private String artist = UIUtils.getString(R.string.tape_view_artist);
    private boolean isRunning = false;
    private float rotationDegree = 0;
    private Handler handler = new Handler();
    private List<Rect> gearRectList = new ArrayList<>();

    public TapeView(Context context) {
        this(context,null);
    }

    public TapeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TapeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Runnable rotateRunnable = new Runnable() {
        @Override
        public void run() {
            rotationDegree += 0.4f;
            if(rotationDegree>=360){
                rotationDegree = 0.f;
            }
            invalidate();
            handler.postDelayed(this,10);
        }
    };

    int accelerateCount = 0;
    boolean isAccelerate = true;
    private Runnable accelerateRunnable = new Runnable() {
        @Override
        public void run() {
            if (isAccelerate){
                rotationDegree += 0.4f* accelerateCount;
                accelerateCount += 1;
            }else {
                rotationDegree -= 0.4f* accelerateCount;
                accelerateCount -= 1;
            }

            if (rotationDegree>=360){
                rotationDegree=0.f;
            }
            invalidate();
            //加速结束
            if(accelerateCount ==50){
                isAccelerate = false;
            }
            //减速结束
            if (accelerateCount ==0){
                isAccelerate = true;
                handler.removeCallbacks(this);
                handler.post(rotateRunnable);
                return;
            }
            handler.postDelayed(this,10);
        }
    };

    public void startAccelerate(){
        if (isRunning){
            handler.removeCallbacks(rotateRunnable);
        }
        handler.post(accelerateRunnable);
    }

    public void startRotate(){
        if (!isRunning){
            handler.post(rotateRunnable);
            isRunning = true;
        }
    }

    public void stopRotate(){
        if (isRunning){
            handler.removeCallbacks(rotateRunnable);
            isRunning = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(getResources().getColor(R.color.tapeBlack));

        //画中间主要部分
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.tapeRed));
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.FILL);
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
        canvas.drawRoundRect(1.f/20.f*screenWidth,3.f/32.f*screenHeight,19.f/20.f*screenWidth,23.f/32.f*screenHeight,50,50,paint);
        paint.setColor(getResources().getColor(R.color.tapeBlack));
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
        canvas.drawPath(centerAreaPath,paint);
        //画长方形
        paint.setColor(Color.DKGRAY);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(leftGearCenterPoint.x+gearRadius+centerOvalHorizontalMargin,ellipseRelativeStart.y+centerOvalVerticalMargin,rightGearCenterPoint.x-gearRadius-centerOvalHorizontalMargin,ellipseRelativeStart.y+semicircleDiameter-centerOvalVerticalMargin,paint);
        //画左边唱片
        paint.setColor(getResources().getColor(R.color.tapeBlack));
        canvas.drawRect(ovalStartX,ellipseRelativeStart.y+centerOvalVerticalMargin,ovalStartX+160,ellipseRelativeStart.y+semicircleDiameter-centerOvalVerticalMargin,paint);//填充唱片背景
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.tapeRed));
        drawLeftDisc(canvas,leftGearCenterPoint,ovalLeftTopPoint.add(0,20),paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(0.5f);
        paint.setColor(Color.LTGRAY);
        ovalLeftTopPoint.x += 50;
        drawLeftDisc(canvas,leftGearCenterPoint,ovalLeftTopPoint,paint);
        ovalLeftTopPoint.x += 30;
        drawLeftDisc(canvas,leftGearCenterPoint,ovalLeftTopPoint,paint);
        ovalLeftTopPoint.x += 50;
        drawLeftDisc(canvas,leftGearCenterPoint,ovalLeftTopPoint,paint);
        ovalLeftTopPoint.x += 30;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.tapeBlack));
        drawLeftDisc(canvas,leftGearCenterPoint,ovalLeftTopPoint,paint);
        //画右边唱片
        paint.setColor(getResources().getColor(R.color.tapeBlack));
        canvas.drawRect(ovalStartX+centerOvalWidth-130,ellipseRelativeStart.y+centerOvalVerticalMargin,ovalStartX+centerOvalWidth,ellipseRelativeStart.y+semicircleDiameter-centerOvalVerticalMargin,paint);//填充唱片背景
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.tapeRed));
        drawRightDisc(canvas,rightGearCenterPoint,ovalRightTopPoint.add(0,20),paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(0.5f);
        paint.setColor(Color.LTGRAY);
        ovalRightTopPoint.x -= 50;
        drawRightDisc(canvas,rightGearCenterPoint,ovalRightTopPoint,paint);
        ovalRightTopPoint.x -= 30;
        drawRightDisc(canvas,rightGearCenterPoint,ovalRightTopPoint,paint);
        ovalRightTopPoint.x -= 50;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.tapeBlack));
        drawRightDisc(canvas,rightGearCenterPoint,ovalRightTopPoint,paint);
        //画长方形里面的点
        int count = 1;
        float[] points = new float[14];
        float decX = centerOvalWidth/8;
        for(int i=0;i<14;i+=2){
            points[i] = ovalStartX+count*decX;
            points[i+1] = ovalCenterY;
            count++;
        }
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5);
        canvas.drawPoints(points,paint);
        //画长方形里面的两条短竖线
        canvas.drawLine(ovalStartX+centerOvalWidth/2,ovalCenterY-50,ovalStartX+centerOvalWidth/2,ovalCenterY-20,paint);
        canvas.drawLine(ovalStartX+centerOvalWidth/2,ovalCenterY+20,ovalStartX+centerOvalWidth/2,ovalCenterY+50,paint);
        //齿轮最外层两个圆
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(leftGearCenterPoint.getX(),leftGearCenterPoint.getY(),semicircleRadius-20f,paint);
        canvas.drawCircle(rightGearCenterPoint.getX(),rightGearCenterPoint.getY(),semicircleRadius-20f,paint);
        gearRectList.add(new Rect((int)leftGearCenterPoint.getX()-(int)(semicircleRadius-20f),(int)leftGearCenterPoint.getY()-(int)(semicircleRadius-20f),(int)leftGearCenterPoint.getX()+(int)(semicircleRadius-20f),(int)leftGearCenterPoint.getY()+(int)(semicircleRadius-20f)));
        gearRectList.add(new Rect((int)rightGearCenterPoint.getX()-(int)(semicircleRadius-20f),(int)rightGearCenterPoint.getY()-(int)(semicircleRadius-20f),(int)rightGearCenterPoint.getX()+(int)(semicircleRadius-20f),(int)rightGearCenterPoint.getY()+(int)(semicircleRadius-20f)));
        paint.setColor(Color.BLACK);
        canvas.drawCircle(leftGearCenterPoint.getX(),leftGearCenterPoint.getY(),semicircleRadius-30f,paint);
        canvas.drawCircle(rightGearCenterPoint.getX(),rightGearCenterPoint.getY(),semicircleRadius-30f,paint);
        //齿轮最里面两个圆
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(leftGearCenterPoint.getX(),leftGearCenterPoint.getY(),smallCircleRadius,paint);
        canvas.drawCircle(rightGearCenterPoint.getX(),rightGearCenterPoint.getY(),smallCircleRadius,paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(leftGearCenterPoint.getX(),leftGearCenterPoint.getY(),smallCircleRadius/3,paint);
        canvas.drawCircle(rightGearCenterPoint.getX(),rightGearCenterPoint.getY(),smallCircleRadius/3,paint);
        //画齿轮的齿
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(20);
        for (int i=0;i<12;i++){
            canvas.save();
            canvas.rotate(i*30+rotationDegree,leftGearCenterPoint.x,leftGearCenterPoint.y);
            if(i%2==0){
                canvas.drawLine(leftGearCenterPoint.x,leftGearCenterPoint.y-gearRadius+15.f,leftGearCenterPoint.x,leftGearCenterPoint.y-gearRadius+15.f+gearRadius/6,paint);
            }else {
                canvas.drawLine(leftGearCenterPoint.x,leftGearCenterPoint.y-smallCircleRadius-gearRadius/6,leftGearCenterPoint.x,leftGearCenterPoint.y-smallCircleRadius,paint);
            }
            canvas.restore();

            canvas.save();
            canvas.rotate(i*30+rotationDegree,rightGearCenterPoint.x,rightGearCenterPoint.y);
            if(i%2==0){
                canvas.drawLine(rightGearCenterPoint.x,rightGearCenterPoint.y-gearRadius+15.f,rightGearCenterPoint.x,rightGearCenterPoint.y-gearRadius+gearRadius/6+15.f,paint);
            }else {
                canvas.drawLine(rightGearCenterPoint.x,rightGearCenterPoint.y-smallCircleRadius-gearRadius/6,rightGearCenterPoint.x,rightGearCenterPoint.y-smallCircleRadius,paint);
            }
            canvas.restore();
        }

        //写字
        paint.setColor(getResources().getColor(R.color.tapeMusicInfoTextColor));
        paint.setStrokeWidth(10);
        paint.setTextSize(60);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(title,1.f/2.f*screenWidth,1.f/6.f*screenHeight,paint);
        paint.setTextSize(50);
        canvas.drawText(artist,1.f/2.f*screenWidth,1.f/6.f*screenHeight+100,paint);
        paint.setStrokeWidth(5);
        paint.setColor(getResources().getColor(R.color.tapeInfoTextColor));
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(60);
        canvas.drawText(UIUtils.getString(R.string.tape_view_side),leftGearCenterPoint.x-gearRadius-sideATextMargin,leftGearCenterPoint.y-20,paint);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(80);
        canvas.drawText(UIUtils.getString(R.string.tape_view_side_vaule),leftGearCenterPoint.x-gearRadius-sideATextMargin-60,leftGearCenterPoint.y+80,paint);
        paint.setTextSize(40);
        canvas.drawText(UIUtils.getString(R.string.tape_view_copyright),screenWidth/2,leftGearCenterPoint.y+gearRadius+120,paint);

        //四个钉子
        float nailRadius = 1.f/50.f*screenWidth;
        float nailMargin = 1.f/100.f*screenWidth;
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        Path nailPath = new Path();
        {
            nailPath.addCircle(nailMargin+nailRadius,nailMargin+nailRadius,nailRadius, Path.Direction.CW);//左上
            nailPath.addCircle(nailMargin+nailRadius,screenHeight-nailMargin-nailRadius,nailRadius,Path.Direction.CW);//左下
            nailPath.addCircle(screenWidth-nailMargin-nailRadius,nailMargin+nailRadius,nailRadius,Path.Direction.CW);//右上
            nailPath.addCircle(screenWidth-nailMargin-nailRadius,screenHeight-nailMargin-nailRadius,nailRadius,Path.Direction.CW);//右下
        }
        canvas.drawPath(nailPath,paint);

        //底部梯形
        paint.setColor(getResources().getColor(R.color.tapeRed));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
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
        canvas.drawPath(bottomAreaPath,paint);
        for(int i=0;i<8;i++){
            //画线
            float desY = bottomAreaHeight/8;
            float startX = bottomLeftTopPoint.x-i*(desY/tan);
            float startY = bottomLeftTopPoint.y+desY*i;
            float endX = bottomRightTopPoint.x+i*(desY/tan);
            float endY = bottomRightTopPoint.y+desY*i;
            canvas.drawLine(startX,startY,endX,endY,paint);
        }
        //梯形中的五个孔
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(screenWidth/2,screenHeight-bottomAreaHeight+bottomAreaHeight/6,bottomAreaHeight/10,paint);
        canvas.drawCircle(screenWidth/2-bottomAreaBottomLength/3,screenHeight-bottomAreaHeight+bottomAreaHeight/6*5,bottomAreaHeight/8,paint);
        canvas.drawCircle(screenWidth/2+bottomAreaBottomLength/3,screenHeight-bottomAreaHeight+bottomAreaHeight/6*5,bottomAreaHeight/8,paint);
        canvas.drawRect(bottomLeftTopPoint.x+bottomAreaTopLength/4,screenHeight-bottomAreaHeight+bottomAreaHeight/2,bottomLeftTopPoint.x+bottomAreaTopLength/4+bottomAreaHeight/5,screenHeight-bottomAreaHeight+bottomAreaHeight/2+bottomAreaHeight/5,paint);
        canvas.drawRect(bottomRightTopPoint.x-bottomAreaTopLength/4,screenHeight-bottomAreaHeight+bottomAreaHeight/2,bottomRightTopPoint.x-bottomAreaTopLength/4+bottomAreaHeight/5,screenHeight-bottomAreaHeight+bottomAreaHeight/2+bottomAreaHeight/5,paint);
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
        //angle = angle;//这里+1是为了扩大弧，使弧与长方形更好的贴合
        canvas.drawArc(gearCenter.x-radius,gearCenter.y-radius,gearCenter.x+radius,gearCenter.y+radius,-angle,2*angle,false,paint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawRightDisc(Canvas canvas, MyPoint gearCenter, MyPoint point, Paint paint){
        float tan = (gearCenter.y-point.y)/(gearCenter.x-point.x);
        float raius = (float) Math.sqrt(Math.pow(gearCenter.y-point.y,2)+Math.pow(gearCenter.x-point.x,2));
        float angle = (float) Math.atan(tan)/(float) Math.PI*180.f;
        //angle = angle;//这里+1是为了扩大弧，使弧与长方形更好的贴合
        canvas.drawArc(gearCenter.x-raius,gearCenter.y-raius,gearCenter.x+raius,gearCenter.y+raius,180-angle,2*angle,false,paint);
    }

    public void setTitle(String title){
        this.title = title;
        invalidate();
    }

    public void setArtist(String artist){
        this.artist = artist;
        invalidate();
    }

    public interface OnGearClickListener{
        /**
         * 左边齿轮点击事件
         */
        void leftGearClick();

        /**
         * 右边齿轮点击事件
         */
        void rightGearClick();

        /**
         * 其他区域点击事件
         */
        void otherAreaClick();
    }

    private OnGearClickListener listener;

    public void setOnGearClickListener(OnGearClickListener onGearClickListener) {
        this.listener = onGearClickListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(gearRectList.get(0).contains((int)event.getX(),(int)event.getY())){
                listener.leftGearClick();
            }else if(gearRectList.get(1).contains((int)event.getX(),(int)event.getY())){
                listener.rightGearClick();
            }else {
                listener.otherAreaClick();
            }
        }
        return super.onTouchEvent(event);
    }
}
