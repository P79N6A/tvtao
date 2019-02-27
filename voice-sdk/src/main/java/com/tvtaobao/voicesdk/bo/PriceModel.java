package com.tvtaobao.voicesdk.bo;

import java.util.List;

/**
 * Created by xutingting on 2017/11/3.
 */

public class PriceModel {

    private String balance;
    private List<PriceDo> data;
    private String itemId;
    private String picUrl;
    private String price;
    private String title;
    private String trendRenderPicUrl;
    public void setBalance(String balance) {
        this.balance = balance;
    }
    public String getBalance() {
        return balance;
    }

    public void setData(List<PriceDo> data) {
        this.data = data;
    }
    public List<PriceDo> getData() {
        return data;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    public String getItemId() {
        return itemId;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
    public String getPicUrl() {
        return picUrl;
    }

    public void setPrice(String price) {
        this.price = price;
    }
    public String getPrice() {
        return price;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public String getTrendRenderPicUrl() {
        return trendRenderPicUrl;
    }

    public void setTrendRenderPicUrl(String trendRenderPicUrl) {
        this.trendRenderPicUrl = trendRenderPicUrl;
    }
}
