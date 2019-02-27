/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author tianxiang
 * @date 2012-10-18 下午3:52:29
 */
public class Seller extends BaseMO implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -6704952130389496030L;

    // 卖家id
    private Long userNumId;

    // 卖家所在地
    private String location;

    // 卖家昵称
    private String nick;

    // B卖家还是C卖家,输出值分别为“B", "C"
    private String type;

    // 卖家固定电话，和卖家移动电话不能同时为空
    private String phone;

    // 卖家移动电话，和卖家固定电话不能同时为空
    private String mobile;

    // 卖家认证，如果无认证，返回“未认证商家”
    private String certify;
    //店铺ID
    private Long shopId;

    private EvaluateInfo[] evaluateInfos;

    /**
     * 卖家信用级别等级,值为0到20
     * 分别对应1心到5皇冠
     * 1-5对应1心到5心
     * 6-10对应1钻到5钻
     * 11-15对应1皇冠到5皇冠
     * 16-20对应1金冠到5金冠
     */
    private Integer creditLevel;

    // 卖家好评率百分比值，如"100.00%"
    private String goodRatePercent;

    // 注册时间
    private String userRegDate;

    public Long getUserNumId() {
        return userNumId;
    }

    public void setUserNumId(Long userNumId) {
        this.userNumId = userNumId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCertify() {
        return certify;
    }

    public void setCertify(String certify) {
        this.certify = certify;
    }

    public Integer getCreditLevel() {
        return creditLevel;
    }

    public void setCreditLevel(Integer creditLevel) {
        this.creditLevel = creditLevel;
    }

    public String getGoodRatePercent() {
        return goodRatePercent;
    }

    public void setGoodRatePercent(String goodRatePercent) {
        this.goodRatePercent = goodRatePercent;
    }

    public String getUserRegDate() {
        return userRegDate;
    }

    public void setUserRegDate(String userRegDate) {
        this.userRegDate = userRegDate;
    }

    public static Seller resolveFromMTOP(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        Seller s = new Seller();
        if (!obj.isNull("certify")) {
            s.setCertify(obj.getString("certify"));
        }

        if (!obj.isNull("creditLevel")) {
            s.setCreditLevel(obj.getInt("creditLevel"));
        }

        if (!obj.isNull("goodRatePercentage")) {
            s.setGoodRatePercent(obj.getString("goodRatePercentage"));
        }

        if (!obj.isNull("location")) {
            s.setLocation(obj.getString("location"));
        }

        if (!obj.isNull("mobile")) {
            s.setMobile(obj.getString("mobile"));
        }

        if (!obj.isNull("nick")) {
            s.setNick(obj.getString("nick"));
        }

        if (!obj.isNull("phone")) {
            s.setPhone(obj.getString("phone"));
        }

        if (!obj.isNull("type")) {
            s.setType(obj.getString("type"));
        }

        if (!obj.isNull("userNumId")) {
            s.setUserNumId(obj.getLong("userNumId"));
        }

        if (!obj.isNull("userRegDate")) {
            s.setUserRegDate(obj.getString("userRegDate"));
        }
        if (!obj.isNull("shopId")) {
            s.setShopId(obj.getLong("shopId"));
        }

        if (!obj.isNull("evaluateInfo")) {
            JSONArray array = obj.getJSONArray("evaluateInfo");
            EvaluateInfo[] temp = new EvaluateInfo[array.length()];
            for (int i = 0; i < array.length(); i++) {
                temp[i] = EvaluateInfo.resolveFromMTOP(array.getJSONObject(i));
            }
            s.setEvaluateInfos(temp);
        }
        return s;
    }

    public EvaluateInfo[] getEvaluateInfos() {
        return evaluateInfos;
    }

    public void setEvaluateInfos(EvaluateInfo[] evaluateInfos) {
        this.evaluateInfos = evaluateInfos;
    }
    public Long getShopId() {
        return shopId;
    }

    
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
}
