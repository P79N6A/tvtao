package com.yunos.tvtaobao.live.tvtaomsg.po.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by pan on 2017/2/20.
 */

public class NotifyCnrChangeItemId {
    private String topic;
    private String tid;

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
            this.tid = json.getString("tid");
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

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}
