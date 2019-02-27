package com.yunos.tvtaobao.live.request;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.tvtaobao.voicesdk.utils.GsonUtil;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.LiveDetailBean;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by libin on 16/9/17.
 */

public class GetLiveDetailRequest extends BaseMtopRequest {
    //延迟直播详情接口
    private String API = "mtop.taobao.tvtao.liveservice.getlivedetail";
    //private String API = "mtop.mediaplatform.live.livedetail";
    private String version = "1.0";

    public GetLiveDetailRequest(String liveId) {
        if (liveId != null){
            addParams("liveId",liveId);
        }
            addParams("deviceId",CloudUUIDWrapper.getCloudUUID());
    }

    @Override
    protected LiveDetailBean resolveResponse(JSONObject obj) throws Exception {
        if (!TextUtils.isEmpty(obj.toString())) {

            return GsonUtil.parseJson(obj.toString(), new TypeToken<LiveDetailBean>() {
            });
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
