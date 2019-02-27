package com.yunos.tvtaobao.biz.request.item;


import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.LiveBonusResult;
import com.yunos.tvtaobao.biz.request.bo.LiveBonusTimeItem;
import com.yunos.tvtaobao.biz.request.bo.LiveBonusTimeResult;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class GetLiveBonusTimeRequest extends BaseMtopRequest {


    private final static String API = "mtop.taobao.tvtao.welafretime.getTimeList";

    public GetLiveBonusTimeRequest() {

    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }
    @SuppressWarnings("unchecked")
    @Override
    protected LiveBonusTimeResult resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        AppDebug.d(TAG,obj.toString());
        return JSON.parseObject(obj.toString(),LiveBonusTimeResult.class);    }


    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return "1.0";
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
