package com.yunos.tvtaobao.biz.request.core;


import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * User: qinmiao.caoqm
 * Date: 12-11-3
 * Time: 下午7:58
 */
public class MtopResponse {

    private String api;
    private String v;

    //响应内容状态码"ret":["SUCCESS::调用成功"]
    private List<String> ret = new ArrayList<String>();
    private JSONObject data;

    public String getApi() {
        return this.api;
    }

    public String getV() {
        return this.v;
    }

    public JSONObject getData() {
        return this.data;
    }

    public List<String> getRet() {
        return this.ret;
    }

    public boolean isTopSuccess() {
        return this.ret.contains("SUCCESS");
    }

    public boolean containsCode(String code) {
        return this.ret.contains(code);
    }

    public MtopResponse edcode(String jsonSrc) throws JSONException {

        JSONObject json = new JSONObject(jsonSrc);
        if (json.has("api")) {
            this.api = json.getString("api");
        }
        if (json.has("v")) {
            this.v = json.getString("v");
        }
        if (json.has("data")) {
            this.data = json.optJSONObject("data");
        }
        if (json.has("ret")) {
            //解析成List
            String retStr = json.getString("ret");
            //去掉首尾 [ ]
            retStr = retStr.substring(1, retStr.length() - 2);
            this.ret.addAll(StringToList(retStr));
        }

        return this;
    }

    public String toString() {
        return "TopApiResponse [api=" + this.api + ", v=" + this.v + ", data=" + this.data + ", ret=" + this.ret + "]";
    }

    public static List<String> StringToList(String listText) {
        List<String> list = new ArrayList<String>();
        if (TextUtils.isEmpty(listText)) {
            list.add("ERROR");
            return list;
        }

        String[] text = listText.split(",|::|\"");
        for (String str : text) {

            if (!TextUtils.isEmpty(str)) {
                list.add(str);
            }

        }

        return list;
    }
}
