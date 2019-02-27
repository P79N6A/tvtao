package com.yunos.tvlife.app.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

public class SyncPositionManager extends FocusedBasePositionManager {

	private static String TAG = "SyncPositionManager";
	private static boolean DEBUG = true;

	private Rect mDstRect = null;
	private Rect mLastDstRect = null;

	public SyncPositionManager(FocusParams params, ContainInterface container) {
		super(params, container);
	}

	@Override
	public void drawFrame(Canvas canvas) {
		super.drawFrame(canvas);
		ItemInterface selectedItem = getSelectedItem();
		if (selectedItem != null) {
			if (DEBUG) {
				Log.d(TAG, "drawFrame: mCurrentFrame = " + mCurrentFrame + ", frame rate = " + getParams().getFrameRate()
						+ ", is scrolling = " + isScrolling() + ", mFocusRect = " + mFocusRect);
			}
			if (mCurrentFrame <= getParams().getFrameRate()) {
				if (mIsFirstFrame || isScrolling()) {
					obtainDstRect(selectedItem);
				}
				
				mIsFirstFrame = false;

				if (mLastDstRect == null) {
					drawScaleAndFocus(canvas, selectedItem, true, selectedItem.getIfScale());
					// this.mCurrentFrame++;
					// this.mContainer.invalidate();
				} else {
					if (isLastFrame()) {
						drawScaleAndFocus(canvas, selectedItem, true, selectedItem.getIfScale());
					} else {
						if (mIsFocusMove) {
							drawFocus(canvas, selectedItem);
							drawScaleAndFocus(canvas, selectedItem, false, selectedItem.getIfScale());
						} else {
							drawScaleAndFocus(canvas, selectedItem, true, selectedItem.getIfScale());
						}

					}

				}

				mCurrentFrame++;
				if (mCurrentFrame >= getParams().getFrameRate()) {
					setState(STATE_IDLE);
				}
				this.mContainer.invalidate();
			} else {
				if (isScrolling() || !isLastFrame()) {
					drawScaleAndFocus(canvas, selectedItem, true, selectedItem.getIfScale());
					obtainDstRect(selectedItem);
				} else {
					drawDrawable(canvas);
				}
			}

		}
	}

	void obtainDstRect(ItemInterface selectedItem) {
		if (getParams().getScale() || selectedItem.getIfScale()) {
			mDstRect = getDstRect(selectedItem);
		} else {
			mDstRect = getOriginalRect(selectedItem);
		}
		offsetRect(mDstRect, true);
	}

	void drawFocus(Canvas canvas, ItemInterface selectedItem) {
		if (DEBUG) {
			Log.d(TAG, "drawFocus: mCurrentFrame = " + mCurrentFrame + ", frame rate = " + getParams().getFocusFrameRate()
					+ ", is scrolling = " + isScrolling());
		}

		float coef = (float) this.mCurrentFrame / getParams().getFrameRate();
		coef = selectedItem.getFrameFocusInterpolator().getInterpolation(coef);

		mFocusRect.left = (int) (mLastDstRect.left + (mDstRect.left - mLastDstRect.left) * coef);
		mFocusRect.right = (int) (mLastDstRect.right + (mDstRect.right - mLastDstRect.right) * coef);
		mFocusRect.top = (int) (mLastDstRect.top + (mDstRect.top - mLastDstRect.top) * coef);
		mFocusRect.bottom = (int) (mLastDstRect.bottom + (mDstRect.bottom - mLastDstRect.bottom) * coef);

		// offsetRect(mFocusRect);
		drawDrawable(canvas);
	}

	@Override
	public void reset() {
		super.reset();
		if (hasFocus()) {
			mLastDstRect = mDstRect;
		} else {
			mLastDstRect = null;
			mDstRect = null;
		}
	}
	
	@Override
	public String getLogTag() {
		return TAG;
	}
}
