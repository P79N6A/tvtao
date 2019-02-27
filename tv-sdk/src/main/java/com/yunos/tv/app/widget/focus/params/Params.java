package com.yunos.tv.app.widget.focus.params;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;


public class Params {
	ScaleParams mScaleParams = null;
	FocusParams mFocusParams = null;
	AlphaParams mAlphaParams = new AlphaParams(20, 0, 1.0f, new AccelerateDecelerateInterpolator());

	public Params(float scaleX, float scaleY, int scaleFrameRate, Interpolator scaleInterpolator, boolean focusVisible,
			int focusFrameRate, Interpolator focusInterpolator) {
		mScaleParams = new ScaleParams(scaleX, scaleY, scaleFrameRate, scaleInterpolator);
		mFocusParams = new FocusParams(focusVisible, focusFrameRate, focusInterpolator);
	}
	
	public Params(float scaleX, float scaleY, int scaleFrameRate,
			Interpolator scaleInterpolator, boolean focusVisible,
			int focusFrameRate, Interpolator focusInterpolator,
			int alphaFrameRate, float fromAlpha, float toAlpha,
			Interpolator alphaInterpolator) {

		mScaleParams = new ScaleParams(scaleX, scaleY, scaleFrameRate,
				scaleInterpolator);
		mFocusParams = new FocusParams(focusVisible, focusFrameRate,
				focusInterpolator);
		mAlphaParams = new AlphaParams(alphaFrameRate, fromAlpha, toAlpha,
				alphaInterpolator);
	}
	
	public Params(ScaleParams scaleParams, FocusParams focusParams,
			AlphaParams alphaParams) {
		mScaleParams = scaleParams;
		mFocusParams = focusParams;
		mAlphaParams = alphaParams;
	}

	public ScaleParams getScaleParams() {
		return mScaleParams;
	}

	public FocusParams getFocusParams() {
		return mFocusParams;
	}
	
	public AlphaParams getAlphaParams(){
		return mAlphaParams;
	}
}
