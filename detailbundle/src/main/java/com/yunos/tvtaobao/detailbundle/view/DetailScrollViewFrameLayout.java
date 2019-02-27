package com.yunos.tvtaobao.detailbundle.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yunos.tv.app.widget.FocusFinder;
import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.FocusStateListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.core.common.AppDebug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetailScrollViewFrameLayout extends FrameLayout implements DeepListener, ItemListener {

    protected static final String TAG = "DetailScrollViewFrameLayout";
    protected static final boolean DEBUG = true;

    protected int mIndex = -1;

    private ItemSelectedListener mOnItemSelectedListener = null;
    private onViewKeyDownUpListener mOnViewKeyDownUpListener = null;

    private boolean mAutoSearchFocus = false;

    protected Map<View, NodeInfo> mNodeMap = new HashMap<View, NodeInfo>();

    private View mLastSelectedView = null;

    private FocusFinder mFocusFinder;

    protected boolean mNeedInit = true;

    protected Params mParams = new Params(1.05f, 1.05f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());
    protected FocusRectParams mFocusRectparams = new FocusRectParams();

    View mNextFocus = null;
    DeepListener mDeep = null;
    DeepListener mLastDeep = null;
    int mNextDirection;

    boolean mLayouted = false;
    boolean mNeedReset = false;
    boolean mNeedInitNode = true;
    boolean mDeepFocus = false;
    FocusStateListener mFocusStateListener = null;
    OnItemClickListener mOnItemClickListener;
    ViewGroup mFindRootView;
    boolean mNeedFocused = true;
    boolean mFocusBackground = false;
    boolean mClearDataDetachedFromWindow = true;

    boolean mAimateWhenGainFocusFromLeft = true;
    boolean mAimateWhenGainFocusFromRight = true;
    boolean mAimateWhenGainFocusFromUp = true;
    boolean mAimateWhenGainFocusFromDown = true;

    boolean mIsAnimate = true;

    private Rect mClipFocusRect = new Rect(); // 默认focus框

    public DetailScrollViewFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context);
    }

    public DetailScrollViewFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public DetailScrollViewFrameLayout(Context context) {
        super(context);
        initLayout(context);
    }

    public void setAnimateWhenGainFocus(boolean fromleft, boolean fromUp, boolean fromRight, boolean fromDown) {
        mAimateWhenGainFocusFromLeft = fromleft;
        mAimateWhenGainFocusFromUp = fromUp;
        mAimateWhenGainFocusFromRight = fromRight;
        mAimateWhenGainFocusFromDown = fromDown;
    }

    public void setClearDataDetachedFromWindowEnable(boolean enable) {
        mClearDataDetachedFromWindow = enable;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mClearDataDetachedFromWindow) {
            mLayouted = false;
            mNeedInitNode = true;
            if (mNodeMap != null) {
                mNodeMap.clear();
            }
            if (mFocusFinder != null) {
                mFocusFinder.clearFocusables();
            }
        }
    }

    public void setFocusBackground(boolean back) {
        mFocusBackground = back;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    public void setOnItemSelectedListener(ItemSelectedListener listener) {
        this.mOnItemSelectedListener = listener;
    }

    public void setOnViewKeyDownUpListener(onViewKeyDownUpListener l) {
        mOnViewKeyDownUpListener = l;
    }

    private void performItemClick() {
        if (mDeep != null) {
            mDeep.onItemClick();
            return;
        }

        if (this.mOnItemClickListener != null) {
            this.mOnItemClickListener.onItemClick(this, getSelectedView());
        }
    }

    protected void performItemSelect(View v, boolean isSelected, boolean isLocal) {
        if (!isLocal) {
            if (mDeep != null) {
                mDeep.onItemSelected(isSelected);
                return;
            }
        }

        v.setSelected(isSelected);
        if (this.mOnItemSelectedListener != null) {
            this.mOnItemSelectedListener.onItemSelected(v, mIndex, isSelected, this);
        }
    }

    public void setAutoSearchFocus(boolean autoSearchFocus) {
        this.mAutoSearchFocus = autoSearchFocus;
    }

    public void setOnFocusStateListener(FocusStateListener l) {
        mFocusStateListener = l;
    }

    private void initLayout(Context conext) {
        mFocusFinder = new FocusFinder();
    }

    public boolean isNeedFocusItem() {
        return mNeedFocused;
    }

    public void release() {
        this.mNodeMap.clear();
    }

    public void clearFocusedIndex() {
        mIndex = -1;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mDeep != null && mDeep.canDeep() && mDeep.hasDeepFocus()) {
            return mDeep.onKeyUp(keyCode, event);
        }

        if ((KeyEvent.KEYCODE_DPAD_CENTER == keyCode || KeyEvent.KEYCODE_ENTER == keyCode) && getSelectedView() != null) {
            performItemClick();
            getSelectedView().performClick();
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public void setSelected(boolean selected) {
        if (selected == false) {
            mNeedFocused = true;
        }
        super.setSelected(selected);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        AppDebug.d(TAG, "onKeyDown keyCode = " + keyCode);
        if (mDeep != null && mDeep.canDeep() && mDeep.hasDeepFocus()) {// by
                                                                       // leiming.yanlm
            if (mDeep.onKeyDown(keyCode, event)) {
                reset();
                return true;
            }
        }

        int direction = 0;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                direction = View.FOCUS_LEFT;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                direction = View.FOCUS_RIGHT;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                direction = View.FOCUS_DOWN;
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                direction = View.FOCUS_UP;
                break;
        }

        if (mNextFocus != null && this.mNodeMap.containsKey(mNextFocus) && mNextFocus.isFocusable()) {
            mIsAnimate = true;
            if (mDeep != null && mDeep.canDeep()) {
                if (mDeep.hasDeepFocus()) {
                    // if (mDeep.onKeyDown(keyCode, event)) {//移到上面
                    // reset();
                    // }
                } else {
                    Rect previouslyFocusedRect = getFocusedRect(getSelectedView(), mNextFocus);
                    if (mLastDeep != null && mLastDeep.hasDeepFocus()) {
                        mLastDeep.onFocusDeeped(false, direction, null);
                        mLastDeep = null;
                    }
                    mDeep.onFocusDeeped(true, mNextDirection, previouslyFocusedRect);
                    NodeInfo info = this.mNodeMap.get(mNextFocus);
                    mIndex = info.index;
                    reset();
                }
                return true;
            }

            if (mLastDeep != null && mLastDeep.hasDeepFocus()) {
                mLastDeep.onFocusDeeped(false, direction, null);
            }
            mLastSelectedView = getSelectedView();

            if (mLastSelectedView != null) {
                mLastSelectedView.setSelected(false);
                performItemSelect(mLastSelectedView, false, false);
                OnFocusChangeListener listener = mLastSelectedView.getOnFocusChangeListener();
                if (listener != null) {
                    listener.onFocusChange(mLastSelectedView, false);
                }
            }

            NodeInfo info = this.mNodeMap.get(mNextFocus);
            mIndex = info.index;

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    info.fromRight = mLastSelectedView;
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    info.fromLeft = mLastSelectedView;
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    info.fromUp = mLastSelectedView;
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    info.fromDown = mLastSelectedView;
                    break;
            }

            mLastDeep = null;
            View selectedView = getSelectedView();
            if (selectedView != null) {
                selectedView.setSelected(true);
                performItemSelect(selectedView, true, false);
                OnFocusChangeListener listener = selectedView.getOnFocusChangeListener();
                if (listener != null) {
                    listener.onFocusChange(selectedView, true);
                }
            }

            reset();
            this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(mNextDirection));
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    protected void reset() {
        if (getSelectedView() == null) {
            return;
        }

        if (mDeep != null) {
            mFocusRectparams.set(mDeep.getFocusParams());
        } else {
            if (mIndex == -1 && getChildCount() > 0) {
                mIndex = getFocusableItemIndex();
            }
            ItemListener item = (ItemListener) getSelectedView();
            if (item != null) {
                mFocusRectparams.set(item.getFocusParams());
            }
        }

        offsetDescendantRectToMyCoords(getSelectedView(), mFocusRectparams.focusRect());
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect, ViewGroup findRoot) {
        mFindRootView = findRoot;
        onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        mFindRootView = null;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        AppDebug.d(TAG, "onFocusChanged");
        if (getOnFocusChangeListener() != null) {
            getOnFocusChangeListener().onFocusChange(this, gainFocus);
        }

        if (gainFocus) {
            mNeedFocused = false;
            if (this.mAutoSearchFocus && previouslyFocusedRect != null) {
                if (mFindRootView == null) {
                    mFindRootView = this;
                }
                View v = this.mFocusFinder.findNextFocusFromRect(mFindRootView, previouslyFocusedRect, direction);
                if (this.mNodeMap.containsKey(v)) {
                    NodeInfo info = this.mNodeMap.get(v);
                    mIndex = info.index;
                } else {
                    if (mIndex < 0) {
                        mIndex = getFocusableItemIndex();
                    }
                }
            } else {
                if (mIndex < 0) {
                    mIndex = getFocusableItemIndex();
                }
            }

            if (getSelectedView() instanceof DeepListener) {
                mDeep = (DeepListener) getSelectedView();
                if (mDeep.canDeep()) {
                    Rect rect = new Rect(previouslyFocusedRect);
                    offsetRectIntoDescendantCoords((View) mDeep, rect);
                    mDeep.onFocusDeeped(gainFocus, direction, rect);
                }
            }
            if (!mLayouted) {
                mNeedReset = true;
            } else {
                reset();
                performItemSelect(getSelectedView(), gainFocus, true);
            }
        } else {
            if (mDeep != null && mDeep.canDeep()) {
                mDeep.onFocusDeeped(gainFocus, direction, null);
            } else {
                if (mLayouted) {
                    performItemSelect(getSelectedView(), gainFocus, true);
                } else {
                    mNeedReset = true;
                }
            }

            mDeep = null;
        }

        mIsAnimate = checkAnimate(direction);
    }

    private boolean checkAnimate(int direction) {
        switch (direction) {
            case View.FOCUS_LEFT:
                return mAimateWhenGainFocusFromRight ? true : false;
            case View.FOCUS_UP:
                return mAimateWhenGainFocusFromDown ? true : false;
            case View.FOCUS_RIGHT:
                return mAimateWhenGainFocusFromLeft ? true : false;
            case View.FOCUS_DOWN:
                return mAimateWhenGainFocusFromUp ? true : false;
        }

        return true;
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
        if ((hasFocus() || hasDeepFocus()) && item != null) {
            if (item != null) {
                item.getFocusedRect(r);
                this.offsetDescendantRectToMyCoords(item, r);
                return;
            }
        }
        super.getFocusedRect(r);
    }

    public void setSelectedView(View v) {
        if (!this.mNodeMap.containsKey(v)) {
            throw new IllegalArgumentException("Parent does't contain this view");
        }

    }

    public View getSelectedView() {
        int indexOfView = mIndex;
        View selectedView = getChildAt(indexOfView);
        return selectedView;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        super.onLayout(changed, l, t, r, b);
        initNode();
        mLayouted = true;
        reset();
        if (mNeedReset) {
            performItemSelect(getSelectedView(), hasFocus() || hasDeepFocus(), true);
            mNeedReset = false;
        }
    }

    public void forceInitNode() {
        mNeedInitNode = true;
        initNode();
    }

    protected void initNode() {
        if (mNeedInitNode) {
            mFocusFinder.clearFocusables();
            mFocusFinder.initFocusables(this);

            this.mNodeMap.clear();
            for (int index = 0; index < this.getChildCount(); index++) {
                View child = this.getChildAt(index);
                if (!child.isFocusable()) {
                    continue;
                }

                if (!(child instanceof ItemListener)) {
                    continue;
                }

                if (!this.mNodeMap.containsKey(child)) {
                    NodeInfo info = new NodeInfo();
                    info.index = index;
                    this.mNodeMap.put(child, info);
                }
            }

            mNeedInitNode = false;
        }

    }

    public void notifyLayoutChanged() {
        AppDebug.d(TAG, "notifyLayoutChanged");
        mNeedInitNode = true;
        requestLayout();
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        mLayouted = false;
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

    private static class NodeInfo {

        public int index;
        public View fromLeft;
        public View fromRight;
        public View fromUp;
        public View fromDown;
    }

    @Override
    public FocusRectParams getFocusParams() {
        View v = getSelectedView();
        if ((hasFocus() || hasDeepFocus()) && v != null) {
            if (mFocusRectparams == null || isScrolling()) {
                reset();
            }
            return mFocusRectparams;
        } else {
            Rect r = new Rect();
            getFocusedRect(r);

            mFocusRectparams.set(r, 0.5f, 0.5f);
            return mFocusRectparams;
        }
    }

    @Override
    public boolean canDraw() {
        if (mDeep != null) {
            return mDeep.canDraw();
        }
        return getSelectedView() != null;
    }

    @Override
    public boolean isAnimate() {
        if (mDeep != null) {
            return mDeep.isAnimate();
        }
        return mIsAnimate;
    }

    @Override
    public ItemListener getItem() {
        View v = getSelectedView();
        if ((hasFocus() || hasDeepFocus()) && v != null) {
            if (mDeep != null) {
                if (mDeep.hasDeepFocus()) {
                    return mDeep.getItem();
                } else if (mLastDeep != null) {
                    return mLastDeep.getItem();
                }
            } else if (mLastDeep != null) {
                return mLastDeep.getItem();
            }

            return (ItemListener) getSelectedView();
        } else {
            return this;
        }
    }

    @Override
    public boolean isScrolling() {
        return false;
    }

    @Override
    public Params getParams() {
        if (mParams == null) {
            throw new IllegalArgumentException("The params is null, you must call setScaleParams before it's running");
        }

        return mParams;
    }

    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        AppDebug.d(TAG, "preOnKeyDown keyCode = " + keyCode);
        if (mDeep != null) {
            if (mDeep.preOnKeyDown(keyCode, event)) {
                return true;
            }
        }

        View selectedView = getSelectedView();
        NodeInfo nodeInfo = this.mNodeMap.get(selectedView);
        View nextFocus = null;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mNextDirection = FOCUS_LEFT;
                if (nodeInfo != null && nodeInfo.fromLeft != null && nodeInfo.fromLeft.isFocusable()) {
                    nextFocus = nodeInfo.fromLeft;
                } else {
                    nextFocus = mFocusFinder.findNextFocus(this, selectedView, mNextDirection);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mNextDirection = FOCUS_RIGHT;
                if (nodeInfo != null && nodeInfo.fromRight != null && nodeInfo.fromRight.isFocusable()) {
                    nextFocus = nodeInfo.fromRight;
                } else {
                    nextFocus = mFocusFinder.findNextFocus(this, selectedView, mNextDirection);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mOnViewKeyDownUpListener != null) {
                    mOnViewKeyDownUpListener.onViewKeyDownUp(keyCode, event);
                    return true;
                } else {
                    mNextDirection = FOCUS_DOWN;
                    if (nodeInfo != null && nodeInfo.fromDown != null && nodeInfo.fromDown.isFocusable()) {
                        nextFocus = nodeInfo.fromDown;
                    } else {
                        nextFocus = mFocusFinder.findNextFocus(this, selectedView, mNextDirection);
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (mOnViewKeyDownUpListener != null) {
                    mOnViewKeyDownUpListener.onViewKeyDownUp(keyCode, event);
                    return true;
                } else {
                    mNextDirection = FOCUS_UP;
                    if (nodeInfo != null && nodeInfo.fromUp != null && nodeInfo.fromUp.isFocusable()) {
                        nextFocus = nodeInfo.fromUp;
                    } else {
                        nextFocus = mFocusFinder.findNextFocus(this, selectedView, mNextDirection);
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
                return true;
            default:
                return false;
        }

        mNextFocus = nextFocus;
        if (nextFocus != null) {
            if (mDeep != null) {
                mLastDeep = mDeep;
                mDeep = null;
            }
            if (nextFocus instanceof DeepListener) {
                mDeep = (DeepListener) nextFocus;
                if (!mDeep.canDeep()) {
                    mDeep = null;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    Rect getFocusedRect(View from, View to) {
        Rect rFrom = new Rect();
        from.getFocusedRect(rFrom);
        Rect rTo = new Rect();
        to.getFocusedRect(rTo);

        offsetDescendantRectToMyCoords(from, rFrom);
        offsetDescendantRectToMyCoords(to, rTo);

        int xDiff = rFrom.left - rTo.left;
        int yDiff = rFrom.top - rTo.top;
        int rWidth = rFrom.width();
        int rheight = rFrom.height();
        rFrom.left = xDiff;
        rFrom.right = rFrom.left + rWidth;
        rFrom.top = yDiff;
        rFrom.bottom = rFrom.top + rheight;

        return rFrom;
    }

    @Override
    public boolean hasDeepFocus() {
        return mDeepFocus;
    }

    @Override
    public boolean canDeep() {
        return true;
    }

    @Override
    public void onFocusDeeped(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        mDeepFocus = gainFocus;
        onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    public boolean isScale() {
        return true;
    }

    @Override
    public int getItemWidth() {
        View v = getSelectedView();
        if ((hasFocus() || hasDeepFocus()) && v != null) {
            return v.getWidth();
        }

        return getWidth();
    }

    @Override
    public int getItemHeight() {
        View v = getSelectedView();
        if ((hasFocus() || hasDeepFocus()) && v != null) {
            return v.getHeight();
        }

        return getHeight();
    }

    @Override
    public Rect getManualPadding() {
        return null;
    }

    @Override
    public void onFocusStart() {
        if (mDeep != null) {
            mDeep.onFocusStart();
            return;
        }
        if (mFocusStateListener != null) {
            mFocusStateListener.onFocusStart(getSelectedView(), this);
        }
    }

    @Override
    public void onFocusFinished() {
        if (mDeep != null) {
            mDeep.onFocusFinished();
            return;
        }
        if (mFocusStateListener != null) {
            mFocusStateListener.onFocusFinished(getSelectedView(), this);
        }
    }

    /**
     * Interface definition for a callback to be invoked when an item in this
     * AdapterView has been clicked.
     */
    public interface OnItemClickListener {

        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p>
         * Implementers can call getItemAtPosition(position) if they need to
         * access the data associated with the selected item.
         * @param parent
         *            The AdapterView where the click happened.
         * @param view
         *            The view within the AdapterView that was clicked (this
         *            will be a view provided by the adapter)
         * @param position
         *            The position of the view in the adapter.
         * @param id
         *            The row id of the item that was clicked.
         */
        void onItemClick(ViewGroup parent, View view);
    }

    @Override
    public void onItemSelected(boolean selected) {
        performItemSelect(getSelectedView(), selected, false);
    }

    @Override
    public void onItemClick() {
        performClick();
    }

    private int getFocusableItemIndex() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.isFocusable() && childView.getVisibility() == View.VISIBLE) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public boolean isFocusBackground() {
        if (mDeep != null) {
            return mDeep.isFocusBackground();
        }
        return mFocusBackground;
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

    public interface onViewKeyDownUpListener {

        void onViewKeyDownUp(int keyCode, KeyEvent event);
    }

    /*
     * (non-Javadoc)
     * @see com.yunos.tv.app.widget.focus.listener.FocusListener#getClipFocusRect()
     */
    @Override
    public Rect getClipFocusRect() {
        //TODO Auto-generated method stub
        if (mClipFocusRect != null) {
            return mClipFocusRect;
        }
        return new Rect();
    }

}
