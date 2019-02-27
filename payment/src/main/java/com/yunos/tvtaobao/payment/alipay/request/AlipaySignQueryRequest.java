package com.yunos.tvtaobao.payment.alipay.request;

import com.alibaba.wireless.security.open.SecException;
import com.alibaba.wireless.security.open.SecurityGuardManager;
import com.yunos.tvtaobao.payment.alipay.task.AlipayQueryResult;
import com.yunos.tvtaobao.payment.utils.CloudUUIDWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import mtopsdk.mtop.domain.MtopRequest;

/**
 * Created by rca on 08/09/2017.
 */

public class AlipaySignQueryRequest extends MtopRequest {
    private final static String API = "mtop.taobao.tvtao.TvTaoAlipayPageSignQuery";
    private final static String API_VERSION = "1.0";

    public AlipaySignQueryRequest() {
        super();
        setApiName(API);
        setVersion(API_VERSION);
        JSONObject data = new JSONObject();
        try {
            data.put("uuid", CloudUUIDWrapper.getCloudUUID());
//            data.put("deviceId", CloudUUIDWrapper.getCloudUUID());
            data.put("umt", SecurityGuardManager.getInstance(null).getUMIDComp().getSecurityToken());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (SecException e) {
            e.printStackTrace();
        }
        setData(data.toString());
        setNeedEcode(false);
        setNeedSession(false);
    }


    protected AlipayQueryResult resolveResponse(JSONObject obj) throws Exception {
        return AlipayQueryResult.resolveFromJson(obj);
    }

}
