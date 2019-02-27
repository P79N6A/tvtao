package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 外卖订单的订单信息.
 */
public class TakeOutOrderInfoDetails4TradeInfo implements Serializable {

    private static final long serialVersionUID = -610484326142980001L;

    private String alipayNo;
    private String autoConfirmTime;
    private String autoConfirmTimestamp;
    private String cancelOrderTime;
    private String createTime;
    private String payTime;
    private String tbOrderId;

    public String getAlipayNo() {
        return alipayNo;
    }

    public void setAlipayNo(String alipayNo) {
        this.alipayNo = alipayNo;
    }

    public String getAutoConfirmTime() {
        return autoConfirmTime;
    }

    public void setAutoConfirmTime(String autoConfirmTime) {
        this.autoConfirmTime = autoConfirmTime;
    }

    public String getAutoConfirmTimestamp() {
        return autoConfirmTimestamp;
    }

    public void setAutoConfirmTimestamp(String autoConfirmTimestamp) {
        this.autoConfirmTimestamp = autoConfirmTimestamp;
    }

    public String getCancelOrderTime() {
        return cancelOrderTime;
    }

    public void setCancelOrderTime(String cancelOrderTime) {
        this.cancelOrderTime = cancelOrderTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getTbOrderId() {
        return tbOrderId;
    }

    public void setTbOrderId(String tbOrderId) {
        this.tbOrderId = tbOrderId;
    }

    public static TakeOutOrderInfoDetails4TradeInfo resolverFromMtop(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        TakeOutOrderInfoDetails4TradeInfo details4TradeInfo = new TakeOutOrderInfoDetails4TradeInfo();

        if (obj != null) {
            details4TradeInfo.setAlipayNo(obj.optString("alipayNo"));
            details4TradeInfo.setAutoConfirmTime(obj.optString("autoConfirmTime"));
            details4TradeInfo.setAutoConfirmTimestamp(obj.optString("autoConfirmTimestamp"));
            details4TradeInfo.setCancelOrderTime(obj.optString("cancelOrderTime"));
            details4TradeInfo.setCreateTime(obj.optString("createTime"));
            details4TradeInfo.setPayTime(obj.optString("payTime"));
            details4TradeInfo.setTbOrderId(obj.optString("tbOrderId"));
        }

        return details4TradeInfo;
    }
}
