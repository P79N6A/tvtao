package com.yunos.tvtaobao.biz.request.item;


import android.util.Log;

import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

/**
 * 获取后台类目信息
 */
public class GetStdCats extends BaseMtopRequest {

    private static final long serialVersionUID = 6403615798555209809L;

    private final static String API = "mtop.taobao.iforest.getstdcats";

    public GetStdCats(String categoryId ) {
        addParams("cat_ids", categoryId);
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        Log.d(TAG,obj.toString());
        JSONArray jsonArray = obj.optJSONArray("result");
        JSONArray pathArray =jsonArray.getJSONObject(0).getJSONArray("path");
        return pathArray.getJSONObject(0).optString("catId");
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
