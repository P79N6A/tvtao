/**
 *
 */
package com.yunos.tvtaobao.tradelink.buildorder.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.yunos.tv.app.widget.focus.FocusGallery;

/**
 */
public class BuildOrderGallery extends FocusGallery {

    private BitmapDrawable leftDrawable;
    private BitmapDrawable rightDrawable;
    private int mDx;
    private int mDy;
    private boolean mLeftShow;
    private boolean mRightShow;

    private Rect arrowRect;

    private boolean mLoop;

    /**
     * @param context
     */
    public BuildOrderGallery(Context context) {
        super(context);
        onInit();
    }

    /**
     * @param context
     * @param attrs
     */
    public BuildOrderGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        onInit();
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public BuildOrderGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onInit();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        // 画左侧提示箭头
        if (leftDrawable != null && mLeftShow) {
            arrowRect.left = mDx;
            arrowRect.top = mDy;
            arrowRect.right = arrowRect.left + leftDrawable.getIntrinsicWidth();
            arrowRect.bottom = arrowRect.top + leftDrawable.getIntrinsicHeight();
            leftDrawable.setBounds(arrowRect);
            leftDrawable.draw(canvas);
        }

        // 画右侧提示箭头
        if (rightDrawable != null && mRightShow) {

            arrowRect.right = getWidth() - mDx;
            arrowRect.bottom = getHeight() - mDy;
            arrowRect.left = arrowRect.right - rightDrawable.getIntrinsicWidth();
            arrowRect.top = arrowRect.bottom - rightDrawable.getIntrinsicHeight();
            rightDrawable.setBounds(arrowRect);
            rightDrawable.draw(canvas);
        }
    }

    /**
     * 初始化
     */
    private void onInit() {
        arrowRect = new Rect();
        leftDrawable = null;
        rightDrawable = null;
        mLeftShow = false;
        mRightShow = false;
        mLoop = false;
        mDx = 0;
        mDy = 0;
    }

    /**
     * 设置左右箭头的资源
     *
     * @param leftBm
     * @param rightBm
     */
    public void setHintArrow(Bitmap leftBm, Bitmap rightBm) {
        leftDrawable = new BitmapDrawable(leftBm);
        rightDrawable = new BitmapDrawable(rightBm);
        invalidate();
    }

    /**
     * 设置箭头离父视图的间距，
     *
     * @param dx
     * @param dy
     */
    public void setArrowSpace(int dx, int dy) {
        mDx = dx;
        mDy = dy;
        invalidate();
    }

    /**
     * 设置提示箭头是否显示
     *
     * @param leftshow
     * @param rightshow
     */
    public void setArrowShow(boolean leftshow, boolean rightshow) {
        mLeftShow = leftshow;
        mRightShow = rightshow;
        invalidate();
    }

    public void setLoopDraw(boolean loop) {
        mLoop = loop;
    }

    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                //performItemClick(getSelectedView(), getSelectedItemPosition(), getSelectedItemId());
                //return true;
        }
        return super.preOnKeyDown(keyCode, event);
    }

    @Override
    public boolean canDraw() {
        boolean result = super.canDraw();
        return result;
    }
}
