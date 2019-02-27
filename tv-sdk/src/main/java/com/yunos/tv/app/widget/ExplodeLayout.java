package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.yunos.tv.app.widget.ExplodeLayoutAdapter.Size;

public class ExplodeLayout extends View {

	public ExplodeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ExplodeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ExplodeLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	ExplodeLayoutAdapter mAdapter;

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	class ExplodeInfo {
		
	}
	
	
	public class DrawingItem {
		Size itemSize;
		Position itemPosition;
		ItemScale itemScale;
		ItemAlpha itemAlpha;
		Drawable itemDrawable;
		Rect itemOriRect = new Rect();
		Rect itemRect = new Rect();

		public DrawingItem(Drawable item, Size size, Position position, ItemScale scale, ItemAlpha alpha) {
			itemDrawable = item;
			itemSize = size;
			itemPosition = position;
			itemScale = scale;
			itemAlpha = alpha;
			
			getRect();
		}

		public void explode(){
			itemScale.explode();
			itemAlpha.explode();
		}
		
		public void collipse(){
			itemScale.collipse();
			itemAlpha.collipse();
		}
		
		public boolean draw(Canvas canvas) {
			boolean hr = false;
			if(!itemScale.isFinished()){
				getScaledRect(itemScale.getScale());
			}
			itemDrawable.setBounds(itemRect);
			itemDrawable.setAlpha(itemAlpha.getAlpha());
			itemDrawable.draw(canvas);
			return hr;
		}
		
		private void getRect(){
			int centerX = getWidth() / 2;
			int centerY = getHeight() / 2;
			
			itemOriRect.left = centerX + itemPosition.x();
			itemOriRect.right = itemRect.left + itemSize.width;
			itemOriRect.top = centerY - itemPosition.y();
			itemOriRect.bottom = itemRect.top + itemSize.height;
			
			itemRect.set(itemOriRect);
		}
		private void getScaledRect(float scale){
			int diffW = (int) (itemOriRect.width() * (1.0f - scale)) / 2;
			int diffH = (int) (itemOriRect.height() * (1.0f - scale)) / 2;
			
			itemRect.left = itemOriRect.left + diffW;
			itemRect.right = itemOriRect.right - diffW;
			itemRect.top = itemOriRect.top + diffH;
			itemRect.bottom = itemOriRect.bottom - diffH;
		}
	}

	public class ItemScale extends Scroller {
		int startScale;
		int endScale;
		int currScale;
		int duration;

		public ItemScale(int s, int e, int d) {
			super(getContext(), new DecelerateInterpolator());
			currScale = s;
			startScale = s;
			endScale = e;
			duration = d;
		}

		public ItemScale(ItemScale scale) {
			super(getContext(), new DecelerateInterpolator());
			currScale = scale.startScale;
			startScale = scale.startScale;
			endScale = scale.endScale;
			duration = scale.duration;
		}

		public void explode() {
			if (!isFinished()) {
				forceFinished(true);
			}
			super.startScroll(currScale, 0, endScale - currScale, 0, duration);
		}

		public void collipse() {
			if (!isFinished()) {
				forceFinished(true);
			}
			super.startScroll(currScale, 0, startScale - currScale, 0, duration);
		}

		@Override
		public boolean computeScrollOffset() {
			boolean hr = super.computeScrollOffset();
			if (hr) {
				currScale = getCurrX();
			}

			return hr;
		}

		public float getScale() {
			return (float) currScale / 100;
		}

	}

	public class ItemAlpha extends Scroller {
		int startAlpha;
		int endAlpha;
		int currAlpha;
		int duration;

		public ItemAlpha(int s, int e, int d) {
			super(getContext(), new DecelerateInterpolator());
			currAlpha = s;
			startAlpha = s;
			endAlpha = e;
			duration = d;
		}

		public ItemAlpha(ItemAlpha alpha) {
			super(getContext(), new DecelerateInterpolator());
			currAlpha = alpha.startAlpha;
			startAlpha = alpha.startAlpha;
			endAlpha = alpha.endAlpha;
			duration = alpha.duration;
		}
		
		public void explode() {
			if (!isFinished()) {
				forceFinished(true);
			}
			super.startScroll(currAlpha, 0, endAlpha - currAlpha, 0, duration);
		}

		public void collipse() {
			if (!isFinished()) {
				forceFinished(true);
			}
			super.startScroll(currAlpha, 0, startAlpha - currAlpha, 0, duration);
		}

		@Override
		public boolean computeScrollOffset() {
			boolean hr = super.computeScrollOffset();
			if (hr) {
				currAlpha = getCurrX();
			}

			return hr;
		}

		public int getAlpha() {
			return currAlpha;
		}
	}
}
