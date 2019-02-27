package com.yunos.tvtaobao.detailbundle.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.yunos.tv.app.widget.focus.FocusRelativeLayout;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.detailbundle.listener.InnerFocusListener;

public class InnerGroupFocusRelativeLayout extends FocusRelativeLayout {

    private boolean mItemInnerFocusState;
    private ItemSelectedListener mOnInnerSelectedListener = null;

    public InnerGroupFocusRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initFocusScrollerLinearLayout();
    }

    public InnerGroupFocusRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFocusScrollerLinearLayout();
    }

    public InnerGroupFocusRelativeLayout(Context context) {
        super(context);
        initFocusScrollerLinearLayout();
    }

    public void clearInnerFocusState() {
        mItemInnerFocusState = false;
    }
    
    public void setInnerSelectedListener(ItemSelectedListener listener) {
        this.mOnInnerSelectedListener = listener;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        AppDebug.i(TAG, "inerfoucs --> onKeyUp --> keyCode = " + keyCode + "; mItemInnerFocusState = "
                + mItemInnerFocusState);

        View selectedView = getSelectedView();
        AppDebug.i(TAG, "inerfoucs --> onKeyUp --> selectedView = " + selectedView);
        
        if (selectedView != null) {

            if (selectedView instanceof InnerFocusFrameLayout) {
                InnerFocusFrameLayout innerFocusFrameLayout = (InnerFocusFrameLayout) selectedView;
                if (innerFocusFrameLayout.onKeyUp(keyCode, event)) {
                    return true;
                }
            }
            
            if (mItemInnerFocusState) { 
                if (selectedView instanceof InnerFocusListener) {
                    InnerFocusListener focusView = (InnerFocusListener) selectedView;
                    return focusView.onKeyUp(keyCode, event);
                }
            } else {
                if (checkKeyCode(keyCode, event)) {
                    if (selectedView instanceof InnerFocusListener) {
                        InnerFocusListener focusView = (InnerFocusListener) selectedView;
                        if (focusView.onKeyUp(keyCode, event)) {
                            return true;
                        }
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

        View selectedView = getSelectedView();

        AppDebug.i(TAG, "inerfoucs --> onKeyDown --> selectedView = " + selectedView);

        if (selectedView != null) {
            if (selectedView instanceof InnerFocusFrameLayout) {
                InnerFocusFrameLayout innerFocusFrameLayout = (InnerFocusFrameLayout) selectedView;
                if (innerFocusFrameLayout.onKeyDown(keyCode, event)) {
                    return true;
                }
            }

            // 在item的内部处理
            if (mItemInnerFocusState) {
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
                    if (selectedView instanceof InnerFocusListener) {
                        InnerFocusListener focusView = (InnerFocusListener) selectedView;
                        if (focusView.onKeyDown(keyCode, event)) {
                            return true;
                        }
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
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            //case KeyEvent.KEYCODE_DPAD_LEFT:
            //case KeyEvent.KEYCODE_DPAD_RIGHT:
                
                View selectedView = getSelectedView();
                
                if (selectedView != null && selectedView instanceof InnerFocusFrameLayout) {
                    return true;
                }

                // 从外部focus切换到内部focus
                if (!mItemInnerFocusState) {
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
                break;
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

    /**
     * 
     */
    private void initFocusScrollerLinearLayout() {

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
                    } else {
                       if (mOnInnerSelectedListener != null) {
                           mOnInnerSelectedListener.onItemSelected(v, position, isSelected, view);
                       } 
                    }
                    mItemInnerFocusState = false;
                } else {
                    View selectedView = getSelectedView();
                    if (selectedView instanceof InnerFocusListener) {
                        InnerFocusListener focusView = (InnerFocusListener) selectedView;
                        focusView.performItemSelect(true);
                    }  else {
                        if (mOnInnerSelectedListener != null) {
                            mOnInnerSelectedListener.onItemSelected(v, position, isSelected, view);
                        } 
                     }
                }
            }
        });
    }

    /**
     * 判断是否是ok键
     * @param keyCode
     * @param event
     * @return
     */
    private boolean checkKeyCode(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER
                || keyCode == KeyEvent.KEYCODE_ENTER) {
            return true;
        }
        return false;
    }
}
