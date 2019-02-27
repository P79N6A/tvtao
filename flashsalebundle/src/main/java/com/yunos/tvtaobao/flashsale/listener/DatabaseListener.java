package com.yunos.tvtaobao.flashsale.listener;

public interface DatabaseListener {
	
	/**
	 * 查询结果码
	 */
	public static final byte QUERY_UNDO = 0;    //未开始
	public static final byte QUERY_DOING = 1;    //查询中
	public static final byte QUERY_DONE = 2;    //查询成功
	
	/**
	 * 查询数据库接口返回后执行
	 * @param queryState
	 */
	void onQueryDone(byte queryState);

}
