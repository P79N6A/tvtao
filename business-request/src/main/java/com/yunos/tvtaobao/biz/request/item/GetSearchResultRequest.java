/**
 * $
 * PROJECT NAME: TVTaobao
 * PACKAGE NAME: com.yunos.tvtaobao.request.item
 * FILE NAME: SearchRequest.java
 * CREATED TIME: 2014年10月23日
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.yunos.tvtaobao.biz.request.base.BaseHttpRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2014年10月23日 下午7:36:48
 */
public class GetSearchResultRequest extends BaseHttpRequest {

    private static final long serialVersionUID = 4196001261446570498L;

    public GetSearchResultRequest(String q, String area, String code, int n) {
        if (n > 0) {
            addParams("n", String.valueOf(n));
        }
        addParams("q", q);
        addParams("area", area);
        addParams("code", code);
    }

    @Override
    public ArrayList<String> resolveResult(String result) throws Exception {
        ArrayList<String> returnList = new ArrayList<String>();
        if (TextUtils.isEmpty(result) || !result.contains("result")) {
            return returnList;
        }
        JSONObject obj = new JSONObject(result);
        JSONArray array = obj.getJSONArray("result");
        if (array == null) {
            return returnList;
        }

        for (int i = 0; i < array.length(); i++) {
            JSONArray child = array.getJSONArray(i);
            if (child != null) {
                for (int j = 0; j < child.length(); j++) {
                    String text = child.getString(j);
                    if (!TextUtils.isEmpty(text)) {
                        returnList.add(child.getString(j));
                    }
                }
            }
        }

        return returnList;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @Override
    protected String getHttpDomain() {
        return "https://suggest.taobao.com/sug";
    }
}
