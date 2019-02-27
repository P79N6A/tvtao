package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author xuanjue.hk
 * @date 2012-11-23
 * */
public class DeliverInfo implements Serializable {
    
    
    /**
     * 
     */
    private static final long serialVersionUID = 1558140207778470313L;
    
    private String address;
    private String name;
    private String post;
    private String phone;
    private String memo;

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone
     *            the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the memo
     */
    public String getMemo() {
        return memo;
    }

    /**
     * @param memo
     *            the memo to set
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address
     *            the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the post
     */
    public String getPost() {
        return post;
    }

    /**
     * @param post
     *            the post to set
     */
    public void setPost(String post) {
        this.post = post;
    }
    
    public static DeliverInfo resolveFromMTOP(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        
        DeliverInfo deliverInfo = new DeliverInfo();
        if (!obj.isNull("address")) {
            deliverInfo.setAddress(obj.getString("address"));
        }
        
        if (!obj.isNull("name")) {
            deliverInfo.setName(obj.getString("name"));
        }
        
        if (!obj.isNull("phone")) {
            deliverInfo.setPhone(obj.getString("phone"));
        }
        
        if (!obj.isNull("post")) {
            deliverInfo.setPost(obj.getString("post"));
        }
        
        if (!obj.isNull("memo")) {
            deliverInfo.setMemo(obj.getString("memo"));
        }
        return deliverInfo;
    }
}

