/**
 * $
 * PROJECT NAME: TVTaobao
 * PACKAGE NAME: com.yunos.tvtaobao.request.item
 * FILE NAME: GetHotWordsRequest.java
 * CREATED TIME: 2014年10月31日
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2014年10月31日 下午1:43:36
 */
public class GetHotWordsRequest extends BaseMtopRequest {

    private static final long serialVersionUID = -7150582517538460125L;

    private static String API = "com.yunos.tv.tao.itemService.getHotWords";
    private String version = "1.0";

    public GetHotWordsRequest(String type) {
        addParams("uuid", CloudUUIDWrapper.getCloudUUID());
        addParams("key", "q");
        addParams("type", type);
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
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<String> resolveResponse(JSONObject obj) throws Exception {
        if (obj == null || TextUtils.isEmpty(obj.toString()) || TextUtils.isEmpty(obj.optString("result"))) {
            return null;
        }
        return JSON.parseArray(obj.optString("result"),String.class);
    }
}
