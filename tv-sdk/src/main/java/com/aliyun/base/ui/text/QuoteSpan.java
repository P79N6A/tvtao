package com.aliyun.base.ui.text;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.LeadingMarginSpan;
import android.text.style.UpdateAppearance;
import android.util.Log;

public class QuoteSpan extends CharacterStyle implements android.text.style.LineBackgroundSpan, LeadingMarginSpan , UpdateAppearance{

	private Drawable mDrawableSingle;
	private Drawable mDrawableTop;
	private Drawable mDrawableMiddle;
	private Drawable mDrawableBottom;
	private int mStart = -1;
	private int mEnd = -1;

	public QuoteSpan(Drawable drawablemSingle, Drawable drawableTop, Drawable drawableMiddle, Drawable drawableBottom) {

		mDrawableSingle = drawablemSingle;
		mDrawableTop = drawableTop;
		mDrawableMiddle = drawableMiddle;
		mDrawableBottom = drawableBottom;
	}

	public void setIndex(int start, int end) {
		mStart = start;
		mEnd = end;
	}

	// @Override
	// public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top,
	// int baseline, int bottom, CharSequence text, int start, int end, boolean
	// first, Layout layout) {
	// if (mStart == -1) {
	// SpannableString span = (SpannableString) text;
	// mStart = span.getSpanStart(this);
	// mEnd = span.getSpanEnd(this);
	// }
	// Drawable drawable = null;
	// //Log.i("", "--------mEnd:" + mEnd + " mStart:" + mStart + " start:" +
	// start + " end:" + end);
	// if (start == mStart) {
	// if (end + 1 >= mEnd) {
	// drawable=mDrawableSingle;
	//
	// } else {
	// drawable=mDrawableTop;
	// }
	//
	// } else {
	// if (end + 1 >= mEnd) {
	// drawable=mDrawableBottom;
	// } else {
	// drawable=mDrawableMiddle;
	// }
	// }
	// //Log.i("",dir + "  " + baseline + "left:" + x + " top:" + top +
	// " right:" + layout.getEllipsizedWidth() + " bottom:" + bottom + " rect:"
	// +c.getClipBounds());
	// drawable.setBounds(x, top, layout.getEllipsizedWidth(), bottom);
	// drawable.draw(c);
	//
	// }

	@Override
	public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start,
			int end, int lnum) {
		if (mStart == -1) {
			if (text instanceof SpannedString) {
				SpannedString span = (SpannedString) text;
				mStart = span.getSpanStart(this);
				mEnd = span.getSpanEnd(this);
			} else if (text instanceof SpannableString) {
				SpannableString span = (SpannableString) text;
				mStart = span.getSpanStart(this);
				mEnd = span.getSpanEnd(this);
			} else {
				
			}
			
		}
		Drawable drawable = null;
		// Log.i("", "--------mEnd:" + mEnd + " mStart:" + mStart + " start:" +
		// start + " end:" + end);
		if (start == mStart) {
			if (end + 1 >= mEnd) {
				drawable = mDrawableSingle;
			} else {
				drawable = mDrawableTop;
			}

		} else {
			if (end + 1 >= mEnd) {
				drawable = mDrawableBottom;
			} else {
				drawable = mDrawableMiddle;
			}
		}
		// Log.i("",dir + "  " + baseline + "left:" + x + " top:" + top +
		// " right:" + layout.getEllipsizedWidth() + " bottom:" + bottom +
		// " rect:" +c.getClipBounds());
		drawable.setBounds(left, top, right, bottom+3);
		drawable.draw(c);
		
	}

	
	public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, FontMetricsInt fm) {
		Log.i("", "-----------------chooseHeight--------------------------------" + start + " " + end + " " + spanstartv + " " + v + " font: " + fm) ;
		if (start == 0) {
			
//			fm.ascent = -50; 
//            fm.descent = 0; 

            fm.top = -40;
           // fm.bottom = 0;
		} else {			 
			if (end + 1 >= mEnd) {
				fm.bottom = 10;
			} 
		}
	}

	
	public int getLeadingMargin(boolean first) {
		return 10;
	}

	
	public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start,
			int end, boolean first, Layout layout) {
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setColor(Color.GRAY);
	}
	
	public static class BlockquoteStart extends StartSpan {
	}
}
