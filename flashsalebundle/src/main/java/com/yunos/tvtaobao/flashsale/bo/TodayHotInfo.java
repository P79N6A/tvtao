/**
*  Copyright (C) 2015 The ALI OS Project
*
*  Version     Date            Author
*  
*             2015-4-19       lizhi.ywp
*
*/
package com.yunos.tvtaobao.flashsale.bo;

import org.json.JSONObject;

import java.io.Serializable;

public class TodayHotInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -453226443946762758L;
	
	/**图片地址*/
	private String picUrl;
	
	/**文案*/
	private String text;
	
	/**提示信息*/
	private String tip;

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}
	
	public static TodayHotInfo resolveResponse(JSONObject obj){
		if( null == obj){
			return null;
		}
		TodayHotInfo info = new TodayHotInfo();
		info.setPicUrl(obj.optString("picUrl"));
		info.setText(obj.optString("text"));
		info.setTip(obj.optString("tip"));
		return info;
	}
}
