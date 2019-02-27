package com.yunos.tvtaobao.takeoutbundle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.yunos.tv.core.common.AppDebug;

public class TOOrderListPositionManager extends FocusOriginalPositionManager {

    private String TAG = "TOOrderListPositionManager";

    public TOOrderListPositionManager(Context context) {
        super(context);
        init();
    }

    public TOOrderListPositionManager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TOOrderListPositionManager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        //TODO ORIGINAL
        //setPivotX(getWidth() / 2);
        //setPivotY(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        View focus = getFocused();
        if (focus instanceof TOInnerFocusHorizontalListView) {
            TOInnerFocusHorizontalListView listView = (TOInnerFocusHorizontalListView) focus;
            if (listView.actionInnerFocus(keyCode, event)) {
                AppDebug.i(TAG, "onKeyDown actionInnerFocus keyCode="+keyCode);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
