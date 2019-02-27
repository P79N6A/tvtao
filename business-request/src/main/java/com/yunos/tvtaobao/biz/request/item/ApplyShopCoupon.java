package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApplyShopCoupon extends BaseMtopRequest {

    private static final long serialVersionUID = -2588212548156853370L;
    private String supplierId;
    private String spreadId;
    private final static String API = "mtop.taobao.buyerResourceMtopWriteService.applyCoupon";

    public ApplyShopCoupon(String sellerId, String activityId) {
        super();
        this.supplierId = sellerId;
        this.spreadId = activityId;
    }

    @Override
    protected Map<String, String> getAppData() {
        Map<String, String> obj = new HashMap<String, String>();
        if (!TextUtils.isEmpty(supplierId)) {
            obj.put("supplierId", supplierId);
        }
        if (!TextUtils.isEmpty(spreadId)) {
            obj.put("spreadId", spreadId);
        }

        return obj;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected JSONObject resolveResponse(JSONObject obj) throws Exception {
        return obj;
    }

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
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return true;
    }

}
