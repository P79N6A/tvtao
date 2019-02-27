/**
 * $
 * PROJECT NAME: business-view
 * PACKAGE NAME: com.yunos.tvtaobao.biz.widget
 * FILE NAME: DialogFocusPositionManager.java
 * CREATED TIME: 2015年6月2日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.tradelink.dialog;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.yunos.tv.app.widget.focus.FocusPositionManager;

/**
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2015年6月2日 下午8:56:38
 */
public class DialogFocusPositionManager extends FocusPositionManager {

    private Button mPositiveButton;
    private Button mNegativeButton;
    private CUR_SELECT mCurSelect;

    private enum CUR_SELECT {
        POSITIVE_BTN, NEGATIVE_BTN
    }

    public DialogFocusPositionManager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DialogFocusPositionManager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DialogFocusPositionManager(Context context) {
        super(context);
    }

    /**
     * 必须通过外部来调用，在本类的构造函数中调用获取不到子控件
     */
    public void initView() {
        View view = findViewById(com.yunos.tvtaobao.businessview.R.id.super_parent);
        mPositiveButton = (Button) view.findViewById(com.yunos.tvtaobao.businessview.R.id.positiveButton);
        mNegativeButton = (Button) view.findViewById(com.yunos.tvtaobao.businessview.R.id.negativeButton);
        mCurSelect = CUR_SELECT.POSITIVE_BTN;
        changeBtnSelectStatus();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (mCurSelect == CUR_SELECT.POSITIVE_BTN) {
                    if (mNegativeButton != null && mNegativeButton.getVisibility() == View.VISIBLE) {
                        mCurSelect = CUR_SELECT.NEGATIVE_BTN;
                        changeBtnSelectStatus();
                    }
                }
                return true;
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                if (mCurSelect == CUR_SELECT.NEGATIVE_BTN) {
                    if (mPositiveButton != null && mPositiveButton.getVisibility() == VISIBLE) {
                        mCurSelect = CUR_SELECT.POSITIVE_BTN;
                        changeBtnSelectStatus();
                    }
                }
                return true;
            }
        }

        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                || event.getKeyCode() == KeyEvent.KEYCODE_NUMPAD_ENTER) {
            if (mCurSelect == CUR_SELECT.POSITIVE_BTN) {
                if (mPositiveButton != null) {
                    mPositiveButton.dispatchKeyEvent(event);
                }
            } else {
                if (mNegativeButton != null) {
                    mNegativeButton.dispatchKeyEvent(event);
                }
            }
        }

        return super.dispatchKeyEvent(event);
    }

    /**
     * 改变按钮的状态
     */
    private void changeBtnSelectStatus() {
        if (mPositiveButton == null || mNegativeButton == null) {
            return;
        }

        if (mCurSelect == CUR_SELECT.POSITIVE_BTN) {
            mPositiveButton.setTextColor(getResources().getColor(com.yunos.tvtaobao.businessview.R.color.ytbv_white));
            mPositiveButton.setBackgroundColor(getResources().getColor(com.yunos.tvtaobao.businessview.R.color.ytbv_button_focus));
            mNegativeButton.setTextColor(getResources().getColor(com.yunos.tvtaobao.businessview.R.color.ytbv_unfocus_text_color));
            mNegativeButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        } else {
            mPositiveButton.setTextColor(getResources().getColor(com.yunos.tvtaobao.businessview.R.color.ytbv_unfocus_text_color));
            mPositiveButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            mNegativeButton.setTextColor(getResources().getColor(com.yunos.tvtaobao.businessview.R.color.ytbv_white));
            mNegativeButton.setBackgroundColor(getResources().getColor(com.yunos.tvtaobao.businessview.R.color.ytbv_button_focus));
        }
    }
}
