/**
 * $
 * PROJECT NAME: business-request
 * PACKAGE NAME: com.yunos.tvtaobao.biz.request.bo
 * FILE NAME: MyCouponsList.java
 * CREATED TIME: 2016年3月3日
 * COPYRIGHT: Copyright(c) 2013 ~ 2016 All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.bo;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * 我的优惠券列表
 * Class Descripton.
 *
 * @author mi.cao
 * @data 2016年3月3日 下午5:31:53
 */
public class MyCouponsList implements Serializable {

    private static final long serialVersionUID = 4986940290622063079L;

    /**
     * 业务类型
     */
    private String bizType;
    /**
     * 业务名称
     */
    private String bizTitle;
    /**
     * 优惠券类型
     */
    private String couponType;
    /**
     * 优惠券总数
     */
    private String totalNum;
    /**
     * 优惠券列表
     */
    private ArrayList<MyCoupon> couponList;

    public String getBizType() {
        return bizType;
    }

    public String getBizTitle() {
        return bizTitle;
    }

    public String getCouponType() {
        return couponType;
    }

    public String getTotalNum() {
        return totalNum;
    }

    public ArrayList<MyCoupon> getCouponList() {
        return couponList;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public void setBizTitle(String bizTitle) {
        this.bizTitle = bizTitle;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
    }

    public void setCouponList(ArrayList<MyCoupon> couponList) {
        this.couponList = couponList;
    }

    @Override
    public String toString() {
        String text = "[ totalNum = " + totalNum + ", bizTitle = " + bizTitle + ", couponType = " + couponType
                + ", bizType = " + bizType + ", couponList = " + couponList + " ]";
        return text;
    }
}
