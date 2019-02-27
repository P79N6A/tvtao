package com.aliyun.base.ui;

import android.content.Context;
import android.text.Spannable;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class HyperTextView extends TextView {
	
	public HyperTextView(Context context) {
		 this(context, null);
	}
	
	public HyperTextView(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}

	public HyperTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, -1);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (getMovementMethod() != null) {
			return getMovementMethod().onTouchEvent(this, (Spannable) getText(), event);
		}
		return super.onTouchEvent(event);
	}
	
	public void setHyperMovementMethod(MovementMethod movement) {
        super.setMovementMethod(movement);
		setFocusable(false);
		setClickable(false);
		setLongClickable(false);
    }
}
