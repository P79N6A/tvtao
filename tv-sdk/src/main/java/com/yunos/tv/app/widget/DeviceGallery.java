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

public class DeviceGallery extends AbsGallery {

	private static final String TAG = "DeviceGallery";

	protected static final boolean DEBUG = true;

	/**
	 * Left most edge of a child seen so far during layout.
	 */
	private int mLeftMost;

	/**
	 * Right most edge of a child seen so far during layout.
	 */
	private int mRightMost;

	/**
	 * Executes the delta scrolls from a fling or scroll movement.
	 */
	private FlingRunnable mFlingRunnable = new FlingRunnable();
	private FlingRunnableLeft mFlingRunnableLeft = new FlingRunnableLeft();
	private FlingRunnableRight mFlingRunnableRight = new FlingRunnableRight();
	private AttendRunnabe mAttendRunnabe = new AttendRunnabe();
	/**
	 * If true, mFirstPosition is the position of the rightmost child, and the
	 * children are ordered right to left.
	 */
	private boolean mIsRtl = true;

	private View mAddedView = null;

	protected int mFixedPaddingLeft = 145;
	protected int mFixedPaddingRight = 145;

	protected OnAddListener mOnAddListener;
	protected OnRemoveListener mOnRemoveListener;

	protected boolean mNeedReset = false;

	public interface OnAddListener {
		public void onAddStart(int position, View child, ViewGroup parent);

		public void onAddFinish(int position, View child, ViewGroup parent);
	}

	public interface OnRemoveListener {
		public void onRemoveStart(int position, View child, ViewGroup parent);

		public void onRemoveFinish(int position, View child, ViewGroup parent);
	}

	public DeviceGallery(Context context) {
		super(context);
		init(context);
	}

	public DeviceGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DeviceGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public void setAddOrRemoveDuration(int duration) {
		mAnimationDuration = duration;
	}

	public void setOnAddListener(OnAddListener l) {
		mOnAddListener = l;
	}

	public void setOnRemoveListener(OnRemoveListener l) {
		mOnRemoveListener = l;
	}

	public void setFixedPadding(int paddingLeft, int paddingRight) {
		mFixedPaddingLeft = paddingLeft;
		mFixedPaddingRight = paddingRight;
	}

	public boolean add(int position) {
		Log.d(TAG, "add: position = " + position);
		if (isAdding() || isRemoving()) {
			throw new IllegalArgumentException("DeviceGallery: add, you can't add item when you are adding or removeing item");
		}

		if (mLastScrollState == OnScrollListener.SCROLL_STATE_FLING) {
			throw new IllegalArgumentException("DeviceGallery: add, you can't add item when you are scrolling");
		}

		if (position > mItemCount) {
			throw new IllegalArgumentException("DeviceGallery: add, you can't add position must be less than item count, but it is = " + position + ", item count = " + mItemCount);
		}

		if (getChildCount() <= 0) {
			Log.i(TAG, "add branch 0");
			mItemCount++;
			mFirstPosition = 0;
			setSelectedPositionInt(0);
			setNextSelectedPositionInt(0);
			View attendView = addCenter(mFixedPaddingLeft, true);
			mAttendRunnabe.startAttend(attendView, AttendRunnabe.ATTEND, position, false);
			mNeedReset = true;
			return true;
		}

		boolean isAnimate = false;
		if (position > getFirstVisiblePosition() && position <= getLastVisiblePosition()) {
			Log.i(TAG, "add branch 1");
			View child = getChildAt(0);
			int actualWidth = getWidth() - mFixedPaddingLeft - mFixedPaddingRight;
			int childWidth = mItemCount * child.getWidth() + mSpacing * (mItemCount - 1);
			int newChildWidth = childWidth + child.getWidth() + mSpacing;

			if (position == mSelectedPosition) {
				mNeedReset = true;
			}

			int selectedPosition = mSelectedPosition;
			if (position < mSelectedPosition) {
				selectedPosition++;
				setSelectedPositionInt(selectedPosition);
				setNextSelectedPositionInt(selectedPosition);
			}
			
			if (newChildWidth <= actualWidth) {
				Log.i(TAG, "add branch 1-1");
				int oldLeftEdge = getChildAt(0).getLeft();
				int newLeftEdge = (getWidth() - newChildWidth) / 2;
				int leftScroll = oldLeftEdge - newLeftEdge;
				int childLeft = getChildAt(position - getFirstVisiblePosition() - 1).getRight() + mSpacing - leftScroll;
				int oldRightEdge = getChildAt(getChildCount() - 1).getRight();
				int newRightEdge = getWidth() - newLeftEdge;
				int rightScroll = newRightEdge - oldRightEdge;
				View attendView = makeAndAddView(position, Math.abs(position - mSelectedPosition), childLeft, true, position);
				mAttendRunnabe.startAttend(attendView, AttendRunnabe.ATTEND, position, false);
				smoothScrollLeftBy(-leftScroll, position - 1);
				smoothScrollRightBy(rightScroll, position + 1);
			} else if (childWidth <= actualWidth) {
				Log.i(TAG, "add branch 1-2");
				int oldLeftEdge = getChildAt(0).getLeft();
				int newLeftEdge = mFixedPaddingLeft;
				int leftScroll = oldLeftEdge - newLeftEdge;
				int childLeft = getChildAt(position - getFirstVisiblePosition() - 1).getRight() + mSpacing - leftScroll;
				int oldRightEdge = getChildAt(getChildCount() - 1).getRight();
				int newRightEdge = mFixedPaddingLeft + getChildAt(0).getWidth() * (getChildCount() + 1) + mSpacing * getChildCount();
				int rightScroll = newRightEdge - oldRightEdge;

				View attendView = makeAndAddView(position, Math.abs(position - mSelectedPosition), childLeft, true, position);
				mAttendRunnabe.startAttend(attendView, AttendRunnabe.ATTEND, position, false);
				smoothScrollLeftBy(-leftScroll, position - 1);
				smoothScrollRightBy(rightScroll, position + 1);
			} else {
				Log.i(TAG, "add branch 1-3");
				int childLeft = getChildAt(position - getFirstVisiblePosition() - 1).getRight() + mSpacing;
				int rightScroll = getChildAt(0).getWidth() + mSpacing;
				View attendView = makeAndAddView(position, Math.abs(position - mSelectedPosition), childLeft, true, position - getFirstVisiblePosition());
				mAttendRunnabe.startAttend(attendView, AttendRunnabe.ATTEND, position, false);
				smoothScrollRightBy(rightScroll, position + 1);
			}

			isAnimate = true;

		} else if (position > getLastVisiblePosition()) {
			Log.i(TAG, "add branch 2");
			View leftChild = getChildAt(0);
			int actualWidth = getWidth() - mFixedPaddingLeft - mFixedPaddingRight;
			int childWidth = mItemCount * leftChild.getWidth() + mSpacing * (mItemCount - 1);
			if (actualWidth > childWidth) {
				Log.i(TAG, "add branch 2-1");
				int oldLeftEdge = leftChild.getLeft();
				int newLeftEdge = (getWidth() - (childWidth + leftChild.getWidth() + mSpacing)) / 2;
				if (newLeftEdge < mFixedPaddingLeft) {
					newLeftEdge = mFixedPaddingLeft;
				}
				int leftScroll = oldLeftEdge - newLeftEdge;
				View rightChild = getChildAt(getChildCount() - 1);

				int count = getChildCount();
				View attendView = makeAndAddView(position, Math.abs(position - mSelectedPosition), rightChild.getRight() + mSpacing - leftScroll, true);
				mAttendRunnabe.startAttend(attendView, AttendRunnabe.ATTEND, position, false);
				smoothScrollLeftBy(-leftScroll, count - 1);
				isAnimate = true;

				if (position == mSelectedPosition) {
					mNeedReset = true;
				}

			} else {
				Log.i(TAG, "add branch 2-2");
				View rightChild = getChildAt(getChildCount() - 1);
				int rightEdge = rightChild.getRight() + mSpacing;
				if (rightEdge < getWidth()) {
					Log.i(TAG, "add branch 2-2-1");
					View attendView = makeAndAddView(position, Math.abs(position - mSelectedPosition), rightEdge, true);
					mAttendRunnabe.startAttend(attendView, AttendRunnabe.ATTEND, position, false);
					isAnimate = true;

					if (position == mSelectedPosition) {
						mNeedReset = true;
					}
				}
			}
		} else if (position == getFirstVisiblePosition()) {
			Log.i(TAG, "add branch 3");
			View leftChild = getChildAt(0);
			int actualWidth = getWidth() - mFixedPaddingLeft - mFixedPaddingRight;
			int childWidth = mItemCount * leftChild.getWidth() + mSpacing * (mItemCount - 1);
			int newChildWidth = childWidth + leftChild.getWidth() + mSpacing;
			int leftEdge = leftChild.getLeft();
			int oldLeftEdge = leftChild.getLeft() - mSpacing - leftChild.getWidth();
			int newLeftEdge = mFixedPaddingLeft;
			int rightScroll = 0;
			if (newChildWidth <= actualWidth) {
				Log.i(TAG, "add branch 3-1");
				newLeftEdge = (getWidth() - newChildWidth) / 2;
				if (newLeftEdge < mFixedPaddingLeft) {
					newLeftEdge = mFixedPaddingLeft;
				}
				rightScroll = newLeftEdge - oldLeftEdge;

				setSelectedPositionInt(position);
				setNextSelectedPositionInt(position);
				if (position == mSelectedPosition) {
					mNeedReset = true;
				}
				
				View attendView = makeAndAddView(position, Math.abs(position - mSelectedPosition), newLeftEdge, true, 0);
				mAttendRunnabe.startAttend(attendView, AttendRunnabe.ATTEND, position, false);
				isAnimate = true;
				
				// mFirstPosition = position;
				smoothScrollRightBy(rightScroll, 1);
			} else if (leftEdge >= mFixedPaddingLeft) {
				Log.i(TAG, "add branch 3-2");
				View attendView = makeAndAddView(position, Math.abs(position - mSelectedPosition), newLeftEdge, true, 0);
				// mFirstPosition = position;
				rightScroll = leftChild.getWidth() + mSpacing - (leftEdge - mFixedPaddingLeft);

				setSelectedPositionInt(position);
				setNextSelectedPositionInt(position);

				if (position == mSelectedPosition) {
					mNeedReset = true;
				}
				
				smoothScrollRightBy(rightScroll, 1);
				mAttendRunnabe.startAttend(attendView, AttendRunnabe.ATTEND, position, false);
				isAnimate = true;
			} else {
				Log.i(TAG, "add branch 3-3");
				mFirstPosition++;
				int selectedPosition = mSelectedPosition + 1;
				setSelectedPositionInt(selectedPosition);
				setNextSelectedPositionInt(selectedPosition);
			}

		} else if (position < getFirstVisiblePosition()) {
			Log.i(TAG, "add branch 4");
			mFirstPosition++;

			int selectedPosition = mSelectedPosition + 1;
			setSelectedPositionInt(selectedPosition);
			setNextSelectedPositionInt(selectedPosition);
		}

		// if (position <= mSelectedPosition) {
		// int selectedPosition = mSelectedPosition + 1;
		// setSelectedPositionInt(selectedPosition);
		// setNextSelectedPositionInt(selectedPosition);
		// }

		mItemCount++;

		return isAnimate;
	}

	public boolean remove(int position) {
		Log.d(TAG, "remove: position = " + position);
		if (isAdding() || isRemoving()) {
			throw new IllegalArgumentException("DeviceGallery: add, you can't remove item when you are adding or removeing item");
		}

		if (mLastScrollState == OnScrollListener.SCROLL_STATE_FLING) {
			throw new IllegalArgumentException("DeviceGallery: remove, you can't remove item when you are scrolling");
		}

		if (getChildCount() <= 0 || mItemCount <= 0) {
			throw new IllegalArgumentException("DeviceGallery: remove: item count is 0, can't remove");
		}

		boolean isAnimate = false;
		boolean adjust = false;
		int lastSelectedPosition = mSelectedPosition;

		if (position >= getFirstVisiblePosition() && position <= getLastVisiblePosition()) {
			Log.i(TAG, "remove branch 1");
			View child = getChildAt(0);
			int actualWidth = getWidth() - mFixedPaddingLeft - mFixedPaddingRight;
			int childWidth = mItemCount * child.getWidth() + mSpacing * (mItemCount - 1);
			int newChildWidth = childWidth - (child.getWidth() + mSpacing);

			if (newChildWidth >= actualWidth) {
				Log.i(TAG, "remove branch 1-1");
				View rightChild = getChildAt(getChildCount() - 1);
				int rightEdge = rightChild.getRight();
				int rightMaxScroll = (mItemCount - 1 - getLastVisiblePosition()) * (rightChild.getWidth() + mSpacing);
				if (rightEdge > getWidth() - mFixedPaddingRight) {
					rightMaxScroll += (rightEdge - (getWidth() - mFixedPaddingRight));
				}

				View leftChild = getChildAt(0);
				int leftEdge = leftChild.getLeft();
				int itemWidth = rightChild.getWidth() + mSpacing;
				if (rightMaxScroll >= itemWidth) {
					Log.i(TAG, "remove branch 1-1-1");
					if (position == getFirstVisiblePosition()) {
						Log.i(TAG, "remove branch 1-1-1-1");
						if (leftEdge < mFixedPaddingLeft) {
							Log.i(TAG, "remove branch 1-1-1-1-1");
							if (getFirstVisiblePosition() > 0) {
								Log.i(TAG, "remove branch 1-1-1-1-1-1");
								leftEdge -= (leftChild.getWidth() + mSpacing);
								makeAndAddView(getFirstVisiblePosition() - 1, Math.abs(getFirstVisiblePosition() - 1 - mSelectedPosition), leftEdge, true, 0);
								mFirstPosition--;
								smoothScrollLeftBy(itemWidth, position - 1);
							} else {
								Log.i(TAG, "remove branch 1-1-1-1-1-2");
								int newLeftEdge = leftEdge + leftChild.getWidth() + mSpacing;
								if (newLeftEdge > mFixedPaddingLeft) {
									int rightScroll = newLeftEdge - mFixedPaddingLeft;
									smoothScrollRightBy(-rightScroll, position + 1);
								}
							}
						} else {
							Log.i(TAG, "remove branch 1-1-1-1-2");
							smoothScrollRightBy(-itemWidth, position + 1);
							if (position == mSelectedPosition) {
								int selectedPosition = mSelectedPosition + 1;
								setSelectedPositionInt(selectedPosition);
								setNextSelectedPositionInt(selectedPosition);
							}
						}
						
						adjust = true;
					} else if (position == getLastVisiblePosition() && position + 1 < mItemCount) {
						Log.i(TAG, "remove branch 1-1-1-2");
						rightEdge += mSpacing;
						makeAndAddView(position + 1, Math.abs(position + 1 - mSelectedPosition), rightEdge, true);
						smoothScrollRightBy(-itemWidth, position + 1);
					} else {
						Log.i(TAG, "remove branch 1-1-1-3");
						smoothScrollRightBy(-itemWidth, position + 1);
						if (position <= mSelectedPosition) {
							Log.i(TAG, "remove branch 1-1-1-3-1");
							if (position == mSelectedPosition) {
								int selectedPosition = mSelectedPosition + 1;
								setSelectedPositionInt(selectedPosition);
								setNextSelectedPositionInt(selectedPosition);
							}

							adjust = true;
						}
					}

					View attendView = removeView(position);
					mAttendRunnabe.startAttend(attendView, AttendRunnabe.OUT, position, adjust);
					isAnimate = true;
				} else {
					Log.i(TAG, "remove branch 1-1-2");
					int rightScroll = rightMaxScroll;

					if (getChildCount() > position - getFirstVisiblePosition() && position - getFirstVisiblePosition() >= 0) {
						Log.i(TAG, "remove branch 1-1-2-1");
						smoothScrollRightBy(-rightScroll, position + 1);
					}

					if (position - 1 >= 0) {
						Log.i(TAG, "remove branch 1-1-2-2");
						int leftScroll = itemWidth - rightScroll;
						if (position == getFirstVisiblePosition()) {
							leftEdge = getChildAt(0).getLeft() - mSpacing;
							makeAndAddView(position - 1, Math.abs(position - 1 - mSelectedPosition), leftEdge, true);
						}
						smoothScrollLeftBy(leftScroll, position - 1);
					}

					if (position + 1 < mItemCount && position <= mSelectedPosition) {
						Log.i(TAG, "remove branch 1-1-2-3");
						if (position == mSelectedPosition) {
							int selectedPosition = mSelectedPosition + 1;
							setSelectedPositionInt(selectedPosition);
							setNextSelectedPositionInt(selectedPosition);
						}

						adjust = true;
					} else if (position + 1 == mItemCount && position == mSelectedPosition) {
						Log.i(TAG, "remove branch 1-1-2-4");
						if (mItemCount > 1) {
							int selectedPosition = mSelectedPosition - 1;
							setSelectedPositionInt(selectedPosition);
							setNextSelectedPositionInt(selectedPosition);

							adjust = false;
						}
					}

					View attendView = removeView(position);
					mAttendRunnabe.startAttend(attendView, AttendRunnabe.OUT, position, adjust);
					isAnimate = true;
				}
			} else {  
				Log.i(TAG, "remove branch 1-2");
				int intevel = (actualWidth - newChildWidth) / 2;
				int oldRightEdge = getChildAt(getChildCount() - 1).getRight();
				int newRightEdge = intevel + newChildWidth + mFixedPaddingLeft;
				int rightScroll = oldRightEdge - newRightEdge;

				int oldLeftEdge = getChildAt(0).getLeft() - getFirstVisiblePosition() * (getChildAt(0).getWidth() + mSpacing);
				int newLeftEdge = intevel + mFixedPaddingLeft;
				int leftScroll = newLeftEdge - oldLeftEdge;

				/*add by shenzhi.hsz begin*/
				if (position <= mSelectedPosition) {
					Log.i(TAG, "remove branch 1-2-1");
					adjust = true;
					if (position == mSelectedPosition) {
						Log.i(TAG, "remove branch 1-2-1-1");
						if (position + 1 < mItemCount) {
							Log.i(TAG, "remove branch 1-2-1-1-1");
							int selectedPosition = mSelectedPosition + 1;
							setSelectedPositionInt(selectedPosition);
							setNextSelectedPositionInt(selectedPosition);
						} else {
							Log.i(TAG, "remove branch 1-2-1-1-2");
							int selectedPosition = mSelectedPosition - 1;
							setSelectedPositionInt(selectedPosition);
							setNextSelectedPositionInt(selectedPosition);
							adjust = false;
						}
					}
				}
				/*add by shenzhi.hsz end*/
				
				View attendView = removeView(position);
				mAttendRunnabe.startAttend(attendView, AttendRunnabe.OUT, position, adjust);
				isAnimate = true;
				if (getChildCount() > position - getFirstVisiblePosition() && position - getFirstVisiblePosition() >= 0) {
					smoothScrollRightBy(-rightScroll, position + 1);
				}

				if (position - 1 >= 0) {
					smoothScrollLeftBy(leftScroll, position - 1);
				}
				
			}

			if (position == lastSelectedPosition) {
				mNeedReset = true;
			}

		} else if (position < getFirstVisiblePosition()) {
			Log.i(TAG, "remove branch 2");
			mFirstPosition--;

			int selectedPosition = mSelectedPosition - 1;
			setSelectedPositionInt(selectedPosition);
			setNextSelectedPositionInt(selectedPosition);
		}

		mItemCount--;

		return isAnimate;
	}

	public boolean isAdding() {
		return mAttendRunnabe.isAdding();
	}

	public boolean isRemoving() {
		return mAttendRunnabe.isRemoving();
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
		Log.d(TAG, "onLayout");
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

	public int getLeftScrollDistance() {
		if (mFlingRunnable != null) {
			return mFlingRunnable.getLeftScrollDistance();
		}
		return 0;
	}

	int trackMotionScrollLeft(int deltaX, int end) {
		if (getChildCount() == 0) {
			return 0;
		}

		int count = 0;
		boolean toLeft = deltaX < 0;

		int limitedDeltaX = deltaX;// getLimitedMotionScrollAmount(toLeft,
									// deltaX);
		if (limitedDeltaX != deltaX) {
			// The above call returned a limited amount, so stop any
			// scrolls/flings
			mFlingRunnable.endFling(false);
			onFinishedMovement();
		}

		offsetLeftChildrenLeftAndRight(limitedDeltaX, end);

		detachOffScreenChildren(toLeft);

		if (toLeft) {
			// If moved left, there will be empty space on the right
			count = fillToGalleryRight();
		} else {
			// Similarly, empty space on the left
			count = fillToGalleryLeft();
		}

		// Clear unused views
		mRecycler.clear();

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

		return count;
	}
 
	int trackMotionScrollRight(int deltaX, int start) {
		if (getChildCount() == 0) {
			return 0;
		}

		int count = 0;
		boolean toLeft = deltaX < 0;

		int limitedDeltaX = deltaX;// getLimitedMotionScrollAmount(toLeft,
									// deltaX);
		if (limitedDeltaX != deltaX) {
			// The above call returned a limited amount, so stop any
			// scrolls/flings
			mFlingRunnable.endFling(false);
			onFinishedMovement();
		}

		offsetRightChildrenLeftAndRight(limitedDeltaX, start);

		detachOffScreenChildren(toLeft);

		if (toLeft) {
			// If moved left, there will be empty space on the right
			count = fillToGalleryRight();
		} else {
			// Similarly, empty space on the left
			count = fillToGalleryLeft();
		}

		// Clear unused views
		mRecycler.clear();

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

		return count;
	}

	void trackMotionScroll(int deltaX) {
		if (getChildCount() == 0) {
			return;
		}

		boolean toLeft = deltaX < 0;

		int limitedDeltaX = deltaX;// getLimitedMotionScrollAmount(toLeft,
									// deltaX);
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
		mRecycler.clear();

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

	void offsetLeftChildrenLeftAndRight(int offset, int end) {
		for (int i = 0; i <= end - getFirstVisiblePosition(); i++) {
			getChildAt(i).offsetLeftAndRight(offset);
		}
	}

	void offsetRightChildrenLeftAndRight(int offset, int start) {
		for (int i = getChildCount() - 1; i >= start - getFirstVisiblePosition(); i--) {
			getChildAt(i).offsetLeftAndRight(offset);
		}
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
//		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingRight();
		return getLeft() + getWidth()/2;
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
		Log.d(TAG, "layout");
		mIsRtl = false;// isLayoutRtl();

		// int childrenLeft = mSpinnerPadding.left;
		// int childrenWidth = getRight() - getLeft() - mSpinnerPadding.left -
		// mSpinnerPadding.right;

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

		int leftEdge = mFixedPaddingLeft;
		boolean isEmpty = false;
		if (getChildCount() <= 0) {
			isEmpty = true;
		} else {
			if (mSelectedPosition >= getFirstVisiblePosition() && mSelectedPosition <= getLastVisiblePosition()) {
				leftEdge = getChildAt(mSelectedPosition - mFirstPosition).getLeft();
			}
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

		mFirstPosition = mSelectedPosition;
		addCenter(leftEdge, isEmpty);

		fillToGalleryRight();
		fillToGalleryLeft();

		// Flush any cached views that did not get reused above
		mRecycler.clear();

		invalidate();
		checkSelectionChanged();

		mDataChanged = false;
		mNeedSync = false;
		setNextSelectedPositionInt(mSelectedPosition);

		updateSelectedItemMetadata();
	}

	private View addCenter(int leftEdge, boolean isEmpty) {
		View sel = makeAndAddView(mSelectedPosition, 0, leftEdge, true);
		if (isEmpty) {
			mFirstPosition = mSelectedPosition;
			int width = sel.getWidth();
			int actualWidth = width * mItemCount + mSpacing * (mItemCount - 1);
			int effWidth = getWidth() - mFixedPaddingLeft - mFixedPaddingRight;
			if (actualWidth < effWidth) {
				// Put the selected child in the center

				int selectedOffset = (effWidth - actualWidth) / 2;
				sel.offsetLeftAndRight(selectedOffset);
			}

			if (sel != null) {
				positionSelector(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom());
			}
		}

		return sel;
	}

	private int fillToGalleryLeft() {
		if (mIsRtl) {
			return fillToGalleryLeftRtl();
		} else {
			return fillToGalleryLeftLtr();
		}
	}

	private int fillToGalleryLeftRtl() {
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
		return 0;
	}

	private int fillToGalleryLeftLtr() {
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
		int count = 0;

		while (curRightEdge > galleryLeft && curPosition >= 0) {
			count++;
			prevIterationView = makeAndAddView(curPosition, curPosition - mSelectedPosition, curRightEdge, false);

			// Remember some state
			mFirstPosition = curPosition;

			// Set state for next iteration
			curRightEdge = prevIterationView.getLeft() - itemSpacing;
			curPosition--;
		}

		return count;
	}

	private int fillToGalleryRight() {
		if (mIsRtl) {
			return fillToGalleryRightRtl();
		} else {
			return fillToGalleryRightLtr();
		}
	}

	private int fillToGalleryRightRtl() {
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

		return 0;
	}

	private int fillToGalleryRightLtr() {
		int itemSpacing = mSpacing;
		int galleryRight = getRight() - getLeft() - getPaddingRight();
		int numChildren = getChildCount();
		int numItems = mItemCount;
		// if(mAttendRunnabe.isRemoving()){
		// numChildren--;
		// }
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

		if (mAttendRunnabe.isRemoving()) {
			curPosition--;
		}

		int count = 0;
		while (curLeftEdge < galleryRight && curPosition < numItems) {
			count++;
			prevIterationView = makeAndAddView(curPosition, curPosition - mSelectedPosition, curLeftEdge, true);

			// Set state for next iteration
			curLeftEdge = prevIterationView.getRight() + itemSpacing;
			curPosition++;
		}

		return count;
	}

	private View removeView(int position) {
		View child = getChildAt(position - getFirstVisiblePosition());
		// removeViewInLayout(child);
		return child;
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

		return makeAndAddView(position, offset, x, fromLeft, fromLeft != mIsRtl ? -1 : 0);
	}

	private View makeAndAddView(int position, int offset, int x, boolean fromLeft, int index) {

		View child = null;
		if (!mDataChanged) {
			child = mRecycler.get(position);
			if (child != null) {
				// Can reuse an existing view
				int childLeft = child.getLeft();

				// Remember left and right edges of where views have been placed
				mRightMost = Math.max(mRightMost, childLeft + child.getMeasuredWidth());
				mLeftMost = Math.min(mLeftMost, childLeft);

				// Position the view
//				setUpChild(child, offset, x, fromLeft, index);
//
//				return child;
			}
		}

		// Nothing found in the recycler -- ask the adapter for a view
//		child = mAdapter.getView(position, null, this);
		child = mAdapter.getView(position, child, this);

		// Position the view
		setUpChild(child, offset, x, fromLeft, index);

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
	private void setUpChild(View child, int offset, int x, boolean fromLeft, int index) {

		// Respect layout params that are already in the view. Otherwise
		// make some up...
		Gallery.LayoutParams lp = (Gallery.LayoutParams) child.getLayoutParams();
		if (lp == null) {
			lp = (Gallery.LayoutParams) generateDefaultLayoutParams();
		}

		addViewInLayout(child, index, lp);

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
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

		super.onFling(e1, e2, velocityX, velocityY);
		// Fling the gallery!
		mFlingRunnable.startUsingVelocity((int) -velocityX);

		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

		if (DEBUG)
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
		mFlingRunnable.startUsingDistance(distance);
	}

	public void smoothScrollLeftBy(int distance, int end) {
		mFlingRunnableLeft.startUsingDistance(distance, end);
	}

	public void smoothScrollRightBy(int distance, int start) {
		mFlingRunnableRight.startUsingDistance(distance, start);
	}

	public void setFlingScrollMaxStep(float maxStep) {
		mFlingRunnable.setMaxStep(maxStep);
		mFlingRunnableLeft.setMaxStep(maxStep);
		mFlingRunnableRight.setMaxStep(maxStep);
	}

	public void setFlipScrollFrameCount(int frameCount) {
		mFlingRunnable.setFrameCount(frameCount);
		mFlingRunnableLeft.setFrameCount(frameCount);
		mFlingRunnableRight.setFrameCount(frameCount);
	}
	
	protected boolean isFling(){
		return !mFlingRunnable.isFinished();
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

	private class AttendRunnabe implements Runnable {
		public static final int ATTEND = 1;
		public static final int OUT = 2;

		private int MIN_SCALE = 400;
		private int MAX_SCALE = 1000;
		private int OFFSET = 200;
		private int mode = 0;
		private View mView;

		protected Scroller mScaleScroller;
		protected Scroller mOffsetScroller;

		private int mCurrX = 0;

		private int position;

		private boolean mAdjust = false;

		public AttendRunnabe() {
			Log.e(TAG, "AttendRunnabe: AttendRunnabe");
			mScaleScroller = new Scroller(getContext(), new DecelerateInterpolator());
			mOffsetScroller = new Scroller(getContext(), new DecelerateInterpolator());
		}

		public boolean isRemoving() {
			return mode == OUT && !mOffsetScroller.isFinished();
		}

		public boolean isAdding() {
			return mode == ATTEND && !mOffsetScroller.isFinished();
		}

		public void startAttend(View v, int m, int p, boolean adjust) {
			Log.d(TAG, "AttendRunnabe: startAttend: v = " + v + ", m = " + m + ", position = " + p);
			mView = v;
			position = p;
			mAdjust = adjust;

			if (m == ATTEND) {
				mView.offsetTopAndBottom(-OFFSET);
				float scale = (float) MIN_SCALE / 1000;
				mView.setScaleX(scale);
				mView.setScaleY(scale);
				mView.setAlpha(scale);
				mScaleScroller.startScroll(MIN_SCALE, 0, MAX_SCALE - MIN_SCALE, 0, mAnimationDuration);
				mOffsetScroller.startScroll(0, 0, OFFSET, 0, mAnimationDuration);
				mCurrX = 0;

				if (mOnAddListener != null) {
					mOnAddListener.onAddStart(position, mView, DeviceGallery.this);
				}
			} else if (m == OUT) {
				float scale = (float) MAX_SCALE / 1000;
				mView.setScaleX(scale);
				mView.setScaleY(scale);
				mView.setAlpha(scale);
				mScaleScroller.startScroll(MAX_SCALE, 0, MIN_SCALE - MAX_SCALE, 0, mAnimationDuration);
				mOffsetScroller.startScroll(0, 0, OFFSET, 0, mAnimationDuration);
				mCurrX = 0;

				if (mOnRemoveListener != null) {
					mOnRemoveListener.onRemoveStart(position, mView, DeviceGallery.this);
				}
			}

			mode = m;

			post(this);
		}

		private void end() {
			Log.d(TAG, "AttendRunnabe: end");
			removeCallbacks(this);

			mOffsetScroller.forceFinished(true);
			mScaleScroller.forceFinished(true);

			if (mode == OUT) {
				if (mAdjust) {
					int selectedPosition = mSelectedPosition - 1;
					setSelectedPositionInt(selectedPosition);
					setNextSelectedPositionInt(selectedPosition);
				}

				mRecycler.put(position, mView);
				removeViewInLayout(mView);
				invalidate();
				
				if (mOnRemoveListener != null) {
					mOnRemoveListener.onRemoveFinish(position, mView, DeviceGallery.this);
				}
			} else {
				if (mOnAddListener != null) {
					mOnAddListener.onAddFinish(position, mView, DeviceGallery.this);
				}
			}

			mView = null;
			mode = 0;
			mAdjust = false;
			mNeedReset = false;
		}

		@Override
		public void run() {
			boolean more = mOffsetScroller.computeScrollOffset();
			mScaleScroller.computeScrollOffset();
			float scale = (float) mScaleScroller.getCurrX() / 1000;
			mView.setScaleX(scale);
			mView.setScaleY(scale);
			mView.setAlpha(scale);
			int coef = 0;
			if (mode == ATTEND) {
				coef = -1;
			} else if (mode == OUT) {
				coef = 1;
			}
			if (more) {
				int x = mOffsetScroller.getCurrX();
				int diff = mCurrX - x;
				mCurrX = x;
				mView.offsetTopAndBottom(diff * coef);
				post(this);
			} else {
				end();
			}
		}
	}

	private class FlingRunnableRight extends BaseRunnable {
		int start = 0;

		public void startUsingDistance(int distance, int s) {
			// super.startUsingDistance(distance);
			start = s;

			mLastFlingX = 0;
			mScroller.startScroll(0, 0, -distance, 0, mAnimationDuration);
			post(this);
		}

		@Override
		public void run() {

			if (mItemCount == 0) {
				endFling(true);
				return;
			}

			mShouldStopFling = false;

			// final Scroller scroller = mScroller;
			boolean more = mScroller.computeScrollOffset();
			final int x = mScroller.getCurrX();

			// Flip sign to convert finger direction to list items direction
			// (e.g. finger moving down means list is moving towards the top)
			int delta = mLastFlingX - x;

			// Pretend that each frame of a fling scroll is a touch scroll
			if (delta > 0) {
				// Moving towards the left. Use leftmost view as
				// mDownTouchPosition
				// mDownTouchPosition = mIsRtl ? (mFirstPosition +
				// getChildCount() - 1) : mFirstPosition;

				// Don't fling more than 1 screen
				delta = Math.min(getWidth() - getPaddingLeft() - getPaddingRight() - 1, delta);
			} else {
				// Moving towards the right. Use rightmost view as
				// mDownTouchPosition
				int offsetToLast = getChildCount() - 1;
				// mDownTouchPosition = mIsRtl ? mFirstPosition :
				// (mFirstPosition + getChildCount() - 1);

				// Don't fling more than 1 screen
				delta = Math.max(-(getWidth() - getPaddingRight() - getPaddingLeft() - 1), delta);
			}

			trackMotionScrollRight(delta, start);

			if (more && !mShouldStopFling) {
				mLastFlingX = x;
				post(this);
			} else {
				endFling(true);
			}
		}

	}

	private class FlingRunnableLeft extends BaseRunnable {
		int end = 0;

		public void startUsingDistance(int distance, int e) {
			// super.startUsingDistance(distance);
			end = e;

			mLastFlingX = 0;
			mScroller.startScroll(0, 0, -distance, 0, mAnimationDuration);
			post(this);
		}

		@Override
		public void run() {

			if (mItemCount == 0) {
				endFling(true);
				return;
			}

			mShouldStopFling = false;

			// final Scroller scroller = mScroller;
			boolean more = mScroller.computeScrollOffset();
			final int x = mScroller.getCurrX();

			// Flip sign to convert finger direction to list items direction
			// (e.g. finger moving down means list is moving towards the top)
			int delta = mLastFlingX - x;

			// Pretend that each frame of a fling scroll is a touch scroll
			if (delta > 0) {
				// Moving towards the left. Use leftmost view as
				// mDownTouchPosition
				// mDownTouchPosition = mIsRtl ? (mFirstPosition +
				// getChildCount() - 1) : mFirstPosition;

				// Don't fling more than 1 screen
				delta = Math.min(getWidth() - getPaddingLeft() - getPaddingRight() - 1, delta);
			} else {
				// Moving towards the right. Use rightmost view as
				// mDownTouchPosition
				int offsetToLast = getChildCount() - 1;
				// mDownTouchPosition = mIsRtl ? mFirstPosition :
				// (mFirstPosition + getChildCount() - 1);

				// Don't fling more than 1 screen
				delta = Math.max(-(getWidth() - getPaddingRight() - getPaddingLeft() - 1), delta);
			}

			trackMotionScrollLeft(delta, end);

			if (more && !mShouldStopFling) {
				mLastFlingX = x;
				post(this);
			} else {
				endFling(true);
			}
		}

	}

	/**
	 * Responsible for fling behavior. Use {@link #startUsingVelocity(int)} to
	 * initiate a fling. Each frame of the fling is handled in {@link #run()}. A
	 * FlingRunnable will keep re-posting itself until the fling is done.
	 */
	public class FlingRunnable extends BaseRunnable {
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

		@Override
		public void startUsingDistance(int distance) {
			super.startUsingDistance(distance);
			reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
		}

		@Override
		protected void endFling(boolean scrollIntoSlots) {
			super.endFling(scrollIntoSlots);
			reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
		}
	}

	public abstract class BaseRunnable implements Runnable {
		/**
		 * Tracks the decay of a fling scroll
		 */
		protected Scroller mScroller;

		/**
		 * X value reported by mScroller on the previous fling
		 */
		protected int mLastFlingX;

		protected ListLoopScroller mListLoopScroller;
		protected int mFrameCount;
		protected float mDefatultScrollStep = 5.0f;

		public BaseRunnable() {
			mScroller = new Scroller(getContext(), new DecelerateInterpolator());
			mListLoopScroller = new ListLoopScroller();
		}

		private void startCommon() {
			// Remove any pending flings
			removeCallbacks(this);
		}

		public void setMaxStep(float maxStep) {
			mListLoopScroller.setMaxStep(maxStep);
		}

		public void setFrameCount(int count) {
			mFrameCount = count;
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

			// mScroller.startScroll(0, 0, -distance, 0, mAnimationDuration);
			// post(this);
		}

		public void stop(boolean scrollIntoSlots) {
			removeCallbacks(this);
			endFling(scrollIntoSlots);
		}

		protected void endFling(boolean scrollIntoSlots) {
			/*
			 * Force the scroller's status to finished (without setting its
			 * position to the end)
			 */
			mScroller.forceFinished(true);
			mListLoopScroller.finish();

			// if (scrollIntoSlots)
			// scrollIntoSlots();
		}

		public int getLeftScrollDistance() {
			return mListLoopScroller.getFinal() - mListLoopScroller.getCurr();
		}
	}

}
