package com.yunos.tvtaobao.tradelink.request;


import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.tradelink.buildorder.bean.PaidAdvertBo;

import org.json.JSONObject;

import java.util.Map;

public class PaidAdvertRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 1108244529146807654L;

    private static final String API = "com.yunos.tv.tao.itemService.getPaidAdvert";

    private String bizOrderId;

    public PaidAdvertRequest(String bizOrderId) {
        this.bizOrderId = bizOrderId;
    }

    @Override
    protected Map<String, String> getAppData() {
        addParams("uuid", CloudUUIDWrapper.getCloudUUID());
        addParams("version", Config.getMtopApiVersion());
        addParams("bizOrderId", bizOrderId);
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected PaidAdvertBo resolveResponse(JSONObject obj) throws Exception {
        return PaidAdvertBo.fromMTOP(obj);
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
