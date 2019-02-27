package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.yunos.tv.aliTvSdk.R;

import java.util.ArrayList;
import java.util.List;

public class SurfaceLiquidProgressBar extends SurfaceView implements Callback {

	static final String TAG = "SurfaceLiquidProgressBar";

	private AlphaAnimation mAnimation;
	private boolean mHasAnimation;
	private Transformation mTransformation;
	boolean mbHasLiquid = true;
	SurfaceHolder mSfh;
	DrawThread mDrawThread = null;

	public SurfaceLiquidProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SurfaceLiquidProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SurfaceLiquidProgressBar(Context context) {
		super(context);
		init();
	}

	private void init() {
		mSfh = getHolder();
		getHolder().setFormat(PixelFormat.TRANSPARENT);
		setZOrderOnTop(true);
	}

	@Override
	public void setVisibility(int visibility) {
		int v = getVisibility();
		super.setVisibility(visibility);
		if (v == View.VISIBLE && (visibility == View.GONE || visibility == View.INVISIBLE)) {
			stop();
		}

		if (visibility == View.VISIBLE && (v == View.GONE || v == View.INVISIBLE)) {
			start();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		stop();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		start();
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		float diameter = (mStdDotRadius + mStdRotateRadius) * 2;
		int min = Math.min(getMeasuredHeight(), getMeasuredWidth());
		mRotateRadius = (int) (mStdRotateRadius * min / diameter);
		mDotRadius = min / 2 - mRotateRadius;

		if (mLiquidProgress == null || mSphereProgress == null) {
			mLiquidProgress = new LiquidProgressEffect(mRotateRadius, mDotRadius);
			mLiquidProgress.setColor(mColor);
			mSphereProgress = new SphereProgressEffect(mRotateRadius, mDotRadius);
			mSphereProgress.setColor(mColor);
			if (mPathOutPutInfoArray == null) {
				loadPathOutPutInfo(getContext());
			}

			setNewPathOutPutInfo();
		}
	}

	private void loadPathOutPutInfo(Context context) {
		int stdWidth = 1920 * 9 / 20;
		Object obj = null;

		Input input = new Input();

		int typeDpi = FQ_DPI_2160;
		String strfilename = "";
		int id = R.raw.progress_curve_file_h_l;
		if (typeDpi == H_DPI_1080) {
			strfilename = "progress_curve_file_h_l";
			stdWidth = 1920 * 9 / 20;
			id = R.raw.progress_curve_file_h_l;
		} else if (typeDpi == M_DPI_720) {
			strfilename = "progress_curve_file_m_l";
			id = R.raw.progress_curve_file_m_l;
			stdWidth = 1920 * 3 / 10;
		} else if (typeDpi == Q_DPI_1440) {
			strfilename = "progress_curve_file_q_l";
			id = R.raw.progress_curve_file_q_l;
			stdWidth = 1920 * 3 / 5;
		} else if (typeDpi == FQ_DPI_2160) {
			strfilename = "progress_curve_file_fq_l";
			id = R.raw.progress_curve_file_fq_l;
			stdWidth = 1920 * 9 / 10;
		}

		boolean flag = input.inPutArrayFromRaw(context, id);
		if (flag) {
			Log.v(TAG, "Read From  file = " + strfilename + " succeed");

			obj = input.getOutputInfo();
		}

		if (obj != null) {
			mPathWidth = stdWidth;

			mPathOutPutInfoArray = (ArrayList<PathOutPutInfo>) obj;
		}

		mbIsLoad = true;
	}

	public static final int M_DPI_720 = 0;
	public static final int H_DPI_1080 = 1;
	public static final int Q_DPI_1440 = 2;
	public static final int FQ_DPI_2160 = 3;

	public void setHasLiquidEffect(boolean hasLiquid) {
		mbHasLiquid = hasLiquid;
	}

	/**
	 * <p>
	 * Start the indeterminate progress animation.
	 * </p>
	 */
	private void startAnimation() {
		if (getVisibility() != VISIBLE) {
			return;
		}

		mHasAnimation = true;

		if (mTransformation == null) {
			mTransformation = new Transformation();
		} else {
			mTransformation.clear();
		}

		if (mAnimation == null) {
			mAnimation = new AlphaAnimation(0.0f, 1.0f);
		} else {
			mAnimation.reset();
		}

		mAnimation.setRepeatCount(Animation.INFINITE);
		mAnimation.setDuration(4000);
		mAnimation.setStartTime(Animation.START_ON_FIRST_FRAME);
	}

	private float mProgress = 0;
	private float mProgressSpeed = 0.01f;

	protected synchronized void drawLiquid(Canvas canvas) {

		mProgress += mProgressSpeed;
		updateProgress(mProgress);

		if (mHasAnimation) {
			canvas.save();
			long time = getDrawingTime();

			mAnimation.getTransformation(time, mTransformation);
			float scale = mTransformation.getAlpha();

			int centerX = getWidth() / 2;
			int centerY = getHeight() / 2;

			if (mbHasLiquid) {
				if (mPathOutPutInfoArray != null) {
					mLiquidProgress.drawCircle(canvas, centerX, centerY);
				} else {
					mSphereProgress.drawCircle(canvas, centerX, centerY);
				}
			} else {
				mSphereProgress.drawCircle(canvas, centerX, centerY);
			}

			// postInvalidateOnAnimation();

			canvas.restore();
		}
	}

	// 测试是否采用circle近视的处理
	private static final boolean mUseCircle = true;

	private static final float mStdRotateRadius = 180;
	private static final float mStdDotRadius = 40;

	// 1080 resource its width and this width its radius
	private static final float mStdPathWidth = 864;// 576;
	private static final float mStdPathRotateRadius = 81;// 54;

	LiquidProgressEffect mLiquidProgress;
	SphereProgressEffect mSphereProgress;

	static private int mPathWidth;

	private int mRotateRadius;
	private int mDotRadius;

	private int mColor = 0x00ccff;

	ArrayList<HighPrecisionPathInfo> mPathArray = new ArrayList<HighPrecisionPathInfo>();

	static private ArrayList<PathOutPutInfo> mPathOutPutInfoArray;
	static private boolean mbIsLoad = false;

	// this data is see the resource to dicide which shape could use circle
	private boolean mHeadCurveIsCircle[] = { true, true, true, false, false, false, false, false, false, false, false, false, false, false, false,// all
																																					// 255
																																					// so
																																					// it
																																					// is
																																					// not
																																					// use
																																					// circle
			false, false, false, false, false,// all 255 link together
			false, false, false, true, true, true, true, true, true, true };

	private boolean mTailCurveIsCircle[] = { true, true, true, true, true, true, true, true, false, false, false, false, false, false, false,// all
																																				// 255
																																				// so
																																				// it
																																				// is
																																				// not
																																				// use
																																				// circle
			false, false, false, false, false,// all 255 link together
			false, false, false, false, false, false, false, false, true, true };

	public void updateProgress(float progress) {
		mLiquidProgress.updateRotateProgress(progress);
		mSphereProgress.updateRotateProgress(progress);
	}

	private void setNewPathOutPutInfo() {
		if (mPathOutPutInfoArray != null) {
			mPathArray.clear();
			HighPrecisionPathInfo tempPathInfo;

			for (int i = 0; i < mPathOutPutInfoArray.size(); i++) {
				tempPathInfo = new HighPrecisionPathInfo();
				tempPathInfo.mArrHead = pointSerArrayToPointArray(mPathOutPutInfoArray.get(i).mArrHead);
				tempPathInfo.mArrTail = pointSerArrayToPointArray(mPathOutPutInfoArray.get(i).mArrTail);

				if (tempPathInfo.mArrHead != null) {
					Path path = pointArrToPath(tempPathInfo.mArrHead);
					tempPathInfo.mPathHead = path;

				}

				if (tempPathInfo.mArrTail != null) {
					Path path = pointArrToPath(tempPathInfo.mArrTail);
					tempPathInfo.mPathTail = path;
				}

				if (i < mHeadCurveIsCircle.length && mUseCircle) {
					tempPathInfo.mHeadPathIsCircle = mHeadCurveIsCircle[i];
					tempPathInfo.mTailPathIsCircle = mTailCurveIsCircle[i];
				}

				mPathArray.add(tempPathInfo);
			}

			mLiquidProgress.setPathArray(mPathArray);
		}
	}

	private Path pointArrToPath(List<PointF> array) {
		PointF pt;
		PointF pt1;
		PointF pt2;

		if (array != null && array.size() >= 1) {
			Path path = new Path();

			pt = array.get(0);
			path.moveTo(pt.x, pt.y);

			int lineType = 1;

			if (lineType == 0) {
				for (int i = 0; i < array.size(); i++) {

					pt = array.get(i);

					path.lineTo(pt.x, pt.y);
				}
			} else if (lineType == 1) {

				int iCount = array.size() / 3;
				for (int i = 0; i < iCount; i++) {

					pt = array.get(i * 3 + 0);
					pt1 = array.get(i * 3 + 1);
					pt2 = array.get(i * 3 + 2);

					path.cubicTo(pt.x, pt.y, pt1.x, pt1.y, pt2.x, pt2.y);
				}

				int d = array.size() % 3;
				if (d == 1) {
					pt = array.get(array.size() - 1);

					path.lineTo(pt.x, pt.y);
				} else if (d == 2) {
					pt = array.get(array.size() - 2);

					path.lineTo(pt.x, pt.y);

					pt = array.get(array.size() - 1);

					path.lineTo(pt.x, pt.y);
				}

			} else if (lineType == 2) {
				int iCount = array.size() - 1;
				for (int i = 0; i < iCount; i++) {
					pt = array.get(i + 0);
					pt1 = array.get(i + 1);

					path.quadTo(pt.x, pt.y, pt1.x, pt1.y);
				}

				pt = array.get(array.size() - 1);

				path.lineTo(pt.x, pt.y);
			} else if (lineType == 3) {
				int iCount = (array.size() - 1) / 2;
				for (int i = 0; i < iCount; i++) {
					pt = array.get(2 * i + 0);
					pt1 = array.get(2 * i + 1);
					pt2 = array.get(2 * i + 2);

					path.cubicTo(pt.x, pt.y, pt1.x, pt1.y, pt2.x, pt2.y);
				}

			} else {
				for (int i = 0; i < array.size(); i++) {
					if (i % 7 == 0)

					{
						pt = array.get(i);

						path.lineTo(pt.x, pt.y);
					}
				}
			}

			path.close();

			return path;
		}

		return null;
	}

	ArrayList<PointF> pointSerArrayToPointArray(ArrayList<Point> ptArray) {

		float pathScale = 1.0f * (mStdPathWidth) / (mPathWidth) * mRotateRadius / mStdPathRotateRadius;

		if (ptArray == null) {
			return null;
		}
		ArrayList<PointF> pointArr = new ArrayList<PointF>();
		Point ptSer;
		for (int i = 0; i < ptArray.size(); i++) {
			ptSer = ptArray.get(i);
			pointArr.add(new PointF((ptSer.x * pathScale), (ptSer.y * pathScale)));
		}

		return pointArr;
	}

	private void stop() {
		if (mDrawThread != null) {
			mDrawThread.forcedStop();
		}

		mDrawThread = null;
	}

	private void start() {
		startAnimation();
		if (mDrawThread == null) {
			mDrawThread = new DrawThread();
			mDrawThread.start();
		}
	}

	class DrawThread extends Thread {
		private boolean mIsStop = false;

		public void forcedStop() {
			synchronized (this) {
				mIsStop = true;
			}
		}

		private boolean isStop() {
			synchronized (this) {
				return mIsStop;
			}
		}

		public void run() {
			while (!isStop()) {
				Canvas canvas = null;
				try {
					canvas = mSfh.lockCanvas();
				} catch (Exception e) {
					Log.e(TAG, "DrawThread run lock canvas failed, msg = " + e.getMessage());
					e.printStackTrace();
					return;
				}
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
				canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
				drawLiquid(canvas);

				canvas.restore();
				mSfh.unlockCanvasAndPost(canvas);
			}

			if (isStop()) {
				Canvas canvas = mSfh.lockCanvas();
				if (canvas == null) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					return;
				}
				canvas.save();
				canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
				canvas.restore();
				mSfh.unlockCanvasAndPost(canvas);
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		holder.setFormat(PixelFormat.TRANSPARENT);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		holder.setFormat(PixelFormat.TRANSPARENT);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		holder.setFormat(PixelFormat.TRANSPARENT);
	}
}
