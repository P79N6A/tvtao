package com.yunos.tvtaobao.biz.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.yunos.tv.app.widget.FocusFinder;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;

import java.util.ArrayList;

/**
 * 在内部进行查找可以选中的子View，选中的状态也只改变了selected的状态
 * @author tingmeng.ytm
 */
public class InnerFocusLayout extends RelativeLayout implements ItemListener {
    private final String TAG = "InnerFocusLayout";
    private InnerFocusFinder mFocusFinder; // 查找器
    private View mSelectedView; // 选中的View
    private View mNextFocus; // 已经查找到的View
    protected FocusRectParams mFocusRectparams; // focus区域参数
    private OnInnerItemSelectedListener mOnInnerItemSelectedListener; // 选中子View的监听回调
    public InnerFocusLayout(Context context) {
        super(context);
        initLayout(context);
    }

    public InnerFocusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public InnerFocusLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context);
    }
    
    public void setOnInnerItemSelectedListener(OnInnerItemSelectedListener listener){
        mOnInnerItemSelectedListener = listener;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ((KeyEvent.KEYCODE_DPAD_CENTER == keyCode || KeyEvent.KEYCODE_ENTER == keyCode || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
            if (isPressed()) { // by zhangle:　click事件需要onkeydown+onkeyup
                setPressed(false);
                if (mSelectedView != null) {
                    mSelectedView.performClick();
                }
            }
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    /**
     * 设置上次找到的Focus为selected状态
     */
    public void setNextFocusSelected(){
        if (mNextFocus != null && mNextFocus.isFocusable()) {
            performItemSelect(mSelectedView, false);
            mSelectedView = mNextFocus;
            mNextFocus = null;
            performItemSelect(mSelectedView, true);
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
            this.setPressed(true); // by zhangle:click事件需要onkeydown + onkeyup
            return true;
        }
        if (mNextFocus != null && mNextFocus.isFocusable()) {
            performItemSelect(mSelectedView, false);
            mSelectedView = mNextFocus;
            mNextFocus = null;
            performItemSelect(mSelectedView, true);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (views == null) {
            return;
        }
        if (!isFocusable()) {
            return;
        }
        if ((focusableMode & FOCUSABLES_TOUCH_MODE) == FOCUSABLES_TOUCH_MODE && isInTouchMode() && !isFocusableInTouchMode()) {
            return;
        }
        views.add(this);
    }

    @Override
    public void getFocusedRect(Rect r) {
        View item = mSelectedView;
        if (hasFocus()) {
            if (item != null) {
                item.getFocusedRect(r);
                this.offsetDescendantRectToMyCoords(item, r);
                return;
            }
        }
        super.getFocusedRect(r);
    }    

    /**
     * 取得内部首次focus的View
     * @return
     */
    protected View getFirstFocusView(){
        return null;
    }
    
    /**
     * 查找第一个View
     * @param keyCode
     * @return
     */
    public boolean findFirstFocus(int keyCode){
        Rect preRect = new Rect();
        View nextFocus = getFirstFocusView();
        if (nextFocus == null) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    preRect.set(getWidth(), 0, getWidth() + 1, getHeight());
                    nextFocus = this.mFocusFinder.findNextFocusFromRect(this, preRect, FOCUS_LEFT);
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    preRect.set(-1, 0, 0, getHeight());
                    nextFocus = this.mFocusFinder.findNextFocusFromRect(this, preRect, FOCUS_RIGHT);
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    preRect.set(0, -1, getWidth(), 0);
                    nextFocus = this.mFocusFinder.findNextFocusFromRect(this, preRect, FOCUS_DOWN);
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    preRect.set(0, getHeight(), getWidth(), getHeight() + 1);
                    nextFocus = this.mFocusFinder.findNextFocusFromRect(this, preRect, FOCUS_UP);
                    break;
                default:
                    return false;
            }
        }
        mNextFocus = nextFocus;
        if (nextFocus != null) {
            return true;
        } else {
            Log.w(TAG, "findFirstFocus can not find the new focused");
            return false;
        }
    }
    
    /**
     * 查找下一个
     * @param keyCode
     * @param event
     * @return
     */
    public boolean findNextFocus(int keyCode, KeyEvent event) {
        Log.d(TAG, "preOnKeyDown keyCode = " + keyCode);

        View selectedView = mSelectedView;
        View nextFocus = null;
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_LEFT:
            nextFocus = mFocusFinder.findNextFocus(this, selectedView, FOCUS_LEFT);
            break;
        case KeyEvent.KEYCODE_DPAD_RIGHT:
            nextFocus = mFocusFinder.findNextFocus(this, selectedView, FOCUS_RIGHT);
            break;
        case KeyEvent.KEYCODE_DPAD_DOWN:
            nextFocus = mFocusFinder.findNextFocus(this, selectedView, FOCUS_DOWN);
            break;
        case KeyEvent.KEYCODE_DPAD_UP:
            nextFocus = mFocusFinder.findNextFocus(this, selectedView, FOCUS_UP);
            break;
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_ENTER:
        case KeyEvent.KEYCODE_NUMPAD_ENTER:
            return false;
        default:
            return false;
        }

        mNextFocus = nextFocus;
        if (nextFocus != null) {
            return true;
        } else {
            Log.w(TAG, "findNextFocus can not find the new focused");
            return false;
        }
    }

    public boolean isChangedInnerKey(int keyCode){
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isScale() {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public FocusRectParams getFocusParams() {
        Rect r = new Rect();
        getFocusedRect(r);
        mFocusRectparams.set(r, 0.5f, 0.5f);
        return mFocusRectparams;
    }

    @Override
    public int getItemWidth() {
        return getWidth();
    }

    @Override
    public int getItemHeight() {
        return getHeight();
    }

    @Override
    public Rect getManualPadding() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void drawBeforeFocus(Canvas canvas) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void drawAfterFocus(Canvas canvas) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    public void clearItemSelected(){
        performItemSelect(mSelectedView, false);
        mSelectedView = null;
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 清除
        if (mFocusFinder != null) {
            mFocusFinder.clearFocusables();
        }
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        initNode();
    }

    /**
     * 初始化每个item的结点
     */
    protected void initNode() {
        mFocusFinder.clearFocusables();
        mFocusFinder.initFocusables(this);
    }
    
    /**
     * 初始化
     * @param conext
     */
    private void initLayout(Context conext) {
        mFocusFinder = new InnerFocusFinder();
        mFocusRectparams = new FocusRectParams();
    }
    
    /**
     * 选中指定的View
     * @param selectedView
     * @param selected
     */
    private void performItemSelect(View selectedView, boolean selected){
        if (selectedView != null) {
            selectedView.setSelected(selected);
            if (mOnInnerItemSelectedListener != null) {
                mOnInnerItemSelectedListener.onInnerItemSelected(selectedView, selected, this);
            }
        }
    }
    
    /**
     * 内部子View选中的监听方法
     * @author tingmeng.ytm
     *
     */
    public interface OnInnerItemSelectedListener{
        public void onInnerItemSelected(View view, boolean isSelected, View parentView);
    }
    
    /**
     * 在内部查找的Finder
     * @author tingmeng.ytm
     *
     */
    private class InnerFocusFinder extends FocusFinder {
        
        /**
         * 重写了初始化的方法，深入到子View里面添加
         */
        @Override
        public void initFocusables(ViewGroup root) {
            for (int index = 0; index < root.getChildCount(); index++) {
                View child = root.getChildAt(index);
                // ViewGroup自身不能focus，并且是可见的，进入查找子View
                if (child instanceof ViewGroup && !child.isFocusable() && child.getVisibility() == View.VISIBLE) {
                    // 递归回调
                    initFocusables((ViewGroup)child);
                } else {
                    // 添加的View是可Focus的，并且是可见的
                    if (child.isFocusable() && child.getVisibility() == View.VISIBLE) {
                        addFocusable(child);
                    }
                }
            }
        }
    }
}
