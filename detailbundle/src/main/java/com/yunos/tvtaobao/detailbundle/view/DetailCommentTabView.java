package com.yunos.tvtaobao.detailbundle.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.yunos.tv.app.widget.FocusFinder;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.detailbundle.listener.InnerFocusListener;

import java.util.ArrayList;

public class DetailCommentTabView extends RelativeLayout implements ItemListener, InnerFocusListener {

    private final String TAG = "DetailCommentTabView";
    private InnerFocusFinder mFocusFinder; // 查找器
    private View mSelectedView; // 选中的View
    private View mNextFocus; // 已经查找到的View
    private View mFirstFocusView;
    private OnInnerItemSelectedListener mOnInnerItemSelectedListener; // 选中子View的监听回调

    protected FocusRectParams mFocusRectparams = new FocusRectParams();

    private Paint mBackgroudPaint;

    private Paint mSelectViewPaint;
    private Rect mSelectViewRect;
    private boolean mDrawSelectColor;

    // 分割线的画笔 
    private Paint mDividerPaint;
    // 分割线的位置
    private boolean mDividerHorizontal;
    private float mDividerWidth;

    public DetailCommentTabView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context);
    }

    public DetailCommentTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public DetailCommentTabView(Context context) {
        super(context);
        initLayout(context);
    }

    public void setOnInnerItemSelectedListener(OnInnerItemSelectedListener listener) {
        mOnInnerItemSelectedListener = listener;
    }

    @Override
    public boolean isScale() {
        return false;
    }

    @Override
    public FocusRectParams getFocusParams() {
        Rect r = new Rect();
        getFocusedRect(r);
        adjustFocusRect(r);
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
        return null;
    }

    @Override
    public void drawBeforeFocus(Canvas canvas) {

    }

    @Override
    public void drawAfterFocus(Canvas canvas) {

    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public boolean findFirstFocus(int keyCode) {
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
        AppDebug.i(TAG, "inerfoucs --> findFirstFocus --> mNextFocus = " + mNextFocus);
        if (nextFocus != null) {
            return true;
        } else {
            AppDebug.w(TAG, "inerfoucs --> findFirstFocus can not find the new focused");
            return false;
        }
    }

    @Override
    public boolean findNextFocus(int keyCode, KeyEvent event) {
        if (mSelectedView == null) {
            mSelectedView = getFirstFocusView();
        }
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
                return true;
            default:
                return false;
        }

        mNextFocus = nextFocus;
        AppDebug.i(TAG, "inerfoucs --> findNextFocus --> mNextFocus = " + mNextFocus);
        if (nextFocus != null) {
            return true;
        } else {
            AppDebug.w(TAG, "inerfoucs --> findNextFocus can not find the new focused");
            return false;
        }
    }

    @Override
    public void setNextFocusSelected() {
        if (mNextFocus != null && mNextFocus.isFocusable()) {
            performItemSelect(mSelectedView, false, true);
            mSelectedView = mNextFocus;
            mNextFocus = null;
            performItemSelect(mSelectedView, true, true);
        }
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

    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (views == null) {
            return;
        }
        if (!isFocusable()) {
            return;
        }
        if ((focusableMode & FOCUSABLES_TOUCH_MODE) == FOCUSABLES_TOUCH_MODE && isInTouchMode()
                && !isFocusableInTouchMode()) {
            return;
        }
        views.add(this);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        AppDebug.i(TAG, "inerfoucs -->  onKeyUp --> mSelectedView = " + mSelectedView + "; isPressed() = "
                + isPressed() + "; keyCode = " + keyCode);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER
                || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
            this.setPressed(true); // by zhangle:click事件需要onkeydown + onkeyup
            return true;
        }
        AppDebug.i(TAG, "inerfoucs -->  onKeyDown --> mNextFocus = " + mNextFocus);
        if (mNextFocus != null && mNextFocus.isFocusable()) {
            performItemSelect(mSelectedView, false, true);
            mSelectedView = mNextFocus;
            mNextFocus = null;
            performItemSelect(mSelectedView, true, true);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 根据内部子view来调整Focus的区域
     * @param r
     */
    private void adjustFocusRect(Rect r) {
        int count = getChildCount();
        r.left = Integer.MAX_VALUE;
        r.right = Integer.MIN_VALUE;
        r.top = Integer.MAX_VALUE;
        r.bottom = Integer.MIN_VALUE;
        for (int index = 0; index < count; index++) {
            View view = getChildAt(index);
            if (view != null) {
                if (view.getVisibility() != View.VISIBLE) {
                    continue;
                }
                if (r.left > view.getLeft()) {
                    r.left = view.getLeft();
                }
                if (r.right < view.getRight()) {
                    r.right = view.getRight();
                }

                if (r.top > view.getTop()) {
                    r.top = view.getTop();
                }

                if (r.bottom < view.getBottom()) {
                    r.bottom = view.getBottom();
                }
            }
        }
    }

    /**
     * 取得内部首次focus的View
     * @return
     */
    protected View getFirstFocusView() {
        return mFirstFocusView;
    }

    public void setFirstFocusView(View v) {
        mFirstFocusView = v;
    }

    /**
     * 初始化
     * @param conext
     */
    private void initLayout(Context conext) {
        mFocusFinder = new InnerFocusFinder();
        mFocusRectparams = new FocusRectParams();
        mSelectViewRect = new Rect();
    }

    /**
     * 初始化每个item的结点
     */
    protected void initNode() {
        mFocusFinder.clearFocusables();
        mFocusFinder.initFocusables(this);
    }

    /**
     * 选中指定的View
     * @param selectedView
     * @param selected
     */
    private void performItemSelect(View selectedView, boolean selected, boolean fatherViewselected) {
        AppDebug.i(TAG, "inerfoucs --> performItemSelect --> selectedView = " + selectedView + "; selected = "
                + selected);
        if (selectedView != null) {
            selectedView.setSelected(selected);
            if (mOnInnerItemSelectedListener != null) {
                mOnInnerItemSelectedListener.onInnerItemSelected(selectedView, selected, fatherViewselected, this);
            }
        }
    }

    /**
     * 内部子View选中的监听方法
     */
    public interface OnInnerItemSelectedListener {

        public void onInnerItemSelected(View view, boolean isSelected, boolean fatherViewselected, View parentView);
    }

    /**
     * 在内部查找的Finder
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
                    initFocusables((ViewGroup) child);
                } else {
                    // 添加的View是可Focus的，并且是可见的
                    if (child.isFocusable() && child.getVisibility() == View.VISIBLE) {
                        addFocusable(child);
                    }
                }
            }
        }
    }

    @Override
    public void clearItemSelected(boolean fatherViewselected, boolean clearselectview) {
        performItemSelect(mSelectedView, false, fatherViewselected);
        if (clearselectview) {
            mSelectedView = null;
        }
    }

    @Override
    public void performItemSelect(boolean selected) {
        View selectview = mSelectedView;
        if (selectview == null) {
            selectview = getFirstFocusView();
        }

        AppDebug.i(TAG, "inerfoucs --> performItemSelect -->InnerFocusListener --> selected = " + selected
                + "; selectview = " + selectview);
        if (selectview != null) {
            mSelectedView = selectview;
            performItemSelect(selectview, selected, true);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mBackgroudPaint != null) {
            Rect drawRt = new Rect();
            adjustFocusRect(drawRt);
            canvas.drawRect(drawRt, mBackgroudPaint);
        }

        int count = getChildCount();
        if (mDividerPaint != null && count > 0) {
            for (int index = 0; index < count; index++) {
                if (index >= count - 1) {
                    // 最后一个不需要画分割线
                    break;
                }

                View view = getChildAt(index);
                if (view != null && view.getVisibility() == View.VISIBLE) {
                    if (mDividerHorizontal) {
                        canvas.drawLine(0.0f, view.getBottom(), getWidth(), view.getBottom(), mDividerPaint);
                    } else {
                        canvas.drawLine(view.getRight(), 0.0f, view.getRight(), getHeight(), mDividerPaint);
                    }
                }
            }
        }

        if (mSelectViewPaint != null && mDrawSelectColor) {
            Rect drawRt = getSelectViewFocusRect();
            AppDebug.i(TAG, "getSelectViewFocusRect --> drawRt = " + drawRt + "; mSelectedView = " + mSelectedView);
            if (drawRt != null && !drawRt.isEmpty()) {
                canvas.drawRect(drawRt, mSelectViewPaint);
            }
        }

        super.dispatchDraw(canvas);

    }

    private Rect getSelectViewFocusRect() {
        View select = mSelectedView;
        if (select != null) {
            mSelectViewRect.setEmpty();
            mSelectViewRect.set(select.getLeft(), select.getTop(), select.getRight(), select.getBottom());
            if (select.getLeft() > mDividerWidth) {
                mSelectViewRect.left = select.getLeft() - (int) mDividerWidth;
            }
            if (select.getTop() > mDividerWidth) {
                mSelectViewRect.top = select.getTop() - (int) mDividerWidth;
            }

            if (select.getRight() + mDividerWidth < getRight()) {
                mSelectViewRect.right = select.getRight() + (int) mDividerWidth;
            }
            if (select.getBottom() + mDividerWidth < getBottom()) {
                mSelectViewRect.bottom = select.getBottom() + (int) mDividerWidth;
            }

            return mSelectViewRect;
        }
        return null;
    }

    
    public void setBackgroudColor(int color) {
        if (mBackgroudPaint == null) {
            // 创建Paint
            mBackgroudPaint = new Paint();
            // 设置画笔风格 
            mBackgroudPaint.setStyle(Paint.Style.FILL);
            mBackgroudPaint.setStrokeJoin(Paint.Join.ROUND);
            // 设置画笔方形
            mBackgroudPaint.setStrokeCap(Paint.Cap.SQUARE);
            mBackgroudPaint.setDither(true);
        }
        mBackgroudPaint.setColor(color);
        invalidate();
    }

    public void setSelectViewBackgroudColor(int color) {
        if (mSelectViewPaint == null) {
            // 创建Paint
            mSelectViewPaint = new Paint();
            // 设置画笔风格 
            mSelectViewPaint.setStyle(Paint.Style.FILL);
            mSelectViewPaint.setStrokeJoin(Paint.Join.ROUND);
            // 设置画笔方形
            mSelectViewPaint.setStrokeCap(Paint.Cap.SQUARE);
            mSelectViewPaint.setDither(true);
        }
        AppDebug.i(TAG, "setSelectViewBackgroudColor --> color = " + color + "; mSelectViewPaint = " + mSelectViewPaint);
        mSelectViewPaint.setColor(color);
        invalidate();
    }
    
    public void setDrawSelectColor(boolean select){
        mDrawSelectColor = select;
        invalidate();
        AppDebug.i(TAG, "setDrawSelectColor --> select = " + select + "; mSelectViewPaint = " + mSelectViewPaint);
    }

    /**
     * 设置分割线
     */
    public void setDividerDrawable(int dividercolor, float dividerhight) {
        if (mDividerPaint == null) {
            // 创建Paint
            mDividerPaint = new Paint();
            // 设置画笔风格 
            mDividerPaint.setStyle(Paint.Style.STROKE);
            mDividerPaint.setStrokeJoin(Paint.Join.ROUND);
            // 设置画笔方形
            mDividerPaint.setStrokeCap(Paint.Cap.SQUARE);
            mDividerPaint.setDither(true);
            // 设置使用抗锯齿功能
            mDividerPaint.setAntiAlias(true);
        }
        mDividerWidth = dividerhight;
        mDividerPaint.setColor(dividercolor);
        mDividerPaint.setStrokeWidth(mDividerWidth);
        invalidate();
    }

    /**
     * 画水平线
     * @param divider
     */
    public void setDividerHorizontal(boolean divider) {
        mDividerHorizontal = divider;
    }
}
