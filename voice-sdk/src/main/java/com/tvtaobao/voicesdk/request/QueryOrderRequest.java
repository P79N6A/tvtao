package com.tvtaobao.voicesdk.request;

import android.text.TextUtils;

import com.tvtaobao.voicesdk.type.PayStatus;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by pan on 2017/8/13.
 */

public class QueryOrderRequest extends BaseMtopRequest {
    private final String TAG = "QueryOrderRequest";
    private final String API = "mtop.taobao.tvtao.speech.order.queryByOutOrderId";
    private final String VERSION = "1.0";

    public QueryOrderRequest(String outOrderId) {
        addParams("outOrderId", outOrderId);
    }

    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {
        AppDebug.e("TVTao_QueryOrder", "obj : " + obj);
        if (!TextUtils.isEmpty(obj.toString())) {
            if (obj.has("errorMessage")) {
                String errorMessage = obj.getString("errorMessage");
                if (!TextUtils.isEmpty(errorMessage))
                    return errorMessage;
            }

            JSONObject bizOrder = obj.getJSONObject("bizOrder");
            String payStatus = bizOrder.getString("payStatus");
            AppDebug.e("TVTao_QueryOrder", "payStatus : " + payStatus + " ，" + PayStatus.STATUS_NOT_PAY.getPayStatus());
            if (payStatus.equals(String.valueOf(PayStatus.STATUS_NOT_PAY.getPayStatus())) || payStatus.equals(String.valueOf(PayStatus.STATUS_PAID.getPayStatus())))
                return payStatus;

            if (payStatus.equals(String.valueOf(PayStatus.STATUS_REFUNDED.getPayStatus())))
                return PayStatus.STATUS_REFUNDED.getPayInfo();

            if (payStatus.equals(String.valueOf(PayStatus.STATUS_TRANSFERED.getPayStatus())))
                return PayStatus.STATUS_TRANSFERED.getPayInfo();

            if (payStatus.equals(String.valueOf(PayStatus.STATUS_NO_OUT_ORDER.getPayStatus())))
                return PayStatus.STATUS_NO_OUT_ORDER.getPayInfo();

            if (payStatus.equals(String.valueOf(PayStatus.STATUS_CLOSED_BY_TAOBAO.getPayStatus())))
                return PayStatus.STATUS_CLOSED_BY_TAOBAO.getPayInfo();

            if (payStatus.equals(String.valueOf(PayStatus.STATUS_NOT_REDY.getPayStatus())))
                return PayStatus.STATUS_NOT_REDY.getPayInfo();

            if (payStatus.equals(String.valueOf(PayStatus.PAY_STATUS_APPEND_PAY.getPayStatus())))
                return PayStatus.PAY_STATUS_APPEND_PAY.getPayInfo();

            if (payStatus.equals(String.valueOf(PayStatus.PAY_STATUS_WAIT_ACCOUNT.getPayStatus())))
                return PayStatus.PAY_STATUS_WAIT_ACCOUNT.getPayInfo();

        }
        return "query";
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return VERSION;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @Override
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }
}
