/**
 *
 */
package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.QueryBagRequestBo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class QueryBagRequest extends BaseMtopRequest {

    private String TAG = "QueryBagRequest";
    private static final long serialVersionUID = 4725190889190641767L;

    private static final String API = "mtop.trade.queryBag";
    //预发二套
//    private static final String API = "mtop.trade.queryBag.pre2";

    //    "exParams":"{\"version\":\"1.1.1\",\"mergeCombo\":\"true\",\"globalSell\":\"1\"}"
    public QueryBagRequest(QueryBagRequestBo queryBagRequestBo) {
        if (queryBagRequestBo != null) {
            if (!TextUtils.isEmpty(queryBagRequestBo.getP())) {
                addParams("p", queryBagRequestBo.getP());
                AppDebug.e(TAG + "p = ", queryBagRequestBo.getP());

            }
            if (!TextUtils.isEmpty(queryBagRequestBo.getFeature())) {
                addParams("feature", queryBagRequestBo.getFeature());
                AppDebug.e(TAG + "feature = ", queryBagRequestBo.getFeature());
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("version", "1.1.1");
                jsonObject.put("mergeCombo", true);
                jsonObject.put("globalSell", "1");
                JSONObject tvtaoExtra = new JSONObject();

                tvtaoExtra.put("appKey", Config.getChannel());
                //TODO 预发环境测试渠道号
//                tvtaoExtra.put("appKey","142857");

                String tvOptions= TvOptionsConfig.getTvOptions();
                String tvOptionsResult=tvOptions.substring(0,tvOptions.length()-1);
                AppDebug.e("tvOptions = ",tvOptionsResult+"1");
                tvtaoExtra.put("tvOptions", tvOptionsResult+"1");

                jsonObject.put("tvtaoExtra", tvtaoExtra.toString());

                addParams("exParams", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            addParams("isPage", String.valueOf(queryBagRequestBo.isPage()));
            addParams("extStatus", String.valueOf(queryBagRequestBo.getExtStatus()));
//            addParams("dataMd5","1f2926ce56613a8be6441678ca49c2bf");
//            addParams("mixed","false");

            if (!TextUtils.isEmpty(queryBagRequestBo.getCartFrom())) {
                addParams("cartFrom", queryBagRequestBo.getCartFrom());
                AppDebug.e(TAG + "cartFrom = ", queryBagRequestBo.getCartFrom());

            }
            AppDebug.e("TAG", "queryBagRequestBo = " + queryBagRequestBo.toString());
        }
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    public String getTTid() {
        return Config.getChannel() + "@taobao_android_7.10.0";
    }

    @Override
    protected String getApiVersion() {
        return "5.0";
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @Override
    public boolean getNeedEcode() {
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        AppDebug.e("resolveResponse", obj.toString());
        return obj.toString();
    }
}
