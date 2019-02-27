package com.yunos.tvtaobao.payment.alipay.task;

import org.json.JSONObject;

/**
 * Created by rca on 06/09/2017.
 */

public class AlipaySignResult {
    public String qrCode;
    public String qrToken;

    public static AlipaySignResult resolveFromJson(JSONObject jsonObject) {
        AlipaySignResult result = new AlipaySignResult();
        result.qrCode = jsonObject.optString("qrCode");
        result.qrToken = jsonObject.optString("qrToken");
        return result;
    }
}
