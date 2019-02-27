package com.yunos.tvtaobao.biz.request.bo;

/**
 * Created by linmu on 2018/9/12.
 */

public class TradeDetailCoupon {

    private String coupon;
    private String fee;
    private String itemId;
    private String outPreferentialId;
    private String tagId;
    private String tvoptions;
    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }
    public String getCoupon() {
        return coupon;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }
    public String getFee() {
        return fee;
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

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }
    public String getTagId() {
        return tagId;
    }

    public void setTvoptions(String tvoptions) {
        this.tvoptions = tvoptions;
    }
    public String getTvoptions() {
        return tvoptions;
    }

}
