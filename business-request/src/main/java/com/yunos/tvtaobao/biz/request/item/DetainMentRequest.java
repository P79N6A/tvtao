package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetainMentRequest extends BaseMtopRequest {

    /**
     *
     */
    private static final long serialVersionUID = 7592070242650783051L;

    private String API = "mtop.relationrecommend.WirelessRecommend.recommend";
    private String version = "2.0";

    private String user_Id = "";

    public DetainMentRequest(String user_Id) {
        this.user_Id = user_Id;

        addParams("appId", "987");
        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(user_Id)) {
            params.put("user_Id", user_Id);
        }
        params.put("callSource", "tvtaobao");
        params.put("platform", "android");
        params.put("channel", Config.getTTid());
        //"pageSize": "200"
        params.put("pageSize","200");
        String param = JSON.toJSONString(params);
        AppDebug.d("DetainMentRequest", param);
        addParams("params", param);

    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return version;
    }

    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {
        if (obj != null)
            return obj.toString();
        return null;
    }

    @Override
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return true;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }
}
