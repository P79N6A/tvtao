package com.yunos.tvtaobao.payment.alipay.request;

import org.json.JSONException;
import org.json.JSONObject;

import mtopsdk.mtop.domain.MtopRequest;

public class GetOrderDetailRequest extends MtopRequest {

    public GetOrderDetailRequest(String orderId) {
        super();
        setApiName("mtop.order.queryOrderDetail");
        setVersion("1.0.alipay");
        JSONObject data = new JSONObject();
        try {
            data.put("orderId", orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setData(data.toString());
        setNeedSession(true);
        setNeedEcode(false);
    }

    public static String getResponseStatus(JSONObject dataObject) {
        JSONObject orderInfo = dataObject.optJSONObject("orderInfo");
        if (orderInfo != null) {
            String status = orderInfo.optString("orderStatusCode");
            return status;
        }
        return null;
    }

}