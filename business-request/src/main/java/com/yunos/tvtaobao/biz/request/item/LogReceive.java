package com.yunos.tvtaobao.biz.request.item;

import android.util.Log;

import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by ljy on 2018/1/11.
 */

public class LogReceive extends BaseMtopRequest {

    private final static String API = "mtop.taobao.tvtao.tvtaoappservice.logreceive";


    public LogReceive(String osVersion, String uuid, String channelId, String code, String versionCode, String versionName, String systemInfo,String log) {
        addParams("versionCode", versionCode);
        addParams("code", code);
        addParams("versionName", versionName);
        addParams("uuid", uuid);
        addParams("channelId", channelId);
        addParams("systemInfo", systemInfo);
        addParams("osVersion", osVersion);
        addParams("log", log);
    }

    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {
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
