package com.aliyun.base.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class TabButton extends RadioButton {

	public TabButton(Context context) {
		 this(context, null);
	}
	    
	public TabButton(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
		
	}

	public TabButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
}
