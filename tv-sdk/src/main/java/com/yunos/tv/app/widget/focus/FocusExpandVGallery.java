package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.yunos.tv.app.widget.focus.listener.ExpandVGalleryListener;

import java.util.ArrayList;

public class FocusExpandVGallery extends FocusVGallery implements ExpandVGalleryListener {
	protected static final String TAG = "FocusExpandVGallery";
	protected static final boolean DEBUG = false;

	public FocusExpandVGallery(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public FocusExpandVGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public FocusExpandVGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	ScaleRunnable mScaleRunnable = new ScaleRunnable();
	boolean mStart = false;
	ArrayList<FlingRunnable> mFlingManager = null;
	int mFlingDuration = 200;
	boolean mCollipseWhenLostFocus = true;
	boolean mIsCurrentExpand = false;
	boolean mIsFling = false;// 如果FocusExpandFlingLinearLayout在滚动和View本身在滚动，都不允许layout。
	boolean mNeedLayout = false;

	int mOffset = 50;
	float mOffsetCoef = 0.5f;
	int mLayoutOffsetX = 0;

	public void expand() {
		mIsCurrentExpand = true;
		mScaleRunnable.expand();
	}

	public void collipse() {
		mIsCurrentExpand = false;
		mScaleRunnable.collipse();
	}

	public void setCollipseWhenLostFocus(boolean collipse) {
		mCollipseWhenLostFocus = collipse;
	}

	@Override
	public void offsetLeftAndRight(int offset) {
		mLayoutOffsetX += offset;
		super.offsetLeftAndRight(offset);
		if (!mIsCurrentExpand) {
			return;
		}

		for (int index = 0; index < getChildCount(); index++) {
			FlingRunnable runnable = mFlingManager.get(index);
			// float coef = runnable.getCoef();
			// int diff = (int) (offset * (1.0f - coef));
			// runnable.getChild().offsetLeftAndRight(-diff);
			// runnable.offset(diff);
			int maxOffset = runnable.getMaxOffset();
			maxOffset = -maxOffset;
			if (offset < 0) {
				if (maxOffset < runnable.getDistance()) {
					int diff = maxOffset - runnable.getDistance();
					if (diff < offset) {
						runnable.offset(offset);
						runnable.getChild().offsetLeftAndRight(-offset);
					} else {
						runnable.offset(diff);
						runnable.getChild().offsetLeftAndRight(-diff);
					}

					if (maxOffset >= runnable.getDistance() && !runnable.isStart()) {
						// runnable.start();
					}
				}
			} else {
				if (maxOffset > runnable.getDistance()) {
					int diff = maxOffset - runnable.getDistance();
					if (diff > offset) {
						runnable.offset(offset);
						runnable.getChild().offsetLeftAndRight(-offset);
					} else {
						runnable.offset(diff);
						runnable.getChild().offsetLeftAndRight(-diff);

					}
				}

				if (maxOffset <= runnable.getDistance() && !runnable.isStart()) {
					// runnable.start();
				}
			}

		}
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		Log.d(TAG, "onFocusChanged");
		if (gainFocus) {
			if (mLayouted) {
				expand();
			}
		} else {
			if (mLayouted && mCollipseWhenLostFocus) {
				collipse();
			}
		}
	}

	/**
	 * 是否在做移动或者放大缩小
	 * 
	 * @return
	 */
	public boolean isMove() {
		return !(mScaleRunnable.isFinished() && !mIsFling);
	}

	/**
	 * 判断左右平移是否完成
	 * 
	 * @return
	 */
	public boolean isFlingFinished() {
		if (mFlingManager != null) {
			for (int index = 0; index < mFlingManager.size(); index++) {
				if (mFlingManager.get(index) != null && !mFlingManager.get(index).isFinished()) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		super.onLayout(changed, l, t, r, b);
		super.offsetLeftAndRight(mLayoutOffsetX);

		if (!isLayoutRequested()) {
			return;
		}
		if (getChildCount() > 0) {
			if (mFlingManager == null) {
				int count = getHeight() / getSelectedView().getHeight() + 5;
				mFlingManager = new ArrayList<FlingRunnable>(count);
				for (int index = 0; index < count; index++) {
					mFlingManager.add(new FlingRunnable());
				}
			}
		}
		mScaleRunnable.setCollipse();
		if (mIsCurrentExpand) {
			expand();
		} else {
			collipse();
		}
	}
	
	public void resetChilds(){
		mScaleRunnable.setExpand();
	}

	private class FlingRunnable implements Runnable {
		private Scroller mScroller;
		private View mChild;
		/**
		 * X value reported by mScroller on the previous fling
		 */
		private int mLastFlingX;
		private int mDuration;
		private int mDistance;
		private boolean mStart = false;
		private float mCoef = 1.0f;
		private int mMaxOffset = 0;

		public FlingRunnable() {
			mScroller = new Scroller(getContext(), new DecelerateInterpolator());
		}

		public void setChild(View child) {
			mChild = child;
		}

		public void reset() {
			mMaxOffset = 0;
			mDistance = 0;
			mStart = false;
		}

		public void setCoef(float coef) {
			mCoef = coef;
		}

		public float getCoef() {
			return mCoef;
		}

		public void setDuration(int duration) {
			mDuration = duration;
		}

		public int getDistance() {
			return mDistance;
		}

		public void setMaxOffset(int offset) {
			mMaxOffset = offset;
		}

		public int getMaxOffset() {
			return mMaxOffset;
		}

		public void offset(int offset) {
			mDistance += offset;
		}

		public View getChild() {
			return mChild;
		}

		public boolean isStart() {
			return mStart;
		}

		public void start(int duration) {
			if (mDistance == 0) {
				if (isFlingFinished()) {
					mIsFling = false;
				}
				return;
			}

			mStart = true;
			mScroller.startScroll(0, 0, -mDistance, 0, duration);
			mLastFlingX = 0;
			post(this);
		}

		public boolean isFinished() {
			return mScroller.isFinished();
		}

		public void stop() {
			if (!isFinished()) {
				int deltaX = mLastFlingX - mScroller.getFinalX();
				if (mChild != null) {
					mChild.offsetLeftAndRight(deltaX);
				}

			}

			removeCallbacks(this);
			mDistance = 0;
			mScroller.forceFinished(true);
		}

		@Override
		public void run() {
			boolean more = mScroller.computeScrollOffset();
			final int x = mScroller.getCurrX();
			int deltaX = mLastFlingX - x;

			if (mChild != null) {
				mChild.offsetLeftAndRight(deltaX);
			}

			if (more) {
				invalidate();
				mLastFlingX = x;
				post(this);
			} else {
				stop();
				if (isFlingFinished()) {
					mIsFling = false;
				}
			}
		}

	}

	private class ScaleRunnable implements Runnable {

		private Scroller mScroller;
		private static final int EXPAND = 2001;
		private static final int COLLIPSE = 2002;
		private int mode = COLLIPSE;

		private int mMinValue = 0;
		private int mMaxvalue = 100;
		private int mCurrent = mMinValue;
		private int mDuration = 300;

		public ScaleRunnable() {
			mScroller = new Scroller(getContext(), new DecelerateInterpolator());
		}

		public void setCollipse() {
			mode = COLLIPSE;
			mCurrent = mMinValue;
			trackScale(mMinValue);
		}

		public void setExpand(){
			mode = EXPAND;
			mCurrent = mMaxvalue;
			trackScale(mMaxvalue);
		}
		
		public void expand() {
			if (mode == EXPAND) {
				// Log.d("test", "drop expand"+" mode:" + mode +
				// ";finish:"+isFinished()+"X:"+mScroller.getCurrX()+";id:"+getId());
				return;
			}
			mode = EXPAND;
			mScroller.startScroll(mCurrent, 0, mMaxvalue - mCurrent, 0, mDuration);
			post(this);
		}

		public void collipse() {
			if (mode == COLLIPSE) {
				// Log.d("test", "drop collipse"+" mode:" + mode +
				// ";finish:"+isFinished()+"X:"+mScroller.getCurrX()+";id:"+getId());
				return;
			}

			mode = COLLIPSE;
			mScroller.startScroll(mCurrent, 0, mMinValue - mCurrent, 0, mDuration);
			post(this);
		}

		private void trackScale(int x) {
			float scale = (float) x / 100;

			for (int index = 0; index < getChildCount(); index++) {
				if (index == getSelectedItemPosition() - getFirstVisiblePosition()) {
					continue;
				}

				View child = getChildAt(index);
				child.setScaleX(scale);
				child.setScaleY(scale);
				child.setAlpha(scale);
			}
		}

		public boolean isFinished() {
			return mScroller.isFinished();
		}

		@Override
		public void run() {
			boolean more = mScroller.computeScrollOffset();
			final int x = mScroller.getCurrX();
			trackScale(x);
			mCurrent = x;

			if (more) {
				invalidate();
				post(this);
			}
		}

	}

	@Override
	public void start(int scrollDistance) {
		for (int index = 0; index < getChildCount(); index++) {
			FlingRunnable runnable = mFlingManager.get(index);
			runnable.stop();
		}

		if(getChildCount() > 0) {
			mIsFling = true;// FocusExpandFlingLinearLayout 在滚动,如果列表为空则不能加入该条不然会导致整个上下按键不能使用
		}
		int upCount = getSelectedItemPosition() - getFirstVisiblePosition();
		int downCount = getLastVisiblePosition() - getSelectedItemPosition();
		int maxCount = 0;
		if (downCount >= upCount) {
			for (int i = getSelectedItemPosition() + 1 - getFirstVisiblePosition(); i <= getLastVisiblePosition() - getFirstVisiblePosition(); i++) {
				View child = getChildAt(i);
				if (child != null && child.getTop() > 0 && child.getBottom() < getHeight()) {
					maxCount++;
				}
			}
		} else {
			for (int i = getFirstVisiblePosition(); i <= getSelectedItemPosition() - getFirstVisiblePosition() - 1; i++) {
				View child = getChildAt(i);
				if (child != null && child.getTop() > 0 && child.getBottom() < getHeight()) {
					maxCount++;
				}
			}
		}

		if (maxCount <= 0) {
			maxCount = Math.max(upCount, downCount) - 1;
		}
		mOffset = scrollDistance / maxCount;

		for (int index = 0; index < getChildCount(); index++) {
			FlingRunnable runnable = mFlingManager.get(index);
			runnable.reset();
			runnable.setDuration(mFlingDuration);
			runnable.setChild(getChildAt(index));

			int pow = Math.abs(index - (getSelectedItemPosition() - getFirstVisiblePosition()));
			int diff = mOffset * pow;
			float coef = (float) (Math.abs(scrollDistance) - diff) / Math.abs(scrollDistance);
			runnable.setCoef(coef);
			runnable.setMaxOffset(diff);
		}
		mStart = true;
	}

	@Override
	public void stop() {
		mStart = false;
		for (int index = 0; index < getChildCount(); index++) {
			FlingRunnable runnable = mFlingManager.get(index);
			int pow = Math.abs(index - (getSelectedItemPosition() - getFirstVisiblePosition()));
			float coef = (float) Math.pow(0.8, pow);
			int duration = (int) (mFlingDuration * pow * coef);
			runnable.start(duration);
		}

	}
	
	@Override
	public boolean isExpandFinished() {
		return isFlingFinished();
	}

}
