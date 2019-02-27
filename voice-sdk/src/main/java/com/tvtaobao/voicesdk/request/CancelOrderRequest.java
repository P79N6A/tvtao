package com.tvtaobao.voicesdk.request;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by pan on 2017/8/13.
 */

public class CancelOrderRequest extends BaseMtopRequest {
    private final String TAG = "CancelOrderRequest";
    private final String API = "mtop.taobao.tvtao.speech.order.cancelDelayCreatingOrder";
    private final String VERSION = "1.0";

    public CancelOrderRequest(String outOrderId) {
        addParams("outOrderId", outOrderId);
    }

    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {
        AppDebug.e("TVTao_CancelOrder", "obj : " + obj);
        return obj.getString("cancel");
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

    @Override
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }
}
