package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


public class PaginationItemRates implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1347926490161531370L;
    
    /**
     * 总数
     */
    private String total;
    
    /**
     * 总页数
     */
    private String totalPage;
    
    // 评价总数
    private String feedAllCount;
    
    /**
     * 好评总数
     */
    private String feedGoodCount;
    
    /**
     * 中评总数
     */
    private String feedNormalCount;
    
    /**
     * 差评总数
     */
    private String feedBadCount;
    
    /**
     * 有图的评价数
     */
    private String feedPicCount;
    
    
    
    public String getFeedAllCount() {
        return feedAllCount;
    }


    
    public void setFeedAllCount(String feedAllCount) {
        this.feedAllCount = feedAllCount;
    }


    
    public String getFeedPicCount() {
        return feedPicCount;
    }


    
    public void setFeedPicCount(String feedPicCount) {
        this.feedPicCount = feedPicCount;
    }


    
    public String getFeedAppendCount() {
        return feedAppendCount;
    }


    
    public void setFeedAppendCount(String feedAppendCount) {
        this.feedAppendCount = feedAppendCount;
    }

    /**
     * 追加的评价数
     */
    private String feedAppendCount;
    
    /**
     * 评价列表
     */
    private ItemRates[] itemRates;

    
    public String getTotal() {
        return total;
    }

    
    public void setTotal(String total) {
        this.total = total;
    }

    
    public String getTotalPage() {
        return totalPage;
    }

    
    public void setTotalPage(String totalPage) {
        this.totalPage = totalPage;
    }

    
    public String getFeedGoodCount() {
        return feedGoodCount;
    }

    
    public void setFeedGoodCount(String feedGoodCount) {
        this.feedGoodCount = feedGoodCount;
    }

    
    public String getFeedNormalCount() {
        return feedNormalCount;
    }

    
    public void setFeedNormalCount(String feedNormalCount) {
        this.feedNormalCount = feedNormalCount;
    }

    
    public String getFeedBadCount() {
        return feedBadCount;
    }

    
    public void setFeedBadCount(String feedBadCount) {
        this.feedBadCount = feedBadCount;
    }

    
    public ItemRates[] getItemRates() {
        return itemRates;
    }

    
    public void setItemRates(ItemRates[] itemRates) {
        this.itemRates = itemRates;
    }
    
    /**
     * 解析
     * @param obj
     * @return
     * @throws JSONException
     */
    public static PaginationItemRates resolveFromMTOP(JSONObject obj) throws JSONException {
        if (obj == null) return null;
        PaginationItemRates paginationItemRates = new PaginationItemRates();
        if (!obj.isNull("total")) {
            paginationItemRates.setTotal(obj.getString("total"));
        }
        if (!obj.isNull("totalPage")) {
            paginationItemRates.setTotalPage(obj.getString("totalPage"));
        }
        if (!obj.isNull("feedGoodCount")) {
            paginationItemRates.setFeedGoodCount(obj.getString("feedGoodCount"));
        }
        if (!obj.isNull("feedNormalCount")) {
            paginationItemRates.setFeedNormalCount(obj.getString("feedNormalCount"));
        }
        if (!obj.isNull("feedBadCount")) {
            paginationItemRates.setFeedBadCount(obj.getString("feedBadCount"));
        }
        
        if (!obj.isNull("feedAllCount")) {
            paginationItemRates.setFeedAllCount(obj.getString("feedAllCount"));
        }
        if (!obj.isNull("feedPicCount")) {
            paginationItemRates.setFeedPicCount(obj.getString("feedPicCount"));
        }
        if (!obj.isNull("feedAppendCount")) {
            paginationItemRates.setFeedAppendCount(obj.getString("feedAppendCount"));
        } 
        if (!obj.isNull("rateList")) {
            JSONArray array = obj.getJSONArray("rateList");
            ItemRates[] temp = new ItemRates[array.length()];
            for (int i = 0; i < array.length(); i++) {
                temp[i] = ItemRates.resolveFromMTOP(array.getJSONObject(i));
            }
            paginationItemRates.setItemRates(temp);
        }
        return paginationItemRates;
    }
}
