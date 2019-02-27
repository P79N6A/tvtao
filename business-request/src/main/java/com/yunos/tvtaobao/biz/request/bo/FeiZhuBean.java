package com.yunos.tvtaobao.biz.request.bo;

import java.util.List;

/**
 * Created by dingbin on 2017/6/2.
 */

public class FeiZhuBean {

    private NewFeiZhuBean newFeiZhuBean;

    private List<String> service;
    private String soldCount;
    private String oldPrice;
    private String newPrice;
    private String buyText;
    private String cartText;
    private boolean hasCoupon;
    private String rightDesc;
    private String flayerTitle;
    private String mileageTitle;

    public NewFeiZhuBean getNewFeiZhuBean() {
        return newFeiZhuBean;
    }

    public void setNewFeiZhuBean(NewFeiZhuBean newFeiZhuBean) {
        this.newFeiZhuBean = newFeiZhuBean;
    }

    public String getFlayerTitle() {
        return flayerTitle;
    }

    public void setFlayerTitle(String flayerTitle) {
        this.flayerTitle = flayerTitle;
    }

    public String getMileageTitle() {
        return mileageTitle;
    }

    public void setMileageTitle(String mileageTitle) {
        this.mileageTitle = mileageTitle;
    }

    public String getRightDesc() {
        return rightDesc;
    }

    public void setRightDesc(String rightDesc) {
        this.rightDesc = rightDesc;
    }


    public boolean isHasCoupon() {
        return hasCoupon;
    }

    public void setHasCoupon(boolean hasCoupon) {
        this.hasCoupon = hasCoupon;
    }

    public String getBuyText() {
        return buyText;
    }

    public void setBuyText(String buyText) {
        this.buyText = buyText;
    }

    public String getCartText() {
        return cartText;
    }

    public void setCartText(String cartText) {
        this.cartText = cartText;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(String newPrice) {
        this.newPrice = newPrice;
    }

    public List<String> getService() {
        return service;
    }

    public void setService(List<String> service) {
        this.service = service;
    }

    public String getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(String soldCount) {
        this.soldCount = soldCount;
    }

    @Override
    public String toString() {
        return "FeiZhuBean{" +
                "service=" + service +
                ", soldCount='" + soldCount + '\'' +
                ", oldPrice='" + oldPrice + '\'' +
                ", newPrice='" + newPrice + '\'' +
                '}';
    }
}
