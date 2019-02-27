package com.yunos.tv.app.widget.round;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView.ScaleType;

import com.yunos.tv.aliTvSdk.R;

/*
 * from:
 * https://github.com/vinc3m1/RoundedImageView
 * 
 * 去掉圆角之后不要使用BitmapShader的方式 
 */
@SuppressWarnings("UnusedDeclaration")
public class RoundedImageView extends View {

	public static final String TAG = "RoundedImageView";

	protected int mCornerRadius = 20;
	protected Drawable mDrawable;
	private BitmapShader mBitmapShader;
	private Matrix mShaderMatrix = new Matrix();
	protected Paint mBitmapPaint = new Paint();
	protected RectF mBounds = new RectF();
	protected Rect mDrawableBounds = new Rect();
	private RectF mBitmapRect = new RectF();
	protected boolean mLayouted = false;
	protected boolean mReset = false;
	private ScaleType mScaleType = ScaleType.FIT_XY;

	protected boolean needHandleRoundImage = false;
	protected Rect mannulPadding = new Rect();
	protected RectF mannulBounds = new RectF();

	public RoundedImageView(Context context) {
		super(context);
		init_RoundedImageView(context, null);
	}

	public RoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init_RoundedImageView(context, attrs);
	}

	public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init_RoundedImageView(context, attrs);
	}

	private void init_RoundedImageView(Context context, AttributeSet attrs) {
		mBitmapPaint.setStyle(Paint.Style.FILL);
		mBitmapPaint.setAntiAlias(true);
		if (attrs != null) {
			try {
				TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageViewAttr);
				if (types == null) {
					return;
				}
				int default_corner = context.getResources().getDimensionPixelSize(R.dimen.default_corner);
				mCornerRadius = types.getDimensionPixelSize(R.styleable.RoundedImageViewAttr_cornerRadius, default_corner);
				
				Drawable d = types.getDrawable(R.styleable.RoundedImageViewAttr_src);
				if (d != null) {
					setImageDrawable(d);
				}
				types.recycle();
			} catch (Exception e) {

			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		if (mDrawable != null) {
			if (needHandleRoundImage) {
				if (mCornerRadius > 0) {
					canvas.drawRoundRect(!mannulBounds.isEmpty() ? mannulBounds : mBounds, mCornerRadius, mCornerRadius, mBitmapPaint);
				} else {
					canvas.drawRect(mannulBounds.isEmpty() ? mannulBounds : mBounds, mBitmapPaint);
				}
			} else {
				if (!mannulBounds.isEmpty()) {
					mDrawableBounds.set((int) mannulBounds.left, (int) mannulBounds.top, (int) mannulBounds.right,
							(int) mannulBounds.bottom);
				}
				if (!mDrawableBounds.isEmpty()) {
					mDrawable.setBounds(mDrawableBounds);
				}
				mDrawable.draw(canvas);
			}

		}
	}

	// @SuppressLint("WrongCall")
	// protected void superDraw(Canvas canvas) {
	// super.onDraw(canvas);
	// }

	public void setScaleType(ScaleType type) {
		mScaleType = type;
		if (mLayouted && !isLayoutRequested()) {
			computeBounds();
		}
	}

	@Override
	public void layout(int l, int t, int r, int b) {
		super.layout(l, t, r, b);
		computeBounds();
		mLayouted = true;
		if (mReset) {
			reset();
			mReset = false;
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 视图重用时，可能前后的大小不一致，此时需要重新计算mBounds的大小，一般在layout中重新计算
		mReset = true;
	}

	void computeBounds() {
		if (mScaleType.equals(ScaleType.CENTER_INSIDE)) {
			if (mDrawable != null) {
				float scale = 1.0f;
				int dwidth = mDrawable.getIntrinsicWidth();
				int dheight = mDrawable.getIntrinsicHeight();

				int vwidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
				int vheight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
				if (dwidth <= vwidth && dheight <= vheight) {
					scale = 1.0f;
				} else {
					scale = Math.min((float) vwidth / (float) dwidth, (float) vheight / (float) dheight);
				}

				int newWidth = (int) (dwidth * scale);
				int newHeight = (int) (dheight * scale);
				int dx = (int) ((vwidth - newWidth) * 0.5f + 0.5f);
				int dy = (int) ((vheight - newHeight) * 0.5f + 0.5f);
				dx += getPaddingLeft();
				dy += getPaddingTop();
				mBounds.set(dx, dy, dx + newWidth, dy + newHeight);
				mDrawableBounds.set((int) mBounds.left, (int) mBounds.top, (int) mBounds.right, (int) mBounds.bottom);
				mannulBounds.set(mBounds.left + mannulPadding.left, mBounds.top + mannulPadding.top, mBounds.right - mannulPadding.right,
						mBounds.bottom - mannulPadding.bottom);
			} else {
				mBounds.setEmpty();
				mDrawableBounds.setEmpty();
				mannulBounds.setEmpty();
			}
		} else {
			mBounds.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
			mDrawableBounds.set((int) mBounds.left, (int) mBounds.top, (int) mBounds.right, (int) mBounds.bottom);
			mannulBounds.set(mBounds.left + mannulPadding.left, mBounds.top + mannulPadding.top, mBounds.right - mannulPadding.right,
					mBounds.bottom - mannulPadding.bottom);
		}
	}

	@Override
	public void requestLayout() {
		super.requestLayout();
		mLayouted = false;
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		invalidate();
	}

	public void setImageDrawable(Drawable drawable) {
		mDrawable = drawable;
		if (mLayouted) {
			computeBounds();
			reset();
		} else {
			mReset = true;
		}
	}

	public void setImageBitmap(Bitmap bitmap) {
		mDrawable = bitmap == null ? null : new BitmapDrawable(getResources(), bitmap);
		if (mLayouted) {
			reset();
		} else {
			mReset = true;
		}
	}

	public void reset() {
		// mBitmapRect.set(getPaddingLeft(), getPaddingTop(), getPaddingLeft() +
		// mDrawable.getIntrinsicWidth(), getPaddingTop() +
		// mDrawable.getIntrinsicHeight());
		if (mDrawable != null && needHandleRoundImage && mDrawable instanceof BitmapDrawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) mDrawable;
			Bitmap b = bitmapDrawable.getBitmap();
			mBitmapRect.set(0, 0, b.getWidth(), b.getHeight());
			mShaderMatrix.set(null);
			mShaderMatrix.setRectToRect(mBitmapRect, mBounds, Matrix.ScaleToFit.FILL);

			mBitmapShader = new BitmapShader(b, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			mBitmapShader.setLocalMatrix(mShaderMatrix);

			mBitmapPaint.setShader(mBitmapShader);
			// mBitmapPaint.setAlpha(100);//0是全透明
		}

		invalidate();
	}

	@Override
	public void invalidate() {
//		if (Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			super.invalidate();
		} else {
			super.postInvalidate();
		}
	}

	public int getCornerRadius() {
		return mCornerRadius;
	}

	public void setCornerRadius(int radius) {
		if (!needHandleRoundImage) {
			return;
		}
		if (mCornerRadius == radius) {
			return;
		}

		mCornerRadius = radius;
		invalidate();
	}

//	public void release() {
//		if (mDrawable != null) {
//			mDrawable.setCallback(null);
//			mDrawable = null;
//		}
//		mBitmapShader = null;
//		mShaderMatrix = null;
//		mBitmapPaint = null;
//		mBounds = null;
//		mBitmapRect = null;
//	}

	public void setNeedHandleRoundImage(boolean needHandleRoundImage) {
		this.needHandleRoundImage = needHandleRoundImage;
	}

	public void setMannulPadding(int left, int top, int right, int bottom) {
		this.mannulPadding.set(left, top, right, bottom);
	}
	
	public Drawable getDrawable() {
		return mDrawable;
	}
}
