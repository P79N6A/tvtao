package com.yunos.tv.app.widget.graphics;

import android.graphics.Paint;

public interface PainterInterface {
	public boolean isLayoutRequested();

	public void invalidate();
	
	public void postInvalidate();
	
	public void setLayerType(int layerType, Paint paint);
}
