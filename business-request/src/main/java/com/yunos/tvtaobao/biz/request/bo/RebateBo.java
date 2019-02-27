package com.yunos.tvtaobao.biz.request.bo;

/**
 * Created by linmu on 2018/8/17.
 */

public class RebateBo {

    private String coupon;
    private String couponMessage;
    private String discntPrice;
    private String icon;
    private String itemId;
    private String outPreferentialId;
    private String picUrl;
    private String tagId;
    private boolean mjf;   //买就返标签

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCouponMessage(String couponMessage) {
        this.couponMessage = couponMessage;
    }

    public String getCouponMessage() {
        return couponMessage;
    }

    public void setDiscntPrice(String discntPrice) {
        this.discntPrice = discntPrice;
    }

    public String getDiscntPrice() {
        return discntPrice;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setOutPreferentialId(String outPreferentialId) {
        this.outPreferentialId = outPreferentialId;
    }

    public String getOutPreferentialId() {
        return outPreferentialId;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagId() {
        return tagId;
    }


    public boolean isMjf() {
        return mjf;
    }

    public void setMjf(boolean mjf) {
        this.mjf = mjf;
    }

}
