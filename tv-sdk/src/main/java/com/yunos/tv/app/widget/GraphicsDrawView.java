package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;

public class GraphicsDrawView extends View {

	public GraphicsDrawView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public GraphicsDrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GraphicsDrawView(Context context) {
		super(context);
	}

	int DEFAULT_ANIMATE_DURATION = 10000;

	ArcPaint mArcPaint = new ArcPaint();

	public ArcPaint getArcPaint() {
		return mArcPaint;
	}


	@Override
	protected void onDraw(Canvas canvas) {

		mArcPaint.draw(canvas);
	}

	abstract class BasePaint extends Paint {
		Interpolator mInterpolator = new AccelerateDecelerateFrameInterpolator();
		Scroller mScroller = new Scroller(getContext(), mInterpolator);

		public void setInterpolator(Interpolator i) {
			mInterpolator = i;
			resetScroller();
		}

		private void resetScroller() {
			mScroller = new Scroller(getContext(), mInterpolator);
		}

		public abstract void draw(Canvas canvas);

		public boolean isFinished() {
			return mScroller.isFinished();
		}

		public boolean shouldDraw() {
			if (isLayoutRequested()) {
				return false;
			}
			if (mScroller == null) {
				return false;
			}

			if (mScroller.isFinished()) {
				return false;
			}

			return true;
		}

		public void paintInvalidate() {
			if (!isFinished()) {
				invalidate();
			}
		}
	}

	public class ArcPaint extends BasePaint {
		static private final int MAX_DEGREE = 360;
		static public final int LOCK_WISE = 1;
		static public final int ANTI_LOCK_WISE = 2;

		int CLIP_MAX_DEGREE = MAX_DEGREE;
		int CLIP_MIN_DEGREE = 0;
		int mDegree = 0;
		int mDirection = LOCK_WISE;
		
		@Override
		public void draw(Canvas canvas) {
			if (!shouldDraw()) {
				return;
			}
			drawArc(canvas);
		}

		public void drawArc(Canvas canvas) {
			int width = getWidth();
			int height = getHeight();
			mScroller.computeScrollOffset();
			mDegree = mScroller.getCurrX();

			Paint paint = new Paint();
			paint.setStrokeWidth(20);
			paint.setStyle(Style.STROKE);
			paint.setColor(Color.BLUE);

			canvas.drawArc(new RectF(0, 0, width, height), CLIP_MIN_DEGREE, mDegree - CLIP_MIN_DEGREE , false, paint);
			paintInvalidate();
		}

		public void show() {
			show(CLIP_MIN_DEGREE, CLIP_MAX_DEGREE, DEFAULT_ANIMATE_DURATION);
		}

		public void show(int startDegree, int endDegree, int duration) {
			if (endDegree - startDegree > MAX_DEGREE) {
				endDegree = startDegree + MAX_DEGREE;
			}
			CLIP_MIN_DEGREE = startDegree;
			CLIP_MAX_DEGREE = endDegree;
			mScroller.startScroll(CLIP_MIN_DEGREE, 0, mDirection == LOCK_WISE ? CLIP_MAX_DEGREE - CLIP_MIN_DEGREE : CLIP_MIN_DEGREE - CLIP_MAX_DEGREE, 0, duration);
			mDegree = CLIP_MIN_DEGREE;
			paintInvalidate();
		}

		public void setDriection(int d) {
			if (d != LOCK_WISE && d != ANTI_LOCK_WISE) {
				throw new IllegalArgumentException("CircleClipView: setDriection direciton must be LOCK_WISE or ANTI_LOCK_WISE");
			}

			mDirection = d;
		}

	}

}
