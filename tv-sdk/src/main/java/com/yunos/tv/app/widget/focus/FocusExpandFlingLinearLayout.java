package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.yunos.tv.app.widget.focus.listener.ExpandVGalleryListener;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;

import java.util.ArrayList;

public class FocusExpandFlingLinearLayout extends FocusFlingLinearLayout {
	protected static final String TAG = "FocusExpandFlingLinearLayout";

	public static final int DEFAULT = 1000;
	public static final int EXPAND = 1001;
	public static final int COLlIPSE = 1002;
	
	boolean mLayouted = false;//是否Layout过
	boolean mIsLayout = false;//是否正在Layout
	boolean isExpandOrCollipse = false;
	
	protected int mLayoutTimes = 0;
	int mode = DEFAULT;
	int mDuration = 200;
	int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;
	
	ArrayList<FlingRunnable> mRunnabneList;
	ExpandAttendListener mExpandAttendListener = null;
	FEFLLayoutFinisedListener mLayoutFinishedLinster;

	public interface ExpandAttendListener {
		public void start(int mode);

		public void end(int mode);
	}

	public interface OnSelectAlphaChangedListener{
		public void onAlphaChanged(View child, float alpha);
	}

	OnSelectAlphaChangedListener mOnSelectAlphaChangedListener = null;
	
	public FocusExpandFlingLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FocusExpandFlingLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FocusExpandFlingLinearLayout(Context context) {
		super(context);
	}

	public void setExpandAttendListener(ExpandAttendListener l) {
		mExpandAttendListener = l;
	}
	
	public void setOnSelectAlphaChangedListener(OnSelectAlphaChangedListener l){
		mOnSelectAlphaChangedListener = l;
	}

	public void setExpandDuration(int duration) {
		mDuration = duration;
	}

	public void expand() {
		Log.d(TAG, "expand");
		if (!isExpadnFinished()) {
			Log.w(TAG, "expand: mode = " + ", you must be call expand after collipse finish");
			return;
		}

		if (mode == EXPAND) {
			Log.w(TAG, "expand: you must call expand after collipse called ");
			return;
		}

		int lastMode = mode;
		mode = EXPAND;
		if (lastMode == DEFAULT) {
			reportExpandState(true);
			expandDefault();
		} else {
			reportExpandState(true);
			int delayed = 0;
			for (int index = 0; index < getChildCount(); index++) {
				FlingRunnable runnable = mRunnabneList.get(index);

				if (!runnable.collipsed()) {
					continue;
				}

				ExpandVGalleryListener l = (ExpandVGalleryListener) getChildAt(index);
				runnable.start((int) (delayed * 0.8f), mDuration);
				delayed += mDuration;
			}
		}

	}

	private void expandDefault() {
		Log.d(TAG, "expandDefault");
		int delayed = 0;
		for (int index = 0; index < getChildCount(); index++) {
			FlingRunnable runnable = mRunnabneList.get(index);
			View child = runnable.getChild();
			int left = child.getLeft();
			int right = child.getRight();
			if (right <= 0 || left >= getWidth()) {
				continue;
			}

			int distance = right - left;
			child.offsetLeftAndRight(-distance);
			runnable.start(distance, (int) (delayed * 0.8f), mDuration);

			delayed += mDuration;
		}
	}

	public void collipse() {
		Log.d(TAG, "collipse");
		if (!isExpadnFinished()) {
			Log.w(TAG, "expand: mode = " + ", you must be call expand after expand or collipse finish");
			return;
		}

		if (mode == COLlIPSE) {
			Log.w(TAG, "expand: you must call collipse after expand called ");
			return;
		}

		mode = COLlIPSE;
		int delayed = 0;
		reportExpandState(true);
		for (int index = getChildCount() - 1; index >= 0; index--) {
			FlingRunnable runnable = mRunnabneList.get(index);
			View child = runnable.getChild();
			int left = child.getLeft();
			int right = child.getRight();
			if (right <= 0 || left >= getWidth()) {
				continue;
			}

			int distance = right - left;
			ExpandVGalleryListener l = (ExpandVGalleryListener) child;
			runnable.start(distance, (int) (delayed * 0.8f), mDuration);
			delayed += mDuration;
		}
	}

	protected void reportExpandState(boolean start) {
		if (start) {
			if (mExpandAttendListener != null) {
				isExpandOrCollipse = true;
				mExpandAttendListener.start(mode);
			}
		} else {
			if (mExpandAttendListener != null) {
				if (isExpadnFinished()) {
					isExpandOrCollipse = false;
					mExpandAttendListener.end(mode);
				}
			}
		}
	}
	
	/**
	 * 是否展开完毕
	 * @return
	 */
	protected boolean isExpadnFinished() {
		for (int index = 0; index < mRunnabneList.size(); index++) {
			if (!mRunnabneList.get(index).isFinished()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 是否展开完毕,包括child的展开
	 * @return
	 */
	protected boolean isAllScrollFinished() {
		for (int index = 0; index < mRunnabneList.size(); index++) {
			if (!mRunnabneList.get(index).isAllFinished()) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void reportScrollStateChange(int newState) {
		super.reportScrollStateChange(newState);
		if (mScrollState != newState) {
			if (newState == OnScrollListener.SCROLL_STATE_FLING) {
				for (int index = 0; index < getChildCount(); index++) {
					ExpandVGalleryListener l = (ExpandVGalleryListener) getChildAt(index);
					l.start(mScrollDistance);
				}
			} else if (newState == OnScrollListener.SCROLL_STATE_IDLE) {
				for (int index = 0; index < getChildCount(); index++) {
					ExpandVGalleryListener l = (ExpandVGalleryListener) getChildAt(index);
					l.stop();
				}
			}
		}

		mScrollState = newState;
	}
	
	/**
	 * 如果正在展开或者正在Layout的时候禁止键事件
	 * @param keyCode
	 * @return
	 */
	public boolean checkState(int keyCode) {
		if (isMove()) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN
					|| keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				Log.d(TAG, "drop keyevnet because move"+keyCode);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean preOnKeyDown(int keyCode, KeyEvent event) {
		if(checkState(keyCode)) {
			return true;
		} 
		return super.preOnKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "onKeyDown keyCode = " + keyCode);
		if(checkState(keyCode)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(checkState(keyCode)) {
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	public void setLayoutFinishedListener(FEFLLayoutFinisedListener l) {
		mLayoutFinishedLinster = l;
	}

	public interface FEFLLayoutFinisedListener {
		public void onLayoutFinished();
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.d(TAG, "on layout!!!");
		super.onLayout(changed, l, t, r, b);
		if(!isLayoutRequested()) {
			return;
		}
		if(mLayoutTimes >=2 && isMove()) {
			Log.d(TAG, "drop layout because is move"+mLayoutTimes+";mScrollState:"+mScrollState);
			return;
		}
		
		if (mRunnabneList == null) {
			mRunnabneList = new ArrayList<FlingRunnable>(getChildCount());
			for (int index = 0; index < getChildCount(); index++) {
				mRunnabneList.add(new FlingRunnable(getChildAt(index)));
			}
		}
		mLayoutTimes++;
		mLayouted = true;
		if (mLayoutFinishedLinster != null && mLayoutTimes >= 2) {
			mLayoutFinishedLinster.onLayoutFinished();
		}
	}
	
	/**
	 * 用来判断是否执行完展开和滚动
	 * @return
	 */
	public boolean isMove() {
		return mLayouted && !( (mScrollState == OnScrollListener.SCROLL_STATE_IDLE) && isExpadnFinished());
	}

	@Override
	public boolean isScrolling() {
		return !isAllScrollFinished()
				|| mScrollState != OnScrollListener.SCROLL_STATE_IDLE
				|| super.isScrolling();
	}
	
	@Override
	public boolean isAnimate() {
		if(!isExpadnFinished()){
			return false;
		}
		return super.isAnimate();
	}

	private class FlingRunnable implements Runnable {

		private Scroller mAccelerateScroller;
		private Scroller mDecelerateScroller;
		private Scroller mScroller;
		private View mChild;
		/**
		 * X value reported by mScroller on the previous fling
		 */
		private int mLastFlingX;
		private int mDuration;
		private int mDistance;
		private boolean mStart;
		private int mTotal;
		private boolean mCollipsed;
		private boolean mFinished = true;

		public FlingRunnable(View child) {
			mChild = child;
			mStart = false;
			mTotal = 0;
			mAccelerateScroller = new Scroller(getContext(), new AccelerateInterpolator());
			mDecelerateScroller = new Scroller(getContext(), new DecelerateInterpolator());
		}

		public FlingRunnable(View child, boolean colipse) {
			mChild = child;
			mStart = false;
			mTotal = 0;
			mCollipsed = colipse;
			mAccelerateScroller = new Scroller(getContext(), new AccelerateInterpolator());
			mDecelerateScroller = new Scroller(getContext(), new DecelerateInterpolator());
		}

		public int getDistance() {
			return mDistance;
		}

		private boolean startUsingDistance() {
			if (mDistance == 0)
				return false;

			if (getChildCount() <= 0) {
				return false;
			}

			ExpandVGalleryListener l = (ExpandVGalleryListener) mChild;
			mScroller = mode == EXPAND ? mDecelerateScroller : mDecelerateScroller;
			mStart = true;
			int distance = mode == EXPAND ? -mDistance : mDistance;
			l.start(distance);
			mScroller.startScroll(0, 0, distance, 0, mDuration);

			mLastFlingX = 0;
			return true;
		}

		public boolean collipsed() {
			return mCollipsed;
		}

		public View getChild() {
			return mChild;
		}

		public void start(int distance, long delayed, int duration) {
			mDistance = distance;
			start(delayed, duration);
		}

		public void start(long delayed, int duration) {
			float alpha = mode == EXPAND ? 0.0f : 1.0f;
			mDuration = duration;
			mFinished = false;
			mChild.setAlpha(alpha);
			postDelayed(this, delayed);
		}

		public boolean isFinished() {
			return mFinished;
		}
		
		public boolean isAllFinished() {
			ExpandVGalleryListener l = (ExpandVGalleryListener) mChild;
			if(l != null) {
				return l.isExpandFinished() && mFinished;
			} else {
				return mFinished;
			}
		}

		private void endFling(boolean scrollIntoSlots) {
			/*
			 * Force the scroller's status to finished (without setting its
			 * position to the end)
			 */
			mScroller.forceFinished(true);
			mStart = false;
			mTotal = 0;
			mFinished = true;

			ExpandVGalleryListener l = (ExpandVGalleryListener) mChild;
			l.stop();

			mCollipsed = mode == COLlIPSE ? true : false;

			reportExpandState(false);
		}

		@Override
		public void run() {
			if (!mStart) {
				if (startUsingDistance()) {
					post(this);
					return;
				} else {
					return;
				}
			}

			boolean more = mScroller.computeScrollOffset();
			final int x = mScroller.getCurrX();
			int deltaX = mLastFlingX - x;
			mTotal += deltaX;
			float alpha = Math.abs((float) mTotal / mDistance);
			alpha = mode == EXPAND ? alpha : 1.0f - alpha;
			if(mChild == getSelectedView()){
				if(mOnSelectAlphaChangedListener != null){
					mOnSelectAlphaChangedListener.onAlphaChanged(mChild, alpha);
				}
			}
			mChild.setAlpha(alpha);
			mChild.offsetLeftAndRight(deltaX);
			if (more) {
				mLastFlingX = x;
				invalidate();
				post(this);
			} else {
				endFling(true);
			}

		}

	}

}
