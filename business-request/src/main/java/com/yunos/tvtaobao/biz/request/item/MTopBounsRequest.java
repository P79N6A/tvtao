package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.yunos.tvtaobao.biz.request.base.BaseHttpRequest;

import java.util.Map;

/**
 * Created by pan on 16/10/13.
 */

public class MTopBounsRequest extends BaseHttpRequest {
    @Override
    public String resolveResult(String result) throws Exception {
        if (!TextUtils.isEmpty(result)) {
            return result;

        }
        return null;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @Override
    protected String getHttpDomain() {
        return "https://fragment.tmall.com/yunos/tianmaojiugongge?spm=0.0.0.0.5mxi3j";
    }
}
