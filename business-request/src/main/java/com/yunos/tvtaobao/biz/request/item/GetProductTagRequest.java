package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.ProductTagBo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by pan on 2017/3/23.
 */

public class GetProductTagRequest extends BaseMtopRequest {
    private final String API = "mtop.taobao.tvtao.itemservice.getdetail";
    private final String VERSION = "1.0";
    private String detail_v;

    public GetProductTagRequest(String itemId, List<String> list, boolean isZTC, String source,boolean isPre,String amount,String extParams) {
        addParams("itemId", itemId);

        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        for (int i = 0 ; i < list.size() ; i++) {
            array.put(list.get(i));
        }

        try {
            object.put("traceRoutes", array);
            object.put("price",amount);
            String tvOptions = TvOptionsConfig.getTvOptions();
            if (!TextUtils.isEmpty(tvOptions)) {
                object.put("tvOptions",tvOptions);
            }
            AppDebug.e("GetProductTagRequest", object.toString());
            addParams("exParams", object.toString());
        } catch (JSONException e) {

        }
        if (detail_v != null) {
            addParams("detail_v", detail_v);
        }
        String appkey = SharePreferences.getString("device_appkey", "");
        String brandName = SharePreferences.getString("device_brandname", "");
        if (appkey.equals("10004416") && brandName.equals("海尔")) {
            addParams("appKey", "2017092310");
        }else {
//            addParams("appKey", "2017092310");
            addParams("appKey", Config.getChannel());
        }

        AppDebug.e("GetProductTagRequest", "isPre = " + isPre);
        AppDebug.e("GetProductTagRequest", "amount = " + amount);

        addParams("isPre",String.valueOf(isPre));

        if(amount!=null) {
            addParams("amount", amount);
        }

        addParams("v","1.0");
        addParams("extParams", extParams);
    }

    @Override
    protected ProductTagBo resolveResponse(JSONObject obj) throws Exception {

        AppDebug.e("GetProductTagRequest", "obj = " + obj);
        AppDebug.e("GetProductTagRequest", "resolveResponse has tag " + obj.has("tag"));
        ProductTagBo productTagBo = new ProductTagBo();

        if (obj.has("tag")) {
            JSONObject tag = obj.getJSONObject("tag");
            productTagBo = JSON.parseObject(tag.toString(),productTagBo.getClass());
        }

        if(obj.has("item")){
            JSONObject item = obj.getJSONObject("item");
            if(item.has("pointBlacklisted")){
                productTagBo.setPointBlacklisted(item.getString("pointBlacklisted"));
            }
        }

        if(obj.has("couponType")){
            productTagBo.setCouponType(obj.getString("couponType"));
        }


        return productTagBo;
    }

    @Override
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return VERSION;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }
}
