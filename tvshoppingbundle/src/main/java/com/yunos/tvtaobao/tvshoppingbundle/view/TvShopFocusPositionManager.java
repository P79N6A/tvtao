/**
 * $
 * PROJECT NAME: MouseTest
 * PACKAGE NAME: com.example.mousetest
 * FILE NAME: TvShopFocusPositionManager.java
 * CREATED TIME: 2016年1月11日
 * COPYRIGHT: Copyright(c) 2013 ~ 2016 All Rights Reserved.
 */
package com.yunos.tvtaobao.tvshoppingbundle.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.yunos.tv.app.widget.focus.FocusPositionManager;

public class TvShopFocusPositionManager extends FocusPositionManager {

    public TvShopFocusPositionManager(Context context) {
        super(context);
    }

    public TvShopFocusPositionManager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public TvShopFocusPositionManager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
