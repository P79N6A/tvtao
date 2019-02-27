/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 收获地址
 * @author tianxiang
 * @date 2012-10-31 下午2:00:09
 */
public class Address implements Serializable {

    private static final long serialVersionUID                 = -587025104691192413L;
    /** 默认收货地址状态值 */
    public static final int   DEFAULT_DELIVER_ADDRESS_STATUS   = 1;
    public static final int   UNDEFAULT_DELIVER_ADDRESS_STATUS = 0;

    private String            deliverId                        = "";                  // 收货地址id
    private String            fullName                         = "";                  // 用户名
    private String            mobile                           = "";                  // 手机号
    private String            post                             = "";                  // 邮编
    private String            divisionCode                     = "";                  // 地区编号
    private String            province                         = "";                  // 省
    private String            city                             = "";                  // 市
    private String            area                             = "";                  // 区
    private String            addressDetail                    = "";                  // 详细地址
    private int               status;                                                 // 默认收货地址;0表示否，1表示是
    private int               addressType                      = 0;

    public String getDeliverId() {
        return deliverId;
    }

    public void setDeliverId(String deliverId) {
        this.deliverId = deliverId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getDivisionCode() {
        return divisionCode;
    }

    public void setDivisionCode(String divisionCode) {
        this.divisionCode = divisionCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    //省、市、区 完整地址
    public String getFullAddress() {
        return province + " " + city + " " + area + " " + addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAddressType() {
        return addressType;
    }

    public void setAddressType(int addressType) {
        this.addressType = addressType;
    }

    public static Address fromMTOP(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }

        Address item = new Address();
        item.setDeliverId(obj.optString("deliverId"));
        item.setFullName(obj.optString("fullName"));
        item.setMobile(obj.optString("mobile"));
        item.setPost(obj.optString("post"));

        item.setDivisionCode(obj.optString("divisionCode"));
        item.setProvince(obj.optString("province"));
        item.setCity(obj.optString("city"));
        item.setArea(obj.optString("area"));
        item.setAddressDetail(obj.optString("addressDetail"));
        item.setStatus(obj.optInt("status"));
        item.setAddressType(obj.optInt("addressType"));

        return item;
    }

    public boolean equals(Address a) {
        if (deliverId.equals(a.deliverId) && fullName.equals(a.fullName) && mobile.equals(a.mobile) && post.equals(a.post) && divisionCode.equals(a.divisionCode) && addressDetail.equals(a.addressDetail)) {
            return true;
        }
        return false;

    }

}
