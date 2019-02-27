package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Checkable;
import android.widget.ListAdapter;

public class ListView extends AbsListView {
    private static final String TAG = "ListView";
    /**
     * When arrow scrolling, need a certain amount of pixels to preview next
     * items. This is usually the fading edge, but if that is small enough, we
     * want to make sure we preview at least this many pixels.
     */
    private static final int MIN_SCROLL_PREVIEW_PIXELS = 2;

    int mDividerHeight;
    Drawable mDivider;
    private boolean mDividerIsOpaque;

    private final ArrowScrollFocusResult mArrowScrollFocusResult = new ArrowScrollFocusResult();

    public ListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public ListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public ListView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public View getFirstVisibleChild() {
        return getChildAt(getFirsVisibletChildIndex());
    }

    @Override
    public View getLastVisibleChild() {
        return getChildAt(getLastVisibleChildIndex());
    }

    @Override
    public int getFirsVisibletChildIndex() {
        return getUpPreLoadedCount();
    }

    @Override
    public int getLastVisibleChildIndex() {
        return getChildCount() - 1 - getDownPreLoadedCount();
    }

    @Override
    public int getVisibleChildCount() {
        return getChildCount() - getUpPreLoadedCount() - getDownPreLoadedCount();
    }

    @Override
    public int getFirstPosition() {
        return getFirstVisiblePosition() - getUpPreLoadedCount();
    }

    @Override
    public int getLastPosition() {
        return getFirstPosition() + getChildCount() - 1;
    }

    @Override
    public int getLastVisiblePosition() {
        return mFirstPosition + getVisibleChildCount() - 1;
    }

    @Override
    void fillGap(boolean isDown) {
        final int visibleChildCount = getVisibleChildCount();
        if (isDown) {
            int paddingTop = 0;
            if ((getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
                paddingTop = getListPaddingTop();
            }
            final int startOffset = visibleChildCount > 0 ? getChildAt(getLastVisibleChildIndex()).getBottom() + mDividerHeight + mSpacing : paddingTop;
            fillDown(getFirstVisiblePosition() + visibleChildCount, startOffset);
            correctTooHigh(visibleChildCount);
        } else {
            int paddingBottom = 0;
            if ((getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
                paddingBottom = getListPaddingBottom();
            }
            final int startOffset = visibleChildCount > 0 ? getChildAt(getFirsVisibletChildIndex()).getTop() - mDividerHeight - mSpacing : getHeight() - paddingBottom;
            fillUp(getFirstVisiblePosition() - 1, startOffset);
            correctTooLow(visibleChildCount);
        }
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

        final ListAdapter adapter = mAdapter;
        int closetChildIndex = -1;
        int closestChildTop = 0;
        if (adapter != null && gainFocus && previouslyFocusedRect != null) {
            previouslyFocusedRect.offset(getScrollX(), getScrollY());

            // Don't cache the result of getChildCount or mFirstPosition here,
            // it could change in layoutChildren.
            if (adapter.getCount() < getVisibleChildCount() + mFirstPosition) {
                mLayoutMode = LAYOUT_NORMAL;
                layoutChildren();
            }

            // figure out which item should be selected based on previously
            // focused rect
            Rect otherRect = mTempRect;
            int minDistance = Integer.MAX_VALUE;
            final int visibleChildCount = getVisibleChildCount();
            final int firstPosition = getFirstVisiblePosition();

            for (int i = 0; i < visibleChildCount; i++) {
                // only consider selectable views
                if (!adapter.isEnabled(firstPosition + i)) {
                    continue;
                }

                View other = getChildAt(i);
                other.getDrawingRect(otherRect);
                offsetDescendantRectToMyCoords(other, otherRect);
                int distance = getDistance(previouslyFocusedRect, otherRect, direction);

                if (distance < minDistance) {
                    minDistance = distance;
                    closetChildIndex = i;
                    closestChildTop = other.getTop();
                }
            }
        }

        if (closetChildIndex >= 0) {
            closestChildTop = closestChildTop > mListPadding.top ? closestChildTop : mListPadding.top;
            setSelectionFromTop(closetChildIndex + getFirstVisiblePosition(), closestChildTop);
        } else {
            // requestLayout();
        }
    }

    /**
     * Sets the drawable that will be drawn between each item in the list. If
     * the drawable does not have an intrinsic height, you should also call
     * {@link #setDividerHeight(int)}
     *
     * @param divider The drawable to use.
     */
    public void setDivider(Drawable divider) {
        if (divider != null) {
            mDividerHeight = divider.getIntrinsicHeight();
        } else {
            mDividerHeight = 0;
        }
        mDivider = divider;
        mDividerIsOpaque = divider == null || divider.getOpacity() == PixelFormat.OPAQUE;
        requestLayout();
        invalidate();
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

            if (mAdapter == null) {
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

            View sel;
            View oldSel = null;
            View oldFirst = null;
            View newSel = null;

            View focusLayoutRestoreView = null;

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
                case LAYOUT_MOVE_SELECTION:
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
            }

            boolean dataChanged = mDataChanged;
            if (dataChanged) {
                handleDataChanged();
            }

            // Handle the empty set by removing all views that are visible
            // and calling it a day
            if (mItemCount == 0) {
                resetList();
                // invokeOnItemScrollListener();
                return;
            } else if (mItemCount != mAdapter.getCount()) {
                throw new IllegalStateException("The content of the adapter has changed but " + "ListView did not receive a notification. Make sure the content of "
                        + "your adapter is not modified from a background thread, but only " + "from the UI thread. [in ListView(" + getId() + ", " + getClass() + ") with Adapter("
                        + mAdapter.getClass() + ")]");
            }

            setSelectedPositionInt(mNextSelectedPosition);

            // Pull all children into the RecycleBin.
            // These views will be reused if possible
            final int firstPosition = getFirstVisiblePosition();
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

            // // Remember which child, if any, had accessibility focus.
            // final ViewRootImpl viewRootImpl = getViewRootImpl();
            // if (viewRootImpl != null) {
            // final View accessFocusedView =
            // viewRootImpl.getAccessibilityFocusedHost();
            // if (accessFocusedView != null) {
            // final View accessFocusedChild = findAccessibilityFocusedChild(
            // accessFocusedView);
            // if (accessFocusedChild != null) {
            // if (!dataChanged ||
            // isDirectChildHeaderOrFooter(accessFocusedChild)) {
            // // If the views won't be changing, try to maintain
            // // focus on the current view host and (if
            // // applicable) its virtual view.
            // accessibilityFocusLayoutRestoreView = accessFocusedView;
            // accessibilityFocusLayoutRestoreNode = viewRootImpl
            // .getAccessibilityFocusedVirtualView();
            // } else {
            // // Otherwise, try to maintain focus at the same
            // // position.
            // accessibilityFocusPosition =
            // getPositionForView(accessFocusedChild);
            // }
            // }
            // }
            // }

            // Clear out old views
            detachAllViewsFromParent();
            recycleBin.removeSkippedScrap();
            mUpPreLoadedCount = 0;
            mDownPreLoadedCount = 0;

            switch (mLayoutMode) {
                case LAYOUT_SET_SELECTION:
                    if (newSel != null) {
                        sel = fillFromSelection(newSel.getTop(), childrenTop, childrenBottom);
                    } else {
                        sel = fillFromMiddle(childrenTop, childrenBottom);
                    }
                    break;
                case LAYOUT_SYNC:
                    sel = fillSpecific(mSyncPosition, mSpecificTop);
                    break;
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
                case LAYOUT_MOVE_SELECTION:
                    sel = moveSelection(oldSel, newSel, delta, childrenTop, childrenBottom);
                    break;
                default:
                    if (childCount == 0) {
                        if (!mStackFromBottom) {
                            final int position = lookForSelectablePosition(0, true);
                            setSelectedPositionInt(position);
                            sel = fillFromTop(childrenTop);
                        } else {
                            final int position = lookForSelectablePosition(mItemCount - 1, false);
                            setSelectedPositionInt(position);
                            sel = fillUp(mItemCount - 1, childrenBottom);
                        }
                    } else {
                        if (mSelectedPosition >= 0 && mSelectedPosition < mItemCount) {
                            sel = fillSpecific(mSelectedPosition, oldSel == null ? childrenTop : oldSel.getTop());
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
                    final boolean focusWasTaken = (sel == focusLayoutRestoreDirectChild && focusLayoutRestoreView != null && focusLayoutRestoreView.requestFocus()) || sel.requestFocus();
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

            // // Attempt to restore accessibility focus.
            // if (accessibilityFocusLayoutRestoreNode != null) {
            // accessibilityFocusLayoutRestoreNode.performAction(
            // AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS);
            // } else if (accessibilityFocusLayoutRestoreView != null) {
            // accessibilityFocusLayoutRestoreView.requestAccessibilityFocus();
            // } else if (accessibilityFocusPosition != INVALID_POSITION) {
            // // Bound the position within the visible children.
            // final int position = MathUtils.constrain(
            // (accessibilityFocusPosition - mFirstPosition), 0,
            // (getChildCount() - 1));
            // final View restoreView = getChildAt(position);
            // if (restoreView != null) {
            // restoreView.requestAccessibilityFocus();
            // }
            // }

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

    /**
     * Fills the grid based on positioning the new selection at a specific
     * location. The selection may be moved so that it does not intersect the
     * faded edges. The grid is then filled upwards and downwards from there.
     *
     * @param selectedTop    Where the selected item should be
     * @param childrenTop    Where to start drawing children
     * @param childrenBottom Last pixel where children can be drawn
     * @return The view that currently has selection
     */
    public View fillFromSelection(int selectedTop, int childrenTop, int childrenBottom) {
        int fadingEdgeLength = getVerticalFadingEdgeLength();
        final int selectedPosition = mSelectedPosition;

        View sel;

        final int topSelectionPixel = getTopSelectionPixel(childrenTop, fadingEdgeLength, selectedPosition);
        final int bottomSelectionPixel = getBottomSelectionPixel(childrenBottom, fadingEdgeLength, selectedPosition);

        sel = makeAndAddView(selectedPosition, selectedTop, true, mListPadding.left, true);

        // Some of the newly selected item extends below the bottom of the list
        if (sel.getBottom() > bottomSelectionPixel) {
            // Find space available above the selection into which we can scroll
            // upwards
            final int spaceAbove = sel.getTop() - topSelectionPixel;

            // Find space required to bring the bottom of the selected item
            // fully into view
            final int spaceBelow = sel.getBottom() - bottomSelectionPixel;
            final int offset = Math.min(spaceAbove, spaceBelow);

            // Now offset the selected item to get it into view
            sel.offsetTopAndBottom(-offset);
        } else if (sel.getTop() < topSelectionPixel) {
            // Find space required to bring the top of the selected item fully
            // into view
            final int spaceAbove = topSelectionPixel - sel.getTop();

            // Find space available below the selection into which we can scroll
            // downwards
            final int spaceBelow = bottomSelectionPixel - sel.getBottom();
            final int offset = Math.min(spaceAbove, spaceBelow);

            // Offset the selected item to get it into view
            sel.offsetTopAndBottom(offset);
        }

        // Fill in views above and below
        fillAboveAndBelow(sel, selectedPosition);

        if (!mStackFromBottom) {
            correctTooHigh(getVisibleChildCount());
        } else {
            correctTooLow(getVisibleChildCount());
        }

        return sel;
    }

    /**
     * Put mSelectedPosition in the middle of the screen and then build up and
     * down from there. This method forces mSelectedPosition to the center.
     *
     * @param childrenTop    Top of the area in which children can be drawn, as measured in
     *                       pixels
     * @param childrenBottom Bottom of the area in which children can be drawn, as measured
     *                       in pixels
     * @return Currently selected view
     */
    protected View fillFromMiddle(int childrenTop, int childrenBottom) {
        int height = childrenBottom - childrenTop;

        int position = reconcileSelectedPosition();

        View sel = makeAndAddView(position, childrenTop, true, mListPadding.left, true);
        mFirstPosition = position;

        int selHeight = sel.getMeasuredHeight();
        if (selHeight <= height) {
            sel.offsetTopAndBottom((height - selHeight) / 2);
        }

        fillAboveAndBelow(sel, position);

        if (!mStackFromBottom) {
            correctTooHigh(getVisibleChildCount());
        } else {
            correctTooLow(getVisibleChildCount());
        }

        return sel;
    }

    /**
     * Put a specific item at a specific location on the screen and then build
     * up and down from there.
     *
     * @param position The reference view to use as the starting point
     * @param top      Pixel offset from the top of this view to the top of the
     *                 reference view.
     * @return The selected view, or null if the selected view is outside the
     * visible area.
     */
    protected View fillSpecific(int position, int top) {
        boolean tempIsSelected = position == mSelectedPosition;
        View temp = makeAndAddView(position, top, true, mListPadding.left, tempIsSelected);
        // Possibly changed again in fillUp if we add rows above this one.
        mFirstPosition = position;

        View above;
        View below;

        final int dividerHeight = mDividerHeight;
        if (!mStackFromBottom) {
            above = fillUp(position - 1, temp.getTop() - dividerHeight - mSpacing);
            // This will correct for the top of the first view not touching the
            // top of the list
            adjustViewsUpOrDown();
            below = fillDown(position + 1, temp.getBottom() + dividerHeight + mSpacing);
            int visibleChildCount = getVisibleChildCount();
            if (visibleChildCount > 0) {
                correctTooHigh(visibleChildCount);
            }
        } else {
            below = fillDown(position + 1, temp.getBottom() + dividerHeight + mSpacing);
            // This will correct for the bottom of the last view not touching
            // the bottom of the list
            adjustViewsUpOrDown();
            above = fillUp(position - 1, temp.getTop() - dividerHeight - mSpacing);
            int visibleChildCount = getVisibleChildCount();
            if (visibleChildCount > 0) {
                correctTooLow(visibleChildCount);
            }
        }

        if (tempIsSelected) {
            return temp;
        } else if (above != null) {
            return above;
        } else {
            return below;
        }
    }

    /**
     * Fills the list from pos up to the top of the list view.
     *
     * @param pos        The first position to put in the list
     * @param nextBottom The location where the bottom of the item associated with pos
     *                   should be drawn
     * @return The view that is currently selected
     */
    protected View fillUp(int pos, int nextBottom) {
        View selectedView = null;

        int end = 0;
        if ((getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
            end = mListPadding.top;
        }

        while (nextBottom > end && pos >= 0) {
            if (mUpPreLoadedCount > 0) {
                nextBottom = getChildAt(pos - getFirstPosition()).getTop() - mDividerHeight - mSpacing;
                mUpPreLoadedCount--;
                pos--;
                continue;
            }
            // is this the selected item?
            boolean selected = pos == mSelectedPosition;
            View child = makeAndAddView(pos, nextBottom, false, mListPadding.left, selected);
            nextBottom = child.getTop() - mDividerHeight - mSpacing;
            if (selected) {
                selectedView = child;
            }
            pos--;
        }

        mFirstPosition = pos + 1;
        fillUpPreLoad();
        // setVisibleRangeHint(mFirstPosition, mFirstPosition + getChildCount()
        // - 1);
        return selectedView;
    }

    protected void fillUpPreLoad() {
        if (mPreLoadCount <= 0 || mUpPreLoadedCount >= mPreLoadCount) {
            return;
        }

        int pos = getFirstPosition() - 1;
        int nextBottom = getFirstChild().getTop() - mDividerHeight - mSpacing;
        int preLoadPos = pos - (mPreLoadCount - mUpPreLoadedCount);

        while (pos > preLoadPos && pos >= 0) {
            View child = makeAndAddView(pos, nextBottom, false, mListPadding.left, false);
            nextBottom = child.getTop() - mDividerHeight - mSpacing;
            pos--;
            mUpPreLoadedCount++;
        }
    }

    /**
     * Make sure views are touching the top or bottom edge, as appropriate for
     * our gravity
     */
    protected void adjustViewsUpOrDown() {
        final int visibleChildCount = getVisibleChildCount();
        int delta;

        if (visibleChildCount > 0) {
            View child;

            if (!mStackFromBottom) {
                // Uh-oh -- we came up short. Slide all views up to make them
                // align with the top
                child = getFirstVisibleChild();
                delta = child.getTop() - mListPadding.top;
                if (mFirstPosition != 0) {
                    // It's OK to have some space above the first item if it is
                    // part of the vertical spacing
                    delta -= mDividerHeight - mSpacing;
                }
                if (delta < 0) {
                    // We only are looking to see if we are too low, not too
                    // high
                    delta = 0;
                }
            } else {
                // we are too high, slide all views down to align with bottom
                child = getLastVisibleChild();
                delta = child.getBottom() - (getHeight() - mListPadding.bottom);

                if (mFirstPosition + visibleChildCount < mItemCount) {
                    // It's OK to have some space below the last item if it is
                    // part of the vertical spacing
                    delta += mDividerHeight + mSpacing;
                }

                if (delta > 0) {
                    delta = 0;
                }
            }

            if (delta != 0) {
                offsetChildrenTopAndBottom(-delta);
            }
        }
    }

    /**
     * Fills the list from top to bottom, starting with mFirstPosition
     *
     * @param nextTop The location where the top of the first item should be drawn
     * @return The view that is currently selected
     */
    protected View fillFromTop(int nextTop) {
        mFirstPosition = Math.min(mFirstPosition, mSelectedPosition);
        mFirstPosition = Math.min(mFirstPosition, mItemCount - 1);
        if (mFirstPosition < 0) {
            mFirstPosition = 0;
        }
        return fillDown(mFirstPosition, nextTop);
    }

    /**
     * Fills the list based on positioning the new selection relative to the old
     * selection. The new selection will be placed at, above, or below the
     * location of the new selection depending on how the selection is moving.
     * The selection will then be pinned to the visible part of the screen,
     * excluding the edges that are faded. The list is then filled upwards and
     * downwards from there.
     *
     * @param oldSel         The old selected view. Useful for trying to put the new
     *                       selection in the same place
     * @param newSel         The view that is to become selected. Useful for trying to put
     *                       the new selection in the same place
     * @param delta          Which way we are moving
     * @param childrenTop    Where to start drawing children
     * @param childrenBottom Last pixel where children can be drawn
     * @return The view that currently has selection
     */
    public View moveSelection(View oldSel, View newSel, int delta, int childrenTop, int childrenBottom) {
        int fadingEdgeLength = getVerticalFadingEdgeLength();
        final int selectedPosition = mSelectedPosition;

        View sel;

        final int topSelectionPixel = getTopSelectionPixel(childrenTop, fadingEdgeLength, selectedPosition);
        final int bottomSelectionPixel = getBottomSelectionPixel(childrenTop, fadingEdgeLength, selectedPosition);

        if (delta > 0) {
            /*
			 * Case 1: Scrolling down.
			 */

			/*
			 * Before After | | | | +-------+ +-------+ | A | | A | | 1 | =>
			 * +-------+ +-------+ | B | | B | | 2 | +-------+ +-------+ | | | |
			 * 
			 * Try to keep the top of the previously selected item where it was.
			 * oldSel = A sel = B
			 */

            // Put oldSel (A) where it belongs
            oldSel = makeAndAddView(selectedPosition - 1, oldSel.getTop(), true, mListPadding.left, false);

            final int dividerHeight = mDividerHeight;

            // Now put the new selection (B) below that
            sel = makeAndAddView(selectedPosition, oldSel.getBottom() + dividerHeight + mSpacing, true, mListPadding.left, true);

            // Some of the newly selected item extends below the bottom of the
            // list
            if (sel.getBottom() > bottomSelectionPixel) {

                // Find space available above the selection into which we can
                // scroll upwards
                int spaceAbove = sel.getTop() - topSelectionPixel;

                // Find space required to bring the bottom of the selected item
                // fully into view
                int spaceBelow = sel.getBottom() - bottomSelectionPixel;

                // Don't scroll more than half the height of the list
                int halfVerticalSpace = (childrenBottom - childrenTop) / 2;
                int offset = Math.min(spaceAbove, spaceBelow);
                offset = Math.min(offset, halfVerticalSpace);

                // We placed oldSel, so offset that item
                oldSel.offsetTopAndBottom(-offset);
                // Now offset the selected item to get it into view
                sel.offsetTopAndBottom(-offset);
            }

            // Fill in views above and below
            if (!mStackFromBottom) {
                fillUp(mSelectedPosition - 2, sel.getTop() - dividerHeight - mSpacing);
                adjustViewsUpOrDown();
                fillDown(mSelectedPosition + 1, sel.getBottom() + dividerHeight + mSpacing);
            } else {
                fillDown(mSelectedPosition + 1, sel.getBottom() + dividerHeight + mSpacing);
                adjustViewsUpOrDown();
                fillUp(mSelectedPosition - 2, sel.getTop() - dividerHeight - mSpacing);
            }
        } else if (delta < 0) {
			/*
			 * Case 2: Scrolling up.
			 */

			/*
			 * Before After | | | | +-------+ +-------+ | A | | A | +-------+ =>
			 * | 1 | | B | +-------+ | 2 | | B | +-------+ +-------+ | | | |
			 * 
			 * Try to keep the top of the item about to become selected where it
			 * was. newSel = A olSel = B
			 */

            if (newSel != null) {
                // Try to position the top of newSel (A) where it was before it
                // was selected
                sel = makeAndAddView(selectedPosition, newSel.getTop(), true, mListPadding.left, true);
            } else {
                // If (A) was not on screen and so did not have a view, position
                // it above the oldSel (B)
                sel = makeAndAddView(selectedPosition, oldSel.getTop(), false, mListPadding.left, true);
            }

            // Some of the newly selected item extends above the top of the list
            if (sel.getTop() < topSelectionPixel) {
                // Find space required to bring the top of the selected item
                // fully into view
                int spaceAbove = topSelectionPixel - sel.getTop();

                // Find space available below the selection into which we can
                // scroll downwards
                int spaceBelow = bottomSelectionPixel - sel.getBottom();

                // Don't scroll more than half the height of the list
                int halfVerticalSpace = (childrenBottom - childrenTop) / 2;
                int offset = Math.min(spaceAbove, spaceBelow);
                offset = Math.min(offset, halfVerticalSpace);

                // Offset the selected item to get it into view
                sel.offsetTopAndBottom(offset);
            }

            // Fill in views above and below
            fillAboveAndBelow(sel, selectedPosition);
        } else {

            int oldTop = oldSel.getTop();

			/*
			 * Case 3: Staying still
			 */
            sel = makeAndAddView(selectedPosition, oldTop, true, mListPadding.left, true);

            // We're staying still...
            if (oldTop < childrenTop) {
                // ... but the top of the old selection was off screen.
                // (This can happen if the data changes size out from under us)
                int newBottom = sel.getBottom();
                if (newBottom < childrenTop + 20) {
                    // Not enough visible -- bring it onscreen
                    sel.offsetTopAndBottom(childrenTop - sel.getTop());
                }
            }

            // Fill in views above and below
            fillAboveAndBelow(sel, selectedPosition);
        }

        return sel;
    }

    /**
     * Calculate the top-most pixel we can draw the selection into
     *
     * @param childrenTop      Top pixel were children can be drawn
     * @param fadingEdgeLength Length of the fading edge in pixels, if present
     * @param selectedPosition The position that will be selected
     * @return The top-most pixel we can draw the selection into
     */
    private int getTopSelectionPixel(int childrenTop, int fadingEdgeLength, int selectedPosition) {
        // first pixel we can draw the selection into
        int topSelectionPixel = childrenTop;
        if (selectedPosition > 0) {
            topSelectionPixel += fadingEdgeLength;
        }
        return topSelectionPixel;
    }

    /**
     * Calculate the bottom-most pixel we can draw the selection into
     *
     * @param childrenBottom   Bottom pixel were children can be drawn
     * @param fadingEdgeLength Length of the fading edge in pixels, if present
     * @param selectedPosition The position that will be selected
     * @return The bottom-most pixel we can draw the selection into
     */
    private int getBottomSelectionPixel(int childrenBottom, int fadingEdgeLength, int selectedPosition) {
        int bottomSelectionPixel = childrenBottom;
        if (selectedPosition != mItemCount - 1) {
            bottomSelectionPixel -= fadingEdgeLength;
        }
        return bottomSelectionPixel;
    }

    /**
     * Fills the list from pos down to the end of the list view.
     *
     * @param pos     The first position to put in the list
     * @param nextTop The location where the top of the item associated with pos
     *                should be drawn
     * @return The view that is currently selected, if it happens to be in the
     * range that we draw.
     */
    private View fillDown(int pos, int nextTop) {
        View selectedView = null;

        int end = (getBottom() - getTop());
        if ((getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
            end -= mListPadding.bottom;
        }

        while (nextTop < end && pos < mItemCount) {
            if (mDownPreLoadedCount > 0) {
                nextTop = getChildAt(pos - getFirstPosition()).getBottom() + mDividerHeight + mSpacing;
                pos++;
                mDownPreLoadedCount--;
                continue;
            }
            // is this the selected item?
            boolean selected = pos == mSelectedPosition;
            View child = makeAndAddView(pos, nextTop, true, mListPadding.left, selected);

            nextTop = child.getBottom() + mDividerHeight + mSpacing;
            if (selected) {
                selectedView = child;
            }
            pos++;
        }

        fillDownPreLoad();
        // setVisibleRangeHint(mFirstPosition, mFirstPosition + getChildCount()
        // - 1);
        return selectedView;
    }

    private void fillDownPreLoad() {
        if (mPreLoadCount <= 0 || mDownPreLoadedCount >= mPreLoadCount) {
            return;
        }

        int nextTop = getLastChild().getBottom() + mDividerHeight + mSpacing;
        int pos = getLastPosition() + 1;
        int preLoadPos = pos + mPreLoadCount - mDownPreLoadedCount;
        while (pos < preLoadPos && pos < mItemCount) {
            boolean selected = pos == mSelectedPosition;
            View child = makeAndAddView(pos, nextTop, true, mListPadding.left, selected);
            nextTop = child.getBottom() + mDividerHeight + mSpacing;
            pos++;

            mDownPreLoadedCount++;
        }
    }

    /**
     * Once the selected view as been placed, fill up the visible area above and
     * below it.
     *
     * @param sel      The selected view
     * @param position The position corresponding to sel
     */
    private void fillAboveAndBelow(View sel, int position) {
        final int dividerHeight = mDividerHeight;
        if (!mStackFromBottom) {
            fillUp(position - 1, sel.getTop() - dividerHeight - mSpacing);
            adjustViewsUpOrDown();
            fillDown(position + 1, sel.getBottom() + dividerHeight + mSpacing);
        } else {
            fillDown(position + 1, sel.getBottom() + dividerHeight + mSpacing);
            adjustViewsUpOrDown();
            fillUp(position - 1, sel.getTop() - dividerHeight);
        }
    }

    /**
     * Check if we have dragged the bottom of the list too high (we have pushed
     * the top element off the top of the screen when we did not need to).
     * Correct by sliding everything back down.
     *
     * @param childCount Number of children
     */
    private void correctTooHigh(int bisibleChildCount) {
        // First see if the last item is visible. If it is not, it is OK for the
        // top of the list to be pushed up.
        int lastPosition = getLastVisiblePosition();
        if (lastPosition == mItemCount - 1 && bisibleChildCount > 0) {

            // Get the last child ...
            final View lastChild = getLastVisibleChild();

            // ... and its bottom edge
            final int lastBottom = lastChild.getBottom();

            // This is bottom of our drawable area
            final int end = (getBottom() - getTop()) - mListPadding.bottom;

            // This is how far the bottom edge of the last view is from the
            // bottom of the
            // drawable area
            int bottomOffset = end - lastBottom;
            View firstChild = getFirstVisibleChild();
            final int firstTop = firstChild.getTop();

            // Make sure we are 1) Too high, and 2) Either there are more rows
            // above the
            // first row or the first row is scrolled off the top of the
            // drawable area
            if (bottomOffset > 0 && (mFirstPosition > 0 || firstTop < mListPadding.top)) {
                if (mFirstPosition == 0) {
                    // Don't pull the top too far down
                    bottomOffset = Math.min(bottomOffset, mListPadding.top - firstTop);
                }
                // Move everything down
                offsetChildrenTopAndBottom(bottomOffset);
                if (mFirstPosition > 0) {
                    // Fill the gap that was opened above mFirstPosition with
                    // more rows, if
                    // possible
                    fillUp(mFirstPosition - 1, firstChild.getTop() - mDividerHeight - mSpacing);
                    // Close up the remaining gap
                    adjustViewsUpOrDown();
                }

            }
        }
    }

    /**
     * Check if we have dragged the bottom of the list too low (we have pushed
     * the bottom element off the bottom of the screen when we did not need to).
     * Correct by sliding everything back up.
     *
     * @param childCount Number of children
     */
    private void correctTooLow(int bisibleChildCount) {
        // First see if the first item is visible. If it is not, it is OK for
        // the
        // bottom of the list to be pushed down.
        if (mFirstPosition == 0 && bisibleChildCount > 0) {

            // Get the first child ...
            final View firstChild = getFirstVisibleChild();

            // ... and its top edge
            final int firstTop = firstChild.getTop();

            // This is top of our drawable area
            final int start = mListPadding.top;

            // This is bottom of our drawable area
            final int end = (getBottom() - getTop()) - mListPadding.bottom;

            // This is how far the top edge of the first view is from the top of
            // the
            // drawable area
            int topOffset = firstTop - start;
            View lastChild = getChildAt(getLastVisibleChildIndex());
            final int lastBottom = lastChild.getBottom();
            int lastPosition = getLastVisiblePosition();

            // Make sure we are 1) Too low, and 2) Either there are more rows
            // below the
            // last row or the last row is scrolled off the bottom of the
            // drawable area
            if (topOffset > 0) {
                if (lastPosition < mItemCount - 1 || lastBottom > end) {
                    if (lastPosition == mItemCount - 1) {
                        // Don't pull the bottom too far up
                        topOffset = Math.min(topOffset, lastBottom - end);
                    }
                    // Move everything up
                    offsetChildrenTopAndBottom(-topOffset);
                    if (lastPosition < mItemCount - 1) {
                        // Fill the gap that was opened below the last position
                        // with more rows, if
                        // possible
                        fillDown(lastPosition + 1, lastChild.getBottom() + mDividerHeight + mSpacing);
                        // Close up the remaining gap
                        adjustViewsUpOrDown();
                    }
                } else if (lastPosition == mItemCount - 1) {
                    adjustViewsUpOrDown();
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Sets up mListPadding
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int childWidth = 0;
        int childHeight = 0;
        int childState = 0;

        mItemCount = mAdapter == null ? 0 : mAdapter.getCount();
        if (mItemCount > 0 && (widthMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.UNSPECIFIED)) {
            final View child = obtainView(0, mIsScrap);

            measureScrapChild(child, 0, widthMeasureSpec);

            childWidth = child.getMeasuredWidth();
            childHeight = child.getMeasuredHeight();
            childState = combineMeasuredStates(childState, child.getMeasuredState());

            if (recycleOnMeasure() && mRecycler.shouldRecycleViewType(((LayoutParams) child.getLayoutParams()).viewType)) {
                mRecycler.addScrapView(child, -1);
            }
        }

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            widthSize = mListPadding.left + mListPadding.right + childWidth + getVerticalScrollbarWidth();
        } else {
            widthSize |= (childState & MEASURED_STATE_MASK);
        }

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = mListPadding.top + mListPadding.bottom + childHeight + getVerticalFadingEdgeLength() * 2;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            // TODO: after first layout we should maybe start at the first visible position, not 0
            if (isMeasueHeightOfChildren()) {
                heightSize = measureHeightOfChildren(widthMeasureSpec, 0, NO_POSITION, heightSize, -1);
            }
        }

        setMeasuredDimension(widthSize, heightSize);
        mWidthMeasureSpec = widthMeasureSpec;
    }

    protected boolean isMeasueHeightOfChildren() {
        return true;
    }

    /**
     * Obtain the view and add it to our list of children. The view can be made
     * fresh, converted from an unused view, or used as is if it was in the
     * recycle bin.
     *
     * @param position     Logical position in the list
     * @param y            Top or bottom edge of the view to add
     * @param flow         If flow is true, align top edge to y. If false, align bottom
     *                     edge to y.
     * @param childrenLeft Left edge where children should be positioned
     * @param selected     Is this position selected?
     * @return View that was added
     */
    private View makeAndAddView(int position, int y, boolean flow, int childrenLeft, boolean selected) {
        View child;

        if (!mDataChanged) {
            // Try to use an existing view for this position
            child = mRecycler.getActiveView(position);
            if (child != null) {
                // Found it -- we're using an existing child
                // This just needs to be positioned
                setupChild(child, position, y, flow, childrenLeft, selected, true);

                return child;
            }
        }

        // Make a new view for this position, or convert an unused view if
        // possible
        child = obtainView(position, mIsScrap);

        // This needs to be positioned and measured
        setupChild(child, position, y, flow, childrenLeft, selected, mIsScrap[0]);

        return child;
    }

    /**
     * Add a view as a child and make sure it is measured (if necessary) and
     * positioned properly.
     *
     * @param child        The view to add
     * @param position     The position of this child
     * @param y            The y position relative to which this view will be positioned
     * @param flowDown     If true, align top edge to y. If false, align bottom edge to
     *                     y.
     * @param childrenLeft Left edge where children should be positioned
     * @param selected     Is this position selected?
     * @param recycled     Has this view been pulled from the recycle bin? If so it does
     *                     not need to be remeasured.
     */
    private void setupChild(View child, int position, int y, boolean flowDown, int childrenLeft, boolean selected, boolean recycled) {
        final boolean isSelected = selected && shouldShowSelector();
        final boolean updateChildSelected = isSelected != child.isSelected();
        final int mode = mTouchMode;
        final boolean isPressed = mode > TOUCH_MODE_DOWN && mode < TOUCH_MODE_SCROLL && mMotionPosition == position;
        final boolean updateChildPressed = isPressed != child.isPressed();
        final boolean needToMeasure = !recycled || updateChildSelected || child.isLayoutRequested();

        // Respect layout params that are already in the view. Otherwise make
        // some up...
        // noinspection unchecked
        AbsListView.LayoutParams p = (AbsListView.LayoutParams) child.getLayoutParams();
        if (p == null) {
            p = (AbsListView.LayoutParams) generateDefaultLayoutParams();
        }
        p.viewType = mAdapter.getItemViewType(position);

        if ((recycled && !p.forceAdd) || (p.recycledHeaderFooter && p.viewType == ITEM_VIEW_TYPE_HEADER_OR_FOOTER)) {
            attachViewToParent(child, flowDown ? -1 : 0, p);
        } else {
            p.forceAdd = false;
            if (p.viewType == ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
                p.recycledHeaderFooter = true;
            }
            addViewInLayout(child, flowDown ? -1 : 0, p, true);
        }

        if (updateChildSelected) {
            child.setSelected(isSelected);
        }

        if (updateChildPressed) {
            child.setPressed(isPressed);
        }

        if (mChoiceMode != CHOICE_MODE_NONE && mCheckStates != null) {
            if (child instanceof Checkable) {
                ((Checkable) child).setChecked(mCheckStates.get(position));
            } else if (getContext().getApplicationInfo().targetSdkVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                child.setActivated(mCheckStates.get(position));
            }
        }

        if (needToMeasure) {
            int childWidthSpec = getChildMeasureSpec(mWidthMeasureSpec, mListPadding.left + mListPadding.right, p.width);
            int lpHeight = p.height;
            int childHeightSpec;
            if (lpHeight > 0) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
            } else {
                childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }
            child.measure(childWidthSpec, childHeightSpec);
        } else {
            cleanupLayoutState(child);
        }

        final int w = child.getMeasuredWidth();
        final int h = child.getMeasuredHeight();
        final int childTop = flowDown ? y : y - h;

        if (needToMeasure) {
            final int childRight = childrenLeft + w;
            final int childBottom = childTop + h;
            child.layout(childrenLeft, childTop, childRight, childBottom);
        } else {
            child.offsetLeftAndRight(childrenLeft - child.getLeft());
            child.offsetTopAndBottom(childTop - child.getTop());
        }

        if (mCachingStarted && !child.isDrawingCacheEnabled()) {
            child.setDrawingCacheEnabled(true);
        }

        if (recycled && (((AbsListView.LayoutParams) child.getLayoutParams()).scrappedFromPosition) != position) {
            child.jumpDrawablesToCurrentState();
        }
    }

    private void measureScrapChild(View child, int position, int widthMeasureSpec) {
        LayoutParams p = (LayoutParams) child.getLayoutParams();
        if (p == null) {
            p = (AbsListView.LayoutParams) generateDefaultLayoutParams();
            child.setLayoutParams(p);
        }
        p.viewType = mAdapter.getItemViewType(position);
        p.forceAdd = true;

        int childWidthSpec = getChildMeasureSpec(widthMeasureSpec, mListPadding.left + mListPadding.right, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * @return True to recycle the views used to measure this ListView in
     * UNSPECIFIED/AT_MOST modes, false otherwise.
     */
    protected boolean recycleOnMeasure() {
        return true;
    }

    /**
     * Measures the height of the given range of children (inclusive) and
     * returns the height with this ListView's padding and divider heights
     * included. If maxHeight is provided, the measuring will stop when the
     * current height reaches maxHeight.
     *
     * @param widthMeasureSpec             The width measure spec to be given to a child's
     *                                     {@link View#measure(int, int)}.
     * @param startPosition                The position of the first child to be shown.
     * @param endPosition                  The (inclusive) position of the last child to be shown.
     *                                     Specify {@link #NO_POSITION} if the last child should be the
     *                                     last available child from the adapter.
     * @param maxHeight                    The maximum height that will be returned (if all the children
     *                                     don't fit in this value, this value will be returned).
     * @param disallowPartialChildPosition In general, whether the returned height should only contain
     *                                     entire children. This is more powerful--it is the first
     *                                     inclusive position at which partial children will not be
     *                                     allowed. Example: it looks nice to have at least 3 completely
     *                                     visible children, and in portrait this will most likely fit;
     *                                     but in landscape there could be times when even 2 children can
     *                                     not be completely shown, so a value of 2 (remember, inclusive)
     *                                     would be good (assuming startPosition is 0).
     * @return The height of this ListView with the given children.
     */
    final int measureHeightOfChildren(int widthMeasureSpec, int startPosition, int endPosition, final int maxHeight, int disallowPartialChildPosition) {

        final ListAdapter adapter = mAdapter;
        if (adapter == null) {
            return mListPadding.top + mListPadding.bottom;
        }

        // Include the padding of the list
        int returnedHeight = mListPadding.top + mListPadding.bottom;
        final int dividerHeight = 0;// ((mDividerHeight > 0) && mDivider !=
        // null) ? mDividerHeight : 0;
        // The previous height value that was less than maxHeight and contained
        // no partial children
        int prevHeightWithoutPartialChild = 0;
        int i;
        View child;

        // mItemCount - 1 since endPosition parameter is inclusive
        endPosition = (endPosition == NO_POSITION) ? adapter.getCount() - 1 : endPosition;
        final AbsListView.RecycleBin recycleBin = mRecycler;
        final boolean recyle = recycleOnMeasure();
        final boolean[] isScrap = mIsScrap;

        for (i = startPosition; i <= endPosition; ++i) {
            child = obtainView(i, isScrap);

            measureScrapChild(child, i, widthMeasureSpec);

            if (i > 0) {
                // Count the divider for all but one child
                returnedHeight += dividerHeight;
            }

            // Recycle the view before we possibly return from the method
            if (recyle && recycleBin.shouldRecycleViewType(((LayoutParams) child.getLayoutParams()).viewType)) {
                recycleBin.addScrapView(child, -1);
            }

            returnedHeight += child.getMeasuredHeight();

            if (returnedHeight >= maxHeight) {
                // We went over, figure out which height to return. If
                // returnedHeight > maxHeight,
                // then the i'th position did not fit completely.
                return (disallowPartialChildPosition >= 0) // Disallowing is
                        // enabled (> -1)
                        && (i > disallowPartialChildPosition) // We've past the
                        // min pos
                        && (prevHeightWithoutPartialChild > 0) // We have a prev
                        // height
                        && (returnedHeight != maxHeight) // i'th child did not
                        // fit completely
                        ? prevHeightWithoutPartialChild : maxHeight;
            }

            if ((disallowPartialChildPosition >= 0) && (i >= disallowPartialChildPosition)) {
                prevHeightWithoutPartialChild = returnedHeight;
            }
        }

        // At this point, we went through the range of children, and they each
        // completely fit, so return the returnedHeight
        return returnedHeight;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Dispatch in the normal way
        boolean handled = super.dispatchKeyEvent(event);
        if (!handled) {
            // If we didn't handle it...
            View focused = getFocusedChild();
            if (focused != null && event.getAction() == KeyEvent.ACTION_DOWN) {
                // ... and our focused child didn't handle it
                // ... give it to ourselves so we can scroll if necessary
                handled = onKeyDown(event.getKeyCode(), event);
            }
        }
        return handled;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return commonKey(keyCode, 1, event);
    }

    private boolean commonKey(int keyCode, int count, KeyEvent event) {
        if (mAdapter == null || !mIsAttached) {
            return false;
        }

        if (mDataChanged) {
            layoutChildren();
        }

        boolean handled = false;
        int action = event.getAction();
        int navigation = SoundEffectConstants.NAVIGATION_LEFT;
        if (action != KeyEvent.ACTION_UP) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (event.hasNoModifiers()) {
                        handled = resurrectSelectionIfNeeded();
                        if (!handled) {
                            while (count-- > 0) {
                                if (arrowScroll(FOCUS_UP)) {
                                    handled = true;
                                } else {
                                    break;
                                }
                            }
                        }
                    } else if (event.hasModifiers(KeyEvent.META_ALT_ON)) {
                        handled = resurrectSelectionIfNeeded() || fullScroll(FOCUS_UP);
                    }
                    navigation = SoundEffectConstants.NAVIGATION_UP;
                    break;

                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (event.hasNoModifiers()) {
                        handled = resurrectSelectionIfNeeded();
                        if (!handled) {
                            while (count-- > 0) {
                                if (arrowScroll(FOCUS_DOWN)) {
                                    handled = true;
                                } else {
                                    break;
                                }
                            }
                        }
                    } else if (event.hasModifiers(KeyEvent.META_ALT_ON)) {
                        handled = resurrectSelectionIfNeeded() || fullScroll(FOCUS_DOWN);
                    }
                    navigation = SoundEffectConstants.NAVIGATION_DOWN;
                    break;

                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (event.hasNoModifiers()) {
                        handled = handleHorizontalFocusWithinListItem(FOCUS_LEFT);
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (event.hasNoModifiers()) {
                        handled = handleHorizontalFocusWithinListItem(FOCUS_RIGHT);
                    }
                    navigation = SoundEffectConstants.NAVIGATION_RIGHT;
                    break;

                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    if (event.hasNoModifiers()) {
                        handled = resurrectSelectionIfNeeded();
                        if (!handled && event.getRepeatCount() == 0 && getVisibleChildCount() > 0) {
                            keyPressed();
                            handled = true;
                        }
                    }
                    break;

                case KeyEvent.KEYCODE_SPACE:
                    // if (mPopup == null || !mPopup.isShowing()) {
                    // if (event.hasNoModifiers()) {
                    // handled = resurrectSelectionIfNeeded() ||
                    // pageScroll(FOCUS_DOWN);
                    // } else if (event.hasModifiers(KeyEvent.META_SHIFT_ON)) {
                    // handled = resurrectSelectionIfNeeded() ||
                    // pageScroll(FOCUS_UP);
                    // }
                    // handled = true;
                    // }
                    break;

                case KeyEvent.KEYCODE_PAGE_UP:
                    if (event.hasNoModifiers()) {
                        handled = resurrectSelectionIfNeeded() || pageScroll(FOCUS_UP);
                    } else if (event.hasModifiers(KeyEvent.META_ALT_ON)) {
                        handled = resurrectSelectionIfNeeded() || fullScroll(FOCUS_UP);
                    }
                    navigation = SoundEffectConstants.NAVIGATION_UP;
                    break;

                case KeyEvent.KEYCODE_PAGE_DOWN:
                    if (event.hasNoModifiers()) {
                        handled = resurrectSelectionIfNeeded() || pageScroll(FOCUS_DOWN);
                    } else if (event.hasModifiers(KeyEvent.META_ALT_ON)) {
                        handled = resurrectSelectionIfNeeded() || fullScroll(FOCUS_DOWN);
                    }
                    navigation = SoundEffectConstants.NAVIGATION_DOWN;
                    break;

                case KeyEvent.KEYCODE_MOVE_HOME:
                    if (event.hasNoModifiers()) {
                        handled = resurrectSelectionIfNeeded() || fullScroll(FOCUS_UP);
                    }
                    break;

                case KeyEvent.KEYCODE_MOVE_END:
                    if (event.hasNoModifiers()) {
                        handled = resurrectSelectionIfNeeded() || fullScroll(FOCUS_DOWN);
                    }
                    break;

                case KeyEvent.KEYCODE_TAB:
                    // XXX Sometimes it is useful to be able to TAB through the
                    // items in
                    // a ListView sequentially. Unfortunately this can create an
                    // asymmetry in TAB navigation order unless the list selection
                    // always reverts to the top or bottom when receiving TAB focus
                    // from
                    // another widget. Leaving this behavior disabled for now but
                    // perhaps it should be configurable (and more comprehensive).
                    if (false) {
                        if (event.hasNoModifiers()) {
                            handled = resurrectSelectionIfNeeded() || arrowScroll(FOCUS_DOWN);
                        } else if (event.hasModifiers(KeyEvent.META_SHIFT_ON)) {
                            handled = resurrectSelectionIfNeeded() || arrowScroll(FOCUS_UP);
                        }
                    }
                    break;
            }
        }

        if (handled) {
            this.playSoundEffect(navigation);
            return true;
        }

        // if (sendToTextFilter(keyCode, count, event)) {
        // return true;
        // }

        switch (action) {
            case KeyEvent.ACTION_DOWN:
                return super.onKeyDown(keyCode, event);

            case KeyEvent.ACTION_UP:
                return super.onKeyUp(keyCode, event);

            case KeyEvent.ACTION_MULTIPLE:
                return super.onKeyMultiple(keyCode, count, event);

            default: // shouldn't happen
                return false;
        }
    }

    /**
     * Scrolls to the next or previous item if possible.
     *
     * @param direction either {@link View#FOCUS_UP} or {@link View#FOCUS_DOWN}
     * @return whether selection was moved
     */
    boolean arrowScroll(int direction) {
        try {
            mInLayout = true;
            final boolean handled = arrowScrollImpl(direction);
            if (handled) {
                playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
            }
            return handled;
        } finally {
            mInLayout = false;
        }
    }

    /**
     * Holds results of focus aware arrow scrolling.
     */
    static private class ArrowScrollFocusResult {
        private int mSelectedPosition;
        private int mAmountToScroll;

        /**
         * How {@link android.widget.ListView#arrowScrollFocused} returns its
         * values.
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

    /**
     * Handle an arrow scroll going up or down. Take into account whether items
     * are selectable, whether there are focusable items etc.
     *
     * @param direction Either {@link View#FOCUS_UP} or
     *                  {@link View#FOCUS_DOWN}.
     * @return Whether any scrolling, selection or focus change occured.
     */
    private boolean arrowScrollImpl(int direction) {
        if (getVisibleChildCount() <= 0) {
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
            scrollListItemsBy((direction == FOCUS_UP) ? amountToScroll : -amountToScroll);
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

        // if the current selection is panned off, we need to remove the
        // selection
        if (nextSelectedPosition == INVALID_POSITION && selectedView != null && !isViewAncestorOf(selectedView, this)) {
            selectedView = null;
            hideSelector();

            // but we don't want to set the ressurect position (that would make
            // subsequent
            // unhandled key events bring back the item we just scrolled off!)
            mResurrectToPosition = INVALID_POSITION;
        }

        if (needToRedraw) {
            if (selectedView != null) {
                positionSelector(selectedPos, selectedView);
                // mSelectedTop = selectedView.getTop();
            }
            if (!awakenScrollBars()) {
                invalidate();
            }
            // invokeOnItemScrollListener();
            return true;
        }

        return false;
    }

    /**
     * Scroll the children by amount, adding a view at the end and removing
     * views that fall off as necessary.
     *
     * @param amount The amount (positive or negative) to scroll.
     */
    private void scrollListItemsBy(int amount) {
        offsetChildrenTopAndBottom(amount);

        final int listBottom = getHeight() - mListPadding.bottom;
        final int listTop = mListPadding.top;
        final AbsListView.RecycleBin recycleBin = mRecycler;

        if (amount < 0) {
            // shifted items up

            // may need to pan views into the bottom space
            int visibleChildCount = getVisibleChildCount();
            View last = getLastVisibleChild();
            while (last.getBottom() < listBottom) {
                final int lastVisiblePosition = getLastVisiblePosition();
                if (lastVisiblePosition < mItemCount - 1) {
                    last = addViewBelow(last, lastVisiblePosition);
                    visibleChildCount++;
                } else {
                    break;
                }
            }

            // may have brought in the last child of the list that is skinnier
            // than the fading edge, thereby leaving space at the end. need
            // to shift back
            if (last.getBottom() < listBottom) {
                offsetChildrenTopAndBottom(listBottom - last.getBottom());
            }

            // top views may be panned off screen
            View firstVisibleChild = getFirstVisibleChild();
            while (firstVisibleChild.getBottom() < listTop) {
                View firstChild = getFirstChild();
                AbsListView.LayoutParams layoutParams = (LayoutParams) firstVisibleChild.getLayoutParams();
                if (recycleBin.shouldRecycleViewType(layoutParams.viewType)) {
                    detachViewFromParent(firstChild);
                    recycleBin.addScrapView(firstChild, getFirstPosition());
                } else {
                    removeViewInLayout(firstChild);
                }
                firstVisibleChild = getFirstVisibleChild();
                mFirstPosition++;
            }
        } else {
            // shifted items down
            View first = getFirstVisibleChild();

            // may need to pan views into top
            while ((first.getTop() > listTop) && (mFirstPosition > 0)) {
                first = addViewAbove(first, mFirstPosition);
                mFirstPosition--;
            }

            // may have brought the very first child of the list in too far and
            // need to shift it back
            if (first.getTop() > listTop) {
                offsetChildrenTopAndBottom(listTop - first.getTop());
            }

            View lastVisibleChild = getLastVisibleChild();

            // bottom view may be panned off screen
            while (lastVisibleChild.getTop() > listBottom) {
                View lastChild = getLastChild();
                AbsListView.LayoutParams layoutParams = (LayoutParams) lastVisibleChild.getLayoutParams();
                if (recycleBin.shouldRecycleViewType(layoutParams.viewType)) {
                    detachViewFromParent(lastChild);
                    recycleBin.addScrapView(lastChild, getLastPosition());
                } else {
                    removeViewInLayout(lastChild);
                }
                lastVisibleChild = getLastVisibleChild();
            }
        }
    }

    private View addViewAbove(View theView, int position) {
        int abovePosition = position - 1 - mUpPreLoadedCount;
        View view = obtainView(abovePosition, mIsScrap);
        if (view != null) {
            int edgeOfNewChild = theView.getTop() - mDividerHeight - mSpacing;
            setupChild(view, abovePosition, edgeOfNewChild, false, mListPadding.left, false, mIsScrap[0]);
            return view;
        } else {
            mUpPreLoadedCount--;
            return getFirstChild();
        }
    }

    private View addViewBelow(View theView, int position) {
        int belowPosition = position + 1 + mDownPreLoadedCount;
        View view = obtainView(belowPosition, mIsScrap);
        if (view != null) {
            int edgeOfNewChild = getLastChild().getBottom() + mDividerHeight + mSpacing;
            setupChild(view, belowPosition, edgeOfNewChild, true, mListPadding.left, false, mIsScrap[0]);
            return view;
        } else {
            mDownPreLoadedCount--;
            return getLastChild();
        }
    }

    /**
     * Determine the distance to the nearest edge of a view in a particular
     * direction.
     *
     * @param descendant A descendant of this list.
     * @return The distance, or 0 if the nearest edge is already on screen.
     */
    private int distanceToView(View descendant) {
        int distance = 0;
        descendant.getDrawingRect(mTempRect);
        offsetDescendantRectToMyCoords(descendant, mTempRect);
        final int listBottom = getBottom() - getTop() - mListPadding.bottom;
        if (mTempRect.bottom < mListPadding.top) {
            distance = mListPadding.top - mTempRect.bottom;
        } else if (mTempRect.top > listBottom) {
            distance = mTempRect.top - listBottom;
        }
        return distance;
    }

    /**
     * Return true if child is an ancestor of parent, (or equal to the parent).
     */
    private boolean isViewAncestorOf(View child, View parent) {
        if (child == parent) {
            return true;
        }

        final ViewParent theParent = child.getParent();
        return (theParent instanceof ViewGroup) && isViewAncestorOf((View) theParent, parent);
    }

    /**
     * When selection changes, it is possible that the previously selected or
     * the next selected item will change its size. If so, we need to offset
     * some folks, and re-layout the items as appropriate.
     *
     * @param selectedView        The currently selected view (before changing selection).
     *                            should be <code>null</code> if there was no previous
     *                            selection.
     * @param direction           Either {@link View#FOCUS_UP} or
     *                            {@link View#FOCUS_DOWN}.
     * @param newSelectedPosition The position of the next selection.
     * @param newFocusAssigned    whether new focus was assigned. This matters because when
     *                            something has focus, we don't want to show selection (ugh).
     */
    private void handleNewSelectionChange(View selectedView, int direction, int newSelectedPosition, boolean newFocusAssigned) {
        if (newSelectedPosition == INVALID_POSITION) {
            throw new IllegalArgumentException("newSelectedPosition needs to be valid");
        }

        // whether or not we are moving down or up, we want to preserve the
        // top of whatever view is on top:
        // - moving down: the view that had selection
        // - moving up: the view that is getting selection
        View topView;
        View bottomView;
        int topViewIndex, bottomViewIndex;
        boolean topSelected = false;
        final int selectedIndex = mSelectedPosition - mFirstPosition;
        final int nextSelectedIndex = newSelectedPosition - mFirstPosition;
        if (direction == FOCUS_UP) {
            topViewIndex = nextSelectedIndex;
            bottomViewIndex = selectedIndex;
            topView = getChildAt(topViewIndex);
            bottomView = selectedView;
            topSelected = true;
        } else {
            topViewIndex = selectedIndex;
            bottomViewIndex = nextSelectedIndex;
            topView = selectedView;
            bottomView = getChildAt(bottomViewIndex);
        }

        final int numChildren = getChildCount();

        // start with top view: is it changing size?
        if (topView != null) {
            topView.setSelected(!newFocusAssigned && topSelected);
            measureAndAdjustDown(topView, topViewIndex, numChildren);
        }

        // is the bottom view changing size?
        if (bottomView != null) {
            bottomView.setSelected(!newFocusAssigned && !topSelected);
            measureAndAdjustDown(bottomView, bottomViewIndex, numChildren);
        }
    }

    /**
     * Re-measure a child, and if its height changes, lay it out preserving its
     * top, and adjust the children below it appropriately.
     *
     * @param child       The child
     * @param childIndex  The view group index of the child.
     * @param numChildren The number of children in the view group.
     */
    private void measureAndAdjustDown(View child, int childIndex, int numChildren) {
        int oldHeight = child.getHeight();
        measureItem(child);
        if (child.getMeasuredHeight() != oldHeight) {
            // lay out the view, preserving its top
            relayoutMeasuredItem(child);

            // adjust views below appropriately
            final int heightDelta = child.getMeasuredHeight() - oldHeight;
            for (int i = childIndex + 1; i < numChildren; i++) {
                getChildAt(i).offsetTopAndBottom(heightDelta);
            }
        }
    }

    /**
     * Measure a particular list child. TODO: unify with setUpChild.
     *
     * @param child The child.
     */
    private void measureItem(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = getChildMeasureSpec(mWidthMeasureSpec, mListPadding.left + mListPadding.right, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * Layout a child that has been measured, preserving its top position. TODO:
     * unify with setUpChild.
     *
     * @param child The child.
     */
    private void relayoutMeasuredItem(View child) {
        final int w = child.getMeasuredWidth();
        final int h = child.getMeasuredHeight();
        final int childLeft = mListPadding.left;
        final int childRight = childLeft + w;
        final int childTop = child.getTop();
        final int childBottom = childTop + h;
        child.layout(childLeft, childTop, childRight, childBottom);
    }

    /**
     * Do an arrow scroll based on focus searching. If a new view is given
     * focus, return the selection delta and amount to scroll via an
     * {@link ArrowScrollFocusResult}, otherwise, return null.
     *
     * @param direction either {@link View#FOCUS_UP} or
     *                  {@link View#FOCUS_DOWN}.
     * @return The result if focus has changed, or <code>null</code>.
     */
    private ArrowScrollFocusResult arrowScrollFocused(final int direction) {
        final View selectedView = getSelectedView();
        View newFocus;
        if (selectedView != null && selectedView.hasFocus()) {
            View oldFocus = selectedView.findFocus();
            newFocus = FocusFinder.getInstance().findNextFocus(this, oldFocus, direction);
        } else {
            if (direction == FOCUS_DOWN) {
                final boolean topFadingEdgeShowing = (mFirstPosition > 0);
                final int listTop = mListPadding.top + (topFadingEdgeShowing ? getArrowScrollPreviewLength() : 0);
                final int ySearchPoint = (selectedView != null && selectedView.getTop() > listTop) ? selectedView.getTop() : listTop;
                mTempRect.set(0, ySearchPoint, 0, ySearchPoint);
            } else {
                final boolean bottomFadingEdgeShowing = (mFirstPosition + getChildCount() - 1) < mItemCount;
                final int listBottom = getHeight() - mListPadding.bottom - (bottomFadingEdgeShowing ? getArrowScrollPreviewLength() : 0);
                final int ySearchPoint = (selectedView != null && selectedView.getBottom() < listBottom) ? selectedView.getBottom() : listBottom;
                mTempRect.set(0, ySearchPoint, 0, ySearchPoint);
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
                        && ((direction == FOCUS_DOWN && selectablePosition < positionOfNewFocus) || (direction == FOCUS_UP && selectablePosition > positionOfNewFocus))) {
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
                // max scroll amount, we are getting it at least partially in
                // view,
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
     *
     * @param direction          either {@link View#FOCUS_UP} or
     *                           {@link View#FOCUS_DOWN}.
     * @param newFocus           The view that would take focus.
     * @param positionOfNewFocus The position of the list item containing newFocus
     * @return The amount to scroll. Note: this is always positive! Direction
     * needs to be taken into account when actually scrolling.
     */
    private int amountToScrollToNewFocus(int direction, View newFocus, int positionOfNewFocus) {
        int amountToScroll = 0;
        newFocus.getDrawingRect(mTempRect);
        offsetDescendantRectToMyCoords(newFocus, mTempRect);
        if (direction == FOCUS_UP) {
            if (mTempRect.top < mListPadding.top) {
                amountToScroll = mListPadding.top - mTempRect.top;
                if (positionOfNewFocus > 0) {
                    amountToScroll += getArrowScrollPreviewLength();
                }
            }
        } else {
            final int listBottom = getHeight() - mListPadding.bottom;
            if (mTempRect.bottom > listBottom) {
                amountToScroll = mTempRect.bottom - listBottom;
                if (positionOfNewFocus < mItemCount - 1) {
                    amountToScroll += getArrowScrollPreviewLength();
                }
            }
        }
        return amountToScroll;
    }

    /**
     * @param newFocus The view that would have focus.
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
     * @param direction either {@link View#FOCUS_UP} or
     *                  {@link View#FOCUS_DOWN}.
     * @return The position of the next selectable position of the views that
     * are currently visible, taking into account the fact that there
     * might be no selection. Returns {@link #INVALID_POSITION} if there
     * is no selectable view on screen in the given direction.
     */
    protected int lookForSelectablePositionOnScreen(int direction) {
        final int firstPosition = getFirstVisiblePosition();
        if (direction == FOCUS_DOWN) {
            int startPos = (mSelectedPosition != INVALID_POSITION) ? mSelectedPosition + 1 : firstPosition;
            if (startPos >= mAdapter.getCount()) {
                return INVALID_POSITION;
            }
            if (startPos < firstPosition) {
                startPos = firstPosition;
            }

            final int lastVisiblePos = getLastVisiblePosition();
            final ListAdapter adapter = getAdapter();
            for (int pos = startPos; pos <= lastVisiblePos; pos++) {
                if (adapter.isEnabled(pos) && getChildAt(pos - firstPosition).getVisibility() == VISIBLE) {
                    return pos;
                }
            }
        } else {
            int last = getLastVisiblePosition();
            int startPos = (mSelectedPosition != INVALID_POSITION) ? mSelectedPosition - 1 : last;
            if (startPos < 0 || startPos >= mAdapter.getCount()) {
                return INVALID_POSITION;
            }
            if (startPos > last) {
                startPos = last;
            }

            final ListAdapter adapter = getAdapter();
            for (int pos = startPos; pos >= firstPosition; pos--) {
                if (adapter.isEnabled(pos) && getChildAt(pos - firstPosition).getVisibility() == VISIBLE) {
                    return pos;
                }
            }
        }
        return INVALID_POSITION;
    }

    /**
     * Determine how much we need to scroll in order to get the next selected
     * view visible, with a fading edge showing below as applicable. The amount
     * is capped at {@link #getMaxScrollAmount()} .
     *
     * @param direction            either {@link View#FOCUS_UP} or
     *                             {@link View#FOCUS_DOWN}.
     * @param nextSelectedPosition The position of the next selection, or
     *                             {@link #INVALID_POSITION} if there is no next selectable
     *                             position
     * @return The amount to scroll. Note: this is always positive! Direction
     * needs to be taken into account when actually scrolling.
     */
    int amountToScroll(int direction, int nextSelectedPosition) {
        final int listBottom = getHeight() - mListPadding.bottom;
        final int listTop = mListPadding.top;

        final int numChildren = getVisibleChildCount();

        if (direction == FOCUS_DOWN) {
            int indexToMakeVisible = numChildren - 1;
            if (nextSelectedPosition != INVALID_POSITION) {
                indexToMakeVisible = nextSelectedPosition - getFirstVisiblePosition();
            }

            final int positionToMakeVisible = getFirstVisiblePosition() + indexToMakeVisible;
            final View viewToMakeVisible = getChildAt(indexToMakeVisible);

            int goalBottom = listBottom;
            if (positionToMakeVisible < mItemCount - 1) {
                goalBottom -= getArrowScrollPreviewLength();
            }

            if (viewToMakeVisible.getBottom() <= goalBottom) {
                // item is fully visible.
                return 0;
            }

            if (nextSelectedPosition != INVALID_POSITION && (goalBottom - viewToMakeVisible.getTop()) >= getMaxScrollAmount()) {
                // item already has enough of it visible, changing selection is
                // good enough
                return 0;
            }

            int amountToScroll = (viewToMakeVisible.getBottom() - goalBottom);

            if ((mFirstPosition + numChildren) == mItemCount) {
                // last is last in list -> make sure we don't scroll past it
                final int max = getChildAt(numChildren - 1).getBottom() - listBottom;
                amountToScroll = Math.min(amountToScroll, max);
            }

            return Math.min(amountToScroll, getMaxScrollAmount());
        } else {
            int indexToMakeVisible = 0;
            if (nextSelectedPosition != INVALID_POSITION) {
                indexToMakeVisible = nextSelectedPosition - getFirstVisiblePosition();
            }
            final int positionToMakeVisible = getFirstVisiblePosition() + indexToMakeVisible;
            final View viewToMakeVisible = getChildAt(indexToMakeVisible);
            int goalTop = listTop;
            if (positionToMakeVisible > 0) {
                goalTop += getArrowScrollPreviewLength();
            }
            if (viewToMakeVisible.getTop() >= goalTop) {
                // item is fully visible.
                return 0;
            }

            if (nextSelectedPosition != INVALID_POSITION && (viewToMakeVisible.getBottom() - goalTop) >= getMaxScrollAmount()) {
                // item already has enough of it visible, changing selection is
                // good enough
                return 0;
            }

            int amountToScroll = (goalTop - viewToMakeVisible.getTop());
            if (getFirstVisiblePosition() == 0) {
                // first is first in list -> make sure we don't scroll past it
                final int max = listTop - getChildAt(0).getTop();
                amountToScroll = Math.min(amountToScroll, max);
            }
            return Math.min(amountToScroll, getMaxScrollAmount());
        }
    }

    /**
     * @return The amount to preview next items when arrow srolling.
     */
    protected int getArrowScrollPreviewLength() {
        return Math.max(MIN_SCROLL_PREVIEW_PIXELS, getVerticalFadingEdgeLength());
    }

    /**
     * @return The maximum amount a list view will scroll in response to an
     * arrow event.
     */
    public int getMaxScrollAmount() {
        return (int) (MAX_SCROLL_FACTOR * (getBottom() - getTop()));
    }

    /**
     * Go to the last or first item if possible (not worrying about panning
     * across or navigating within the internal focus of the currently selected
     * item.)
     *
     * @param direction either {@link View#FOCUS_UP} or {@link View#FOCUS_DOWN}
     * @return whether selection was moved
     */
    boolean fullScroll(int direction) {
        boolean moved = false;
        if (direction == FOCUS_UP) {
            if (mSelectedPosition != 0) {
                int position = lookForSelectablePosition(0, true);
                if (position >= 0) {
                    mLayoutMode = LAYOUT_FORCE_TOP;
                    setSelectionInt(position);
                    // TODO
                    // invokeOnItemScrollListener();
                }
                moved = true;
            }
        } else if (direction == FOCUS_DOWN) {
            if (mSelectedPosition < mItemCount - 1) {
                int position = lookForSelectablePosition(mItemCount - 1, true);
                if (position >= 0) {
                    mLayoutMode = LAYOUT_FORCE_BOTTOM;
                    setSelectionInt(position);
                    // TODO
                    // invokeOnItemScrollListener();
                }
                moved = true;
            }
        }

        if (moved && !awakenScrollBars()) {
            awakenScrollBars();
            invalidate();
        }

        return moved;
    }

    /**
     * To avoid horizontal focus searches changing the selected item, we
     * manually focus search within the selected item (as applicable), and
     * prevent focus from jumping to something within another item.
     *
     * @param direction one of {View.FOCUS_LEFT, View.FOCUS_RIGHT}
     * @return Whether this consumes the key event.
     */
    private boolean handleHorizontalFocusWithinListItem(int direction) {
        if (direction != FOCUS_LEFT && direction != FOCUS_RIGHT) {
            throw new IllegalArgumentException("direction must be one of" + " {View.FOCUS_LEFT, View.FOCUS_RIGHT}");
        }

        final int numChildren = getVisibleChildCount();
        if (mItemsCanFocus && numChildren > 0 && mSelectedPosition != INVALID_POSITION) {
            final View selectedView = getSelectedView();
            if (selectedView != null && selectedView.hasFocus() && selectedView instanceof ViewGroup) {

                final View currentFocus = selectedView.findFocus();
                final View nextFocus = FocusFinder.getInstance().findNextFocus((ViewGroup) selectedView, currentFocus, direction);
                if (nextFocus != null) {
                    // do the math to get interesting rect in next focus'
                    // coordinates
                    currentFocus.getFocusedRect(mTempRect);
                    offsetDescendantRectToMyCoords(currentFocus, mTempRect);
                    offsetRectIntoDescendantCoords(nextFocus, mTempRect);
                    if (nextFocus.requestFocus(direction, mTempRect)) {
                        return true;
                    }
                }
                // we are blocking the key from being handled (by returning
                // true)
                // if the global result is going to be some other view within
                // this
                // list. this is to acheive the overall goal of having
                // horizontal d-pad navigation remain in the current item.
                final View globalNextFocus = FocusFinder.getInstance().findNextFocus((ViewGroup) getRootView(), currentFocus, direction);
                if (globalNextFocus != null) {
                    return isViewAncestorOf(globalNextFocus, this);
                }
            }
        }
        return false;
    }

    /**
     * Scrolls up or down by the number of items currently present on screen.
     *
     * @param direction either {@link View#FOCUS_UP} or {@link View#FOCUS_DOWN}
     * @return whether selection was moved
     */
    boolean pageScroll(int direction) {
        int nextPage = -1;
        boolean down = false;

        if (direction == FOCUS_UP) {
            nextPage = Math.max(0, mSelectedPosition - getVisibleChildCount() - 1);
        } else if (direction == FOCUS_DOWN) {
            nextPage = Math.min(mItemCount - 1, mSelectedPosition + getVisibleChildCount() - 1);
            down = true;
        }

        if (nextPage >= 0) {
            int position = lookForSelectablePosition(nextPage, down);
            if (position >= 0) {
                mLayoutMode = LAYOUT_SPECIFIC;
                mSpecificTop = getPaddingTop() + getVerticalFadingEdgeLength();

                if (down && position > mItemCount - getVisibleChildCount()) {
                    mLayoutMode = LAYOUT_FORCE_BOTTOM;
                }

                if (!down && position < getVisibleChildCount()) {
                    mLayoutMode = LAYOUT_FORCE_TOP;
                }

                setSelectionInt(position);
                // TODO
                // invokeOnItemScrollListener();
                if (!awakenScrollBars()) {
                    invalidate();
                }

                return true;
            }
        }

        return false;
    }

    /**
     * Sets the currently selected item. If in touch mode, the item will not be
     * selected but it will still be positioned appropriately. If the specified
     * selection position is less than 0, then the item at position 0 will be
     * selected.
     *
     * @param position Index (starting at 0) of the data item to be selected.
     */
    @Override
    public void setSelection(int position) {
        setSelectionFromTop(position, mListPadding.top);
    }

    /**
     * Sets the selected item and positions the selection y pixels from the top
     * edge of the ListView. (If in touch mode, the item will not be selected
     * but it will still be positioned appropriately.)
     *
     * @param position Index (starting at 0) of the data item to be selected.
     * @param y        The distance from the top edge of the ListView (plus padding)
     *                 that the item will be positioned.
     */
    public void setSelectionFromTop(int position, int y) {
        if (mAdapter == null) {
            return;
        }

        if (!isInTouchMode()) {
            position = lookForSelectablePosition(position, true);
            if (position >= 0) {
                setNextSelectedPositionInt(position);
            }
        } else {
            mResurrectToPosition = position;
        }

        if (position >= 0) {
            mLayoutMode = LAYOUT_SPECIFIC;
            mSpecificTop = /* mListPadding.top + */y;

            if (mNeedSync) {
                mSyncPosition = position;
                mSyncRowId = mAdapter.getItemId(position);
            }

            // if (mPositionScroller != null) {
            // mPositionScroller.stop();
            // }
            // requestLayout();
            layoutChildren();
        }
    }

    /**
     * Find a position that can be selected (i.e., is not a separator).
     *
     * @param position The starting position to look at.
     * @param lookDown Whether to look down for other positions.
     * @return The next selectable position starting at position and then
     * searching either up or down. Returns {@link #INVALID_POSITION} if
     * nothing can be found.
     */
    @Override
    protected int lookForSelectablePosition(int position, boolean lookDown) {
        final ListAdapter adapter = mAdapter;
        if (adapter == null || isInTouchMode()) {
            return INVALID_POSITION;
        }

        final int count = adapter.getCount();
        if (!mAreAllItemsSelectable) {
            if (lookDown) {
                position = Math.max(0, position);
                while (position < count && !adapter.isEnabled(position)) {
                    position++;
                }
            } else {
                position = Math.min(position, count - 1);
                while (position >= 0 && !adapter.isEnabled(position)) {
                    position--;
                }
            }

            if (position < 0 || position >= count) {
                return INVALID_POSITION;
            }
            return position;
        } else {
            if (position < 0 || position >= count) {
                return INVALID_POSITION;
            }
            return position;
        }
    }

    @Override
    int findMotionRow(int y) {
        int childCount = getVisibleChildCount();
        if (childCount > 0) {
            if (!mStackFromBottom) {
                for (int i = mUpPreLoadedCount; i < childCount + mUpPreLoadedCount; i++) {
                    View v = getChildAt(i);
                    if (y <= v.getBottom()) {
                        return mFirstPosition + i;
                    }
                }
            } else {
                for (int i = childCount + mUpPreLoadedCount - 1; i >= mUpPreLoadedCount; i--) {
                    View v = getChildAt(i);
                    if (y >= v.getTop()) {
                        return mFirstPosition + i;
                    }
                }
            }
        }
        return INVALID_POSITION;
    }

    @Override
    protected void detachOffScreenChildren(boolean isDown) {
        int numChildren = getChildCount();
        int firstPosition = mFirstPosition;
        int start = 0;
        int count = 0;

        if (isDown) {
            final int top = getPaddingTop();
            final int lastVisibleIndex = getLastVisibleChildIndex();
            for (int i = getFirsVisibletChildIndex(); i <= lastVisibleIndex; i++) {
                int n = i;
                int index = i;
                View child = getChildAt(n);
                if (child == null) {
                    Log.e(TAG, TAG + ".detachOffScreenChildren.down.child == null");
                    return;
                }
                if (child.getBottom() >= top) {
                    break;
                } else {
                    start = n;
                    count++;
                    int pos = firstPosition + n;

                    if (mUpPreLoadedCount >= mPreLoadCount) {
                        child = getFirstChild();
                        pos = getFirstPosition();
                        index = 0;
                        detachViewFromParent(index);
                        mRecycler.addScrapView(child, pos);
                    } else {
                        mUpPreLoadedCount++;
                    }
                }
            }

            start = 0;
        } else {
            final int bottom = getHeight() - getPaddingBottom();
            final int firstVisibleIndex = getFirsVisibletChildIndex();
            final int lastVisibleIndex = getLastVisibleChildIndex();
            for (int i = lastVisibleIndex; i >= firstVisibleIndex; i--) {
                int n = i;
                int index = i;
                View child = getChildAt(n);
                if (child == null) {
                    Log.e(TAG, TAG + ".detachOffScreenChildren.up.child == null");
                    return;
                }
                if (child.getTop() <= bottom) {
                    break;
                } else {
                    start = n;
                    count++;
                    int pos = firstPosition + n;
                    if (mDownPreLoadedCount >= mPreLoadCount) {
                        child = getLastChild();
                        pos = getLastPosition();
                        index = getChildCount() - 1;
                        detachViewFromParent(index);
                        mRecycler.addScrapView(child, pos);
                    } else {
                        mDownPreLoadedCount++;
                    }

                }
            }
        }

        // detachViewsFromParent(start, count);

        if (isDown) {
            mFirstPosition += count;
        }
    }

    @Override
    public View getSelectedView() {
        if (mItemCount > 0 && mSelectedPosition >= 0) {
            return getChildAt(mSelectedPosition - getFirstPosition());
        } else {
            Log.e(TAG, "getSelectedView: return null! this:" + this.toString() + ", mItemCount:" + mItemCount + ", mSelectedPosition:" + mSelectedPosition);
            return null;
        }
    }
}
