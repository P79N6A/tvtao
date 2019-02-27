package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;


public class GoodsListItem implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 138560600867817904L;
    //    "auctionId": "5399837497",
    //    "auctionType": "b",
    //    "hdfk": "0",
    //    "picUrl": "http://img2012.i04.wimg.taobao.com/bao/uploaded/i2/T1PNK7XaNoXXaT5lZU_015631.jpg",
    //    "quantity": "56",
    //    "reservePrice": "35.0",
    //    "salePrice": "35.0",
    //    "sold": "4927",
    //    "title": "不二家官方旗舰店：60支桶装果味棒棒糖（121006）日本",
    //    "totalSoldQuantity": "4927"3
    private String auctionId;
    private String auctionType;
    private String hdfk;
    private String picUrl;
    private String quantity;
    private String reservePrice;
    private String salePrice;
    private int    sold;
    private String title;
    private String totalSoldQuantity;

    public String getAuctionId() {
        return this.auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    public String getAuctionType() {
        return this.auctionType;
    }

    public void setAuctionType(String auctionType) {
        this.auctionType = auctionType;
    }

    public String getHdfk() {
        return this.hdfk;
    }

    public void setHdfk(String hdfk) {
        this.hdfk = hdfk;
    }

    public String getPicUrl() {
        return this.picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getQuantity() {
        return this.quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getReservePrice() {
        return this.reservePrice;
    }

    public void setReservePrice(String reservePrice) {
        this.reservePrice = reservePrice;
    }

    public String getSalePrice() {
        return this.salePrice;
    }

    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    public int getSold() {
        return this.sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTotalSoldQuantity() {
        return this.totalSoldQuantity;
    }

    public void setTotalSoldQuantity(String totalSoldQuantity) {
        this.totalSoldQuantity = totalSoldQuantity;
    }

}
