package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.Cat;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetShopCatInfoRequest extends BaseMtopRequest {

    private static final long serialVersionUID = -2585365513297237130L;
    private String sellerId;
    private String shopId;
    //缺少拿不到数据
    private String catId = "22";
    private final static String API = "com.taobao.search.api.getCatInfoInShop";

    public GetShopCatInfoRequest(String sellerId, String shopId) {
        super();
        this.sellerId = sellerId;
        this.shopId = shopId;
    }

    @Override
    protected Map<String, String> getAppData() {
        Map<String, String> obj = new HashMap<String, String>();
        if (!TextUtils.isEmpty(sellerId)) {
            obj.put("sellerId", sellerId);
        }
        if (!TextUtils.isEmpty(shopId)) {
            obj.put("shopId", shopId);
        }
        if (!TextUtils.isEmpty(catId)) {
            obj.put("catId", catId);
        }

        return obj;
    }

    @Override
    protected List<Cat> resolveResponse(JSONObject obj) throws Exception {
        if (!obj.isNull("cats")) {
            return JSON.parseArray(obj.getString("cats"), Cat.class);
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
