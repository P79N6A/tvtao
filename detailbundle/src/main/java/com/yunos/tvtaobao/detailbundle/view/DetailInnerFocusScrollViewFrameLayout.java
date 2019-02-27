package com.yunos.tvtaobao.detailbundle.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.detailbundle.listener.InnerFocusListener;

public class DetailInnerFocusScrollViewFrameLayout extends DetailScrollViewFrameLayout {

    protected final String TAG = "DetailInnerFocusScrollViewFrameLayout";
    private boolean mItemInnerFocusState;

    public DetailInnerFocusScrollViewFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initFocusScrollerLinearLayout();
    }

    public DetailInnerFocusScrollViewFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFocusScrollerLinearLayout();
    }

    public DetailInnerFocusScrollViewFrameLayout(Context context) {
        super(context);
        initFocusScrollerLinearLayout();
    }
    
    public void clearInnerFocusState() {
        mItemInnerFocusState = false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        AppDebug.i(TAG, "inerfoucs --> onKeyUp --> keyCode = " + keyCode + "; mItemInnerFocusState = "
                + mItemInnerFocusState);
        if (mItemInnerFocusState) {
            View selectedView = getSelectedView();
            if (selectedView instanceof InnerFocusListener) {
                InnerFocusListener focusView = (InnerFocusListener) selectedView;
                return focusView.onKeyUp(keyCode, event);
            }
        } else {
            if (checkKeyCode(keyCode, event)) {
                View selectedView = getSelectedView();
                if (selectedView instanceof InnerFocusListener) {
                    InnerFocusListener focusView = (InnerFocusListener) selectedView;
                    if (focusView.onKeyUp(keyCode, event)) {
                        return true;
                    }
                }
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AppDebug.i(TAG, "inerfoucs --> onKeyDown --> keyCode = " + keyCode + "; mItemInnerFocusState = "
                + mItemInnerFocusState);
        if (getChildCount() <= 0) {
            return super.onKeyDown(keyCode, event);
        }

        // 在item的内部处理
        if (mItemInnerFocusState) {
            View selectedView = getSelectedView();
            if (selectedView instanceof InnerFocusListener) {
                InnerFocusListener focusView = (InnerFocusListener) selectedView;
                if (focusView.onKeyDown(keyCode, event)) {
                    return true;
                }
            } else {
                mItemInnerFocusState = false;
            }
        } else {
            if (checkKeyCode(keyCode, event)) {
                View selectedView = getSelectedView();
                if (selectedView instanceof InnerFocusListener) {
                    InnerFocusListener focusView = (InnerFocusListener) selectedView;
                    if (focusView.onKeyDown(keyCode, event)) {
                        return true;
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        AppDebug.i(TAG, "inerfoucs --> preOnKeyDown --> keyCode = " + keyCode + "; event = " + event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                // 从外部focus切换到内部focus
                if (!mItemInnerFocusState) {
                    View selectedView = getSelectedView();
                    if (selectedView instanceof InnerFocusListener) {
                        InnerFocusListener focusView = (InnerFocusListener) selectedView;
                        // 如果能找到focus就进入内部选中模式
                        if (focusView.findNextFocus(keyCode, event)) {
                            mItemInnerFocusState = true;
                            return true;
                        }
                    }
                } else {
                    // 如果已经在进入到内部就在内部进行focus
                    if (innerFocus(keyCode, event)) {
                        return true;
                    }
                }
            default:
                break;
        }

        return super.preOnKeyDown(keyCode, event);
    }

    /**
     * 进入到内部进行focus
     * @param keyCode
     * @param event
     * @return
     */
    private boolean innerFocus(int keyCode, KeyEvent event) {
        if (mItemInnerFocusState) {
            View selectedView = getSelectedView();
            if (selectedView instanceof InnerFocusListener) {
                InnerFocusListener focusView = (InnerFocusListener) selectedView;
                // 如果能找到focus就进入内部选中模式
                if (focusView.findNextFocus(keyCode, event)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void initFocusScrollerLinearLayout() {
        mParams = new Params(1.05f, 1.05f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());

        setOnItemSelectedListener(new ItemSelectedListener() {

            @Override
            public void onItemSelected(View v, int position, boolean isSelected, View view) {

                AppDebug.i(TAG, "inerfoucs --> initSelectedListener --> v = " + v + "; isSelected = " + isSelected);

                if (!isSelected) {
                    View selectedView = getSelectedView();
                    // 清除内部相关内容
                    if (selectedView instanceof InnerFocusListener) {
                        InnerFocusListener focusView = (InnerFocusListener) selectedView;
                        focusView.clearItemSelected(false, false);
                    }
                    mItemInnerFocusState = false;
                } else {
                    View selectedView = getSelectedView();
                    if (selectedView instanceof InnerFocusListener) {
                        InnerFocusListener focusView = (InnerFocusListener) selectedView;
                        focusView.performItemSelect(true);
                    }
                }
            }
        });
    }

    private boolean checkKeyCode(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isScale() {
        return true;
    }
}
