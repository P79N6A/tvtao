package com.yunos.tvtaobao.zhuanti.net.item;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseHttpRequest;
import com.yunos.tvtaobao.zhuanti.bo.TvBuyItems;

import java.util.Map;

/**
 * Created by chenjiajuan on 17/4/20.
 */

public class GetTvBuyItemsRequest extends BaseHttpRequest {
    private String HOST = "";

    public GetTvBuyItemsRequest(String url) {
        HOST = url;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TvBuyItems resolveResult(String result) throws Exception {
        if (TextUtils.isEmpty(result))
            return null;
        TvBuyItems tvBuyItems = JSON.parseObject(result, TvBuyItems.class);
        return tvBuyItems;
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
