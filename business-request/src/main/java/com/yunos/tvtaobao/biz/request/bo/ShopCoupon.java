/** $
 * PROJECT NAME: business-request
 * PACKAGE NAME: com.yunos.tvtaobao.biz.request.bo
 * FILE    NAME: RelatedItem.java
 * CREATED TIME: 2015年5月13日
 *    COPYRIGHT: Copyright(c) 2013 ~ 2015  All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;


public class ShopCoupon implements Serializable {
    private static final long serialVersionUID = 5565784138138808947L;
    
    private int type;
    private String activityId;
    private String bonusName;
    private String desc;
    private String discountFee;
    private String validTime;
    private boolean canApply;
    private int ownNum;
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public String getActivityId() {
        return activityId;
    }
    
    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }
    
    public String getBonusName() {
        return bonusName;
    }
    
    public void setBonusName(String bonusName) {
        this.bonusName = bonusName;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    public String getDiscountFee() {
        return discountFee;
    }
    
    public void setDiscountFee(String discountFee) {
        this.discountFee = discountFee;
    }
    
    public String getValidTime() {
        return validTime;
    }
    
    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }
    
    public boolean isCanApply() {
        return canApply;
    }
    
    public void setCanApply(boolean canApply) {
        this.canApply = canApply;
    }
    
    public int getOwnNum() {
        return ownNum;
    }
    
    public void setOwnNum(int ownNum) {
        this.ownNum = ownNum;
    }
    
}
