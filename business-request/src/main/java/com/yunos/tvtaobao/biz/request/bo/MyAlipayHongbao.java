/**
 * $
 * PROJECT NAME: business-request
 * PACKAGE NAME: com.yunos.tvtaobao.biz.request.bo
 * FILE NAME: MyAlipayHongbao.java
 * CREATED TIME: 2016年3月10日
 * COPYRIGHT: Copyright(c) 2013 ~ 2016 All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.bo;


import java.io.Serializable;

public class MyAlipayHongbao implements Serializable {

    private static final long serialVersionUID = 597916177196960233L;

    // 金额 TODO 跟currentAmount有什么区别，待定
    private String amount;
    // 红包ID
    private String couponId;
    // 红包名称
    private String couponName;
    //红包当前金额
    private String currentAmount;
    //红包激活时间
    private String gmtActive;
    //红包创建时间
    private String gmtCreate;
    //红包过期时间
    private String gmtExpired;
    //红包发布方
    private String publisherName;
    //红包使用范围
    private String useArea;
    //红包模板ID
    private String templateNid;

    public String getAmount() {
        return amount;
    }

    public String getCouponId() {
        return couponId;
    }

    public String getCouponName() {
        return couponName;
    }

    public String getCurrentAmount() {
        return currentAmount;
    }

    public String getGmtActive() {
        return gmtActive;
    }

    public String getGmtCreate() {
        return gmtCreate;
    }

    public String getGmtExpired() {
        return gmtExpired;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public String getUseArea() {
        return useArea;
    }

    public String getTemplateNid() {
        return templateNid;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public void setCurrentAmount(String currentAmount) {
        this.currentAmount = currentAmount;
    }

    public void setGmtActive(String gmtActive) {
        this.gmtActive = gmtActive;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public void setGmtExpired(String gmtExpired) {
        this.gmtExpired = gmtExpired;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public void setUseArea(String useArea) {
        this.useArea = useArea;
    }

    public void setTemplateNid(String templateNid) {
        this.templateNid = templateNid;
    }

    @Override
    public String toString() {
        String text = "[ amount = " + amount + ", couponId = " + couponId + ", couponName = " + couponName
                + ", currentAmount = " + currentAmount + ", gmtActive = " + gmtActive + ", gmtCreate = " + gmtCreate
                + ", gmtExpired = " + gmtExpired + ", publisherName = " + publisherName + ", useArea = " + useArea
                + ", templateNid = " + templateNid;
        return text;
    }
}
