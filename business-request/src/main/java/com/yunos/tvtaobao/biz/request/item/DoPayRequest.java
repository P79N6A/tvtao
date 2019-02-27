package com.yunos.tvtaobao.biz.request.item;


import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.User;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.DoPayOrders;

import org.json.JSONObject;

import java.util.Map;

public class DoPayRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 3393110315383379325L;

    private static final String API = "mtop.order.doPay";

    private String sid;

    private Long orderId;

    private String apiVersion = "2.0";

    public DoPayRequest(Long orderId) {
        this.setSid(User.getSessionId());
        this.setOrderId(orderId);
    }

    @Override
    protected Map<String, String> getAppData() {
        addParams("sid", sid);
        addParams("orderId", String.valueOf(orderId));

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected DoPayOrders resolveResponse(JSONObject obj) throws Exception {
        if (obj == null){
            return null;
        }
        return JSON.parseObject(obj.toString(), DoPayOrders.class);
    }

    @Override
    protected String getApi() {
        return API;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Override
    public String getApiVersion() {
        return apiVersion;
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
