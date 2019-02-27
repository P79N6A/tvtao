/**
*  Copyright (C) 2015 The ALI OS Project
*
*  Version     Date            Author
*  
*             2015-5-2       lizhi.ywp
*
*/
package com.yunos.tvtaobao.flashsale.listener;

public interface TimerListener {
	/**
	 * This method will be invoked when the timer is arrived 
	 * 
	 * @param id
	 *           id of the timer
	 * @param remainingTime
	 *            the remaining time of the current timer
	 * @param userData
	 *           the user data of the timer
	 * 
	 */
	public void onTimer(int id, long remainingTime, Object userData);
	
	
	/**
	 * This method will be invoked when the timer is end 
	 * 
	 * @param id
	 *           id of the timer
	 * @param userData
	 *           the user data of the timer
	 * 
	 */
	public void onEndTimer(int id, Object userData);
}
