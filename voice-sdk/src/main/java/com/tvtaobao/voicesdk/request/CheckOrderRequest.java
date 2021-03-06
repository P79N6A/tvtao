package com.tvtaobao.voicesdk.request;

import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by pan on 2017/9/23.
 */

public class CheckOrderRequest extends BaseMtopRequest {
    private String API = "mtop.taobao.tvtao.orderextservice.searchLastTradeLogistics";

    public CheckOrderRequest(String timeText, String q, String start, String end) {
        addParams("timeText", timeText);
        addParams("q", q);
        addParams("beginTime", start);
        addParams("endTime", end);
        addParams("v","2.0");
    }

    public CheckOrderRequest(String timeText, String q, String start, String end, String v) {
        addParams("timeText", timeText);
        addParams("q", q);
        addParams("beginTime", start);
        addParams("endTime", end);
        if (TextUtils.isEmpty(v)) {
            addParams("v", "2.0");
        } else {
            addParams("v", v);
        }
    }

    @Override
    protected JSONObject resolveResponse(JSONObject obj) throws Exception {
        AppDebug.w("TVTao_CheckOrderRequest", "obj : " + obj);
        return obj;
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

    @Override
    public boolean getNeedEcode() {
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return true;
    }
}
