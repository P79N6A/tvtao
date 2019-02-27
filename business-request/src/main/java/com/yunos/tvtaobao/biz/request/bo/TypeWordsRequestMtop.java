package com.yunos.tvtaobao.biz.request.bo;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by libin on 16/9/29.
 */

public class TypeWordsRequestMtop extends BaseMtopRequest {

    public TypeWordsRequestMtop(String orgin) {
        addParams("sentence",orgin);
    }

    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {
        obj.toString();

        AppDebug.e("TAG","语音 MTOPS-----yuan"+ obj.toString());

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
        return "mtop.taobao.tvtao.segmentationservice.tagging";
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
