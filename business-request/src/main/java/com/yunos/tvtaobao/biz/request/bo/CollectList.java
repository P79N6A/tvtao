package com.yunos.tvtaobao.biz.request.bo;


import java.io.Serializable;
import java.util.ArrayList;

public class CollectList implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -610484326142980002L;

    private int totalCount;
    private int currentPage;
    private int pageSize;
    private boolean havNextPage;
    private ArrayList<Collect> resultList;

    public int geTotalCount() {
        return totalCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public ArrayList<Collect> getResultList() {
        return resultList;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    
    public int getPageSize() {
        return pageSize;
    }

    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setResultList(ArrayList<Collect> resultList) {
        this.resultList = resultList;
    }

    public boolean isHavNextPage() {
        return havNextPage;
    }

    public void setHavNextPage(boolean havNextPage) {
        this.havNextPage = havNextPage;
    }
}
