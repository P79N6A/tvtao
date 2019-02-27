/**
*  Copyright (C) 2015 The ALI OS Project
*
*  Version     Date            Author
*  
*             2015-4-4       lizhi.ywp
*
*/
package com.yunos.tvtaobao.flashsale.bo;

import java.io.Serializable;

public class CategoryItem implements Serializable {
	public final static String STATUS_PAST = "past"; //过去场
	public final static String STATUS_CURRENT = "current"; // 当前场
	public final static String STATUS_FUTRUE = "future"; // 预热场

	public final static byte STATUS_TYPE_PAST = 0; //过去场
	public final static byte STATUS_TYPE_CURRENT = 1; //过去场
	public final static byte STATUS_TYPE_FUTRUE = 2; //过去场
	/**
	 * 
	 */
	private static final long serialVersionUID = 7975381662539107596L;

	/** 抢购场次列表ID */
	private String id;

	/** 抢购场次标题 */
	private String title;

	/** 抢购场次状态 */
	private String status;

	/** 状态描述信息 */
	private String statusDesc;

	/** 抢购场次开始时间 */
	private String startTime;

	/** 抢购场次结束时间 */
	private String endTime;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}



}
