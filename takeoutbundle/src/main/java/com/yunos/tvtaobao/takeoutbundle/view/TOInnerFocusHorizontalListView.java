package com.yunos.tvtaobao.takeoutbundle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;

import com.yunos.tvtaobao.biz.widget.InnerFocusLayout;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_FLING;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

/**
 * 进入到每个item内部的Focus
 * Focus框不会发生变化只是对内部子View的selected状态做出改变
 *
 */
public class TOInnerFocusHorizontalListView extends TOFocusHorizontalListView {
    private boolean mItemInnerFocusState;

    public TOInnerFocusHorizontalListView(Context context) {
        super(context);
    }

    public TOInnerFocusHorizontalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TOInnerFocusHorizontalListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void clearInnerFocusState() {
        mItemInnerFocusState = false;
    }

    @Override
    protected void performSelect(boolean select) {
        if (!select) {
            View selectedView = getSelectedView();
            // 清除内部相关内容
            if (selectedView instanceof InnerFocusLayout) {
                InnerFocusLayout focusView = (InnerFocusLayout)selectedView;
                focusView.clearItemSelected();
            }
            mItemInnerFocusState = false;
        }
        super.performSelect(select);
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mItemInnerFocusState) {
            View selectedView = getSelectedView();
            if (selectedView instanceof InnerFocusLayout) {
                InnerFocusLayout focusView = (InnerFocusLayout)selectedView;
                return focusView.onKeyUp(keyCode, event);
            }
        }
        return super.onKeyUp(keyCode, event);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getChildCount() <= 0) {
            return super.onKeyDown(keyCode, event);
        }
        if (checkState(keyCode)) {
            return true;
        }
        // 在item的内部处理
        if (mItemInnerFocusState) {
            View selectedView = getSelectedView();
            if (selectedView instanceof InnerFocusLayout) {
                InnerFocusLayout focusView = (InnerFocusLayout)selectedView;
                if (focusView.onKeyDown(keyCode, event)) {
                    return true;
                }
            } else {
                mItemInnerFocusState = false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    
    /**
     * 手动查看内部的focus对象
     * @param keyCode 按键值
     */
    public void manualFindFocusInner(int keyCode){
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_LEFT:
        case KeyEvent.KEYCODE_MOVE_HOME: 
        case KeyEvent.KEYCODE_DPAD_RIGHT:
        case KeyEvent.KEYCODE_MOVE_END:
            break;
        case KeyEvent.KEYCODE_DPAD_UP:
        case KeyEvent.KEYCODE_DPAD_DOWN:
            // 从外部focus切换到内部focus
            if (!mItemInnerFocusState) {
                View selectedView = getSelectedView();
                if (selectedView instanceof InnerFocusLayout) {
                    InnerFocusLayout focusView = (InnerFocusLayout)selectedView;
                    // 如果能找到focus就进入内部选中模式
                    if (focusView.isChangedInnerKey(keyCode) && focusView.findFirstFocus(keyCode)) {
                        mItemInnerFocusState = true;
                        focusView.setNextFocusSelected();
                    }
                }
            }
        default:
            break;
        }
    }
    
    /**
     * 处理内部Focus的动作
     * @param keyCode
     * @param event
     * @return boolean true 成功处理完成
     */
    public boolean actionInnerFocus(int keyCode, KeyEvent event){
        if (mItemInnerFocusState && innerFocus(keyCode, event)) {
            View selectedView = getSelectedView();
            if (selectedView instanceof InnerFocusLayout) {
                InnerFocusLayout focusView = (InnerFocusLayout)selectedView;
                focusView.setNextFocusSelected();
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        if (checkState(keyCode)) {
            return false;
        }
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_LEFT:
        case KeyEvent.KEYCODE_MOVE_HOME: 
        case KeyEvent.KEYCODE_DPAD_RIGHT:
        case KeyEvent.KEYCODE_MOVE_END:
        {
            if (innerFocus(keyCode, event)) {
                return true;
            }
            break;
        }
        case KeyEvent.KEYCODE_DPAD_UP:
        case KeyEvent.KEYCODE_DPAD_DOWN:
            // 从外部focus切换到内部focus
            if (!mItemInnerFocusState) {
                View selectedView = getSelectedView();
                if (selectedView instanceof InnerFocusLayout) {
                    InnerFocusLayout focusView = (InnerFocusLayout)selectedView;
                    // 如果能找到focus就进入内部选中模式
                    if (focusView.isChangedInnerKey(keyCode) && focusView.findFirstFocus(keyCode)) {
                        mItemInnerFocusState = true;
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                // 如果已经在进入到内部就在内部进行focus
                if (innerFocus(keyCode, event)) {
                    return true;
                } else {
                    return false;
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
    private boolean innerFocus(int keyCode, KeyEvent event){
        if (mItemInnerFocusState) {
            View selectedView = getSelectedView();
            if (selectedView instanceof InnerFocusLayout) {
                InnerFocusLayout focusView = (InnerFocusLayout)selectedView;
                // 如果能找到focus就进入内部选中模式
                if (focusView.findNextFocus(keyCode, event)) {
                    return true;
                }
            }
        }
        return false;
    }
}
