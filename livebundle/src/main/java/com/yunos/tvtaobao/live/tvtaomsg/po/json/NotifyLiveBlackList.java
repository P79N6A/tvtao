package com.yunos.tvtaobao.live.tvtaomsg.po.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * 订阅
 * Created by zhiping on 16/12/21.
 */
public class NotifyLiveBlackList {
    private String topic;

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

    //解码
    public void decode(byte[] brr) {
        try {
            JSONObject json = JSON.parseObject(new String(brr,"UTF-8"));

            this.topic = json.getString("topic");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
