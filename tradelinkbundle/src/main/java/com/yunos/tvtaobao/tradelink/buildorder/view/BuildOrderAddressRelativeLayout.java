package com.yunos.tvtaobao.tradelink.buildorder.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.yunos.tv.app.widget.focus.FocusRelativeLayout;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.tradelink.R;


public class BuildOrderAddressRelativeLayout extends FocusRelativeLayout {

    private int mTop;

    public BuildOrderAddressRelativeLayout(Context context) {
        super(context);
        initBuildOrderAddressRelativeLayout(context);
    }

    public BuildOrderAddressRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBuildOrderAddressRelativeLayout(context);
    }

    public BuildOrderAddressRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initBuildOrderAddressRelativeLayout(context);
    }

    private void initBuildOrderAddressRelativeLayout(Context context) {
        mTop = this.getResources().getDimensionPixelSize(R.dimen.dp_8);
    }


    @Override
    protected void reset() {
        super.reset();
        if (getSelectedView() == null) {
            return;
        }
        Rect rect = mFocusRectparams.focusRect();
        rect.left = 0;
        rect.top = mTop;
        rect.right = getWidth();
        rect.bottom = getHeight();
    }

    @Override
    public Rect getClipFocusRect() {
        Rect rect = new Rect();
        getDrawingRect(rect);
        rect.top += mTop;
        return rect;
    }

    @Override
    public ItemListener getItem() {
        return this;
    }

    @Override
    public boolean canDraw() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        AppDebug.d(TAG, "onFocus changed " + gainFocus);
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        AppDebug.d(TAG, " pre onKeyDown: " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                //performItemClick(getSelectedView(), getSelectedItemPosition(), getSelectedItemId());
                //return true;
                return true;
        }
        return super.preOnKeyDown(keyCode, event);
    }
}
