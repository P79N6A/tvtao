package com.yunos.tv.app.widget.focus.listener;

import android.graphics.Canvas;
import android.graphics.Rect;

public interface DrawListener {
	public boolean isDynamicFocus();
	
	public void setRect(Rect r);
	
	public void setRadius(int r);
	
	public void start();
	
	public void stop();
	
	public void setVisible(boolean visible);
	
	public void setAlpha(float alpha);
	
	public void draw(Canvas canvas);
}
