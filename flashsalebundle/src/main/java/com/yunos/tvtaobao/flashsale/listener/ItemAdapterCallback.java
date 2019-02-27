package com.yunos.tvtaobao.flashsale.listener;

/**
*  Copyright (C) 2015 The ALI OS Project
*
*  Version     Date            Author
*  
*             2015-4-19       lizhi.ywp
*
*/

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.yunos.tv.core.common.ImageLoaderManager;

public interface ItemAdapterCallback<T> {
	/**
	 * This method will be invoked when the adapter view need to update
	 * 
	 * @param imageLoader
	 *           the image loader
	 * @param option
	 *           the image option       
	 * @param data
	 *           the displaying data        
	 * @param userData
	 *           the user data of the displaying
	 * 
	 */
	public void display(ImageLoaderManager imageLoader,
                        DisplayImageOptions option, T data, Object userData, boolean isScroll);
}
