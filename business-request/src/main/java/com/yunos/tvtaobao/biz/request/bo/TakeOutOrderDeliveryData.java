package com.yunos.tvtaobao.biz.request.bo;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class TakeOutOrderDeliveryData implements Serializable {

    private static final long serialVersionUID = -610484326142980001L;

    private String deliveryName;
    private String deliveryMobile;
    private String latitude;
    private String longitude;

    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    public String getDeliveryMobile() {
        return deliveryMobile;
    }

    public void setDeliveryMobile(String deliveryMobile) {
        this.deliveryMobile = deliveryMobile;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public static TakeOutOrderDeliveryData resolverFromMtop(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }

        TakeOutOrderDeliveryData deliveryData = new TakeOutOrderDeliveryData();
        if (obj != null) {
            if (!obj.isNull("deliveryman_info")) {
                JSONObject manInfo = obj.getJSONObject("deliveryman_info");
                deliveryData.setDeliveryName(manInfo.optString("name"));
                deliveryData.setDeliveryMobile(manInfo.optString("phone"));
            }

            if (!obj.isNull("tracking_info")) {
                JSONObject address = obj.getJSONObject("tracking_info");
                deliveryData.setLatitude(address.optString("latitude"));
                deliveryData.setLongitude(address.optString("longitude"));
            }
        }

        return deliveryData;
    }
}
