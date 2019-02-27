package com.yunos.tvtaobao.biz.request.bo;

import android.text.Html;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 主搜索商品实体
 * @author shengchun
 */
public class Goods implements Serializable {

    private static final long serialVersionUID = 6257317536315697347L;
    
    /**
     * 宝贝title
     */
    private String title = null;
    /**
     * 宝贝价格
     */
    private String price = null;
    /**
     * 宝贝图片链接
     */
    private String picUrl = null;
    /**
     * 宝贝所在地
     */
    private String location = null;
    /**
     * 已售出数
     */
    private String sold = null;
    /**
     * 信誉
     */
    private String ratesum = null;
    /**
     * 店铺类型，天猫or淘宝
     */
    private String userType = null;
    /**
     * 宝贝itemid
     */
    private String itemId = null;
    /**
     * 卖家nick
     */
    private String nick = null;
    /**
     * 卖家userid
     */
    private String userId = null;
    /**
     * 折扣价
     */
    private String priceWithRate = null;
    
    
    /**
     * 运费
     */
    private String fastPostFee = null;
    



    // 折扣率 =9000表示9折，促销搜索用
    private String zkRate;
    
    
    
    
    public String getZkRate() {
        return zkRate;
    }

    
    public void setZkRate(String zkRate) {
        this.zkRate = zkRate;
    }
    
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getPrice() {
        return price;
    }
    
    public void setPrice(String price) {
        this.price = price;
    }
    
    public String getPicUrl() {
        return picUrl;
    }
    
    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getSold() {
        return sold;
    }
    
    public void setSold(String sold) {
        this.sold = sold;
    }
    
    public String getRatesum() {
        return ratesum;
    }
    
    public void setRatesum(String ratesum) {
        this.ratesum = ratesum;
    }
    
    public String getUserType() {
        return userType;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    public String getItemId() {
        return itemId;
    }
    
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    
    public String getNick() {
        return nick;
    }
    
    public void setNick(String nick) {
        this.nick = nick;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getPriceWithRate() {
        return priceWithRate;
    }
    
    public void setPriceWithRate(String priceWithRate) {
        this.priceWithRate = priceWithRate;
    }
    
    
    
    public void setFastPostFee(String fastPostFee)
    {
        this.fastPostFee = fastPostFee;
    }
    
    public String getFastPostFee()
    {
        return this.fastPostFee;
    }
    
    
    /**
     * 原则上所有字段需反转义输出，目前没有反转义，后续如有问题时需加上反转义，可参考淘宝主客户端
     * @param obj
     * @return
     * @throws JSONException
     */
    public static Goods resolveFromMTOP(JSONObject obj) throws JSONException {
        if (obj == null) return null;
        Goods goods = new Goods();
        if (!obj.isNull("title")) {
            String title = obj.getString("title");
            //替换换行符
            title = title.replaceAll("\r\n", " ").replaceAll("\n", " ").replaceAll("\r", " ");
            title = Html.fromHtml(title).toString();
            goods.setTitle(title);
        }
        if (!obj.isNull("price")) {
            goods.setPrice(obj.getString("price"));
        }
        if (!obj.isNull("pic_path")) {
            String picUrl = obj.getString("pic_path");
            picUrl = picUrl.replace("_60x60.jpg", "");
            goods.setPicUrl(picUrl);
        }
        if (!obj.isNull("location")) {
            String location = obj.getString("location");
            try {
                int len = location.indexOf(" ");
                if (len != -1) {
                    location = new String(location.substring(len + 1));
                }
            } catch (Exception e) {
                
            }
            goods.setLocation(location);
        }
        if (!obj.isNull("sold")) {
            goods.setSold(obj.getString("sold"));
        }
        if (!obj.isNull("priceWap")) {
            goods.setPriceWithRate(obj.getString("priceWap"));
        }
        if (!obj.isNull("userType")) {
            goods.setUserType(obj.getString("userType"));
        }
        if (!obj.isNull("item_id")) {
            goods.setItemId(obj.getString("item_id"));   
        }
        if (!obj.isNull("userId")) {
            goods.setUserId(obj.getString("userId"));
        }
        if (!obj.isNull("nick")) {
            goods.setNick(obj.getString("nick"));
        }
        if (!obj.isNull("ratesum")) {
            goods.setRatesum(obj.getString("ratesum"));
        }
        
        if (!obj.isNull("fastPostFee")) {
            goods.setFastPostFee(obj.getString("fastPostFee")); 
        }
        
        
        goods.setZkRate(obj.optString("zkRate", null));
        
        
        return goods;
    }
}
