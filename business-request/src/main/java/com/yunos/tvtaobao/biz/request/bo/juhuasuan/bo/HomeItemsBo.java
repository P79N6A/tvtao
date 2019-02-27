package com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo;



import com.yunos.tvtaobao.biz.request.core.JsonResolver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class HomeItemsBo extends BaseMO {

    private static final long serialVersionUID = -1738409524853905138L;
    private String title;
    private String e_name;
    private String desc;
    private String type;
    private String bg_img;
    private String front_img;

    private Map<String, String> content;

    public static HomeItemsBo resolveFromMTOP(JSONObject obj) throws JSONException {
        if (null == obj) {
            return null;
        }
        HomeItemsBo item = new HomeItemsBo();
        item.setTitle(obj.optString("title"));
        item.setE_name(obj.optString("e_name"));
        item.setDesc(obj.optString("desc"));
        item.setType(obj.optString("type"));
        item.setBg_img(obj.optString("bg_img"));
        item.setFront_img(obj.optString("front_img"));
        if (obj.has("content")) {
            Map<String, String> map = JsonResolver.jsonobjToMap(obj.getJSONObject("content"));
            item.setContent(map);
        }
        return item;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "HomeItemsBo [title=" + title + ", e_name=" + e_name + ", desc=" + desc + ", type=" + type + ", bg_img="
                + bg_img + ", front_img=" + front_img + ", content=" + content + "]";
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the e_name
     */
    public String getE_name() {
        return e_name;
    }

    /**
     * @param e_name the e_name to set
     */
    public void setE_name(String e_name) {
        this.e_name = e_name;
    }

    /**
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @param desc the desc to set
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
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
     * @return the front_img
     */
    public String getFront_img() {
        return front_img;
    }

    /**
     * @param front_img the front_img to set
     */
    public void setFront_img(String front_img) {
        this.front_img = front_img;
    }

    /**
     * @return the content
     */
    public Map<String, String> getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(Map<String, String> content) {
        this.content = content;
    }
}
