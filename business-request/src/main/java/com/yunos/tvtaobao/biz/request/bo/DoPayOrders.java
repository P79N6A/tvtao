package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;
import java.util.List;


public class DoPayOrders implements Serializable {

    private static final long serialVersionUID = -5551651438805741048L;

    //支付宝订单id集合，用于客户端跳安全支付控件
    private List<String> orderOutIds;
    
    //是否可以支付,
    private boolean canPay;
    
    //需要关联支付的原因,以及各种不允许支付的原因（可能是英文字符）
    private String reason;
    
    //实付款
    private String price;

    public List<String> getOrderOutIds() {
        return orderOutIds;
    }

    public void setOrderOutIds(List<String> orderOutIds) {
        this.orderOutIds = orderOutIds;
    }

    public boolean isCanPay() {
        return canPay;
    }

    public void setCanPay(boolean canPay) {
        this.canPay = canPay;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
    
    
}
