package com.yunos.tvtaobao.flashsale.bo;

import java.io.Serializable;

public class RecommendInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2374776240406900593L;
	private String title;
	private String picUrl;
	private String source;
	private double salePrice;	
	private String itemId;
	private String url;
	private String scm;
	private double reservePrice;	
	private int sold;	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public double getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getScm() {
		return scm;
	}
	public void setScm(String scm) {
		this.scm = scm;
	}
	public double getReservePrice() {
		return reservePrice;
	}
	public void setReservePrice(double reservePrice) {
		this.reservePrice = reservePrice;
	}
	public int getSold() {
		return sold;
	}
	public void setSold(int sold) {
		this.sold = sold;
	}
	
}
