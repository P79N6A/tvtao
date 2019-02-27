package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


public class OrderDetailMO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 421670632341695025L;
    
    private OrderInfoObject orderInfo;
    private OrderSellerInfo sellerInfo;
    private DeliverInfo deliverInfo;

    /**
     * @return the orderInfo
     */
    public OrderInfoObject getOrderInfo() {
        return orderInfo;
    }

    /**
     * @param orderInfo
     *            the orderInfo to set
     */
    public void setOrderInfo(OrderInfoObject orderInfo) {
        this.orderInfo = orderInfo;
    }

    /**
     * @return the sellerInfo
     */
    public OrderSellerInfo getSellerInfo() {
        return sellerInfo;
    }

    /**
     * @param sellerInfo
     *            the sellerInfo to set
     */
    public void setSellerInfo(OrderSellerInfo sellerInfo) {
        this.sellerInfo = sellerInfo;
    }

    /**
     * @return the deliverInfo
     */
    public DeliverInfo getDeliverInfo() {
        return deliverInfo;
    }

    /**
     * @param deliverInfo
     *            the deliverInfo to set
     */
    public void setDeliverInfo(DeliverInfo deliverInfo) {
        this.deliverInfo = deliverInfo;
    }
    
    public static OrderDetailMO resolveFromMTOP(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        OrderDetailMO orderDetail = new OrderDetailMO();
        if (!obj.isNull("orderInfo")) {
            orderDetail.setOrderInfo(OrderInfoObject.resolveFromMTOP(obj.getJSONObject("orderInfo")));
        }
        
        if (!obj.isNull("sellerInfo")) {
            orderDetail.setSellerInfo(OrderSellerInfo.resolveFromMTOP(obj.getJSONObject("sellerInfo")));
        }
        
        if (!obj.isNull("deliverInfo")) {
            orderDetail.setDeliverInfo(DeliverInfo.resolveFromMTOP(obj.getJSONObject("deliverInfo")));
        }
        return orderDetail;
    }
}
