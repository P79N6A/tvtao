package com.yunos.tvtaobao.payresult;

import android.os.AsyncTask;

import com.ali.auth.third.offline.login.context.LoginContext;
import com.yunos.tvtaobao.payment.alipay.request.GetAlipayAccountRequest;

import org.json.JSONException;

import mtopsdk.mtop.domain.MtopResponse;
import mtopsdk.mtop.intf.Mtop;

/**
 * Created by wuhaoteng on 2018/8/8.
 */

public class AliPayAccountTask extends AsyncTask<String, Integer, String> {
    private PayResultActivity.OnAllDataFetchListener mOnAllDataFetchListener;
    private PayResultDataFetcher.OnItemDataFetchListener mOnItemDataFetchListener;

    public AliPayAccountTask(PayResultActivity.OnAllDataFetchListener mOnAllDataFetchListener, PayResultDataFetcher.OnItemDataFetchListener mOnItemDataFetchListener) {
        this.mOnAllDataFetchListener = mOnAllDataFetchListener;
        this.mOnItemDataFetchListener = mOnItemDataFetchListener;
    }

    @Override
    protected String doInBackground(String... strings) {
        if(isCancelled()){
            return null;
        }
        String account = "";
        GetAlipayAccountRequest alipayAccountRequest = new GetAlipayAccountRequest(LoginContext.credentialService.getSession().userid);
        MtopResponse alipayAccountResponse = Mtop.instance(null).build(alipayAccountRequest, null).useWua().syncRequest();
        if (alipayAccountResponse.isApiSuccess()) {
            try {
                account = alipayAccountResponse.getDataJsonObject().getString("account");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return account;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (mOnItemDataFetchListener != null) {
            mOnItemDataFetchListener.onComplete();
        }
        if (mOnAllDataFetchListener != null) {
            mOnAllDataFetchListener.onFetchAccount(s);
        }

    }
}
