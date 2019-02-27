package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.LiveFollowResult;
import com.yunos.tvtaobao.biz.request.bo.LiveIsFollowStatus;

import org.json.JSONObject;

import java.util.Map;

public class GetLiveIsFollowStatusRequest extends BaseMtopRequest {


    private final static String API = "mtop.cybertron.follow.detail";

    public GetLiveIsFollowStatusRequest(String followedId) {

        addParams("type","1");
        if(!TextUtils.isEmpty(followedId)) {
            addParams("followedId", followedId);
        }


    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected LiveIsFollowStatus resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        AppDebug.d(TAG,obj.toString());
        return JSON.parseObject(obj.toString(),LiveIsFollowStatus.class);    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return "3.0";
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
