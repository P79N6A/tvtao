package com.tvtaobao.voicesdk.bo;

/**
 * Created by rca on 01/09/2017.
 */

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * "alipayAccountForUser": "2088512406598552",
 * "applyQrCodeUrl": "https://mobilecodec.alipay.com/show.htm?code=ptxcossinqtygmgu84&picSize=M",
 * "authResultForUser": "false",
 * "authStateForDevice": "0"
 */
public class AlipayAuthResult {

    @SerializedName("alipayAccountForUser")
    public String alipayAccount;

    @SerializedName("applyQrCodeUrl")
    public String qrCodeUrl;

    @SerializedName("authResultForUser")
    public boolean authResult;

    @SerializedName("authStateForDevice")
    public int authState;

    public static AlipayAuthResult resolveFromJson(JSONObject jsonObject) {
        if (jsonObject == null)
            return null;
        AlipayAuthResult result = new AlipayAuthResult();
        result.alipayAccount = jsonObject.optString("alipayAccountForUser");
        result.qrCodeUrl = jsonObject.optString("applyQrCodeUrl");
        result.authResult = jsonObject.optBoolean("authResultForUser");
        result.authState = jsonObject.optInt("authStateForDevice");
        return result;
    }


}
