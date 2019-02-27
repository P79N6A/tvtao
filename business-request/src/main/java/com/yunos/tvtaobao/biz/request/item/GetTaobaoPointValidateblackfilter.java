package com.yunos.tvtaobao.biz.request.item;


import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

public class GetTaobaoPointValidateblackfilter extends BaseMtopRequest {

    private static final long serialVersionUID = 6403615798555209809L;

    private final static String API = "mtop.taobao.tvtao.commonservice.validateblackfilter";

    public GetTaobaoPointValidateblackfilter(String categoryId, String itemId) {
        addParams("categoryId", categoryId);
        addParams("itemId", itemId);
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Boolean resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return false;
        }

        return obj.optBoolean("result");
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
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }
}
