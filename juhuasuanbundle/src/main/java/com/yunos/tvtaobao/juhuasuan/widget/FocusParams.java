package com.yunos.tvtaobao.juhuasuan.widget;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;


public class FocusParams {
    

    public int mFrameRate = FocusedBasePositionManager.DEFAULT_FRAME_RATE;
    private int mFocusFrameRate = 2;
    private int mScaleFrameRate = 2;

    private float mScaleXValue = 1.0f;
    private float mScaleYValue = 1.0f;

    private int mScaledMode = FocusedBasePositionManager.SCALED_FIXED_X;
    private int mFixedScaledX = 30;
    private int mFixedScaledY = 30;

    private int mode = FocusedBasePositionManager.FOCUS_ASYNC_DRAW;

    private Rect mSelectedPaddingRect = new Rect();
    private Rect mSelectedPaddingRectShadow = new Rect();
    private Drawable mSelectedDrawable = null;
    private Drawable mSelectedDrawableShadow = null;

    private Rect mManualSelectedPaddingRect = new Rect();

    private boolean mIsScale = true;

    private boolean mIsBackground = false;

    public FocusParams() {

    }

    /**
     * 设置缩放模式
     * 
     * @param mode
     */
    public void setScaleMode(int mode) {
        this.mScaledMode = mode;
    }

    public int getScaleMode() {
        return this.mScaledMode;
    }

    public void setScale(boolean isScale) {
        this.mIsScale = isScale;
    }

    public boolean getScale() {
        return this.mIsScale;
    }

    /**
     * 设置移动时边框的资源id
     * 
     *            资源id
     */
    public void setFocusDrawable(Drawable focusDrawable) {
        mSelectedDrawable = focusDrawable;
        mSelectedPaddingRect = new Rect();
        mSelectedDrawable.getPadding(mSelectedPaddingRect);
    }

    public Drawable getFocusDrawable() {
        return this.mSelectedDrawable;
    }

    /**
     * 设置移动结束时边框的资源id
     * 
     *            资源id
     */
    public void setFocusShadowDrawable(Drawable focusShadowDrawable) {
        mSelectedDrawableShadow = focusShadowDrawable;
        mSelectedPaddingRectShadow = new Rect();
        mSelectedDrawableShadow.getPadding(mSelectedPaddingRectShadow);
    }

    public Drawable getFocusShadowDrawable() {
        return this.mSelectedDrawableShadow;
    }

    /**
     * 设置选中项缩放比例
     * 
     * @param scaleXValue
     *            横向缩放比例
     * @param scaleYValue
     *            纵向缩放比例
     */
    public void setItemScaleValue(float scaleXValue, float scaleYValue) {
        mScaleXValue = scaleXValue;
        mScaleYValue = scaleYValue;
    }

    /**
     * 设置横向放大固定像素,放大后的元素比放大前元素增加的宽度.如:设置改值为10,则左右各延伸5像素
     * 
     * @param x
     *            放大的像素值
     */
    public void setItemScaleFixedX(int x) {
        mFixedScaledX = x;
    }

    public int getItemScaleFixedX() {
        return this.mFixedScaledX;
    }

    /**
     * 设置纵向放大固定像素,放大后的元素比放大前元素增加的宽度.如:设置改值为10,则上下各延伸5像素
     * 
     * @param y
     *            放大的像素值
     */
    public void setItemScaleFixedY(int y) {
        mFixedScaledY = y;
    }

    public int getItemScaleFixedY() {
        return mFixedScaledY;
    }

    public float getItemScaleXValue() {
        return this.mScaleXValue;
    }

    public float getItemScaleYValue() {
        return this.mScaleYValue;
    }

    public void setBackground(boolean isBackground) {
        this.mIsBackground = isBackground;
    }

    public boolean isBackground() {
        return this.mIsBackground;
    }

    /**
     * 设置焦点框移动状态,可查看 {@link #FOCUS_SYNC_DRAW}, {@link #FOCUS_ASYNC_DRAW},
     * {@link #FOCUS_STATIC_DRAW}
     * 
     * @param mode
     */
    public void setFocusMode(int mode) {
        this.mode = mode;
    }

    public int getFocusMode() {
        return this.mode;
    }

    public void setFrameRate(int rate) {
        this.mFrameRate = rate;
        if (getFocusMode() == FocusedBasePositionManager.FOCUS_ASYNC_DRAW) {
            if (rate % 2 == 0) {
                this.mScaleFrameRate = rate / 2;
                this.mFocusFrameRate = rate / 2;
            } else {
                this.mScaleFrameRate = rate / 2;
                this.mFocusFrameRate = rate / 2 + 1;
            }
        } else if (getFocusMode() == FocusedBasePositionManager.FOCUS_STATIC_DRAW) {
            this.mScaleFrameRate = rate;
            this.mFocusFrameRate = 0;
        } else if (getFocusMode() == FocusedBasePositionManager.FOCUS_SYNC_DRAW) {
            this.mScaleFrameRate = rate;
            this.mFocusFrameRate = 0;
        }
    }

    public void setFrameRate(int scaleFrameRate, int focusFrameRate) {
        this.mFrameRate = scaleFrameRate + focusFrameRate;
        this.mScaleFrameRate = scaleFrameRate;
        this.mFocusFrameRate = focusFrameRate;
    }

    public int getFrameRate() {
        return this.mFrameRate;
    }

    public int getScaleFrameRate() {
        return this.mScaleFrameRate;
    }

    public int getFocusFrameRate() {
        return this.mFocusFrameRate;
    }

    // 焦点狂向4个方向扩展的大小
    public void setManualPadding(int left, int top, int right, int bottom) {
        this.mManualSelectedPaddingRect.left = left;
        this.mManualSelectedPaddingRect.right = right;
        this.mManualSelectedPaddingRect.top = top;
        this.mManualSelectedPaddingRect.bottom = bottom;
    }

    public int getManualPaddingLeft() {
        return this.mManualSelectedPaddingRect.left;
    }

    public int getManualPaddingRight() {
        return this.mManualSelectedPaddingRect.right;
    }

    public int getManualPaddingTop() {
        return this.mManualSelectedPaddingRect.top;
    }

    public int getManualPaddingBottom() {
        return this.mManualSelectedPaddingRect.bottom;
    }

    public Rect getSelectedPadding() {
        return this.mSelectedPaddingRect;
    }

    public Rect getSelectedShadowPadding() {
        return this.mSelectedPaddingRectShadow;
    }

} 
