package com.yunos.tvtaobao.biz.request.item;


import com.google.gson.JsonArray;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

public class CancelCollectionRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 8352342586453408484L;


    private final static String API = "mtop.taobao.mercury.delCollects";

    public CancelCollectionRequest(String itemId) {
        JSONArray array = new JSONArray();
        array.put(itemId);
        addParams("itemIds", array.toString());
        addParams("appName","detail");
        addParams("favType","1");
    }


    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {
        AppDebug.d("CheckFavRequest",obj+"-------------");
        String isCollect = null;
        if (!obj.isNull("result")) {
            isCollect = obj.getString("result");
        }
        return isCollect;
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
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return true;
    }

}
