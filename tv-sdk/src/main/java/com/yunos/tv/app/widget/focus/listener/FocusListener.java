package com.yunos.tv.app.widget.focus.listener;

import android.graphics.Rect;
import android.view.KeyEvent;

import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;

public interface FocusListener {
	public FocusRectParams getFocusParams();

	public boolean canDraw();

	public boolean isAnimate();

	public ItemListener getItem();

	public boolean isScrolling();

	public Params getParams();

	public boolean isFocusBackground();

	public boolean preOnKeyDown(int keyCode, KeyEvent event);

	public boolean onKeyDown(int keyCode, KeyEvent event);

	public boolean onKeyUp(int keyCode, KeyEvent event);

	public void onFocusStart();

	public void onFocusFinished();
	
	/**
	 * @return Rect, the Rectangle coordinates of the current which you want to clip the Focus.
	 * */
	public Rect getClipFocusRect();
}
