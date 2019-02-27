package com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * 聚划算首页背景图
 * @author hanqi
 */
public class HomeBackgroundBo extends BaseMO {

    private static final long serialVersionUID = -8346084825413748032L;
    private String bg_img;
    private String logo;

    public static HomeBackgroundBo resolveFromMTOP(JSONObject obj) throws JSONException {
        if (null == obj) {
            return null;
        }
        HomeBackgroundBo item = new HomeBackgroundBo();
        item.setBg_img(obj.optString("bg_img"));
        item.setLogo(obj.optString("logo"));
        return item;
    }

    /**
     * @return the bg_img
     */
    public String getBg_img() {
        return bg_img;
    }

    /**
     * @param bg_img the bg_img to set
     */
    public void setBg_img(String bg_img) {
        this.bg_img = bg_img;
    }

    /**
     * @return the logo
     */
    public String getLogo() {
        return logo;
    }

    /**
     * @param logo the logo to set
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }
}
