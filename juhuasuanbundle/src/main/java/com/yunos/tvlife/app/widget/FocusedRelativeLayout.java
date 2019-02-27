package com.yunos.tvlife.app.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Adapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.yunos.tvlife.app.widget.FocusedBasePositionManager.ContainInterface;
import com.yunos.tvlife.app.widget.FocusedBasePositionManager.FocusItemSelectedListener;
import com.yunos.tvlife.app.widget.FocusedBasePositionManager.FocusParams;
import com.yunos.tvlife.app.widget.FocusedBasePositionManager.ItemInterface;
import com.yunos.tvlife.app.widget.FocusedBasePositionManager.PositionInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FocusedRelativeLayout extends RelativeLayout implements PositionInterface, ContainInterface {

    public static final String TAG                       = "FocusedRelativeLayout";
    private static final boolean       DEBUG                     = true;

    /**
     * Layout�������ģʽ:������ǰ����ʾ���� {@link #setHorizontalMode(int)}
     */
    public static final int            HORIZONTAL_SINGEL         = 1;
    /**
     * Layout�������ģʽ:�������GridView,���Ʒ�ҳ����{@link #setHorizontalMode(int)}
     */
    public static final int            HORIZONTAL_FULL           = 2;

    public static final int            HORIZONTAL_OUTSIDE_SINGEL = 3;

    public static final int            HORIZONTAL_OUTSIDE_FULL   = 4;

    private static final int           SCROLL_DURATION           = 100;

    private long                       KEY_INTERVEL              = 20;                                 // ms
    private long                       mKeyTime                  = 0;

    public int                         mIndex                    = -1;

    private HotScroller                mScroller;
    private int                        mScreenWidth;
    private int                        mViewRight                = 20;
    private int                        mViewLeft                 = 0;

    private int                        mMinLeft;
    private int                        mMinScaledLeft;
    private int                        mMaxLeft;
    private int                        mMaxScaledLeft;

    private int                        mMinRight;
    private int                        mMinScaledRight;
    private int                        mMaxRight;
    private int                        mMaxScaledRight;

    private int                        mMinTop;
    private int                        mMinScaledTop;
    private int                        mMaxTop;
    private int                        mMaxScaledTop;

    private int                        mMinBottom;
    private int                        mMinScaledBottom;
    private int                        mMaxBottom;
    private int                        mMaxScaledBottom;

    private int                        mHorizontalMode           = -1;
    private FocusItemSelectedListener  mOnItemSelectedListener   = null;
    private FocusedBasePositionManager mPositionManager;
    private OnScrollListener           mScrollerListener         = null;
    private FocusFinder                mFocusFinder;
    private int                        mLastScrollState          = OnScrollListener.SCROLL_STATE_IDLE;
    private boolean                    mAutoSearchFocus          = false;
    private OutsideScrollListener      mOutsideScrollListener    = null;
    private boolean                    mForcedFocus              = false;
    private Map<View, NodeInfo> mNodeMap                  = new HashMap<View, NodeInfo>();
    private FocusStateListener         mFocusStateListener       = null;
    private View mLastSelectedView         = null;

    public FocusFinder getFocusFinder() {
        return mFocusFinder;
    }

    public void setFocusfinder(FocusFinder focusFinder) {
        mFocusFinder = focusFinder;
    }

    public void setScrollerListener(OnScrollListener l) {
        this.mScrollerListener = l;
    }

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
        if (null == this.mPositionManager) {
            return;
        }
        this.mPositionManager.setScrolling(isScrolling);
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {

    }

    @Override
    public void createPositionManager(FocusParams params) {
        this.mPositionManager = FocusedBasePositionManager.createPositionManager(params, this);
    }

    /**
     * ���ú������ģʽ {@link #HORIZONTAL_SINGEL} {@link #HORIZONTAL_FULL} {@link #HORIZONTAL_OUTSIDE_SINGEL} {@link #HORIZONTAL_OUTSIDE_FULL}
     * @param mode
     */
    public void setHorizontalMode(int mode) {
        this.mHorizontalMode = mode;
    }

    /**
     * ������ģʽΪ{@link #HORIZONTAL_SINGEL}ʱ, �����ƶ������Ҳ��Ԫ��ʱ���ұ߽�ľ���
     * @param right
     *            ���ұ߽�ľ���
     */
    public void setViewRight(int right) {
        mViewRight = right;
    }

    public int getViewRight() {
        return this.mViewRight;
    }

    /**
     * ������ģʽΪ{@link #HORIZONTAL_SINGEL}ʱ, �����ƶ���������Ԫ��ʱ����߽�ľ���
     * @param left
     *            ����߽߱�ľ���
     */
    public void setViewLeft(int right) {
        mViewLeft = right;
    }

    public int getViewLeft() {
        return this.mViewLeft;
    }

    public FocusedRelativeLayout(Context contxt) {
        super(contxt);
        setChildrenDrawingOrderEnabled(true);
        mScroller = new HotScroller(contxt, new DecelerateInterpolator());
        mScreenWidth = contxt.getResources().getDisplayMetrics().widthPixels;
        mFocusFinder = new FocusFinder();
    }

    public FocusedRelativeLayout(Context contxt, AttributeSet attrs) {
        super(contxt, attrs);
        setChildrenDrawingOrderEnabled(true);
        mScroller = new HotScroller(contxt, new DecelerateInterpolator());
        mScreenWidth = contxt.getResources().getDisplayMetrics().widthPixels;
        mFocusFinder = new FocusFinder();
    }

    public FocusedRelativeLayout(Context contxt, AttributeSet attrs, int defStyle) {
        super(contxt, attrs, defStyle);
        setChildrenDrawingOrderEnabled(true);
        mScroller = new HotScroller(contxt, new DecelerateInterpolator());
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
        Log.i("aabb", "==========init===========w*h=======" + getWidth() + "x" + getHeight());
        if (getChildCount() <= 0) {
            return;
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
                continue;
            }

            if (!(child instanceof ItemInterface)) {
                continue;
            }

            if (!this.mNodeMap.containsKey(child)) {
                NodeInfo info = new NodeInfo();
                info.index = index;
                this.mNodeMap.put(child, info);
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
            Log.d(TAG, "init: mMinLeft = " + mMinLeft + ", mMaxLeft = " + mMaxLeft + ", mMinRight = " + mMinRight + ", mMaxRight = " + mMaxRight + ", mMinTop = " + mMinTop + ", mMaxTop = " + mMaxTop + ", mMinBottom = " + mMinBottom + ", mMaxBottom = " + mMaxBottom);

            Log.d(TAG, "init: minLeftView = " + minLeftView + ", maxLeftView = " + maxLeftView + ", minRightView = " + minRightView + ", maxRightView = " + maxRightView + ", minTopView = " + minTopView + ", maxTopView = " + maxTopView + ", minBottomView = " + minBottomView + ", maxBottomView = "
                    + maxBottomView);
        }

        // maxItemRightView maybe null! �����ж�̬addView���������ʼʱchildcountΪ0. by leiming32
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

            mFocusFinder.clearFocusables();
            mFocusFinder.initFocusables(this);
        }
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
        if (getMaxItemScaledRight() < right && getMaxItemScaledRight() > getContext().getResources().getDisplayMetrics().widthPixels - this.mViewRight) {
            return mFocusFinder.findNextFocus(this, v, FOCUS_LEFT);
        }

        return null;
    }

    /**
     * �����Ԫ����Ϣ
     */
    public void release() {
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

        //		if (!this.mPositionManager.hasFocus() && mAutoSearchFocus && this.mPositionManager.getState() == FocusedBasePositionManager.STATE_IDLE) {
        //			mIndex = -1;
        //		}

        Log.i(TAG, "dispatchDraw time = " + (System.currentTimeMillis() - dispatchTime));
        dispatchTime = System.currentTimeMillis();
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        String rStr = null;
        if (previouslyFocusedRect != null) {
            rStr = previouslyFocusedRect.toShortString();
        } else {
            rStr = "null";
        }
        Log.d(TAG, "onFocusChanged this = " + this + ", mScreenWidth = " + mScreenWidth + ", mIndex = " + mIndex + ", gainFocus = " + gainFocus + ", child count = " + this.getChildCount() + ",previouslyFocusedRect:" + rStr + ",mAutoSearchFocus:" + mAutoSearchFocus);

        synchronized (this) {
            mKeyTime = System.currentTimeMillis();
        }

        //previouslyFocusedRect���ﰴ��Ļ���ϵ��mFocusFinder.findNextFocusFromRectҲ�ǰ���Ļ���ϵ
        if (hasFocus() && this.mAutoSearchFocus && previouslyFocusedRect != null) {
            View v = this.mFocusFinder.findNextFocusFromRect(this, previouslyFocusedRect, direction);
            if (this.mNodeMap.containsKey(v)) {
                NodeInfo info = this.mNodeMap.get(v);
                mIndex = info.index;
            }
        }

        if (this.mPositionManager == null) {
            return;
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
            //			if (mAutoSearchFocus) {
            //				mIndex = 0;//����¼�ϴ�λ��
            //			}
        } else {
            if (!mForcedFocus || mIndex < 0) {
                if (-1 == mIndex) {
                    mIndex = 0;
                }
                ItemInterface item = (ItemInterface) getSelectedView();
                this.mPositionManager.stopDraw();
                this.mPositionManager.reset();
                this.mPositionManager.setSelectedItem(item);
                this.mPositionManager.computeScaleXY();
                this.mPositionManager.setScaleCurrentView(item.getIfScale());
                getSelectedView().setSelected(true);
                performItemSelect(getSelectedView(), true);
                this.mPositionManager.setState(FocusedBasePositionManager.STATE_DRAWING);
                this.mPositionManager.startDraw();
                //				horizontalScroll();
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
        if ((focusableMode & FOCUSABLES_TOUCH_MODE) == FOCUSABLES_TOUCH_MODE && isInTouchMode() && !isFocusableInTouchMode()) {
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
        this.mScroller.setFinalX(value);
        this.mScroller.abortAnimation();
        super.setScrollX(value);
    }

    public void forceFocus(boolean isFocus) {
        mForcedFocus = isFocus;
        // this.mPositionManager.setFocus(isFocus);
    }

    public void setSelectedView(View v) {
        if (!this.mNodeMap.containsKey(v)) {
            throw new IllegalArgumentException("Parent does't contain this view");
        }

        View lastSelectedView = getSelectedView();
        if (lastSelectedView != null) {
            lastSelectedView.setSelected(false);
            performItemSelect(lastSelectedView, false);
        }

        this.mIndex = this.mNodeMap.get(v).index;
        View selectedView = v;
        performItemSelect(selectedView, true);
        selectedView.setSelected(true);
        ItemInterface item = (ItemInterface) selectedView;
        boolean isScale = item.getIfScale();

        this.mPositionManager.stopDraw();
        this.mPositionManager.reset();
        this.mPositionManager.setSelectedItem(item);
        this.mPositionManager.computeScaleXY();
        this.mPositionManager.setScaleCurrentView(isScale);
        this.mPositionManager.setState(FocusedBasePositionManager.STATE_DRAWING);
        this.mPositionManager.startDraw();
        horizontalScroll();
    }

    /**
     * ��ȡ��ǰѡ�е���Ԫ��
     * @return ��ǰѡ�е���Ԫ��
     */
    public View getSelectedView() {
        int indexOfView = mIndex;
        View selectedView = getChildAt(indexOfView);
        return selectedView;
    }

    void postFocusDraw(Canvas canvas) {
        if (this.mLastSelectedView != null) {
            postFocusDraw(this.mLastSelectedView, canvas);
        }

        if (getSelectedView() != null) {
            postFocusDraw(getSelectedView(), canvas);
        }
    }

    void postFocusDraw(View v, Canvas canvas) {
        if (v instanceof FocusDrawListener) {
            canvas.save();
            canvas.translate(v.getLeft(), v.getTop());
            canvas.clipRect(0, 0, v.getWidth(), v.getHeight());
            FocusDrawListener focusDrawListener = (FocusDrawListener) v;
            focusDrawListener.postFocusDraw(canvas);

            canvas.restore();
        }
    }

    boolean isKeyDown = false;

    // @Override
    // public boolean onKeyUp(int keyCode, KeyEvent event) {
    // if ((KeyEvent.KEYCODE_DPAD_CENTER == keyCode || KeyEvent.KEYCODE_ENTER ==
    // keyCode) && isKeyDown) {
    // if (getSelectedView() != null) {
    // getSelectedView().performClick();
    // }
    // }
    //
    // isKeyDown = false;
    // return super.onKeyUp(keyCode, event);
    // }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown -- keyCode:" + keyCode);
        if (event.getRepeatCount() == 0 && KeyEvent.KEYCODE_DPAD_CENTER == keyCode || KeyEvent.KEYCODE_ENTER == keyCode) {
            isKeyDown = true;
            if (getSelectedView() != null) {
                getSelectedView().performClick();
            }
        }

        if (keyCode != KeyEvent.KEYCODE_DPAD_LEFT && keyCode != KeyEvent.KEYCODE_DPAD_RIGHT && keyCode != KeyEvent.KEYCODE_DPAD_UP && keyCode != KeyEvent.KEYCODE_DPAD_DOWN) {
            return super.onKeyDown(keyCode, event);
        }

        synchronized (this) {
            if (System.currentTimeMillis() - mKeyTime <= KEY_INTERVEL || !this.mPositionManager.canDrawNext()) {
                Log.d(TAG, "onKeyDown mAnimationTime = " + mKeyTime + " -- current time = " + System.currentTimeMillis() + ", is scroll finished = " + mScroller.isFinished());
                return true;
            }
            mKeyTime = System.currentTimeMillis();
        }
        if (this.mPositionManager.isScrolling()) {
            return true;
        }

        View lastSelectedView = getSelectedView();
        NodeInfo nodeInfo = this.mNodeMap.get(lastSelectedView);
        Log.i(TAG, "onKeyDown mIndex="+mIndex+" childCount="+getChildCount()+" nodeInfo="+nodeInfo);
        if (nodeInfo == null) {
            return super.onKeyDown(keyCode, event);
        }
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
            horizontalScroll();
            // this.invalidate();
        } else {
            Log.w(TAG, "onKeyDown select view is null");
            this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
            return false;
        }

        this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
        return true;
    }

    protected boolean selectView(View v, int keyCode, View lastSelectedView, Integer direction, boolean changeNode) {
        if (null == direction) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    direction = FOCUS_LEFT;
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    direction = FOCUS_RIGHT;
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    direction = FOCUS_DOWN;
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    direction = FOCUS_UP;
                    break;
                default:
                    return false;
            }
        }
        Log.d(TAG, TAG + ".selectView containView(v)=" + containView(v) + ", this.mNodeMap.containsKey(v)="
                + this.mNodeMap.containsKey(v) + ", v.isFocusable()=" + v.isFocusable());
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
            if (changeNode) {
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
            horizontalScroll();
            Log.d(TAG, TAG + ".selectView select view is view=" + v);
        } else {
            Log.w(TAG, TAG + ".selectView select view is null");
            this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
            return false;
        }
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

    private void horizontalScroll() {
        if (HORIZONTAL_SINGEL == this.mHorizontalMode || HORIZONTAL_OUTSIDE_SINGEL == this.mHorizontalMode) {
            scrollSingel();
        } else if (HORIZONTAL_FULL == this.mHorizontalMode || HORIZONTAL_OUTSIDE_FULL == this.mHorizontalMode) {
            scrollFull();
        }
    }

    void scrollFull() {
        ItemInterface item = (ItemInterface) getSelectedView();
        Rect rectScaled = item.getItemScaledRect(this.mPositionManager.getParams().getItemScaleXValue(), this.mPositionManager.getParams().getItemScaleYValue());
        int left = rectScaled.left;
        int right = rectScaled.right;

        Log.d(TAG, "scrollFull left = " + left + ", right = " + right + ", scaleX = " + this.mPositionManager.getParams().getItemScaleXValue());
        int[] location = new int[2];
        getLocationOnScreen(location);
        int rightbind = location[0] + this.getWidth();
        if (rightbind > mScreenWidth) {
            rightbind = mScreenWidth;
        }

        if (right > rightbind + this.mScroller.getCurrX()) {
            int dx = left - getMinItemLeft() - this.mScroller.getCurrX();
            Log.d(TAG, "scrollFull to right dx = " + dx + ", mStartX = " + getMinItemLeft() + ", mScreenWidth = " + mScreenWidth + ", left = " + left);

            int duration = dx * SCROLL_DURATION / 300;
            smoothScrollBy(dx, duration);
            return;
        }

        Log.d(TAG, "scroll conrtainer left = " + getMinItemLeft());
        if (getMinItemLeft() + this.mScroller.getCurrX() > left) {
            int dx = right + getMinItemLeft() - mScreenWidth - this.mScroller.getCurrX();
            Log.d(TAG, "scrollFull to left dx = " + dx + ", mStartX = " + getMinItemLeft() + ", currX = " + mScroller.getCurrX() + ", mScreenWidth = " + mScreenWidth + ", left = " + left);
            if (mScroller.getCurrX() < Math.abs(dx)) {
                dx = -mScroller.getCurrX();
            }
            int duration = -dx * SCROLL_DURATION / 300;
            smoothScrollBy(dx, duration);
        }
    }

    void scrollSingel() {
        int maxScrollDuration = 150;
        ItemInterface item = (ItemInterface) getSelectedView();
        float scaleX = this.mPositionManager.getParams().getItemScaleXValue();
        float scaleY = this.mPositionManager.getParams().getItemScaleYValue();
        Rect rectScaled = item.getItemScaledRect(scaleX, scaleY);
        int scaledLeft = rectScaled.left;
        int scaledRight = rectScaled.right;

        //		int[] location = new int[2];
        //		getLocationOnScreen(location);
        Rect visibleRect = new Rect();
        getGlobalVisibleRect(visibleRect);
        int rightBind = /* mScreenWidth *//* location[0] + getWidth() */visibleRect.right - mViewRight; // by leiming.yanlm
        if (HORIZONTAL_OUTSIDE_SINGEL == this.mHorizontalMode && this.mOutsideScrollListener != null) {
            rightBind += this.mOutsideScrollListener.getCurrX();
        } else {
            rightBind += this.mScroller.getCurrX();
        }

        if (scaledRight >= rightBind) {
            int dx = (scaledRight - rightBind);
            int duration = dx * SCROLL_DURATION / 245;
            if (duration > maxScrollDuration) {
                duration = maxScrollDuration;
            }
            if (HORIZONTAL_OUTSIDE_SINGEL == this.mHorizontalMode && this.mOutsideScrollListener != null) {
                this.mOutsideScrollListener.smoothOutsideScrollBy(dx, duration);
            } else {
                smoothScrollBy(dx, duration);
            }
            return;
        }

        Log.d(TAG, "scroll conrtainer left = " + getMinItemLeft());
        int leftBind = mViewLeft + getMinItemLeft();
        if (HORIZONTAL_OUTSIDE_SINGEL == this.mHorizontalMode && this.mOutsideScrollListener != null) {
            leftBind += this.mOutsideScrollListener.getCurrX();
        } else {
            leftBind += this.mScroller.getCurrX();
        }
        if (scaledLeft < leftBind) {
            int dx = scaledLeft - leftBind;
            int duration = -dx * SCROLL_DURATION / 245;
            if (duration > maxScrollDuration) {
                duration = maxScrollDuration;
            }
            if (HORIZONTAL_OUTSIDE_SINGEL == this.mHorizontalMode && this.mOutsideScrollListener != null) {
                if (this.mOutsideScrollListener.getCurrX() > Math.abs(dx)) {
                    this.mOutsideScrollListener.smoothOutsideScrollBy(dx, duration);
                } else {
                    this.mOutsideScrollListener.smoothOutsideScrollBy(-this.mOutsideScrollListener.getCurrX(), duration);
                }
            } else {
                if (mScroller.getCurrX() > Math.abs(dx)) {
                    smoothScrollBy(dx, duration);
                } else {
                    smoothScrollBy(-mScroller.getCurrX(), duration);
                }
            }
        }
    }

    /**
     * ���������ָ��λ��
     * @param fx
     *            ��������
     * @param duration
     *            ����ʱ��
     */
    public void smoothScrollTo(int fx, int duration) {
        int dx = fx - mScroller.getFinalX();
        smoothScrollBy(dx, duration);
    }

    /**
     * �������һ�ξ���
     * @param dx
     *            ��������
     * @param duration
     *            �����¼�
     */
    public void smoothScrollBy(int dx, int duration) {
        if (dx == 0) {
            return;
        }
        Log.w(TAG, "smoothScrollBy dx = " + dx);
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, mScroller.getFinalY(), duration);
        this.mPositionManager.setScrolling(true);
        reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
        invalidate();
    }

    void reportScrollStateChange(int newState) {
        if (newState != mLastScrollState) {
            if (mScrollerListener != null) {
                mLastScrollState = newState;
                mScrollerListener.onScrollStateChanged(this, newState);
            }
        }
    }

    @Override
    public void computeScroll() {
        if (this.mHorizontalMode == HORIZONTAL_FULL || this.mHorizontalMode == HORIZONTAL_SINGEL) {
            if (mScroller.computeScrollOffset()) {
                scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            }

            if (mScroller.isFinished()) {
                this.mPositionManager.setScrolling(false);
                reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
            }
        }
        super.computeScroll();
    }

    class HotScroller extends Scroller {

        public HotScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
            // TODO Auto-generated constructor stub
        }

        public HotScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
            // TODO Auto-generated constructor stub
        }

        public HotScroller(Context context) {
            super(context, new AccelerateDecelerateInterpolator());
            // TODO Auto-generated constructor stub
        }

        @Override
        public boolean computeScrollOffset() {
            if (!isFinished()) {
                invalidate();
            }
            return super.computeScrollOffset();
        }
    }

    class NodeInfo {

        public int  index;
        public View fromLeft;
        public View fromRight; //λ�ڵ�ǰview���ұ�
        public View fromUp;
        public View fromDown;
    }

    public interface OnScrollListener {

        /**
         * The view is not scrolling. Note navigating the list using the
         * trackball counts as being in the idle state since these transitions
         * are not animated.
         */
        public static int SCROLL_STATE_IDLE         = 0;

        /**
         * The user is scrolling using touch, and their finger is still on the
         * screen
         */
        public static int SCROLL_STATE_TOUCH_SCROLL = 1;

        /**
         * The user had previously been scrolling using touch and had performed
         * a fling. The animation is now coasting to a stop
         */
        public static int SCROLL_STATE_FLING        = 2;

        /**
         * Callback method to be invoked while the list view or grid view is
         * being scrolled. If the view is being scrolled, this method will be
         * called before the next frame of the scroll is rendered. In
         * particular, it will be called before any calls to {@link Adapter#getView(int, View, ViewGroup)}.
         * @param view
         *            The view whose scroll state is being reported
         * @param scrollState
         *            The current scroll state. One of {@link #SCROLL_STATE_IDLE}, {@link #SCROLL_STATE_TOUCH_SCROLL} or {@link #SCROLL_STATE_IDLE}.
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
    public int getViewScrollX() {
        return mScroller.getCurrX();
        //		return 0; // by leiming.yanlm
    }

    @Override
    public int getViewScrollY() {
        return mScroller.getCurrY();
        //		return 0;
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
}
