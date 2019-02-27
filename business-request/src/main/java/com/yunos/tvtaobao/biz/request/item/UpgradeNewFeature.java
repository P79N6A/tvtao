package com.yunos.tvtaobao.biz.request.item;

import android.util.Log;

import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by LJY on 18/3/23.
 * 新版本特性弹框
 */

public class UpgradeNewFeature extends BaseMtopRequest {

    private final static String API = "mtop.taobao.tvtao.tvtaoappservice.current";


    public UpgradeNewFeature(String version, String uuid, String channelId, String code, String versionCode, String versionName, String systemInfo,String umtoken,String modelInfo) {
        addParams("versionCode", versionCode);
        addParams("code", code);
        addParams("versionName", versionName);
        addParams("uuid", uuid);
        addParams("channelId", channelId);
        addParams("systemInfo", systemInfo);
        addParams("version", version);
        addParams("umToken", umtoken);
        addParams("modelInfo", modelInfo);


    }

    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {
//        AppDebug.d(TAG, "obj " + obj.toString());
        Log.d(TAG, "obj " + obj.toString());
        if (obj != null)
            return obj.toString();
        else
            return null;
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
        return "1.0";
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }
}
