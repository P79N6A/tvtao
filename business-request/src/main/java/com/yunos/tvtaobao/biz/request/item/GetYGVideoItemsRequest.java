package com.yunos.tvtaobao.biz.request.item;

import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.YGAcrVideoItem;
import com.yunos.tvtaobao.biz.request.core.JsonResolver;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by pan on 2017/2/17.
 */

public class GetYGVideoItemsRequest extends BaseMtopRequest {
    private String API = "mtop.taobao.tvtao.tvbuyapiservice.getepisodescheduleinfos";
    private String VERSION = "1.0";

    public GetYGVideoItemsRequest(String episodeId) {
        addParams("episodeId", episodeId);
    }
    @Override
    protected List<YGAcrVideoItem> resolveResponse(JSONObject obj) throws Exception {
        return JsonResolver.resolveYGAcrVideoItem(obj);
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
