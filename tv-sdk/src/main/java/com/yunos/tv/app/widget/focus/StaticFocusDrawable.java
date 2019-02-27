package com.yunos.tv.app.widget.focus;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.yunos.tv.app.widget.focus.listener.DrawListener;

public class StaticFocusDrawable implements DrawListener {
	public final static boolean DEBUG = false;
	Drawable mDrawable;
	boolean mVisible = true;
	Rect mPadding = new Rect();
	Rect mRect = new Rect();
	float mAlpha = 1.0f;

	public StaticFocusDrawable(Drawable d) {
		mDrawable = d;
		d.getPadding(mPadding);
	}

	@Override
	public boolean isDynamicFocus() {
		return false;
	}

	@Override
	public void setRect(Rect r) {
		mRect.set(r);
		mRect.top -= mPadding.top;
		mRect.left -= mPadding.left;
		mRect.right += mPadding.right;
		mRect.bottom += mPadding.bottom;

		mDrawable.setBounds(mRect);
	}

	@Override
	public void setRadius(int r) {

	}

	@Override
	public void start() {

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVisible(boolean visible) {
		mVisible = visible;
	}

	@Override
	public void draw(Canvas canvas) {
		if (mDrawable == null) {
			throw new IllegalArgumentException("StaticFocusDrawable: drawable is null");
		}

		if (!mVisible) {
			return;
		}

		if (mDrawable != null) {
			mDrawable.setAlpha((int) (mAlpha * 255));
		}

		mDrawable.draw(canvas);
	}

	@Override
	public void setAlpha(float alpha) {
		mAlpha = alpha;
		if (DEBUG){
		    Log.d("StaticFocusDrawable", "setAlpha::alpha = " + alpha);
		}

	}

}
