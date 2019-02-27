package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 外卖订单的订单信息.
 */
public class TakeOutOrderInfoDetails4ContactInfo implements Serializable {

    private static final long serialVersionUID = -610484326142980001L;

    private String sellerName;
    private String sellerPhone;
    private String serverName;
    private String serverPhone;
    private String serviceProvider;
    private String storeName;
    private String storePhone;

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerPhone() {
        return sellerPhone;
    }

    public void setSellerPhone(String sellerPhone) {
        this.sellerPhone = sellerPhone;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerPhone() {
        return serverPhone;
    }

    public void setServerPhone(String serverPhone) {
        this.serverPhone = serverPhone;
    }

    public String getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(String serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone;
    }

    public static TakeOutOrderInfoDetails4ContactInfo resolverFromMtop(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        TakeOutOrderInfoDetails4ContactInfo contactInfo = new TakeOutOrderInfoDetails4ContactInfo();

        if (obj != null) {
            contactInfo.setSellerName(obj.optString("sellerName"));
            contactInfo.setSellerPhone(obj.optString("sellerPhone"));
            contactInfo.setServerName(obj.optString("serverName"));
            contactInfo.setServerPhone(obj.optString("serverPhone"));
            contactInfo.setServiceProvider(obj.optString("serviceProvider"));
            contactInfo.setStoreName(obj.optString("storeName"));
            contactInfo.setStorePhone(obj.optString("storePhone"));
        }

        return contactInfo;
    }
}
