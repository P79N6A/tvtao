package com.yunos.tvtaobao.live.request;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.tvtaobao.voicesdk.utils.GsonUtil;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.TMallLiveCommentBean;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by pan on 16/9/29.
 */

public class GetTMallCommentRequest extends BaseMtopRequest {
    private String API = "mtop.tmall.wireless.fun.funcommentservicemtopapi.getcomments";
    private String version = "1.0";

    public GetTMallCommentRequest(String app, String sourceId, int type, int direction, String timeStamp, String id, int count, boolean includeCommentCount) {
        addParams("app", app);
        addParams("sourceId", sourceId);
        addParams("type", type+"");
        addParams("direction", direction+"");
        if (!timeStamp.equals("0"))
            addParams("timeStamp", timeStamp+"");
        if (!id.equals("0"))
            addParams("id", id+"");
        if (count != 0)
            addParams("count", count+"");
        addParams("includeCommentCount", includeCommentCount+"");
    }

    @Override
    protected TMallLiveCommentBean resolveResponse(JSONObject obj) throws Exception {
        if (!TextUtils.isEmpty(obj.toString())) {
            return GsonUtil.parseJson(obj.toString(), new TypeToken<TMallLiveCommentBean>() {
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
