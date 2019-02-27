package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.yunos.tv.app.widget.focus.ScalePositionManager;

public class ScaleView extends BaseView {
	private final static String TAG = "ScaleView";

	private RectF mMidRect = new RectF();
	private RectF mFinalMidRect = new RectF();
	
	public ScaleView(Context context) {
		super(context);
	}

	public ScaleView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScaleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setBitmap(Drawable backDrawable, BitmapDrawable middleDrawable, BitmapDrawable foreDrawable) {
		super.setBitmap(backDrawable, middleDrawable, foreDrawable);
		if(middleDrawable != null) {
			mMidRect.set(0, 0, middleDrawable.getIntrinsicWidth(), middleDrawable.getIntrinsicHeight());
		}
	}
	
	/**
	 * 绘制
	 * 
	 * @param canvas
	 */
	@Override
	protected boolean drawMidground(Canvas canvas) {
		if(middleDrawable == null) 
			return false;
		boolean hr = mMiddleScroller.computeScrollOffset();
		if (hr) {
			updateOffset();
		}
		
		mShaderMatrix.set(null);
		int offset = mOffset + mFrom;
		float scale = (float) offset / 100;
  		updateRect(scale);
		mBitmapShader.setLocalMatrix(mShaderMatrix);
		canvas.drawRoundRect(realRectF, roundRadius, roundRadius, mBitmapPaint);

		return hr;
	}
	
	private void updateRect(float scale){
		ScalePositionManager.instance().getScaledRect(mMidRect, mFinalMidRect, scale, scale, 0.0f, 0.0f);
		float diffX = mFinalMidRect.centerX() - realRect.centerX();
		float diffY = mFinalMidRect.centerY() - realRect.centerY();
		mFinalMidRect.offset(-diffX, -diffY);
//		mFinalMidRect.inset(-diffX, -diffY);
//		mShaderMatrix.mapRect(mFinalMidRect);
		mShaderMatrix.setRectToRect(mMidRect, mFinalMidRect, Matrix.ScaleToFit.FILL);
		
	}
}