package com.yunos.voice.Do;

/**
 * Created by xutingting on 2017/11/1.
 */
public class BillDo {

    private String actualPaidFee;
    private String auctionPicUrl;
    private String auctionTitle;
    private String bizOrderId;
    private String gmtCreate;
    private String hasDetail;
    private String totalFee;
    public void setActualPaidFee(String actualPaidFee) {
        this.actualPaidFee = actualPaidFee;
    }
    public String getActualPaidFee() {
        return actualPaidFee;
    }

    public String getAuctionPicUrl() {
        return auctionPicUrl;
    }

    public void setAuctionPicUrl(String auctionPicUrl) {
        this.auctionPicUrl = auctionPicUrl;
    }

    public void setAuctionTitle(String auctionTitle) {
        this.auctionTitle = auctionTitle;
    }
    public String getAuctionTitle() {
        return auctionTitle;
    }

    public void setBizOrderId(String bizOrderId) {
        this.bizOrderId = bizOrderId;
    }
    public String getBizOrderId() {
        return bizOrderId;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }
    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setHasDetail(String hasDetail) {
        this.hasDetail = hasDetail;
    }
    public String getHasDetail() {
        return hasDetail;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }
    public String getTotalFee() {
        return totalFee;
    }

}