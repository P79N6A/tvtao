/**
 * $
 * PROJECT NAME: juhuasuan
 * PACKAGE NAME: com.yunos.tvtaobao.juhuasuan.view
 * FILE NAME: BrandFocusGallery.java
 * CREATED TIME: 2015-1-13
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.juhuasuan.view;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.FocusGallery;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.core.common.AppDebug;

/**
 * 品牌团商品列表用的gallery控件
 * @version
 * @author hanqi
 * @data 2015-1-13 下午8:11:00
 */
public class BrandFocusGallery extends FocusGallery {

    private int mCenter; // 中心位置，用来计算离中心的坐标的距离来判断item的变化弧度
    private float mSelectScale = 1.0f; // 选中item的放大比例
    private int scrollNextNum = 3;

    public BrandFocusGallery(Context context) {
        super(context);
        initParams();
    }

    public BrandFocusGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        initParams();
    }

    public BrandFocusGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initParams();
    }

    public void initParams() {
        //参数1表示只画一次，画多了会有问题
        mParams = new Params(1.0f, 1.0f, 1, null, true, 20, new AccelerateDecelerateFrameInterpolator());
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        // 每次滚动的回调，来对item做相关的状态显示变化
        changeAllChild();
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        View selectedView = getSelectedView();
        if (getLeftScrollDistance() == 0) {
            if (selectedView != null) {
                // 布局完成后取得中心的坐标位置
                mCenter = getChildViewCenter(selectedView);
            }
            changeAllChild();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (checkState(keyCode)) {
            return true;
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (getChildCount() > 0) {
                    if (Math.abs(getLeftScrollDistance()) > getChildAt(0).getWidth() * scrollNextNum) {
                        return true;
                    }
                }
            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 改变所有的子view的状态
     * （以中心点为目标，按一定的比例进行状态变化，离中心越远的变化越大）
     */
    private void changeAllChild() {
        int childCount = getChildCount();
        // 必须为两个的时候才能计算出两个item之间的差距离
        if (childCount > 1) {
            int center = mCenter;
            int itemGap = getChildViewCenter(getChildAt(1)) - getChildViewCenter(getChildAt(0));
            itemGap = Math.abs(itemGap);
            // 有效值
            if (center > 0 && itemGap > 0) {
                for (int i = 0; i < childCount; i++) {
                    View childView = getChildAt(i);
                    int itemCenter = getChildViewCenter(childView);
                    float itemRatio = Math.abs((float) (center - itemCenter) / (float) itemGap);
                    if (itemRatio > 1) {
                        itemRatio = 1;
                    }
                    // 计算出缩放系数
                    float currScale = mSelectScale - itemRatio * (mSelectScale - 1.0f);
                    AppDebug.i(TAG, TAG + ".changeAllChild i=" + i + ", currScale=" + currScale + ", itemRatio="
                            + itemRatio + ", center=" + center + ", itemCenter=" + itemCenter + ", itemGap=" + itemGap
                            + ", view=" + childView);
                    changeChild(childView, currScale);
                }
            }
        }
    }

    /**
     * 变化item的状态
     * @param item
     * @param scale
     * @param rotate
     * @param zoomRatio
     */
    private void changeChild(View item, float scale) {
        AppDebug.i(TAG, TAG + ".changeChild 1==> item.size=" + item.getWidth() + ", " + item.getHeight());
        // 缩放
        item.setScaleX(scale);
        item.setScaleY(scale);
        AppDebug.i(TAG, TAG + ".changeChild 2==> item.size=" + item.getWidth() + ", " + item.getHeight());
    }

    /**
     * 取得item的中心坐标
     * @param childView
     * @return
     */
    private int getChildViewCenter(View childView) {
        return childView.getLeft() + childView.getWidth() / 2;
    }

    public float getSelectScale() {
        return mSelectScale;
    }

    public void setSelectScale(float selectScale) {
        this.mSelectScale = selectScale;
    }

    @Override
    public void drawAfterFocus(Canvas canvas) {
        AppDebug.i(TAG, TAG + ".drawAfterFocus size=" + getWidth() + ", " + getHeight());
    }
}
