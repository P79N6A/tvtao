package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.ShopDetailData;
import com.yunos.tvtaobao.biz.request.bo.ShopSearchResultBean;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by chenjiajuan on 17/12/15.
 *
 * @describe
 */

public class ShopSearchRequest extends BaseMtopRequest {

    private String API="mtop.taobao.wireless.item.search";
    private String version="1.0";

    public ShopSearchRequest(String shopId, String keyword, String orderType, int pageSize, int pageNo){
        if (!TextUtils.isEmpty(shopId)){
            addParams("storeId",shopId);
        }
        if (!TextUtils.isEmpty(keyword)){
            addParams("keyword",keyword);
        }
        if (!TextUtils.isEmpty(orderType)){
            addParams("orderType",orderType);
        }

        addParams("pageSize",pageSize+"");
        addParams("pageNo",pageNo+"");

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
    protected ShopSearchResultBean resolveResponse(JSONObject obj) throws Exception {
        if (obj==null){
            return  null;
        }
        return JSON.parseObject(obj.toString(),ShopSearchResultBean.class);
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
