package com.yunos.tv.app.widget.focus.listener;

import android.graphics.Canvas;

public interface FocusDrawStateListener {
	public void drawBeforFocus(Canvas canvas);

	public void drawAfterFocus(Canvas canvas);
}
