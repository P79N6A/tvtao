package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.ShopDetailData;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by chenjiajuan on 17/12/15.
 *
 * @describe
 */

public class ShopDetailDataRequest extends BaseMtopRequest {

    private String API="mtop.taobao.waimai.giraffe.queryShopDetail";
    private String version="2.0";

    public ShopDetailDataRequest(String shopId, String serviceId, String longitude,String latitude,String extFeature , String pageNo, String genreIds){
        if (!TextUtils.isEmpty(shopId)){
            addParams("storeId",shopId);
        }
        if (!TextUtils.isEmpty(serviceId)){
            addParams("serviceId",serviceId);
        }
        if (!TextUtils.isEmpty(extFeature)){
            addParams("extFeature",extFeature);
        }
        if (!TextUtils.isEmpty(latitude)){
            //纬度
            addParams("latitude",latitude);
        }
        if (!TextUtils.isEmpty(longitude)){
            //经度
            addParams("longitude",longitude);
        }

        if (!TextUtils.isEmpty(pageNo)){
            //经度
            addParams("pageNo",pageNo);
        }

        if (!TextUtils.isEmpty(genreIds)){
            //经度
            addParams("genreIds",genreIds);
        }

        addParams("pageSize","200");
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

    @Override
    protected ShopDetailData resolveResponse(JSONObject obj) throws Exception {
        if (obj==null){
            return  null;
        }
        return JSON.parseObject(obj.toString(),ShopDetailData.class);
    }

    @Override
    public boolean getNeedEcode() {
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }
}
