package com.aliyun.base.ui.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ReplacementSpan;

import java.lang.ref.WeakReference;

public abstract class ImageSpan extends ReplacementSpan {

	protected String mSource;
	
	public String getSource() {
		return mSource;
	}
	
	public void setSource(String source) {
		mSource = source;
	}

	/**
	 * A constant indicating that the bottom of this span should be aligned with
	 * the bottom of the surrounding text, i.e., at the same level as the lowest
	 * descender in the text.
	 */
	public static final int ALIGN_BOTTOM = 0;

	/**
	 * A constant indicating that the bottom of this span should be aligned with
	 * the baseline of the surrounding text.
	 */
	public static final int ALIGN_BASELINE = 1;

	public static final int ALIGN_CENTER = 2;

	protected final int mVerticalAlignment;

	/**
	 * @param verticalAlignment
	 *            one of {@link DynamicDrawableSpan#ALIGN_BOTTOM} or
	 *            {@link DynamicDrawableSpan#ALIGN_BASELINE}.
	 */
	public ImageSpan(String source, int verticalAlignment) {
		mVerticalAlignment = verticalAlignment;
		mSource = source;
	}

	public ImageSpan(String source) {
		mSource = source;
		mVerticalAlignment = ALIGN_BOTTOM;
	}

	public abstract void getDrawable(String path);

	public void setDrawable(Drawable drawable) {
		mDrawableRef = new WeakReference<Drawable>(drawable);
	}
	
	@Override
	public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
		Drawable drawable = getCachedDrawable();
		
		Rect rect = null;
		if (drawable == null) {
			rect = new Rect(0, 0, 0, 0);
		} else {
			rect = drawable.getBounds();
		}
		
        if (fm != null) {
            fm.ascent = -rect.bottom; 
            fm.descent = 0; 

            fm.top = fm.ascent;
            fm.bottom = 0;
        }
		return rect.right;
	}
	
	@Override
	public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
		Drawable drawable = getCachedDrawable();
		if (drawable == null) {
			return;
		}
		
//        canvas.save();
//        
//        int transY = bottom - drawable.getBounds().bottom;
//        if (mVerticalAlignment == ALIGN_BASELINE) {
//            transY -= paint.getFontMetricsInt().descent;
//        }
//
//        canvas.translate(x, transY);
//        drawable.draw(canvas);
//        canvas.restore();
		
		drawable.draw(canvas);
		
        
		//TODO 先注释掉
//		canvas.save();
//		// Log.i("", "getSize:" + b);
//		int transY = bottom - mDrawable.getBounds().bottom;
//		if (mVerticalAlignment == ALIGN_BASELINE) {
//			transY -= paint.getFontMetricsInt().descent;
//		}
//		canvas.translate(x, transY);
//	
//		Rect r = new Rect();
//		canvas.getClipBounds(r);
//		Log.i("ImageSpan", "draw: " + mDrawable.getBounds().toShortString() + ", r:" + r.toShortString());
//		mDrawable.setBounds(r);
//		mDrawable.draw(canvas);
//		Rect rect = new Rect(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
//		canvas.restore();
	}
	
    protected Drawable getCachedDrawable() {
        Drawable d = null;

        if (mDrawableRef != null) {
        	d = mDrawableRef.get();
        }
        if (d == null) {
            getDrawable(mSource);
            return null;
        }

        return d;
    }

    protected WeakReference<Drawable> mDrawableRef;

}
