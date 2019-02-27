package com.yunos.tvtaobao.live.request;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.tvtaobao.voicesdk.utils.GsonUtil;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.TMallLiveCommentBean;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by pan on 16/10/6.
 */

public class SendCommentRequest extends BaseMtopRequest {
    private String API = "mtop.tmall.wireless.fun.funReplyServiceMtopApi.comment";
    private String version = "1.0";

    public SendCommentRequest(String cid, String parentId, String text, String source, String type, String itemId) {
        Log.e("---->", "cid : " + cid + "  , parentId = " + parentId);
        addParams("app", "tlive");
        addParams("sourceId", cid);
        addParams("parentId", parentId);
        addParams("text", text);
        addParams("source", source);
        addParams("type", type);
        addParams("itemId", itemId);
    }

    @Override
    protected TMallLiveCommentBean.ModelBean.DataBean resolveResponse(JSONObject obj) throws Exception {
        if (!TextUtils.isEmpty(obj.toString())) {

            Log.e("TMallLiveListBean",obj+"");
            return GsonUtil.parseJson(obj.toString(), new TypeToken<TMallLiveCommentBean.ModelBean.DataBean>() {
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
