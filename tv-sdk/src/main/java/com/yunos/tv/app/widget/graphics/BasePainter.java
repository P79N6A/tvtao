package com.yunos.tv.app.widget.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;

abstract class BasePainter {
	static final int DEFAULT_ANIMATE_DURATION = 1000;
	static public final int LOCK_WISE = 1;
	static public final int ANTI_LOCK_WISE = 2;
	
//	Interpolator mInterpolator = new CubicBezierInterpolator(1, 0, 0, 1);
	Interpolator mInterpolator = new AccelerateDecelerateFrameInterpolator();
//	Interpolator mInterpolator = new LinearInterpolator();
	Scroller mScroller;
	Context mContext;
	Paint mPaint = new Paint();
	PainterInterface mPainterInterface;
	int mDirection = LOCK_WISE;
	
	public BasePainter(Context context) {
		mContext = context;
		mScroller = new Scroller(context, mInterpolator);
		
		mPaint.setStrokeWidth(5);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(Color.BLUE);
		mPaint.setAntiAlias(true);

	}

	public void setDriection(int d) {
		if (d != LOCK_WISE && d != ANTI_LOCK_WISE) {
			throw new IllegalArgumentException("CircleClipView: setDriection direciton must be LOCK_WISE or ANTI_LOCK_WISE");
		}

		mDirection = d;
	}
	
	public void resgister(PainterInterface i) {
		mPainterInterface = i;
	}

	public void unregister() {
		mPainterInterface = null;
	}
	
	public void setPaint(Paint p){
		mPaint = p;
	}
	
	public Paint getPaint(){
		return mPaint;
	}
	
	public void setInterpolator(Interpolator i) {
		mInterpolator = i;
		resetScroller();
	}

	private void resetScroller() {
		mScroller = new Scroller(mContext, mInterpolator);
	}

	public boolean draw(Canvas canvas) {
		if(!shouldDraw()){
			return false;
		}
		
		return true;
	}
	
	public void forcedFinished(){
		mScroller.forceFinished(true);
	}

	public boolean isFinished() {
		return mScroller.isFinished();
	}

	public boolean shouldDraw() {
		if (mPainterInterface.isLayoutRequested()) {
			return false;
		}
		if (mScroller == null) {
			return false;
		}

		return true;
	}

	public void paintInvalidate() {
		if (!isFinished() && mPainterInterface != null) {
			mPainterInterface.invalidate();
		}
	}
	
	public void paintPostInvalidate(){
		if (!isFinished() && mPainterInterface != null) {
			mPainterInterface.postInvalidate();
		}
	}
}
