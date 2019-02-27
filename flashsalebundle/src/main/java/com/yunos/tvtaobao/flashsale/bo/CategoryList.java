/**
 *  Copyright (C) 2015 The ALI OS Project
 *
 *  Version     Date            Author
 *  
 *             2015-4-4       lizhi.ywp
 *
 */
package com.yunos.tvtaobao.flashsale.bo;

import android.text.TextUtils;


import com.yunos.tv.core.common.AppDebug;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CategoryList implements Serializable {

	private final static  String TAG =  "CategoryList";
	/**
	 * 
	 */
	private static final long serialVersionUID = 686423214403716900L;

	/** 服务端当前计算时间，用来计算倒计时 */
	private String sysTime;

	/** 记录所有抢购场次信息 */
	private ArrayList<CategoryItem> batchs;

	/** 记录当前获取时系统时间 */
	private long mLocalRef;
	
	public CategoryItem getLastItem(){
		if (null != batchs) {
			int size = batchs.size();
			if( size > 0){
				return batchs.get(size - 1);
			}
		}
		return null;
	}
	
	public CategoryItem getCurItem() {
		if (null != batchs) {
			for (CategoryItem item : batchs) {
				if (TextUtils.equals(CategoryItem.STATUS_CURRENT,
						item.getStatus())) {
					return item;
				}else if(TextUtils.equals(CategoryItem.STATUS_FUTRUE,
						item.getStatus()) ){
					/**如果没有当前场次，则把下一个作为当前长*/
						AppDebug.i(TAG,"getCurItem: the futrue time is setted by the current time");
					item.setStatus(CategoryItem.STATUS_CURRENT);
					return item;
				}
			}
		}
		return null;
	}

	public CategoryItem getItem(String key) {
		if (null != batchs) {
			for (CategoryItem item : batchs) {
				if (TextUtils.equals(key, item.getId())) {
					return item;
				}
			}
		}
		return null;
	}

	public void setLocalRef(long ref) {
		mLocalRef = ref;
	}

	public long getLocalRef() {
		return mLocalRef;
	}

	public String getSysTime() {
		return sysTime;
	}

	public void setSysTime(String sysTime) {
		this.sysTime = sysTime;
	}

	public List<CategoryItem> getItems() {
		return batchs;
	}

	public void setBatchs(ArrayList<CategoryItem> batchs) {
		this.batchs = batchs;
	}

}
