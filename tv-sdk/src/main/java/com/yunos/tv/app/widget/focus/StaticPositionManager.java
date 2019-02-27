package com.yunos.tv.app.widget.focus;


import android.graphics.Canvas;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.PositionListener;
import com.yunos.tv.app.widget.focus.params.AlphaParams;
import com.yunos.tv.app.widget.focus.params.ScaleParams;

public class StaticPositionManager extends PositionManager {

    static String TAG = "StaticPositionManager";

    private boolean mReset = false;

    public StaticPositionManager(int focusMode, PositionListener l) {
        super(focusMode, l);
    }

    @Override
    public void draw(Canvas canvas) {
        if (DEBUG) {
            Log.i(TAG, TAG + ".draw.mStart = " + mStart);
        }

        preDrawOut(canvas);
        super.draw(canvas);
        if (DEBUG) {
            Log.i(TAG, TAG + ".draw.mFrame = " + mFrame + ".mScaleFrame = " + mScaleFrame + ".mFocusFrame = "
                    + mFocusFrame + ".mAlphaFrame = " + mAlphaFrame + ".mFocusFrameRate = " + mFocusFrameRate
                    + ".mScaleFrameRate = " + mScaleFrameRate + ".mAlphaFrameRate = " + mAlphaFrameRate);
        }

        if (!mStart) {
            return;
        }

        if (!mFocus.canDraw()) {
            //drawFocus(canvas);
            postDrawOut(canvas);
            mListener.postInvalidateDelayed(30);
            return;
        }

        ItemListener item = mFocus.getItem();
        if (item == null) {
            return;
        }

        processReset();
        drawBeforeFocus(item, canvas);
        boolean isInvalidate = false;
        if (!isFinished()) {
            if (mFrame == 1) {
                mFocus.onFocusStart();

            }
            if (!isAlphaFinished()) {
                //drawFadeOutAlpha(canvas);
                drawAlpha(item);
                mAlphaFrame++;
            }
            if (!isScaleFinished()) {
                if (DEBUG) {
                    Log.i(TAG, TAG + ".test.isScale = " + item.isScale());
                }

                if (item.isScale()) {
                    drawScale(canvas, item);
                    drawStaticFocus(canvas, item, item.getScaleX(), item.getScaleY());
                } else {
                    drawStaticFocus(canvas, item);
                }

                mScaleFrame++;
            } else if (mFocus.isScrolling()) {
                drawStaticFocus(canvas, item);
                isInvalidate = true;
            } else if (mForceDrawFocus) {
                drawStaticFocus(canvas, item);
                isInvalidate = true;
                mForceDrawFocus = false;
            } else {
                drawFocus(canvas);
            }

            mFrame++;
            isInvalidate = true;
            if (mFrame == getTotalFrame()) {
                mFocus.onFocusFinished();
            }

        } else if (mFocus.isScrolling()) {
            drawStaticFocus(canvas, item);
            isInvalidate = true;
        } else if (mForceDrawFocus) {
            drawStaticFocus(canvas, item);
            isInvalidate = true;
            mForceDrawFocus = false;
        } else {
            drawFocus(canvas);
        }
        if (mSelector != null)
            if (!mPause && (isInvalidate || mSelector.isDynamicFocus())) {
                mListener.invalidate();
            }

        if (isFinished()) {
            addCurNode(item);// 动画结束时保存当前选择的节点
            resetSelector();
        } else {
            mCurNodeAdded = false;
        }
        drawAfterFocus(item, canvas);
        postDrawOut(canvas);
        mLastItem = item;
    }

    public void resetSelector() {
        if (mConvertSelector != null) {
            mSelector = mConvertSelector;
            setConvertSelector(null);
        }
    }

    private void processReset() {
        if (DEBUG) {
            Log.i(TAG, TAG + ".processReset.mReset = " + mReset);
        }

        if (mReset) {
            ItemListener item = mFocus.getItem();
            if (item == null) {
                Log.e(TAG, "processReset: item is null! mFocus:" + mFocus);
                return;
            }
            removeNode(item);
            //removeScaleNode(item);
            mReset = false;
        }
    }

    void drawAlpha(ItemListener item) {
        AlphaParams alphaParams = mFocus.getParams().getAlphaParams();
        float dstAlpha = alphaParams.getFromAlpha();
        float diffAlpha = alphaParams.getToAlpha() - alphaParams.getFromAlpha();
        float coef = (float) mFrame / alphaParams.getAlphaFrameRate();

        Interpolator alphaInterpolator = mFocus.getParams().getAlphaParams().getAlphaInteroplator();
        if (alphaInterpolator == null) {
            alphaInterpolator = new LinearInterpolator();
        }

        coef = alphaInterpolator.getInterpolation(coef);

        dstAlpha = dstAlpha + diffAlpha * coef;

        if (mLastItem == item) {
            dstAlpha = alphaParams.getToAlpha();
        }

        if (mConvertSelector != null) {
            mConvertSelector.setAlpha(dstAlpha);
            if(mSelector!=null) {
                mSelector.setAlpha(0.0f);
            }
        } else {
            if(mSelector!=null) {
                mSelector.setAlpha(dstAlpha);
            }
        }
    }

    void drawScale(Canvas canvas, ItemListener item) {
        ScaleParams scaleParams = mFocus.getParams().getScaleParams();
        float dstScaleXValue = 1.0f;
        float dstScaleYValue = 1.0f;

        float itemDiffScaleXValue = scaleParams.getScaleX() - 1.0f;
        float itemDiffScaleYValue = scaleParams.getScaleY() - 1.0f;

        float coef = (float) mFrame / scaleParams.getScaleFrameRate();
        Interpolator scaleInterpolator = mFocus.getParams().getScaleParams().getScaleInterpolator();

        if (scaleInterpolator == null) {
            scaleInterpolator = new LinearInterpolator();
        }

        coef = scaleInterpolator.getInterpolation(coef);
        dstScaleXValue = 1.0f + itemDiffScaleXValue * coef;
        dstScaleYValue = 1.0f + itemDiffScaleYValue * coef;

        if (mLastItem == item) {
            dstScaleXValue = scaleParams.getScaleX();
            dstScaleYValue = scaleParams.getScaleY();
        }

        item.setScaleX(dstScaleXValue);
        item.setScaleY(dstScaleYValue);
    }

    public int getTotalFrame() {
        return Math.max(mScaleFrameRate, mAlphaFrameRate);
    }

    @Override
    public boolean isFinished() {
        return isScaleFinished() && isAlphaFinished();
    }

    public boolean isScaleFinished() {
        return mFrame > mScaleFrameRate;
    }

    public boolean isAlphaFinished() {
        return mFrame > mAlphaFrameRate;
    }

    @Override
    public void reset() {
        super.reset();
        mReset = true;
    }
}
