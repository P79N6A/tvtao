package com.yunos.tvtaobao.payment.alipay.task;

import android.text.TextUtils;

import org.json.JSONObject;

/**
 * Created by rca on 08/09/2017.
 */

/**
 * response:{"invalidTime":"2018-03-26 13:41:45","code":"10000","status":"NORMAL","msg":"Success","agreementNo":"20170926171088006991"}
 */
public class AlipayQueryResult {
    public boolean success;
    public String token;
    public String agreementNo;
    public String subMsg;
    public String subCode;
    public String code;

    public static AlipayQueryResult resolveFromJson(JSONObject jObj) {
        AlipayQueryResult result = new AlipayQueryResult();
        result.token = jObj.optString("loginToken");
        result.code = jObj.optString("code");
        result.agreementNo = jObj.optString("agreementNo");
        result.subCode = jObj.optString("subCode");
        result.subMsg = jObj.optString("subMsg");
        result.success = !TextUtils.isEmpty(result.token);
        return result;
    }
}