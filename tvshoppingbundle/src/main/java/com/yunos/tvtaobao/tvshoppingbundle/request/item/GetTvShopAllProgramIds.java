/**
 * $
 * PROJECT NAME: TvShopping
 * PACKAGE NAME: com.yunos.tvshopping.request.item
 * FILE NAME: GetTvShopAllProgramIds.java
 * CREATED TIME: 2015年9月7日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
package com.yunos.tvtaobao.tvshoppingbundle.request.item;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class GetTvShopAllProgramIds extends BaseMtopRequest {

    private static final long serialVersionUID = 3702139207627214762L;
    private static String API = "com.yunos.alitv.ProgramAdvertApiService.queryAllProgramIds";
    private String version = "1.0";

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<String> resolveResponse(JSONObject obj) throws Exception {
        if (obj == null || TextUtils.isEmpty(obj.toString()) || TextUtils.isEmpty(obj.optString("result"))) {
            return null;
        }

        List<String> rtn = null;
        try {
            rtn = JSON.parseArray(obj.optString("result"), String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rtn;
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
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }
}
