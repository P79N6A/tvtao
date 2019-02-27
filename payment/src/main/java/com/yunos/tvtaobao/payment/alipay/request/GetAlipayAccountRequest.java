package com.yunos.tvtaobao.payment.alipay.request;

import org.json.JSONException;
import org.json.JSONObject;

import mtopsdk.mtop.domain.MtopRequest;

/**
 * Created by rca on 21/12/2017.
 */

public class GetAlipayAccountRequest extends MtopRequest {
    public GetAlipayAccountRequest(String taobaoUserId) {
        super();
        setApiName("mtop.taobao.tvtao.aliuserservice.getAlipayAccount");
        setVersion("1.0");
        setNeedEcode(false);
        setNeedSession(true);
    }
}
