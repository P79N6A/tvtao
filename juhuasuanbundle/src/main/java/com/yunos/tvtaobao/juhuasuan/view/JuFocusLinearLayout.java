/**
 * $
 * PROJECT NAME: juhuasuan
 * PACKAGE NAME: com.yunos.tvtaobao.juhuasuan.view
 * FILE NAME: JuFocusLinearLayout.java
 * CREATED TIME: 2015-1-13
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.juhuasuan.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.FocusLinearLayout;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.juhuasuan.R;

/**
 * 聚划算FocusLinearLayout实现类，主要是为Ali
 * @version
 * @author hanqi
 * @data 2015-1-13 下午5:29:05
 */
public class JuFocusLinearLayout extends FocusLinearLayout {

    protected float scale = 1.5f;

    public JuFocusLinearLayout(Context context) {
        super(context);
        initParams();
    }

    public JuFocusLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initParams();
    }

    public JuFocusLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initParams();
    }

    public void initParams() {
        mParams = new Params(1.0f, 1.0f, -1, null, true, 20, new AccelerateDecelerateFrameInterpolator());
    }

    @Override
    public boolean isScale() {
        return false;
    }

    public Rect getFocusedRect() {
        Rect r = new Rect();
        getFocusedRect(r);

        return r;
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
    public void setScaleX(float scaleX) {
        //        AppDebug.i(TAG, TAG + ".setScaleX scaleX=" + scaleX + ", view=" + this);
        //        AppDebug.i(TAG, TAG + ".setScaleX " + Log.getStackTraceString(new Throwable()));
        super.setScaleX(scaleX);
    }

    @Override
    public void setScaleY(float scaleY) {
        super.setScaleY(scaleY);
    }

    @Override
    public View getRootView() {
        View parent = this;

        while (parent.getParent() != null && parent.getParent() instanceof View) {
            parent = (View) parent.getParent();
            if (parent instanceof FocusPositionManager) {
                break;
            }
        }
        return parent;
    }

    @Override
    public FocusRectParams getFocusParams() {
        Rect r = getFocusedRect();

        int scaleX = (int) ((r.right - r.left) * scale - (r.right - r.left));
        int scaleY = (int) ((r.bottom - r.top) * scale - (r.bottom - r.top));
        r.left = r.left - scaleX / 2;
        r.right = r.right + scaleX / 2;
        r.top = r.top - scaleY / 2;
        r.bottom = r.bottom + scaleY / 2;

        r.left = r.left + getPaddingLeft();
        r.top = r.top + getPaddingTop();

        r.right = r.right - getPaddingRight();
        r.bottom = r.bottom - getPaddingBottom();

        AppDebug.i(TAG, TAG + ".getFocusParams r=" + r);

        //        onAjustItemFouceBound(r);

        return new FocusRectParams(r, 0.5f, 0.5f);
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public Rect getManualPadding() {
        Rect rect = new Rect();
        rect.setEmpty();
        rect.left = getResources().getDimensionPixelSize(R.dimen.dp_2);
        rect.top = getResources().getDimensionPixelSize(R.dimen.dp_2);
        rect.right -= getResources().getDimensionPixelSize(R.dimen.dp_2);
        rect.bottom -= getResources().getDimensionPixelSize(R.dimen.dp_2);
        return rect;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    /*
     * (non-Javadoc)
     * @see com.yunos.tv.app.widget.focus.FocusLinearLayout#drawAfterFocus(android.graphics.Canvas)
     */
    @Override
    public void drawAfterFocus(Canvas canvas) {
        super.drawAfterFocus(canvas);
        AppDebug.i(TAG, TAG + ".drawAfterFocus getParams=" + getParams().getScaleParams().getScaleX());
    }
}
