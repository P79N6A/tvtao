package com.yunos.tvtaobao.zhuanti.net.item;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseHttpRequest;
import com.yunos.tvtaobao.zhuanti.bo.enumration.GoodItemSold;

import java.util.Map;

/**
 * Created by chenjiajuan on 17/4/22.
 */

public class GetGoodSoldRequest extends BaseHttpRequest {
    private String HOST = "http://api.s.m.taobao.com/search.json?vm=nw&n=40&nid=";

    public GetGoodSoldRequest(String itemId) {
        HOST = HOST + itemId;
    }

    @Override
    public GoodItemSold resolveResult(String result) throws Exception {
        if (TextUtils.isEmpty(result))
            return null;
        GoodItemSold goodItemSold = JSON.parseObject(result.toString(), GoodItemSold.class);
        AppDebug.e("TAG", "GetGoodSoldRequest = " + goodItemSold.toString());
        return goodItemSold;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @Override
    protected String getHttpDomain() {
        return HOST;
    }
}
