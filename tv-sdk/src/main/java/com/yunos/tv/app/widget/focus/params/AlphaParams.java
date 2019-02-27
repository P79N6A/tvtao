package com.yunos.tv.app.widget.focus.params;

import android.view.animation.Interpolator;

public class AlphaParams {
	private int mAlphaFrameRate = 5;
	private Interpolator mAlphaInterpolator = null;
	float mFromAlpha = 0;
	float mToAlpha = 1.0f;

	public AlphaParams(int alphaFrameRate, float fromAlpha, float toAlpha, Interpolator interpolator) {
		mAlphaFrameRate = alphaFrameRate;
		mFromAlpha = fromAlpha;
		mToAlpha = toAlpha;
		mAlphaInterpolator = interpolator;
	}

	public float getFromAlpha() {
		return mFromAlpha;
	}

	public float getToAlpha() {
		return mToAlpha;
	}

	public Interpolator getAlphaInteroplator() {
		return mAlphaInterpolator;
	}

	public int getAlphaFrameRate() {
		return mAlphaFrameRate;
	}
}
