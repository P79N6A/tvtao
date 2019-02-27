/** $
 * PROJECT NAME: TaobaoSDK-2.1.x
 * PACKAGE NAME: com.yunos.tbsdk.bo
 * FILE    NAME: Coupon.java
 * CREATED TIME: 2014-10-8
 *    COPYRIGHT: Copyright(c) 2013 ~ 2014  All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;


public class Coupon implements Serializable {

    private static final long serialVersionUID = -8085336548214928590L;

    private String startDay;
    private String endDay;
    
    // 0 网点 1 ktv/电影 2 其他
    private int modelType = 0;
    
    private String couponId;
    
    // logo图片
    private String logoUrl;
    
    // 优惠券种类0：线上店铺优惠券 1：线上商品优惠券 2：传统团购券 3：淘点点券 4：电影票 5：ktv 6：O2O优惠券 7：包邮券
    // 15 : 店铺红包
    private int couponType;
    
    // 优惠券名字
    private String couponName;
    
    // 背景颜色
    private String bgColor;
    
    // 提供者
    private String supplier;
    
    // 1：双十一 2：双十二 3：让红包飞 4:38大促 5：购物车BC优惠券
    private String activityFlag;
    
    //抵扣金额
    private String discount;
    
    //开始时间,带时分秒
    private String startTime;
    
    //结束时间,带时分秒
    private String endTime;
    
    private String source = "";
    
    //使用时需要满足的条件
    private String condition;

    // -1.逻辑删除 1.可用 ，0.冻结,-1.不存在，-2.已使用 3转赠 4 过期
    private int status;
    
    //卖家ID
    private long sellerId;

    
    public String getCouponName() {
        return couponName;
    }

    
    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    
    public String getBgColor() {
        return bgColor;
    }

    
    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    
    public String getSupplier() {
        return supplier;
    }

    
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    
    public String getActivityFlag() {
        return activityFlag;
    }

    
    public void setActivityFlag(String activityFlag) {
        this.activityFlag = activityFlag;
    }

    
    public String getDiscount() {
        return discount;
    }

    
    public void setDiscount(String discount) {
        this.discount = discount;
    }

    
    public String getStartTime() {
        return startTime;
    }

    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    
    public String getEndTime() {
        return endTime;
    }

    
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    
    public String getSource() {
        return source;
    }

    
    public void setSource(String source) {
        this.source = source;
    }

    
    public String getCondition() {
        return condition;
    }

    
    public void setCondition(String condition) {
        this.condition = condition;
    }

    
    public int getStatus() {
        return status;
    }

    
    public void setStatus(int status) {
        this.status = status;
    }

    
    public long getSellerId() {
        return sellerId;
    }

    
    public void setSellerId(long sellerId) {
        this.sellerId = sellerId;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public int getModelType() {
        return modelType;
    }

    public void setModelType(int modelType) {
        this.modelType = modelType;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public int getCouponType() {
        return couponType;
    }

    public void setCouponType(int couponType) {
        this.couponType = couponType;
    }

}
