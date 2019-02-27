/**
 *
 */
package com.yunos.tvtaobao.biz.request.item;


import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class UpdateBagRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 4725190889190641767L;

    private static final String API = "mtop.trade.updateBag";
    //预发二套
//    private static final String API = "mtop.trade.updateBag.pre2";

    private String mParams;
    private String mCartFrom;

    public UpdateBagRequest(String params, String cartFrom) {
        mParams = params;
        mCartFrom = cartFrom;

        addParams("p", mParams);
        addParams("extStatus", "0");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("version", "1.1.1");
            jsonObject.put("mergeCombo", true);
            jsonObject.put("globalSell", "1");

            JSONObject tvtaoExtra = new JSONObject();
            tvtaoExtra.put("appKey", Config.getChannel());
            //TODO 预发环境测试渠道号
//            tvtaoExtra.put("appKey","142857");
            String tvOptions= TvOptionsConfig.getTvOptions();
            String tvOptionsResult=tvOptions.substring(0,tvOptions.length()-1);
            AppDebug.e("tvOptions = ",tvOptionsResult+"1");
            tvtaoExtra.put("tvOptions", tvOptionsResult+"1");
            jsonObject.put("tvtaoExtra", tvtaoExtra.toString());



            addParams("exParams", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject feature = new JSONObject();
        try {
            feature.put("gzip", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addParams("feature", feature.toString());

        addParams("cartFrom", mCartFrom);
    }

    @Override
    public String getTTid() {
        return Config.getChannel() + "@taobao_android_7.10.0";
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @Override
    protected String getApi() {
        return API;
    }

    //3.1
    @Override
    protected String getApiVersion() {
        return "4.0";
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
        return obj.toString();
    }
}
