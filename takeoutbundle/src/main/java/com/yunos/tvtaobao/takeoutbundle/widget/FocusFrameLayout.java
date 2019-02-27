package com.yunos.tvtaobao.takeoutbundle.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;

import java.util.ArrayList;

/**
 * Created by haoxiang on 2017/12/13
 * //  最小focus接口实现
 * //  代理focus内部移动事件
 * //  代理focus点击事件
 * //  代理focus聚焦与失去事件
 */

public class FocusFrameLayout extends FrameLayout implements DeepListener, ItemListener {

    protected static final String TAG = "ShopHomeTitleView";


    public FocusFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFocusable(true);
        setClickable(true);
    }

    public FocusFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setClickable(true);
    }

    public FocusFrameLayout(Context context) {
        super(context);
        setFocusable(true);
        setClickable(true);
    }

    private Params mParams = new Params(1.05f, 1.05f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());
    private FocusRectParams mFocusRectparams = new FocusRectParams();
    private OnFocusActionLister mOnFocusActionLister;
    private boolean mShouldSkipKeyEvent;
    private boolean mCanDeep = true;
    // --------------------------------DeepListener and ItemListener imp-----------------------------//
    @Override
    public void drawBeforeFocus(Canvas canvas) {
    }

    @Override
    public void drawAfterFocus(Canvas canvas) {
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public void setScaleX(float scaleX) {
    }

    @Override
    public float getScaleX() {
        return 0;
    }

    @Override
    public void setScaleY(float scaleY) {
    }

    @Override
    public float getScaleY() {
        return 0;
    }

    @Override
    public boolean isScale() {
        return false;
    }

    @Override
    public int getItemWidth() {
        return getWidth();
    }

    @Override
    public int getItemHeight() {
        return getHeight();
    }

    @Override
    public Rect getManualPadding() {
        return null;
    }

    @Override
    public boolean canDeep() {
        return mCanDeep;
    }

    @Override
    public boolean hasDeepFocus() {
        return true;
    }

    @Override
    public void onFocusDeeped(boolean gainFocus, int direction, Rect previouslyFocusedRect) {

    }

    @Override
    public void onItemSelected(boolean selected) {
    }

    @Override
    public void onItemClick() {

    }

    @Override
    public FocusRectParams getFocusParams() {
        Rect r = new Rect();
        getFocusedRect(r);
        mFocusRectparams.set(r, 0.5f, 0.5f);
        return mFocusRectparams;
    }

    @Override
    public boolean canDraw() {
        return false;
    }

    @Override
    public boolean isAnimate() {
        return false;
    }

    @Override
    public ItemListener getItem() {
        return this;
    }

    @Override
    public Params getParams() {
        return mParams;
    }



    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        mShouldSkipKeyEvent = false;
        if (mOnFocusActionLister != null) {
            int direction = 0;
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    direction = FOCUS_LEFT;
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    direction = FOCUS_RIGHT;
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    direction = FOCUS_DOWN;
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    direction = FOCUS_UP;
                    break;
            }

            if (direction != 0 && mOnFocusActionLister.onInterceptDirectionKeyEvent(direction, event)) {
                mShouldSkipKeyEvent = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mShouldSkipKeyEvent || super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if(event.getAction() == KeyEvent.ACTION_UP){
                    super.performClick();
                    if(mOnFocusActionLister!=null){
                        mOnFocusActionLister.onClick(this);
                    }
                }
                break;
        }

        return mShouldSkipKeyEvent || super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean performClick() {
        return true;
    }

    public boolean isScrolling() {
        return false;
    }

    @Override
    public boolean isFocusBackground() {
        return false;
    }


    @Override
    public void onFocusStart() {
    }

    @Override
    public void onFocusFinished() {
    }

    @Override
    public Rect getClipFocusRect() {
        return null;
    }

    @Override
    public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus && mOnFocusActionLister != null) {
            mOnFocusActionLister.onFocusChanged(true);
        }
        if (!gainFocus && mOnFocusActionLister != null) {
            mOnFocusActionLister.onFocusChanged(false);
        }

    }

    // --------------------------------DeepListener and ItemListener end-----------------------------//


    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (views == null) {
            return;
        }
        if (!isFocusable()) {
            return;
        }
        views.add(this);
    }



    //-----------------------------------------------api---------------------------------------------//

    public void setCanDeep(boolean canDeep){
        mCanDeep = canDeep;
    }

    public void setOnFocusActionLister(OnFocusActionLister lister) {
        this.mOnFocusActionLister = lister;
    }

    public interface OnFocusActionLister {
        /**
         * 是否需要拦截此次方向键
         *
         * @param direction FOCUS_UP  FOCUS_DOWN  FOCUS_LEFT FOCUS_RIGHT:
         * @param keyEvent
         * @return
         */
        boolean onInterceptDirectionKeyEvent(int direction, KeyEvent keyEvent);

        void onFocusChanged(boolean gainFocus);

        void onClick(View view);
    }

}













