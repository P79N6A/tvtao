package com.yunos.tvtaobao.payment.alipay.request;

import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.wireless.security.open.SecException;
import com.alibaba.wireless.security.open.SecurityGuardManager;
import com.yunos.tvtaobao.payment.alipay.task.AlipaySignResult;
import com.yunos.tvtaobao.payment.utils.CloudUUIDWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import mtopsdk.mtop.domain.MtopRequest;

/**
 * Created by rca on 06/09/2017.
 */

public class AlipaySignRequest extends MtopRequest {

    private static final String API = "mtop.taobao.tvtao.TvTaoAlipayPageSign";
    private static final String API_VERSION = "1.0";

    public AlipaySignRequest(String alipayUserId) {
        setNeedEcode(true);
        setNeedSession(false);
        setApiName(API);
        setVersion(API_VERSION);
        Intent intent = new Intent();
        JSONObject data = new JSONObject();
        try {
//            data.put("deviceId",CloudUUIDWrapper.getCloudUUID());

            data.put("uuid", CloudUUIDWrapper.getCloudUUID());
            if (!TextUtils.isEmpty(alipayUserId)) {
                data.put("signUserId", alipayUserId);
            }
            data.put("signvalidityPeriod", "6m");
            data.put("umt", SecurityGuardManager.getInstance(null).getUMIDComp().getSecurityToken());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (SecException e) {
            e.printStackTrace();
        }
        setData(data.toString());

    }

    public AlipaySignResult resolveResponse(JSONObject obj) throws Exception {
        return AlipaySignResult.resolveFromJson(obj);
    }

}
