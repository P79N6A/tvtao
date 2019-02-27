package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pan on 2017/2/17.
 */

public class YGAcrVideoItem {
    /**
     * id : 5862316035c4726b1bd4093b
     * startAt : 1860
     * endAt : 1905
     * duration : 45
     * iconUrl : http://oss.51yaobao.tv/system/meta_icon/5865f60e35c4726f3cd40b0d.png?1483077134
     * itemInfo : {"tid":"520446224066","subTitle":"秋装新款针织衫汤唯同款外套开衫女短款毛衣薄外搭","shortTitle":"命中注定 同款开衫","itemImageURL":"http://oss.51yaobao.tv/system/item/items/tvbuy/5865/d67d/35c4/726f/3cd4/0ad7/5865d67d35c4726f3cd40ad7.png?1483069205"}
     */

    private String id;
    private String startAt;
    private String endAt;
    private String duration;
    private String iconUrl;
    private String hotline;
    private String tid;
    private String thirdItemId;
    private String subTitle;
    private String shortTitle;
    private String itemImageURL;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getEndAt() {
        return endAt;
    }

    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getHotline() {
        return hotline;
    }

    public void setHotline(String hotline) {
        this.hotline = hotline;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getThirdItemId() {
        return thirdItemId;
    }

    public void setThirdItemId(String thirdItemId) {
        this.thirdItemId = thirdItemId;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getItemImageURL() {
        return itemImageURL;
    }

    public void setItemImageURL(String itemImageURL) {
        this.itemImageURL = itemImageURL;
    }

    public static YGAcrVideoItem fromMTOP(JSONObject obj) throws JSONException {
        if (obj == null)
            return null;
        YGAcrVideoItem item = new YGAcrVideoItem();
        item.setId(obj.getString("id"));
        item.setStartAt(obj.getString("startAt"));
        item.setEndAt(obj.getString("endAt"));
        item.setDuration(obj.getString("duration"));
        item.setIconUrl(obj.getString("iconUrl"));
        item.setHotline(obj.getString("hotline"));
        JSONObject itemInfo = obj.getJSONObject("itemInfo");
        item.setTid(itemInfo.getString("tid"));
        item.setThirdItemId(itemInfo.getString("thirdItemId"));
        item.setSubTitle(itemInfo.getString("subTitle"));
        item.setShortTitle(itemInfo.getString("shortTitle"));
        item.setItemImageURL(itemInfo.getString("itemImageURL"));
        return item;
    }
}
