package com.yunos.voice.request;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.tvtaobao.voicesdk.bo.PriceData;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by pan on 2017/11/3.
 */

public class PriceTrendRquest extends BaseMtopRequest {
    private String API = "mtop.taobao.tvtao.speech.item.detail.getPriceTrend";

    public PriceTrendRquest(String itemId, String key) {
        if (!TextUtils.isEmpty(itemId))
            addParams("itemId", itemId);

        if (!TextUtils.isEmpty(key))
            addParams("q", key);
    }

    @Override
    protected PriceData resolveResponse(JSONObject obj) throws Exception {
        PriceData priceData = new Gson().fromJson(obj.toString(),PriceData.class);
        return priceData;
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
    protected Map<String, String> getAppData() {
        return null;
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
