package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pan on 2017/3/1.
 */

public class YGAttachInfo {

    /**
     * coverUrl : http://oss.51yaobao.tv/system/item/common_items/imgs/58b392c7cd897f04479dc142/medium_2173a2eb9eedc980f94349f1f18f3bc2cc68eaeb.jpg?1488163528
     * hotline : 4009002300
     * id : 58affd621749f114c010d2e6
     * itemInfo : {"attrs":[{"name":"能效等级","value":"无"},{"name":"品牌","value":"Apple/苹果"},{"name":"系列","value":"MacBook Air"}],"itemImageURL":"http://oss.51yaobao.tv/system/item/common_items/imgs/58b392c7cd897f04479dc142/medium_2173a2eb9eedc980f94349f1f18f3bc2cc68eaeb.jpg?1488163528","shortTitle":"MacBook Air","subTitle":"国行Apple/苹果 MacBook Air MMGF2CH/A13.3英寸轻薄","thirdItemId":"4444444444","tid":"530682608285"}
     * name : cibn
     * topic : 58affd621749f114c010d2e6
     * videoUrl : http://dsvideo.ott.cibntv.net/2017/02/23/3e5ada7420bb41aea969d367459bd0b8/0a0f19c0ee3bd0043bf00a12f200b6b0.m3u8
     */

    private String coverUrl;
    private String hotline;
    private String id;
    private String itemImageURL;
    private String shortTitle;
    private String subTitle;
    private String thirdItemId;
    private String tid;
    private String name;
    private String topic;
    private String videoUrl;

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getHotline() {
        return hotline;
    }

    public void setHotline(String hotline) {
        this.hotline = hotline;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemImageURL() {
        return itemImageURL;
    }

    public void setItemImageURL(String itemImageURL) {
        this.itemImageURL = itemImageURL;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getThirdItemId() {
        return thirdItemId;
    }

    public void setThirdItemId(String thirdItemId) {
        this.thirdItemId = thirdItemId;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public static YGAttachInfo fromMTOP(JSONObject obj) throws JSONException {
        if (obj == null)
            return null;

        YGAttachInfo item = new YGAttachInfo();
        item.setCoverUrl(obj.getString("coverUrl"));
        item.setHotline(obj.getString("hotline"));
        item.setId(obj.getString("id"));
        if (obj.has("itemInfo")) {
            JSONObject itemInfo = obj.getJSONObject("itemInfo");
            item.setItemImageURL(itemInfo.getString("itemImageURL"));
            item.setShortTitle(itemInfo.getString("shortTitle"));
            item.setSubTitle(itemInfo.getString("subTitle"));
            item.setThirdItemId(itemInfo.getString("thirdItemId"));
            item.setTid(itemInfo.getString("tid"));
        } else {
            item.setItemImageURL("");
            item.setShortTitle("");
            item.setSubTitle("");
            item.setThirdItemId("");
            item.setTid("");
        }
        item.setName(obj.getString("name"));
        item.setTopic(obj.getString("topic"));
        item.setVideoUrl(obj.getString("videoUrl"));

        return item;
    }
}
