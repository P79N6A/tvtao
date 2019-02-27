/**
 *  Copyright (C) 2015 The ALI OS Project
 *
 *  Version     Date            Author
 *  
 *             2015-4-19       lizhi.ywp
 *
 */
package com.yunos.tvtaobao.flashsale.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.yunos.tvtaobao.flashsale.listener.FlipperItemListener;


public class FocusFlipperView extends AnimatorView implements
		AnimatorView.OnViewChangeListener {

	protected static String TAG = "FocusViewFlipper";

	public FocusFlipperView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public FocusFlipperView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public FocusFlipperView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void OnViewChange(View selectView, int selectPos) {
		if (selectView instanceof FlipperItemListener) {
			FlipperItemListener l = (FlipperItemListener) selectView;
			l.OnViewChange(selectView, selectPos);
		}
	}

	@Override
	protected void onPageWillUnselected(View selectView, int selectPos,
                                        View unselectView, int unselectPos) {
		super.onPageWillUnselected(selectView, selectPos, unselectView,
				unselectPos);
		if (unselectView instanceof FlipperItemListener) {
			FlipperItemListener l = (FlipperItemListener) unselectView;

			l.onPageWillUnselected(selectView, selectPos, unselectView,
					unselectPos);
		}
	}

	@Override
	protected void onPageUnselected(View unselectView, int unselectPos) {
		super.onPageUnselected(unselectView, unselectPos);
		if (unselectView instanceof FlipperItemListener) {
			FlipperItemListener l = (FlipperItemListener) unselectView;

			l.onPageUnselected(unselectView, unselectPos);
		}
	}

	@Override
	protected void onPageWillSelected(View selectView, int selectPos,
                                      View unselectView, int unselectPos) {
		super.onPageWillSelected(selectView, selectPos, unselectView,
				unselectPos);
		if (selectView instanceof FlipperItemListener) {
			FlipperItemListener l = (FlipperItemListener) selectView;

			l.onPageWillSelected(selectView, selectPos, unselectView,
					unselectPos);
		}
	}

	@Override
	protected void onPageSelected(View selectView, int selectPos) {
		super.onPageSelected(selectView, selectPos);

		if (selectView instanceof FlipperItemListener) {
			FlipperItemListener l = (FlipperItemListener) selectView;

			l.onPageSelected(selectView, selectPos);
		}
	}

	@Override
	protected void initView() {
		super.setOnViewChangeListener(this);
	}

	public FliperItemView getFliperItemView(byte type) {
		int size = super.getChildCount();
		for (int index = 0; index < size; index++) {
			FliperItemView v = (FliperItemView) super.getChildAt(index);
			if (v.getPageType() == type) {
				return v;
			}
		}
		return null;
	}

	public void onDestroy() {
		int size = super.getChildCount();
		for (int index = 0; index < size; index++) {
			FliperItemView v = (FliperItemView) super.getChildAt(index);
			if (null != v) {
				v.onDestroy();
			}
		}
//		super.removeAllViews();
	}

	public void onResume() {
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View v = getChildAt(i);
			if (null != v && v.getVisibility() == View.VISIBLE
					&& v instanceof FliperItemView) {
				FliperItemView itemView = (FliperItemView) v;
				itemView.onResume();
			}
		}
	}
}
