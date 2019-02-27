/**
 * $
 * PROJECT NAME: business-request
 * PACKAGE NAME: com.yunos.tvtaobao.biz.request.item
 * FILE NAME: GetCouponRecommendList.java
 * CREATED TIME: 2016年4月25日
 * COPYRIGHT: Copyright(c) 2013 ~ 2016 All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.item;


import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.CouponRecommendList;

import org.json.JSONObject;

import java.util.Map;

public class GetCouponRecommendList extends BaseMtopRequest {

    private static final long serialVersionUID = -5119194448917610828L;
    private String API = "mtop.wallet.coupon.getRecommendList";
    private String version = "1.0";

    public GetCouponRecommendList(String sellerId, String couponId) {
        addParams("sellerId", sellerId);
        addParams("couponId", couponId);
        addParams("couponType", "1");
        addParams("bizType", "1");
        addParams("source", "2");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected CouponRecommendList resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        return JSON.parseObject(obj.toString(),CouponRecommendList.class);
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
