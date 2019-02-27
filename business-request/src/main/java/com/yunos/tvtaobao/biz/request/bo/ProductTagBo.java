package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;

/**
 * Created by pan on 2017/3/24.
 */

public class ProductTagBo implements Serializable{

    private String couponType;
    private String itemId;
    private int position;
    private String icon;
    private String outPreferentialId;
    private String lastTraceKeyword;
    private double pointRate = 1.0;
    private String pointSchemeId;
    private String pointBlacklisted;
    private String isVip;
    private String cart;
    private String coupon;
    private String picUrl;
    private String couponMessage;
    private boolean isPre;
    private String tagId;

    public String getCouponType() {
        return couponType;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getOutPreferentialId() {
        return outPreferentialId;
    }

    public void setOutPreferentialId(String outPreferentialId) {
        this.outPreferentialId = outPreferentialId;
    }

    public String getLastTraceKeyword() {
        return lastTraceKeyword;
    }

    public void setLastTraceKeyword(String lastTraceKeyword) {
        this.lastTraceKeyword = lastTraceKeyword;
    }

    public double getPointRate() {
        return pointRate;
    }

    public void setPointRate(double pointRate) {
        this.pointRate = pointRate;
    }

    public String getPointSchemeId() {
        return pointSchemeId;
    }

    public void setPointSchemeId(String pointSchemeId) {
        this.pointSchemeId = pointSchemeId;
    }

    public String getPointBlacklisted() {
        return pointBlacklisted;
    }

    public void setPointBlacklisted(String pointBlacklisted) {
        this.pointBlacklisted = pointBlacklisted;
    }

    public String getIsVip() {
        return isVip;
    }

    public void setIsVip(String isVip) {
        this.isVip = isVip;
    }

    public String getCart() {
        return cart;
    }

    public void setCart(String cart) {
        this.cart = cart;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getCouponMessage() {
        return couponMessage;
    }

    public void setCouponMessage(String couponMessage) {
        this.couponMessage = couponMessage;
    }

    public boolean isPre() {
        return isPre;
    }

    public void setPre(boolean pre) {
        isPre = pre;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }
}
