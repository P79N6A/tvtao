package com.yunos.tvtaobao.biz.request.item;


import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.OrderLogisticMo;

import org.json.JSONObject;

import java.util.Map;

public class GetOrderLogistic extends BaseMtopRequest {

    private static final long serialVersionUID = -9160622703250397359L;

    private static final String API = "mtop.logistic.getLogisticByOrderId";

    private Long orderId;

    public GetOrderLogistic(Long orderId) {
        this.setOrderId(orderId);
    }

    @Override
    protected Map<String, String> getAppData() {
        addParams("orderId", String.valueOf(orderId));

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected OrderLogisticMo resolveResponse(JSONObject obj) throws Exception {
        return OrderLogisticMo.resolveFromMTOP(obj);
    }

    @Override
    protected String getApi() {
        return API;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Override
    public boolean getNeedEcode() {
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }

    @Override
    protected String getApiVersion() {
        return "1.0";
    }
}
