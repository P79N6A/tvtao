package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


public class OrderSellerInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7979043703805714444L;
    
    private String name;
    private String sellerId;
    private String sellerNick;
    private String tel;
    private String alipayAccount;

    /**
     * @return the alipayAccount
     */
    public String getAlipayAccount() {
        return alipayAccount;
    }

    /**
     * @param alipayAccount
     *            the alipayAccount to set
     */
    public void setAlipayAccount(String alipayAccount) {
        this.alipayAccount = alipayAccount;
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
     * @return the sellerId
     */
    public String getSellerId() {
        return sellerId;
    }

    /**
     * @param sellerId
     *            the sellerId to set
     */
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    /**
     * @return the sellerNick
     */
    public String getSellerNick() {
        return sellerNick;
    }

    /**
     * @param sellerNick
     *            the sellerNick to set
     */
    public void setSellerNick(String sellerNick) {
        this.sellerNick = sellerNick;
    }

    /**
     * @return the tel
     */
    public String getTel() {
        return tel;
    }

    /**
     * @param tel
     *            the tel to set
     */
    public void setTel(String tel) {
        this.tel = tel;
    }
    
    public static OrderSellerInfo resolveFromMTOP(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        
        OrderSellerInfo orderSellerInfo = new OrderSellerInfo();
        if (!obj.isNull("name")) {
            orderSellerInfo.setName(obj.getString("name"));
        }
        
        if (!obj.isNull("sellerId")) {
            orderSellerInfo.setSellerId(obj.getString("sellerId"));
        }
        
        if (!obj.isNull("sellerNick")) {
            orderSellerInfo.setSellerNick(obj.getString("sellerNick"));
        }
        
        if (!obj.isNull("tel")) {
            orderSellerInfo.setTel(obj.getString("tel"));
        }
        
        if (!obj.isNull("alipayAccount")) {
            orderSellerInfo.setAlipayAccount(obj.getString("alipayAccount"));
        }
        return orderSellerInfo;
    }
}
