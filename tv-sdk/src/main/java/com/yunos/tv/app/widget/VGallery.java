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
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.yunos.tv.app.widget.focus.listener.OnScrollListener;

public class VGallery extends AbsGallery {

	private static final String TAG = "VGallery";

	private static final boolean localLOGV = false;

	/**
	 * Left most edge of a child seen so far during layout.
	 */
	private int mTopMost;

	/**
	 * Right most edge of a child seen so far during layout.
	 */
	private int mBottomMost;

	/**
	 * Executes the delta scrolls from a fling or scroll movement.
	 */
	private FlingRunnable mFlingRunnable = new FlingRunnable();

	/**
	 * If true, mFirstPosition is the position of the rightmost child, and the
	 * children are ordered right to left.
	 */
	private boolean mIsRtl = true;

	public VGallery(Context context) {
		super(context);
		init(context);
	}

	public VGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public VGallery(Context context, AttributeSet attrs, int defStyle) {
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
		mGravity = Gravity.CENTER_HORIZONTAL;
	}

	@Override
	protected int computeVerticalScrollExtent() {
		// Only 1 item is considered to be selected
		return 1;
	}

	@Override
	protected int computeVerticalScrollOffset() {
		// Current scroll position is the same as the selected position
		return mSelectedPosition;
	}

	@Override
	protected int computeVerticalScrollRange() {
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
	int getChildWidth(View child) {
		return child.getMeasuredWidth();
	}

	/**
	 * Tracks a motion scroll. In reality, this is used to do just about any
	 * movement to items (touch scroll, arrow-key scroll, set an item as
	 * selected).
	 * 
	 * @param deltaX
	 *            Change in X from the previous event.
	 */
	void trackMotionScroll(int deltaY) {

		if (getChildCount() == 0) {
			return;
		}

		boolean toUp = deltaY < 0;

		int limitedDeltaY = getLimitedMotionScrollAmount(toUp, deltaY);
		if (limitedDeltaY != deltaY) {
			// The above call returned a limited amount, so stop any
			// scrolls/flings
			mFlingRunnable.endFling(false);
			// onFinishedMovement();
		}

		offsetChildrenTopAndBottom(limitedDeltaY);

		detachOffScreenChildren(toUp);

		if (toUp) {
			// If moved left, there will be empty space on the right
			fillToGalleryDown();
		} else {
			// Similarly, empty space on the left
			fillToGalleryUp();
		}

		// Clear unused views
		if (!mByPosition) {
			mRecycler.clear();
		}
		// setSelectionToCenterChild();

		final View selChild = mSelectedChild;
		if (selChild != null) {
			final int childTop = selChild.getTop();
			final int childCenter = selChild.getHeight() / 2;
			final int galleryCenter = getHeight() / 2;
			mSelectedCenterOffset = childTop + childCenter - galleryCenter;
		}

		onScrollChanged(0, 0, 0, 0); // dummy values, View's implementation does
										// not use these.

		invalidate();
	}

	int getLimitedMotionScrollAmount(boolean motionToUp, int deltaY) {
		int extremeItemPosition = motionToUp != mIsRtl ? mItemCount - 1 : 0;
		View extremeChild = getChildAt(extremeItemPosition - mFirstPosition);

		if (extremeChild == null) {
			return deltaY;
		}

		int extremeChildCenter = getCenterOfView(extremeChild);
		int galleryCenter = getCenterOfGallery();

		if (motionToUp) {
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

		return motionToUp ? Math.max(centerDifference, deltaY) : Math.min(centerDifference, deltaY);
	}

	/**
	 * @return The center of this Gallery.
	 */
	private int getCenterOfGallery() {
		return (getHeight() - getPaddingTop() - getPaddingBottom()) / 2 + getPaddingBottom();
	}

	/**
	 * @return The center of the given view.
	 */
	private static int getCenterOfView(View view) {
		return view.getTop() + view.getHeight() / 2;
	}

	/**
	 * Detaches children that are off the screen (i.e.: Gallery bounds).
	 * 
	 * @param toLeft
	 *            Whether to detach children to the left of the Gallery, or to
	 *            the right.
	 */
	private void detachOffScreenChildren(boolean toUp) {
		int numChildren = getChildCount();
		int firstPosition = mFirstPosition;
		int start = 0;
		int count = 0;

		if (toUp) {
			final int galleryTop = getPaddingTop();
			for (int i = 0; i < numChildren; i++) {
				int n = mIsRtl ? (numChildren - 1 - i) : i;
				final View child = getChildAt(n);
				if (child.getBottom() >= galleryTop) {
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
			final int galleryBottom = getHeight() - getPaddingBottom();
			for (int i = numChildren - 1; i >= 0; i--) {
				int n = mIsRtl ? numChildren - 1 - i : i;
				final View child = getChildAt(n);
				if (child.getTop() <= galleryBottom) {
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

		if (toUp != mIsRtl) {
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
		if (selView.getTop() <= galleryCenter && selView.getBottom() >= galleryCenter) {
			return;
		}

		// TODO better search
		int closestEdgeDistance = Integer.MAX_VALUE;
		int newSelectedChildIndex = 0;
		for (int i = getChildCount() - 1; i >= 0; i--) {

			View child = getChildAt(i);

			if (child.getTop() <= galleryCenter && child.getBottom() >= galleryCenter) {
				// This child is in the center
				newSelectedChildIndex = i;
				break;
			}

			int childClosestEdgeDistance = Math.min(Math.abs(child.getTop() - galleryCenter), Math.abs(child.getBottom() - galleryCenter));
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

		int childrenTop = mSpinnerPadding.top;
		int childrenHeight = getBottom() - getTop() - mSpinnerPadding.top - mSpinnerPadding.bottom;

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
		mBottomMost = 0;
		mTopMost = 0;

		// Make selected view and center it

		/*
		 * mFirstPosition will be decreased as we add views to the left later
		 * on. The 0 for x will be offset in a couple lines down.
		 */
		mFirstPosition = mSelectedPosition;
		View sel = makeAndAddView(mSelectedPosition, 0, 0, true);

		// Put the selected child in the center
		int selectedOffset = childrenTop + (childrenHeight / 2) - (sel.getHeight() / 2);
		sel.offsetTopAndBottom(selectedOffset);

		if (sel != null) {
			positionSelector(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom());
		}
		fillToGalleryDown();
		fillToGalleryUp();

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

	private void fillToGalleryUp() {
		if (mIsRtl) {
			fillToGalleryUpRtl();
		} else {
			fillToGalleryUpLtr();
		}
	}

	private void fillToGalleryUpRtl() {
		int itemSpacing = mSpacing;
		int galleryTop = getPaddingTop();
		int numChildren = getChildCount();
		int numItems = mItemCount;

		// Set state for initial iteration
		View prevIterationView = getChildAt(numChildren - 1);
		int curPosition;
		int curBottomEdge;

		if (prevIterationView != null) {
			curPosition = mFirstPosition + numChildren;
			curBottomEdge = prevIterationView.getTop() - itemSpacing;
		} else {
			// No children available!
			mFirstPosition = curPosition = mItemCount - 1;
			curBottomEdge = getBottom() - getTop() - getPaddingBottom();
			mShouldStopFling = true;
		}

		while (curBottomEdge > galleryTop && curPosition < mItemCount) {
			prevIterationView = makeAndAddView(curPosition, curPosition - mSelectedPosition, curBottomEdge, false);

			// Set state for next iteration
			curBottomEdge = prevIterationView.getTop() - itemSpacing;
			curPosition++;
		}
	}

	private void fillToGalleryUpLtr() {
		int itemSpacing = mSpacing;
		int galleryTop = getPaddingTop();

		// Set state for initial iteration
		View prevIterationView = getChildAt(0);
		int curPosition;
		int curBottomEdge;

		if (prevIterationView != null) {
			curPosition = mFirstPosition - 1;
			curBottomEdge = prevIterationView.getTop() - itemSpacing;
		} else {
			// No children available!
			curPosition = 0;
			curBottomEdge = getBottom() - getTop() - getPaddingBottom();
			mShouldStopFling = true;
		}

		while (curBottomEdge > galleryTop && curPosition >= 0) {
			prevIterationView = makeAndAddView(curPosition, curPosition - mSelectedPosition, curBottomEdge, false);

			// Remember some state
			mFirstPosition = curPosition;

			// Set state for next iteration
			curBottomEdge = prevIterationView.getTop() - itemSpacing;
			curPosition--;
		}
	}

	private void fillToGalleryDown() {
		if (mIsRtl) {
			fillToGalleryDownRtl();
		} else {
			fillToGalleryDownLtr();
		}
	}

	private void fillToGalleryDownRtl() {
		int itemSpacing = mSpacing;
		int galleryBottom = getBottom() - getTop() - getPaddingBottom();

		// Set state for initial iteration
		View prevIterationView = getChildAt(0);
		int curPosition;
		int curTopEdge;

		if (prevIterationView != null) {
			curPosition = mFirstPosition - 1;
			curTopEdge = prevIterationView.getBottom() + itemSpacing;
		} else {
			curPosition = 0;
			curTopEdge = getPaddingTop();
			mShouldStopFling = true;
		}

		while (curTopEdge < galleryBottom && curPosition >= 0) {
			prevIterationView = makeAndAddView(curPosition, curPosition - mSelectedPosition, curTopEdge, true);

			// Remember some state
			mFirstPosition = curPosition;

			// Set state for next iteration
			curTopEdge = prevIterationView.getBottom() + itemSpacing;
			curPosition--;
		}
	}

	private void fillToGalleryDownLtr() {
		int itemSpacing = mSpacing;
		int galleryBottom = getBottom() - getTop() - getPaddingBottom();
		int numChildren = getChildCount();
		int numItems = mItemCount;

		// Set state for initial iteration
		View prevIterationView = getChildAt(numChildren - 1);
		int curPosition;
		int curTopEdge;

		if (prevIterationView != null) {
			curPosition = mFirstPosition + numChildren;
			curTopEdge = prevIterationView.getBottom() + itemSpacing;
		} else {
			mFirstPosition = curPosition = mItemCount - 1;
			curTopEdge = getPaddingTop();
			mShouldStopFling = true;
		}

		while (curTopEdge < galleryBottom && curPosition < numItems) {
			prevIterationView = makeAndAddView(curPosition, curPosition - mSelectedPosition, curTopEdge, true);

			// Set state for next iteration
			curTopEdge = prevIterationView.getBottom() + itemSpacing;
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
	private View makeAndAddView(int position, int offset, int y, boolean fromTop) {

		View child;
		if (!mDataChanged) {
			child = mRecycler.get(position);
			if (child != null) {
				// Can reuse an existing view
				int childTop = child.getTop();

				// Remember left and right edges of where views have been placed
				mBottomMost = Math.max(mBottomMost, childTop + child.getMeasuredHeight());
				mTopMost = Math.min(mTopMost, childTop);
				if (mByPosition) {
					child = mAdapter.getView(position, child, this);
				}
				// Position the view
				setUpChild(child, offset, y, fromTop);

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
		setUpChild(child, offset, y, fromTop);

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
	private void setUpChild(View child, int offset, int y, boolean fromTop) {

		// Respect layout params that are already in the view. Otherwise
		// make some up...
		VGallery.LayoutParams lp = (VGallery.LayoutParams) child.getLayoutParams();
		if (lp == null) {
			lp = (VGallery.LayoutParams) generateDefaultLayoutParams();
		}

		addViewInLayout(child, fromTop != mIsRtl ? -1 : 0, lp);

		child.setSelected(offset == 0);

		// Get measure specs
		int childHeightSpec = getChildMeasureSpec(mHeightMeasureSpec, mSpinnerPadding.top + mSpinnerPadding.bottom, lp.height);
		int childWidthSpec = getChildMeasureSpec(mWidthMeasureSpec, mSpinnerPadding.left + mSpinnerPadding.right, lp.width);

		// Measure child
		child.measure(childWidthSpec, childHeightSpec);

		int childTop;
		int childBottom;

		// Position vertically based on gravity setting
		int childLeft = calculateLeft(child, true);
		int childRight = childLeft + child.getMeasuredWidth();

		int height = child.getMeasuredHeight();
		if (fromTop) {
			childTop = y;
			childBottom = childTop + height;
		} else {
			childTop = y - height;
			childBottom = y;
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
	private int calculateLeft(View child, boolean duringLayout) {
		int myWidth = duringLayout ? getMeasuredWidth() : getWidth();
		int childWidth = duringLayout ? child.getMeasuredWidth() : child.getWidth();

		int childLeft = 0;

		switch (mGravity) {
		case Gravity.LEFT:
			childLeft = mSpinnerPadding.left;
			break;
		case Gravity.CENTER_HORIZONTAL:
			int availableSpace = myWidth - mSpinnerPadding.right - mSpinnerPadding.left - childWidth;
			childLeft = mSpinnerPadding.left + (availableSpace / 2);
			break;
		case Gravity.RIGHT:
			childLeft = myWidth - mSpinnerPadding.right - childWidth;
			break;
		}
		return childLeft;
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
		trackMotionScroll(-1 * (int) distanceY);

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

	// Unused methods from GestureDetector.OnGestureListener above

	/**
	 * Handles left, right, and clicking
	 * 
	 * @see View#onKeyDown
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {

		case KeyEvent.KEYCODE_DPAD_UP:
			if (movePrevious()) {
				playSoundEffect(SoundEffectConstants.NAVIGATION_UP);
				return true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if (moveNext()) {
				playSoundEffect(SoundEffectConstants.NAVIGATION_DOWN);
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

	public void stopScroll(boolean scrollIntoSlots) {
		if (mFlingRunnable != null) {
			mFlingRunnable.stop(scrollIntoSlots);
		}
	}

	public void setFlingScrollFrameCount(int frameCount) {
		if (mFlingRunnable != null) {
			mFlingRunnable.setFrameCount(frameCount);
		}
	}

	public void setFlingScrollPostDelay(int delay) {
		if (mFlingRunnable != null) {
			mFlingRunnable.setPostDelay(delay);
		}
	}

	public void setFlingScrollMaxStep(float maxStep) {
		if (mFlingRunnable != null) {
			mFlingRunnable.setMaxStep(maxStep);
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
	protected class FlingRunnable implements Runnable {
		/**
		 * Tracks the decay of a fling scroll
		 */
		private Scroller mScroller;

		/**
		 * X value reported by mScroller on the previous fling
		 */
		private int mLastFlingY;

		private ListLoopScroller mListLoopScroller;
		private int mFrameCount;
		private float mDefatultScrollStep = 5.0f;
		private int mPostDelay = 0;

		public boolean isFinished() {
			if (mScroller.isFinished() && mListLoopScroller.isFinished()) {
				return true;
			}
			return false;
		}

		public FlingRunnable() {
			mScroller = new Scroller(getContext(), new DecelerateInterpolator());
			mListLoopScroller = new ListLoopScroller();
		}

		private void startCommon() {
			// Remove any pending flings
			removeCallbacks(this);
		}

		public void startUsingVelocity(int initialVelocity) {
			if (initialVelocity == 0)
				return;

			startCommon();

			int initialY = initialVelocity < 0 ? Integer.MAX_VALUE : 0;
			mLastFlingY = initialY;
			mScroller.fling(0, initialY, 0, initialVelocity, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
			post(this);
		}

		public void startUsingDistance(int distance) {
			if (distance == 0)
				return;

			mLastFlingY = 0;
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
			if (mListLoopScroller.isFinished()) {
				startCommon();
				mListLoopScroller.startScroll(0, -distance, frameCount);
				post(this);
			} else {
				mListLoopScroller.startScroll(0, -distance, frameCount);
			}

			reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
			// mScroller.startScroll(0, 0, 0, -distance, mAnimationDuration);
			// post(this);
		}

		public void stop(boolean scrollIntoSlots) {
			removeCallbacks(this);
			endFling(scrollIntoSlots);
		}

		public void setFrameCount(int frameCount) {
			mFrameCount = frameCount;
		}

		public void setMaxStep(float maxStep) {
			mListLoopScroller.setMaxStep(maxStep);
		}

		public void setPostDelay(int delay) {
			mPostDelay = delay;
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
			final int y = mListLoopScroller.getCurr();

			// Flip sign to convert finger direction to list items direction
			// (e.g. finger moving down means list is moving towards the top)
			int delta = mLastFlingY - y;
			// Pretend that each frame of a fling scroll is a touch scroll
			if (delta > 0) {
				// Moving towards the left. Use leftmost view as
				// mDownTouchPosition
				mDownTouchPosition = mIsRtl ? (mFirstPosition + getChildCount() - 1) : mFirstPosition;

				// Don't fling more than 1 screen
				delta = Math.min(getHeight() - getPaddingTop() - getPaddingBottom() - 1, delta);
			} else {
				// Moving towards the right. Use rightmost view as
				// mDownTouchPosition
				int offsetToLast = getChildCount() - 1;
				mDownTouchPosition = mIsRtl ? mFirstPosition : (mFirstPosition + getChildCount() - 1);

				// Don't fling more than 1 screen
				delta = Math.max(-(getHeight() - getPaddingTop() - getPaddingBottom() - 1), delta);
			}

			trackMotionScroll(delta);

			if (more && !mShouldStopFling) {
				mLastFlingY = y;
				// post(this);
				postDelayed(this, mPostDelay);
			} else {
				endFling(true);
			}
		}

	}

	public FlingRunnable getFlingRunnable() {
		return mFlingRunnable;
	}
}
