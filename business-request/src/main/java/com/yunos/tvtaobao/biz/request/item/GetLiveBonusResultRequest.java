package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.SystemUtil;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.LiveBonusResult;
import com.yunos.tvtaobao.biz.request.bo.MyCouponsList;

import org.json.JSONObject;

import java.util.Map;

public class GetLiveBonusResultRequest extends BaseMtopRequest {


    private final static String API = "mtop.taobao.tvtao.lottery.sendbenefit";

    public GetLiveBonusResultRequest(String bizId, String type,String asac) {

        addParams("bizId",bizId);
        addParams("type",type);
        if(!TextUtils.isEmpty(CloudUUIDWrapper.getCloudUUID())) {
            addParams("uuid", CloudUUIDWrapper.getCloudUUID());
        }
        if(!TextUtils.isEmpty(asac)) {
            addParams("asac", asac);
        }

    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected LiveBonusResult resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        AppDebug.d(TAG,obj.toString());
        return JSON.parseObject(obj.toString(),LiveBonusResult.class);    }

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
