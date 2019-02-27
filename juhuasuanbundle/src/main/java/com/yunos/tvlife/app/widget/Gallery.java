/*
 * Copyright (C) 2007 The Android Open Source Project
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

package com.yunos.tvlife.app.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Transformation;
import android.widget.Scroller;

/**
 * Coverflow的基类，由android的{@link android.widget.Gallery}改造而成：
 * <p>
 * 增加了选择框的绘制；
 * </p>
 * <p>
 * 增加滚动到指定位置的方法；
 * </p>
 * <p>
 * 增加自动滚动的方法；
 * </p>
 * <p>
 * 增加滚动的监听；
 * </p>
 */
public class Gallery extends AbsSpinner implements GestureDetector.OnGestureListener {

	private static final String TAG = "Gallery";
	private static final boolean DEBUG = false;

	private static final boolean localLOGV = false;
	private static final boolean localLOGD = false;
	/**
	 * Duration in milliseconds from the start of a scroll during which we're
	 * unsure whether the user is scrolling or flinging.
	 */
	private static final int SCROLL_TO_FLING_UNCERTAINTY_TIMEOUT = 250;

	/**
	 * Horizontal spacing between items.
	 */
	int mSpacing = -20;

	/**
	 * How long the transition animation should run when a child view changes
	 * position, measured in milliseconds.
	 */
	private int mAnimationDuration = 400;

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

	private int mGravity;

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
	 * mark focus
	 */
	boolean gainFocus;

	/**
	 * Executes the delta scrolls from a fling or scroll movement.
	 */
	private FlingRunnable mFlingRunnable = new FlingRunnable();

	private AutoScrollRunnable mAutoScrollRunnable = new AutoScrollRunnable();
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
	 * If true, mFirstPosition is the position of the rightmost child, and the
	 * children are ordered right to left.
	 */
	private boolean mIsRtl = false;

	private boolean mAutoScroll;

	private boolean mAutoPaused;

	private int mAutoScrollDelay = 5000;

	private OnScrollingListener mOnScrollingListener;
	private boolean isScrolling;
	protected int scrollPosition = INVALID_POSITION;

	public Gallery(Context context) {
		super(context, null);
	}

	public Gallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public Gallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mGestureDetector = new GestureDetector(context, this);
		mGestureDetector.setIsLongpressEnabled(true);

		// TypedArray a = context.obtainStyledAttributes(attrs,
		// com.android.internal.R.styleable.Gallery, defStyle, 0);

		// int index =
		// a.getInt(com.android.internal.R.styleable.Gallery_gravity, -1);
		// if (index >= 0) {
		// setGravity(index);
		// }

		setGravity(Gravity.CENTER_VERTICAL);

		// int animationDuration =
		// a.getInt(com.android.internal.R.styleable.Gallery_animationDuration,
		// -1);
		// if (animationDuration > 0) {
		// setAnimationDuration(animationDuration);
		// }

		// int spacing =
		// a.getDimensionPixelOffset(com.android.internal.R.styleable.Gallery_spacing,
		// 0);
		// setSpacing(spacing);

		// float unselectedAlpha =
		// a.getFloat(com.android.internal.R.styleable.Gallery_unselectedAlpha,
		// 0.5f);
		setUnselectedAlpha(0.85f);

		// a.recycle();

		// We draw the selected item last (because otherwise the item to the
		// right overlaps it)
		int flags = getGroupFlags();
		flags |= 0x400;

		flags |= 0x800;

		setGroupFlags(flags);
	}

	/**
	 * Whether or not to callback on any {@link #getOnItemSelectedListener()}
	 * while the items are being flinged. If false, only the final selected item
	 * will cause the callback. If true, all items between the first and the
	 * final will cause callbacks.
	 * 
	 * @param shouldCallback
	 *            Whether or not to callback on the listener while the items are
	 *            being flinged.
	 */
	public void setCallbackDuringFling(boolean shouldCallback) {
		mShouldCallbackDuringFling = shouldCallback;
	}

	/**
	 * Whether or not to callback when an item that is not selected is clicked.
	 * If false, the item will become selected (and re-centered). If true, the
	 * {@link #getOnItemClickListener()} will get the callback.
	 * 
	 * @param shouldCallback
	 *            Whether or not to callback on the listener when a item that is
	 *            not selected is clicked.
	 * @hide
	 */
	public void setCallbackOnUnselectedItemClick(boolean shouldCallback) {
		mShouldCallbackOnUnselectedItemClick = shouldCallback;
	}

	/**
	 * Sets how long the transition animation should run when a child view
	 * changes position. Only relevant if animation is turned on.
	 * 
	 * @param animationDurationMillis
	 *            The duration of the transition, in milliseconds.
	 * 
	 * @attr ref android.R.styleable#Gallery_animationDuration
	 */
	public void setAnimationDuration(int animationDurationMillis) {
		mAnimationDuration = animationDurationMillis;
	}

	/**
	 * Sets the spacing between items in a Gallery
	 * 
	 * @param spacing
	 *            The spacing in pixels between items in the Gallery
	 * 
	 * @attr ref android.R.styleable#Gallery_spacing
	 */
	public void setSpacing(int spacing) {
		mSpacing = spacing;
	}

	/**
	 * Sets the alpha of items that are not selected in the Gallery.
	 * 
	 * @param unselectedAlpha
	 *            the alpha for the items that are not selected.
	 * 
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

	public void setIsRtl(boolean isRtl) {
		mIsRtl = isRtl;
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
		return new AbsSpinner.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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

	protected void OnScrolling(boolean status) {

	}

	@Override
	int getChildHeight(View child) {
		return child.getMeasuredHeight();
	}

	/**
	 * Tracks a motion scroll. In reality, this is used to do just about any
	 * movement to items (touch scroll, arrow-key scroll, set an item as
	 * selected).
	 *
	 * @param deltaX
	 *            Change in X from the previous event.
	 */
	void trackMotionScroll(int deltaX) {

		if (getChildCount() == 0) {
			return;
		}

		boolean toLeft = deltaX < 0;

		int limitedDeltaX = getLimitedMotionScrollAmount(toLeft, deltaX);
		if (limitedDeltaX != deltaX) {
			// The above call returned a limited amount, so stop any
			// scrolls/flings
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
		// mRecycler.clear();

		setSelectionToCenterChild();

		onScrollChanged(0, 0, 0, 0); // dummy values, View's implementation does
										// not use these.

		invalidate();
	}

	int getLimitedMotionScrollAmount(boolean motionToLeft, int deltaX) {
		int extremeItemPosition = motionToLeft != mIsRtl ? mItemCount - 1 : 0;
		View extremeChild = getChildAt(extremeItemPosition - mFirstPosition);

		if (extremeChild == null) {
			return deltaX;
		}

		int extremeChildCenter = getCenterOfView(extremeChild);
		int galleryCenter = getCenterOfGallery();

		if (motionToLeft) {
			if (extremeChildCenter <= galleryCenter) {

				// The extreme child is past his boundary point!
				return 0;
			}
		} else {
			if (extremeChildCenter >= galleryCenter) {

				// The extreme child is past his boundary point!
				return 0;
			}
		}

		int centerDifference = galleryCenter - extremeChildCenter;

		return motionToLeft ? Math.max(centerDifference, deltaX) : Math.min(centerDifference, deltaX);
	}

	/**
	 * Offset the horizontal location of all children of this view by the
	 * specified number of pixels.
	 *
	 * @param offset
	 *            the number of pixels to offset
	 */
	private void offsetChildrenLeftAndRight(int offset) {
		for (int i = getChildCount() - 1; i >= 0; i--) {
			getChildAt(i).offsetLeftAndRight(offset);
			// specialize for hardware accelerated not refresh every child bug
			// if(android.os.Build.VERSION.SDK_INT >=
			// android.os.Build.VERSION_CODES.JELLY_BEAN)
			getChildAt(i).invalidate();
		}
	}

	/**
	 * @return The center of this Gallery.
	 */
	private int getCenterOfGallery() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
	}

	/**
	 * @return The center of the given view.
	 */
	private static int getCenterOfView(View view) {
		return view.getLeft() + view.getWidth() / 2;
	}

	/**
	 * Detaches children that are off the screen (i.e.: Gallery bounds).
	 *
	 * @param toLeft
	 *            Whether to detach children to the left of the Gallery, or to
	 *            the right.
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
	protected void scrollIntoSlots() {

		if (getChildCount() == 0 || mSelectedChild == null)
			return;

		int selectedCenter = getCenterOfView(mSelectedChild);
		int targetCenter = getCenterOfGallery();

		int scrollAmount = targetCenter - selectedCenter;
		if (isInScrollPosition()) {
			View scrollTo = getChildAt(scrollPosition - mFirstPosition);
			if (scrollTo != null) {
				int scrollCenter = getCenterOfView(scrollTo);
				scrollAmount = targetCenter - scrollCenter;
			}
			if (DEBUG)
				Log.d(TAG, "isInScrollPosition scrollPosition = " + scrollPosition + ", mSelectedPosition = " + mSelectedPosition
						+ ", scrollAmount = " + scrollAmount);
		} else {
			scrollPosition = INVALID_POSITION;
		}
		if (scrollAmount != 0) {
			mFlingRunnable.startUsingDistance(scrollAmount);
		} else {
			onFinishedMovement();
			// if lose focus, not show selector
			if (gainFocus)
				positionSelector(getSelectedItemPosition(), getSelectedView());
		}
	}

	private boolean isInScrollPosition() {
		return scrollPosition != INVALID_POSITION && scrollPosition != mSelectedPosition;
	}

	private void onFinishedMovement() {
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

			int childClosestEdgeDistance = Math.min(Math.abs(child.getLeft() - galleryCenter), Math.abs(child.getRight() - galleryCenter));
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
	 * Creates and positions all views for this Gallery.
	 * <p>
	 * We layout rarely, most of the time {@link #trackMotionScroll(int)} takes
	 * care of repositioning, adding, and removing children.
	 *
	 * @param delta
	 *            Change in the selected position. +1 means the selection is
	 *            moving to the right, so views are scrolling to the left. -1
	 *            means the selection is moving to the left.
	 */
	@Override
	void layout(int delta, boolean animate) {

		// mIsRtl = isLayoutRtl();

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

		// All views go in recycler while we are in layout
		recycleAllViews();

		// Clear out old views
		// removeAllViewsInLayout();
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
		mFirstPosition = mSelectedPosition;
		View sel = makeAndAddView(mSelectedPosition, 0, 0, true);
		resetItemLayout(sel);
		// Put the selected child in the center
		int selectedOffset = childrenLeft + (childrenWidth / 2) - (sel.getWidth() / 2);
		sel.offsetLeftAndRight(selectedOffset);
		fillToGalleryRight();
		fillToGalleryLeft();

		// positionSelector(getSelectedItemPosition(), getSelectedView());
		// setScalableView(getSelectedItemPosition(), getSelectedView());
		// Flush any cached views that did not get reused above
//		mRecycler.clear();

		invalidate();
		checkSelectionChanged();

		mDataChanged = false;
		mNeedSync = false;
		setNextSelectedPositionInt(mSelectedPosition);

		updateSelectedItemMetadata();
	}

	private void fillToGalleryLeft() {
		if (mIsRtl) {
			fillToGalleryLeftRtl();
		} else {
			fillToGalleryLeftLtr();
		}
	}

	private void resetItemLayout(View selectedView) {
		if (selectedView != null) {
			mItemWidth = selectedView.getWidth();
			mItemHeight = selectedView.getHeight();
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
			prevIterationView = makeAndAddView(curPosition, curPosition - mSelectedPosition, curRightEdge, false);

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
			boolean isInScrollPositionMode = scrollPosition != INVALID_POSITION;
			if (isInScrollPositionMode) {
				curPosition = getCurPosInScrollPositionMode(false);
				scrollPosition = INVALID_POSITION;
			} else {
				curPosition = 0;
			}
			curRightEdge = getRight() - getLeft() - getPaddingRight();
			mShouldStopFling = true;
		}

		while (curRightEdge > galleryLeft && curPosition >= 0) {
			prevIterationView = makeAndAddView(curPosition, curPosition - mSelectedPosition, curRightEdge, false);

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
		int galleryRight = getRight() - getLeft() - getPaddingLeft();

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
			prevIterationView = makeAndAddView(curPosition, curPosition - mSelectedPosition, curLeftEdge, true);

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
			boolean isInScrollPositionMode = scrollPosition != INVALID_POSITION;
			if (isInScrollPositionMode) {
				mFirstPosition = curPosition = getCurPosInScrollPositionMode(true);
				scrollPosition = INVALID_POSITION;
			} else {
				mFirstPosition = curPosition = mItemCount - 1;
			}
			curLeftEdge = getPaddingLeft();
			mShouldStopFling = true;
		}

		while (curLeftEdge < galleryRight && curPosition < numItems) {
			prevIterationView = makeAndAddView(curPosition, curPosition - mSelectedPosition, curLeftEdge, true);

			// Set state for next iteration
			curLeftEdge = prevIterationView.getRight() + itemSpacing;
			curPosition++;
		}
	}

	private int getCurPosInScrollPositionMode(boolean fromLeft) {
		int itemWidth = getItemWidth() + mSpacing;
		int halfWidth = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2;
		int maxCount = (int) Math.ceil(1.0f * halfWidth / itemWidth);
		int curPos = -1;
		if (fromLeft) {
			curPos = scrollPosition - maxCount + 1;
			curPos = curPos <= 0 ? 0 : curPos;
		} else {
			curPos = scrollPosition + maxCount - 1;
			curPos = curPos >= mItemCount ? mItemCount : curPos;
		}
		if (DEBUG)
			Log.d(TAG, "getCurPosInScrollPositionMode fromLeft = " + fromLeft + ", curPos = " + curPos + ", scrollPosition = "
					+ scrollPosition);
		return curPos;
	}

	/**
	 * Obtain a view, either by pulling an existing view from the recycler or by
	 * getting a new one from the adapter. If we are animating, make sure there
	 * is enough information in the view's layout parameters to animate from the
	 * old to new positions.
	 *
	 * @param position
	 *            Position in the gallery for the view to obtain
	 * @param offset
	 *            Offset from the selected position
	 * @param x
	 *            X-coordinate indicating where this view should be placed. This
	 *            will either be the left or right edge of the view, depending
	 *            on the fromLeft parameter
	 * @param fromLeft
	 *            Are we positioning views based on the left edge? (i.e.,
	 *            building from left to right)?
	 * @return A view that has been added to the gallery
	 */
	private View makeAndAddView(int position, int offset, int x, boolean fromLeft) {

		View child;
		if (!mDataChanged) {
			child = mRecycler.getScrapView(position);
			if (child != null) {
				// Can reuse an existing view
				int childLeft = child.getLeft();

				// Remember left and right edges of where views have been placed
				mRightMost = Math.max(mRightMost, childLeft + child.getMeasuredWidth());
				mLeftMost = Math.min(mLeftMost, childLeft);

				// Position the view
				setUpChild(child, offset, x, fromLeft);

				return child;
			}
		}

		// Nothing found in the recycler -- ask the adapter for a view
//		child = mAdapter.getView(position, null, this);
		child = obtainView(position, mIsScrap);

		// Position the view
		setUpChild(child, offset, x, fromLeft);

		return child;
	}

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

        return child;
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
		} else {
			if (mSelectedPosition == mOldSelectedPosition && mSelectedPosition == 0 && gainFocus) {
				if (DEBUG)
					Log.d(TAG, "specialize for first pos gain focus");
				// specialize for first gain focus
				if (mSelectorRect.isEmpty()) {
					// correct selector rect when focus before layout is called
					positionSelector(0, getSelectedView());
				}
				setScalableView(0, getSelectedView());
			}
		}
	}

	void onSelectionChanged(int oldSelectedPosition, int newSelectedPosition, long oldSelectedRowId, long newSelectedRowId) {
		if (newSelectedPosition != INVALID_POSITION && gainFocus) {
			if (DEBUG)
				Log.d(TAG, "onSelectionChanged setScalableView" + ", oldSelectedPosition = " + oldSelectedPosition
						+ ", newSelectedPosition = " + newSelectedPosition + ", mInLayout = " + mInLayout);
			if (mSelectorRect.isEmpty()) {
				// correct selector rect when focus is called before layout
				positionSelector(newSelectedPosition, getSelectedView());
			}
			setScalableView(newSelectedPosition, getSelectedView());
		}
		postInvalidate();
	}

	/**
	 * Helper for makeAndAddView to set the position of a view and fill out its
	 * layout parameters.
	 *
	 * @param child
	 *            The view to position
	 * @param offset
	 *            Offset from the selected position
	 * @param x
	 *            X-coordinate indicating where this view should be placed. This
	 *            will either be the left or right edge of the view, depending
	 *            on the fromLeft parameter
	 * @param fromLeft
	 *            Are we positioning views based on the left edge? (i.e.,
	 *            building from left to right)?
	 */
	private void setUpChild(View child, int offset, int x, boolean fromLeft) {

		// Respect layout params that are already in the view. Otherwise
		// make some up...
		AbsSpinner.LayoutParams lp = (AbsSpinner.LayoutParams) child.getLayoutParams();
		if (lp == null) {
			lp = (AbsSpinner.LayoutParams) generateDefaultLayoutParams();
		}

		addViewInLayout(child, fromLeft != mIsRtl ? -1 : 0, lp);

		child.setSelected(offset == 0);

		// Get measure specs
		int childHeightSpec = ViewGroup.getChildMeasureSpec(mHeightMeasureSpec, mSpinnerPadding.top + mSpinnerPadding.bottom, lp.height);
		int childWidthSpec = ViewGroup.getChildMeasureSpec(mWidthMeasureSpec, mSpinnerPadding.left + mSpinnerPadding.right, lp.width);

		// Measure child
		child.measure(childWidthSpec, childHeightSpec);

		int childLeft;
		int childRight;

		// Position vertically based on gravity setting
		int childTop = calculateTop(child, true);
		int childBottom = childTop + child.getMeasuredHeight();

		int width = child.getMeasuredWidth();
		if (fromLeft) {
			childLeft = x;
			childRight = childLeft + width;
		} else {
			childLeft = x - width;
			childRight = x;
		}

		child.layout(childLeft, childTop, childRight, childBottom);
	}

	/**
	 * Figure out vertical placement based on mGravity
	 *
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

			// An item tap should make it selected, so scroll to this child.
			scrollToChild(mDownTouchPosition - mFirstPosition);

			// Also pass the click so the client knows, if it wants to.
			if (mShouldCallbackOnUnselectedItemClick || mDownTouchPosition == mSelectedPosition) {
				performItemClick(mDownTouchView, mDownTouchPosition, mAdapter.getItemId(mDownTouchPosition));
			}

			return true;
		}

		return false;
	}

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

		if (DEBUG)
			Log.v(TAG, String.valueOf(e2.getX() - e1.getX()));

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

		// As the user scrolls, we want to callback selection changes so
		// related-
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
	 *
	 * @see android.view.View#onKeyDown
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
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

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
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
					performItemClick(getChildAt(selectedIndex), mSelectedPosition, mAdapter.getItemId(mSelectedPosition));
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
		return scrollToChild(mSelectedPosition - 1);
		/*
		 * if (mItemCount > 0 && mSelectedPosition > 0) {
		 * scrollToChild(mSelectedPosition - mFirstPosition - 1); return true; }
		 * else { return false; }
		 */
	}

	boolean moveNext() {
		return scrollToChild(mSelectedPosition + 1);
		/*
		 * if (mItemCount > 0 && mSelectedPosition < mItemCount - 1) {
		 * scrollToChild(mSelectedPosition - mFirstPosition + 1); return true; }
		 * else { return false; }
		 */
	}

	/*
	 * private boolean scrollToChild(int childPosition) { View child =
	 * getChildAt(childPosition);
	 * 
	 * if (child != null) { int distance = getCenterOfGallery() -
	 * getCenterOfView(child); mFlingRunnable.startUsingDistance(distance);
	 * return true; }
	 * 
	 * return false; }
	 */

	/**
	 * 滚动到指定的位置
	 * 
	 * @param childPosition
	 *            指定的位置
	 * @return false 位置不在正确范围内 true 滚动到指定的位置
	 */
	public boolean scrollToChild(int childPosition) {
		if (childPosition < 0 || childPosition > getAdapter().getCount() - 1) {
			if (DEBUG)
				Log.d(TAG, "out of child position range");
			return false;
		}
		smoothScrollToPosition(childPosition);
		return true;
	}

	private void smoothScrollToPosition(int childPosition) {
		scrollPosition = childPosition;
		int distance = 0;
		int selectPos = getRelSelectedPosition();
		int delta = selectPos - childPosition;
		distance = delta * (getItemWidth() + mSpacing);
		int minCanScrollDistance = getWidth();
		if (DEBUG)
			Log.d(TAG, " smoothScrollToPosition childPosition = " + childPosition);
		mFlingRunnable.startUsingDistance(distance);
	}
	
	public void smoothScrollBy(int distance, int duration){
		mFlingRunnable.startScroll(-distance, duration);
	}
	
	
	public void endFling(){
		mFlingRunnable.endFling(false);
	}
	
	public int getActualX(){
		return mFlingRunnable.getActualX();
	}
	
	/**
	 * 开始自动滚动
	 */
	public void startAutoScroll() {
		mAutoScroll = true;
		mAutoPaused = false;
		mAutoScrollRunnable.stop();
		mAutoScrollRunnable.run();
	}

	/**
	 * 停止自动滚动
	 */
	public void stopAutoScroll() {
		mAutoScroll = false;
		mAutoScrollRunnable.stop();
	}

	/**
	 * 设置间隔多长时间滚动一次
	 * 
	 * @param delay
	 *            自动滚动间隔的时间
	 */
	public void setAutoScrollDelay(int delay) {
		mAutoScrollDelay = delay;
	}

	private class AutoScrollRunnable implements Runnable {

		@Override
		public void run() {
			if (mAutoScroll) {
				if (!mAutoPaused) {
					int selectPos = getRelSelectedPosition();
					int nextPos = INVALID_POSITION;
					if (selectPos == getAdapter().getCount() - 1) {
						nextPos = 0;
					} else {
						nextPos = selectPos + 1;
					}
					scrollToChild(nextPos);
				}
				postDelayed(this, mAutoScrollDelay);
			}
		}

		public void stop() {
			removeCallbacks(this);
		}

	}

	int getRelSelectedPosition() {
		int selectPos = INVALID_POSITION;
		if (!gainFocus && mSelectedPosition == INVALID_POSITION) {
			selectPos = mOldSelectedPosition;
		} else {
			selectPos = mSelectedPosition;
		}
		return selectPos;
	}

	@Override
	void setSelectedPositionInt(int position) {
		super.setSelectedPositionInt(position);

		// Updates any metadata we keep about the selected item.
		updateSelectedItemMetadata();
	}

	private void updateSelectedItemMetadata() {

		View oldSelectedChild = mSelectedChild;

		View child = mSelectedChild = getChildAt(mSelectedPosition - mFirstPosition);
		if (child == null) {
			return;
		}

		// focus in Gallery, not dispatch to child
		child.setSelected(true);
		child.setFocusable(false);

		// child.setSelected(true);
		// child.setFocusable(true);

		// if (hasFocus()) {
		// child.requestFocus();
		// }

		// We unfocus the old child down here so the above hasFocus check
		// returns true
		if (oldSelectedChild != null && oldSelectedChild != child) {

			// Make sure its drawable state doesn't contain 'selected'
			oldSelectedChild.setSelected(false);

			// Make sure it is not focusable anymore, since otherwise arrow keys
			// can make this one be focused
			oldSelectedChild.setFocusable(false);
		}

	}

	/**
	 * Describes how the child views are aligned.
	 * 
	 * @param gravity
	 * 
	 * @attr ref android.R.styleable#Gallery_gravity
	 */
	public void setGravity(int gravity) {
		if (mGravity != gravity) {
			mGravity = gravity;
			requestLayout();
		}
	}

	/*
	 * @Override protected int getChildDrawingOrder(int childCount, int i) { int
	 * selectedIndex; Log.d(TAG, "getChildDrawingOrder ==> " + ", gainFocus= " +
	 * gainFocus + ", mSelectedPosition = " + mSelectedPosition +
	 * ", mOldSelectedPosition = " + mOldSelectedPosition +
	 * ", mFirstPosition = " + mFirstPosition); if(!gainFocus &&
	 * mSelectedPosition == INVALID_POSITION && mOldSelectedPosition !=
	 * INVALID_POSITION){ selectedIndex = mOldSelectedPosition - mFirstPosition;
	 * }else{ selectedIndex = mSelectedPosition - mFirstPosition; }
	 * 
	 * // Just to be safe if (selectedIndex < 0) return i;
	 * 
	 * if (i == childCount - 1) { // Draw the selected child last return
	 * selectedIndex; } else if (i >= selectedIndex) { // Move the children
	 * after the selected child earlier one return i + 1; } else { // Keep the
	 * children before the selected child the same return i; } }
	 */

	private int getDrawingOrder(int childCount, int selectedIndex, int i) {

		int subPoint = 0;
		int addPoint = 0;
		int result = childCount - 1;

		subPoint = selectedIndex;
		addPoint = selectedIndex;

		subPoint = selectedIndex;
		addPoint = selectedIndex;

		if (selectedIndex < 0) {
			return childCount - 1 - i;
		}

		if (selectedIndex > childCount - 1) {
			return i;
		}

		if (result == i) {
			return selectedIndex;
		}
		for (int v = 0; v <= childCount; v++) {
			subPoint--;
			if (subPoint >= 0) {
				result--;
				if (result == i) {
					return subPoint;
				}
			}
			addPoint++;
			if (addPoint <= childCount - 1) {
				result--;
				if (result == i) {
					return addPoint;
				}
			}
		}
		return i;
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		int selectedIndex = 0;

		// Log.d(TAG, "getChildDrawingOrder ==> "
		// + ", gainFocus= " + gainFocus
		// + ", mSelectedPosition = " + mSelectedPosition
		// + ", mOldSelectedPosition = " + mOldSelectedPosition
		// + ", mFirstPosition = " + mFirstPosition);

		return getDrawingOrder(childCount, getRelSelectedPosition() - mFirstPosition, i);
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		// Exception e = new Exception("Gallery on focus changed");
		// e.printStackTrace();
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

		/*
		 * The gallery shows focus by focusing the selected item. So, give focus
		 * to our selected item instead. We steal keys from our selected item
		 * elsewhere.
		 */
		// if (gainFocus && mSelectedChild != null) {
		// mSelectedChild.requestFocus(direction);
		// mSelectedChild.setSelected(true);
		// }
		this.gainFocus = gainFocus;
		if (gainFocus) {
			// get focus on Gallery
			// mSelectedPosition = 0;
			// mOldSelectedPosition = INVALID_POSITION;
			mSelectedPosition = mOldSelectedPosition == INVALID_POSITION ? 0 : mOldSelectedPosition;
			positionSelector(mSelectedPosition, getSelectedView());
			setScalableView(mSelectedPosition, getSelectedView());

			if (DEBUG)
				Log.d(TAG, "onFocusChanged setScalableView" + ", mSelectedPosition = " + mSelectedPosition + " , gainFocus = "
						+ this.gainFocus + ", mInLayout = " + mInLayout);
		} else {
			// lose focus
			mOldSelectedPosition = mSelectedPosition;
			mSelectedPosition = INVALID_POSITION;
			mSelectorRect.setEmpty();
			clearScalableView();
		}
		checkSelectionChanged();

	}

	@Override
	public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
		super.onInitializeAccessibilityEvent(event);
		event.setClassName(Gallery.class.getName());
	}

	public boolean isCoverFlowScrolling() {
		return isScrolling;
	}

	// @Override
	// public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info)
	// {
	// super.onInitializeAccessibilityNodeInfo(info);
	// info.setClassName(Gallery.class.getName());
	// info.setScrollable(mItemCount > 1);
	// if (isEnabled()) {
	// if (mItemCount > 0 && mSelectedPosition < mItemCount - 1) {
	// info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
	// }
	// if (isEnabled() && mItemCount > 0 && mSelectedPosition > 0) {
	// info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
	// }
	// }
	// }

	// @Override
	// public boolean performAccessibilityAction(int action, Bundle arguments) {
	// if (super.performAccessibilityAction(action, arguments)) {
	// return true;
	// }
	// switch (action) {
	// case AccessibilityNodeInfo.ACTION_SCROLL_FORWARD: {
	// if (isEnabled() && mItemCount > 0 && mSelectedPosition < mItemCount - 1)
	// {
	// final int currentChildIndex = mSelectedPosition - mFirstPosition;
	// return scrollToChild(currentChildIndex + 1);
	// }
	// } return false;
	// case AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD: {
	// if (isEnabled() && mItemCount > 0 && mSelectedPosition > 0) {
	// final int currentChildIndex = mSelectedPosition - mFirstPosition;
	// return scrollToChild(currentChildIndex - 1);
	// }
	// } return false;
	// }
	// return false;
	// }

	/**
	 * Responsible for fling behavior. Use {@link #startUsingVelocity(int)} to
	 * initiate a fling. Each frame of the fling is handled in {@link #run()}. A
	 * FlingRunnable will keep re-posting itself until the fling is done.
	 */
	private class FlingRunnable implements Runnable {
		/**
		 * Tracks the decay of a fling scroll
		 */
		private Scroller mScroller;

		/**
		 * X value reported by mScroller on the previous fling
		 */
		private int mLastFlingX;
		
		private int mActualX = 0;

		public FlingRunnable() {
			mScroller = new Scroller(getContext());
		}
		
		public int getActualX(){
			return mActualX;
		}

		private void startCommon() {
			// Remove any pending flings
			removeCallbacks(this);
			if (mAutoScroll) {
				mAutoPaused = true;
			}
			if (!isScrolling) {
				isScrolling = true;
				if (mOnScrollingListener != null) {
					mOnScrollingListener.OnScroll(true);
				}

				OnScrolling(isScrolling);
			}
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

		public void startScroll(int distance, int duration){
			if (distance == 0)
				return;

			startCommon();

			mLastFlingX = 0;
			OnScrolling(true);
			mScroller.startScroll(0, 0, -distance, 0, duration);
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
			if (mAutoScroll) {
				mAutoPaused = false;
			}

			if (isScrolling) {
				isScrolling = false;
				if (mOnScrollingListener != null) {
					mOnScrollingListener.OnScroll(false);
				}

				OnScrolling(isScrolling);
			}
		}

		@Override
		public void run() {

			if (mItemCount == 0) {
				endFling(true);
				return;
			}
			
//			if(mScroller.isFinished()){
//				return;
//			}

			mShouldStopFling = false;

			final Scroller scroller = mScroller;
			boolean more = scroller.computeScrollOffset();
			final int x = scroller.getCurrX();
			mActualX = x;

			// Flip sign to convert finger direction to list items direction
			// (e.g. finger moving down means list is moving towards the top)
			int delta = mLastFlingX - x;

			// Pretend that each frame of a fling scroll is a touch scroll
			if (delta > 0) {
				// Moving towards the left. Use leftmost view as
				// mDownTouchPosition
				mDownTouchPosition = mIsRtl ? (mFirstPosition + getChildCount() - 1) : mFirstPosition;

				// Don't fling more than 1 screen
				delta = Math.min(getWidth() - getPaddingLeft() - getPaddingRight() - 1, delta);
			} else {
				// Moving towards the right. Use rightmost view as
				// mDownTouchPosition
				int offsetToLast = getChildCount() - 1;
				mDownTouchPosition = mIsRtl ? mFirstPosition : (mFirstPosition + getChildCount() - 1);

				// Don't fling more than 1 screen
				delta = Math.max(-(getWidth() - getPaddingRight() - getPaddingLeft() - 1), delta);
			}

			trackMotionScroll(delta);

			if (DEBUG)
				Log.d(TAG, "----------------flingrunnable delta = " + delta + ", scrollPosition = " + scrollPosition + ", more = " + more
						+ ", mShouldStopFling = " + mShouldStopFling);

			if (more && !mShouldStopFling) {
				mLastFlingX = x;
				int curSelectdPos = getRelSelectedPosition();
				if (scrollPosition != INVALID_POSITION && scrollPosition != curSelectdPos) {
					int screenWidth = getWidth() - getPaddingRight() - getPaddingLeft() - 1;
					int deltaPos = curSelectdPos - scrollPosition;
					int perItemWidth = getItemWidth() + mSpacing;
					int distance = deltaPos * perItemWidth;
					int absDelta = Math.abs(distance);
					if (DEBUG)
						Log.d(TAG, "absDelta = " + absDelta + ", screenWidth = " + screenWidth + ", deltaPos = " + deltaPos
								+ ", perItemWidth = " + perItemWidth + ", curSelectdPos = " + curSelectdPos + ", mFirstPosition = "
								+ mFirstPosition + ", scrollPosition = " + scrollPosition);
					if (absDelta > screenWidth) {
						smoothScrollToPosition(scrollPosition);
					} else {
						post(this);
					}
				} else {
					post(this);
				}
			} else {
				// scrollPosition = INVALID_POSITION;
				endFling(true);
			}
		}

	}

	/**
	 * 滚动的监听
	 * <p>
	 * 开始和停止滚动时会回调
	 * </p>
	 */
	public interface OnScrollingListener {

		/**
		 * @param scrolling
		 *            false 停止滚动时的回调 true 开始滚动时的回调
		 * @return true/false 无影响
		 */
		boolean OnScroll(boolean scrolling);
	}

	/**
	 * 设置滚动的监听，开始和停止滚动时会回调
	 * 
	 * @param listener
	 *            滚动的监听
	 */
	public void setOnScrollingListener(OnScrollingListener listener) {
		mOnScrollingListener = listener;
	}
}
