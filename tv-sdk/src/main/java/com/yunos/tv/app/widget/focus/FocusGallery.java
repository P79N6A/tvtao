package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.yunos.tv.app.widget.Gallery;
import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;

import java.util.ArrayList;

public class FocusGallery extends Gallery implements DeepListener, ItemListener {
	protected static final String TAG = "FocusGallery";
	protected static final boolean DEBUG = false;

	protected Params mParams = new Params(1.1f, 1.1f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());
	protected FocusRectParams mFocusRectparams = new FocusRectParams();
	protected Rect mClipFocusRect = new Rect();
	boolean mCanDraw = true;
	boolean mIsAnimate = true;
	boolean mLayouted = false;
	boolean mDeepFocus = false;
	ItemSelectedListener mItemSelectedListener;
	GalleyPreKeyListener mGalleryPreKeyListener;
	boolean mReset = false;
	boolean mFocusBackground = false;

	boolean mAimateWhenGainFocusFromLeft = true;
	boolean mAimateWhenGainFocusFromRight = true;
	boolean mAimateWhenGainFocusFromUp = true;
	boolean mAimateWhenGainFocusFromDown = true;

	public FocusGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FocusGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FocusGallery(Context context) {
		super(context);
	}

	public void setAnimateWhenGainFocus(boolean fromleft, boolean fromUp, boolean fromRight, boolean fromDown) {
		mAimateWhenGainFocusFromLeft = fromleft;
		mAimateWhenGainFocusFromUp = fromUp;
		mAimateWhenGainFocusFromRight = fromRight;
		mAimateWhenGainFocusFromDown = fromDown;
	}

	public void setFocusBackground(boolean back) {
		mFocusBackground = back;
	}

	public void setPreKeyListener(GalleyPreKeyListener l) {
		mGalleryPreKeyListener = l;
	}

	public void setOnItemSelectedListener(ItemSelectedListener listener) {
		mItemSelectedListener = listener;
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		Log.d(TAG, "onFocusChanged");
		if (gainFocus) {
			if (gainFocus && getChildCount() > 0 && mLayouted) {
				// getFocusParams();
				if (getLeftScrollDistance() == 0) {
					reset();
				}
			}
		}

		mIsAnimate = checkAnimate(direction);

		performSelect(gainFocus);
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
		mLayouted = true;
		if (!isLayoutRequested()) {
			return;
		}

		if ((hasFocus() || hasDeepFocus()) && getChildCount() > 0 && mLayouted) {
			if (getLeftScrollDistance() == 0) {
				reset();
			}
		}
		
		mClipFocusRect.set(0, 0, getWidth(), getHeight());
	}

	@Override
	public void requestLayout() {
		super.requestLayout();
		mLayouted = false;
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

		mIsRtl = false;// isLayoutRtl();

		int childrenLeft = mSpinnerPadding.left;
		int childrenWidth = getRight() - getLeft() - mSpinnerPadding.left - mSpinnerPadding.right;

		if (mDataChanged) {
			handleDataChanged();
		}

		// Handle an empty gallery by removing all views.
		if (mItemCount == 0) {
			resetList();
			return;
		}

		int lastPosition = getLastVisiblePosition();
		int selectPosition = mSelectedPosition;
		boolean isOutSideVisibleRegion = false;
		int preOffsetX = 0;
		int deltaPosition = 0;
		if (mNextSelectedPosition >= 0) {
			deltaPosition = mNextSelectedPosition - mSelectedPosition;
		}
	
		int childrenNum = getChildCount();
		// ToRight
		if (mFirstPosition <= lastPosition) {
			if (mSelectedPosition < mFirstPosition) {
				isOutSideVisibleRegion = true;
				selectPosition = mFirstPosition;
				if (getChildAt(0) != null)
					preOffsetX = getChildAt(0).getLeft();
			} else if (mSelectedPosition > lastPosition) { // ToLeft
				isOutSideVisibleRegion = true;
				selectPosition = mFirstPosition + childrenNum - 1;
				if (getChildAt(childrenNum - 1) != null)
					preOffsetX = getChildAt(childrenNum - 1).getLeft();
			}
		}

		// Update to the new selected position.
		if (mNextSelectedPosition >= 0) {
			setSelectedPositionInt(mNextSelectedPosition);
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

		// Make selected view and center it

		/*
		 * mFirstPosition will be decreased as we add views to the left later
		 * on. The 0 for x will be offset in a couple lines down.
		 */
		// if the current position's delta is not equal to zero, then application should be called setSelection,
		// So make sure this case use common layout.
		if (deltaPosition != 0){
			selectPosition = mSelectedPosition;
		}
		
		mFirstPosition = selectPosition;


		View sel = makeAndAddView(selectPosition, 0, preOffsetX, true);

		// Put the selected child in the center
		if (!isOutSideVisibleRegion) {
			int selectedOffset = childrenLeft + (childrenWidth / 2) - (sel.getWidth() / 2) + getLeftScrollDistance();
			sel.offsetLeftAndRight(selectedOffset);
		}

		if (sel != null) {
			positionSelector(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom());
		}
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

	protected void reset() {
		ItemListener item = (ItemListener) getSelectedView();
		if (item != null) { // by leiming.yanlm
			mFocusRectparams.set(item.getFocusParams());
			offsetDescendantRectToMyCoords(getSelectedView(), mFocusRectparams.focusRect());
		}
	}

	@Override
	public Params getParams() {
		if (mParams == null) {
			throw new IllegalArgumentException("The params is null, you must call setScaleParams before it's running");
		}

		return mParams;
	}

	@Override
	public boolean canDraw() {
		if (mItemCount <= 0) {
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
		return mIsAnimate;
	}

	@Override
	public ItemListener getItem() {
		return (ItemListener) getSelectedView();
	}

	@Override
	public boolean isScrolling() {
		return isFling();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "onKeyDown keyCode = " + keyCode);
		if (checkState(keyCode)) {
			return true;
		}

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (getSelectedItemPosition() <= 0) {
				return true;
			}
			performSelect(false);// added by yanchang.zhao

			mIsAnimate = true;
			setSelectedPositionInt(getSelectedItemPosition() - 1);
			setNextSelectedPositionInt(getSelectedItemPosition() - 1);
			int distance = getChildItemWidth();

			distance += mSpacing;// getSelectedItemPosition() == 0 ? distance :
									// distance + mSpacing;
			smoothScrollBy(distance);

			if (canDraw()) {// added by yanchang.zhao
				mReset = false;
				performSelect(true);
			} else {
				mReset = true;
			}// end
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (getSelectedItemPosition() >= mItemCount - 1) {
				return true;
			}
			int selectePos = getSelectedItemPosition();
			if (selectePos == mItemCount - 1) {
				return true;
			}
			performSelect(false);// added by yanchang.zhao
			mIsAnimate = true;
			setSelectedPositionInt(getSelectedItemPosition() + 1);
			setNextSelectedPositionInt(getSelectedItemPosition() + 1);
			distance = getChildItemWidth();

			distance += mSpacing;// getSelectedItemPosition() == mItemCount - 1
									// ? distance : distance + mSpacing;

			smoothScrollBy(-distance);
			if (canDraw()) {// added by yanchang.zhao
				mReset = false;
				performSelect(true);
			} else {
				mReset = true;
			}// end
			return true;
		default:
			mIsAnimate = false;
			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	protected int getChildItemWidth() {
		View v = getChildAt(0);
		return v == null ? 0 : v.getWidth();
	}

	@Override
	public boolean preOnKeyDown(int keyCode, KeyEvent event) {
		if (checkState(keyCode)) {
			return true;
		}
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if (mGalleryPreKeyListener != null) {
				mGalleryPreKeyListener.preKeyDownListener(this, keyCode, event);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			return true;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			return getSelectedItemPosition() > 0 ? true : false;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			return getSelectedItemPosition() < mItemCount - 1 ? true : false;
		default:
			break;
		}

		return false;
	}

	public interface GalleyPreKeyListener {
		public void preKeyDownListener(View v, int keyCode, KeyEvent event);
	}

	public boolean checkState(int keyCode) {
		if (mLastScrollState == OnScrollListener.SCROLL_STATE_FLING) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
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

	private void performSelect(boolean select) {
		if (mItemSelectedListener != null) {
			mItemSelectedListener.onItemSelected(getSelectedView(), getSelectedItemPosition(), select, this);
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
	public boolean canDeep() {
		return true;
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
		return null;
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
