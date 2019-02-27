package com.yunos.tvtaobao.biz.request.bo;

/**
 * Created by linmu on 2018/9/8.
 */

public class PageInfo {
    private String hasMore;
    private String nextStartTime;
    private String pageSize;
    private String preloadPage;
    private String startRow;
    private String totalCount;
    public void setHasMore(String hasMore) {
        this.hasMore = hasMore;
    }
    public String getHasMore() {
        return hasMore;
    }

    public void setNextStartTime(String nextStartTime) {
        this.nextStartTime = nextStartTime;
    }
    public String getNextStartTime() {
        return nextStartTime;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }
    public String getPageSize() {
        return pageSize;
    }

    public void setPreloadPage(String preloadPage) {
        this.preloadPage = preloadPage;
    }
    public String getPreloadPage() {
        return preloadPage;
    }

    public void setStartRow(String startRow) {
        this.startRow = startRow;
    }
    public String getStartRow() {
        return startRow;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }
    public String getTotalCount() {
        return totalCount;
    }

}