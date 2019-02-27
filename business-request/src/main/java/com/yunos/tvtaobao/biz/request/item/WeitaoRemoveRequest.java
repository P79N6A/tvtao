package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenjiajuan on 17/5/22.
 */

public class WeitaoRemoveRequest extends BaseMtopRequest {
    private String API="mtop.taobao.weitao.follow.remove";
    private String mPubAccountId;
    private String mOriginBiz="tvtaobao";
    private String mOriginPage;
    private String mOriginFlag;


    public   WeitaoRemoveRequest(String pubAccountId, String originPage, String originFlag){
           this.mPubAccountId=pubAccountId;
        this.mOriginPage=originPage;
        this.mOriginFlag=originFlag;


    }
    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {
        if (obj==null){
            return null;
        }
        return obj.toString();

    }

    @Override
    public boolean getNeedEcode() {
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return "3.1";
    }

    @Override
    protected Map<String, String> getAppData() {
        Map<String,String> params=new HashMap<String, String>();
        if (!TextUtils.isEmpty(mPubAccountId)){
            params.put("pubAccountId",mPubAccountId);
        }
         if (!TextUtils.isEmpty(mOriginBiz)){
             params.put("originBiz",mOriginBiz);
         }

        params.put("originPage",mOriginPage);
        params.put("originFlag",mOriginFlag);
        return params;
    }
}
