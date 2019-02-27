/**
 * $
 * PROJECT NAME: TVTaobao
 * PACKAGE NAME: com.yunos.tvtaobao.view
 * FILE NAME: MenueFocusRelativeLayout.java
 * CREATED TIME: 2015年3月19日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.menu.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;

/**
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2015年3月19日 下午4:32:06
 */
public class MenueRelativeLayout extends RelativeLayout implements ItemListener {

    private Params mParams = new Params(1.05f, 1.05f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());

    public MenueRelativeLayout(Context context) {
        super(context);
    }

    public MenueRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenueRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isScale() {
        return true;
    }

    private Rect getFocusedRect() {
        Rect r = new Rect();
        getFocusedRect(r);
        return r;
    }

    @Override
    public FocusRectParams getFocusParams() {
        Rect focuse = getFocusedRect();
        FocusRectParams focusRectParams = new FocusRectParams(focuse, 0.5f, 0.5f);
        return focusRectParams;
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
}
