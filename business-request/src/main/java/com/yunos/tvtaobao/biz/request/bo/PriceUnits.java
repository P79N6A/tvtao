package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


public class PriceUnits implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1164868570081118066L;
    
    /**
     * 价格的名称
     */
    private String name;
    
    /**
     * 价格的区间
     */
    private String price;
    
    /**
     * 显示的样式
     */
    private String display;
    
    /**
     * 是否有效
     */
    private Boolean valid;

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public String getPrice() {
        return price;
    }

    
    public void setPrice(String price) {
        this.price = price;
    }

    public String getDisplay() {
        return display;
    }

    
    public void setDisplay(String display) {
        this.display = display;
    }

    
    public Boolean getValid() {
        return valid;
    }

    
    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    /**
     * 解析价格
     * @param obj
     * @return
     * @throws JSONException
     */
    public static PriceUnits resolveFromMTOP(JSONObject obj)
            throws JSONException {
        if (obj == null) return null;
        PriceUnits priceUnits = new PriceUnits();
        if (!obj.isNull("name")) {
            priceUnits.setName(obj.getString("name"));
        }
        if (!obj.isNull("price")) {
            priceUnits.setPrice(obj.getString("price"));
        }
        if (!obj.isNull("display")) {
            priceUnits.setDisplay(obj.getString("display"));
        }
        if (!obj.isNull("valid")) {
            priceUnits.setValid(obj.getBoolean("valid"));
        }
        return priceUnits;
    }
}
