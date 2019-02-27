package com.yunos.tv.app.widget.Dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

import com.yunos.tv.aliTvSdk.R;
import com.yunos.tv.app.widget.focus.FocusButton;
import com.yunos.tv.app.widget.focus.params.Params;

public class AlertDialogFocusButton extends FocusButton {
	private boolean mIsDeepFocus = false;
	private int mTextUnSelectedColor = 0;
	
	public AlertDialogFocusButton(Context context) {
		super(context);
		init(context);
	}
	
	
	public AlertDialogFocusButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	
	public AlertDialogFocusButton(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	@Override
	public boolean isFocusBackground() {
		return true;
	}
	
	
	@Override
	public boolean isScale() {
		return false;
	}
	
	private void init(Context context){
		mParams = new Params(1.0f, 1.0f, 5, null, true, 5, new DecelerateInterpolator());
		mTextUnSelectedColor = context.getResources().getColor(R.color.tui_text_color_white_50_alpha);
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		if (gainFocus){
			this.setTextColor(Color.WHITE);
		}else{
			this.setTextColor(mTextUnSelectedColor);
		}
	}	
}
