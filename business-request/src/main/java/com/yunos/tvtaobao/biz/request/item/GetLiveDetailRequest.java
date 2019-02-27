package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.LiveDetailBean;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by libin on 16/9/17.
 */

public class GetLiveDetailRequest extends BaseMtopRequest {
    private String API = "mtop.mediaplatform.live.livedetail";
    private String version = "1.0";

    public GetLiveDetailRequest(String liveId, String creatorId) {
        if (liveId != null)
            addParams("liveId",liveId);
        if (creatorId != null)
            addParams("creatorId",creatorId);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected LiveDetailBean resolveResponse(JSONObject obj) throws Exception {
        if (!TextUtils.isEmpty(obj.toString())) {

            return JSON.parseObject(obj.toString(),LiveDetailBean.class);
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
        return version;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

}
