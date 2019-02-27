/**
 * Copyright (C) 2015 The ALI OS Project
 * Version Date Author
 * 2015-4-19 lizhi.ywp
 */
package com.yunos.tvtaobao.detailbundle.flash;


import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import java.util.List;

public class RequestManager extends BusinessRequest {

    private final static String TAG = "RequestManager";

    /** 获取时间列表 */
    public final static byte REQ_getCategoryList = 0;
    public final static byte REQ_STOCK_BY_BATCHID = 1;
    public final static byte REQ_STOCK_LIST = 2;
    /** 时段抢购s */
    public final static byte REQ_GetTodayHotListRequest = 3;
    /** 获取首页信息接口 */
    public final static byte REQ_ENTRY_INFO = 4;

    /** 获取秒杀数据 */
    public final static byte REQ_getSeckillInfo = 5;

    public final static int CODE_SUCCESS = 200;

    /**
     * 获取场次抢购列表
     * @param listener
     *            请求结果返回的回调函数
     */
    public void getCategoryList(final RequestListener<CategoryList> listener) {
        baseRequest(new CommMtopRequest(REQ_getCategoryList), listener, false);
    }

    /**
     * 获取整点抢购的商品列表
     * @param timeId
     *            整点抢购场次的标识
     * @param listener
     *            请求结果返回的回调函数
     */
    public void getStockList(String timeId, final RequestListener<List<GoodsInfo>> listener) {
        baseRequest(new GetStockInfoRequest(timeId, REQ_STOCK_LIST), listener, false);
    }

    /**
     * 获取商品的信息
     * @param batchId
     *            以逗号分割
     * @param listener
     *            请求结果返回的回调函数
     */
    public void getStockByBatchId(String batchId, final RequestListener<List<GoodsInfo>> listener) {
        baseRequest(new GetStockInfoRequest(batchId, REQ_STOCK_BY_BATCHID), listener, false);
    }

    /**
     * 获取商品的信息
     * @param startTime
     * @param listener
     *            请求结果返回的回调函数
     */
    public void getSeckillInfo(String startTime, RequestListener<List<GoodsInfo>> listener) {
        baseRequest(new GetStockInfoRequest(startTime, REQ_getSeckillInfo), listener, false);
    }

    /**
     * 获取商品的信息
     * @param itemId
     *            商品标识
     * @param sellerId
     *            卖家的id
     * @param listener
     *            请求结果返回的回调函数
     */
    public void getRecommadById(String itemId, String sellerId, final RequestListener<List<RecommendInfo>> listener) {
        baseRequest(new GetRecommadRequest(itemId, sellerId), listener, false);
    }

    /**
     * 获取疯抢列表
     * @param listener
     *            请求结果返回的回调函数
     */
    public void getTodayHotList(final RequestListener<TodayHotList> listener) {
        baseRequest(new CommMtopRequest(REQ_GetTodayHotListRequest), listener, false);
    }

    /**
     * 获取入口的运营数据 TODO 这个接口什么时候调用
     * @return
     */
    public void getEntryInfo(RequestListener<EntryInfo> listener) {
        baseRequest(new CommMtopRequest(REQ_ENTRY_INFO), listener, false);
    }
}
