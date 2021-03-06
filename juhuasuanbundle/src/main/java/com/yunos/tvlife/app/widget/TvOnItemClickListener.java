package com.yunos.tvlife.app.widget;

import android.view.View;
import android.widget.AdapterView;

public abstract class TvOnItemClickListener implements AdapterView.OnItemClickListener {

	boolean isPalyBtnClick = false;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		synchronized (this) {
			if (isPalyBtnClick) {
				return;
			}
			isPalyBtnClick = true;
		}

		// do my work
		onItemClicked(parent, view, position, id);

		synchronized (this) {
			isPalyBtnClick = false;
		}

	}

	public abstract void onItemClicked(AdapterView<?> parent, View view, int position, long id);
}
