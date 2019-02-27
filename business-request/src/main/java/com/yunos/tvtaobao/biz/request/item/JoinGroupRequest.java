/**
 * 
 */
package com.yunos.tvtaobao.biz.request.item;


import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.JoinGroupResult;

import org.json.JSONObject;

import java.util.Map;

/**
 * 参团请求
 */
public class JoinGroupRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 6872185179642331711L;

    private static final String API = "mtop.ju.group.join";

    private String itemId;

    public JoinGroupRequest(String itemId) {
        this.itemId = itemId;
    }

    @Override
    protected Map<String, String> getAppData() {
        addParams("itemId", itemId);
        return null;
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return "3.0";
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
    protected JoinGroupResult resolveResponse(JSONObject obj) throws Exception {
        return JoinGroupResult.fromMTOP(obj);
    }
}
