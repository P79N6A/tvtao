package com.yunos.tv.app.widget.focus.listener;

import android.view.View;

public interface FocusStateListener {
	public void onFocusStart(View v, View parent);
	public void onFocusFinished(View v, View parent);
}
