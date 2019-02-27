package com.yunos.tvtaobao.biz.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;

import com.yunos.tv.app.widget.HListView;
import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;

import java.util.ArrayList;


public class FocusGroupHorizonalListView extends HListView implements DeepListener, ItemListener {
    private final String TAG = "FocusGroupHorizonalListView";
    protected static final boolean DEBUG = false;
    
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
    int mItemWidth;
    private Rect mClipFocusRect = new Rect(); // 默认focus框
    public FocusGroupHorizonalListView(Context context) {
        super(context);
    }

    public FocusGroupHorizonalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusGroupHorizonalListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
        Log.d(TAG, "onFocusChanged");
        if (!mAutoSearch) {
            if (getOnFocusChangeListener() != null) {
                getOnFocusChangeListener().onFocusChange(this, gainFocus);
            }
        }

        if (mAutoSearch) {
            super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        }
        if (gainFocus && getChildCount() > 0 && mLayouted) {
            // getFocusParams();
            if (getLeftScrollDistance() == 0) {
                reset();
            }
        }
        
        if(getSelectedView() != null && mLayouted){
            onItemSelected(gainFocus);
        } else {
            mReset = true;
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
    public void setSelection(int position) {
        setSelectedPositionInt(position);
        setNextSelectedPositionInt(position);
        if (getChildCount() > 0 && mLayouted) {
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

            int childrenLeft = mListPadding.left;
            int childrenRight = getRight() - getLeft() - mListPadding.right;

            int childCount = getChildCount();
            int index = 0;
            int delta = 0;

            View sel = null;
            View oldSel = null;
            View oldFirst = null;
            View newSel = null;
            View focusLayoutRestoreView = null;
            int lastPosition = getLastVisiblePosition();
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
                if (index >= 0 && index < childCount) {
                    newSel = getChildAt(index);
                }
                break;
            case LAYOUT_FORCE_LEFT:
            case LAYOUT_FORCE_RIGHT:
            case LAYOUT_SPECIFIC:
            case LAYOUT_SYNC:
                break;
            default:
                // Remember the previously selected view
                index = mSelectedPosition - mFirstPosition;
                if (index >= 0 && index < childCount) {
                    oldSel = getChildAt(index);
                }

                // Remember the previous first child
                oldFirst = getChildAt(0);

                if (mNextSelectedPosition >= 0) {
                    delta = mNextSelectedPosition - mSelectedPosition;
                }

                // Caution: newSel might be null
                newSel = getChildAt(index + delta);
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
                        + "your adapter is not modified from a background thread, but only " + "from the UI thread. [in ListView("
                        + getId() + ", " + getClass() + ") with Adapter(" + getAdapter().getClass() + ")]");
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
                for (int i = 0; i < childCount; i++) {
                    recycleBin.addScrapView(getChildAt(i), firstPosition + i);
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

            switch (mLayoutMode) {
            case LAYOUT_SET_SELECTION:
                if (newSel != null) {
                    sel = fillFromSelection(newSel.getLeft(), childrenLeft, childrenRight);
                } else {
                    sel = fillFromMiddle(childrenLeft, childrenRight);
                }
                break;
            case LAYOUT_SYNC:
                sel = fillSpecific(mSyncPosition, mSpecificLeft);
                break;
            case LAYOUT_MOVE_SELECTION:
                sel = moveSelection(oldSel, newSel, delta, childrenLeft, childrenRight);
                break;
            case LAYOUT_FORCE_RIGHT:
                sel = fillLeft(mItemCount - 1, childrenRight);
                adjustViewsLeftOrRight();
                break;
            case LAYOUT_FORCE_LEFT:
                mFirstPosition = 0;
                sel = fillFromLeft(childrenLeft);
                adjustViewsLeftOrRight();
                break;
            case LAYOUT_SPECIFIC:
                sel = fillSpecific(reconcileSelectedPosition(), mSpecificLeft);
                break;
            case LAYOUT_FROM_MIDDLE:
                sel = fillFromMiddle(childrenLeft, childrenRight);
                break;
            default:
                if (childCount == 0) {
                    if (!mStackFromBottom) {
                        final int position = lookForSelectablePosition(mSelectedPosition, true);
                        setSelectedPositionInt(position);
                        sel = fillFromLeft(childrenLeft);
                    } else {
                        final int position = lookForSelectablePosition(mItemCount - 1, false);
                        setSelectedPositionInt(position);
                        sel = fillLeft(mItemCount - 1, childrenRight);
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
                        sel = fillSpecific(selectPsotion, oldSel == null ? childrenLeft : oldSel.getLeft());
                    } else if (mFirstPosition < mItemCount) {
                        sel = fillSpecific(mFirstPosition, oldFirst == null ? childrenLeft : oldFirst.getLeft());
                    } else {
                        sel = fillSpecific(0, childrenLeft);
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
                    final boolean focusWasTaken = (sel == focusLayoutRestoreDirectChild && focusLayoutRestoreView != null && focusLayoutRestoreView
                            .requestFocus()) || sel.requestFocus();
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

        mLayouted = true;
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
            if (getHeaderViewsCount() > 0 && getAdapter().getCount() > getHeaderViewsCount()) {
                index = getHeaderViewsCount();
            }

            if (getAdapter().getCount() > 0) {
                index = 0;
            }
            if(index >= 0){
                final View child = obtainView(index, mIsScrap);
                mItemWidth = child.getMeasuredWidth();
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
        if ((focusableMode & FOCUSABLES_TOUCH_MODE) == FOCUSABLES_TOUCH_MODE && isInTouchMode() && !isFocusableInTouchMode()) {
            return;
        }
        views.add(this);
    }

    private void reset() {
        ItemListener item = (ItemListener) getSelectedView();
        if (item != null) { // by leiming.yanlm
            mFocusRectparams.set(item.getFocusParams());
            offsetDescendantRectToMyCoords(getSelectedView(), mFocusRectparams.focusRect());
        }
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
        int left = getChildAt(0).getLeft();

        for (int index = getFirstVisiblePosition() - 1; index >= 0; index--) {
            if (index >= getHeaderViewsCount()) {
                if(mItemWidth <= 0){
                    Log.e(TAG, "FocusHList mItemWidth <= 0");
                }
                left -= mItemWidth;
            } else {
                left -= getHeaderView(index).getWidth();
            }
        }

        mFocusRectparams.focusRect().left = left;
        mFocusRectparams.focusRect().right = left + header.getWidth();

        // offsetDescendantRectToMyCoords(getSelectedView(),
        // mFocusRectparams.focusRect());
    }

    @Override
    public FocusRectParams getFocusParams() {
        return mFocusRectparams;
    }

    @Override
    public boolean canDraw() {
        View v = getSelectedView();
        if (v != null && mReset) {
            performSelect(true);
            mReset = false;
        }
        return getSelectedView() != null && mLayouted;
    }

    @Override
    public boolean isAnimate() {
        return mIsAnimate;
    }

    private boolean moveLeft() {
        if (Math.abs(getLeftScrollDistance()) > getChildAt(0).getWidth() * 3) {
            return true;
        }

        performSelect(false);
        mReset = false;
        int nextSelectedPosition = getSelectedItemPosition() - 1 >= 0 ? getSelectedItemPosition() - 1 : INVALID_POSITION;
        if (nextSelectedPosition != INVALID_POSITION) {
            setSelectedPositionInt(nextSelectedPosition);
            setNextSelectedPositionInt(nextSelectedPosition);
            if (canDraw()) {
                mReset = false;
                performSelect(true);
            } else {
                mReset = true;
            }

            amountToCenterScroll(FOCUS_UP, nextSelectedPosition);

            return true;
        }

        return false;
    }

    protected void performSelect(boolean select) {
        if (mItemSelectedListener != null) {
            mItemSelectedListener.onItemSelected(getSelectedView(), getSelectedItemPosition(), select, this);
        }
    }

    private boolean moveRight() {
        if (getLeftScrollDistance() > getChildAt(0).getWidth() * 3) {
            return true;
        }
        performSelect(false);
        mReset = false;
        int nextSelectedPosition = getSelectedItemPosition() + 1 < mItemCount ? getSelectedItemPosition() + 1 : INVALID_POSITION;
        if (nextSelectedPosition != INVALID_POSITION) {
            setSelectedPositionInt(nextSelectedPosition);
            setNextSelectedPositionInt(nextSelectedPosition);
            if (canDraw()) {
                mReset = false;
                performSelect(true);
            } else {
                mReset = true;
            }

            amountToCenterScroll(FOCUS_DOWN, nextSelectedPosition);

            return true;
        }

        return false;
    }

    public boolean checkState(int keyCode) {
        if (mLastScrollState == OnScrollListener.SCROLL_STATE_FLING) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                return true;
            }
        }

        return false;
    }

    int amountToCenterScroll(int direction, int nextSelectedPosition) {
        int center = (getWidth() - mListPadding.left - mListPadding.right) / 2 + mListPadding.left;
        final int listRight = getWidth() - mListPadding.right;
        final int listLeft = mListPadding.left;
        final int numChildren = getChildCount();
        int amountToScroll = 0;
        int distanceLeft = getLeftScrollDistance();
        if (direction == View.FOCUS_DOWN) {
            View nextSelctedView = getChildAt(nextSelectedPosition - mFirstPosition);
            int nextSelectedCenter = 0;
            boolean reset = false;
            if (nextSelctedView == null) {
                nextSelctedView = getChildAt(getChildCount() - 1);
                nextSelectedCenter = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
                nextSelectedCenter += nextSelctedView.getWidth() * (nextSelectedPosition - getLastVisiblePosition());

                reset = false;
            } else {
                nextSelectedCenter = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
                reset = true;
            }

            int finalNextSelectedCenter = nextSelectedCenter - distanceLeft;

            if (finalNextSelectedCenter > center) {
                amountToScroll = finalNextSelectedCenter - center;
                int maxDiff = getItemDistance(getLastVisiblePosition(), mItemCount - 1, View.FOCUS_DOWN);
                maxDiff -= distanceLeft;
                View lastVisibleView = getChildAt(numChildren - 1);
                if (lastVisibleView.getRight() > listRight) {
                    maxDiff += (lastVisibleView.getRight() - listRight);
                }

                if (amountToScroll > maxDiff) {
                    amountToScroll = maxDiff;
                }

                if (reset) {
                    reset();
                    mFocusRectparams.focusRect().offset(-distanceLeft, 0);
                    if (DEBUG) {
                        Log.i(TAG, "amountToCenterScroll: focus rect = " + mFocusRectparams.focusRect() + ", distanceLeft = " + distanceLeft + ", nextSelectedPosition = " + nextSelectedPosition);
                    }
                }

                if (amountToScroll > 0) {
                    if (reset) {
                        mFocusRectparams.focusRect().offset(-amountToScroll, 0);
                    } else {
                        mFocusRectparams.focusRect().offset((nextSelctedView.getWidth() - amountToScroll), 0);
                    }

                    if (DEBUG) {
                        Log.d(TAG, "amountToCenterScroll: focus down amountToScroll = " + amountToScroll + ", focus rect = " + mFocusRectparams.focusRect());
                    }
                    smoothScrollBy(amountToScroll);
                    mIsAnimate = true;
                } else {
                    if (!reset) {
                        mFocusRectparams.focusRect().offset(nextSelctedView.getWidth(), 0);
                    }
                    mIsAnimate = true;
                }
            } else {
                reset();
                mFocusRectparams.focusRect().offset(-distanceLeft, 0);
                mIsAnimate = true;
            }

            return amountToScroll;
        } else if (direction == View.FOCUS_UP) {
            View nextSelctedView = getChildAt(nextSelectedPosition - mFirstPosition);
            int nextSelectedCenter = 0;
            boolean reset = false;
            Log.i("test", "FOCUS_UP nextSelctedView="+nextSelctedView+" distanceLeft="+distanceLeft);
            if (nextSelctedView == null) {
                nextSelctedView = getChildAt(0);
                nextSelectedCenter = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
                if (nextSelectedPosition >= getHeaderViewsCount()) {
                    nextSelectedCenter -= nextSelctedView.getWidth() * (getFirstVisiblePosition() - nextSelectedPosition);
                } else {
                    nextSelectedCenter -= nextSelctedView.getWidth() * (getFirstVisiblePosition() - getHeaderViewsCount());
                    for (int i = getHeaderViewsCount() - 1; i >= nextSelectedPosition; i--) {
                        nextSelectedCenter -= getHeaderView(i).getWidth();
                    }
                }

                reset = false;
            } else {
                nextSelectedCenter = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
                Log.i("test", "nextSelectedCenter="+nextSelectedCenter+" left="+nextSelctedView.getLeft()+" right="+nextSelctedView.getRight());
                reset = true;
            }

            int finalNextSelectedCenter = nextSelectedCenter - distanceLeft;

            if (finalNextSelectedCenter < center) {
                amountToScroll = center - finalNextSelectedCenter;
                int maxDiff = 0;

                if (getFirstVisiblePosition() >= getHeaderViewsCount()) {
                    maxDiff = getItemDistance(getHeaderViewsCount(), getFirstVisiblePosition(), View.FOCUS_UP);
                }

                int start = getHeaderViewsCount() - 1;
                if (start > getFirstVisiblePosition() - 1) {
                    start = getFirstVisiblePosition() - 1;
                }
                for (int i = start; i >= 0; i--) {
                    maxDiff += getHeaderView(i).getWidth();
                }
                if (maxDiff < 0) {
                    maxDiff = 0;
                }

                maxDiff += distanceLeft;
                View firstVisibleView = getChildAt(0);
                if (firstVisibleView.getLeft() < listLeft) {
                    maxDiff += (listLeft - firstVisibleView.getLeft());
                }

                if (amountToScroll > maxDiff) {
                    amountToScroll = maxDiff;
                }

                if (reset) {
                    reset();
                    mFocusRectparams.focusRect().offset(-distanceLeft, 0);
                    if (DEBUG) {
                        Log.i("test", "amountToCenterScroll: focus rect = " + mFocusRectparams.focusRect() + ", distanceLeft = " + distanceLeft + ", nextSelectedPosition = " + nextSelectedPosition);
                    }
                } else if (nextSelectedPosition < getHeaderViewsCount()) {
                    reset = true;
                    resetHeader(nextSelectedPosition);
                    mFocusRectparams.focusRect().offset(-distanceLeft, 0);
                }

                if (amountToScroll > 0) {
                    if (reset) {
                        mFocusRectparams.focusRect().offset(amountToScroll, 0);
                    } else {
                        mFocusRectparams.focusRect().offset(-(nextSelctedView.getWidth() - amountToScroll), 0);
                    }

                    if (DEBUG) {
                        Log.d("test", "amountToCenterScroll: focus down amountToScroll = " + amountToScroll + ", focus rect = " + mFocusRectparams.focusRect());
                    }
                    smoothScrollBy(-amountToScroll);
                    mIsAnimate = true;
                } else {
                    if (!reset) {
                        mFocusRectparams.focusRect().offset(-nextSelctedView.getWidth(), 0);
                    }
                    mIsAnimate = true;
                }
            } else {
                reset();
                mFocusRectparams.focusRect().offset(-distanceLeft, 0);
                mIsAnimate = true;
            }

            return amountToScroll;
        }

        return 0;
    }

    @Override
    public ItemListener getItem() {
        return (ItemListener) getSelectedView();
    }

    @Override
    public boolean isScrolling() {
        return false;
    }

    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        if (checkState(keyCode)) {
            return true;
        }

        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_LEFT:
        case KeyEvent.KEYCODE_MOVE_HOME: {
            // int nextSelectedPosition = getSelectedItemPosition() - 1 >= 0 ?
            // getSelectedItemPosition() - 1 : INVALID_POSITION;
            // if (nextSelectedPosition != INVALID_POSITION) {
            // final View nextSelctedView = getChildAt(nextSelectedPosition -
            // mFirstPosition);
            // if(nextSelctedView == null){
            // return false;
            // }
            // }
            return getSelectedItemPosition() > 0 ? true : false;
        }
        case KeyEvent.KEYCODE_DPAD_RIGHT:
        case KeyEvent.KEYCODE_MOVE_END: {
            // int nextSelectedPosition = getSelectedItemPosition() + 1 <
            // mItemCount ? getSelectedItemPosition() + 1 : INVALID_POSITION;
            // if (nextSelectedPosition != INVALID_POSITION) {
            // final View nextSelctedView = getChildAt(nextSelectedPosition -
            // mFirstPosition);
            // if(nextSelctedView == null){
            // return false;
            // }
            // }
            return getSelectedItemPosition() < mItemCount - 1 ? true : false;
        }
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_ENTER:
            return true;
        case KeyEvent.KEYCODE_DPAD_UP:
        case KeyEvent.KEYCODE_DPAD_DOWN:
            return true;

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
        // ignore warning
        return getWidth();
    }

    @Override
    public int getItemHeight() {
        // ignore warning
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 如果选中的itemView还未加入到里面，就忽略此次按键
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            int nextSelectedPosition = getSelectedItemPosition() - 1 >= 0 ? getSelectedItemPosition() - 1 : INVALID_POSITION;
            View nextSelectedView = getChildAt(nextSelectedPosition - getFirstVisiblePosition());
            Log.i(TAG, "onKeyDown KEYCODE_DPAD_LEFT nextSelectedPosition="+nextSelectedPosition+" nextSelectedView="+nextSelectedView);
            if (nextSelectedView == null) {
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            int nextSelectedPosition = getSelectedItemPosition() + 1 < mItemCount ? getSelectedItemPosition() + 1 : INVALID_POSITION;
            View nextSelectedView = getChildAt(nextSelectedPosition - getFirstVisiblePosition());
            Log.i(TAG, "onKeyDown KEYCODE_DPAD_RIGHT nextSelectedPosition="+nextSelectedPosition+" nextSelectedView="+nextSelectedView);
            if (nextSelectedView == null) {
                return true;
            }
        }
        if (getChildCount() <= 0) {
            return super.onKeyDown(keyCode, event);
        }

        if (checkState(keyCode)) {
            return true;
        }

        if (mDistance < 0) {
            mDistance = getChildAt(0).getWidth();
        }

        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_LEFT:
            if (moveLeft()) {
                playSoundEffect(SoundEffectConstants.NAVIGATION_LEFT);
                return true;
            }
            break;
        case KeyEvent.KEYCODE_DPAD_RIGHT:
            if (moveRight()) {
                playSoundEffect(SoundEffectConstants.NAVIGATION_RIGHT);
                return true;
            }
            break;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 取的开始位置跟结束位置之间的距离
     * @param startPos
     * @param endPos
     * @param direction
     * @return
     */
    private int getItemDistance(int startPos, int endPos, int direction) {
        GroupBaseAdapter groupAdapter = (GroupBaseAdapter)getAdapter();
        // 有效性
        if (startPos < 0 || endPos < 0 || endPos < startPos) {
            throw new IllegalArgumentException();
        }
        // 相同为0
        if (startPos == endPos) {
            return 0;
        }
        int startGroupPos = groupAdapter.getGroupPos(startPos);
        int startItemPos = groupAdapter.getGroupItemPos(startPos);
        int endGroupPos = groupAdapter.getGroupPos(endPos);
        int endItemPos = groupAdapter.getGroupItemPos(endPos);
        int hintWidth = getGroupHintWidth(groupAdapter);
        int itemWidth = getGroupItemWidth(groupAdapter);
        int totalWidth = 0;
        if (direction == View.FOCUS_DOWN) {
            if (startGroupPos != endGroupPos) {
                // 在不同的组内
                if (startItemPos == Integer.MAX_VALUE) {
                    // 组标识+后续的item总量
                    totalWidth += groupAdapter.getItemCount(startGroupPos) * itemWidth;
                } else {
                    // 后续的总量，本身不记入
                    totalWidth += (groupAdapter.getItemCount(startGroupPos) - 1 - startItemPos) * itemWidth;
                }
                if (endItemPos == Integer.MAX_VALUE) {
                    // 只用添加组标识
                    totalWidth += hintWidth;
                } else {
                    // 前面的item总量 + 组标识
                    totalWidth += (endItemPos + 1) * itemWidth + hintWidth;
                }
                for (int i = startGroupPos + 1; i < endGroupPos; i++) {
                    // 所有中间组的距离 所有item + 组标识
                    totalWidth += groupAdapter.getItemCount(i) * itemWidth;
                    totalWidth += hintWidth;
                }
            } else {
                // 在相同的组内
                if (startItemPos == Integer.MAX_VALUE) {
                    totalWidth += (endItemPos + 1) * itemWidth;
                } else {
                    totalWidth += (endItemPos - startItemPos) * itemWidth;
                }
            }
        } else if (direction == View.FOCUS_UP) {
            if (startGroupPos != endGroupPos) {
                // 在不同的组内
                if (startItemPos == Integer.MAX_VALUE) {
                    // 组标识+后续的item总量
                    totalWidth += hintWidth + groupAdapter.getItemCount(startGroupPos) * itemWidth;
                } else {
                    // 后续的总量，本身不记入
                    totalWidth += (groupAdapter.getItemCount(startGroupPos) - startItemPos) * itemWidth;
                }
                if (endItemPos == Integer.MAX_VALUE) {
                    // 只用添加组标识
                    //totalWidth += hintWidth;
                } else {
                    // 前面的item总量 + 组标识
                    totalWidth += endItemPos * itemWidth + hintWidth;
                }
                for (int i = startGroupPos + 1; i < endGroupPos; i++) {
                    // 所有中间组的距离 所有item + 组标识
                    totalWidth += groupAdapter.getItemCount(i) * itemWidth;
                    totalWidth += hintWidth;
                }
            } else {
                // 在相同的组内
                if (startItemPos == Integer.MAX_VALUE) {
                    totalWidth += hintWidth;
                    totalWidth += endItemPos * itemWidth;
                } else {
                    totalWidth += (endItemPos - startItemPos) * itemWidth;
                }
            }
        }
        Log.i(TAG, "getItemDistance startPos="+startPos+" endPos="+endPos+" startGroupPos="+startGroupPos+" startItemPos="+startItemPos+
                " endGroupPos="+endGroupPos+" endItemPos="+endItemPos+" hintWidth="+hintWidth+" itemWidth="+itemWidth+" totalWidth="+totalWidth);
        return totalWidth;
    }    
    
    /**
     * 取得组标识的宽度
     * @param groupAdapter
     * @return
     */
    private int getGroupHintWidth(GroupBaseAdapter groupAdapter){
        Rect hintRect = groupAdapter.getGroupHintRect();
        if (hintRect != null) {
            return hintRect.width();
        }
        return 0;
    }
    
    /**
     * 取得组内item的宽度
     * @param groupAdapter
     * @return
     */
    private int getGroupItemWidth(GroupBaseAdapter groupAdapter){
        Rect itemRect = groupAdapter.getGroupItemRect();
        if (itemRect != null) {
            return itemRect.width();
        }
        return 0;
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
