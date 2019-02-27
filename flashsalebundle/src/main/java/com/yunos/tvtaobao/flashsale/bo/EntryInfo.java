package com.yunos.tvtaobao.flashsale.bo;

import java.io.Serializable;

public class EntryInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -345973180851502427L;
	private String future_time;
	private double discount_rate;
	private String sys_time;
	private String status;
	private String item_id;
	private String name;
	private String item_pic;

	public String getFutureTime() {
		return future_time;
	}

	public void setFutureTime(String future_time) {
		this.future_time = future_time;
	}

	public double getDiscountRate() {
		return discount_rate;
	}

	public void setDiscountRate(double discount_rate) {
		this.discount_rate = discount_rate;
	}

	public String getSysTime() {
		return sys_time;
	}

	public void setSysTime(String sys_time) {
		this.sys_time = sys_time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getItemId() {
		return item_id;
	}

	public void setItemId(String item_id) {
		this.item_id = item_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getItemPic() {
		return item_pic;
	}

	public void setItemPic(String item_pic) {
		this.item_pic = item_pic;
	}
	/** 记录当前获取时系统时间 */
	private long mLocalRef;

	public void setLocalRef(long ref) {
		mLocalRef = ref;
	}

	public long getLocalRef() {
		return mLocalRef;
	}

	public void setFuture_time(String future_time) {
		this.future_time = future_time;
	}

	public void setDiscount_rate(double discount_rate) {
		this.discount_rate = discount_rate;
	}

	public void setSys_time(String sys_time) {
		this.sys_time = sys_time;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public void setItem_pic(String item_pic) {
		this.item_pic = item_pic;
	}
}
