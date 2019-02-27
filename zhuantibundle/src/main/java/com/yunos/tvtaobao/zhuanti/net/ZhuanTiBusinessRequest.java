package com.yunos.tvtaobao.zhuanti.net;


import android.content.Context;

import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.SearchResult;
import com.yunos.tvtaobao.biz.request.item.AddBagRequest;
import com.yunos.tvtaobao.zhuanti.bo.TvBuyItems;
import com.yunos.tvtaobao.zhuanti.bo.TvGetIntegration;
import com.yunos.tvtaobao.zhuanti.bo.TvIntegration;
import com.yunos.tvtaobao.zhuanti.bo.TvVideoDetail;
import com.yunos.tvtaobao.zhuanti.bo.TvVideoList;
import com.yunos.tvtaobao.zhuanti.bo.enumration.GoodItemSold;
import com.yunos.tvtaobao.zhuanti.net.item.GetGoodSoldRequest;
import com.yunos.tvtaobao.zhuanti.net.item.GetTvBuyItemsRequest;
import com.yunos.tvtaobao.zhuanti.net.item.GetVideoListRequest;
import com.yunos.tvtaobao.zhuanti.net.item.GetVideoPointSchemeRequest;
import com.yunos.tvtaobao.zhuanti.net.item.GetVideoPointsRequest;
import com.yunos.tvtaobao.zhuanti.net.request.GetVideoDetailRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * 接口调用封装
 * @author hanqi
 * @date 2014-6-20
 */
public class ZhuanTiBusinessRequest extends BusinessRequest {

    public static final String TAG = "ZhuanTiBusinessRequest";
    public static ZhuanTiBusinessRequest mBusinessRequest;

    /**
     * 单例
     * @return
     */
    public static ZhuanTiBusinessRequest getBusinessRequest() {
        if (mBusinessRequest == null) {
            mBusinessRequest = new ZhuanTiBusinessRequest();
        }
        return mBusinessRequest;
    }

//    /**
//     * 获取视频购物tms配置信息
//     * @param context
//     * @param tms
//     *            tms地址
//     * @param listener
//     */
//    public void requestTmsVideoItems(final Context context, final String tms,
//                                     final RequestListener<VideoRule> listener) {
//        baseRequest(new GetTmsVideoItemsRequest(tms), listener, false);
//    }
//    /**
//     * 获取新版视频详情页视频列表
//     * @param context
//     * @param tms         地址
//     * @param listener
//     */
//
//    public void requestTvBuyItems(final Context context, final String tms,
//                                  final RequestListener<TvBuyItems> listener){
//        baseRequest(new GetTvBuyItemsRequest(tms),listener,false);
//    }

    /**
     * 获取 NewTvBuyActivity视频列表
     * @param videoId
     * @param album
     * @param listener
     */
    public void requestTvVideoList(final String videoId, final String album,
                                   final RequestListener<TvVideoList> listener){
        baseRequest(new GetVideoListRequest(videoId,album),listener,false);
    }


    public  void requestVideoDetail(final String videoId, final RequestListener<TvVideoDetail> listener){
        baseRequest(new GetVideoDetailRequest(videoId),listener,false);
    }


    public  void requestVideoPointScheme(final RequestListener<TvIntegration> listener){
        baseRequest(new GetVideoPointSchemeRequest(),listener,false);
    }



    /**
     * 视频详情页，商品加入购物车
     * @param itemId
     * @param quantity 数量
     * @param skuId    可为null
     * @param extParams  可为null
     * @param listener
     */
    public  void requestAddCartRequest(final String itemId, final int quantity, final String skuId, final String extParams,
                                       final RequestListener<ArrayList<SearchResult>> listener){
        baseRequest(new AddBagRequest(itemId, quantity, skuId, extParams), listener, true, true);
    }

    /**
     * 视频详情页，领取积分
     * @param pointSchemeId
     * @param listener
     */

    public void requestGetVideoPointRequest(final String pointSchemeId , final RequestListener<TvGetIntegration> listener){
        baseRequest(new GetVideoPointsRequest(pointSchemeId),listener,true);
    }

    /**
     * 获取新版视频详情页视频列表
     * @param context
     * @param tms         地址
     * @param listener
     */

    public void requestTvBuyItems(final Context context, final String tms,
                                  final RequestListener<TvBuyItems> listener){
        baseRequest(new GetTvBuyItemsRequest(tms),listener,false);
    }

    /**
     * 获取商品已售卖参数
     * @param itemId
     * @param listener
     */
   public void requestGoodSoldRequest(final String itemId,
                                      final RequestListener<GoodItemSold> listener){
       baseRequest(new GetGoodSoldRequest(itemId),listener,false);
   }
//
//    /**
//     * 获取画报型型商品信息
//     * @param context
//     * @param tms
//     *            tms地址
//     * @param listener
//     * @author hanqi
//     * @date 2014-8-13
//     */
//    public void requestTmsHuabaoItems(final Context context, final String tms,
//                                      final RequestListener<List<ImageItem>> listener) {
//        baseRequest(new GetTmsHuabaoItemsRequest(tms), listener, false);
//    }
//
//
//    /**
//     * [天天特价、淘宝清仓]从tms获取天天特价、淘宝清仓分会场商品规则
//     * @param context
//     * @param url
//     * @param listener
//     */
//    public void requestTmsTeJiaItemRule(final Context context, final String url,
//                                        final RequestListener<TeJiaRule> listener) {
//
//        baseRequest(new GetTmsTejiaItemRuleRequest(url), listener, false);
//    }
//
//    /**
//     * [天天特价]获取 抽取十元包邮商品
//     * 特价的一个特殊频道，用来抽取十元包邮商品，ruleType必填。
//     * @param context
//     * @param pageSize
//     * @param pageNum
//     * @param listener
//     */
//    public void requestGetTejia(Context context, String ruleType, Integer pageSize, Integer pageNum,
//                                final RequestListener<TejiaResult> listener) {
//
//        requestGetTejia(context, null, null, null, null, pageSize, pageNum, false, false, ruleType, listener);
//    }
//
//    /**
//     * [天天特价]获取 抽取类目活动商品
//     * @param context
//     * @param pageSize
//     * @param pageNum
//     * @param listener
//     */
//    public void requestGetTejia(Context context, Integer actId, Integer pageSize, Integer pageNum,
//                                final RequestListener<TejiaResult> listener) {
//
//        requestGetTejia(context, null, null, actId, null, pageSize, pageNum, false, false, null, listener);
//    }
//
//    /**
//     * [天天特价]请求获取天天特价的商品
//     * （如有特殊场景可以用多态，比如：抽取主题活动商品
//     * public void requestGetTejia(final Context context, final Integer actId,
//     * final Integer pageSize, final Integer pageNum, final
//     * BusinessRequestListener listener) {
//     * requestGetTejia(context, null, null, actId, null, pageSize, pageNum,
//     * null, null, listener);
//     * }）
//     * @param context
//     * @param actIds
//     *            要抽取的商品所属的特价活动（类目或主题）的ID，以逗号分隔，如5,7,2
//     * @param cids
//     *            要抽取的商品所属的淘宝类目的ID，以逗号分隔，如50010850,50000852
//     * @param actId
//     *            要抽取商品所属的单个特价活动（类目或主题）的ID
//     * @param actTime
//     *            抽取哪一天的商品，如20130808
//     * @param pageSize
//     *            每个promotionId返回的商品个数，取值区间为[1,300]。注意：
//     *            实际获取的商品个数可能会少于你指定的pageSize。
//     * @param pageNum
//     *            当前页码，必须为正数
//     * @param isPre
//     *            是否返回参加的活动尚未开始的商品
//     * @param isEnd
//     *            是否返回参加的活动已经结束的商品
//     * @param listener
//     *            结果回调函数
//     */
//    public void requestGetTejia(final Context context, final String actIds, final String cids, final Integer actId,
//                                final String actTime, final Integer pageSize, final Integer pageNum, final Boolean isPre,
//                                final Boolean isEnd, final String ruleType, final RequestListener<TejiaResult> listener) {
//
//        baseRequest(new GetTianTianTeJiaItemRequest(actIds, cids, actId, actTime, pageSize, pageNum, isPre, isEnd,
//                ruleType), listener, false);
//    }

//    /**
//     * [淘宝清仓]通过活动ID获取淘宝清仓商品
//     * @param context
//     * @param actIds
//     * @param page
//     * @param pageSize
//     * @param listener
//     */
//    public void requestGetTaoBaoQingCangItem(Context context, List<Long> actIds, Integer page, Integer pageSize,
//                                             final RequestListener<PaginationQingCangItem> listener) {
//        requestGetTaoBaoQingCangItem(context, page, pageSize, null, actIds, null, null, listener);
//    }

//    /**
//     * [淘宝清仓]通过类目ID获取淘宝清仓商品
//     * @param context
//     * @param page
//     * @param pageSize
//     * @param cateIds
//     * @param listener
//     */
//    public void requestGetTaoBaoQingCangItem(Context context, Integer page, Integer pageSize, List<Long> cateIds,
//                                             final RequestListener<PaginationQingCangItem> listener) {
//        requestGetTaoBaoQingCangItem(context, page, pageSize, cateIds, null, null, null, listener);
//    }
//
//    /**
//     * [淘宝清仓]获取淘宝清仓商品
//     * @param context
//     * @param page
//     * @param pageSize
//     * @param cateIds 类目ID列表
//     * @param actIds 活动ID列表
//     * @param actDates 日期列表
//     * @param today 是否只加载今天的活动商品
//     * @param listener
//     * @author hanqi
//     */
//    public void requestGetTaoBaoQingCangItem(final Context context, final Integer page, final Integer pageSize,
//                                             final List<Long> cateIds, final List<Long> actIds, final List<String> actDates, final Boolean today,
//                                             final RequestListener<PaginationQingCangItem> listener) {
//
//        baseRequest(new GetTaoBaoQingCangItemRequest(page, pageSize, cateIds, actIds, actDates, today), listener, false);
//    }
}
