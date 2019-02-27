package com.yunos.tvtaobao.tradelink.buildorder.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.ListView;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.core.common.AppDebug;

import java.util.ArrayList;

public class TabFocusListView extends ListView implements DeepListener, ItemListener {

    protected static String TAG = "TabFocusListView";
    protected static boolean DEBUG = false;
    protected Params mParams = new Params(1.05f, 1.05f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());
    FocusRectParams mFocusRectparams = new FocusRectParams();

    boolean mIsAnimate = true;
    int mDistance = -1;
    boolean mDeepFocus = false;
    boolean mAutoSearch = false;
    ItemSelectedListener mItemSelectedListener;
    boolean mLayouted = false;
    boolean mReset = false;
    boolean mFocusBackground = false;

    boolean mAimateWhenGainFocusFromLeft = true;
    boolean mAimateWhenGainFocusFromRight = true;
    boolean mAimateWhenGainFocusFromUp = true;
    boolean mAimateWhenGainFocusFromDown = true;

    int mItemHeight = 0;

    DeepListener mDeep = null;
    // DeepListener mLastDeep = null;

    boolean mNeedReset = false;
    boolean mDeepMode = false;

    private OnTabKeyDownListener mOnTabKeyDownListener;
    private OnLayoutStateListener mOnLayoutStateListener;

    private Rect mClipFocusRect = new Rect(); // 默认focus框

    public TabFocusListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TabFocusListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabFocusListView(Context context) {
        super(context);
    }

    public void setDeepMode(boolean mode) {
        this.mDeepMode = mode;
    }

    public void setAnimateWhenGainFocus(boolean fromleft, boolean fromUp, boolean fromRight, boolean fromDown) {
        mAimateWhenGainFocusFromLeft = fromleft;
        mAimateWhenGainFocusFromUp = fromUp;
        mAimateWhenGainFocusFromRight = fromRight;
        mAimateWhenGainFocusFromDown = fromDown;
    }

    public void setFocusBackground(boolean back) {
        mFocusBackground = back;
    }

    public void setOnItemSelectedListener(ItemSelectedListener listener) {
        mItemSelectedListener = listener;
    }
     


    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        AppDebug.d(TAG, "onFocusChanged");

        if (!mAutoSearch) {
            if (getOnFocusChangeListener() != null) {
                getOnFocusChangeListener().onFocusChange(this, gainFocus);
            }
        }

        if (mAutoSearch) {
            super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        }

        if (gainFocus) {
            if (getVisibleChildCount() > 0 && mLayouted) {
                // getFocusParams();
                if (getLeftScrollDistance() == 0) {
                    reset();
                }
            }

            if (mDeepMode) {
                boolean isDeep = false;
                if (getSelectedView() instanceof DeepListener) {
                    mDeep = (DeepListener) getSelectedView();
                    if (mDeep.canDeep()) {
                        isDeep = true;
                        Rect focusRect = new Rect(previouslyFocusedRect);
                        offsetRectIntoDescendantCoords((View) mDeep, focusRect);
                        mDeep.onFocusDeeped(gainFocus, direction, focusRect);
                        reset();
                    }
                }

                if (!isDeep) {
                    if (!mLayouted) {
                        mNeedReset = true;
                    } else {
                        reset();
                        performSelect(gainFocus);
                    }
                }
            } else {
                performSelect(gainFocus);
            }
        } else {
            if (mDeepMode) {
                if (mDeep != null && mDeep.canDeep()) {
                    mDeep.onFocusDeeped(gainFocus, direction, null);
                } else {
                    if (mLayouted) {
                        performSelect(gainFocus);
                    } else {
                        mNeedReset = true;
                    }
                }
            } else {
                performSelect(gainFocus);
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
    
    public void setSelectionAndLayouted(int position){
        AppDebug.d(TAG, "setSelectionAndLayouted position = " + position + "; mLayouted = " + mLayouted);
        setSelectedPositionInt(position);
        setNextSelectedPositionInt(position);
        mLayoutMode = LAYOUT_FROM_MIDDLE;
        if (!isLayoutRequested()) {
            AppDebug.d(TAG, "setSelectionAndLayouted isLayoutRequested = " + isLayoutRequested());
            layoutChildren();
        }
        reset();
    }
    

    @Override
    public void setSelection(int position) {
        setSelectedPositionInt(position);
        setNextSelectedPositionInt(position);
        if (getVisibleChildCount() > 0 && mLayouted) {
            mLayoutMode = LAYOUT_FROM_MIDDLE;
            if (isLayoutRequested()) {
                return;
            }
            layoutChildren();
        }

        reset();
    }

    @Override
    protected void layoutChildren() {
        final boolean blockLayoutRequests = mBlockLayoutRequests;
        if (!blockLayoutRequests) {
            mBlockLayoutRequests = true;
        } else {
            return;
        }

        try {
            // super.layoutChildren();

            invalidate();

            if (getAdapter() == null) {
                resetList();
                // invokeOnItemScrollListener();
                return;
            }

            int childrenTop = mListPadding.top;
            int childrenBottom = getBottom() - getTop() - mListPadding.bottom;

            int visibleChildCount = getVisibleChildCount();
            int childCount = getChildCount();
            int index = 0;
            int delta = 0;

            View sel = null;
            View oldSel = null;
            View oldFirst = null;
            View newSel = null;
            View focusLayoutRestoreView = null;
            int lastPosition = getLastPosition();
            boolean dataChanged = mDataChanged;
            if (dataChanged) {
                mLayoutMode = LAYOUT_FROM_MIDDLE;
            }

            // AccessibilityNodeInfo accessibilityFocusLayoutRestoreNode = null;
            // View accessibilityFocusLayoutRestoreView = null;
            // int accessibilityFocusPosition = INVALID_POSITION;

            // Remember stuff we will need down below
            switch (mLayoutMode) {
                case LAYOUT_SET_SELECTION:
                    index = mNextSelectedPosition - mFirstPosition;
                    if (index >= 0 && index < visibleChildCount) {
                        newSel = getChildAt(index + getUpPreLoadedCount());
                    }
                    break;
                case LAYOUT_FORCE_TOP:
                case LAYOUT_FORCE_BOTTOM:
                case LAYOUT_SPECIFIC:
                case LAYOUT_SYNC:
                    break;
                default:
                    // Remember the previously selected view
                    index = mSelectedPosition - mFirstPosition;
                    if (index >= 0 && index < visibleChildCount) {
                        oldSel = getChildAt(index + getUpPreLoadedCount());
                    }

                    // Remember the previous first child
                    oldFirst = getFirstVisibleChild();

                    if (mNextSelectedPosition >= 0) {
                        delta = mNextSelectedPosition - mSelectedPosition;
                    }

                    // Caution: newSel might be null
                    newSel = getChildAt(index + delta + getUpPreLoadedCount());
                    break;
            }

            if (dataChanged) {
                handleDataChanged();
            }

            // Handle the empty set by removing all views that are visible
            // and calling it a day
            if (mItemCount == 0) {
                resetList();
                // invokeOnItemScrollListener();
                return;
            } else if (mItemCount != getAdapter().getCount()) {
                throw new IllegalStateException("The content of the adapter has changed but "
                        + "ListView did not receive a notification. Make sure the content of "
                        + "your adapter is not modified from a background thread, but only "
                        + "from the UI thread. [in ListView(" + getId() + ", " + getClass() + ") with Adapter("
                        + getAdapter().getClass() + ")]");
            }

            setSelectedPositionInt(mNextSelectedPosition);

            // Pull all children into the RecycleBin.
            // These views will be reused if possible
            final int firstPosition = mFirstPosition;
            final RecycleBin recycleBin = mRecycler;

            // reset the focus restoration
            View focusLayoutRestoreDirectChild = null;

            // Don't put header or footer views into the Recycler. Those are
            // already cached in mHeaderViews;
            if (dataChanged) {
                int firstvisibleChildIndex = getFirsVisibletChildIndex();
                for (int i = firstvisibleChildIndex - 1; i >= 0; i--) {
                    recycleBin.addScrapView(getChildAt(i), firstPosition + (i - getUpPreLoadedCount()));
                }
                for (int i = firstvisibleChildIndex; i < childCount; i++) {
                    recycleBin.addScrapView(getChildAt(i), firstPosition + (i - getUpPreLoadedCount()));
                }
            } else {
                recycleBin.fillActiveViews(childCount, firstPosition);
            }

            // take focus back to us temporarily to avoid the eventual
            // call to clear focus when removing the focused child below
            // from messing things up when ViewAncestor assigns focus back
            // to someone else
            final View focusedChild = getFocusedChild();
            if (focusedChild != null) {
                // TODO: in some cases focusedChild.getParent() == null

                // we can remember the focused view to restore after relayout if
                // the
                // data hasn't changed, or if the focused position is a header
                // or footer
                if (!dataChanged || isDirectChildHeaderOrFooter(focusedChild)) {
                    focusLayoutRestoreDirectChild = focusedChild;
                    // remember the specific view that had focus
                    focusLayoutRestoreView = findFocus();
                    if (focusLayoutRestoreView != null) {
                        // tell it we are going to mess with it
                        focusLayoutRestoreView.onStartTemporaryDetach();
                    }
                }
                requestFocus();
            }

            // Clear out old views
            detachAllViewsFromParent();
            recycleBin.removeSkippedScrap();
            mUpPreLoadedCount = 0;
            mDownPreLoadedCount = 0;

            switch (mLayoutMode) {
                case LAYOUT_FORCE_BOTTOM:
                    sel = fillUp(mItemCount - 1, childrenBottom);
                    adjustViewsUpOrDown();
                    break;
                case LAYOUT_FORCE_TOP:
                    mFirstPosition = 0;
                    sel = fillFromTop(childrenTop);
                    adjustViewsUpOrDown();
                    break;
                case LAYOUT_SPECIFIC:
                    sel = fillSpecific(reconcileSelectedPosition(), mSpecificTop);
                    break;
                case LAYOUT_FROM_MIDDLE:
                    sel = fillFromMiddle(childrenTop, childrenBottom);
                    break;
                default:
                    if (childCount == 0) {
                        if (!mStackFromBottom) {
                            final int position = lookForSelectablePosition(mSelectedPosition, true);
                            setSelectedPositionInt(position);
                            sel = fillFromTop(childrenTop);
                        } else {
                            final int position = lookForSelectablePosition(mItemCount - 1, false);
                            setSelectedPositionInt(position);
                            sel = fillUp(mItemCount - 1, childrenBottom);
                        }
                    } else {
                        int selectPsotion = mSelectedPosition;
                        if (mSelectedPosition >= firstPosition + delta && mSelectedPosition <= lastPosition + delta) {
                            selectPsotion = mSelectedPosition;
                        } else {
                            oldSel = oldFirst;
                            selectPsotion = mFirstPosition + delta;
                        }

                        if (mSelectedPosition >= 0 && mSelectedPosition < mItemCount) {
                            sel = fillSpecific(selectPsotion, oldSel == null ? childrenTop : oldSel.getTop());
                        } else if (mFirstPosition < mItemCount) {
                            sel = fillSpecific(mFirstPosition, oldFirst == null ? childrenTop : oldFirst.getTop());
                        } else {
                            sel = fillSpecific(0, childrenTop);
                        }
                    }
                    break;
            }

            // Flush any cached views that did not get reused above
            recycleBin.scrapActiveViews();

            if (sel != null) {
                // the current selected item should get focus if items
                // are focusable
                if (mItemsCanFocus && hasFocus() && !sel.hasFocus()) {
                    final boolean focusWasTaken = (sel == focusLayoutRestoreDirectChild
                            && focusLayoutRestoreView != null && focusLayoutRestoreView.requestFocus())
                            || sel.requestFocus();
                    if (!focusWasTaken) {
                        // selected item didn't take focus, fine, but still want
                        // to make sure something else outside of the selected
                        // view
                        // has focus
                        final View focused = getFocusedChild();
                        if (focused != null) {
                            focused.clearFocus();
                        }
                        positionSelector(INVALID_POSITION, sel);
                    } else {
                        sel.setSelected(false);
                        mSelectorRect.setEmpty();
                    }
                } else {
                    positionSelector(INVALID_POSITION, sel);
                }
                // mSelectedTop = sel.getTop();
            } else {
                if (mTouchMode > TOUCH_MODE_DOWN && mTouchMode < TOUCH_MODE_SCROLL) {
                    View child = getChildAt(mMotionPosition - mFirstPosition);
                    if (child != null)
                        positionSelector(mMotionPosition, child);
                } else {
                    // mSelectedTop = 0;
                    mSelectorRect.setEmpty();
                }

                // even if there is not selected position, we may need to
                // restore
                // focus (i.e. something focusable in touch mode)
                if (hasFocus() && focusLayoutRestoreView != null) {
                    focusLayoutRestoreView.requestFocus();
                }
            }

            // tell focus view we are done mucking with it, if it is still in
            // our view hierarchy.
            if (focusLayoutRestoreView != null && focusLayoutRestoreView.getWindowToken() != null) {
                focusLayoutRestoreView.onFinishTemporaryDetach();
            }

            mLayoutMode = LAYOUT_NORMAL;
            mDataChanged = false;
            // if (mPositionScrollAfterLayout != null) {
            // post(mPositionScrollAfterLayout);
            // mPositionScrollAfterLayout = null;
            // }
            mNeedSync = false;
            setNextSelectedPositionInt(mSelectedPosition);

            // updateScrollIndicators();

            if (mItemCount > 0) {
                checkSelectionChanged();
            }

            // invokeOnItemScrollListener();
        } finally {
            if (!blockLayoutRequests) {
                mBlockLayoutRequests = false;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (hasFocus() || hasDeepFocus()) {
            if (getLeftScrollDistance() == 0) {
                reset();
            }
        }

        if (mNeedReset) {
            performSelect(hasFocus() || hasDeepFocus());
            mNeedReset = false;
        }

        mLayouted = true;
        
        if (mOnLayoutStateListener != null) {
            // onLayout完成
            mOnLayoutStateListener.layoutFinish(this);
        }
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        mLayouted = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int index = -1;
        if (getAdapter() != null) {

            // 如果没有头部，则item的高度就是第一个元素的高度
            if (getAdapter().getCount() > 0) {
                index = 0;
            }

            // 如果有头部，则item的高度就是头部下方第一个元素的高度
            if (getHeaderViewsCount() > 0 && getAdapter().getCount() > getHeaderViewsCount()) {
                index = getHeaderViewsCount();
            }

            final View child = obtainView(index, mIsScrap);
            if (child != null) {
                int measureHeight = child.getMeasuredHeight();
                if (measureHeight == 0) {// 如果有item的高度为0，则重新测量高度
                    int unSpecified = View.MeasureSpec.UNSPECIFIED;
                    int w = View.MeasureSpec.makeMeasureSpec(0, unSpecified);
                    int h = View.MeasureSpec.makeMeasureSpec(0, unSpecified);
                    child.measure(w, h);
                    measureHeight = child.getMeasuredHeight();
                }
                mItemHeight = measureHeight;
            }
        }
    }

    @Override
    public Params getParams() {
        if (mParams == null) {
            throw new IllegalArgumentException("The params is null, you must call setScaleParams before it's running");
        }

        return mParams;
    }

    @Override
    public void getFocusedRect(Rect r) {
        if (hasFocus() || hasDeepFocus()) {
            super.getFocusedRect(r);
            return;
        }

        getDrawingRect(r);
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (hasFocus()) {
            super.addFocusables(views, direction, focusableMode);
            return;
        }

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

    protected void reset() {
        if (getSelectedView() == null) {
            return;
        }

        if (mDeep != null) {
            mFocusRectparams.set(mDeep.getFocusParams());
        } else {
            ItemListener item = (ItemListener) getSelectedView();
            if (item != null) {
                mFocusRectparams.set(item.getFocusParams());
            }
        }

        offsetDescendantRectToMyCoords(getSelectedView(), mFocusRectparams.focusRect());
    }

    private void resetHeader(int nextSelectedPosition) {
        View header = getHeaderView(nextSelectedPosition);
        ItemListener item = (ItemListener) header;

        if (item != null) {
            mFocusRectparams.set(item.getFocusParams());
        }
        // mFocusRectparams.focusRect().left = mListPadding.left;
        // mFocusRectparams.focusRect().right = mListPadding.left +
        // header.getWidth();
        int top = getChildAt(0).getTop();

        for (int index = getFirstPosition() - 1; index >= 0; index--) {
            if (index >= getHeaderViewsCount()) {
                top -= mItemHeight;
            } else {
                top -= getHeaderView(index).getHeight();
            }
        }

        mFocusRectparams.focusRect().top = top;
        mFocusRectparams.focusRect().bottom = top + header.getHeight();

        // offsetDescendantRectToMyCoords(getSelectedView(),
        // mFocusRectparams.focusRect());
    }

    @Override
    public FocusRectParams getFocusParams() {
        return mFocusRectparams;
    }

    @Override
    public boolean canDraw() {
        if (mDeep != null) {
            return mDeep.canDraw();
        }

        View v = getSelectedView();
        if (v != null && mReset) {
            performSelect(true);
            mReset = false;
        }
        return getSelectedView() != null && mLayouted;
    }

    @Override
    public boolean isAnimate() {
        if (mDeep != null) {
            return mDeep.isAnimate();
        }

        return mIsAnimate;
    }

    public boolean onKeyDownDeep(int keyCode, KeyEvent event) {
        if (mDeepMode && mDeep != null && mDeep.canDeep() && mDeep.hasDeepFocus()) {// by
            // leiming.yanlm
            if (mDeep.onKeyDown(keyCode, event)) {
                reset();
                mFocusRectparams.focusRect().offset(0, -getLeftScrollDistance());
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AppDebug.d(TAG, "onKeyDown keyCode = " + keyCode);

        if (onKeyDownDeep(keyCode, event)) {
            return true;
        }

        if (getChildCount() <= 0) {
            return super.onKeyDown(keyCode, event);
        }

        if (checkState(keyCode)) {
            return true;
        }

        if (mDistance < 0) {
            mDistance = getChildAt(0).getHeight();
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (moveUp()) {
                    playSoundEffect(SoundEffectConstants.NAVIGATION_LEFT);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (moveDown()) {
                    playSoundEffect(SoundEffectConstants.NAVIGATION_LEFT);
                    return true;
                }
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mDeep != null && mDeep.canDeep() && mDeep.hasDeepFocus()) {
            return mDeep.onKeyUp(keyCode, event);
        }
        return super.onKeyUp(keyCode, event);
    }

    private boolean moveUp() {
        int nextSelectedPosition = getSelectedItemPosition() - 1 >= 0 ? getSelectedItemPosition() - 1
                : INVALID_POSITION;
        if (mDeepMode) {
            if (getChildAt(nextSelectedPosition - getFirstPosition()) == null) {
                return true;
            }
        }

        performSelect(false);
        mReset = false;

        if (nextSelectedPosition != INVALID_POSITION) {
            setSelectedPositionInt(nextSelectedPosition);
            setNextSelectedPositionInt(nextSelectedPosition);

            if (mDeepMode) {
                View lastSelectView = (View) mDeep;
                Rect focusRect = new Rect(mDeep.getFocusParams().focusRect());
                mDeep.onFocusDeeped(false, View.FOCUS_UP, null);
                mDeep = null;
                DeepListener deep = (DeepListener) getSelectedView();
                if (deep.canDeep()) {
                    mDeep = deep;
                    offsetDescendantRectToMyCoords(lastSelectView, focusRect);
                    offsetRectIntoDescendantCoords(getSelectedView(), focusRect);
                    mDeep.onFocusDeeped(true, View.FOCUS_UP, focusRect);
                }
            }

            if (canDraw()) {
                mReset = false;
                performSelect(true);
            } else {
                mReset = true;
            }

            int amountToScroll = amountToCenterScroll(FOCUS_UP, nextSelectedPosition);

            // if (mIsAnimate) {
            // reset();
            // if (amountToScroll != 0) {
            // smoothScrollBy(amountToScroll);
            // mFocusRectparams.focusRect().top -= amountToScroll;
            // mFocusRectparams.focusRect().bottom -= amountToScroll;
            // }
            // }
            return true;
        }

        return false;
    }

    private void performSelect(boolean select) {
        if (mItemSelectedListener != null) {
            mItemSelectedListener.onItemSelected(getSelectedView(), getSelectedItemPosition(), select, this);
        }
    }

    private boolean moveDown() {
        int nextSelectedPosition = getSelectedItemPosition() + 1 < mItemCount ? getSelectedItemPosition() + 1
                : INVALID_POSITION;
        if (mDeepMode) {
            if (getChildAt(nextSelectedPosition - getFirstPosition()) == null) {
                return true;
            }
        }
        performSelect(false);
        mReset = false;

        if (nextSelectedPosition != INVALID_POSITION) {
            setSelectedPositionInt(nextSelectedPosition);
            setNextSelectedPositionInt(nextSelectedPosition);
            if (mDeepMode) {
                View lastSelectView = (View) mDeep;
                Rect focusRect = new Rect(mDeep.getFocusParams().focusRect());
                mDeep.onFocusDeeped(false, View.FOCUS_DOWN, null);
                mDeep = null;
                DeepListener deep = (DeepListener) getSelectedView();
                if (deep.canDeep()) {
                    mDeep = deep;
                    offsetDescendantRectToMyCoords(lastSelectView, focusRect);
                    offsetRectIntoDescendantCoords(getSelectedView(), focusRect);
                    mDeep.onFocusDeeped(true, View.FOCUS_DOWN, focusRect);
                }
            }

            if (canDraw()) {
                mReset = false;
                performSelect(true);
            } else {
                mReset = true;
            }

            int amountToScroll = amountToCenterScroll(FOCUS_DOWN, nextSelectedPosition);

            // if (mIsAnimate) {
            // reset();
            // if (amountToScroll != 0) {
            // smoothScrollBy(amountToScroll);
            // // mFocusRectparams.focusRect().top -= amountToScroll;
            // // mFocusRectparams.focusRect().bottom -= amountToScroll;
            // }
            // }
            return true;
        }

        return false;
    }

    public boolean checkState(int keyCode) {
        if (mLastScrollState == OnScrollListener.SCROLL_STATE_FLING) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                return true;
            }
        }

        return false;
    }

    int amountToCenterScroll(int direction, int nextSelectedPosition) {
        int center = (getHeight() - mListPadding.top - mListPadding.bottom) / 2 + mListPadding.top;
        final int listBottom = getHeight() - mListPadding.bottom;
        final int listTop = mListPadding.top;
        final int numChildren = getVisibleChildCount();
        int amountToScroll = 0;
        int distanceLeft = getLeftScrollDistance();
        if (direction == View.FOCUS_DOWN) {
            View nextSelctedView = getChildAt(nextSelectedPosition - getFirstPosition());
            int nextSelectedCenter = 0;
            boolean reset = false;
            if (nextSelctedView == null) {
                nextSelctedView = getLastChild();
                nextSelectedCenter = (nextSelctedView.getTop() + nextSelctedView.getBottom()) / 2;
                nextSelectedCenter += nextSelctedView.getHeight() * (nextSelectedPosition - getLastPosition());

                reset = false;
            } else {
                nextSelectedCenter = (nextSelctedView.getTop() + nextSelctedView.getBottom()) / 2;
                reset = true;
            }

            int finalNextSelectedCenter = nextSelectedCenter - distanceLeft;

            if (finalNextSelectedCenter > center) {
                amountToScroll = finalNextSelectedCenter - center;
                int maxDiff = nextSelctedView.getHeight() * (mItemCount - getLastVisiblePosition() - 1);
                maxDiff -= distanceLeft;
                View lastVisibleView = getLastVisibleChild();
                if (lastVisibleView.getBottom() > getHeight() - mListPadding.bottom) {
                    maxDiff += (lastVisibleView.getBottom() - (getHeight() - mListPadding.bottom));
                }

                if (amountToScroll > maxDiff) {
                    amountToScroll = maxDiff;
                }

                if (reset) {
                    reset();
                    mFocusRectparams.focusRect().offset(0, -distanceLeft);
                    if (DEBUG) {
                        AppDebug.i(TAG, "amountToCenterScroll: focus rect = " + mFocusRectparams.focusRect()
                                + ", distanceLeft = " + distanceLeft + ", nextSelectedPosition = "
                                + nextSelectedPosition);
                    }
                }

                if (amountToScroll > 0) {
                    if (reset) {
                        mFocusRectparams.focusRect().offset(0, -amountToScroll);
                    } else {
                        mFocusRectparams.focusRect().offset(0, (nextSelctedView.getHeight() - amountToScroll));
                    }

                    if (DEBUG) {
                        AppDebug.d(TAG, "amountToCenterScroll: focus down amountToScroll = " + amountToScroll
                                + ", focus rect = " + mFocusRectparams.focusRect());
                    }
                    smoothScrollBy(amountToScroll);
                    mIsAnimate = true;
                } else {
                    if (!reset) {
                        mFocusRectparams.focusRect().offset(0, nextSelctedView.getHeight());
                    }
                    mIsAnimate = true;
                }
            } else {
                reset();
                mFocusRectparams.focusRect().offset(0, -distanceLeft);
                mIsAnimate = true;
            }

            return amountToScroll;
        } else if (direction == View.FOCUS_UP) {
            View nextSelctedView = getChildAt(nextSelectedPosition - getFirstPosition());
            int nextSelectedCenter = 0;
            boolean reset = false;
            if (nextSelctedView == null) {
                nextSelctedView = getFirstVisibleChild();
                nextSelectedCenter = (nextSelctedView.getTop() + nextSelctedView.getBottom()) / 2;
                if (nextSelectedPosition >= getHeaderViewsCount()) {
                    nextSelectedCenter -= nextSelctedView.getHeight()
                            * (getFirstVisiblePosition() - nextSelectedPosition);
                } else {
                    nextSelectedCenter -= nextSelctedView.getHeight()
                            * (getFirstVisiblePosition() - getHeaderViewsCount());
                    for (int i = getHeaderViewsCount() - 1; i >= nextSelectedPosition; i--) {
                        nextSelectedCenter -= getHeaderView(i).getHeight();
                    }
                }

                reset = false;
            } else {
                nextSelectedCenter = (nextSelctedView.getTop() + nextSelctedView.getBottom()) / 2;
                reset = true;
            }

            int finalNextSelectedCenter = nextSelectedCenter - distanceLeft;

            if (finalNextSelectedCenter < center) {
                amountToScroll = center - finalNextSelectedCenter;
                int maxDiff = 0;
                int start = getHeaderViewsCount() - 1;
                if (getFirstVisiblePosition() >= getHeaderViewsCount()) {
                    maxDiff = nextSelctedView.getHeight() * (getFirstVisiblePosition() - getHeaderViewsCount());
                } else {
                    start = getFirstVisiblePosition() - 1;
                }
                for (int i = start; i >= 0; i--) {
                    maxDiff += getHeaderView(i).getHeight();
                }
                if (maxDiff < 0) {
                    maxDiff = 0;
                }

                maxDiff += distanceLeft;
                View firstVisibleView = getFirstVisibleChild();
                if (firstVisibleView.getTop() < listTop) {
                    maxDiff += (listTop - firstVisibleView.getTop());
                }

                if (amountToScroll > maxDiff) {
                    amountToScroll = maxDiff;
                }

                if (reset) {
                    reset();
                    mFocusRectparams.focusRect().offset(0, -distanceLeft);
                    if (DEBUG) {
                        AppDebug.i(TAG, "amountToCenterScroll: focus rect = " + mFocusRectparams.focusRect()
                                + ", distanceLeft = " + distanceLeft + ", nextSelectedPosition = "
                                + nextSelectedPosition);
                    }
                } else if (nextSelectedPosition < getHeaderViewsCount()) {
                    reset = true;
                    resetHeader(nextSelectedPosition);
                    mFocusRectparams.focusRect().offset(0, -distanceLeft);
                }

                if (amountToScroll > 0) {
                    if (reset) {
                        mFocusRectparams.focusRect().offset(0, amountToScroll);
                    } else {
                        mFocusRectparams.focusRect().offset(0, -(nextSelctedView.getHeight() - amountToScroll));
                    }

                    if (DEBUG) {
                        AppDebug.d(TAG, "amountToCenterScroll: focus down amountToScroll = " + amountToScroll
                                + ", focus rect = " + mFocusRectparams.focusRect());
                    }
                    smoothScrollBy(-amountToScroll);
                    mIsAnimate = true;
                } else {
                    if (!reset) {
                        mFocusRectparams.focusRect().offset(0, -nextSelctedView.getHeight());
                    }
                    mIsAnimate = true;
                }
            } else {
                reset();
                mFocusRectparams.focusRect().offset(0, -distanceLeft);
                mIsAnimate = true;
            }

            return amountToScroll;
        }

        return 0;
    }

    @Override
    public ItemListener getItem() {
        if (mDeep != null) {
            if (mDeep.hasDeepFocus()) {
                return mDeep.getItem();
            }
            // else if (mLastDeep != null) {
            // return mLastDeep.getItem();
            // }
        }
        // else if (mLastDeep != null) {
        // return mLastDeep.getItem();
        // }

        return (ItemListener) getSelectedView();
    }

    @Override
    public boolean isScrolling() {
        return false;
    }

    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {

        if (mOnTabKeyDownListener != null) {
            View selectview = getSelectedView();
            int selectpos = getSelectedItemPosition();
            if (mOnTabKeyDownListener.onTabKeyDown(selectview, selectpos, keyCode, event)) {
                return true;
            }
        }

        if (mDeep != null) {
            if (mDeep.preOnKeyDown(keyCode, event)) {
                return true;
            }
        }

        // if (checkState(keyCode)) {
        // return true;
        // }

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_PAGE_UP:
            case KeyEvent.KEYCODE_MOVE_HOME:
                return getSelectedItemPosition() > 0 ? true : false;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_PAGE_DOWN:
            case KeyEvent.KEYCODE_MOVE_END:
                return getSelectedItemPosition() < mItemCount - 1 ? true : false;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                return false;

            default:
                break;
        }

        return false;
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
    public void onItemSelected(boolean selected) {
        performSelect(selected);
    }

    @Override
    public void onItemClick() {
        if (getSelectedView() != null) {
            performItemClick(getSelectedView(), getSelectedItemPosition(), 0);
        }
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

    @Override
    public void onFocusStart() {
        if (mDeep != null) {
            mDeep.onFocusStart();
            return;
        }

        super.onFocusStart();
    }

    @Override
    public void onFocusFinished() {
        if (mDeep != null) {
            mDeep.onFocusFinished();
            return;
        }
        super.onFocusFinished();
    }

    @Override
    public boolean performItemClick(View view, int position, long id) {
        if (mDeep != null) {
            mDeep.onItemClick();
            return true;
        }

        return super.performItemClick(view, position, id);
    }

    /**
     * 设置onKey的监听
     * @param l
     */
    public void setOnTabKeyDownListener(OnTabKeyDownListener l) {
        mOnTabKeyDownListener = l;
    }

    /**
     * 设置布局状态监听
     * @param l
     */
    public void setOnLayoutStateListener(OnLayoutStateListener l) {
        mOnLayoutStateListener = l;
    }

    public static interface OnTabKeyDownListener {

        public boolean onTabKeyDown(View selectView, int selectPos, int keyCode, KeyEvent event);
    }

    public static interface OnLayoutStateListener {

        // 布局完成监听类
        public void layoutFinish(ListView fatherView);
    }

    /*
     * (non-Javadoc)
     * @see com.yunos.tv.app.widget.focus.listener.FocusListener#getClipFocusRect()
     */
    @Override
    public Rect getClipFocusRect() {
        if (mClipFocusRect != null) {
            return mClipFocusRect;
        }
        return new Rect();
    }
}
