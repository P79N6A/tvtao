package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.ArrayList;

public class RoundProgressBar extends View {

	public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RoundProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RoundProgressBar(Context context) {
		super(context);
		init();
	}

	final int MAX_DEGREE = 360;
	final int PAINT_MAX_ALPHA = 255;
	final int DEFAULT_OUTER_RADIUS = 50;
	final int DEFAULT_INNER_RADIUS = 10;

	final int MIN_ALPHA = 20;
	final int MAX_ALPHA = 100;

	int mMinAlpha = MIN_ALPHA;
	int mMaxAlpha = MAX_ALPHA;
	int mOuterRadius = DEFAULT_OUTER_RADIUS;
	int mInnerRadius = DEFAULT_INNER_RADIUS;
	int mPointNum = 8;

	int mStartDegree = 0;
	int mIntevel = -45;
	int mCenterX = 0;
	int mCenterY = 0;
	boolean mStart = false;
	int mDelayed = 5;
	ArrayList<RoundPosition> mRoundPositions;
	DropManager mDropManager = new DropManager();
	Paint mPaint = new Paint();
	Path mPath = new Path();
	int mFixedOverlapDegree = 0;
	int mDropIndex = 7;
	int mAlphaOffset = 0;
	int mColor = 0x00ccff;
	int mDegreeIntevel = 1;
	boolean mScraped = true;
	Drawable mDrawable;

	public void setScraped(boolean scraped) {
		mScraped = scraped;
	}

	public void setAlphaRange(int minAlpha, int maxAlpha) {
		if (minAlpha < 0) {
			throw new IllegalArgumentException("setAlphaRange: minAlpha must be > 0");
		}

		if (maxAlpha < 0) {
			throw new IllegalArgumentException("setAlphaRange: maxAlpha must be > 0");
		}

		if (maxAlpha <= minAlpha) {
			throw new IllegalArgumentException("setAlphaRange: maxAlpha must be > minAlpha");
		}

		mMinAlpha = minAlpha;
		mMaxAlpha = maxAlpha;
	}

	public void setPointNum(int num) {
		mPointNum = num;
		mIntevel = -MAX_DEGREE / num;
	}

	public void setDelayed(int delayed) {
		mDelayed = delayed;
	}

	public void setOuterDegreeIntevel(int intevel) {
		mDegreeIntevel = intevel;
	}

	public void setDefaultDropIndex(int index) {
		if (index < 0 || index > mPointNum) {
			throw new IllegalArgumentException("setDefaultDropIndex index = " + index + ", mPointNum = " + mPointNum);
		}
		mDropIndex = index;
	}

	public void setDropDegreeIntevel(int intevel) {
		mDropManager.mOffset = intevel;
	}

	public void setDropStartDelayed(int delayed) {
		mDropManager.mDelayed = delayed;
	}

	public void setDropControllCoef(float coef) {
		mDropManager.mControllCoef = coef;
	}

	private void init() {
		// setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(mColor);

		// mDrawable =
		// getContext().getResources().getDrawable(R.drawable.loading_point);
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (!mStart) {
			return;
		}
		if (getVisibility() != View.VISIBLE) {
			return;
		}

		int startDegree = mStartDegree + mAlphaOffset * mIntevel;
		int degree = startDegree;
		for (int index = mAlphaOffset; index < mPointNum + mAlphaOffset; index++) {
			int i = index % mPointNum;
			int x = (int) (mCenterX + Math.cos(degree * 2 * Math.PI / MAX_DEGREE) * mOuterRadius);
			int y = (int) (mCenterY - Math.sin(degree * 2 * Math.PI / MAX_DEGREE) * mOuterRadius);

			RoundPosition position = mRoundPositions.get(i);
			position.x = x;
			position.y = y;
			position.degree = degree;
			degree += mIntevel;
		}

		boolean isForeOverlap = mDropManager.isForeOverlap();
		boolean isBackOverlap = mDropManager.isBackOverlap();
		mPaint.setAlpha(255);
		mDropManager.draw(canvas);

		for (int index = mAlphaOffset; index < mPointNum + mAlphaOffset; index++) {
			int i = index % mPointNum;
			RoundPosition position = mRoundPositions.get(i);
			degree = startDegree - position.degree;
			degree = MAX_DEGREE - degree;

			int alpha = mMinAlpha + degree * (mMaxAlpha - mMinAlpha) / (MAX_DEGREE - mIntevel);
			alpha = alpha * PAINT_MAX_ALPHA / 100;
			if (!mDropManager.isFinished()) {
				if (isForeOverlap) {
					if (i == ((mDropIndex + 1) % mPointNum)) {
						alpha = PAINT_MAX_ALPHA;
					}
				}

				if (isBackOverlap) {
					if (i == mDropIndex) {
						alpha = PAINT_MAX_ALPHA;
					}
				}
			} else {
				if (i == ((mDropIndex + 1) % mPointNum)) {
					alpha = PAINT_MAX_ALPHA;
				}
			}

			drawCircle(canvas, position.x, position.y, alpha);
		}

		mStartDegree += -mDegreeIntevel;
		if (mStartDegree <= -MAX_DEGREE) {
			mStartDegree = 0;
		}

		postInvalidateDelayed(mDelayed);
	}

	void drawCircle(Canvas canvas, int x, int y, int alpha) {
		if (mDrawable != null) {
			drawDrawableCircle(canvas, x, y, alpha);
		} else {
			drawColorCircle(canvas, x, y, alpha);
		}
	}

	void drawColorCircle(Canvas canvas, int x, int y, int alpha) {
		mPaint.setAlpha(alpha);
		canvas.drawCircle(x, y, mInnerRadius, mPaint);
	}

	void drawDrawableCircle(Canvas canvas, int x, int y, int alpha) {
		mDrawable.setAlpha(alpha);
		Rect r = new Rect();
		r.set(x - mInnerRadius, y - mInnerRadius, x + mInnerRadius, y + mInnerRadius);
		mDrawable.setBounds(r);
		mDrawable.draw(canvas);
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mStart = false;
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		mStart = true;
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mCenterX = getMeasuredWidth() / 2;
		mCenterY = getMeasuredHeight() / 2;

		if (mRoundPositions == null) {
			mRoundPositions = new ArrayList<RoundPosition>(mPointNum);
			for (int index = 0; index < mPointNum; index++) {
				mRoundPositions.add(new RoundPosition());
			}

			double degree = Math.asin((double) mInnerRadius / mOuterRadius);
			mFixedOverlapDegree = (int) (degree * MAX_DEGREE / (Math.PI * 2));

			if (mScraped) {
				int diameter = (DEFAULT_OUTER_RADIUS + DEFAULT_INNER_RADIUS) * 2;
				int min = Math.min(getMeasuredHeight(), getMeasuredWidth());
				mInnerRadius = DEFAULT_INNER_RADIUS * min / diameter;
				mOuterRadius = min / 2 - mInnerRadius;

			}
		}
	}

	private class RoundPosition {
		int degree;
		int x;
		int y;
	}

	private class DropManager implements Runnable {
		final int FORE_BACK_START_DEGREE = 30;
		final float DEFAULT_COEF = 0.6f;
		int mForeDegree = 0;
		int mBackDegree = 0;
		int mOffset = -3;
		boolean mIsReseting = false;
		int mDelayed = 100;
		float mControllCoef = 0.2f;
		int mForeBackDegre = 0;
		int mBackForeDegree = 0;
		boolean mForebackStart = false;
		float mRadiusCoef = 0.4f;
		Interpolator mFrameInterpolator = new DecelerateInterpolator();

		public void draw(Canvas canvas) {
			if (!isForeFinished()) {
				drawFore(canvas);
			} else if (!isBackFinished()) {
				drawBack(canvas);
			} else {
				if (!mIsReseting) {
					mIsReseting = true;
					postDelayed(this, mDelayed);
				}
			}
		}

		private void drawFore(Canvas canvas) {
			int degree = mRoundPositions.get(mDropIndex).degree;
			int foreDegree = degree + mForeDegree;
			drawFore(canvas, degree, foreDegree);
			drawForeBack(canvas);
			mForeDegree += mOffset;
			mBackForeDegree = mForeDegree;
		}

		private void drawFore(Canvas canvas, int backDegree, int foreDegree) {
			double foreArc = convertDegreeeToArc(foreDegree);
			int dstX = (int) (mCenterX + Math.cos(foreArc) * mOuterRadius);
			int dstY = (int) (mCenterY - Math.sin(foreArc) * mOuterRadius);
			double backArc = convertDegreeeToArc(backDegree);
			Double sinDegree = Math.sin(backArc);
			Double cosDegree = Math.cos(backArc);
			int topSrcX = (int) (mCenterX + cosDegree * (mOuterRadius + mInnerRadius));
			int topSrcY = (int) (mCenterY - sinDegree * (mOuterRadius + mInnerRadius));
			int bottomSrcX = (int) (mCenterX + cosDegree * (mOuterRadius - mInnerRadius));
			int bottomSrcY = (int) (mCenterY - sinDegree * (mOuterRadius - mInnerRadius));
			// float coef = mControllCoef + DEFAULT_COEF * (foreDegree -
			// backDegree) / mIntevel;

			float coef = mControllCoef * ((float) (foreDegree - backDegree) / mIntevel);// +
																						// DEFAULT_COEF
																						// *
																						// ((float)
																						// (foreDegree
																						// -
																						// backDegree)
																						// /
																						// mIntevel);
			double controllArc = convertDegreeeToArc((int) (foreDegree - (foreDegree - backDegree) * (coef)));
			Double sinControllDegree = Math.sin(controllArc);
			Double cosControllDegree = Math.cos(controllArc);

			float radiusCoef = 1.0f - (float) (foreDegree - backDegree) / mIntevel * mRadiusCoef;
			float radius = mInnerRadius;// * radiusCoef;
			int topControllX = (int) (mCenterX + cosControllDegree * (mOuterRadius + radius));
			int topControllY = (int) (mCenterY - sinControllDegree * (mOuterRadius + radius));

			int bottomControllX = (int) (mCenterX + cosControllDegree * (mOuterRadius - radius));
			int bottomControllY = (int) (mCenterY - sinControllDegree * (mOuterRadius - radius));

			drawCurve(canvas, topSrcX, topSrcY, topControllX, topControllY, bottomSrcX, bottomSrcY, bottomControllX, bottomControllY, dstX, dstY);
		}

		private void drawBack(Canvas canvas) {
			int degree = mRoundPositions.get(mDropIndex).degree;
			int backDegree = degree + mBackDegree;
			int foreDegree = degree + mForeBackDegre;
			drawBack(canvas, backDegree, foreDegree, false);
			drawBackFore(canvas);
			mBackDegree += mOffset;
		}

		private void drawBack(Canvas canvas, int backDegree, int foreDegree, boolean isReverse) {
			double backArc = convertDegreeeToArc(backDegree);
			int dstX = (int) (mCenterX + Math.cos(backArc) * mOuterRadius);
			int dstY = (int) (mCenterY - Math.sin(backArc) * mOuterRadius);
			double foreArc = convertDegreeeToArc(foreDegree);
			Double sinDegree = Math.sin(foreArc);
			Double cosDegree = Math.cos(foreArc);
			int topSrcX = (int) (mCenterX + cosDegree * (mOuterRadius + mInnerRadius));
			int topSrcY = (int) (mCenterY - sinDegree * (mOuterRadius + mInnerRadius));
			int bottomSrcX = (int) (mCenterX + cosDegree * (mOuterRadius - mInnerRadius));
			int bottomSrcY = (int) (mCenterY - sinDegree * (mOuterRadius - mInnerRadius));
			double coef = 1.0f;//mControllCoef * ((float) (foreDegree - backDegree) / mIntevel);// 1.0f;
			if (isReverse) {
				 coef = mControllCoef * (1.0f - (float) (foreDegree - backDegree) / mIntevel);// 1.0f;
			}else{
				coef = mControllCoef * ((float) (foreDegree - backDegree) / mIntevel);// 1.0f;
			}
			// if (isReverse) {
			// coef = (mControllCoef + DEFAULT_COEF) * Math.pow((double)
			// (foreDegree - backDegree) / mIntevel, 1.4f);
			// } else {
			// coef = mControllCoef + DEFAULT_COEF * (1.0f - (float) (foreDegree
			// - backDegree) / mIntevel);
			// coef = Math.pow(coef, 2.3);
			// }
			double controllArc = convertDegreeeToArc((int) (backDegree + (foreDegree - backDegree) * (coef)));
			Double sinControllDegree = Math.sin(controllArc);
			Double cosControllDegree = Math.cos(controllArc);

			float radiusCoef = 1.0f - (float) (foreDegree - backDegree) / mIntevel * mRadiusCoef;
			float radius = mInnerRadius;// * radiusCoef;
			int topControllX = (int) (mCenterX + cosControllDegree * (mOuterRadius + radius));
			int topControllY = (int) (mCenterY - sinControllDegree * (mOuterRadius + radius));

			int bottomControllX = (int) (mCenterX + cosControllDegree * (mOuterRadius - radius));
			int bottomControllY = (int) (mCenterY - sinControllDegree * (mOuterRadius - radius));

			drawCurve(canvas, topSrcX, topSrcY, topControllX, topControllY, bottomSrcX, bottomSrcY, bottomControllX, bottomControllY, dstX, dstY);

		}

		double convertDegreeeToArc(int degree) {
			return degree * 2 * Math.PI / MAX_DEGREE;
		}

		private void drawBackFore(Canvas canvas) {
			if (mBackForeDegree < 0) {
				int degree = mRoundPositions.get(mDropIndex).degree;
				int foreDegree = degree + mBackForeDegree;
				drawFore(canvas, degree, foreDegree);

				mBackForeDegree -= mOffset * 2;
			}
		}

		private void drawForeBack(Canvas canvas) {
			if (mForeDegree < (mIntevel + FORE_BACK_START_DEGREE) && !mForebackStart) {
				mForebackStart = true;
			}

			if (mForebackStart) {
				int degree = mRoundPositions.get(mDropIndex).degree;
				int finalForeDegree = degree + mIntevel;
				int foreBackDegree = degree + mIntevel - mForeBackDegre;

				drawBack(canvas, foreBackDegree, finalForeDegree, true);
				if (mForeBackDegre + mOffset * 2 >= mIntevel) {
					mForeBackDegre += mOffset * 2;
				} else {
					mForeBackDegre = mIntevel;
				}
			}
		}

		private void drawCurve(Canvas canvas, int topSrcX, int topSrcY, int topControllX, int topControllY, int bottomSrcX, int bottomSrcY, int bottomControllX, int bottomControllY, int dstX, int dstY) {
			mPath.reset();
			mPath.moveTo(topSrcX, topSrcY);
			mPath.quadTo(topControllX, topControllY, dstX, dstY);
			mPath.moveTo(bottomSrcX, bottomSrcY);
			mPath.quadTo(bottomControllX, bottomControllY, dstX, dstY);
			mPath.moveTo(topSrcX, topSrcY);
			mPath.lineTo(dstX, dstY);
			mPath.lineTo(bottomSrcX, bottomSrcY);
			mPath.close();

			canvas.drawPath(mPath, mPaint);
		}

		private boolean isFinished() {
			return isForeFinished() && isBackFinished();
		}

		private boolean isForeFinished() {
			return Math.abs(mForeDegree) >= Math.abs(mIntevel);
		}

		private boolean isBackFinished() {
			return Math.abs(mBackDegree) >= Math.abs(mIntevel);
		}

		public boolean isForeOverlap() {
			return (Math.abs(mForeDegree) >= Math.abs(mIntevel + mFixedOverlapDegree)) || mForebackStart;
		}

		public boolean isBackOverlap() {
			return Math.abs(mBackDegree) <= Math.abs(mFixedOverlapDegree) || mBackForeDegree < 0;
		}

		public void reset() {
			mForeDegree = 0;
			mBackDegree = 0;
			mIsReseting = false;
			mForebackStart = false;
			mForeBackDegre = 0;
			mDropIndex++;
			if (mDropIndex >= mPointNum) {
				mDropIndex = 0;
			}

			mAlphaOffset++;
			if (mAlphaOffset >= mPointNum) {
				mAlphaOffset = 0;
			}
		}

		@Override
		public void run() {
			reset();
		}
	}
}
