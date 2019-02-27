package com.yunos.tv.app.widget.inputmethod;

import android.inputmethodservice.Keyboard.Key;
import android.widget.PopupWindow;

import com.yunos.tv.app.widget.inputmethod.FocusKeyboardView.OnKeyboardActionListener;

public interface IKeyboardExpand {
	public void setKeyboardExpand(PopupWindow popupWindow, Key key, OnKeyboardActionListener listener);
}
