package com.yunos.tvtaobao.biz.request.ztc;

import android.text.TextUtils;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class KMRefluxRequest extends BaseMtopRequest {
    private static final String API = "mtop.taobao.tvtao.kmRefluxService.reflux";
    private static final String VERSION = "1.0";

    @Override
    protected JSONObject resolveResponse(JSONObject obj) throws Exception {
        return obj;
    }

    public KMRefluxRequest(String itemId, String title, String picUrl) {
        JSONObject refluxParams = new JSONObject();
        try {
            refluxParams.put("appkey", Config.getChannel());
            refluxParams.put("business", "tvtaobao");
            refluxParams.put("picUrl", TextUtils.isEmpty(picUrl) ? "" : picUrl);
            refluxParams.put("title", TextUtils.isEmpty(title) ? "" : title);
            refluxParams.put("umtoken", Config.getUmtoken(CoreApplication.getApplication()));
            refluxParams.put("version", Config.getVersionName(CoreApplication.getApplication()));
            addParams("refluxParams", refluxParams.toString());
            addParams("itemId", itemId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return VERSION;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }
}
