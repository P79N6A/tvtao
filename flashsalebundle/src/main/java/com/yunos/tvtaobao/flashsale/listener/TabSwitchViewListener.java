/**
*  Copyright (C) 2015 The ALI OS Project
*
*  Version     Date            Author
*  
*             2015-5-2       lizhi.ywp
*
*/
package com.yunos.tvtaobao.flashsale.listener;

import android.view.View;

import com.yunos.tvtaobao.flashsale.view.AbstractTabLayout;


/** defined tab switch view must be entitied */
public interface TabSwitchViewListener {
//	/**
//	 * set current tab key
//	 * 
//	 * @param key
//	 *            the current key
//	 */
//	public void setCurrentKey(String key);	
	
	/**
	 * set tab Switch listener
	 * 
	 * @param l
	 *            listener
	 */
	public void setTabSwitchListener(AbstractTabLayout.TabSwitchListener l);

	/**
	 * set tab data
	 * 
	 * @param data
	 *            data
	 */
	public void setObject(Object data);
	
	/**
	 * get tab view
	 * 
	 */
	public View getView();
	
	/**
	 * destory the current view
	 * 
	 */
	public void onDestroy();
}
