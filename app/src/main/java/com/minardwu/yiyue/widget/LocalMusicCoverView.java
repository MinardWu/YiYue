package com.minardwu.yiyue.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.utils.CoverLoader;
import com.minardwu.yiyue.utils.ImageUtils;


public class LocalMusicCoverView extends View implements ValueAnimator.AnimatorUpdateListener {
    private static final long TIME_UPDATE = 10L;
    private static final float DISC_ROTATION_INCREASE = 0.1f;
    private static final float NEEDLE_ROTATION_PLAY = 0.0f;
    private static final float NEEDLE_ROTATION_PAUSE = -25.0f;
    private Handler mHandler = new Handler();
    private Bitmap discBitmap;
    private Bitmap coverBitmap;
    private Bitmap needleBitmap;
    private Drawable mTopLine;
    private Drawable mCoverBorder;
    private int mTopLineHeight;
    private int mCoverBorderWidth;
    private Matrix discMatrix = new Matrix();
    private Matrix coverMatrix = new Matrix();
    private Matrix needleMatrix = new Matrix();
    private ValueAnimator playAnimator;
    private ValueAnimator pauseAnimator;
    private float discAndCoverRotation = 0.0f;
    private float mNeedleRotation = NEEDLE_ROTATION_PLAY;
    private boolean isPlaying = false;

    // 图片起始坐标
    private Point discStartPoint = new Point();
    private Point coverStartPoint = new Point();
    private Point needleStartPoint = new Point();
    // 旋转中心坐标
    private Point discCenterPoint = new Point();
    private Point coverCenterPoint = new Point();
    private Point needleCenterPoint = new Point();

    public LocalMusicCoverView(Context context) {
        this(context, null);
    }

    public LocalMusicCoverView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocalMusicCoverView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTopLine = getResources().getDrawable(R.drawable.play_page_cover_top_line_shape);
        mCoverBorder = getResources().getDrawable(R.drawable.play_page_cover_border_shape);
        discBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.play_page_disc);
        discBitmap = ImageUtils.resizeImage(discBitmap, (int) (getScreenWidth() * 0.75), (int) (getScreenWidth() * 0.75));
        coverBitmap = CoverLoader.getInstance().loadRound(null);
        needleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.play_page_needle);
        needleBitmap = ImageUtils.resizeImage(needleBitmap, (int) (getScreenWidth() * 0.25), (int) (getScreenWidth() * 0.375));
        mTopLineHeight = dp2px(1);
        mCoverBorderWidth = dp2px(1);

        playAnimator = ValueAnimator.ofFloat(NEEDLE_ROTATION_PAUSE, NEEDLE_ROTATION_PLAY);
        playAnimator.setDuration(300);
        playAnimator.addUpdateListener(this);
        pauseAnimator = ValueAnimator.ofFloat(NEEDLE_ROTATION_PLAY, NEEDLE_ROTATION_PAUSE);
        pauseAnimator.setDuration(300);
        pauseAnimator.addUpdateListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initSize();
    }

    /**
     * 确定图片起始坐标与旋转中心坐标
     */
    private void initSize() {
        int discOffsetY = 0;
        discStartPoint.x = (getWidth() - discBitmap.getWidth()) / 2;
        discStartPoint.y = discOffsetY;
        coverStartPoint.x = (getWidth() - coverBitmap.getWidth()) / 2;
        coverStartPoint.y = discOffsetY + (discBitmap.getHeight() - coverBitmap.getHeight()) / 2;
        needleStartPoint.x = getWidth() / 2 - needleBitmap.getWidth() / 6;
        needleStartPoint.y = -needleBitmap.getWidth() / 6;
        discCenterPoint.x = getWidth() / 2;
        discCenterPoint.y = discBitmap.getHeight() / 2 + discOffsetY;
        coverCenterPoint.x = discCenterPoint.x;
        coverCenterPoint.y = discCenterPoint.y;
        needleCenterPoint.x = discCenterPoint.x;
        needleCenterPoint.y = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height ;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = discBitmap.getWidth();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = discBitmap.getHeight();
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 1.绘制顶部虚线
        mTopLine.setBounds(0, 0, getWidth(), mTopLineHeight);
        mTopLine.draw(canvas);
        // 2.绘制黑胶唱片外侧半透明边框
        mCoverBorder.setBounds(
                discStartPoint.x - mCoverBorderWidth,
                discStartPoint.y - mCoverBorderWidth,
                discStartPoint.x + discBitmap.getWidth() + mCoverBorderWidth,
                discStartPoint.y + discBitmap.getHeight() + mCoverBorderWidth);
        mCoverBorder.draw(canvas);
        // 3.绘制黑胶,setRotate和preTranslate顺序很重要
        discMatrix.setRotate(discAndCoverRotation, discCenterPoint.x, discCenterPoint.y);//设置旋转中心和旋转角度
        discMatrix.preTranslate(discStartPoint.x, discStartPoint.y);//设置图片起始坐标
        canvas.drawBitmap(discBitmap, discMatrix, null);
        // 4.绘制封面
        coverMatrix.setRotate(discAndCoverRotation, coverCenterPoint.x, coverCenterPoint.y);
        coverMatrix.preTranslate(coverStartPoint.x, coverStartPoint.y);
        canvas.drawBitmap(coverBitmap, coverMatrix, null);
        // 5.绘制指针
//        needleMatrix.setRotate(mNeedleRotation, needleCenterPoint.x, needleCenterPoint.y);
//        needleMatrix.preTranslate(needleStartPoint.x, needleStartPoint.y);
//        canvas.drawBitmap(needleBitmap, needleMatrix, null);
    }

    public void initNeedle(boolean isPlaying) {
        mNeedleRotation = isPlaying ? NEEDLE_ROTATION_PLAY : NEEDLE_ROTATION_PAUSE;
        invalidate();
    }

    public void setCoverBitmap(Bitmap bitmap) {
        coverBitmap = bitmap;
        discAndCoverRotation = 0.0f;
        invalidate();
    }

    public void start() {
        if (isPlaying) {
            return;
        }
        isPlaying = true;
        mHandler.post(rotationRunnable);
        playAnimator.start();
    }

    public void pause() {
        if (!isPlaying) {
            return;
        }
        isPlaying = false;
        mHandler.removeCallbacks(rotationRunnable);
        pauseAnimator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mNeedleRotation = (float) animation.getAnimatedValue();
        invalidate();
    }

    private Runnable rotationRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying) {
                discAndCoverRotation += DISC_ROTATION_INCREASE;
                if (discAndCoverRotation >= 360) {
                    discAndCoverRotation = 0;
                }
                invalidate();
            }
            mHandler.postDelayed(this, TIME_UPDATE);
        }
    };

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    private int dp2px(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
