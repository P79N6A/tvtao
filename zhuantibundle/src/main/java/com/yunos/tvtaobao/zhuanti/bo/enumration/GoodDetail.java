package com.yunos.tvtaobao.zhuanti.bo.enumration;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenjiajuan on 17/4/22.
 */

public class GoodDetail implements Serializable {

    public String itemId;
    public String title;
    public String price;
    public boolean canAddBug;
    public boolean isStart;
    public String picsPath;
    public String sold;
    public String originalPrice;
    public String deliveryFees;
    public List<String> afterGuaranteeList;
    public String skuId;
    public long seller;
    public String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSeller() {
        return seller;
    }

    public void setSeller(long seller) {
        this.seller = seller;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public String getDeliveryFees() {
        return deliveryFees;
    }

    public void setDeliveryFees(String deliveryFees) {
        this.deliveryFees = deliveryFees;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCanAddBug() {
        return canAddBug;
    }

    public void setCanAddBug(boolean canAddBug) {
        this.canAddBug = canAddBug;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setSale(boolean sale) {
        isStart = sale;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPicsPath() {
        return picsPath;
    }

    public void setPicsPath(String picsPath) {
        this.picsPath = picsPath;
    }

    public String getSold() {
        return sold;
    }

    public void setSold(String sold) {
        this.sold = sold;
    }

    public List<String> getAfterGuaranteeList() {
        return afterGuaranteeList;
    }

    public void setAfterGuaranteeList(List<String> afterGuaranteeList) {
        this.afterGuaranteeList = afterGuaranteeList;
    }
}
