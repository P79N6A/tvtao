package com.yunos.tv.app.widget.focus;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.PositionListener;
import com.yunos.tv.app.widget.focus.params.ScaleParams;

public class SyncPositionManager extends PositionManager {

	protected static final String TAG = "SyncPositionManager";
	protected static final boolean DEBUG = false;
	private Rect mLastFocusRect = new Rect();
	private Rect mCurrFocusRect = new Rect();
	private boolean mReset = false;
	private boolean mIsAnimate = false;

	public SyncPositionManager(int focusMode, PositionListener l) {
		super(focusMode, l);
	}

	@Override
	public void draw(Canvas canvas) {
		preDrawUnscale(canvas);
		super.draw(canvas);
		if (!mStart) {
			return;
		}

		if (!mFocus.canDraw()) {
			drawFocus(canvas);
			postDrawUnscale(canvas);
			mListener.postInvalidateDelayed(30);
			return;
		}

		ItemListener item = mFocus.getItem();
		if(item == null){
		    return;
		}
		processReset();
		drawBeforeFocus(item, canvas);
		boolean isInvalidate = false;
		if (!isFinished()) {
			boolean isScrolling = mFocus.isScrolling();

			if (mFrame == 1) {
				mFocus.onFocusStart();
                onFocusStart();
			}
            onFocusProcess();
			boolean drawFocus = true;
			if (mLastFocusRect.isEmpty()) {
				drawFocus = false;
			}

			if (isScrolling && !isFocusFinished()) {
				updateDstRect(item);
			}

			if (mIsAnimate) {
				if (!isFocusFinished() && drawFocus) {
					drawFocus(canvas, item);
					mFocusFrame++;
				}
			} else {
				if (isScaleFinished()) {
					if (isScrolling) {
						updateDstRect(item);
						mFocusRect.set(mCurrFocusRect);
					}
					drawFocus(canvas);
				} else {
					drawFocus = false;
				}
			}

			if (!isScaleFinished()) {
				
				if (item.isScale()) {
					if (isScrolling) {
						updateDstRect(item);
						mFocusRect.set(mCurrFocusRect);
					}
					drawScale(canvas, item, !drawFocus);
				} else if (!drawFocus) {
					drawStaticFocus(canvas, item, mFocus.getParams().getScaleParams().getScaleX(), mFocus.getParams().getScaleParams().getScaleY());
				}
				mScaleFrame++;

			} else if (!drawFocus) {
				if (isScrolling) {
					updateDstRect(item);
					mFocusRect.set(mCurrFocusRect);
				}
				drawFocus(canvas);
				//isScaleFinished之后，会直接执行 Math.max，导致mFrame != getTotalFrame(),不会执行onFocusFinished
				//可以通过filteractivity左边列表加载来测试
//				mFrame = Math.max(mFocusFrameRate, mScaleFrameRate) + 1;
				// return;
			}

			mFrame++;
			// mListener.invalidate();
			isInvalidate = true;
			if (mFrame == getTotalFrame()) {
				mFocus.onFocusFinished();
                onFocusFinished();
			}

		} else if (mFocus.isScrolling()) {
			drawStaticFocus(canvas, item);
			mLastFocusRect.set(mFocusRect);
			mCurrFocusRect.set(mFocusRect);
			isInvalidate = true;
		} else if(mForceDrawFocus){
			drawStaticFocus(canvas, item);
			isInvalidate = true;
			mForceDrawFocus = false;
		} else {
			drawFocus(canvas);
		}

		if (!item.isFinished()) {
			isInvalidate = true;
		}

		if (!mPause && (isInvalidate || mSelector.isDynamicFocus())) {
			mListener.invalidate();
		}

		drawAfterFocus(item, canvas);
		postDrawUnscale(canvas);
	}

    private void onFocusStart(){
        if(mSelectorPolator == null){
            mSelectorPolator = new AccelerateDecelerateFrameInterpolator();
        }
    }

    private void onFocusFinished(){
        resetSelector();
    }

    public void resetSelector(){
        if(mConvertSelector != null){
            mSelector = mConvertSelector;
            setConvertSelector(null);
        }
    }

    private void onFocusProcess(){
        int totalFrame = getTotalFrame();
        float alpha1 = mSelectorPolator.getInterpolation(mFrame * 1.0f / totalFrame);
        float alpha2 = 1 - mSelectorPolator.getInterpolation(mFrame * 1.0f / totalFrame);
        if(mFrame >= totalFrame || !mIsAnimate){
            resetSelector();
        }
        if(mConvertSelector != null && mFrame < totalFrame && mIsAnimate){
            mConvertSelector.setAlpha(alpha1);
            if(mSelector != null){
                mSelector.setAlpha(alpha2);
            }
        } else if(mSelector != null) {
            mSelector.setAlpha(1);
        }
    }

	private void processReset() {
		if (mReset) {
			ScaleParams scaleParams = mFocus.getParams().getScaleParams();
			ItemListener item = mFocus.getItem();
			if (item == null) {
				Log.e(TAG, "processReset: item is null! mFocus:" + mFocus);
				return ;
			}
			removeScaleNode(item);
			scaleParams.computeScaleXY(item.getItemWidth(), item.getItemHeight());
			mLastFocusRect.set(mFocusRect);
			updateDstRect(item);
			mIsAnimate = mFocus.isAnimate();
			mReset = false;
		}
	}

	private void updateDstRect(ItemListener item) {
		mCurrFocusRect.set(getFinalRect(item));
	}

	private void drawFocus(Canvas canvas, ItemListener item) {
		int focusFrameRate = mFocus.getParams().getFocusParams().getFocusFrameRate();

		if (DEBUG) {
			Log.d(TAG, "drawFocus: mFrame = " + mFrame + ", focus frame rate = " + focusFrameRate + ", is scrolling = " + mFocus.isScrolling());
		}

		float coef = (float) this.mFrame / focusFrameRate;

		Interpolator focusInterpolator = mFocus.getParams().getFocusParams().getFocusInterpolator();
		if (focusInterpolator == null) {
			focusInterpolator = new LinearInterpolator();
		}
		coef = focusInterpolator.getInterpolation(coef);

		mFocusRect.left = (int) (mLastFocusRect.left + (mCurrFocusRect.left - mLastFocusRect.left) * coef);
		mFocusRect.right = (int) (mLastFocusRect.right + (mCurrFocusRect.right - mLastFocusRect.right) * coef);
		mFocusRect.top = (int) (mLastFocusRect.top + (mCurrFocusRect.top - mLastFocusRect.top) * coef);
		mFocusRect.bottom = (int) (mLastFocusRect.bottom + (mCurrFocusRect.bottom - mLastFocusRect.bottom) * coef);

		drawFocus(canvas);
	}

	private void drawScale(Canvas canvas, ItemListener item, boolean drawFocus) {
		ScaleParams scaleParams = mFocus.getParams().getScaleParams();
		float dstScaleXValue = 1.0f;
		float dstScaleYValue = 1.0f;

		float itemDiffScaleXValue = scaleParams.getScaleX() - 1.0f;
		float itemDiffScaleYValue = scaleParams.getScaleY() - 1.0f;

		float coef = (float) mFrame / scaleParams.getScaleFrameRate();
		Interpolator scaleInterpolator = mFocus.getParams().getScaleParams().getScaleInterpolator();

		if (scaleInterpolator == null) {
			scaleInterpolator = new LinearInterpolator();
		}

		coef = scaleInterpolator.getInterpolation(coef);
		dstScaleXValue = 1.0f + itemDiffScaleXValue * coef;
		dstScaleYValue = 1.0f + itemDiffScaleYValue * coef;

		item.setScaleX(dstScaleXValue);
		item.setScaleY(dstScaleYValue);

		if (drawFocus) {
			drawStaticFocus(canvas, item, dstScaleXValue, dstScaleYValue);
		}
	}

	@Override
	public boolean isFinished() {
		return mFrame > getTotalFrame();
	}

	public int getTotalFrame() {
		return Math.max(mFocusFrameRate, mScaleFrameRate);
	}

	private boolean isFocusFinished() {
		return mFrame > mFocusFrameRate;
	}

	private boolean isScaleFinished() {
		return mFrame > mScaleFrameRate;
	}

	@Override
	public void reset() {
		super.reset();
		mReset = true;
	}
}
