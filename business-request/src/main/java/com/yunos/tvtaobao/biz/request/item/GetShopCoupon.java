package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.User;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.ShopCoupon;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetShopCoupon extends BaseMtopRequest {

    private static final long serialVersionUID = -1818780348054330252L;
    private String sellerId;
    private final static String API = "mtop.shop.querybuyerbonus";

    public GetShopCoupon(String sellerId) {
        super();
        this.sellerId = sellerId;
    }

    @Override
    protected Map<String, String> getAppData() {
        Map<String, String> obj = new HashMap<String, String>();
        if (!TextUtils.isEmpty(sellerId)) {
            obj.put("sellerId", sellerId);
        }
        if (!TextUtils.isEmpty(User.getUserId())) {
            obj.put("userId", User.getUserId());
        }

        return obj;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<ShopCoupon> resolveResponse(JSONObject obj) throws Exception {
        if (!obj.isNull("result")) {
            return JSON.parseArray(obj.getString("result"),ShopCoupon.class);
        }
        return null;
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return "2.0";
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
