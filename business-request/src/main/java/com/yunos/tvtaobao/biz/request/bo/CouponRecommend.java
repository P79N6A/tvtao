/**
 * $
 * PROJECT NAME: business-request
 * PACKAGE NAME: com.yunos.tvtaobao.biz.request.bo
 * FILE NAME: CouponRecommend.java
 * CREATED TIME: 2016年4月25日
 * COPYRIGHT: Copyright(c) 2013 ~ 2016 All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.bo;


public class CouponRecommend {

    // 商品ID
    private String itemId;
    // 商品名称
    private String itemTitle;
    // 商品链接
    private String url;
    // 图片链接
    private String picUrl;
    // 店铺名称
    private String shopName;
    // 原价
    private String originalCost;
    // 现价
    private String currentPrice;

    /**
     * 获取商品ID
     * @return
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * 获取商品标题
     * @return
     */
    public String getItemTitle() {
        return itemTitle;
    }

    /**
     * 获取商品链接
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * 获取商品图片链接
     * @return
     */
    public String getPicUrl() {
        return picUrl;
    }

    /**
     * 获取店铺名称
     * @return
     */
    public String getShopName() {
        return shopName;
    }

    /**
     * 获取原价
     * @return
     */
    public String getOriginalCost() {
        return originalCost;
    }

    /**
     * 获取当前价格
     * @return
     */
    public String getCurrentPrice() {
        return currentPrice;
    }
}
