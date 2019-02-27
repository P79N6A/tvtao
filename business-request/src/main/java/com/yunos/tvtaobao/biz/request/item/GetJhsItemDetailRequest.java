package com.yunos.tvtaobao.biz.request.item;


import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.JhsItemDetail;

import org.json.JSONObject;

import java.util.Map;

public class GetJhsItemDetailRequest extends BaseMtopRequest {

    private static final long serialVersionUID = -8531841827806200048L;

    //聚划算ID
    private Long juId;

    private final static String API = "mtop.ju.detail.getByJuId";

    public GetJhsItemDetailRequest(Long juId) {
        this.juId = juId;
    }

    @Override
    protected Map<String, String> getAppData() {
        addParams("juId", String.valueOf(juId));
        return null;
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

    @SuppressWarnings("unchecked")
    @Override
    protected JhsItemDetail resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        return JSON.parseObject(obj.toString(),JhsItemDetail.class);
    }

}
