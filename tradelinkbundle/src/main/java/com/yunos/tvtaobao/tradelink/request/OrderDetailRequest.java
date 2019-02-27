package com.yunos.tvtaobao.tradelink.request;

import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.tradelink.buildorder.bean.OrderDetailBo;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhujun on 11/28/16.
 */

public class OrderDetailRequest extends BaseMtopRequest {
    @Override
    protected OrderDetailBo resolveResponse(JSONObject obj) throws Exception {
        return OrderDetailBo.resolveJson(obj);
    }

    private String orderId;

    public OrderDetailRequest(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public boolean getNeedEcode() {
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return true;
    }

    @Override
    protected String getApi() {
        return "mtop.order.queryDetail";
    }

    @Override
    protected String getApiVersion() {
        return "3.0";
    }

    @Override
    protected Map<String, String> getAppData() {
        Map<String, String> params = new HashMap<>();
        params.put("bizOrderId", orderId);
        params.put("appVersion", "1.0");
        params.put("appName", "tborder");
        return params;
    }
}
