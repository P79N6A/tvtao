package com.yunos.tv.app.widget.Interpolator;

public class ADInterpolator extends TweenInterpolator {
	private float mFactor = 0.5f;//加速时间占比率，不能为0和1，0.5代表加速时间和减速时间是一样的。
	
	public ADInterpolator() {
	}
	
	public ADInterpolator(float factor) {
		mFactor = factor;
	}
	
	private float calcValue(float input) {
		if(mFactor == 0.5f) {
			return input;
		} else if (input < mFactor){
			return input*0.5f/mFactor;
		} else {
			return 0.5f + 0.5f*(input-mFactor)/(1-mFactor);
		}
	}

	@Override
	public float getInterpolation(float input) {
		return (float)(Math.cos((calcValue(input) + 1) * Math.PI) / 2.0f) + 0.5f;
	}

    @Override
    public float interpolation(float t, float b, float c, float d) {
        return getInterpolation(1 - (d - t) / d) * c + b;
    }

}
