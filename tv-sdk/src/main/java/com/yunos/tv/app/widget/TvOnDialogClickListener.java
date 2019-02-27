package com.yunos.tv.app.widget;
import android.content.DialogInterface;

public abstract class TvOnDialogClickListener implements DialogInterface.OnClickListener {
	
	boolean isPalyBtnClick = false;
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		synchronized (this) {
			if (isPalyBtnClick) {
				return;
			}
			isPalyBtnClick = true;
		}
		
		//do my work
		onClicked(dialog, which);
		
		synchronized (this) {
			isPalyBtnClick = false;
		}
		
	}
	
	public abstract void onClicked(DialogInterface dialog, int which);
}
