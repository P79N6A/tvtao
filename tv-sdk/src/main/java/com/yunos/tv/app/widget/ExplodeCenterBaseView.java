package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.BitmapShader;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;

public abstract class ExplodeCenterBaseView extends View {
	private final static String TAG = "ExplodeCenterBaseView";
	protected static final boolean DEBUG = false;
	
	public final static int STATIC = 0; // 正常模式
	public final static int MID_MOTION = 1; // 只有中间层动模式
	public final static int ENTER_MOTION = 2; // 入场动画模式,入场动画模式不允许暂停。

	private Drawable backDrawable;
	protected BitmapDrawable middleDrawable;
	private BitmapDrawable foreDrawable;

	/**
	 * 是否在初始化的动画阶段
	 */
	//是否显示中间层
	private boolean isEnableMid = true;
	
	private boolean isRun;
	protected Paint paint;
	protected Camera camera;
	protected Scroller mForgroundScroller;
	protected MidScroller mMiddleScroller;

	protected int width;
	protected int height;
	// 该View的目标绘制区域
	protected Rect dstRect = new Rect();
	// 前景和中间层区域
	protected Rect realRect = new Rect();
	protected RectF realRectF = new RectF();
	protected Matrix comMatrix;

	// 中间层和前景相对背景的padding
	protected int imagePaddingLeft = 45;
	protected int imagePaddingtop = 0;
	protected int imagePaddingRight = 45;
	protected int imagePaddingBottom = 90;

	// 圆角半径
	protected int roundRadius = 70;
	// 入场动画周期
	protected int mEnterBaseDuration = 1000;
	protected int mEnterMidDuration = 1400;
	// 滑动周期
	protected int mFlowDuration = 1000;
	// 当前状态
	protected int mCurrentMode = STATIC;
	// 当前进度
	public float mCurrentInterpolator = 0.0f;
	public float mLastInterpolator = 0.0f;
	// 当前移动方向，true为向右移动或者放大，false为反向，
	public boolean mDirection = true;

	// 前景文字绘制
	private String mEnText;
	private String mCnText;
	private float mEnTextSize;
	private float mCnTextSize;
	private int mEnTextColor;
	private int mCnTextColor;
	private float mEnTextBeginLeft;
	private float mEnTextBeginTop;
	private float mEnTextEndLeft;
	private float mCnTextBeginLeft;
	private float mCnTextBeginTop;
	private float mCnTextEndLeft;

	protected int mOffset = 0;
	protected int mFrom = 50;
	protected int mTo = 150;
	protected int mCoef = 1;
	protected int translateDistance = 100;// 移动距离
	
	protected Paint mBitmapPaint = new Paint();
	protected BitmapShader mBitmapShader;
	protected Matrix mShaderMatrix = new Matrix();
	
	public ExplodeCenterBaseView(Context context) {
		super(context);
		initView(context, null);
	}

	public ExplodeCenterBaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context, attrs);
	}

	public ExplodeCenterBaseView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context, attrs);
	}

	private void initView(Context context, AttributeSet attrs) {
		mForgroundScroller = new Scroller(getContext(), new AccelerateDecelerateFrameInterpolator());
		mMiddleScroller = new MidScroller(getContext(), new AccelerateDecelerateFrameInterpolator());
		camera = new Camera();
		paint = getPaint();
	}
	
	public void setEnableMid(boolean enable) {
		isEnableMid = enable;
	}

	public void setBitmap(Drawable backDrawable, BitmapDrawable middleDrawable, BitmapDrawable foreDrawable) {
		this.backDrawable = backDrawable;
		this.middleDrawable = middleDrawable;
		this.foreDrawable = foreDrawable;
		
		if(middleDrawable != null) {
			mBitmapShader = new BitmapShader(middleDrawable.getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			
			mBitmapPaint = new Paint();
			mBitmapPaint.setStyle(Paint.Style.FILL);
			mBitmapPaint.setAntiAlias(true);
			mBitmapPaint.setShader(mBitmapShader);
		}

	}

	public void setText(String enText, String cnText) {
		mEnText = enText;
		mCnText = cnText;
	}

	public void setTextSize(float enTextSize, float cnTextSize) {
		mEnTextSize = enTextSize;
		mCnTextSize = cnTextSize;
	}

	public void setTextColor(int enTextColor, int cnTextColor) {
		mEnTextColor = enTextColor;
		mCnTextColor = cnTextColor;
	}

	public void setEnTextOffset(float beginLeft, float endLeft, float top) {
		mEnTextBeginLeft = beginLeft;
		mEnTextBeginTop = top;
		mEnTextEndLeft = endLeft;
	}

	public void setCnTextOffset(float beginLeft, float endLeft, float top) {
		mCnTextBeginLeft = beginLeft;
		mCnTextBeginTop = top;
		mCnTextEndLeft = endLeft;
	}

	/**
	 * 默认设置为1.6S和2S
	 * 
	 * @param baseDuration
	 *            底层和最前面层的时间
	 * @param mindDuration
	 *            中间层的时间
	 */
	public void setEnterDuration(int baseDuration, int midDuration) {
		mEnterBaseDuration = baseDuration;
		mEnterMidDuration = midDuration;
	}

	/**
	 * 滑动时的动画周期
	 * 
	 * @param flowDuration
	 */
	public void setFlowDuration(int flowDuration) {
		mFlowDuration = flowDuration;
	}

	/**
	 * 设置中间层圆角半径
	 * 
	 * @param radius
	 */
	public void setRoundRadius(int radius) {
		roundRadius = radius;
	}

	/**
	 * 设置中间层移动参数
	 * 如果是Translate,则0~500这种设置
	 * 如果是Scale，怎按放大比率*100，如 0.5*100~1.5*100
	 * @param from
	 * @param to
	 */
	public void setTranslationDistance(int from, int to) {
		translateDistance = to - from;
		
		mFrom = from;
		mTo = to;
		
//		resetState();
	}
	
	/**
	 * 重新设置时，还原到初始位置，由于目前桌面不会更改尺寸，因此暂时不用此方法。
	 */
	protected void resetState() {
		isRun = false;
		stopForground();
		stopMidground();
		mOffset = 0;
		mCoef = 1;
	}

	
	/**
	 * 开始动画过程
	 */
	public void start(int mode) {
		if (isRunning()) {
			return;
		}

		this.isRun = true;
		mCurrentMode = mode;
		
		if (mCurrentMode == ENTER_MOTION) {
			startForground();
			startMidground();
		} else if (mCurrentMode == MID_MOTION) {
			startMidground();
		}
		mCurrentMode = ENTER_MOTION;

		invalidate();
	}

	public void stop() {
//		isRun = false;
//		stopForground();
//		stopMidground();
	}

	protected void startForground() {
		mForgroundScroller.startScroll(0, 0, realRect.width(), 0, mEnterBaseDuration);
	}
	
	protected void stopForground(){
		if (!mForgroundScroller.isFinished()) {
			mForgroundScroller.forceFinished(true);
		}
	}

	protected void startMidground() {
		if(!isEnableMid) {
			return;
		}
		if(mCurrentMode == MID_MOTION) {
			mMiddleScroller.startScroll(0, 0, translateDistance, 0, mFlowDuration);
		} else if (mCurrentMode == ENTER_MOTION){
			mMiddleScroller.startScroll(0, 0, translateDistance, 0, mEnterMidDuration);
		}
	}
	
	protected void stopMidground() {		
		if (!mMiddleScroller.isFinished()) {
			updateOffset();
			mMiddleScroller.forceFinished(true);
		}
		
	}
	
	boolean isRunning() {
		return this.isRun;
	}

	public void setPaddings(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
		imagePaddingLeft = paddingLeft;
		imagePaddingtop = paddingTop;
		imagePaddingRight = paddingRight;
		imagePaddingBottom = paddingBottom;
	}

	@Override
	public void layout(int l, int t, int r, int b) {
		super.layout(l, t, r, b);

		width = getWidth() - getPaddingLeft() - getPaddingRight();
		height = getHeight() - getPaddingTop() - getPaddingBottom();

		dstRect.set(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + (int) width, getPaddingTop() + (int) height);
		realRect.set(imagePaddingLeft, imagePaddingtop, width - imagePaddingRight, height - imagePaddingBottom);
		realRectF.set(imagePaddingLeft, imagePaddingtop, width - imagePaddingRight, height - imagePaddingBottom);
	}

	int f = 0;
	long time = 0;

	@Override
	protected void onDraw(Canvas canvas) {
		drawFrame(canvas);
	}

	private Paint getPaint() {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		paint.setXfermode(new PorterDuffXfermode(Mode.DARKEN));
		return paint;
	}

	/**
	 * 绘制
	 * 
	 * @param canvas
	 */
	protected void drawFrame(Canvas canvas) {
		boolean isInvalidate = false;

		drawBackground(canvas);

		if (isEnableMid && drawMidground(canvas)) {
			isInvalidate = true;
		}

		if (drawForeground(canvas)) {
			isInvalidate = true;
		}

		if (isInvalidate) {
			if(f == 0) {
				time = System.currentTimeMillis();
			}
			f++;
			invalidate();
		} else {
			f++;
			if(DEBUG) {
				Log.d(TAG, "baseview draw frame:"+f*1000/(System.currentTimeMillis()-time));
			}
			f = 0;
			isRun = false;
			mCurrentMode = STATIC;
		}
	}

	protected boolean drawMidground(Canvas canvas) {
		return false;
	}

	protected boolean drawForeground(Canvas canvas) {
	    if(foreDrawable == null){
	        return false;
	    }
		boolean hr = mForgroundScroller.computeScrollOffset();
		// 绘制最前层
		int offset = realRect.width();
		int enTextOffset = (int) (mEnTextBeginLeft - mEnTextEndLeft);
		int cnTextOffset = (int) (mCnTextBeginLeft - mCnTextEndLeft);
		if (isRunning() && mCurrentMode == ENTER_MOTION) {
			if (hr) {
				offset = mForgroundScroller.getCurrX();
				if(realRect.width() != 0) {
					enTextOffset = enTextOffset*offset / realRect.width() ;
					cnTextOffset = cnTextOffset*offset / realRect.width() ;
				}
			}
		}

		Rect foreDstRect = new Rect(realRect);// 目标矩形，即该控件的矩形
		foreDstRect.left += (realRect.width() - offset);
		foreDstRect.right += (realRect.width() - offset);

		foreDrawable.setBounds(foreDstRect);
		canvas.clipRect(realRect);
		foreDrawable.draw(canvas);

		if (mEnText != null) {
			paint.setTextSize(mEnTextSize);
			paint.setColor(mEnTextColor);
//			canvas.drawText(mEnText, mEnTextBeginLeft - enTextOffset, mEnTextBeginTop, paint);
		}

		if (mCnText != null) {
			paint.setTextSize(mCnTextSize);
			paint.setColor(mCnTextColor);
			canvas.drawText(mCnText, mCnTextBeginLeft - cnTextOffset, mCnTextBeginTop, paint);
		}

		return hr;

	}

	protected boolean drawBackground(Canvas canvas) {
		if (null != backDrawable) {
			backDrawable.setBounds(dstRect);
			backDrawable.draw(canvas);
		}

		return false;
	}

	protected void updateOffset(){
		int offset = mMiddleScroller.getOffset();

		if (mCoef > 0) {
			if (mOffset + offset >= translateDistance) {
				mCoef = -1;
				offset = (mOffset + offset) - translateDistance;
				mOffset = translateDistance;
			}
		} else {
			if (mOffset - offset < 0) {
				mCoef = 1;
				offset -= mOffset;
				mOffset = 0;
			}
		}

		mOffset += offset * mCoef;
	}
	
	class MidScroller extends Scroller {
		int mCurrX = 0;
		int mOffset = 0;

		public MidScroller(Context context, Interpolator interpolator, boolean flywheel) {
			super(context, interpolator, flywheel);
			// TODO Auto-generated constructor stub
		}

		public MidScroller(Context context, Interpolator interpolator) {
			super(context, interpolator);
			// TODO Auto-generated constructor stub
		}

		public MidScroller(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean computeScrollOffset() {
			boolean hr = super.computeScrollOffset();
			mOffset = getCurrX() - mCurrX;
			mCurrX = getCurrX();

			return hr;
		}

		public int getOffset() {
			return mOffset;
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy) {
			super.startScroll(startX, startY, dx, dy);
			mCurrX = startX;
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy, int duration) {
			super.startScroll(startX, startY, dx, dy, duration);
			mCurrX = startX;
		}
	}
}