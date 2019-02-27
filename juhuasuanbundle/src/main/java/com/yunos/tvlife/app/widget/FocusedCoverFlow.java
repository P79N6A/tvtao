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
import android.widget.AdapterView;

import com.yunos.tvlife.app.widget.FocusedBasePositionManager.ContainInterface;
import com.yunos.tvlife.app.widget.FocusedBasePositionManager.FocusItemSelectedListener;
import com.yunos.tvlife.app.widget.FocusedBasePositionManager.FocusParams;
import com.yunos.tvlife.app.widget.FocusedBasePositionManager.ItemInterface;
import com.yunos.tvlife.app.widget.FocusedBasePositionManager.PositionInterface;
import com.yunos.tvlife.lib.LOG;

public class FocusedCoverFlow extends CoverFlow implements PositionInterface, ContainInterface {
	private static final String TAG = "FocusedCoverFlow";
	private static final boolean DEBUG = true;
	private static final int SCROLL_LEFT = 0;
	private static final int SCROLL_RIGHT = 1;

	protected int mCurrentPosition = INVALID_POSITION;
	private int mLastPosition = -1;
	private FocusedBasePositionManager mPositionManager;
	private AdapterView.OnItemClickListener mOnItemClickListener = null;
	private FocusItemSelectedListener mOnItemSelectedListener = null;
	private boolean mIsFocusInit = false;
	private long mKeyTime = 0;
	private int mDistance = -1;
	private int mScrollDirection = SCROLL_LEFT;
	private int mLastScrollDirection = SCROLL_LEFT;

	public FocusedCoverFlow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public FocusedCoverFlow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FocusedCoverFlow(Context context) {
		super(context);
		init(context);
	}

	@Override
	public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
		this.mOnItemClickListener = listener;
	}

	@Override
	public void setOnItemSelectedListener(FocusItemSelectedListener listener) {
		this.mOnItemSelectedListener = listener;
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

	@Override
	protected void OnScrolling(boolean status) {
		this.mPositionManager.setScrolling(status);
	}
	
	@Override
	public void dispatchDraw(Canvas canvas) {
		LOG.d(TAG, DEBUG, "dispatchDraw child count = " + this.getChildCount() + ", first position = " + this.getFirstVisiblePosition()
				+ ", last posititon = " + this.getLastVisiblePosition() + ", mCurrentPosition = " + mCurrentPosition);
		super.dispatchDraw(canvas);

		if (this.mPositionManager.getSelectedItem() == null && getSelectedView() != null && hasFocus()) {
			this.mPositionManager.setSelectedItem((ItemInterface)getSelectedView());
			performItemSelect(getSelectedView(), mCurrentPosition, true);
		}

		if (getSelectedView() != null) {
			this.mPositionManager.drawFrame(canvas);
		}

		for (int index = 0; index < getChildCount(); index++) {
			drawChildInside(canvas, getChildAt(index), getDrawingTime());
		}
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		LOG.i(TAG,
				DEBUG,
				"onFocusChanged,gainFocus:" + gainFocus + ", mCurrentPosition = " + mCurrentPosition + ", child count = "
						+ this.getChildCount());

		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

		forceGainFocues(gainFocus, false);
	}

	public void forceGainFocues(boolean gainFocus, boolean isForced) {
		synchronized (this) {
			mKeyTime = System.currentTimeMillis();
		}

		if (gainFocus != this.mPositionManager.hasFocus()) {
			mIsFocusInit = false;
		}
		this.mPositionManager.setFocus(gainFocus);
		if (isForced) {
			setForceGainFocus(gainFocus);
		}
		focusInit(gainFocus);
	}

	void init(Context context) {
		Log.i(TAG, "init mCurrentPosition11:" + mCurrentPosition);
		setChildrenDrawingOrderEnabled(true);
		setDrawTextInside(false);
		Log.i(TAG, "init mCurrentPosition12:" + mCurrentPosition);
	}

	private void focusInit(boolean gainFocus) {
		if (mIsFocusInit) {
			return;
		}

		if (mCurrentPosition < 0) {
			mCurrentPosition = getSelectedItemPosition();
		}

		if (mCurrentPosition < 0) {
			mHandler.sendEmptyMessageDelayed(FOCUS_CHANGE, 100);
			return;
		}

		LOG.i(TAG, DEBUG, "focusInit mCurrentPosition = " + mCurrentPosition);
		if (mCurrentPosition < 0) {
			mCurrentPosition = 0;
		}

		if (!gainFocus) {
			this.mPositionManager.stopDraw();
			this.mPositionManager.reset();
			this.mPositionManager.setSelectedItem(null); 
			this.mLastPosition = this.mCurrentPosition;
			this.mPositionManager.setFocusDrawableVisible(false, true);
			this.mPositionManager.setFocusDrawableShadowVisible(false, true);
			this.mPositionManager.setTransAnimation(false);
			this.mPositionManager.setScaleCurrentView(false);
			this.mPositionManager.drawUnscale();
		} else {
			// mCurrentPosition = super.getSelectedItemPosition();
			this.mPositionManager.setScaleCurrentView(true);
			if (getSelectedView() != null) {
				this.mPositionManager.setSelectedItem((ItemInterface) getSelectedView());
				performItemSelect(getSelectedView(), mCurrentPosition, gainFocus);
				
				if (mCurrentPosition >= 0) {
					mIsFocusInit = true;
				}
			} 
			
			this.mPositionManager.setFocusDrawableVisible(true, true);
			this.mPositionManager.setFocusDrawableShadowVisible(true, true);
			this.mPositionManager.setState(FocusedBasePositionManager.STATE_DRAWING);
			this.mPositionManager.startDraw();
		}

		invalidate();
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

			return super.onKeyUp(keyCode, event);
		}

		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getRepeatCount() == 0) {
			isKeyDown = true;
		}

		if (getSelectedView() == null) { // by leiming.yanlm
			return true;
		}

		if (mDistance < 0) {
			mDistance = getSelectedView().getWidth() + mSpacing;
		}

		if (keyCode != KeyEvent.KEYCODE_DPAD_RIGHT && keyCode != KeyEvent.KEYCODE_DPAD_LEFT) {
			return super.onKeyDown(keyCode, event);
		}

		synchronized (this) {
			if (this.mPositionManager.getState() == FocusedBasePositionManager.STATE_DRAWING) {
				return true;
			}
			mKeyTime = System.currentTimeMillis();
		}

		if (getSelectedView() != null && getSelectedView().onKeyDown(keyCode, event)) {
			return true;
		}

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (!arrowScroll(FOCUS_LEFT)) {
				Log.d(TAG, "onKeyDown left super.onkeydown");
				return super.onKeyDown(keyCode, event);
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:

			if (!arrowScroll(FOCUS_RIGHT)) {
				Log.d(TAG, "onKeyDown right super.onkeydown");
				return super.onKeyDown(keyCode, event);
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public boolean arrowScroll(int direction) {
		View lastSelectedView = getSelectedView();
		int lastPosition = mCurrentPosition;
		boolean isScroll = false;
		boolean isToleft = false;
		boolean isTrans = true;
		boolean isNeedAni = true;
		// boolean isNeedTrans = true;
		switch (direction) {
		case FOCUS_LEFT:
			if (mCurrentPosition > 0) {
				mCurrentPosition--;
				mCurrentSelectedPosition = mCurrentPosition;
				int selectedPos = mSelectedPosition - getFirstVisiblePosition();
				int visableDiff = selectedPos - getMidItemCount() / 2;
				int leftMidPos = getFirstVisiblePosition() + visableDiff;
				if (mCurrentPosition < leftMidPos) {
					isScroll = true;
					isToleft = true;
					mLastScrollDirection = mScrollDirection;
					mScrollDirection = SCROLL_LEFT;
				}
			} else {
				if (mCurrentPosition == mSelectedPosition) {
					return false;
				} else {
					mCurrentSelectedPosition = 0;
					isNeedAni = false;
					isScroll = true;
					isToleft = true;
					mLastScrollDirection = mScrollDirection;
					mScrollDirection = SCROLL_LEFT;
				}
			}
			break;
		case FOCUS_RIGHT:
			if (mCurrentPosition < getCount() - 1) {
				mCurrentPosition++;
				mCurrentSelectedPosition = mCurrentPosition;
				int selectedPos = mSelectedPosition;
				int rightMidPos = selectedPos + getMidItemCount() / 2;
				// int rightMidPos = getLastVisiblePosition() + visableDiff;
				if (mCurrentPosition > rightMidPos) {
					isScroll = true;
					isToleft = false;
					mLastScrollDirection = mScrollDirection;
					mScrollDirection = SCROLL_RIGHT;
				}
			} else {
				if (mCurrentPosition == mSelectedPosition) {
					return false;
				} else {
					isNeedAni = false;
					isScroll = true;
					isToleft = false;
					mLastScrollDirection = mScrollDirection;
					mScrollDirection = SCROLL_RIGHT;
				}
			}
			break;
		}

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

		if (isScroll) {
			arrowScroll(isToleft);
			isTrans = false;
		}
		// setSelection(mCurrentPosition);

		// setSelectedPositionInt(mCurrentPosition);
		if (isNeedAni) {
			this.mPositionManager.stopDraw();
			this.mPositionManager.reset();
			this.mPositionManager.setState(FocusedBasePositionManager.STATE_DRAWING);
			if (getSelectedView() != null) {
				boolean isScaleLastView = true;
				boolean isScaleCurrentView = true;
				this.mPositionManager.setSelectedItem((ItemInterface)getSelectedView());
				this.mPositionManager.setTransAnimation(isTrans);
				this.mPositionManager.setFocusMove(isTrans);
				// setSelection(mCurrentPosition);
				this.mPositionManager.setContrantNotDraw(false);
				this.mPositionManager.setScaleCurrentView(isScaleCurrentView);
				this.invalidate();
			} else {
				this.mPositionManager.setContrantNotDraw(true);
				mHandler.sendEmptyMessageDelayed(DRAW_FOCUS, 10);
				this.mPositionManager.setSelectedItem(null);
				this.mPositionManager.setFocusMove(isTrans);
				this.mPositionManager.startDraw();
			}
		}

		return true;
	}

	int mScrollX = 0;

	void arrowScroll(boolean isLeft) {
		endFling();
		int distance = mDistance;
		int x = getActualX();

		mScrollX -= x;
		if (mScrollDirection != mLastScrollDirection) {
			mScrollX = 0;
			x = 0;
		}
		if (isLeft) {
			distance = -distance;
		}

		mScrollX += distance;
		if (isLeft) {
			movePrevious(mScrollX);
		} else {
			moveNext(mScrollX);
		}
	}

	boolean movePrevious(int distane) {
		scrollPosition = mSelectedPosition - 1;
		smoothScrollBy(distane, 1000);
		return true;
	}

	boolean moveNext(int distane) {
		scrollPosition = mSelectedPosition + 1;
		smoothScrollBy(distane, 1000);
		return true;

	}

	private static final int DRAW_FOCUS = 1;
	private static final int FOCUS_CHANGE = 2;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DRAW_FOCUS:
				if (getSelectedView() != null) {
					performItemSelect(getSelectedView(), mCurrentPosition, true);
				} else {
					Log.w(TAG, "Handler handleMessage selected view is null delay");
					sendEmptyMessageDelayed(DRAW_FOCUS, 10);
					return;
				}
				boolean isScaleLastView = true;
				boolean isScaleCurrentView = true;
				mPositionManager.setTransAnimation(false);

				mPositionManager.setState(FocusedBasePositionManager.STATE_DRAWING);
				mPositionManager.setContrantNotDraw(false);
				mPositionManager.setScaleCurrentView(isScaleCurrentView);
				mPositionManager.startDraw();
				invalidate();
				break;
			case FOCUS_CHANGE:
				focusInit(hasFocus());
			}
		}
	};

	@Override
	protected void scrollIntoSlots() {

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
	public void createPositionManager(FocusParams params) {
		this.mPositionManager = FocusedBasePositionManager.createPositionManager(params, this);
	}
}
