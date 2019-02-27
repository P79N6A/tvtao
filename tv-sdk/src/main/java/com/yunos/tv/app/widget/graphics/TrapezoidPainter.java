package com.yunos.tv.app.widget.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.view.View;

public class TrapezoidPainter extends BaseClipPainter {

	static final String TAG = TrapezoidPainter.class.getSimpleName();
	private int mDuration = DEFAULT_ANIMATE_DURATION;
	private Trapezoid mTrapezoid = new Trapezoid();
	private int mTotalDistance;
	
	public TrapezoidPainter(Context context) {
		super(context);
	}

	@Override
	public void resgister(PainterInterface i) {
		super.resgister(i);
		if(mPainterInterface != null){
			mPainterInterface.setLayerType(View.LAYER_TYPE_SOFTWARE, mPaint);
		}
	}
	
	public void setTrapezoid(int left, int top, int right, int bottom) {
		Trapezoid trapezoid = mTrapezoid;
		if (trapezoid == null) {
			trapezoid = new Trapezoid();
			mTrapezoid = trapezoid;
		}
		trapezoid.setTrapezoid(left, top, right, bottom);
	}

	public void setTrapezoidColor(int[] color) {
		Trapezoid trapezoid = mTrapezoid;
		if (trapezoid == null) {
			trapezoid = new Trapezoid();
			mTrapezoid = trapezoid;
		}
		trapezoid.setTrapezoidColor(color);
	}

	public void show(int offset, int duration) {
		mDuration = duration;
		mTotalDistance = offset;
		if(mDirection == LOCK_WISE){
			mScroller.startScroll(0, 0, offset, 0, mDuration);
		} else if(mDirection == ANTI_LOCK_WISE) {
			mScroller.startScroll(offset, 0, -offset, 0, mDuration);
		}
		paintPostInvalidate();
	}

	@Override
	public boolean draw(Canvas canvas) {
		mScroller.computeScrollOffset();
		boolean shoudDraw = super.draw(canvas);
		if (shoudDraw) {
			drawTrapezoid(canvas);
			paintPostInvalidate();
		}
		return shoudDraw;
	}

	protected void drawTrapezoid(Canvas canvas) {
		Trapezoid trapezoid = mTrapezoid;
		if (trapezoid != null) {
			trapezoid.calcuDistance(mScroller.getCurrX());
			trapezoid.drawTrapezoid(canvas);
		}
	}

	public void setDuration(int duration) {
		mDuration = duration;
	}

	public int getDuration() {
		return mDuration;
	}

	class Trapezoid {

		private int mOffsetX = 0;
		private int mLeft;
		private int mTop;
		private int mRight;
		private int mBottom;
		/* 设置渐变色 */
		private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
		private Bitmap mSrcBitmap;
        private Bitmap mDstBitmap;//目标，带渐变
        private int[] mTrapezoidColor = new int[] { 0xFFFFFF00, 0xea00ff8D, 0xcc00ff8D, 0xb900ff8D, 0x7700ff8D, 0x2200ff8D };

		public Trapezoid() {

		}

		public Trapezoid(int left, int top, int right, int bottom) {
			setTrapezoid(left, top, right, bottom);
		}

		public void setTrapezoid(int left, int top, int right, int bottom) {
			mLeft = left;
			mTop = top;
			mRight = right;
			mBottom = bottom;
		}

		public void setTrapezoidColor(int[] trapezoidColor) {
			mTrapezoidColor = trapezoidColor;
			if(mDstBitmap != null){
				mDstBitmap.recycle();
				mDstBitmap = null;
			}
			if(mSrcBitmap != null){
				mSrcBitmap.recycle();
				mSrcBitmap = null;
			}
		}

		void calcuDistance(int offset) {
			mOffsetX = offset;
		}

		void drawTrapezoid(Canvas canvas) {
			if(mTotalDistance == 0){
				return ;
			}

			if(mDstBitmap == null){
				mDstBitmap = makeDstBitmap(mLeft, mTop, mRight, mBottom);
			}
			
			if(mSrcBitmap == null){
				mSrcBitmap = makeSrcBitmap(mLeft + mTotalDistance, mTop, mRight + mTotalDistance, mBottom);
			}
			
			Paint trapezoidPaint = getPaint();
			if (trapezoidPaint == null) {
				return;
			}

			trapezoidPaint.reset();
			trapezoidPaint.setFilterBitmap(true);
			
			int sc = canvas.saveLayer(mLeft + mTotalDistance, mTop, mRight + mTotalDistance, mBottom, null,
                    Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
			
			trapezoidPaint.setAntiAlias(true);
			trapezoidPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
			trapezoidPaint.setStyle(Paint.Style.FILL);
			
			if(mDstBitmap != null){
				canvas.drawBitmap(mDstBitmap, mLeft + mOffsetX, mTop, trapezoidPaint);
				trapezoidPaint.setXfermode(mXfermode);
			}
			
			if(mSrcBitmap != null){
				canvas.drawBitmap(mSrcBitmap, mLeft + mTotalDistance, mTop, trapezoidPaint);
	            trapezoidPaint.setXfermode(null);
			}
            
            canvas.restoreToCount(sc);
		}
		
		Bitmap makeDstBitmap(int left, int top, int right, int bottom) {
	        Bitmap bitmap = Bitmap.createBitmap(right - left, bottom - top, Bitmap.Config.ARGB_8888);
	        
	        Canvas bpCanvas = new Canvas(bitmap);
	        
	        Paint bpPaint = new Paint();
	        bpPaint.setAntiAlias(true);
	        bpPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
	        bpPaint.setFilterBitmap(true);
	        bpPaint.setStyle(Paint.Style.FILL);      
	        Shader shader = new LinearGradient(0, bottom - top, right - left, bottom - top, mTrapezoidColor, null, Shader.TileMode.CLAMP);
			bpPaint.setShader(shader);
			
	        bpCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
	        bpCanvas.drawRect(0, 0, right - left, bottom - top, bpPaint);
	        return bitmap;
	    }

	    Bitmap makeSrcBitmap(int left, int top, int right, int bottom) {
	    	Bitmap bitmap = Bitmap.createBitmap(right - left, bottom - top, Bitmap.Config.ARGB_8888);
	    	
	        Canvas bpCanvas = new Canvas(bitmap);
	        
	        Paint bpPaint = new Paint();
	        bpPaint.setAntiAlias(true);
	        bpPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
	        bpPaint.setFilterBitmap(true);
	        bpPaint.setStyle(Paint.Style.FILL);
	        bpPaint.setColor(0xFF66AAFF);
	        
	        Path clipPath = getRelativeClipPath(left, top);
	        bpCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
	        bpCanvas.drawPath(clipPath, bpPaint);
	        return bitmap;
	    }
	}
}
