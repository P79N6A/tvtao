package com.yunos.tv.app.widget;

import android.graphics.drawable.Drawable;

import com.yunos.tv.app.widget.ExplodeLayout.DrawingItem;

public interface ExplodeLayoutAdapter {

	public Size getDrawingSize();

	public float getExplodeScale();

	public int getDrawingCount();

	public DrawingItem getDrawingItem();

	public Drawable getForeground();

	public class Size {
		int height;
		int width;

		public Size(int w, int h) {
			height = h;
			width = w;
		}
	}

	
}
