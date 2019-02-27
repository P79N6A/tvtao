package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;

public class FocusFlingLinearLayout extends FocusLinearLayout {
	protected static final String TAG = "FocusScrollerLinearLayout";
	protected static final boolean DEBUG = true;

	public FocusFlingLinearLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mScroller = new Scroller(context);
	}

	public FocusFlingLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mScroller = new Scroller(context);
	}

	public FocusFlingLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mScroller = new Scroller(context);
	}

	public static final int MIN_VALUE = 0x80000000;
	public static final int MAX_VALUE = 0x7fffffff;

	private static final int SCROLL_DURATION = 100;

	public static final int HORIZONTAL_INVALID = -1;

	public static final int HORIZONTAL_SINGEL = 1;

	public static final int HORIZONTAL_FULL = 2;

	public static final int HORIZONTAL_OUTSIDE_SINGEL = 3;

	public static final int HORIZONTAL_OUTSIDE_FULL = 4;

	private int mScrollMode = HORIZONTAL_SINGEL;

	private OnScrollListener mScrollerListener = null;

	private int mMinLeft;
	private int mMinScaledLeft;
	private int mMaxLeft;
	private int mMaxScaledLeft;

	private int mMinRight;
	private int mMinScaledRight;
	private int mMaxRight;
	private int mMaxScaledRight;

	private int mMinTop;
	private int mMinScaledTop;
	private int mMaxTop;
	private int mMaxScaledTop;

	private int mMinBottom;
	private int mMinScaledBottom;
	private int mMaxBottom;
	private int mMaxScaledBottom;

	View mMinLeftView = null;
	View mMaxLeftView = null;

	View mMinRightView = null;
	View mMaxRightView = null;

	View mMinTopView = null;
	View maxTopView = null;

	View minBottomView = null;
	View maxBottomView = null;

	private OutsideScrollListener mOutsideScrollListener = null;

	private Scroller mScroller;

	private int mLastScrollState = OnScrollListener.SCROLL_STATE_IDLE;

	private int mManualPaddingRight = 20;
	private int mManualPaddingBottom = 20;

	private int mCenterX = 640;
	private int mCenterY = 360;
	private int mLastHorizontalDirection = 0;
	private int mLastVerticalDirection = 0;
	private int mDuration = 200;

	private int mScrollX = 0;
	private int mScrollY = 0;
	protected int mScrollDistance = 0;

	private FlingRunnable mFlingRunnable = new FlingRunnable();

	public void setOnScrollListener(OnScrollListener l) {
		mScrollerListener = l;
	}

	public void setHorizontalMode(int mode) {
		mScrollMode = mode;
	}

	public void setOutsideScrollListener(OutsideScrollListener l) {
		this.mOutsideScrollListener = l;
	}

	public void setManualPaddingRight(int padding) {
		mManualPaddingRight = padding;
	}

	public void setManualPaddingBottom(int padding) {
		mManualPaddingBottom = padding;
	}

	public void setCenter(int centerX, int centerY) {
		mCenterX = centerX;
		mCenterY = centerY;
	}

	public void setScrollDuration(int duration) {
		mDuration = duration;
	}

	@Override
	protected void initNode() {
		if (mNeedInitNode) {
			if (getChildCount() <= 0) {
				return;
			}

			this.mMinLeft = MAX_VALUE;
			this.mMaxLeft = MIN_VALUE;

			this.mMinRight = MAX_VALUE;
			this.mMaxRight = MIN_VALUE;

			this.mMinTop = MAX_VALUE;
			this.mMaxTop = MIN_VALUE;

			this.mMinBottom = MAX_VALUE;
			this.mMaxBottom = MIN_VALUE;

			for (int index = 0; index < this.getChildCount(); index++) {
				View child = this.getChildAt(index);
				if (!child.isFocusable() || child.getVisibility() == GONE) {
					continue;
				}

				// for left
				if (child.getLeft() < this.mMinLeft) {
					mMinLeftView = child;
					this.mMinLeft = child.getLeft();
				}

				if (child.getLeft() > this.mMaxLeft) {
					mMaxLeftView = child;
					this.mMaxLeft = child.getLeft();
				}

				// for right
				if (child.getRight() < this.mMinRight) {
					mMinRightView = child;
					this.mMinRight = child.getRight();
				}

				if (child.getRight() > this.mMaxRight) {
					mMaxRightView = child;
					this.mMaxRight = child.getRight();
				}

				// for top
				if (child.getTop() < this.mMinTop) {
					mMinTopView = child;
					this.mMinTop = child.getTop();
				}

				if (child.getTop() > this.mMaxTop) {
					maxTopView = child;
					this.mMaxTop = child.getTop();
				}

				// for bottom
				if (child.getBottom() < this.mMinBottom) {
					minBottomView = child;
					this.mMinBottom = child.getBottom();
				}

				if (child.getBottom() > this.mMaxBottom) {
					maxBottomView = child;
					this.mMaxBottom = child.getBottom();
				}
			}

			if (DEBUG) {
				Log.d(TAG, "init: mMinLeft = " + mMinLeft + ", mMaxLeft = " + mMaxLeft + ", mMinRight = " + mMinRight + ", mMaxRight = " + mMaxRight + ", mMinTop = " + mMinTop + ", mMaxTop = "
						+ mMaxTop + ", mMinBottom = " + mMinBottom + ", mMaxBottom = " + mMaxBottom);

				Log.d(TAG, "init: minLeftView = " + mMinLeftView + ", maxLeftView = " + mMaxLeftView + ", minRightView = " + mMinRightView + ", maxRightView = " + mMaxRightView + ", minTopView = "
						+ mMinTopView + ", maxTopView = " + maxTopView + ", minBottomView = " + minBottomView + ", maxBottomView = " + maxBottomView);
			}

			if (this.getChildCount() > 0) {
				Rect r = new Rect();
				{
					ItemListener item = (ItemListener) mMinLeftView;
					FocusRectParams focusParams = item.getFocusParams();
					r.set(focusParams.focusRect());
					offsetDescendantRectToMyCoords(mMinLeftView, r);
					this.mMinScaledLeft = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(), focusParams.coefX(),
							focusParams.coefY()).left;
				}

				{
					ItemListener item = (ItemListener) mMaxLeftView;
					FocusRectParams focusParams = item.getFocusParams();
					r.set(focusParams.focusRect());
					offsetDescendantRectToMyCoords(mMaxLeftView, r);
					this.mMaxScaledLeft = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(), focusParams.coefX(),
							focusParams.coefY()).left;
				}

				{
					ItemListener item = (ItemListener) mMinRightView;
					FocusRectParams focusParams = item.getFocusParams();
					r.set(focusParams.focusRect());
					offsetDescendantRectToMyCoords(mMinRightView, r);
					this.mMinScaledRight = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(), focusParams.coefX(),
							focusParams.coefY()).right;
				}

				{
					ItemListener item = (ItemListener) mMaxRightView;
					FocusRectParams focusParams = item.getFocusParams();
					r.set(focusParams.focusRect());
					offsetDescendantRectToMyCoords(mMaxRightView, r);
					this.mMaxScaledRight = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(), focusParams.coefX(),
							focusParams.coefY()).right;
				}

				{
					ItemListener item = (ItemListener) mMinTopView;
					FocusRectParams focusParams = item.getFocusParams();
					r.set(focusParams.focusRect());
					offsetDescendantRectToMyCoords(mMinTopView, r);
					this.mMinScaledTop = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(), focusParams.coefX(),
							focusParams.coefY()).top;
				}

				{
					ItemListener item = (ItemListener) maxTopView;
					FocusRectParams focusParams = item.getFocusParams();
					r.set(focusParams.focusRect());
					offsetDescendantRectToMyCoords(maxTopView, r);
					this.mMaxScaledTop = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(), focusParams.coefX(),
							focusParams.coefY()).top;
				}

				{
					ItemListener item = (ItemListener) minBottomView;
					FocusRectParams focusParams = item.getFocusParams();
					r.set(focusParams.focusRect());
					offsetDescendantRectToMyCoords(minBottomView, r);
					this.mMinScaledBottom = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(), focusParams.coefX(),
							focusParams.coefY()).bottom;
				}

				{
					ItemListener item = (ItemListener) maxBottomView;
					FocusRectParams focusParams = item.getFocusParams();
					r.set(focusParams.focusRect());
					offsetDescendantRectToMyCoords(maxBottomView, r);
					this.mMaxScaledBottom = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(), focusParams.coefX(),
							focusParams.coefY()).bottom;
				}

				mMaxRight -= mScrollX;
				mMaxBottom -= mScrollY;
			}
		}

		super.initNode();
	}

	public void setSelectedView(View v, int keyCode) {
		if (!this.mNodeMap.containsKey(v)) {
			throw new IllegalArgumentException("Parent does't contain this view");
		}
		
		View selectedView = getSelectedView();
		if (v == null || selectedView == null) {
			return;
		}
		
		OnFocusChangeListener listener = selectedView.getOnFocusChangeListener();
		if (selectedView instanceof DeepListener) {
			DeepListener deep = (DeepListener) selectedView;
			if (deep.canDeep() && deep.hasDeepFocus()) {
				deep.onFocusDeeped(false, 0, null);
			} else {
				listener.onFocusChange(selectedView, false);
			}
		} else {
			listener.onFocusChange(selectedView, false);
		}

		mIndex = mNodeMap.get(v).index;

		listener = v.getOnFocusChangeListener();
		if (v instanceof DeepListener) {
			mDeep = (DeepListener) v;
			if (!mDeep.canDeep()) {
				mDeep = null;
				listener.onFocusChange(v, true);
			} else {
				mDeep.onFocusDeeped(true, 0, null);
			}
		} else {
			listener.onFocusChange(v, true);
		}
		
		reset() ;

		scrollSingel(keyCode);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean hr = super.onKeyDown(keyCode, event);
		if (hr) {
			scrollSingel(keyCode);
		}

		return hr;
	}

	// @Override
	// public void computeScroll() {
	// if (this.mScrollMode == HORIZONTAL_FULL || this.mScrollMode ==
	// HORIZONTAL_SINGEL) {
	// if (mScroller.computeScrollOffset()) {
	// int offsetX = mScroller.getCurrX() - mScrollX;
	// int offsetY = mScroller.getCurrY() - mScrollY;
	//
	// mScrollX = mScroller.getCurrX();
	// mScrollY = mScroller.getCurrY();
	//
	// mFocusRectparams.focusRect().offset(offsetX, offsetY);
	// scrollTo(mScrollX, mScrollY);
	// invalidate();
	// }
	//
	// if (mScroller.isFinished()) {
	// reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
	// }
	// }
	// super.computeScroll();
	// }

	public void smoothScrollBy(int dx, int dy, int duration) {
		if (dx == 0 && dy == 0) {
			return;
		}

		mScrollDistance = dx;
		// Log.w(TAG, "smoothScrollBy dx = " + dx + ", duration = " + duration);
		// mScrollX = mScroller.getCurrX();
		// mScrollY = mScroller.getCurrY();
		// if (!mScroller.isFinished()) {
		// mScroller.forceFinished(true);
		// }
		// mScroller.startScroll(mScrollX, mScrollY, dx, dy, duration);
		// reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);

		mFlingRunnable.startUsingDistance(dx, dy, duration);
	}

	protected void reportScrollStateChange(int newState) {
		if (newState != mLastScrollState) {
			if (mScrollerListener != null) {
				mLastScrollState = newState;
				mScrollerListener.onScrollStateChanged(this, newState);
			}
		}
	}

	void scrollSingel(int keyCode) {
		View selectedView = getSelectedView();

		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			int diffX = getOffset(keyCode);
			int diffY = 0;
			if (mLastVerticalDirection == KeyEvent.KEYCODE_DPAD_DOWN || mLastVerticalDirection == KeyEvent.KEYCODE_DPAD_UP) {
				diffY = getOffset(mLastVerticalDirection);
			}

			smoothScrollBy(diffX, diffY, mDuration);
			mFocusRectparams.focusRect().offset(-diffX, -diffY);

			mLastHorizontalDirection = keyCode;

		} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			int diffX = getOffset(keyCode);
			int diffY = 0;
			if (mLastVerticalDirection == KeyEvent.KEYCODE_DPAD_DOWN || mLastVerticalDirection == KeyEvent.KEYCODE_DPAD_UP) {
				diffY = getOffset(mLastVerticalDirection);
			}

			smoothScrollBy(diffX, diffY, mDuration);
			mFocusRectparams.focusRect().offset(-diffX, -diffY);

			mLastHorizontalDirection = keyCode;

		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			int diffY = getOffset(keyCode);
			int diffX = 0;
			if (mLastHorizontalDirection == KeyEvent.KEYCODE_DPAD_LEFT || mLastHorizontalDirection == KeyEvent.KEYCODE_DPAD_RIGHT) {
				diffX = getOffset(mLastHorizontalDirection);
			}

			smoothScrollBy(diffX, diffY, mDuration);

			mFocusRectparams.focusRect().offset(-diffX, -diffY);

			mLastVerticalDirection = keyCode;

		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			int diffY = getOffset(keyCode);
			int diffX = 0;
			if (mLastHorizontalDirection == KeyEvent.KEYCODE_DPAD_LEFT || mLastHorizontalDirection == KeyEvent.KEYCODE_DPAD_RIGHT) {
				diffX = getOffset(mLastHorizontalDirection);
			}

			smoothScrollBy(diffX, diffY, mDuration);

			mFocusRectparams.focusRect().offset(-diffX, -diffY);

			mLastVerticalDirection = keyCode;
		}
	}

	private int getOffset(int keyCode) {
		View selectedView = getSelectedView();
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_RIGHT: {
			int centerX = mCenterX;
			int selectCenterX = getCenterX(selectedView);
			int diff = selectCenterX - centerX;
			Rect visibleRect = new Rect();
			getGlobalVisibleRect(visibleRect);
			int currRightBind = visibleRect.width();
			if (diff > 0) {
				if (currRightBind + diff <= mMaxRight + mManualPaddingRight + mScrollX) {
				} else {
					diff -= ((currRightBind + diff) - (mMaxRight + mManualPaddingRight + mScrollX));
				}

				if (diff > 0) {
					return diff;
				}

			}
			return 0;
		}
		case KeyEvent.KEYCODE_DPAD_LEFT: {
			int centerX = mCenterX;
			int selectCenterX = getCenterX(selectedView);
			int diff = centerX - selectCenterX;
			int currLeftBind = mMinLeft - mScrollX;
			if (diff > 0) {
				if (currLeftBind - diff >= mMinLeft) {
				} else {
					diff -= (mMinLeft - (currLeftBind - diff));
				}

				if (diff > 0) {
					return -diff;
				}

			}

			return 0;
		}
		case KeyEvent.KEYCODE_DPAD_DOWN: {
			int centerY = mCenterY;
			int selectCenterY = getCenterY(selectedView);
			int diff = selectCenterY - centerY;
			int currBottomBind = 0;
			Rect visibleRect = new Rect();
			getGlobalVisibleRect(visibleRect);
			int vheight = visibleRect.height();
			currBottomBind = vheight;
			if (diff > 0) {
				if (currBottomBind + diff <= mMaxBottom + mManualPaddingBottom + mScrollY) {
				} else {
					diff -= ((currBottomBind + diff) - (mMaxBottom + mManualPaddingBottom + mScrollY));
				}

				if (diff > 0) {
					return diff;
				}
			}

			return 0;
		}
		case KeyEvent.KEYCODE_DPAD_UP: {
			int centerY = mCenterY;
			int selectCenterY = getCenterY(selectedView);
			int diff = centerY - selectCenterY;
			int currTopBind = mMinTop - mScrollY;
			if (diff > 0) {
				if (currTopBind - diff >= mMinTop) {
				} else {
					diff -= (mMinTop - (currTopBind - diff));
				}

				if (diff > 0) {
					return -diff;
				}
			}

			return 0;
		}
		}

		// throw new
		// IllegalArgumentException("FocusScrollerRelativeLayout: getOffset: direction must be FOCUS_LEFT, FOCUS_RIGHT, FOCUS_UP, or FOCUS_DOWN, but it is "
		// + direction);
		return 0;
	}

	private int getCenterX(View v) {
		int selectCenterX = 0;
		if ((mFocusRectparams.centerMode() & FocusRectParams.CENTER_X_FOCUS) == FocusRectParams.CENTER_X_FOCUS) {
			selectCenterX = (mFocusRectparams.focusRect().left + mFocusRectparams.focusRect().right) / 2;
		} else if ((mFocusRectparams.centerMode() & FocusRectParams.CENTER_X) == FocusRectParams.CENTER_X) {
			selectCenterX = (v.getLeft() + v.getRight()) / 2;
		} else {
			throw new IllegalArgumentException("FocusScrollerRelativeLayout: getCenterX: mFocusRectparams.centerMode() = " + mFocusRectparams.centerMode());
		}

		return selectCenterX;
	}

	private int getCenterY(View v) {
		int selectCenterY = 0;
		if ((mFocusRectparams.centerMode() & FocusRectParams.CENTER_Y_FOCUS) == FocusRectParams.CENTER_Y_FOCUS) {
			selectCenterY = (mFocusRectparams.focusRect().top + mFocusRectparams.focusRect().bottom) / 2;
		} else if ((mFocusRectparams.centerMode() & FocusRectParams.CENTER_Y) == FocusRectParams.CENTER_Y) {
			selectCenterY = (v.getTop() + v.getBottom()) / 2;
		} else {
			throw new IllegalArgumentException("FocusScrollerRelativeLayout: getCenterY: mFocusRectparams.centerMode() = " + mFocusRectparams.centerMode());
		}

		return selectCenterY;
	}

	public interface OutsideScrollListener {
		public int getCurrX();

		public int getCurrY();

		public void smoothOutsideScrollBy(int distance, int duration);
	}

	void trackMotionScroll(int deltaX, int deltaY) {
		mScrollX += deltaX;
		mScrollY += deltaY;
		for (int index = 0; index < getChildCount(); index++) {
			View child = getChildAt(index);
			if (deltaX != 0) {
				child.offsetLeftAndRight(deltaX);
			}

			if (deltaY != 0) {
				child.offsetTopAndBottom(deltaY);
			}
		}
	}
	
	@Override
	public boolean isScrolling() {
		// TODO Auto-generated method stub
		if (mDeep != null){
			return mDeep.isScrolling()|| !mScroller.isFinished();
		}
		return !mScroller.isFinished();
	}

	private class FlingRunnable implements Runnable {

		private Scroller mScroller;

		/**
		 * X value reported by mScroller on the previous fling
		 */
		private int mLastFlingX;
		private int mLastFlingY;

		public FlingRunnable() {
			mScroller = new Scroller(getContext(), new DecelerateInterpolator(0.6f));
		}

		public void startUsingDistance(int distanceX, int distanceY, int duration) {
			if (distanceX == 0 && distanceY == 0)
				return;

			if (getChildCount() <= 0) {
				return;
			}

			if (!isFinished()) {
				endFling(true);
			}

			reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
			mScroller.startScroll(0, 0, distanceX, distanceY, duration);

			mLastFlingX = 0;
			mLastFlingY = 0;

			post(this);
		}

		private void endFling(boolean scrollIntoSlots) {
			/*
			 * Force the scroller's status to finished (without setting its
			 * position to the end)
			 */
			reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
			mScroller.forceFinished(true);
		}

		@Override
		public void run() {
			boolean more = mScroller.computeScrollOffset();
			final int x = mScroller.getCurrX();
			final int y = mScroller.getCurrY();
			int deltaX = mLastFlingX - x;
			int deltaY = mLastFlingY - y;

			trackMotionScroll(deltaX, deltaY);

			if (more) {
				mLastFlingX = x;
				mLastFlingY = y;
				invalidate();
				post(this);
			} else {
				endFling(true);
			}

		}

	}
}
