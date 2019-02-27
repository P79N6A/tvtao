package com.tvtaobao.voicesdk.request;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.tvtaobao.voicesdk.bo.Location;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/8/16
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class TakeOutSearchRequest extends BaseMtopRequest {
    private final String API = "mtop.taobao.tvtao.takeout.queryKeywordShopListNew";
    private final String VERSION = "1.0";

    private Location location;
    private String lat = null;
    private String lon = null;

    public TakeOutSearchRequest(String keywords, int pageNo, int pageSize, String locaStr, String orderType) {
        JSONObject object = new JSONObject();
        try {
            if (!TextUtils.isEmpty(locaStr)) {
                location = JSON.parseObject(locaStr, Location.class);
                lat = location.x;
                lon = location.y;
            }

            if (TextUtils.isEmpty(lat) && TextUtils.isEmpty(lon)) {
                String loc = SharePreferences.getString("location");
                if (loc != null) {
                    location = JSON.parseObject(loc, Location.class);
                }
                if (location != null) {
                    lat = location.x;
                    lon = location.y;
                }
            }

            object.put("lon", lon);
            object.put("lat", lat);
            object.put("keyWord", keywords);
            object.put("pageNo", String.valueOf(pageNo));
            object.put("pageSize", String.valueOf(pageSize));
            object.put("appkey", Config.getChannel());
            if (!TextUtils.isEmpty(orderType)) {
                object.put("orderType", orderType);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addParams("jsonStr", object.toString());
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
    protected String resolveResponse(JSONObject obj) throws Exception {
        return obj.toString();
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
