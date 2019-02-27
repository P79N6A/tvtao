package com.yunos.tvtaobao.payment.alipay.task;

import android.os.AsyncTask;
import android.util.Log;

import com.ali.auth.third.offline.login.context.LoginContext;
import com.yunos.tvtaobao.payment.alipay.request.AgreementPayRequest;
import com.yunos.tvtaobao.payment.alipay.request.AlipaySignQueryRequest;
import com.yunos.tvtaobao.payment.alipay.request.GetAlipayAccountRequest;
import com.yunos.tvtaobao.payment.alipay.request.GetOrderDetailRequest;

import org.json.JSONException;
import org.json.JSONObject;

import mtopsdk.mtop.domain.MtopResponse;
import mtopsdk.mtop.intf.Mtop;

/**
 * Created by rca on 15/12/2017.
 */

public class AgreementPayTask extends AsyncTask {

    public interface AgreementPayListener {
        void onPayMentSuccess(AgreementPayTask task, String account);

        void onPayMentFailure(AgreementPayTask task, String code);

        void onNeedAuth(AgreementPayTask task, String buyerId);
    }

    private AgreementPayListener listener;


    public void setListener(AgreementPayListener listener) {
        this.listener = listener;
    }

    public static final String AUTH_NOT_VALID = "invalidAuth";
    public static final String PAY_ERROR = "payerror";

    private String buyerId;
    private String account;
    private String bizOrderId;

    private boolean checkDepositStatus = false;//是否判断订单的BUYER_PAYED_DEPOSIT状态

    public void setCheckDepositStatus(boolean checkDepositStatus) {
        this.checkDepositStatus = checkDepositStatus;
    }

    public boolean shouldCheckDepositStatus() {
        return checkDepositStatus;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public void setBizOrderId(String bizOrderId) {
        this.bizOrderId = bizOrderId;
    }


    @Override
    protected void onProgressUpdate(Object[] values) {
        Integer val = (Integer) values[0];
        if (val == 0)
            notifyNeedAuth();
        else if (val == 1)
            notifySuccess();
        else if (val == 2)
            notifyError();
        super.onProgressUpdate(values);
    }

    private boolean finish = false;

    private int step = 0;

    private Object lock = new Object();

    public void resumePay() {
        synchronized (lock) {
            lock.notify();
        }
    }

    public void stop() {
        finish = true;
        synchronized (lock) {
            lock.notify();
        }
    }

    public void pause() {
        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Object doInBackground(Object... objects) {
        while (!finish) {
            if (step == 0) {
                if (!checkAuthValid()) {
                    publishProgress(0);//needauth
                    pause();//TODO Thread.sleep会导致asynctask所在的executer线程sleep,目前执行时可以指定executor为 THREADPOOL_EXECUTOR来绕过这个问题，后续:改进后台任务机制
                } else
                    step++;
            } else if (step == 1) {
                if (doPay()) {
                    publishProgress(1);//success
                } else
                    publishProgress(2);//pay error
                finish = true;
            }

        }
        return null;
    }

    private void notifyError() {
        if (listener != null)
            listener.onPayMentFailure(this, doErrorMapping(errorMsg));
    }

    private void notifySuccess() {
        if (listener != null) {
            listener.onPayMentSuccess(this, account);
        }
    }

    private void notifyNeedAuth() {
        if (listener != null) {
            listener.onNeedAuth(this, buyerId);
        }
    }

    private String taobaoUid = null;

    private boolean checkAuthValid() {
        taobaoUid = LoginContext.credentialService.getSession().userid;
        GetAlipayAccountRequest alipayAccountRequest = new GetAlipayAccountRequest(LoginContext.credentialService.getSession().userid);
        MtopResponse alipayAccountResponse = Mtop.instance(null).build(alipayAccountRequest, null).useWua().syncRequest();
        if (alipayAccountResponse.isApiSuccess()) {
            try {
                buyerId = alipayAccountResponse.getDataJsonObject().getString("accountNo");
                account = alipayAccountResponse.getDataJsonObject().getString("account");
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
                    if (!alipayId.equals(buyerId))
                        return false;//publishProgress(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }

    private String errorMsg;

    private String doErrorMapping(String errorCode) {
        if ("PAY_QUOTA_EXCEED".equals(errorCode)) {
            return "当日购物金额超出免密支付额度";
        } else if ("USER_BALANCE_NOT_ENOUGH".equals(errorCode)) {
            return "您的支付宝账户余额不足";
        }
        return "网络好像有点不通畅";
    }

    public String getErrorMsg() {
        return doErrorMapping(errorMsg);
    }

    private boolean doPay() {
        AgreementPayRequest request = new AgreementPayRequest(taobaoUid, bizOrderId);
        MtopResponse response = Mtop.instance(null).build(request, null).useWua().syncRequest();
        if (response.isApiSuccess()) {
            JSONObject data = response.getDataJsonObject();
            Log.d("test", "pay data:" + data);
            boolean success = data.optBoolean("success", false);
            if (success) {
                errorMsg = null;
                return true;
            } else {
                errorMsg = response.getRetCode();
                if("USER_BALANCE_NOT_ENOUGH".equals(errorMsg))//账户余额不足，直接返回
                    return false;
                int retryCount = 4;
                do {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    GetOrderDetailRequest checkRequest = new GetOrderDetailRequest(bizOrderId);
                    MtopResponse checkResponse = Mtop.instance(null).build(checkRequest, null).useWua().syncRequest();
                    if (checkResponse.isApiSuccess()) {
                        JSONObject resp = checkResponse.getDataJsonObject();
                        String status = GetOrderDetailRequest.getResponseStatus(resp);
                        if (!"WAIT_BUYER_PAY".equals(status)) {
                            return checkPaid(status);
                        }
                    }
                    retryCount--;
                } while (retryCount > 0);
            }
        } else {
            errorMsg = response.getRetCode();
            if("USER_BALANCE_NOT_ENOUGH".equals(errorMsg))//账户余额不足，直接返回
                return false;
            int retryCount = 4;
            do {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                GetOrderDetailRequest checkRequest = new GetOrderDetailRequest(bizOrderId);
                MtopResponse checkResponse = Mtop.instance(null).build(checkRequest, null).useWua().syncRequest();
                if (checkResponse.isApiSuccess()) {
                    JSONObject resp = checkResponse.getDataJsonObject();
                    String status = GetOrderDetailRequest.getResponseStatus(resp);
                    if (!"WAIT_BUYER_PAY".equals(status)) {
                        return checkPaid(status);
                    }
                }
                retryCount--;
            } while (retryCount > 0);
        }
        return false;
    }

    private boolean checkPaid(String statusCode) {
        errorMsg = statusCode;
        if ("TRADE_FINISHED".equals(statusCode))
            return true;
        if ("WAIT_SELLER_SEND_GOODS".equals(statusCode))
            return true;
        if ("BUYER_PAYED_DEPOSIT".equals(statusCode) && shouldCheckDepositStatus()) //认为购物车不可能进入预售定金订单，暂时如此判断
            return true;
        if ("WAIT_BUYER_CONFIRM_GOODS".equals(statusCode))
            return true;
        return false;
    }

}
