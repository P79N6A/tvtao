package com.yunos.tvlife.app.widget;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.OverScroller;

import com.yunos.tvlife.lib.LOG;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class FlingManager implements Runnable {
	private static final String TAG = "FlingManager";
	private static final boolean DEBUG = true;
	/**
	 * Tracks the decay of a fling scroll
	 */
	private final OverScroller mScroller;
	private final FocusedGridView mGridView;
	/**
	 * Y value reported by mScroller on the previous fling
	 */
	private int mLastFlingY;

	int mActualY;

	public interface FlingCallback {
		public void flingLayoutChildren();

		public int getScrollY();

		public int getClipToPaddingMask();

		public boolean flingOverScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY,
                                         int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent);

		public void flingDetachViewsFromParent(int start, int count);

		public boolean flingAwakenScrollBars();
	};

	FlingCallback mFlingCallback;

	int getActualY() {
		synchronized (FlingManager.this) {
			return mActualY;
		}
	}

	// private final Runnable mCheckFlywheel = new Runnable() {
	// public void run() {
	// final int activeId = mActivePointerId;
	// final VelocityTracker vt = mVelocityTracker;
	// final OverScroller scroller = mScroller;
	// if (vt == null || activeId == INVALID_POINTER) {
	// return;
	// }
	//
	// vt.computeCurrentVelocity(1000, mMaximumVelocity);
	// final float yvel = -vt.getYVelocity(activeId);
	//
	// if (Math.abs(yvel) >= mMinimumVelocity
	// && scroller.isScrollingInDirection(0, yvel)) {
	// // Keep the fling alive a little longer
	// postDelayed(this, FLYWHEEL_TIMEOUT);
	// } else {
	// endFling();
	// mTouchMode = TOUCH_MODE_SCROLL;
	// reportScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
	// }
	// }
	// };

	private static final int FLYWHEEL_TIMEOUT = 40; // milliseconds

	public FlingManager(FocusedGridView gridView, FlingCallback callback) {
		mGridView = gridView;
		mFlingCallback = callback;
		mScroller = new OverScroller(mGridView.getContext());
	}

	// void start(int initialVelocity) {
	// int initialY = initialVelocity < 0 ? Integer.MAX_VALUE : 0;
	// mLastFlingY = initialY;
	// mScroller.fling(0, initialY, 0, initialVelocity,
	// 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
	// mTouchMode = TOUCH_MODE_FLING;
	// post(this);
	//
	// if (PROFILE_FLINGING) {
	// if (!mFlingProfilingStarted) {
	// Debug.startMethodTracing("AbsListViewFling");
	// mFlingProfilingStarted = true;
	// }
	// }
	//
	// if (mFlingStrictSpan == null) {
	// mFlingStrictSpan = StrictMode.enterCriticalSpan("AbsListView-fling");
	// }
	// }

	// void startSpringback() {
	// if (mScroller.springBack(0, mScrollY, 0, 0, 0, 0)) {
	// mTouchMode = TOUCH_MODE_OVERFLING;
	// invalidate();
	// post(this);
	// } else {
	// mTouchMode = TOUCH_MODE_REST;
	// reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
	// }
	// }

	// void startOverfling(int initialVelocity) {
	// mScroller.fling(0, mScrollY, 0, initialVelocity, 0, 0,
	// Integer.MIN_VALUE, Integer.MAX_VALUE, 0, getHeight());
	// mTouchMode = TOUCH_MODE_OVERFLING;
	// invalidate();
	// post(this);
	// }

	// void edgeReached(int delta) {
	// mScroller.notifyVerticalEdgeReached(mScrollY, 0, mOverflingDistance);
	// final int overscrollMode = getOverScrollMode();
	// if (overscrollMode == OVER_SCROLL_ALWAYS ||
	// (overscrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS && !contentFits()))
	// {
	// mTouchMode = TOUCH_MODE_OVERFLING;
	// final int vel = (int) mScroller.getCurrVelocity();
	// if (delta > 0) {
	// mEdgeGlowTop.onAbsorb(vel);
	// } else {
	// mEdgeGlowBottom.onAbsorb(vel);
	// }
	// } else {
	// mTouchMode = TOUCH_MODE_REST;
	// if (mPositionScroller != null) {
	// mPositionScroller.stop();
	// }
	// }
	// invalidate();
	// post(this);
	// }

	void startScroll(int distance, int duration) {
		LOG.i(TAG, DEBUG, "FlingRunnable startScroll distance = " + distance + ", duration");
		int initialY = distance < 0 ? Integer.MAX_VALUE : 0;
		mLastFlingY = initialY;
		mActualY = initialY;
		mScroller.startScroll(0, initialY, 0, distance, duration);
		// mTouchMode = TOUCH_MODE_FLING;
		setTouchMode(4);
		mGridView.post(this);
	}

	void endFling() {
		// mTouchMode = TOUCH_MODE_REST;
		LOG.i(TAG, DEBUG, "FlingRunnable endFling");
		setTouchMode(-1);

		mGridView.removeCallbacks(this);
		// removeCallbacks(mCheckFlywheel);

		focusedReportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
		focusedClearScrollingCache();
		mScroller.abortAnimation();

		// if (mFlingStrictSpan != null) {
		// mFlingStrictSpan.finish();
		// mFlingStrictSpan = null;
		// }
		finishFlingStrictSpan();
	}

	// void flywheelTouch() {
	// postDelayed(mCheckFlywheel, FLYWHEEL_TIMEOUT);
	// }

	public void run() {
		int mode = getTouchMode();
		switch (mode) {
		default:
			endFling();
			return;

			// case TOUCH_MODE_SCROLL:
			// if (mScroller.isFinished()) {
			// return;
			// }
			// Fall through
		case 4: {
			if (getDataChanged()) {
				mFlingCallback.flingLayoutChildren();
			}

			if (mGridView.getAdapter().getCount() == 0 || mGridView.getChildCount() == 0) {
				endFling();
				return;
			}

			final OverScroller scroller = mScroller;
			boolean more = scroller.computeScrollOffset();
			final int y = scroller.getCurrY();
			synchronized (FlingManager.this) {
				mActualY = y;
			}

			// Flip sign to convert finger direction to list items direction
			// (e.g. finger moving down means list is moving towards the
			// top)
			int delta = mLastFlingY - y;

			// Pretend that each frame of a fling scroll is a touch scroll
			if (delta > 0) {
				// List is moving towards the top. Use first view as
				// mMotionPosition
				// mMotionPosition = mFirstPosition;
				setMotionPosition(mGridView.getFirstVisiblePosition());
				final View firstView = mGridView.getChildAt(0);
				// mMotionViewOriginalTop = firstView.getTop();
				setMotionViewOriginalTop(firstView.getTop());
				// Don't fling more than 1 screen
				delta = Math.min(mGridView.getHeight() - mGridView.getPaddingBottom() - mGridView.getPaddingTop() - 1, delta);
			} else {
				// List is moving towards the bottom. Use last view as
				// mMotionPosition
				int offsetToLast = mGridView.getChildCount() - 1;
				// mMotionPosition = mFirstPosition + offsetToLast;
				setMotionPosition(mGridView.getFirstVisiblePosition() + offsetToLast);

				final View lastView = mGridView.getChildAt(offsetToLast);
				// mMotionViewOriginalTop = lastView.getTop();
				setMotionViewOriginalTop(lastView.getTop());

				// Don't fling more than 1 screen
				delta = Math.max(-(mGridView.getHeight() - mGridView.getPaddingBottom() - mGridView.getPaddingTop() - 1), delta);
			}

			// Check to see if we have bumped into the scroll limit
			View motionView = mGridView.getChildAt(getMotionPosition() - mGridView.getFirstVisiblePosition());
			int oldTop = 0;
			if (motionView != null) {
				oldTop = motionView.getTop();
			}

			// Don't stop just because delta is zero (it could have been
			// rounded)
			final boolean atEnd = trackMotionScroll(delta, delta) && (delta != 0);
			if (atEnd) {
//				if (motionView != null) {
//					// Tweak the scroll for how far we overshot
//					int overshoot = -(delta - (motionView.getTop() - oldTop));
//					// overScrollBy(0, overshoot, 0, mScrollY, 0, 0, 0,
//					// mOverflingDistance, false);
//					mFlingCallback.flingOverScrollBy(0, overshoot, 0, mFlingCallback.getScrollY(), 0, 0, 0, getOverflingDistance(), false);
//				}
//				if (more) {
//					edgeReached(delta);
//				}
				Log.w(TAG, "FlingManager.run at end");
				endFling();
				break;
			}

			if (more && !atEnd) {
				mGridView.invalidate();
				mLastFlingY = y;
				mGridView.post(this);
			} else {
				endFling();

				// if (PROFILE_FLINGING) {
				// if (mFlingProfilingStarted) {
				// Debug.stopMethodTracing();
				// mFlingProfilingStarted = false;
				// }
				//
				// if (mFlingStrictSpan != null) {
				// mFlingStrictSpan.finish();
				// mFlingStrictSpan = null;
				// }
				// }
			}
			break;
		}

		// case TOUCH_MODE_OVERFLING: {
		// final OverScroller scroller = mScroller;
		// if (scroller.computeScrollOffset()) {
		// final int scrollY = mScrollY;
		// final int currY = scroller.getCurrY();
		// final int deltaY = currY - scrollY;
		// if (overScrollBy(0, deltaY, 0, scrollY, 0, 0,
		// 0, mOverflingDistance, false)) {
		// final boolean crossDown = scrollY <= 0 && currY > 0;
		// final boolean crossUp = scrollY >= 0 && currY < 0;
		// if (crossDown || crossUp) {
		// int velocity = (int) scroller.getCurrVelocity();
		// if (crossUp) velocity = -velocity;
		//
		// // Don't flywheel from this; we're just continuing things.
		// scroller.abortAnimation();
		// start(velocity);
		// } else {
		// startSpringback();
		// }
		// } else {
		// invalidate();
		// post(this);
		// }
		// } else {
		// endFling();
		// }
		// break;
		// }
		}
	}

	boolean trackMotionScroll(int deltaY, int incrementalDeltaY) {
		LOG.i(TAG, DEBUG, "FlingRunnable trackMotionScroll");
		final int childCount = mGridView.getChildCount();
		if (childCount == 0) {
			return true;
		}
		
		View scaleChild = null;
		for(int index = 0; index < mGridView.getChildCount(); index++){
			scaleChild = mGridView.getChildAt(index);
			if(scaleChild.getScaleX() == 1.0f || scaleChild.getScaleY() == 1.0f){
				break;
			}
		}

		if(scaleChild == null){
			return true;
		}
		
		final int firstTop = scaleChild.getTop();
		final int lastBottom = mGridView.getChildAt(childCount - 1).getBottom();

		final Rect listPadding = getListPadding();

		// "effective padding" In this case is the amount of padding that
		// affects
		// how much space should not be filled by items. If we don't clip to
		// padding
		// there is no effective padding.
		int effectivePaddingTop = 0;
		int effectivePaddingBottom = 0;
		if ((getGroupFlags() & mFlingCallback.getClipToPaddingMask()) == mFlingCallback.getClipToPaddingMask()) {
			effectivePaddingTop = listPadding.top;
			effectivePaddingBottom = listPadding.bottom;
		}

		// FIXME account for grid vertical spacing too?
		final int spaceAbove = effectivePaddingTop - firstTop;
		final int end = mGridView.getHeight() - effectivePaddingBottom;
		final int spaceBelow = lastBottom - end;

		final int height = mGridView.getHeight() - mGridView.getPaddingBottom() - mGridView.getPaddingTop();
		if (deltaY < 0) {
			deltaY = Math.max(-(height - 1), deltaY);
		} else {
			deltaY = Math.min(height - 1, deltaY);
		}

		if (incrementalDeltaY < 0) {
			incrementalDeltaY = Math.max(-(height - 1), incrementalDeltaY);
		} else {
			incrementalDeltaY = Math.min(height - 1, incrementalDeltaY);
		}

		final int firstPosition = mGridView.getFirstVisiblePosition();

		// Update our guesses for where the first and last views are
		if (firstPosition == 0) {
			// mFirstPositionDistanceGuess = firstTop - listPadding.top;
			setFirstPositionDistanceGuess(firstTop - listPadding.top);
		} else {
			// mFirstPositionDistanceGuess += incrementalDeltaY;
			setFirstPositionDistanceGuess(incrementalDeltaY);
		}
		if (firstPosition + childCount == mGridView.getAdapter().getCount()) {
			// mLastPositionDistanceGuess = lastBottom + listPadding.bottom;
			setFirstPositionDistanceGuess(lastBottom + listPadding.bottom);
		} else {
			// mLastPositionDistanceGuess += incrementalDeltaY;
			setFirstPositionDistanceGuess(incrementalDeltaY);
		}

//		Log.w(TAG, "trackMotionScroll firstTop = " + firstTop + ", listPadding.top = " + listPadding.top + ", incrementalDeltaY = "
//				+ incrementalDeltaY);
		final boolean cannotScrollDown = (firstPosition == 0 && firstTop >= listPadding.top && incrementalDeltaY >= 0);
		final boolean cannotScrollUp = (firstPosition + childCount == mGridView.getAdapter().getCount()
				&& lastBottom <= mGridView.getHeight() - listPadding.bottom && incrementalDeltaY <= 0);

		if (cannotScrollDown || cannotScrollUp) {
			Log.w(TAG, "trackMotionScroll cannotScrollDown = " + cannotScrollDown + ", cannotScrollUp = " + cannotScrollUp);
			return incrementalDeltaY != 0;
		}

		final boolean down = incrementalDeltaY < 0;

		final boolean inTouchMode = mGridView.isInTouchMode();
		if (inTouchMode) {
			focusedHideSelector();
		}

		final int headerViewsCount = 0;
		final int footerViewsStart = mGridView.getAdapter().getCount() - 0;

		int start = 0;
		int count = 0;

		if (down) {
			int top = -incrementalDeltaY;
			if ((getGroupFlags() & mFlingCallback.getClipToPaddingMask()) == mFlingCallback.getClipToPaddingMask()) {
				top += listPadding.top;
			}
			for (int i = 0; i < childCount; i++) {
				final View child = mGridView.getChildAt(i);
				if (child.getBottom() >= top) {
					break;
				} else {
					count++;
					int position = firstPosition + i;
					if (position >= headerViewsCount && position < footerViewsStart) {
						// mRecycler.addScrapView(child, position);
						focusedAddScrapView(child, position);
						if (ViewDebug.TRACE_RECYCLER) {
							ViewDebug.trace(child, ViewDebug.RecyclerTraceType.MOVE_TO_SCRAP_HEAP, firstPosition + i, -1);
						}
					}
				}
			}
		} else {
			int bottom = mGridView.getHeight() - incrementalDeltaY;
			if ((getGroupFlags() & mFlingCallback.getClipToPaddingMask()) == mFlingCallback.getClipToPaddingMask()) {
				bottom -= listPadding.bottom;
			}
			for (int i = childCount - 1; i >= 0; i--) {
				final View child = mGridView.getChildAt(i);
				if (child.getTop() <= bottom) {
					break;
				} else {
					start = i;
					count++;
					int position = firstPosition + i;
					if (position >= headerViewsCount && position < footerViewsStart) {
						// mRecycler.addScrapView(child, position);
						focusedAddScrapView(child, position);
						if (ViewDebug.TRACE_RECYCLER) {
							ViewDebug.trace(child, ViewDebug.RecyclerTraceType.MOVE_TO_SCRAP_HEAP, firstPosition + i, -1);
						}
					}
				}
			}
		}

		// mMotionViewNewTop = mMotionViewOriginalTop + deltaY;
		setMotionViewNewTop(getMotionViewOriginalTop() + deltaY);

		// mBlockLayoutRequests = true;
		setBlockLayoutRequests(true);

		if (count > 0) {
			mFlingCallback.flingDetachViewsFromParent(start, count);
		}
		focusedOffsetChildrenTopAndBottom(incrementalDeltaY);

		if (down) {
			// mFirstPosition += count;
			setFirstPosition(mGridView.getFirstVisiblePosition() + count);
		}

		mGridView.invalidate();

		final int absIncrementalDeltaY = Math.abs(incrementalDeltaY);
		if (spaceAbove < absIncrementalDeltaY || spaceBelow < absIncrementalDeltaY) {
			focusedFillGap(down);
		}

		if (!inTouchMode && getSelectedPosition() != GridView.INVALID_POSITION) {
			final int childIndex = getSelectedPosition() - mGridView.getFirstVisiblePosition();
			if (childIndex >= 0 && childIndex < mGridView.getChildCount()) {
				focusedPositionSelector(getSelectedPosition(), mGridView.getChildAt(childIndex));
			}
		} else if (getSelectorPosition() != GridView.INVALID_POSITION) {
			final int childIndex = getSelectorPosition() - mGridView.getFirstVisiblePosition();
			if (childIndex >= 0 && childIndex < mGridView.getChildCount()) {
				focusedPositionSelector(GridView.INVALID_POSITION, mGridView.getChildAt(childIndex));
			}
		} else {
			// mSelectorRect.setEmpty();
			setSelectorRectEmpty();
		}

		// mBlockLayoutRequests = false;
		setBlockLayoutRequests(false);

		focusedInvokeOnItemScrollListener();
		mFlingCallback.flingAwakenScrollBars();

		return false;
	}

	Rect getListPadding() {
		try {
			Class<?> c = Class.forName("android.widget.AbsListView");
			Field listPadding = c.getDeclaredField("mListPadding");
			listPadding.setAccessible(true);
			return (Rect) listPadding.get(this.mGridView);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	int getGroupFlags() {
		try {
			Class<?> c = Class.forName("android.view.ViewGroup");
			Field flags = c.getDeclaredField("mGroupFlags");
			flags.setAccessible(true);
			return flags.getInt(this.mGridView);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	void focusedHideSelector() {
		try {
			Class<?> c = Class.forName("android.widget.AbsListView");
			Method hideSelector = c.getDeclaredMethod("hideSelector");
			hideSelector.setAccessible(true);
			hideSelector.invoke(this.mGridView);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void focusedAddScrapView(View scrap, int position) {
		try {
			Class<?> c = Class.forName("android.widget.AbsListView");
			Field mRecycler = c.getDeclaredField("mRecycler");
			mRecycler.setAccessible(true);
			Object objRecyler = mRecycler.get(this.mGridView);

			Class<?> classRecycleBin = Class.forName("android.widget.AbsListView$RecycleBin");
			Method addScrapView = classRecycleBin.getDeclaredMethod("addScrapView", new Class[] { View.class, int.class });
			addScrapView.setAccessible(true);
			addScrapView.invoke(objRecyler, scrap, position);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void setFirstPositionDistanceGuess(int distance) {
		try {
			Class<?> c = Class.forName("android.widget.AbsListView");
			Field mFirstPositionDistanceGuess = c.getDeclaredField("mFirstPositionDistanceGuess");
			mFirstPositionDistanceGuess.setAccessible(true);
			mFirstPositionDistanceGuess.setInt(this.mGridView, distance);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	int getMotionViewOriginalTop() {
		try {
			Class<?> c = Class.forName("android.widget.AbsListView");
			Field mMotionViewOriginalTop = c.getDeclaredField("mMotionViewOriginalTop");
			mMotionViewOriginalTop.setAccessible(true);
			return mMotionViewOriginalTop.getInt(this.mGridView);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	void setMotionViewOriginalTop(int top) {
		try {
			Class<?> c = Class.forName("android.widget.AbsListView");
			Field mMotionViewOriginalTop = c.getDeclaredField("mMotionViewOriginalTop");
			mMotionViewOriginalTop.setAccessible(true);
			mMotionViewOriginalTop.setInt(this.mGridView, top);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void setMotionViewNewTop(int top) {
		try {
			Class<?> c = Class.forName("android.widget.AbsListView");
			Field mMotionViewNewTop = c.getDeclaredField("mMotionViewNewTop");
			mMotionViewNewTop.setAccessible(true);
			mMotionViewNewTop.setInt(this.mGridView, top);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void setBlockLayoutRequests(boolean isBlock) {
		try {
			Class<?> c = Class.forName("android.widget.AdapterView");
			Field mBlockLayoutRequests = c.getDeclaredField("mBlockLayoutRequests");
			mBlockLayoutRequests.setAccessible(true);
			mBlockLayoutRequests.setBoolean(this.mGridView, isBlock);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void focusedOffsetChildrenTopAndBottom(int offset) {
		try {
			Class<?> c = Class.forName("android.view.ViewGroup");
			Method offsetChildrenTopAndBottom = c.getDeclaredMethod("offsetChildrenTopAndBottom", new Class[] { int.class });
			offsetChildrenTopAndBottom.setAccessible(true);
			offsetChildrenTopAndBottom.invoke(this.mGridView, offset);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void setFirstPosition(int position) {
		try {
			Class<?> c = Class.forName("android.widget.AdapterView");
			Field mFirstPosition = c.getDeclaredField("mFirstPosition");
			mFirstPosition.setAccessible(true);
			mFirstPosition.setInt(this.mGridView, position);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void focusedFillGap(boolean down) {
		try {
			Class<?> c = Class.forName("android.widget.GridView");
			Method fillGap = c.getDeclaredMethod("fillGap", new Class[] { boolean.class });
			fillGap.setAccessible(true);
			fillGap.invoke(this.mGridView, down);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	int getSelectedPosition() {
		try {
			Class<?> c = Class.forName("android.widget.AdapterView");
			Field mSelectedPosition = c.getDeclaredField("mSelectedPosition");
			mSelectedPosition.setAccessible(true);
			mSelectedPosition.getInt(this.mGridView);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	int getSelectorPosition() {
		try {
			Class<?> c = Class.forName("android.widget.AbsListView");
			Field mSelectorPosition = c.getDeclaredField("mSelectorPosition");
			mSelectorPosition.setAccessible(true);
			mSelectorPosition.getInt(this.mGridView);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	void focusedPositionSelector(int position, View sel) {
		try {
			Class<?> c = Class.forName("android.widget.AbsListView");
			Method positionSelector = c.getDeclaredMethod("positionSelector", new Class[] { int.class, View.class });
			positionSelector.setAccessible(true);
			positionSelector.invoke(this.mGridView, position, sel);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void setSelectorRectEmpty() {
		try {
			Class<?> c = Class.forName("android.widget.AbsListView");
			Field mSelectorRect = c.getDeclaredField("mSelectorRect");
			mSelectorRect.setAccessible(true);

			Class<?> rect = Class.forName("android.graphics.Rect");
			Method setEmpty = rect.getDeclaredMethod("setEmpty");
			setEmpty.invoke(this.mGridView);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void focusedInvokeOnItemScrollListener() {
		try {
			Class<?> c = Class.forName("android.widget.AbsListView");
			Method invokeOnItemScrollListener = c.getDeclaredMethod("invokeOnItemScrollListener");
			invokeOnItemScrollListener.setAccessible(true);
			invokeOnItemScrollListener.invoke(this.mGridView);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void setTouchMode(int mode) {
		try {
			Class<?> c = Class.forName("android.widget.AbsListView");
			Field mTouchMode = c.getDeclaredField("mTouchMode");
			mTouchMode.setAccessible(true);
			Object obj = mTouchMode.get(this.mGridView);
			mTouchMode.setInt(this.mGridView, mode);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	int getTouchMode() {
		try {
			Class<?> c = Class.forName("android.widget.AbsListView");
			Field mTouchMode = c.getDeclaredField("mTouchMode");
			mTouchMode.setAccessible(true);
			return mTouchMode.getInt(this.mGridView);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	boolean getDataChanged() {
		try {
			Class<?> c = Class.forName("android.widget.AdapterView");
			Field mDataChanged = c.getDeclaredField("mDataChanged");
			mDataChanged.setAccessible(true);
			return mDataChanged.getBoolean(this.mGridView);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	int getMotionPosition() {
		try {
			Class<?> c = Class.forName("android.widget.AbsListView");
			Field mMotionPosition = c.getDeclaredField("mMotionPosition");
			mMotionPosition.setAccessible(true);
			return mMotionPosition.getInt(this.mGridView);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	void setMotionPosition(int pos) {
		try {
			Class<?> c = Class.forName("android.widget.AbsListView");
			Field mMotionPosition = c.getDeclaredField("mMotionPosition");
			mMotionPosition.setAccessible(true);
			mMotionPosition.setInt(this.mGridView, pos);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	int getOverflingDistance() {
		try {
			Class<?> c = Class.forName("android.widget.AbsListView");
			Field mOverflingDistance = c.getDeclaredField("mOverflingDistance");
			mOverflingDistance.setAccessible(true);
			return mOverflingDistance.getInt(this.mGridView);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	void edgeReached(int delta) {
		try {
			Class<?> c = Class.forName("android.widget.AbsListView");
			Method edgeReached = c.getDeclaredMethod("edgeReached");
			edgeReached.setAccessible(true);
			edgeReached.invoke(this.mGridView);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void focusedReportScrollStateChange(int newState) {
		try {
			Class<?> c = Class.forName("android.widget.AbsListView");
			Method reportScrollStateChange = c.getDeclaredMethod("reportScrollStateChange", new Class[] { int.class });
			reportScrollStateChange.setAccessible(true);
			reportScrollStateChange.invoke(this.mGridView, newState);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			int a;
			a = 2;
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void focusedClearScrollingCache() {
		try {
			Class<?> c = Class.forName("android.widget.AbsListView");
			Method clearScrollingCache = c.getDeclaredMethod("clearScrollingCache");
			clearScrollingCache.setAccessible(true);
			clearScrollingCache.invoke(this.mGridView);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void finishFlingStrictSpan() {
		try {
			Class<?> c = Class.forName("android.widget.AbsListView");
			Field mFlingStrictSpan = c.getDeclaredField("mFlingStrictSpan");
			mFlingStrictSpan.setAccessible(true);
			Object objpan = mFlingStrictSpan.get(this.mGridView);
			if (null == objpan) {
				return;
			}

			Class<?> classPan = Class.forName("android.os.StrictMode$Span");
			Method finish = classPan.getDeclaredMethod("finish");
			finish.invoke(this.mGridView);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void focusedSetNextSelectedPositionInt(int pos) {
		try {
			Class<?> c = Class.forName("android.widget.AdapterView");
			Method setNextSelectedPositionInt = c.getDeclaredMethod("setNextSelectedPositionInt", new Class[] { int.class });
			setNextSelectedPositionInt.setAccessible(true);
			setNextSelectedPositionInt.invoke(this.mGridView, pos);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}