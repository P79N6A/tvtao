package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


public class JhsItemInfo implements Serializable {
    
    private enum JhsItemInfoStateEnum {
        WAIT_FOR_START, AVAIL_BUY, EXIST_HOLDER, NO_STOCK, OUT_OF_TIME
    };
   
    /**
     * 
     */
    private static final long serialVersionUID = -6565653073356907357L;
    
    /**
     * 活动价
     */
    private Long activityPrice;
    
    /**
     * 折扣
     */
    private String discount;
    
    /**
     * 组ID
     */
    private Long groupId;
    
    /**
     * 购买人数
     */
    private Integer soldCount;
    
    /**
     * 限购数
     */
    private Integer limitNum;
    
    /**
     * 团购开始时间
     */
    private String onlineStartTime;
    
    /**
     * 团购结束时间
     */
    private String onlineEndTime;
    
    /**
     * 商品状态
     */
    private String jhsItemStatus;
    
    /**
     * 是否包邮 1-包邮 0-不包邮
     */
    private String payPostage;

    
    public Long getActivityPrice() {
        return activityPrice;
    }

    
    public void setActivityPrice(Long activityPrice) {
        this.activityPrice = activityPrice;
    }

    
    public String getDiscount() {
        return discount;
    }

    
    public void setDiscount(String discount) {
        this.discount = discount;
    }

    
    public Long getGroupId() {
        return groupId;
    }

    
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    
    public Integer getSoldCount() {
        return soldCount;
    }

    
    public void setSoldCount(Integer soldCount) {
        this.soldCount = soldCount;
    }

    
    public Integer getLimitNum() {
        return limitNum;
    }

    
    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }

    
    public String getOnlineStartTime() {
        return onlineStartTime;
    }

    
    public void setOnlineStartTime(String onlineStartTime) {
        this.onlineStartTime = onlineStartTime;
    }

    
    public String getOnlineEndTime() {
        return onlineEndTime;
    }

    
    public void setOnlineEndTime(String onlineEndTime) {
        this.onlineEndTime = onlineEndTime;
    }

    
    public String getJhsItemStatus() {
        return jhsItemStatus;
    }

    
    public void setJhsItemStatus(String jhsItemStatus) {
        this.jhsItemStatus = jhsItemStatus;
    }

    
    public String getPayPostage() {
        return payPostage;
    }

    
    public void setPayPostage(String payPostage) {
        this.payPostage = payPostage;
    }
    
    /**
     * 解析JSON
     * @param obj
     * @return
     * @throws JSONException
     */
    public static JhsItemInfo resolveFromMTOP(JSONObject obj) throws JSONException {
        if (obj == null) return null;
        JhsItemInfo jhsItemInfo = new JhsItemInfo();
        if (!obj.isNull("activityPrice")) {
            jhsItemInfo.setActivityPrice(obj.getLong("activityPrice"));
        }
        if (!obj.isNull("discount")) {
            jhsItemInfo.setDiscount(obj.getString("discount"));
        }
        if (!obj.isNull("groupId")) {
            jhsItemInfo.setGroupId(obj.getLong("groupId"));
        }
        if (!obj.isNull("soldCount")) {
            jhsItemInfo.setSoldCount(obj.getInt("soldCount"));
        }
        if (!obj.isNull("limitNum")) {
            jhsItemInfo.setLimitNum(obj.getInt("limitNum"));
        }
        if (!obj.isNull("onlineStartTime")) {
            jhsItemInfo.setOnlineEndTime(obj.getString("onlineStartTime"));
        }
        if (!obj.isNull("onlineEndTime")) {
            jhsItemInfo.setOnlineEndTime(obj.getString("onlineEndTime"));
        }
        if (!obj.isNull("jhsItemStatus")) {
            jhsItemInfo.setJhsItemStatus(obj.getString("jhsItemStatus"));
        }
        if (!obj.isNull("payPostage")) {
            jhsItemInfo.setPayPostage(obj.getString("payPostage"));
        }
        return jhsItemInfo;
    }
    
    /**
     * 是否可购买
     * 
     * @return
     */
    public boolean isAbleBuy() {
        return jhsItemStatus.equals(JhsItemInfoStateEnum.AVAIL_BUY.name());
    }
    
    /**
     * 是否等待开始
     * @return
     */
    public boolean isWaitForStart() {
        return jhsItemStatus.equals(JhsItemInfoStateEnum.WAIT_FOR_START.name());
    }
    
    /**
     * 是否有占座
     * @return
     */
    public boolean isHolder() {
        return jhsItemStatus.equals(JhsItemInfoStateEnum.EXIST_HOLDER.name());
    }
    
    /**
     * 是否卖光
     * @return
     */
    public boolean isNoStock() {
        return jhsItemStatus.equals(JhsItemInfoStateEnum.NO_STOCK.name());
    }
    
    /**
     * 是否已结束
     * @return
     */
    public boolean isEnd() {
        return jhsItemStatus.equals(JhsItemInfoStateEnum.OUT_OF_TIME.name());
    }
    
    /**
     * 获取购买状态描述
     * @return
     */
    public String getBuyStatus() {
        if (isWaitForStart()) {
            return "即将开始";
        }
        
        if (isAbleBuy()) {
            return "立即购买";
        }
        
        if (isHolder()) {
            return "已占座";
        }
        
        if (isNoStock()) {
            return "已卖光";
        }
        
        if (isEnd()) {
            return "已结束";
        }
        
        return "立即购买";
    }
}
