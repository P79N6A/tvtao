package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

public class SeekBar extends View {
	private int maxValue = 100;

	public SeekBar(Context context) {
		super(context);
	}

	public SeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private SeekDrawable mBackgroundDrawable = new SeekDrawable();
	private SeekDrawable mProgressDrawable = new SeekDrawable();
	private SeekDrawable mSecProgressDrawable = new SeekDrawable();

	/**
	 * A callback that notifies clients when the progress level has been
	 * changed. This includes changes that were initiated by the user through a
	 * touch gesture or arrow key/trackball as well as changes that were
	 * initiated programmatically.
	 */
	public interface OnSeekBarChangeListener {

		/**
		 * Notification that the progress level has changed. Clients can use the
		 * fromUser parameter to distinguish user-initiated changes from those
		 * that occurred programmatically.
		 * 
		 * @param seekBar
		 *            The SeekBar whose progress has changed
		 * @param progress
		 *            The current progress level. This will be in the range
		 *            0..max where max was set by
		 *            {@link ProgressBar#setMax(int)}. (The default value for
		 *            max is 100.)
		 * @param fromUser
		 *            True if the progress change was initiated by the user.
		 */
		void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);

		/**
		 * Notification that the user has started a touch gesture. Clients may
		 * want to use this to disable advancing the seekbar.
		 * 
		 * @param seekBar
		 *            The SeekBar in which the touch gesture began
		 */
		void onStartTrackingTouch(SeekBar seekBar);

		/**
		 * Notification that the user has finished a touch gesture. Clients may
		 * want to use this to re-enable advancing the seekbar.
		 * 
		 * @param seekBar
		 *            The SeekBar in which the touch gesture began
		 */
		void onStopTrackingTouch(SeekBar seekBar);
	}

	private OnSeekBarChangeListener mOnSeekBarChangeListener;

	public void setMaxValue(int max) {
		maxValue = max;
	}

	public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
		mOnSeekBarChangeListener = l;
	}

	public void setDrawable(Drawable background, Drawable progress, Drawable secProgress) {
		mBackgroundDrawable.setDrawable(background);
		mBackgroundDrawable.setProgress(maxValue);
		mProgressDrawable.setDrawable(progress);
		mSecProgressDrawable.setDrawable(secProgress);

		invalidate();
	}
	
	public void enableRadius(boolean enable){
	    mBackgroundDrawable.enableRadius(enable);
	    mProgressDrawable.enableRadius(enable);
	    mSecProgressDrawable.enableRadius(enable);
	}

	public int getProgressRadius() {
		if (mProgressDrawable == null) {
			throw new NullPointerException("You did not set progress drawable by setDrawable");
		}
		return mProgressDrawable.getRadius();
	}

	public int getSecondaryProgressRadius() {
		if (mSecProgressDrawable == null) {
			throw new NullPointerException("You did not set secondary progress drawable by setDrawable");
		}
		return mSecProgressDrawable.getRadius();
	}

	public int getBackgroundRadius() {
		if (mBackgroundDrawable == null) {
			throw new NullPointerException("You did not set background drawable by setDrawable");
		}
		return mBackgroundDrawable.getRadius();
	}

	public int getProgressWidth() {
		if (mProgressDrawable == null) {
			throw new NullPointerException("You did not set progress drawable by setDrawable");
		}
		return mProgressDrawable.getWidth();
	}

	public int getSecondaryProgressWidth() {
		if (mSecProgressDrawable == null) {
			throw new NullPointerException("You did not set secondary progress drawable by setDrawable");
		}
		return mSecProgressDrawable.getWidth();
	}

	public int getBackgroundWidth() {
		if (mBackgroundDrawable == null) {
			throw new NullPointerException("You did not set background drawable by setDrawable");
		}
		return mBackgroundDrawable.getWidth();
	}

	public void setProgress(int progress) {
		if (progress < 0) {
			progress = 0;
		}

		if (progress > maxValue) {
			progress = maxValue;
		}

		if (mProgressDrawable.getProgress() == progress) {
			return;
		}

		mProgressDrawable.setProgress(progress);
		refreshProgress();
		invalidate();
	}

	public int getProgress() {
		return mProgressDrawable.getProgress();
	}

	public void setSecProgress(int progress) {
		if (mSecProgressDrawable.getProgress() == progress) {
			return;
		}

		mSecProgressDrawable.setProgress(progress);
		invalidate();
	}

	public void refreshProgress() {
		if (mOnSeekBarChangeListener != null) {
			mOnSeekBarChangeListener.onProgressChanged(this, getProgress(), false);
		}
	}

	@Override
	public void invalidate() {
		if (Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()) {
			super.invalidate();
		} else {
			super.postInvalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mBackgroundDrawable.draw(canvas);
		mSecProgressDrawable.draw(canvas);
		mProgressDrawable.draw(canvas);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mBackgroundDrawable.onMessure();
		mSecProgressDrawable.onMessure();
		mProgressDrawable.onMessure();
	}

	class SeekDrawable {
		private Paint mBitmapPaint = new Paint();
		private BitmapShader mBitmapShader;
		private Matrix mShaderMatrix = new Matrix();
		BitmapDrawable mDrawable;
		RectF mRect = new RectF();
		RectF mBitmapRect = new RectF();
		int mProgress = 0;
		int mRadius = 50;
		boolean mEnableRadius = true;
		int mPadding = 0;
		int mLastProgress = -1;
		boolean mIsMessured = false;

		public SeekDrawable() {

		}

		public SeekDrawable(Drawable d) {
			mDrawable = (BitmapDrawable) d;
			initMatrix();
		}

		public void setDrawable(Drawable d) {
			mDrawable = (BitmapDrawable) d;
			initMatrix();
		}

		public void enableRadius(boolean enable){
		    mEnableRadius = enable;
		}
		
		public void onMessure() {
			mPadding = (getMeasuredHeight() - mDrawable.getBitmap().getHeight()) / 2;
			mRadius = mEnableRadius ? mDrawable.getBitmap().getHeight() / 2 : 0;
			mIsMessured = true;
		}

		public int getRadius() {
			return mEnableRadius ? mRadius : 0;
		}

		public int getWidth() {
			return (int) mRect.width();
		}

		private void initMatrix() {
			mBitmapPaint.setStyle(Paint.Style.FILL);
			mBitmapPaint.setAntiAlias(true);
			mBitmapShader = new BitmapShader(mDrawable.getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			mShaderMatrix.set(null);
			mBitmapShader.setLocalMatrix(mShaderMatrix);
			mBitmapPaint.setShader(mBitmapShader);

			mBitmapRect.set(0, 0, mDrawable.getBitmap().getWidth(), mDrawable.getBitmap().getHeight());
		}

		public void setProgress(int progress) {
			mProgress = progress;
			updateRect();
		}

		public int getProgress() {
			return mProgress;
		}

		private void updateRect() {
			if (!mIsMessured) {
				return;
			}
			
			if (mLastProgress != mProgress) {
				int diameter = mRadius * 2;
				int width = diameter + (SeekBar.this.getWidth() - diameter) * mProgress / maxValue;
				int offset = SeekBar.this.getWidth() - width;
				mRect.set(-offset, mPadding, width, getHeight() - mPadding);
			    mShaderMatrix.setRectToRect(mBitmapRect, mRect, Matrix.ScaleToFit.FILL);
				mRect.left = 0;
				mBitmapShader.setLocalMatrix(mShaderMatrix);
			}

			mLastProgress = mProgress;
		}

		public void draw(Canvas canvas) {
			if (mDrawable == null) {
				return;
			}

			updateRect();
			if(mEnableRadius){
			    canvas.drawRoundRect(mRect, mRadius, mRadius, mBitmapPaint);
			} else {
			    canvas.drawRect(mRect, mBitmapPaint);
			}
		}

	}
}
