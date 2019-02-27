package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.LoadingBo;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class GetAdvertsRequest extends BaseMtopRequest {

    private static final long serialVersionUID = -534597323930871635L;
    private final static String API = "com.yunos.tv.tao.itemService.getAdverts";
    private String apiVersion = "1.0";

    @Override
    protected Map<String, String> getAppData() {
        addParams("uuid", CloudUUIDWrapper.getCloudUUID());
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<LoadingBo> resolveResponse(JSONObject obj) throws Exception {
        if (!TextUtils.isEmpty(obj.optString("result"))) {
            return JSON.parseArray(obj.optString("result"),LoadingBo.class);
        }
        return null;
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return apiVersion;
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
