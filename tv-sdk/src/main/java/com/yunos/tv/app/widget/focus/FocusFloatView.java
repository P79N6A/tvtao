package com.yunos.tv.app.widget.focus;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.app.widget.round.TransitionRoundedImageView;
import com.yunos.tv.app.widget.utils.ImageUtils;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FocusFloatView extends FrameLayout implements FocusListener, ItemListener {
	protected final String TAG = "FloatView";
	protected final boolean DEBUG = false;

	protected Params mParams = new Params(1.1f, 1.1f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());
	public static int BOTTOM_CENTER = 134;
	public static int BOTTOM_RIGHT = 135;
	public static int BOTTOM_LEFT = 136;
	protected int mScaleType = 134;

	protected ScaleImageView mFloatImageView;
	protected TransitionRoundedImageView mBackImageView;

	private int mCurrentFrame = 6;
	private int mFrameRate = 6;
	private int mLastFrame = 6;
	protected int mTopspace = 0;
	protected int mBottomspace = 0;
	// private int mMovespace = 0;
	private float mScale = 0.0f;

	private boolean isStop = false;
	private boolean isFocusDraw = false;
	private boolean isScale = false;
	private static boolean isFinish = false;
	private boolean isEnd = false;

	protected LayoutParams mLayoutParams;

	public FocusFloatView(Context context) {
		super(context);
		init(context);
	}

	public FocusFloatView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		init(context);
	}

	public FocusFloatView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		Log.d(TAG, "init");
		mBackImageView = new TransitionRoundedImageView(context);
		// mBackImageView.setScaleType(ImageView.ScaleType.FIT_XY);
		mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		mFloatImageView = new ScaleImageView(context);
	}

	public void setFloatFrameLayoutParams(LayoutParams layoutParams, final int topSpace, final int bottomSpace, final int paddingTop) {// 添加子view布局
		Log.d(TAG, "setFloatFrameLayoutParams");
		if (mBackImageView != null) {
			this.mTopspace = topSpace;
			this.mBottomspace = bottomSpace;
			mLayoutParams.topMargin = topSpace;
			mLayoutParams.bottomMargin = bottomSpace;
			this.addViewInLayout(mBackImageView, 0, mLayoutParams);
		}

		if (mFloatImageView != null) {
			float scale = (float) (layoutParams.height - paddingTop) / layoutParams.height;
			this.mScale = 2.0f - scale;
			mFloatImageView.setPadding(0, paddingTop, 0, 0);
			mFloatImageView.setWidth(layoutParams.width, layoutParams.height);
			if (isScale) {
				layoutParams.width = (int) (layoutParams.width * getParams().getScaleParams().getScaleX());
				layoutParams.height = (int) (layoutParams.height * getParams().getScaleParams().getScaleX());
			}
			this.addViewInLayout(mFloatImageView, 1, layoutParams);
			Log.i(TAG, "setFloatFrameLayoutParams mScale = " + mScale);
		}
	}

	public void setFrame(int frame) {// 设置移动参数
		Log.d(TAG, "setFrame frame = " + frame);
		this.mFrameRate = frame;
		this.mCurrentFrame = frame;
		this.mLastFrame = frame;
	}

	// public void setMoveParams(int moveSpace) {// 设置移动参数
	// Log.d(TAG, "====setMoveParams======");
	// this.mMovespace = moveSpace;
	// }

	public void setFloatScaleType(int scaleType, float scale) {// 设置移动参数
		Log.d(TAG, "setFloatParams scaleType = " + scaleType + ", scale = " + scale);
		this.mScale = scale;
		this.mScaleType = scaleType;
	}

	public void setFloatScaleType(int scaleType, boolean isBackViewScale) {// 设置移动参数
		Log.d(TAG, "setFloatParams scaleType = " + scaleType + ", isBackViewScale = " + isBackViewScale);
		this.mScaleType = scaleType;
		this.isScale = isBackViewScale;
	}

	public void setFloatScaleType(int scaleType, float scale, boolean isBackViewScale) {// 设置移动参数
		Log.d(TAG, "setFloatParams scaleType = " + scaleType);
		this.mScaleType = scaleType;
		this.mScale = scale;
		this.isScale = isBackViewScale;
	}

	public void setBackgroundBitmap(Bitmap backBitmap, Bitmap beforeBitmap) {// 设置图片
		Log.d(TAG, "setBackgroundBitmap");
		if (mBackImageView != null && backBitmap != null) {
			mBackImageView.setImageBitmap(ImageUtils.getScaleBitmap(backBitmap, getLayoutParams().width, (getLayoutParams().height - mTopspace)));
		}
		if (mFloatImageView != null && beforeBitmap != null) {
			mFloatImageView.setImageBitmap(beforeBitmap);
		}
	}

	public TransitionRoundedImageView getBackRoundedImageView() {
		return mBackImageView;
	}

	public ImageView getFloatImageView() {
		return mFloatImageView;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (DEBUG) {
			Log.d(TAG, "dispatchDraw isFocusDraw = " + isFocusDraw);
		}
		if (isFocusDraw) {
			if (DEBUG) {
				Log.d(TAG, "dispatchDraw isEnd = " + isEnd);
			}

			if (mBackImageView != null) {
				drawChild(canvas, mBackImageView, getDrawingTime());
			}
		} else {
			super.dispatchDraw(canvas);
		}
	}

	public void startToLeft(boolean isLeft) {
		// if (isLeft) {
		// int right = Math.round((float) ((mFrameRate - mCurrentFrame) *
		// mMovespace) / mFrameRate);
		// mFloatImageView.scrollBy(right, 0);
		// Log.d(TAG, right + "=====left====" + mCurrentFrame);
		// } else {
		// int left = Math.round((float) ((mCurrentFrame * mMovespace)) /
		// mFrameRate);
		// Log.d(TAG, left + "=====right====" + mCurrentFrame);
		// mFloatImageView.scrollBy(-left, 0);
		// }
	}

	public void startToScale(boolean isScale) {
		// Log.d(TAG, "=====startToScale====");
		float scale = 1.0f;
		if (isScale) {
			scale = 1.0f + (float) (((mFrameRate - mCurrentFrame) * (mScale - 1.0)) / mFrameRate);
			if (DEBUG) {
				Log.d(TAG, "startToScale scale " + scale + ", mCurrentFrame = " + mCurrentFrame);
			}
		} else {
			scale = mScale - (float) ((mCurrentFrame * (mScale - 1.0)) / mFrameRate);
			if (DEBUG) {
				Log.d(TAG, "startToScale scale " + scale + ", mCurrentFrame = " + mCurrentFrame);
			}
		}

		mFloatImageView.setScale(scale, true, getScaleY());
		mFloatImageView.invalidate();
	}

	public void startAnim() {
		Log.d(TAG, "startAnim");
		if (!isFocusDraw && isEnd) {
			resetInvalidate();
		}
		isEnd = false;
		isStop = false;
		isFinish = false;
		mCurrentFrame = mLastFrame;
	}

	public void stopAnim() {
		Log.d(TAG, "stopAnim");
		isEnd = false;
		isStop = true;
		isFinish = false;
		mCurrentFrame = mLastFrame;
	}

	public void stop() {
		Log.d(TAG, "stop");
		isEnd = true;
		isStop = true;
		isFinish = true;
		isFocusDraw = false;
		mCurrentFrame = mLastFrame = mFrameRate;
		invalidate();
		mFloatImageView.setScale(1.0f, false, 1.0f);
		mFloatImageView.invalidate();

	}

	@Override
	public int getItemWidth() {
		return getWidth();
	}

	@Override
	public int getItemHeight() {
		return getHeight() - this.getPaddingTop();
	}

	private void resetInvalidate() {
		isFocusDraw = true;
		invalidate();
	}

	protected class ScaleImageView extends ImageView {
		private Drawable mDrawable;
		private boolean isDraw = false;
		private float scale = 1.0f;
		private float scaleY = 1.0f;
		private int mWidth = 0;
		private int mHeight = 0;
		private Rect rect = new Rect();

		public ScaleImageView(Context context) {
			super(context);
		}

		public ScaleImageView(Context context, AttributeSet attrs) {
			this(context, attrs, 0);
		}

		public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			int paddLeft = (int) (mWidth * mScale - mWidth);
			if (mScaleType == BOTTOM_RIGHT) {
				rect.set(paddLeft, getPaddingTop(), mWidth, mHeight - mBottomspace);
			} else if (mScaleType == BOTTOM_LEFT) {
				int scaleLeft = (int) ((scaleY - 1.0) * (mWidth - getPaddingTop()));
				int paddTop = (int) ((scaleY - 1.0) * (mBackImageView.getHeight() - mBottomspace));
				rect.set(0, getPaddingTop(), (mWidth - paddLeft + scaleLeft), (mHeight - mBottomspace + paddTop));
			} else {
				paddLeft = getPaddingTop() / 2;
				rect.set(paddLeft, getPaddingTop(), (mWidth - paddLeft), mHeight - mBottomspace);
				// rect.set((getPaddingLeft() + mMovespace),
				// getPaddingTop(),(getWidth() - getPaddingLeft() - mMovespace),
				// getHeight());
			}
			if (!isDraw) {
				// Log.d(TAG, "=====child==onDraw===false==="+rect);
				// super.onDraw(canvas);
				mDrawable = getDrawable();
				if (mDrawable != null) {
					// Log.d(TAG, "=====child==onDraw===mDrawable != null===");
					mDrawable.setBounds(rect);
					mDrawable.draw(canvas);
				}
			} else {
				// Log.d(TAG, (rect.right - rect.left) +
				// "=====child==onDraw===true===" + scale);
				if (mDrawable != null) {
					if (mScaleType == BOTTOM_RIGHT) {
						mDrawable.setBounds(getScaledRect(rect, scale, scale, 1.0f, 1.0f));
					} else if (mScaleType == BOTTOM_LEFT) {
						mDrawable.setBounds(getScaledRect(rect, scale, scale, 0.0f, 1.0f));
					} else {
						mDrawable.setBounds(getScaledRect(rect, scale, scale, 0.5f, 1.0f));
					}
					mDrawable.draw(canvas);
				}
			}
		}

		public Rect getScaledRect(Rect r, float scaleX, float scaleY, float coefX, float coefY) {
			int width = r.width();
			int height = r.height();

			float diffScaleX = scaleX - 1.0f;
			float diffScaleY = scaleY - 1.0f;
			r.left -= (width * coefX * diffScaleX + 0.5f);
			r.right += (width * (1.0f - coefX) * diffScaleX + 0.5f);
			r.top -= (height * coefY * diffScaleY + 0.5f);
			r.bottom += (height * (1.0f - coefY) * diffScaleY + 0.5f);

			return r;
		}

		public void setScale(float rectScale, boolean isdraw, float parentScaleY) {
			this.scale = rectScale;
			this.isDraw = isdraw;
			this.scaleY = parentScaleY;
		}

		public void setWidth(int width, int height) {
			this.mWidth = width;
			this.mHeight = height;
		}

		public void update() {
			mDrawable = getDrawable();
		}

	}

	public void updateFloatView() {
		mFloatImageView.update();
	}

	@Override
	public void drawAfterFocus(Canvas canvas) {
		if (!isEnd) {
			resetCanvas(canvas);
			drawCanvas(canvas);
		}
	}

	@Override
	public void drawBeforeFocus(Canvas canvas) {
		// drawCanvas(canvas);
	}

    int[] offscreen = new int[2];
	private void resetCanvas(Canvas canvas) {
		canvas.restore();
		canvas.save();
		//Rect rect = new Rect();
		//mBackImageView.getGlobalVisibleRect(rect);
        if(offscreen == null){
            offscreen = new int[2];
        } else {
            offscreen[0] = 0;
            offscreen[1] = 0;
        }

        getLocationOnScreen(offscreen);
		canvas.translate(offscreen[0], offscreen[1]);
		canvas.restore();
	}

	protected void drawCanvas(Canvas canvas) {
		if (mFloatImageView != null && mFloatImageView.mDrawable != null) {
			if (!isFocusDraw) {
				resetInvalidate();
			}
			isFinish = false;
			mLastFrame = mCurrentFrame;
			if (isStop) {
				// if (mMovespace > 0) {// 右移动
				// startToLeft(false);
				// }

				if (mScale >= 1) {// 缩小
					startToScale(false);
				}
				if (mCurrentFrame < mFrameRate && mCurrentFrame >= 0) {
					drawChild(canvas, mFloatImageView, getDrawingTime());
					mCurrentFrame++;
				} else {
					if (DEBUG) {
						Log.d(TAG, "drawCanvas postFocusDraw mCurrentFrame =" + mCurrentFrame);
					}
					if (isFocusDraw) {
						drawChild(canvas, mFloatImageView, getDrawingTime());
					}
					isFinish = true;
					isFocusDraw = false;
					if (mCurrentFrame > mFrameRate || mCurrentFrame < 0) {
						if (DEBUG) {
							Log.d(TAG, "drawCanvas postFocusDraw mCurrentFrame =" + mCurrentFrame);
						}
						mCurrentFrame = mFrameRate;
					}
					invalidate();
				}
			} else {
				// if (mMovespace > 0) {// 左移动
				// startToLeft(true);
				// }
				if (mScale >= 1) {// 放大
					startToScale(true);
				}

				drawChild(canvas, mFloatImageView, getDrawingTime());
				mCurrentFrame--;

				if (mCurrentFrame <= 0) {
					mCurrentFrame = 0;
					isFinish = true;
				}
			}
		} else {
			isFinish = true;
		}
	}

	@Override
	public FocusRectParams getFocusParams() {
        Rect r = new Rect();
        if (mBackImageView != null){
            mBackImageView.getFocusedRect(r);
            offsetDescendantRectToMyCoords(mBackImageView, r);
        } else {
            getFocusedRect(r);
        }
        return new FocusRectParams(r, 0.5f, 0.5f);

		// Rect r = new Rect();
		// mBackImageView.getFocusedRect(r);
		// ViewGroup root = (ViewGroup) getParent();
		// root.offsetDescendantRectToMyCoords(mBackImageView, r);
		// return new FocusRectParams(r, 0.5f, 0.5f);
	}

	@Override
	public Rect getManualPadding() {
		return null;
	}

	@Override
	public boolean isScale() {
		return isScale;
	}

	@Override
	public boolean canDraw() {
		return true;
	}

	@Override
	public ItemListener getItem() {
		return this;
	}

	@Override
	public Params getParams() {
		return mParams;
	}

	@Override
	public boolean isAnimate() {
		return true;
	}

	@Override
	public boolean isFocusBackground() {
		return false;
	}

	@Override
	public boolean isScrolling() {
		return false;
	}

	@Override
	public void onFocusFinished() {
	}

	@Override
	public void onFocusStart() {
	}

	@Override
	public boolean preOnKeyDown(int keyCode, KeyEvent arg1) {
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
	public boolean isFinished() {
		return isFinish;
	}

	@Override
	public Rect getClipFocusRect() {
		// TODO Auto-generated method stub
		return null;
	}

}