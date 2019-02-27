/**
 * $
 * PROJECT NAME: TVTaobao
 * PACKAGE NAME: com.yunos.tvtaobao.activity.search
 * FILE NAME: SearchResultItemLayout.java
 * CREATED TIME: 2014年10月20日
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.search.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tvtaobao.search.R;

/**
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2014年10月20日 下午8:13:10
 */
public class SearchResultItemLayout extends FrameLayout implements ItemListener {

    private Rect rect = null;

    public SearchResultItemLayout(Context context) {
        super(context);
        onInitSearchResultItemLayout(context);
    }

    public SearchResultItemLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onInitSearchResultItemLayout(context);
    }

    public SearchResultItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        onInitSearchResultItemLayout(context);
    }

    private void onInitSearchResultItemLayout(Context context) {
        rect = new Rect();
        rect.left = (int) context.getResources().getDimension(R.dimen.dp_3);
        rect.top = (int) context.getResources().getDimension(R.dimen.dp_0);
        rect.right = (int) context.getResources().getDimension(R.dimen.dp_0);
        rect.bottom = (int) context.getResources().getDimension(R.dimen.dp_0);
    }

    @Override
    public void drawAfterFocus(Canvas arg0) {
    }

    @Override
    public void drawBeforeFocus(Canvas arg0) {
    }

    @Override
    public int getItemHeight() {
        return getHeight();
    }

    @Override
    public int getItemWidth() {
        return getWidth();
    }

    @Override
    public Rect getManualPadding() {
        return null;
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public boolean isScale() {
        return false;
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
        onAdjustFouce(r);
        return new FocusRectParams(r, 0.5f, 0.5f);
    }

    public Rect getFocusedRect() {
        Rect r = new Rect();
        getFocusedRect(r);
        return r;
    }

    private void onAdjustFouce(Rect rt) {
        rt.left += rect.left;
        rt.top += rect.top;
        rt.right -= rect.right;
        rt.bottom -= rect.bottom;
    }
}
