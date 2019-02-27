package com.yunos.tvtaobao.search.view;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.core.common.AppDebug;

public class SearchFocusPositionManager extends FocusPositionManager {

    private String TAG = "SearchFocusPositionManager";

    public SearchFocusPositionManager(Context context) {
        super(context);
    }

    public SearchFocusPositionManager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchFocusPositionManager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        AppDebug.v(TAG, TAG + ".onFocusChanged.gainFocus = " + gainFocus + ".direction = " + direction
                + ".previouslyFocusedRect = " + previouslyFocusedRect);
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        View focusView = getFocused();
        //        AppDebug.v(TAG, TAG + ".onKeyDown.keyCode = " + keyCode + ".focusView = " + focusView);
        if (focusView instanceof FocusListener) {
            FocusListener focusListener = (FocusListener) focusView;
            ItemListener itemListener = focusListener.getItem();
            if (itemListener instanceof HotWordView) {
                HotWordView categoryList = (HotWordView) itemListener;
                AppDebug.v(TAG, TAG + ".onKeyDown.keyCode = " + keyCode);
                if (categoryList.onInnerKey(keyCode, event)) {
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
