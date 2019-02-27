/**
 *
 */
package com.yunos.tvtaobao.biz.request.item;


import android.location.Location;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.common.User;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeGoodsBean;
import com.yunos.tvtaobao.biz.request.bo.QueryBagRequestBo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class GetGuessLikeRequest extends BaseMtopRequest {

    private static final String API = "mtop.taobao.wireless.guess.get";

    public GetGuessLikeRequest(String channel) {
        String loc = SharePreferences.getString("location");
//            if (loc != null) {
//                Location location = JSON.parseObject(loc, Location.class);
//                if(location!=null){
//
//                }
//            }
//            if (!TextUtils.isEmpty(queryBagRequestBo.getFeature())) {
//                addParams("feature", queryBagRequestBo.getFeature());
//            }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("currencyCode", "CNY");
            jsonObject.put("countryNumCode", "156");
            jsonObject.put("countryId", "CN");
            jsonObject.put("actualLanguageCode", "zh-CN");
            addParams("edition", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addParams("channel", channel);
        addParams("pageNum", "0");
        addParams("nick", User.getNick());
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
    protected GuessLikeGoodsBean resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        AppDebug.e("GuessLikeGoodsBean = " ,obj.toString());
        return JSON.parseObject(obj.toString(), GuessLikeGoodsBean.class);
    }
}
