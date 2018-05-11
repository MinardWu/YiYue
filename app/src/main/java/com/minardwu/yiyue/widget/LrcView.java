package com.minardwu.yiyue.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Looper;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;


import com.minardwu.yiyue.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 歌词
 */
public class LrcView extends View {

    private static final long ADJUST_DURATION = 100;
    private static final long TIMELINE_KEEP_TIME = 2 * DateUtils.SECOND_IN_MILLIS;

    private List<LrcEntry> lrcEntries = new ArrayList<>();
    private TextPaint lrcTextPaint = new TextPaint();
    private TextPaint timeTextPaint = new TextPaint();
    private Paint.FontMetrics timeFontMetrics;
    private Drawable playDrawable;
    private float dividerHeight;
    private long animationDuration;
    private int normalTextColor;
    private int currentTextColor;
    private int timeTextColor;
    private int timelineColor;
    private int timelineTextColor;
    private int drawableWidth;
    private int timeTextWidth;
    private String defaultLabel;
    private float lrcPadding;
    private OnPlayClickListener mOnPlayClickListener;
    private ValueAnimator valueAnimator;
    private GestureDetector gestureDetector;
    private Scroller mScroller;
    private float offset;
    private float lrcTextSize;
    private float timelineHeight;
    private float timeTextSize;
    private int defDuration;
    private int currentLine;
    private Object flag;
    private boolean isShowTimeline;
    private boolean isTouching;
    private boolean isFling;

    public interface OnPlayClickListener {
        /**
         * 播放按钮被点击，应该跳转到指定播放位置
         * @return 是否成功消费该事件，如果成功消费，则会更新UI
         */
        boolean onPlayClick(long time);
    }

    public LrcView(Context context) {
        this(context, null);
    }

    public LrcView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.LrcView);
        lrcPadding = ta.getDimension(R.styleable.LrcView_lrcPadding, 0);
        lrcTextSize = ta.getDimension(R.styleable.LrcView_lrcTextSize, getResources().getDimension(R.dimen.lrc_text_size));
        dividerHeight = ta.getDimension(R.styleable.LrcView_lrcDividerHeight, getResources().getDimension(R.dimen.lrc_divider_height));
        normalTextColor = ta.getColor(R.styleable.LrcView_lrcNormalTextColor, getResources().getColor(R.color.lrc_normal_text_color));
        currentTextColor = ta.getColor(R.styleable.LrcView_lrcCurrentTextColor, getResources().getColor(R.color.lrc_current_text_color));

        defDuration = getResources().getInteger(R.integer.lrc_animation_duration);
        animationDuration = ta.getInt(R.styleable.LrcView_lrcAnimationDuration, defDuration);
        animationDuration = (animationDuration < 0) ? defDuration : animationDuration;

        defaultLabel = TextUtils.isEmpty(ta.getString(R.styleable.LrcView_lrcLabel))
                ? getContext().getString(R.string.lrc_label)
                : ta.getString(R.styleable.LrcView_lrcLabel);

        playDrawable = ta.getDrawable(R.styleable.LrcView_lrcPlayDrawable);
        playDrawable = (playDrawable == null) ? getResources().getDrawable(R.drawable.ic_lrc_play) : playDrawable;

        timelineColor = ta.getColor(R.styleable.LrcView_lrcTimelineColor, getResources().getColor(R.color.lrc_timeline_color));
        timelineHeight = ta.getDimension(R.styleable.LrcView_lrcTimelineHeight, getResources().getDimension(R.dimen.lrc_timeline_height));
        timelineTextColor = ta.getColor(R.styleable.LrcView_lrcTimelineTextColor, getResources().getColor(R.color.lrc_timeline_text_color));
        timeTextColor = ta.getColor(R.styleable.LrcView_lrcTimeTextColor, getResources().getColor(R.color.lrc_time_text_color));
        timeTextSize = ta.getDimension(R.styleable.LrcView_lrcTimeTextSize, getResources().getDimension(R.dimen.lrc_time_text_size));
        ta.recycle();

        drawableWidth = (int) getResources().getDimension(R.dimen.lrc_drawable_width);
        timeTextWidth = (int) getResources().getDimension(R.dimen.lrc_time_width);

        lrcTextPaint.setAntiAlias(true);
        lrcTextPaint.setTextSize(lrcTextSize);
        lrcTextPaint.setTextAlign(Paint.Align.LEFT);

        timeTextPaint.setAntiAlias(true);
        timeTextPaint.setTextSize(timeTextSize);
        timeTextPaint.setTextAlign(Paint.Align.CENTER);
        timeTextPaint.setStrokeWidth(timelineHeight);
        timeTextPaint.setStrokeCap(Paint.Cap.ROUND);
        timeFontMetrics = timeTextPaint.getFontMetrics();

        gestureDetector = new GestureDetector(getContext(), mSimpleOnGestureListener);
        gestureDetector.setIsLongpressEnabled(false);
        mScroller = new Scroller(getContext());
    }

    public void setNormalColor(int normalColor) {
        normalTextColor = normalColor;
        postInvalidate();
    }

    public void setCurrentColor(int currentColor) {
        currentTextColor = currentColor;
        postInvalidate();
    }

    public void setTimelineTextColor(int timelineTextColor) {
        this.timelineTextColor = timelineTextColor;
        postInvalidate();
    }

    public void setTimelineColor(int timelineColor) {
        this.timelineColor = timelineColor;
        postInvalidate();
    }

    public void setTimeTextColor(int timeTextColor) {
        this.timeTextColor = timeTextColor;
        postInvalidate();
    }

    /**
     * 设置播放按钮点击监听器
     * @param onPlayClickListener 如果为非 null ，则激活歌词拖动功能，否则将将禁用歌词拖动功能
     */
    public void setOnPlayClickListener(OnPlayClickListener onPlayClickListener) {
        mOnPlayClickListener = onPlayClickListener;
    }

    /**
     * 设置歌词为空时屏幕中央显示的文字，如“暂无歌词”
     */
    public void setLabel(final String label) {
        runOnUi(new Runnable() {
            @Override
            public void run() {
                reset();
                defaultLabel = label;
                invalidate();
            }
        });
    }

    /**
     * 加载歌词文件
     * @param lrcFile 歌词文件
     */
    public void loadLrc(final File lrcFile) {
        reset();
        setFlag(lrcFile);
        new AsyncTask<File, Integer, List<LrcEntry>>() {
            @Override
            protected List<LrcEntry> doInBackground(File... params) {
                return LrcEntry.parseLrc(params[0]);
            }

            @Override
            protected void onPostExecute(List<LrcEntry> lrcEntries) {
                if (getFlag() == lrcFile) {
                    onLrcLoaded(lrcEntries);
                    setFlag(null);
                }
            }
        }.execute(lrcFile);
    }

    /**
     * 加载歌词文件
     * @param lrcText 歌词文本
     */
    public void loadLrc(final String lrcText) {
        reset();
        setFlag(lrcText);
        new AsyncTask<String, Integer, List<LrcEntry>>() {
            @Override
            protected List<LrcEntry> doInBackground(String... params) {
                return LrcEntry.parseLrc(params[0]);
            }

            @Override
            protected void onPostExecute(List<LrcEntry> lrcEntries) {
                if (getFlag() == lrcText) {
                    onLrcLoaded(lrcEntries);
                    setFlag(null);
                }
            }
        }.execute(lrcText);
    }

    private void onLrcLoaded(List<LrcEntry> entryList) {
        if (entryList != null && !entryList.isEmpty()) {
            lrcEntries.addAll(entryList);
        }
        initEntryList();
        invalidate();
    }

    private void initEntryList() {
        if (!hasLrc() || getWidth() == 0) {
            return;
        }
        Collections.sort(lrcEntries);
        for (LrcEntry lrcEntry : lrcEntries) {
            lrcEntry.init(lrcTextPaint, (int) getLrcWidth());
        }
        offset = getHeight() / 2;
    }

    /**
     * 歌词是否有效
     * @return 如果歌词有效返回true
     */
    public boolean hasLrc() {
        return !lrcEntries.isEmpty();
    }

    /**
     * 刷新歌词
     * @param time 当前播放时间
     */
    public void updateTime(final long time) {
        runOnUi(new Runnable() {
            @Override
            public void run() {
                if (!hasLrc()) {
                    return;
                }
                int line = findShowLine(time);
                if (line != currentLine) {
                    currentLine = line;
                    if (!isShowTimeline) {
                        scrollTo(line);
                    } else {
                        invalidate();
                    }
                }
            }
        });
    }

    /**
     * 将歌词滚动到指定时间
     * @param time 指定的时间
     * @deprecated 请使用 {@link #updateTime(long)} 代替
     */
    @Deprecated
    public void onDrag(long time) {
        updateTime(time);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            initEntryList();
            int l = (timeTextWidth - drawableWidth) / 2;
            int t = getHeight() / 2 - drawableWidth / 2;
            int r = l + drawableWidth;
            int b = t + drawableWidth;
            playDrawable.setBounds(l, t, r, b);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 无歌词文件，则只绘制中间的label
        int centerY = getHeight() / 2;
        if (!hasLrc()) {
            lrcTextPaint.setColor(currentTextColor);
            @SuppressLint("DrawAllocation")
            StaticLayout staticLayout = new StaticLayout(defaultLabel, lrcTextPaint, (int) getLrcWidth(),
                    Layout.Alignment.ALIGN_CENTER, 1f, 0f, false);
            drawText(canvas, staticLayout, centerY);
            return;
        }

        int centerLine = getCenterLine();
        //如果要显示时间线则开始绘制
        if (isShowTimeline) {
            playDrawable.draw(canvas);
            timeTextPaint.setColor(timelineColor);
            canvas.drawLine(timeTextWidth, centerY, getWidth() - timeTextWidth, centerY, timeTextPaint);
            timeTextPaint.setColor(timeTextColor);
            String timeText = LrcUtils.formatTime(lrcEntries.get(centerLine).getTime());
            float timeX = getWidth() - timeTextWidth / 2;
            float timeY = centerY - (timeFontMetrics.descent + timeFontMetrics.ascent) / 2;
            canvas.drawText(timeText, timeX, timeY, timeTextPaint);
        }

        //canvas绘制每一行歌词
        canvas.translate(0, offset);
        float y = 0;
        for (int i = 0; i < lrcEntries.size(); i++) {
            if (i > 0) {
                y += (lrcEntries.get(i - 1).getHeight() + lrcEntries.get(i).getHeight()) / 2 + dividerHeight;
            }
            if (i == currentLine) {
                lrcTextPaint.setColor(currentTextColor);
            } else if (isShowTimeline && i == centerLine) {
                lrcTextPaint.setColor(timelineTextColor);
            } else {
                lrcTextPaint.setColor(normalTextColor);
            }
            drawText(canvas, lrcEntries.get(i).getStaticLayout(), y);
        }
    }

    /**
     * 画一行歌词
     * @param y 每一行歌词的中心Y坐标
     */
    private void drawText(Canvas canvas, StaticLayout staticLayout, float y) {
        canvas.save();
        canvas.translate(lrcPadding, y - staticLayout.getHeight() / 2);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            isTouching = false;
            if (hasLrc() && !isFling) {
                adjustCenter();
                postDelayed(hideTimelineRunnable, TIMELINE_KEEP_TIME);
            }
        }
        return gestureDetector.onTouchEvent(event);
    }

    private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            if (hasLrc() && mOnPlayClickListener != null) {
                mScroller.forceFinished(true);
                removeCallbacks(hideTimelineRunnable);
                isTouching = true;
                invalidate();
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(Math.abs(distanceY)>50){
                isShowTimeline = true;
            }
            if (hasLrc()) {
                offset += -distanceY;
                offset = Math.min(offset, getOffset(0));
                offset = Math.max(offset, getOffset(lrcEntries.size() - 1));
                invalidate();
                return true;
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (hasLrc()) {
                mScroller.fling(0, (int) offset, 0, (int) velocityY, 0, 0, (int) getOffset(lrcEntries.size() - 1), (int) getOffset(0));
                isFling = true;
                return true;
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (hasLrc() && isShowTimeline && playDrawable.getBounds().contains((int) e.getX(), (int) e.getY())) {
                int centerLine = getCenterLine();
                long centerLineTime = lrcEntries.get(centerLine).getTime();
                // onPlayClick消费了才更新UI
                if (mOnPlayClickListener != null && mOnPlayClickListener.onPlayClick(centerLineTime)) {
                    isShowTimeline = false;
                    removeCallbacks(hideTimelineRunnable);
                    currentLine = centerLine;
                    invalidate();
                    return true;
                }
            }else {
                post(hideTimelineRunnable);
                if(listener!=null){
                    listener.onClick(LrcView.this);
                }
            }
            return super.onSingleTapConfirmed(e);
        }
    };

    public Rect getPlayDarwableBounds(){
        return playDrawable.getBounds();
    }

    private Runnable hideTimelineRunnable = new Runnable() {
        @Override
        public void run() {
            if (hasLrc() && isShowTimeline) {
                isShowTimeline = false;
                scrollTo(currentLine);
            }
        }
    };

    /**
     * 重写View的computeScroll()方法
     */
    @Override
    public void computeScroll() {
        //利用Scroller.computeScrollOffset()判断了滑动是否结束，返回true表示滑动还没有结束则继续重绘view,在重绘过程中又会调用View自身的computeScroll()方法
        if (mScroller.computeScrollOffset()) {
            offset = mScroller.getCurrY();
            invalidate();
        }

        if (isFling && mScroller.isFinished()) {
            isFling = false;
            if (hasLrc() && !isTouching) {
                adjustCenter();
                postDelayed(hideTimelineRunnable, TIMELINE_KEEP_TIME);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(hideTimelineRunnable);
        super.onDetachedFromWindow();
    }

    public void reset() {
        stopAnimation();
        mScroller.forceFinished(true);
        isShowTimeline = false;
        isTouching = false;
        isFling = false;
        removeCallbacks(hideTimelineRunnable);
        lrcEntries.clear();
        offset = 0;
        currentLine = 0;
        invalidate();
    }

    /**
     * 滚动到某一行
     */
    private void scrollTo(int line) {
        scrollTo(line, animationDuration);
    }

    /**
     * 将中心行微调至正中心
     */
    private void adjustCenter() {
        scrollTo(getCenterLine(), ADJUST_DURATION);
    }

    private void scrollTo(int line, long duration) {
        float offset = getOffset(line);
        stopAnimation();

        valueAnimator = ValueAnimator.ofFloat(this.offset, offset);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LrcView.this.offset = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    private void stopAnimation() {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.end();
        }
    }

    /**
     * 二分法查找当前时间应该显示的行数（最后一个 <= time 的行数）
     */
    private int findShowLine(long time) {
        int left = 0;
        int right = lrcEntries.size();
        while (left <= right) {
            int middle = (left + right) / 2;
            long middleTime = lrcEntries.get(middle).getTime();

            if (time < middleTime) {
                right = middle - 1;
            } else {
                if (middle + 1 >= lrcEntries.size() || time < lrcEntries.get(middle + 1).getTime()) {
                    return middle;
                }

                left = middle + 1;
            }
        }

        return 0;
    }

    private int getCenterLine() {
        int centerLine = 0;
        float minDistance = Float.MAX_VALUE;
        for (int i = 0; i < lrcEntries.size(); i++) {
            if (Math.abs(offset - getOffset(i)) < minDistance) {
                minDistance = Math.abs(offset - getOffset(i));
                centerLine = i;
            }
        }
        return centerLine;
    }

    private float getOffset(int line) {
        if (lrcEntries.get(line).getOffset() == Float.MIN_VALUE) {
            float offset = getHeight() / 2;
            for (int i = 1; i <= line; i++) {
                offset -= (lrcEntries.get(i - 1).getHeight() + lrcEntries.get(i).getHeight()) / 2 + dividerHeight;
            }
            lrcEntries.get(line).setOffset(offset);
        }

        return lrcEntries.get(line).getOffset();
    }

    private float getLrcWidth() {
        return getWidth() - lrcPadding * 2;
    }

    private void runOnUi(Runnable r) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            r.run();
        } else {
            post(r);
        }
    }

    private Object getFlag() {
        return flag;
    }

    private void setFlag(Object flag) {
        this.flag = flag;
    }

    public interface OnLrcViewForOutsideUseClickListener{
        void onClick(View view);
    }

    private OnLrcViewForOutsideUseClickListener listener;

    public void setOnLrcViewForOutsideUseClickListener(OnLrcViewForOutsideUseClickListener onLrcViewForOutsideUseClickListener){
        this.listener = onLrcViewForOutsideUseClickListener;
    }
}
