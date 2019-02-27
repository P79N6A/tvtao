package com.yunos.tvtaobao.live.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.FocusDrawStateListener;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.FocusStateListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;

public class FocusButton extends Button implements FocusListener, ItemListener {

	protected Params mParams = new Params(1.1f, 1.1f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());

	protected FocusRectParams mFocusRectparams = new FocusRectParams();

	boolean mFocusBackground = false;

	boolean mAimateWhenGainFocusFromLeft = true;
	boolean mAimateWhenGainFocusFromRight = true;
	boolean mAimateWhenGainFocusFromUp = true;
	boolean mAimateWhenGainFocusFromDown = true;

	boolean mIsAnimate = true;
	protected FocusStateListener mFocusStateListener = null;
	protected FocusDrawStateListener mFocusDrawStateListener = null;

	public FocusButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public FocusButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public FocusButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void setOnFocusStateListener(FocusStateListener l) {
		mFocusStateListener = l;
	}

	public void setOnFocusDrawStateListener(FocusDrawStateListener l) {
		mFocusDrawStateListener = l;
	}

	public void setAnimateWhenGainFocus(boolean fromleft, boolean fromUp, boolean fromRight, boolean fromDown) {
		mAimateWhenGainFocusFromLeft = fromleft;
		mAimateWhenGainFocusFromUp = fromUp;
		mAimateWhenGainFocusFromRight = fromRight;
		mAimateWhenGainFocusFromDown = fromDown;
	}

	public void setFocusBackground(boolean back) {
		mFocusBackground = back;
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

		mIsAnimate = checkAnimate(direction);
	}

	private boolean checkAnimate(int direction) {
		switch (direction) {
		case View.FOCUS_LEFT:
			return mAimateWhenGainFocusFromRight ? true : false;
		case View.FOCUS_UP:
			return mAimateWhenGainFocusFromDown ? true : false;
		case View.FOCUS_RIGHT:
			return mAimateWhenGainFocusFromLeft ? true : false;
		case View.FOCUS_DOWN:
			return mAimateWhenGainFocusFromUp ? true : false;
		}

		return true;
	}

	@Override
	public FocusRectParams getFocusParams() {
		Rect r = new Rect();
		getFocusedRect(r);

		mFocusRectparams.set(r, 0.5f, 0.5f);
		return mFocusRectparams;
	}

	@Override
	public boolean canDraw() {
		return true;
	}

	@Override
	public boolean isAnimate() {
		return mIsAnimate;
	}

	@Override
	public boolean isScale() {
		return true;
	}

	@Override
	public Params getParams() {
		if (mParams == null) {
			throw new IllegalArgumentException("The params is null, you must call setScaleParams before it's running");
		}

		return mParams;
	}

	@Override
	public ItemListener getItem() {
		return this;
	}

	@Override
	public boolean isScrolling() {
		return false;
	}

	@Override
	public int getItemWidth() {
		return getWidth();
	}

	@Override
	public int getItemHeight() {
		return getHeight();
	}

	@Override
	public boolean preOnKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			return true;

		default:
			break;
		}
		return false;
	}

	@Override
	public Rect getManualPadding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onFocusStart() {
		if (mFocusStateListener != null) {
			mFocusStateListener.onFocusStart(this, (View) this.getParent());
		}
	}

	@Override
	public void onFocusFinished() {
		if (mFocusStateListener != null) {
			mFocusStateListener.onFocusFinished(this, (View) this.getParent());
		}
	}

	@Override
	public boolean isFocusBackground() {
		return mFocusBackground;
	}

	@Override
	public void drawBeforeFocus(Canvas canvas) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawAfterFocus(Canvas canvas) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isFinished() {
		return true;
	}

	@Override
	public Rect getClipFocusRect() {
		// TODO Auto-generated method stub
		return null;
	}
}
