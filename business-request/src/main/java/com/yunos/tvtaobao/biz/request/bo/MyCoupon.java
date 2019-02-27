/**
 * $
 * PROJECT NAME: business-request
 * PACKAGE NAME: com.yunos.tvtaobao.biz.request.bo
 * FILE NAME: MyCoupon.java
 * CREATED TIME: 2016年3月3日
 * COPYRIGHT: Copyright(c) 2013 ~ 2016 All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.bo;


import java.io.Serializable;

/**
 * 我的优惠券数据结构
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2016年3月3日 下午5:39:34
 */
public class MyCoupon implements Serializable {

    private static final long serialVersionUID = 4511096011130599716L;

    // 优惠券id
    private String couponId;
    // 卖家ID
    private String supplierId;
    //优惠券类型
    private String couponType;
    //优惠券结束时间
    private String endTime;
    //优惠券开始时间
    private String startTime;
    // 优惠券名称
    private String title;
    // 使用期限
    private String endDay;
    // 优惠券状态 1-可用，0-不用
    private String status;
    // 优惠券金额
    private String amount;
    // 使用条件
    private String useCondition;
    // 是否读取
    private String hasRead;
    // 是否可删
    private String canDelete;
    // 店铺logo
    private String shopLogo;
    // 限定信息
    private String limitedPrompt;
    // 优惠券类型
    private String bizType;
    // 扩展类型
    private String spreadType;
    private String activtyDay;

    public String getCouponId() {
        return couponId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public String getCouponType() {
        return couponType;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getTitle() {
        return title;
    }

    public String getEndDay() {
        return endDay;
    }

    public String getStatus() {
        return status;
    }

    public String getAmount() {
        return amount;
    }

    public String getUseCondition() {
        return useCondition;
    }

    public String getHasRead() {
        return hasRead;
    }

    public String getCanDelete() {
        return canDelete;
    }

    public String getShopLogo() {
        return shopLogo;
    }

    public String getLimitedPrompt() {
        return limitedPrompt;
    }

    public String getBizType() {
        return bizType;
    }

    public String getSpreadType() {
        return spreadType;
    }

    public String getActivtyDay() {
        return activtyDay;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setUseCondition(String useCondition) {
        this.useCondition = useCondition;
    }

    public void setHasRead(String hasRead) {
        this.hasRead = hasRead;
    }

    public void setCanDelete(String canDelete) {
        this.canDelete = canDelete;
    }

    public void setShopLogo(String shopLogo) {
        this.shopLogo = shopLogo;
    }

    public void setLimitedPrompt(String limitedPrompt) {
        this.limitedPrompt = limitedPrompt;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public void setSpreadType(String spreadType) {
        this.spreadType = spreadType;
    }

    public void setActivtyDay(String activtyDay) {
        this.activtyDay = activtyDay;
    }

    @Override
    public String toString() {
        String text = "[couponId = " + couponId + ", supplierId = " + supplierId + ",couponType = " + couponType
                + ", startTime = " + startTime + ", endTime = " + endTime + ", title = " + title + ", status = "
                + status + ", amount = " + amount + ", endDay = " + endDay + ", useCondition = " + useCondition
                + ", limitedPrompt = " + limitedPrompt + ", bizType = " + bizType;

        return text;
    }
}
