package com.yunos.tvtaobao.detailbundle.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.yunos.tv.app.widget.ListView;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.core.common.AppDebug;

import java.util.ArrayList;

public class InnerFocusFrameLayout extends FrameLayout implements ItemListener {

    private final String TAG = "InnerFocusFrameLayout";

    public InnerFocusFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public InnerFocusFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InnerFocusFrameLayout(Context context) {
        super(context);
    }

    @Override
    public boolean isScale() {
        return false;
    }

    @Override
    public FocusRectParams getFocusParams() {
        Rect r = new Rect();
        getFocusedRect(r);
        FocusRectParams focusRectparams = new FocusRectParams();
        focusRectparams.set(r, 0.5f, 0.5f);
        return focusRectparams;
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
    public void drawBeforeFocus(Canvas canvas) {

    }

    @Override
    public void drawAfterFocus(Canvas canvas) {

    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (views == null) {
            return;
        }
        if (!isFocusable()) {
            return;
        }
        if ((focusableMode & FOCUSABLES_TOUCH_MODE) == FOCUSABLES_TOUCH_MODE && isInTouchMode()
                && !isFocusableInTouchMode()) {
            return;
        }
        views.add(this);
    }

    @Override
    public boolean performClick() {
        AppDebug.i(TAG, "performClick -->");
        if (childViewClick()) {
            return true;
        }
        return super.performClick();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AppDebug.i(TAG, "onKeyDown --> keyCode = " + keyCode + "; event = " + event);
        if (!checkEnterKeyCode(keyCode, event)) {
            if (onKeyDownOfchildView(keyCode, event)) {
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        AppDebug.i(TAG, "onKeyUp --> keyCode = " + keyCode + "; event = " + event);
        if (!checkEnterKeyCode(keyCode, event)) { 
            if (onKeyUpOfchildView(keyCode, event)) {
                return true;
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    /**
     * 把单击事件传给子VIEW
     */
    private boolean childViewClick() {
        int count = getChildCount();
        boolean result = false;
        for (int index = 0; index < count; index++) {
            View view = getChildAt(index);
            if (view != null) {
                if (view.getVisibility() != View.VISIBLE) {
                    continue;
                }
                if (view instanceof ListView) {
                    ListView listView = (ListView) view;
                    listView.performItemClick(listView.getSelectedView(), listView.getSelectedItemPosition(),
                            listView.getSelectedItemId());
                    result = true;
                } else {
                    view.performClick();
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * 子VIEW的onKeyDown
     * @param keyCode
     * @param event
     * @return
     */
    private boolean onKeyDownOfchildView(int keyCode, KeyEvent event) {
        int count = getChildCount();
        boolean result = false;
        for (int index = 0; index < count; index++) {
            View view = getChildAt(index);
            if (view != null) {
                if (view.getVisibility() != View.VISIBLE) {
                    continue;
                }
                if (view instanceof ListView) {
                    ListView listView = (ListView) view;
                    result = listView.onKeyDown(keyCode, event);
                }
            }
        }
        AppDebug.i(TAG, "onKeyDownOfchildView --> keyCode = " + keyCode + "; event = " + event + "; result = " + result);
        return result;
    }

    /**
     * 子VIEW的onKeyUp
     * @param keyCode
     * @param event
     * @return
     */
    private boolean onKeyUpOfchildView(int keyCode, KeyEvent event) {
        int count = getChildCount();
        boolean result = false;
        for (int index = 0; index < count; index++) {
            View view = getChildAt(index);
            if (view != null) {
                if (view.getVisibility() != View.VISIBLE) {
                    continue;
                }
                if (view instanceof ListView) {
                    ListView listView = (ListView) view;
                    result = listView.onKeyUp(keyCode, event);
                }
            }
        }
        AppDebug.i(TAG, "onKeyUpOfchildView --> keyCode = " + keyCode + "; event = " + event + "; result = " + result);
        return result;
    }

    /**
     * 判断是否是ok键
     * @param keyCode
     * @param event
     * @return
     */
    private boolean checkEnterKeyCode(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER
                || keyCode == KeyEvent.KEYCODE_ENTER) {
            return true;
        }
        return false;
    }
}
