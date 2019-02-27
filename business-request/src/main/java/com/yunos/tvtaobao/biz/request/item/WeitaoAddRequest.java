package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by chenjiajuan on 17/5/22.
 */

public class WeitaoAddRequest extends BaseMtopRequest {
    private String API="mtop.taobao.social.follow.weitao.add";
    private String mAccountType;
    private String mOriginBiz="tvtaobao";
    private String mPubAccountId;
    private String mOriginPage;
    private String mOriginFlag;


    public WeitaoAddRequest(String accountType, String pubAccountId, String originPage, String originFlag){
        this.mAccountType=accountType;
        this.mPubAccountId=pubAccountId;
        this.mOriginPage=originPage;
        this.mOriginFlag=originFlag;
        if (!TextUtils.isEmpty(mAccountType)){
            addParams("accountType",mAccountType);
        }

        if (!TextUtils.isEmpty(mPubAccountId)){
            addParams("pubAccountId",mPubAccountId);
        }
            if (!TextUtils.isEmpty(mOriginBiz)){
                addParams("originBiz",mOriginBiz);
            }
        addParams("originPage",mOriginPage);
        addParams("originFlag",mOriginFlag);

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
        return null;
    }
}
