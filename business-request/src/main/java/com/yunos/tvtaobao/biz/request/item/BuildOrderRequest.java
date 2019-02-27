/**
 *
 */
package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;
import android.util.Log;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.BuildOrderRequestBo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BuildOrderRequest extends BaseMtopRequest {

    private static final long serialVersionUID = -7010317316195443367L;

        private static final String API = "mtop.trade.buildOrder";
    //预发二套
//    private static final String API = "mtop.trade.buildOrder.pre2";

    private BuildOrderRequestBo mBuildOrderRequestBo;

    private boolean mHasAddCart;

    private boolean presale = false;

    public BuildOrderRequest(BuildOrderRequestBo buildOrderRequestBo, boolean hasAddCart) {
        mBuildOrderRequestBo = buildOrderRequestBo;
        mHasAddCart = hasAddCart;
    }

    @Override
    protected Map<String, String> getAppData() {
        Map<String, String> obj = new HashMap<String, String>();
        if (mBuildOrderRequestBo != null) {
            if (!TextUtils.isEmpty(mBuildOrderRequestBo.getDeliveryId())) {
                obj.put("deliveryId", mBuildOrderRequestBo.getDeliveryId());
            }

            if (!TextUtils.isEmpty(mBuildOrderRequestBo.getCartIds())) {
                obj.put("cartIds", mBuildOrderRequestBo.getCartIds());
            }

            if (mBuildOrderRequestBo.isSettlementAlone()) {
                obj.put("isSettlementAlone", "true");
                obj.put("buyParam", mBuildOrderRequestBo.getBuyParam());
            } else if (!TextUtils.isEmpty(mBuildOrderRequestBo.getCartIds())) {
                obj.put("cartIds", mBuildOrderRequestBo.getCartIds());
            }

            String s = mBuildOrderRequestBo.getExtParams();
            Log.i(TAG, "1 exParams : " + s);
            if (TextUtils.isEmpty(s)) {
                JSONObject job = new JSONObject();
                try {
                    job.put("coVersion", "2.0");
                    job.put("coupon", "true");
                    job.put("biz_scene", "TV_PAY");
                    job.put("notAutoAgreementPay", 1);
//                    if (mBuildOrderRequestBo.isPreSell())
//                        job.put("agreementPay_product", "S_PHASE_STANDARD");//加上预售标
//                    job.put("agreementPay_product", "S_PHASE");
//                    job.put("agreementPay_biz", "null");
//                    job.put("agreementPay_product", "null" );
                    JSONObject tvtaoEx = new JSONObject();
                    String appkey = SharePreferences.getString("device_appkey", "");
                    String brandName = SharePreferences.getString("device_brandname", "");
                    if (appkey.equals("10004416") && brandName.equals("海尔")) {
                        tvtaoEx.put("appKey", "2017092310");
                    } else {
                        tvtaoEx.put("appKey", Config.getChannel());
                    }
                    tvtaoEx.put("deviceId", DeviceUtil.getStbID());
                    tvtaoEx.put("cartFlag", "cart".equals(mBuildOrderRequestBo.getFrom()) ? "1" : "0");
                    String tvOptions = TvOptionsConfig.getTvOptions();
                    if(mBuildOrderRequestBo.getFrom().equals("cart")){
                        tvtaoEx.put("tvOptions",tvOptions.substring(0,5) + "1");
                    }else {
                        tvtaoEx.put("tvOptions", tvOptions);
                    }

                    job.put("TvTaoEx", tvtaoEx);
                    AppDebug.i("[tvOptionsDebug] mtop.trade.buildOrder tvTaoEx ----> ", tvtaoEx != null ? tvtaoEx.toString() : "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                s = job.toString();
            } else {
                try {
                    JSONObject jobj = new JSONObject(s);
                    jobj.put("coVersion", "2.0");
                    jobj.put("coupon", "true");
                    jobj.put("biz_scene", "TV_PAY");
                    jobj.put("notAutoAgreementPay", 1);
//                    if (mBuildOrderRequestBo.isPreSell())
//                        jobj.put("agreementPay_product", "S_PHASE_STANDARD");//加上预售标
//                    jobj.put("agreementPay_product", "S_PHASE");
//                    jobj.put("agreementPay_biz", "null");
//                    jobj.put("agreementPay_product", "null");
                    JSONObject TvTaoEx;
                    if (jobj.has("TvTaoEx")) {
                        TvTaoEx = jobj.getJSONObject("TvTaoEx");
                    } else {
                        TvTaoEx = new JSONObject();
                    }
                    String appkey = SharePreferences.getString("device_appkey", "");
                    String brandName = SharePreferences.getString("device_brandname", "");
                    if (appkey.equals("10004416") && brandName.equals("海尔")) {
                        TvTaoEx.put("appKey", "2017092310");
                    } else {
//            addParams("appKey", "2017092310");
                        TvTaoEx.put("appKey", Config.getChannel());
                    }
                    TvTaoEx.put("deviceId", DeviceUtil.getStbID());
                    TvTaoEx.put("cartFlag", mBuildOrderRequestBo.getFrom().equals("cart") ? "1" : "0");
                    String tvOptions = TvOptionsConfig.getTvOptions();
                    if(mBuildOrderRequestBo.getFrom().equals("cart")){
                        TvTaoEx.put("tvOptions",tvOptions.substring(0,5)+"1");
                    }else {
                        TvTaoEx.put("tvOptions", tvOptions);
                    }
                    if (!jobj.has("TvTaoEx")) {
                        jobj.put("TvTaoEx", TvTaoEx);
                    }
                    s = jobj.toString();
                    AppDebug.i("[tvOptionsDebug] mtop.trade.buildOrder tvTaoEx ----> ", TvTaoEx != null ? TvTaoEx.toString() : "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.w(TAG, "2 exParams : " + s);
            obj.put("exParams", s);//mBuildOrderRequestBo.getExtParams());

            obj.put("buyNow", String.valueOf(mBuildOrderRequestBo.isBuyNow()));

            if (mBuildOrderRequestBo.isBuyNow()) {
                if (!TextUtils.isEmpty(mBuildOrderRequestBo.getItemId())) {
                    obj.put("itemId", mBuildOrderRequestBo.getItemId());
                }

                obj.put("quantity", String.valueOf(mBuildOrderRequestBo.getQuantity()));

                if (!TextUtils.isEmpty(mBuildOrderRequestBo.getSkuId())) {
                    obj.put("skuId", mBuildOrderRequestBo.getSkuId());
                }

                if (!TextUtils.isEmpty(mBuildOrderRequestBo.getServiceId())) {
                    obj.put("serviceId", mBuildOrderRequestBo.getServiceId());
                }

                if (!TextUtils.isEmpty(mBuildOrderRequestBo.getActivityId())) {
                    obj.put("activityId", mBuildOrderRequestBo.getActivityId());
                }

                if (!TextUtils.isEmpty(mBuildOrderRequestBo.getTgKey())) {
                    obj.put("tgKey", mBuildOrderRequestBo.getTgKey());
                }
            }
        }

        return obj;
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return "3.0";
    }

    @Override
    public boolean getNeedEcode() {
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String resolveResponse(org.json.JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }
}
