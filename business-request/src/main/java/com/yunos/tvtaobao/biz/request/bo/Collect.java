package com.yunos.tvtaobao.biz.request.bo;


import android.text.TextUtils;

import java.io.Serializable;

public class Collect implements Serializable {

    private static final long serialVersionUID = -610484326142980003L;
    private String            numId;
    private String            img;
    private String            price;
    private String            promotionPrice;
    private String            ownernick;
    private String            title;
    private String            collectCount;
    
    public String getNumId() {
        return numId;
    }
    
    public void setNumId(String numId) {
        this.numId = numId;
    }
    
    public String getImg() {
        return img;
    }
    
    public void setImg(String img) {
        this.img = img;
    }
    
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPromotionPrice() {
        if (TextUtils.isEmpty(promotionPrice)) {
            return price;
        }
        return promotionPrice;
    }
    
    public void setPromotionPrice(String promotionPrice) {
        this.promotionPrice = promotionPrice;
    }
    
    public String getOwnernick() {
        return ownernick;
    }
    
    public void setOwnernick(String ownernick) {
        this.ownernick = ownernick;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getCollectCount() {
        return collectCount;
    }
    
    public void setCollectCount(String collectCount) {
        this.collectCount = collectCount;
    }

    
}
