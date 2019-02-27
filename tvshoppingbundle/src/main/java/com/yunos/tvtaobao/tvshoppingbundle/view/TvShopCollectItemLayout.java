/**
 * $
 * PROJECT NAME: MovieBuy
 * PACKAGE NAME: com.example.moviebuy
 * FILE NAME: CollectItemLayout.java
 * CREATED TIME: 2015年1月2日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.tvshoppingbundle.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tvtaobao.tvshoppingbundle.R;

/**
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2015年1月2日 下午8:44:08
 */
public class TvShopCollectItemLayout extends RelativeLayout implements ItemListener {

    private Context mContext;

    public TvShopCollectItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public void drawAfterFocus(Canvas arg0) {
    }

    @Override
    public void drawBeforeFocus(Canvas arg0) {
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

    private void adjustRectParam(Rect r) {
        r.left += mContext.getResources().getDimension(R.dimen.dp_4_5);
        r.right = r.left + (int) mContext.getResources().getDimension(R.dimen.dp_280);
        r.top = r.bottom - (int) mContext.getResources().getDimension(R.dimen.dp_168_5);
    }

    public Rect getFocusedRect() {
        Rect r = new Rect();
        getFocusedRect(r);
        return r;
    }

    @Override
    public FocusRectParams getFocusParams() {
        Rect r = getFocusedRect();
        adjustRectParam(r);
        return new FocusRectParams(r, 0.5f, 0.5f);
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
        return true;
    }

}
