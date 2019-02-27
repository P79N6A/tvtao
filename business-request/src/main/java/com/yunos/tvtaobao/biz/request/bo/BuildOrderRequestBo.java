/** $
 * PROJECT NAME: business-request
 * PACKAGE NAME: com.yunos.tvtaobao.biz.request.bo
 * FILE    NAME: BuildOrderRequest.java
 * CREATED TIME: 2015-2-26
 *    COPYRIGHT: Copyright(c) 2013 ~ 2015  All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;


public class BuildOrderRequestBo implements Serializable {
    
    private static final long serialVersionUID = -5731049163432947160L;

    //收货地址id，如果为空取用户默认地址
    private String deliveryId;
    
    //用户的购物车记录id，多个以逗号分割,立即购买可不填
    private String cartIds;
    
    //是否立即购买
    private boolean buyNow;
    
    /*------------ 如果立即购买需要传以下参数 ------------*/
    
    //商品id
    private String itemId;
    
    //购买数量
    private int quantity;
    
    //skuId, 商品没有sku可不填
    private String skuId;
    
    //服务的id。服务id|服务skuid-服务id|服务skuid
    private String serviceId;
    
    //活动id--无线渠道
    private String activityId;
    
    //聚划算的key
    private String tgKey;
    
    //是否单独处理结算
    private boolean isSettlementAlone;
    
    //单独结算时的参数
    private String buyParam;
    
    // 来自哪个界面[购物车，或者sku]
    private String mFrom;
    
    //从详情带来的扩展参数
    private String extParams;

    //是否预售
    private boolean isPreSell;

    private String tagId;

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getFrom() {
        return mFrom;
    }



    public void setFrom(String mFrom) {
        this.mFrom = mFrom;
    }


    public String getDeliveryId() {
        return deliveryId;
    }

    
    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    
    public String getCartIds() {
        return cartIds;
    }

    
    public void setCartIds(String cartIds) {
        this.cartIds = cartIds;
    }

    
    public boolean isBuyNow() {
        return buyNow;
    }

    
    public void setBuyNow(boolean buyNow) {
        this.buyNow = buyNow;
    }

    
    public String getItemId() {
        return itemId;
    }

    
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    
    public int getQuantity() {
        return quantity;
    }

    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    
    public String getSkuId() {
        return skuId;
    }

    
    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    
    public String getServiceId() {
        return serviceId;
    }

    
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    
    public String getActivityId() {
        return activityId;
    }

    
    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    
    public String getTgKey() {
        return tgKey;
    }

    
    public void setTgKey(String tgKey) {
        this.tgKey = tgKey;
    }


    
    public boolean isSettlementAlone() {
        return isSettlementAlone;
    }


    
    public void setSettlementAlone(boolean isSettlementAlone) {
        this.isSettlementAlone = isSettlementAlone;
    }


    
    public String getBuyParam() {
        return buyParam;
    }


    
    public void setBuyParam(String buyParam) {
        this.buyParam = buyParam;
    }



    public String getExtParams() {
        return extParams;
    }



    public void setExtParams(String extParams) {
        this.extParams = extParams;
    }

    public boolean isPreSell() {
        return isPreSell;
    }

    public void setPreSell(boolean preSell) {
        isPreSell = preSell;
    }

    @Override
    public String toString() {
        return "BuildOrderRequestBo{" +
                "deliveryId='" + deliveryId + '\'' +
                ", cartIds='" + cartIds + '\'' +
                ", buyNow=" + buyNow +
                ", itemId='" + itemId + '\'' +
                ", quantity=" + quantity +
                ", skuId='" + skuId + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", activityId='" + activityId + '\'' +
                ", tgKey='" + tgKey + '\'' +
                ", isSettlementAlone=" + isSettlementAlone +
                ", buyParam='" + buyParam + '\'' +
                ", mFrom='" + mFrom + '\'' +
                ", extParams='" + extParams + '\'' +
                ", isPreSell=" + isPreSell +
                '}';
    }
}
