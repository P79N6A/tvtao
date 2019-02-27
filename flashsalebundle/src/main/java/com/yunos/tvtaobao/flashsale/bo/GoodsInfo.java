/**
*  Copyright (C) 2015 The ALI OS Project
*
*  Version     Date            Author
*  
*             2015-4-4       lizhi.ywp
*
*/
package com.yunos.tvtaobao.flashsale.bo;

import com.yunos.tvtaobao.biz.request.bo.RebateBo;

import java.io.Serializable;

public class GoodsInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6464843790510349172L;
	public final static byte ITEM_TYPE_STOCK_INFO = 0;
	public final static byte ITEM_TYPE_SECKILL_INFO = 1;
	
	public final static byte ITEM_TYPE_MAX = 2;

	/** 商品抢购ID */
	private String id;

	/** 商品id */
	private String itemId;

	/** 商品名称 */
	private String name;
	
	/** 原始价格 */
	private double price;
	
	/** 销售价格 */
	private double salePrice;

	/** 商品的地址 */
	private String url;
	
	/** 商品的图片地址 */
	private String picUrl;
	
	/** 商品类型 */
	private int itemType;
	
	/** 将要销售的产品 */
	private boolean future;
	
	/** 是否显示库存率 */
	private boolean showStockPercent;
	
	/** 销售数量 */
	private int itemSoldNum;
	
	/**销售信息*/
	private String itemSoldInfo;
	
	/** 销售完文案信息 */
	private String soldOutMessage;
	
	/** 商品的抢购率 */
	private double soldRate;
	

	/** 闹钟提前多少秒提醒 */
	private long remindTime;
	
	/** 开始时间 */	
	private String startTime;
	
	/** 结束时间，给闹钟使用 */
	private String endTime;
	
	/**关注人数*/
	private int itemViewerNum;
	

	/** 剩余商品数量,3.0没有此字段 */
	private int remainingStock;

	//返利相关数据
	private RebateBo rebateBo;
	
	
	public int getItemViewerNum() {
		return itemViewerNum;
	}

	public void setItemViewerNum(int itemViewerNum) {
		this.itemViewerNum = itemViewerNum;
	}

	public String getSeckillId() {
		return id;
	}

	public void setSeckillId(String seckillId) {
		this.id = seckillId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public boolean isShowStockPercent() {
		return showStockPercent;
	}

	public void setShowStockPercent(boolean showStockPercent) {
		this.showStockPercent = showStockPercent;
	}

	public double getStockPercent() {
		return soldRate;
	}

	public void setStockPercent(double soldRate) {
		this.soldRate = soldRate;
	}


	public int getSoldNum() {
		return itemSoldNum;
	}

	public void setSoldNum(int itemSoldNum) {
		this.itemSoldNum = itemSoldNum;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isFuture() {
		return future;
	}

	public void setIsFuture(boolean isFuture) {
		this.future = isFuture;
	}

	public double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSoldOutInfo() {
		return soldOutMessage;
	}

	public void setSoldOutInfo(String soldOutInfo) {
		this.soldOutMessage = soldOutInfo;
	}

	public int getType() {
		return itemType;
	}

	public void setType(int type) {
		itemType = type;
	}
	public long getRemindTime() {
		return remindTime;
	}

	public void setRemindTime(long remindTime) {
		this.remindTime = remindTime;
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

	
	public int getRemainingNum() {
		return remainingStock;
	}

	public void setRemainingNum(int remainingNum) {
		this.remainingStock = remainingNum;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setItemType(int itemType) {
		this.itemType = itemType;
	}

	public void setFuture(boolean future) {
		this.future = future;
	}

	public void setItemSoldNum(int itemSoldNum) {
		this.itemSoldNum = itemSoldNum;
	}

	public void setItemSoldInfo(String itemSoldInfo) {
		this.itemSoldInfo = itemSoldInfo;
	}

	public void setSoldOutMessage(String soldOutMessage) {
		this.soldOutMessage = soldOutMessage;
	}

	public void setSoldRate(double soldRate) {
		this.soldRate = soldRate;
	}

	public void setRemainingStock(int remainingStock) {
		this.remainingStock = remainingStock;
	}

	public String getId() {
		return id;
	}

	public int getItemType() {
		return itemType;
	}

	public int getItemSoldNum() {
		return itemSoldNum;
	}

	public String getItemSoldInfo() {
		return itemSoldInfo;
	}

	public String getSoldOutMessage() {
		return soldOutMessage;
	}

	public double getSoldRate() {
		return soldRate;
	}

	public int getRemainingStock() {
		return remainingStock;
	}

	public RebateBo getRebateBo() {
		return rebateBo;
	}

	public void setRebateBo(RebateBo rebateBo) {
		this.rebateBo = rebateBo;
	}

	public static void copyGoodsInfo(GoodsInfo dest, GoodsInfo src){
		if(null != dest && null != src){
//			dest.endTime = src.endTime;
			dest.future = src.future;
			dest.id = src.id;
			dest.itemId = src.itemId;
			dest.itemSoldInfo = src.itemSoldInfo;
			dest.itemSoldNum = src.itemSoldNum;
			dest.itemType = src.itemType;
			dest.itemViewerNum = src.itemViewerNum;
			dest.name = src.name;
			dest.picUrl = src.picUrl;
			dest.price = src.price;
			dest.remainingStock = src.remainingStock;
			dest.remindTime = src.remindTime;
			dest.salePrice = src.salePrice;
			dest.showStockPercent = src.showStockPercent;
			dest.soldOutMessage = src.soldOutMessage;
			dest.soldRate = src.soldRate;
//			dest.startTime = src.startTime;
			dest.url = src.url;
		}
	}
}
