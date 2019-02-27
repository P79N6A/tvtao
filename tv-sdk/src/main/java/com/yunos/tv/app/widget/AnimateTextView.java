package com.yunos.tv.app.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.yunos.tv.app.widget.Interpolator.Linear;
import com.yunos.tv.app.widget.Interpolator.TimeInterpolatorHelper;
import com.yunos.tv.app.widget.Interpolator.TweenInterpolator;
import com.yunos.tv.app.widget.focus.FocusTextView;

public class AnimateTextView extends FocusTextView {
    private static final String TAG = "AnimateTextView";

    private Drawable mBackground;

    private int mDuration = 250;
    private int mWidthStart = 0;
    private int mWidthTarget = 0;
    private float mBackgroundAlphaStart = 0f;
    private float mBackgroundAlphaTarget = 1f;
    private float mTextAlphaStart = 0f;
    private float mTextAlphaTarget = 0.8f;
    
    private boolean mIsShow = false;

    private TweenInterpolator mVisibleWidthInterpolator;
    private TweenInterpolator mBackgroundAlphaInterpolator;
    private TweenInterpolator mTextAlphaInterpolator;

    private TimeInterpolatorHelper mVisibleWidthTimeInterpolator;
    private TimeInterpolatorHelper mBackgroundAlphaTimeInterpolator;
    private TimeInterpolatorHelper mTextAlphaTimeInterpolator;

    public AnimateTextView(Context context) {
        super(context);
        init();
    }

    public AnimateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimateTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mVisibleWidthInterpolator = new Linear.EaseNone();
        mBackgroundAlphaInterpolator = new Linear.EaseNone();
        mTextAlphaInterpolator = new Linear.EaseNone();

        mVisibleWidthTimeInterpolator = new TimeInterpolatorHelper();
        mBackgroundAlphaTimeInterpolator = new TimeInterpolatorHelper();
        mTextAlphaTimeInterpolator = new TimeInterpolatorHelper();

        setUpInterpolation();
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
        mVisibleWidthInterpolator.setDuration(mDuration);
        mBackgroundAlphaInterpolator.setDuration(mDuration);
        mTextAlphaInterpolator.setDuration(mDuration);
    }

    private void setUpInterpolation() {
        mVisibleWidthInterpolator.setDuration(mDuration);
        mVisibleWidthInterpolator.setStartAndTarget(mWidthStart, mWidthTarget);
        mVisibleWidthTimeInterpolator.setInterpolator(mVisibleWidthInterpolator);

        mBackgroundAlphaInterpolator.setDuration(mDuration);
        mBackgroundAlphaInterpolator.setStartAndTarget(mBackgroundAlphaStart, mBackgroundAlphaTarget);
        mBackgroundAlphaTimeInterpolator.setInterpolator(mBackgroundAlphaInterpolator);

        mTextAlphaInterpolator.setDuration(mDuration);
        mTextAlphaInterpolator.setStartAndTarget(mTextAlphaStart, mTextAlphaTarget);
        mTextAlphaTimeInterpolator.setInterpolator(mTextAlphaInterpolator);
    }

    public TweenInterpolator getVisibleWidthInterpolator() {
        return mVisibleWidthInterpolator;
    }

    public void setVisibleWidthInterpolator(TweenInterpolator visibleWidthInterpolator) {
        this.mVisibleWidthInterpolator = visibleWidthInterpolator;
        setUpInterpolation();
    }

    public TweenInterpolator getBackgroundAlphaInterpolator() {
        return mBackgroundAlphaInterpolator;
    }

    public void setBackgroundAlphaInterpolator(TweenInterpolator backgroundAlphaInterpolator) {
        this.mBackgroundAlphaInterpolator = backgroundAlphaInterpolator;
        setUpInterpolation();
    }

    public TweenInterpolator getTextAlphaInterpolator() {
        return mTextAlphaInterpolator;
    }

    public void setTextAlphaInterpolator(TweenInterpolator textAlphaInterpolator) {
        this.mTextAlphaInterpolator = textAlphaInterpolator;
        setUpInterpolation();
    }

    public void setShowDelay(int delay) {
        mVisibleWidthTimeInterpolator.setForwardDelay(delay);
        mBackgroundAlphaTimeInterpolator.setForwardDelay(delay);
        mTextAlphaTimeInterpolator.setForwardDelay(delay);
    }

    public void setHideDelay(int delay) {
        mVisibleWidthTimeInterpolator.setReverseDelay(delay);
        mBackgroundAlphaTimeInterpolator.setReverseDelay(delay);
        mTextAlphaTimeInterpolator.setReverseDelay(delay);
    }

    public void setVisibleWidthStartAndTarget(int start, int target) {
        this.mWidthStart = start;
        this.mWidthTarget = target;
        mVisibleWidthInterpolator.setStartAndTarget(start, target);
    }

    public void setBackgroundAlphaStartAndTarget(float start, float target) {
        this.mBackgroundAlphaStart = start;
        this.mBackgroundAlphaTarget = target;
        mBackgroundAlphaInterpolator.setStartAndTarget(start, target);
    }

    public void setTextAlphaStartAndTarget(float start, float target) {
        this.mTextAlphaStart = start;
        this.mTextAlphaTarget = target;
        mTextAlphaInterpolator.setStartAndTarget(start, target);
    }

    @Override
    public Drawable getBackground() {
        return mBackground;
    }

//    @Override
    public void setBackground(Drawable background) {
        if (mBackground == background) {
            return;
        }
        mBackground = background;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Log.d(TAG, "ondraw:" + mStatus);
        int bgWidth = (int) mVisibleWidthTimeInterpolator.getStatusTarget();
        float bgAlpha =  mBackgroundAlphaTimeInterpolator.getStatusTarget();
        float textAlpha = mTextAlphaTimeInterpolator.getStatusTarget();
        
        if (mVisibleWidthTimeInterpolator.track()) {
            bgWidth = (int) mVisibleWidthTimeInterpolator.getCurrent();
        }

        if (mBackgroundAlphaTimeInterpolator.track()) {
            bgAlpha = mBackgroundAlphaTimeInterpolator.getCurrent();
        }

        if (mTextAlphaTimeInterpolator.track()) {
            textAlpha = mTextAlphaTimeInterpolator.getCurrent();
        }

        canvas.clipRect(0, 0, bgWidth, getBottom() - getTop());
        
        if (mBackground != null) {
            mBackground.setBounds(0, 0, bgWidth, getBottom() - getTop());
            mBackground.setAlpha((int) (bgAlpha * 255));
            mBackground.draw(canvas);
        }
//        Log.d(TAG,"bgWidth:" + bgWidth + ",bgAlpha:" + bgAlpha + ",textAlpha:" + textAlpha);
        ColorStateList colorList = getTextColors().withAlpha((int) (textAlpha * 255));
        setTextColor(colorList);
        
        super.onDraw(canvas);
        
        if (mVisibleWidthTimeInterpolator.isRunning() || mBackgroundAlphaTimeInterpolator.isRunning()
                || mTextAlphaTimeInterpolator.isRunning()) {
            invalidate();
        }
        
    }

    public boolean isShow(){
        return mIsShow;
    }
    
    /**
     * 显示
     */
    public void show() {
//        Log.d(TAG, "show");
        mIsShow = true;
        mVisibleWidthTimeInterpolator.start();
        mBackgroundAlphaTimeInterpolator.start();
        mTextAlphaTimeInterpolator.start();
        invalidate();
    }

    /**
     * 隐藏
     */
    public void hide() {
//        Log.d(TAG, "hide");
        mIsShow = false;
        mVisibleWidthTimeInterpolator.reverse();
        mBackgroundAlphaTimeInterpolator.reverse();
        mTextAlphaTimeInterpolator.reverse();
        invalidate();
    }
}
