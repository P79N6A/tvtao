package com.yunos.tvtaobao.biz.request.item;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * 淘客聚划算列表打点接口
 * Created by huangdaju on 16/9/23.
 */
public class TaokeJHSListAnalysisRequest extends BaseMtopRequest {

    private String api = "mtop.taobao.tvtao.taokeservice.btoc";
    private String stbId = "";
    private String nickname = "";

    public TaokeJHSListAnalysisRequest(String stbId, String nickname) {
//        stbId = DeviceUtil.getStbID();
//        if (stbId != null) {
            addParams("login", stbId);
            addParams("name", nickname);
//        }
    }

    public TaokeJHSListAnalysisRequest(String stbId, String nickname,String bizSource) {
        addParams("login", stbId);
        addParams("name", nickname);
        addParams("bizSource", bizSource);
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
