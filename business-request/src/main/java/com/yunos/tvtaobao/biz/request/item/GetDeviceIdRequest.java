package com.yunos.tvtaobao.biz.request.item;


import com.yunos.CloudUUIDWrapper;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

public class GetDeviceIdRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 4546635303661975952L;

    private static final String API = "mtop.sys.newDeviceId";

    private String apiVersion = "4.0";


    @Override
    protected Map<String, String> getAppData() {
        addParams("device_global_id", CloudUUIDWrapper.getCloudUUID());
        addParams("new_device", "false");

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

    @SuppressWarnings("unchecked")
    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {
        String deviceId = "";
        if (!obj.isNull("device_id")) {
            deviceId = obj.getString("device_id");
        }
        return deviceId;
    }

}
