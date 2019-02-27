package com.yunos.tv.app.widget.focus.params;

import android.view.animation.Interpolator;

public class ScaleParams {

	public static final int SCALED_FIXED_COEF = 1;
	public static final int SCALED_FIXED_X = 2;
	public static final int SCALED_FIXED_Y = 3;

	private int mScaleMode = SCALED_FIXED_COEF;
	private float mScaleX = 1.1f;
	private float mScaleY = 1.1f;
	private int mFixedScaleX;
	private int mFixedScaleY;
	private int mScaleFrameRate = 5;
	private Interpolator mScaleInterpolator = null;

	public ScaleParams(float scaleX, float scaleY, int scaleFrameRate, Interpolator interpolator) {
		mScaleMode = SCALED_FIXED_COEF;
		mScaleX = scaleX;
		mScaleY = scaleY;
		mScaleFrameRate = scaleFrameRate;
		mScaleInterpolator = interpolator;
	}

	public ScaleParams(int scaleMode, int fixedScale, int scaleFrameRate, Interpolator interpolator) {
		mScaleFrameRate = scaleFrameRate;
		mScaleInterpolator = interpolator;
		setScale(scaleMode, fixedScale);
	}

	public void setScaleFrameRate(int rate) {
		mScaleFrameRate = rate;
	}

	public void setScale(int scaleMode, float scaleX, float scaleY) {
		if (scaleMode == SCALED_FIXED_COEF) {
			mScaleMode = SCALED_FIXED_COEF;
			mScaleX = scaleX;
			mScaleY = scaleY;
		} else {
			throw new IllegalArgumentException("setScale:scaleMode must be SCALED_FIXED_COEF(1), but it is " + scaleMode);
		}
	}

	public void setScale(int scaleMode, int fixedScale) {
		if (scaleMode == SCALED_FIXED_X) {
			mFixedScaleX = fixedScale;
		} else if (scaleMode == SCALED_FIXED_Y) {
			mFixedScaleY = fixedScale;
		} else {
			throw new IllegalArgumentException("scaleMode must be SCALED_FIXED_X(2) or SCALED_FIXED_Y(3), but it is " + scaleMode);
		}
	}

	public Interpolator getScaleInterpolator() {
		return mScaleInterpolator;
	}

	public int getScaleFrameRate() {
		return mScaleFrameRate;
	}

	public int getScaleMode() {
		return mScaleMode;
	}

	public float getScaleX() {
		return mScaleX;
	}

	public float getScaleY() {
		return mScaleY;
	}

	public int getFixedScaleX() {
		return mFixedScaleX;
	}

	public int getFixedScaleY() {
		return mFixedScaleY;
	}

	public void computeScaleXY(int width, int height) {
		if (mScaleMode == SCALED_FIXED_X) {
			mScaleX = 1.0f + (float) mFixedScaleX / width;
			mScaleY = mScaleX;
		} else if (mScaleMode == SCALED_FIXED_Y) {
			mScaleY = 1.0f + (float) mFixedScaleY / height;
			mScaleX = mScaleY;
		} else if (mScaleMode == SCALED_FIXED_COEF) {

		} else {
			throw new IllegalArgumentException("scaleMode must be SCALED_FIXED_COEF(1), SCALED_FIXED_X(2) or SCALED_FIXED_Y(3), but it is "
					+ mScaleMode);
		}
	}
}
