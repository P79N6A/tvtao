package com.yunos.tvtaobao.biz.request.item;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * 淘客登录打点接口
 * Created by huangdaju on 16/9/23.
 */
public class TaokeLoginAnalysisRequest extends BaseMtopRequest {

    private String api = "mtop.taobao.tvtao.taokeservice.login";
    private String stbId = "XXX";    //机顶盒号

    public TaokeLoginAnalysisRequest(String nickname) {
        stbId = DeviceUtil.getStbID();
        if (stbId != null) {
            addParams("login", stbId);
            addParams("name", nickname);
        }
    }

    public TaokeLoginAnalysisRequest(String nickname,String bizSource) {
        stbId = DeviceUtil.getStbID();
        if (stbId != null) {
            addParams("login", stbId);
            addParams("name", nickname);
        }

        if (bizSource != null) {
            addParams("bizSource", bizSource);
        }
    }


    @Override
    protected <T> T resolveResponse(JSONObject obj) throws Exception {
        AppDebug.v(TAG, TAG + ".requestGetItemDetail_v6 --> resolveResponse = " + obj);
        return null;
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
        return api;
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
