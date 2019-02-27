/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yunos.tv.app.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.util.LongSparseArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.StateSet;
import android.view.ActionMode;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.AbsListView.SelectionBoundsAdjuster;
import android.widget.Adapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.app.widget.utils.ReflectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract base class for spinner widgets. SDK users will probably not need
 * to use this class.
 *
 * @attr ref android.R.styleable#AbsSpinner_entries
 */
public abstract class AbsBaseListView extends AdapterView<ListAdapter> {
    /**
     * A class that represents a fixed view in a list, for example a header at
     * the top or a footer at the bottom.
     */
    public class FixedViewInfo {
        /**
         * The view to add to the list
         */
        public View view;
        /**
         * The data backing the view. This is returned from
         * {@link ListAdapter#getItem(int)}.
         */
        public Object data;
        /**
         * <code>true</code> if the fixed view should be selectable in the list
         */
        public boolean isSelectable;
    }

    private final static String TAG = "AbsBaseListView";
    private final static boolean DEBUG = false;

    /**
     * Normal list that does not indicate choices
     */
    public static final int CHOICE_MODE_NONE = 0;

    /**
     * The list allows up to one choice
     */
    public static final int CHOICE_MODE_SINGLE = 1;

    /**
     * The list allows multiple choices
     */
    public static final int CHOICE_MODE_MULTIPLE = 2;

    /**
     * The list allows multiple choices in a modal selection mode
     */
    public static final int CHOICE_MODE_MULTIPLE_MODAL = 3;

    /**
     * Controls if/how the user may choose/check items in the list
     */
    int mChoiceMode = CHOICE_MODE_NONE;

    /**
     * Indicates that we are not in the middle of a touch gesture
     */
    static final int TOUCH_MODE_REST = -1;

    /**
     * Indicates we just received the touch event and we are waiting to see if
     * the it is a tap or a scroll gesture.
     */
    protected static final int TOUCH_MODE_DOWN = 0;

    /**
     * Indicates the touch has been recognized as a tap and we are now waiting
     * to see if the touch is a longpress
     */
    static final int TOUCH_MODE_TAP = 1;

    /**
     * Indicates we have waited for everything we can wait for, but the user's
     * finger is still down
     */
    static final int TOUCH_MODE_DONE_WAITING = 2;

    /**
     * Indicates the touch gesture is a scroll
     */
    protected static final int TOUCH_MODE_SCROLL = 3;

    /**
     * Indicates the view is in the process of being flung
     */
    static final int TOUCH_MODE_FLING = 4;

    /**
     * Indicates the touch gesture is an overscroll - a scroll beyond the
     * beginning or end.
     */
    static final int TOUCH_MODE_OVERSCROLL = 5;

    /**
     * Indicates the view is being flung outside of normal content bounds and
     * will spring back.
     */
    static final int TOUCH_MODE_OVERFLING = 6;

    /**
     * Used to indicate a no preference for a position type.
     */
    static final int NO_POSITION = -1;

    /**
     * Sentinel value for no current active pointer. Used by
     * {@link #mActivePointerId}.
     */
    static final int INVALID_POINTER = -1;

    /**
     * Regular layout - usually an unsolicited layout from the view system
     */
    protected static final int LAYOUT_NORMAL = 0;

    /**
     * Show the first item
     */
    protected static final int LAYOUT_FORCE_TOP = 1;

    /**
     * Force the selected item to be on somewhere on the screen
     */
    protected static final int LAYOUT_SET_SELECTION = 2;

    /**
     * Show the last item
     */
    protected static final int LAYOUT_FORCE_BOTTOM = 3;

    /**
     * Make a mSelectedItem appear in a specific location and build the rest of
     * the views from there. The top is specified by mSpecificTop.
     */
    protected static final int LAYOUT_SPECIFIC = 4;

    /**
     * Layout to sync as a result of a data change. Restore mSyncPosition to
     * have its top at mSpecificTop
     */
    protected static final int LAYOUT_SYNC = 5;

    /**
     * Layout as a result of using the navigation keys
     */
    protected static final int LAYOUT_MOVE_SELECTION = 6;

    /**
     * Show the first item
     */
    protected static final int LAYOUT_FORCE_LEFT = 7;

    /**
     * Show the first item
     */
    protected static final int LAYOUT_FORCE_RIGHT = 8;

    protected static final int LAYOUT_FROM_MIDDLE = 9;

    /**
     * When set, this ViewGroup should not intercept touch events. {@hide
     * <p>
     * <p>
     * <p>
     * <p>
     * }
     */
    static final int FLAG_DISALLOW_INTERCEPT = 0x80000;

    static final boolean PROFILE_FLINGING = false;

    /**
     * When set, the drawing method will call
     * {@link #getChildDrawingOrder(int, int)} to get the index of the child to
     * draw for that iteration.
     */
    protected static final int FLAG_USE_CHILD_DRAWING_ORDER = 0x400;

    /**
     * When set, this ViewGroup supports static transformations on children;
     * this causes
     * {@link #getChildStaticTransformation(View, android.view.animation.Transformation)}
     * to be invoked when a child is drawn.
     * <p>
     * Any subclass overriding
     * {@link #getChildStaticTransformation(View, android.view.animation.Transformation)}
     * should set this flags in {@link #mGroupFlags}.
     */
    protected static final int FLAG_SUPPORT_STATIC_TRANSFORMATIONS = 0x800;

    /**
     * When arrow scrolling, ListView will never scroll more than this factor
     * times the height of the list.
     */
    static final float MAX_SCROLL_FACTOR = 0.33f;

    ListAdapter mAdapter;

    int mHeightMeasureSpec;
    int mWidthMeasureSpec;
    int mItemWidth;
    int mItemHeight;
    protected boolean mBlockLayoutRequests;

    int mSelectionLeftPadding = 0;
    int mSelectionTopPadding = 0;
    int mSelectionRightPadding = 0;
    int mSelectionBottomPadding = 0;
    protected final Rect mListPadding = new Rect();

    protected Drawable mSelector;

    protected final RecycleBin mRecycler = new RecycleBin();
    private DataSetObserver mDataSetObserver;

    /**
     * Temporary frame to hold a child View's frame rectangle
     */
    private Rect mTouchFrame;

    /**
     * If mAdapter != null, whenever this is true the adapter has stable IDs.
     */
    boolean mAdapterHasStableIds;

    protected final boolean[] mIsScrap = new boolean[1];

    int mDividerWidth;

    /**
     * The offset in pixels form the left of the AdapterView to the left of the
     * currently selected view. Used to save and restore state.
     */
    int mSelectedLeft = 0;

    int mSelectorPosition;

    /**
     * adapter is used to show scalable content it is not add to view, but
     * layout by view
     */
    Adapter mScalableAdapter;

    /**
     * the view is showed scalable content draw in dispatchDraw.
     */
    View mScalableView;

    /**
     * spacing of mScalableView & selected view used in vertical spacing.
     */
    int mScalableViewSpacing;

    final RecycleBin mScalableRecycler = new RecycleBin();

    /**
     * Horizontal and Vertical spacing between items.
     */
    protected int mSpacing = 0;

    private Runnable mClearScrollingCache;

    /**
     * When set to true, the list automatically discards the children's bitmap
     * cache after scrolling.
     */
    private boolean mScrollingCacheEnabled;

    /**
     * When the view is scrolling, this flag is set to true to indicate
     * subclasses that the drawing cache was enabled on the children
     */
    boolean mCachingStarted;
    boolean mCachingActive;

    /**
     * Defines the selector's location and dimension at drawing time
     */
    protected Rect mSelectorRect = new Rect();

    /**
     * Track if we are currently attached to a window.
     */
    boolean mIsAttached;

    /**
     * The position to resurrect the selected position to.
     */
    int mResurrectToPosition = 0;

    /**
     * Controls how the next layout will happen
     */
    protected int mLayoutMode = LAYOUT_NORMAL;

    /**
     * Determines speed during touch scrolling
     */
    VelocityTracker mVelocityTracker;

    ContextMenuInfo mContextMenuInfo;

    /**
     * Acts upon click
     */
    AbsBaseListView.PerformClick mPerformClick;

    /**
     * The last CheckForLongPress runnable we posted, if any
     */
    CheckForLongPress mPendingCheckForLongPress;

    /**
     * Controls CHOICE_MODE_MULTIPLE_MODAL. null when inactive.
     */
    ActionMode mChoiceActionMode;

    /**
     * Wrapper for the multiple choice mode callback; AbsListView needs to
     * perform a few extra actions around what application code does.
     */
    MultiChoiceModeWrapper mMultiChoiceModeCallback;

    /**
     * Running state of which positions are currently checked
     */
    SparseBooleanArray mCheckStates;

    /**
     * Running state of which IDs are currently checked. If there is a value for
     * a given key, the checked state for that ID is true and the value holds
     * the last known position in the adapter for that id.
     */
    LongSparseArray<Integer> mCheckedIdStates;

    /**
     * The view of the item that received the user's down touch.
     */
    private View mDownTouchView;

    /**
     * Running count of how many items are currently checked
     */
    int mCheckedItemCount;

    /**
     * The position of the item that received the user's down touch.
     */
    private int mDownTouchPosition;

    /**
     * The position of the view that received the down motion event
     */
    protected int mMotionPosition;

    protected int mTouchMode = TOUCH_MODE_REST;

    boolean mAreAllItemsSelectable = true;

    /**
     * set a exactly selected size
     */
    private Rect mExactlyUserSelectedRect;

    /**
     * Indicates whether the list selector should be drawn on top of the
     * children or behind
     */
    private boolean mDrawSelectorOnTop = true;

    private OnScrollListener mOnScrollListener;
    protected int mLastScrollState = OnScrollListener.SCROLL_STATE_IDLE;

    /**
     * The select child's view (from the adapter's getView) is enabled.
     */
    private boolean mIsChildViewEnabled;

    final Rect mTempRect = new Rect();

    protected ArrayList<FixedViewInfo> mHeaderViewInfos = new ArrayList<FixedViewInfo>();
    protected ArrayList<FixedViewInfo> mFooterViewInfos = new ArrayList<FixedViewInfo>();

    protected boolean mNeedLayout = false;

    int mPreLoadCount = 0;

    public void setPreLoadCount(int count) {
        mPreLoadCount = count;
    }

    public int getPreLoadCount() {
        return mPreLoadCount;
    }

    public int getFirstPosition() {
        return getFirstVisiblePosition();
    }


    public int getLastPosition() {
        return getLastVisiblePosition();
    }

    public int getFirsVisibletChildIndex() {
        return 0;
    }

    public int getLastVisibleChildIndex() {
        return getChildCount() - 1;
    }

    public int getVisibleChildCount() {
        return getChildCount();
    }

    public View getFirstChild() {
        return getChildAt(0);
    }

    public View getLastChild() {
        return getChildAt(getChildCount() - 1);
    }

    public View getFirstVisibleChild() {
        return getChildAt(0);
    }

    public View getLastVisibleChild() {
        return getChildAt(getChildCount() - 1);
    }

    /**
     * @param child a direct child of this list.
     * @return Whether child is a header or footer view.
     */
    protected boolean isDirectChildHeaderOrFooter(View child) {

        final ArrayList<FixedViewInfo> headers = mHeaderViewInfos;
        final int numHeaders = headers.size();
        for (int i = 0; i < numHeaders; i++) {
            if (child == headers.get(i).view) {
                return true;
            }
        }
        final ArrayList<FixedViewInfo> footers = mFooterViewInfos;
        final int numFooters = footers.size();
        for (int i = 0; i < numFooters; i++) {
            if (child == footers.get(i).view) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add a fixed view to appear at the top of the list. If addHeaderView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * NOTE: Call this before calling setAdapter. This is so ListView can wrap
     * the supplied cursor with one that will also account for header and footer
     * views.
     *
     * @param v            The view to add.
     * @param data         Data to associate with this view
     * @param isSelectable whether the item is selectable
     */
    public void addHeaderView(View v, Object data, boolean isSelectable) {

        if (mAdapter != null && !(mAdapter instanceof HeaderViewListAdapter)) {
            throw new IllegalStateException("Cannot add header view to list -- setAdapter has already been called.");
        }

        FixedViewInfo info = new FixedViewInfo();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        mHeaderViewInfos.add(info);
        mAreAllItemsSelectable &= isSelectable;
        // in the case of re-adding a header view, or adding one later on,
        // we need to notify the observer
        if (mAdapter != null && mDataSetObserver != null) {
            mDataSetObserver.onChanged();
        }
    }

    public View getHeaderView(int index) {
        if (index > getHeaderViewsCount() - 1 || index < 0) {
            throw new IllegalArgumentException("Cannot get header");
        }

        return mHeaderViewInfos.get(index).view;
    }

    /**
     * Add a fixed view to appear at the top of the list. If addHeaderView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * NOTE: Call this before calling setAdapter. This is so ListView can wrap
     * the supplied cursor with one that will also account for header and footer
     * views.
     *
     * @param v The view to add.
     */
    public void addHeaderView(View v) {
        addHeaderView(v, null, true);
    }

    public int getHeaderViewsCount() {
        return mHeaderViewInfos.size();
    }

    /**
     * Removes a previously-added header view.
     *
     * @param v The view to remove
     * @return true if the view was removed, false if the view was not a header
     * view
     */
    public boolean removeHeaderView(View v) {
        if (mHeaderViewInfos.size() > 0) {
            boolean result = false;
            if (mAdapter != null && ((HeaderViewListAdapter) mAdapter).removeHeader(v)) {
                if (mDataSetObserver != null) {
                    mDataSetObserver.onChanged();
                }
                result = true;
            }
            removeFixedViewInfo(v, mHeaderViewInfos);
            return result;
        }
        return false;
    }

    private void removeFixedViewInfo(View v, ArrayList<FixedViewInfo> where) {
        int len = where.size();
        for (int i = 0; i < len; ++i) {
            FixedViewInfo info = where.get(i);
            if (info.view == v) {
                where.remove(i);
                break;
            }
        }
    }

    /**
     * Add a fixed view to appear at the bottom of the list. If addFooterView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * NOTE: Call this before calling setAdapter. This is so ListView can wrap
     * the supplied cursor with one that will also account for header and footer
     * views.
     *
     * @param v            The view to add.
     * @param data         Data to associate with this view
     * @param isSelectable true if the footer view can be selected
     */
    public void addFooterView(View v, Object data, boolean isSelectable) {

        // NOTE: do not enforce the adapter being null here, since unlike in
        // addHeaderView, it was never enforced here, and so existing apps are
        // relying on being able to add a footer and then calling setAdapter to
        // force creation of the HeaderViewListAdapter wrapper

        FixedViewInfo info = new FixedViewInfo();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        mAreAllItemsSelectable &= isSelectable;
        mFooterViewInfos.add(info);

        // in the case of re-adding a footer view, or adding one later on,
        // we need to notify the observer
        if (mAdapter != null && mDataSetObserver != null) {
            mDataSetObserver.onChanged();
        }
    }

    /**
     * Add a fixed view to appear at the bottom of the list. If addFooterView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * NOTE: Call this before calling setAdapter. This is so ListView can wrap
     * the supplied cursor with one that will also account for header and footer
     * views.
     *
     * @param v The view to add.
     */
    public void addFooterView(View v) {
        addFooterView(v, null, true);
    }

    public int getFooterViewsCount() {
        return mFooterViewInfos.size();
    }

    /**
     * Removes a previously-added footer view.
     *
     * @param v The view to remove
     * @return true if the view was removed, false if the view was not a footer
     * view
     */
    public boolean removeFooterView(View v) {
        if (mFooterViewInfos.size() > 0) {
            boolean result = false;
            if (mAdapter != null && ((HeaderViewListAdapter) mAdapter).removeFooter(v)) {
                if (mDataSetObserver != null) {
                    mDataSetObserver.onChanged();
                }
                result = true;
            }
            removeFixedViewInfo(v, mFooterViewInfos);
            return result;
        }
        return false;
    }

    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    /**
     * Indicates whether this view is in a state where the selector should be
     * drawn. This will happen if we have focus but are not in touch mode, or we
     * are in the middle of displaying the pressed state for an item.
     *
     * @return True if the selector should be shown
     */
    boolean shouldShowSelector() {
        return (hasFocus() && !isInTouchMode()) || touchModeDrawsInPressedState();
    }

    void reportScrollStateChange(int newState) {
        if (newState != mLastScrollState) {
            mLastScrollState = newState;
            if (mOnScrollListener != null) {
                mLastScrollState = newState;
                mOnScrollListener.onScrollStateChanged(this, newState);
            }
        }
    }

    /**
     * @return True if the current touch mode requires that we draw the selector
     * in the pressed state.
     */
    boolean touchModeDrawsInPressedState() {
        // TODO
        // FIXME use isPressed for this
        // switch (mTouchMode) {
        // case TOUCH_MODE_TAP:
        // case TOUCH_MODE_DONE_WAITING:
        // return true;
        // default:
        // return false;
        // }
        return false;
    }

    protected boolean needMeasureSelectedView = true;

    public AbsBaseListView(Context context) {
        super(context);
        initAbsSpinner();
    }

    public AbsBaseListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsBaseListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAbsSpinner();
        //
        // TypedArray a = context.obtainStyledAttributes(attrs,
        // com.android.internal.R.styleable.AbsSpinner, defStyle, 0);
        //
        // CharSequence[] entries =
        // a.getTextArray(com.android.internal.R.styleable.AbsSpinner_entries);
        // if (entries != null) {
        // ArrayAdapter<CharSequence> adapter =
        // new ArrayAdapter<CharSequence>(context,
        // com.android.internal.R.layout.simple_spinner_item, entries);
        // adapter.setDropDownViewResource(com.android.internal.R.layout.simple_spinner_dropdown_item);
        // setAdapter(adapter);
        // }
        //
        // a.recycle();
    }

    protected void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }

    protected void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    protected void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * Common code for different constructor flavors
     */
    private void initAbsSpinner() {
        setFocusable(true);
        setWillNotDraw(false);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (null != mAdapter) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
            resetList();
        }

        if (mHeaderViewInfos.size() > 0 || mFooterViewInfos.size() > 0) {
            mAdapter = new HeaderViewListAdapter(mHeaderViewInfos, mFooterViewInfos, adapter);
        } else {
            mAdapter = adapter;
        }
        // mAdapter = adapter;

        mOldSelectedPosition = INVALID_POSITION;
        mOldSelectedRowId = INVALID_ROW_ID;
        mRecycler.clear();

        if (mAdapter != null) {
            mAreAllItemsSelectable = mAdapter.areAllItemsEnabled();
            mOldItemCount = mItemCount;
            mAdapterHasStableIds = mAdapter.hasStableIds();
            mItemCount = mAdapter.getCount();
            checkFocus();

            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);

            mRecycler.setViewTypeCount(mAdapter.getViewTypeCount());

            int position = initPosition();

            setSelectedPositionInt(position);
            setNextSelectedPositionInt(position);

            if (mItemCount == 0) {
                // Nothing selected
                checkSelectionChanged();
            }

        } else {
            mAreAllItemsSelectable = true;
            checkFocus();
            resetList();
            // Nothing selected
            checkSelectionChanged();
        }

        requestLayout();
    }

    protected void positionSelector(int position, View sel) {
        if (position != INVALID_POSITION) {
            mSelectorPosition = position;
        }

        final Rect selectorRect = mSelectorRect;
        selectorRect.set(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom());
        if (sel instanceof SelectionBoundsAdjuster) {
            ((SelectionBoundsAdjuster) sel).adjustListItemSelectionBounds(selectorRect);
        }
        positionSelector(selectorRect.left, selectorRect.top, selectorRect.right, selectorRect.bottom);

        final boolean isChildViewEnabled = mIsChildViewEnabled;
        if (sel.isEnabled() != isChildViewEnabled) {
            mIsChildViewEnabled = !isChildViewEnabled;
            if (getSelectedItemPosition() != INVALID_POSITION) {
                refreshDrawableState();
            }
        }
    }

    void positionSelector(int l, int t, int r, int b) {
        mSelectorRect.set(l - mSelectionLeftPadding, t - mSelectionTopPadding, r + mSelectionRightPadding, b + mSelectionBottomPadding);
    }

    /**
     * Get a view and have it show the data associated with the specified
     * position. This is called when we have already discovered that the view is
     * not available for reuse in the recycle bin. The only choices left are
     * converting an old view or making a new one.
     *
     * @param position The position to display
     * @param isScrap  Array of at least 1 boolean, the first entry will become true
     *                 if the returned view was taken from the scrap heap, false if
     *                 otherwise.
     * @return A view displaying the data associated with the specified position
     */
    protected View obtainView(int position, boolean[] isScrap) {
        isScrap[0] = false;
        View scrapView;

        scrapView = mRecycler.getTransientStateView(position);
        if (scrapView != null) {
            return scrapView;
        }

        scrapView = mRecycler.getScrapView(position);
        if (DEBUG) {
            Log.d(TAG, "obtainView->getScrapView position = " + position + " scrapView=" + scrapView);
        }

        if (mAdapter == null) {
            Log.e(TAG, TAG + ".obtainView.mAdapter = " + mAdapter);
            return null;
        }
        View child;
        if (scrapView != null) {
            child = mAdapter.getView(position, scrapView, this);

            // if (child.getImportantForAccessibility() ==
            // IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
            // child.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
            // }

            if (child != scrapView) {
                mRecycler.addScrapView(scrapView, position);
                // if (mCacheColorHint != 0) {
                // child.setDrawingCacheBackgroundColor(mCacheColorHint);
                // }
            } else {
                isScrap[0] = true;
                // TODO by lawin
                // child.dispatchFinishTemporaryDetach();
            }
        } else {
            child = mAdapter.getView(position, null, this);

            // if (child.getImportantForAccessibility() ==
            // IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
            // child.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
            // }

            // if (mCacheColorHint != 0) {
            // child.setDrawingCacheBackgroundColor(mCacheColorHint);
            // }
        }

        if (mAdapterHasStableIds) {
            final ViewGroup.LayoutParams vlp = child.getLayoutParams();
            LayoutParams lp;
            if (vlp == null) {
                lp = (LayoutParams) generateDefaultLayoutParams();
            } else if (!checkLayoutParams(vlp)) {
                lp = (LayoutParams) generateLayoutParams(vlp);
            } else {
                lp = (LayoutParams) vlp;
            }
            lp.itemId = mAdapter.getItemId(position);
            child.setLayoutParams(lp);
        }

        // if (AccessibilityManager.getInstance(mContext).isEnabled()) {
        // if (mAccessibilityDelegate == null) {
        // mAccessibilityDelegate = new ListItemAccessibilityDelegate();
        // }
        // if (child.getAccessibilityDelegate() == null) {
        // child.setAccessibilityDelegate(mAccessibilityDelegate);
        // }
        // }

        return child;
    }

    protected void invalidateParentIfNeeded() {
        if (isHardwareAccelerated() && getParent() instanceof View) {
            View parent = (View) getParent();
            parent.invalidate();
        }
    }

    int initPosition() {
        return mItemCount > 0 ? 0 : INVALID_POSITION;
    }

    protected int getListLeft() {
        return mListPadding.left;
    }

    protected int getListTop() {
        return mListPadding.top;
    }

    protected int getListRight() {
        return getWidth() - mListPadding.right;
    }

    protected int getListBottom() {
        return getHeight() - mListPadding.bottom;
    }

    /**
     * List padding is the maximum of the normal view's padding and the padding
     * of the selector.
     *
     * @return The top list padding.
     * @see View#getPaddingTop()
     * @see #getSelector()
     */
    public int getListPaddingTop() {
        return mListPadding.top;
    }

    /**
     * List padding is the maximum of the normal view's padding and the padding
     * of the selector.
     *
     * @return The bottom list padding.
     * @see View#getPaddingBottom()
     * @see #getSelector()
     */
    public int getListPaddingBottom() {
        return mListPadding.bottom;
    }

    /**
     * List padding is the maximum of the normal view's padding and the padding
     * of the selector.
     *
     * @return The left list padding.
     * @see View#getPaddingLeft()
     * @see #getSelector()
     */
    public int getListPaddingLeft() {
        return mListPadding.left;
    }

    /**
     * List padding is the maximum of the normal view's padding and the padding
     * of the selector.
     *
     * @return The right list padding.
     * @see View#getPaddingRight()
     * @see #getSelector()
     */
    public int getListPaddingRight() {
        return mListPadding.right;
    }

    public void setSelector(Drawable selector) {
        mSelector = selector;

        Rect padding = new Rect();
        mSelector.getPadding(padding);
        setSelectorPadding(padding.left, padding.top, padding.right, padding.bottom);
    }

    public void setSelector(int selectorId) {
        mSelector = getContext().getResources().getDrawable(selectorId);

        Rect padding = new Rect();
        mSelector.getPadding(padding);
        setSelectorPadding(padding.left, padding.top, padding.right, padding.bottom);
    }

    protected void drawSelector(Canvas canvas) {
        if (hasFocus() && mSelector != null && mSelectorRect != null && !mSelectorRect.isEmpty()) {
            Rect selectorRect = new Rect(mExactlyUserSelectedRect != null ? mExactlyUserSelectedRect : mSelectorRect);
            mSelector.setBounds(selectorRect);
            mSelector.draw(canvas);
        }
    }

    /**
     * If there is a selection returns false. Otherwise resurrects the selection
     * and returns true if resurrected.
     */
    boolean resurrectSelectionIfNeeded() {
        if (mSelectedPosition < 0 && resurrectSelection()) {
            updateSelectorState();
            return true;
        }
        return false;
    }

    void updateSelectorState() {
        if (mSelector != null) {
            if (shouldShowSelector()) {
                mSelector.setState(getDrawableState());
            } else {
                mSelector.setState(StateSet.NOTHING);
            }
        }
    }

    /**
     * Controls whether the selection highlight drawable should be drawn on top
     * of the item or behind it.
     *
     * @param onTop If true, the selector will be drawn on the item it is
     *              highlighting. The default is false.
     * @attr ref android.R.styleable#AbsListView_drawSelectorOnTop
     */
    public void setDrawSelectorOnTop(boolean onTop) {
        mDrawSelectorOnTop = onTop;
    }

    public boolean drawSclectorOnTop() {
        return mDrawSelectorOnTop;
    }

    /**
     * set exactly selected rect width & height
     *
     * @param width  the selected rect width
     * @param height the selected rect height
     */
    public void setExactlyUserSelectedRect(int left, int top, int right, int bottom) {
        mExactlyUserSelectedRect = new Rect(left, top, right, bottom);
    }

    /**
     * clear exactly selected rect width & height
     */
    public void clearExactlyUserSelectedRect() {
        mExactlyUserSelectedRect = null;
    }

    /**
     * set selector rect padding, used for layout selector.
     *
     * @param leftPadding
     * @param topPadding
     * @param rightPadding
     * @param bottomPadding
     */
    public void setSelectorPadding(int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        mSelectionLeftPadding = leftPadding;
        mSelectionTopPadding = topPadding;
        mSelectionRightPadding = rightPadding;
        mSelectionBottomPadding = bottomPadding;
    }

    private void setupScalableView(View scalableView, View child) {
        AbsBaseListView.LayoutParams lp = (AbsBaseListView.LayoutParams) child.getLayoutParams();
        if (lp == null) {
            lp = (AbsBaseListView.LayoutParams) generateDefaultLayoutParams();
        }
        // Get measure specs
        int childHeightSpec = getChildMeasureSpec(mHeightMeasureSpec, mListPadding.top + mListPadding.bottom, lp.height);
        int childWidthSpec = getChildMeasureSpec(mWidthMeasureSpec, mListPadding.left + mListPadding.right, lp.width);

        // Measure child
        scalableView.measure(childWidthSpec, childHeightSpec);
        int height = scalableView.getMeasuredHeight();
        int l, r, t, b;
        l = child.getLeft();
        r = child.getRight();
        t = child.getBottom() + mScalableViewSpacing;
        b = t + height;
        // Log.d(TAG, "setUpScalableView l = " + l + ", r = " + r + ", t = " + t
        // + ", b = " + b);
        scalableView.layout(l, t, r, b);
    }

    /**
     * Performs button-related actions during a touch down event.
     *
     * @param event The event.
     * @return True if the down was consumed.
     * @hide
     */
    protected boolean performButtonActionOnTouchDown(MotionEvent event) {
        if ((event.getButtonState() & MotionEvent.BUTTON_SECONDARY) != 0) {
            if (showContextMenu()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates the ContextMenuInfo returned from {@link #getContextMenuInfo()}.
     * This methods knows the view, position and ID of the item that received
     * the long press.
     *
     * @param view     The view that received the long press.
     * @param position The position of the item that received the long press.
     * @param id       The ID of the item that received the long press.
     * @return The extra information that should be returned by
     * {@link #getContextMenuInfo()}.
     */
    ContextMenuInfo createContextMenuInfo(View view, int position, long id) {
        return new AdapterContextMenuInfo(view, position, id);
    }

    @Override
    protected ContextMenuInfo getContextMenuInfo() {
        return mContextMenuInfo;
    }

    @Override
    public boolean showContextMenu() {

        if (isPressed() && mSelectedPosition >= 0) {
            int index = mSelectedPosition - mFirstPosition;
            View v = getChildAt(index);
            return dispatchLongPress(v, mSelectedPosition, mSelectedRowId);
        }

        return false;
    }

    @Override
    public boolean showContextMenuForChild(View originalView) {

        final int longPressPosition = getPositionForView(originalView);
        if (longPressPosition < 0) {
            return false;
        }

        final long longPressId = mAdapter.getItemId(longPressPosition);
        return dispatchLongPress(originalView, longPressPosition, longPressId);
    }

    boolean performLongPress(final View child, final int longPressPosition, final long longPressId) {
        // CHOICE_MODE_MULTIPLE_MODAL takes over long press.
        if (mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL) {
            if (mChoiceActionMode == null && (mChoiceActionMode = startActionMode(mMultiChoiceModeCallback)) != null) {
                setItemChecked(longPressPosition, true);
                performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            }
            return true;
        }

        boolean handled = false;
        if (mOnItemLongClickListener != null) {
            handled = mOnItemLongClickListener.onItemLongClick(AbsBaseListView.this, child, longPressPosition, longPressId);
        }
        if (!handled) {
            mContextMenuInfo = createContextMenuInfo(child, longPressPosition, longPressId);
            handled = super.showContextMenuForChild(AbsBaseListView.this);
        }
        if (handled) {
            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }
        return handled;
    }

    private boolean dispatchLongPress(View view, int position, long id) {
        boolean handled = false;

        if (mOnItemLongClickListener != null) {
            handled = mOnItemLongClickListener.onItemLongClick(this, mDownTouchView, mDownTouchPosition, id);
        }

        if (!handled) {
            mContextMenuInfo = new AdapterContextMenuInfo(view, position, id);
            handled = super.showContextMenuForChild(this);
        }

        if (handled) {
            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }

        return handled;
    }

    /**
     * Returns the number of items currently selected. This will only be valid
     * if the choice mode is not {@link #CHOICE_MODE_NONE} (default).
     * <p>
     * <p>
     * To determine the specific items that are currently selected, use one of
     * the <code>getChecked*</code> methods.
     *
     * @return The number of items currently selected
     * @see #getCheckedItemPosition()
     * @see #getCheckedItemPositions()
     * @see #getCheckedItemIds()
     */
    public int getCheckedItemCount() {
        return mCheckedItemCount;
    }

    /**
     * Returns the checked state of the specified position. The result is only
     * valid if the choice mode has been set to {@link #CHOICE_MODE_SINGLE} or
     * {@link #CHOICE_MODE_MULTIPLE}.
     *
     * @param position The item whose checked state to return
     * @return The item's checked state or <code>false</code> if choice mode is
     * invalid
     * @see #setChoiceMode(int)
     */
    public boolean isItemChecked(int position) {
        if (mChoiceMode != CHOICE_MODE_NONE && mCheckStates != null) {
            return mCheckStates.get(position);
        }

        return false;
    }

    /**
     * Sets the checked state of the specified position. The is only valid if
     * the choice mode has been set to {@link #CHOICE_MODE_SINGLE} or
     * {@link #CHOICE_MODE_MULTIPLE}.
     *
     * @param position The item whose checked state is to be checked
     * @param value    The new checked state for the item
     */
    public void setItemChecked(int position, boolean value) {
        if (mChoiceMode == CHOICE_MODE_NONE) {
            return;
        }

        // Start selection mode if needed. We don't need to if we're unchecking
        // something.
        if (value && mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL && mChoiceActionMode == null) {
            if (mMultiChoiceModeCallback == null || !mMultiChoiceModeCallback.hasWrappedCallback()) {
                throw new IllegalStateException("AbsListView: attempted to start selection mode " + "for CHOICE_MODE_MULTIPLE_MODAL but no choice mode callback was "
                        + "supplied. Call setMultiChoiceModeListener to set a callback.");
            }
            mChoiceActionMode = startActionMode(mMultiChoiceModeCallback);
        }

        if (mChoiceMode == CHOICE_MODE_MULTIPLE || mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL) {
            boolean oldValue = mCheckStates.get(position);
            mCheckStates.put(position, value);
            if (mCheckedIdStates != null && mAdapter.hasStableIds()) {
                if (value) {
                    mCheckedIdStates.put(mAdapter.getItemId(position), position);
                } else {
                    mCheckedIdStates.delete(mAdapter.getItemId(position));
                }
            }
            if (oldValue != value) {
                if (value) {
                    mCheckedItemCount++;
                } else {
                    mCheckedItemCount--;
                }
            }
            if (mChoiceActionMode != null) {
                final long id = mAdapter.getItemId(position);
                mMultiChoiceModeCallback.onItemCheckedStateChanged(mChoiceActionMode, position, id, value);
            }
        } else {
            boolean updateIds = mCheckedIdStates != null && mAdapter.hasStableIds();
            // Clear all values if we're checking something, or unchecking the
            // currently
            // selected item
            if (value || isItemChecked(position)) {
                mCheckStates.clear();
                if (updateIds) {
                    mCheckedIdStates.clear();
                }
            }
            // this may end up selecting the value we just cleared but this way
            // we ensure length of mCheckStates is 1, a fact
            // getCheckedItemPosition relies on
            if (value) {
                mCheckStates.put(position, true);
                if (updateIds) {
                    mCheckedIdStates.put(mAdapter.getItemId(position), position);
                }
                mCheckedItemCount = 1;
            } else if (mCheckStates.size() == 0 || !mCheckStates.valueAt(0)) {
                mCheckedItemCount = 0;
            }
        }

        // Do not generate a data change while we are in the layout phase
        if (!mInLayout && !mBlockLayoutRequests) {
            mDataChanged = true;
            rememberSyncState();
            requestLayout();
        }
    }

    @Override
    public void getFocusedRect(Rect r) {
        View view = getSelectedView();
        if (view != null && view.getParent() == this) {
            // the focused rectangle of the selected view offset into the
            // coordinate space of this view.
            view.getFocusedRect(r);
            offsetDescendantRectToMyCoords(view, r);
        } else {
            // otherwise, just the norm
            super.getFocusedRect(r);
        }
    }

    /**
     * Clear any choices previously set
     */
    public void clearChoices() {
        if (mCheckStates != null) {
            mCheckStates.clear();
        }
        if (mCheckedIdStates != null) {
            mCheckedIdStates.clear();
        }
        mCheckedItemCount = 0;
    }

    /**
     * A base class for Runnables that will check that their view is still
     * attached to the original window as when the Runnable was created.
     */
    private class WindowRunnnable {
        private int mOriginalAttachCount;

        public void rememberWindowAttachCount() {
            mOriginalAttachCount = getWindowAttachCount();
        }

        public boolean sameWindow() {
            return hasWindowFocus() && getWindowAttachCount() == mOriginalAttachCount;
        }
    }

    class PerformClick extends WindowRunnnable implements Runnable {
        int mClickMotionPosition;

        public void run() {
            // The data has changed since we posted this action in the event
            // queue,
            // bail out before bad things happen
            if (mDataChanged)
                return;

            final ListAdapter adapter = mAdapter;
            final int motionPosition = mClickMotionPosition;
            if (adapter != null && mItemCount > 0 && motionPosition != INVALID_POSITION && motionPosition < adapter.getCount() && sameWindow()) {
                final View view = getChildAt(motionPosition - mFirstPosition);
                // If there is no view, something bad happened (the view
                // scrolled off the
                // screen, etc.) and we should cancel the click
                if (view != null) {
                    performItemClick(view, motionPosition, adapter.getItemId(motionPosition));
                }
            }
        }
    }

    class CheckForLongPress extends WindowRunnnable implements Runnable {
        public void run() {
            final int motionPosition = mMotionPosition;
            final View child = getChildAt(motionPosition - mFirstPosition);
            if (child != null) {
                final int longPressPosition = mMotionPosition;
                final long longPressId = mAdapter.getItemId(mMotionPosition);

                boolean handled = false;
                if (sameWindow() && !mDataChanged) {
                    handled = performLongPress(child, longPressPosition, longPressId);
                }
                if (handled) {
                    mTouchMode = TOUCH_MODE_REST;
                    setPressed(false);
                    child.setPressed(false);
                } else {
                    mTouchMode = TOUCH_MODE_DONE_WAITING;
                }
            }
        }
    }

    /**
     * set scalable view for this
     *
     * @param position
     * @param child
     */
    void setScalableView(int position, View child) {
        if (mScalableAdapter != null) {
            View scalView;
            scalView = mScalableAdapter.getView(position, null, this);
            Log.d(TAG, " getScalableView position = " + position + " child = " + getChildAt(position));
            if (scalView != null && child != null) {
                setupScalableView(scalView, child);
                mScalableView = scalView;
            } else {
                mScalableView = null;
            }
        }
    }

    int getItemWidth() {
        return mItemWidth;
    }

    int getItemHeight() {
        return mItemHeight;
    }

    void clearScalableView() {
        mScalableView = null;
    }

    /**
     * set scalable adapter, used for show scalable content
     *
     * @param adapter
     */
    public void setScalableAdapter(Adapter adapter) {
        mScalableAdapter = adapter;
        mScalableRecycler.clear();
    }

    /**
     * set vertical spacing of mScalableView & selected view
     *
     * @param spacing
     */
    public void setScalableViewSpacing(int spacing) {
        mScalableViewSpacing = spacing;
    }

    /**
     * Indicates whether the children's drawing cache is used during a scroll.
     * By default, the drawing cache is enabled but this will consume more
     * memory.
     *
     * @return true if the scrolling cache is enabled, false otherwise
     * @see #setScrollingCacheEnabled(boolean)
     * @see View#setDrawingCacheEnabled(boolean)
     */
    @ViewDebug.ExportedProperty
    public boolean isScrollingCacheEnabled() {
        return mScrollingCacheEnabled;
    }

    /**
     * Enables or disables the children's drawing cache during a scroll. By
     * default, the drawing cache is enabled but this will use more memory.
     * <p>
     * When the scrolling cache is enabled, the caches are kept after the first
     * scrolling. You can manually clear the cache by calling
     * {@link ViewGroup#setChildrenDrawingCacheEnabled(boolean)}.
     *
     * @param enabled true to enable the scroll cache, false otherwise
     * @see #isScrollingCacheEnabled()
     * @see View#setDrawingCacheEnabled(boolean)
     */
    public void setScrollingCacheEnabled(boolean enabled) {
        if (mScrollingCacheEnabled && !enabled) {
            clearScrollingCache();
        }
        mScrollingCacheEnabled = enabled;
    }

    protected void createScrollingCache() {
        if (mScrollingCacheEnabled && !mCachingStarted) {
            setChildrenDrawnWithCacheEnabled(true);
            setChildrenDrawingCacheEnabled(true);
            mCachingStarted = mCachingActive = true;
        }
    }

    protected void clearScrollingCache() {
        if (mClearScrollingCache == null) {
            mClearScrollingCache = new Runnable() {
                public void run() {
                    if (mCachingStarted) {
                        mCachingStarted = mCachingActive = false;
                        setChildrenDrawnWithCacheEnabled(false);
                        if ((getPersistentDrawingCache() & PERSISTENT_SCROLLING_CACHE) == 0) {
                            setChildrenDrawingCacheEnabled(false);
                        }
                        if (!isAlwaysDrawnWithCacheEnabled()) {
                            invalidate();
                        }
                    }
                }
            };
        }
        post(mClearScrollingCache);
    }

    /**
     * Clear out all children from the list
     */
    protected void resetList() {
        mDataChanged = false;
        mNeedSync = false;

        removeAllViewsInLayout();
        mOldSelectedPosition = INVALID_POSITION;
        mOldSelectedRowId = INVALID_ROW_ID;
        mOldItemCount = mItemCount;
        mItemCount = 0;

        setSelectedPositionInt(INVALID_POSITION);
        setNextSelectedPositionInt(INVALID_POSITION);
        invalidate();
    }

    void requestLayoutIfNecessary() {
        if (getChildCount() > 0) {
            resetList();
            requestLayout();
            invalidate();
        }
    }

    /**
     * Offset the vertical location of all children of this view by the
     * specified number of pixels.
     *
     * @param offset the number of pixels to offset
     */
    public void offsetChildrenTopAndBottom(int offset) {
        int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            getChildAt(i).offsetTopAndBottom(offset);
        }
    }

    /**
     * Offset the horizontal location of all children of this view by the
     * specified number of pixels.
     *
     * @param offset the number of pixels to offset
     */
    protected void offsetChildrenLeftAndRight(int offset) {
        int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            getChildAt(i).offsetLeftAndRight(offset);
        }
    }

    /**
     * @see View#measure(int, int)
     * <p>
     * Figure out the dimensions of this Spinner. The width comes from the
     * widthMeasureSpec as Spinnners can't have their width set to
     * UNSPECIFIED. The height is based on the height of the selected item
     * plus padding.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize;
        int heightSize;

        mListPadding.left = getPaddingLeft() + mSelectionLeftPadding/*
         * >
         * mSelectionLeftPadding
         * ?
         * getPaddingLeft
         * () :
         * mSelectionLeftPadding
         */;
        mListPadding.top = getPaddingTop() + mSelectionTopPadding/*
         * >
         * mSelectionTopPadding
         * ?
         * getPaddingTop
         * () :
         * mSelectionTopPadding
         */;
        mListPadding.right = getPaddingRight() + mSelectionRightPadding/*
         * >
         * mSelectionRightPadding
         * ?
         * getPaddingRight
         * () :
         * mSelectionRightPadding
         */;
        mListPadding.bottom = getPaddingBottom() + mSelectionBottomPadding/*
         * >
         * mSelectionBottomPadding
         * ?
         * getPaddingBottom
         * () :
         * mSelectionBottomPadding
         */;

        if (mDataChanged) {
            handleDataChanged();
        }

        int preferredHeight = 0;
        int preferredWidth = 0;
        boolean needsMeasuring = true;

        if (needMeasureSelectedView) {
            int selectedPosition = getSelectedItemPosition();
            if (mAdapter != null) {
                int count = mAdapter.getCount();
                if (getHeaderViewsCount() < count) {
                    selectedPosition = getHeaderViewsCount();
                }
            }

            if (selectedPosition >= 0 && mAdapter != null && selectedPosition < mAdapter.getCount()) {
                // Try looking in the recycler. (Maybe we were measured once
                // already)
                if (DEBUG) {
                    Log.d(TAG, "obtainView getScrapView selectedPosition position = " + selectedPosition);
                }
                View view = mRecycler.getScrapView(selectedPosition);
                if (view == null) {
                    // Make a new one
                    view = mAdapter.getView(selectedPosition, null, this);
                    if (view != null) {
                        // Put in recycler for re-measuring and/or layout
                        mRecycler.addScrapView(view, selectedPosition);
                    }
                }

                if (view != null) {
                    if (view.getLayoutParams() == null) {
                        mBlockLayoutRequests = true;
                        view.setLayoutParams(generateDefaultLayoutParams());
                        mBlockLayoutRequests = false;
                    }
                    measureChild(view, widthMeasureSpec, heightMeasureSpec);
                    mItemHeight = getChildHeight(view);
                    mItemWidth = getChildWidth(view);

                    preferredHeight = getChildHeight(view) + mListPadding.top + mListPadding.bottom;
                    preferredWidth = getChildWidth(view) + mListPadding.left + mListPadding.right;

                    needsMeasuring = false;
                }
            }
        }

        if (needsMeasuring) {
            // No views -- just use padding
            preferredHeight = mListPadding.top + mListPadding.bottom;
            if (widthMode == View.MeasureSpec.UNSPECIFIED) {
                preferredWidth = mListPadding.left + mListPadding.right;
            }
        }

        preferredHeight = Math.max(preferredHeight, getSuggestedMinimumHeight());
        preferredWidth = Math.max(preferredWidth, getSuggestedMinimumWidth());

        heightSize = resolveSizeAndState(preferredHeight, heightMeasureSpec, 0);
        widthSize = resolveSizeAndState(preferredWidth, widthMeasureSpec, 0);

        setMeasuredDimension(widthSize, heightSize);
        mHeightMeasureSpec = heightMeasureSpec;
        mWidthMeasureSpec = widthMeasureSpec;
    }

    int getChildHeight(View child) {
        return child.getMeasuredHeight();
    }

    protected int getChildWidth(View child) {
        return child.getMeasuredWidth();
    }

    protected void keyPressed() {

        final View child = getChildAt(mSelectedPosition - mFirstPosition);
        if (child != null) {
            child.setPressed(true);
        }

        setPressed(true);
    }

    protected void dispatchUnpress() {

        for (int i = getChildCount() - 1; i >= 0; i--) {
            getChildAt(i).setPressed(false);
        }

        setPressed(false);
    }

    // @Override
    // protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
    // return new ViewGroup.LayoutParams(
    // ViewGroup.LayoutParams.MATCH_PARENT,
    // ViewGroup.LayoutParams.WRAP_CONTENT);
    // }

    void recycleAllViews() {
        final int childCount = getChildCount();
        final AbsBaseListView.RecycleBin recycleBin = mRecycler;
        final int position = mFirstPosition;

        // All views go in recycler
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            int index = position + i;
            recycleBin.addScrapView(v, index);
        }
    }

    /**
     * Jump directly to a specific item in the adapter data.
     */
    public void setSelection(int position, boolean animate) {
        // Animate only if requested position is already on screen somewhere
        boolean shouldAnimate = animate && mFirstPosition <= position && position <= mFirstPosition + getChildCount() - 1;
        setSelectionInt(position, shouldAnimate);
    }

    @Override
    public void setSelection(int position) {
        setNextSelectedPositionInt(position);
        requestLayout();
        invalidate();
    }

    /**
     * Makes the item at the supplied position selected.
     *
     * @param position the position of the item to select
     */
    void setSelectionInt(int position) {
        setNextSelectedPositionInt(position);
        boolean awakeScrollbars = false;

        final int selectedPosition = mSelectedPosition;

        if (selectedPosition >= 0) {
            if (position == selectedPosition - 1) {
                awakeScrollbars = true;
            } else if (position == selectedPosition + 1) {
                awakeScrollbars = true;
            }
        }

        // TODO layout value = 0 maybe is wrong
        // layout(0, false);

        if (awakeScrollbars) {
            awakenScrollBars();
        }
    }

    /**
     * Makes the item at the supplied position selected.
     *
     * @param position Position to select
     * @param animate  Should the transition be animated
     */
    void setSelectionInt(int position, boolean animate) {
        if (position != mOldSelectedPosition) {
            mBlockLayoutRequests = true;
            int delta = position - mSelectedPosition;
            setNextSelectedPositionInt(position);
            // layout(delta, animate);
            mBlockLayoutRequests = false;
        }
    }

    abstract protected void layoutChildren();

    /**
     * Sets the spacing between items in a Gallery
     *
     * @param spacing The spacing in pixels between items in the Gallery
     * @attr ref android.R.styleable#Gallery_spacing
     */
    public void setSpacing(int spacing) {
        mSpacing = spacing;
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof AbsBaseListView.LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        /*
         * Gallery expects Gallery.LayoutParams.
         */
        return new AbsBaseListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
    }

    @Override
    public View getSelectedView() {
        if (mItemCount > 0 && mSelectedPosition >= 0) {
            return getChildAt(mSelectedPosition - mFirstPosition);
        } else {
            Log.e(TAG, "getSelectedView: return null! this:" + this.toString() + ", mItemCount:" + mItemCount + ", mSelectedPosition:" + mSelectedPosition);
            return null;
        }
    }

    /**
     * Override to prevent spamming ourselves with layout requests as we place
     * views
     *
     * @see View#requestLayout()
     */
    @Override
    public void requestLayout() {
        if (!mBlockLayoutRequests) {
            mNeedLayout = true;
            super.requestLayout();
        }
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus && mSelectedPosition < 0 && !isInTouchMode()) {
            if (!mIsAttached && mAdapter != null) {
                // Data may have changed while we were detached and it's valid
                // to change focus while detached. Refresh so we don't die.
                mDataChanged = true;
                mOldItemCount = mItemCount;
                mItemCount = mAdapter.getCount();
            }
            resurrectSelection();
        }
    }

    boolean resurrectSelection() {
        return true;
    }

    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public int getCount() {
        return mItemCount;
    }

    void hideSelector() {
        if (mSelectedPosition != INVALID_POSITION) {
            // TODO by lawin
            if (mLayoutMode != LAYOUT_SPECIFIC) {
                mResurrectToPosition = mSelectedPosition;
            }
            if (mNextSelectedPosition >= 0 && mNextSelectedPosition != mSelectedPosition) {
                mResurrectToPosition = mNextSelectedPosition;
            }
            setSelectedPositionInt(INVALID_POSITION);
            setNextSelectedPositionInt(INVALID_POSITION);
            mSelectedLeft = 0;
        }
    }

    /**
     * @return A position to select. First we try mSelectedPosition. If that has
     * been clobbered by entering touch mode, we then try
     * mResurrectToPosition. Values are pinned to the range of items
     * available in the adapter
     */
    protected int reconcileSelectedPosition() {
        int position = mSelectedPosition;
        if (position < 0) {
            position = mResurrectToPosition;
        }
        position = Math.max(0, position);
        position = Math.min(position, mItemCount - 1);
        return position;
    }

    /**
     * Maps a point to a position in the list.
     *
     * @param x X in local coordinate
     * @param y Y in local coordinate
     * @return The position of the item which contains the specified point, or
     * {@link #INVALID_POSITION} if the point does not intersect an
     * item.
     */
    public int pointToPosition(int x, int y) {
        Rect frame = mTouchFrame;
        if (frame == null) {
            mTouchFrame = new Rect();
            frame = mTouchFrame;
        }

        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            View child = getChildAt(i);
            if (child.getVisibility() == VISIBLE) {
                child.getHitRect(frame);
                if (frame.contains(x, y)) {
                    return mFirstPosition + i;
                }
            }
        }
        return INVALID_POSITION;
    }

    static class SavedState extends View.BaseSavedState {
        long selectedId;
        int position;

        /**
         * Constructor called from {@link AbsBaseListView#onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            selectedId = in.readLong();
            position = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(selectedId);
            out.writeInt(position);
        }

        @Override
        public String toString() {
            return "AbsSpinner.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " selectedId=" + selectedId + " position=" + position + "}";
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.selectedId = getSelectedItemId();
        if (ss.selectedId >= 0) {
            ss.position = getSelectedItemPosition();
        } else {
            ss.position = INVALID_POSITION;
        }
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());

        if (ss.selectedId >= 0) {
            mDataChanged = true;
            mNeedSync = true;
            mSyncRowId = ss.selectedId;
            mSyncPosition = ss.position;
            mSyncMode = SYNC_SELECTED_POSITION;
            requestLayout();
        }
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        /**
         * View type for this view, as returned by
         * {@link Adapter#getItemViewType(int) }
         */
        int viewType;

        /**
         * The position the view was removed from when pulled out of the scrap
         * heap.
         *
         * @hide
         */
        int scrappedFromPosition;

        /**
         * The ID the view represents
         */
        long itemId = -1;

        @ViewDebug.ExportedProperty(category = "list")
        boolean recycledHeaderFooter;

        /**
         * When an AbsSpinner is measured with an AT_MOST measure spec, it needs
         * to obtain children views to measure itself. When doing so, the
         * children are not attached to the window, but put in the recycler
         * which assumes they've been attached before. Setting this flag will
         * force the reused view to be attached to the window rather than just
         * attached to the parent.
         */
        @ViewDebug.ExportedProperty(category = "list")
        boolean forceAdd;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(int w, int h, int viewType) {
            super(w, h);
            this.viewType = viewType;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    static View retrieveFromScrap(ArrayList<View> scrapViews, int position) {
        /*StringBuffer buffer = new StringBuffer("\n retrieveFromScrap position = " + position);
        try{
            String b = null;
            b.length();
        } catch (Exception e){
            buffer.append(getExceptionString(e));
        }*/

        int size = scrapViews.size();
        if (size > 0) {
            // See if we still have a view for this position.
            for (int i = 0; i < size; i++) {
                View view = scrapViews.get(i);
                int fromPosition = ((LayoutParams) view.getLayoutParams()).scrappedFromPosition;
                if (fromPosition == position) {
                    //Log.d(TAG,"retrieveFromScrap position = " + position + "  scrappedFromPosition = " + fromPosition + " scrap view size = " + size + " view = " + view.hashCode() + "\n" + buffer.toString());
                    scrapViews.remove(i);
                    return view;
                }
            }
            //Log.d(TAG,"retrieveFromScrap position = " + position + "  scrappedFromPosition = " + (size - 1) + " scrap view size = " + size + " view = " + scrapViews.get(size - 1).hashCode() + "\n" + buffer.toString());
            return scrapViews.remove(size - 1);
        } else {
            return null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mIsAttached = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mRecycler.clear();
        mIsAttached = false;
    }

    /**
     * A RecyclerListener is used to receive a notification whenever a View is
     * placed inside the RecycleBin's scrap heap. This listener is used to free
     * resources associated to Views placed in the RecycleBin.
     *
     * @see android.widget.AbsListView.RecycleBin
     * @see android.widget.AbsListView#setRecyclerListener(android.widget.AbsListView.RecyclerListener)
     */
    public static interface RecyclerListener {
        /**
         * Indicates that the specified View was moved into the recycler's scrap
         * heap. The view is not displayed on screen any more and any expensive
         * resource associated with the view should be discarded.
         *
         * @param view
         */
        void onMovedToScrapHeap(View view);
    }

    /**
     * The RecycleBin facilitates reuse of views across layouts. The RecycleBin
     * has two levels of storage: ActiveViews and ScrapViews. ActiveViews are
     * those views which were onscreen at the start of a layout. By
     * construction, they are displaying current information. At the end of
     * layout, all views in ActiveViews are demoted to ScrapViews. ScrapViews
     * are old views that could potentially be used by the adapter to avoid
     * allocating views unnecessarily.
     *
     * @see android.widget.AbsListView#setRecyclerListener(android.widget.AbsListView.RecyclerListener)
     * @see android.widget.AbsListView.RecyclerListener
     */
    public class RecycleBin {
        private RecyclerListener mRecyclerListener;

        /**
         * The position of the first view stored in mActiveViews.
         */
        private int mFirstActivePosition;

        /**
         * Views that were on screen at the start of layout. This array is
         * populated at the start of layout, and at the end of layout all view
         * in mActiveViews are moved to mScrapViews. Views in mActiveViews
         * represent a contiguous range of Views, with position of the first
         * view store in mFirstActivePosition.
         */
        private View[] mActiveViews = new View[0];

        /**
         * Unsorted views that can be used by the adapter as a convert view.
         */
        private ArrayList<View>[] mScrapViews;

        private int mViewTypeCount;

        private ArrayList<View> mCurrentScrap;

        private ArrayList<View> mSkippedScrap;

        private SparseArray<View> mTransientStateViews;

        public void setViewTypeCount(int viewTypeCount) {
            if (viewTypeCount < 1) {
                throw new IllegalArgumentException("Can't have a viewTypeCount < 1");
            }
            // noinspection unchecked
            ArrayList<View>[] scrapViews = new ArrayList[viewTypeCount];
            for (int i = 0; i < viewTypeCount; i++) {
                scrapViews[i] = new ArrayList<View>();
            }
            mViewTypeCount = viewTypeCount;
            mCurrentScrap = scrapViews[0];
            mScrapViews = scrapViews;
        }

        public void markChildrenDirty() {
            if (mViewTypeCount == 1) {
                final ArrayList<View> scrap = mCurrentScrap;
                final int scrapCount = scrap.size();
                for (int i = 0; i < scrapCount; i++) {
                    scrap.get(i).forceLayout();
                }
            } else {
                final int typeCount = mViewTypeCount;
                for (int i = 0; i < typeCount; i++) {
                    final ArrayList<View> scrap = mScrapViews[i];
                    final int scrapCount = scrap.size();
                    for (int j = 0; j < scrapCount; j++) {
                        scrap.get(j).forceLayout();
                    }
                }
            }
            if (mTransientStateViews != null) {
                final int count = mTransientStateViews.size();
                for (int i = 0; i < count; i++) {
                    mTransientStateViews.valueAt(i).forceLayout();
                }
            }
        }

        public boolean shouldRecycleViewType(int viewType) {
            return viewType >= 0;
        }

        /**
         * Clears the scrap heap.
         */
        void clear() {
            if (mViewTypeCount == 1) {
                final ArrayList<View> scrap = mCurrentScrap;
                final int scrapCount = scrap.size();
                for (int i = 0; i < scrapCount; i++) {
                    removeDetachedView(scrap.remove(scrapCount - 1 - i), false);
                }
            } else {
                final int typeCount = mViewTypeCount;
                for (int i = 0; i < typeCount; i++) {
                    final ArrayList<View> scrap = mScrapViews[i];
                    final int scrapCount = scrap.size();
                    for (int j = 0; j < scrapCount; j++) {
                        removeDetachedView(scrap.remove(scrapCount - 1 - j), false);
                    }
                }
            }
            if (mTransientStateViews != null) {
                mTransientStateViews.clear();
            }
        }

        /**
         * Fill ActiveViews with all of the children of the AbsListView.
         *
         * @param childCount          The minimum number of views mActiveViews should hold
         * @param firstActivePosition The position of the first view that will be stored in
         *                            mActiveViews
         */
        public void fillActiveViews(int childCount, int firstActivePosition) {
            if (mActiveViews.length < childCount) {
                mActiveViews = new View[childCount];
            }
            mFirstActivePosition = firstActivePosition;

            final View[] activeViews = mActiveViews;
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                // Don't put header or footer views into the scrap heap
                if (lp != null && lp.viewType != ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
                    // Note: We do place AdapterView.ITEM_VIEW_TYPE_IGNORE in
                    // active views.
                    // However, we will NOT place them into scrap views.
                    activeViews[i] = child;
                }
            }
        }

        /**
         * Get the view corresponding to the specified position. The view will
         * be removed from mActiveViews if it is found.
         *
         * @param position The position to look up in mActiveViews
         * @return The view if it is found, null otherwise
         */
        View getActiveView(int position) {
            int index = position - mFirstActivePosition;
            final View[] activeViews = mActiveViews;
            if (index >= 0 && index < activeViews.length) {
                final View match = activeViews[index];
                activeViews[index] = null;
                return match;
            }
            return null;
        }

        View getTransientStateView(int position) {
            if (mTransientStateViews == null) {
                return null;
            }
            final int index = mTransientStateViews.indexOfKey(position);
            if (index < 0) {
                return null;
            }
            final View result = mTransientStateViews.valueAt(index);
            mTransientStateViews.removeAt(index);
            return result;
        }

        /**
         * Dump any currently saved views with transient state.
         */
        void clearTransientStateViews() {
            if (mTransientStateViews != null) {
                mTransientStateViews.clear();
            }
        }

        /**
         * @return A view from the ScrapViews collection. These are unordered.
         */
        View getScrapView(int position) {
            if (mViewTypeCount == 1) {
                return retrieveFromScrap(mCurrentScrap, position);
            } else {
                if (mAdapter != null) {
                    int whichScrap = mAdapter.getItemViewType(position);
                    if (whichScrap >= 0 && whichScrap < mScrapViews.length) {
                        return retrieveFromScrap(mScrapViews[whichScrap], position);
                    }
                }
            }
            return null;
        }

        /**
         * Put a view into the ScrapViews list. These views are unordered.
         *
         * @param scrap The view to add
         */
        public void addScrapView(View scrap, int position) {
            /*StringBuffer buffer = new StringBuffer();
            try{
                String b = null;
                b.length();
            } catch (Exception e){
                buffer.append(getExceptionString(e));
            }*/

            LayoutParams lp = (LayoutParams) scrap.getLayoutParams();
            if (lp == null) {
                return;
            }

            lp.scrappedFromPosition = position;

            // Don't put header or footer views or views that should be ignored
            // into the scrap heap
            int viewType = lp.viewType;
            //final boolean scrapHasTransientState = scrap.hasTransientState();
            boolean scrapHasTransientState = false;
            try {
                scrapHasTransientState = (Boolean) ReflectUtils.invokeMethod(scrap, "hasTransientState", new Class[0], new Object[0]);
            } catch (Exception e) {

            }
            if (!shouldRecycleViewType(viewType) || scrapHasTransientState) {
                if (viewType != ITEM_VIEW_TYPE_HEADER_OR_FOOTER || scrapHasTransientState) {
                    if (mSkippedScrap == null) {
                        mSkippedScrap = new ArrayList<View>();
                    }
                    mSkippedScrap.add(scrap);
                }
                if (scrapHasTransientState) {
                    if (mTransientStateViews == null) {
                        mTransientStateViews = new SparseArray<View>();
                    }
                    //scrap.dispatchStartTemporaryDetach();
                    try {

                        ReflectUtils.invokeMethod(scrap, "dispatchStartTemporaryDetach", new Object[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mTransientStateViews.put(position, scrap);
                }
                return;
            }

            // TODO
            // scrap.dispatchStartTemporaryDetach();
            try {

                ReflectUtils.invokeMethod(scrap, "dispatchStartTemporaryDetach", new Object[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mViewTypeCount == 1) {
                mCurrentScrap.add(scrap);
                //Log.d(TAG, "addScrapView position = " + lp.scrappedFromPosition +" mCurrentScrap "
                // + " scrap view size = " + mCurrentScrap.size() + " convertView = " + (scrap == null ? "null ":scrap.hashCode()) + "\n" + buffer.toString());
            } else {
                mScrapViews[viewType].add(scrap);
                //Log.d(TAG, "addScrapView position = " + lp.scrappedFromPosition +" viewType= " + viewType
                // + " scrap view size = " + mScrapViews.length + " convertView = " + (scrap == null ? "null ":scrap.hashCode()) + "\n" + buffer.toString());
            }

            scrap.setAccessibilityDelegate(null);
            if (mRecyclerListener != null) {
                mRecyclerListener.onMovedToScrapHeap(scrap);
            }
        }

        /**
         * Finish the removal of any views that skipped the scrap heap.
         */
        public void removeSkippedScrap() {
            if (mSkippedScrap == null) {
                return;
            }
            final int count = mSkippedScrap.size();
            for (int i = 0; i < count; i++) {
                removeDetachedView(mSkippedScrap.get(i), false);
            }
            mSkippedScrap.clear();
        }

        /**
         * Move all views remaining in mActiveViews to mScrapViews.
         */
        public void scrapActiveViews() {
            final View[] activeViews = mActiveViews;
            final boolean hasListener = mRecyclerListener != null;
            final boolean multipleScraps = mViewTypeCount > 1;

            ArrayList<View> scrapViews = mCurrentScrap;
            final int count = activeViews.length;
            for (int i = count - 1; i >= 0; i--) {
                final View victim = activeViews[i];
                if (victim != null) {
                    final LayoutParams lp = (LayoutParams) victim.getLayoutParams();
                    int whichScrap = lp.viewType;

                    activeViews[i] = null;

                    //final boolean scrapHasTransientState = victim.hasTransientState();
                    boolean scrapHasTransientState = false;
                    try {
                        scrapHasTransientState = (Boolean) ReflectUtils.invokeMethod(victim, "hasTransientState", new Class[0], new Object[0]);
                    } catch (Exception e) {

                    }
                    if (!shouldRecycleViewType(whichScrap) || scrapHasTransientState) {
                        // Do not move views that should be ignored
                        if (whichScrap != ITEM_VIEW_TYPE_HEADER_OR_FOOTER || scrapHasTransientState) {
                            removeDetachedView(victim, false);
                        }
                        if (scrapHasTransientState) {
                            if (mTransientStateViews == null) {
                                mTransientStateViews = new SparseArray<View>();
                            }
                            mTransientStateViews.put(mFirstActivePosition + i, victim);
                        }
                        continue;
                    }

                    if (multipleScraps) {
                        scrapViews = mScrapViews[whichScrap];
                    }

                    // TODO
                    // victim.dispatchStartTemporaryDetach();
                    try {

                        ReflectUtils.invokeMethod(victim, "dispatchStartTemporaryDetach", new Object[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    lp.scrappedFromPosition = mFirstActivePosition + i;
                    scrapViews.add(victim);

                    victim.setAccessibilityDelegate(null);
                    if (hasListener) {
                        mRecyclerListener.onMovedToScrapHeap(victim);
                    }
                }
            }

            pruneScrapViews();
        }

        /**
         * Makes sure that the size of mScrapViews does not exceed the size of
         * mActiveViews. (This can happen if an adapter does not recycle its
         * views).
         */
        private void pruneScrapViews() {
            final int maxViews = mActiveViews.length;
            final int viewTypeCount = mViewTypeCount;
            final ArrayList<View>[] scrapViews = mScrapViews;
            for (int i = 0; i < viewTypeCount; ++i) {
                final ArrayList<View> scrapPile = scrapViews[i];
                int size = scrapPile.size();
                final int extras = size - maxViews;
                size--;
                for (int j = 0; j < extras; j++) {
                    removeDetachedView(scrapPile.remove(size--), false);
                }
            }

            boolean hasTransientState = false;
            if (mTransientStateViews != null) {
                for (int i = 0; i < mTransientStateViews.size(); i++) {
                    final View v = mTransientStateViews.valueAt(i);
                    try {
                        hasTransientState = (Boolean) ReflectUtils.invokeMethod(v, "hasTransientState", new Class[0], new Object[0]);
                    } catch (Exception e) {

                    }
                    if (!hasTransientState) {
                        mTransientStateViews.removeAt(i);
                        i--;
                    }
                }
            }
        }

        /**
         * Puts all views in the scrap heap into the supplied list.
         */
        void reclaimScrapViews(List<View> views) {
            if (mViewTypeCount == 1) {
                views.addAll(mCurrentScrap);
            } else {
                final int viewTypeCount = mViewTypeCount;
                final ArrayList<View>[] scrapViews = mScrapViews;
                for (int i = 0; i < viewTypeCount; ++i) {
                    final ArrayList<View> scrapPile = scrapViews[i];
                    views.addAll(scrapPile);
                }
            }
        }

        /**
         * Updates the cache color hint of all known views.
         *
         * @param color The new cache color hint.
         */
        void setCacheColorHint(int color) {
            if (mViewTypeCount == 1) {
                final ArrayList<View> scrap = mCurrentScrap;
                final int scrapCount = scrap.size();
                for (int i = 0; i < scrapCount; i++) {
                    scrap.get(i).setDrawingCacheBackgroundColor(color);
                }
            } else {
                final int typeCount = mViewTypeCount;
                for (int i = 0; i < typeCount; i++) {
                    final ArrayList<View> scrap = mScrapViews[i];
                    final int scrapCount = scrap.size();
                    for (int j = 0; j < scrapCount; j++) {
                        scrap.get(j).setDrawingCacheBackgroundColor(color);
                    }
                }
            }
            // Just in case this is called during a layout pass
            final View[] activeViews = mActiveViews;
            final int count = activeViews.length;
            for (int i = 0; i < count; ++i) {
                final View victim = activeViews[i];
                if (victim != null) {
                    victim.setDrawingCacheBackgroundColor(color);
                }
            }
        }
    }

    /**
     * A MultiChoiceModeListener receives events for
     * {@link AbsListView#CHOICE_MODE_MULTIPLE_MODAL}. It acts as the
     * {@link ActionMode.Callback} for the selection mode and also receives
     * {@link #onItemCheckedStateChanged(ActionMode, int, long, boolean)} events
     * when the user selects and deselects list items.
     */
    public interface MultiChoiceModeListener extends ActionMode.Callback {
        /**
         * Called when an item is checked or unchecked during selection mode.
         *
         * @param mode     The {@link ActionMode} providing the selection mode
         * @param position Adapter position of the item that was checked or unchecked
         * @param id       Adapter ID of the item that was checked or unchecked
         * @param checked  <code>true</code> if the item is now checked,
         *                 <code>false</code> if the item is now unchecked.
         */
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked);
    }

    class MultiChoiceModeWrapper implements MultiChoiceModeListener {
        private MultiChoiceModeListener mWrapped;

        public void setWrapped(MultiChoiceModeListener wrapped) {
            mWrapped = wrapped;
        }

        public boolean hasWrappedCallback() {
            return mWrapped != null;
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if (mWrapped.onCreateActionMode(mode, menu)) {
                // Initialize checked graphic state?
                setLongClickable(false);
                return true;
            }
            return false;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return mWrapped.onPrepareActionMode(mode, menu);
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return mWrapped.onActionItemClicked(mode, item);
        }

        public void onDestroyActionMode(ActionMode mode) {
            mWrapped.onDestroyActionMode(mode);
            mChoiceActionMode = null;

            // Ending selection mode means deselecting everything.
            clearChoices();

            mDataChanged = true;
            rememberSyncState();
            requestLayout();

            setLongClickable(true);
        }

        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            mWrapped.onItemCheckedStateChanged(mode, position, id, checked);

            // If there are no items selected we no longer need the selection
            // mode.
            if (getCheckedItemCount() == 0) {
                mode.finish();
            }
        }
    }

    /**
     * ListAdapter used when a ListView has header views. This ListAdapter wraps
     * another one and also keeps track of the header views and their associated
     * data objects.
     * <p>
     * This is intended as a base class; you will probably not need to use this
     * class directly in your own code.
     */
    public class HeaderViewListAdapter implements WrapperListAdapter, Filterable {

        private final ListAdapter mAdapter;

        // These two ArrayList are assumed to NOT be null.
        // They are indeed created when declared in ListView and then shared.
        ArrayList<FixedViewInfo> mHeaderViewInfos;
        ArrayList<FixedViewInfo> mFooterViewInfos;

        // Used as a placeholder in case the provided info views are indeed
        // null.
        // Currently only used by some CTS tests, which may be removed.
        final ArrayList<FixedViewInfo> EMPTY_INFO_LIST = new ArrayList<FixedViewInfo>();

        boolean mAreAllFixedViewsSelectable;

        private final boolean mIsFilterable;

        public HeaderViewListAdapter(ArrayList<ListView.FixedViewInfo> headerViewInfos, ArrayList<ListView.FixedViewInfo> footerViewInfos, ListAdapter adapter) {
            mAdapter = adapter;
            mIsFilterable = adapter instanceof Filterable;

            if (headerViewInfos == null) {
                mHeaderViewInfos = EMPTY_INFO_LIST;
            } else {
                mHeaderViewInfos = headerViewInfos;
            }

            if (footerViewInfos == null) {
                mFooterViewInfos = EMPTY_INFO_LIST;
            } else {
                mFooterViewInfos = footerViewInfos;
            }

            mAreAllFixedViewsSelectable = areAllListInfosSelectable(mHeaderViewInfos) && areAllListInfosSelectable(mFooterViewInfos);
        }

        public int getHeadersCount() {
            return mHeaderViewInfos.size();
        }

        public int getFootersCount() {
            return mFooterViewInfos.size();
        }

        public boolean isEmpty() {
            return mAdapter == null || mAdapter.isEmpty();
        }

        private boolean areAllListInfosSelectable(ArrayList<ListView.FixedViewInfo> infos) {
            if (infos != null) {
                for (ListView.FixedViewInfo info : infos) {
                    if (!info.isSelectable) {
                        return false;
                    }
                }
            }
            return true;
        }

        public boolean removeHeader(View v) {
            for (int i = 0; i < mHeaderViewInfos.size(); i++) {
                ListView.FixedViewInfo info = mHeaderViewInfos.get(i);
                if (info.view == v) {
                    mHeaderViewInfos.remove(i);

                    mAreAllFixedViewsSelectable = areAllListInfosSelectable(mHeaderViewInfos) && areAllListInfosSelectable(mFooterViewInfos);

                    return true;
                }
            }

            return false;
        }

        public boolean removeFooter(View v) {
            for (int i = 0; i < mFooterViewInfos.size(); i++) {
                ListView.FixedViewInfo info = mFooterViewInfos.get(i);
                if (info.view == v) {
                    mFooterViewInfos.remove(i);

                    mAreAllFixedViewsSelectable = areAllListInfosSelectable(mHeaderViewInfos) && areAllListInfosSelectable(mFooterViewInfos);

                    return true;
                }
            }

            return false;
        }

        public int getCount() {
            if (mAdapter != null) {
                return getFootersCount() + getHeadersCount() + mAdapter.getCount();
            } else {
                return getFootersCount() + getHeadersCount();
            }
        }

        public boolean areAllItemsEnabled() {
            if (mAdapter != null) {
                return mAreAllFixedViewsSelectable && mAdapter.areAllItemsEnabled();
            } else {
                return true;
            }
        }

        public boolean isEnabled(int position) {
            // Header (negative positions will throw an
            // ArrayIndexOutOfBoundsException)
            int numHeaders = getHeadersCount();
            if (position < numHeaders) {
                return mHeaderViewInfos.get(position).isSelectable;
            }

            // Adapter
            final int adjPosition = position - numHeaders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = mAdapter.getCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.isEnabled(adjPosition);
                }
            }

            // Footer (off-limits positions will throw an
            // ArrayIndexOutOfBoundsException)
            return mFooterViewInfos.get(adjPosition - adapterCount).isSelectable;
        }

        public Object getItem(int position) {
            // Header (negative positions will throw an
            // ArrayIndexOutOfBoundsException)
            int numHeaders = getHeadersCount();
            if (position < numHeaders) {
                return mHeaderViewInfos.get(position).data;
            }

            // Adapter
            final int adjPosition = position - numHeaders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = mAdapter.getCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItem(adjPosition);
                }
            }

            // Footer (off-limits positions will throw an
            // ArrayIndexOutOfBoundsException)
            return mFooterViewInfos.get(adjPosition - adapterCount).data;
        }

        public long getItemId(int position) {
            int numHeaders = getHeadersCount();
            if (mAdapter != null && position >= numHeaders) {
                int adjPosition = position - numHeaders;
                int adapterCount = mAdapter.getCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        public boolean hasStableIds() {
            if (mAdapter != null) {
                return mAdapter.hasStableIds();
            }
            return false;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // Header (negative positions will throw an
            // ArrayIndexOutOfBoundsException)
            int numHeaders = getHeadersCount();
            if (position < numHeaders) {
                return mHeaderViewInfos.get(position).view;
            }

            // Adapter
            final int adjPosition = position - numHeaders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = mAdapter.getCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getView(adjPosition, convertView, parent);
                }
            }

            // Footer (off-limits positions will throw an
            // ArrayIndexOutOfBoundsException)
            return mFooterViewInfos.get(adjPosition - adapterCount).view;
        }

        public int getItemViewType(int position) {
            int numHeaders = getHeadersCount();
            if (mAdapter != null && position >= numHeaders) {
                int adjPosition = position - numHeaders;
                int adapterCount = mAdapter.getCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemViewType(adjPosition);
                }
            }

            return ITEM_VIEW_TYPE_HEADER_OR_FOOTER;
        }

        public int getViewTypeCount() {
            if (mAdapter != null) {
                return mAdapter.getViewTypeCount();
            }
            return 1;
        }

        public void registerDataSetObserver(DataSetObserver observer) {
            if (mAdapter != null) {
                mAdapter.registerDataSetObserver(observer);
            }
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (mAdapter != null) {
                mAdapter.unregisterDataSetObserver(observer);
            }
        }

        public Filter getFilter() {
            if (mIsFilterable) {
                return ((Filterable) mAdapter).getFilter();
            }
            return null;
        }

        public ListAdapter getWrappedAdapter() {
            return mAdapter;
        }
    }

    public static String getExceptionString(Throwable e) {

        if (e == null) {
            return "e==null";
        }

        StringBuffer err = new StringBuffer();
        err.append(e.toString());
        err.append("\n");

        err.append("at ");
        StackTraceElement[] stack = e.getStackTrace();
        if (stack != null) {
            for (int i = 0; i < stack.length; i++) {
                err.append(stack[i].toString());
                err.append("\n");
            }
        }

        Throwable cause = e.getCause();
        if (cause != null) {
            err.append("\n");
            err.append("Caused by: ");
            err.append(getExceptionString(cause));
        }
        return err.toString();
    }
}
