package com.yunos.tvtaobao.zhuanti.net.item;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.zhuanti.bo.TvGetIntegration;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by chenjiajuan on 17/4/25.
 */

public class GetVideoPointsRequest extends BaseMtopRequest {
    private String API = "mtop.taobao.tvtao.tvtaopointservice.getPoints";
    private String version = "1.0";
    private String appkey = Config.getAppKey();
    private String deviceId = CloudUUIDWrapper.getCloudUUID();


    public GetVideoPointsRequest(String pointSchemeId) {
        if (!TextUtils.isEmpty(pointSchemeId)) {
            addParams("pointSchemeId", pointSchemeId);
        }
        if (!TextUtils.isEmpty(appkey)) {
            addParams("appkey", appkey);
        }
        if (!TextUtils.isEmpty(deviceId)) {
            addParams("deviceId", deviceId);
        }

    }


    @Override
    protected TvGetIntegration resolveResponse(JSONObject obj) throws Exception {
        if (!TextUtils.isEmpty(obj.toString())) {
            AppDebug.e(TAG, "resolveResponse TvIntegration = " + obj.toString());
            return JSON.parseObject(obj.toString(), TvGetIntegration.class);
        }
        return null;
    }

    @Override
    public boolean getNeedEcode() {
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return true;
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
    protected Map<String, String> getAppData() {
        return null;
    }
}
