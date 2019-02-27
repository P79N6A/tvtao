package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.IntevelHListView;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.lib.SystemProUtils;

import java.util.ArrayList;

public class FocusIntevelHListView extends IntevelHListView implements DeepListener, ItemListener {
	protected static final String TAG = "FocusIntevelHListView";
	protected Params mParams = new Params(1.1f, 1.1f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());
	protected FocusRectParams mFocusRectparams = new FocusRectParams();
	
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

	public FocusIntevelHListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public FocusIntevelHListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public FocusIntevelHListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void setAnimateWhenGainFocus(boolean fromleft, boolean fromUp, boolean fromRight, boolean fromDown) {
		mAimateWhenGainFocusFromLeft = fromleft;
		mAimateWhenGainFocusFromUp = fromUp;
		mAimateWhenGainFocusFromRight = fromRight;
		mAimateWhenGainFocusFromDown = fromDown;
	}
	
	public void setAutoSearch(boolean autoSearch) {
		mAutoSearch = autoSearch;
	}

	public void setFocusBackground(boolean back) {
		mFocusBackground = back;
	}

	public void setOnItemSelectedListener(ItemSelectedListener listener) {
		mItemSelectedListener = listener;
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		Log.d(TAG, "onFocusChanged");
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

	private boolean checkAnimate(int direction) {
		switch (direction) {
		case FOCUS_LEFT:
			return mAimateWhenGainFocusFromRight ? true : false;
		case FOCUS_UP:
			return mAimateWhenGainFocusFromDown ? true : false;
		case FOCUS_RIGHT:
			return mAimateWhenGainFocusFromLeft ? true : false;
		case FOCUS_DOWN:
			return mAimateWhenGainFocusFromUp ? true : false;
		}

		return true;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (!hasFocus() && !hasDeepFocus()) {
			Rect rect = new Rect();
			getDrawingRect(rect);
			offsetDescendantRectToMyCoords(this, rect);

			mFocusRectparams.set(rect, 0.5f, 0.5f);
		} else {
			reset();
		}

		mLayouted = true;
		
		mClipFocusRect.set(0, 0, getWidth(), getBottom());
	}

	@Override
	public void requestLayout() {
		super.requestLayout();
		mLayouted = false;
	}

	@Override
	public Params getParams() {
		if (mParams == null) {
			throw new IllegalArgumentException("The params is null, you must call setScaleParams before it's running");
		}

		return mParams;
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

	private void reset() {
		ItemListener item = (ItemListener) getSelectedView();
		if (item != null) { // by leiming.yanlm
			mFocusRectparams.set(item.getFocusParams());
			offsetDescendantRectToMyCoords(getSelectedView(), mFocusRectparams.focusRect());
		}
		
	}

	@Override
	public FocusRectParams getFocusParams() {
		View v = getSelectedView();
		if (v != null) {
			if (mFocusRectparams == null || isScrolling()) {
				reset();
			}
			return mFocusRectparams;
		} else {
			Rect r = new Rect();
			getFocusedRect(r);

			mFocusRectparams.set(r, 0.5f, 0.5f);
			return mFocusRectparams;
		}
	}

	@Override
	public boolean canDraw() {
		View v = getSelectedView();
		if (v != null && mReset) {
			performSelect(true);
			mReset = false;
		}
		return getSelectedView() != null && mLayouted;
	}

	@Override
	public boolean isAnimate() {
		return mIsAnimate;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "onKeyDown keyCode = " + keyCode);
		if (getChildCount() <= 0) {
			return super.onKeyDown(keyCode, event);
		}

		if (mDistance < 0) {
			mDistance = getChildAt(0).getHeight();
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

		if (DEBUG) {
			Log.d(TAG, "commonKey: list = " + convertListToString());
		}

		return super.onKeyDown(keyCode, event);
	}

	private boolean moveLeft() {
		if (Math.abs(getLeftScrollDistance()) > getChildAt(0).getHeight() * 3) {
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

	private void performSelect(boolean select) {
		if (mItemSelectedListener != null) {
			mItemSelectedListener.onItemSelected(getSelectedView(), getSelectedItemPosition(), select, this);
		}
	}

	private boolean moveRight() {
		if (getLeftScrollDistance() > getChildAt(0).getHeight() * 3) {
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
		if (nextSelectedPosition == 7) {
			int a = 0;
			a++;
			a--;
		}

		final int listRight = getWidth() - mListPadding.right;
		final int listLeft = mListPadding.left;
		final int numChildren = getChildCount();
		int amountToScroll = 0;
		int distanceLeft = getLeftScrollDistance();
		int center = ((getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) ? (getWidth() - listLeft - listRight) / 2 + listLeft : getWidth() / 2;
		if (direction == FOCUS_RIGHT) {
			View nextSelctedView = getChildAt(nextSelectedPosition - mFirstPosition);
			int nextSelectedCenter = 0;
			boolean reset = false;
			int intevel = 0;
			if (nextSelctedView == null) {
				nextSelctedView = getChildAt(getChildCount() - 1);
				nextSelectedCenter = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
				Object lastVisibleItem = getAdapter().getItem(getLastVisiblePosition());
				Object lastItem = getAdapter().getItem(nextSelectedPosition - 1);
				Object item = getAdapter().getItem(nextSelectedPosition);
				if (!lastItem.equals(item)) {
					intevel = mIntevel;
				}

				nextSelectedCenter += nextSelctedView.getWidth() * (nextSelectedPosition - getLastVisiblePosition());
				for (int index = getLastVisiblePosition() + 1; index <= nextSelectedPosition; index++) {
					item = getAdapter().getItem(index);
					if (!lastVisibleItem.equals(item)) {
						nextSelectedCenter += mIntevel;
					}

					lastVisibleItem = item;
				}

				reset = false;
			} else {
				nextSelectedCenter = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
				reset = true;
			}

			int finalNextSelectedCenter = nextSelectedCenter - distanceLeft;

			if (finalNextSelectedCenter > center) {
				amountToScroll = finalNextSelectedCenter - center;
				if (DEBUG) {
					Log.d(TAG, "amountToCenterScroll amountToScroll = " + amountToScroll + ", nextSelctedView = " + nextSelctedView + ", nextSelectedPosition = " + nextSelectedPosition
							+ ", focus to right" + ", distanceLeft = " + distanceLeft);
				}
				int maxDiff = nextSelctedView.getWidth() * (mItemCount - getLastVisiblePosition() - 1);
				Object lastItem = getAdapter().getItem(getLastVisiblePosition());
				for (int index = getLastVisiblePosition() + 1; index < mItemCount; index++) {
					Object item = getAdapter().getItem(index);
					if (!lastItem.equals(item)) {
						maxDiff += mIntevel;
					}

					lastItem = item;

					if (maxDiff > amountToScroll) {
						break;
					}
				}

				maxDiff -= distanceLeft;
				View lastVisibleView = getChildAt(numChildren - 1);
				int spacing = 0;
				if (nextSelectedPosition != mItemCount - 1) {
					spacing = mSpacing;
				}

				if (lastVisibleView.getRight() + spacing > getWidth() - mListPadding.right) {
					maxDiff += (lastVisibleView.getRight() + spacing - (getWidth() - mListPadding.right));
				}

				if (amountToScroll > maxDiff) {
					amountToScroll = maxDiff;
				}

				if (reset) {
					reset();
					offsetFocusRectLeftAndRight(-distanceLeft, -distanceLeft);
				}

				if (amountToScroll > 0) {
					if (reset) {
						offsetFocusRectLeftAndRight(-amountToScroll, -amountToScroll);
					} else {
						offsetFocusRectLeftAndRight((nextSelctedView.getWidth() + mSpacing + intevel - amountToScroll),
								(nextSelctedView.getWidth() + mSpacing + intevel - amountToScroll));
					}

					smoothScrollBy(amountToScroll);
					mIsAnimate = true;
				} else {
					if (!reset) {
						offsetFocusRectLeftAndRight((nextSelctedView.getWidth() + mSpacing + intevel),
								(nextSelctedView.getWidth() + mSpacing + intevel));
					}

					mIsAnimate = true;
				}
			} else {
				reset();
				mIsAnimate = true;
			}

			return amountToScroll;
		} else if (direction == FOCUS_LEFT) {
			View nextSelctedView = getChildAt(nextSelectedPosition - mFirstPosition);
			int nextSelectedCenter = 0;
			boolean reset = false;
			int intevel = 0;

			if (nextSelctedView == null) {
				nextSelctedView = getChildAt(0);
				nextSelectedCenter = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;

				Object firstItem = getAdapter().getItem(getFirstVisiblePosition());
				Object lastItem = getAdapter().getItem(nextSelectedPosition + 1);
				Object item = getAdapter().getItem(nextSelectedPosition);
				if (!lastItem.equals(item)) {
					intevel = mIntevel;
				}

				nextSelectedCenter -= (nextSelctedView.getWidth() + mSpacing) * (getFirstVisiblePosition() - nextSelectedPosition);
				for (int index = getFirstVisiblePosition() - 1; index >= nextSelectedPosition; index--) {
					item = getAdapter().getItem(index);
					if (!firstItem.equals(item)) {
						nextSelectedCenter -= mIntevel;
					}
					firstItem = item;
				}

				reset = false;
			} else {
				nextSelectedCenter = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
				reset = true;
			}

			int finalNextSelectedCenter = nextSelectedCenter - distanceLeft;

			if (finalNextSelectedCenter < center) {
				if (DEBUG) {
					Log.d(TAG, "amountToCenterScroll amountToScroll = " + amountToScroll + ", nextSelctedView = " + nextSelctedView + ", nextSelectedPosition = " + nextSelectedPosition
							+ ", focus to left" + ", distanceLeft = " + distanceLeft);
				}
				amountToScroll = center - finalNextSelectedCenter;
				int maxDiff = nextSelctedView.getWidth() * (getFirstVisiblePosition());
				Object firstItem = getAdapter().getItem(getFirstVisiblePosition());
				for (int index = getFirstVisiblePosition() - 1; index >= 0; index--) {
					Object item = getAdapter().getItem(index);
					if (!firstItem.equals(item)) {
						maxDiff += mIntevel;
					}

					firstItem = item;

					if (maxDiff > amountToScroll) {
						break;
					}
				}

				if (maxDiff < 0) {
					maxDiff = 0;
				}

				maxDiff += distanceLeft;
				View firstVisibleView = getChildAt(0);
				int spacing = 0;
				if (nextSelectedPosition != 0) {
					spacing = mSpacing;
				}

				if (firstVisibleView.getLeft() - spacing < listLeft) {
					maxDiff += (listLeft - (firstVisibleView.getLeft() - spacing));
				}

				if (amountToScroll > maxDiff) {
					amountToScroll = maxDiff;
				}

				if (reset) {
					reset();
					offsetFocusRectLeftAndRight(-distanceLeft, -distanceLeft);
				}

				if (amountToScroll > 0) {
					if (reset) {
						offsetFocusRectLeftAndRight(amountToScroll, amountToScroll);
					} else {
						offsetFocusRectLeftAndRight(-(nextSelctedView.getWidth() + mSpacing + intevel - amountToScroll),
								(nextSelctedView.getWidth() + mSpacing + intevel - amountToScroll));
					}

					smoothScrollBy(-amountToScroll);
					mIsAnimate = true;
				} else {
					if (!reset) {
						offsetFocusRectLeftAndRight(-(nextSelctedView.getWidth() + mSpacing + intevel),
								-(nextSelctedView.getWidth() + mSpacing + intevel));
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
	public ItemListener getItem() {
		return (ItemListener) getSelectedView();
	}

	@Override
	public boolean isScrolling() {
		return (mLastScrollState != OnScrollListener.SCROLL_STATE_IDLE);
	}

	@Override
	public boolean preOnKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "preOnKeyDown keyCode = " + keyCode);
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
	public boolean hasDeepFocus() {
		return mDeepFocus;
	}

	@Override
	public boolean canDeep() {
		return true;
	}

	@Override
	public void onFocusDeeped(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		mDeepFocus = gainFocus;
		onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	}

	@Override
	public boolean isScale() {
		return true;
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
	
	public void offsetFocusRectLeftAndRight(int left, int right) {
		if (SystemProUtils.getGlobalFocusMode() == PositionManager.FOCUS_SYNC_DRAW) {
			mFocusRectparams.focusRect().left += left;
			mFocusRectparams.focusRect().right += right;
		}
	}

	@Override
	public Rect getClipFocusRect() {
		// TODO Auto-generated method stub
		return mClipFocusRect;
	}
}
