package com.yunos.tvtaobao.biz.request.item;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * 淘客商品详情打点接口
 * Created by huangdaju on 16/9/23.
 */
public class TaokeDetailAnalysisRequest extends BaseMtopRequest {

    private String api = "mtop.taobao.tvtao.taokeservice.info";


    public TaokeDetailAnalysisRequest(String stbId, String nickname, String tid, String sourceType, String sellerId) {
            addParams("login", stbId);
            addParams("name", nickname);
            addParams("tid", tid);
            addParams("source_type", sourceType);
            addParams("sellerId", sellerId);
        AppDebug.v(TAG, TAG + ".TaokeDetailAnalysisRequest --> sellerId = " + sellerId);
        AppDebug.v(TAG, TAG + ".TaokeDetailAnalysisRequest --> tid = " + tid);
    }

    public TaokeDetailAnalysisRequest(String stbId, String nickname, String tid, String sourceType, String sellerId,String bizSource) {
        addParams("login", stbId);
        addParams("name", nickname);
        addParams("tid", tid);
        addParams("source_type", sourceType);
        addParams("sellerId", sellerId);
        addParams("bizSource", bizSource);
        AppDebug.v(TAG, TAG + ".TaokeDetailAnalysisRequest --> sellerId = " + sellerId);
        AppDebug.v(TAG, TAG + ".TaokeDetailAnalysisRequest --> tid = " + tid);
        AppDebug.v(TAG, TAG + ".TaokeDetailAnalysisRequest --> bizSource = " + bizSource);
    }


    @Override
    protected <T> T resolveResponse(JSONObject obj) throws Exception {
        AppDebug.v(TAG, TAG + ".TaokeDetailAnalysisRequest --> resolveResponse = " + obj);
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
