package com.yunos.tvtaobao.biz.request.item;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * <pre>
 *     author : xutingting
 *     e-mail : xutingting@zhiping.tech
 *     time   : 2018/01/09
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ScanBindRequest extends BaseMtopRequest {
    private String API = "mtop.taobao.tvtao.user.bind";
    private String version = "1.0";

    public ScanBindRequest(String uuid, String deviceId,String appKey) {
        addParams("uuid",uuid);
        addParams("deviceId",deviceId);
        addParams("appKey",appKey);
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
    protected <T> T resolveResponse(JSONObject obj) throws Exception {
        AppDebug.v(TAG, TAG + ".ScanBindRequest --> resolveResponse = " + obj);
        return null;
    }

    @Override
    public boolean getNeedEcode() {
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return true;
    }
}
