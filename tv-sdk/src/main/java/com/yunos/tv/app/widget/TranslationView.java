package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class TranslationView extends BaseView {
	private final static String TAG = "TranslationView";
	

	public TranslationView(Context context) {
		super(context);
	}

	public TranslationView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TranslationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public void setTranslationDistance(int from, int to) {
		translateDistance = to - from;
		
		if(middleDrawable != null && translateDistance > middleDrawable.getIntrinsicWidth() ) {
			translateDistance = middleDrawable.getIntrinsicWidth();
		}
//		Log.d(TAG, "translate distance is "+ translateDistance +";from"+from+";to:"+to);
		
		mFrom = from;
		mTo = to;
		
//		resetState();
	}
	

	@Override
	protected boolean drawMidground(Canvas canvas) {
		if(middleDrawable == null) 
			return false;
		boolean hr = mMiddleScroller.computeScrollOffset();
		if (hr) {
			updateOffset();
		} 

		int offset = mOffset + mFrom;
		mShaderMatrix.set(null);
		mShaderMatrix.setTranslate(-offset, 0);
		mBitmapShader.setLocalMatrix(mShaderMatrix);
		canvas.drawRoundRect(realRectF, roundRadius, roundRadius, mBitmapPaint);

		return hr;
	}
	
	
}