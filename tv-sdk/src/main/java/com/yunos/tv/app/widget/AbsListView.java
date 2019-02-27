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
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Debug;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EdgeEffect;

import com.yunos.tv.app.widget.focus.listener.OnScrollListener;

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

public abstract class AbsListView extends AbsBaseListView {

	private static final String TAG = "AbsListView";
	private static final boolean DEBUG = false;

	/**
	 * Helper for detecting touch gestures.
	 */
	// private GestureDetector mGestureDetector;

	/**
	 * Executes the delta scrolls from a fling or scroll movement.
	 */
	FlingRunnable mFlingRunnable = new FlingRunnable();

	int mSelectedLeft;

	boolean unhandleFullVisible;

	protected boolean mItemsCanFocus = false;

	/**
	 * Handles scrolling between positions within the list.
	 */
	private PositionScroller mPositionScroller;

	protected boolean mStackFromBottom = false;

	/**
	 * Helper object that renders and controls the fast scroll thumb.
	 */
	// private FastScroller mFastScroller;

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
	 * If true, do not callback to item selected listener.
	 */
	private boolean mSuppressSelectionChanged;

	/**
	 * If true, we have received the "invoke" (center or enter buttons) key
	 * down. This is checked before we action on the "invoke" key up, and is
	 * subsequently cleared.
	 */
	protected boolean mReceivedInvokeKeyDown;

	/**
	 * If true, mFirstPosition is the position of the rightmost child, and the
	 * children are ordered right to left.
	 */
	// protected boolean mStackFromBottom = false;

	/**
	 * The X value associated with the the down motion event
	 */
	int mMotionX;

	/**
	 * The Y value associated with the the down motion event
	 */
	int mMotionY;

	/**
	 * How far the finger moved before we started scrolling
	 */
	int mMotionCorrection;

	/**
	 * Y value from on the previous motion event (if any)
	 */
	int mLastY;

	/**
	 * ID of the active pointer. This is used to retain consistency during
	 * drags/flings if multiple pointers are used.
	 */
	private int mActivePointerId = INVALID_POINTER;

	/**
	 * Used for determining when to cancel out of overscroll.
	 */
	private int mDirection = 0;

	/**
	 * The last CheckForTap runnable we posted, if any
	 */
	private Runnable mPendingCheckForTap;

	/**
	 * The offset to the top of the mMotionPosition view when the down motion
	 * event was received
	 */
	int mMotionViewOriginalTop;

	/**
	 * Delayed action for touch mode.
	 */
	private Runnable mTouchModeReset;

	private int mMaximumVelocity;
	private int mMinimumVelocity;

	private float mVelocityScale = 1.0f;

	/**
	 * Maximum distance to overscroll by during edge effects
	 */
	int mOverscrollDistance;

	/**
	 * Tracks the state of the top edge glow.
	 */
	private EdgeEffect mEdgeGlowTop;

	/**
	 * Tracks the state of the bottom edge glow.
	 */
	private EdgeEffect mEdgeGlowBottom;

	private static final boolean PROFILE_SCROLLING = false;

	private boolean mScrollProfilingStarted = false;

	private int mTouchSlop;

	private boolean mFlingProfilingStarted = false;

	/**
	 * Maximum distance to overfling during edge effects
	 */
	int mOverflingDistance;

	/**
	 * Used for smooth scrolling at a consistent rate
	 */
	static final Interpolator sLinearInterpolator = new LinearInterpolator();
	protected int mUpPreLoadedCount = 0;
	protected int mDownPreLoadedCount = 0;

	public AbsListView(Context context) {
		super(context);
        needMeasureSelectedView = false;
	}

	public AbsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
        needMeasureSelectedView = false;
	}

	public AbsListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		needMeasureSelectedView = false;
	}
	
	public int getUpPreLoadedCount() {
		return mUpPreLoadedCount;
	}

	public int getDownPreLoadedCount() {
		return mDownPreLoadedCount;
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		if (!isLayoutRequested()) {
			return;
		}
		/*
		 * Remember that we are in layout to prevent more layout request from
		 * being generated.
		 */
		mInLayout = true;
		layoutChildren();
		mInLayout = false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		View v;

		if (mPositionScroller != null) {
			mPositionScroller.stop();
		}

		if (!mIsAttached) {
			// Something isn't right.
			// Since we rely on being attached to get data set change
			// notifications,
			// don't risk doing anything where we might try to resync and find
			// things
			// in a bogus state.
			return false;
		}

		// if (mFastScroller != null) {
		// boolean intercepted = mFastScroller.onInterceptTouchEvent(ev);
		// if (intercepted) {
		// return true;
		// }
		// }

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			int touchMode = mTouchMode;
			if (touchMode == TOUCH_MODE_OVERFLING || touchMode == TOUCH_MODE_OVERSCROLL) {
				mMotionCorrection = 0;
				return true;
			}

			final int x = (int) ev.getX();
			final int y = (int) ev.getY();
			mActivePointerId = ev.getPointerId(0);

			int motionPosition = findMotionRow(y);
			if (touchMode != TOUCH_MODE_FLING && motionPosition >= 0) {
				// User clicked on an actual view (and was not stopping a
				// fling).
				// Remember where the motion event started
				v = getChildAt(motionPosition - mFirstPosition);
				mMotionViewOriginalTop = v.getTop();
				mMotionX = x;
				mMotionY = y;
				mMotionPosition = motionPosition;
				mTouchMode = TOUCH_MODE_DOWN;
				clearScrollingCache();
			}
			mLastY = Integer.MIN_VALUE;
			initOrResetVelocityTracker();
			mVelocityTracker.addMovement(ev);
			if (touchMode == TOUCH_MODE_FLING) {
				return true;
			}
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			switch (mTouchMode) {
			case TOUCH_MODE_DOWN:
				int pointerIndex = ev.findPointerIndex(mActivePointerId);
				if (pointerIndex == -1) {
					pointerIndex = 0;
					mActivePointerId = ev.getPointerId(pointerIndex);
				}
				final int y = (int) ev.getY(pointerIndex);
				initVelocityTrackerIfNotExists();
				mVelocityTracker.addMovement(ev);
				if (startScrollIfNeeded(y)) {
					return true;
				}
				break;
			}
			break;
		}

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP: {
			mTouchMode = TOUCH_MODE_REST;
			mActivePointerId = INVALID_POINTER;
			recycleVelocityTracker();
			reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
			break;
		}

		case MotionEvent.ACTION_POINTER_UP: {
			onSecondaryPointerUp(ev);
			break;
		}
		}

		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!isEnabled()) {
			// A disabled view that is clickable still consumes the touch
			// events, it just doesn't respond to them.
			return isClickable() || isLongClickable();
		}

		if (mPositionScroller != null) {
			mPositionScroller.stop();
		}

		if (!mIsAttached) {
			// Something isn't right.
			// Since we rely on being attached to get data set change
			// notifications,
			// don't risk doing anything where we might try to resync and find
			// things
			// in a bogus state.
			return false;
		}

		// if (mFastScroller != null) {
		// boolean intercepted = mFastScroller.onTouchEvent(ev);
		// if (intercepted) {
		// return true;
		// }
		// }

		final int action = ev.getAction();

		View v;

		initVelocityTrackerIfNotExists();
		mVelocityTracker.addMovement(ev);

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			switch (mTouchMode) {
			case TOUCH_MODE_OVERFLING: {
				mFlingRunnable.endFling();
				if (mPositionScroller != null) {
					mPositionScroller.stop();
				}
				mTouchMode = TOUCH_MODE_OVERSCROLL;
				mMotionX = (int) ev.getX();
				mMotionY = mLastY = (int) ev.getY();
				mMotionCorrection = 0;
				mActivePointerId = ev.getPointerId(0);
				mDirection = 0;
				break;
			}

			default: {
				mActivePointerId = ev.getPointerId(0);
				final int x = (int) ev.getX();
				final int y = (int) ev.getY();
				int motionPosition = pointToPosition(x, y);
				if (!mDataChanged) {
					if ((mTouchMode != TOUCH_MODE_FLING) && (motionPosition >= 0) && (getAdapter().isEnabled(motionPosition))) {
						// User clicked on an actual view (and was not stopping
						// a fling).
						// It might be a click or a scroll. Assume it is a click
						// until
						// proven otherwise
						mTouchMode = TOUCH_MODE_DOWN;
						// FIXME Debounce
						if (mPendingCheckForTap == null) {
							mPendingCheckForTap = new CheckForTap();
						}
						postDelayed(mPendingCheckForTap, ViewConfiguration.getTapTimeout());
					} else {
						if (mTouchMode == TOUCH_MODE_FLING) {
							// Stopped a fling. It is a scroll.
							createScrollingCache();
							mTouchMode = TOUCH_MODE_SCROLL;
							mMotionCorrection = 0;
							motionPosition = findMotionRow(y);
							mFlingRunnable.flywheelTouch();
						}
					}
				}

				if (motionPosition >= 0) {
					// Remember where the motion event started
					v = getChildAt(motionPosition - mFirstPosition);
					mMotionViewOriginalTop = v.getTop();
				}
				mMotionX = x;
				mMotionY = y;
				mMotionPosition = motionPosition;
				mLastY = Integer.MIN_VALUE;
				break;
			}
			}

			if (performButtonActionOnTouchDown(ev)) {
				if (mTouchMode == TOUCH_MODE_DOWN) {
					removeCallbacks(mPendingCheckForTap);
				}
			}
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			int pointerIndex = ev.findPointerIndex(mActivePointerId);
			if (pointerIndex == -1) {
				pointerIndex = 0;
				mActivePointerId = ev.getPointerId(pointerIndex);
			}
			final int y = (int) ev.getY(pointerIndex);

			if (mDataChanged) {
				// Re-sync everything if data has been changed
				// since the scroll operation can query the adapter.
				layoutChildren();
			}

			switch (mTouchMode) {
			case TOUCH_MODE_DOWN:
			case TOUCH_MODE_TAP:
			case TOUCH_MODE_DONE_WAITING:
				// Check if we have moved far enough that it looks more like a
				// scroll than a tap
				startScrollIfNeeded(y);
				break;
			case TOUCH_MODE_SCROLL:
			case TOUCH_MODE_OVERSCROLL:
				scrollIfNeeded(y);
				break;
			}
			break;
		}

		case MotionEvent.ACTION_UP: {
			switch (mTouchMode) {
			case TOUCH_MODE_DOWN:
			case TOUCH_MODE_TAP:
			case TOUCH_MODE_DONE_WAITING:
				final int motionPosition = mMotionPosition;
				final View child = getChildAt(motionPosition - mFirstPosition);

				final float x = ev.getX();
				final boolean inList = x > mListPadding.left && x < getWidth() - mListPadding.right;

				if (child != null && !child.hasFocusable() && inList) {
					if (mTouchMode != TOUCH_MODE_DOWN) {
						child.setPressed(false);
					}

					if (mPerformClick == null) {
						mPerformClick = new PerformClick();
					}

					final AbsListView.PerformClick performClick = mPerformClick;
					performClick.mClickMotionPosition = motionPosition;
					performClick.rememberWindowAttachCount();

					mResurrectToPosition = motionPosition;

					if (mTouchMode == TOUCH_MODE_DOWN || mTouchMode == TOUCH_MODE_TAP) {
						final Handler handler = getHandler();
						if (handler != null) {
							handler.removeCallbacks(mTouchMode == TOUCH_MODE_DOWN ? mPendingCheckForTap : mPendingCheckForLongPress);
						}
						mLayoutMode = LAYOUT_NORMAL;
						if (!mDataChanged && mAdapter.isEnabled(motionPosition)) {
							mTouchMode = TOUCH_MODE_TAP;
							setSelectedPositionInt(mMotionPosition);
							layoutChildren();
							child.setPressed(true);
							positionSelector(mMotionPosition, child);
							setPressed(true);
							if (mSelector != null) {
								Drawable d = mSelector.getCurrent();
								if (d != null && d instanceof TransitionDrawable) {
									((TransitionDrawable) d).resetTransition();
								}
							}
							if (mTouchModeReset != null) {
								removeCallbacks(mTouchModeReset);
							}
							mTouchModeReset = new Runnable() {
								@Override
								public void run() {
									mTouchMode = TOUCH_MODE_REST;
									child.setPressed(false);
									setPressed(false);
									if (!mDataChanged) {
										performClick.run();
									}
								}
							};
							postDelayed(mTouchModeReset, ViewConfiguration.getPressedStateDuration());
						} else {
							mTouchMode = TOUCH_MODE_REST;
							updateSelectorState();
						}
						return true;
					} else if (!mDataChanged && mAdapter.isEnabled(motionPosition)) {
						performClick.run();
					}
				}
				mTouchMode = TOUCH_MODE_REST;
				updateSelectorState();
				break;
			case TOUCH_MODE_SCROLL:
				final int childCount = getChildCount();
				if (childCount > 0) {
					final int firstChildTop = getChildAt(0).getTop();
					final int lastChildBottom = getChildAt(childCount - 1).getBottom();
					final int contentTop = mListPadding.top;
					final int contentBottom = getHeight() - mListPadding.bottom;
					if (mFirstPosition == 0 && firstChildTop >= contentTop && mFirstPosition + childCount < mItemCount && lastChildBottom <= getHeight() - contentBottom) {
						mTouchMode = TOUCH_MODE_REST;
						reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
					} else {
						final VelocityTracker velocityTracker = mVelocityTracker;
						velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);

						final int initialVelocity = (int) (velocityTracker.getYVelocity(mActivePointerId) * mVelocityScale);
						// Fling if we have enough velocity and we aren't at a
						// boundary.
						// Since we can potentially overfling more than we can
						// overscroll, don't
						// allow the weird behavior where you can scroll to a
						// boundary then
						// fling further.
						if (Math.abs(initialVelocity) > mMinimumVelocity
								&& !((mFirstPosition == 0 && firstChildTop == contentTop - mOverscrollDistance) || (mFirstPosition + childCount == mItemCount && lastChildBottom == contentBottom
										+ mOverscrollDistance))) {
							if (mFlingRunnable == null) {
								mFlingRunnable = new FlingRunnable();
							}
							reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);

							mFlingRunnable.start(-initialVelocity);
						} else {
							mTouchMode = TOUCH_MODE_REST;
							reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
							if (mFlingRunnable != null) {
								mFlingRunnable.endFling();
							}
							if (mPositionScroller != null) {
								mPositionScroller.stop();
							}
						}
					}
				} else {
					mTouchMode = TOUCH_MODE_REST;
					reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
				}
				break;

			case TOUCH_MODE_OVERSCROLL:
				if (mFlingRunnable == null) {
					mFlingRunnable = new FlingRunnable();
				}
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				final int initialVelocity = (int) velocityTracker.getYVelocity(mActivePointerId);

				reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
				if (Math.abs(initialVelocity) > mMinimumVelocity) {
					mFlingRunnable.startOverfling(-initialVelocity);
				} else {
					mFlingRunnable.startSpringback();
				}

				break;
			}

			setPressed(false);

			if (mEdgeGlowTop != null) {
				mEdgeGlowTop.onRelease();
				mEdgeGlowBottom.onRelease();
			}

			// Need to redraw since we probably aren't drawing the selector
			// anymore
			invalidate();

			final Handler handler = getHandler();
			if (handler != null) {
				handler.removeCallbacks(mPendingCheckForLongPress);
			}

			recycleVelocityTracker();

			mActivePointerId = INVALID_POINTER;

			if (PROFILE_SCROLLING) {
				if (mScrollProfilingStarted) {
					Debug.stopMethodTracing();
					mScrollProfilingStarted = false;
				}
			}

			// if (mScrollStrictSpan != null) {
			// mScrollStrictSpan.finish();
			// mScrollStrictSpan = null;
			// }
			break;
		}

		case MotionEvent.ACTION_CANCEL: {
			switch (mTouchMode) {
			case TOUCH_MODE_OVERSCROLL:
				if (mFlingRunnable == null) {
					mFlingRunnable = new FlingRunnable();
				}
				mFlingRunnable.startSpringback();
				break;

			case TOUCH_MODE_OVERFLING:
				// Do nothing - let it play out.
				break;

			default:
				mTouchMode = TOUCH_MODE_REST;
				setPressed(false);
				View motionView = this.getChildAt(mMotionPosition - mFirstPosition);
				if (motionView != null) {
					motionView.setPressed(false);
				}
				clearScrollingCache();

				final Handler handler = getHandler();
				if (handler != null) {
					handler.removeCallbacks(mPendingCheckForLongPress);
				}

				recycleVelocityTracker();
			}

			if (mEdgeGlowTop != null) {
				mEdgeGlowTop.onRelease();
				mEdgeGlowBottom.onRelease();
			}
			mActivePointerId = INVALID_POINTER;
			break;
		}

		case MotionEvent.ACTION_POINTER_UP: {
			onSecondaryPointerUp(ev);
			final int x = mMotionX;
			final int y = mMotionY;
			final int motionPosition = pointToPosition(x, y);
			if (motionPosition >= 0) {
				// Remember where the motion event started
				v = getChildAt(motionPosition - mFirstPosition);
				mMotionViewOriginalTop = v.getTop();
				mMotionPosition = motionPosition;
			}
			mLastY = y;
			break;
		}

		case MotionEvent.ACTION_POINTER_DOWN: {
			// New pointers take over dragging duties
			final int index = ev.getActionIndex();
			final int id = ev.getPointerId(index);
			final int x = (int) ev.getX(index);
			final int y = (int) ev.getY(index);
			mMotionCorrection = 0;
			mActivePointerId = id;
			mMotionX = x;
			mMotionY = y;
			final int motionPosition = pointToPosition(x, y);
			if (motionPosition >= 0) {
				// Remember where the motion event started
				v = getChildAt(motionPosition - mFirstPosition);
				mMotionViewOriginalTop = v.getTop();
				mMotionPosition = motionPosition;
			}
			mLastY = y;
			break;
		}
		}

		return true;
	}

	private void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		final int pointerId = ev.getPointerId(pointerIndex);
		if (pointerId == mActivePointerId) {
			// This was our active pointer going up. Choose a new
			// active pointer and adjust accordingly.
			// TODO: Make this decision more intelligent.
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mMotionX = (int) ev.getX(newPointerIndex);
			mMotionY = (int) ev.getY(newPointerIndex);
			mMotionCorrection = 0;
			mActivePointerId = ev.getPointerId(newPointerIndex);
		}
	}

	private void scrollIfNeeded(int y) {
		final int rawDeltaY = y - mMotionY;
		final int deltaY = rawDeltaY - mMotionCorrection;
		int incrementalDeltaY = mLastY != Integer.MIN_VALUE ? y - mLastY : deltaY;

		if (mTouchMode == TOUCH_MODE_SCROLL) {
			if (PROFILE_SCROLLING) {
				if (!mScrollProfilingStarted) {
					Debug.startMethodTracing("AbsListViewScroll");
					mScrollProfilingStarted = true;
				}
			}

			// if (mScrollStrictSpan == null) {
			// // If it's non-null, we're already in a scroll.
			// mScrollStrictSpan =
			// StrictMode.enterCriticalSpan("AbsListView-scroll");
			// }

			if (y != mLastY) {
				// We may be here after stopping a fling and continuing to
				// scroll.
				// If so, we haven't disallowed intercepting touch events yet.
				// Make sure that we do so in case we're in a parent that can
				// intercept.
				if ((getGroupFlags() & FLAG_DISALLOW_INTERCEPT) == 0 && Math.abs(rawDeltaY) > mTouchSlop) {
					final ViewParent parent = getParent();
					if (parent != null) {
						parent.requestDisallowInterceptTouchEvent(true);
					}
				}

				final int motionIndex;
				if (mMotionPosition >= 0) {
					motionIndex = mMotionPosition - mFirstPosition;
				} else {
					// If we don't have a motion position that we can reliably
					// track,
					// pick something in the middle to make a best guess at
					// things below.
					motionIndex = getChildCount() / 2;
				}

				int motionViewPrevTop = 0;
				View motionView = this.getChildAt(motionIndex);
				if (motionView != null) {
					motionViewPrevTop = motionView.getTop();
				}

				// No need to do all this work if we're not going to move anyway
				boolean atEdge = false;
				if (incrementalDeltaY != 0) {
					atEdge = trackMotionScroll(deltaY, incrementalDeltaY);
				}

				// Check to see if we have bumped into the scroll limit
				motionView = this.getChildAt(motionIndex);
				if (motionView != null) {
					// Check if the top of the motion view is where it is
					// supposed to be
					final int motionViewRealTop = motionView.getTop();
					if (atEdge) {
						// Apply overscroll

						int overscroll = -incrementalDeltaY - (motionViewRealTop - motionViewPrevTop);
						overScrollBy(0, overscroll, 0, getScrollY(), 0, 0, 0, mOverscrollDistance, true);
						if (Math.abs(mOverscrollDistance) == Math.abs(getScrollY())) {
							// Don't allow overfling if we're at the edge.
							if (mVelocityTracker != null) {
								mVelocityTracker.clear();
							}
						}

						final int overscrollMode = getOverScrollMode();
						if (overscrollMode == OVER_SCROLL_ALWAYS || (overscrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS && !contentFits())) {
							mDirection = 0; // Reset when entering overscroll.
							mTouchMode = TOUCH_MODE_OVERSCROLL;
							if (rawDeltaY > 0) {
								mEdgeGlowTop.onPull((float) overscroll / getHeight());
								if (!mEdgeGlowBottom.isFinished()) {
									mEdgeGlowBottom.onRelease();
								}
								// invalidate(mEdgeGlowTop.getBounds(false));
							} else if (rawDeltaY < 0) {
								mEdgeGlowBottom.onPull((float) overscroll / getHeight());
								if (!mEdgeGlowTop.isFinished()) {
									mEdgeGlowTop.onRelease();
								}
								// invalidate(mEdgeGlowBottom.getBounds(true));
							}
						}
					}
					mMotionY = y;
				}
				mLastY = y;
			}
		} else if (mTouchMode == TOUCH_MODE_OVERSCROLL) {
			if (y != mLastY) {
				final int oldScroll = getScrollY();
				final int newScroll = oldScroll - incrementalDeltaY;
				int newDirection = y > mLastY ? 1 : -1;

				if (mDirection == 0) {
					mDirection = newDirection;
				}

				int overScrollDistance = -incrementalDeltaY;
				if ((newScroll < 0 && oldScroll >= 0) || (newScroll > 0 && oldScroll <= 0)) {
					overScrollDistance = -oldScroll;
					incrementalDeltaY += overScrollDistance;
				} else {
					incrementalDeltaY = 0;
				}

				if (overScrollDistance != 0) {
					overScrollBy(0, overScrollDistance, 0, getScrollY(), 0, 0, 0, mOverscrollDistance, true);
					final int overscrollMode = getOverScrollMode();
					if (overscrollMode == OVER_SCROLL_ALWAYS || (overscrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS && !contentFits())) {
						if (rawDeltaY > 0) {
							mEdgeGlowTop.onPull((float) overScrollDistance / getHeight());
							if (!mEdgeGlowBottom.isFinished()) {
								mEdgeGlowBottom.onRelease();
							}
							// invalidate(mEdgeGlowTop.getBounds(false));
						} else if (rawDeltaY < 0) {
							mEdgeGlowBottom.onPull((float) overScrollDistance / getHeight());
							if (!mEdgeGlowTop.isFinished()) {
								mEdgeGlowTop.onRelease();
							}
							// invalidate(mEdgeGlowBottom.getBounds(true));
						}
					}
				}

				if (incrementalDeltaY != 0) {
					// Coming back to 'real' list scrolling
					if (getScrollY() != 0) {
						scrollTo(getScrollX(), 0);// mScrollY = 0;
						invalidateParentIfNeeded();
					}

					trackMotionScroll(incrementalDeltaY, incrementalDeltaY);

					mTouchMode = TOUCH_MODE_SCROLL;

					// We did not scroll the full amount. Treat this essentially
					// like the
					// start of a new touch scroll
					final int motionPosition = findClosestMotionRow(y);

					mMotionCorrection = 0;
					View motionView = getChildAt(motionPosition - mFirstPosition);
					mMotionViewOriginalTop = motionView != null ? motionView.getTop() : 0;
					mMotionY = y;
					mMotionPosition = motionPosition;
				}
				mLastY = y;
				mDirection = newDirection;
			}
		}
	}

	/**
	 * Find the row closest to y. This row will be used as the motion row when
	 * scrolling.
	 * 
	 * @param y
	 *            Where the user touched
	 * @return The position of the first (or only) item in the row closest to y
	 */
	int findClosestMotionRow(int y) {
		final int childCount = getChildCount();
		if (childCount == 0) {
			return INVALID_POSITION;
		}

		final int motionRow = findMotionRow(y);
		return motionRow != INVALID_POSITION ? motionRow : mFirstPosition + childCount - 1;
	}

	/**
	 * @return true if all list content currently fits within the view
	 *         boundaries
	 */
	private boolean contentFits() {
		final int childCount = getChildCount();
		if (childCount == 0)
			return true;
		if (childCount != mItemCount)
			return false;

		return getChildAt(0).getTop() >= mListPadding.top && getChildAt(childCount - 1).getBottom() <= getHeight() - mListPadding.bottom;
	}

	private boolean startScrollIfNeeded(int y) {
		// Check if we have moved far enough that it looks more like a
		// scroll than a tap
		final int deltaY = y - mMotionY;
		final int distance = Math.abs(deltaY);
		final boolean overscroll = getScrollY() != 0;
		if (overscroll || distance > mTouchSlop) {
			createScrollingCache();
			if (overscroll) {
				mTouchMode = TOUCH_MODE_OVERSCROLL;
				mMotionCorrection = 0;
			} else {
				mTouchMode = TOUCH_MODE_SCROLL;
				mMotionCorrection = deltaY > 0 ? mTouchSlop : -mTouchSlop;
			}
			final Handler handler = getHandler();
			// Handler should not be null unless the AbsListView is not attached
			// to a
			// window, which would make it very hard to scroll it... but the
			// monkeys
			// say it's possible.
			if (handler != null) {
				handler.removeCallbacks(mPendingCheckForLongPress);
			}
			setPressed(false);
			View motionView = getChildAt(mMotionPosition - mFirstPosition);
			if (motionView != null) {
				motionView.setPressed(false);
			}
			reportScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
			// Time to start stealing events! Once we've stolen them, don't let
			// anyone
			// steal from us
			final ViewParent parent = getParent();
			if (parent != null) {
				parent.requestDisallowInterceptTouchEvent(true);
			}
			scrollIfNeeded(y);
			return true;
		}

		return false;
	}

	final class CheckForTap implements Runnable {
		public void run() {
			if (mTouchMode == TOUCH_MODE_DOWN) {
				mTouchMode = TOUCH_MODE_TAP;
				final View child = getChildAt(mMotionPosition - mFirstPosition);
				if (child != null && !child.hasFocusable()) {
					mLayoutMode = LAYOUT_NORMAL;

					if (!mDataChanged) {
						child.setPressed(true);
						setPressed(true);
						layoutChildren();
						positionSelector(mMotionPosition, child);
						refreshDrawableState();

						final int longPressTimeout = ViewConfiguration.getLongPressTimeout();
						final boolean longClickable = isLongClickable();

						if (mSelector != null) {
							Drawable d = mSelector.getCurrent();
							if (d != null && d instanceof TransitionDrawable) {
								if (longClickable) {
									((TransitionDrawable) d).startTransition(longPressTimeout);
								} else {
									((TransitionDrawable) d).resetTransition();
								}
							}
						}

						if (longClickable) {
							if (mPendingCheckForLongPress == null) {
								mPendingCheckForLongPress = new CheckForLongPress();
							}
							mPendingCheckForLongPress.rememberWindowAttachCount();
							postDelayed(mPendingCheckForLongPress, longPressTimeout);
						} else {
							mTouchMode = TOUCH_MODE_DONE_WAITING;
						}
					} else {
						mTouchMode = TOUCH_MODE_DONE_WAITING;
					}
				}
			}
		}
	}

	abstract void fillGap(boolean isDown);

	/**
	 * Find the row closest to y. This row will be used as the motion row when
	 * scrolling
	 * 
	 * @param y
	 *            Where the user touched
	 * @return The position of the first (or only) item in the row containing y
	 */
	abstract int findMotionRow(int y);

	/**
	 * Tracks a motion scroll. In reality, this is used to do just about any
	 * movement to items (touch scroll, arrow-key scroll, set an item as
	 * selected).
	 * 
	 * @param deltaX
	 *            Change in X from the previous event.
	 */
	boolean trackMotionScroll(int deltaX, int incrementalDeltaX) {

		if (getChildCount() == 0) {
			return true;
		}

		boolean isDown = deltaX < 0;

		int limitedDeltaX = deltaX;
		// int limitedDeltaX = deltaX;
		if (limitedDeltaX != deltaX) {
			mFlingRunnable.endFling();
			// onFinishedMovement();
		}

		offsetChildrenTopAndBottom(limitedDeltaX);

		detachOffScreenChildren(isDown);

		fillGap(isDown);

		onScrollChanged(0, 0, 0, 0);

		invalidate();

		return false;
	}

	/**
	 * Detaches children that are off the screen (i.e.: Gallery bounds).
	 * 
	 * @param toLeft
	 *            Whether to detach children to the left of the Gallery, or to
	 *            the right.
	 */
	protected void detachOffScreenChildren(boolean isDown) {
		int numChildren = getChildCount();
		int firstPosition = mFirstPosition;
		int start = 0;
		int count = 0;

		if (isDown) {
			final int top = getPaddingTop();
			for (int i = 0; i < numChildren; i++) {
				int n = i;
				final View child = getChildAt(n);
				if (child.getBottom() >= top) {
					break;
				} else {
					start = n;
					count++;
					mRecycler.addScrapView(child, firstPosition + n);
				}
			}

			start = 0;
		} else {
			final int bottom = getHeight() - getPaddingBottom();
			for (int i = numChildren - 1; i >= 0; i--) {
				int n = i;
				final View child = getChildAt(n);
				if (child.getTop() <= bottom) {
					break;
				} else {
					start = n;
					count++;
					mRecycler.addScrapView(child, firstPosition + n);
				}
			}
		}

		detachViewsFromParent(start, count);

		if (isDown) {
			mFirstPosition += count;
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
			canvas.clipRect(scrollX + getPaddingLeft(), scrollY + getPaddingTop(), scrollX + getRight() - getLeft() - getPaddingRight(), scrollY + getBottom() - getTop() - getPaddingBottom());
			int flags = getGroupFlags();
			flags &= ~CLIP_TO_PADDING_MASK;
			setGroupFlags(flags);
		}

		final boolean drawSelectorOnTop = drawSclectorOnTop();
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
			setGroupFlags(flags);
		}
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
			mSpecificTop = mListPadding.left + x;

			if (mNeedSync) {
				mSyncPosition = position;
				mSyncRowId = mAdapter.getItemId(position);
			}

			requestLayout();
		}
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

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (mAdapter == null || !mIsAttached) {
			return false;
		}
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER: {

			if (!isEnabled()) {
				return true;
			}
			if (/* isClickable() && isPressed() && */mSelectedPosition >= 0 && mAdapter != null && mSelectedPosition < mAdapter.getCount()) {

				final View view = getChildAt(mSelectedPosition - mFirstPosition);
				if (view != null) {
					performItemClick(view, mSelectedPosition, mSelectedRowId);
					view.setPressed(false);
				}
				setPressed(false);

				return true;
			}
		}
		}

		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void setSelectedPositionInt(int position) {
		super.setSelectedPositionInt(position);

		// Updates any metadata we keep about the selected item.
		// updateSelectedItemMetadata(position);
		setNextSelectedPositionInt(position);
	}

	/**
	 * What is the distance between the source and destination rectangles given
	 * the direction of focus navigation between them? The direction basically
	 * helps figure out more quickly what is self evident by the relationship
	 * between the rects...
	 * 
	 * @param source
	 *            the source rectangle
	 * @param dest
	 *            the destination rectangle
	 * @param direction
	 *            the direction
	 * @return the distance between the rectangles
	 */
	static int getDistance(Rect source, Rect dest, int direction) {
		int sX, sY; // source x, y
		int dX, dY; // dest x, y
		switch (direction) {
		case FOCUS_RIGHT:
			sX = source.right;
			sY = source.top + source.height() / 2;
			dX = dest.left;
			dY = dest.top + dest.height() / 2;
			break;
		case FOCUS_DOWN:
			sX = source.left + source.width() / 2;
			sY = source.bottom;
			dX = dest.left + dest.width() / 2;
			dY = dest.top;
			break;
		case FOCUS_LEFT:
			sX = source.left;
			sY = source.top + source.height() / 2;
			dX = dest.right;
			dY = dest.top + dest.height() / 2;
			break;
		case FOCUS_UP:
			sX = source.left + source.width() / 2;
			sY = source.top;
			dX = dest.left + dest.width() / 2;
			dY = dest.bottom;
			break;
		case FOCUS_FORWARD:
		case FOCUS_BACKWARD:
			sX = source.right + source.width() / 2;
			sY = source.top + source.height() / 2;
			dX = dest.left + dest.width() / 2;
			dY = dest.top + dest.height() / 2;
			break;
		default:
			throw new IllegalArgumentException("direction must be one of " + "{FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, " + "FOCUS_FORWARD, FOCUS_BACKWARD}.");
		}
		int deltaX = dX - sX;
		int deltaY = dY - sY;
		return deltaY * deltaY + deltaX * deltaX;
	}

	/**
	 * Attempt to bring the selection back if the user is switching from touch
	 * to trackball mode
	 * 
	 * @return Whether selection was set to something.
	 */
	@Override
	boolean resurrectSelection() {
		final int childCount = getChildCount();

		if (childCount <= 0) {
			return false;
		}

		int selectedTop = 0;
		int selectedPos;
		int childrenTop = mListPadding.top;
		int childrenBottom = getBottom() - getTop() - mListPadding.bottom;
		final int firstPosition = mFirstPosition;
		final int toPosition = mResurrectToPosition;
		boolean down = true;

		if (toPosition >= firstPosition && toPosition < firstPosition + childCount) {
			selectedPos = toPosition;

			final View selected = getChildAt(selectedPos - mFirstPosition);
			selectedTop = selected.getTop();
			int selectedBottom = selected.getBottom();

			// We are scrolled, don't get in the fade
			if (selectedTop < childrenTop) {
				selectedTop = childrenTop + getVerticalFadingEdgeLength();
			} else if (selectedBottom > childrenBottom) {
				selectedTop = childrenBottom - selected.getMeasuredHeight() - getVerticalFadingEdgeLength();
			}
		} else {
			if (toPosition < firstPosition) {
				// Default to selecting whatever is first
				selectedPos = firstPosition;
				for (int i = 0; i < childCount; i++) {
					final View v = getChildAt(i);
					final int top = v.getTop();

					if (i == 0) {
						// Remember the position of the first item
						selectedTop = top;
						// See if we are scrolled at all
						if (firstPosition > 0 || top < childrenTop) {
							// If we are scrolled, don't select anything that is
							// in the fade region
							childrenTop += getVerticalFadingEdgeLength();
						}
					}
					if (top >= childrenTop) {
						// Found a view whose top is fully visisble
						selectedPos = firstPosition + i;
						selectedTop = top;
						break;
					}
				}
			} else {
				final int itemCount = mItemCount;
				down = false;
				selectedPos = firstPosition + childCount - 1;

				for (int i = childCount - 1; i >= 0; i--) {
					final View v = getChildAt(i);
					final int top = v.getTop();
					final int bottom = v.getBottom();

					if (i == childCount - 1) {
						selectedTop = top;
						if (firstPosition + childCount < itemCount || bottom > childrenBottom) {
							childrenBottom -= getVerticalFadingEdgeLength();
						}
					}

					if (bottom <= childrenBottom) {
						selectedPos = firstPosition + i;
						selectedTop = top;
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
		mTouchMode = TOUCH_MODE_REST;
		clearScrollingCache();
		mSpecificTop = selectedTop;
		selectedPos = lookForSelectablePosition(selectedPos, down);
		if (selectedPos >= firstPosition && selectedPos <= getLastVisiblePosition()) {
			mLayoutMode = LAYOUT_SPECIFIC;
			updateSelectorState();
			setSelectionInt(selectedPos);
			// TODO
			// invokeOnItemScrollListener();
		} else {
			selectedPos = INVALID_POSITION;
		}
		reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);

		return selectedPos >= 0;
	}

    /**
     * set default scroll step
     */
    public void setDefatultScrollStep(float step) {
        if (mFlingRunnable == null) {
            mFlingRunnable = new FlingRunnable();
        }
        mFlingRunnable.setDefatultScrollStep(step);
    }
	
	/**
	 * Smoothly scroll by distance pixels over duration milliseconds.
	 * 
	 * @param distance
	 *            Distance to scroll in pixels.
	 */
	public void smoothScrollBy(int distance) {
		smoothScrollBy(distance, false);
	}

	void smoothScrollBy(int distance, boolean linear) {
		if (mFlingRunnable == null) {
			mFlingRunnable = new FlingRunnable();
		}

		// No sense starting to scroll if we're not going anywhere
		final int firstPos = mFirstPosition;
		final int childCount = getChildCount();
		final int lastPos = firstPos + childCount;
		final int topLimit = getPaddingTop();
		final int bottomLimit = getHeight() - getPaddingBottom();

		if (distance == 0 || mItemCount == 0 || childCount == 0 || (firstPos == 0 && getChildAt(0).getTop() == topLimit && distance < 0)
				|| (lastPos == mItemCount && getChildAt(childCount - 1).getBottom() == bottomLimit && distance > 0)) {
			mFlingRunnable.endFling();
			if (mPositionScroller != null) {
				mPositionScroller.stop();
			}
		} else {
			reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
			mFlingRunnable.startScroll(distance, linear);
		}
	}

	public void setFlipScrollFrameCount(int frameCount) {
		if (mFlingRunnable != null) {
			mFlingRunnable.setFrameCount(frameCount);
		}
	}

	public void setFlipScrollMaxStep(float maxStep) {
		if (mFlingRunnable != null) {
			mFlingRunnable.setMaxStep(maxStep);
		}
	}

	public boolean isListLoopScrolling() {
		if (mFlingRunnable != null) {
			return mFlingRunnable.isListLoopScrolling();
		}
		return false;
	}

	public int getLeftScrollDistance() {
		if (mFlingRunnable != null) {
			return mFlingRunnable.getLeftScrollDistance();
		}
		return 0;
	}

	public void postOnAnimation(Runnable action) {
		// API 16 need super.postOnAniamtion(action)
		post(action);
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
	 * Responsible for fling behavior. Use {@link #start(int)} to initiate a
	 * fling. Each frame of the fling is handled in {@link #run()}. A
	 * FlingRunnable will keep re-posting itself until the fling is done.
	 * 
	 */
	private class FlingRunnable implements Runnable {
		/**
		 * Tracks the decay of a fling scroll
		 */
		private final OverScroller mScroller;

		private ListLoopScroller mListLoopScroller;

		/**
		 * Y value reported by mScroller on the previous fling
		 */
		private int mLastFlingY;

		private int mFrameCount;

		private float mDefatultScrollStep = 5.0f;
		
        public void setDefatultScrollStep(float step) {
            mDefatultScrollStep = step;
        }
		
		private final Runnable mCheckFlywheel = new Runnable() {
			public void run() {
				final int activeId = mActivePointerId;
				final VelocityTracker vt = mVelocityTracker;
				final OverScroller scroller = mScroller;
				if (vt == null || activeId == INVALID_POINTER) {
					return;
				}

				vt.computeCurrentVelocity(1000, mMaximumVelocity);
				final float yvel = -vt.getYVelocity(activeId);

				if (Math.abs(yvel) >= mMinimumVelocity && scroller.isScrollingInDirection(0, yvel)) {
					// Keep the fling alive a little longer
					postDelayed(this, FLYWHEEL_TIMEOUT);
				} else {
					endFling();
					mTouchMode = TOUCH_MODE_SCROLL;
					reportScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
				}
			}
		};

		private static final int FLYWHEEL_TIMEOUT = 40; // milliseconds

		FlingRunnable() {
			mScroller = new OverScroller(getContext());
			mListLoopScroller = new ListLoopScroller();
		}

		void start(int initialVelocity) {
			int initialY = initialVelocity < 0 ? Integer.MAX_VALUE : 0;
			mLastFlingY = initialY;
			mScroller.setInterpolator(null);
			mScroller.fling(0, initialY, 0, initialVelocity, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
			mTouchMode = TOUCH_MODE_FLING;
			postOnAnimation(this);

			if (PROFILE_FLINGING) {
				if (!mFlingProfilingStarted) {
					Debug.startMethodTracing("AbsListViewFling");
					mFlingProfilingStarted = true;
				}
			}

			// if (mFlingStrictSpan == null) {
			// mFlingStrictSpan =
			// StrictMode.enterCriticalSpan("AbsListView-fling");
			// }
		}

		void startSpringback() {
			if (mScroller.springBack(0, getScrollY(), 0, 0, 0, 0)) {
				mTouchMode = TOUCH_MODE_OVERFLING;
				invalidate();
				postOnAnimation(this);
			} else {
				mTouchMode = TOUCH_MODE_REST;
				reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
			}
		}

		void startOverfling(int initialVelocity) {
			mScroller.setInterpolator(null);
			mScroller.fling(0, getScrollY(), 0, initialVelocity, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, getHeight());
			mTouchMode = TOUCH_MODE_OVERFLING;
			invalidate();
			postOnAnimation(this);
		}

		void edgeReached(int delta) {
			mScroller.notifyVerticalEdgeReached(getScrollY(), 0, mOverflingDistance);
			final int overscrollMode = getOverScrollMode();
			if (overscrollMode == OVER_SCROLL_ALWAYS || (overscrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS && !contentFits())) {
				mTouchMode = TOUCH_MODE_OVERFLING;
				final int vel = (int) mScroller.getCurrVelocity();
				if (delta > 0) {
					mEdgeGlowTop.onAbsorb(vel);
				} else {
					mEdgeGlowBottom.onAbsorb(vel);
				}
			} else {
				mTouchMode = TOUCH_MODE_REST;
				if (mPositionScroller != null) {
					mPositionScroller.stop();
				}
			}
			invalidate();
			postOnAnimation(this);
		}

		void startScroll(int distance, boolean linear) {
			int frameCount;
			if (mFrameCount <= 0) {
				// use default sroll step
				frameCount = (int) (distance / mDefatultScrollStep);
				if (frameCount < 0) {
					frameCount = -frameCount;
				}
				else if(frameCount == 0){
					frameCount = 1;
				}
			} else {
				frameCount = mFrameCount;
			}
			mLastFlingY = 0;
			if (mListLoopScroller.isFinished()) {
				mListLoopScroller.startScroll(0, distance, frameCount);
				mTouchMode = TOUCH_MODE_FLING;
				postOnAnimation(this);
			} else {
				mListLoopScroller.startScroll(0, distance, frameCount);
			}
		}

		void endFling() {
			mTouchMode = TOUCH_MODE_REST;

			removeCallbacks(this);
			removeCallbacks(mCheckFlywheel);

			reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
			clearScrollingCache();
			mScroller.abortAnimation();
			mListLoopScroller.finish();
			// if (mFlingStrictSpan != null) {
			// mFlingStrictSpan.finish();
			// mFlingStrictSpan = null;
			// }
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

		boolean isListLoopScrolling() {
			if (mTouchMode == TOUCH_MODE_FLING) {
				return mListLoopScroller.isFinished() == false ? true : false;
			}
			return false;
		}

		void flywheelTouch() {
			postDelayed(mCheckFlywheel, FLYWHEEL_TIMEOUT);
		}

		public void run() {
			switch (mTouchMode) {
			default:
				endFling();
				return;

			case TOUCH_MODE_SCROLL:
				if (mScroller.isFinished()) {
					return;
				}
				// Fall through
			case TOUCH_MODE_FLING: {
				if (mDataChanged) {
					layoutChildren();
				}

				if (mItemCount == 0 || getChildCount() == 0) {
					endFling();
					return;
				}

				// final OverScroller scroller = mScroller;
				boolean more = mListLoopScroller.computeScrollOffset();
				final int y = mListLoopScroller.getCurr();

				// Flip sign to convert finger direction to list items direction
				// (e.g. finger moving down means list is moving towards the
				// top)
				int delta = mLastFlingY - y;

				// Pretend that each frame of a fling scroll is a touch scroll
				if (delta > 0) {
					// List is moving towards the top. Use first view as
					// mMotionPosition
					mMotionPosition = mFirstPosition;
					final View firstView = getChildAt(0);
					mMotionViewOriginalTop = firstView.getTop();

					// Don't fling more than 1 screen
					delta = Math.min(getHeight() - getPaddingBottom() - getTop() - 1, delta);
				} else {
					// List is moving towards the bottom. Use last view as
					// mMotionPosition
					int offsetToLast = getChildCount() - 1;
					mMotionPosition = mFirstPosition + offsetToLast;

					final View lastView = getChildAt(offsetToLast);
					mMotionViewOriginalTop = lastView.getTop();

					// Don't fling more than 1 screen
					delta = Math.max(-(getHeight() - getPaddingBottom() - getPaddingTop() - 1), delta);
				}

				// Check to see if we have bumped into the scroll limit
				View motionView = getChildAt(mMotionPosition - mFirstPosition);
				int oldTop = 0;
				if (motionView != null) {
					oldTop = motionView.getTop();
				}

				// Don't stop just because delta is zero (it could have been
				// rounded)
				final boolean atEdge = trackMotionScroll(delta, delta);
				final boolean atEnd = atEdge && (delta != 0);
				if (atEnd) {
					if (motionView != null) {
						// Tweak the scroll for how far we overshot
						int overshoot = -(delta - (motionView.getTop() - oldTop));
						overScrollBy(0, overshoot, 0, getScrollY(), 0, 0, 0, mOverflingDistance, false);
					}
					if (more) {
						edgeReached(delta);
					}
					break;
				}

				if (more && !atEnd) {
					if (atEdge)
						invalidate();
					mLastFlingY = y;
					postOnAnimation(this);
				} else {
					endFling();

					if (PROFILE_FLINGING) {
						if (mFlingProfilingStarted) {
							Debug.stopMethodTracing();
							mFlingProfilingStarted = false;
						}

						// if (mFlingStrictSpan != null) {
						// mFlingStrictSpan.finish();
						// mFlingStrictSpan = null;
						// }
					}
				}
				break;
			}

			case TOUCH_MODE_OVERFLING: {
				final OverScroller scroller = mScroller;
				if (scroller.computeScrollOffset()) {
					final int scrollY = getScrollY();
					final int currY = scroller.getCurrY();
					final int deltaY = currY - scrollY;
					if (overScrollBy(0, deltaY, 0, scrollY, 0, 0, 0, mOverflingDistance, false)) {
						final boolean crossDown = scrollY <= 0 && currY > 0;
						final boolean crossUp = scrollY >= 0 && currY < 0;
						if (crossDown || crossUp) {
							int velocity = (int) scroller.getCurrVelocity();
							if (crossUp)
								velocity = -velocity;

							// Don't flywheel from this; we're just continuing
							// things.
							scroller.abortAnimation();
							start(velocity);
						} else {
							startSpringback();
						}
					} else {
						invalidate();
						postOnAnimation(this);
					}
				} else {
					endFling();
				}
				break;
			}
			}
		}
	}

	class PositionScroller implements Runnable {
		private static final int SCROLL_DURATION = 400;

		private static final int MOVE_DOWN_POS = 1;
		private static final int MOVE_UP_POS = 2;
		private static final int MOVE_DOWN_BOUND = 3;
		private static final int MOVE_UP_BOUND = 4;
		private static final int MOVE_OFFSET = 5;

		private int mMode;
		private int mTargetPos;
		private int mBoundPos;
		private int mLastSeenPos;
		private int mScrollDuration;
		private final int mExtraScroll;

		private int mOffsetFromTop;

		PositionScroller() {
			mExtraScroll = ViewConfiguration.get(getContext()).getScaledFadingEdgeLength();
		}

		void start(int position) {
			stop();

			final int firstPos = mFirstPosition;
			final int lastPos = firstPos + getChildCount() - 1;

			int viewTravelCount;
			if (position <= firstPos) {
				viewTravelCount = firstPos - position + 1;
				mMode = MOVE_UP_POS;
			} else if (position >= lastPos) {
				viewTravelCount = position - lastPos + 1;
				mMode = MOVE_DOWN_POS;
			} else {
				// Already on screen, nothing to do
				return;
			}

			if (viewTravelCount > 0) {
				mScrollDuration = SCROLL_DURATION / viewTravelCount;
			} else {
				mScrollDuration = SCROLL_DURATION;
			}
			mTargetPos = position;
			mBoundPos = INVALID_POSITION;
			mLastSeenPos = INVALID_POSITION;

			post(this);
		}

		void start(int position, int boundPosition) {
			stop();

			if (boundPosition == INVALID_POSITION) {
				start(position);
				return;
			}

			final int firstPos = mFirstPosition;
			final int lastPos = firstPos + getChildCount() - 1;

			int viewTravelCount;
			if (position <= firstPos) {
				final int boundPosFromLast = lastPos - boundPosition;
				if (boundPosFromLast < 1) {
					// Moving would shift our bound position off the screen.
					// Abort.
					return;
				}

				final int posTravel = firstPos - position + 1;
				final int boundTravel = boundPosFromLast - 1;
				if (boundTravel < posTravel) {
					viewTravelCount = boundTravel;
					mMode = MOVE_UP_BOUND;
				} else {
					viewTravelCount = posTravel;
					mMode = MOVE_UP_POS;
				}
			} else if (position >= lastPos) {
				final int boundPosFromFirst = boundPosition - firstPos;
				if (boundPosFromFirst < 1) {
					// Moving would shift our bound position off the screen.
					// Abort.
					return;
				}

				final int posTravel = position - lastPos + 1;
				final int boundTravel = boundPosFromFirst - 1;
				if (boundTravel < posTravel) {
					viewTravelCount = boundTravel;
					mMode = MOVE_DOWN_BOUND;
				} else {
					viewTravelCount = posTravel;
					mMode = MOVE_DOWN_POS;
				}
			} else {
				// Already on screen, nothing to do
				return;
			}

			if (viewTravelCount > 0) {
				mScrollDuration = SCROLL_DURATION / viewTravelCount;
			} else {
				mScrollDuration = SCROLL_DURATION;
			}
			mTargetPos = position;
			mBoundPos = boundPosition;
			mLastSeenPos = INVALID_POSITION;

			post(this);
		}

		void startWithOffset(int position, int offset) {
			startWithOffset(position, offset, SCROLL_DURATION);
		}

		void startWithOffset(int position, int offset, int duration) {
			stop();

			mTargetPos = position;
			mOffsetFromTop = offset;
			mBoundPos = INVALID_POSITION;
			mLastSeenPos = INVALID_POSITION;
			mMode = MOVE_OFFSET;

			final int firstPos = mFirstPosition;
			final int childCount = getChildCount();
			final int lastPos = firstPos + childCount - 1;

			int viewTravelCount;
			if (position < firstPos) {
				viewTravelCount = firstPos - position;
			} else if (position > lastPos) {
				viewTravelCount = position - lastPos;
			} else {
				// On-screen, just scroll.
				final int targetTop = getChildAt(position - firstPos).getTop();
				smoothScrollBy(targetTop - offset);
				return;
			}

			// Estimate how many screens we should travel
			final float screenTravelCount = (float) viewTravelCount / childCount;
			mScrollDuration = screenTravelCount < 1 ? (int) (screenTravelCount * duration) : (int) (duration / screenTravelCount);
			mLastSeenPos = INVALID_POSITION;

			post(this);
		}

		void stop() {
			removeCallbacks(this);
		}

		public void run() {
			if (mTouchMode != TOUCH_MODE_FLING && mLastSeenPos != INVALID_POSITION) {
				return;
			}

			final int listHeight = getHeight();
			final int firstPos = mFirstPosition;

			switch (mMode) {
			case MOVE_DOWN_POS: {
				final int lastViewIndex = getChildCount() - 1;
				final int lastPos = firstPos + lastViewIndex;

				if (lastViewIndex < 0) {
					return;
				}

				if (lastPos == mLastSeenPos) {
					// No new views, let things keep going.
					post(this);
					return;
				}

				final View lastView = getChildAt(lastViewIndex);
				final int lastViewHeight = lastView.getHeight();
				final int lastViewTop = lastView.getTop();
				final int lastViewPixelsShowing = listHeight - lastViewTop;
				final int extraScroll = lastPos < mItemCount - 1 ? mExtraScroll : mListPadding.bottom;

				smoothScrollBy(lastViewHeight - lastViewPixelsShowing + extraScroll);

				mLastSeenPos = lastPos;
				if (lastPos < mTargetPos) {
					post(this);
				}
				break;
			}

			case MOVE_DOWN_BOUND: {
				final int nextViewIndex = 1;
				final int childCount = getChildCount();

				if (firstPos == mBoundPos || childCount <= nextViewIndex || firstPos + childCount >= mItemCount) {
					return;
				}
				final int nextPos = firstPos + nextViewIndex;

				if (nextPos == mLastSeenPos) {
					// No new views, let things keep going.
					post(this);
					return;
				}

				final View nextView = getChildAt(nextViewIndex);
				final int nextViewHeight = nextView.getHeight();
				final int nextViewTop = nextView.getTop();
				final int extraScroll = mExtraScroll;
				if (nextPos < mBoundPos) {
					smoothScrollBy(Math.max(0, nextViewHeight + nextViewTop - extraScroll));

					mLastSeenPos = nextPos;

					post(this);
				} else {
					if (nextViewTop > extraScroll) {
						smoothScrollBy(nextViewTop - extraScroll);
					}
				}
				break;
			}

			case MOVE_UP_POS: {
				if (firstPos == mLastSeenPos) {
					// No new views, let things keep going.
					post(this);
					return;
				}

				final View firstView = getChildAt(0);
				if (firstView == null) {
					return;
				}
				final int firstViewTop = firstView.getTop();
				final int extraScroll = firstPos > 0 ? mExtraScroll : mListPadding.top;

				smoothScrollBy(firstViewTop - extraScroll);

				mLastSeenPos = firstPos;

				if (firstPos > mTargetPos) {
					post(this);
				}
				break;
			}

			case MOVE_UP_BOUND: {
				final int lastViewIndex = getChildCount() - 2;
				if (lastViewIndex < 0) {
					return;
				}
				final int lastPos = firstPos + lastViewIndex;

				if (lastPos == mLastSeenPos) {
					// No new views, let things keep going.
					post(this);
					return;
				}

				final View lastView = getChildAt(lastViewIndex);
				final int lastViewHeight = lastView.getHeight();
				final int lastViewTop = lastView.getTop();
				final int lastViewPixelsShowing = listHeight - lastViewTop;
				mLastSeenPos = lastPos;
				if (lastPos > mBoundPos) {
					smoothScrollBy(-(lastViewPixelsShowing - mExtraScroll));
					post(this);
				} else {
					final int bottom = listHeight - mExtraScroll;
					final int lastViewBottom = lastViewTop + lastViewHeight;
					if (bottom > lastViewBottom) {
						smoothScrollBy(-(bottom - lastViewBottom));
					}
				}
				break;
			}

			case MOVE_OFFSET: {
				if (mLastSeenPos == firstPos) {
					// No new views, let things keep going.
					post(this);
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
					smoothScrollBy((int) (-getHeight() * modifier));
					post(this);
				} else if (position > lastPos) {
					smoothScrollBy((int) (getHeight() * modifier));
					post(this);
				} else {
					// On-screen, just scroll.
					final int targetTop = getChildAt(position - firstPos).getTop();
					final int distance = targetTop - mOffsetFromTop;
					smoothScrollBy(distance);
				}
				break;
			}

			default:
				break;
			}
		}
	}

}
