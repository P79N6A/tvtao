package com.yunos.tvtaobao.tvlive.bo.model.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by pan on 2017/1/22.
 */

public class YGVideoInfo implements Serializable{
    /**
     * coverUrl : 央广购物
     * id : 5899395ee4b0b0081983b527
     * item : {"attrs":[{"name":"xxx"},{"name":"xxxxxx"}],"itemImageURL":"http://oss.51yaobao.tv/system/item/common_items/imgs/58761dad012ebc0fcf9b5d1d/medium_bfdd635310674f69dd143a710c83354b0a9ee715.jpg?1484135854","shortTitle":"央广购物","subTitle":"新款2016秋冬时尚半高领长袖毛衣女套头宽松毛衫加厚高领针织衫","tid":"539549240145"}
     * name : 央广购物
     * topic : b84d60be-0a38-46d4-85f3-71408b5413ef
     * videoUrl : rtmp://live.zhiping.tv/tmall-live/5899395ee4b0b0081983b527
     */

    private int type; //0 央广直播, 1 央广点播
    private String coverUrl;
    private String id;
    private String name;
    private String topic;
    private String videoUrl;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public static YGVideoInfo fromMTOP(JSONObject obj) throws JSONException {
        if (obj == null)
            return null;

        YGVideoInfo item = new YGVideoInfo();
        item.setCoverUrl(obj.getString("coverUrl"));
        item.setId(obj.getString("id"));
        item.setName(obj.getString("name"));
        item.setVideoUrl(obj.getString("videoUrl"));
        if (obj.has("topic")) {
            item.setTopic(obj.getString("topic"));
        } else {
            item.setTopic("");
        }
        return item;
    }

}
