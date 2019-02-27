package com.yunos.tvtaobao.payment.alipay.request;

import com.alibaba.wireless.security.open.SecException;
import com.alibaba.wireless.security.open.SecurityGuardManager;
import com.yunos.tvtaobao.payment.utils.CloudUUIDWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import mtopsdk.mtop.domain.MtopRequest;

/**
 * Created by rca on 15/12/2017.
 */

public class AgreementPayRequest extends MtopRequest {
    public AgreementPayRequest(String buyerId, String bizOrderId) {
        setApiName("mtop.taobao.tvtao.TvTaoAlipayTpAgreementPay");
        setVersion("1.0");
        JSONObject data = new JSONObject();
        try {
//            data.put("deviceId", CloudUUIDWrapper.getCloudUUID());
            data.put("uuid", CloudUUIDWrapper.getCloudUUID());
//            data.put("buyerId", buyerId);
            data.put("bizOrderId", bizOrderId);
            data.put("umt", SecurityGuardManager.getInstance(null).getUMIDComp().getSecurityToken());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (SecException e) {
            e.printStackTrace();
        }
        setData(data.toString());
        setNeedSession(true);
        setNeedEcode(false);
    }


}
