package com.yunos.tvtaobao.takeoutbundle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.yunos.tv.app.widget.focus.FocusPositionManager;

/**
 * 订单详情页，左边的可滚动区域.
 */
public class TOOrderDetailPositionManager extends FocusPositionManager {

    private String TAG = "TOOrderDetailPositionManager";

    public TOOrderDetailPositionManager(Context context) {
        super(context);
    }

    public TOOrderDetailPositionManager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TOOrderDetailPositionManager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        View focus = getFocused();
        if (focus instanceof TOFocusNoDeepRelativeLayout) {
            TOFocusNoDeepRelativeLayout relativeLayout = (TOFocusNoDeepRelativeLayout) focus;
            if (relativeLayout.actionScroll(keyCode, event)) {
                return true;
            }
        }

        if (focus instanceof TOFocusHorizontalListView) {// 修复AONE[13587312]
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
