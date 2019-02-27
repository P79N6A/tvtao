package com.yunos.tvtaobao.biz.request.item;


import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

public class GetTaobaoPoint extends BaseMtopRequest {

    private static final long serialVersionUID = 6403615798555209809L;

    private final static String API = "mtop.taobao.tvtao.tvtaopointservice.querycurrenttotal";

    public GetTaobaoPoint() {

    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        AppDebug.d(TAG,obj.toString());
        return obj.getString("ct");
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
