package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SurfaceProgressbar extends SurfaceView {
	static final String TAG = "SurfaceProgressbar";

	public SurfaceProgressbar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mSfh = getHolder();
	}

	public SurfaceProgressbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mSfh = getHolder();
	}

	public SurfaceProgressbar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mSfh = getHolder();

	}

	Matrix matrix = new Matrix();
	BitmapDrawable mDrawable;
	Rect mDrawablePadding = new Rect();
	SurfaceHolder mSfh;
	int mDigree = 0;
	boolean mIsStop = false;

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(mDrawable.getBitmap().getWidth() + this.getPaddingLeft() + this.getPaddingRight(), mDrawable.getBitmap()
				.getHeight() + this.getPaddingBottom() + this.getPaddingTop());
	}

	public void setProgressResId(int id) {
		// mDrawable = (BitmapDrawable)
		// getResources().getDrawable(R.drawable.tui_ic_progressbarindicator_big);
		mDrawable = (BitmapDrawable) getResources().getDrawable(id);
		mDrawable.getPadding(mDrawablePadding);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		start();
	}

	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		stop();
	}

	private void stop() {
		mIsStop = true;
	}

	private void start() {
		mIsStop = false;
		new Thread() {
			public void run() {
				while (!mIsStop) {
					Canvas canvas = mSfh.lockCanvas();
					if (canvas == null) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						continue;
					}
					canvas.save();

					int left = SurfaceProgressbar.this.getLeft();
					int top = SurfaceProgressbar.this.getTop();
					int right = SurfaceProgressbar.this.getRight();
					int bottom = SurfaceProgressbar.this.getBottom();

					canvas.translate(SurfaceProgressbar.this.getPaddingLeft(), SurfaceProgressbar.this.getPaddingTop());
					canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

					int bitLeft = left + SurfaceProgressbar.this.getPaddingLeft() + mDrawablePadding.left;
					int bitRight = right - SurfaceProgressbar.this.getPaddingRight() - mDrawablePadding.right;
					int bitTop = top + SurfaceProgressbar.this.getPaddingTop() + mDrawablePadding.top;
					int bitBottom = bottom - SurfaceProgressbar.this.getPaddingBottom() - mDrawablePadding.bottom;

					matrix.setRotate(mDigree, (bitLeft + bitRight) / 2 - left, (bitTop + bitBottom) / 2 - top);
					mDigree += 10;

					mDrawable.setBounds(bitLeft - left, bitTop - top, bitRight - left, bitBottom - top);
					canvas.setMatrix(matrix);
					mDrawable.draw(canvas);
					canvas.restore();
					mSfh.unlockCanvasAndPost(canvas);

					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
}
