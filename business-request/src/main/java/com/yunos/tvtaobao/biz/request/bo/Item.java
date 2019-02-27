/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author tianxiang
 * @date 2012-10-18 下午5:57:09
 */
public class Item implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5315141269862627730L;

	// id
	private Long itemNumId;

	// 名称
	private String title;

	// 原价
	private Long price;

	// 物品状态，全新或二手
	private String stuffStatus;

	// 销售状态，正常，下架，删除，从未上架，CC，已售完
	private String itemStatus;

	// 产地，如"杭州"
	private String location;

	// 图片路径
	private String[] picsPath;

	// 30天内销售量
	private Integer totalSoldQuantity;

	// 评价数量
	private Integer evaluateCount;

	// 宝贝库存。当宝贝为非sku商品时，使用该值代表库存；当宝贝为sku商品时，该值只做为参考，具体sku对应的库存见sku库存
	private Integer quantity;

	// 是否已下架
	private Boolean soldout;

	// 收藏人气
	private Integer favcount;

	// 天猫宝贝的平均得分，如4.7, 只有天猫宝贝才有该得分
	private Double itemGradeAvg;

	// 是否为sku商品，true表示sku商品，false表示非sku商品
	private Boolean sku;

	// wap版本宝贝的详情URL
	private String itemUrl;
	
    private Tag tag;

    private String category;

    //4.2内容详细链接
    private String fullDescUrl;

    public String getFullDescUrl() {
        return fullDescUrl;
    }


    public void setFullDescUrl(String fullDescUrl) {
        this.fullDescUrl = fullDescUrl;
    }

	public Long getItemNumId() {
		return itemNumId;
	}

	public void setItemNumId(Long itemNumId) {
		this.itemNumId = itemNumId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public String getStuffStatus() {
		return stuffStatus;
	}

	public void setStuffStatus(String stuffStatus) {
		this.stuffStatus = stuffStatus;
	}

	public String getItemStatus() {
		return itemStatus;
	}

	public void setItemStatus(String itemStatus) {
		this.itemStatus = itemStatus;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getTotalSoldQuantity() {
		return totalSoldQuantity;
	}

	public void setTotalSoldQuantity(Integer totalSoldQuantity) {
		this.totalSoldQuantity = totalSoldQuantity;
	}

	public Integer getEvaluateCount() {
		return evaluateCount;
	}

	public void setEvaluateCount(Integer evaluateCount) {
		this.evaluateCount = evaluateCount;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Boolean getSoldout() {
		return soldout;
	}

	public void setSoldout(Boolean soldout) {
		this.soldout = soldout;
	}

	public Integer getFavcount() {
		return favcount;
	}

	public void setFavcount(Integer favcount) {
		this.favcount = favcount;
	}

	public Double getItemGradeAvg() {
		return itemGradeAvg;
	}

	public void setItemGradeAvg(Double itemGradeAvg) {
		this.itemGradeAvg = itemGradeAvg;
	}

	public Boolean getSku() {
		return sku;
	}

	public void setSku(Boolean sku) {
		this.sku = sku;
	}

	public String getItemUrl() {
		return itemUrl;
	}

	public void setItemUrl(String itemUrl) {
		this.itemUrl = itemUrl;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public String[] getPicsPath() {
		return picsPath;
	}

	public void setPicsPath(String[] picsPath) {
		this.picsPath = picsPath;
	}

	public static Item resolveFromMTOP(JSONObject obj) throws JSONException {
		if (obj == null) {
			return null;
		}

		Item item = new Item();
		if (!obj.isNull("evaluateCount")) {
			item.setEvaluateCount(obj.getInt("evaluateCount"));
		}

		if (!obj.isNull("favcount")) {
			item.setFavcount(obj.getInt("favcount"));
		}

		if (!obj.isNull("itemGradeAvg")) {
			item.setItemGradeAvg(obj.getDouble("itemGradeAvg"));
		}

		if (!obj.isNull("itemNumId")) {
			item.setItemNumId(obj.getLong("itemNumId"));
		}

		if (!obj.isNull("itemStatus")) {
			item.setItemStatus(obj.getString("itemStatus"));
		}

		if (!obj.isNull("itemUrl")) {
			item.setItemUrl(obj.getString("itemUrl"));
		}

		if (!obj.isNull("location")) {
			item.setLocation(obj.getString("location"));
		}

		if (!obj.isNull("picsPath")) {
			JSONArray array = obj.getJSONArray("picsPath");
			String[] temp = new String[array.length()];
			for (int i = 0; i < array.length(); i++) {
				temp[i] = array.getString(i);
			}
			item.setPicsPath(temp);
		}

		if (!obj.isNull("price")) {
			item.setPrice(obj.getLong("price"));
		}

		if (!obj.isNull("quantity")) {
			item.setQuantity(obj.getInt("quantity"));
		}

		if (!obj.isNull("sku")) {
			item.setSku(obj.getBoolean("sku"));
		}

		if (!obj.isNull("soldout")) {
			item.setSoldout(obj.getBoolean("soldout"));
		}

		if (!obj.isNull("stuffStatus")) {
			item.setStuffStatus(obj.getString("stuffStatus"));
		}

		if (!obj.isNull("title")) {
			item.setTitle(obj.getString("title"));
		}

		if (!obj.isNull("totalSoldQuantity")) {
			item.setTotalSoldQuantity(obj.getInt("totalSoldQuantity"));
		}

		if (!obj.isNull("tag")) {
			item.setTag(Tag.resolveFromMTOP(obj.getJSONObject("tag")));
		}
		if (!obj.isNull("fullDescUrl")) {
		    item.setFullDescUrl(obj.getString("fullDescUrl"));
		}
		if (!obj.isNull("category")) {
		    item.setFullDescUrl(obj.getString("category"));
		}

		return item;
	}
	
	public String getItemBuyStatus() {
	    if (getSoldout()) {
            return "已下架";
        }
	    if ("正常".equals(itemStatus)) {
            return "立即购买";
        }
        if ("下架".equals(itemStatus)) {
            return "已下架";
        }
        if ("删除".equals(itemStatus)) {
            return "已删除";
        }
        if ("从未上架".equals(itemStatus)) {
            return "即将开始";
        }
        if ("CC".equals(itemStatus)) {
            return "即将开始";
        }
        if ("已售完".equals(itemStatus)) {
            return "已卖光";
        }
        return "立即购买";
    }
    
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
