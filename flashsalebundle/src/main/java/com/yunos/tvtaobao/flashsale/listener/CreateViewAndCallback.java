/**
*  Copyright (C) 2015 The ALI OS Project
*
*  Version     Date            Author
*  
*             2015-4-19       lizhi.ywp
*
*/
package com.yunos.tvtaobao.flashsale.listener;

import android.view.View;

public interface CreateViewAndCallback<T>{
	
	/**
	 * This method will be invoked when the item view need to be created 
	 * 
	 * @param info
	 *           the item information
	 * @param position
	 *            the current position
	 * 
	 */
	public View OnCreateViewAndCallback(T info, int position);
}
