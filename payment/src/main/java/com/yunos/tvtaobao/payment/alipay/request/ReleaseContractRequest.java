package com.yunos.tvtaobao.payment.alipay.request;

import com.alibaba.wireless.security.open.SecException;
import com.alibaba.wireless.security.open.SecurityGuardManager;
import com.yunos.tvtaobao.payment.utils.CloudUUIDWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import mtopsdk.mtop.domain.MtopRequest;


/**
 * Created by rca on 13/12/2017.
 */

public class ReleaseContractRequest extends MtopRequest {
    public ReleaseContractRequest() {
        super();
//        setApiName("mtop.taobao.tvtao.TvTaoAlipayUnSign");
        setApiName("mtop.taobao.tvtao.TvTaoAlipayRelieveContract");
        setVersion("1.0");
        setNeedEcode(false);
        setNeedSession(false);
        JSONObject data = new JSONObject();
        try {
            data.put("uuid", CloudUUIDWrapper.getCloudUUID());
            data.put("umt", SecurityGuardManager.getInstance(null).getUMIDComp().getSecurityToken());
//            data.put("deviceId", CloudUUIDWrapper.getCloudUUID());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (SecException e) {
            e.printStackTrace();
        }
        setData(data.toString());
    }
}
