package com.yunos.tvlife.app.widget;
import android.view.View;

public abstract class TvOnViewClickListener implements View.OnClickListener {
	
	boolean isPalyBtnClick = false;
	
	@Override
	public void onClick(View v) {
		synchronized (this) {
			if (isPalyBtnClick) {
				return;
			}
			isPalyBtnClick = true;
		}
		
		//do my work
		onClicked(v);
		
		synchronized (this) {
			isPalyBtnClick = false;
		}
		
	}
	
	public abstract void onClicked(View v);
}
