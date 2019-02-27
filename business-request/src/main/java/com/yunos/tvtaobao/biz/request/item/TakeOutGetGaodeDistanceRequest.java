package com.yunos.tvtaobao.biz.request.item;

import com.yunos.tvtaobao.biz.request.base.BaseHttpRequest;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderCancelData;

import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

public class TakeOutGetGaodeDistanceRequest extends BaseHttpRequest {

    private String appKey = "aee2300ce283a15d48d6e484bce6ab9a";
    private String origin;
    private String destination;

    public TakeOutGetGaodeDistanceRequest(String org, String dest) {
        origin = org;
        destination = dest;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String resolveResult(String result) throws Exception {
        return result;
    }

    @Override
    protected Map<String, String> getAppData() {
        Map<String, String> params = new TreeMap<String, String>();
        params.put("key", appKey);
        params.put("origin", origin);
        params.put("destination", destination);

        return params;
    }

    @Override
    protected String getHttpDomain() {
        return "http://restapi.amap.com/v4/direction/bicycling";
    }
}