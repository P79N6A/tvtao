package com.yunos.tv.app.widget.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;

public class ArcPainter extends BasePainter {

	static private final int MAX_DEGREE = 360;

	int CLIP_MAX_DEGREE = MAX_DEGREE;
	int CLIP_MIN_DEGREE = 0;
	int mDegree = 0;
	int mX = 0;
	int mY = 0;
	int mRadius = 0;
	boolean mShowing = true;

	public ArcPainter(Context context) {
		super(context);
	}

	public void setCenter(int x, int y) {
		mX = x;
		mY = y;
	}

	public void setRadius(int radius) {
		mRadius = radius;
	}

	@Override
	public boolean draw(Canvas canvas) {
		if (!super.draw(canvas)) {
			return false;
		}
		drawArc(canvas);

		return true;
	}

	public void drawArc(Canvas canvas) {
		mScroller.computeScrollOffset();
		mDegree = mScroller.getCurrX();
		int degree = mShowing ? mDegree - CLIP_MIN_DEGREE: CLIP_MAX_DEGREE - CLIP_MIN_DEGREE - (mDegree - CLIP_MIN_DEGREE);
		canvas.drawArc(new RectF(mX - mRadius, mY - mRadius, mX + mRadius, mY + mRadius), CLIP_MIN_DEGREE, degree, false, getPaint());
		paintInvalidate();
	}

	public void show(int startDegree, int endDegree, int duration, boolean isShow) {
		mShowing = isShow;
		if (endDegree - startDegree > MAX_DEGREE) {
			endDegree = startDegree + MAX_DEGREE;
		}
		CLIP_MIN_DEGREE = startDegree;
		CLIP_MAX_DEGREE = endDegree;
		mScroller.startScroll(CLIP_MIN_DEGREE, 0, CLIP_MAX_DEGREE - CLIP_MIN_DEGREE, 0, duration);
		mDegree = CLIP_MIN_DEGREE;
		paintInvalidate();
	}

}
