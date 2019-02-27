package com.aliyun.base.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

/**
 * 可重新设置bitmap的BitmapDrawable
 * @author leiming.yanlm
 *
 */
public class AsyncDrawable extends BitmapDrawable {
	
	private Bitmap mBitmap;
	private Paint boundsPaint;
	
	public AsyncDrawable(Bitmap bitmap) {
		super(bitmap);
		mBitmap = bitmap;
		this.setBounds(10, 10, mBitmap.getWidth() + 20, mBitmap.getHeight() + 21);
	}

	@Override
	public void draw(Canvas canvas) {
		if (mBitmap != null) {
			int bmWidth = mBitmap.getWidth();
			int bmHeight = mBitmap.getHeight();
			canvas.save();
			Rect rect = this.getBounds();
			canvas.drawLines(new float[] { rect.left, rect.top, rect.left + bmWidth + 10, rect.top, 
					rect.left + bmWidth + 10, rect.top, rect.left + bmWidth + 10, rect.top + bmHeight + 10,  
					rect.left + bmWidth + 10, rect.top + bmHeight + 10, rect.left, rect.top + bmHeight + 10,
					rect.left, rect.top + bmHeight + 10, rect.left, rect.top }, getBoundsPaint());
			
			Paint paint = new Paint();
			canvas.drawBitmap(mBitmap, rect.left + 5, rect.top + 5, paint);
			canvas.restore();
		}
	}

	private Paint getBoundsPaint() {
		if (boundsPaint == null) {
			boundsPaint = new Paint();
			boundsPaint.setAntiAlias(true);
			boundsPaint.setColor(Color.GRAY);
			boundsPaint.setStyle(Paint.Style.STROKE);
//			boundsPaint.setStrokeWidth(1 * BaseAppInfo.perDip);
		}
		return boundsPaint;
	}

	public Bitmap getAsyncBitmap() {
		return mBitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		mBitmap = bitmap;
		this.setBounds(10, 10, mBitmap.getWidth() + 20, mBitmap.getHeight() + 20);
	}


}