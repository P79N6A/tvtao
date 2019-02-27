package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.yunos.tv.app.widget.focus.ScalePositionManager;

public class ExplodeCenterView extends ExplodeCenterBaseView{
	public static int AnimType_Translate = 0;
	public static int AnimType_Scale = 1;
	private int mCurAnimType = 0;
	private RectF mMidRect = new RectF();
	private RectF mFinalMidRect = new RectF();
	
	public ExplodeCenterView(Context context) {
		super(context);
	}

	public ExplodeCenterView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExplodeCenterView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setAnimType(int animType) {
		if(animType >= 0 && animType <=1) {
			mCurAnimType = animType;
			resetState();
		}
	}
	
	@Override
	public void setBitmap(Drawable backDrawable, BitmapDrawable middleDrawable,
			BitmapDrawable foreDrawable) {
		super.setBitmap(backDrawable, middleDrawable, foreDrawable);
		if(mCurAnimType == AnimType_Scale && middleDrawable != null) {
			mMidRect.set(0, 0, middleDrawable.getIntrinsicWidth(), middleDrawable.getIntrinsicHeight());
		}
	}
	
	/**
	 * 设置动画移动
	 */
	@Override
	public void setTranslationDistance(int from, int to) {
		super.setTranslationDistance(from, to);
		if(mCurAnimType == AnimType_Translate) {
			if (middleDrawable != null
					&& translateDistance > middleDrawable.getIntrinsicWidth()) {
				translateDistance = middleDrawable.getIntrinsicWidth();
			}
		}
	}

	@Override
	protected boolean drawMidground(Canvas canvas) {
		if (middleDrawable == null)
			return false;

		boolean hr = mMiddleScroller.computeScrollOffset();
		if (hr) {
			updateOffset();
		}

		int offset = mOffset + mFrom;
		mShaderMatrix.set(null);
		if(mCurAnimType == AnimType_Translate) {
			mShaderMatrix.setTranslate(-offset, 0);
		} else if(mCurAnimType == AnimType_Scale) {
			float scale = (float) offset / 100;
	  		updateRect(scale);
		}
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
