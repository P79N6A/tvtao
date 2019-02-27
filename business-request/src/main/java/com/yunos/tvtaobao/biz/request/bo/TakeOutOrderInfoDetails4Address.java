package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 外卖订单的订单信息.
 */
public class TakeOutOrderInfoDetails4Address implements Serializable {

    private static final long serialVersionUID = -610484326142980001L;

    private String address;
    private String area;
    private String areaCode;
    private boolean available;
    private String city;
    private String cityCode;
    private boolean defaultValue;
    private String id;
    private String mobile;
    private String name;
    private String province;
    private String street;
    private String userId;
    private String positionX;
    private String positionY;

    public String getFullAddress() {
        return province + " " + city + " " + area + " " + street + " " + address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public boolean getAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public boolean isDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPositionX() {
        return positionX;
    }

    public void setPositionX(String positionX) {
        this.positionX = positionX;
    }

    public String getPositionY() {
        return positionY;
    }

    public void setPositionY(String positionY) {
        this.positionY = positionY;
    }

    public static TakeOutOrderInfoDetails4Address resolverFromMtop(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        TakeOutOrderInfoDetails4Address details4Address = new TakeOutOrderInfoDetails4Address();

        if (obj != null) {
            details4Address.setDefaultValue(obj.optBoolean("defaultValue", false));
            details4Address.setAddress(obj.optString("address"));
            details4Address.setArea(obj.optString("area"));
            details4Address.setAreaCode(obj.optString("areaCode"));
            details4Address.setAvailable(obj.optBoolean("available", false));
            details4Address.setCity(obj.optString("city"));
            details4Address.setCityCode(obj.optString("cityCode"));
            details4Address.setId(obj.optString("id"));
            details4Address.setMobile(obj.optString("mobile"));
            details4Address.setName(obj.optString("name"));
            details4Address.setProvince(obj.optString("province"));
            details4Address.setStreet(obj.optString("street"));
            details4Address.setUserId(obj.optString("userId"));
            details4Address.setPositionX(obj.optString("x"));
            details4Address.setPositionY(obj.optString("y"));
        }

        return details4Address;
    }
}
