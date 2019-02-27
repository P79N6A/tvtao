package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.util.GsonUtil;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.DoPayOrders;
import com.yunos.tvtaobao.biz.request.bo.SearchRelationRecommendBean;
import com.yunos.tvtaobao.biz.request.bo.SearchRelationRecommendItemBean;

import junit.runner.Version;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSON;


/**
 * Created by xtt
 * on 2018/12/21
 * desc
 */
public class GetSearhRelationRecommendRequest extends BaseMtopRequest {

    private static final String API = "mtop.relationrecommend.wirelessrecommend.recommend";

    private static final String VERSION = "2.0";

    private static final String TAG = "GetSearhRelationRecommendRequest";


    public GetSearhRelationRecommendRequest(String key) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("utd_id", "V/dBKGjeAZUDAEW8r1BXs/v1");
            jsonObject.put("tab", "");
            jsonObject.put("ttid", Config.getTTid());
            AppDebug.i(TAG, "ttid = " + Config.getTTid());
            jsonObject.put("area", "wireless_gbdt_newoutput");
            jsonObject.put("code", "utf-8");
            if (User.isLogined() && !TextUtils.isEmpty(User.getNick())) {
                jsonObject.put("u", User.getNick());
            }
            jsonObject.put("q", key);
            jsonObject.put("sversion", "5.8");
            jsonObject.put("editionCode", "CN");
            jsonObject.put("_input_charset", "UTF-8");
            jsonObject.put("_output_charset", "UTF-8");


            addParams("params", jsonObject.toString());
        } catch (JSONException e) {
            AppDebug.d(TAG, e.toString());
        }
        addParams("appId", "10650");

    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @Override
    protected ArrayList<String> resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            AppDebug.d(TAG, "--------->>obj is null<<------------");
            return null;
        }

        SearchRelationRecommendBean searchRelationRecommendBean = JSON.parseObject(obj.toString(), SearchRelationRecommendBean.class);
        ArrayList<String> returnList = new ArrayList<String>();

        if (searchRelationRecommendBean != null
                && searchRelationRecommendBean.getResult() != null
                && searchRelationRecommendBean.getResult().size() > 0) {
            List<SearchRelationRecommendBean.Result> results = searchRelationRecommendBean.getResult();
            for (int i = 0; i < results.size(); i++) {
                if (results.get(i) != null && results.get(i).getData() != null) {
                    SearchRelationRecommendItemBean data = results.get(i).getData();
                    if (data.getResult() != null && data.getResult().size() > 0) {
                        List<SearchRelationRecommendItemBean.DataResult> dataResultList = data.getResult();
                        for(int j = 0;j<dataResultList.size();j++){
                            if(dataResultList.get(j)!=null) {
                                SearchRelationRecommendItemBean.DataResult dataResult = dataResultList.get(j);
                                if(!TextUtils.isEmpty(dataResult.getShowtext())){
                                    returnList.add(dataResult.getShowtext());
                                }

                            }
                        }


                    }
                }
            }
        }
        return returnList;
    }

    @Override
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return VERSION;
    }


}
