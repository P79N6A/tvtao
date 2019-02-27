package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;
import java.util.ArrayList;

public class BrandMO implements Serializable{
	/**
     * 
     */
    private static final long serialVersionUID = 4767298014606039748L;
    private String code;
	private String name;
	private String logo;
	private String brandDes;
	private boolean isFollow;
	private String firstLatter;
	private String infoUrl;
	private String background;
	/**
	 * 主背景图：现在的策略是去第一个商品的第一个图
	 */
	private String mainPicUrl;
	private ArrayList<ItemMO> itemList;

	@Override
	public String toString() {
		return "BrandMO [code=" + code + ", name=" + name + ", logo=" + logo
				+ ", brandDes=" + brandDes + ", isFollow=" + isFollow
				+ ", firstLatter=" + firstLatter + ", infoUrl=" + infoUrl
				+ ", background=" + background + ", itemList=" + itemList + "]";
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getBrandDes() {
		return brandDes;
	}

	public void setBrandDes(String brandDes) {
		this.brandDes = brandDes;
	}

	// public boolean isFollow() {
	// return isFollow;
	// }
	// public void setFollow(boolean isFollow) {
	// this.isFollow = isFollow;
	// }

	public String getFirstLatter() {
		return firstLatter;
	}

	public void setFirstLatter(String firstLatter) {
		if (firstLatter != null) {
			this.firstLatter = firstLatter.toUpperCase();
		}
	}

	public String getInfoUrl() {
		return infoUrl;
	}

	public void setInfoUrl(String infoUrl) {
		this.infoUrl = infoUrl;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	/**
	 * 起初策略：取第一个商品第一张图
	 * @return
	 */
	public String getMainPicUrl() {
		return mainPicUrl;
	}

	public void setMainPicUrl(String mainPicUrl) {
		this.mainPicUrl = mainPicUrl;
	}

	public ArrayList<ItemMO> getItemList() {
		return itemList;
	}

	public void addItem(ItemMO item) {
		if (item != null) {
			if (itemList == null) {
				itemList = new ArrayList<ItemMO>();
			}
			itemList.add(item);
		}
	}

}
