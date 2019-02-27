package com.yunos.tv.app.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import com.yunos.tv.app.widget.Interpolator.CubicBezierInterpolator;

public class NotifyMarqueeTextView extends MarqueeTextView {

	protected final static int STATE_NONE = 0;
	protected final static int STATE_CONTENT = 1;
	protected final static int STATE_EXPAND = 2;
	protected final static int STATE_COLLIPSE = 3;
	
	protected int mNotifyBackGroundState = STATE_NONE;
	protected long mDuration = 600;
	protected long mCurrTime = 0;
	protected long mStepTime = 20;
	protected int mNotifyBackgroundId = 0;
	protected Bitmap mNotifyBackground = null;
	protected Rect mCurrRect = new Rect();
	protected CubicBezierInterpolator mCubicBezierMove = new CubicBezierInterpolator(1, 0, 0, 1);
	protected CubicBezierInterpolator mCubicBezierAlpha = new CubicBezierInterpolator(1, 1, 0, 0);
	private int mAnimateBackgroundColor = 0xFF010101;

	public NotifyMarqueeTextView(Context context) {
		super(context);
	}

	public NotifyMarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NotifyMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected float calcuStep() {
		return super.calcuStep() - getMeasuredWidth();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (getMeasuredWidth() * getMeasuredHeight() != 0) {
			createNotifyBackgroundIfNeeded();
		}
	}

	private void createNotifyBackgroundIfNeeded(){
		boolean isNeeded = false;
		if(mNotifyBackground == null && mNotifyBackgroundId != 0){
			isNeeded = true;
		}
		
		if(mNotifyBackground != null && mNotifyBackground.getWidth() == getMeasuredWidth() && mNotifyBackground.getHeight() == getMeasuredHeight()){
			isNeeded = false;
		}
		
		if(isNeeded){
			Bitmap background = BitmapFactory.decodeResource(getResources(), mNotifyBackgroundId);
			mNotifyBackground = createRepeater(getMeasuredWidth(), background);
		}
	}
	
	public void setAnimateBackgroundResId(int resId){
		mNotifyBackgroundId = resId;
		if(mNotifyBackground != null){
			mNotifyBackground.recycle();
			mNotifyBackground = null;
		}
		createNotifyBackgroundIfNeeded();
	}
	
	public void setAnimateBackgroundColor(int color){
		mAnimateBackgroundColor = color;
	}

	private Bitmap createRepeater(int width, Bitmap src){
		int count = (width + src.getWidth() - 1) / src.getWidth();
		Bitmap repeate = Bitmap.createBitmap(width, src.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(repeate);
		for(int i = 0; i < count; i++){
			canvas.drawBitmap(src, src.getWidth() * i, 0, null);
		}
		return repeate;
	}
	
	public void setText(String text) {
		mNotifyBackGroundState = STATE_NONE;
		super.stopMarquee();
		if (isStart()) {
			super.startMarquee();
		}
		super.setText(text);
		setInit(false);
	}
	
	public void startMarquee() {
		mNotifyBackGroundState = STATE_EXPAND;
		invalidate();
	}
	
	public void stopMarquee(){
		mNotifyBackGroundState = STATE_COLLIPSE;
		invalidate();
	}

	@Override
	public void onDraw(Canvas canvas) {
		Paint paint = getPaint();
		if(mNotifyBackGroundState == STATE_CONTENT){
			if(mNotifyBackground != null){
				canvas.drawBitmap(mNotifyBackground, 0, 0, paint);
			} else {
				canvas.drawColor(mAnimateBackgroundColor);
			}
			super.onDraw(canvas);
		} else if(mNotifyBackGroundState == STATE_COLLIPSE || mNotifyBackGroundState == STATE_EXPAND) {
			float interpolationValue = mCubicBezierMove.getInterpolation(mCurrTime * 1.0f / mDuration);
			float distance = getMeasuredWidth() * interpolationValue;
			float left = distance - getMeasuredWidth();
			if(mNotifyBackground != null){
				canvas.drawBitmap(mNotifyBackground, left, 0, paint);
			} else {
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				paint.setColor(mAnimateBackgroundColor);
				mCurrRect.set(getPaddingLeft(), getPaddingTop(), (int)distance, getHeight() - getPaddingBottom());
				canvas.drawRect(mCurrRect, paint);
			}
			
			if(mNotifyBackGroundState == STATE_EXPAND){
				if(mCurrTime + mStepTime >= mDuration){
					mNotifyBackGroundState = STATE_CONTENT;
					super.startMarquee();
				}
			} else if(mNotifyBackGroundState == STATE_COLLIPSE){
				if(mCurrTime <= 0){
					mNotifyBackGroundState = STATE_NONE;
					super.stopMarquee();
				}
			}
			mBgHandler.sendEmptyMessageDelayed(mNotifyBackGroundState, mStepTime);
		}
	}
	
	@SuppressLint("HandlerLeak")
	Handler mBgHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case STATE_COLLIPSE:
				mCurrTime -= mStepTime;
				break;
			case STATE_EXPAND:
				mCurrTime += mStepTime;
				break;
			default:
				break;
			}
			
			invalidate();
		}
	};
	
	public void setDuration(long time){
		mDuration = time;
	}
	
	public void setStepTime(long time){
		mStepTime = time;
	}
	
	public long getStepTime(){
		return mStepTime;
	}
	
	public long getDuration(){
		return mDuration;
	}
	
	public CubicBezierInterpolator getMoveCubicBezierInterpolator(){
		return mCubicBezierMove;
	}
	
	public CubicBezierInterpolator getAlphaCubicBezierInterpolator(){
		return mCubicBezierMove;
	}
	
	public void setMoveCubicBezierInterpolator(CubicBezierInterpolator i){
		mCubicBezierMove = i;
	}
	
	public void setAlphaCubicBezierInterpolator(CubicBezierInterpolator i){
		mCubicBezierAlpha = i;
	}
	
	@SuppressLint("MissingSuperCall")
	protected void onDetachedFromWindow() {
		mCurrTime = 0;
		if(mNotifyBackground != null){
			mNotifyBackground.recycle();
			mNotifyBackground = null;
		}
	}
}
