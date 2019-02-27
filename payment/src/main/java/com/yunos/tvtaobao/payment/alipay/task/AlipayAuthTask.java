package com.yunos.tvtaobao.payment.alipay.task;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.ali.auth.third.core.MemberSDK;
import com.ali.auth.third.core.callback.LoginCallback;
import com.ali.auth.third.core.model.Session;
import com.ali.auth.third.offline.login.LoginService;
import com.yunos.tvtaobao.payment.alipay.AlipayTaskListener;
import com.yunos.tvtaobao.payment.alipay.request.AlipaySignQueryRequest;
import com.yunos.tvtaobao.payment.alipay.request.AlipaySignRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mtopsdk.mtop.domain.MtopRequest;
import mtopsdk.mtop.domain.MtopResponse;
import mtopsdk.mtop.intf.Mtop;

/**
 * Created by rca on 12/12/2017.
 */

public class AlipayAuthTask extends AsyncTask {

    public static final int FAIL_RETRY_DELAY_MILLIS = 5000;

    public static final int QUERY_DELAY_MILLIS = 3000;

    public static final int EXPIRATION_MILLIS = 5 * 60 * 1000;

    public static class AlipayAuthTaskResult {
        private int step;
        private int status;
        private Object object;

        AlipayAuthTaskResult(int step, int status, Object object) {
            this.step = step;
            this.status = status;
            this.object = object;
        }

        public int getStep() {
            return step;
        }

        public int getStatus() {
            return status;
        }

        public Object getObject() {
            return object;
        }
    }

    public interface AlipayAuthTaskListener {
        void onReceivedAlipayAuthStateNotify(AlipayAuthTaskResult result);
    }

    private AlipayAuthTaskListener mListener;

    public void setListener(AlipayAuthTaskListener listener) {
        this.mListener = listener;
    }


    public static final int STEP_GEN = 0, STEP_QUERY = 1, STEP_LOGIN = 2;

    public static final int STATUS_SUCCESS = 0, STATUS_FAIL = 1, STATUS_EXPIRE = 2;

    private Context mContext;
    private boolean finish = false;

    public AlipayAuthTask(Context context) {
        super();
        mContext = context;
    }

    private MtopRequest currentRequest;
    private int step = STEP_GEN;
    private String loginToken = null;
    private String alipayUserId = null;

    private long timeStamp = 0L;

    public void setAlipayUserId(String alipayUserId) {
        this.alipayUserId = alipayUserId;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        while (!finish && !isCancelled()) {//整个业务循环 TODO isCancelled方法判断不足以屏蔽回调，cancel之后循环体内请求完成仍然会回调，需要解决
            if (isExpire())
                step = STEP_GEN;
            if (step == STEP_GEN) {
                currentRequest = new AlipaySignRequest(alipayUserId);
                MtopResponse response = Mtop.instance(mContext).build(currentRequest, null).useWua().setConnectionTimeoutMilliSecond(5000).setSocketTimeoutMilliSecond(3000).syncRequest();
                if (!response.isApiSuccess()) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                JSONObject data = response.getDataJsonObject();
                AlipaySignResult result = AlipaySignResult.resolveFromJson(data);
                publishProgress(new AlipayAuthTaskResult(STEP_GEN, STATUS_SUCCESS, result.qrCode));
                timeStamp = System.currentTimeMillis();
                step = STEP_QUERY;
            } else if (step == STEP_QUERY) {
                if (doAuthQuery())
                    break;
            }

        }
        return null;
    }

    private boolean isExpire() {
        long time = System.currentTimeMillis();
        return time - timeStamp > EXPIRATION_MILLIS;
    }

    /**
     * send AuthQuery request and handle response
     *
     * @return true if query is success & auth is complete , false else
     */
    private boolean doAuthQuery() {
        currentRequest = new AlipaySignQueryRequest();
        MtopResponse response = Mtop.instance(mContext).build(currentRequest, null).useWua().setConnectionTimeoutMilliSecond(5000).setSocketTimeoutMilliSecond(3000).syncRequest();
        if (!response.isApiSuccess()) {
            try {
                Thread.sleep(FAIL_RETRY_DELAY_MILLIS);
                return false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            //TODO
            AlipayQueryResult result = AlipayQueryResult.resolveFromJson(response.getDataJsonObject());
            if (!TextUtils.isEmpty(result.token) && !TextUtils.isEmpty(result.agreementNo)) {
                publishProgress(new AlipayAuthTaskResult(STEP_QUERY, STATUS_SUCCESS, null));
                loginToken = result.token;
                return true;
            }
            try {
                Thread.sleep(QUERY_DELAY_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        if (values.length == 1) {
            AlipayAuthTaskResult result = (AlipayAuthTaskResult) values[0];
            notifyResult(result);
        }

    }

    private void notifyResult(AlipayAuthTaskResult result) {
        if (mListener != null) {
            mListener.onReceivedAlipayAuthStateNotify(result);
        }
    }

}
