package com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CountList<T> extends ArrayList<T> {

    private static final long serialVersionUID = -6358779524239576369L;
    // 商品的总数量
    private Integer itemCount;
    // 每页个数
    private Integer pageSize;
    // 当前页数
    private Integer currentPage;
    // 总页数
    private Integer totalPage;

    /**
     * @param preItemList
     */
    public CountList(List<T> list) {
        super();
        for (T t : list) {
            add(t);
        }
    }

    /**
     * 
     */
    public CountList() {
        super();
    }

    public static <T> CountList<T> fromMTOP(JSONObject obj) throws JSONException {
        if (null == obj) {
            return null;
        }
        CountList<T> item = new CountList<T>();
        item.setItemCount(obj.optInt("totalItem"));
        if (null == item.getItemCount() || item.getItemCount() <= 0) {
            item.setItemCount(obj.optInt("itemCount"));
        }
        item.setPageSize(obj.optInt("pageSize"));
        item.setCurrentPage(obj.optInt("currentPage"));
        item.setTotalPage(obj.optInt("totalPage"));
        return item;
    }

    /**
     * @return the itemCount
     */
    public Integer getItemCount() {
        return itemCount;
    }

    /**
     * @param itemCount the itemCount to set
     */
    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    /**
     * @return the pageSize
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize the pageSize to set
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return the currentPage
     */
    public Integer getCurrentPage() {
        return currentPage;
    }

    /**
     * @param currentPage the currentPage to set
     */
    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * @return the totalPage
     */
    public Integer getTotalPage() {
        return totalPage;
    }

    /**
     * @param totalPage the totalPage to set
     */
    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CountList [itemCount=" + itemCount + ", pageSize=" + pageSize + ", currentPage=" + currentPage
                + ", totalPage=" + totalPage + ", size=" + size() + "]";
    }

}
