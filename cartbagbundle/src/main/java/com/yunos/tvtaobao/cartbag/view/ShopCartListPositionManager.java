package com.yunos.tvtaobao.cartbag.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.widget.InnerFocusGroupHorizonalListView;


public class ShopCartListPositionManager extends FocusPositionManager {
    private String TAG = "ShopCartListPostionManager";
    public ShopCartListPositionManager(Context context) {
        super(context);
    }

    public ShopCartListPositionManager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShopCartListPositionManager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        View focus = getFocused();
        if (focus instanceof InnerFocusGroupHorizonalListView) {
            InnerFocusGroupHorizonalListView listView = (InnerFocusGroupHorizonalListView)focus;
            if (listView.actionInnerFocus(keyCode, event)) {
                AppDebug.i(TAG, "onKeyDown actionInnerFocus keyCode="+keyCode);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
