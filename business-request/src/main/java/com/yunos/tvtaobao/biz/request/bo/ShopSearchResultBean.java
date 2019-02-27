package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yuanqihui on 2018/3/1.
 */

public class ShopSearchResultBean implements Serializable{




    /**
     * pageNo : 1
     * pageSize : 15
     * totalNum : 6
     * totalPage : 1
     */
    private String haveNext;
    private String pageNo;
    private String pageSize;
    private String totalNum;
    private String totalPage;
    private List<ItemListBean> itemInfoList;

    @Override
    public String toString() {
        return "ShopSearchResultBean{" +
                "haveNext='" + haveNext + '\'' +
                ", pageNo='" + pageNo + '\'' +
                ", pageSize='" + pageSize + '\'' +
                ", totalNum='" + totalNum + '\'' +
                ", totalPage='" + totalPage + '\'' +
                ", itemInfoList=" + itemInfoList +
                '}';
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
    }

    public String getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(String totalPage) {
        this.totalPage = totalPage;
    }

    public String getHaveNext() {
        return haveNext;
    }

    public void setHaveNext(String haveNext) {
        this.haveNext = haveNext;
    }

    public List<ItemListBean> getItemInfoList() {
        return itemInfoList;
    }

    public void setItemInfoList(List<ItemListBean> itemInfoList) {
        this.itemInfoList = itemInfoList;
    }
}
