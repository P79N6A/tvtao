/**
 * $
 * PROJECT NAME: business-request
 * PACKAGE NAME: com.yunos.tvtaobao.biz.request.bo
 * FILE NAME: MyAlipayHongbaoList.java
 * CREATED TIME: 2016年3月3日
 * COPYRIGHT: Copyright(c) 2013 ~ 2016 All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.bo;


import java.io.Serializable;
import java.util.ArrayList;

public class MyAlipayHongbaoList implements Serializable {

    private static final long serialVersionUID = 895703763902174871L;

    // 红包总数量
    private String totalCount;
    // 红包总金额
    private String totalAmount;
    // 红包数据获取时间
    private String alipayDate;
    // ?
    private boolean activityTime;
    // ?
    private boolean showFissionPacket;
    // 红包列表
    private ArrayList<MyAlipayHongbao> couponList;

    public String getTotalCount() {
        return totalCount;
    }

    public String gettotalAmount() {
        return totalAmount;
    }

    public String getalipayDate() {
        return alipayDate;
    }

    public boolean getactivityTime() {
        return activityTime;
    }

    public boolean getshowFissionPacket() {
        return showFissionPacket;
    }

    public ArrayList<MyAlipayHongbao> getCouponList() {
        return couponList;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getAlipayDate() {
        return alipayDate;
    }

    public void setAlipayDate(String alipayDate) {
        this.alipayDate = alipayDate;
    }

    public boolean isActivityTime() {
        return activityTime;
    }

    public void setActivityTime(boolean activityTime) {
        this.activityTime = activityTime;
    }

    public boolean isShowFissionPacket() {
        return showFissionPacket;
    }

    public void setShowFissionPacket(boolean showFissionPacket) {
        this.showFissionPacket = showFissionPacket;
    }

    public void setCouponList(ArrayList<MyAlipayHongbao> couponList) {
        this.couponList = couponList;
    }

    @Override
    public String toString() {
        String text = "[ totalCount = " + totalCount + ", totalAmount = " + totalAmount + ", alipayDate = "
                + alipayDate + ", activityTime = " + activityTime + ", showFissionPacket = " + showFissionPacket
                + ", couponList = " + couponList + " ]";
        return text;
    }
}
