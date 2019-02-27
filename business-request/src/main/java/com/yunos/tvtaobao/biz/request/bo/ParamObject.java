package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


public class ParamObject implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7969575361477885013L;
    
    private String payType;
    private String orderId;

    /**
     * @return the payType
     */
    public String getPayType() {
        return payType;
    }

    /**
     * @param payType
     *            the payType to set
     */
    public void setPayType(String payType) {
        this.payType = payType;
    }

    /**
     * @return the orderId
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * @param orderId
     *            the orderId to set
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public static ParamObject resolverFromMtop(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        ParamObject paramObject = new ParamObject();
        paramObject.setOrderId(obj.optString("orderId"));
        paramObject.setPayType(obj.optString("payType"));
        return paramObject;
    }
}
