package com.yunos.tvtaobao.biz.request.item;

import com.yunos.tvtaobao.biz.request.base.BaseHttpRequest;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by libin on 16/9/26.
 */

public class TypeWordsRequest extends BaseHttpRequest {
    private String orgin = "";

    public TypeWordsRequest(String orgin) {
        this.orgin = orgin;
    }

    @Override
    public String resolveResult(String result) throws Exception {
        return result;
    }

    @Override
    protected Map<String, String> getAppData() {
        Map<String, String> params = new TreeMap<String, String>();

        params.put("sentence",orgin);

        return params;
    }

    @Override
    protected String getHttpDomain() {
        return "http://121.196.200.124:8888/nlp/pos_tagging";
    }
}
