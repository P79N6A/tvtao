package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

public class RenderPaySuccessRequest extends BaseMtopRequest {
    private String API = "mtop.trade.receipt.renderPaySuccess";
    private String version = "1.0";


    public RenderPaySuccessRequest(String idStr) {
        super();
        if (!TextUtils.isEmpty(idStr)) {
            addParams("mainBizOrderIdsStr", idStr);
        }
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

    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {
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
}
