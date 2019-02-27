package com.yunos.tv.app.widget.focus.listener;

import android.graphics.Rect;

public interface DeepListener extends FocusListener{
	public boolean canDeep();
	
	public boolean hasDeepFocus();
	
	void onFocusDeeped(boolean gainFocus, int direction, Rect previouslyFocusedRect);
	
	public void onItemSelected(boolean selected);
	
	public void onItemClick();
}
