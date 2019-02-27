package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.AddBagBo;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by chenjiajuan on 17/12/22.
 *
 * @describe
 */

public class TakeOutAddBagRequest extends BaseMtopRequest {
    private String API="mtop.trade.addbag";
    private String version="3.1";

    public TakeOutAddBagRequest(String itemId,String skuId,String quantity,String exParams,String cartFrom){
        if (!TextUtils.isEmpty(itemId)){
            addParams("itemId",itemId);
        }
        if (!TextUtils.isEmpty(skuId)){
            addParams("skuId",skuId);
        }
        if (!TextUtils.isEmpty(quantity)){
            addParams("quantity",quantity);
        }
        if (!TextUtils.isEmpty(exParams)){
            addParams("exParams",exParams);
        }
        if (!TextUtils.isEmpty(cartFrom)){
            addParams("cartFrom",cartFrom);
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
    protected AddBagBo resolveResponse(JSONObject obj) throws Exception {
        if (obj==null){
            return  null;
        }else {
           return JSON.parseObject(obj.toString(),AddBagBo.class);
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
