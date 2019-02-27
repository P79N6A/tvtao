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

public interface TabGridViewListener {
	/**
	 * This method will be invoked when the view had been gone
	 * 
	 */
	public void onUnselect();
	
	/**
	 * This method will be invoked when the view had been visible
	 * 
	 */
	public void onSelect();
	
	
	/**
	 * get tab view
	 * 
	 */
	public View getView();
	
	/**
	 * set tab data
	 * 
	 * @param data
	 *            data
	 */
	public void setObject(Object data, Object userData);
	
	/**
	 * This method will be invoked before exit interface 
	 * 
	 */
	public void onDestroy();
	
}
