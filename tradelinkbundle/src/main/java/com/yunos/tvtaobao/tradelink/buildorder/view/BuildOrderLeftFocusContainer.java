package com.yunos.tvtaobao.tradelink.buildorder.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;

/**
 * Created by zhujun on 9/29/16.
 */

public class BuildOrderLeftFocusContainer extends RelativeLayout implements FocusListener, ItemListener {
    protected Params mParams = new Params(1.1f, 1.1f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());

    public BuildOrderLeftFocusContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BuildOrderLeftFocusContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BuildOrderLeftFocusContainer(Context context) {
        this(context, null);
    }

    @Override
    public boolean isScale() {
        return false;
    }

    public interface PreKeyDownListener {
        boolean preOnKeyDown(BuildOrderLeftFocusContainer container, KeyEvent keyEvent);
    }

    private PreKeyDownListener mListener;

    public void setPreKeyDownListener(PreKeyDownListener listener) {
        this.mListener = listener;
    }

    private FocusRectParams mFocusRectparams = new FocusRectParams();

    @Override
    public FocusRectParams getFocusParams() {
        Rect r = new Rect();
        getLocalVisibleRect(r);

        mFocusRectparams.set(r, 0.5f, 0.5f);
        return mFocusRectparams;
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
        return true;
    }

    @Override
    public boolean canDraw() {
        return true;
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
    public boolean isScrolling() {
        return false;
    }

    @Override
    public Params getParams() {
        return mParams;
    }

    @Override
    public boolean isFocusBackground() {
        return false;
    }

    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        if (mListener != null) return mListener.preOnKeyDown(this, event);
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
}
