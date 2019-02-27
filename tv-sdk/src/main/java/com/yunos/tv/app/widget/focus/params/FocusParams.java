package com.yunos.tv.app.widget.focus.params;

import android.view.animation.Interpolator;

public class FocusParams {

	private boolean mFocusVisible = true;
	private int mFocusFrameRate = 5;
	private Interpolator mFocusInterpolator = null;
	
	public FocusParams(boolean focusVisible, int focusFrameRate, Interpolator interpolator){
		this.mFocusVisible = focusVisible;
		this.mFocusFrameRate = focusFrameRate;
		this.mFocusInterpolator = interpolator;
	}
	
	public void setFocusVisible(boolean visible){
		mFocusVisible = visible;
	}
	
	public boolean isFocusVisible(){
		return mFocusVisible;
	}
	
	public void setFocusFrameRate(int rate){
		this.mFocusFrameRate = rate;
	}
	
	public int getFocusFrameRate(){
		return this.mFocusFrameRate;
	}
	
	public Interpolator getFocusInterpolator() {
		return mFocusInterpolator;
	}
}
