package com.yunos.tv.app.widget.focus;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.yunos.tv.app.widget.focus.listener.DrawListener;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.PositionListener;

public class FocusPositionManager extends FrameLayout implements PositionListener {

    protected static String TAG = "FocusPositionManager";
    protected static boolean DEBUG = false;

    public FocusPositionManager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public FocusPositionManager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FocusPositionManager(Context context) {
        super(context);
        init();
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mPositionManager != null) {
            mPositionManager.release();
        }
    }

    View mFocused;

    public View getFocused() {
        return mFocused;
    }

    PositionManager mPositionManager;
    boolean mLayouted = false;
    FocusRequestRunnable mFocusRequestRunnable = new FocusRequestRunnable();

    private void init() {
        mPositionManager = PositionManager.createPositionManager(PositionManager.FOCUS_STATIC_DRAW, this);
    }

    public void setFocusMode(int focusMode) {
        if (mPositionManager != null) {
            mPositionManager.release();
            mPositionManager = null;
        }
        mPositionManager = PositionManager.createPositionManager(focusMode, this);
    }

    public void setSelector(DrawListener selector) {
        mPositionManager.setSelector(selector);
    }

    public void setConvertSelector(DrawListener convertSelector) {
        mPositionManager.setConvertSelector(convertSelector);
    }

    long time = 0;
    int f = 0;

    public void requestFocus(View v, int direction) {
        if (v == null) {
            throw new NullPointerException();
        }

        if (!(v instanceof FocusListener)) {
            throw new IllegalArgumentException("The view you request focus must extend from FocusListener");
        }

        Log.d(TAG, TAG + ".requestFocus v = " + v + ", direction = " + direction);
        View rootFocus = findRootFocus(v);

        Rect previouslyFocusedRect = getFocusedRect(mFocused, rootFocus);

        Log.d(TAG, TAG + ".requestFocus rootFocus = " + rootFocus + ", mFocused = " + mFocused
                + ", previouslyFocusedRect = " + previouslyFocusedRect);
        if (!rootFocus.hasFocus()) {
            rootFocus.requestFocus(direction, previouslyFocusedRect);
        }

        Log.i(TAG, TAG + ".requestFocus.mPositionManager.stop()");
        mPositionManager.stop();

        if (mFocused != rootFocus) {
            mFocused = rootFocus;
            mPositionManager.reset((FocusListener) mFocused);
        } else {
            mPositionManager.reset();
        }
    }

    private View findRootFocus(View child) {
        ViewParent temp = child.getParent();
        View rootFocus = child;
        try{
            while (temp != this) {
                if (temp instanceof FocusListener) {
                    rootFocus = (View) temp;
                }

                temp = temp.getParent();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return rootFocus;
    }

    public void forceDrawFocus() {
        if (mPositionManager != null) {
            mPositionManager.forceDrawFocus();
        }
    }

    public void focusShow() {
        Log.d(TAG, "focusShow");
        focusStart();
        invalidate();
    }

    public void focusHide() {
        Log.d(TAG, "focusHide");
        focusStop();
        invalidate();
    }

    public void focusPause() {
        Log.d(TAG, "focusPause");
        mPositionManager.focusPause();
    }

    public void focusStop() {
        Log.d(TAG, "focusStop");
        mPositionManager.focusStop();
        //Log.i(TAG, TAG + ".focusStop.findFocusChild = " + findFocusChild(this));
    }

    public void focusStart() {
        Log.d(TAG, "focusStart");
        mPositionManager.focusStart();
    }

    /**
     * 是否可以focus
     *
     * @return
     */
    public boolean IsFocusStarted() {
        return mPositionManager.isFocusStarted();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mLayouted) {
            if (mFocused == null) {
                super.dispatchDraw(canvas);
                return;
            }
        } else {
            postInvalidateDelayed(30);
            super.dispatchDraw(canvas);
            return;
        }

        drawBackFocus(canvas);
        super.dispatchDraw(canvas);
        drawForeFocus(canvas);

        if (DEBUG) {
            if (System.currentTimeMillis() - time >= 1000) {
                Log.d(TAG, "dispatchDraw f = " + f);
                time = System.currentTimeMillis();
                f = 0;
            }

            f++;
        }
    }

    private void drawBackFocus(Canvas canvas) {
        if (mPositionManager.isFocusBackground()) {
            drawFocus(canvas);
        }
    }

    private void drawForeFocus(Canvas canvas) {
        if (!mPositionManager.isFocusBackground()) {
            drawFocus(canvas);
        }
    }

    private void drawFocus(Canvas canvas) {
        if (DEBUG) {
            Log.i(TAG, TAG + ".drawFocus.mFocused = " + mFocused + ".mLayouted = " + mLayouted);
        }
        if (mFocused != null && mLayouted) {
            mPositionManager.draw(canvas);
        } else if (!mLayouted) {
            postInvalidateDelayed(30);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (checkValidKey(event.getKeyCode()) && (mFocused == null || !mFocused.hasFocus()) && mLayouted) {
            Log.w(TAG,
                    "dispatchKeyEvent mFocused is null, may be no focusbale view in your layout, or mouse switch to key");
            deliverFocus();
            invalidate();
            return true;
        }

        if (event.dispatch(this, null, this)) {
            return true;
        }

        return false;
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

        if (DEBUG) {
            Log.d(TAG, "onFocusChanged: child count = " + getChildCount() + ", gainFocus = " + gainFocus);
        }

        if (gainFocus && mLayouted) {
            deliverFocus();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (DEBUG) {
            Log.i(TAG, TAG + ".onLayout");
        }

        if (hasFocus() && !mLayouted && mFocused == null) {
            deliverFocus();
        }
        mLayouted = true;
    }

    private void deliverFocus() {
        if (mFocused == null) {
            mFocused = findFocusChild(this);
        }

        Log.d(TAG, "deliverFocus mFocused = " + mFocused);

        try {
            if (mFocused != null) {
                mFocused.requestFocus();
                // mFocusRequestRunnable.start(mFocused);
                mPositionManager.reset((FocusListener) mFocused);
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    public void resetFocused() {
        if (mFocused == null) {
            mFocused = findFocusChild(this);
        }

        Log.d(TAG, "resetFocused mFocused = " + mFocused);
        if (mFocused != null) {
            if (!mFocused.hasFocus()) {
                mFocused.requestFocus();
            }
            mPositionManager.reset((FocusListener) mFocused);
        }
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        mLayouted = false;
    }

    private View mFirstRequestChild;// 指定哪个child第一个获得焦点

    public void setFirstFocusChild(View firstRequestChild) {
        this.mFirstRequestChild = firstRequestChild;
    }

    private View findFocusChild(ViewGroup v) {
        View result = null;
        for (int index = 0; index < v.getChildCount(); index++) {
            View child = v.getChildAt(index);
            if (child instanceof FocusListener) {
                if (child.isFocusable() == false || child.getVisibility() != View.VISIBLE) {
                    continue;
                }
                if (child == mFirstRequestChild) {
                    return child;
                }
                if (result == null) {// 记录第一个满足条件的child
                    result = child;
                }
            } else if (child instanceof ViewGroup) {
                child = findFocusChild((ViewGroup) child);
                if (child != null) {
                    return child;
                }
            }
        }

        return result;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mFocused != null && mFocused.onKeyUp(keyCode, event)) {
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (DEBUG) {
            Log.d(TAG, "onKeyDown: keyCode = " + keyCode + ".mFocused = " + mFocused);
        }

        if (isNeedCheckValidKey(keyCode) && !checkValidKey(keyCode)) {
            return super.onKeyDown(keyCode, event);
        }

        if (!mPositionManager.canDrawNext()) {
            return true;
        }

        if (mPositionManager.preOnKeyDown(keyCode, event)) {
            boolean hr = false;
            if (keyCode != KeyEvent.KEYCODE_DPAD_CENTER && keyCode != KeyEvent.KEYCODE_ENTER
                    && keyCode != KeyEvent.KEYCODE_NUMPAD_ENTER) {
                Log.i(TAG, TAG + ".onKeyDown.mPositionManager.stop()");
                mPositionManager.stop();
                hr = true;
            }
            if (mPositionManager.onKeyDown(keyCode, event)) {
                if (hr) {
                    mPositionManager.reset();
                }
                return true;
            }
        }

        if (mFocused == null) {
            return true;
        }

        View focused = null;
        int direction = 0;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                direction = View.FOCUS_LEFT;
                focused = focusSearch(mFocused, direction);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                direction = View.FOCUS_RIGHT;
                focused = focusSearch(mFocused, direction);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                direction = View.FOCUS_UP;
                focused = focusSearch(mFocused, direction);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                direction = View.FOCUS_DOWN;
                focused = focusSearch(mFocused, direction);
                break;
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_ESCAPE:
                return super.onKeyDown(keyCode, event);

            default:
                break;
        }

        Log.i(TAG, "onKeyDown the new focused = " + focused + ", previous focused = " + mFocused);
        if (focused != null && focused != this) {
            if (!checkFocus(focused, mFocused, direction)) {
                Log.i(TAG, "onKeyDown: checkFocus failed  new focus = " + focused + ", old focus = " + mFocused);
                mFocused = focused;
                return super.onKeyDown(keyCode, event);
            }
            if (focused instanceof FocusListener) {
                View lastFocused = mFocused;
                mFocused = focused;
                mPositionManager.stop();
                mFocused.requestFocus(direction, getFocusedRect(lastFocused, mFocused));
                mPositionManager.reset((FocusListener) mFocused);
            } else {
                Log.w(TAG, "onKeyDown the new focused is not instance of FocusListener");
            }
            this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
            return true;
        } else {
            Log.w(TAG, "onKeyDown can not find the new focus");
            return true;
        }
    }

    boolean checkFocus(View newFocus, View oldFocus, int direction) {

        if (!isChild(this, newFocus)) {
            return false;
        }
        // if (direction == View.FOCUS_LEFT) {
        // Rect newRect = new Rect();
        // Rect oldRect = new Rect();
        // newFocus.getFocusedRect(newRect);
        // offsetDescendantRectToMyCoords(newFocus, newRect);
        // oldFocus.getFocusedRect(oldRect);
        // offsetDescendantRectToMyCoords(oldFocus, oldRect);
        //
        // if (newRect.left > oldRect.left) {
        // return false;
        // }
        //
        // if (newRect.bottom < oldRect.top || newRect.top > oldRect.bottom) {
        // return false;
        // }
        // }

        return true;
    }

    boolean isChild(ViewGroup p, View newFocus) {
        for (int index = 0; index < p.getChildCount(); index++) {
            View v = p.getChildAt(index);
            if (v == newFocus) {
                return true;
            } else if (v instanceof ViewGroup) {

                if (isChild((ViewGroup) v, newFocus)) {
                    return true;
                }
            }
        }

        return false;
    }

    Rect getFocusedRect(View from, View to) {
        if (from == null || to == null) {
            return null;
        }

        Rect rFrom = new Rect();
        from.getFocusedRect(rFrom);
        Rect rTo = new Rect();
        to.getFocusedRect(rTo);

        try {
            offsetDescendantRectToMyCoords(from, rFrom);
            offsetDescendantRectToMyCoords(to, rTo);
        } catch (Exception e) {
            e.printStackTrace();
        }


        int xDiff = rFrom.left - rTo.left;
        int yDiff = rFrom.top - rTo.top;
        int rWidth = rFrom.width();
        int rheight = rFrom.height();
        rFrom.left = xDiff;
        rFrom.right = rFrom.left + rWidth;
        rFrom.top = yDiff;
        rFrom.bottom = rFrom.top + rheight;

        return rFrom;
    }

    @Override
    public void offsetDescendantRectToItsCoords(View descendant, Rect rect) {
        offsetDescendantRectToMyCoords(descendant, rect);
    }

    @Override
    public void reset() {
        Log.i(TAG, TAG + ".reset.mFocused = " + mFocused);
        boolean isInvalidate = true;

        if (mFocused != null) {
            mPositionManager.stop();
            mPositionManager.reset();
        } else {
            mFocused = findFocusChild(this);
            if (mFocused != null) {
                mFocused.requestFocus();
                mPositionManager.reset((FocusListener) mFocused);
            } else {
                isInvalidate = false;
            }
        }

        if (isInvalidate) {
            invalidate();
        }
    }

    private class FocusRequestRunnable implements Runnable {

        View mView;

        public FocusRequestRunnable() {
            // mView = v;
        }

        public void start(View v) {
            mView = v;
            if (mView != null) {
                removeCallbacks(this);
                post(this);
            }
        }

        @Override
        public void run() {
            mView.requestFocus();
            invalidate();
        }

    }

    private boolean checkValidKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DEL:
                return true;
            default:
                return false;
        }
    }

    public PositionManager getPositionManager() {
        return mPositionManager;
    }

    public int getFocusFrameRate() {
        return mPositionManager.getFocusFrameRate();
    }

    public int getCurFocusFrame() {
        return mPositionManager.getCurFocusFrame();
    }

    protected View getFocusedView() {
        return mFocused;
    }

    protected boolean isNeedCheckValidKey(int keyCode) {
        return true;
    }

    public DrawListener getSelector() {
        return mPositionManager.getSelector();
    }

    public DrawListener setConvertSelector() {
        return mPositionManager.getConvertSelector();
    }
}
