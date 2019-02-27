package com.yunos.tvlife.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.yunos.tvlife.app.widget.FocusedBasePositionManager.ContainInterface;
import com.yunos.tvlife.app.widget.FocusedBasePositionManager.FocusItemSelectedListener;
import com.yunos.tvlife.app.widget.FocusedBasePositionManager.FocusParams;
import com.yunos.tvlife.app.widget.FocusedBasePositionManager.ItemInterface;
import com.yunos.tvlife.app.widget.FocusedBasePositionManager.PositionInterface;

public class FocusedListView extends ListView implements PositionInterface, ContainInterface {
	public static final String TAG = "FocusedListView";

	public static final int SCROLLING_DURATION = 200;
	private static final int SCROLLING_DELAY = 10;

	private static final int SCROLL_UP = 0;
	private static final int SCROLL_DOWN = 1;

	private long KEY_INTERVEL = 20;// ms
	private long mKeyTime = 0;

	private OnScrollListener mOuterScrollListener; // hlist 没有scroll回调
	private OnItemClickListener mOnItemClickListener = null;
	private FocusItemSelectedListener mOnItemSelectedListener = null;
	private FocusedBasePositionManager mPositionManager;
	private int mFocusViewId = -1;
	private int mCurrentPosition = -1;
	private int mLastPosition = -1;

	private Object lock = new Object();
	private boolean isScrolling = false;
	private boolean mNeedScroll = false;
	int mScrollDistance = 0;
	int mScrollDuration = SCROLLING_DURATION;
	private boolean mAutoSearchFocus = false;

	private int mScrollDirection = SCROLL_UP;
	private int mLastScrollDirection = SCROLL_UP;
	private boolean mNeedDrawChild = false;
	private boolean mForcedFocus = false;

	public FocusedListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public FocusedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FocusedListView(Context context) {
		super(context);
		init(context);
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
	public void createPositionManager(FocusParams params) {
		this.mPositionManager = FocusedBasePositionManager.createPositionManager(params, this);
	}

	public void forceFocus(boolean isFocus) {
		if(mForcedFocus && !isFocus){
			this.mPositionManager.stopDraw();
			this.mPositionManager.reset();
			this.mPositionManager.setSelectedItem(null);
			this.mLastPosition = this.mCurrentPosition;
			mCurrentPosition = this.getSelectedItemPosition();
			this.mPositionManager.setFocusDrawableVisible(false, true);
			this.mPositionManager.setFocusDrawableShadowVisible(false, true);
			this.mPositionManager.setTransAnimation(false);
			this.mPositionManager.setScaleCurrentView(false);
			this.mPositionManager.drawUnscale();
			
			if (getSelectedView() != null) {
				performItemSelect(getSelectedView(), mCurrentPosition, hasFocus());
			}
		}
		mForcedFocus = isFocus;
		// this.mPositionManager.setFocus(isFocus);
	}

	private void init(Context context) {
		// setSelector(R.color.transparent);
		setChildrenDrawingOrderEnabled(true);
		super.setOnScrollListener(mOnScrollListener); // TODO
	}

	/**
	 * 设置滚动时间，默认900ms
	 * 
	 * @param duration
	 */
	public void setScrollDuration(int duration) {
		mScrollDuration = duration;
	}

	private OnScrollListener mOnScrollListener = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (mOuterScrollListener != null) {
				mOuterScrollListener.onScrollStateChanged(view, scrollState);
			}
			Log.i(TAG, "onScrollStateChanged scrolling");
			switch (scrollState) {
			case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
			case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				setScrolling(true);
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 最后一个项目出来的时候显示
				Log.i(TAG, "onScrollStateChanged idle mNeedScroll = " + mNeedScroll);
				Log.d("lingdang", "mCurrentPosition=" + mCurrentPosition);
				if (mNeedScroll) {
					setSelection(mCurrentPosition);
				}
				setScrolling(false);
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

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		this.mOuterScrollListener = l;
	}

	private void performItemSelect(View v, int position, boolean isSelected) {
		Log.d(TAG, "performItemSelect position = " + position + ", isSelected = " + isSelected);
		if (this.mOnItemSelectedListener != null) {
			this.mOnItemSelectedListener.onItemSelected(v, position, isSelected, this);
			if (v.isFocusable()) {
				if (isSelected) {
					v.requestFocus();
				} else {
					v.clearFocus();
				}
			}
		}
	}

	private void performItemClick() {
		View v = this.getSelectedView();
		if (v != null && this.mOnItemClickListener != null) {
			this.mOnItemClickListener.onItemClick(this, v, mCurrentPosition, 0);
		}
	}

	@Override
	public void dispatchDraw(Canvas canvas) {
		 Log.i(TAG, "dispatchDraw child count = " + this.getChildCount());

		if (this.mPositionManager != null && this.mPositionManager.getParams().isBackground()) {
			this.mPositionManager.drawFrame(canvas);
		}

		super.dispatchDraw(canvas);
		if (this.mPositionManager.getSelectedItem() == null && getSelectedView() != null && hasFocus()) {
			this.mPositionManager.setSelectedItem((ItemInterface) getSelectedView());
			performItemSelect(getSelectedView(), mCurrentPosition, true);
		}
		if (this.mPositionManager != null && !this.mPositionManager.getParams().isBackground()) {
			Log.i(TAG, "dispatchDraw -- drawFrame");
			this.mPositionManager.drawFrame(canvas);
		}
		if (this.mPositionManager.getState() == FocusedBasePositionManager.STATE_IDLE) {
			synchronized (this) {
				refreshPosition = -1;
			}
		}

		// if(this.mPositionManager.isLastFrame() && mNeedDrawChild){
		// FocusedHorizontalListView.this.drawChild(canvas, getSelectedView(),
		// getDrawingTime());
		// mNeedDrawChild = false;
		// }
	}

	private void setScrolling(boolean scrolling) {
		synchronized (this.lock) {
			this.isScrolling = scrolling;
			this.mPositionManager.setScrolling(scrolling);
		}
	}

	private boolean isScrolling() {
		synchronized (this.lock) {
			return this.isScrolling;
		}
	}

	private static final int DRAW_FOCUS = 1;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DRAW_FOCUS:
				Log.i(TAG, "Handler handleMessage");
				if (getSelectedView() != null) {
					performItemSelect(getSelectedView(), mCurrentPosition, true);
				} else {
					sendEmptyMessageDelayed(DRAW_FOCUS, SCROLLING_DELAY);
				}

				mPositionManager.setTransAnimation(false);
				mPositionManager.setContrantNotDraw(false);
				mPositionManager.setScaleCurrentView(true);

				mPositionManager.setSelectedItem((ItemInterface) getSelectedView());

				mPositionManager.startDraw();

				if (!isScrolling()) {
					invalidate();
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void setSelection(int position) {
		// super.setSelection(position);

		int top = 0;
		View lastSelectedView = getSelectedView();
		if (lastSelectedView != null) {
			top = lastSelectedView.getTop() - this.getPaddingTop();
		} else {
			top = this.getPaddingTop();
		}
		super.setSelectionFromTop(position, top);
		this.mLastPosition = this.mCurrentPosition;
		this.mCurrentPosition = position;
		
		//忽然跳到某个位置上。上次位置回调选中false状态
		if (refreshPosition > -1 && mCurrentPosition != mLastPosition) {
			if (lastSelectedView != null) {
				performItemSelect(lastSelectedView, mLastPosition, false);
			}
//			this.requestLayout();
		}
		
		Log.i(TAG, "setSelection = mCurrentPosition:" + position + ",top:" + top + ",getSelectedView().getTop():"
				+ (getSelectedView() != null ? getSelectedView().getTop() + "" : "null") + ",this.getPaddingTop():" + this.getPaddingTop()
				+ ",getCount():" + this.getCount() + ", lastpos:" + mLastPosition);
	}
	
	@Override
	public int getSelectedItemPosition() {
		return mCurrentPosition;
	}

	public View getSelectedView() {
		int pos = mCurrentPosition;
		int indexOfView = pos - getFirstVisiblePosition();
		View selectedView = getChildAt(indexOfView);

		return selectedView;
	}

	public void setAutoSearchFocus(boolean autoSearchFocus) {
		this.mAutoSearchFocus = autoSearchFocus;
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		Log.i(TAG,
				"onFocusChanged,gainFocus:" + gainFocus + ", mCurrentPosition = " + mCurrentPosition + ", child count = "
						+ this.getChildCount());
		if (mAutoSearchFocus) {
			super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		} else {
		    if(getOnFocusChangeListener() != null){
		        getOnFocusChangeListener().onFocusChange(this, gainFocus);
		    }
		}
		synchronized (this) {
			mKeyTime = System.currentTimeMillis();
		}

		this.mPositionManager.setFocus(gainFocus || mForcedFocus);
		if (!gainFocus) {
			if (!mForcedFocus) {
				this.mPositionManager.stopDraw();
				this.mPositionManager.reset();
				this.mPositionManager.setSelectedItem(null);
				this.mLastPosition = this.mCurrentPosition;
				mCurrentPosition = this.getSelectedItemPosition();
				this.mPositionManager.setFocusDrawableVisible(false, true);
				this.mPositionManager.setFocusDrawableShadowVisible(false, true);
				this.mPositionManager.setTransAnimation(false);
				this.mPositionManager.setScaleCurrentView(false);
				this.mPositionManager.drawUnscale();
				if (getSelectedView() != null) {
					performItemSelect(getSelectedView(), mCurrentPosition, gainFocus);
				}
			}
		} else {
			if (!mForcedFocus || mCurrentPosition < 0) {
				mCurrentPosition = super.getSelectedItemPosition();
				mCurrentPosition = mCurrentPosition > -1 && mCurrentPosition < this.getCount() ? mCurrentPosition : 0;
				Log.i(TAG, "onFocusChanged mCurrentPosition = " + mCurrentPosition);
				setSelection(mCurrentPosition);
				this.mPositionManager.setScaleCurrentView(true);
				this.mPositionManager.stopDraw();
				this.mPositionManager.reset();
				this.mPositionManager.setFocusDrawableVisible(true, true);
				this.mPositionManager.setFocusDrawableShadowVisible(true, true);
				this.mPositionManager.setTransAnimation(false);
				View selectView = getSelectedView();
				if (selectView != null) {
					this.mPositionManager.setSelectedItem((ItemInterface) selectView);
					performItemSelect(selectView, mCurrentPosition, gainFocus);
				}

				this.mPositionManager.setState(FocusedBasePositionManager.STATE_DRAWING);
				this.mPositionManager.startDraw();
			}	
		}

		invalidate();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (getSelectedView() != null && getSelectedView().onKeyUp(keyCode, event)) {
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
			performItemClick();
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			return true;
		}

		Log.i(TAG, "onKeyUp super:" + keyCode);
		return super.onKeyUp(keyCode, event);
	}

	/**
	 * 按键按下时的监听
	 */
	private onKeyDownListener onKeyDownListener;

	/**
	 * 设置按键监听回调
	 * 
	 * @param l
	 *            按键监听回调 {@link #onKeyDownListener}
	 */
	public void setOnKeyDownListener(onKeyDownListener l) {
		onKeyDownListener = l;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (onKeyDownListener != null && onKeyDownListener.onKeyDown(keyCode, event)) {
			return true;
		}
		if (keyCode != KeyEvent.KEYCODE_DPAD_UP && keyCode != KeyEvent.KEYCODE_DPAD_DOWN) {
			return super.onKeyDown(keyCode, event);
		}

		synchronized (this) {
			if (System.currentTimeMillis() - mKeyTime <= KEY_INTERVEL
					|| this.mPositionManager.getState() == FocusedBasePositionManager.STATE_DRAWING || isScrolling()) {
				Log.d(TAG, "onKeyDown KeyInterval:" + (System.currentTimeMillis() - mKeyTime <= KEY_INTERVEL) + ", getState():"
						+ (this.mPositionManager.getState() == 1 ? "drawing" : "idle") + ", isScrolling: = " + isScrolling);
				return true;
			}
			mKeyTime = System.currentTimeMillis();
		}

		if (getSelectedView() != null && getSelectedView().onKeyDown(keyCode, event)) {
			return true;
		}

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			if (!arrowScroll(FOCUS_UP)) {
				Log.d(TAG, "onKeyDown up super.onkeydown");
				return super.onKeyDown(keyCode, event);
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_DOWN:

			if (!arrowScroll(FOCUS_DOWN)) {
				Log.d(TAG, "onKeyDown down super.onkeydown");
				return super.onKeyDown(keyCode, event);
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public boolean arrowScroll(int direction) {
		if (mScrollDistance <= 0) {
			if (this.getChildCount() > 0) {
				mScrollDistance = this.getChildAt(0).getHeight() + this.getDividerHeight();
			}
		}

		View lastSelectedView = getSelectedView();
		View mCurrentView;
		int lastPosition = mCurrentPosition;

		boolean isNeedTrans = true;
		int scrollBy = 0;
		int paddedTop = this.getListPaddingTop();
		int paddedBottom = getHeight() - getListPaddingBottom();
		// Log.d(TAG,"paddedTop:" + paddedTop + "paddedBottom:" + paddedBottom);
		switch (direction) {
		case FOCUS_UP:
			if (mCurrentPosition > 0) {
				mCurrentPosition--;
				mCurrentView = this.getChildAt(mCurrentPosition - getFirstVisiblePosition());

				// TODO
				if (mCurrentView != null) {
					int targetTop = mCurrentView.getTop();
					if (targetTop < paddedTop) {
						scrollBy = targetTop - paddedTop;
					}
				} else {
					scrollBy = -mScrollDistance;
				}

				if (scrollBy != 0) {
					mLastScrollDirection = mScrollDirection;
					mScrollDirection = SCROLL_UP;
				}
			} else {
				return false;
			}
			break;
		case FOCUS_DOWN:
			if (mCurrentPosition < getCount() - 1) {
				mCurrentPosition++;
				mCurrentView = this.getChildAt(mCurrentPosition - getFirstVisiblePosition());
				// TODO
				// Log.d(TAG,"mCurrentView:" + mCurrentView);
				if (mCurrentView != null) {
					int targetBottom = mCurrentView.getBottom();
					if (targetBottom > paddedBottom) {
						scrollBy = targetBottom - paddedBottom;
					}
				} else {
					Log.d(TAG, "mScrollDistance:" + mScrollDistance);
					scrollBy = mScrollDistance;
				}

				if (scrollBy != 0) {
					mLastScrollDirection = mScrollDirection;
					mScrollDirection = SCROLL_DOWN;
				}
			} else {
				return false;
			}
			break;
		}
		Log.i(TAG, "arrowScroll scrollBy = " + scrollBy + " mCurrentPosition = " + mCurrentPosition);

		playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
		if (lastPosition != mCurrentPosition) {
			this.mLastPosition = lastPosition;
		}

		if (lastSelectedView != null) {
			performItemSelect(lastSelectedView, lastPosition, false);
		}

		if (getSelectedView() != null && getSelectedView() != lastSelectedView && lastPosition != mCurrentPosition) {
			performItemSelect(getSelectedView(), mCurrentPosition, true);
		}
		// setSelection(mCurrentPosition);

		boolean isScaleLastView = true;
		boolean isScaleCurrentView = true;
		this.mPositionManager.stopDraw();
		this.mPositionManager.reset();

		this.mPositionManager.setSelectedItem((ItemInterface) getSelectedView());
		this.mPositionManager.setTransAnimation(isNeedTrans);
		this.mPositionManager.setState(FocusedBasePositionManager.STATE_DRAWING);
		if (scrollBy != 0) {
			this.mPositionManager.setContrantNotDraw(true);
			mNeedScroll = true;
			this.mPositionManager.setSelectedItem(null);
			this.mPositionManager.setFocusMove(false);
			arrowSmoothScroll(scrollBy);
			mHandler.sendEmptyMessageDelayed(DRAW_FOCUS, SCROLLING_DELAY);
		} else {
			setSelection(mCurrentPosition);
			this.mPositionManager.setFocusMove(true);
			this.mPositionManager.setContrantNotDraw(false);
			this.mPositionManager.setScaleCurrentView(isScaleCurrentView);
			this.mPositionManager.startDraw();
		}

		return true;
	}

	// 上一次滚动的距离
	// public int getLastFlingY() {
	// try{
	// Field flingRunnable =
	// getClass().getSuperclass().getSuperclass().getDeclaredField("mFlingRunnable");
	// flingRunnable.setAccessible(true);
	// Field lastFlingY =
	// flingRunnable.getType().getDeclaredField("mLastFlingY");
	// lastFlingY.setAccessible(true);
	// int actualY = lastFlingY.getInt(flingRunnable.get(this));
	// Log.d(TAG,"getActualY:" + actualY);
	// return actualY;
	// } catch (Exception e){
	// e.printStackTrace();
	// }
	// return 0;
	// }
	//
	// void endFling() {
	// try{
	// Field flingRunnable =
	// getClass().getSuperclass().getSuperclass().getDeclaredField("mFlingRunnable");
	// flingRunnable.setAccessible(true);
	// Method end = flingRunnable.getType().getDeclaredMethod("endFling");
	// end.setAccessible(true);
	// end.invoke(flingRunnable);
	// } catch (Exception e){
	// e.printStackTrace();
	// }
	// }

	// int mScrollY = 0;

	// TODO:增加滚动打断
	void arrowSmoothScroll(int scrollBy) {
		mNeedScroll = scrollBy != 0;
		smoothScrollBy(scrollBy, mScrollDuration);
	}

	// void arrowSmoothScroll(int scrollBy) {
	// boolean isScrolling = isScrolling();
	// Log.d(TAG,"isScrolling:" + isScrolling);
	// endFling();
	//
	// int lastFlyingY = getLastFlingY();
	// int currY = getLastFlingY();
	// Log.d(TAG, "arrowSmoothScroll currY = " + currY + ", mCurrentPosition = "
	// + mCurrentPosition + ", isScrolling = "
	// + isScrolling + ", scrollBy = " + scrollBy);
	// if (mScrollY < 0) {
	// currY -= Integer.MAX_VALUE;
	// }
	//
	// Log.d(TAG, "arrowSmoothScroll currY = " + currY + ", mScrolly = " +
	// mScrollY + ", scrollBy = " + scrollBy);
	// mScrollY -= currY;
	// if (mScrollDirection != mLastScrollDirection) {
	// mScrollY = 0;
	// currY = 0;
	// }
	// if (mScrollY != 0 && isScrolling && mScrollDirection ==
	// mLastScrollDirection) {
	// if (scrollBy > 0) {
	// scrollBy = mScrollDistance;
	// } else {
	// scrollBy = -mScrollDistance;
	// }
	//
	// mNeedScroll = false;
	//
	// } else {
	// mNeedScroll = true;
	// }
	//
	// // checkSelection();
	// mScrollY += scrollBy;
	// Log.d(TAG, "arrowSmoothScroll mScrollY = " + mScrollY);
	// smoothScrollBy(scrollBy, this.mScrollDuration);
	// }

	// void checkSelection() {
	// for (int pos = mCurrentPosition; pos <= getLastVisiblePosition(); pos +=
	// 1) {
	// View child = this.getChildAt(pos - getFirstVisiblePosition());
	// if (null == child) {
	// continue;
	// }
	// Log.d(TAG, "arrowSmoothScroll child.getTop() = " + child.getTop() +
	// ", child.getBottom() = " + child.getBottom()
	// + ", this.getTop() = " + this.getTop() + ", this.getBottom() = " +
	// this.getBottom() + ", pos = " + pos);
	// if (child.getTop() >= this.getTop() && child.getBottom() <=
	// this.getBottom()) {
	// Log.d(TAG, "arrowSmoothScroll child.getTop() = " + child.getTop() +
	// ", child.getBottom() = " + child.getBottom()
	// + ", pos = " + pos);
	// super.setSelection(pos);
	// return;
	// }
	// }
	//
	// for (int pos = mCurrentPosition; pos >= getFirstVisiblePosition(); pos -=
	// 1) {
	// View child = this.getChildAt(pos - getFirstVisiblePosition());
	// if (null == child) {
	// continue;
	// }
	// Log.d(TAG, "arrowSmoothScroll child.getTop() = " + child.getTop() +
	// ", child.getBottom() = " + child.getBottom()
	// + ", this.getTop() = " + this.getTop() + ", this.getBottom() = " +
	// this.getBottom() + ", pos = " + pos);
	// if (child.getTop() >= this.getTop() && child.getBottom() <=
	// this.getBottom()) {
	// Log.d(TAG, "arrowSmoothScroll child.getTop() = " + child.getTop() +
	// ", child.getBottom() = " + child.getBottom()
	// + ", pos = " + pos);
	// super.setSelection(pos);
	// return;
	// }
	// }
	// }

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

	/**
	 * 按键按下时的监听
	 */
	public interface onKeyDownListener {
		/**
		 * 按键按下时的回调监听
		 * 
		 * @param keyCode
		 * @param event
		 * @return 如果返回true,则GridView不再处理按键事件,否则会处理
		 */
		public boolean onKeyDown(int keyCode, KeyEvent event);
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
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		Log.i(TAG, "onLayout -- changed :" + changed + ", refreshPosition:" + refreshPosition);
		
		
		//忽然跳到某位置，调用refreshPosition之后
		if (refreshPosition > -1) {
			if (refreshPosition >= getChildCount()) {
				synchronized (this) {
					refreshPosition = -1;
				}
				return;
			}
			
			View selectedView = getSelectedView();
			if (selectedView == null || mCurrentPosition == mLastPosition) {
				return;
			}
			
			this.mPositionManager.stopDraw();
			this.mPositionManager.reset();
			
			this.mPositionManager.setTransAnimation(false);
			this.mPositionManager.setSelectedItem((ItemInterface)selectedView);
//			this.mPositionManager.setLastSelectedItem(null);
//			this.mPositionManager.setScaleLastView(false);
			this.mPositionManager.setScaleCurrentView(true);
			this.mPositionManager.drawUnscale();
			performItemSelect(selectedView, mCurrentPosition, true);
//			this.mPositionManager.setNeedDraw(true);
//			this.mPositionManager.setContrantNotDraw(f);
			this.mPositionManager.setState(FocusedBasePositionManager.STATE_DRAWING);
			this.mPositionManager.startDraw();
		}
	}
	
	private int refreshPosition = -1;
	/**
	 * 忽然跳到某个位置上。
	 * @param pos 新位置下标
	 */
	public void refreshPosition(int pos) {
		synchronized (this) {
			refreshPosition = pos;
		}
		setSelection(pos);
	}
	
}
