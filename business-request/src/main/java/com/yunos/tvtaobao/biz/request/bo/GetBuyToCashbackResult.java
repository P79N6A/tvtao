package com.yunos.tvtaobao.biz.request.bo;

import com.yunos.tv.core.common.AppDebug;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by LJY on 18/9/7.
 */

public class GetBuyToCashbackResult {

    private Map<String,String> mapIds;

    public Map<String, String> getGoodIds() {
        return mapIds;
    }

    public void setGoodIds(Map<String, String> goodIds) {
        this.mapIds = goodIds;
    }

}
