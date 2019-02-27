package com.yunos.tvtaobao.tradelink.buildorder.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.yunos.tv.app.widget.focus.FocusLinearLayout;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;

public class AddressEditRelativeLayout extends RelativeLayout implements ItemListener {

    public AddressEditRelativeLayout(Context context) {
        super(context);
    }

    public AddressEditRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AddressEditRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isScale() {
        return false;
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

    private Rect getFocusedRect() {
        Rect r = new Rect();
        ViewGroup root = (ViewGroup) getThisChildRootView();
        r.left = 0;
        r.top = 0;
        r.right = root.getWidth();
        r.bottom = root.getHeight();
        //        root.getFocusedRect(r);  
        return r;
    }

    public View getThisChildRootView() {
        View parent = this;
        while (parent.getParent() != null && parent.getParent() instanceof View) {
            parent = (View) parent.getParent();
            if (parent instanceof FocusLinearLayout) {
                break;
            }
        }
        return parent;
    }

}
