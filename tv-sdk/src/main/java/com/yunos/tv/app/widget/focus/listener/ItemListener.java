package com.yunos.tv.app.widget.focus.listener;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.yunos.tv.app.widget.focus.params.FocusRectParams;

public interface ItemListener {

	public boolean isScale();

	public void setScaleX(float scaleX);

	public float getScaleX();

	public void setScaleY(float scaleY);

	public float getScaleY();

	public FocusRectParams getFocusParams();

	public int getItemWidth();

	public int getItemHeight();
	
	public Rect getManualPadding();
	
	public void drawBeforeFocus(Canvas canvas);
	
	public void drawAfterFocus(Canvas canvas);
	
	public boolean isFinished();
}
