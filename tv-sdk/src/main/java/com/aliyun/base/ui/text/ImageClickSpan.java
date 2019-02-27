package com.aliyun.base.ui.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.aliyun.base.ui.text.LinkImgMovementMethod.ClickStyle;

public abstract class ImageClickSpan extends ImageSpan implements ClickStyle {
	
	private boolean mClickable = true;
	
	private Object tag;
	
	public Object getTag() {
		return tag;
	}
	
	public void setTag(Object tag) {
		this.tag = tag;
	}
	
	public ImageClickSpan(String source, int verticalAlignment) {
		super(source, verticalAlignment);
	}

	public ImageClickSpan(String source) {
		super(source);
	}

	protected Rect mRect = new Rect();

	@Override
	public boolean checkIn(int x, int y) {
//		Log.i("", "x: " + x + " y: " + y + "ImageClickSpan checkIn " + mRect );
		if (mRect != null && mClickable) {
			return mRect.contains(x, y);
		}
		return false;
	}
	
	public void setDrawable(Drawable drawable) {
		super.setDrawable(drawable);
		mRect = null;
	}

	@Override
	public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
		return super.getSize(paint, text, start, end, fm);
	}
	
	@Override
	public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
		Drawable mDrawable = getCachedDrawable();
		if (mDrawable == null) {
			return;
		}
		mRect = new Rect(mDrawable.getBounds());
		mRect.offsetTo((int)x,  top);
		
		if (mRect == null) {
			mRect = new Rect(mDrawable.getBounds());
			mRect.offsetTo((int)x,  top);
		}
		
		canvas.save();
//		canvas.clipRect((int)x, top, mRect.right, mRect.bottom);
//		Log.i("aabb", "--------------=======canvas.getClipBounds():" + canvas.getClipBounds());
		canvas.translate(x, top);
		mDrawable.draw(canvas);
		canvas.restore();
	}

	public void setClickable(boolean clickable) {
		this.mClickable = clickable;
	}
	
	public boolean mIsNewLine = true;

}
