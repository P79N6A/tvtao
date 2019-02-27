package com.tvtaobao.voicesdk.bo;

import com.tvtaobao.voicesdk.utils.JSONUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by pan on 2017/9/14.
 */

public class SearchObject implements Serializable {
    public boolean showUI = false;
    public String keyword;
    public int startIndex = 0;
    public int endIndex = 30;
    public boolean needJinnang = true;
    public boolean needFeeds = false;
    public String priceScope;
    public String sorting;

    public static SearchObject resolverData(String data) {
        JSONObject object = null;
        SearchObject search = new SearchObject();
        try {
            object = new JSONObject(data);
        } catch (JSONException e) {
            return search;
        }

        search.showUI = JSONUtil.getBoolean(object, "showUI");
        search.keyword = JSONUtil.getString(object, "keyword");
        search.startIndex = JSONUtil.getInt(object, "startIndex");
        search.endIndex = JSONUtil.getInt(object, "endIndex");
        search.needJinnang = JSONUtil.getBoolean(object, "needJinnang");
        search.needFeeds = JSONUtil.getBoolean(object, "needFeeds");
        search.priceScope = JSONUtil.getString(object, "priceScope");
        search.sorting = JSONUtil.getString(object, "sorting");
        return search;
    }

    /**
     * 清除排序
     */
    public void clearSift() {
        priceScope = null;
        sorting = null;
    }

    @Override
    public String toString() {
        JSONObject object = new JSONObject();
        try {
            object.put("type", "seach");
            object.put("showUI", showUI);
            object.put("keyword", keyword);
            object.put("startIndex", startIndex);
            object.put("endIndex", endIndex);
            object.put("needJinnang", needJinnang);
            object.put("priceScope", priceScope);
            object.put("sorting", sorting);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }
}
