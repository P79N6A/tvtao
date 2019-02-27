package com.yunos.tv.app.widget.round;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

//@TargetApi(VERSION_CODES.JELLY_BEAN_MR2) //4.3及以上有效
public class RoundPathImageView extends ImageView {
	public static final String TAG = "RoundCornerImageView";
	
	Path mPath = null;
	protected float mCornerRadius = 20.f;
	boolean needHandleRoundImage = true;
	
	public boolean isNeedHandleRoundImage() {
		return needHandleRoundImage;
	}

	public void setNeedHandleRoundImage(boolean needHandleRoundImage) {
		this.needHandleRoundImage = needHandleRoundImage;
	}

	public RoundPathImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public RoundPathImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RoundPathImageView(Context context) {
		super(context);
	}
	
	public float getCornerRadius() {
		return mCornerRadius;
	}

	public void setCornerRadius(float cornerRadius) {
		this.mCornerRadius = cornerRadius;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (needHandleRoundImage) {
			if (mPath == null) {
				mPath = new Path();
				int width = this.getWidth();
				int height = this.getHeight();
				mPath.addRoundRect(new RectF(0.0F, 0.0F, width, height), this.mCornerRadius, this.mCornerRadius, Path.Direction.CW);
			}
			
			canvas.clipPath(mPath);
		}
		super.onDraw(canvas);
	}
	
}
