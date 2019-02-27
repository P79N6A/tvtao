package com.yunos.tvlife.app.widget;

import android.graphics.Canvas;
import android.util.Log;

public class StaticPositionManager extends FocusedBasePositionManager {
	private static String TAG = "StaticPositionManager";
	private static boolean DEBUG = true;

	public StaticPositionManager(FocusParams params, ContainInterface container) {
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
					mIsFirstFrame = false;
				}

				drawScaleAndFocus(canvas, selectedItem, true, selectedItem.getIfScale());
				this.mContainer.invalidate();
				if (mCurrentFrame >= getParams().getFrameRate()) {
					setState(STATE_IDLE);
				}

				mCurrentFrame++;
			} else {
				if (isScrolling() || !isLastFrame()) {
					drawScaleAndFocus(canvas, selectedItem, true, selectedItem.getIfScale());
				} else {
					drawDrawable(canvas);
				}
			}
		}
	}
	
	@Override
	public String getLogTag() {
		return TAG;
	}

}
