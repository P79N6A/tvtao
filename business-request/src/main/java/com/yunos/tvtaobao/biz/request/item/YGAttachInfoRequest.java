package com.yunos.tvtaobao.biz.request.item;

import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.YGAttachInfo;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by pan on 2017/3/1.
 */

public class YGAttachInfoRequest extends BaseMtopRequest {
    private final String API = "mtop.taobao.tvtao.tvtaoliveservice.getattachinfo";
    private final String VERSION = "1.0";

    public YGAttachInfoRequest(String liveId) {
        addParams("liveId", liveId);
    }
    @Override
    protected YGAttachInfo resolveResponse(JSONObject obj) throws Exception {
        if (obj != null) {
            String result = obj.getString("result");
            JSONObject object = new JSONObject(result);
            return YGAttachInfo.fromMTOP(object);
        }
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
}
