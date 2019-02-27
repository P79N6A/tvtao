package com.yunos.tv.app.widget.focus.listener;

import android.view.View;

/**
 * 焦点选中状态监听器
 * 
 */
public interface ItemSelectedListener {
	/**
	 * 选中状态变化回调
	 * 
	 * @param v
	 *            选中的元素
	 * @param position
	 *            元素对应的位置
	 * @param isSelected
	 *            元素是否选中
	 * @param view
	 *            父容器
	 */
	public void onItemSelected(View v, int position, boolean isSelected, View view);
}