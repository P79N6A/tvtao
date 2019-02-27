package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.GoodsList;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GetShopItemListRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 6966879569286042791L;

    private final static String API = "com.taobao.search.api.getShopItemList";
    private String uid;
    private String shopId;
    private String sort;
    private String newDays;
    private String catId;
    private String q;
    private String startPrice;
    private String endPrice;
    private String pageSize;
    private String currentPage;

    public GetShopItemListRequest(String uid, String shopId, String sort, String newDays, String catId, String q,
                                  String startPrice, String endPrice, String pageSize, String currentPage) {
        super();
        this.uid = uid;
        this.shopId = shopId;
        this.sort = sort;
        this.newDays = newDays;
        this.catId = catId;
        this.q = q;
        this.startPrice = startPrice;
        this.endPrice = endPrice;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
    }

    @Override
    protected Map<String, String> getAppData() {
        Map<String, String> obj = new HashMap<String, String>();
        if (!TextUtils.isEmpty(uid)) {
            obj.put("uid", uid);
        }
        if (!TextUtils.isEmpty(shopId)) {
            obj.put("shopId", shopId);
        }
        if (!TextUtils.isEmpty(sort)) {
            obj.put("sort", sort);
        }
        if (!TextUtils.isEmpty(newDays)) {
            obj.put("newDays", newDays);
        }
        if (!TextUtils.isEmpty(catId)) {
            obj.put("catId", catId);
        }
        if (!TextUtils.isEmpty(q)) {
            obj.put("q", q);
        }
        if (!TextUtils.isEmpty(startPrice)) {
            obj.put("startPrice", startPrice);
        }
        if (!TextUtils.isEmpty(endPrice)) {
            obj.put("endPrice", endPrice);
        }
        if (!TextUtils.isEmpty(pageSize)) {
            obj.put("pageSize", pageSize);
        }
        if (!TextUtils.isEmpty(currentPage)) {
            obj.put("currentPage", currentPage);
        }

        return obj;
    }

    @Override
    protected GoodsList resolveResponse(JSONObject obj) throws Exception {
        if (!TextUtils.isEmpty(obj.toString())) {
            return JSON.parseObject(obj.toString(),GoodsList.class);
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
