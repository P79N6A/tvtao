/**
 *
 */
package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AdjustBuildOrderRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 4951719698201296155L;

    private static final String API = "mtop.trade.adjustBuildOrder";

    private String params;

    public AdjustBuildOrderRequest(String params) {
        this.params = params;
    }

    @Override
    protected Map<String, String> getAppData() {
        Map<String, String> paramsObj = new HashMap<String, String>();
        if (!TextUtils.isEmpty(params)) {
            paramsObj.put("params", params);
        }
        paramsObj.put("feature", "{\"gzip\":\"true\"}");
        return paramsObj;
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

    @SuppressWarnings("unchecked")
    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

}
