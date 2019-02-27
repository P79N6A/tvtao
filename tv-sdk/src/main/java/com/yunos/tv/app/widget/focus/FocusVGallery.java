package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.VGallery;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;

import java.util.ArrayList;

public class FocusVGallery extends VGallery implements DeepListener, ItemListener {
	protected static String TAG = "FocusVGallery";
	protected static boolean DEBUG = false;

	protected Params mParams = new Params(1.1f, 1.1f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());
	protected FocusRectParams mFocusRectparams = new FocusRectParams();
	protected Rect mClipFocusRect = new Rect();
	
	boolean mCanDraw = true;
	boolean mIsAnimate = true;
	public boolean mLayouted = false;
	boolean mDeepFocus = false;
	ItemSelectedListener mItemSelectedListener;
	GalleyPreKeyListener mGalleryPreKeyListener;
	boolean mReset = false;
	boolean mFocusBackground = false;

	boolean mAimateWhenGainFocusFromLeft = true;
	boolean mAimateWhenGainFocusFromRight = true;
	boolean mAimateWhenGainFocusFromUp = true;
	boolean mAimateWhenGainFocusFromDown = true;

	public FocusVGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FocusVGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FocusVGallery(Context context) {
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
				reset();
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
			reset();
		}
		
		mClipFocusRect.set(0, 0, getWidth(), getHeight());
	}

	@Override
	public void requestLayout() {
		super.requestLayout();
		mLayouted = false;
	}

	private void reset() {
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
		case KeyEvent.KEYCODE_DPAD_UP:
			if (getSelectedItemPosition() <= 0) {
				return true;
			}
			performSelect(false);// added by yanchang.zhao

			mIsAnimate = true;
			setSelectedPositionInt(getSelectedItemPosition() - 1);
			setNextSelectedPositionInt(getSelectedItemPosition() - 1);
			int distance = getChildAt(0).getHeight();

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
		case KeyEvent.KEYCODE_DPAD_DOWN:
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

			distance = getChildAt(0).getHeight();

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

	@Override
	public boolean preOnKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "preOnKeyDown keyCode = " + keyCode);
		if (checkState(keyCode)) {
			return true;
		}
		
		if (getChildCount() <= 0) {
			return false;
		}
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			return getSelectedItemPosition() > 0 ? true : false;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			return getSelectedItemPosition() < mItemCount - 1 ? true : false;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			return true;
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (mGalleryPreKeyListener != null) {
				mGalleryPreKeyListener.preKeyDownListener(this, keyCode, event);
			}
			break;
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
			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
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
	
	public boolean isFlingRunnableFinish(){
		return getFlingRunnable() == null ? true : getFlingRunnable().isFinished();
	}

	@Override
	public Rect getClipFocusRect() {
		return mClipFocusRect;
	}
}
