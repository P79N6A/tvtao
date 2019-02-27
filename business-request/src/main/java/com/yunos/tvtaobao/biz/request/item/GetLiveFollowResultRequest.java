package com.yunos.tvtaobao.biz.request.item;


import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.LiveFollowResult;

import org.json.JSONObject;

import java.util.Map;

public class GetLiveFollowResultRequest extends BaseMtopRequest {


    private final static String API = "mtop.taobao.social.follow.weitao.add";

    public GetLiveFollowResultRequest(String followedId) {

        addParams("followedId",followedId);
        addParams("type","1");
        addParams("originBiz","taobaozhibo");


    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected LiveFollowResult resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        AppDebug.d(TAG,obj.toString());
        return JSON.parseObject(obj.toString(),LiveFollowResult.class);    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return "3.2";
    }

    @Override
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return true;
    }
}
