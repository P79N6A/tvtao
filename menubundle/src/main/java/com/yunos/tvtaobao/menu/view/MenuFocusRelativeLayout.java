/**
 * $
 * PROJECT NAME: TVTaobao
 * PACKAGE NAME: com.yunos.tvtaobao.view
 * FILE NAME: MenuFocusRelativeLayout.java
 * CREATED TIME: 2015年3月19日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.menu.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.yunos.tv.app.widget.focus.FocusRelativeLayout;

/**
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2015年3月19日 下午8:21:11
 */
public class MenuFocusRelativeLayout extends FocusRelativeLayout {

    public MenuFocusRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MenuFocusRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuFocusRelativeLayout(Context context) {
        super(context);
    }

    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
                return true;
            default:
                return super.preOnKeyDown(keyCode, event);
        }
    }
}
