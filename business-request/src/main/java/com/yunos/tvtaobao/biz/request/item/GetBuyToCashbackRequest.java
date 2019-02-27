package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.CouponRecommendList;
import com.yunos.tvtaobao.biz.request.bo.GetBuyToCashbackResult;
import com.yunos.tvtaobao.biz.request.bo.GoodsSearchZtcResult;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LJY on 18/9/7.
 */

public class GetBuyToCashbackRequest extends BaseMtopRequest {

    private String API = "mtop.taobao.tvtao.tvtaosearchservice.getBuyToCashback";

    private String version = "1.0";

    private String item_ids = null; // 查询指定的商品

    public GetBuyToCashbackRequest(String item_ids) {
        super();
        if (!TextUtils.isEmpty(item_ids)) {
            addParams("nid", item_ids);
        }
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return version;
    }

    @Override
    protected Map<String, String> getAppData() {

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Map<String, String> resolveResponse(JSONObject obj) throws Exception {
        if(obj!=null){
            AppDebug.v(TAG,obj.toString());
            Map<String, String> map = new HashMap<String, String>();
            return JSON.parseObject(obj.toString(),map.getClass());
        }
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
