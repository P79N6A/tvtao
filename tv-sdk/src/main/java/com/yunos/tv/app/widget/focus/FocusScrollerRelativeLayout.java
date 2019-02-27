package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.lib.SystemProUtils;

public class FocusScrollerRelativeLayout extends FocusRelativeLayout {

	protected static final String TAG = "FocusScrollerRelativeLayout";
	protected static final boolean DEBUG = false;

	public FocusScrollerRelativeLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mScroller = new Scroller(context);
	}

	public FocusScrollerRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mScroller = new Scroller(context);
	}

	public FocusScrollerRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
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
	private int mDuration = 500;

	private int mScrollX = 0;
	private int mScrollY = 0;

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
				if (!child.isFocusable() || child.getVisibility() != View.VISIBLE) {
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

            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)getLayoutParams();
            if(lp != null){
                int leftMargin = lp.leftMargin;
                int topMargin = lp.topMargin;
                int rightMargin = lp.rightMargin;
                int bottomMargin = lp.bottomMargin;

                this.mMinLeft += leftMargin - rightMargin;
                this.mMaxLeft += leftMargin - rightMargin;

                this.mMinRight += leftMargin - rightMargin;
                this.mMaxRight += leftMargin - rightMargin;

                this.mMinTop += topMargin - bottomMargin;
                this.mMaxTop += topMargin - bottomMargin;

                this.mMinBottom += topMargin - bottomMargin;
                this.mMaxBottom += topMargin - bottomMargin;
            }

			if (DEBUG) {
				Log.d(TAG, "init: mMinLeft = " + mMinLeft + ", mMaxLeft = " + mMaxLeft + ", mMinRight = " + mMinRight + ", mMaxRight = " + mMaxRight + ", mMinTop = " + mMinTop + ", mMaxTop = "
						+ mMaxTop + ", mMinBottom = " + mMinBottom + ", mMaxBottom = " + mMaxBottom);

				Log.d(TAG, "init: minLeftView = " + mMinLeftView + ", maxLeftView = " + mMaxLeftView + ", minRightView = " + mMinRightView + ", maxRightView = " + mMaxRightView + ", minTopView = "
						+ mMinTopView + ", maxTopView = " + maxTopView + ", minBottomView = " + minBottomView + ", maxBottomView = " + maxBottomView);
			}

			if (this.getChildCount() > 0) {
				Rect r = new Rect();
                FocusRectParams focusParams = null;
                {
                    if(mMinLeftView != null){
                        focusParams = getScaleRectParams((ItemListener) mMinLeftView, r);
                    }
                    if(focusParams == null){
                        focusParams = getFocusParams();
                        getFocusedRect(r);
                    }
					this.mMinScaledLeft = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(), focusParams.coefX(),
							focusParams.coefY()).left;
				}

				{
                    if(mMaxLeftView != null){
                        focusParams = getScaleRectParams((ItemListener) mMaxLeftView, r);
                    }
                    if(focusParams == null){
                        focusParams = getFocusParams();
                        getFocusedRect(r);
                    }
					this.mMaxScaledLeft = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(), focusParams.coefX(),
							focusParams.coefY()).left;
				}

				{
                    if(mMinRightView != null){
                        focusParams = getScaleRectParams((ItemListener) mMinRightView, r);
                    }
                    if(focusParams == null){
                        focusParams = getFocusParams();
                        getFocusedRect(r);
                    }
					this.mMinScaledRight = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(), focusParams.coefX(),
							focusParams.coefY()).right;
				}

				{
                    if(mMaxRightView != null){
                        focusParams = getScaleRectParams((ItemListener) mMaxRightView, r);
                    }
                    if(focusParams == null){
                        focusParams = getFocusParams();
                        getFocusedRect(r);
                    }
					this.mMaxScaledRight = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(), focusParams.coefX(),
							focusParams.coefY()).right;
				}

				{
                    if(mMinTopView != null){
                        focusParams = getScaleRectParams((ItemListener) mMinTopView, r);
                    }
                    if(focusParams == null){
                        focusParams = getFocusParams();
                        getFocusedRect(r);
                    }
					this.mMinScaledTop = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(), focusParams.coefX(),
							focusParams.coefY()).top;
				}

				{
                    if(maxTopView != null){
                        focusParams = getScaleRectParams((ItemListener) maxTopView, r);
                    }
                    if(focusParams == null){
                        focusParams = getFocusParams();
                        getFocusedRect(r);
                    }
					this.mMaxScaledTop = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(), focusParams.coefX(),
							focusParams.coefY()).top;
				}

				{
                    if(minBottomView != null){
                        focusParams = getScaleRectParams((ItemListener) minBottomView, r);
                    }
                    if(focusParams == null){
                        focusParams = getFocusParams();
                        getFocusedRect(r);
                    }
					this.mMinScaledBottom = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(), focusParams.coefX(),
							focusParams.coefY()).bottom;
				}

				{
                    if(maxBottomView != null){
                        focusParams = getScaleRectParams((ItemListener) maxBottomView, r);
                    }
                    if(focusParams == null){
                        focusParams = getFocusParams();
                        getFocusedRect(r);
                    }
					this.mMaxScaledBottom = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(), focusParams.coefX(),
							focusParams.coefY()).bottom;
				}
			}
		}

		super.initNode();
	}

    private FocusRectParams getScaleRectParams(ItemListener listener, Rect rect){
        FocusRectParams focusParams = null;
        if(listener != null){
            ItemListener item = listener;
            focusParams = item.getFocusParams();
            rect.set(focusParams.focusRect());
            offsetDescendantRectToMyCoords((View)listener, rect);
        }

        return focusParams;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if(gainFocus && previouslyFocusedRect != null){
            previouslyFocusedRect.offset(getScrollX(), getScrollY());
        }
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		offsetFocusRect(-getLeftX(), -getLeftY());
//		mFocusRectparams.focusRect().offset(-getLeftX(), -getLeftY());
	}

    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean hr = super.onKeyDown(keyCode, event);

        if(DEBUG){
            Log.d(TAG, "onKeyDown hr:" + hr + ", keyCode:" + keyCode);
        }

		if (hr) {
			scrollSingel(keyCode);
		}

		return hr;
	}

	@Override
	public boolean preOnKeyDown(int keyCode, KeyEvent event) {
		boolean hr = super.preOnKeyDown(keyCode, event);

		Log.d(TAG, "preOnKeyDown hr:" + hr + ", keyCode:" + keyCode);

		if (!hr && mOnScrollEndListener != null) {
			mOnScrollEndListener.onScrollEnd(keyCode, event);
		}
		return hr;
	}

	@Override
	public boolean isScrolling() {
		if (mDeep != null){
			return (mDeep.isScrolling()||!mScroller.isFinished());
		}
		
		return !mScroller.isFinished();
	}

	public OnScrollEndListener mOnScrollEndListener;

	public OnScrollEndListener getOnScrollEndListener() {
		return mOnScrollEndListener;
	}

	public void setOnScrollEndListener(OnScrollEndListener onScrollEndListener) {
		this.mOnScrollEndListener = onScrollEndListener;
	}

	public interface OnScrollEndListener {
		public void onScrollEnd(int keyCode, KeyEvent event);
	}

	@Override
	public void computeScroll() {
		if (this.mScrollMode == HORIZONTAL_FULL || this.mScrollMode == HORIZONTAL_SINGEL) {
			if (mScroller.computeScrollOffset()) {
				int offsetX = mScroller.getCurrX() - mScrollX;
				int offsetY = mScroller.getCurrY() - mScrollY;

				mScrollX = mScroller.getCurrX();
				mScrollY = mScroller.getCurrY();

				offsetFocusRect(offsetX, offsetY);
				// mFocusRectparams.focusRect().offset(offsetX, offsetY);
				scrollTo(mScrollX, mScrollY);
				invalidate();
			}

			if (mScroller.isFinished()) {
				reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
			}
		}
		super.computeScroll();
	}

	public void offsetFocusRect(int offsetX, int offsetY) {
		if (SystemProUtils.getGlobalFocusMode() == PositionManager.FOCUS_SYNC_DRAW) {
			mFocusRectparams.focusRect().offset(offsetX, offsetY);
		}
	}

	public boolean smoothScrollBy(int dx, int dy, int duration) {
		if (dx == 0 && dy == 0) {
			Log.w(TAG, "smoothScrollBy dx = " + dx + ", dy = " + dy + ", quit");
			if (!mScroller.isFinished()) {
				int remainScrollDistanceX = mScroller.getFinalX() - mScroller.getCurrX();
				int remainScrollDistanceY = mScroller.getFinalY() - mScroller.getCurrY();
				offsetFocusRect(-remainScrollDistanceX, -remainScrollDistanceY);
//				mFocusRectparams.focusRect().offset(-remainScrollDistanceX, -remainScrollDistanceY);
			}
			return false;
		}
		Log.d(TAG, "smoothScrollBy dx = " + dx + ", duration = " + duration);
		mScrollX = mScroller.getCurrX();
		mScrollY = mScroller.getCurrY();
		if (!mScroller.isFinished()) {
			mScroller.forceFinished(true);
		}
		mScroller.startScroll(mScrollX, mScrollY, dx, dy, duration);
		reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
		invalidate();
        return true;
	}

	protected int getLeftX() {
		return mScroller.getFinalX() - mScroller.getCurrX();
	}

	protected int getLeftY() {
		return mScroller.getFinalY() - mScroller.getCurrY();
	}

	void reportScrollStateChange(int newState) {
		if (newState != mLastScrollState) {
			if (mScrollerListener != null) {
				mLastScrollState = newState;
				mScrollerListener.onScrollStateChanged(this, newState);
			}
		}
	}

	protected void scrollSingel(int keyCode) {
		View selectedView = getSelectedView();

		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			int diffX = getOffset(keyCode);
			int diffY = 0;
			if (mLastVerticalDirection == KeyEvent.KEYCODE_DPAD_DOWN || mLastVerticalDirection == KeyEvent.KEYCODE_DPAD_UP) {
				diffY = getOffset(mLastVerticalDirection);
			}

			boolean start = smoothScrollBy(diffX, diffY, mDuration);
			if (start) {
				offsetFocusRect(-diffX, -diffY);
				// mFocusRectparams.focusRect().offset(-diffX, -diffY);
			}

			mLastHorizontalDirection = keyCode;

		} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			int diffX = getOffset(keyCode);
			int diffY = 0;
			if (mLastVerticalDirection == KeyEvent.KEYCODE_DPAD_DOWN || mLastVerticalDirection == KeyEvent.KEYCODE_DPAD_UP) {
				diffY = getOffset(mLastVerticalDirection);
			}

			boolean start = smoothScrollBy(diffX, diffY, mDuration);
			if (start) {
				offsetFocusRect(-diffX, -diffY);
//				mFocusRectparams.focusRect().offset(-diffX, -diffY);
			}

			mLastHorizontalDirection = keyCode;

		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			int diffY = getOffset(keyCode);
			int diffX = 0;
			if (mLastHorizontalDirection == KeyEvent.KEYCODE_DPAD_LEFT || mLastHorizontalDirection == KeyEvent.KEYCODE_DPAD_RIGHT) {
				diffX = getOffset(mLastHorizontalDirection);
			}

			boolean start = smoothScrollBy(diffX, diffY, mDuration);
			if (start) {
				offsetFocusRect(-diffX, -diffY);
//				mFocusRectparams.focusRect().offset(-diffX, -diffY);
			}

			mLastVerticalDirection = keyCode;

		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			int diffY = getOffset(keyCode);
			int diffX = 0;
			if (mLastHorizontalDirection == KeyEvent.KEYCODE_DPAD_LEFT || mLastHorizontalDirection == KeyEvent.KEYCODE_DPAD_RIGHT) {
				diffX = getOffset(mLastHorizontalDirection);
			}

			boolean start = smoothScrollBy(diffX, diffY, mDuration);
			if (start) {
				offsetFocusRect(-diffX, -diffY);
//				mFocusRectparams.focusRect().offset(-diffX, -diffY);
			}

			mLastVerticalDirection = keyCode;
		}
	}

	private int getOffset(int keyCode) {
		View selectedView = getSelectedView();
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_RIGHT: {
			int centerX = mCenterX + getScrollX();
			int selectCenterX = getCenterX(selectedView);
			int diff = selectCenterX - centerX;
			if (diff > 0) {
				Rect visibleRect = new Rect();
				getGlobalVisibleRect(visibleRect);
				int currRightBind = visibleRect.width() + getScrollX();
				if (currRightBind + diff <= mMaxRight + mManualPaddingRight) {
				} else {
					diff -= ((currRightBind + diff) - (mMaxRight + mManualPaddingRight));
				}

				if (diff > 0) {
					return diff;
				}

			}
			return 0;
		}
		case KeyEvent.KEYCODE_DPAD_LEFT: {
			int centerX = mCenterX + getScrollX();
			int selectCenterX = getCenterX(selectedView);
			int diff = centerX - selectCenterX;
			if (diff > 0) {
				int currLeftBind = getScrollX() + mMinLeft;
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
			int centerY = mCenterY + getScrollY();
			int selectCenterY = getCenterY(selectedView);
			int diff = selectCenterY - centerY;
			if (diff > 0) {
				int currBottomBind = 0;
				Rect visibleRect = new Rect();
				getGlobalVisibleRect(visibleRect);
				int vheight = visibleRect.height();
				currBottomBind = vheight + getScrollY();
				if (currBottomBind + diff <= mMaxBottom + mManualPaddingBottom) {
				} else {
					diff -= ((currBottomBind + diff) - (mMaxBottom + mManualPaddingBottom));
				}

				if (diff > 0) {
					return diff;
				}
			}

			return 0;
		}
		case KeyEvent.KEYCODE_DPAD_UP: {
			int centerY = mCenterY + getScrollY();
			int selectCenterY = getCenterY(selectedView);
			int diff = centerY - selectCenterY;
			if (diff > 0) {
				int currTopBind = getScrollY() + mMinTop;
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

	/**
	 * 
	 * @param dx
	 *            正数时往左滚动，看到更多右边的内容，负数时往右滚动
	 * @param dy
	 *            正数时往上滚动，看到更多下面的内容。
	 * @param duration
	 */
	public void smoothScroll(int dx, int dy, int duration) {
		if (dx == 0 && dy == 0) {
			return;
		}
		if (!mScroller.isFinished()) {
			mScroller.forceFinished(true);
		}
		if (dx != 0) {
			// int maxRight = getWidth();
			int maxRight = mMaxRight;
			int scrollx = mScroller.getCurrX();
			Log.d(TAG, "smoothScrollBy dx = " + dx + ", duration = " + duration + ", myscroolx" + scrollx + ",w:" + maxRight);

			if (dx > 0) {// 往左动，看右边内容
				// 判断右边界
				Rect visibleRect = new Rect();
				getGlobalVisibleRect(visibleRect);
				int diff = maxRight + mManualPaddingRight - scrollx - visibleRect.width();
				if (diff <= 0) {
					return;
				}
				if (diff < dx) {
					dx = diff;
				}
			} else {
				if (scrollx <= 0) {
					return;
				}
				int abs_dx = -dx;
				if (abs_dx > scrollx) {
					dx = -scrollx;
				}
			}
		} else {
			// int maxBottom = getHeight();
			int maxBottom = mMaxBottom;
			int scrolly = mScroller.getCurrY();

			if (dy > 0) {// 往上动，看下边内容
				// 判断下边界
				Rect visibleRect = new Rect();
				getGlobalVisibleRect(visibleRect);
				int diff = maxBottom + mManualPaddingBottom - scrolly - visibleRect.height();
				if (diff <= 0) {
					return;
				}
				if (diff < dy) {
					dy = diff;
				}
			} else {
				if (scrolly <= 0) {
					return;
				}
				int abs_dy = -dy;
				if (abs_dy > scrolly) {
					dy = -scrolly;
				}
			}
		}
		mScroller.startScroll(mScroller.getCurrX(), mScroller.getCurrY(), dx, dy, duration);
		invalidate();
	}

    public int getLastHorizontalDirection() {
        return mLastHorizontalDirection;
    }

    public int getLastVerticalDirection() {
        return mLastVerticalDirection;
    }
}
