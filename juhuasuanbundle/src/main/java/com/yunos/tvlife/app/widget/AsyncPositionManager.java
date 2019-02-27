package com.yunos.tvlife.app.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

public class AsyncPositionManager extends FocusedBasePositionManager {

	private static String TAG = "AsyncPositionManager";
	private static boolean DEBUG = true;

	private Rect mOriginalRect = null;
	private Rect mLastOriginalRect = null;

	public AsyncPositionManager(FocusParams params, ContainInterface container) {
		super(params, container);
	}

	@Override
	public void drawFrame(Canvas canvas) {
		super.drawFrame(canvas);
		ItemInterface selectedItem = getSelectedItem();
		if (selectedItem != null) {
			if (DEBUG) {
				Log.d(TAG, "drawFrame: mCurrentFrame = " + mCurrentFrame + ", frame rate = " + getParams().getFrameRate()
						+ ", is scrolling = " + isScrolling() + ", mLastOriginalRect = " + mLastOriginalRect + ", mOriginalRect = "
						+ mOriginalRect);
			}
			if (mCurrentFrame <= getParams().getFrameRate()) {
				if (mIsFirstFrame) {
					mOriginalRect = getOriginalRect(selectedItem);
					offsetRect(mOriginalRect);
					if (DEBUG) {
						Log.d(TAG, "drawFrame: get original rect mOriginalRect = " + mOriginalRect);
					}
				}

				if (mLastOriginalRect == null) {
					mCurrentFrame = getParams().getFocusFrameRate() + 1;
					mIsFirstFrame = false;
					mLastOriginalRect = mOriginalRect;
				}

				if (mCurrentFrame <= getParams().getFocusFrameRate() && this.mIsFocusMove) {
					drawFocus(canvas, selectedItem);
				} else {
					drawScaleAndFocus(canvas, selectedItem, true, selectedItem.getIfScale());
				}
				
				this.mIsFirstFrame = false;

				if (mCurrentFrame >= getParams().getFrameRate()) {
					setState(STATE_IDLE);
				}
                mCurrentFrame++;
				this.mContainer.invalidate();
			} else {
				if (isScrolling() || !isLastFrame()) {
					super.drawScaleAndFocus(canvas, selectedItem, true, selectedItem.getIfScale());
					mOriginalRect = getOriginalRect(selectedItem);
					offsetRect(mOriginalRect);
//					mOriginalRect.set(this.mFocusRect);
				} else {
					drawDrawable(canvas);
				}
			}

		}
	}

	void drawFocus(Canvas canvas, ItemInterface selectedItem) {
		if (this.mIsFirstFrame) {
			mFocusRect.set(mLastOriginalRect);
			if (DEBUG) {
				Log.d(TAG, "drawFocus: draw original rect: mFocusRect = " + mFocusRect);
			}
			// offsetRect(mFocusRect);
			drawDrawable(canvas);
			this.mContainer.invalidate();
			return;
		}

		float coef = (float) this.mCurrentFrame / getParams().getFocusFrameRate();
		coef = selectedItem.getFrameFocusInterpolator().getInterpolation(coef);

		Rect focusPadding = selectedItem.getFocusPadding(mOriginalRect, mLastOriginalRect, getFocusDirection(), this.getParams().getFocusFrameRate());

		if (focusPadding == null) {
			mFocusRect.left = (int) (mLastOriginalRect.left + (mOriginalRect.left - mLastOriginalRect.left) * coef);
			mFocusRect.right = (int) (mLastOriginalRect.right + (mOriginalRect.right - mLastOriginalRect.right) * coef);
			mFocusRect.top = (int) (mLastOriginalRect.top + (mOriginalRect.top - mLastOriginalRect.top) * coef);
			mFocusRect.bottom = (int) (mLastOriginalRect.bottom + (mOriginalRect.bottom - mLastOriginalRect.bottom) * coef);
		} else {
			mFocusRect.left = (int) (focusPadding.left + mLastOriginalRect.left + (mOriginalRect.left - mLastOriginalRect.left) * coef);
			mFocusRect.right = (int) (focusPadding.right + mLastOriginalRect.right + (mOriginalRect.right - mLastOriginalRect.right) * coef);
			mFocusRect.top = (int) (focusPadding.top + mLastOriginalRect.top + (mOriginalRect.top - mLastOriginalRect.top) * coef);
			mFocusRect.bottom = (int) (focusPadding.bottom + mLastOriginalRect.bottom + (mOriginalRect.bottom - mLastOriginalRect.bottom)
					* coef);
		}

		if (DEBUG) {
			Log.d(TAG, "drawFocus: mCurrentFrame = " + mCurrentFrame + ", frame rate = " + getParams().getFocusFrameRate()
					+ ", is scrolling = " + isScrolling() + ", mFocusRect = " + mFocusRect);
		}
		// offsetRect(mFocusRect);
		drawDrawable(canvas);
	}

	@Override
	public void reset() {
		super.reset();
		if (hasFocus()) {
			mLastOriginalRect = mOriginalRect;
		} else {
			mLastOriginalRect = null;
			mOriginalRect = null;
		}
	}

	@Override
	public boolean canDrawNext() {
		return mCurrentFrame >= getParams().getFocusFrameRate();
	}

	@Override
	public String getLogTag() {
		return TAG;
	}
}
