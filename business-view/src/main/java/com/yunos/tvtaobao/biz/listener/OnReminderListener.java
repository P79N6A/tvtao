package com.yunos.tvtaobao.biz.listener;

public interface OnReminderListener {	
	public final static byte RET_FULL = -1;
	public final static byte RET_EXIST = -2;
	public final static byte RET_ERROR = -3;
	public final static byte RET_SUCCESS = 0;
	
	/**
	 * 添加提醒
	 * 
	 * @param itemId 
	 * 		商品的抢购id
	 * @param reminderTime  : format : yyyyMMddHHmmss
	 * 		  提醒时间
	 */
	public byte addReminder(String itemId, String reminderTime);
	
	/**
	 * 取消提醒
	 * 
	 * @param itemId 
	 * 		商品的抢购id
	 */
	public void removeReminder(String itemId);
	
	
	/**
	 * 获取当前设置提醒的状态
	 * 		RET_FULL: 不能添加设置提醒，  其他：可以  
	 */
	public byte getReminderState();
	
	/**
	 * 是否设置了提醒
	 * 
	 * @param itemId 
	 * 		商品的抢购id
	 */
	public boolean hasReminder(String itemId);
	
	/**
	 * 获取服务器的当前时间
	 * 
	 */
	public long getServerCurrentTime();
}
