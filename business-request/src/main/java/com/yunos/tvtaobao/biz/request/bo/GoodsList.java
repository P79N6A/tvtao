package com.yunos.tvtaobao.biz.request.bo;


import java.io.Serializable;
import java.util.List;

public class GoodsList implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 3250538045046006676L;
    //    "currentSort": "coefp",
    //    "itemsArray": [
    //    "totalResults": "71",
    //    "shopTitle": "不二家官方旗舰店",
    //    "shopId": "61261204",
    //    "pageSize": "10",
    //    "currentPage": "1"
    private String              currentSort;
    private List<GoodsListItem> itemsArray;
    private int                 totalResults;
    private String              shopTitle;
    private String              shopId;
    private String              pageSize;
    private String              currentPage;
    /** 是否有还有商品，true 有，false 没有 */
    public boolean              isHave;

    public String getCurrentSort() {
        return this.currentSort;
    }

    public void setCurrentSort(String currentSort) {
        this.currentSort = currentSort;
    }

    public List<GoodsListItem> getItemsArray() {
        return this.itemsArray;
    }

    public void setItemsArray(List<GoodsListItem> itemsArray) {
        this.itemsArray = itemsArray;
    }

    public int getTotalResults() {
        return this.totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public String getShopTitle() {
        return this.shopTitle;
    }

    public void setShopTitle(String shopTitle) {
        this.shopTitle = shopTitle;
    }

    public String getShopId() {
        return this.shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    @Override
    public String toString() {
        return "GoodsList [currentSort=" + currentSort + ", itemsArray=" + itemsArray + ", totalResults=" + totalResults + ", shopTitle=" + shopTitle + ", shopId=" + shopId + ", pageSize=" + pageSize + ", currentPage=" + currentPage + "]";
    }

}
