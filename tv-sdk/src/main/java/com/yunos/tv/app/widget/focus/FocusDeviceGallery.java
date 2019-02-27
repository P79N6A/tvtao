package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;

import com.yunos.tv.app.widget.DeviceGallery;
import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.FocusDrawStateListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;

import java.util.ArrayList;

public class FocusDeviceGallery extends DeviceGallery implements DeepListener, ItemListener {
	protected static final String TAG = "DeviceGallery";

	protected Params mParams = new Params(1.1f, 1.1f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());
	FocusRectParams mFocusRectparams = new FocusRectParams();
	protected Rect mClipFocusRect = new Rect();
	
	boolean mIsAnimate = true;
	int mDistance = -1;
	boolean mDeepFocus = false;
	boolean mAutoSearch = false;
	ItemSelectedListener mItemSelectedListener;
	boolean mLayouted = false;
	boolean mReset = false;
	boolean mFocusBackground = false;
	boolean mAimateWhenGainFocusFromLeft = true;
	boolean mAimateWhenGainFocusFromRight = true;
	boolean mAimateWhenGainFocusFromUp = true;
	boolean mAimateWhenGainFocusFromDown = true;
	
	protected FocusDrawStateListener mFocusDrawStateListener = null;

	public FocusDeviceGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public FocusDeviceGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public FocusDeviceGallery(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void setOnFocusDrawStateListener(FocusDrawStateListener l){
		mFocusDrawStateListener = l;
	}
	
	public void setAnimateWhenGainFocus(boolean fromleft, boolean fromUp, boolean fromRight, boolean fromDown){
		mAimateWhenGainFocusFromLeft = fromleft;
		mAimateWhenGainFocusFromUp = fromUp;
		mAimateWhenGainFocusFromRight = fromRight;
		mAimateWhenGainFocusFromDown = fromDown;
	}
	
	public void setFocusBackground(boolean back){
		mFocusBackground = back;
	}

	public void setOnItemSelectedListener(ItemSelectedListener listener) {
		this.mItemSelectedListener = listener;
	}
	
	@Override
	public boolean add(int position) {
		boolean hr = super.add(position);
		if (hr) {
			reset();
		}
		
		return hr;
	}
	
	@Override
	public boolean remove(int position) {
		boolean hr = super.remove(position);
		if (hr) {
			reset();
		}
		
		return hr;
	}

	public void reset() {
		ItemListener item = (ItemListener) getSelectedView();
		if (item != null) { // by leiming.yanlm
			mFocusRectparams.set(item.getFocusParams());
			offsetDescendantRectToMyCoords(getSelectedView(), mFocusRectparams.focusRect());
		}
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		if (!mAutoSearch) {
			if (getOnFocusChangeListener() != null) {
				getOnFocusChangeListener().onFocusChange(this, gainFocus);
			}
		}
		
		if (mAutoSearch) {
			super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		}
		if (gainFocus && getChildCount() > 0 && mLayouted) {
			// getFocusParams();
			reset();
		}

		mIsAnimate = checkAnimate(direction);
	}
	
	private boolean checkAnimate(int direction){
		switch (direction) {
		case View.FOCUS_LEFT:
			return mAimateWhenGainFocusFromRight ? true : false;
		case View.FOCUS_UP:
			return mAimateWhenGainFocusFromDown ? true : false;
		case View.FOCUS_RIGHT:
			return mAimateWhenGainFocusFromLeft ? true : false;
		case View.FOCUS_DOWN:
			return mAimateWhenGainFocusFromUp ? true : false;
		}
		
		return true;
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (hasFocus() || hasDeepFocus()) {
			reset();
		}

		mLayouted = true;
		mClipFocusRect.set(0, 0, getWidth(), getHeight());
	}

	@Override
	public void requestLayout() {
		super.requestLayout();
		mLayouted = false;
	}

	@Override
	public void getFocusedRect(Rect r) {
		if (hasFocus() || hasDeepFocus()) {
			super.getFocusedRect(r);
			return;
		}

		getDrawingRect(r);
	}

	@Override
	public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
		if (hasFocus()) {
			super.addFocusables(views, direction, focusableMode);
			return;
		}

		if (views == null) {
			return;
		}
		if (!isFocusable()) {
			return;
		}
		if ((focusableMode & FOCUSABLES_TOUCH_MODE) == FOCUSABLES_TOUCH_MODE && isInTouchMode() && !isFocusableInTouchMode()) {
			return;
		}
		views.add(this);
	}

	@Override
	public FocusRectParams getFocusParams() {
		if(mNeedReset || isScrolling()){
			reset();
		}
		return mFocusRectparams;
	}

	private void performSelect(boolean select) {
		if (mItemSelectedListener != null) {
			mItemSelectedListener.onItemSelected(getSelectedView(), getSelectedItemPosition(), select, this);
		}
	}

	@Override
	public boolean canDraw() {
		if(mItemCount <= 0){
			return false;
		}
		
		View v = getSelectedView();
		if (v != null && mReset) {
			performSelect(true);
			mReset = false;
		}
		return getSelectedView() != null && mLayouted;
	}

	@Override
	public boolean isAnimate() {
		return mIsAnimate && (!(isAdding() || isRemoving()));
	}

	@Override
	public ItemListener getItem() {
		return (ItemListener) getSelectedView();
	}

	@Override
	public boolean isScrolling() {
		return isAdding() || isRemoving() || isFling();
	}

	@Override
	public Params getParams() {
		if (mParams == null) {
			throw new IllegalArgumentException("The params is null, you must call setScaleParams before it's running");
		}

		return mParams;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(isAdding() || isRemoving()){
			return true;
		}
		
		if (getChildCount() <= 0) {
			return super.onKeyDown(keyCode, event);
		}

		if (mDistance < 0) {
//			mDistance = getChildAt(0).getHeight();
			mDistance = getChildAt(0).getWidth();
		}

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (moveLeft()) {
				playSoundEffect(SoundEffectConstants.NAVIGATION_LEFT);
				return true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (moveRight()) {
				playSoundEffect(SoundEffectConstants.NAVIGATION_LEFT);
				return true;
			}
			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	private boolean moveLeft() {
//		if (Math.abs(getLeftScrollDistance()) > getChildAt(0).getHeight() * 3) {
		if (Math.abs(getLeftScrollDistance()) > getChildAt(0).getWidth() * 3) {
			return true;
		}

		performSelect(false);
		mReset = false;
		int nextSelectedPosition = getSelectedItemPosition() - 1 >= 0 ? getSelectedItemPosition() - 1 : INVALID_POSITION;
		if (nextSelectedPosition != INVALID_POSITION) {
			setSelectedPositionInt(nextSelectedPosition);
			setNextSelectedPositionInt(nextSelectedPosition);
			if (canDraw()) {
				mReset = false;
				performSelect(true);
			} else {
				mReset = true;
			}

			int amountToScroll = amountToCenterScroll(FOCUS_LEFT, nextSelectedPosition);

			// if (mIsAnimate) {
			// reset();
			// if (amountToScroll != 0) {
			// smoothScrollBy(amountToScroll);
			// mFocusRectparams.focusRect().top -= amountToScroll;
			// mFocusRectparams.focusRect().bottom -= amountToScroll;
			// }
			// }
			return true;
		}

		return false;
	}

	private boolean moveRight() {
//		if (getLeftScrollDistance() > getChildAt(0).getHeight() * 3) {
		if (getLeftScrollDistance() > getChildAt(0).getWidth() * 3) {
			return true;
		}
		performSelect(false);
		mReset = false;
		int nextSelectedPosition = getSelectedItemPosition() + 1 < mItemCount ? getSelectedItemPosition() + 1 : INVALID_POSITION;
		if (nextSelectedPosition != INVALID_POSITION) {
			setSelectedPositionInt(nextSelectedPosition);
			setNextSelectedPositionInt(nextSelectedPosition);
			if (canDraw()) {
				mReset = false;
				performSelect(true);
			} else {
				mReset = true;
			}

			int amountToScroll = amountToCenterScroll(FOCUS_RIGHT, nextSelectedPosition);

			// if (mIsAnimate) {
			// reset();
			// if (amountToScroll != 0) {
			// smoothScrollBy(amountToScroll);
			// // mFocusRectparams.focusRect().top -= amountToScroll;
			// // mFocusRectparams.focusRect().bottom -= amountToScroll;
			// }
			// }
			return true;
		}

		return false;
	}

	int amountToCenterScroll(int direction, int nextSelectedPosition) {
		int center = (getWidth() - mFixedPaddingLeft - mFixedPaddingRight) / 2 + mFixedPaddingLeft;
		final int listRight = getWidth() - mFixedPaddingRight;
		final int listLeft = mFixedPaddingLeft;
		final int numChildren = getChildCount();
		int amountToScroll = 0;
		int distanceLeft = getLeftScrollDistance();
		if (direction == View.FOCUS_RIGHT) {
			View nextSelctedView = getChildAt(nextSelectedPosition - mFirstPosition);
			int nextSelectedCenter = 0;
			boolean reset = false;
			if (nextSelctedView == null) {
				nextSelctedView = getChildAt(getChildCount() - 1);
				nextSelectedCenter = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
				nextSelectedCenter += (nextSelectedPosition - getLastVisiblePosition()) * nextSelctedView.getWidth();
				nextSelectedCenter += (nextSelectedPosition - getLastVisiblePosition()) * mSpacing;
				reset = false;
			} else {
				nextSelectedCenter = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
				reset = true;
			}

			int finalNextSelectedCenter = nextSelectedCenter - distanceLeft;

			if (finalNextSelectedCenter > center) {
				amountToScroll = finalNextSelectedCenter - center;
				int maxDiff = (mItemCount - getLastVisiblePosition() - 1) * nextSelctedView.getWidth();
				maxDiff += (mItemCount - getLastVisiblePosition() - 1) * mSpacing;
				maxDiff -= distanceLeft;
				View lastVisibleView = getChildAt(numChildren - 1);
				if (lastVisibleView.getRight() > getWidth() - mFixedPaddingRight) {
					maxDiff += (lastVisibleView.getRight() - (getWidth() - mFixedPaddingRight));
				}

				if (amountToScroll > maxDiff) {
					amountToScroll = maxDiff;
				}

				if (reset) {
					reset();
                    offsetFocusRectLeftAndRight(mFocusRectparams, -distanceLeft, -distanceLeft);
				}

				if (amountToScroll > 0) {
					if (reset) {
						offsetFocusRectLeftAndRight(mFocusRectparams, -amountToScroll, -amountToScroll);
					} else {
						offsetFocusRectLeftAndRight(mFocusRectparams, (nextSelctedView.getWidth() + mSpacing - amountToScroll),
								(nextSelctedView.getWidth() + mSpacing - amountToScroll));
					}

					Log.d(TAG, "amountToCenterScroll move right amountToScroll = " + amountToScroll);
					smoothScrollBy(-amountToScroll);
					mIsAnimate = true;
				} else {
					if (!reset) {
						offsetFocusRectLeftAndRight(mFocusRectparams, (nextSelctedView.getWidth() + mSpacing),
								(nextSelctedView.getWidth() + mSpacing));
					}
					mIsAnimate = true;
				}
			} else {
				reset();
				mIsAnimate = true;
			}

			return amountToScroll;
		} else if (direction == View.FOCUS_LEFT) {
			View nextSelctedView = getChildAt(nextSelectedPosition - mFirstPosition);
			int nextSelectedCenter = 0;
			boolean reset = false;
			if (nextSelctedView == null) {
				nextSelctedView = getChildAt(0);
				nextSelectedCenter = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
				nextSelectedCenter -= (getFirstVisiblePosition() - nextSelectedPosition) * nextSelctedView.getWidth();
				nextSelectedCenter -= (getFirstVisiblePosition() - nextSelectedPosition) * mSpacing;

				reset = false;
			} else {
				nextSelectedCenter = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
				reset = true;
			}

			int finalNextSelectedCenter = nextSelectedCenter - distanceLeft;

			if (finalNextSelectedCenter < center) {
				amountToScroll = center - finalNextSelectedCenter;
				int maxDiff = getFirstVisiblePosition() * nextSelctedView.getWidth();
				maxDiff += getFirstVisiblePosition() * mSpacing;

				maxDiff += distanceLeft;
				View firstVisibleView = getChildAt(0);
				if (firstVisibleView.getLeft() < listLeft) {
					maxDiff += (listLeft - firstVisibleView.getLeft());
				}

				if (amountToScroll > maxDiff) {
					amountToScroll = maxDiff;
				}

				if (reset) {
					reset();
                    offsetFocusRectLeftAndRight(mFocusRectparams, -distanceLeft, -distanceLeft);
				}

				if (amountToScroll > 0) {
					if (reset) {
						offsetFocusRectLeftAndRight(mFocusRectparams, amountToScroll, amountToScroll);
					} else {
						offsetFocusRectLeftAndRight(mFocusRectparams, -(nextSelctedView.getWidth() + mSpacing - amountToScroll),
								-(nextSelctedView.getWidth() + mSpacing - amountToScroll));
					}

					smoothScrollBy(amountToScroll);
					mIsAnimate = true;
				} else {
					if (!reset) {
						offsetFocusRectLeftAndRight(mFocusRectparams, -(nextSelctedView.getWidth() + mSpacing), 
								-(nextSelctedView.getWidth() + mSpacing));
					}
					mIsAnimate = true;
				}
			} else {
				reset();
				mIsAnimate = true;
			}

			return amountToScroll;
		}

		return 0;
	}

	@Override
	public boolean preOnKeyDown(int keyCode, KeyEvent event) {
		if(isAdding() || isRemoving()){
			return true;
		}
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_MOVE_HOME: {
			// int nextSelectedPosition = getSelectedItemPosition() - 1 >= 0 ?
			// getSelectedItemPosition() - 1 : INVALID_POSITION;
			// if (nextSelectedPosition != INVALID_POSITION) {
			// final View nextSelctedView = getChildAt(nextSelectedPosition -
			// mFirstPosition);
			// if(nextSelctedView == null){
			// return false;
			// }
			// }
			return getSelectedItemPosition() > 0 ? true : false;
		}
		case KeyEvent.KEYCODE_DPAD_RIGHT:
		case KeyEvent.KEYCODE_MOVE_END: {
			// int nextSelectedPosition = getSelectedItemPosition() + 1 <
			// mItemCount ? getSelectedItemPosition() + 1 : INVALID_POSITION;
			// if (nextSelectedPosition != INVALID_POSITION) {
			// final View nextSelctedView = getChildAt(nextSelectedPosition -
			// mFirstPosition);
			// if(nextSelctedView == null){
			// return false;
			// }
			// }
			return getSelectedItemPosition() < mItemCount - 1 ? true : false;
		}
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			return true;

		default:
			break;
		}

		return false;
	}

	@Override
	public boolean isScale() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getItemWidth() {
		return getWidth();
	}

	@Override
	public int getItemHeight() {
		return getHeight();
	}

	@Override
	public Rect getManualPadding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canDeep() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasDeepFocus() {
		return mDeepFocus;
	}

	@Override
	public void onFocusDeeped(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		mDeepFocus = gainFocus;
		onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	}

	@Override
	public void onItemSelected(boolean selected) {
		performSelect(selected);
	}

	@Override
	public void onItemClick() {
		if (getSelectedView() != null) {
			performItemClick(getSelectedView(), getSelectedItemPosition(), 0);
		}
	}

	@Override
	public boolean isFocusBackground() {
		return mFocusBackground;
	}
	

	@Override
	public void drawBeforeFocus(Canvas canvas) {
		// TODO Auto-generated method stub
	}

	@Override
	public void drawAfterFocus(Canvas canvas) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isFinished() {
		return true;
	}

	@Override
	public Rect getClipFocusRect() {
		return mClipFocusRect;
	}
}
