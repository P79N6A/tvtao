package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;

/**
 * Created by chenjiajuan on 17/9/19.
 */


public class BuildOrderPreSale implements Serializable {

    private static final long serialVersionUID = 572761700631368713L;

    /**
     "priceText":"50"
     "priceTitle":"定金"
     "presale":"190"
     "presaleTitle":"尾款"
     */
    private String priceText;
    private String priceTitle;
    private String presale;
    private String presaleTitle;
    /**
     * extraText":"付尾款后7天内发货",
     "orderedItemAmount":"169",
     "tip":"支付尾款时间：2017.11.11 00:00~2017.11.12 00:00",
     "startTime":"1504886400000",
     "endTime":"1510327800000",
     "status":"2",
     "text":"11.10 23:30结束"
     "notifyTitle":"通知支付尾款的手机号"
     "notifyValue":"15706806167"
     "deliverTitle":"发货时间"
     "deliverValue":"付款后七天内发货"
     "promotion":"商品优惠 定金抵用:省30.00元"
     */
    private String  extraText;
    private  String  orderedItemAmount;
    private String tip;
    private  String startTime;
    private String endTime;
    private String status;
    private String text;
    private  String notifyTitle;
    private  String notifyValue;
    private String deliverTitle;
    private String deliverValue;
    private String promotion;

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }

    public String getDeliverTitle() {
        return deliverTitle;
    }

    public void setDeliverTitle(String deliverTitle) {
        this.deliverTitle = deliverTitle;
    }

    public String getDeliverValue() {
        return deliverValue;
    }

    public void setDeliverValue(String deliverValue) {
        this.deliverValue = deliverValue;
    }

    public String getPresaleTitle() {
        return presaleTitle;
    }

    public void setPresaleTitle(String presaleTitle) {
        this.presaleTitle = presaleTitle;
    }

    public String getPriceTitle() {
        return priceTitle;
    }

    public void setPriceTitle(String priceTitle) {
        this.priceTitle = priceTitle;
    }

    public String getPresale() {
        return presale;
    }

    public void setPresale(String presale) {
        this.presale = presale;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriceText() {
        return priceText;
    }

    public void setPriceText(String priceText) {
        this.priceText = priceText;
    }

    public String getExtraText() {
        return extraText;
    }

    public void setExtraText(String extraText) {
        this.extraText = extraText;
    }

    public String getOrderedItemAmount() {
        return orderedItemAmount;
    }

    public void setOrderedItemAmount(String orderedItemAmount) {
        this.orderedItemAmount = orderedItemAmount;
    }

    public String getNotifyValue() {
        return notifyValue;
    }

    public void setNotifyValue(String notifyValue) {
        this.notifyValue = notifyValue;
    }

    public String getNotifyTitle() {
        return notifyTitle;
    }

    public void setNotifyTitle(String notifyTitle) {
        this.notifyTitle = notifyTitle;
    }

    @Override
    public String toString() {
        return "BuildOrderPreSale{" +
                "priceText='" + priceText + '\'' +
                ", priceTitle='" + priceTitle + '\'' +
                ", presale='" + presale + '\'' +
                ", presaleTitle='" + presaleTitle + '\'' +
                ", extraText='" + extraText + '\'' +
                ", orderedItemAmount='" + orderedItemAmount + '\'' +
                ", tip='" + tip + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", status='" + status + '\'' +
                ", text='" + text + '\'' +
                ", notifyTitle='" + notifyTitle + '\'' +
                ", notifyValue='" + notifyValue + '\'' +
                ", deliverTitle='" + deliverTitle + '\'' +
                ", deliverValue='" + deliverValue + '\'' +
                ", promotion='" + promotion + '\'' +
                '}';
    }
}
