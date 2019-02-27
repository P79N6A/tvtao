/**
 *
 */
package com.yunos.tvtaobao.tradelink.buildorder.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.LinearLayout;

import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;

/**
 *
 */
public class BuildOrderItemView extends LinearLayout implements ItemListener {

    private boolean mDrawDivider;
    private boolean mAjustFocus;
    private OnKeyListener mOnKeyListener;
    private Paint mDividerPaint;
    private Rect mManualRect;
    private boolean blockLeftRightKey = false;

    public void setBlockLeftRightKey(boolean blockLeftRightKey) {
        this.blockLeftRightKey = blockLeftRightKey;
    }

    /**
     * @param context
     */
    public BuildOrderItemView(Context context) {
        super(context);
        mAjustFocus = false;
        mDrawDivider = false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
            return blockLeftRightKey ? true : super.onKeyUp(keyCode, event);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
            return blockLeftRightKey ? true : super.onKeyDown(keyCode, event);
        return super.onKeyDown(keyCode, event);
    }

    /**
     * @param context
     * @param attrs
     */
    public BuildOrderItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAjustFocus = false;
        mDrawDivider = false;
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public BuildOrderItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mAjustFocus = false;
        mDrawDivider = false;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mDividerPaint != null && mDrawDivider) {
            int top = getHeight();
            int left = getPaddingLeft();
            int right = getPaddingRight();
            canvas.drawLine(left, top, getWidth() - right, getHeight(), mDividerPaint);
        }
    }

    @Override
    public void drawAfterFocus(Canvas arg0) {
    }

    @Override
    public void drawBeforeFocus(Canvas arg0) {
    }

    @Override
    public FocusRectParams getFocusParams() {
        Rect focuse = getFocusedRect();
        if (mAjustFocus) {
            onAjustItemFoucePadding(focuse);
        }
        onAdjustManualBound(focuse);
        FocusRectParams focusRectParams = new FocusRectParams(focuse, 0.5f, 0.5f);
        return focusRectParams;
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
    public int getItemWidth() {
        return getWidth();
    }

    @Override
    public int getItemHeight() {
        return getHeight();
    }

    private Rect getFocusedRect() {
        Rect r = new Rect();
        getFocusedRect(r);
        return r;
    }

    public void setAjustFocus(boolean ajustFocus, Rect manualRect) {
        mAjustFocus = ajustFocus;
        mManualRect = manualRect;
    }

    /**
     * 设置分割线
     */
    public void setDividerDrawable(int dividercolor, int dividerhight) {
        if (mDividerPaint == null) {
            // 创建Paint
            mDividerPaint = new Paint();
            // 设置画笔风格
            mDividerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mDividerPaint.setStrokeJoin(Paint.Join.ROUND);
            // 设置画笔方形
            mDividerPaint.setStrokeCap(Paint.Cap.SQUARE);
            mDividerPaint.setDither(true);
            // 设置使用抗锯齿功能
            mDividerPaint.setAntiAlias(true);
        }
        mDividerPaint.setColor(dividercolor);
        mDividerPaint.setStrokeWidth(dividerhight);
        invalidate();
    }

    public void drawDivider(boolean draw) {
        mDrawDivider = draw;
        invalidate();
    }

    public void setOnItemKeyDownListener(OnKeyListener l) {
        mOnKeyListener = l;
    }

    public OnKeyListener getOnKeyListener() {
        return mOnKeyListener;
    }

    /**
     * 调整Item的Fouce区域
     *
     * @param rt
     */
    private void onAjustItemFoucePadding(Rect rt) {

        Rect rect = new Rect();
        rect.left = getPaddingLeft();
        rect.right = getPaddingRight();
        rect.top = getPaddingTop();
        rect.bottom = getPaddingBottom();

        // 减去Padding
        rt.left += rect.left;
        rt.top += rect.top;
        rt.right -= rect.right;
        rt.bottom -= rect.bottom;
    }

    public void onAdjustManualBound(Rect rt) {
        Rect inputboxViewRt = mManualRect;
        if (inputboxViewRt != null) {
            rt.left += inputboxViewRt.left;
            rt.top += inputboxViewRt.top;
            rt.right -= inputboxViewRt.right;
            rt.bottom -= inputboxViewRt.bottom;
        }
    }

}
