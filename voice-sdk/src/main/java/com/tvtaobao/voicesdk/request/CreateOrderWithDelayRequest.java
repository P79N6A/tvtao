package com.tvtaobao.voicesdk.request;

import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by pan on 2017/7/27.
 */

public class CreateOrderWithDelayRequest extends BaseMtopRequest {
    private final String API = "mtop.taobao.tvtao.speech.order.createDelayOrderByAgreementPay";
    private final String VERSION = "1.0";

    public CreateOrderWithDelayRequest(String outPreferentialId, String tagId, String tvOptions, String itemId, String skuId, String deliveryAddressId, int quantity) {
        addParams("itemId", itemId);
        addParams("skuId", skuId);
        addParams("deliveryAddressId", deliveryAddressId);
        addParams("buyQuantity", quantity + "");
        addParams("appkey", Config.getAppKey());
        addParams("deviceId", CloudUUIDWrapper.getCloudUUID());
        addParams("ttid", Config.getTTid());
        try {
            JSONObject jobj = new JSONObject();
            jobj.put("coVersion", "2.0");
            jobj.put("coupon", "true");
            JSONObject mTvTaoEx = new JSONObject();
            mTvTaoEx.put("appkey", Config.getChannel());
            mTvTaoEx.put("isFromVoice", true);
            mTvTaoEx.put("deviceId", CloudUUIDWrapper.getCloudUUID());
            mTvTaoEx.put("outPreferentialId", outPreferentialId);
            mTvTaoEx.put("tagId", tagId);
            mTvTaoEx.put("tvOptions", tvOptions);
            jobj.put("TvTaoEx", mTvTaoEx);

            AppDebug.e(TAG, "exParams : " + jobj.toString());
            addParams("exParams", jobj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            addParams("uuid", CloudUUIDWrapper.getCloudUUID());
//            data.put("deviceId", CloudUUIDWrapper.getCloudUUID());
            addParams("umt", Config.getUmtoken(CoreApplication.getApplication()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Object resolveResponse(JSONObject obj) throws Exception {
        AppDebug.e("TVTao_CreateOrder", "obj : " + obj);
        return obj.toString();
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
