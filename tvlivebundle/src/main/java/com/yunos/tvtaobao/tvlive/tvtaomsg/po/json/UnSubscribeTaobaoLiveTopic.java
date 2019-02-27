package com.yunos.tvtaobao.tvlive.tvtaomsg.po.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * 取消订阅订阅
 * Created by zhiping on 16/12/21.
 */
public class UnSubscribeTaobaoLiveTopic {
    private int type;
    private String topic;
    private String userid;

    //解码
    public void decode(byte[] brr) {
        try {
            JSONObject json = JSON.parseObject(new String(brr,"UTF-8"));

            this.type = json.getInteger("type");
            this.topic = json.getString("topic");
            this.userid = json.getString("userid");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //解码
    public byte[] encode() {
        try {
            String str = JSON.toJSONStringWithDateFormat(this, "yyyy-MM-dd HH:mm:ss");

            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "UnSubscribeTaobaoLiveTopic{" +
                "type=" + type +
                ", topic='" + topic + '\'' +
                ", userid='" + userid + '\'' +
                '}';
    }
}
