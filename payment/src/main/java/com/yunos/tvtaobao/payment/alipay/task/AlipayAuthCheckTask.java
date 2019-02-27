package com.yunos.tvtaobao.payment.alipay.task;

import android.os.AsyncTask;

import com.ali.auth.third.offline.login.context.LoginContext;
import com.yunos.tvtaobao.payment.alipay.request.AlipaySignQueryRequest;
import com.yunos.tvtaobao.payment.alipay.request.GetAlipayAccountRequest;

import org.json.JSONException;
import org.json.JSONObject;

import mtopsdk.mtop.domain.MtopResponse;
import mtopsdk.mtop.intf.Mtop;

public class AlipayAuthCheckTask extends AsyncTask<String, Integer, AlipayAuthCheckTask.AlipayAuthCheckResult> {

    public static class AlipayAuthCheckResult {
        public boolean auth;
        public String alipayId;

        AlipayAuthCheckResult(boolean auth, String alipayId) {
            this.auth = auth;
            this.alipayId = alipayId;
        }
    }

    private String buyerId;

    private AlipayAuthCheckResult checkAuthValid() {
        GetAlipayAccountRequest alipayAccountRequest = new GetAlipayAccountRequest(LoginContext.credentialService.getSession().userid);
        MtopResponse alipayAccountResponse = Mtop.instance(null).build(alipayAccountRequest, null).useWua().syncRequest();
        if (alipayAccountResponse.isApiSuccess()) {
            try {
                buyerId = alipayAccountResponse.getDataJsonObject().getString("accountNo");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        AlipaySignQueryRequest request = new AlipaySignQueryRequest();
        MtopResponse response = Mtop.instance(null).build(request, null).useWua().syncRequest();
        if (response.isApiSuccess()) {
            JSONObject data = response.getDataJsonObject();
            if (data.has("agreementNo")) {
                try {
                    String alipayId = data.getString("alipayUserId");
                    if (!alipayId.equals(buyerId)) {
                        return new AlipayAuthCheckResult(false, buyerId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return new AlipayAuthCheckResult(true, buyerId);
            }
        }
        return new AlipayAuthCheckResult(false, buyerId);
    }

    @Override
    protected AlipayAuthCheckResult doInBackground(String... strings) {
        return checkAuthValid();
    }
}
