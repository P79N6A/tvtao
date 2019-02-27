package com.yunos.tvtaobao.biz.request.bo;


import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ItemRates implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8097860001794189899L;

    /**
     * 买家昵称
     */
    private String userNick;

    /**
     * 买家信誉等级
     */
    private String userStar;

    /**
     * 买家信誉图片
     */
    private String userStarPic;

    /**
     * 评价类型 -1 差评 0 中评 1 好评
     */
    private int rateType;

    /**
     * 评价内容
     */
    private String feedback;

    /**
     * 评价时间
     */
    private String feedbackDate;

    /**
     * 是否匿名 1-匿名 0-非匿名
     */
    private boolean annoy;

    // SKU  
    private Map<String, String> mSkuMap;

    // 图片的地址
    private ArrayList<String> mPicUrlList;

    // 追加的评论信息
    private AppendedFeed mAppendedFeed;

    public ArrayList<String> getPicUrlList() {
        return mPicUrlList;
    }

    public void setPicUrlList(ArrayList<String> mPicUrlList) {
        this.mPicUrlList = mPicUrlList;
    }

    public AppendedFeed getAppendedFeed() {
        return mAppendedFeed;
    }

    public void setAppendedFeed(AppendedFeed mAppendedFeed) {
        this.mAppendedFeed = mAppendedFeed;
    }

    public Map<String, String> getSkuMap() {
        return mSkuMap;
    }

    public void setSkuMap(Map<String, String> mSkuMap) {
        this.mSkuMap = mSkuMap;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getUserStar() {
        return userStar;
    }

    public void setUserStar(String userStar) {
        this.userStar = userStar;
    }

    public String getUserStarPic() {
        return userStarPic;
    }

    public void setUserStarPic(String userStarPic) {
        this.userStarPic = userStarPic;
    }

    public int getRateType() {
        return rateType;
    }

    public void setRateType(int rateType) {
        this.rateType = rateType;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(String feedbackDate) {
        this.feedbackDate = feedbackDate;
    }

    public boolean getAnnoy() {
        return annoy;
    }

    public void setAnnoy(boolean annoy) {
        this.annoy = annoy;
    }

    /**
     * 解析
     * @param response
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static ItemRates resolveFromMTOP(JSONObject obj) throws JSONException {
        if (obj == null)
            return null;
        ItemRates itemRates = new ItemRates();
        if (!obj.isNull("userStar")) {
            itemRates.setUserStar(obj.getString("userStar"));
        }
        if (!obj.isNull("userStarPic")) {
            itemRates.setUserStarPic(obj.getString("userStarPic"));
        }
        if (!obj.isNull("annoy")) {
            String annoy = obj.optString("annoy");
            if ("true".equals(annoy) || "1".equals(annoy)) {
                itemRates.setAnnoy(true);
            } else {
                itemRates.setAnnoy(false);
            }
        }
        if (!obj.isNull("userNick")) {
            String userNick = obj.getString("userNick");
            if (itemRates.getAnnoy()) {
                char[] ch = userNick.toCharArray();
                userNick = ch[0] + "**" + ch[ch.length - 1];
            }
            itemRates.setUserNick(userNick);
        }
        if (!obj.isNull("rateType")) {
            itemRates.setRateType(obj.getInt("rateType"));
        }
        if (!obj.isNull("feedback")) {
            itemRates.setFeedback(obj.getString("feedback"));
        }
        if (!obj.isNull("feedbackDate")) {
            itemRates.setFeedbackDate(obj.getString("feedbackDate"));
        }

        if (!obj.isNull("skuMap")) {
            Map<String, String> skuMap = new HashMap<String, String>();
            JSONObject ob = obj.getJSONObject("skuMap");
            if (obj != null) {
                Iterator<String> it = ob.keys();
                while (it.hasNext()) {
                    String key = it.next();
                    if (!TextUtils.isEmpty(key)) {
                        skuMap.put(key, ob.getString(key));
                    }
                }
                itemRates.setSkuMap(skuMap);
            }
        }

        if (!obj.isNull("feedPicPathList")) {
            JSONArray picArray = obj.getJSONArray("feedPicPathList");
            ArrayList<String> picList = new ArrayList<String>();
            if (picArray != null) {
                for (int i = 0; i < picArray.length(); i++) {
                    String pic_url = picArray.getString(i);
                    if (!TextUtils.isEmpty(pic_url)) {
                        picList.add(pic_url);
                    }
                }
                itemRates.setPicUrlList(picList);
            }
        }

        if (!obj.isNull("appendedFeed")) {
            itemRates.setAppendedFeed(AppendedFeed.resolveFromMTOP(obj.getJSONObject("appendedFeed")));
        }
        return itemRates;
    }

}
