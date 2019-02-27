package com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品评论接口返回的数据对象
 * http://dev.wireless.taobao.net/mediawiki/index.php?title=Mtop.wdetail.getItemRatesV2
 * 
 * @author hanqi
 * @date 2014-6-5
 */
public class PaginationItemRate extends BaseMO {

    private static final long serialVersionUID = 7467851418432984165L;

    //查询结果评价总数
    private Long total;
    //查询的结果总页数
    private Integer totalPage;
    //有评论内容的好评数+中评数+差评数
    private Long feedAllCount;
    //有评论内容的好评数，商城的商品无此字段，查询出错时也无此字段
    private Long feedGoodCount;
    //中评数，商城的商品无此字段，查询出错时也无此字段
    private Long feedNormalCount;
    //差评数，商城的商品无此字段，查询出错时也无此字段
    private Long feedBadCount;
    //追加评论数
    private Long feedAppendCount;
    //有图片的评论的数量
    private Long feedPicCount;
    //评论列表
    private List<ItemRate> rateList;
    //0 是集市 1是天猫
    private Integer userType;

    public static PaginationItemRate resolveFromMTOP(JSONObject obj) throws JSONException {
        PaginationItemRate item = null;
        try {
            item = new PaginationItemRate();
            item.setTotal(obj.getLong("total"));
            item.setTotalPage(obj.getInt("totalPage"));
            if (obj.has("feedAllCount")) {
                item.setFeedAllCount(obj.getLong("feedAllCount"));
            }
            if (obj.has("feedGoodCount")) {
                item.setFeedGoodCount(obj.getLong("feedGoodCount"));
            }
            if (obj.has("feedNormalCount")) {
                item.setFeedNormalCount(obj.getLong("feedNormalCount"));
            }
            if (obj.has("feedBadCount")) {
                item.setFeedBadCount(obj.getLong("feedBadCount"));
            }
            if (obj.has("feedAppendCount")) {
                item.setFeedAppendCount(obj.getLong("feedAppendCount"));
            }
            if (obj.has("feedPicCount")) {
                item.setFeedPicCount(obj.getLong("feedPicCount"));
            }
            if (obj.has("rateList")) {
                JSONArray array = obj.getJSONArray("rateList");
                if (null != array && array.length() > 0) {
                    List<ItemRate> list = new ArrayList<ItemRate>();
                    for (int i = 0; i < array.length(); i++) {
                        ItemRate rate = ItemRate.resolveFromMTOP(array.getJSONObject(i));
                        list.add(rate);
                    }
                    item.setRateList(list);
                }
            }
            item.setUserType(obj.getInt("userType"));
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
        return "PaginationItemRate [total=" + total + ", totalPage=" + totalPage + ", feedAllCount=" + feedAllCount + ", feedGoodCount="
                + feedGoodCount + ", feedNormalCount=" + feedNormalCount + ", feedBadCount=" + feedBadCount + ", feedAppendCount=" + feedAppendCount
                + ", feedPicCount=" + feedPicCount + ", rateList=" + rateList + ", userType=" + userType + "]";
    }

    /**
     * @return the total
     */
    public Long getTotal() {
        return total;
    }

    /**
     * @param total
     *            the total to set
     */
    public void setTotal(Long total) {
        this.total = total;
    }

    /**
     * @return the totalPage
     */
    public Integer getTotalPage() {
        return totalPage;
    }

    /**
     * @param totalPage
     *            the totalPage to set
     */
    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    /**
     * @return the feedAllCount
     */
    public Long getFeedAllCount() {
        return feedAllCount;
    }

    /**
     * @param feedAllCount
     *            the feedAllCount to set
     */
    public void setFeedAllCount(Long feedAllCount) {
        this.feedAllCount = feedAllCount;
    }

    /**
     * @return the feedGoodCount
     */
    public Long getFeedGoodCount() {
        return feedGoodCount;
    }

    /**
     * @param feedGoodCount
     *            the feedGoodCount to set
     */
    public void setFeedGoodCount(Long feedGoodCount) {
        this.feedGoodCount = feedGoodCount;
    }

    /**
     * @return the feedNormalCount
     */
    public Long getFeedNormalCount() {
        return feedNormalCount;
    }

    /**
     * @param feedNormalCount
     *            the feedNormalCount to set
     */
    public void setFeedNormalCount(Long feedNormalCount) {
        this.feedNormalCount = feedNormalCount;
    }

    /**
     * @return the feedBadCount
     */
    public Long getFeedBadCount() {
        return feedBadCount;
    }

    /**
     * @param feedBadCount
     *            the feedBadCount to set
     */
    public void setFeedBadCount(Long feedBadCount) {
        this.feedBadCount = feedBadCount;
    }

    /**
     * @return the feedAppendCount
     */
    public Long getFeedAppendCount() {
        return feedAppendCount;
    }

    /**
     * @param feedAppendCount
     *            the feedAppendCount to set
     */
    public void setFeedAppendCount(Long feedAppendCount) {
        this.feedAppendCount = feedAppendCount;
    }

    /**
     * @return the feedPicCount
     */
    public Long getFeedPicCount() {
        return feedPicCount;
    }

    /**
     * @param feedPicCount
     *            the feedPicCount to set
     */
    public void setFeedPicCount(Long feedPicCount) {
        this.feedPicCount = feedPicCount;
    }

    /**
     * @return the rateList
     */
    public List<ItemRate> getRateList() {
        return rateList;
    }

    /**
     * @param rateList
     *            the rateList to set
     */
    public void setRateList(List<ItemRate> rateList) {
        this.rateList = rateList;
    }

    /**
     * @return the userType
     */
    public Integer getUserType() {
        return userType;
    }

    /**
     * @param userType
     *            the userType to set
     */
    public void setUserType(Integer userType) {
        this.userType = userType;
    }

}
