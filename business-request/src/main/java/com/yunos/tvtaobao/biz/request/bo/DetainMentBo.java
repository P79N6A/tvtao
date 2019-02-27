package com.yunos.tvtaobao.biz.request.bo;


import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DetainMentBo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 9098172277300419097L;

    private String nid;

    private String title;

    private String real_wap_price;

    private String pict_url;

    private String click_url;

    private String auction_tag;

    private String flag;

    private String server;

    private String ctrScore;

    private String cvrScore;

    private String retentionScore;

    private String weight;

    private String tiggerItemInfo;

    private String matchType;

    private String triggerItemId;

    private String rankType;

    private String rankScore;

    private String algFlag;

    private String month_sale;

    private String similarUrl;

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return real_wap_price;
    }

    public void setPrice(String real_wap_price) {
        this.real_wap_price = real_wap_price;
    }

    public String getPictUrl() {
        return pict_url;
    }

    public void setPictUrl(String pict_url) {
        this.pict_url = pict_url;
    }

    public String getClickUrl() {
        return click_url;
    }

    public void setClickUrl(String click_url) {
        this.click_url = click_url;
    }

    public String getAuctionTag() {
        return auction_tag;
    }

    public void setAuctionTag(String auction_tag) {
        this.auction_tag = auction_tag;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getCtrScore() {
        return ctrScore;
    }

    public void setCtrScore(String ctrScore) {
        this.ctrScore = ctrScore;
    }

    public String getCvrScore() {
        return cvrScore;
    }

    public void setCvrScore(String cvrScore) {
        this.cvrScore = cvrScore;
    }

    public String getRetentionScore() {
        return retentionScore;
    }

    public void setRetentionScore(String retentionScore) {
        this.retentionScore = retentionScore;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getTiggerItemInfo() {
        return tiggerItemInfo;
    }

    public void setTiggerItemInfo(String tiggerItemInfo) {
        this.tiggerItemInfo = tiggerItemInfo;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getTriggerItemId() {
        return triggerItemId;
    }

    public void setTriggerItemId(String triggerItemId) {
        this.triggerItemId = triggerItemId;
    }

    public String getRankType() {
        return rankType;
    }

    public void setRankType(String rankType) {
        this.rankType = rankType;
    }

    public String getRankScore() {
        return rankScore;
    }

    public void setRankScore(String rankScore) {
        this.rankScore = rankScore;
    }

    public String getAlgFlag() {
        return algFlag;
    }

    public void setAlgFlag(String algFlag) {
        this.algFlag = algFlag;
    }

    public String getMonthSale() {
        return month_sale;
    }

    public void setMonthSale(String month_sale) {
        this.month_sale = month_sale;
    }

    public String getSimilarUrl() {
        return similarUrl;
    }

    public void setSimilarUrl(String similarUrl) {
        this.similarUrl = similarUrl;
    }

    public static DetainMentBo[] resolve(String response) throws JSONException {

        if (TextUtils.isEmpty(response)) {
            return null;
        }

        JSONObject obj = new JSONObject(response);
        JSONArray resultArray = obj.optJSONArray("result");
        if (resultArray == null || resultArray.length() == 0) {
            return null;
        }

        List<DetainMentBo> list = new ArrayList<DetainMentBo>();
        list.clear();

        int lenth = resultArray.length();

        for (int i = 0; i < lenth; i++) {

            JSONObject result = resultArray.optJSONObject(i);
            if (result != null) {
                DetainMentBo detainMentBo = new DetainMentBo();

                detainMentBo.setNid(result.optString("nid"));
                detainMentBo.setTitle(result.optString("title"));
                detainMentBo.setPrice(result.optString("real_wap_price"));
                detainMentBo.setPictUrl(result.optString("pict_url"));
                detainMentBo.setClickUrl(result.optString("click_url"));
                detainMentBo.setAuctionTag(result.optString("auction_tag"));
                detainMentBo.setFlag(result.optString("flag"));
                detainMentBo.setServer(result.optString("server"));
                detainMentBo.setCtrScore(result.optString("ctrScore"));
                detainMentBo.setCvrScore(result.optString("cvrScore"));
                detainMentBo.setRetentionScore(result.optString("retentionScore"));
                detainMentBo.setWeight(result.optString("weight"));
                detainMentBo.setTiggerItemInfo(result.optString("tiggerItemInfo"));
                detainMentBo.setMatchType(result.optString("matchType"));

                detainMentBo.setTriggerItemId(result.optString("triggerItemId"));
                detainMentBo.setRankType(result.optString("rankType"));
                detainMentBo.setRankScore(result.optString("rankScore"));
                detainMentBo.setAlgFlag(result.optString("algFlag"));
                detainMentBo.setMonthSale(result.optString("month_sale"));
                detainMentBo.setSimilarUrl(result.optString("similarUrl"));

                String title = detainMentBo.getTitle();
                String picurl = detainMentBo.getPictUrl();
                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(picurl)) {
                    list.add(detainMentBo);
                }
            }
        }
        if (list.isEmpty()) {
            return null;
        }
        return list.toArray(new DetainMentBo[list.size()]);
    }

    @Override
    public String toString() {
        return "DetainMentBo [nid=" + nid + ", title=" + title + ", real_wap_price=" + real_wap_price + ", pict_url="
                + pict_url + ", click_url=" + click_url + ", auction_tag=" + auction_tag + ", flag=" + flag
                + ", server=" + server + ", ctrScore=" + ctrScore + ", cvrScore=" + cvrScore + ", retentionScore="
                + retentionScore + ", weight=" + weight + ", tiggerItemInfo=" + tiggerItemInfo + ", matchType="
                + matchType + ", triggerItemId=" + triggerItemId + ", rankType=" + rankType + ", rankScore="
                + rankScore + ", algFlag=" + algFlag + ", month_sale=" + month_sale + ", similarUrl=" + similarUrl
                + "]";
    }
}
