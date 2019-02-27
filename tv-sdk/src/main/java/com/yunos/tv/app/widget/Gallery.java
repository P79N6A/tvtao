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

package com.yunos.tv.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;

public class Gallery extends AbsGallery {

	private static final String TAG = "Gallery";

	private static final boolean localLOGV = false;

	/**
	 * Left most edge of a child seen so far during layout.
	 */
	protected int mLeftMost;

	/**
	 * Right most edge of a child seen so far during layout.
	 */
	protected int mRightMost;

	/**
	 * Executes the delta scrolls from a fling or scroll movement.
	 */
	private FlingRunnable mFlingRunnable = new FlingRunnable();

	/**
	 * If true, mFirstPosition is the position of the rightmost child, and the
	 * children are ordered right to left.
	 */
	protected boolean mIsRtl = true;

	public Gallery(Context context) {
		super(context);
		init(context);
	}

	public Gallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public Gallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);

		// TypedArray a = context.obtainStyledAttributes(
		// attrs, com.android.internal.R.styleable.Gallery, defStyle, 0);
		//
		// int index =
		// a.getInt(com.android.internal.R.styleable.Gallery_gravity, -1);
		// if (index >= 0) {
		// setGravity(index);
		// }
		//
		// int animationDuration =
		// a.getInt(com.android.internal.R.styleable.Gallery_animationDuration,
		// -1);
		// if (animationDuration > 0) {
		// setAnimationDuration(animationDuration);
		// }
		//
		// int spacing =
		// a.getDimensionPixelOffset(com.android.internal.R.styleable.Gallery_spacing,
		// 0);
		// setSpacing(spacing);
		//
		// float unselectedAlpha = a.getFloat(
		// com.android.internal.R.styleable.Gallery_unselectedAlpha, 0.5f);
		// setUnselectedAlpha(unselectedAlpha);
		//
		// a.recycle();

		// We draw the selected item last (because otherwise the item to the
		// right overlaps it)
	}

	private void init(Context context) {
		mGravity = Gravity.CENTER_VERTICAL;
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
		if (!mByPosition) {
			mRecycler.clear();
		}

		// setSelectionToCenterChild();

		final View selChild = mSelectedChild;
		if (selChild != null) {
			final int childLeft = selChild.getLeft();
			final int childCenter = selChild.getWidth() / 2;
			final int galleryCenter = getWidth() / 2;
			mSelectedCenterOffset = childLeft + childCenter - galleryCenter;
		}

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
	 * @return The center of this Gallery.
	 */
	private int getCenterOfGallery() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingRight();
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
					mRecycler.put(firstPosition + n, child);
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
					mRecycler.put(firstPosition + n, child);
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

		if (getChildCount() == 0 || mSelectedChild == null)
			return;

		int selectedCenter = getCenterOfView(mSelectedChild);
		int targetCenter = getCenterOfGallery();

		int scrollAmount = targetCenter - selectedCenter;
		if (scrollAmount != 0) {
			mFlingRunnable.startUsingDistance(scrollAmount);
		} else {
			onFinishedMovement();
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
	protected void layout(int delta, boolean animate) {

		mIsRtl = false;// isLayoutRtl();

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

		// Put the selected child in the center
		int selectedOffset = childrenLeft + (childrenWidth / 2) - (sel.getWidth() / 2) + mSelectedCenterOffset;
		sel.offsetLeftAndRight(selectedOffset);

		if (sel != null) {
			positionSelector(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom());
		}
		fillToGalleryRight();
		fillToGalleryLeft();

		// Flush any cached views that did not get reused above
		if (!mByPosition) {
			mRecycler.clear();
		}

		invalidate();
		checkSelectionChanged();

		mDataChanged = false;
		mNeedSync = false;
		setNextSelectedPositionInt(mSelectedPosition);

		updateSelectedItemMetadata();
	}

	protected void fillToGalleryLeft() {
		if (mIsRtl) {
			fillToGalleryLeftRtl();
		} else {
			fillToGalleryLeftLtr();
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
			curPosition = 0;
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

	protected void fillToGalleryRight() {
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
			mFirstPosition = curPosition = mItemCount - 1;
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
	protected View makeAndAddView(int position, int offset, int x, boolean fromLeft) {

		View child;
		if (!mDataChanged) {
			child = mRecycler.get(position);

			if (child != null) {
				// Can reuse an existing view
				int childLeft = child.getLeft();

				// Remember left and right edges of where views have been placed
				mRightMost = Math.max(mRightMost, childLeft + child.getMeasuredWidth());
				mLeftMost = Math.min(mLeftMost, childLeft);

				if (mByPosition) {
					child = mAdapter.getView(position, child, this);
				}

				// Position the view
				setUpChild(child, offset, x, fromLeft);

				return child;
			}
		}

		if (mByPosition) {
			child = mRecycler.get(position);
		} else {
			child = null;
		}
		
		// Nothing found in the recycler -- ask the adapter for a view
		child = mAdapter.getView(position, child, this);

		// Position the view
		setUpChild(child, offset, x, fromLeft);

		return child;
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
		Gallery.LayoutParams lp = (Gallery.LayoutParams) child.getLayoutParams();
		if (lp == null) {
			lp = (Gallery.LayoutParams) generateDefaultLayoutParams();
		}

		addViewInLayout(child, fromLeft != mIsRtl ? -1 : 0, lp);

		child.setSelected(offset == 0);

		// Get measure specs
		int childHeightSpec = getChildMeasureSpec(mHeightMeasureSpec, mSpinnerPadding.top + mSpinnerPadding.bottom, lp.height);
		int childWidthSpec = getChildMeasureSpec(mWidthMeasureSpec, mSpinnerPadding.left + mSpinnerPadding.right, lp.width);

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
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

		super.onFling(e1, e2, velocityX, velocityY);
		// Fling the gallery!
		mFlingRunnable.startUsingVelocity((int) -velocityX);

		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

		if (localLOGV)
			Log.v(TAG, String.valueOf(e2.getX() - e1.getX()));

		super.onScroll(e1, e2, distanceX, distanceY);

		// Track the motion
		trackMotionScroll(-1 * (int) distanceX);

		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {

		super.onDown(e);
		// Kill any existing fling/scroll
		mFlingRunnable.stop(false);

		// Must return true to get matching events for this down event.
		return true;
	}

	/**
	 * Called when a touch event's action is MotionEvent.ACTION_UP.
	 */
	@Override
	protected void onUp() {
		super.onUp();

		if (mFlingRunnable.isFinished()) {
			scrollIntoSlots();
		}
	}

	/**
	 * Handles left, right, and clicking
	 * 
	 * @see View#onKeyDown
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

	public void smoothScrollBy(int distance) {
		if (mFlingRunnable != null) {
			mFlingRunnable.startUsingDistance(distance);
		}
	}

	public void setFlingScrollFrameCount(int frameCount) {
		if (mFlingRunnable != null) {
			mFlingRunnable.setFrameCount(frameCount);
		}
	}

	public void setFlingScrollMaxStep(float maxStep) {
		if (mFlingRunnable != null) {
			mFlingRunnable.setMaxStep(maxStep);
		}
	}

	public int getLeftScrollDistance() {
		if (mFlingRunnable != null) {
			return mFlingRunnable.getLeftScrollDistance();
		}
		return 0;
	}

	public void setFlingScrollSlowDownRatio(float ratio) {
		if (mFlingRunnable != null) {
			mFlingRunnable.setSlowDownRatio(ratio);
		}
	}

	@Override
	protected boolean scrollToChild(int childPosition) {
		View child = getChildAt(childPosition);

		if (child != null) {
			int distance = getCenterOfGallery() - getCenterOfView(child);
			mFlingRunnable.startUsingDistance(distance);
			return true;
		}

		return false;
	}

	protected boolean isFling() {
		return !mFlingRunnable.isFinished();
	}

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

		private ListLoopScroller mListLoopScroller;
		private int mFrameCount;
		private float mDefatultScrollStep = 5.0f;

		public FlingRunnable() {
			mScroller = new Scroller(getContext(), new AccelerateDecelerateFrameInterpolator());
			mListLoopScroller = new ListLoopScroller();
		}

		private void startCommon() {
			// Remove any pending flings
			removeCallbacks(this);
		}

		void setFrameCount(int frameCount) {
			mFrameCount = frameCount;
		}

		void setMaxStep(float maxStep) {
			mListLoopScroller.setMaxStep(maxStep);
		}

		int getLeftScrollDistance() {
			return mListLoopScroller.getFinal() - mListLoopScroller.getCurr();
		}

		void setSlowDownRatio(float ratio) {
			mListLoopScroller.setSlowDownRatio(ratio);
		}

		public boolean isFinished() {
			if (mScroller.isFinished() && mListLoopScroller.isFinished()) {
				return true;
			}
			return false;
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

			mLastFlingX = 0;
			int frameCount;
			if (mFrameCount <= 0) {
				// use default sroll step
				frameCount = (int) (distance / mDefatultScrollStep);
				if (frameCount < 0) {
					frameCount = -frameCount;
				} else if (frameCount == 0) {
					frameCount = 1;
				}
			} else {
				frameCount = mFrameCount;
			}
			mLastFlingX = 0;
			if (mListLoopScroller.isFinished()) {
				startCommon();
				mListLoopScroller.startScroll(0, -distance, frameCount);
				post(this);
			} else {
				mListLoopScroller.startScroll(0, -distance, frameCount);
			}

			reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
			// mScroller.startScroll(0, 0, -distance, 0, mAnimationDuration);
			// post(this);
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
			reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
			mScroller.forceFinished(true);
			mListLoopScroller.finish();
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

			// final Scroller scroller = mScroller;
			boolean more = mListLoopScroller.computeScrollOffset();
			final int x = mListLoopScroller.getCurr();

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

			if (more && !mShouldStopFling) {
				mLastFlingX = x;
				post(this);
			} else {
				endFling(true);
			}
		}

	}

}
