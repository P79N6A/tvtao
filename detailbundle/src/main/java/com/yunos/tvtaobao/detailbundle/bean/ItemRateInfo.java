package com.yunos.tvtaobao.detailbundle.bean;


import com.yunos.tvtaobao.biz.request.bo.ItemRates;
import com.yunos.tvtaobao.detailbundle.adapter.DetailEvaluateAdapter;
import com.yunos.tvtaobao.detailbundle.view.DetailListView;

import java.util.ArrayList;

public class ItemRateInfo {

    // 数据
    public ArrayList<ItemRates> mRatesList;
    public DetailListView mEvaluateListView;
    public DetailEvaluateAdapter mEvaluateAdapter;
    // 是否执行点击
    public boolean isPerformClick;

    // TAB父view 是否得到焦点
    public boolean isGainFoucs;
    
    // 当前界面是否显示 
    public boolean isShow;
    
    // 当前内容关联的TAB号
    public int tabNumber;
    
    // 父的VIEW是否显示
    public boolean isFatherViewShow;
    
    // 当前请求第几页
    public int   currentPageNum;
    
    // 每页请求的总数
    public int   pageSize;
    
    // 终止请求
    public boolean end_request;
    
    // 当前TAB总的评论数
    public int total_count; 
    
    public boolean request_loading;
    
    // 第一次请求数据
    public boolean request_first;

    @Override
    public String toString() {
        return "ItemRateInfo [mRatesList=" + mRatesList + ", mEvaluateListView=" + mEvaluateListView
                + ", mEvaluateAdapter=" + mEvaluateAdapter + ", isPerformClick=" + isPerformClick + ", isGainFoucs="
                + isGainFoucs + ", isShow=" + isShow + ", tabNumber=" + tabNumber + ", isFatherViewShow="
                + isFatherViewShow + ", currentPageNum=" + currentPageNum + ", pageSize=" + pageSize + ", end_request="
                + end_request + ", total_count=" + total_count + ", request_loading=" + request_loading
                + ", request_first=" + request_first + "]";
    }
 
}
