/**
 *  Copyright (C) 2015 The ALI OS Project
 *
 *  Version     Date            Author
 *  
 *             2015-4-19       lizhi.ywp
 *
 */
package com.yunos.tvtaobao.flashsale.adapter;

import android.content.Context;
import android.view.View;

import com.yunos.tv.app.widget.focus.FocusHListView;
import com.yunos.tv.app.widget.focus.listener.FocusStateListener;
import com.yunos.tvtaobao.flashsale.R;
import com.yunos.tvtaobao.flashsale.bo.RecommendInfo;

public class RecommendAdapter extends BaseItemAdapter<RecommendInfo> {
	private FocusHListView mFocusHListView;

	public RecommendAdapter(Context context, FocusHListView listView) {
		super(context);
		// TODO Auto-generated constructor stub
		mFocusHListView = listView;
		setMaskAlpha(1.0f, 0.9f);
		mFocusHListView.setOnFocusStateListener(new FocusStateListener() {
			@Override
			public void onFocusStart(View v, View parent) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFocusFinished(View v, View parent) {
				// TODO Auto-generated method stub
				int firstIndex, lastIndex;
				View selectView = mFocusHListView.getSelectedView();

				firstIndex = mFocusHListView.getFirstVisiblePosition();
				lastIndex = mFocusHListView.getLastVisiblePosition();
				for (int index = firstIndex; index <= lastIndex; index++) {
					View child = mFocusHListView.getChildAt(index - firstIndex);
					if (child == selectView) {
						child.setAlpha(mFocusAlpha);
					} else {
						child.setAlpha(mUnfocusAlpha);
					}

				}
			}
		});
	}

	@Override
	public View createViewAndCallback(RecommendInfo info, int position) {
		// TODO Auto-generated method stub
		return mInflater.inflate(R.layout.fs_recmmend_item, null);
	}

	@Override
	protected void OnInit() {
		super.OnInit();
	}

}
