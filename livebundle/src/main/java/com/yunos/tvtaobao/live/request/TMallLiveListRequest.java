package com.yunos.tvtaobao.live.request;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.tvtaobao.voicesdk.utils.GsonUtil;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.TMallLiveBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pan on 16/10/8.
 */

public class TMallLiveListRequest extends BaseMtopRequest {
//    private final String live_list_url = "http://114.55.187.29:7888/api/v1/tmall/live/home"; //测试数据
//    private final String live_list_url = "http://liveapi.zhiping.tv/api/v1/tmall/live/home"; //生产数据
    private String API = "mtop.taobao.tvtao.tvtaoliveservice.gettmallhomelive";
    private String version = "1.0";
    private List<TMallLiveBean> list;

    public TMallLiveListRequest() {
        list = new ArrayList<TMallLiveBean>();
    }

    @Override
    protected List<TMallLiveBean> resolveResponse(JSONObject obj) throws Exception {
        if (!TextUtils.isEmpty(obj.toString())) {
            String result = obj.getString("result");
            JSONObject data = new JSONObject(result);
            JSONArray dataArray = data.getJSONArray("data");
            int lenght = dataArray.length();
            for (int i = 0 ; i < lenght ; i++) {
                list.add(GsonUtil.parseJson(dataArray.get(i).toString(), new TypeToken<TMallLiveBean>() {
                }));
            }
            return list;
        }
        return null;
    }

    @Override
    public boolean getNeedEcode() {
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return true;
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
}
