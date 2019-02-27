package com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo;


import com.yunos.tvtaobao.biz.request.core.JsonResolver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 评价评论
 * 
 * @author hanqi
 * @date 2014-6-5
 */
public class ItemRate extends BaseMO {

    private static final long serialVersionUID = -8499072479420537523L;

    //评价数据id
    private Long id;
    //商品ID，itemId
    private Long auctionNumId;
    //商品名称
    private String auctionTitle;
    //买家用户id,已被隐藏，无法使用
    private Long userId;
    //买家用户昵称
    private String userNick;
    //买家信用等级
    private Integer userStar;
    //用户头像
    private String headPicUrl;
    //是否匿名评价,0否，1是
    private Integer annoy;
    //评价类型 -1 – 差评, 0 –中评, 1 –好评
    private Integer rateType;
    //用户评价内容
    private String feedback;
    //评价日期
    private String feedbackDate;
    //卖家对用户第一次评价的回复内容（非追加评论回复）
    private String reply;
    //商品订单SKU信息
    private Map<String, String> skuMap;
    //追加评价信息
    private ItemRateAppend appendedFeed;
    //用户创建评价时上传的图片
    private List<String> feedPicPathList;

    public static ItemRate resolveFromMTOP(JSONObject obj) throws JSONException {
        ItemRate item = null;
        try {
            item = new ItemRate();
            item.setId(obj.getLong("id"));
            item.setAuctionNumId(obj.getLong("auctionNumId"));
            if (obj.has("auctionTitle")) {
                item.setAuctionTitle(obj.getString("auctionTitle"));
            }
            if (obj.has("userId")) {
                item.setUserId(obj.getLong("userId"));
            }
            item.setUserNick(obj.getString("userNick"));
            item.setUserStar(obj.getInt("userStar"));
            if (obj.has("headPicUrl")) {
                item.setHeadPicUrl(obj.getString("headPicUrl"));
            }
            if (obj.has("annoy")) {
                try {
                    item.setAnnoy(obj.getInt("annoy"));
                } catch (JSONException e) {
                    Boolean annoy = obj.getBoolean("annoy");
                    if (annoy) {
                        item.setAnnoy(1);
                    } else {
                        item.setAnnoy(0);
                    }
                }
            }
            item.setRateType(obj.getInt("rateType"));
            item.setFeedback(obj.getString("feedback"));
            item.setFeedbackDate(obj.getString("feedbackDate"));
            if (obj.has("reply")) {
                item.setReply(obj.getString("reply"));
            }
            if (obj.has("skuMap")) {
                item.setSkuMap(JsonResolver.jsonobjToMap(obj.getJSONObject("skuMap")));
            }
            if (obj.has("appendedFeed")) {
                item.setAppendedFeed(ItemRateAppend.resolveFromMTOP(obj.getJSONObject("appendedFeed")));
            }
            if (obj.has("feedPicPathList")) {
                item.setFeedPicPathList(JsonResolver.resolveStringArray(obj.getJSONArray("feedPicPathList")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    public static class ItemRateAppend extends BaseMO {

        private static final long serialVersionUID = -4211318135284047036L;

        //追加评价的内容
        private String appendedFeedback;
        //追加评价的时间跟确认收货的时间之间的天数，如果计算错误返回-1
        private Integer intervalDay;
        //追加评价时上传的退片，如果为空不返回
        private List<String> appendFeedPicPathList;

        public static ItemRateAppend resolveFromMTOP(JSONObject obj) throws JSONException {
            ItemRateAppend item = null;
            try {
                item = new ItemRateAppend();
                item.setAppendedFeedback(obj.getString("appendedFeedback"));
                item.setIntervalDay(obj.getInt("intervalDay"));
                if (obj.has("appendFeedPicPathList")) {
                    item.setAppendFeedPicPathList(JsonResolver.resolveStringArray(obj.getJSONArray("appendFeedPicPathList")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return item;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "ItemRateAppend [appendedFeedback=" + appendedFeedback + ", intervalDay=" + intervalDay + ", appendFeedPicPathList="
                    + appendFeedPicPathList + "]";
        }

        /**
         * @return the appendedFeedback
         */
        public String getAppendedFeedback() {
            return appendedFeedback;
        }

        /**
         * @param appendedFeedback
         *            the appendedFeedback to set
         */
        public void setAppendedFeedback(String appendedFeedback) {
            this.appendedFeedback = appendedFeedback;
        }

        /**
         * @return the intervalDay
         */
        public Integer getIntervalDay() {
            return intervalDay;
        }

        /**
         * @param intervalDay
         *            the intervalDay to set
         */
        public void setIntervalDay(Integer intervalDay) {
            this.intervalDay = intervalDay;
        }

        /**
         * @return the appendFeedPicPathList
         */
        public List<String> getAppendFeedPicPathList() {
            return appendFeedPicPathList;
        }

        /**
         * @param appendFeedPicPathList
         *            the appendFeedPicPathList to set
         */
        public void setAppendFeedPicPathList(List<String> appendFeedPicPathList) {
            this.appendFeedPicPathList = appendFeedPicPathList;
        }

    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ItemRate [id=" + id + ", auctionNumId=" + auctionNumId + ", auctionTitle=" + auctionTitle + ", userId=" + userId + ", userNick="
                + userNick + ", userStar=" + userStar + ", headPicUrl=" + headPicUrl + ", annoy=" + annoy + ", rateType=" + rateType + ", feedback="
                + feedback + ", feedbackDate=" + feedbackDate + ", reply=" + reply + ", skuMap=" + skuMap + ", appendedFeed=" + appendedFeed
                + ", feedPicPathList=" + feedPicPathList + "]";
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the auctionNumId
     */
    public Long getAuctionNumId() {
        return auctionNumId;
    }

    /**
     * @param auctionNumId
     *            the auctionNumId to set
     */
    public void setAuctionNumId(Long auctionNumId) {
        this.auctionNumId = auctionNumId;
    }

    /**
     * @return the auctionTitle
     */
    public String getAuctionTitle() {
        return auctionTitle;
    }

    /**
     * @param auctionTitle
     *            the auctionTitle to set
     */
    public void setAuctionTitle(String auctionTitle) {
        this.auctionTitle = auctionTitle;
    }

    /**
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId
     *            the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return the userNick
     */
    public String getUserNick() {
        return userNick;
    }

    /**
     * @param userNick
     *            the userNick to set
     */
    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    /**
     * @return the userStar
     */
    public Integer getUserStar() {
        return userStar;
    }

    /**
     * @param userStar
     *            the userStar to set
     */
    public void setUserStar(Integer userStar) {
        this.userStar = userStar;
    }

    /**
     * @return the headPicUrl
     */
    public String getHeadPicUrl() {
        return headPicUrl;
    }

    /**
     * @param headPicUrl
     *            the headPicUrl to set
     */
    public void setHeadPicUrl(String headPicUrl) {
        this.headPicUrl = headPicUrl;
    }

    /**
     * @return the annoy
     */
    public Integer getAnnoy() {
        return annoy;
    }

    /**
     * @param annoy
     *            the annoy to set
     */
    public void setAnnoy(Integer annoy) {
        this.annoy = annoy;
    }

    /**
     * @return the rateType
     */
    public Integer getRateType() {
        return rateType;
    }

    /**
     * @param rateType
     *            the rateType to set
     */
    public void setRateType(Integer rateType) {
        this.rateType = rateType;
    }

    /**
     * @return the feedback
     */
    public String getFeedback() {
        return feedback;
    }

    /**
     * @param feedback
     *            the feedback to set
     */
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    /**
     * @return the feedbackDate
     */
    public String getFeedbackDate() {
        return feedbackDate;
    }

    /**
     * @param feedbackDate
     *            the feedbackDate to set
     */
    public void setFeedbackDate(String feedbackDate) {
        this.feedbackDate = feedbackDate;
    }

    /**
     * @return the reply
     */
    public String getReply() {
        return reply;
    }

    /**
     * @param reply
     *            the reply to set
     */
    public void setReply(String reply) {
        this.reply = reply;
    }

    /**
     * @return the skuMap
     */
    public Map<String, String> getSkuMap() {
        return skuMap;
    }

    /**
     * @param skuMap
     *            the skuMap to set
     */
    public void setSkuMap(Map<String, String> skuMap) {
        this.skuMap = skuMap;
    }

    /**
     * @return the appendedFeed
     */
    public ItemRateAppend getAppendedFeed() {
        return appendedFeed;
    }

    /**
     * @param appendedFeed
     *            the appendedFeed to set
     */
    public void setAppendedFeed(ItemRateAppend appendedFeed) {
        this.appendedFeed = appendedFeed;
    }

    /**
     * @return the feedPicPathList
     */
    public List<String> getFeedPicPathList() {
        return feedPicPathList;
    }

    /**
     * @param feedPicPathList
     *            the feedPicPathList to set
     */
    public void setFeedPicPathList(List<String> feedPicPathList) {
        this.feedPicPathList = feedPicPathList;
    }

}
