/**
 * $
 * PROJECT NAME: TopicBuy
 * PACKAGE NAME: com.yunos.tv.topicbuy.bo
 * FILE NAME: ItemTejia.java
 * CREATED TIME: 2014-8-25
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
package com.yunos.tvtaobao.zhuanti.bo;


import android.text.Html;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/***
 * 天天特价商品信息
 * @version
 * @author yunzhong.qyz
 * @data 2014-8-25 下午4:49:32
 * @modify hanqi
 */
public class ItemTejia implements Serializable {

    private static final long serialVersionUID = -4055573571295171034L;

    // 商品id
    private String itemId;
    // 商品标题
    private String title;
    // 商品的短标题
    private String shortTitle;
    // 商品链接
    private String itemUrl;
    // 商品价格,标准格式(小数点后两位小数)
    private String price;
    // 商品打折后价格,标准格式(小数点后两位小数)
    private String discountPrice;
    // 图片链接地址,全地址
    private String imageUrl;
    // 商品折扣率,保证discount=(discountPrice/price)*10
    private String discount;
    // 活动开始时间
    private String startTime;
    // 活动结束时间
    private String endTime;
    // 商品的品牌
    private String brandName;
    // 商品的状态（等待活动开始：0， 活动进行中：1， 已经卖光：4或5，活动已经结束：9
    private String status;
    // 商品现在的库存
    private String currentQuantity;
    // 商品总的库存
    private String quantity;
    // 商品卖出的数目
    private String sellOutCount;
    // 卖家的id
    private String sellerId;
    // 卖家的昵称
    private String sellerNick;
    // 活动的特性信息
    private String feature;

    /***
     * @param obj
     * @return
     * @throws JSONException
     */
    public static ItemTejia fromMtop(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }

        ItemTejia itemTejia = null;
        try {
            itemTejia = new ItemTejia();
            itemTejia.setItemId(obj.optString("itemId")); 
            
            if (!obj.isNull("title")) {
                String title = obj.optString("title");
                //替换换行符
                title = title.replaceAll("\r\n", " ").replaceAll("\n", " ").replaceAll("\r", " ");
                title = Html.fromHtml(title).toString();
                itemTejia.setTitle(title);
            }
            
            if (!obj.isNull("shortTitle")) {
                String title = obj.optString("shortTitle");
                //替换换行符
                title = title.replaceAll("\r\n", " ").replaceAll("\n", " ").replaceAll("\r", " ");
                title = Html.fromHtml(title).toString();
                itemTejia.setShortTitle(title);
            }
            
            itemTejia.setItemUrl(obj.optString("itemUrl"));
            itemTejia.setPrice(obj.optString("price"));
            itemTejia.setDiscountPrice(obj.optString("discountPrice"));
            itemTejia.setImageUrl(obj.optString("imageUrl"));
            itemTejia.setDiscount(obj.optString("discount"));
            itemTejia.setStartTime(obj.optString("startTime"));
            itemTejia.setEndTime(obj.optString("endTime"));
            itemTejia.setBrandName(obj.optString("brandName"));
            itemTejia.setStatus(obj.optString("status"));
            itemTejia.setCurrentQuantity(obj.optString("currentQuantity"));
            itemTejia.setQuantity(obj.optString("quantity"));
            itemTejia.setSellOutCount(obj.optString("sellOutCount"));
            itemTejia.setSellerId(obj.optString("sellerId"));
            itemTejia.setSellerNick(obj.optString("sellerNick"));
            itemTejia.setFeature(obj.optString("feature"));
        } catch (Exception e) {
            itemTejia = null;
            e.printStackTrace();
        }

        return itemTejia;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ItemTejia [itemId=" + itemId + ", title=" + title + ", shortTitle=" + shortTitle + ", itemUrl="
                + itemUrl + ", price=" + price + ", discountPrice=" + discountPrice + ", imageUrl=" + imageUrl
                + ", discount=" + discount + ", startTime=" + startTime + ", endTime=" + endTime + ", brandName="
                + brandName + ", status=" + status + ", currentQuantity=" + currentQuantity + ", quantity=" + quantity
                + ", sellOutCount=" + sellOutCount + ", sellerId=" + sellerId + ", sellerNick=" + sellerNick
                + ", feature=" + feature + "]";
    }

    /**
     * @return
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * @param itemId
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return
     */
    public String getShortTitle() {
        return shortTitle;
    }

    /**
     * @param shortTitle
     */
    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    /**
     * @return
     */
    public String getItemUrl() {
        return itemUrl;
    }

    /**
     * @param itemUrl
     */
    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    /**
     * @return
     */
    public String getPrice() {
        return price;
    }

    /**
     * @param price
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     * @return
     */
    public String getDiscountPrice() {
        return discountPrice;
    }

    /**
     * @param discountPrice
     */
    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    /**
     * @return
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * @param imageUrl
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * @return
     */
    public String getDiscount() {
        return discount;
    }

    /**
     * @param discount
     */
    public void setDiscount(String discount) {
        this.discount = discount;
    }

    /**
     * @return
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @param startTime
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @return
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * @param endTime
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * @return
     */
    public String getBrandName() {
        return brandName;
    }

    /***
     * @param brandName
     */
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    /**
     * @return
     */
    public String getStatus() {
        return status;
    }

    /***
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return
     */
    public String getCurrentQuantity() {
        return currentQuantity;
    }

    /**
     * @param currentQuantity
     */
    public void setCurrentQuantity(String currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    /**
     * @return
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * @param quantity
     */
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    /**
     * @return
     */
    public String getSellOutCount() {
        return sellOutCount;
    }

    /**
     * @param sellOutCount
     */
    public void setSellOutCount(String sellOutCount) {
        this.sellOutCount = sellOutCount;
    }

    /**
     * @return
     */
    public String getSellerId() {
        return sellerId;
    }

    /**
     * @param sellerId
     */
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    /**
     * @return
     */
    public String getSellerNick() {
        return sellerNick;
    }

    /**
     * @param sellerNick
     */
    public void setSellerNick(String sellerNick) {
        this.sellerNick = sellerNick;
    }

    /**
     * @return
     */
    public String getFeature() {
        return feature;
    }

    /**
     * @param feature
     */
    public void setFeature(String feature) {
        this.feature = feature;
    }

}
