package com.yunos.tvtaobao.biz.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.yunos.tv.core.common.AppDebug;

public class TabFocusPositionManager extends GridViewFocusPositionManager {

    /*
     * 针对 mTabColorRect 和 mLogoDrawableRect
     * 如果 RECT 的 bottom 设置为 此值 ， 那么则取getHeight()
     * 如果 RECT 的 right 设置为 此值 ， 那么则取getWidth()
     */
    public static final int MATCH_PARENT = -1;

    private ColorDrawable mBackgroudColor;
    private ColorDrawable mTabColor;
    private BitmapDrawable mLogoDrawable;

    private Rect mTabColorRect;
    private Rect mLogoDrawableRect;

    private boolean mCanScroll;

    public TabFocusPositionManager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onInitTabFocusPositionManager(context);
    }

    public TabFocusPositionManager(Context context, AttributeSet attrs) {
        super(context, attrs);
        onInitTabFocusPositionManager(context);
    }

    public TabFocusPositionManager(Context context) {
        super(context);
        onInitTabFocusPositionManager(context);
    }

    private void onInitTabFocusPositionManager(Context context) {
        mBackgroudColor = null;
        mTabColor = null;
        mLogoDrawable = null;
        mCanScroll = true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        // 绘制背景颜色
        if (mBackgroudColor != null) {
            mBackgroudColor.setBounds(0, 0, getWidth(), getHeight());
            mBackgroudColor.draw(canvas);
        }

        // 绘制TAB的颜色
        if (mTabColor != null) {
            if (mTabColorRect.bottom == MATCH_PARENT) {
                mTabColorRect.bottom = getHeight();
            }
            mTabColor.setBounds(mTabColorRect);
            mTabColor.draw(canvas);
        }

        // 绘制LOG
        if (mLogoDrawable != null) {
            if (mLogoDrawableRect.bottom == MATCH_PARENT) {
                mLogoDrawableRect.bottom = getHeight();
            }
            mLogoDrawable.setBounds(mLogoDrawableRect);
            mLogoDrawable.draw(canvas);
        }

        super.dispatchDraw(canvas);
    }

    /**
     * 设置背景颜色
     * @param color
     */
    public void setBackgroudColor(int color) {
        if (mBackgroudColor != null) {
            mBackgroudColor.setCallback(null);
            mBackgroudColor = null;
        }
        mBackgroudColor = new ColorDrawable(color);
        invalidate();
    }

    public void onInitBackgroud() {
        mBackgroudColor = null;
        invalidate();
    }

    /**
     * 设置TAB颜色
     * @param color
     * @param menurRect
     */
    public void setTabColor(int color, Rect tabRect) {
        if (mTabColor != null) {
            mTabColor.setCallback(null);
            mTabColor = null;
        }
        mTabColorRect = new Rect(tabRect);
        mTabColor = new ColorDrawable(color);
        invalidate();
    }

    /**
     * 设置Logo
     * @param bm
     * @param logoRect
     */
    public void setLeftLogo(Bitmap bm, Rect logoRect) {
        mLogoDrawableRect = new Rect(logoRect);
        if (bm != null && !bm.isRecycled()) {
            if (mLogoDrawable != null) {
                mLogoDrawable.setCallback(null);
                mLogoDrawable = null;
            }
            mLogoDrawable = new BitmapDrawable(bm);
            invalidate();
        }
    }

    /**
     * 设置该控件以及该控件下的子view是否支持滚动
     * @param canScroll
     */
    public void setCanScroll(boolean canScroll) {
        mCanScroll = canScroll;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        AppDebug.v(TAG, TAG + ".onInterceptTouchEvent.ev = " + ev + ".mCanScroll = " + mCanScroll);
        if (ev.getAction() == MotionEvent.ACTION_MOVE && !mCanScroll) {// 如果不能滚动，则返回true，进行拦截
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
