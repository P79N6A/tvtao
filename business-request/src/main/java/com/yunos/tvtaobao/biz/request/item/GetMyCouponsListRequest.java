/**
 * $
 * PROJECT NAME: business-request
 * PACKAGE NAME: com.yunos.tvtaobao.biz.request.item
 * FILE NAME: GetShopCouponsListRequest.java
 * CREATED TIME: 2016年3月3日
 * COPYRIGHT: Copyright(c) 2013 ~ 2016 All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.item;


import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.MyCouponsList;

import org.json.JSONObject;

import java.util.Map;

/**
 * 获取我的网店优惠券或生活优惠券
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2016年3月3日 下午4:06:00
 */
public class GetMyCouponsListRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 1990846608850777395L;
    private String API = "mtop.wallet.coupon.getmycouponlistbytype";
    private String v = "4.0";

    public GetMyCouponsListRequest(String bizType, String couponType) {
        addParams("bizType", bizType);
        addParams("couponType", couponType);
        addParams("source", "0");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected MyCouponsList resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        return JSON.parseObject(obj.toString(),MyCouponsList.class);
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
        return v;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }
}
