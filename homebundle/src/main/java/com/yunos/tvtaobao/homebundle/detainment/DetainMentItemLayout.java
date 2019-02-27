package com.yunos.tvtaobao.homebundle.detainment;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tvtaobao.biz.common.DrawRect;
import com.yunos.tvtaobao.homebundle.R;

public class DetainMentItemLayout extends FrameLayout implements ItemListener {

    private DrawRect mDrawRect;

    public DetainMentItemLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DetainMentItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DetainMentItemLayout(Context context) {
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

    }

    @Override
    public void drawBeforeFocus(Canvas arg0) {

    }

    private Rect getFocusedRect() {
        Rect r = new Rect();
        getFocusedRect(r);
        r.top = r.top + (int) (getResources().getDimension(R.dimen.dp_2));
        r.bottom = r.bottom - (int) (getResources().getDimension(R.dimen.dp_2));
        return r;
    }

    @Override
    public FocusRectParams getFocusParams() {
        Rect focuse = getFocusedRect();
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
