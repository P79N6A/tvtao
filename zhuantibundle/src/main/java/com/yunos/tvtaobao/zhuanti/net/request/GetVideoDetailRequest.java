package com.yunos.tvtaobao.zhuanti.net.request;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.zhuanti.bo.TvVideoDetail;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by chenjiajuan on 17/6/20.
 */

public class GetVideoDetailRequest extends BaseMtopRequest {
    private String API = "mtop.taobao.tvtao.VideoApiService.getDetail";
    private String version = "1.0";

    public GetVideoDetailRequest(String videoId) {
        if (!TextUtils.isEmpty(videoId)) {
            addParams("videoId", videoId);
        }

    }

    @Override
    protected TvVideoDetail resolveResponse(JSONObject obj) throws Exception {
        if (!TextUtils.isEmpty(obj.toString())) {
            return JSON.parseObject(obj.toString(), TvVideoDetail.class);
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
