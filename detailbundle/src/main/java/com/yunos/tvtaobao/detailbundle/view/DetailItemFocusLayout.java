package com.yunos.tvtaobao.detailbundle.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.core.common.AppDebug;

public class DetailItemFocusLayout extends LinearLayout implements ItemListener {

    private boolean mDrawLeft;
    private boolean mDrawRight;
    private boolean mDrawTop;
    private boolean mDrawBottom;
    private Paint mDividerPaint;

    public DetailItemFocusLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DetailItemFocusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DetailItemFocusLayout(Context context) {
        super(context);
        init();
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
    public void onDraw(Canvas canvas) {
        AppDebug.i("draw", "onDraw start");
        
        super.draw(canvas);

        
    }
    
    

    @Override
    protected void dispatchDraw(Canvas canvas) {
        AppDebug.i("draw", "dispatchDraw start");
        super.dispatchDraw(canvas);
        if (mDrawLeft) {
            canvas.drawLine(0, 0, 1, getHeight(), mDividerPaint);
        }
        if (mDrawRight) {
            canvas.drawLine(getWidth(), 1, getWidth() - 1, getHeight(), mDividerPaint);
        }
        if (mDrawTop) {
            canvas.drawLine(0, 0, getWidth(), 1, mDividerPaint);
        }
        if (mDrawBottom) {
            canvas.drawLine(0, getHeight(), getWidth(), getHeight() - 1, mDividerPaint);
        }
    }

    private void init() {
        mDividerPaint = new Paint();
        // 设置画笔风格 
        mDividerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mDividerPaint.setStrokeJoin(Paint.Join.ROUND);
        // 设置画笔方形
        mDividerPaint.setStrokeCap(Paint.Cap.SQUARE);
        mDividerPaint.setDither(true);
        // 设置使用抗锯齿功能
        mDividerPaint.setAntiAlias(true);
        mDividerPaint.setColor(Color.parseColor("#22ffffff"));
        mDividerPaint.setStrokeWidth(1);
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    private Rect getFocusedRect() {
        Rect r = new Rect();
        getFocusedRect(r);
        return r;
    }

    public void drawLine(boolean left, boolean right, boolean top, boolean bottom) {
        mDrawLeft = left;
        mDrawRight = right;
        mDrawTop = top;
        mDrawBottom = bottom;

        AppDebug.i("draw", "draw mDrawLeft=" + mDrawLeft + ",mDrawRight=" + mDrawRight + ",mDrawTop=" + mDrawTop
                + ",mDrawBottom=" + mDrawBottom);
        invalidate();
    }
}
