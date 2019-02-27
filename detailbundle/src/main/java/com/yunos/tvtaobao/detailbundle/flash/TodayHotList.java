/**
 *  Copyright (C) 2015 The ALI OS Project
 *
 *  Version     Date            Author
 *  
 *             2015-4-19       lizhi.ywp
 *
 */
package com.yunos.tvtaobao.detailbundle.flash;


import org.json.JSONArray;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;

public class TodayHotList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7009204103936366387L;

	/** 疯抢描述信息 */
	private String desc;

	/** 疯抢列表信息 */
	private ArrayList<GoodsInfo> items;

	/** 文案相关信息 */
	private TodayHotInfo message;

	/** tab 提示标题信息 */
	private ArrayList<String> tabTitle;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public ArrayList<GoodsInfo> getItems() {
		return items;
	}

	public void setItems(ArrayList<GoodsInfo> items) {
		this.items = items;
	}

	public TodayHotInfo getMessage() {
		return message;
	}

	public void setMessage(TodayHotInfo message) {
		this.message = message;
	}

	public ArrayList<String> getTabTitle() {
		return tabTitle;
	}

	public void setTabTitle(ArrayList<String> tabTitle) {
		this.tabTitle = tabTitle;
	}

	public static TodayHotList resolveResponse(JSONObject obj){
		if( null == obj){
			return null;
		}
		TodayHotList todayHotList = new TodayHotList();
		
		todayHotList.setDesc(obj.optString("desc"));
		JSONArray array;
		
		array = obj.optJSONArray("tabTitle");
		int size = ( null != array) ? array.length() : 0;
		if( size > 0){
			ArrayList<String> tabTitle = new ArrayList<String>(size);
			for( int index = 0; index < size; index++){
				tabTitle.add(array.optString(index));
			}
			todayHotList.setTabTitle(tabTitle);
		}
		todayHotList.setMessage(TodayHotInfo.resolveResponse(obj.optJSONObject("message")));
		array = obj.optJSONArray("items");
		size = ( null != array) ? array.length() : 0;
		if( size > 0){
			todayHotList.items = new ArrayList<GoodsInfo>(size);
			GoodsInfo info;
			
			for(int index = 0; index < size; index++){
				info = JsonUtils.resolveResponse(array.optJSONObject(index));
				if( null != info){
					todayHotList.items.add(info);
				}
			}
		}
		
		return todayHotList;
	}

}
