package com.yunos.tvtaobao.biz.request.bo;


import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class AppendedFeed implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8561464526519303091L;

    //  评价追加内容 
    private String appendedFeedback;

    // 追加评论图片的地址
    private ArrayList<String> appendFeedPicPathList;

    public String getAppendedFeedback() {
        return appendedFeedback;
    }

    public void setAppendedFeedback(String appendedFeedback) {
        this.appendedFeedback = appendedFeedback;
    }

    public ArrayList<String> getAppendFeedPicPathList() {
        return appendFeedPicPathList;
    }

    public void setAppendFeedPicPathList(ArrayList<String> appendFeedPicPathList) {
        this.appendFeedPicPathList = appendFeedPicPathList;
    }

    /**
     * 解析
     * @param response
     * @return
     * @throws Exception
     */
    public static AppendedFeed resolveFromMTOP(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        AppendedFeed appendedFeed = new AppendedFeed();
        if (!obj.isNull("appendedFeedback")) {
            appendedFeed.setAppendedFeedback(obj.getString("appendedFeedback"));
        }

        if (!obj.isNull("appendFeedPicPathList")) {
            JSONArray picArray = obj.getJSONArray("appendFeedPicPathList");
            ArrayList<String> picList = new ArrayList<String>();
            if (picArray != null) {
                for (int i = 0; i < picArray.length(); i++) {
                    String pic_url = picArray.getString(i);
                    if (!TextUtils.isEmpty(pic_url)) {
                        picList.add(pic_url);
                    }
                }
                appendedFeed.setAppendFeedPicPathList(picList);
            }
        }
        return appendedFeed;
    }
}
