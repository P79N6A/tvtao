package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.AddBagBo;
import com.yunos.tvtaobao.biz.request.bo.TakeOutBag;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by chenjiajuan on 17/12/22.
 *
 * @describe
 */

public class TakeOutGetBagRequest extends BaseMtopRequest {
    private String API="mtop.taobao.waimai.giraffe.singleshoppingcart.get";
    private String version="1.0";


    public TakeOutGetBagRequest(String storeId,String longitude,String latitude,String extFeature){
        if (!TextUtils.isEmpty(storeId)){
            addParams("storeId",storeId);
        }
        if (!TextUtils.isEmpty(longitude)){
            addParams("longitude",longitude);
        }
        if (!TextUtils.isEmpty(latitude)){
            addParams("latitude",latitude);
        }
        if (!TextUtils.isEmpty(extFeature)){
            addParams("extFeature",extFeature);
        }

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
    protected TakeOutBag resolveResponse(JSONObject obj) throws Exception {
        if (obj==null){
            return  null;
        }else {
            AppDebug.e("TakeOutGetBagRequest","response = "+obj.toString());
           return JSON.parseObject(obj.toString(),TakeOutBag.class);
        }
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
