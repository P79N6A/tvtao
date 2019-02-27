package com.yunos.tvlife.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.Scroller;

import com.yunos.tvlife.app.widget.FlingManager.FlingCallback;
import com.yunos.tvlife.app.widget.FocusedBasePositionManager.ContainInterface;
import com.yunos.tvlife.app.widget.FocusedBasePositionManager.FocusItemSelectedListener;
import com.yunos.tvlife.app.widget.FocusedBasePositionManager.FocusParams;
import com.yunos.tvlife.app.widget.FocusedBasePositionManager.ItemInterface;
import com.yunos.tvlife.app.widget.FocusedBasePositionManager.PositionInterface;
import com.yunos.tvlife.lib.LOG;

import java.lang.reflect.Method;

public class FocusedGridView extends GridView implements PositionInterface, FlingCallback, ContainInterface {
	private static final String TAG = "FocusedGridView";
	private static final boolean DEBUG = true;

	private static final int SCROLLING_DURATION = 1200;
	private static final int SCROLLING_DELAY = 10;

	private static final int SCROLL_UP = 0;
	private static final int SCROLL_DOWN = 1;

	private long KEY_INTERVEL = 150;// ms
	private long mKeyTime = 0;

	protected int mCurrentPosition = -1;
	private int mLastPosition = -1;
	private OnScrollListener mOuterScrollListener;
	private boolean isScrolling = false;
	private Object lock = new Object();
	private int mStartX;

	private FocusedBasePositionManager mPositionManager;
	private OnItemClickListener mOnItemClickListener = null;
	private FocusItemSelectedListener mOnItemSelectedListener = null;
	private int mHeaderPosition = -1;
	private boolean mHeaderSelected = false;
	private boolean mIsFocusInit = false;
	private int mLastOtherPosition = -1;
	private boolean mInit = false;
	private boolean mAutoChangeLine = true;
	private int mScrollDirection = SCROLL_DOWN;
	private int mLastScrollDirection = SCROLL_DOWN;
	private int mScrollDuration = SCROLLING_DURATION;
	private FocusDrawListener mFocusDrawListener = null;

	private OnScrollListener mOnScrollListener = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (mOuterScrollListener != null) {
				mOuterScrollListener.onScrollStateChanged(view, scrollState);
			}
			switch (scrollState) {
			case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
			case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				LOG.d(TAG, DEBUG, "onScrollStateChanged fling");
				setScrolling(true);
				mPositionManager.setScrolling(true);
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 最后一个项目出来的时候显示
				LOG.d(TAG, DEBUG, "onScrollStateChanged idle");
				setScrolling(false);
				mPositionManager.setScrolling(false);
				break;
			default:
				break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (mOuterScrollListener != null) {
				mOuterScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		}
	};

	public void setFocusDrawListener(FocusDrawListener l) {
		this.mFocusDrawListener = l;
	}

	public void setScrollDuration(int duration) {
		this.mScrollDuration = duration;
	}

	public void setAutoChangeLine(boolean isChange) {
		this.mAutoChangeLine = isChange;
	}

	public void setHeaderPosition(int position) {
		this.mHeaderPosition = position;
	}

	private boolean hasHeader() {
		return this.mHeaderPosition >= 0 ? true : false;
	}

	private boolean checkHeaderPosition() {
		return hasHeader() && this.mCurrentPosition < this.getNumColumns() ? true : false;
	}

	public boolean checkHeaderPosition(int position) {
		return hasHeader() && position < this.getNumColumns() ? true : false;
	}

	public boolean checkFromHeaderPosition() {
		return hasHeader() && mLastPosition < this.getNumColumns() ? true : false;
	}

	public FocusedGridView(Context contxt) {
		super(contxt);
		init(contxt);
	}

	public FocusedGridView(Context contxt, AttributeSet attrs) {
		super(contxt, attrs);
		init(contxt);
	}

	public FocusedGridView(Context contxt, AttributeSet attrs, int defStyle) {
		super(contxt, attrs, defStyle);
		init(contxt);
	}

	private void initLeftPosition() {
		if (!mInit) {
			mInit = true;
			int location[] = new int[2];
			this.getLocationOnScreen(location);
			mStartX = location[0] + getPaddingLeft();
			LOG.d(TAG, DEBUG, "initLeftPosition mStartX = " + mStartX);
		}
	}

	public void init(Context context) {
		Log.i(TAG, "init mCurrentPosition11:" + mCurrentPosition);
		setChildrenDrawingOrderEnabled(true);
		super.setOnScrollListener(mOnScrollListener);

		Log.i(TAG, "init mCurrentPosition12:" + mCurrentPosition);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return true;
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		// Current selected index.
		int selectedIndex = getSelectedItemPosition() - getFirstVisiblePosition();
		if (selectedIndex < 0) {
			return i;
		}

		if (i < selectedIndex) {
			return i;
		} else if (i >= selectedIndex) {
			return childCount - 1 - i + selectedIndex;
		} else {
			return i;
		}
	}

	private void setScrolling(boolean scrolling) {
		synchronized (this.lock) {
			this.isScrolling = scrolling;
		}
	}

	private boolean isScrolling() {
		synchronized (this.lock) {
			return this.isScrolling;
		}
	}

	@Override
	public void dispatchDraw(Canvas canvas) {
		LOG.d(TAG, DEBUG, "dispatchDraw child count = " + this.getChildCount() + ", first position = " + this.getFirstVisiblePosition()
				+ ", last posititon = " + this.getLastVisiblePosition());
		if (getSelectedView() != null && this.mPositionManager.isLastFrame()) {
			this.mPositionManager.drawFrame(canvas);
		}

		super.dispatchDraw(canvas);

		if (this.mFocusDrawListener != null) {
			canvas.save();
			this.mFocusDrawListener.beforFocusDraw(canvas);
			canvas.restore();
		}
		if (this.mPositionManager.getSelectedItem() == null && getSelectedView() != null && hasFocus()) {
			this.mPositionManager.setSelectedItem((ItemInterface) getSelectedView());
			performItemSelect(getSelectedView(), mCurrentPosition, true);
		}

		if (getSelectedView() != null && !this.mPositionManager.isLastFrame()) {
			this.mPositionManager.drawFrame(canvas);

			if (hasFocus()) {
				focusInit();
			}

			if (checkHeaderPosition() && isScrolling()) {
				getSelectedView().invalidate();
			}
		}
	}
	
	public void subSelectPosition() {
		arrowScroll(FOCUS_LEFT);
	}

	@Override
	public void getFocusedRect(Rect r) {
		Log.i(TAG, "getFocusedRect mCurrentPosition" + mCurrentPosition + ",getFirstVisiblePosition:" + this.getFirstVisiblePosition()
				+ ",getLastVisiblePosition:" + getLastVisiblePosition());
		if (mCurrentPosition < this.getFirstVisiblePosition() || mCurrentPosition > this.getLastVisiblePosition()) {
			mCurrentPosition = this.getFirstVisiblePosition();
			Log.i(TAG, "mCurrentPosition9:" + mCurrentPosition);
		}

		super.getFocusedRect(r);
	}

	@Override
	public void setSelection(int position) {
		LOG.i(TAG, DEBUG, "setSelection = " + position + ", mCurrentPosition = " + mCurrentPosition);
		View lastSelectedView = getSelectedView();
		this.mLastPosition = this.mCurrentPosition;
		this.mCurrentPosition = position;
		// setNextSelectedPositionInt(position);
		// super.setSelection(position);
		
		View selectedView = getSelectedView();
		if (selectedView == null || mCurrentPosition == mLastPosition) {
			return;
		}
		
		this.mPositionManager.stopDraw();
		this.mPositionManager.reset();
		
		this.mPositionManager.setTransAnimation(false);
		this.mPositionManager.setSelectedItem((ItemInterface)getSelectedView());
//		this.mPositionManager.setLastSelectedItem(null);
//		this.mPositionManager.setScaleLastView(false);
		this.mPositionManager.setScaleCurrentView(true);
		if (lastSelectedView != null) {
			performItemSelect(lastSelectedView, mLastPosition, false);
		}
		performItemSelect(getSelectedView(), mCurrentPosition, true);
//		this.mPositionManager.setNeedDraw(true);
		this.mPositionManager.setContrantNotDraw(true);
		this.mPositionManager.setState(FocusedBasePositionManager.STATE_DRAWING);
		this.mPositionManager.startDraw();

		this.requestLayout();
		// this.invalidate();
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		this.mOuterScrollListener = l;
	}

	public static int FOCUS_ITEM_REMEMBER_LAST = 0;
	public static int FOCUS_ITEM_AUTO_SEARCH = 1;
	private int focusPositionMode = FOCUS_ITEM_REMEMBER_LAST;

	public void setFocusPositionMode(int mode) {
		LOG.i(TAG, DEBUG, "setFocusPositionMode mode = " + mode);
		focusPositionMode = mode;
	}

	public int getFocusPositionMode() {
		return focusPositionMode;
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		LOG.i(TAG,
				DEBUG,
				"onFocusChanged,gainFocus:" + gainFocus + ", mCurrentPosition = " + mCurrentPosition + ", child count = "
						+ this.getChildCount());
		if (focusPositionMode == FOCUS_ITEM_AUTO_SEARCH) {
			LOG.d(TAG, DEBUG, "super.onFocusChanged");
			super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		}
		synchronized (this) {
			mKeyTime = System.currentTimeMillis();
		}

		if (gainFocus != this.mPositionManager.hasFocus()) {
			this.mPositionManager.stopDraw();
			this.mPositionManager.reset();
			mIsFocusInit = false;
		}

		this.mPositionManager.setFocus(gainFocus);

		invalidate();
		focusInit();
		initLeftPosition();
	}

	private void focusInit() {
		if (mIsFocusInit) {
			return;
		}
		LOG.i(TAG, DEBUG, "focusInit mCurrentPosition = " + mCurrentPosition + ", getSelectedItemPosition() = " + getSelectedItemPosition());
		if (mCurrentPosition < 0) {
			Log.i(TAG, "mCurrentPosition1:" + mCurrentPosition);
			mCurrentPosition = this.getSelectedItemPosition();
		}

		if (mCurrentPosition < 0) {
			Log.i(TAG, "mCurrentPosition2:" + mCurrentPosition);
			mCurrentPosition = 0;
		}

		if (mCurrentPosition < this.getFirstVisiblePosition() || mCurrentPosition > this.getLastVisiblePosition()) {
			Log.i(TAG, "mCurrentPosition3:" + mCurrentPosition);
			mCurrentPosition = this.getFirstVisiblePosition();
		}

		if (!hasFocus()) {
			// this.mPositionManager.drawFrame(null); //TODO
			// canvas为null可能导致图片drawable.draw(canvas)空指针;
			this.mPositionManager.setSelectedItem(null);
			this.mLastPosition = this.mCurrentPosition;
			this.mPositionManager.setFocusDrawableVisible(false, true);
			this.mPositionManager.setFocusDrawableShadowVisible(false, true);
			this.mPositionManager.setTransAnimation(false);
			this.mPositionManager.setScaleCurrentView(false);
			if (checkHeaderPosition()) {
				this.mPositionManager.setContrantNotDraw(true);
				// this.mPositionManager.setScaleLastView(false);
			} else {
				// this.mPositionManager.setScaleLastView(true);
			}
			this.mPositionManager.drawUnscale();
		} else {
			if (focusPositionMode == FOCUS_ITEM_AUTO_SEARCH) {
				mCurrentPosition = super.getSelectedItemPosition();
				Log.i(TAG, "mCurrentPosition4:" + mCurrentPosition);
			} else {
				setSelection(mCurrentPosition > -1 && mCurrentPosition < this.getCount() ? mCurrentPosition : 0);
			}

			// this.mPositionManager.setLastSelectedView(null);
			// this.mPositionManager.setScaleLastView(false);
			if (checkHeaderPosition()) {
				this.mPositionManager.setContrantNotDraw(true);
				this.mPositionManager.setScaleCurrentView(false);
			} else {
				this.mPositionManager.setScaleCurrentView(true);
			}

			this.mPositionManager.setFocusDrawableVisible(true, true);
			this.mPositionManager.setFocusDrawableShadowVisible(true, true);
			this.mPositionManager.setSelectedItem((ItemInterface) getSelectedView());
			this.mPositionManager.setState(FocusedBasePositionManager.STATE_DRAWING);
			this.mPositionManager.startDraw();
		}

		if (getSelectedView() != null) {
			if (checkHeaderPosition()) {
				performItemSelect(getSelectedView(), mHeaderPosition, hasFocus());
			} else {
				performItemSelect(getSelectedView(), mCurrentPosition, hasFocus());
			}

			if (mCurrentPosition >= 0) {
				mIsFocusInit = true;
			}
		}

		invalidate();
	}

	@Override
	public int getSelectedItemPosition() {
		return mCurrentPosition;
	}

	public int getLastSelectedItemPosition() {
		return mLastPosition;
	}

	public View getSelectedView() {
		if (this.getChildCount() <= 0) {
			return null;
		}

		int pos = mCurrentPosition;
		if (pos < this.getFirstVisiblePosition() || pos > this.getLastVisiblePosition()) {
			Log.w(TAG, "getSelectedView mCurrentPosition = " + mCurrentPosition + ", getFirstVisiblePosition() = "
					+ getFirstVisiblePosition() + ", getLastVisiblePosition() = " + getLastVisiblePosition());
			return null;
		}

		if (checkHeaderPosition()) {
			pos = this.mHeaderPosition;
		}

		int indexOfView = pos - getFirstVisiblePosition();
		View selectedView = getChildAt(indexOfView);

		LOG.d(TAG, DEBUG, "getSelectedView getSelectedView: mCurrentPosition = " + mCurrentPosition + ", indexOfView = " + indexOfView
				+ ", child count = " + this.getChildCount() + ", getFirstVisiblePosition() = " + getFirstVisiblePosition()
				+ ", getLastVisiblePosition() = " + getLastVisiblePosition());
		return selectedView;
	}

	private void performItemSelect(View v, int position, boolean isSelected) {
		if (this.mOnItemSelectedListener != null) {
			this.mOnItemSelectedListener.onItemSelected(v, position, isSelected, this);
		}
	}

	private void performItemClick() {
		View v = this.getSelectedView();
		if (v != null && this.mOnItemClickListener != null) {
			this.mOnItemClickListener.onItemClick(this, v, mCurrentPosition, 0);
		}
	}

	boolean isKeyDown = false;

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (getSelectedView() != null && getSelectedView().onKeyUp(keyCode, event)) {
			isKeyDown = false;
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
			if (isKeyDown) {
				performItemClick();
			}

			isKeyDown = false;
			return true;
		}
		if (isKeyDown) {
			isKeyDown = false;

			return true;
		}

		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (onKeyDownListener != null && onKeyDownListener.onKeyDown(keyCode, event)) {
			return true;
		}
		if (!(keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_LEFT
				|| keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP)) {
			return super.onKeyDown(keyCode, event);
		}

		if (event.getRepeatCount() == 0) {
			isKeyDown = true;
		}
		LOG.i(TAG, DEBUG, "onKeyDown keyCode = " + keyCode + ", child count = " + this.getChildCount() + ", mCurrentPosition = "
				+ mCurrentPosition);

		synchronized (this) {
			if (/*
				 * System.currentTimeMillis() - mKeyTime <= KEY_INTERVEL ||
				 */this.mPositionManager.getState() == FocusedBasePositionManager.STATE_DRAWING /*
																								 * ||
																								 * isScrolling
																								 * (
																								 * )
																								 */) {
				Log.w(TAG,
						"onKeyDown KyeInterval = " + (System.currentTimeMillis() - mKeyTime) + ", getState() = "
								+ this.mPositionManager.getState() + ", isScrolling() = " + isScrolling());
				return true;
			}

			if (isScrolling()
					&& (!(mCurrentPosition >= getFirstVisiblePosition() && mCurrentPosition <= getLastVisiblePosition()) || System
							.currentTimeMillis() - mKeyTime <= KEY_INTERVEL)) {
				return true;
			}
			mKeyTime = System.currentTimeMillis();
		}

		if (this.getChildCount() <= 0) {
			return true;
		}

		if (!this.mAutoChangeLine) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && (mCurrentPosition + 1) % this.getNumColumns() == 0) {
				return false;
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && mCurrentPosition != 0 && mCurrentPosition % this.getNumColumns() == 0) {
				return false;
			}
		}

		if (getSelectedView() != null && getSelectedView().onKeyDown(keyCode, event)) {
			return true;
		}

		if (hasHeader()
				&& (((mCurrentPosition + 1) / this.getNumColumns() == 1 && (mCurrentPosition + 1) % this.getNumColumns() == 0 && KeyEvent.KEYCODE_DPAD_RIGHT == keyCode) || (mCurrentPosition
						/ this.getNumColumns() == 1
						&& mCurrentPosition % this.getNumColumns() == 0 && KeyEvent.KEYCODE_DPAD_LEFT == keyCode))) {
			return true;
		}

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			if (!arrowScroll(FOCUS_UP)) {
				Log.w(TAG, "arrowScroll up return false");
				return false;
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if (!arrowScroll(FOCUS_DOWN)) {
				Log.w(TAG, "arrowScroll down return false");
				return false;
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (!arrowScroll(FOCUS_LEFT)) {
				Log.w(TAG, "arrowScroll left return false");
				return false;
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (!arrowScroll(FOCUS_RIGHT)) {
				Log.w(TAG, "arrowScroll right return false");
				return false;
			}
			return true;
		}

		return true;
	}

	int mScrollDistance = 0;
	int mScrollHeaderDiscance = 0;

	public boolean arrowScroll(int direction) {
		if (mScrollDistance <= 0) {
			if (this.hasHeader()) {
				if (this.getChildAt(getNumColumns()) != null) {
					mScrollDistance = (int) (this.getChildAt(getNumColumns()).getHeight());
				}
				mScrollHeaderDiscance = (int) (this.getChildAt(this.mHeaderPosition).getHeight());
				LOG.d(TAG, DEBUG, "scrollBy: mScrollHeaderDiscance " + mScrollHeaderDiscance);
			} else {
				if (this.getCount() > 0) {
					mScrollDistance = (int) (this.getChildAt(0).getHeight());
					LOG.d(TAG, DEBUG, "scrollBy: mScrollDistance " + mScrollDistance);
				}
			}
		}
		LOG.i(TAG, DEBUG, "scrollBy:mCurrentPosition before " + mCurrentPosition);
		View lastSelectedView = getSelectedView();
		View mCurrentView;
		int lastPosition = mCurrentPosition;
		boolean isNeedTrans = true;
		int scrollBy = 0;
		int columns = getNumColumns();
		int paddedTop = getListPaddingTop();
		int paddedBottom = getHeight() - getListPaddingBottom();
		switch (direction) {
		case FOCUS_UP:
			if (mCurrentPosition >= columns) {
				mCurrentPosition -= columns;
				if (this.checkHeaderPosition()) {
					mCurrentView = this.getChildAt(this.mHeaderPosition - getFirstVisiblePosition());
					mCurrentPosition = this.mHeaderPosition;
					Log.i(TAG, "mCurrentPosition5:" + mCurrentPosition);
				} else {
					mCurrentView = this.getChildAt(mCurrentPosition - getFirstVisiblePosition());
				}

				if (mCurrentView != null) {
					if (mCurrentView.getTop() < paddedTop) {
						endFling();
						scrollBy = mCurrentView.getTop() - paddedTop;
					}
				} else {
					endFling();
					// 如果往上到了CoverFlow,则滚动的高度需要改变
					scrollBy = this.checkHeaderPosition(mCurrentPosition) ? -mScrollHeaderDiscance : -mScrollDistance;
				}

				if (scrollBy != 0) {
					mLastScrollDirection = mScrollDirection;
					mScrollDirection = SCROLL_UP;
					isNeedTrans = false;
				}
			} else {
				return false;
			}
			break;
		case FOCUS_DOWN:
			// 判断当前是否为最后一行
			if (mCurrentPosition / columns < (getCount() - 1) / columns) {
				mCurrentPosition += columns;
				mCurrentPosition = (mCurrentPosition > getCount() - 1) ? getCount() - 1 : mCurrentPosition;
				Log.i(TAG, "mCurrentPosition6:" + mCurrentPosition);
				mCurrentView = this.getChildAt(mCurrentPosition - getFirstVisiblePosition());
				if (mCurrentView != null) {
					if (mCurrentView.getBottom() > paddedBottom) {
						endFling();
						scrollBy = mCurrentView.getBottom() - paddedBottom;
					}
				} else {
					endFling();
					scrollBy = /* isHeaderViewVisible ? mScrollHeaderDiscance : */mScrollDistance;
				}
				if (scrollBy != 0) {
					mLastScrollDirection = mScrollDirection;
					mScrollDirection = SCROLL_DOWN;
					isNeedTrans = false;
				}
			} else {
				return false;
			}
			break;
		case FOCUS_LEFT:
			if (mCurrentPosition > 0) {
				mCurrentPosition -= 1;
				if ((mCurrentPosition + 1) % columns == 0) {
					if (this.checkHeaderPosition()) {
						mCurrentView = this.getChildAt(this.mHeaderPosition - getFirstVisiblePosition());
						mCurrentPosition = this.mHeaderPosition;
						Log.i(TAG, "mCurrentPosition7:" + mCurrentPosition);
					} else {
						mCurrentView = this.getChildAt(mCurrentPosition - getFirstVisiblePosition());
					}
					if (mCurrentView != null) {
						if (mCurrentView.getTop() < paddedTop) {
							endFling();
							scrollBy = mCurrentView.getTop() - paddedTop;
						}
						isNeedTrans = false;
					} else {
						endFling();
						// 如果往上到了CoverFlow,则滚动的高度需要改变
						scrollBy = this.checkHeaderPosition(mCurrentPosition) ? -mScrollHeaderDiscance : -mScrollDistance;
					}
				}

				if (scrollBy != 0) {
					mLastScrollDirection = mScrollDirection;
					mScrollDirection = SCROLL_UP;
					isNeedTrans = false;
				}
			} else {
				return false;
			}
			break;
		case FOCUS_RIGHT:
			if (mCurrentPosition < getCount() - 1) {
				mCurrentPosition += 1;
				if (mCurrentPosition % columns == 0) {
					mCurrentView = this.getChildAt(mCurrentPosition - getFirstVisiblePosition());
					if (mCurrentView != null) {
						if (mCurrentView.getBottom() > paddedBottom) {
							endFling();
							scrollBy = mCurrentView.getBottom() - paddedBottom;
						}
						isNeedTrans = false;
					} else {
						endFling();
						scrollBy = mScrollDistance;
					}
				}
				if (scrollBy != 0) {
					mLastScrollDirection = mScrollDirection;
					mScrollDirection = SCROLL_DOWN;
					isNeedTrans = false;
				}
			} else {
				return false;
			}
			break;
		}
		LOG.d(TAG, DEBUG, "arrowScroll: mCurrentPosition = " + mCurrentPosition);
		// add by leming.yanlm
		playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));

		if (lastPosition != mCurrentPosition) {
			this.mLastPosition = lastPosition;
		}

		// this.mPositionManager.stopDraw();
		// this.mPositionManager.reset();
		if (checkHeaderPosition(mCurrentPosition)) {
			// setNextSelectedPositionInt(mCurrentPosition);
			if (scrollBy != 0) {
				this.mPositionManager.setContrantNotDraw(true);
				arrowSmoothScroll(scrollBy);
				// smoothScrollBy(mScrollY, SCROLLING_DURATION);
				mHandler.sendEmptyMessageDelayed(DRAW_FOCUS, SCROLLING_DELAY);
				if (lastSelectedView != null) {
					performItemSelect(lastSelectedView, lastPosition, false);
				}
				return true;
			} else {
				this.mPositionManager.setContrantNotDraw(true);
				this.mPositionManager.setTransAnimation(false);
				if (checkFromHeaderPosition()) {
					LOG.d(TAG, DEBUG, "arrowScroll focus form header");
					this.mPositionManager.setScaleCurrentView(false);
					// this.mPositionManager.setScaleLastView(false);
					// this.mPositionManager.setNeedDraw(false);
				} else {
					LOG.d(TAG, DEBUG, "arrowScroll focus form other");
					this.mLastOtherPosition = lastPosition;
					this.mPositionManager.setScaleCurrentView(false);
					// this.mPositionManager.setScaleLastView(true);
					// this.mPositionManager.setNeedDraw(true);
					invalidate();
				}
				LOG.d(TAG, DEBUG, "arrowScroll header mCurrentPosition = " + mCurrentPosition + ", mHeaderPosition = " + mHeaderPosition
						+ ", mHeaderSelected = " + mHeaderSelected);
				if (!mHeaderSelected) {
					mHeaderSelected = true;
					if (lastSelectedView != null) {
						performItemSelect(lastSelectedView, lastPosition, false);
					}

					if (getSelectedView() != null && getSelectedView() != lastSelectedView && lastPosition != mCurrentPosition) {
						performItemSelect(getSelectedView(), mHeaderPosition, true);
					}
				}

				return true;
			}
		}

		boolean isScaleLastView = true;
		boolean isScaleCurrentView = true;
		LOG.d(TAG, DEBUG, "arrowScroll this.mLastPosition = " + this.mLastPosition + ", this.mCurrentPosition = " + this.mCurrentPosition
				+ ", lastPosition = " + lastPosition);
		if (checkFromHeaderPosition()) {
			isNeedTrans = false;
			isScaleLastView = false;
			if (this.mLastOtherPosition >= 0 && lastPosition == mHeaderPosition) {
				mCurrentPosition = this.mLastOtherPosition;
				Log.i(TAG, "mCurrentPosition8:" + mCurrentPosition);
			}
		}

		mHeaderSelected = false;

		if (lastSelectedView != null) {
			performItemSelect(lastSelectedView, lastPosition, false);
		}

		if (getSelectedView() != null && getSelectedView() != lastSelectedView && lastPosition != mCurrentPosition) {
			performItemSelect(getSelectedView(), mCurrentPosition, true);
		}

		if (checkHeaderPosition()) {
			isNeedTrans = false;
			isScaleCurrentView = false;
		}
		this.mPositionManager.setFocusMove(isNeedTrans);

		// setNextSelectedPositionInt(mCurrentPosition);
		this.mPositionManager.setState(FocusedBasePositionManager.STATE_DRAWING);
		if (scrollBy != 0) {
			LOG.i(TAG, DEBUG, "scrollBy: scrollBy = " + scrollBy);
			this.mPositionManager.setContrantNotDraw(true);
			this.mPositionManager.stopDraw();
			this.mPositionManager.reset();
			this.mPositionManager.setSelectedItem(null);
			arrowSmoothScroll(scrollBy);
			mHandler.sendEmptyMessageDelayed(DRAW_FOCUS, SCROLLING_DELAY);
		} else {

			this.mPositionManager.setFocusDirection(direction);
			this.mPositionManager.stopDraw();
			this.mPositionManager.reset();

			this.mPositionManager.setSelectedItem((ItemInterface) getSelectedView());
			this.mPositionManager.setContrantNotDraw(false);
			this.mPositionManager.setScaleCurrentView(isScaleCurrentView);
			// this.mPositionManager.setScaleLastView(isScaleLastView);
			this.invalidate();
		}

		return true;
	}

	void arrowSmoothScroll(int scrollBy) {
		boolean isScrolling = isScrolling();
		endFling();
		int currY = getCurrentY();
		LOG.d(TAG, DEBUG, "arrowSmoothScroll currY = " + currY + ", mCurrentPosition = " + mCurrentPosition + ", isScrolling = "
				+ isScrolling);
		if (mScrollY < 0) {
			currY -= Integer.MAX_VALUE;
		}

		LOG.d(TAG, DEBUG, "arrowSmoothScroll currY = " + currY + ", mScrollY = " + mScrollY + ", scrollBy = " + scrollBy);
		mScrollY -= currY;
		if (mScrollDirection != mLastScrollDirection) {
			mScrollY = 0;
			currY = 0;
		}

		mScrollY += scrollBy;
		LOG.d(TAG, DEBUG, "arrowSmoothScroll mScrollY = " + mScrollY);
		smoothScrollBy(mScrollY, mScrollDuration);

	}

	private Method methodSetNextSelectedPositionInt = null;

	void setNextSelectedPositionInt(int position) {
		// if (methodSetNextSelectedPositionInt == null) {
		// try {
		// methodSetNextSelectedPositionInt =
		// getClass().getSuperclass().getMethod("setNextSelectedPositionInt",
		// int.class);
		// } catch (NoSuchMethodException e) {
		// e.printStackTrace();
		// } catch (SecurityException e) {
		// e.printStackTrace();
		// }
		// }
		// if (methodSetNextSelectedPositionInt != null) {
		// try {
		// methodSetNextSelectedPositionInt.invoke(this, position);
		// } catch (IllegalAccessException e) {
		// e.printStackTrace();
		// } catch (IllegalArgumentException e) {
		// e.printStackTrace();
		// } catch (InvocationTargetException e) {
		// e.printStackTrace();
		// }
		// }
	}

	void endFling() {
		if (mFlingManager != null) {
			mFlingManager.endFling();
		}
	}

	int mScrollY = 0;

	int getCurrentY() {
		if (mFlingManager != null) {
			return mFlingManager.getActualY();
		}
		return 0;
	}

	private static final int DRAW_FOCUS = 1;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DRAW_FOCUS:
				if (getSelectedView() != null) {
					performItemSelect(getSelectedView(), mCurrentPosition, true);
				} else {
					Log.w(TAG, "Handler handleMessage selected view is null delay");
					sendEmptyMessageDelayed(DRAW_FOCUS, SCROLLING_DELAY);
					return;
				}

				if (checkHeaderPosition()) {
					if (!checkFromHeaderPosition()) {
						mPositionManager.setContrantNotDraw(true);
						mPositionManager.setScaleCurrentView(false);
					}
				} else {
					mPositionManager.setContrantNotDraw(false);
					mPositionManager.setScaleCurrentView(true);
				}
				mPositionManager.setTransAnimation(false);
				// mPositionManager.setScaleLastView(true);

				// mPositionManager.setNeedDraw(true);
				mPositionManager.stopDraw();
				mPositionManager.reset();
				mPositionManager.setSelectedItem((ItemInterface) getSelectedView());

				if (!isScrolling()) {
					invalidate();
				}
				break;
			default:
				break;
			}
		}
	};

	public interface onKeyDownListener {
		public boolean onKeyDown(int keyCode, KeyEvent event);
	}

	private onKeyDownListener onKeyDownListener;

	public void setOnKeyDownListener(onKeyDownListener l) {
		onKeyDownListener = l;
	}

	public interface FocusDrawListener {
		public void beforFocusDraw(Canvas canvas);

		public void drawChild(Canvas canvas);
	}

	private boolean checkFocusPosition() {
		// Rect dstRect = this.mPositionManager.getDstRectAfterScale(true);
		Rect dstRect = this.mPositionManager.getDstRect();
		// Log.i(TAG, "checkFocusPosition:" +
		// this.mPositionManager.getCurrentRect() + "," + hasFocus() + "," +
		// dstRect + "," + isShown());
		if (null == this.mPositionManager.getCurrentRect() || !hasFocus() || null == dstRect || !isShown()
				|| this.mPositionManager.getContrantNotDraw()) {
			return false;
		}

		if (Math.abs(dstRect.left - this.mPositionManager.getCurrentRect().left) > 5
				|| Math.abs(dstRect.right - this.mPositionManager.getCurrentRect().right) > 5
				|| Math.abs(dstRect.top - this.mPositionManager.getCurrentRect().top) > 5
				|| Math.abs(dstRect.bottom - this.mPositionManager.getCurrentRect().bottom) > 5) {
			return true;
		}

		return false;
	}

	class FocusedScroller extends Scroller {

		public FocusedScroller(Context context, Interpolator interpolator, boolean flywheel) {
			super(context, interpolator, flywheel);
			// TODO Auto-generated constructor stub
		}

		public FocusedScroller(Context context, Interpolator interpolator) {
			super(context, interpolator);
			// TODO Auto-generated constructor stub
		}

		public FocusedScroller(Context context) {
			super(context, new AccelerateDecelerateInterpolator());
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean computeScrollOffset() {
			boolean isFinished = isFinished();
			boolean needInvalidate = checkFocusPosition();
			if (!isFinished || needInvalidate) {
				invalidate();
			}
			// invalidate();
			boolean hr = super.computeScrollOffset();
			// Log.d(TAG, "FocusedScroller computeScrollOffset isFinished = " +
			// isFinished + ", mOutsieScroll = " + mOutsieScroll + ", hr = "
			// + hr);
			return hr;
		}
	}

	public interface ScrollerListener {
		public void horizontalSmoothScrollBy(int dx, int duration);

		public int getCurrX(boolean isActual);

		public int getFinalX(boolean isActual);

		public boolean isFinished();
	}

	FlingManager mFlingManager;

	public void smoothScrollBy(int distance, int duration) {
		if (mFlingManager == null) {
			mFlingManager = new FlingManager(this, this);
		}

		// No sense starting to scroll if we're not going anywhere
		final int firstPos = getFirstVisiblePosition();
		final int childCount = getChildCount();
		final int lastPos = firstPos + childCount - 1;
		final int topLimit = getPaddingTop();
		final int bottomLimit = getHeight() - getPaddingBottom();

		if (distance == 0 || getAdapter().getCount() == 0 || childCount == 0
				|| (firstPos == 0 && getChildAt(0).getTop() == topLimit && distance < 0)
				|| (lastPos == getAdapter().getCount() - 1 && getChildAt(childCount - 1).getBottom() == bottomLimit && distance > 0)) {
			mFlingManager.endFling();
			// if (mFlingManager != null) {
			// mPositionScroller.stop();
			// }
		} else {
			mFlingManager.focusedReportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
			mFlingManager.startScroll(distance, duration);
		}
	}

	@Override
	public void flingLayoutChildren() {
		layoutChildren();
	}

	@Override
	public int getClipToPaddingMask() {
		return CLIP_TO_PADDING_MASK;
	}

	@Override
	public boolean flingOverScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		return overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
	}

	@Override
	public void flingDetachViewsFromParent(int start, int count) {
		detachViewsFromParent(start, count);
	}

	@Override
	public boolean flingAwakenScrollBars() {
		return awakenScrollBars();
	}

	@Override
	public void createPositionManager(FocusParams params) {
		this.mPositionManager = FocusedBasePositionManager.createPositionManager(params, this);
	}

	@Override
	public void setOnItemClickListener(OnItemClickListener listener) {
		this.mOnItemClickListener = listener;
	}

	@Override
	public void setOnItemSelectedListener(FocusItemSelectedListener listener) {
		this.mOnItemSelectedListener = listener;
	}

	@Override
	public int getViewScrollX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getViewScrollY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void reportState(int state) {
		// TODO Auto-generated method stub

	}

}
