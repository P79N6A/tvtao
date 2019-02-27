package com.yunos.tv.app.widget.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

public class TextPainter extends BaseClipPainter {

	static final String TAG = TextPainter.class.getSimpleName();

	private int mDuration = DEFAULT_ANIMATE_DURATION;
	private float mStartAlpha = 0;
	private float mEndAlpha = 1;
	private List<TextPoint> mTextList = new ArrayList<TextPoint>();

	public TextPainter(Context context) {
		super(context);
	}

	public void removeTextPoint(String text){
		int size = mTextList == null ? 0 : mTextList.size();
		TextPoint point = null;
		for(int i = 0; i < size; i++){
			point = mTextList.get(i);
			if(point.mTextValue.equals(text)){
				mTextList.remove(point);
				size = mTextList.size();
			}
		}
	}
	
	public void removeAllTextPoint(){
		if(mTextList == null){
			return ;
		}
		
		mTextList.clear();
	}
	
	public void addTextPoint(String text, float textSize, int textColor, int orginBorder, int dstBorder, int topBorder, Paint.Align align) {
		TextPoint textPoint = new TextPoint(text);
		textPoint.setTextSize(textSize);
		textPoint.setTextColor(textColor);
		textPoint.setTextAlign(align);
		textPoint.setOrginBorder(orginBorder);
		textPoint.setDstBorder(dstBorder);
		textPoint.setTopBorder(topBorder);
		mTextList.add(textPoint);
	}

	public void setDuration(int duration) {
		mDuration = duration;
	}

	public void show(int offset, int duration) {
		mDuration = duration;
		if(mDirection == LOCK_WISE){
			mScroller.startScroll(0, 0, offset, 0, mDuration);
		} else if(mDirection == ANTI_LOCK_WISE) {
			mScroller.startScroll(offset, 0, -offset, 0, mDuration);
		}
		paintPostInvalidate();
	}
	
	public void show(int offset, float startAlpha, float endAlpha, int duration) {
		mDuration = duration;
		mStartAlpha = startAlpha;
		mEndAlpha = endAlpha;
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
			drawText(canvas);
			paintPostInvalidate();
		}
		return shoudDraw;
	}

	public void drawText(Canvas canvas) {
		int size = mTextList.size();
		TextPoint textPoint = null;
		for (int i = 0; i < size; i++) {
			textPoint = mTextList.get(i);
			if (textPoint != null) {
				textPoint.calcuAlpha(mScroller.getCurrX() - mScroller.getStartX(), mScroller.getFinalX() - mScroller.getStartX());
				textPoint.calcuDistance(mScroller.getCurrX());
				textPoint.drawText(canvas);
			}
		}
	}

	class TextPoint {
		private String mTextValue = "";
		private int mOriginBorder;
		private int mDstBorder;
		private int mTopBorder;
		private int mOffsetX = 0;
		private float mTextSize = 17;
		private float mTextAlpha;
		private int mTextColor = Color.WHITE;
		private Paint.Align mAlign = Paint.Align.RIGHT;

		public TextPoint(String value) {
			mTextValue = value;
		}

		public void setOrginBorder(int border) {
			mOriginBorder = border;
		}

		public void setDstBorder(int border) {
			mDstBorder = border;
		}
		
		public void setTopBorder(int border) {
			mTopBorder = border;
		}
		
		public void calcuAlpha(int offset, int distance){
			if(distance != 0){
				mTextAlpha = mStartAlpha + offset * (mEndAlpha - mStartAlpha) / distance;
			} else {
				mTextAlpha = mStartAlpha;
			}
		}

		public void calcuDistance(int offset) {
			mOffsetX = offset;
		}

		public void setTextColor(int color) {
			mTextColor = color;
		}

		public void setTextSize(float textSize) {
			mTextSize = textSize;
		}

		public void setTextAlign(Paint.Align align) {
			mAlign = align;
		}

		public int getTextColor() {
			return mTextColor;
		}

		public float getTextSize() {
			return mTextSize;
		}

		public void drawText(Canvas canvas) {
			Paint textPaint = getPaint();
			if (textPaint != null) {
				textPaint.reset();
			} else {
				return;
			}
			textPaint.setColor(mTextColor);
			textPaint.setTextSize(mTextSize);
			textPaint.setAntiAlias(true);
			textPaint.setTextAlign(mAlign);
			textPaint.setAlpha((int)(mTextAlpha * 255));
			if (isNeedClip()) {
				canvas.clipPath(getClipPath());
			}
			if(mTextValue != null){
				canvas.drawText(mTextValue, mOriginBorder + mOffsetX, mTopBorder - textPaint.ascent(), textPaint);
			}
		}
	}

}
