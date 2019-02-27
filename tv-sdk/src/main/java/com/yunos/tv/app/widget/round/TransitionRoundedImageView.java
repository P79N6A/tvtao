package com.yunos.tv.app.widget.round;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;

import com.yunos.tv.aliTvSdk.R;

public class TransitionRoundedImageView extends RoundedImageView {
	public static final String TAG = "TransitionRoundedImageView";
	private final boolean DEBUG = false;
	/**
	 * A transition is about to start.
	 */
	private static final int TRANSITION_STARTING = 0;

	/**
	 * The transition has started and the animation is in progress
	 */
	private static final int TRANSITION_RUNNING = 1;

	/**
	 * No transition will be applied
	 */
	private static final int TRANSITION_NONE = 2;

	public static final int MAX_FRAME = 10;
	private int mFrameCount = 0;
	private int mCurrentFrame = 0;

	private int mTransitionState = TRANSITION_NONE;
	private long mStartTimeMillis;
	private int mFrom = 0;
	private int mTo = 255;
	private int mDuration;
	private int mAlpha = 0;
	private Drawable mOldDrawable;
	protected Paint mOldBitmapPaint = null;
	
	public TransitionRoundedImageView(Context context) {
		super(context);
	}

	public TransitionRoundedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init_TransitionRoundedImageView(context, attrs);
	}

	public TransitionRoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (!isInEditMode()) {
			init_TransitionRoundedImageView(context, attrs);
		}
	}
	
	void init_TransitionRoundedImageView(Context context, AttributeSet attrs) {
		try {
			TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageViewAttr);
			mFrameCount = types.getInteger(R.styleable.RoundedImageViewAttr_frameCount, 0);
			types.recycle();
		} catch (Exception e) {

		}
	}
	
	public void setDrawableTime(BitmapDrawable drawable, int durationMillis) {
		if (durationMillis <= 0) {
			setDrawableImmediate(drawable);
			return;
		}
		if (drawable == null) {
			setDrawableImmediate(null);
			return;
		} else {
			mOldDrawable = mDrawable;
			mDrawable = drawable;
		}
		startTransition();
		mDuration = durationMillis;

		if (mLayouted) {
			computeBounds();
			reset();
		} else {
			mReset = true;
		}
	}
	
	public void setDrawableFrame(BitmapDrawable drawable, int frame) {
		if (frame <= 0) {
			setDrawableImmediate(drawable);
			return;
		}
		if (drawable == null) {
			setDrawableImmediate(null);
			return;
		} else {
			mOldDrawable = mDrawable;
			mDrawable = drawable;
		}
		mFrameCount = frame;
		startTransition();

		if (mLayouted) {
			computeBounds();
			reset();
		} else {
			mReset = true;
		}
	}
	
	public void setImageDrawable(Drawable drawable) {
		setDrawableImmediate(drawable);
	}
	
	public void setDrawableImmediate(Drawable drawable) {
		releaseOldBitmap();
		mDrawable = drawable;
		endTransition();
		if (mLayouted) {
			computeBounds();
			reset();
		} else {
			mReset = true;
		}
	}

	/**
	 * 按时间渐变
	 * 
	 * @param bitmap
	 * @param durationMillis
	 */
	public void setBitmapTime(Bitmap bitmap, int durationMillis) {
		if (durationMillis <= 0) {
			setBitmapImmediate(bitmap);
			return;
		}
		if (bitmap == null) {
			setBitmapImmediate(null);
			return;
		} else {
			mOldDrawable = mDrawable;
			mDrawable = new BitmapDrawable(getResources(), bitmap);
		}
		startTransition();
		mDuration = durationMillis;

		if (mLayouted) {
			reset();
		} else {
			mReset = true;
		}
	}

	/**
	 * 按帧数渐变
	 * 
	 * @param bitmap
	 * @param frame
	 */
	public void setBitmapFrame(Bitmap bitmap, int frame) {
		if (frame <= 0) {
			setBitmapImmediate(bitmap);
			return;
		}
		if (bitmap == null) {
			setBitmapImmediate(null);
			return;
		} else {
			mOldDrawable = mDrawable;
			mDrawable = new BitmapDrawable(getResources(), bitmap);
		}
		mFrameCount = frame;
		startTransition();

		if (mLayouted) {
			reset();
		} else {
			mReset = true;
		}
	}
	
	public void setImageBitmap(Bitmap bitmap) {
		setBitmapImmediate(bitmap);
	}
	
	public void setBitmapImmediate(Bitmap bitmap) {
		releaseOldBitmap();
		mDrawable = bitmap == null ? null : new BitmapDrawable(getResources(), bitmap);
		endTransition();
		
		if (mLayouted) {
			reset();
		} else {
			mReset = true;
		}
	}

	void startTransition() {
		mAlpha = 0;
		mTransitionState = TRANSITION_STARTING;
		mCurrentFrame = 0;
	}

	void endTransition() {
		mAlpha = 0xFF;
		mTransitionState = TRANSITION_NONE;
	}

	boolean frameDraw() {
		boolean done = false;

		switch (mTransitionState) {
		case TRANSITION_STARTING:
			mCurrentFrame = 0;
			mStartTimeMillis = SystemClock.uptimeMillis();
			done = false;
			mTransitionState = TRANSITION_RUNNING;
			break;
		case TRANSITION_RUNNING:
			if (mCurrentFrame < mFrameCount) {
				mAlpha = (int) (255 * mCurrentFrame / mFrameCount);
				mCurrentFrame++;
			} else {
				done = true;
				if (DEBUG) {
					Log.d(TAG, "frameDraw mCurrentFrame:" + testFrame);
				}
			}
			break;
		case TRANSITION_NONE:
			done = true;
			break;
		}
		
		if (done) {
			mAlpha = 0xFF;
		}
		
		return done;
	}

	int testFrame = 0;
	boolean timeDraw() {
		boolean done = false;
		switch (mTransitionState) {
		case TRANSITION_STARTING:
			mStartTimeMillis = SystemClock.uptimeMillis();
			done = false;
			mTransitionState = TRANSITION_RUNNING;
			testFrame = 0;
			break;
		case TRANSITION_RUNNING:
			if (mStartTimeMillis >= 0) {
				float normalized = (float) (SystemClock.uptimeMillis() - mStartTimeMillis) / mDuration;
				done = normalized >= 1.0f;
				normalized = Math.min(normalized, 1.0f);
				mAlpha = (int) (mFrom + (mTo - mFrom) * normalized);
				testFrame++;
			}
			break;
		case TRANSITION_NONE:
			done = true;
			break;
		}
		if (done) {
			mAlpha = 0xFF;
		}
		return done;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mDrawable == null) {
			releaseOldBitmap();
			super.onDraw(canvas);
			return;
		}

		boolean done = false;
		if (mFrameCount > 0) {
			done = frameDraw();
		} else {
			done = timeDraw();
		}
		
		if (mOldDrawable != null) {
			if (needHandleRoundImage) {
				mOldBitmapPaint.setAlpha(0xFF - mAlpha);
			} else {
				mOldDrawable.setAlpha(0xFF - mAlpha);
			}
			drawOldBitmap(canvas);
		}
		
		if (needHandleRoundImage) {
			mBitmapPaint.setAlpha(mAlpha);
		} else {
			mDrawable.setAlpha(mAlpha);
		}
		super.onDraw(canvas);
		if (!done) {
			invalidate();
		} else {
			releaseOldBitmap();
		}
	}
	
	void releaseOldBitmap() {
		if (mOldDrawable != null) {
			mOldDrawable.setCallback(null);
			mOldDrawable = null;
		}
	}
	
	private RectF mOldBitmapRect;
	private Matrix mOldShaderMatrix;
	private BitmapShader mOldBitmapShader;
	@Override
	public void reset() {
		if (mOldDrawable != null && needHandleRoundImage && mOldDrawable instanceof BitmapDrawable) {
			if (mOldBitmapPaint == null) {
				mOldBitmapPaint = new Paint();
				mOldBitmapPaint.setStyle(Paint.Style.FILL);
				mOldBitmapPaint.setAntiAlias(true);
				
				mOldBitmapRect = new RectF();
				mOldShaderMatrix = new Matrix();
			}
			
			BitmapDrawable bitmapDrawable = (BitmapDrawable) mOldDrawable;
			Bitmap b = bitmapDrawable.getBitmap();
			mOldBitmapRect.set(0, 0, b.getWidth(), b.getHeight());
			mOldShaderMatrix.set(null);
			mOldShaderMatrix.setRectToRect(mOldBitmapRect, mBounds, Matrix.ScaleToFit.FILL);

			mOldBitmapShader = new BitmapShader(b, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			mOldBitmapShader.setLocalMatrix(mOldShaderMatrix);
			
			mOldBitmapPaint.setShader(mOldBitmapShader);
		}
		
		super.reset();
	}
	
	void drawOldBitmap(Canvas canvas) {
		if (mOldDrawable == null) {
			return;
		}
		if (needHandleRoundImage) {
			if (mCornerRadius > 0) {
				canvas.drawRoundRect(!mannulBounds.isEmpty() ? mannulBounds : mBounds, mCornerRadius, mCornerRadius, mOldBitmapPaint);
			} else {
				canvas.drawRect(mannulBounds.isEmpty() ? mannulBounds : mBounds, mOldBitmapPaint);
			}
		} else {
			if (!mannulBounds.isEmpty()) {
				mDrawableBounds.set((int) mannulBounds.left, (int) mannulBounds.top, (int) mannulBounds.right,
						(int) mannulBounds.bottom);
			}
			if (!mDrawableBounds.isEmpty()) {
				mOldDrawable.setBounds(mDrawableBounds);
			}
			mOldDrawable.draw(canvas);
		}
	}
	

	public int getFrameCount() {
		return mFrameCount;
	}

	public void setFrameCount(int frameCount) {
		this.mFrameCount = frameCount;
	}

}
