package com.yunos.tvtaobao.biz.common;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

/**
 * 画卡片位上面的蒙板
 * @author tingmeng.ytm
 */
public class DrawRect {

    private Paint mPaint; // 画笔
    private Rect mDrawRect; // 画的区域
    private boolean mShowMark; // 是否要画
    private ViewGroup mView; // 画在指定的View上面

    public DrawRect(ViewGroup view) {
        mShowMark = true; // 默认需要画
        mDrawRect = new Rect();
        mPaint = new Paint();
        // 默认黑色，透明度为0.2f
        mPaint.setColor(Color.BLACK);
        mPaint.setAlpha((int) (255 * 0.2f));
        mPaint.setStyle(Paint.Style.FILL);
        mView = view;
    }

    /**
     * 显示蒙板（建议放要setSelected的方法里）
     */
    public void showMark() {
        mShowMark = true;
        mView.invalidate();
    }

    /**
     * 隐藏蒙板（建议放要setSelected的方法里）
     */
    public void hideMark() {
        mShowMark = false;
        mView.invalidate();
    }

    /**
     * 画蒙板(建议放在super.dispathDraw之后)
     * @param canvas
     * @param specialView 指定的内部View
     */
    public void drawRect(Canvas canvas, View specialView) {
        if (mShowMark) {
            if (specialView != null && specialView.getVisibility() == View.VISIBLE) {
                specialView.getFocusedRect(mDrawRect);
                mDrawRect.left += specialView.getPaddingLeft();
                mDrawRect.top += specialView.getPaddingTop();
                mDrawRect.right -= specialView.getPaddingRight();
                mDrawRect.bottom -= specialView.getPaddingBottom();
                mView.offsetDescendantRectToMyCoords(specialView, mDrawRect);
            } else {
                mDrawRect.set(0, 0, mView.getWidth(), mView.getHeight());
            }
            canvas.drawRect(mDrawRect, mPaint);
        }
    }
}
