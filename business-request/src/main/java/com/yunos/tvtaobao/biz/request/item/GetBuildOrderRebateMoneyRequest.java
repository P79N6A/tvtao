package com.yunos.tvtaobao.biz.request.item;


import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.BuildOrderRebateBo;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class GetBuildOrderRebateMoneyRequest extends BaseMtopRequest {

    private static  final String TAG = "GetBuildOrderRebateMoneyRequest";

    private final static String API = "mtop.taobao.tvtao.tvTaoCouponService.getTradeCoupon";

    public GetBuildOrderRebateMoneyRequest(String paramsStr,String extParams) {
        super();
        addParams("paramsStr",paramsStr);
        addParams("extParams", extParams);
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<BuildOrderRebateBo> resolveResponse(JSONObject obj) throws Exception {
        if (!obj.isNull("result")) {
            AppDebug.v(TAG,obj.toString());
            return JSON.parseArray(obj.getString("result"),BuildOrderRebateBo.class);
        }
        return null;
    }

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
