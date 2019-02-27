package com.yunos.tv.app.widget.focus.listener;

import android.graphics.Rect;
import android.view.View;

public interface PositionListener {

	public void offsetDescendantRectToItsCoords(View descendant, Rect rect);

	public void invalidate();
	
	public void postInvalidate();
	
	public void postInvalidateDelayed(long delayMilliseconds);

	public void reset();
}
