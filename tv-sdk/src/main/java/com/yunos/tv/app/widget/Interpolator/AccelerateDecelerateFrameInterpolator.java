package com.yunos.tv.app.widget.Interpolator;

public class AccelerateDecelerateFrameInterpolator extends TweenInterpolator {

	private float mScale = 10;
	private double mCoef;
	private float mExponent = 2;

	public AccelerateDecelerateFrameInterpolator() {
		init();
	}

	public AccelerateDecelerateFrameInterpolator(float scale, float exponent) {
		mScale = scale;
		mExponent = exponent;
		init();
	}

	@Override
	public float getInterpolation(float input) {

		return (float) (Math.atan(Math.pow(input, mExponent) * mScale) * mCoef);
	}

	private void init() {
		mCoef = 1 / Math.atan(mScale);
	}

    @Override
    public float interpolation(float t, float b, float c, float d) {
        return getInterpolation(1 - (d - t) / d) * c + b;
    }
}
