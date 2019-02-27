/**
 * Copyright (C) 2015 The ALI OS Project
 * Version Date Author
 * 2015-4-19 lizhi.ywp
 */
package com.yunos.tvtaobao.flashsale.request.item;


import android.os.SystemClock;

import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.flashsale.bo.CategoryList;
import com.yunos.tvtaobao.flashsale.bo.EntryInfo;
import com.yunos.tvtaobao.flashsale.bo.TodayHotList;
import com.yunos.tvtaobao.flashsale.request.RequestManager;

import org.json.JSONObject;

import java.util.Map;

public class CommMtopRequest extends BaseMtopRequest {

    private static final long serialVersionUID = -5389911441494295550L;
    private byte mFlag;

    public CommMtopRequest(byte flag) {
        mFlag = flag;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object resolveResponse(JSONObject obj) throws Exception {
        AppDebug.e(TAG,"CommMtopRequest---= "+obj.toString());
        // TODO Auto-generated method stub
        Object ret = null;
        long localRef = SystemClock.elapsedRealtime();

        if (mFlag == RequestManager.REQ_ENTRY_INFO) {
            EntryInfo info = JSON.parseObject(obj.toString(), EntryInfo.class);
            if (null != info) {
                info.setLocalRef(localRef);
                ret = info;
                AppDebug.e(TAG,"");
            }
        } else if (mFlag == RequestManager.REQ_getCategoryList) {
            CategoryList categoryList = (null != obj) ? JSON.parseObject(obj.toString(),
                    CategoryList.class) : null;

            if (null != categoryList) {
                categoryList.setLocalRef(localRef);
                ret = categoryList;
            }
        } else if (mFlag == RequestManager.REQ_GetTodayHotListRequest) {
            TodayHotList hotList = TodayHotList.resolveResponse(obj);
            if (null != hotList) {
                ret = hotList;
            }
        }
        return ret;
    }

    @Override
    protected String getApi() {
        if (mFlag == RequestManager.REQ_ENTRY_INFO) {
            AppDebug.e(TAG,"-----flag1");
            return "mtop.msp.qianggou.getIndexItem";
        } else if (mFlag == RequestManager.REQ_getCategoryList) {
            AppDebug.e(TAG,"-----flag2");
            return "mtop.msp.qianggou.queryBatch";
        } else if (mFlag == RequestManager.REQ_GetTodayHotListRequest) {
            AppDebug.e(TAG,"-----flag3");
            return "mtop.msp.qianggou.queryTodayHotItems";
        }
        return null;
    }

    @Override
    protected String getApiVersion() {
        if (mFlag == RequestManager.REQ_getCategoryList) {
            return "3.0";
        }
        return "1.0";

    }

    @Override
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        if (mFlag == RequestManager.REQ_getCategoryList) {
            return true;
        }
        return false;
    }
}
