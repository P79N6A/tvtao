package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.biz.request.bo.ShopCoupon;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetRebateMoneyRequest extends BaseMtopRequest {

    private static  final String TAG = "GetRebateMoneyRequest";

    private final static String API = "mtop.taobao.tvtao.tag.batchQuery";

    public GetRebateMoneyRequest(String itemIdArray, List<String> list ,boolean isFromCartToBuildOrder,boolean isFromBuildOrder,boolean mjf,String extParams) {
        super();
        String tvOptions = TvOptionsConfig.getTvOptions();

        if (!TextUtils.isEmpty(tvOptions)) {
            if(isFromCartToBuildOrder){
                String tvOptionsSubstring = tvOptions.substring(0, tvOptions.length() - 1);
                addParams("tvOptions",tvOptionsSubstring + "1");
                AppDebug.v(TAG,"tvOptions = "+ tvOptionsSubstring + "1");
            }else {
                addParams("tvOptions", tvOptions);
                AppDebug.v(TAG,"tvOptions = " + tvOptions);
            }
        }
        if(!TextUtils.isEmpty(itemIdArray)){
            AppDebug.v(TAG,"params = "+itemIdArray);
            addParams("params", itemIdArray);
        }
        String appKey = Config.getChannel();
        if(!TextUtils.isEmpty(appKey)){
            AppDebug.e(TAG,"appKey = "+appKey);
            addParams("appKey", appKey);
        }

//        addParams("appKey", "142857");
        if(isFromBuildOrder) {
            addParams("from", "build_order");
        }
        addParams("mjf", String.valueOf(mjf));
        JSONArray array = new JSONArray();
        for (int i = 0 ; i < list.size() ; i++) {
            array.put(list.get(i));
        }

        AppDebug.v(TAG,"traceRoutes ="+array);
        addParams("traceRoutes",array.toString());
        addParams("extParams", extParams);

    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<RebateBo> resolveResponse(JSONObject obj) throws Exception {
        if (!obj.isNull("result")) {
            AppDebug.v(TAG,obj.toString());
            return JSON.parseArray(obj.getString("result"),RebateBo.class);
        }
        return null;
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return "1.0";
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
