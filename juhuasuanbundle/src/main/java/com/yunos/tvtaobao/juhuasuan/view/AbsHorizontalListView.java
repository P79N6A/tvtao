/*
 * Copyright (C) 2007 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yunos.tvtaobao.juhuasuan.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.FocusFinder;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Transformation;
import android.widget.Scroller;
import android.widget.SpinnerAdapter;

import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tv.core.common.AppDebug;

/**
 * A view that shows items in a center-locked, horizontally scrolling list.
 * <p>
 * The default values for the Gallery assume you will be using {@link android.R.styleable#Theme_galleryItemBackground}
 * as the background for each View
 * given to the Gallery from the Adapter. If you are not doing this, you may need to adjust some Gallery properties,
 * such as the spacing.
 * <p>
 * Views given to the Gallery should use {@link AbsHorizontalListView.LayoutParams} as their layout parameters type.
 * <p>
 * See the <a href="{@docRoot} resources/tutorials/views/hello-gallery.html">Gallery tutorial</a>.
 * </p>
 * @attr ref android.R.styleable#Gallery_animationDuration
 * @attr ref android.R.styleable#Gallery_spacing
 * @attr ref android.R.styleable#Gallery_gravity
 */
//@Widget
public class AbsHorizontalListView extends AbsSpinner implements GestureDetector.OnGestureListener {

    private static final String TAG = "AbsHorizontalListView";

    private static final boolean localLOGV = false;

    /**
     * Duration in milliseconds from the start of a scroll during which we're
     * unsure whether the user is scrolling or flinging.
     */
    private static final int SCROLL_TO_FLING_UNCERTAINTY_TIMEOUT = 250;

    /**
     * Horizontal spacing between items.
     */
    private int mSpacing = 0;

    /**
     * How long the transition animation should run when a child view changes
     * position, measured in milliseconds.
     */
    private int mAnimationDuration = 200;

    /**
     * The alpha of items that are not selected.
     */
    private float mUnselectedAlpha;

    /**
     * Left most edge of a child seen so far during layout.
     */
    private int mLeftMost;

    /**
     * Right most edge of a child seen so far during layout.
     */
    private int mRightMost;

    int mGravity;
    int mGravityHeightAnchor;

    /**
     * Helper for detecting touch gestures.
     */
    private GestureDetector mGestureDetector;

    /**
     * The position of the item that received the user's down touch.
     */
    private int mDownTouchPosition;

    /**
     * The view of the item that received the user's down touch.
     */
    private View mDownTouchView;

    /**
     * Executes the delta scrolls from a fling or scroll movement.
     */
    private FlingRunnable mFlingRunnable = new FlingRunnable();

    int mSelectedLeft;

    boolean gainFocus;

    Drawable mSelector;
    int mSelectorBorderWidth;
    int mSelectorBorderHeight;

    /**
     * Sets mSuppressSelectionChanged = false. This is used to set it to false
     * in the future. It will also trigger a selection changed.
     */
    private Runnable mDisableSuppressSelectionChangedRunnable = new Runnable() {

        @Override
        public void run() {
            mSuppressSelectionChanged = false;
            selectionChanged();
        }
    };

    /**
     * When fling runnable runs, it resets this to false. Any method along the
     * path until the end of its run() can set this to true to abort any
     * remaining fling. For example, if we've reached either the leftmost or
     * rightmost item, we will set this to true.
     */
    private boolean mShouldStopFling;

    /**
     * The currently selected item's child.
     */
    private View mSelectedChild;

    /**
     * Whether to continuously callback on the item selected listener during a
     * fling.
     */
    private boolean mShouldCallbackDuringFling = true;

    /**
     * Whether to callback when an item that is not selected is clicked.
     */
    private boolean mShouldCallbackOnUnselectedItemClick = true;

    /**
     * If true, do not callback to item selected listener.
     */
    private boolean mSuppressSelectionChanged;

    /**
     * If true, we have received the "invoke" (center or enter buttons) key
     * down. This is checked before we action on the "invoke" key up, and is
     * subsequently cleared.
     */
    private boolean mReceivedInvokeKeyDown;

    private AdapterContextMenuInfo mContextMenuInfo;

    /**
     * If true, this onScroll is the first for this user's drag (remember, a
     * drag sends many onScrolls).
     */
    private boolean mIsFirstScroll;

    /**
     * If true, mFirstPosition is the position of the rightmost child, and
     * the children are ordered right to left.
     */
    private boolean mIsRtl = true;

    // the single allocated result per list view; kinda cheesey but avoids
    // allocating these thingies too often.
    private final ArrowScrollFocusResult mArrowScrollFocusResult = new ArrowScrollFocusResult();

    // used for temporary calculations.
    private final Rect mTempRect = new Rect();

    boolean mItemsCanFocus = true;

    /**
     * Indicates whether the list selector should be drawn on top of the children or behind
     */
    boolean mDrawSelectorOnTop = false;

    /**
     * set a exactly selected size
     */
    Rect mExactlyUserSelectedRect;

    public AbsHorizontalListView(Context context) {
        this(context, null);
    }

    public AbsHorizontalListView(Context context, AttributeSet attrs) {
        //this(context, attrs, yunos.demo.R.attr.horizontalListViewStyle);
        super(context, attrs);
        mGestureDetector = new GestureDetector(context, this);
        mGestureDetector.setIsLongpressEnabled(true);
    }

    public AbsHorizontalListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mGestureDetector = new GestureDetector(context, this);
        mGestureDetector.setIsLongpressEnabled(true);

        //        TypedArray a = context.obtainStyledAttributes(
        //                attrs, com.android.internal.R.styleable.Gallery, defStyle, 0);
        //
        //        int index = a.getInt(com.android.internal.R.styleable.Gallery_gravity, -1);
        //        if (index >= 0) {
        //            setGravity(index);
        //        }
        //
        //        int animationDuration =
        //                a.getInt(com.android.internal.R.styleable.Gallery_animationDuration, -1);
        //        if (animationDuration > 0) {
        //            setAnimationDuration(animationDuration);
        //        }
        //
        //        int spacing =
        //                a.getDimensionPixelOffset(com.android.internal.R.styleable.Gallery_spacing, 0);
        //        setSpacing(spacing);
        //
        //        float unselectedAlpha = a.getFloat(
        //                com.android.internal.R.styleable.Gallery_unselectedAlpha, 1.0f);
        //        setUnselectedAlpha(unselectedAlpha);

        //   a.recycle();

        // We draw the selected item last (because otherwise the item to the
        // right overlaps it)
        //        mGroupFlags |= FLAG_USE_CHILD_DRAWING_ORDER;
        setChildrenDrawingOrderEnabled(true);
        //        
        //        mGroupFlags |= FLAG_SUPPORT_STATIC_TRANSFORMATIONS;
        setStaticTransformationsEnabled(true);
        setSelector(R.drawable.jhs_tui_bg_focus);
    }

    public void setSelector(Drawable selector) {
        mSelector = selector;
    }

    public void setSelector(int selectorId) {
        //        mSelector = AuiResourceFetcher.getResources(getContext()).getDrawable(selectorId);
        mSelector = getContext().getResources().getDrawable(selectorId);
    }

    public void setSelectorBorderWidth(int borderWidth) {
        mSelectorBorderWidth = borderWidth;
    }

    public void setSelectorBorderHeight(int borderHeight) {
        mSelectorBorderHeight = borderHeight;
    }

    /**
     * Whether or not to callback on any {@link #getOnItemSelectedListener()} while the items are being flinged. If
     * false, only the final selected
     * item
     * will cause the callback. If true, all items between the first and the
     * final will cause callbacks.
     * @param shouldCallback
     *            Whether or not to callback on the listener while
     *            the items are being flinged.
     */
    public void setCallbackDuringFling(boolean shouldCallback) {
        mShouldCallbackDuringFling = shouldCallback;
    }

    /**
     * Whether or not to callback when an item that is not selected is clicked.
     * If false, the item will become selected (and re-centered). If true, the {@link #getOnItemClickListener()} will
     * get the callback.
     * @param shouldCallback
     *            Whether or not to callback on the listener when a
     *            item that is not selected is clicked.
     * @hide
     */
    public void setCallbackOnUnselectedItemClick(boolean shouldCallback) {
        mShouldCallbackOnUnselectedItemClick = shouldCallback;
    }

    /**
     * Sets how long the transition animation should run when a child view
     * changes position. Only relevant if animation is turned on.
     * @param animationDurationMillis
     *            The duration of the transition, in
     *            milliseconds.
     * @attr ref android.R.styleable#Gallery_animationDuration
     */
    public void setAnimationDuration(int animationDurationMillis) {
        mAnimationDuration = animationDurationMillis;
    }

    /**
     * Sets the spacing between items in a Gallery
     * @param spacing
     *            The spacing in pixels between items in the Gallery
     * @attr ref android.R.styleable#Gallery_spacing
     */
    public void setSpacing(int spacing) {
        mSpacing = spacing;
    }

    /**
     * Sets the alpha of items that are not selected in the Gallery.
     * @param unselectedAlpha
     *            the alpha for the items that are not selected.
     * @attr ref android.R.styleable#Gallery_unselectedAlpha
     */
    public void setUnselectedAlpha(float unselectedAlpha) {
        mUnselectedAlpha = unselectedAlpha;
    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {

        t.clear();
        t.setAlpha(child == mSelectedChild ? 1.0f : mUnselectedAlpha);

        return true;
    }

    @Override
    protected int computeHorizontalScrollExtent() {
        // Only 1 item is considered to be selected
        return 1;
    }

    @Override
    protected int computeHorizontalScrollOffset() {
        // Current scroll position is the same as the selected position
        return mSelectedPosition;
    }

    @Override
    protected int computeHorizontalScrollRange() {
        // Scroll range is the same as the item count
        return mItemCount;
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof AbsSpinner.LayoutParams;
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
        return new AbsSpinner.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        /*
         * Remember that we are in layout to prevent more layout request from
         * being generated.
         */
        mInLayout = true;
        layout(0, false);
        mInLayout = false;
    }

    @Override
    int getChildHeight(View child) {
        return child.getMeasuredHeight();
    }

    /**
     * Tracks a motion scroll. In reality, this is used to do just about any
     * movement to items (touch scroll, arrow-key scroll, set an item as selected).
     * @param deltaX
     *            Change in X from the previous event.
     */
    void trackMotionScroll(int deltaX) {

        if (getChildCount() == 0) {
            return;
        }

        boolean toLeft = deltaX < 0;

        int limitedDeltaX = getLimitedMotionScrollAmount(toLeft, deltaX);
        //        int limitedDeltaX = deltaX;
        if (limitedDeltaX != deltaX) {
            // The above call returned a limited amount, so stop any scrolls/flings
            mFlingRunnable.endFling(false);
            onFinishedMovement();
        }

        offsetChildrenLeftAndRight(limitedDeltaX);

        detachOffScreenChildren(toLeft);

        if (toLeft) {
            // If moved left, there will be empty space on the right
            fillToGalleryRight();
        } else {
            // Similarly, empty space on the left
            fillToGalleryLeft();
        }

        // Clear unused views
        //        mRecycler.clear();

        //        setSelectionToCenterChild();
        //        setSelectionToNextChild();

        onScrollChanged(0, 0, 0, 0); // dummy values, View's implementation does not use these.

        invalidate();
    }

    /**
     * Looks for the child that is closest to the center and sets it as the
     * selected child.
     */
    private void setSelectionToCenterChild() {

        View selView = mSelectedChild;
        if (mSelectedChild == null)
            return;

        int galleryCenter = getCenterOfGallery();

        // Common case where the current selected position is correct
        if (selView.getLeft() <= galleryCenter && selView.getRight() >= galleryCenter) {
            return;
        }

        // TODO better search
        int closestEdgeDistance = Integer.MAX_VALUE;
        int newSelectedChildIndex = 0;
        for (int i = getChildCount() - 1; i >= 0; i--) {

            View child = getChildAt(i);

            if (child.getLeft() <= galleryCenter && child.getRight() >= galleryCenter) {
                // This child is in the center
                newSelectedChildIndex = i;
                break;
            }

            int childClosestEdgeDistance = Math.min(Math.abs(child.getLeft() - galleryCenter),
                    Math.abs(child.getRight() - galleryCenter));
            if (childClosestEdgeDistance < closestEdgeDistance) {
                closestEdgeDistance = childClosestEdgeDistance;
                newSelectedChildIndex = i;
            }
        }

        int newPos = mFirstPosition + newSelectedChildIndex;

        if (newPos != mSelectedPosition) {
            setSelectedPositionInt(newPos);
            setNextSelectedPositionInt(newPos);
            checkSelectionChanged();
        }
    }

    /**
     * @return The center of this Gallery.
     */
    private int getCenterOfGallery() {
        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
    }

    int getLimitedMotionScrollAmount(boolean motionToLeft, int deltaX) {
        int extremeItemPosition = motionToLeft != mIsRtl ? mItemCount - 1 : 0;
        View extremeChild = getChildAt(extremeItemPosition - mFirstPosition);

        if (extremeChild == null) {
            return deltaX;
        }
        int extremeChildEdge = getEdgeOfView(extremeChild, motionToLeft);
        int galleryEdge = getEdgeOfGallery(motionToLeft);

        if (motionToLeft) {
            if (extremeChildEdge <= galleryEdge) {

                // The extreme child is past his boundary point!
                return 0;
            }
        } else {
            if (extremeChildEdge >= galleryEdge) {

                // The extreme child is past his boundary point!
                return 0;
            }
        }

        int centerDifference = galleryEdge - extremeChildEdge;

        return motionToLeft ? Math.max(centerDifference, deltaX) : Math.min(centerDifference, deltaX);
    }

    /**
     * Offset the horizontal location of all children of this view by the
     * specified number of pixels.
     * @param offset
     *            the number of pixels to offset
     */
    private void offsetChildrenLeftAndRight(int offset) {
        int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            getChildAt(i).offsetLeftAndRight(offset);
        }
    }

    //    /**
    //     * @return The center of this Gallery.
    //     */
    //    private int getCenterOfGallery() {
    //        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
    //    }
    //    
    //    /**
    //     * @return The center of the given view.
    //     */
    //    private static int getCenterOfView(View view) {
    //        return view.getLeft() + view.getWidth() / 2;
    //    }

    private static int getEdgeOfView(View view, boolean isNext) {
        int edge = isNext ? view.getRight() : view.getLeft();
        AppDebug.d(TAG, "getEdgeOfView ==> edge = " + edge + ", isNext = " + isNext);
        return edge;
    }

    private int getEdgeOfGallery(boolean isNext) {
        int edge = isNext ? getWidth() : getPaddingLeft();
        AppDebug.d(TAG, "getEdgeOfGallery ==> edge = " + edge + ", isNext = " + isNext);
        return edge;
    }

    /**
     * Detaches children that are off the screen (i.e.: Gallery bounds).
     * @param toLeft
     *            Whether to detach children to the left of the Gallery, or
     *            to the right.
     */
    private void detachOffScreenChildren(boolean toLeft) {
        int numChildren = getChildCount();
        int firstPosition = mFirstPosition;
        int start = 0;
        int count = 0;

        if (toLeft) {
            final int galleryLeft = getPaddingLeft();
            for (int i = 0; i < numChildren; i++) {
                int n = mIsRtl ? (numChildren - 1 - i) : i;
                final View child = getChildAt(n);
                if (child.getRight() >= galleryLeft) {
                    break;
                } else {
                    start = n;
                    count++;
                    mRecycler.addScrapView(firstPosition + n, child);
                }
            }
            if (!mIsRtl) {
                start = 0;
            }
        } else {
            final int galleryRight = getWidth() - getPaddingRight();
            for (int i = numChildren - 1; i >= 0; i--) {
                int n = mIsRtl ? numChildren - 1 - i : i;
                final View child = getChildAt(n);
                if (child.getLeft() <= galleryRight) {
                    break;
                } else {
                    start = n;
                    count++;
                    mRecycler.addScrapView(firstPosition + n, child);
                }
            }
            if (mIsRtl) {
                start = 0;
            }
        }

        detachViewsFromParent(start, count);

        if (toLeft != mIsRtl) {
            mFirstPosition += count;
        }
    }

    /**
     * Scrolls the items so that the selected item is in its 'slot' (its center
     * is the gallery's center).
     */
    private void scrollIntoSlots() {

        /*
         * if (getChildCount() == 0 || mSelectedChild == null) return;
         * int selectedCenter = getCenterOfView(mSelectedChild);
         * int targetCenter = getCenterOfGallery();
         * int scrollAmount = targetCenter - selectedCenter;
         * if (scrollAmount != 0) {
         * mFlingRunnable.startUsingDistance(scrollAmount);
         * } else {
         * onFinishedMovement();
         * }
         */
        //TODO fix me
    }

    void onFinishedMovement() {
        if (mSuppressSelectionChanged) {
            mSuppressSelectionChanged = false;

            // We haven't been callbacking during the fling, so do it now
            super.selectionChanged();
        }
        invalidate();
    }

    @Override
    void selectionChanged() {
        if (!mSuppressSelectionChanged) {
            super.selectionChanged();
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
     * Looks for the child that is closest to the center and sets it as the
     * selected child.
     */
    /*
     * private void setSelectionToCenterChild() {
     * View selView = mSelectedChild;
     * if (mSelectedChild == null) return;
     * int galleryCenter = getCenterOfGallery();
     * // Common case where the current selected position is correct
     * if (selView.getLeft() <= galleryCenter && selView.getRight() >= galleryCenter) {
     * return;
     * }
     * // TODO better search
     * int closestEdgeDistance = Integer.MAX_VALUE;
     * int newSelectedChildIndex = 0;
     * for (int i = getChildCount() - 1; i >= 0; i--) {
     * View child = getChildAt(i);
     * if (child.getLeft() <= galleryCenter && child.getRight() >= galleryCenter) {
     * // This child is in the center
     * newSelectedChildIndex = i;
     * break;
     * }
     * int childClosestEdgeDistance = Math.min(Math.abs(child.getLeft() - galleryCenter),
     * Math.abs(child.getRight() - galleryCenter));
     * if (childClosestEdgeDistance < closestEdgeDistance) {
     * closestEdgeDistance = childClosestEdgeDistance;
     * newSelectedChildIndex = i;
     * }
     * }
     * int newPos = mFirstPosition + newSelectedChildIndex;
     * if (newPos != mSelectedPosition) {
     * setSelectedPositionInt(newPos);
     * setNextSelectedPositionInt(newPos);
     * checkSelectionChanged();
     * }
     * }
     */

    /**
     * Creates and positions all views for this Gallery.
     * <p>
     * We layout rarely, most of the time {@link #trackMotionScroll(int)} takes care of repositioning, adding, and
     * removing children.
     * @param delta
     *            Change in the selected position. +1 means the selection is
     *            moving to the right, so views are scrolling to the left. -1
     *            means the selection is moving to the left.
     */
    @Override
    void layout(int delta, boolean animate) {
        //TODO
        mIsRtl = /* (getLayoutDirection() == LAYOUT_DIRECTION_RTL) */false;

        int childrenLeft = mSpinnerPadding.left;
        int childrenWidth = getRight() - getLeft() - mSpinnerPadding.left - mSpinnerPadding.right;

        if (mDataChanged) {
            handleDataChanged();
        }

        // Handle an empty gallery by removing all views.
        if (mItemCount == 0) {
            resetList();
            return;
        }

        // Update to the new selected position.
        if (mNextSelectedPosition >= 0) {
            setSelectedPositionInt(mNextSelectedPosition);
        }

        View focusLayoutRestoreView = null;
        // reset the focus restoration
        View focusLayoutRestoreDirectChild = null;

        // take focus back to us temporarily to avoid the eventual
        // call to clear focus when removing the focused child below
        // from messing things up when ViewAncestor assigns focus back
        // to someone else
        final View focusedChild = getFocusedChild();
        if (focusedChild != null) {
            // we can remember the focused view to restore after relayout if the
            // data hasn't changed, or if the focused position is a header or footer
            if (!mDataChanged /* || isDirectChildHeaderOrFooter(focusedChild) */) {
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

        // All views go in recycler while we are in layout
        recycleAllViews();

        // Clear out old views
        //removeAllViewsInLayout();
        detachAllViewsFromParent();

        /*
         * These will be used to give initial positions to views entering the
         * gallery as we scroll
         */
        mRightMost = 0;
        mLeftMost = 0;

        // Make selected view and center it

        /*
         * mFirstPosition will be decreased as we add views to the left later
         * on. The 0 for x will be offset in a couple lines down.
         */

        //        mFirstPosition = mSelectedPosition;
        //TODO need be fix 

        //        View sel = makeAndAddView(mSelectedPosition, 0, true, 0, true);
        View sel = null;

        // Put the selected child in the center
        //        int selectedOffset = childrenLeft + (childrenWidth / 2) - (sel.getWidth() / 2);
        //        sel.offsetLeftAndRight(selectedOffset);

        //        fillToGalleryRight();
        //        fillToGalleryLeft();
        if (mIsRtl) {

        } else {
            sel = fillFromLeft(childrenLeft);
        }

        if (sel != null && gainFocus) {
            // the current selected item should get focus if items
            // are focusable
            if (mItemsCanFocus && hasFocus() && !sel.hasFocus()) {
                final boolean focusWasTaken = (sel == focusLayoutRestoreDirectChild && focusLayoutRestoreView != null && focusLayoutRestoreView
                        .requestFocus()) || sel.requestFocus();
                if (!focusWasTaken) {
                    // selected item didn't take focus, fine, but still want
                    // to make sure something else outside of the selected view
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
            mSelectedLeft = sel.getLeft();
        } else {
            mSelectorRect.setEmpty();
        }

        // tell focus view we are done mucking with it, if it is still in
        // our view hierarchy.
        if (focusLayoutRestoreView != null && focusLayoutRestoreView.getWindowToken() != null) {
            focusLayoutRestoreView.onFinishTemporaryDetach();
        }

        //        // Flush any cached views that did not get reused above
        //        mRecycler.clear();

        invalidate();
        checkSelectionChanged();

        mDataChanged = false;
        mNeedSync = false;

        setNextSelectedPositionInt(mSelectedPosition);
        updateSelectedItemMetadata(mSelectedPosition);
    }

    private void fillToGalleryLeft() {
        if (mIsRtl) {
            fillToGalleryLeftRtl();
        } else {
            fillToGalleryLeftLtr();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        if (!mDrawSelectorOnTop) {
            drawSelector(canvas);
        }

        super.dispatchDraw(canvas);

        if (mDrawSelectorOnTop) {
            drawSelector(canvas);
        }
    }

    //    @Override
    //	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
    //		boolean isDraw = super.drawChild(canvas, child, drawingTime);
    //		return isDraw;
    //
    //		// return super.drawChild(canvas, child, drawingTime);
    //	}

    protected void drawSelector(Canvas canvas) {
        if (mSelectorRect != null && !mSelectorRect.isEmpty() && (mSelector != null)) {
            Rect selectorRect = new Rect(mExactlyUserSelectedRect != null ? mExactlyUserSelectedRect : mSelectorRect);
            mSelector.setBounds(selectorRect);
            mSelector.draw(canvas);
        }
    }

    private void fillToGalleryLeftRtl() {
        int itemSpacing = mSpacing;
        int galleryLeft = getPaddingLeft();
        int numChildren = getChildCount();
        int numItems = mItemCount;

        // Set state for initial iteration
        View prevIterationView = getChildAt(numChildren - 1);
        int curPosition;
        int curRightEdge;

        if (prevIterationView != null) {
            curPosition = mFirstPosition + numChildren;
            curRightEdge = prevIterationView.getLeft() - itemSpacing;
        } else {
            // No children available!
            mFirstPosition = curPosition = mItemCount - 1;
            curRightEdge = getRight() - getLeft() - getPaddingRight();
            mShouldStopFling = true;
        }

        while (curRightEdge > galleryLeft && curPosition < mItemCount) {
            prevIterationView = makeAndAddView(curPosition, curRightEdge, false, mSpinnerPadding.top,
                    mSelectedPosition == curPosition);

            // Set state for next iteration
            curRightEdge = prevIterationView.getLeft() - itemSpacing;
            curPosition++;
        }
    }

    private void fillToGalleryLeftLtr() {
        int itemSpacing = mSpacing;
        int galleryLeft = getPaddingLeft();

        // Set state for initial iteration
        View prevIterationView = getChildAt(0);
        int curPosition;
        int curRightEdge;

        if (prevIterationView != null) {
            curPosition = mFirstPosition - 1;
            curRightEdge = prevIterationView.getLeft() - itemSpacing;
        } else {
            // No children available!
            curPosition = 0;
            curRightEdge = getRight() - getLeft() - getPaddingRight();
            mShouldStopFling = true;
        }

        while (curRightEdge > galleryLeft && curPosition >= 0) {
            prevIterationView = makeAndAddView(curPosition, curRightEdge, false, mSpinnerPadding.top,
                    mSelectedPosition == curPosition);

            // Remember some state
            mFirstPosition = curPosition;

            // Set state for next iteration
            curRightEdge = prevIterationView.getLeft() - itemSpacing;
            curPosition--;
        }
    }

    private void fillToGalleryRight() {
        if (mIsRtl) {
            fillToGalleryRightRtl();
        } else {
            fillToGalleryRightLtr();
        }
    }

    private void fillToGalleryRightRtl() {
        int itemSpacing = mSpacing;
        int galleryRight = getRight() - getLeft() - getPaddingRight();

        // Set state for initial iteration
        View prevIterationView = getChildAt(0);
        int curPosition;
        int curLeftEdge;

        if (prevIterationView != null) {
            curPosition = mFirstPosition - 1;
            curLeftEdge = prevIterationView.getRight() + itemSpacing;
        } else {
            curPosition = 0;
            curLeftEdge = getPaddingLeft();
            mShouldStopFling = true;
        }

        while (curLeftEdge < galleryRight && curPosition >= 0) {
            prevIterationView = makeAndAddView(curPosition, curLeftEdge, true, mSpinnerPadding.top,
                    mSelectedPosition == curPosition);

            // Remember some state
            mFirstPosition = curPosition;

            // Set state for next iteration
            curLeftEdge = prevIterationView.getRight() + itemSpacing;
            curPosition--;
        }
    }

    private void fillToGalleryRightLtr() {
        int itemSpacing = mSpacing;
        int galleryRight = getRight() - getLeft() - getPaddingRight();
        int numChildren = getChildCount();
        int numItems = mItemCount;

        // Set state for initial iteration
        View prevIterationView = getChildAt(numChildren - 1);
        int curPosition;
        int curLeftEdge;

        if (prevIterationView != null) {
            curPosition = mFirstPosition + numChildren;
            curLeftEdge = prevIterationView.getRight() + itemSpacing;
        } else {
            mFirstPosition = curPosition = mItemCount - 1;
            curLeftEdge = getPaddingLeft();
            mShouldStopFling = true;
        }

        while (curLeftEdge < galleryRight && curPosition < numItems) {
            prevIterationView = makeAndAddView(curPosition, curLeftEdge, true, mSpinnerPadding.top,
                    mSelectedPosition == curPosition);

            // Set state for next iteration
            curLeftEdge = prevIterationView.getRight() + itemSpacing;
            curPosition++;
        }
    }

    /**
     * Fills the list from pos left to the end of the list view.
     * @param pos
     *            The first position to put in the list
     * @param nextTop
     *            The location where the left of the item associated with pos
     *            should be drawn
     * @return The view that is currently selected, if it happens to be in the
     *         range that we draw.
     */
    private View fillLeft(int pos, int nextLeft) {
        View selectedView = null;

        int end = getRight() - getLeft();

        //TODO by lawin
        //        if ((mGroupFlags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
        //            end -= mSpinnerPadding.right;
        //        }

        while (nextLeft < end && pos < mItemCount) {
            // is this the selected item?
            boolean selected = pos == mSelectedPosition;
            View child = makeAndAddView(pos, nextLeft, true, mSpinnerPadding.top, selected);

            //            if(this instanceof PokerGroupView && child != null){
            //            	//PokerGroupView max width is dependent
            //            	end = getMaxWidth(child);
            //            }
            nextLeft = child.getRight() + mDividerWidth;
            if (selected) {
                selectedView = child;
            }
            pos++;
        }
        //
        //        setVisibleRangeHint(mFirstPosition, mFirstPosition + getChildCount() - 1);
        return selectedView;
    }

    protected int getMaxWidth(View child) {
        return 0;
    }

    /**
     * Fills the list from left to right, starting with mFirstPosition
     * @param nextLeft
     *            The location where the left of the first item should be
     *            drawn
     * @return The view that is currently selected
     */
    private View fillFromLeft(int nextLeft) {

        mFirstPosition = Math.min(mFirstPosition, mSelectedPosition);
        mFirstPosition = Math.min(mFirstPosition, mItemCount - 1);
        if (mFirstPosition < 0) {
            mFirstPosition = 0;
        }

        /**
         * 计算开始显示的position
         */
        int end = getRight() - getLeft();
        if (end > 0) {
            int startPos = mFirstPosition;
            int pos = startPos;
            int endPos = pos;
            boolean selected = false;
            while (endPos < mItemCount && !selected) {
                pos = startPos;
                int cheildrenWidth = 0;

                while (cheildrenWidth < end && pos < mItemCount && !selected) {
                    selected = pos == mSelectedPosition;
                    cheildrenWidth += obtainChildViewMeasuredWidth(pos) + mDividerWidth;
                    pos++;
                }
                endPos = pos;
                if (endPos < mItemCount && !selected) {
                    startPos++;
                }
            }
            mFirstPosition = startPos;
        }
        return fillLeft(mFirstPosition, nextLeft);
    }

    private int obtainChildViewMeasuredWidth(int position) {

        //        View scrapView = mRecycler.getScrapView(position); //调用这个会导致程序焦点不正常
        View child = mAdapter.getView(position, null, this);

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

        measureScrapChild(child, position, mHeightMeasureSpec);

        return child.getMeasuredWidth();
    }

    protected void measureScrapChild(View child, int position, int heightMeasureSpec) {
        AbsSpinner.LayoutParams p = (AbsSpinner.LayoutParams) child.getLayoutParams();
        if (p == null) {
            p = new AbsSpinner.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            child.setLayoutParams(p);
        }

        p.viewType = mAdapter.getItemViewType(position);

        int childHeightSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec, mSpinnerPadding.top
                + mSpinnerPadding.right, p.height);
        int lpWidth = p.width;
        int childWidthSpec;
        if (lpWidth > 0) {
            childWidthSpec = View.MeasureSpec.makeMeasureSpec(lpWidth, View.MeasureSpec.EXACTLY);
        } else {
            childWidthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * Fills the list from pos right to the top of the list view.
     * @param pos
     *            The first position to put in the list
     * @param nextBottom
     *            The location where the right of the item associated
     *            with pos should be drawn
     * @return The view that is currently selected
     */
    private View fillRight(int pos, int nextRight) {
        View selectedView = null;

        int end = 0;
        //TODO by lawin
        //        if ((mGroupFlags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
        //            end = mListPadding.top;
        //        }

        while (nextRight > end && pos >= 0) {
            // is this the selected item?
            boolean selected = pos == mSelectedPosition;
            View child = makeAndAddView(pos, nextRight, false, mSpinnerPadding.top, selected);
            nextRight = child.getLeft() - mDividerWidth;
            if (selected) {
                selectedView = child;
            }
            pos--;
        }

        mFirstPosition = pos + 1;
        //        setVisibleRangeHint(mFirstPosition, mFirstPosition + getChildCount() - 1);
        return selectedView;
    }

    /**
     * Obtain the view and add it to our list of children. The view can be made
     * fresh, converted from an unused view, or used as is if it was in the
     * recycle bin.
     * @param position
     *            Logical position in the list
     * @param x
     *            left or right edge of the view to add
     * @param flow
     *            If flow is true, align left edge to x. If false, align right
     *            edge to x.
     * @param childrenTop
     *            Top edge where children should be positioned
     * @param selected
     *            Is this position selected?
     * @return View that was added
     */
    private View makeAndAddView(int position, int x, boolean flow, int childrenTop, boolean selected) {
        View child;

        if (!mDataChanged) {
            // Try to use an existing view for this position
            //TODO
            //            child = mRecycler.getActiveView(position);
            child = mRecycler.getScrapView(position);
            if (child != null) {
                // Found it -- we're using an existing child
                // This just needs to be positioned
                setupChild(child, position, x, flow, childrenTop, selected, true);

                return child;
            }
        }

        // Make a new view for this position, or convert an unused view if possible
        child = obtainView(position, mIsScrap);

        // This needs to be positioned and measured
        setupChild(child, position, x, flow, childrenTop, selected, mIsScrap[0]);

        return child;
    }

    int getGravityHeightAnchor(View child) {
        int heightAnchor = 0;
        switch (mGravity) {
            case Gravity.CENTER_VERTICAL:
                int childHeight = child.getMeasuredHeight();
                heightAnchor = mGravityHeightAnchor - (childHeight >> 1);
                break;
        }
        return heightAnchor;
    }

    //    /**
    //     * Obtain a view, either by pulling an existing view from the recycler or by
    //     * getting a new one from the adapter. If we are animating, make sure there
    //     * is enough information in the view's layout parameters to animate from the
    //     * old to new positions.
    //     * 
    //     * @param position Position in the gallery for the view to obtain
    //     * @param offset Offset from the selected position
    //     * @param x X-coordinate indicating where this view should be placed. This
    //     *        will either be the left or right edge of the view, depending on
    //     *        the fromLeft parameter
    //     * @param fromLeft Are we positioning views based on the left edge? (i.e.,
    //     *        building from left to right)?
    //     * @return A view that has been added to the gallery
    //     */
    //    private View makeAndAddView(int position, int offset, int x, boolean fromLeft) {
    //
    //        View child;
    //        if (!mDataChanged) {
    //            child = mRecycler.getScrapView(position);
    //            if (child != null) {
    //                // Can reuse an existing view
    //                int childLeft = child.getLeft();
    //                
    //                // Remember left and right edges of where views have been placed
    //                mRightMost = Math.max(mRightMost, childLeft 
    //                        + child.getMeasuredWidth());
    //                mLeftMost = Math.min(mLeftMost, childLeft);
    //
    //                // Position the view
    //                setUpChild(child, offset, x, fromLeft);
    //
    //                return child;
    //            }
    //        }
    //
    //        // Nothing found in the recycler -- ask the adapter for a view
    //        child = mAdapter.getView(position, null, this);
    //
    //        // Position the view
    //        setUpChild(child, offset, x, fromLeft);
    //
    //        return child;
    //    }

    /**
     * Get a view and have it show the data associated with the specified
     * position. This is called when we have already discovered that the view is
     * not available for reuse in the recycle bin. The only choices left are
     * converting an old view or making a new one.
     * @param position
     *            The position to display
     * @param isScrap
     *            Array of at least 1 boolean, the first entry will become true if
     *            the returned view was taken from the scrap heap, false if otherwise.
     * @return A view displaying the data associated with the specified position
     */
    View obtainView(int position, boolean[] isScrap) {
        isScrap[0] = false;
        View scrapView;

        //        scrapView = mRecycler.getTransientStateView(position);
        //        if (scrapView != null) {
        //            return scrapView;
        //        }

        scrapView = mRecycler.getScrapView(position);

        View child;
        if (scrapView != null) {
            child = mAdapter.getView(position, scrapView, this);

            //            if (child.getImportantForAccessibility() == IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
            //                child.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
            //            }

            if (child != scrapView) {
                mRecycler.addScrapView(position, scrapView);
                //                if (mCacheColorHint != 0) {
                //                    child.setDrawingCacheBackgroundColor(mCacheColorHint);
                //                }
            } else {
                isScrap[0] = true;
                //TODO by lawin
                //                child.dispatchFinishTemporaryDetach();
            }
        } else {
            child = mAdapter.getView(position, null, this);

            //            if (child.getImportantForAccessibility() == IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
            //                child.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
            //            }

            //            if (mCacheColorHint != 0) {
            //                child.setDrawingCacheBackgroundColor(mCacheColorHint);
            //            }
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

        //        if (AccessibilityManager.getInstance(mContext).isEnabled()) {
        //            if (mAccessibilityDelegate == null) {
        //                mAccessibilityDelegate = new ListItemAccessibilityDelegate();
        //            }
        //            if (child.getAccessibilityDelegate() == null) {
        //                child.setAccessibilityDelegate(mAccessibilityDelegate);
        //            }
        //        }

        return child;
    }

    //    /**
    //     * Helper for makeAndAddView to set the position of a view and fill out its
    //     * layout parameters.
    //     * 
    //     * @param child The view to position
    //     * @param offset Offset from the selected position
    //     * @param x X-coordinate indicating where this view should be placed. This
    //     *        will either be the left or right edge of the view, depending on
    //     *        the fromLeft parameter
    //     * @param fromLeft Are we positioning views based on the left edge? (i.e.,
    //     *        building from left to right)?
    //     */
    //    private void setUpChild(View child, int offset, int x, boolean fromLeft) {
    //    	Log.d("lawin", "----------setUpChild = " + offset + " , x = " + x);
    //
    //        // Respect layout params that are already in the view. Otherwise
    //        // make some up...
    //        AbsHorizontalListView.LayoutParams lp = (AbsHorizontalListView.LayoutParams) child.getLayoutParams();
    //        if (lp == null) {
    //            lp = (AbsHorizontalListView.LayoutParams) generateDefaultLayoutParams();
    //        }
    //
    //        addViewInLayout(child, fromLeft != mIsRtl ? -1 : 0, lp);
    //
    //        child.setSelected(offset == 0);
    //
    //        // Get measure specs
    //        int childHeightSpec = ViewGroup.getChildMeasureSpec(mHeightMeasureSpec,
    //                mSpinnerPadding.top + mSpinnerPadding.bottom, lp.height);
    //        int childWidthSpec = ViewGroup.getChildMeasureSpec(mWidthMeasureSpec,
    //                mSpinnerPadding.left + mSpinnerPadding.right, lp.width);
    //
    //        // Measure child
    //        child.measure(childWidthSpec, childHeightSpec);
    //
    //        int childLeft;
    //        int childRight;
    //
    //        // Position vertically based on gravity setting
    //        int childTop = calculateTop(child, true);
    //        int childBottom = childTop + child.getMeasuredHeight();
    //
    //        int width = child.getMeasuredWidth();
    //        if (fromLeft) {
    //            childLeft = x;
    //            childRight = childLeft + width;
    //        } else {
    //            childLeft = x - width;
    //            childRight = x;
    //        }
    //
    //        child.layout(childLeft, childTop, childRight, childBottom);
    //    }

    /**
     * Figure out vertical placement based on mGravity
     * @param child
     *            Child to place
     * @return Where the top of the child should be
     */
    private int calculateTop(View child, boolean duringLayout) {
        int myHeight = duringLayout ? getMeasuredHeight() : getHeight();
        int childHeight = duringLayout ? child.getMeasuredHeight() : child.getHeight();

        int childTop = 0;

        switch (mGravity) {
            case Gravity.TOP:
                childTop = mSpinnerPadding.top;
                break;
            case Gravity.CENTER_VERTICAL:
                int availableSpace = myHeight - mSpinnerPadding.bottom - mSpinnerPadding.top - childHeight;
                childTop = mSpinnerPadding.top + (availableSpace / 2);
                break;
            case Gravity.BOTTOM:
                childTop = myHeight - mSpinnerPadding.bottom - childHeight;
                break;
        }
        return childTop;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Give everything to the gesture detector
        boolean retValue = mGestureDetector.onTouchEvent(event);

        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            // Helper method for lifted finger
            onUp();
        } else if (action == MotionEvent.ACTION_CANCEL) {
            onCancel();
        }

        return retValue;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        if (mDownTouchPosition >= 0) {
            boolean isNext = mDownTouchPosition > mSelectedPosition;
            // An item tap should make it selected, so scroll to this child.
            scrollToChild(mDownTouchPosition - mFirstPosition, isNext);

            // Also pass the click so the client knows, if it wants to.
            if (mShouldCallbackOnUnselectedItemClick || mDownTouchPosition == mSelectedPosition) {
                performItemClick(mDownTouchView, mDownTouchPosition, mAdapter.getItemId(mDownTouchPosition));
            }

            return true;
        }

        return false;
    }

    //		@Override
    //	public boolean onSingleTapUp(MotionEvent e) {
    //
    //		if (mDownTouchPosition >= 0) {
    //			boolean isNext = mDownTouchPosition > mSelectedPosition;
    //			// An item tap should make it selected, so scroll to this child.
    //			scrollToChild((mDownTouchPosition - mFirstPosition), isNext);
    //
    //			// Also pass the click so the client knows, if it wants to.
    //			if (mShouldCallbackOnUnselectedItemClick
    //					|| mDownTouchPosition == mSelectedPosition) {
    //				performItemClick(mDownTouchView, mDownTouchPosition,
    //						mAdapter.getItemId(mDownTouchPosition));
    //			}
    //
    //			return true;
    //		}
    //
    //		return false;
    //	}

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        if (!mShouldCallbackDuringFling) {
            // We want to suppress selection changes

            // Remove any future code to set mSuppressSelectionChanged = false
            removeCallbacks(mDisableSuppressSelectionChangedRunnable);

            // This will get reset once we scroll into slots
            if (!mSuppressSelectionChanged)
                mSuppressSelectionChanged = true;
        }

        // Fling the gallery!
        mFlingRunnable.startUsingVelocity((int) -velocityX);

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        if (localLOGV)
            AppDebug.v(TAG, String.valueOf(e2.getX() - e1.getX()));

        /*
         * Now's a good time to tell our parent to stop intercepting our events!
         * The user has moved more than the slop amount, since GestureDetector
         * ensures this before calling this method. Also, if a parent is more
         * interested in this touch's events than we are, it would have
         * intercepted them by now (for example, we can assume when a Gallery is
         * in the ListView, a vertical scroll would not end up in this method
         * since a ListView would have intercepted it by now).
         */
        getParent().requestDisallowInterceptTouchEvent(true);

        // As the user scrolls, we want to callback selection changes so related-
        // info on the screen is up-to-date with the gallery's selection
        if (!mShouldCallbackDuringFling) {
            if (mIsFirstScroll) {
                /*
                 * We're not notifying the client of selection changes during
                 * the fling, and this scroll could possibly be a fling. Don't
                 * do selection changes until we're sure it is not a fling.
                 */
                if (!mSuppressSelectionChanged)
                    mSuppressSelectionChanged = true;
                postDelayed(mDisableSuppressSelectionChangedRunnable, SCROLL_TO_FLING_UNCERTAINTY_TIMEOUT);
            }
        } else {
            if (mSuppressSelectionChanged)
                mSuppressSelectionChanged = false;
        }

        // Track the motion
        trackMotionScroll(-1 * (int) distanceX);

        mIsFirstScroll = false;
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {

        // Kill any existing fling/scroll
        mFlingRunnable.stop(false);

        // Get the item's view that was touched
        mDownTouchPosition = pointToPosition((int) e.getX(), (int) e.getY());

        if (mDownTouchPosition >= 0) {
            mDownTouchView = getChildAt(mDownTouchPosition - mFirstPosition);
            mDownTouchView.setPressed(true);
        }

        // Reset the multiple-scroll tracking state
        mIsFirstScroll = true;

        // Must return true to get matching events for this down event.
        return true;
    }

    /**
     * Called when a touch event's action is MotionEvent.ACTION_UP.
     */
    void onUp() {

        if (mFlingRunnable.mScroller.isFinished()) {
            scrollIntoSlots();
        }

        dispatchUnpress();
    }

    /**
     * Called when a touch event's action is MotionEvent.ACTION_CANCEL.
     */
    void onCancel() {
        onUp();
    }

    @Override
    public void onLongPress(MotionEvent e) {

        if (mDownTouchPosition < 0) {
            return;
        }

        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        long id = getItemIdAtPosition(mDownTouchPosition);
        dispatchLongPress(mDownTouchView, mDownTouchPosition, id);
    }

    // Unused methods from GestureDetector.OnGestureListener below

    @Override
    public void onShowPress(MotionEvent e) {
    }

    // Unused methods from GestureDetector.OnGestureListener above

    private void dispatchPress(View child) {

        if (child != null) {
            child.setPressed(true);
        }

        setPressed(true);
    }

    private void dispatchUnpress() {

        for (int i = getChildCount() - 1; i >= 0; i--) {
            getChildAt(i).setPressed(false);
        }

        setPressed(false);
    }

    @Override
    public void dispatchSetSelected(boolean selected) {
        /*
         * We don't want to pass the selected state given from its parent to its
         * children since this widget itself has a selected state to give to its
         * children.
         */
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {

        // Show the pressed state on the selected child
        if (mSelectedChild != null) {
            mSelectedChild.setPressed(pressed);
        }
    }

    @Override
    protected ContextMenuInfo getContextMenuInfo() {
        return mContextMenuInfo;
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

    @Override
    public boolean showContextMenu() {

        if (isPressed() && mSelectedPosition >= 0) {
            int index = mSelectedPosition - mFirstPosition;
            View v = getChildAt(index);
            return dispatchLongPress(v, mSelectedPosition, mSelectedRowId);
        }

        return false;
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Gallery steals all key events
        return event.dispatch(this, null, null);
    }

    /**
     * Handles left, right, and clicking
     * @see android.view.View#onKeyDown
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAdapter == null || !mIsAttached) {
            return false;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (movePrevious()) {
                    playSoundEffect(SoundEffectConstants.NAVIGATION_LEFT);
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (moveNext()) {
                    playSoundEffect(SoundEffectConstants.NAVIGATION_RIGHT);
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                mReceivedInvokeKeyDown = true;
                // fallthrough to default handling
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Sets the selected item and positions the selection y pixels from the top edge
     * of the ListView. (If in touch mode, the item will not be selected but it will
     * still be positioned appropriately.)
     * @param position
     *            Index (starting at 0) of the data item to be selected.
     * @param x
     *            The distance from the left edge of the ListView (plus padding) that the
     *            item will be positioned.
     */
    public void setSelectionFromTop(int position, int x) {
        if (mAdapter == null) {
            return;
        }

        if (!isInTouchMode()) {
            position = lookForSelectablePosition(position, true);
            if (position >= 0) {
                setNextSelectedPositionInt(position);
            }
        } else {
            //            mResurrectToPosition = position;
        }

        if (position >= 0) {
            //            mLayoutMode = LAYOUT_SPECIFIC;
            //            mSpecificTop = mSpinnerPadding.left + x;

            if (mNeedSync) {
                mSyncPosition = position;
                mSyncRowId = mAdapter.getItemId(position);
            }

            //            if (mPositionScroller != null) {
            //                mPositionScroller.stop();
            //            }
            requestLayout();
        }
    }

    /**
     * @return The maximum amount a list view will scroll in response to
     *         an arrow event.
     */
    /**
     * When arrow scrolling, ListView will never scroll more than this factor
     * times the height of the list.
     */
    private static final float MAX_SCROLL_FACTOR = 0.33f;

    public int getMaxScrollAmount() {
        return (int) (MAX_SCROLL_FACTOR * (getRight() - getLeft()));
    }

    /**
     * Handle an arrow scroll going up or down. Take into account whether items are selectable,
     * whether there are focusable items etc.
     * @param direction
     *            Either {@link android.view.View#FOCUS_UP} or {@link android.view.View#FOCUS_DOWN}.
     * @return Whether any scrolling, selection or focus change occured.
     */
    boolean arrowScrollImpl(int direction) {
        if (getChildCount() <= 0) {
            return false;
        }

        View selectedView = getSelectedView();
        int selectedPos = mSelectedPosition;

        int nextSelectedPosition = lookForSelectablePositionOnScreen(direction);
        int amountToScroll = amountToScroll(direction, nextSelectedPosition);

        // if we are moving focus, we may OVERRIDE the default behavior
        final ArrowScrollFocusResult focusResult = mItemsCanFocus ? arrowScrollFocused(direction) : null;
        if (focusResult != null) {
            nextSelectedPosition = focusResult.getSelectedPosition();
            amountToScroll = focusResult.getAmountToScroll();
        }

        boolean needToRedraw = focusResult != null;
        if (nextSelectedPosition != INVALID_POSITION) {
            handleNewSelectionChange(selectedView, direction, nextSelectedPosition, focusResult != null);
            setSelectedPositionInt(nextSelectedPosition);
            setNextSelectedPositionInt(nextSelectedPosition);
            selectedView = getSelectedView();
            selectedPos = nextSelectedPosition;
            if (mItemsCanFocus && focusResult == null) {
                // there was no new view found to take focus, make sure we
                // don't leave focus with the old selection
                final View focused = getFocusedChild();
                if (focused != null) {
                    focused.clearFocus();
                }
            }
            needToRedraw = true;
            checkSelectionChanged();
        }

        if (amountToScroll > 0) {
            scrollListItemsBy((direction == View.FOCUS_LEFT) ? amountToScroll : -amountToScroll);
            needToRedraw = true;
        }

        // if we didn't find a new focusable, make sure any existing focused
        // item that was panned off screen gives up focus.
        if (mItemsCanFocus && (focusResult == null) && selectedView != null && selectedView.hasFocus()) {
            final View focused = selectedView.findFocus();
            if (!isViewAncestorOf(focused, this) || distanceToView(focused) > 0) {
                focused.clearFocus();
            }
        }

        // if  the current selection is panned off, we need to remove the selection
        if (nextSelectedPosition == INVALID_POSITION && selectedView != null && !isViewAncestorOf(selectedView, this)) {
            selectedView = null;
            hideSelector();

            // but we don't want to set the ressurect position (that would make subsequent
            // unhandled key events bring back the item we just scrolled off!)
            //            mResurrectToPosition = INVALID_POSITION;
        }

        if (needToRedraw) {
            //
            if (selectedView != null && amountToScroll <= 0) {
                positionSelector(selectedPos, selectedView);
                mSelectedLeft = selectedView.getLeft();
            } else {
                //selector rect changed in flingRunable
            }
            if (!awakenScrollBars()) {
                invalidate();
            }
            //            invokeOnItemScrollListener();
            return true;
        }

        return false;
    }

    @Override
    void checkSelectionChanged() {
        if ((mSelectedPosition != mOldSelectedPosition) || (mSelectedRowId != mOldSelectedRowId)) {
            onSelectionChanged(mOldSelectedPosition, mSelectedPosition, mOldSelectedRowId, mSelectedRowId);
            selectionChanged();
            if (mSelectedPosition != INVALID_POSITION) {
                mOldSelectedPosition = mSelectedPosition;
                mOldSelectedRowId = mSelectedRowId;
            }
        }
    }

    void onSelectionChanged(int oldSelectedPosition, int newSelectedPosition, long oldSelectedRowId,
            long newSelectedRowId) {

    }

    /**
     * Controls whether the selection highlight drawable should be drawn on top of the item or
     * behind it.
     * @param onTop
     *            If true, the selector will be drawn on the item it is highlighting. The default
     *            is false.
     * @attr ref android.R.styleable#AbsListView_drawSelectorOnTop
     */
    public void setDrawSelectorOnTop(boolean onTop) {
        mDrawSelectorOnTop = onTop;
    }

    /**
     * set exactly selected rect width & height
     * @param width
     *            the selected rect width
     * @param height
     *            the selected rect height
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
     * Scroll the children by amount, adding a view at the end and removing
     * views that fall off as necessary.
     * @param amount
     *            The amount (positive or negative) to scroll.
     */
    private void scrollListItemsBy(int amount) {
        //        offsetChildrenLeftAndRight(amount);
        mFlingRunnable.startUsingDistance(amount);
        final int listRight = getWidth() - mSpinnerPadding.right;
        final int listLeft = mSpinnerPadding.left;
        final AbsSpinner.RecycleBin recycleBin = mRecycler;

        if (amount < 0) {
            // shifted items up

            // may need to pan views into the bottom space
            int numChildren = getChildCount();
            View last = getChildAt(numChildren - 1);
            while (last.getRight() < listRight) {
                final int lastVisiblePosition = mFirstPosition + numChildren - 1;
                if (lastVisiblePosition < mItemCount - 1) {
                    last = addViewBackward(last, lastVisiblePosition);
                    numChildren++;
                } else {
                    break;
                }
            }

            // may have brought in the last child of the list that is skinnier
            // than the fading edge, thereby leaving space at the end.  need
            // to shift back
            if (last.getRight() < listRight) {
                offsetChildrenLeftAndRight(listRight - last.getRight());
            }

            // top views may be panned off screen
            View first = getChildAt(0);
            while (first.getRight() < listLeft) {
                AbsSpinner.LayoutParams layoutParams = (LayoutParams) first.getLayoutParams();
                if (recycleBin.shouldRecycleViewType(layoutParams.viewType)) {
                    detachViewFromParent(first);
                    recycleBin.addScrapView(mFirstPosition, first);
                } else {
                    removeViewInLayout(first);
                }
                first = getChildAt(0);
                mFirstPosition++;
            }
        } else {
            // shifted items down
            View first = getChildAt(0);

            // may need to pan views into top
            while ((first.getLeft() > listLeft) && (mFirstPosition > 0)) {
                first = addViewFoward(first, mFirstPosition);
                mFirstPosition--;
            }

            // may have brought the very first child of the list in too far and
            // need to shift it back
            if (first.getLeft() > listLeft) {
                offsetChildrenLeftAndRight(listLeft - first.getLeft());
            }

            int lastIndex = getChildCount() - 1;
            View last = getChildAt(lastIndex);

            // bottom view may be panned off screen
            while (last.getLeft() > listRight) {
                AbsSpinner.LayoutParams layoutParams = (LayoutParams) last.getLayoutParams();
                if (recycleBin.shouldRecycleViewType(layoutParams.viewType)) {
                    detachViewFromParent(last);
                    recycleBin.addScrapView(mFirstPosition + lastIndex, last);
                } else {
                    removeViewInLayout(last);
                }
                last = getChildAt(--lastIndex);
            }
        }
    }

    View addViewFoward(View theView, int position) {
        int abovePosition = position - 1;
        View view = obtainView(abovePosition, mIsScrap);
        int edgeOfNewChild = theView.getLeft() - mDividerWidth;
        setupChild(view, abovePosition, edgeOfNewChild, false, mSpinnerPadding.left, false, mIsScrap[0]);
        return view;
    }

    View addViewBackward(View theView, int position) {
        int belowPosition = position + 1;
        View view = obtainView(belowPosition, mIsScrap);
        int edgeOfNewChild = theView.getRight() + mDividerWidth;
        setupChild(view, belowPosition, edgeOfNewChild, true, mSpinnerPadding.left, false, mIsScrap[0]);
        return view;
    }

    /**
     * Add a view as a child and make sure it is measured (if necessary) and
     * positioned properly.
     * @param child
     *            The view to add
     * @param position
     *            The position of this child
     * @param x
     *            The x position relative to which this view will be positioned
     * @param flowRight
     *            If true, align left edge to x. If false, align right
     *            edge to x.
     * @param childrenTop
     *            top edge where children should be positioned
     * @param selected
     *            Is this position selected?
     * @param recycled
     *            Has this view been pulled from the recycle bin? If so it
     *            does not need to be remeasured.
     */
    private void setupChild(View child, int position, int x, boolean flowRight, int childrenTop, boolean selected,
                            boolean recycled) {
        final boolean isSelected = selected && shouldShowSelector();
        final boolean updateChildSelected = isSelected != child.isSelected();
        //TODO by lawin
        //        final int mode = mTouchMode;
        //        final boolean isPressed = mode > TOUCH_MODE_DOWN && mode < TOUCH_MODE_SCROLL &&
        //                mMotionPosition == position;
        //        final boolean updateChildPressed = isPressed != child.isPressed();
        final boolean needToMeasure = !recycled || updateChildSelected || child.isLayoutRequested();

        // Respect layout params that are already in the view. Otherwise make some up...
        // noinspection unchecked
        AbsSpinner.LayoutParams p = (AbsSpinner.LayoutParams) child.getLayoutParams();
        if (p == null) {
            p = (AbsSpinner.LayoutParams) generateDefaultLayoutParams();
        }
        p.viewType = mAdapter.getItemViewType(position);

        if ((recycled && !p.forceAdd) /*
                                       * || (p.recycledHeaderFooter &&
                                       * p.viewType == AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER)
                                       */) {
            attachViewToParent(child, flowRight ? -1 : 0, p);
        } else {
            p.forceAdd = false;
            //            if (p.viewType == AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
            //                p.recycledHeaderFooter = true;
            //            }
            addViewInLayout(child, flowRight ? -1 : 0, p, true);
        }

        if (updateChildSelected) {
            child.setSelected(isSelected);
        }

        //        if (updateChildPressed) {
        //            child.setPressed(isPressed);
        //        }

        //TODO by lawin
        //        if (mChoiceMode != CHOICE_MODE_NONE && mCheckStates != null) {
        //            if (child instanceof Checkable) {
        //                ((Checkable) child).setChecked(mCheckStates.get(position));
        //            } else if (getContext().getApplicationInfo().targetSdkVersion
        //                    >= android.os.Build.VERSION_CODES.HONEYCOMB) {
        //                child.setActivated(mCheckStates.get(position));
        //            }
        //        }
        if (needToMeasure) {
            int childHeightSpec = ViewGroup.getChildMeasureSpec(mHeightMeasureSpec, mSpinnerPadding.top
                    + mSpinnerPadding.bottom, p.height);
            int lpWidth = p.width;
            int childWidthSpec;
            if (lpWidth > 0) {
                childWidthSpec = View.MeasureSpec.makeMeasureSpec(lpWidth, View.MeasureSpec.EXACTLY);
                ;
            } else {
                childWidthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            }
            child.measure(childWidthSpec, childHeightSpec);
        } else {
            cleanupLayoutState(child);
        }

        final int w = child.getMeasuredWidth();
        final int h = child.getMeasuredHeight();

        final int childLeft = flowRight ? x : x - w;
        childrenTop += getGravityHeightAnchor(child);

        if (needToMeasure) {
            final int childRight = childLeft + w;
            final int childBottom = childrenTop + h;
            child.layout(childLeft, childrenTop, childRight, childBottom);
        } else {
            child.offsetLeftAndRight(childLeft - child.getLeft());
            child.offsetTopAndBottom(childrenTop - child.getTop());
        }

        //TODO
        //        if (mCachingStarted && !child.isDrawingCacheEnabled()) {
        //            child.setDrawingCacheEnabled(true);
        //        }

        if (recycled && (((AbsSpinner.LayoutParams) child.getLayoutParams()).scrappedFromPosition) != position) {
            child.jumpDrawablesToCurrentState();
        }

    }

    /**
     * When selection changes, it is possible that the previously selected or the
     * next selected item will change its size. If so, we need to offset some folks,
     * and re-layout the items as appropriate.
     * @param selectedView
     *            The currently selected view (before changing selection).
     *            should be <code>null</code> if there was no previous selection.
     * @param direction
     *            Either {@link android.view.View#FOCUS_LEFT} or {@link android.view.View#FOCUS_RIGHT}.
     * @param newSelectedPosition
     *            The position of the next selection.
     * @param newFocusAssigned
     *            whether new focus was assigned. This matters because
     *            when something has focus, we don't want to show selection (ugh).
     */
    void handleNewSelectionChange(View selectedView, int direction, int newSelectedPosition, boolean newFocusAssigned) {
        if (newSelectedPosition == INVALID_POSITION) {
            throw new IllegalArgumentException("newSelectedPosition needs to be valid");
        }

        // whether or not we are moving right or left, we want to preserve the
        // left of whatever view is on left:
        // - moving right: the view that had selection
        // - moving left: the view that is getting selection
        View leftView;
        View rightView;
        int leftViewIndex, rightViewIndex;
        boolean leftSelected = false;
        final int selectedIndex = mSelectedPosition - mFirstPosition;
        final int nextSelectedIndex = newSelectedPosition - mFirstPosition;
        if (direction == View.FOCUS_LEFT) {
            leftViewIndex = nextSelectedIndex;
            rightViewIndex = selectedIndex;
            leftView = getChildAt(leftViewIndex);
            rightView = selectedView;
            leftSelected = true;
        } else {
            leftViewIndex = selectedIndex;
            rightViewIndex = nextSelectedIndex;
            leftView = selectedView;
            rightView = getChildAt(rightViewIndex);
        }

        final int numChildren = getChildCount();

        // start with top view: is it changing size?
        if (leftView != null) {
            leftView.setSelected(!newFocusAssigned && leftSelected);
            measureAndAdjustForward(leftView, leftViewIndex, numChildren);
        }

        // is the bottom view changing size?
        if (rightView != null) {
            rightView.setSelected(!newFocusAssigned && !leftSelected);
            measureAndAdjustForward(rightView, rightViewIndex, numChildren);
        }
    }

    /**
     * Re-measure a child, and if its width changes, lay it out preserving its
     * top, and adjust the children below it appropriately.
     * @param child
     *            The child
     * @param childIndex
     *            The view group index of the child.
     * @param numChildren
     *            The number of children in the view group.
     */
    private void measureAndAdjustForward(View child, int childIndex, int numChildren) {
        int oldWidth = child.getWidth();
        measureItem(child);
        if (child.getMeasuredWidth() != oldWidth) {
            // lay out the view, preserving its top
            relayoutMeasuredItem(child);

            // adjust views below appropriately
            final int widthDelta = child.getMeasuredWidth() - oldWidth;
            for (int i = childIndex + 1; i < numChildren; i++) {
                getChildAt(i).offsetLeftAndRight(widthDelta);
            }
        }
    }

    /**
     * Measure a particular list child.
     * TODO: unify with setUpChild.
     * @param child
     *            The child.
     */
    private void measureItem(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        int childHeightSpec = ViewGroup.getChildMeasureSpec(mHeightMeasureSpec, mSpinnerPadding.top
                + mSpinnerPadding.bottom, p.height);
        int lpWidth = p.width;
        int childWidthSpec;
        if (lpWidth > 0) {
            childWidthSpec = View.MeasureSpec.makeMeasureSpec(lpWidth, View.MeasureSpec.EXACTLY);
        } else {
            childWidthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * Layout a child that has been measured, preserving its top position.
     * TODO: unify with setUpChild.
     * @param child
     *            The child.
     */
    private void relayoutMeasuredItem(View child) {
        final int w = child.getMeasuredWidth();
        final int h = child.getMeasuredHeight();
        final int childLeft = mSpinnerPadding.left;
        final int childRight = childLeft + w;
        final int childTop = child.getTop();
        final int childBottom = childTop + h;
        child.layout(childLeft, childTop, childRight, childBottom);
    }

    /**
     * Determine how much we need to scroll in order to get the next selected view
     * visible, with a fading edge showing below as applicable. The amount is
     * capped at {@link #getMaxScrollAmount()} .
     * @param direction
     *            either {@link android.view.View#FOCUS_UP} or {@link android.view.View#FOCUS_DOWN}.
     * @param nextSelectedPosition
     *            The position of the next selection, or {@link #INVALID_POSITION} if there is no next selectable
     *            position
     * @return The amount to scroll. Note: this is always positive! Direction
     *         needs to be taken into account when actually scrolling.
     */
    private int amountToScroll(int direction, int nextSelectedPosition) {
        final int listRight = getWidth() - mSpinnerPadding.right;
        final int listLeft = mSpinnerPadding.left;

        final int numChildren = getChildCount();

        if (direction == View.FOCUS_RIGHT) {
            int indexToMakeVisible = numChildren - 1;
            if (nextSelectedPosition != INVALID_POSITION) {
                indexToMakeVisible = nextSelectedPosition - mFirstPosition;
            }

            final int positionToMakeVisible = mFirstPosition + indexToMakeVisible;
            final View viewToMakeVisible = getChildAt(indexToMakeVisible);

            int goalRight = listRight;
            if (positionToMakeVisible < mItemCount - 1) {
                goalRight -= getArrowScrollPreviewLength();
            }

            if (viewToMakeVisible.getRight() <= goalRight) {
                // item is fully visible.
                return 0;
            }

            if (nextSelectedPosition != INVALID_POSITION
                    && (goalRight - viewToMakeVisible.getLeft()) >= getMaxScrollAmount()) {
                // item already has enough of it visible, changing selection is good enough
                return 0;
            }

            int amountToScroll = (viewToMakeVisible.getRight() - goalRight);

            if ((mFirstPosition + numChildren) == mItemCount) {
                // last is last in list -> make sure we don't scroll past it
                final int max = getChildAt(numChildren - 1).getRight() - goalRight;
                amountToScroll = Math.min(amountToScroll, max);
            }

            return Math.min(amountToScroll, getMaxScrollAmount());
        } else {
            int indexToMakeVisible = 0;
            if (nextSelectedPosition != INVALID_POSITION) {
                indexToMakeVisible = nextSelectedPosition - mFirstPosition;
            }
            final int positionToMakeVisible = mFirstPosition + indexToMakeVisible;
            final View viewToMakeVisible = getChildAt(indexToMakeVisible);
            int goalLeft = listLeft;
            if (positionToMakeVisible > 0) {
                goalLeft += getArrowScrollPreviewLength();
            }
            if (viewToMakeVisible.getLeft() >= goalLeft) {
                // item is fully visible.
                return 0;
            }

            if (nextSelectedPosition != INVALID_POSITION
                    && (viewToMakeVisible.getLeft() - goalLeft) >= getMaxScrollAmount()) {
                // item already has enough of it visible, changing selection is good enough
                return 0;
            }

            int amountToScroll = (goalLeft - viewToMakeVisible.getLeft());
            if (mFirstPosition == 0) {
                // first is first in list -> make sure we don't scroll past it
                final int max = goalLeft - getChildAt(0).getLeft();
                amountToScroll = Math.min(amountToScroll, max);
            }
            return Math.min(amountToScroll, getMaxScrollAmount());
        }
    }

    /**
     * Determine the distance to the nearest edge of a view in a particular
     * direction.
     * @param descendant
     *            A descendant of this list.
     * @return The distance, or 0 if the nearest edge is already on screen.
     */
    int distanceToView(View descendant) {
        int distance = 0;
        descendant.getDrawingRect(mTempRect);
        offsetDescendantRectToMyCoords(descendant, mTempRect);
        final int listBottom = getBottom() - getTop() - mSpinnerPadding.bottom;
        if (mTempRect.bottom < mSpinnerPadding.top) {
            distance = mSpinnerPadding.top - mTempRect.bottom;
        } else if (mTempRect.top > listBottom) {
            distance = mTempRect.top - listBottom;
        }
        return distance;
    }

    /**
     * Do an arrow scroll based on focus searching. If a new view is
     * given focus, return the selection delta and amount to scroll via
     * an {@link ArrowScrollFocusResult}, otherwise, return null.
     * @param direction
     *            either {@link android.view.View#FOCUS_LEFT} or {@link android.view.View#FOCUS_RIGHT}.
     * @return The result if focus has changed, or <code>null</code>.
     */
    ArrowScrollFocusResult arrowScrollFocused(final int direction) {
        final View selectedView = getSelectedView();
        View newFocus;
        if (selectedView != null && selectedView.hasFocus()) {
            View oldFocus = selectedView.findFocus();
            newFocus = FocusFinder.getInstance().findNextFocus(this, oldFocus, direction);
        } else {
            if (direction == View.FOCUS_RIGHT) {
                final boolean leftFadingEdgeShowing = (mFirstPosition > 0);
                final int listLeft = mSpinnerPadding.left + (leftFadingEdgeShowing ? getArrowScrollPreviewLength() : 0);
                final int xSearchPoint = (selectedView != null && selectedView.getLeft() > listLeft) ? selectedView
                        .getLeft() : listLeft;
                mTempRect.set(xSearchPoint, 0, xSearchPoint, 0);
            } else {
                final boolean rightFadingEdgeShowing = (mFirstPosition + getChildCount() - 1) < mItemCount;
                final int listRight = getHeight() - mSpinnerPadding.right
                        - (rightFadingEdgeShowing ? getArrowScrollPreviewLength() : 0);
                final int xSearchPoint = (selectedView != null && selectedView.getRight() < listRight) ? selectedView
                        .getBottom() : listRight;
                mTempRect.set(xSearchPoint, 0, xSearchPoint, xSearchPoint);
            }
            newFocus = FocusFinder.getInstance().findNextFocusFromRect(this, mTempRect, direction);
        }

        if (newFocus != null) {
            final int positionOfNewFocus = positionOfNewFocus(newFocus);

            // if the focus change is in a different new position, make sure
            // we aren't jumping over another selectable position
            if (mSelectedPosition != INVALID_POSITION && positionOfNewFocus != mSelectedPosition) {
                final int selectablePosition = lookForSelectablePositionOnScreen(direction);
                if (selectablePosition != INVALID_POSITION
                        && ((direction == View.FOCUS_RIGHT && selectablePosition < positionOfNewFocus) || (direction == View.FOCUS_LEFT && selectablePosition > positionOfNewFocus))) {
                    return null;
                }
            }

            int focusScroll = amountToScrollToNewFocus(direction, newFocus, positionOfNewFocus);

            final int maxScrollAmount = getMaxScrollAmount();
            if (focusScroll < maxScrollAmount) {
                // not moving too far, safe to give next view focus
                newFocus.requestFocus(direction);
                mArrowScrollFocusResult.populate(positionOfNewFocus, focusScroll);
                return mArrowScrollFocusResult;
            } else if (distanceToView(newFocus) < maxScrollAmount) {
                // Case to consider:
                // too far to get entire next focusable on screen, but by going
                // max scroll amount, we are getting it at least partially in view,
                // so give it focus and scroll the max ammount.
                newFocus.requestFocus(direction);
                mArrowScrollFocusResult.populate(positionOfNewFocus, maxScrollAmount);
                return mArrowScrollFocusResult;
            }
        }
        return null;
    }

    /**
     * Determine how much we need to scroll in order to get newFocus in view.
     * @param direction
     *            either {@link android.view.View#FOCUS_UP} or {@link android.view.View#FOCUS_DOWN}.
     * @param newFocus
     *            The view that would take focus.
     * @param positionOfNewFocus
     *            The position of the list item containing newFocus
     * @return The amount to scroll. Note: this is always positive! Direction
     *         needs to be taken into account when actually scrolling.
     */
    private int amountToScrollToNewFocus(int direction, View newFocus, int positionOfNewFocus) {
        int amountToScroll = 0;
        newFocus.getDrawingRect(mTempRect);
        offsetDescendantRectToMyCoords(newFocus, mTempRect);
        if (direction == View.FOCUS_LEFT) {
            if (mTempRect.left < mSpinnerPadding.left) {
                amountToScroll = mSpinnerPadding.left - mTempRect.left;
                if (positionOfNewFocus > 0) {
                    amountToScroll += getArrowScrollPreviewLength();
                }
            }
        } else {
            final int listRight = getWidth() - mSpinnerPadding.right;
            if (mTempRect.right > listRight) {
                amountToScroll = mTempRect.right - listRight;
                if (positionOfNewFocus < mItemCount - 1) {
                    amountToScroll += getArrowScrollPreviewLength();
                }
            }
        }
        return amountToScroll;
    }

    /**
     * @param direction
     *            either {@link android.view.View#FOCUS_UP} or {@link android.view.View#FOCUS_DOWN}.
     * @return The position of the next selectable position of the views that
     *         are currently visible, taking into account the fact that there might
     *         be no selection. Returns {@link #INVALID_POSITION} if there is no
     *         selectable view on screen in the given direction.
     */
    int lookForSelectablePositionOnScreen(int direction) {
        final int firstPosition = mFirstPosition;
        if (direction == View.FOCUS_RIGHT) {
            int startPos = (mSelectedPosition != INVALID_POSITION) ? mSelectedPosition + 1 : firstPosition;
            if (startPos >= mAdapter.getCount()) {
                return INVALID_POSITION;
            }
            if (startPos < firstPosition) {
                startPos = firstPosition;
            }

            final int lastVisiblePos = getLastVisiblePosition();
            final SpinnerAdapter adapter = getAdapter();
            for (int pos = startPos; pos <= lastVisiblePos; pos++) {
                if (/*
                     * adapter.isEnabled(pos)
                     * &&
                     */getChildAt(pos - firstPosition).getVisibility() == View.VISIBLE) {
                    return pos;
                }
            }
        } else {
            int last = firstPosition + getChildCount() - 1;
            int startPos = (mSelectedPosition != INVALID_POSITION) ? mSelectedPosition - 1 : firstPosition
                    + getChildCount() - 1;
            if (startPos < 0 || startPos >= mAdapter.getCount()) {
                return INVALID_POSITION;
            }
            if (startPos > last) {
                startPos = last;
            }

            final SpinnerAdapter adapter = getAdapter();
            for (int pos = startPos; pos >= firstPosition; pos--) {
                if (/*
                     * adapter.isEnabled(pos)
                     * &&
                     */getChildAt(pos - firstPosition).getVisibility() == View.VISIBLE) {
                    return pos;
                }
            }
        }
        return INVALID_POSITION;
    }

    /**
     * @param newFocus
     *            The view that would have focus.
     * @return the position that contains newFocus
     */
    private int positionOfNewFocus(View newFocus) {
        final int numChildren = getChildCount();
        for (int i = 0; i < numChildren; i++) {
            final View child = getChildAt(i);
            if (isViewAncestorOf(newFocus, child)) {
                return mFirstPosition + i;
            }
        }
        throw new IllegalArgumentException("newFocus is not a child of any of the" + " children of the list!");
    }

    /**
     * Return true if child is an ancestor of parent, (or equal to the parent).
     */
    boolean isViewAncestorOf(View child, View parent) {
        if (child == parent) {
            return true;
        }

        final ViewParent theParent = child.getParent();
        return (theParent instanceof ViewGroup) && isViewAncestorOf((View) theParent, parent);
    }

    /**
     * @return The amount to preview next items when arrow srolling.
     */
    final static int MIN_SCROLL_PREVIEW_PIXELS = 2;

    int getArrowScrollPreviewLength() {
        return Math.max(MIN_SCROLL_PREVIEW_PIXELS, getHorizontalFadingEdgeLength());
    }

    /**
     * Holds results of focus aware arrow scrolling.
     */
    static class ArrowScrollFocusResult {

        private int mSelectedPosition;
        private int mAmountToScroll;

        /**
         * How {@link android.widget.ListView#arrowScrollFocused} returns its values.
         */
        void populate(int selectedPosition, int amountToScroll) {
            mSelectedPosition = selectedPosition;
            mAmountToScroll = amountToScroll;
        }

        public int getSelectedPosition() {
            return mSelectedPosition;
        }

        public int getAmountToScroll() {
            return mAmountToScroll;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mAdapter == null || !mIsAttached) {
            return false;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER: {

                if (mReceivedInvokeKeyDown) {
                    if (mItemCount > 0) {

                        dispatchPress(mSelectedChild);
                        postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                dispatchUnpress();
                            }
                        }, ViewConfiguration.getPressedStateDuration());

                        int selectedIndex = mSelectedPosition - mFirstPosition;
                        performItemClick(getChildAt(selectedIndex), mSelectedPosition,
                                mAdapter.getItemId(mSelectedPosition));
                    }
                }

                // Clear the flag
                mReceivedInvokeKeyDown = false;

                return true;
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    boolean movePrevious() {
        if (mItemCount > 0 && mSelectedPosition > 0) {
            arrowScrollImpl(View.FOCUS_LEFT);
            //            scrollToChild(mSelectedPosition - mFirstPosition - 1, false);
            return true;
        } else {
            return false;
        }
    }

    boolean moveNext() {
        if (mItemCount > 0 && mSelectedPosition < mItemCount - 1) {
            arrowScrollImpl(View.FOCUS_RIGHT);
            //            scrollToChild(mSelectedPosition - mFirstPosition + 1, true);
            return true;
        } else {
            return false;
        }
    }

    /*
     * original code
     * private boolean scrollToChild(int childPosition) {
     * View child = getChildAt(childPosition);
     * if (child != null) {
     * int distance = getCenterOfGallery() - getCenterOfView(child);
     * mFlingRunnable.startUsingDistance(distance);
     * return true;
     * }
     * return false;
     * }
     */

    private boolean scrollToChild(int childPosition, boolean isNext) {
        View child = getChildAt(childPosition);
        int distance = 0;
        int parentEdge = getEdgeOfGallery(isNext);
        int itemIndex = childPosition + mFirstPosition;
        boolean isNewCreate = false;
        if (child == null) {
            //			child = makeAndAddView(itemIndex, itemIndex, parentEdge, isNext);
            //			Log.d("lawin", "----------makeAndAddView  parentEdge = " + parentEdge);
            //			isNewCreate = true;
            return false;
        }
        if (child != null) {
            int childEdge = getEdgeOfView(child, isNext);
            if (isNext) {
                distance = childEdge > parentEdge ? -((childEdge - parentEdge) + mSpacing) : 0;
            } else {
                distance = childEdge < parentEdge ? ((parentEdge - childEdge) + mSpacing) : 0;
            }

            makeNextPosition(itemIndex);
            if (isNewCreate) {
                mFirstPosition = isNext ? mFirstPosition : itemIndex;
            }
            if (Math.abs(distance) > 0) {
                mFlingRunnable.startUsingDistance(distance);
            } else {

            }
            return true;
        }

        return false;
    }

    private void makeNextPosition(int newPos) {
        // TODO Auto-generated method stub
        setSelectedPositionInt(newPos);
        setNextSelectedPositionInt(newPos);
        checkSelectionChanged();
    }

    @Override
    void setSelectedPositionInt(int position) {
        super.setSelectedPositionInt(position);

        // Updates any metadata we keep about the selected item.
        //updateSelectedItemMetadata(position);
        setNextSelectedPositionInt(position);
    }

    private void updateSelectedItemMetadata(int position) {

        View oldSelectedChild = mSelectedChild;
        int selectedIndex = position - mFirstPosition > 0 ? position - mFirstPosition : 0;

        View child = mSelectedChild = getChildAt(selectedIndex);
        if (child == null) {
            return;
        }

        child.setSelected(true);
        //        child.setFocusable(true);
        //
        //        if (hasFocus()) {
        //            child.requestFocus();
        //        }

        // We unfocus the old child down here so the above hasFocus check
        // returns true
        if (oldSelectedChild != null && oldSelectedChild != child) {

            // Make sure its drawable state doesn't contain 'selected'
            oldSelectedChild.setSelected(false);

            // Make sure it is not focusable anymore, since otherwise arrow keys
            // can make this one be focused
            //            oldSelectedChild.setFocusable(false);
        }

    }

    /**
     * Describes how the child views are aligned.
     * @param gravity
     * @attr ref android.R.styleable#Gallery_gravity
     */
    public void setGravity(int gravity) {
        if (mGravity != gravity) {
            mGravity = gravity;
            requestLayout();
        }
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        int selectedIndex = mSelectedPosition - mFirstPosition;

        // Just to be safe
        if (selectedIndex < 0)
            return i;

        if (i == childCount - 1) {
            // Draw the selected child last
            return selectedIndex;
        } else if (i >= selectedIndex) {
            // Move the children after the selected child earlier one
            return i + 1;
        } else {
            // Keep the children before the selected child the same
            return i;
        }
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        /*
         * The gallery shows focus by focusing the selected item. So, give
         * focus to our selected item instead. We steal keys from our
         * selected item elsewhere.
         */
        //        this.gainFocus = gainFocus;
        //        if (gainFocus && mSelectedChild != null) {
        ////            mSelectedChild.requestFocus(direction);
        ////            mSelectedChild.setSelected(true);
        //        }
        //        if(!gainFocus && !mSelectorRect.isEmpty()){
        //        	mSelectorRect.setEmpty();
        //        } else {
        //        	requestLayout();
        //        }
        this.gainFocus = gainFocus;
        if (gainFocus) {
            //get focus on PokerGroupView
            //    		mSelectedPosition = 0;
            //    		mOldSelectedPosition = INVALID_POSITION;
            mSelectedPosition = mOldSelectedPosition == INVALID_POSITION ? 0 : mOldSelectedPosition;
            positionSelector(mSelectedPosition, getSelectedView());
        } else {
            mOldSelectedPosition = mSelectedPosition;
            mSelectedPosition = INVALID_POSITION;
            mSelectorRect.setEmpty();
            //lose focus
        }
        //TODO:temp fix
        handlerFocusChanged();
    }

    protected void handlerFocusChanged() {
        checkSelectionChanged();
    }

    /**
     * Responsible for fling behavior. Use {@link #startUsingVelocity(int)} to
     * initiate a fling. Each frame of the fling is handled in {@link #run()}.
     * A FlingRunnable will keep re-posting itself until the fling is done.
     */
    class FlingRunnable implements Runnable {

        /**
         * Tracks the decay of a fling scroll
         */
        Scroller mScroller;

        /**
         * X value reported by mScroller on the previous fling
         */
        private int mLastFlingX;

        public FlingRunnable() {
            mScroller = new Scroller(getContext());
        }

        private void startCommon() {
            // Remove any pending flings
            removeCallbacks(this);
        }

        public void startUsingVelocity(int initialVelocity) {
            if (initialVelocity == 0)
                return;

            startCommon();

            int initialX = initialVelocity < 0 ? Integer.MAX_VALUE : 0;
            mLastFlingX = initialX;
            mScroller.fling(initialX, 0, initialVelocity, 0, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            post(this);
        }

        public void startUsingDistance(int distance) {
            if (distance == 0)
                return;

            startCommon();

            mLastFlingX = 0;
            mScroller.startScroll(0, 0, -distance, 0, mAnimationDuration);
            post(this);
        }

        public void stop(boolean scrollIntoSlots) {
            removeCallbacks(this);
            endFling(scrollIntoSlots);
        }

        private void endFling(boolean scrollIntoSlots) {
            /*
             * Force the scroller's status to finished (without setting its
             * position to the end)
             */
            mScroller.forceFinished(true);

            if (scrollIntoSlots)
                scrollIntoSlots();
        }

        @Override
        public void run() {

            if (mItemCount == 0) {
                endFling(true);
                return;
            }

            mShouldStopFling = false;

            final Scroller scroller = mScroller;
            boolean more = scroller.computeScrollOffset();
            final int x = scroller.getCurrX();

            // Flip sign to convert finger direction to list items direction
            // (e.g. finger moving down means list is moving towards the top)
            int delta = mLastFlingX - x;

            // Pretend that each frame of a fling scroll is a touch scroll
            if (delta > 0) {
                // Moving towards the left. Use leftmost view as mDownTouchPosition
                mDownTouchPosition = mIsRtl ? (mFirstPosition + getChildCount() - 1) : mFirstPosition;

                // Don't fling more than 1 screen
                delta = Math.min(getWidth() - getPaddingLeft() - getPaddingRight() - 1, delta);
            } else {
                // Moving towards the right. Use rightmost view as mDownTouchPosition
                int offsetToLast = getChildCount() - 1;
                mDownTouchPosition = mIsRtl ? mFirstPosition : (mFirstPosition + getChildCount() - 1);

                // Don't fling more than 1 screen
                delta = Math.max(-(getWidth() - getPaddingRight() - getPaddingLeft() - 1), delta);
            }

            trackMotionScroll(delta);

            if (more && !mShouldStopFling) {
                mLastFlingX = x;
                post(this);
            } else {
                endFling(true);
                //position selector position
                if (getSelectedView() != null) {
                    positionSelector(mSelectedPosition, getSelectedView());
                    mSelectedLeft = getSelectedView().getLeft();
                }
            }
        }

    }
}
