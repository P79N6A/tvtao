package com.yunos.tv.app.widget.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class BitmapTranslatePainter extends BasePainter {

	public final static int HORIZONTAL = 0;
	public final static int VERTICAL = 1;

	public BitmapTranslatePainter(Context context) {
		super(context);
	}

	Drawable mDrawable;
	Rect mRect = new Rect();
	int mOffset = 0;
	int mCurrentAlpha = 255 ;//初始的alpha
	int mStartAlpha ;
	int mEndAlpha ;
	int mMoveType = HORIZONTAL;

	public void setDrawable(Drawable d) {
		mDrawable = d;
	}

	public void setRect(int left, int top, int right, int bottom) {
		mRect.set(left, top, right, bottom);
	}

	public void setMoveType(int moveType) {
		mMoveType = moveType;
	}
	
	public int getDrawableHeight(){
		if(mDrawable == null){
			return 0;
		}
		BitmapDrawable drawAble = (BitmapDrawable)mDrawable;
		Bitmap btmp = drawAble.getBitmap();
		return btmp == null ? 0 : btmp.getHeight();
	}
	
	public void setAlpha(int alpha) {
		mCurrentAlpha = alpha;
	}

	public void show(int offset, int startAlpha, int endAlpha) {
		mOffset = offset;
		mStartAlpha = startAlpha;
		mEndAlpha = endAlpha;
		if (mMoveType == HORIZONTAL) {
			mScroller.startScroll(0, 0, offset, 0, DEFAULT_ANIMATE_DURATION);
		} else if (mMoveType == VERTICAL) {
			mScroller.startScroll(0, 0, 0, offset, DEFAULT_ANIMATE_DURATION);
		}
		paintInvalidate();
	}

	public void show(int offset, int startAlpha, int endAlpha, int duration, int moveType) {
		mOffset = offset;
		mStartAlpha = startAlpha;
		mEndAlpha = endAlpha;
		mScroller.startScroll(0, 0, offset, 0, duration);
		mMoveType = moveType;
		if (mMoveType == HORIZONTAL) {
			mScroller.startScroll(0, 0, offset, 0, duration);
		} else if (mMoveType == VERTICAL) {
			mScroller.startScroll(0, 0, 0, offset, duration);
		}
		paintInvalidate();
	}

	@Override
	public boolean draw(Canvas canvas) {
		if (!super.draw(canvas)) {
			return false;
		}
		drawBitmap(canvas);

		return true;
	}

	void drawBitmap(Canvas canvas) {
		mScroller.computeScrollOffset();
		if(mDrawable != null){
			int offsetX = mScroller.getCurrX() - mScroller.getStartX();
			int offsetY = mScroller.getCurrY() - mScroller.getStartY();
			int left = mRect.left + offsetX;
			int right = mRect.right + offsetX;
			int top = mRect.top + offsetY;
			int bottom = mRect.bottom + offsetY;
			mDrawable.setBounds(left, top, right, bottom);
			if (mEndAlpha != mStartAlpha) {
				if (mMoveType == HORIZONTAL) {
					mCurrentAlpha = (int) (mStartAlpha + ((float) offsetX / mOffset) * (mEndAlpha - mStartAlpha));
				} else if (mMoveType == VERTICAL) {
					mCurrentAlpha = (int) (mStartAlpha + ((float) offsetY / mOffset) * (mEndAlpha - mStartAlpha));
				}
			}
			mDrawable.setAlpha(mCurrentAlpha);
			mDrawable.draw(canvas);
		}
		paintInvalidate();
	}

}
