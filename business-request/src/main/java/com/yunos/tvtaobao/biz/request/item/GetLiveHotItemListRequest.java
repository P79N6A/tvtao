package com.yunos.tvtaobao.biz.request.item;

import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.TBaoShopBean;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by libin on 16/9/18.
 */

public class GetLiveHotItemListRequest extends BaseMtopRequest {
    private String API = "mtop.taobao.tvtao.tvtaoliveservice.gettaobaolivedetailitemlist";
    private String version = "1.0";

    public GetLiveHotItemListRequest(String type, String liveId, String creatorId) {
        addParams("type", type);
        if (liveId != null)
            addParams("liveId", liveId);
        if (creatorId != null)
            addParams("creatorId", creatorId);
    }

    @Override
    protected TBaoShopBean resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        return JSON.parseObject(obj.toString(), TBaoShopBean.class);
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
        return version;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }
}
