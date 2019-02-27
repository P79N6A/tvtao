package com.yunos.tvtaobao.biz.request.item;


import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.OrderListData;

import org.json.JSONObject;

import java.util.Map;

public class GetOrderListRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 8628119122639344258L;

    private final String API = "mtop.order.queryBoughtList";
    private final String version = "4.0";

    public GetOrderListRequest() {
        addParams("page", "1");
        addParams("tabCode", "all");
        addParams("appName", "tborder");
        addParams("appVersion", "1.0");
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
    public boolean getNeedEcode() {
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected OrderListData resolveResponse(JSONObject obj) throws Exception {
        return OrderListData.resolverFromMtop(obj);
    }
}
