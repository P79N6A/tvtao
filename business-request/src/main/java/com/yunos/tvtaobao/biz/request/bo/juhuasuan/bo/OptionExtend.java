/**
 * $
 * PROJECT NAME: juhuasuan
 * PACKAGE NAME: com.yunos.tvtaobao.juhuasuan.bo
 * FILE NAME: OptionExtend.java
 * CREATED TIME: 2014-10-29
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * option的扩展属性
 * @version
 * @author hanqi
 * @data 2014-10-29 下午8:17:00
 */
public class OptionExtend extends BaseMO {

    private static final long serialVersionUID = -3149187785595709744L;
    private Double lowestDiscount;
    private String brandLogoUrl;
    private String onlineStartTime;
    private String onlineEndTime;
    private String wlBannerImgUrl;
    private String wlBrandDesc;
    private Integer soldCount;
    private String online;

    public static OptionExtend resolveFromJson(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }

        OptionExtend item = new OptionExtend();
        item.setLowestDiscount(obj.optDouble("lowestDiscount"));
        item.setBrandLogoUrl(obj.optString("brandLogoUrl"));
        item.setOnlineStartTime(obj.optString("onlineStartTime"));
        item.setOnlineEndTime(obj.optString("onlineEndTime"));
        item.setWlBannerImgUrl(obj.optString("wlBannerImgUrl"));
        item.setWlBrandDesc(obj.optString("wlBrandDesc"));
        item.setSoldCount(obj.optInt("soldCount"));
        item.setOnline(obj.optString("online"));
        return item;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "OptionExtend [lowestDiscount=" + lowestDiscount + ", brandLogoUrl=" + brandLogoUrl
                + ", onlineStartTime=" + onlineStartTime + ", onlineEndTime=" + onlineEndTime + ", wlBannerImgUrl="
                + wlBannerImgUrl + ", wlBrandDesc=" + wlBrandDesc + ", soldCount=" + soldCount + ", online=" + online
                + "]";
    }

    /**
     * @return the lowestDiscount
     */
    public Double getLowestDiscount() {
        return lowestDiscount;
    }

    /**
     * @param lowestDiscount the lowestDiscount to set
     */
    public void setLowestDiscount(Double lowestDiscount) {
        this.lowestDiscount = lowestDiscount;
    }

    /**
     * @return the brandLogoUrl
     */
    public String getBrandLogoUrl() {
        return brandLogoUrl;
    }

    /**
     * @param brandLogoUrl the brandLogoUrl to set
     */
    public void setBrandLogoUrl(String brandLogoUrl) {
        this.brandLogoUrl = brandLogoUrl;
    }

    /**
     * @return the onlineStartTime
     */
    public String getOnlineStartTime() {
        return onlineStartTime;
    }

    /**
     * @param onlineStartTime the onlineStartTime to set
     */
    public void setOnlineStartTime(String onlineStartTime) {
        this.onlineStartTime = onlineStartTime;
    }

    /**
     * @return the onlineEndTime
     */
    public String getOnlineEndTime() {
        return onlineEndTime;
    }

    /**
     * @param onlineEndTime the onlineEndTime to set
     */
    public void setOnlineEndTime(String onlineEndTime) {
        this.onlineEndTime = onlineEndTime;
    }

    /**
     * @return the wlBannerImgUrl
     */
    public String getWlBannerImgUrl() {
        return wlBannerImgUrl;
    }

    /**
     * @param wlBannerImgUrl the wlBannerImgUrl to set
     */
    public void setWlBannerImgUrl(String wlBannerImgUrl) {
        this.wlBannerImgUrl = wlBannerImgUrl;
    }

    /**
     * @return the wlBrandDesc
     */
    public String getWlBrandDesc() {
        return wlBrandDesc;
    }

    /**
     * @param wlBrandDesc the wlBrandDesc to set
     */
    public void setWlBrandDesc(String wlBrandDesc) {
        this.wlBrandDesc = wlBrandDesc;
    }

    /**
     * @return the soldCount
     */
    public Integer getSoldCount() {
        return soldCount;
    }

    /**
     * @param soldCount the soldCount to set
     */
    public void setSoldCount(Integer soldCount) {
        this.soldCount = soldCount;
    }

    /**
     * @return the online
     */
    public String getOnline() {
        return online;
    }

    /**
     * @param online the online to set
     */
    public void setOnline(String online) {
        this.online = online;
    }

}
