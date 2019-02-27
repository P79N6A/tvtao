/**
 * $
 * PROJECT NAME: TvShopping
 * PACKAGE NAME: com.yunos.tvshopping.bo
 * FILE NAME: TvShopTimeItem.java
 * CREATED TIME: 2015年1月8日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo;


import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 边看边购的广告商品数据
 * @version
 * @author mi.cao
 * @data 2015年1月8日 下午7:23:23
 */
public class TbTvShoppingItemBo implements Serializable {

    private static final long serialVersionUID = 6850844999639375461L;

    public static enum ShopType {
        UNKNOWN, SINGLE, LIST
    }

    // 商品ID
    private long itemId;
    //跳转配置链接
    private String itemActionUri;
    // 广告商品图片url
    private String itemImage;
    // 广告类型SINGLE/LIST
    private String type;
    // 广告开始时间 (微秒)
    private long startMillisecond;
    // 广告结束时间 (微秒)
    private long endMillisecond;
    // 在影视最后所有商品列表中显示的商品列表
    private List<Long> itemIds;

    public void setItemId(long id) {
        itemId = id;
    }

    public long getItemId() {
        return itemId;
    }

    public String getItemActionUri() {
        return itemActionUri;
    }

    public void setItemActionUri(String itemActionUri) {
        this.itemActionUri = itemActionUri;
    }

    public void setItemImage(String image) {
        itemImage = image;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public ShopType getShopType() {
        ShopType shopType = ShopType.UNKNOWN;
        if (!TextUtils.isEmpty(type)) {
            if (type.equals("SINGLE")) {
                shopType = ShopType.SINGLE;
            } else if (type.equals("LIST")) {
                shopType = ShopType.LIST;
            }
        }
        return shopType;
    }

    public long getStartMillisecond() {
        return startMillisecond;
    }

    public void setStartMillisecond(long startMillisecond) {
        this.startMillisecond = startMillisecond;
    }

    public long getEndMillisecond() {
        return endMillisecond;
    }

    public void setEndMillisecond(long endMillisecond) {
        this.endMillisecond = endMillisecond;
    }

    public void setStartTime(long time) {
        startMillisecond = time;
    }

    public long getStartTime() {
        return startMillisecond;
    }

    public void setEndTime(long time) {
        endMillisecond = time;
    }

    public long getEndTime() {
        return endMillisecond;
    }

    public void setItemIds(List<Long> itemIds) {
        this.itemIds = itemIds;
    }

    public List<Long> getItemIds() {
        return this.itemIds;
    }

    public boolean isList() {
        if (!TextUtils.isEmpty(type)) {
            return (type.compareTo("LIST") == 0);
        }
        return false;
    }

    @Override
    public String toString() {
        return "TbTvShoppingItemBo{" +
                "itemId=" + itemId +
                ", itemImage='" + itemImage + '\'' +
                ", type='" + type + '\'' +
                ", startMillisecond=" + startMillisecond +
                ", endMillisecond=" + endMillisecond +
                ", itemIds=" + itemIds +
                ", itemActionUri=" + itemActionUri +
                '}';
    }
}
