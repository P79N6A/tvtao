package com.tvtaobao.voicesdk.request;

import com.tvtaobao.voicesdk.bo.AlipayAuthResult;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.common.User;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rca on 01/09/2017.
 */

public class AlipayAuthQRGenRequest extends BaseMtopRequest {
    @Override
    protected String getApi() {
        return "mtop.taobao.tvtao.speech.alipay.authapply";
    }

    @Override
    protected String getApiVersion() {
        return "1.0";
    }

    @Override
    protected Map<String, String> getAppData() {
        HashMap<String, String > data = new HashMap<>();
        data.put("deviceId", CloudUUIDWrapper.getCloudUUID());
        data.put("tbUserId", User.getUserId());
        data.put("ver", "2.0");
        return data;
    }

    @Override
    protected AlipayAuthResult resolveResponse(JSONObject obj) throws Exception {
        return AlipayAuthResult.resolveFromJson(obj);
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
