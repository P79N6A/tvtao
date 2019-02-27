package com.yunos.voice.request;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.voice.Do.NlpDO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pan on 2017/8/8.
 */

public class NlpRequest extends BaseMtopRequest {
    private final String API = "mtop.taobao.tvtao.speech.idst.nls.understand";
    private final String VERSION = "1.0";

    public NlpRequest(String asr_text) {
        addParams("q", asr_text);
    }

    @Override
    protected JSONObject resolveResponse(JSONObject obj) throws Exception {
//        AppDebug.e("TVTao_Nlp", "obj : " + obj);
//        JSONArray frames = obj.getJSONArray("frames");
//        List<NlpDO> nlpDOs = new ArrayList<>();
//        for (int i = 0 ; i < frames.length() ; i++) {
//            nlpDOs.add(NlpDO.resolverDataFromMTOP(frames.getJSONObject(i)));
//        }
        return obj;
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return VERSION;
    }

    @Override
    protected Map<String, String> getAppData() {
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
}
