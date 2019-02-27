package com.yunos.tvtaobao.zhuanti.net.item;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.zhuanti.bo.TvIntegration;

import org.json.JSONObject;

import java.util.Map;


/**
 * Created by chenjijuan on 17/4/25.
 */

public class GetVideoPointSchemeRequest extends BaseMtopRequest {

    private String API = "mtop.taobao.tvtao.interactionsservice.videoPointSchemeSchedules";
    private String version = "1.0";

    public GetVideoPointSchemeRequest() {
        String appKey = Config.getAppKey();
        AppDebug.e(TAG, " appKey = " + appKey);
        if (!TextUtils.isEmpty(appKey))
            addParams("appkey", appKey);
    }

    @Override
    protected TvIntegration resolveResponse(JSONObject obj) throws Exception {
        if (!TextUtils.isEmpty(obj.toString())) {
            AppDebug.e(TAG, "resolveResponse TvIntegration = " + obj.toString());
            return JSON.parseObject(obj.toString(), TvIntegration.class);
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
