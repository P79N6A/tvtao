/** $
 * PROJECT NAME: TaobaoSDK-2.1.x
 * PACKAGE NAME: com.yunos.tbsdk.bo
 * FILE    NAME: CouponList.java
 * CREATED TIME: 2014-10-8
 *    COPYRIGHT: Copyright(c) 2013 ~ 2014  All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;
import java.util.List;


public class JhsItemDetail implements Serializable {
    private static final long serialVersionUID = -2982865539369660901L;
    
    /** 聚划算商品的唯一标识，即juId */
    private Long              id;

    /** 商品ID，与商品中心的商品ID一致 */
    private Long              itemId;

    /** 商品短标题 */
    private String            shortName;
    
    /** 商品卖点特性说明，长名称 */
    private String            longName;

    /** 商品原价，单位：分。 */
    private Long              originalPrice;

    /** 团购价，单位：分 */
    private Long              activityPrice;

    /** 吊牌价，单位：分 */
    private Long              hangtagPrice;

    /** 商品图片url（最终显示在前台的图片） */
    private String            picUrl;
    /**
     * 无线1：1图
     */
    private String  picUrlNew;

    /** 购买人数 */
    private int soldCount;

    /** 是否被锁定 */
    private int isLock;

    /**
     * 上架开始时间（取毫秒）
     */
    private Long onlineStartTime;
    /**
     * 上架结束时间（取毫秒）
     */
    private Long onlineEndTime;
    /**
     * 折扣率
     */
    private Double discount;
    /**
     * 商品状态, 0:即将开始，1：可购买，2：有占座，3：卖光了，4：团购已结束
     */
    private Integer itemDisplayStatus;
    
    /** 用于无线详情展示的标签名称列表 */
    private List<String>       showTagNames;

    
    public Long getId() {
        return id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public Long getItemId() {
        return itemId;
    }

    
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    
    public String getShortName() {
        return shortName;
    }

    
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    
    public String getLongName() {
        return longName;
    }

    
    public void setLongName(String longName) {
        this.longName = longName;
    }

    
    public Long getOriginalPrice() {
        return originalPrice;
    }

    
    public void setOriginalPrice(Long originalPrice) {
        this.originalPrice = originalPrice;
    }

    
    public Long getActivityPrice() {
        return activityPrice;
    }

    
    public void setActivityPrice(Long activityPrice) {
        this.activityPrice = activityPrice;
    }

    
    public Long getHangtagPrice() {
        return hangtagPrice;
    }

    
    public void setHangtagPrice(Long hangtagPrice) {
        this.hangtagPrice = hangtagPrice;
    }

    
    public String getPicUrl() {
        return picUrl;
    }

    
    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    
    public String getPicUrlNew() {
        return picUrlNew;
    }

    
    public void setPicUrlNew(String picUrlNew) {
        this.picUrlNew = picUrlNew;
    }

    
    public int getSoldCount() {
        return soldCount;
    }

    
    public void setSoldCount(int soldCount) {
        this.soldCount = soldCount;
    }

    
    public int getIsLock() {
        return isLock;
    }

    
    public void setIsLock(int isLock) {
        this.isLock = isLock;
    }

    
    public Long getOnlineStartTime() {
        return onlineStartTime;
    }

    
    public void setOnlineStartTime(Long onlineStartTime) {
        this.onlineStartTime = onlineStartTime;
    }

    
    public Long getOnlineEndTime() {
        return onlineEndTime;
    }

    
    public void setOnlineEndTime(Long onlineEndTime) {
        this.onlineEndTime = onlineEndTime;
    }

    
    public Double getDiscount() {
        return discount;
    }

    
    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    
    public Integer getItemDisplayStatus() {
        return itemDisplayStatus;
    }

    
    public void setItemDisplayStatus(Integer itemDisplayStatus) {
        this.itemDisplayStatus = itemDisplayStatus;
    }

    
    public List<String> getShowTagNames() {
        return showTagNames;
    }

    
    public void setShowTagNames(List<String> showTagNames) {
        this.showTagNames = showTagNames;
    }
}
