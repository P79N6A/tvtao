package com.yunos.tvtaobao.biz.request.item;

import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderCancelData;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderInfoDetails;

import org.json.JSONObject;

import java.util.Map;

public class TakeOutCancelOrderRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 8628119122639344258L;

    private final String API = "mtop.taobao.waimai.cheetah.order.cancel";
    private final String version = "1.0";

    public TakeOutCancelOrderRequest(String mainOrderId) {
        addParams("tbMainOrderId", mainOrderId);
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
    protected TakeOutOrderCancelData resolveResponse(JSONObject obj) throws Exception {
        return TakeOutOrderCancelData.resolverFromMtop(obj);
    }
}
