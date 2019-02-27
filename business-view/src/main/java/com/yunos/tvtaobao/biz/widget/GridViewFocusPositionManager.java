package com.yunos.tvtaobao.biz.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;

import com.yunos.tv.app.widget.focus.FocusFlipGridView;
import com.yunos.tv.app.widget.focus.FocusPositionManager;

/**
 * 解决gridview抖动的问题
 */
public class GridViewFocusPositionManager extends FocusPositionManager {
    private static final float DEFAULT_SCROLL_THRESHOLD_RATE = 2.0f; // 默认的最大速度的阀值  
    private float mScrollThresholdRate = DEFAULT_SCROLL_THRESHOLD_RATE;
    private FocusFlipGridView mGridView;

    public GridViewFocusPositionManager(Context context) {
        super(context);
    }

    public GridViewFocusPositionManager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridViewFocusPositionManager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setGridView(FocusFlipGridView mGridView) {
        this.mGridView = mGridView;
    }

    /**
     * 设置GridView滚动的最大速度（rate越大速度最快）
     * @param rate
     */
    public void setGridViewScrollRate(float rate) {
        mScrollThresholdRate = rate;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 过滤动画相反方向的按键事件
        if (mGridView != null) {
            if (!mGridView.isFlipFinished()) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP && mGridView.isFlipDown()) {
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && !mGridView.isFlipDown()) {
                    return true;
                }
                // 限制最大的滚动速度
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    int firstLeft = mGridView.getFlipItemLeftMoveDistance(mGridView.getFirstPosition(), 0);
                    int maxDistance = (int)(mGridView.getHeight() * mScrollThresholdRate);
                    if (maxDistance > 0 && Math.abs(firstLeft) > maxDistance) {
                        Log.w(TAG, "left move distance over max distance");
                        return true;
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
