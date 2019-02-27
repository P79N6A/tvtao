package com.yunos.tvtaobao.biz.request.bo;
/**
 * Created by linmu on 2018/8/27.
 */

public class LiveBonusResult {
    /** 中奖状态 **/
    private String status;

    /** 中奖类型 **/
    private String type;

    /*中奖红包类型*/
    private String couponType;

    /** 中奖金额 **/
    private String amount;

    /** 优惠券门槛 **/
    private String amountDoor;

    /** 店铺名称*/
    private String shopName;

    /** 优惠券名称*/
    private String promotionName;

    /** 中奖结果 **/
    private String message;


    /** 是否粮票包 **/
    private String isLifeGroup;

    /** 剩余抽奖次数 **/
    private  String remain;



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCouponType() {
        return couponType;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmountDoor() {
        return amountDoor;
    }

    public void setAmountDoor(String amountDoor) {
        this.amountDoor = amountDoor;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIsLifeGroup() {
        return isLifeGroup;
    }

    public void setIsLifeGroup(String isLifeGroup) {
        this.isLifeGroup = isLifeGroup;
    }

    public String getRemain() {
        return remain;
    }

    public void setRemain(String remain) {
        this.remain = remain;
    }


}
