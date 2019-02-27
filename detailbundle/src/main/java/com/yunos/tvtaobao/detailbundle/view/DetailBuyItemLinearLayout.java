package com.yunos.tvtaobao.detailbundle.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.FocusStateListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.core.common.AppDebug;

public class DetailBuyItemLinearLayout extends DetailBuyHasInnerView implements FocusListener {

    protected final String TAG = "DetailBuyItemLinearLayout";

    protected Params mParams = new Params(1.05f, 1.05f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());
    protected FocusRectParams mFocusRectparams = new FocusRectParams();
    boolean mFocusBackground = false;

    protected FocusStateListener mFocusStateListener = null;

    boolean mAimateWhenGainFocusFromLeft = true;
    boolean mAimateWhenGainFocusFromRight = true;
    boolean mAimateWhenGainFocusFromUp = true;
    boolean mAimateWhenGainFocusFromDown = true;

    boolean mIsAnimate = true;

    private Rect mClipFocusRect = new Rect(); // 默认focus框

    public DetailBuyItemLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DetailBuyItemLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetailBuyItemLinearLayout(Context context) {
        super(context);
    }

    public void setAnimateWhenGainFocus(boolean fromleft, boolean fromUp, boolean fromRight, boolean fromDown) {
        mAimateWhenGainFocusFromLeft = fromleft;
        mAimateWhenGainFocusFromUp = fromUp;
        mAimateWhenGainFocusFromRight = fromRight;
        mAimateWhenGainFocusFromDown = fromDown;
    }

    public void setFocusBackground(boolean back) {
        mFocusBackground = back;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        AppDebug.i(TAG, TAG + ".onFocusChanged.gainFocus = " + gainFocus + ".direction = " + direction
                + ", previouslyFocusedRect = " + previouslyFocusedRect);
        mIsAnimate = checkAnimate(direction);
    }

    private boolean checkAnimate(int direction) {
        switch (direction) {
            case View.FOCUS_LEFT:
                return mAimateWhenGainFocusFromRight ? true : false;
            case View.FOCUS_UP:
                return mAimateWhenGainFocusFromDown ? true : false;
            case View.FOCUS_RIGHT:
                return mAimateWhenGainFocusFromLeft ? true : false;
            case View.FOCUS_DOWN:
                return mAimateWhenGainFocusFromUp ? true : false;
        }

        return true;
    }

    @Override
    public boolean canDraw() {
        return true;
    }

    @Override
    public boolean isAnimate() {
        return mIsAnimate;
    }

    @Override
    public boolean isScale() {
        return true;
    }

    @Override
    public Params getParams() {
        if (mParams == null) {
            throw new IllegalArgumentException("The params is null, you must call setScaleParams before it's running");
        }

        return mParams;
    }

    @Override
    public ItemListener getItem() {
        return this;
    }

    @Override
    public boolean isScrolling() {
        return false;
    }

    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                return true;

            default:
                break;
        }
        return false;
    }

    @Override
    public void onFocusStart() {
        if (mFocusStateListener != null) {
            mFocusStateListener.onFocusStart(this, (View) this.getParent());
        }
    }

    @Override
    public void onFocusFinished() {
        if (mFocusStateListener != null) {
            mFocusStateListener.onFocusFinished(this, (View) this.getParent());
        }
    }

    @Override
    public boolean isFocusBackground() {
        return mFocusBackground;
    }

    @Override
    public void drawBeforeFocus(Canvas canvas) {

    }

    @Override
    public void drawAfterFocus(Canvas canvas) {

    }

    public void setOnFocusStateListener(FocusStateListener l) {
        mFocusStateListener = l;
    }

    /*
     * (non-Javadoc)
     * @see com.yunos.tv.app.widget.focus.listener.FocusListener#getClipFocusRect()
     */
    @Override
    public Rect getClipFocusRect() {
        AppDebug.i(TAG, TAG + ", getClipFocusRect");
        if (mClipFocusRect != null) {
            return mClipFocusRect;
        }
        return new Rect();
    }

}
