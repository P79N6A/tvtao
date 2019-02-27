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
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StateSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.FocusFinder;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.OverScroller;

/**
 * A view that shows items in horizontally scrolling list.
 * <p>
 * The default values for the AbsHorizontalListView, each View given to the
 * AbsHorizontalListView from the Adapter. If you are not doing this, you may
 * need to adjust some AbsHorizontalListView properties, such as the spacing.
 * <p>
 * Views given to the AbsHorizontalListView should use
 * {@link AbsHorizontalListView.LayoutParams} as their layout parameters type.
 *
 *
 */
// * @attr ref android.R.styleable#Gallery_animationDuration
// * @attr ref android.R.styleable#Gallery_spacing
// * @attr ref android.R.styleable#Gallery_gravity

public abstract class AbsHorizontalListView extends AbsSpinner implements GestureDetector.OnGestureListener {

	private static final String TAG = "AbsHorizontalListView";
	private static final boolean DEBUG = true;

	static final int TOUCH_MODE_REST = -1;

	static final int TOUCH_MODE_FLING = 4;

	/**
	 * Duration in milliseconds from the start of a scroll during which we're
	 * unsure whether the user is scrolling or flinging.
	 */
	private static final int SCROLL_TO_FLING_UNCERTAINTY_TIMEOUT = 250;

	/**
	 * Horizontal spacing between items.
	 */
	protected int mSpacing = 0;

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
	protected int mLeftMost;

	/**
	 * Right most edge of a child seen so far during layout.
	 */
	protected int mRightMost;

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
	FlingRunnable mFlingRunnable = new FlingRunnable();

	private int mLastScrollState = OnScrollListener.SCROLL_STATE_IDLE;

	int mTouchMode = TOUCH_MODE_REST;

	private OnScrollListener mOnScrollListener;
	int mSelectedLeft;

	boolean gainFocus;

	Drawable mSelector;
	int mSelectorBorderWidth;
	int mSelectorBorderHeight;
	boolean unhandleFullVisible;

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
	protected boolean mShouldStopFling;

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
	protected boolean mReceivedInvokeKeyDown;

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
	protected boolean mStackFromRight = false;

	// the single allocated result per list view; kinda cheesey but avoids
	// allocating these thingies too often.
	private final ArrowScrollFocusResult mArrowScrollFocusResult = new ArrowScrollFocusResult();

	// used for temporary calculations.
	private final Rect mTempRect = new Rect();

	boolean mItemsCanFocus = true;

	/**
	 * Indicates whether the list selector should be drawn on top of the
	 * children or behind
	 */
	boolean mDrawSelectorOnTop = false;

	/**
	 * set a exactly selected size
	 */
	Rect mExactlyUserSelectedRect;

	Runnable mPositionScrollAfterLayout;
	/**
	 * Handles scrolling between positions within the list.
	 */
	PositionScroller mPositionScroller;

	private boolean mAreAllItemsSelectable = true;

	private int mLastTouchMode = TOUCH_MODE_UNKNOWN;
	private static final int TOUCH_MODE_UNKNOWN = -1;
	private static final int TOUCH_MODE_ON = 0;
	private static final int TOUCH_MODE_OFF = 1;

	/**
	 * The offset in pixels from the left of the AdapterView to the left of the
	 * view to select during the next layout.
	 */
	int mSpecificLeft;

	/**
	 * An estimate of how many pixels are between the left of the list and the
	 * left of the first position in the adapter, based on the last time we saw
	 * it. Used to hint where to draw edge glows.
	 */
	private int mFirstPositionDistanceGuess;

	/**
	 * An estimate of how many pixels are between the bottom of the list and the
	 * bottom of the last position in the adapter, based on the last time we saw
	 * it. Used to hint where to draw edge glows.
	 */
	private int mLastPositionDistanceGuess;

	/**
	 * The position of the view that received the down motion event
	 */
	int mMotionPosition;

	/**
	 * The offset to the left of the mMotionPosition view when the down motion
	 * event was received
	 */
	int mMotionViewOriginalLeft;

	/**
	 * The desired offset to the left of the mMotionPosition view after a scroll
	 */
	int mMotionViewNewLeft;

	public AbsHorizontalListView(Context context) {
		super(context);
	}

	public AbsHorizontalListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AbsHorizontalListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mGestureDetector = new GestureDetector(context, this);
		mGestureDetector.setIsLongpressEnabled(true);

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
		// com.android.internal.R.styleable.Gallery_unselectedAlpha, 1.0f);
		// setUnselectedAlpha(unselectedAlpha);
		//
		// a.recycle();
		//
		// // We draw the selected item last (because otherwise the item to the
		// // right overlaps it)
		// // mGroupFlags |= FLAG_USE_CHILD_DRAWING_ORDER;
		// //
		// // mGroupFlags |= FLAG_SUPPORT_STATIC_TRANSFORMATIONS;
		// setSelector(yunos.R.drawable.tui_bg_focus);
		needMeasureSelectedView = false;
	}

	public void setSelector(Drawable selector) {
		mSelector = selector;
	}

	public void setOnScrollListener(OnScrollListener l) {
		mOnScrollListener = l;
		// invokeOnItemScrollListener();
	}

	// public void setSelector(int selectorId){
	// mSelector =
	// AuiResourceFetcher.getResources(getContext()).getDrawable(selectorId);
	// }

	public void setSelectorBorderWidth(int borderWidth) {
		mSelectorBorderWidth = borderWidth;
	}

	public void setSelectorBorderHeight(int borderHeight) {
		mSelectorBorderHeight = borderHeight;
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
	 * movement to items (touch scroll, arrow-key scroll, set an item as
	 * selected).
	 *
	 * @param deltaX
	 *            Change in X from the previous event.
	 */
	void trackMotionScroll(int deltaX, int incrementalDeltaX) {

		if (getChildCount() == 0) {
			return;
		}

		boolean toLeft = deltaX < 0;

		int limitedDeltaX = deltaX;// getLimitedMotionScrollAmount(toLeft,
									// deltaX);
		// int limitedDeltaX = deltaX;
		if (limitedDeltaX != deltaX) {
			// The above call returned a limited amount, so stop any
			// scrolls/flings
			mFlingRunnable.endFling(false);
			onFinishedMovement();
		}

		offsetChildrenLeftAndRight(limitedDeltaX);

		detachOffScreenChildren(toLeft);

		// if (toLeft) {
		// // If moved left, there will be empty space on the right
		// fillToGalleryRight();
		// } else {
		// // Similarly, empty space on the left
		// fillToGalleryLeft();
		// }

		fillGap(toLeft);

		// Clear unused views
		// mRecycler.clear();

		// setSelectionToCenterChild();
		// setSelectionToNextChild();

		onScrollChanged(0, 0, 0, 0); // dummy values, View's implementation does
										// not use these.

		invalidate();
	}

	abstract void fillGap(boolean left);

	abstract int lookForSelectablePositionOnScreen(int direction);

	// /**
	// * Looks for the child that is closest to the center and sets it as the
	// * selected child.
	// */
	// private void setSelectionToCenterChild() {
	//
	// View selView = mSelectedChild;
	// if (mSelectedChild == null) return;
	//
	// int galleryCenter = getCenterOfGallery();
	//
	// // Common case where the current selected position is correct
	// if (selView.getLeft() <= galleryCenter && selView.getRight() >=
	// galleryCenter) {
	// return;
	// }
	//
	// // TODO better search
	// int closestEdgeDistance = Integer.MAX_VALUE;
	// int newSelectedChildIndex = 0;
	// for (int i = getChildCount() - 1; i >= 0; i--) {
	//
	// View child = getChildAt(i);
	//
	// if (child.getLeft() <= galleryCenter && child.getRight() >=
	// galleryCenter) {
	// // This child is in the center
	// newSelectedChildIndex = i;
	// break;
	// }
	//
	// int childClosestEdgeDistance = Math.min(Math.abs(child.getLeft() -
	// galleryCenter),
	// Math.abs(child.getRight() - galleryCenter));
	// if (childClosestEdgeDistance < closestEdgeDistance) {
	// closestEdgeDistance = childClosestEdgeDistance;
	// newSelectedChildIndex = i;
	// }
	// }
	//
	// int newPos = mFirstPosition + newSelectedChildIndex;
	//
	// if (newPos != mSelectedPosition) {
	// setSelectedPositionInt(newPos);
	// setNextSelectedPositionInt(newPos);
	// checkSelectionChanged();
	// }
	// }

	/**
	 * @return The center of this Gallery.
	 */
	private int getCenterOfGallery() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
	}

	/**
	 * Returns the number of header views in the list. Header views are special
	 * views at the top of the list that should not be recycled during a layout.
	 *
	 * @return The number of header views, 0 in the default implementation.
	 */
	int getHeaderViewsCount() {
		return 0;
	}

	/**
	 * Returns the number of footer views in the list. Footer views are special
	 * views at the bottom of the list that should not be recycled during a
	 * layout.
	 *
	 * @return The number of footer views, 0 in the default implementation.
	 */
	int getFooterViewsCount() {
		return 0;
	}

	// /**
	// * Track a motion scroll
	// *
	// * @param deltaX Amount to offset mMotionView. This is the accumulated
	// delta since the motion
	// * began. Positive numbers mean the user's finger is moving down the
	// screen.
	// * @param incrementalDeltaX Change in deltaX from the previous event.
	// * @return true if we're already at the beginning/end of the list and have
	// nothing to do.
	// */
	// boolean trackMotionScroll(int deltaX, int incrementalDeltaX) {
	// final int childCount = getChildCount();
	// if (childCount == 0) {
	// return true;
	// }
	//
	// final int firstLeft = getChildAt(0).getLeft();
	// final int lastRight = getChildAt(childCount - 1).getRight();
	//
	// final Rect listPadding = mSpinnerPadding;
	//
	// // "effective padding" In this case is the amount of padding that affects
	// // how much space should not be filled by items. If we don't clip to
	// padding
	// // there is no effective padding.
	// int effectivePaddingLeft = 0;
	// int effectivePaddingRight = 0;
	// // if ((mGroupFlags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
	// // effectivePaddingTop = listPadding.top;
	// // effectivePaddingBottom = listPadding.bottom;
	// // }
	//
	// // FIXME account for grid vertical spacing too?
	// final int spaceFront = effectivePaddingLeft - firstLeft;
	// final int end = getWidth() - effectivePaddingRight;
	// final int spaceBack = lastRight - end;
	//
	// final int width = getWidth() - mSpinnerPadding.left -
	// mSpinnerPadding.right;
	// if (deltaX < 0) {
	// deltaX = Math.max(-(width - 1), deltaX);
	// } else {
	// deltaX = Math.min(width - 1, deltaX);
	// }
	//
	// if (incrementalDeltaX < 0) {
	// incrementalDeltaX = Math.max(-(width - 1), incrementalDeltaX);
	// } else {
	// incrementalDeltaX = Math.min(width - 1, incrementalDeltaX);
	// }
	//
	// final int firstPosition = mFirstPosition;
	//
	// // Update our guesses for where the first and last views are
	// if (firstPosition == 0) {
	// mFirstPositionDistanceGuess = firstLeft - listPadding.left;
	// } else {
	// mFirstPositionDistanceGuess += incrementalDeltaX;
	// }
	// if (firstPosition + childCount == mItemCount) {
	// mLastPositionDistanceGuess = lastRight + listPadding.bottom;
	// } else {
	// mLastPositionDistanceGuess += incrementalDeltaX;
	// }
	//
	// final boolean cannotScrollBackward = (firstPosition == 0 &&
	// firstLeft >= listPadding.left && incrementalDeltaX >= 0);
	// final boolean cannotScrollLeft = (firstPosition + childCount ==
	// mItemCount &&
	// lastRight <= getWidth() - listPadding.right && incrementalDeltaX <= 0);
	//
	// if (cannotScrollBackward || cannotScrollLeft) {
	// return incrementalDeltaX != 0;
	// }
	//
	// final boolean backward = incrementalDeltaX < 0;
	//
	// final boolean inTouchMode = isInTouchMode();
	// if (inTouchMode) {
	// hideSelector();
	// }
	//
	// final int headerViewsCount = getHeaderViewsCount();
	// final int footerViewsStart = mItemCount - getFooterViewsCount();
	//
	// int start = 0;
	// int count = 0;
	//
	// if (backward) {
	// int left = -incrementalDeltaX;
	// // if ((mGroupFlags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
	// // top += listPadding.top;
	// // }
	// for (int i = 0; i < childCount; i++) {
	// final View child = getChildAt(i);
	// if (child.getRight() >= left) {
	// break;
	// } else {
	// count++;
	// int position = firstPosition + i;
	// if (position >= headerViewsCount && position < footerViewsStart) {
	// mRecycler.addScrapView(position, child);
	// }
	// }
	// }
	// } else {
	// int right = getWidth() - incrementalDeltaX;
	// // if ((mGroupFlags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
	// // bottom -= listPadding.bottom;
	// // }
	// for (int i = childCount - 1; i >= 0; i--) {
	// final View child = getChildAt(i);
	// if (child.getLeft() <= right) {
	// break;
	// } else {
	// start = i;
	// count++;
	// int position = firstPosition + i;
	// if (position >= headerViewsCount && position < footerViewsStart) {
	// mRecycler.addScrapView(position, child);
	// }
	// }
	// }
	// }
	//
	// mMotionViewNewLeft = mMotionViewOriginalLeft + deltaX;
	//
	// mBlockLayoutRequests = true;
	//
	// if (count > 0) {
	// detachViewsFromParent(start, count);
	// // mRecycler.removeSkippedScrap();
	// }
	//
	// // invalidate before moving the children to avoid unnecessary invalidate
	// // calls to bubble up from the children all the way to the top
	// if (!awakenScrollBars()) {
	// invalidate();
	// }
	//
	// offsetChildrenLeftAndRight(incrementalDeltaX);
	//
	// if (backward) {
	// mFirstPosition += count;
	// }
	//
	// final int absIncrementalDeltaX = Math.abs(incrementalDeltaX);
	// if (spaceFront < absIncrementalDeltaX || spaceBack <
	// absIncrementalDeltaX) {
	// fillGap(backward);
	// }
	//
	// if (!inTouchMode && mSelectedPosition != INVALID_POSITION) {
	// final int childIndex = mSelectedPosition - mFirstPosition;
	// if (childIndex >= 0 && childIndex < getChildCount()) {
	// positionSelector(mSelectedPosition, getChildAt(childIndex));
	// }
	// } else if (mSelectorPosition != INVALID_POSITION) {
	// final int childIndex = mSelectorPosition - mFirstPosition;
	// if (childIndex >= 0 && childIndex < getChildCount()) {
	// positionSelector(INVALID_POSITION, getChildAt(childIndex));
	// }
	// } else {
	// mSelectorRect.setEmpty();
	// }
	//
	// mBlockLayoutRequests = false;
	//
	// // invokeOnItemScrollListener();
	// return false;
	// }

	int getLimitedMotionScrollAmount(boolean motionToLeft, int deltaX) {
		int extremeItemPosition = motionToLeft != mStackFromRight ? mItemCount - 1 : 0;
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
	 *
	 * @param offset
	 *            the number of pixels to offset
	 */
	protected void offsetChildrenLeftAndRight(int offset) {
		int childCount = getChildCount();
		for (int i = childCount - 1; i >= 0; i--) {
			getChildAt(i).offsetLeftAndRight(offset);
		}
	}

	// /**
	// * @return The center of this Gallery.
	// */
	// private int getCenterOfGallery() {
	// return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 +
	// getPaddingLeft();
	// }
	//
	// /**
	// * @return The center of the given view.
	// */
	// private static int getCenterOfView(View view) {
	// return view.getLeft() + view.getWidth() / 2;
	// }

	private static int getEdgeOfView(View view, boolean isNext) {
		int edge = isNext ? view.getRight() : view.getLeft();
		// if(DEBUG){
		// Log.d(TAG, "getEdgeOfView ==> edge = " + edge + ", isNext = " +
		// isNext);
		// }
		return edge;
	}

	private int getEdgeOfGallery(boolean isNext) {
		int edge = isNext ? getWidth() : getPaddingLeft();
		// if(DEBUG){
		// Log.d(TAG, "getEdgeOfGallery ==> edge = " + edge + ", isNext = " +
		// isNext);
		// }
		return edge;
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
				int n = mStackFromRight ? (numChildren - 1 - i) : i;
				final View child = getChildAt(n);
				if (child.getRight() >= galleryLeft) {
					break;
				} else {
					start = n;
					count++;
					mRecycler.addScrapView(firstPosition + n, child);
				}
			}
			if (!mStackFromRight) {
				start = 0;
			}
		} else {
			final int galleryRight = getWidth() - getPaddingRight();
			for (int i = numChildren - 1; i >= 0; i--) {
				int n = mStackFromRight ? numChildren - 1 - i : i;
				final View child = getChildAt(n);
				if (child.getLeft() <= galleryRight) {
					break;
				} else {
					start = n;
					count++;
					mRecycler.addScrapView(firstPosition + n, child);
				}
			}
			if (mStackFromRight) {
				start = 0;
			}
		}

		detachViewsFromParent(start, count);

		if (toLeft != mStackFromRight) {
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
		 *
		 * int selectedCenter = getCenterOfView(mSelectedChild); int
		 * targetCenter = getCenterOfGallery();
		 *
		 * int scrollAmount = targetCenter - selectedCenter; if (scrollAmount !=
		 * 0) { mFlingRunnable.startUsingDistance(scrollAmount); } else {
		 * onFinishedMovement(); }
		 */
		// TODO fix me
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
	 *
	 * View selView = mSelectedChild; if (mSelectedChild == null) return;
	 *
	 * int galleryCenter = getCenterOfGallery();
	 *
	 * // Common case where the current selected position is correct if
	 * (selView.getLeft() <= galleryCenter && selView.getRight() >=
	 * galleryCenter) { return; }
	 *
	 * // TODO better search int closestEdgeDistance = Integer.MAX_VALUE; int
	 * newSelectedChildIndex = 0; for (int i = getChildCount() - 1; i >= 0; i--)
	 * {
	 *
	 * View child = getChildAt(i);
	 *
	 * if (child.getLeft() <= galleryCenter && child.getRight() >=
	 * galleryCenter) { // This child is in the center newSelectedChildIndex =
	 * i; break; }
	 *
	 * int childClosestEdgeDistance = Math.min(Math.abs(child.getLeft() -
	 * galleryCenter), Math.abs(child.getRight() - galleryCenter)); if
	 * (childClosestEdgeDistance < closestEdgeDistance) { closestEdgeDistance =
	 * childClosestEdgeDistance; newSelectedChildIndex = i; } }
	 *
	 * int newPos = mFirstPosition + newSelectedChildIndex;
	 *
	 * if (newPos != mSelectedPosition) { setSelectedPositionInt(newPos);
	 * setNextSelectedPositionInt(newPos); checkSelectionChanged(); } }
	 */

	public int getActualX() {
		if (mFlingRunnable != null) {
			return mFlingRunnable.getActualX();
		}

		return 0;
	}

	/**
	 * @return A position to select. First we try mSelectedPosition. If that has
	 *         been clobbered by entering touch mode, we then try
	 *         mResurrectToPosition. Values are pinned to the range of items
	 *         available in the adapter
	 */
	int reconcileSelectedPosition() {
		int position = mSelectedPosition;
		if (position < 0) {
			position = mResurrectToPosition;
		}
		position = Math.max(0, position);
		position = Math.min(position, mItemCount - 1);
		return position;
	}

	// @Override
	// public void onWindowFocusChanged(boolean hasWindowFocus) {
	// super.onWindowFocusChanged(hasWindowFocus);
	//
	// final int touchMode = isInTouchMode() ? TOUCH_MODE_ON : TOUCH_MODE_OFF;
	// if (!hasWindowFocus) {
	// setChildrenDrawingCacheEnabled(false);
	// if (mFlingRunnable != null) {
	// removeCallbacks(mFlingRunnable);
	// // let the fling runnable report it's new state which
	// // should be idle
	// mFlingRunnable.endFling(false);
	// if (mPositionScroller != null) {
	// mPositionScroller.stop();
	// }
	// // if (mScrollY != 0) {
	// // mScrollY = 0;
	// // invalidateParentCaches();
	// // finishGlows();
	// // invalidate();
	// // }
	// }
	// // Always hide the type filter
	// // dismissPopup();
	//
	// if (touchMode == TOUCH_MODE_OFF) {
	// // Remember the last selected element
	// mResurrectToPosition = mSelectedPosition;
	// }
	// /// M: bug fix 402306, fast scroller's overlay drawable did not hide when
	// cancel event comes @{
	// // if (mFastScroller != null) {
	// // mFastScroller.resetDraggingStateIfNecessary();
	// // }
	// /// @}
	// } else {
	// /// M: Only show Popup if AbslistView is visible
	// // if (isShown() && mFiltered && !mPopupHidden) {
	// // // Show the type filter only if a filter is in effect
	// // showPopup();
	// // }
	//
	// // If we changed touch mode since the last time we had focus
	// if (touchMode != mLastTouchMode && mLastTouchMode != TOUCH_MODE_UNKNOWN)
	// {
	// // If we come back in trackball mode, we bring the selection back
	// if (touchMode == TOUCH_MODE_OFF) {
	// // This will trigger a layout
	// resurrectSelection();
	//
	// // If we come back in touch mode, then we want to hide the selector
	// } else {
	// hideSelector();
	// mLayoutMode = LAYOUT_NORMAL;
	// //TODO layout value = 0 maybe is wrong
	// layout(0, false);
	// }
	// }
	// }
	//
	// mLastTouchMode = touchMode;
	// }

	/**
	 * Attempt to bring the selection back if the user is switching from touch
	 * to trackball mode
	 *
	 * @return Whether selection was set to something.
	 */
	boolean resurrectSelection() {
		final int childCount = getChildCount();

		if (childCount <= 0) {
			return false;
		}

		int selectedLeft = 0;
		int selectedPos;
		int childrenLeft = mSpinnerPadding.left;
		int childrenRight = getRight() - getTop() - mSpinnerPadding.right;
		final int firstPosition = mFirstPosition;
		final int toPosition = mResurrectToPosition;
		boolean leftDirection = true;

		if (toPosition >= firstPosition && toPosition < firstPosition + childCount) {
			selectedPos = toPosition;

			final View selected = getChildAt(selectedPos - mFirstPosition);
			selectedLeft = selected.getLeft();
			int selectedRight = selected.getRight();

			// We are scrolled, don't get in the fade
			if (selectedLeft < childrenLeft) {
				selectedLeft = childrenLeft + getHorizontalFadingEdgeLength();
			} else if (selectedRight > childrenRight) {
				selectedLeft = childrenRight - selected.getMeasuredWidth() - getHorizontalFadingEdgeLength();
			}
		} else {
			if (toPosition < firstPosition) {
				// Default to selecting whatever is first
				selectedPos = firstPosition;
				for (int i = 0; i < childCount; i++) {
					final View v = getChildAt(i);
					final int left = v.getLeft();

					if (i == 0) {
						// Remember the position of the first item
						selectedLeft = left;
						// See if we are scrolled at all
						if (firstPosition > 0 || left < childrenLeft) {
							// If we are scrolled, don't select anything that is
							// in the fade region
							childrenLeft += getHorizontalFadingEdgeLength();
						}
					}
					if (left >= childrenLeft) {
						// Found a view whose top is fully visisble
						selectedPos = firstPosition + i;
						selectedLeft = left;
						break;
					}
				}
			} else {
				final int itemCount = mItemCount;
				leftDirection = false;
				selectedPos = firstPosition + childCount - 1;

				for (int i = childCount - 1; i >= 0; i--) {
					final View v = getChildAt(i);
					final int left = v.getLeft();
					final int right = v.getRight();

					if (i == childCount - 1) {
						selectedLeft = left;
						if (firstPosition + childCount < itemCount || right > childrenRight) {
							childrenRight -= getHorizontalFadingEdgeLength();
						}
					}

					if (right <= childrenRight) {
						selectedPos = firstPosition + i;
						selectedLeft = left;
						break;
					}
				}
			}
		}

		mResurrectToPosition = INVALID_POSITION;
		removeCallbacks(mFlingRunnable);
		if (mPositionScroller != null) {
			mPositionScroller.stop();
		}
		// mTouchMode = TOUCH_MODE_REST;
		// clearScrollingCache();
		mSpecificLeft = selectedLeft;
		selectedPos = lookForSelectablePosition(selectedPos, leftDirection);
		if (selectedPos >= firstPosition && selectedPos <= getLastVisiblePosition()) {
			mLayoutMode = LAYOUT_SPECIFIC;
			updateSelectorState();
			setSelectionInt(selectedPos);
			// invokeOnItemScrollListener();
		} else {
			selectedPos = INVALID_POSITION;
		}
		// reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);

		return selectedPos >= 0;
	}

	/**
	 * Smoothly scroll to the specified adapter position. The view will scroll
	 * such that the indicated position is displayed.
	 *
	 * @param position
	 *            Scroll to this adapter position.
	 */
	public void smoothScrollToPosition(int position) {
		if (mPositionScroller == null) {
			mPositionScroller = new PositionScroller();
		}
		mPositionScroller.start(position);
	}

	/**
	 * Makes the item at the supplied position selected.
	 *
	 * @param position
	 *            the position of the item to select
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

		if (mPositionScroller != null) {
			mPositionScroller.stop();
		}

		// TODO layout value = 0 maybe is wrong
		layout(0, false);

		if (awakeScrollbars) {
			awakenScrollBars();
		}
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
	 * Make sure views are touching the left or right edge, as appropriate for
	 * our gravity
	 */
	protected void adjustViewsRightOrLeft() {
		final int childCount = getChildCount();
		int delta;

		if (childCount > 0) {
			View child;

			if (!mStackFromRight) {
				// Uh-oh -- we came up short. Slide all views up to make them
				// align with the top
				child = getChildAt(0);
				delta = child.getLeft() - mSpinnerPadding.left;
				if (mFirstPosition != 0) {
					// It's OK to have some space above the first item if it is
					// part of the vertical spacing
					delta -= mDividerWidth;
				}
				if (delta < 0) {
					// We only are looking to see if we are too low, not too
					// high
					delta = 0;
				}
			} else {
				// we are too high, slide all views down to align with bottom
				child = getChildAt(childCount - 1);
				delta = child.getRight() - (getWidth() - mSpinnerPadding.right);

				if (mFirstPosition + childCount < mItemCount) {
					// It's OK to have some space below the last item if it is
					// part of the vertical spacing
					delta += mDividerWidth;
				}

				if (delta > 0) {
					delta = 0;
				}
			}

			if (delta != 0) {
				offsetChildrenLeftAndRight(-delta);
			}
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		int saveCount = 0;
		final boolean clipToPadding = (getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK;
		if (clipToPadding) {
			saveCount = canvas.save();
			final int scrollX = getScrollX();
			final int scrollY = getScrollY();
			canvas.clipRect(scrollX + getPaddingLeft(), scrollY + getPaddingTop(), scrollX + getRight() - getLeft() - getPaddingRight(),
					scrollY + getBottom() - getTop() - getPaddingBottom());
			int flags = getGroupFlags();
			flags &= ~CLIP_TO_PADDING_MASK;
			setGroupFlags(getGroupFlags());
		}

		final boolean drawSelectorOnTop = mDrawSelectorOnTop;
		if (!drawSelectorOnTop) {
			drawSelector(canvas);
		}

		super.dispatchDraw(canvas);

		if (drawSelectorOnTop) {
			drawSelector(canvas);
		}

		if (clipToPadding) {
			canvas.restoreToCount(saveCount);
			int flags = getGroupFlags();
			flags &= ~CLIP_TO_PADDING_MASK;
			setGroupFlags(getGroupFlags());
		}
	}

	// @Override
	// protected boolean drawChild(Canvas canvas, View child, long drawingTime)
	// {
	// boolean isDraw = super.drawChild(canvas, child, drawingTime);
	// return isDraw;
	//
	// // return super.drawChild(canvas, child, drawingTime);
	// }

	protected void drawSelector(Canvas canvas) {
		if (mSelector != null && mSelectorRect != null && !mSelectorRect.isEmpty()) {
			Rect selectorRect = new Rect(mExactlyUserSelectedRect != null ? mExactlyUserSelectedRect : mSelectorRect);
			mSelector.setBounds(selectorRect);
			mSelector.draw(canvas);
		}
	}

	protected void resetItemLayout(View selectedView) {
		mItemWidth = selectedView.getWidth();
		mItemHeight = selectedView.getHeight();
	}

	protected int getMaxWidth(View child, int end) {
		return end;
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

	// /**
	// * Obtain a view, either by pulling an existing view from the recycler or
	// by
	// * getting a new one from the adapter. If we are animating, make sure
	// there
	// * is enough information in the view's layout parameters to animate from
	// the
	// * old to new positions.
	// *
	// * @param position Position in the gallery for the view to obtain
	// * @param offset Offset from the selected position
	// * @param x X-coordinate indicating where this view should be placed. This
	// * will either be the left or right edge of the view, depending on
	// * the fromLeft parameter
	// * @param fromLeft Are we positioning views based on the left edge? (i.e.,
	// * building from left to right)?
	// * @return A view that has been added to the gallery
	// */
	// private View makeAndAddView(int position, int offset, int x, boolean
	// fromLeft) {
	//
	// View child;
	// if (!mDataChanged) {
	// child = mRecycler.getScrapView(position);
	// if (child != null) {
	// // Can reuse an existing view
	// int childLeft = child.getLeft();
	//
	// // Remember left and right edges of where views have been placed
	// mRightMost = Math.max(mRightMost, childLeft
	// + child.getMeasuredWidth());
	// mLeftMost = Math.min(mLeftMost, childLeft);
	//
	// // Position the view
	// setUpChild(child, offset, x, fromLeft);
	//
	// return child;
	// }
	// }
	//
	// // Nothing found in the recycler -- ask the adapter for a view
	// child = mAdapter.getView(position, null, this);
	//
	// // Position the view
	// setUpChild(child, offset, x, fromLeft);
	//
	// return child;
	// }

	/**
	 * Get a view and have it show the data associated with the specified
	 * position. This is called when we have already discovered that the view is
	 * not available for reuse in the recycle bin. The only choices left are
	 * converting an old view or making a new one.
	 *
	 * @param position
	 *            The position to display
	 * @param isScrap
	 *            Array of at least 1 boolean, the first entry will become true
	 *            if the returned view was taken from the scrap heap, false if
	 *            otherwise.
	 *
	 * @return A view displaying the data associated with the specified position
	 */
	View obtainView(int position, boolean[] isScrap) {
		isScrap[0] = false;
		View scrapView;

		// scrapView = mRecycler.getTransientStateView(position);
		// if (scrapView != null) {
		// return scrapView;
		// }

		scrapView = mRecycler.getScrapView(position);

		View child;
		if (scrapView != null) {
			child = mAdapter.getView(position, scrapView, this);

			// if (child.getImportantForAccessibility() ==
			// IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
			// child.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
			// }

			if (child != scrapView) {
				mRecycler.addScrapView(position, scrapView);
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

	// /**
	// * Helper for makeAndAddView to set the position of a view and fill out
	// its
	// * layout parameters.
	// *
	// * @param child The view to position
	// * @param offset Offset from the selected position
	// * @param x X-coordinate indicating where this view should be placed. This
	// * will either be the left or right edge of the view, depending on
	// * the fromLeft parameter
	// * @param fromLeft Are we positioning views based on the left edge? (i.e.,
	// * building from left to right)?
	// */
	// private void setUpChild(View child, int offset, int x, boolean fromLeft)
	// {
	// Log.d("lawin", "----------setUpChild = " + offset + " , x = " + x);
	//
	// // Respect layout params that are already in the view. Otherwise
	// // make some up...
	// AbsHorizontalListView.LayoutParams lp =
	// (AbsHorizontalListView.LayoutParams) child.getLayoutParams();
	// if (lp == null) {
	// lp = (AbsHorizontalListView.LayoutParams) generateDefaultLayoutParams();
	// }
	//
	// addViewInLayout(child, fromLeft != mIsRtl ? -1 : 0, lp);
	//
	// child.setSelected(offset == 0);
	//
	// // Get measure specs
	// int childHeightSpec = ViewGroup.getChildMeasureSpec(mHeightMeasureSpec,
	// mSpinnerPadding.top + mSpinnerPadding.bottom, lp.height);
	// int childWidthSpec = ViewGroup.getChildMeasureSpec(mWidthMeasureSpec,
	// mSpinnerPadding.left + mSpinnerPadding.right, lp.width);
	//
	// // Measure child
	// child.measure(childWidthSpec, childHeightSpec);
	//
	// int childLeft;
	// int childRight;
	//
	// // Position vertically based on gravity setting
	// int childTop = calculateTop(child, true);
	// int childBottom = childTop + child.getMeasuredHeight();
	//
	// int width = child.getMeasuredWidth();
	// if (fromLeft) {
	// childLeft = x;
	// childRight = childLeft + width;
	// } else {
	// childLeft = x - width;
	// childRight = x;
	// }
	//
	// child.layout(childLeft, childTop, childRight, childBottom);
	// }

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
		// boolean retValue = mGestureDetector.onTouchEvent(event);
		//
		// int action = event.getAction();
		// if (action == MotionEvent.ACTION_UP) {
		// // Helper method for lifted finger
		// onUp();
		// } else if (action == MotionEvent.ACTION_CANCEL) {
		// onCancel();
		// }

		return super.onTouchEvent(event);
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
			invalidate();
			return true;
		}

		return false;
	}

	// @Override
	// public boolean onSingleTapUp(MotionEvent e) {
	//
	// if (mDownTouchPosition >= 0) {
	// boolean isNext = mDownTouchPosition > mSelectedPosition;
	// // An item tap should make it selected, so scroll to this child.
	// scrollToChild((mDownTouchPosition - mFirstPosition), isNext);
	//
	// // Also pass the click so the client knows, if it wants to.
	// if (mShouldCallbackOnUnselectedItemClick
	// || mDownTouchPosition == mSelectedPosition) {
	// performItemClick(mDownTouchView, mDownTouchPosition,
	// mAdapter.getItemId(mDownTouchPosition));
	// }
	//
	// return true;
	// }
	//
	// return false;
	// }

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
		trackMotionScroll(-1 * (int) distanceX, -1 * (int) distanceX);

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
	 * Sets the selected item and positions the selection y pixels from the top
	 * edge of the ListView. (If in touch mode, the item will not be selected
	 * but it will still be positioned appropriately.)
	 *
	 * @param position
	 *            Index (starting at 0) of the data item to be selected.
	 * @param x
	 *            The distance from the left edge of the ListView (plus padding)
	 *            that the item will be positioned.
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
			mResurrectToPosition = position;
		}

		if (position >= 0) {
			mLayoutMode = LAYOUT_SPECIFIC;
			mSpecificTop = mSpinnerPadding.left + x;

			if (mNeedSync) {
				mSyncPosition = position;
				mSyncRowId = mAdapter.getItemId(position);
			}

			if (mPositionScroller != null) {
				mPositionScroller.stop();
			}
			requestLayout();
		}
	}

	/**
	 * Find a position that can be selected (i.e., is not a separator).
	 *
	 * @param position
	 *            The starting position to look at.
	 * @param lookLeft
	 *            Whether to look down for other positions.
	 * @return The next selectable position starting at position and then
	 *         searching either up or down. Returns {@link #INVALID_POSITION} if
	 *         nothing can be found.
	 */
	@Override
	int lookForSelectablePosition(int position, boolean lookLeft) {
		if (mAdapter instanceof ListAdapter) {
			final ListAdapter adapter = (ListAdapter) mAdapter;
			if (adapter == null /*|| isInTouchMode()*/) {
				return INVALID_POSITION;
			}

			final int count = adapter.getCount();
			if (!mAreAllItemsSelectable) {
				if (lookLeft) {
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
		} else {
			return position;
		}
	}

	/**
	 * @return The maximum amount a list view will scroll in response to an
	 *         arrow event.
	 */
	/**
	 * When arrow scrolling, ListView will never scroll more than this factor
	 * times the height of the list.
	 */
	private static final float MAX_SCROLL_FACTOR = 0.33f;

	public int getMaxScrollAmount() {
		return (int) (MAX_SCROLL_FACTOR * (getRight() - getLeft()));
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

	void onSelectionChanged(int oldSelectedPosition, int newSelectedPosition, long oldSelectedRowId, long newSelectedRowId) {

	}

	/**
	 * Controls whether the selection highlight drawable should be drawn on top
	 * of the item or behind it.
	 *
	 * @param onTop
	 *            If true, the selector will be drawn on the item it is
	 *            highlighting. The default is false.
	 *
	 * @attr ref android.R.styleable#AbsListView_drawSelectorOnTop
	 */
	public void setDrawSelectorOnTop(boolean onTop) {
		mDrawSelectorOnTop = onTop;
	}

	/**
	 * set exactly selected rect width & height
	 *
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
	 * When selection changes, it is possible that the previously selected or
	 * the next selected item will change its size. If so, we need to offset
	 * some folks, and re-layout the items as appropriate.
	 *
	 * @param selectedView
	 *            The currently selected view (before changing selection).
	 *            should be <code>null</code> if there was no previous
	 *            selection.
	 * @param direction
	 *            Either {@link android.view.View#FOCUS_LEFT} or
	 *            {@link android.view.View#FOCUS_RIGHT}.
	 * @param newSelectedPosition
	 *            The position of the next selection.
	 * @param newFocusAssigned
	 *            whether new focus was assigned. This matters because when
	 *            something has focus, we don't want to show selection (ugh).
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
	 *
	 * @param child
	 *            The child
	 * @param childIndex
	 *            The view group index of the child.
	 * @param numChildren
	 *            The number of children in the view group.
	 */
	void measureAndAdjustForward(View child, int childIndex, int numChildren) {
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
	 * Measure a particular list child. TODO: unify with setUpChild.
	 *
	 * @param child
	 *            The child.
	 */
	private void measureItem(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
		}

		int childHeightSpec = ViewGroup.getChildMeasureSpec(mHeightMeasureSpec, mSpinnerPadding.top + mSpinnerPadding.bottom, p.height);
		int lpWidth = p.width;
		int childWidthSpec;
		if (lpWidth > 0) {
			childWidthSpec = MeasureSpec.makeMeasureSpec(lpWidth, MeasureSpec.EXACTLY);
		} else {
			childWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	/**
	 * Layout a child that has been measured, preserving its top position. TODO:
	 * unify with setUpChild.
	 *
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

	protected int getListRight() {
		final boolean clipToPadding = (getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK;
		return clipToPadding ? getWidth() - mSpinnerPadding.right : getUnClipToPaddingRightEdge();
	}

	/**
	 * @return 当非clipToPadding时，获取该控件LeftEdge位置
	 */
	int getUnClipToPaddingRightEdge() {
		return getWidth() - mSpinnerPadding.right;
	}

	protected int getListLeft() {
		final boolean clipToPadding = (getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK;
		// TODO:need fix the logic
		return clipToPadding ? mSpinnerPadding.left : getUnClipToPaddingLeftEdge();
	}

	/**
	 * @return 当非clipToPadding时，获取该控件LeftEdge位置
	 */
	int getUnClipToPaddingLeftEdge() {
		return mSpinnerPadding.left;
	}

	/**
	 * Determine the distance to the nearest edge of a view in a particular
	 * direction.
	 *
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
	 * Do an arrow scroll based on focus searching. If a new view is given
	 * focus, return the selection delta and amount to scroll via an
	 * {@link ArrowScrollFocusResult}, otherwise, return null.
	 *
	 * @param direction
	 *            either {@link android.view.View#FOCUS_LEFT} or
	 *            {@link android.view.View#FOCUS_RIGHT}.
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
				final int xSearchPoint = (selectedView != null && selectedView.getLeft() > listLeft) ? selectedView.getLeft() : listLeft;
				mTempRect.set(xSearchPoint, 0, xSearchPoint, 0);
			} else {
				final boolean rightFadingEdgeShowing = (mFirstPosition + getChildCount() - 1) < mItemCount;
				final int listRight = getHeight() - mSpinnerPadding.right - (rightFadingEdgeShowing ? getArrowScrollPreviewLength() : 0);
				final int xSearchPoint = (selectedView != null && selectedView.getRight() < listRight) ? selectedView.getBottom()
						: listRight;
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
	 * @param direction
	 *            either {@link android.view.View#FOCUS_UP} or
	 *            {@link android.view.View#FOCUS_DOWN}.
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
	 * When stack from right is set to true, the list fills its content starting
	 * from the right of the view.
	 * 
	 * @param stackFromRight
	 *            true to pin the view's content to the right edge, false to pin
	 *            the view's content to the left edge
	 */
	public void setStackFromRight(boolean stackFromRight) {
		// TODO:need add stackFromRight feature
		// mStackFromRight = stackFromRight;
	}

	/**
	 * Indicates whether the content of this view is pinned to, or stacked from,
	 * the right edge.
	 * 
	 * @return true if the content is stacked from the right edge, false
	 *         otherwise
	 */
	public boolean isStackFromRight() {
		return mStackFromRight;
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
					if (mSelectedPosition != INVALID_POSITION) {
						performItemClick(getChildAt(selectedIndex), mSelectedPosition, mAdapter.getItemId(mSelectedPosition));
					}
				}
			}

			// Clear the flag
			mReceivedInvokeKeyDown = false;

			return true;
		}
		}

		return super.onKeyUp(keyCode, event);
	}

	/*
	 * original code private boolean scrollToChild(int childPosition) { View
	 * child = getChildAt(childPosition);
	 * 
	 * if (child != null) { int distance = getCenterOfGallery() -
	 * getCenterOfView(child); mFlingRunnable.startUsingDistance(distance);
	 * return true; }
	 * 
	 * return false; }
	 */

	private boolean scrollToChild(int childPosition, boolean isNext) {
		View child = getChildAt(childPosition);
		int distance = 0;
		int parentEdge = getEdgeOfGallery(isNext);
		int itemIndex = childPosition + mFirstPosition;
		boolean isNewCreate = false;
		if (child == null) {
			// child = makeAndAddView(itemIndex, itemIndex, parentEdge, isNext);
			// Log.d("lawin", "----------makeAndAddView  parentEdge = " +
			// parentEdge);
			// isNewCreate = true;
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
		// updateSelectedItemMetadata(position);
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
		child.setFocusable(true);

		if (hasFocus()) {
			child.requestFocus();
		}

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

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		this.gainFocus = gainFocus;
		// TODO:temp fix
		handlerFocusChanged(gainFocus);
	}

	protected void handlerFocusChanged(boolean gainFocus) {
		if (gainFocus) {
			if (mSelectedPosition == INVALID_POSITION) {
				mSelectedPosition = 0;
			}
			positionSelector(mSelectedPosition, getSelectedView());
		}
		// TODO 在鼠标和键盘事件间切换会存在bug，需要参考AbsListView的onFocusChanged，以后再完善
	}

	/**
	 * Smoothly scroll by distance pixels over duration milliseconds.
	 * 
	 * @param distance
	 *            Distance to scroll in pixels.
	 * @param duration
	 *            Duration of the scroll animation in milliseconds.
	 */
	public void smoothScrollBy(int distance, int duration) {
		smoothScrollBy(distance, duration, false);
	}

	void smoothScrollBy(int distance, int duration, boolean linear) {
		if (mFlingRunnable == null) {
			mFlingRunnable = new FlingRunnable();
		}

		// No sense starting to scroll if we're not going anywhere
		final int firstPos = mFirstPosition;
		final int childCount = getChildCount();
		final int lastPos = firstPos + childCount;
		final int leftLimit = mSpinnerPadding.left;
		final int rightLimit = getWidth() - mSpinnerPadding.right;

		if (distance == 0 || mItemCount == 0 || childCount == 0 || (firstPos == 0 && getChildAt(0).getLeft() == leftLimit && distance < 0)
				|| (lastPos == mItemCount && getChildAt(childCount - 1).getRight() == rightLimit && distance > 0)) {

			mFlingRunnable.endFling(false);
			if (mPositionScroller != null) {
				mPositionScroller.stop();
			}
		} else {
			// TODO
			reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
			// mFlingRunnable.startScroll(distance, duration, linear);
			mFlingRunnable.startScroll(distance, duration);
		}
	}

	void reportScrollStateChange(int newState) {
		if (newState != mLastScrollState) {
			if (mOnScrollListener != null) {
				mLastScrollState = newState;
				mOnScrollListener.onScrollStateChanged(this, newState);
			}
		}
	}

	public void postOnAnimation(Runnable action) {
		// API 16 need super.postOnAniamtion(action)
		post(action);
	}

	class PositionScroller implements Runnable {
		private static final int SCROLL_DURATION = 200;

		private static final int MOVE_RIGHT_POS = 1;
		private static final int MOVE_LEFT_POS = 2;
		private static final int MOVE_RIGHT_BOUND = 3;
		private static final int MOVE_LEFT_BOUND = 4;
		private static final int MOVE_OFFSET = 5;

		private int mMode;
		private int mTargetPos;
		private int mBoundPos;
		private int mLastSeenPos;
		private int mScrollDuration;
		private final int mExtraScroll;

		private int mOffsetFromLeft;

		PositionScroller() {
			mExtraScroll = ViewConfiguration.get(getContext()).getScaledFadingEdgeLength();
		}

		void start(final int position) {
			stop();

			if (mDataChanged) {
				// Wait until we're back in a stable state to try this.
				mPositionScrollAfterLayout = new Runnable() {
					@Override
					public void run() {
						start(position);
					}
				};
				return;
			}

			final int childCount = getChildCount();
			if (childCount == 0) {
				// Can't scroll without children.
				return;
			}

			final int firstPos = mFirstPosition;
			final int lastPos = firstPos + childCount - 1;

			int viewTravelCount;
			int clampedPosition = Math.max(0, Math.min(getCount() - 1, position));
			if (clampedPosition < firstPos) {
				viewTravelCount = firstPos - clampedPosition + 1;
				mMode = MOVE_LEFT_POS;
			} else if (clampedPosition > lastPos) {
				viewTravelCount = clampedPosition - lastPos + 1;
				mMode = MOVE_RIGHT_POS;
			} else {
				scrollToVisible(clampedPosition, INVALID_POSITION, SCROLL_DURATION);
				return;
			}

			if (viewTravelCount > 0) {
				mScrollDuration = SCROLL_DURATION / viewTravelCount;
			} else {
				mScrollDuration = SCROLL_DURATION;
			}
			mTargetPos = clampedPosition;
			mBoundPos = INVALID_POSITION;
			mLastSeenPos = INVALID_POSITION;

			postOnAnimation(this);
		}

		void start(final int position, final int boundPosition) {
			stop();

			if (boundPosition == INVALID_POSITION) {
				start(position);
				return;
			}

			if (mDataChanged) {
				// Wait until we're back in a stable state to try this.
				mPositionScrollAfterLayout = new Runnable() {
					@Override
					public void run() {
						start(position, boundPosition);
					}
				};
				return;
			}

			final int childCount = getChildCount();
			if (childCount == 0) {
				// Can't scroll without children.
				return;
			}

			final int firstPos = mFirstPosition;
			final int lastPos = firstPos + childCount - 1;

			int viewTravelCount;
			int clampedPosition = Math.max(0, Math.min(getCount() - 1, position));
			if (clampedPosition < firstPos) {
				final int boundPosFromLast = lastPos - boundPosition;
				if (boundPosFromLast < 1) {
					// Moving would shift our bound position off the screen.
					// Abort.
					return;
				}

				final int posTravel = firstPos - clampedPosition + 1;
				final int boundTravel = boundPosFromLast - 1;
				if (boundTravel < posTravel) {
					viewTravelCount = boundTravel;
					mMode = MOVE_LEFT_BOUND;
				} else {
					viewTravelCount = posTravel;
					mMode = MOVE_LEFT_POS;
				}
			} else if (clampedPosition > lastPos) {
				final int boundPosFromFirst = boundPosition - firstPos;
				if (boundPosFromFirst < 1) {
					// Moving would shift our bound position off the screen.
					// Abort.
					return;
				}

				final int posTravel = clampedPosition - lastPos + 1;
				final int boundTravel = boundPosFromFirst - 1;
				if (boundTravel < posTravel) {
					viewTravelCount = boundTravel;
					mMode = MOVE_RIGHT_BOUND;
				} else {
					viewTravelCount = posTravel;
					mMode = MOVE_RIGHT_POS;
				}
			} else {
				scrollToVisible(clampedPosition, boundPosition, SCROLL_DURATION);
				return;
			}

			if (viewTravelCount > 0) {
				mScrollDuration = SCROLL_DURATION / viewTravelCount;
			} else {
				mScrollDuration = SCROLL_DURATION;
			}
			mTargetPos = clampedPosition;
			mBoundPos = boundPosition;
			mLastSeenPos = INVALID_POSITION;

			postOnAnimation(this);
		}

		void startWithOffset(int position, int offset) {
			startWithOffset(position, offset, SCROLL_DURATION);
		}

		void startWithOffset(final int position, int offset, final int duration) {
			stop();

			if (mDataChanged) {
				// Wait until we're back in a stable state to try this.
				final int postOffset = offset;
				mPositionScrollAfterLayout = new Runnable() {
					@Override
					public void run() {
						startWithOffset(position, postOffset, duration);
					}
				};
				return;
			}

			final int childCount = getChildCount();
			if (childCount == 0) {
				// Can't scroll without children.
				return;
			}

			offset += getPaddingTop();

			mTargetPos = Math.max(0, Math.min(getCount() - 1, position));
			mOffsetFromLeft = offset;
			mBoundPos = INVALID_POSITION;
			mLastSeenPos = INVALID_POSITION;
			mMode = MOVE_OFFSET;

			final int firstPos = mFirstPosition;
			final int lastPos = firstPos + childCount - 1;

			int viewTravelCount;
			if (mTargetPos < firstPos) {
				viewTravelCount = firstPos - mTargetPos;
			} else if (mTargetPos > lastPos) {
				viewTravelCount = mTargetPos - lastPos;
			} else {
				// On-screen, just scroll.
				final int targetLeft = getChildAt(mTargetPos - firstPos).getLeft();
				smoothScrollBy(targetLeft - offset, duration, true);
				return;
			}

			// Estimate how many screens we should travel
			final float screenTravelCount = (float) viewTravelCount / childCount;
			mScrollDuration = screenTravelCount < 1 ? duration : (int) (duration / screenTravelCount);
			mLastSeenPos = INVALID_POSITION;

			postOnAnimation(this);
		}

		/**
		 * Scroll such that targetPos is in the visible padded region without
		 * scrolling boundPos out of view. Assumes targetPos is onscreen.
		 */
		void scrollToVisible(int targetPos, int boundPos, int duration) {
			final int firstPos = mFirstPosition;
			final int childCount = getChildCount();
			final int lastPos = firstPos + childCount - 1;
			final int paddedLeft = mSpinnerPadding.left;
			final int paddedRight = getWidth() - mSpinnerPadding.right;

			// if (DEBUG && (targetPos < firstPos || targetPos > lastPos)) {
			// Log.w(TAG, "scrollToVisible called with targetPos " + targetPos
			// + " not visible [" + firstPos + ", " + lastPos + "]");
			// }
			if (boundPos < firstPos || boundPos > lastPos) {
				// boundPos doesn't matter, it's already offscreen.
				boundPos = INVALID_POSITION;
			}

			final View targetChild = getChildAt(targetPos - firstPos);
			final int targetLeft = targetChild.getLeft();
			final int targetRight = targetChild.getRight();
			int scrollBy = 0;

			if (targetRight > paddedRight) {
				scrollBy = targetRight - paddedRight;
			}
			if (targetLeft < paddedLeft) {
				scrollBy = targetLeft - paddedLeft;
			}

			if (scrollBy == 0) {
				return;
			}

			if (boundPos >= 0) {
				final View boundChild = getChildAt(boundPos - firstPos);
				final int boundLeft = boundChild.getLeft();
				final int boundRight = boundChild.getRight();
				final int absScroll = Math.abs(scrollBy);

				if (scrollBy < 0 && boundRight + absScroll > paddedRight) {
					// Don't scroll the bound view off the bottom of the screen.
					scrollBy = Math.max(0, boundRight - paddedRight);
				} else if (scrollBy > 0 && boundLeft - absScroll < paddedLeft) {
					// Don't scroll the bound view off the top of the screen.
					scrollBy = Math.min(0, boundLeft - paddedLeft);
				}
			}

			smoothScrollBy(scrollBy, duration);
		}

		void stop() {
			removeCallbacks(this);
		}

		public void run() {
			final int listWidth = getWidth();
			final int firstPos = mFirstPosition;

			switch (mMode) {
			case MOVE_RIGHT_POS: {
				final int lastViewIndex = getChildCount() - 1;
				final int lastPos = firstPos + lastViewIndex;

				if (lastViewIndex < 0) {
					return;
				}

				if (lastPos == mLastSeenPos) {
					// No new views, let things keep going.
					postOnAnimation(this);
					return;
				}

				final View lastView = getChildAt(lastViewIndex);
				final int lastViewWidth = lastView.getWidth();
				final int lastViewLeft = lastView.getLeft();
				final int lastViewPixelsShowing = listWidth - lastViewLeft;
				final int extraScroll = lastPos < mItemCount - 1 ? Math.max(mSpinnerPadding.bottom, mExtraScroll) : mSpinnerPadding.bottom;

				final int scrollBy = lastViewWidth - lastViewPixelsShowing + extraScroll;
				smoothScrollBy(-scrollBy, mScrollDuration, true);

				mLastSeenPos = lastPos;
				if (lastPos < mTargetPos) {
					postOnAnimation(this);
				}
				break;
			}

			case MOVE_RIGHT_BOUND: {
				final int nextViewIndex = 1;
				final int childCount = getChildCount();

				if (firstPos == mBoundPos || childCount <= nextViewIndex || firstPos + childCount >= mItemCount) {
					return;
				}
				final int nextPos = firstPos + nextViewIndex;

				if (nextPos == mLastSeenPos) {
					// No new views, let things keep going.
					postOnAnimation(this);
					return;
				}

				final View nextView = getChildAt(nextViewIndex);
				final int nextViewWidth = nextView.getWidth();
				final int nextViewLeft = nextView.getLeft();
				final int extraScroll = Math.max(mSpinnerPadding.right, mExtraScroll);
				if (nextPos < mBoundPos) {
					smoothScrollBy(Math.max(0, nextViewWidth + nextViewLeft - extraScroll), mScrollDuration, true);

					mLastSeenPos = nextPos;

					postOnAnimation(this);
				} else {
					if (nextViewLeft > extraScroll) {
						smoothScrollBy(nextViewLeft - extraScroll, mScrollDuration, true);
					}
				}
				break;
			}

			case MOVE_LEFT_POS: {
				if (firstPos == mLastSeenPos) {
					// No new views, let things keep going.
					postOnAnimation(this);
					return;
				}

				final View firstView = getChildAt(0);
				if (firstView == null) {
					return;
				}
				final int firstViewLeft = firstView.getLeft();
				final int extraScroll = firstPos > 0 ? Math.max(mExtraScroll, mSpinnerPadding.left) : mSpinnerPadding.left;

				smoothScrollBy(firstViewLeft - extraScroll, mScrollDuration, true);

				mLastSeenPos = firstPos;

				if (firstPos > mTargetPos) {
					postOnAnimation(this);
				}
				break;
			}

			case MOVE_LEFT_BOUND: {
				final int lastViewIndex = getChildCount() - 2;
				if (lastViewIndex < 0) {
					return;
				}
				final int lastPos = firstPos + lastViewIndex;

				if (lastPos == mLastSeenPos) {
					// No new views, let things keep going.
					postOnAnimation(this);
					return;
				}

				final View lastView = getChildAt(lastViewIndex);
				final int lastViewWidth = lastView.getWidth();
				final int lastViewLeft = lastView.getLeft();
				final int lastViewPixelsShowing = listWidth - lastViewLeft;
				final int extraScroll = Math.max(mSpinnerPadding.left, mExtraScroll);
				mLastSeenPos = lastPos;
				if (lastPos > mBoundPos) {
					smoothScrollBy(-(lastViewPixelsShowing - extraScroll), mScrollDuration, true);
					postOnAnimation(this);
				} else {
					final int right = listWidth - extraScroll;
					final int lastViewRight = lastViewLeft + lastViewWidth;
					if (right > lastViewRight) {
						smoothScrollBy(-(right - lastViewRight), mScrollDuration, true);
					}
				}
				break;
			}

			case MOVE_OFFSET: {
				if (mLastSeenPos == firstPos) {
					// No new views, let things keep going.
					postOnAnimation(this);
					return;
				}

				mLastSeenPos = firstPos;

				final int childCount = getChildCount();
				final int position = mTargetPos;
				final int lastPos = firstPos + childCount - 1;

				int viewTravelCount = 0;
				if (position < firstPos) {
					viewTravelCount = firstPos - position + 1;
				} else if (position > lastPos) {
					viewTravelCount = position - lastPos;
				}

				// Estimate how many screens we should travel
				final float screenTravelCount = (float) viewTravelCount / childCount;

				final float modifier = Math.min(Math.abs(screenTravelCount), 1.f);
				if (position < firstPos) {
					final int distance = (int) (-getWidth() * modifier);
					final int duration = (int) (mScrollDuration * modifier);
					smoothScrollBy(distance, duration, true);
					postOnAnimation(this);
				} else if (position > lastPos) {
					final int distance = (int) (getWidth() * modifier);
					final int duration = (int) (mScrollDuration * modifier);
					smoothScrollBy(distance, duration, true);
					postOnAnimation(this);
				} else {
					// On-screen, just scroll.
					final int targetLeft = getChildAt(position - firstPos).getLeft();
					final int distance = targetLeft - mOffsetFromLeft;
					final int duration = (int) (mScrollDuration * ((float) Math.abs(distance) / getWidth()));
					smoothScrollBy(distance, duration, true);
				}
				break;
			}

			default:
				break;
			}
		}
	}

	protected OverScroller getOverScrollerFromFlingRunnable() {
		if (mFlingRunnable != null) {
			return mFlingRunnable.mScroller;
		}
		return null;
	}

	/**
	 * Set interpolator type when scroll
	 */
	Interpolator mInterpolator = null;

	public void setFlingInterpolator(Interpolator interpolator) {
		mInterpolator = interpolator;
	}

	/**
	 * Responsible for fling behavior. Use {@link #startUsingVelocity(int)} to
	 * initiate a fling. Each frame of the fling is handled in {@link #run()}. A
	 * FlingRunnable will keep re-posting itself until the fling is done.
	 */
	class FlingRunnable implements Runnable {
		/**
		 * Tracks the decay of a fling scroll
		 */
		OverScroller mScroller;

		/**
		 * X value reported by mScroller on the previous fling
		 */
		private int mLastFlingX;
		private int mActualX;

		public FlingRunnable() {
			// TODO: add interpolator
			mScroller = new OverScroller(getContext(), mInterpolator);
		}

		public int getActualX() {
			return mActualX;
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

		public void startScroll(int distance, int duration) {
			if (distance == 0)
				return;

			startCommon();

			int initialX = distance < 0 ? Integer.MAX_VALUE : 0;
			mLastFlingX = initialX;
			mScroller.startScroll(initialX, 0, distance, 0, duration);
			mTouchMode = TOUCH_MODE_FLING;
			post(this);
		}

		public void startUsingDistance(int distance) {
			if (distance == 0)
				return;

			startCommon();

			mLastFlingX = 0;
			mScroller.startScroll(0, 0, -distance, 0, mAnimationDuration);
			mTouchMode = TOUCH_MODE_FLING;
			post(this);
		}

		public void stop(boolean scrollIntoSlots) {
			removeCallbacks(this);
			endFling(scrollIntoSlots);
		}

		public void endFling(boolean scrollIntoSlots) {
			/*
			 * Force the scroller's status to finished (without setting its
			 * position to the end)
			 */

			mTouchMode = TOUCH_MODE_REST;
			removeCallbacks(this);
			mScroller.abortAnimation();
			Log.w(TAG, "endFling");
			reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
			if (scrollIntoSlots)
				scrollIntoSlots();
		}

		@Override
		public void run() {
			switch (mTouchMode) {
			default:
				endFling(true);
				return;

			case TOUCH_MODE_FLING: {
				if (mItemCount == 0) {
					endFling(true);
					return;
				}

				mShouldStopFling = false;

				final OverScroller scroller = mScroller;
				boolean more = scroller.computeScrollOffset();
				final int x = scroller.getCurrX();
				Log.d(TAG, "x = " + x + ", isFinished = " + mScroller.isFinished());
				mActualX = x;
				// Flip sign to convert finger direction to list items direction
				// (e.g. finger moving down means list is moving towards the
				// top)
				int delta = mLastFlingX - x;

				// Pretend that each frame of a fling scroll is a touch scroll
				if (delta > 0) {
					// Moving towards the left. Use leftmost view as
					// mDownTouchPosition
					mDownTouchPosition = mStackFromRight ? (mFirstPosition + getChildCount() - 1) : mFirstPosition;

					// Don't fling more than 1 screen
					delta = Math.min(getWidth() - getPaddingLeft() - getPaddingRight() - 1, delta);
				} else {
					// Moving towards the right. Use rightmost view as
					// mDownTouchPosition
					int offsetToLast = getChildCount() - 1;
					mDownTouchPosition = mStackFromRight ? mFirstPosition : (mFirstPosition + getChildCount() - 1);

					// Don't fling more than 1 screen
					delta = Math.max(-(getWidth() - getPaddingRight() - getPaddingLeft() - 1), delta);
				}

				trackMotionScroll(delta, delta);

				if (more && !mShouldStopFling) {
					mLastFlingX = x;
					post(this);
				} else {
					endFling(true);
					// position selector position
					if (getSelectedView() != null) {
						positionSelector(mSelectedPosition, getSelectedView());
						mSelectedLeft = getSelectedView().getLeft();
					}
				}
			}
			}
		}

	}

	public interface OnScrollListener {

		/**
		 * The view is not scrolling. Note navigating the list using the
		 * trackball counts as being in the idle state since these transitions
		 * are not animated.
		 */
		public static int SCROLL_STATE_IDLE = 0;

		/**
		 * The user is scrolling using touch, and their finger is still on the
		 * screen
		 */
		public static int SCROLL_STATE_TOUCH_SCROLL = 1;

		/**
		 * The user had previously been scrolling using touch and had performed
		 * a fling. The animation is now coasting to a stop
		 */
		public static int SCROLL_STATE_FLING = 2;

		/**
		 * Callback method to be invoked while the list view or grid view is
		 * being scrolled. If the view is being scrolled, this method will be
		 * called before the next frame of the scroll is rendered. In
		 * particular, it will be called before any calls to
		 * {@link Adapter#getView(int, View, ViewGroup)}.
		 * 
		 * @param view
		 *            The view whose scroll state is being reported
		 * 
		 * @param scrollState
		 *            The current scroll state. One of
		 *            {@link #SCROLL_STATE_IDLE},
		 *            {@link #SCROLL_STATE_TOUCH_SCROLL} or
		 *            {@link #SCROLL_STATE_IDLE}.
		 */
		public void onScrollStateChanged(AbsHorizontalListView view, int scrollState);

		/**
		 * Callback method to be invoked when the list or grid has been
		 * scrolled. This will be called after the scroll has completed
		 * 
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
		public void onScroll(AbsHorizontalListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
	}
}
