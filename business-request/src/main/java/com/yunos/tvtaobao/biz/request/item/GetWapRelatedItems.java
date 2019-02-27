package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.RelatedItem;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetWapRelatedItems extends BaseMtopRequest {

    private static final long serialVersionUID = 8769956883600572543L;
    private String sellerId;
    private String itemId;
    private final static String API = "mtop.shop.getWapRelatedItems";

    public GetWapRelatedItems(String itemId, String sellerId) {
        super();
        this.sellerId = sellerId;
        this.itemId = itemId;
    }

    @Override
    protected Map<String, String> getAppData() {
        Map<String, String> obj = new HashMap<String, String>();
        if (!TextUtils.isEmpty(sellerId)) {
            obj.put("sellerId", sellerId);
        }
        if (!TextUtils.isEmpty(itemId)) {
            obj.put("itemId", itemId);
        }

        return obj;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<RelatedItem> resolveResponse(JSONObject obj) throws Exception {
        if (!obj.isNull("itemList")) {
            return JSON.parseArray(obj.getString("itemList"), RelatedItem.class);
        }
        return null;
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return true;
    }

    @Override
    protected String getApiVersion() {
        return "1.0";
    }
}
