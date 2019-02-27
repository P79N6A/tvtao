package com.yunos.tvtaobao.mytaobao.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tvtaobao.biz.common.DrawRect;

public class ItemLayoutForAddress extends RelativeLayout implements ItemListener {

    private DrawRect mDrawRect;

    public ItemLayoutForAddress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ItemLayoutForAddress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ItemLayoutForAddress(Context context) {
        super(context);
        init();
    }

    private void init() {
        mDrawRect = new DrawRect(this);
    }

    /**
     * 设置是否被选中
     * @param selected
     */
    public void setSelect(boolean selected) {
        if (!selected) {
            mDrawRect.showMark();
        } else {
            mDrawRect.hideMark();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mDrawRect.drawRect(canvas, this);
    }

    @Override
    public void drawAfterFocus(Canvas arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawBeforeFocus(Canvas arg0) {
        // TODO Auto-generated method stub

    }

    private Rect getFocusedRect() {
        Rect r = new Rect();
        getFocusedRect(r);
        return r;
    }

    @Override
    public FocusRectParams getFocusParams() {
        Rect focuse = getFocusedRect();
        //onAdjustFocusedRect(focuse);
        FocusRectParams focusRectParams = new FocusRectParams(focuse, 0.5f, 0.5f);
        return focusRectParams;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isFinished() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isScale() {
        // TODO Auto-generated method stub
        return true;
    }
}
