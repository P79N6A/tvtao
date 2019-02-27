package com.yunos.tvtaobao.juhuasuan.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedBasePositionManager.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScrollFocusedFrameLayout extends FrameLayout implements PositionInterface, ContainInterface {

    public static final String TAG = "FocusedFrameLayout";
    private static final boolean DEBUG = true;

    /**
     * Layout横向滚动模式:仅将当前项显示出来 {@link #setHorizontalMode(int)}
     */
    public static final int HORIZONTAL_SINGEL = 1;
    /**
     * Layout横向滚动模式:滚动整个GridView,类似翻页滚动{@link #setHorizontalMode(int)}
     */
    public static final int HORIZONTAL_FULL = 2;

    public static final int HORIZONTAL_OUTSIDE_SINGEL = 3;

    public static final int HORIZONTAL_OUTSIDE_FULL = 4;

    private static final int SCROLL_DURATION = 100;

    private long KEY_INTERVEL = 20;// ms
    private long mKeyTime = 0;

    public int mIndex = -1;

    private int mScreenWidth;
    private int mViewRight = 20;
    private int mViewLeft = 0;

    private int mMinLeft;
    private int mMinScaledLeft;
    private int mMaxLeft;
    private int mMaxScaledLeft;

    private int mMinRight;
    private int mMinScaledRight;
    private int mMaxRight;
    private int mMaxScaledRight;

    private int mMinTop;
    private int mMinScaledTop;
    private int mMaxTop;
    private int mMaxScaledTop;

    private int mMinBottom;
    private int mMinScaledBottom;
    private int mMaxBottom;
    private int mMaxScaledBottom;

    private int mHorizontalMode = -1;
    private FocusItemSelectedListener mOnItemSelectedListener = null;
    private FocusedBasePositionManager mPositionManager;
    private FocusFinder mFocusFinder;
    private int mLastScrollState = OnScrollListener.SCROLL_STATE_IDLE;
    private boolean mAutoSearchFocus = false;
    private OutsideScrollListener mOutsideScrollListener = null;
    private boolean mForcedFocus = false;
    private Map<View, NodeInfo> mNodeMap = new HashMap<View, NodeInfo>();
    private FocusStateListener mFocusStateListener = null;
    private View mLastSelectedView = null;

    private boolean mKeyCon = false;

    public void setOnItemSelectedListener(FocusItemSelectedListener listener) {
        this.mOnItemSelectedListener = listener;
    }

    private void performItemSelect(View v, boolean isSelected) {
        if (this.mOnItemSelectedListener != null) {
            this.mOnItemSelectedListener.onItemSelected(v, mIndex, isSelected, this);
        }
    }

    public void setAutoSearchFocus(boolean autoSearchFocus) {
        this.mAutoSearchFocus = autoSearchFocus;
    }

    public void setOutsideScrollListener(OutsideScrollListener l) {
        this.mOutsideScrollListener = l;
    }

    public void setFocusStateListener(FocusStateListener l) {
        this.mFocusStateListener = l;
    }

    public void setScrolling(boolean isScrolling) {
        this.mPositionManager.setScrolling(isScrolling);
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {

    }

    @Override
    public void createPositionManager(FocusParams params) {
        this.mPositionManager = FocusedBasePositionManager.createPositionManager(params, this);

        //        this.mPositionManager.onSetFocusedFrameLayout(this);
    }

    /**
     * 设置横向滚动模式 {@link #HORIZONTAL_SINGEL} {@link #HORIZONTAL_FULL} {@link #HORIZONTAL_OUTSIDE_SINGEL}
     * {@link #HORIZONTAL_OUTSIDE_FULL}
     * @param mode
     */
    public void setHorizontalMode(int mode) {
        this.mHorizontalMode = mode;
    }

    /**
     * 当滚动模式为{@link #HORIZONTAL_SINGEL}时, 焦点移动到最右侧的元素时和右边界的距离
     * @param right
     *            和右边界的距离
     */
    public void setViewRight(int right) {
        mViewRight = right;
    }

    public int getViewRight() {
        return this.mViewRight;
    }

    /**
     * 当滚动模式为{@link #HORIZONTAL_SINGEL}时, 焦点移动到最左侧的元素时和左边界的距离
     * @param
     */
    public void setViewLeft(int right) {
        mViewLeft = right;
    }

    public int getViewLeft() {
        return this.mViewLeft;
    }

    public ScrollFocusedFrameLayout(Context contxt) {
        super(contxt);
        setChildrenDrawingOrderEnabled(true);
        mScreenWidth = contxt.getResources().getDisplayMetrics().widthPixels;
        mFocusFinder = new FocusFinder();
    }

    public ScrollFocusedFrameLayout(Context contxt, AttributeSet attrs) {
        super(contxt, attrs);
        setChildrenDrawingOrderEnabled(true);
        mScreenWidth = contxt.getResources().getDisplayMetrics().widthPixels;
        mFocusFinder = new FocusFinder();
    }

    public ScrollFocusedFrameLayout(Context contxt, AttributeSet attrs, int defStyle) {
        super(contxt, attrs, defStyle);
        setChildrenDrawingOrderEnabled(true);
        mScreenWidth = contxt.getResources().getDisplayMetrics().widthPixels;
        mFocusFinder = new FocusFinder();
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        int selectedIndex = mIndex;
        if (selectedIndex < 0) {
            return i;
        }

        if (i < selectedIndex) {
            return i;
        } else if (i >= selectedIndex) {
            return childCount - 1 - i + selectedIndex;
        } else {
            return i;
        }
    }

    protected boolean mNeedInit = true;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        super.onLayout(changed, l, t, r, b);

        init();
    }

    protected void setNeedInit(boolean needInit) {
        this.mNeedInit = needInit;
    }

    public static final int MIN_VALUE = 0x80000000;
    public static final int MAX_VALUE = 0x7fffffff;

    protected void init() {
        if (DEBUG) {
            Log.d(TAG, "getChildCount()   = " + this.getChildCount());
        }

        if (!mNeedInit || this.mPositionManager == null) {
            return;
        }
        this.mNodeMap.clear();
        this.mMinLeft = MAX_VALUE;
        this.mMaxLeft = MIN_VALUE;

        this.mMinRight = MAX_VALUE;
        this.mMaxRight = MIN_VALUE;

        this.mMinTop = MAX_VALUE;
        this.mMaxTop = MIN_VALUE;

        this.mMinBottom = MAX_VALUE;
        this.mMaxBottom = MIN_VALUE;

        View minLeftView = null;
        View maxLeftView = null;

        View minRightView = null;
        View maxRightView = null;

        View minTopView = null;
        View maxTopView = null;

        View minBottomView = null;
        View maxBottomView = null;

        for (int index = 0; index < this.getChildCount(); index++) {
            View child = this.getChildAt(index);
            if (!child.isFocusable()) {
                AppDebug.i(TAG, TAG + ".init child is unfocusable! child=" + child);
                continue;
            }

            if (!(child instanceof ItemInterface)) {
                AppDebug.i(TAG, TAG + ".init child is not ItemInterface! child=" + child);
                continue;
            }

            if (!this.mNodeMap.containsKey(child)) {
                NodeInfo info = new NodeInfo();
                info.index = index;
                this.mNodeMap.put(child, info);
                AppDebug.i(TAG, TAG + ".init mNodeMap index=" + index + ", add child=" + child);
            }

            // for left
            if (child.getLeft() < this.mMinLeft) {
                minLeftView = child;
                this.mMinLeft = child.getLeft();
            }

            if (child.getLeft() > this.mMaxLeft) {
                maxLeftView = child;
                this.mMaxLeft = child.getLeft();
            }

            // for right
            if (child.getRight() < this.mMinRight) {
                minRightView = child;
                this.mMinRight = child.getRight();
            }

            if (child.getRight() > this.mMaxRight) {
                maxRightView = child;
                this.mMaxRight = child.getRight();
            }

            // for top
            if (child.getTop() < this.mMinTop) {
                minTopView = child;
                this.mMinTop = child.getTop();
            }

            if (child.getTop() > this.mMaxTop) {
                maxTopView = child;
                this.mMaxTop = child.getTop();
            }

            // for bottom
            if (child.getBottom() < this.mMinBottom) {
                minBottomView = child;
                this.mMinBottom = child.getBottom();
            }

            if (child.getBottom() > this.mMaxBottom) {
                maxBottomView = child;
                this.mMaxBottom = child.getBottom();
            }
        }

        if (DEBUG) {
            Log.i(TAG, TAG + ".init mNodeMap =" + mNodeMap.size());
            Log.d(TAG, "init: mMinLeft = " + mMinLeft + ", mMaxLeft = " + mMaxLeft + ", mMinRight = " + mMinRight
                    + ", mMaxRight = " + mMaxRight + ", mMinTop = " + mMinTop + ", mMaxTop = " + mMaxTop
                    + ", mMinBottom = " + mMinBottom + ", mMaxBottom = " + mMaxBottom);

            Log.d(TAG, "init: minLeftView = " + minLeftView + ", maxLeftView = " + maxLeftView + ", minRightView = "
                    + minRightView + ", maxRightView = " + maxRightView + ", minTopView = " + minTopView
                    + ", maxTopView = " + maxTopView + ", minBottomView = " + minBottomView + ", maxBottomView = "
                    + maxBottomView);
        }

        // maxItemRightView maybe null! 代码中动态addView的情况，初始时childcount为0. by
        // leiming32
        if (this.getChildCount() > 0) {
            float scale = this.mPositionManager.computeScaleXY((ItemInterface) minLeftView);
            this.mMinScaledLeft = this.mPositionManager.getDstRect((ItemInterface) minLeftView, scale, scale).left;
            scale = this.mPositionManager.computeScaleXY((ItemInterface) maxLeftView);
            this.mMaxScaledLeft = this.mPositionManager.getDstRect((ItemInterface) maxLeftView, scale, scale).left;

            scale = this.mPositionManager.computeScaleXY((ItemInterface) minRightView);
            this.mMinScaledRight = this.mPositionManager.getDstRect((ItemInterface) minRightView, scale, scale).right;
            scale = this.mPositionManager.computeScaleXY((ItemInterface) maxRightView);
            this.mMaxScaledRight = this.mPositionManager.getDstRect((ItemInterface) maxRightView, scale, scale).right;

            scale = this.mPositionManager.computeScaleXY((ItemInterface) minTopView);
            this.mMinScaledTop = this.mPositionManager.getDstRect((ItemInterface) minTopView, scale, scale).top;
            scale = this.mPositionManager.computeScaleXY((ItemInterface) maxTopView);
            this.mMaxScaledTop = this.mPositionManager.getDstRect((ItemInterface) maxTopView, scale, scale).top;

            scale = this.mPositionManager.computeScaleXY((ItemInterface) minBottomView);
            this.mMinScaledBottom = this.mPositionManager.getDstRect((ItemInterface) minBottomView, scale, scale).bottom;
            scale = this.mPositionManager.computeScaleXY((ItemInterface) maxBottomView);
            this.mMaxScaledBottom = this.mPositionManager.getDstRect((ItemInterface) maxBottomView, scale, scale).bottom;
        }

        mFocusFinder.clearFocusables();
        mFocusFinder.initFocusables(this);
    }

    // for left
    public int getMinItemLeft() {
        return this.mMinLeft;
    }

    public int getMinItemScaledLeft() {
        return this.mMinScaledLeft;
    }

    public int getMaxItemLeft() {
        return this.mMaxLeft;
    }

    public int getMaxItemScaledLeft() {
        return this.mMaxScaledLeft;
    }

    // for right
    public int getMinItemRight() {
        return this.mMinRight;
    }

    public int getMinItemScaledRight() {
        return this.mMinScaledRight;
    }

    public int getMaxItemRight() {
        return this.mMaxRight;
    }

    public int getMaxItemScaledRight() {
        return this.mMaxScaledRight;
    }

    // for top
    public int getMinItemTop() {
        return this.mMinTop;
    }

    public int getMinItemScaledTop() {
        return this.mMinScaledTop;
    }

    public int getMaxItemTop() {
        return this.mMaxTop;
    }

    public int getMaxItemScaledTop() {
        return this.mMaxScaledTop;
    }

    // for bottom
    public int getMinItemBottom() {
        return this.mMinBottom;
    }

    public int getMinItemScaledBottom() {
        return this.mMinScaledBottom;
    }

    public int getMaxItemBottom() {
        return this.mMaxBottom;
    }

    public int getMaxItemScaledBottom() {
        return this.mMaxScaledBottom;
    }

    public int getScreenRight() {
        return this.mScreenWidth;
    }

    public int getMaxOffsetX() {
        int offset = getMaxItemScaledRight() - this.mScreenWidth + this.mViewRight;
        if (offset < 0) {
            offset = 0;
        }
        return offset;
    }

    public void addFocusable(View child) {
        this.mFocusFinder.addFocusable(child);
    }

    public void removeFocusable(View child) {
        this.mFocusFinder.removeFocusable(child);
    }

    public View reset() {
        View v = getSelectedView();
        init();
        if (v == null) {
            return null;
        }

        float scale = this.mPositionManager.computeScaleXY((ItemInterface) v);
        int right = this.mPositionManager.getDstRect((ItemInterface) v, scale, scale).right;
        if (getMaxItemScaledRight() < right
                && getMaxItemScaledRight() > getContext().getResources().getDisplayMetrics().widthPixels
                        - this.mViewRight) {
            return mFocusFinder.findNextFocus(this, v, FOCUS_LEFT);
        }

        return null;
    }

    /**
     * 清空子元素信息
     */
    public void release() {
        AppDebug.i(TAG, TAG + ".release");
        this.mNodeMap.clear();
    }

    long dispatchTime = 0;

    @Override
    public void dispatchDraw(Canvas canvas) {

        if (this.mPositionManager != null && this.mPositionManager.getParams().isBackground()) {
            if (this.mPositionManager.hasFocus()) {
                this.mPositionManager.drawFrame(canvas);
            } else {
                this.mPositionManager.drawUnscale();
            }

            postFocusDraw(canvas);
        }

        super.dispatchDraw(canvas);

        if (this.mPositionManager != null && !this.mPositionManager.getParams().isBackground()) {
            if (this.mPositionManager.hasFocus()) {
                this.mPositionManager.drawFrame(canvas);
            } else {
                this.mPositionManager.drawUnscale();
            }

            postFocusDraw(canvas);
        }

        Log.i(TAG, "dispatchDraw time = " + (System.currentTimeMillis() - dispatchTime));
        dispatchTime = System.currentTimeMillis();

        //        horizontalScroll();
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        Log.d(TAG, "onFocusChanged this = " + this + ", mScreenWidth = " + mScreenWidth + ", mIndex = " + mIndex
                + ", gainFocus = " + gainFocus + ", child count = " + this.getChildCount());
        synchronized (this) {
            mKeyTime = System.currentTimeMillis();
        }

        if (hasFocus() && this.mAutoSearchFocus && previouslyFocusedRect != null) {
            View v = this.mFocusFinder.findNextFocusFromRect(this, previouslyFocusedRect, direction);
            if (this.mNodeMap.containsKey(v)) {
                NodeInfo info = this.mNodeMap.get(v);
                mIndex = info.index;
            }
        }

        this.mPositionManager.setFocus(gainFocus || mForcedFocus);

        this.mPositionManager.setTransAnimation(false);
        this.mPositionManager.setFocusMove(false);
        if (!gainFocus) {
            if (!mForcedFocus) {
                this.mPositionManager.stopDraw();
                this.mPositionManager.reset();
                this.mPositionManager.setSelectedItem(null);
                getSelectedView().setSelected(false);
                this.mPositionManager.drawUnscale();
                invalidate();
            }

            performItemSelect(getSelectedView(), false);
            this.mPositionManager.setFocusDrawableVisible(false, true);
            this.mPositionManager.setFocusDrawableShadowVisible(false, true);

            OnFocusChangeListener listener = getSelectedView().getOnFocusChangeListener();
            if (listener != null) {
                listener.onFocusChange(getSelectedView(), false);
            }

        } else {
            if (!mForcedFocus || mIndex < 0) {
                if (-1 == mIndex) {
                    mIndex = 0;
                }
                ItemInterface item = (ItemInterface) getSelectedView();
                this.mPositionManager.stopDraw();
                this.mPositionManager.reset();
                this.mPositionManager.setSelectedItem(item);
                this.mPositionManager.computeScaleXY(item);
                this.mPositionManager.setScaleCurrentView(item.getIfScale());
                getSelectedView().setSelected(true);
                performItemSelect(getSelectedView(), true);

                OnFocusChangeListener listener = getSelectedView().getOnFocusChangeListener();
                if (listener != null) {
                    listener.onFocusChange(getSelectedView(), true);
                }

                this.mPositionManager.setState(FocusedBasePositionManager.STATE_DRAWING);
                this.mPositionManager.startDraw();
            }

            this.mPositionManager.setFocusDrawableVisible(true, true);
            this.mPositionManager.setFocusDrawableShadowVisible(true, true);
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
        if ((focusableMode & FOCUSABLES_TOUCH_MODE) == FOCUSABLES_TOUCH_MODE && isInTouchMode()
                && !isFocusableInTouchMode()) {
            return;
        }
        views.add(this);
    }

    @Override
    public void getFocusedRect(Rect r) {
        View item = getSelectedView();
        if (item != null) {
            // int[] location = new int[2];
            // item.getLocationOnScreen(location);
            // r.left = location[0];
            // r.right = location[0] + item.getWidth();
            // r.top = location[1];
            // r.bottom = location[1] + item.getHeight();

            item.getFocusedRect(r);
            this.offsetDescendantRectToMyCoords(item, r);
            // Log.d(TAG, "getFocusedRect r = " + r);
            return;
        }
        super.getFocusedRect(r);
    }

    @Override
    public void setScrollX(int value) {
        super.setScrollX(value);
    }

    public void forceFocus(boolean isFocus) {
        mForcedFocus = isFocus;
        // this.mPositionManager.setFocus(isFocus);
    }

    public void setSelectedView(View v) {
        if (null == mNodeMap || this.mNodeMap.size() <= 0) {
            init();
            AppDebug.i(TAG, TAG + ".setSelectedView init is runned! v=" + v + ", mNodeMap=" + mNodeMap.size()
                    + ", mNodeMap.selectView.nodeinfo=" + mNodeMap.get(mIndex));
        }
        if (!mNodeMap.containsKey(v)) {
            AppDebug.i(TAG,
                    TAG + ".setSelectedView v=" + v + ", mNodeMap=" + this.mNodeMap.size() + ", " + mNodeMap.size()
                            + ", mNodeMap.selectView.nodeinfo=" + mNodeMap.get(mIndex));
            throw new IllegalArgumentException("Parent does't contain this view");
        }

        View lastSelectedView = getSelectedView();
        if (lastSelectedView != null) {
            lastSelectedView.setSelected(false);
            performItemSelect(lastSelectedView, false);
        }

        this.mIndex = this.mNodeMap.get(v).index;
        AppDebug.i(TAG, TAG + ".setSelectedView mIndex=" + mIndex);
        View selectedView = v;
        performItemSelect(selectedView, true);
        selectedView.setSelected(true);
        ItemInterface item = (ItemInterface) selectedView;
        AppDebug.i(TAG, TAG + ".setSelectedView selectedView=" + selectedView.getTag(R.id.tag_position) + ", "
                + selectedView);
        AppDebug.i(TAG, TAG + ".setSelectedView item=" + item);
        boolean isScale = item.getIfScale();

        this.mPositionManager.stopDraw();
        this.mPositionManager.reset();
        this.mPositionManager.setSelectedItem(item);
        this.mPositionManager.computeScaleXY();
        this.mPositionManager.setScaleCurrentView(isScale);
        this.mPositionManager.setState(FocusedBasePositionManager.STATE_DRAWING);
        this.mPositionManager.startDraw();
    }

    /**
     * 获取当前选中的子元素
     * @return 当前选中的子元素
     */
    public View getSelectedView() {
        int indexOfView = mIndex;
        View selectedView = getChildAt(indexOfView);
        return selectedView;
    }

    public void setIndexOfSelectedView(int index) {
        mIndex = index;
    }

    public int getIndexOfSelectedView() {
        return mIndex;
    }

    public void onClearSelectedItem() {

        if (!mForcedFocus) {
            this.mPositionManager.stopDraw();
            this.mPositionManager.reset();
            this.mPositionManager.setSelectedItem(null);
            //getSelectedView().setSelected(false);
            this.mPositionManager.drawUnscale();
            invalidate();
        }
        if (getSelectedView() != null) {
            performItemSelect(getSelectedView(), false);
        }
        this.mPositionManager.setFocusDrawableVisible(false, true);
        this.mPositionManager.setFocusDrawableShadowVisible(false, true);

        if (getSelectedView() != null) {
            OnFocusChangeListener listener = getSelectedView().getOnFocusChangeListener();
            if (listener != null) {
                listener.onFocusChange(getSelectedView(), false);
            }
        }
    }

    void postFocusDraw(Canvas canvas) {

        // Log.d(TAG, "postFocusDraw ---> mLastSelectedView  = " + mLastSelectedView + ", getSelectedView() = " + getSelectedView());

        if (this.mLastSelectedView != null) {
            postFocusDraw(this.mLastSelectedView, canvas);
        }

        if (getSelectedView() != null) {
            postFocusDraw(getSelectedView(), canvas);
        }
    }

    void postFocusDraw(View v, Canvas canvas) {
        if (v instanceof FocusDrawListener) {

            //Log.d(TAG, "postFocusDraw ---> v  = " + v + ", getSelectedView() = " + getSelectedView());

            canvas.save();
            canvas.translate(v.getLeft(), v.getTop());
            canvas.clipRect(0, 0, v.getWidth(), v.getHeight());
            FocusDrawListener focusDrawListener = (FocusDrawListener) v;
            focusDrawListener.postFocusDraw(canvas);

            canvas.restore();
        }
    }

    boolean isKeyDown = false;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d("onkey", "onKeyUp keycode:"+keyCode);
        if ((KeyEvent.KEYCODE_DPAD_CENTER == keyCode || KeyEvent.KEYCODE_ENTER == keyCode) && isKeyDown) {
            if (getSelectedView() != null) {
                getSelectedView().performClick();
            }
        }

        isKeyDown = false;
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("onkey", "onKeyDown keycode:"+keyCode);
        if (event.getRepeatCount() == 0 && KeyEvent.KEYCODE_DPAD_CENTER == keyCode || KeyEvent.KEYCODE_ENTER == keyCode) {
            isKeyDown = true;
        }

        if (keyCode != KeyEvent.KEYCODE_DPAD_LEFT && keyCode != KeyEvent.KEYCODE_DPAD_RIGHT
                && keyCode != KeyEvent.KEYCODE_DPAD_UP && keyCode != KeyEvent.KEYCODE_DPAD_DOWN) {
            return super.onKeyDown(keyCode, event);
        }

        synchronized (this) {
            if (System.currentTimeMillis() - mKeyTime <= KEY_INTERVEL || !this.mPositionManager.canDrawNext()) {
                Log.d(TAG,
                        "onKeyDown mAnimationTime = " + mKeyTime + " -- current time = " + System.currentTimeMillis());
                return true;
            }
            mKeyTime = System.currentTimeMillis();
        }
        if (this.mPositionManager.isScrolling()) {
            return true;
        }

        View lastSelectedView = getSelectedView();
        NodeInfo nodeInfo = this.mNodeMap.get(lastSelectedView);
        View v = null;
        int direction;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                direction = FOCUS_LEFT;
                if (nodeInfo.fromLeft != null && nodeInfo.fromLeft.isFocusable()) {
                    v = nodeInfo.fromLeft;
                } else {
                    v = mFocusFinder.findNextFocus(this, lastSelectedView, direction);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                direction = FOCUS_RIGHT;
                if (nodeInfo.fromRight != null && nodeInfo.fromRight.isFocusable()) {
                    v = nodeInfo.fromRight;
                } else {
                    v = mFocusFinder.findNextFocus(this, lastSelectedView, direction);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                direction = FOCUS_DOWN;
                if (nodeInfo.fromDown != null && nodeInfo.fromDown.isFocusable()) {
                    v = nodeInfo.fromDown;
                } else {
                    v = mFocusFinder.findNextFocus(this, lastSelectedView, direction);
                }

                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                direction = FOCUS_UP;
                if (nodeInfo.fromUp != null && nodeInfo.fromUp.isFocusable()) {
                    v = nodeInfo.fromUp;
                } else {
                    v = mFocusFinder.findNextFocus(this, lastSelectedView, direction);
                }

                break;
            default:
                return super.onKeyDown(keyCode, event);
        }

        Log.d(TAG, "onKeyDown v = " + v);
        if (v != null && containView(v) && this.mNodeMap.containsKey(v) && v.isFocusable()) {
            this.mLastSelectedView = lastSelectedView;
            NodeInfo info = this.mNodeMap.get(v);
            mIndex = info.index;

            if (lastSelectedView != null) {
                lastSelectedView.setSelected(false);
                performItemSelect(lastSelectedView, false);
                OnFocusChangeListener listener = lastSelectedView.getOnFocusChangeListener();
                if (listener != null) {
                    listener.onFocusChange(lastSelectedView, false);
                }
            }
            View selectedView = getSelectedView();
            if (selectedView != null) {
                selectedView.setSelected(true);
                performItemSelect(selectedView, true);
                OnFocusChangeListener listener = selectedView.getOnFocusChangeListener();
                if (listener != null) {
                    listener.onFocusChange(selectedView, true);
                }
            }

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    info.fromRight = lastSelectedView;
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    info.fromLeft = lastSelectedView;
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    info.fromUp = lastSelectedView;
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    info.fromDown = lastSelectedView;
                    break;
            }

            this.mPositionManager.setFocusDirection(direction);
            this.mPositionManager.stopDraw();
            this.mPositionManager.setFocusMove(true);
            this.mPositionManager.reset();
            boolean isScale = true;

            ItemInterface item = (ItemInterface) selectedView;
            isScale = item.getIfScale();

            this.mPositionManager.setSelectedItem(item);
            this.mPositionManager.computeScaleXY();
            this.mPositionManager.setScaleCurrentView(isScale);
            this.mPositionManager.setState(FocusedBasePositionManager.STATE_DRAWING);

            this.mPositionManager.startDraw();

            //            mKeyCon = true;

            //            horizontalScroll();
            // this.invalidate();
        } else {
            Log.w(TAG, "onKeyDown select view is null");
            this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
            return false;
        }

        this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
        return true;
    }

    boolean containView(View v) {
        int[] location = new int[2];
        getLocationOnScreen(location);
        Rect containRect = new Rect();
        containRect.left = location[0];
        containRect.top = location[1];
        containRect.right = location[0] + getWidth();
        containRect.bottom = location[1] + getHeight();

        v.getLocationOnScreen(location);
        Rect viewRect = new Rect();
        viewRect.left = location[0];
        viewRect.top = location[1];
        viewRect.right = location[0] + v.getWidth();
        viewRect.bottom = location[1] + v.getHeight();

        if (containRect.top > viewRect.bottom || containRect.bottom < viewRect.top) {
            return false;
        }

        return true;
    }

    class NodeInfo {

        public int index;
        public View fromLeft;
        public View fromRight;
        public View fromUp;
        public View fromDown;
    }

    public interface OnScrollListener {

        /**
         * The view is not scrolling. Note navigating the list using the
         * trackball counts as being in the idle state since these transitions
         * are not animated.
         */
        public static int SCROLL_STATE_IDLE = 0;

        /**
         * The user is scrolling using touch, and their finger is still on the
         * screen
         */
        public static int SCROLL_STATE_TOUCH_SCROLL = 1;

        /**
         * The user had previously been scrolling using touch and had performed
         * a fling. The animation is now coasting to a stop
         */
        public static int SCROLL_STATE_FLING = 2;

        /**
         * Callback method to be invoked while the list view or grid view is
         * being scrolled. If the view is being scrolled, this method will be
         * called before the next frame of the scroll is rendered. In
         * particular, it will be called before any calls to {@link Adapter#getView(int, View, ViewGroup)}.
         * @param view
         *            The view whose scroll state is being reported
         * @param scrollState
         *            The current scroll state. One of {@link #SCROLL_STATE_IDLE}, {@link #SCROLL_STATE_TOUCH_SCROLL} or
         *            {@link #SCROLL_STATE_IDLE}.
         */
        public void onScrollStateChanged(ViewGroup view, int scrollState);

        /**
         * Callback method to be invoked when the list or grid has been
         * scrolled. This will be called after the scroll has completed
         * @param view
         *            The view whose scroll state is being reported
         * @param firstVisibleItem
         *            the index of the first visible cell (ignore if
         *            visibleItemCount == 0)
         * @param visibleItemCount
         *            the number of visible cells
         * @param totalItemCount
         *            the number of items in the list adaptor
         */
        public void onScroll(ViewGroup view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }

    @Override
    public void reportState(int state) {
        if (this.mFocusStateListener != null) {
            this.mFocusStateListener.reportFocusState(getSelectedView(), this.mIndex, state, this);
        }
    }

    public interface OutsideScrollListener {

        public int getCurrX();

        public int getCurrY();

        public void smoothOutsideScrollBy(int distance, int duration);
    }

    public interface FocusStateListener {

        public void reportFocusState(View v, int position, int state, View parent);
    }

    public interface FocusDrawListener {

        public void postFocusDraw(Canvas canvas);
    }

    @Override
    public int getViewScrollX() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getViewScrollY() {
        // TODO Auto-generated method stub
        return 0;
    }
}
