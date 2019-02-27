package com.yunos.tvtaobao.request.item;


import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.bo.DeviceBo;

import org.json.JSONObject;

import java.util.Map;

public class GetDeviceRequest extends BaseMtopRequest {

    private final static String API = "mtop.taobao.tvtao.dicservice.getdevice";
    private String apiVersion = "1.0";

    public GetDeviceRequest(String model) {
        addParams("model", model);
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected DeviceBo resolveResponse(JSONObject obj) throws Exception {
        AppDebug.d(TAG,"GetDeviceRequest data " + obj.toString());
        if (obj != null) {
//            AppDebug.d(TAG,"GetDeviceRequest data " + (JSON.parseObject(obj.toString(), DeviceBo.class)));
            return JSON.parseObject(obj.toString(), DeviceBo.class);
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
