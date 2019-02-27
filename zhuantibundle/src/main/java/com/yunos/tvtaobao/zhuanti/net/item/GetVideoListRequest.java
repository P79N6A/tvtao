package com.yunos.tvtaobao.zhuanti.net.item;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.zhuanti.bo.TvVideoList;

import org.json.JSONObject;

import java.util.Map;


/**
 * Created by cenjiajuan on 17/6/20.
 */

public class GetVideoListRequest extends BaseMtopRequest {
    private String API = "mtop.taobao.tvtao.VideoApiService.getList";
    private String version = "1.0";

    public GetVideoListRequest(String videoId, String albumId) {
        addParams("videoId", videoId);
        addParams("albumId", albumId);
    }

    @Override
    protected TvVideoList resolveResponse(JSONObject obj) throws Exception {
        if (!TextUtils.isEmpty(obj.toString())) {
            return JSON.parseObject(obj.toString(), TvVideoList.class);
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
