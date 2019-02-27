/**
 *
 */
package com.yunos.tvtaobao.biz.request.item;


import com.yunos.CloudUUIDWrapper;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.CreateOrderResult;

import org.json.JSONObject;

import java.util.Map;

public class CreateOrderRequestV3 extends BaseMtopRequest {

    private static final long serialVersionUID = 2946152565790016558L;

        private static final String API = "mtop.trade.createOrder";
    //预发二套
//    private static final String API = "mtop.trade.createOrder.pre2";

    private String params;

    public CreateOrderRequestV3(String params) {
        this.params = params;
        addParams("orderMarker", "m:terminal=alitv|v:uuid=" + CloudUUIDWrapper.getCloudUUID());
        addParams("params", params);
        addParams("feature", "{\"gzip\":\"true\"}");
    }


    @Override
    protected Map<String, String> getAppData() {
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
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected CreateOrderResult resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        return CreateOrderResult.fromMTOP(obj);
    }
}
