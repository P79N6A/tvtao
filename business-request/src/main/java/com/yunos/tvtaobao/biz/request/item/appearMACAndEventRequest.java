package com.yunos.tvtaobao.biz.request.item;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by libin on 16/11/30.
 */

public class appearMACAndEventRequest extends BaseMtopRequest {

    public appearMACAndEventRequest(String appKey,String o2oShopId,String metaType,String currentMeta,String duration,String routerMAC,String timestamp,String signature,String signVersion) {
        addParams("appKey",appKey);
        addParams("o2oShopId",o2oShopId);
        addParams("metaType",metaType);
        addParams("currentMeta",currentMeta);
        addParams("duration",duration);
        addParams("routerMAC",routerMAC);
        addParams("timestamp",timestamp);
        addParams("signature",signature);
        addParams("signVersion",signVersion);
    }

    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {
        obj.toString();

        AppDebug.e("TAG","上传mac和事件"+ obj.toString());

        return obj.toString();
    }

    @Override
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }

    @Override
    protected String getApi() {
        return "mtop.taobao.tvtao.tvtaoshakeservice.uploadrouterinfo";
    }

    @Override
    protected String getApiVersion() {
        return "1.0";
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }
}
