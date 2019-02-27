package com.yunos.tvtaobao.biz.request;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.taobao.detail.clientDomain.TBDetailResultVO;
import com.taobao.detail.domain.base.Unit;
import com.taobao.wireless.detail.DetailConfig;
import com.taobao.wireless.detail.api.DetailApiProxy;
import com.taobao.wireless.detail.api.DetailApiRequestor;
import com.yunos.tv.core.AppInitializer;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.RunMode;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tvtaobao.biz.request.base.BaseHttpRequest;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.AddBagBo;
import com.yunos.tvtaobao.biz.request.bo.Address;
import com.yunos.tvtaobao.biz.request.bo.BonusBean;
import com.yunos.tvtaobao.biz.request.bo.BuildOrderRebateBo;
import com.yunos.tvtaobao.biz.request.bo.BuildOrderRequestBo;
import com.yunos.tvtaobao.biz.request.bo.CartStyleBean;
import com.yunos.tvtaobao.biz.request.bo.Cat;
import com.yunos.tvtaobao.biz.request.bo.CollectList;
import com.yunos.tvtaobao.biz.request.bo.CollectionsInfo;
import com.yunos.tvtaobao.biz.request.bo.CouponList;
import com.yunos.tvtaobao.biz.request.bo.CouponRecommendList;
import com.yunos.tvtaobao.biz.request.bo.CreateOrderResult;
import com.yunos.tvtaobao.biz.request.bo.DoPayOrders;
import com.yunos.tvtaobao.biz.request.bo.DynamicRecommend;
import com.yunos.tvtaobao.biz.request.bo.FavModule;
import com.yunos.tvtaobao.biz.request.bo.FeiZhuBean;
import com.yunos.tvtaobao.biz.request.bo.FindSameContainerBean;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.bo.GoodsList;
import com.yunos.tvtaobao.biz.request.bo.GoodsSearchMO;
import com.yunos.tvtaobao.biz.request.bo.GoodsSearchResult;
import com.yunos.tvtaobao.biz.request.bo.GoodsSearchResultDo;
import com.yunos.tvtaobao.biz.request.bo.GoodsSearchZtcResult;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeGoodsBean;
import com.yunos.tvtaobao.biz.request.bo.JhsItemDetail;
import com.yunos.tvtaobao.biz.request.bo.JoinGroupResult;
import com.yunos.tvtaobao.biz.request.bo.LiveBonusResult;
import com.yunos.tvtaobao.biz.request.bo.LiveBonusTimeResult;
import com.yunos.tvtaobao.biz.request.bo.LiveFollowResult;
import com.yunos.tvtaobao.biz.request.bo.LiveIsFollowStatus;
import com.yunos.tvtaobao.biz.request.bo.LoadingBo;
import com.yunos.tvtaobao.biz.request.bo.MyAlipayHongbaoList;
import com.yunos.tvtaobao.biz.request.bo.MyCouponsList;
import com.yunos.tvtaobao.biz.request.bo.OrderDetailMO;
import com.yunos.tvtaobao.biz.request.bo.OrderListData;
import com.yunos.tvtaobao.biz.request.bo.OrderLogisticMo;
import com.yunos.tvtaobao.biz.request.bo.PaginationItemRates;
import com.yunos.tvtaobao.biz.request.bo.ProductTagBo;
import com.yunos.tvtaobao.biz.request.bo.PromotionBean;
import com.yunos.tvtaobao.biz.request.bo.QueryBagRequestBo;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.biz.request.bo.RelatedItem;
import com.yunos.tvtaobao.biz.request.bo.SearchResult;
import com.yunos.tvtaobao.biz.request.bo.SellerInfo;
import com.yunos.tvtaobao.biz.request.bo.ShopCoupon;
import com.yunos.tvtaobao.biz.request.bo.ShopDetailData;
import com.yunos.tvtaobao.biz.request.bo.ShopSearchResultBean;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.biz.request.bo.TakeOutBag;
import com.yunos.tvtaobao.biz.request.bo.TakeOutBagAgain;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderCancelData;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderDeliveryData;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderInfoDetails;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderListData;
import com.yunos.tvtaobao.biz.request.bo.TakeVouchers;
import com.yunos.tvtaobao.biz.request.bo.TakeoutApplyCoupon;
import com.yunos.tvtaobao.biz.request.bo.TbItemDetail;
import com.yunos.tvtaobao.biz.request.bo.TopicsEntity;
import com.yunos.tvtaobao.biz.request.bo.TypeWordsRequestMtop;
import com.yunos.tvtaobao.biz.request.bo.ValidateLotteryBean;
import com.yunos.tvtaobao.biz.request.bo.VouchersList;
import com.yunos.tvtaobao.biz.request.bo.VouchersMO;
import com.yunos.tvtaobao.biz.request.bo.VouchersSummary;
import com.yunos.tvtaobao.biz.request.bo.WeitaoFollowBean;
import com.yunos.tvtaobao.biz.request.core.AsyncDataLoader;
import com.yunos.tvtaobao.biz.request.core.AsyncDataLoader.DataLoadCallback;
import com.yunos.tvtaobao.biz.request.core.ServerTimeSynchronizer;
import com.yunos.tvtaobao.biz.request.core.ServiceCode;
import com.yunos.tvtaobao.biz.request.core.ServiceResponse;
import com.yunos.tvtaobao.biz.request.item.*;
import com.yunos.tvtaobao.biz.request.item.alimama.ApplyCoupon;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anetwork.channel.Network;
import anetwork.channel.Request;
import anetwork.channel.Response;
import anetwork.channel.degrade.DegradableNetwork;
import anetwork.channel.entity.RequestImpl;
import anetwork.channel.statist.StatisticData;
import mtopsdk.mtop.domain.MethodEnum;
import mtopsdk.mtop.domain.MtopResponse;
import mtopsdk.mtop.intf.MtopBuilder;

public class BusinessRequest {

    protected String TAG = getClass().getSimpleName();
    public static final Long RECOMMEND_CATEGORY_ID = 0L;
    private static BusinessRequest mBusinessRequest;

    public static BusinessRequest getBusinessRequest() {
        if (mBusinessRequest == null) {
            mBusinessRequest = new BusinessRequest();
        }
        return mBusinessRequest;
    }

    /**
     * 销毁
     */
    public void destory() {
        mBusinessRequest = null;
    }

    /**
     * 通用的接口请求方法
     *
     * @param requestLoadListener 接口请求处理监听
     * @param listener            接口返回处理监听
     * @param needLogin           是否需要登录
     */
    public <T> void baseRequest(final RequestLoadListener<T> requestLoadListener, final RequestListener<T> listener,
                                final boolean needLogin) {
        if (needLogin) {
            AsyncDataLoader.execute(new DataLoadCallback<ServiceResponse<T>>() {

                @Override
                public ServiceResponse<T> load() {
                    ServiceResponse<T> response = requestLoadListener.load();
                    return response;
                }

                @Override
                public void postExecute(ServiceResponse<T> result) {
                    normalPostExecute(result, listener);
                }

                @Override
                public void onStartLogin() {
                    if (listener != null && listener instanceof RequestListener.RequestListenerWithLogin)
                        ((RequestListener.RequestListenerWithLogin) listener).onStartLogin();
                }

                @Override
                public void preExecute() {
                }
            });
        } else {
            AsyncDataLoader.executeWithNoAutoLogin(new DataLoadCallback<ServiceResponse<T>>() {

                @Override
                public ServiceResponse<T> load() {
                    ServiceResponse<T> response = requestLoadListener.load();
                    return response;
                }

                @Override
                public void postExecute(ServiceResponse<T> result) {
                    normalPostExecute(result, listener);
                }

                @Override
                public void onStartLogin() {
                    if (listener != null && listener instanceof RequestListener.RequestListenerWithLogin)
                        ((RequestListener.RequestListenerWithLogin) listener).onStartLogin();
                }

                @Override
                public void preExecute() {
                }
            });
        }
    }

    /**
     * http请求
     *
     * @param request
     * @param listener
     * @param needLogin
     */
    public <T> void baseRequest(final BaseHttpRequest request, final RequestListener<T> listener,
                                final boolean needLogin) {
        baseRequest(request, listener, needLogin, false);
    }

    /**
     * Http请求
     *
     * @param request
     * @param listener
     * @param needLogin
     * @param post      是否post请求,目前post不支持
     */
    public <T> void baseRequest(final BaseHttpRequest request, final RequestListener<T> listener,
                                final boolean needLogin, final boolean post) {
        if (request == null) {
            return;
        }

        if (needLogin) {
            AsyncDataLoader.execute(new DataLoadCallback<ServiceResponse<T>>() {

                @Override
                public ServiceResponse<T> load() {
                    ServiceResponse<T> response = processHttpRequest(request, post);
                    return response;
                }

                @Override
                public void postExecute(ServiceResponse<T> result) {
                    normalPostExecute(result, listener);
                }

                @Override
                public void onStartLogin() {
                    if (listener != null && listener instanceof RequestListener.RequestListenerWithLogin)
                        ((RequestListener.RequestListenerWithLogin) listener).onStartLogin();
                }

                @Override
                public void preExecute() {
                }
            });

        } else {
            AsyncDataLoader.executeWithNoAutoLogin(new DataLoadCallback<ServiceResponse<T>>() {

                @Override
                public ServiceResponse<T> load() {
                    ServiceResponse<T> response = processHttpRequest(request, post);
                    return response;
                }

                @Override
                public void postExecute(ServiceResponse<T> result) {
                    normalPostExecute(result, listener);
                }

                @Override
                public void onStartLogin() {
                    if (listener != null && listener instanceof RequestListener.RequestListenerWithLogin)
                        ((RequestListener.RequestListenerWithLogin) listener).onStartLogin();
                }

                @Override
                public void preExecute() {
                }
            });
        }
    }

    /**
     * MTop数据请求
     *
     * @param request
     * @param listener
     * @param needLogin
     */
    public <T> void baseRequest(final BaseMtopRequest request, final RequestListener<T> listener,
                                final boolean needLogin) {
        baseRequest(request, listener, needLogin, false, 0);
    }

    /**
     * MTop数据请求超时处理
     *
     * @param request
     * @param listener
     * @param needLogin
     */
    public <T> void baseRequest(final BaseMtopRequest request, final RequestListener<T> listener,
                                final boolean needLogin, final int timeout) {
        baseRequest(request, listener, needLogin, false, timeout);
    }

    public <T> void baseRequest(final BaseMtopRequest request, final RequestListener<T> listener,
                                final boolean needLogin, final boolean post) {
        baseRequest(request, listener, needLogin, post, 0);
    }

    /**
     * Mtop数据请求
     *
     * @param request
     * @param listener
     * @param needLogin
     * @param post
     */
    public <T> void baseRequest(final BaseMtopRequest request, final RequestListener<T> listener,
                                final boolean needLogin, final boolean post, final int timeout) {
        if (request == null) {
            return;
        }
        if (needLogin) {
            AsyncDataLoader.execute(new DataLoadCallback<ServiceResponse<T>>() {

                @Override
                public ServiceResponse<T> load() {
                    ServiceResponse<T> response = normalLoad(request, post, timeout);
                    return response;
                }

                @Override
                public void postExecute(ServiceResponse<T> result) {
                    normalPostExecute(result, listener);
                }

                @Override
                public void onStartLogin() {
                    if (listener != null && listener instanceof RequestListener.RequestListenerWithLogin)
                        ((RequestListener.RequestListenerWithLogin) listener).onStartLogin();
                }

                @Override
                public void preExecute() {
                }
            });

        } else {
            AsyncDataLoader.executeWithNoAutoLogin(new DataLoadCallback<ServiceResponse<T>>() {

                @Override
                public ServiceResponse<T> load() {
                    ServiceResponse<T> response = normalLoad(request, post, timeout);
                    return response;
                }

                @Override
                public void postExecute(ServiceResponse<T> result) {
                    normalPostExecute(result, listener);
                }

                @Override
                public void onStartLogin() {
                    if (listener != null && listener instanceof RequestListener.RequestListenerWithLogin)
                        ((RequestListener.RequestListenerWithLogin) listener).onStartLogin();
                }

                @Override
                public void preExecute() {
                }
            });
        }
    }

    /**
     * Mtop同步数据请求，目前只支持不需要登录的请求，调用的位置需在线程中
     *
     * @param request
     * @param listener
     * @param post
     */
    public <T> void baseSyncRequest(final BaseMtopRequest request, final RequestListener<T> listener, final boolean post) {
        if (request == null) {
            return;
        }

        ServiceResponse<T> response = normalLoad(request, post, 0);
        normalPostExecute(response, listener);
    }


    /** -------------------------------------------    语音功能请求部分   ------------------------------------------------*/

    /**
     * 语音动态语点注册
     * @param referrer
     * @param className
     * @param type
     * @param params
     */
    public void requestVoiceRegister(String referrer, String className, String type, String params, RequestListener<JSONObject> listener) {
        baseRequest(new VoiceRegisterRequest(referrer, className, type, params), listener, false, true);
    }

    /** -------------------------------------------    电淘业务请求部分   ------------------------------------------------*/
    /**
     * 主搜索宝贝
     *
     * @param pageSize
     * @param curPage
     * @param kw
     * @param sort
     * @param listener
     */
    public void requestSearch(Integer pageSize, Integer curPage, String kw, String sort, String nid, String tab,
                              final RequestListener<GoodsSearchMO> listener) {
        requestSearch(pageSize, curPage, kw, sort, nid, null, tab, listener);
    }

    /**
     * 主搜索宝贝
     *
     * @param pageSize 当前要获取内容的大小
     * @param curPage  当前第几页
     * @param kw       宝贝的关键字
     * @param sort     宝贝的排序
     * @param nid      宝贝的NID
     * @param catmap   宝贝所属的类目
     * @param listener
     */
    public void requestSearch(Integer pageSize, Integer curPage, String kw, String sort, String nid, String catmap,
                              String tab, final RequestListener<GoodsSearchMO> listener) {
        final SearchRequest request = new SearchRequest();
        request.setCurPage(curPage);
        request.setKw(kw);
        request.setPageSize(pageSize);
        request.setSort(sort);
        request.setNid(nid);
        request.setCatmap(catmap);
        request.setTab(tab);
        baseRequest(request, listener, false);
    }


    /**
     * 获取loading图片数据
     *
     * @param listener
     */
    public void getAdvertsRequest(final RequestListener<List<LoadingBo>> listener) {
        baseRequest(new GetAdvertsRequest(), listener, false);
    }

    //    /**
    //     * @param kw 查询关键字
    //     * @param page_size 每页商品数
    //     * @param page_no 页码
    //     * @param sort 排序方式
    //     * @param cat 前台类目
    //     * @param category 后台类目
    //     */
    //    public void requestSearchTmail(String kw, Integer page_size, Integer page_no, String sort, String cat,
    //            String category, RequestListener<GoodsSearchTmail> listener) {
    //        requestSearchTmail(kw, page_size, page_no, sort, cat, category, null, null, null, null, null, "tvtaobao", null,
    //                listener);
    //    }
    //
    //    /**
    //     * 搜索天猫商品接口
    //     * @param kw 查询关键字
    //     * @param page_size 每页商品数
    //     * @param page_no 页码
    //     * @param sort 排序方式
    //     * @param cat 前台类目
    //     * @param category 后台类目
    //     * @param prop 属性，格式: pid:vid;pid:vid
    //     * @param start_price 开始价格（单位：元）
    //     * @param end_price 结束价格（单位：元）
    //     * @param item_ids 查询指定的商品，商品id之间使用逗号（,）分割。搜索结果只会返回指定的商品
    //     * @param user_id 卖家数字ID
    //     * @param from 调用方名称，必选
    //     * @param auction_tag 商品标签
    //     */
    //    public void requestSearchTmail(String kw, Integer page_size, Integer page_no, String sort, String cat,
    //            String category, String prop, String start_price, String end_price, String item_ids, String user_id,
    //            String from, String auction_tag, RequestListener<GoodsSearchTmail> listener) {
    //        final SearchRequestTmail searchRequest = new SearchRequestTmail();
    //        searchRequest.setQueryKW(kw);
    //        searchRequest.setPageSize(page_size);
    //        searchRequest.setPageNo(page_no);
    //        searchRequest.setSort(sort);
    //        searchRequest.setCat(cat);
    //        searchRequest.setCategory(category);
    //        searchRequest.setProp(prop);
    //        searchRequest.setStartPrice(start_price);
    //        searchRequest.setEndPrice(end_price);
    //        searchRequest.setItemIds(item_ids);
    //        searchRequest.setUserId(user_id);
    //        searchRequest.setFrom(from);
    //        searchRequest.setAuctionTag(auction_tag);
    //        baseRequest(searchRequest, listener, false, false);
    //    }

    /***
     * 请求 “猜你喜欢”
     *
     * @param userId   用户ID
     * @param listener
     */
    public void requestDetainMent(String userId, final RequestListener<String> listener) {
        final DetainMentRequest request = new DetainMentRequest(userId);
        baseRequest(request, listener, false);
    }


    /**
     * 获取语义分析结果
     *
     * @param orgin 输入源
     */
    public void requestTypeWords(String orgin, final RequestListener<String> listener) {
        final TypeWordsRequest request = new TypeWordsRequest(orgin);
        baseRequest(request, listener, false);
    }

    /**
     * 获取后台跳转配置
     */
    public void requestJumpUrl(final RequestListener<String> listener) {
        final JumpUrlRequest request = new JumpUrlRequest();
        baseRequest(request, listener, false);
    }

    /**
     * 获取活动打标
     */
    public void requestHasActivite(final RequestListener<String> listener) {
        final HasActiviteRequest request = new HasActiviteRequest();
        baseRequest(request, listener, false);
    }

    /**
     * 获取点播视频列表--2017.4.20 暂用
     *
     * @param listener
     */
    public void requestVideoJson(final RequestListener<String> listener) {
        final VideoJsonRequest request = new VideoJsonRequest();
        baseRequest(request, listener, false);

    }


    /**
     * 获取电视淘宝全局配置接口
     *
     * @param listener
     */
    public void requestGlobalConfig(final RequestListener<GlobalConfig> listener) {
        final GlobalConfigRequest request = new GlobalConfigRequest();
        baseRequest(request, listener, false);
    }

    /**
     * 从春炎处得到红包接口
     *
     * @param listener
     */
    public void requestBounsByMTop(RequestListener<String> listener) {
        baseRequest(new MTopBounsRequest(), listener, false);
    }

    /**
     * 创建电视淘宝订单成功后,向服务器发送订单记录接口
     *
     * @param itemNumId 商品id
     * @param orderId   订单id
     * @param cartFlag  入口方式
     * @param channel   电视淘宝渠道
     * @param listener
     */
    public void requestQueryCreateTvTaoOrderRequest(String itemNumId, String orderId, String cartFlag, String channel, String versionName, String extParams, final RequestListener<String> listener) {
        final QueryCreateTvTaoOrderRequest request = new QueryCreateTvTaoOrderRequest(itemNumId, orderId, cartFlag, channel, versionName, extParams);
        baseRequest(request, listener, false);
    }

    /**
     * 搜索天猫商品接口
     *
     * @param page_size 每页商品数
     * @param page_no   页码
     * @param sort      排序方式
     * @param cat       前台类目
     * @param category  后台类目
     */
    public void requestSearchMtop(String q, Integer page_size, Integer page_no, String sort, String cat,
                                  String category, RequestListener<GoodsSearchResult> listener) {
        requestSearchMtop(q, page_size, page_no, sort, cat, category, null, null, null, null, null, "tvtaobao", null,
                listener);
    }

    /**
     * 搜索天猫商品接口
     *
     * @param page_size   每页商品数
     * @param page_no     页码
     * @param sort        排序方式
     * @param cat         前台类目
     * @param category    后台类目
     * @param prop        属性，格式: pid:vid;pid:vid
     * @param start_price 开始价格（单位：元）
     * @param end_price   结束价格（单位：元）
     * @param item_ids    查询指定的商品，商品id之间使用逗号（,）分割。搜索结果只会返回指定的商品
     * @param user_id     卖家数字ID
     * @param from        调用方名称，必选
     * @param auction_tag 商品标签
     */
    public void requestSearchMtop(String q, Integer page_size, Integer page_no, String sort, String cat,
                                  String category, String prop, String start_price, String end_price, String item_ids, String user_id,
                                  String from, String auction_tag, RequestListener<GoodsSearchResult> listener) {
        final SearchRequestMtop searchRequest = new SearchRequestMtop();
        searchRequest.setQueryKW(q);
        searchRequest.setPageSize(page_size);
        searchRequest.setPageNo(page_no);
        searchRequest.setSort(sort);
        searchRequest.setCat(cat);
        searchRequest.setCategory(category);
        searchRequest.setProp(prop);
        searchRequest.setStartPrice(start_price);
        searchRequest.setEndPrice(end_price);
        searchRequest.setItemIds(item_ids);
        searchRequest.setUserId(user_id);
        searchRequest.setFrom(from);
        searchRequest.setAuctionTag(auction_tag);
        baseRequest(searchRequest, listener, false);
    }

    public void requestSearchMtopZTC(String q, Integer page_size, Integer page_no, int per, String sort, String cat,
                                     String category, String item_ids, String ip, String flag, int ztcC, RequestListener<GoodsSearchZtcResult> listener) {
        SearchRequestMtopZtc searchRequest = new SearchRequestMtopZtc();
        searchRequest.setQueryKW(q);
        searchRequest.setPageSize(page_size);
        searchRequest.setPageNo(page_no);
        searchRequest.setFlag(flag);
        searchRequest.setPer(per);
        searchRequest.setIp(ip);
        searchRequest.setZtcc(ztcC);
        searchRequest.setCateId(cat);
        searchRequest.setSort(sort);
//        searchRequest.setCat(cat);
//        searchRequest.setCategory(category);
        searchRequest.setItemIds(item_ids);
        baseRequest(searchRequest, listener, false);
    }


    public void requestSearchResult(String q, Integer page_size, Integer page_no, int per, String sort, String cat, String flag,
                                     List<String> list ,boolean isFromCartToBuildOrder,boolean isFromBuildOrder,boolean isTmail,RequestListener<GoodsSearchResultDo> listener) {
        SearchResultRequest searchRequest = new SearchResultRequest(list ,isFromCartToBuildOrder,isFromBuildOrder,isTmail);
        searchRequest.setQueryKW(q);
        searchRequest.setPageSize(page_size);
        searchRequest.setPageNo(page_no);
        searchRequest.setFlag(flag);
        searchRequest.setPer(per);
        searchRequest.setCateId(cat);
        searchRequest.setSort(sort);
        baseRequest(searchRequest, listener, false);
    }

    /**
     * 取得服务的更新时间(异步请求)
     *
     * @param listener
     */
    public void requestUpdatServerTime(final RequestListener<Long> listener) {
        // 因为是自动更新所以无网络的情况下不做处理
        if (NetWorkUtil.isNetWorkAvailable() && !ServerTimeSynchronizer.isServerTime()) {
            baseRequest(new GetServerTimeRequest(), listener, false);
        }
    }

    /**
     * 取得服务的更新时间(同步请求)
     *
     * @param listener
     */
    public void requestSyncUpdatServerTime(final RequestListener<Long> listener) {
        // 因为是自动更新所以无网络的情况下不做处理
        if (NetWorkUtil.isNetWorkAvailable() && !ServerTimeSynchronizer.isServerTime()) {
            baseSyncRequest(new GetServerTimeRequest(), listener, false);
        }
    }

    /**
     * 取得淘宝的商品详请V6
     *
     * @param itemNumId
     * @param extParam
     * @param
     * @param listener
     */
    public void requestGetItemDetailV6(final String itemNumId, final String extParam,
                                       final RequestListener<TBDetailResultV6> listener) {
        Log.i("wordlbin", "requestGetItemDetailV6: itemid= " + itemNumId + " extparam= " + extParam);
        //TODO双11之后需要重新修正
        baseRequest(new GetItemDetailV6Request(itemNumId, extParam), listener, false);
    }


    /**
     * 取得淘宝的商品详请V6，获取双十一购物津贴用淘宝的ttid
     *
     * @param itemNumId
     * @param areaId
     * @param
     * @param listener
     */
    public void requestGetItemDetailV6New(final String itemNumId, final String areaId,
                                          final RequestListener<TBDetailResultV6> listener) {
        Log.i("wordlbin", "requestGetItemDetailV6: itemid= " + itemNumId + " areaId= " + areaId);
        //TODO双11之后需要重新修正
//        baseRequest(new GetItemDetailV6RequestNew(itemNumId, extParam), listener, false);
        baseRequest(new TBDetailV6HttpRequest(itemNumId, areaId), listener, false);

    }


    public void requestGetFeiZhuItemDetail(final String itemid, final RequestListener<FeiZhuBean> listener) {
        baseRequest(new FeiZhuItemDetailRequest(itemid), listener, false);

    }

    /**
     * 取得淘宝的商品详请V5
     *
     * @param itemId
     * @param listener
     */
    public void requestGetItemDetailV5(final String itemId, final String extParam,
                                       final RequestListener<TBDetailResultVO> listener) {
        baseRequest(new RequestLoadListener<TBDetailResultVO>() {

            @Override
            public ServiceResponse<TBDetailResultVO> load() {
                final Map<String, String> params = new HashMap<String, String>();
                params.put("id", itemId);
                DetailConfig.ttid = Config.getTTid();
                if (Config.getRunMode() == RunMode.DAILY) {
                    DetailConfig.env = DetailConfig.WAPTEST;
                }
                GetItemDetailV5Request detailRequest = new GetItemDetailV5Request(itemId, extParam);
                Network network = new DegradableNetwork(CoreApplication.getApplication());
                String url = detailRequest.getUrl();
//                Log.i("worldbin", "v5_url="+url);
                //https://hws.alicdn.com/cache/wdetail/5.0/?ttid=142857%40tvtaobao_android_4.7.03&id=4547215160
                Request request = new RequestImpl(url);
                Response response = network.syncSend(request, null);

                //获取当前的statusCode;statusCode<0表示网络请求异常
                int statusCode = response.getStatusCode();
                //获取当前的Response的head
                Map<String, List<String>> head = response.getConnHeadFields();
                //获取当前请求body数据
                byte[] data = response.getBytedata();
                //获取当前请求网络请求数据
                StatisticData staticData = response.getStatisticData();

                ServiceResponse<TBDetailResultVO> resolveResponse = null;
                if (data != null) {
                    try {
                        resolveResponse = detailRequest.resolveResponse(new String(data));
                    } catch (Exception e) {
                        resolveResponse = new ServiceResponse<TBDetailResultVO>();
                        resolveResponse.update(ServiceCode.DATA_PARSE_ERROR);
                        e.printStackTrace();
                    }
                } else {
                    resolveResponse = new ServiceResponse<TBDetailResultVO>();
                    resolveResponse.update(ServiceCode.HTTP_ERROR);
                }

                AppDebug.v(TAG, TAG + ".requestGetItemDetailV5.url = " + url);
                AppDebug.v(TAG, TAG + ".requestGetItemDetailV5.response = " + response);
                AppDebug.v(TAG, TAG + ".requestGetItemDetailV5.statusCode = " + statusCode);
                AppDebug.v(TAG, TAG + ".requestGetItemDetailV5.head = " + head);
                AppDebug.v(TAG, TAG + ".requestGetItemDetailV5.data = " + data);
                AppDebug.v(TAG, TAG + ".requestGetItemDetailV5.staticData = " + staticData);
                AppDebug.v(TAG, TAG + ".requestGetItemDetailV5.resolveResponse = " + resolveResponse);

                final ServiceResponse<TBDetailResultVO> serviceResponse = resolveResponse;

                try {
                    TBDetailResultVO detailvo = DetailApiProxy.synRequest(params, new DetailApiRequestor() {

                        public TBDetailResultVO syncRequest(Unit apiUnit) {
                            return serviceResponse.getData();
                        }
                    });
                    serviceResponse.setData(detailvo);
                } catch (Exception e) {
                    e.printStackTrace();
                    serviceResponse.setData(null);
                }
                return serviceResponse;
            }

        }, listener, false);
    }

    /**
     * 取得淘宝的商品详请
     *
     * @param itemId
     * @param listener
     */
    public void requestGetItemDetail(final Long itemId, final RequestListener<TbItemDetail> listener) {
        baseRequest(new GetTbItemDetailRequest(itemId), listener, false);
    }

    /**
     * 获取聚划算商品详情
     *
     * @param juId
     * @param listener
     */
    public void requestGetJhsItemDetail(final Long juId, final RequestListener<JhsItemDetail> listener) {
        baseRequest(new GetJhsItemDetailRequest(juId), listener, false);
    }

    /**
     * 取得地址列表（需要登录）
     *
     * @param listener
     */
    public void requestGetAddressList(final RequestListener<List<Address>> listener) {
        baseRequest(new GetAddressListRequest(), listener, true);
    }

    /**
     * 设为默认地址（需要登录）
     *
     * @param deliverId 收货地址id
     * @param listener
     */
    public void requestSetDefaultAddress(final String deliverId, final RequestListener<String> listener) {
        baseRequest(new SetDefaultAddressRequest(deliverId), listener, true);
    }

    /**
     * 参团
     *
     * @param itemId
     * @param listener
     */
    public void requestJoinGroup(final String itemId, final RequestListener<JoinGroupResult> listener) {
        baseRequest(new JoinGroupRequest(itemId), listener, true);
    }

    /**
     * 通过进入详情页路径，获取商品标签
     *
     * @param itemId
     * @param list
     * @param listener
     */
    public void requestProductTag(String itemId, List<String> list, boolean isZTC, String source, boolean isPre, String amount, Context context, RequestListener<ProductTagBo> listener) {

        try {
            JSONObject object = new JSONObject();
            object.put("umToken", Config.getUmtoken(context));
            object.put("wua", Config.getWua(context));
            object.put("isSimulator", Config.isSimulator(context));
            object.put("userAgent", Config.getAndroidSystem(context));
            String extParams = object.toString();
            baseRequest(new GetProductTagRequest(itemId, list, isZTC, source, isPre, amount, extParams), listener, false, true);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


//    /**
//     * 获取返利接口
//     * @param itemIdArray
//     * @param list
//     * @param listener
//     */
//    public void requestRebateMoney( String itemIdArray,List<String> list, RequestListener<List<RebateBo>> listener) {
//        baseRequest(new GetRebateMoneyRequest(itemIdArray, list,false), listener, false, true);
//    }


    /**
     * 获取返利接口
     *
     * @param itemIdArray
     * @param list
     * @param listener
     */
    public void requestRebateMoney(String itemIdArray, List<String> list, boolean isFromCartToBuildOrder, boolean isFromBuildOrder, boolean mjf, String extParams
            , RequestListener<List<RebateBo>> listener) {
        baseRequest(new GetRebateMoneyRequest(itemIdArray, list, isFromCartToBuildOrder, isFromBuildOrder, mjf, extParams), listener, false, true);
    }

    /**
     * 获取下单页返利接口
     *
     * @param infoArray
     * @param listener
     */
    public void requestBuildOrderRebateMoney(String infoArray, String extParams, RequestListener<List<BuildOrderRebateBo>> listener) {
        baseRequest(new GetBuildOrderRebateMoneyRequest(infoArray, extParams), listener, false, true);
    }

    /**
     * 获取买就返标签接口
     *
     * @param itemIdArray
     * @param listener
     */
    public void requestBuyToCashback(String itemIdArray, RequestListener<Map<String, String>> listener) {
        baseRequest(new GetBuyToCashbackRequest(itemIdArray), listener, false, true);
    }

    /**
     * 共建下单,预下单接口
     *
     * @param request
     * @param listener
     */
    public void buildOrder(final BuildOrderRequestBo request, boolean hasAddCart, final RequestListener<String> listener) {
        baseRequest(new BuildOrderRequest(request, hasAddCart), listener, true, true);
    }

    /**
     * 预下单元素调整接口
     *
     * @param params
     * @param listener
     */
    public void adjustBuildOrder(final String params, final RequestListener<String> listener) {
        baseRequest(new AdjustBuildOrderRequest(params), listener, true, true);
    }

    /**
     * 下单接口
     *
     * @param params
     * @param listener
     */
    public void createOrder(final String params, final RequestListener<CreateOrderResult> listener) {
        baseRequest(new CreateOrderRequestV3(params), listener, true, true);
    }

    /**
     * 获取订单详情
     *
     * @param orderId
     * @param listener
     */
    public void requestOrderDetail(final Long orderId, final RequestListener<OrderDetailMO> listener) {
        baseRequest(new GetOrderDetailRequest(orderId), listener, true);
    }

    /**
     * 去支付宝付款、确认收货或者朋友代付
     *
     * @param orderId
     * @return
     */
    public void doPay(final Long orderId, final RequestListener<DoPayOrders> listener) {
        baseRequest(new DoPayRequest(orderId), listener, true);
    }

    /**
     * 获取物流信息
     *
     * @param orderId
     * @param listener
     */
    public void requestOrderLogistic(final Long orderId, final RequestListener<OrderLogisticMo> listener) {
        baseRequest(new GetOrderLogistic(orderId), listener, true);
    }

    /**
     * 获取deviceId
     *
     * @param listener
     */
    public void requestGetDeviceId(final RequestListener<String> listener) {
        baseRequest(new GetDeviceIdRequest(), listener, false);
    }

    /**
     * 检查商品是否已收藏
     *
     * @param itemId
     * @param listener
     */
    public void checkFav(final String itemId, final RequestListener<String> listener) {
        baseRequest(new CheckFavRequest(itemId), listener, true);
    }

    /**
     * 获取宝贝收藏列表
     *
     * @param page
     * @param pageSize
     * @param listener
     */
    public void getCollects(final int page, final int pageSize, final RequestListener<CollectList> listener) {
        baseRequest(new GetCollectsRequest(page, pageSize), listener, true);
    }

    /**
     * 2018.9.10 新版获取宝贝收藏列表
     *
     * @param startTime
     * @param pageSize
     * @param listener
     */
    public void getNewCollects(final String startTime, final int pageSize, final RequestListener<CollectionsInfo> listener) {
        baseRequest(new GetNewCollectsRequest(startTime, pageSize), listener, true);
    }

    /**
     * 2018.9.10 新版获取宝贝收藏列表数量
     *
     * @param listener
     */
    public void getNewCollectsNum(final RequestListener<FavModule> listener) {
        baseRequest(new GetNewCollectsNumRequest(), listener, true);
    }

    /**
     * 管理收藏
     *
     * @param itemId
     * @param listener
     */
    public void manageFav(final String itemId, final String func, final RequestListener<String> listener) {
        baseRequest(new ManageFavRequest(itemId, func), listener, true);
    }

    /**
     * 详情页商品加入收藏
     *
     * @param itemId
     * @param listener
     */

    public void addCollection(final String itemId, final RequestListener<String> listener) {
        baseRequest(new AddCollectionRequest(itemId), listener, true);
    }

    /**
     * 详情页商品加入收藏
     *
     * @param itemId
     * @param listener
     */

    public void cancelCollection(final String itemId, final RequestListener<String> listener) {
        baseRequest(new CancelCollectionRequest(itemId), listener, true);
    }


    /**
     * 取得海尔电视 淘宝的商品优惠券信息
     *
     * @param itemId
     * @param listener
     */
    public void requestGetItemCouponInfo(final String itemId, final RequestListener<String> listener) {
        baseRequest(new GetTbItemCouponInfoRequest(itemId), listener, false);
    }

    /**
     * 领取海尔电视 淘宝的商品使用红包
     *
     * @param itemId
     * @param listener
     */
    public void requestDoItemCoupon(final String itemId, final RequestListener<String> listener) {
        baseRequest(new GetDoTbItemCouponRequest(itemId), listener, true);
    }

    /**
     * 获取宝贝描述 -PC完整版v4.2
     *
     * @param itemId
     * @param listener
     */
    public void requestGetFullItemDesc(final String itemId, final RequestListener<String> listener) {
        baseRequest(new GetFullItemDescRequest(itemId), listener, false);
    }

    /**
     * 宝贝评价
     *
     * @param itemId
     * @param pageNo
     * @param pageSize
     * @param rateType
     * @param listener
     */
    public void requestGetItemRates(final String itemId, final int pageNo, final int pageSize, final String rateType,
                                    final RequestListener<PaginationItemRates> listener) {
        baseRequest(new GetItemRatesRequest(itemId, pageNo, pageSize, rateType), listener, false);
    }

    /**
     * 获取订单列数量
     *
     * @param listener
     */
    public void requestGetOrderCount(final RequestListener<OrderListData> listener) {
        baseRequest(new GetOrderListRequest(), listener, true, true);
    }

    /**
     * 客户端店铺宝贝搜索API
     *
     * @param uid
     * @param shopId
     * @param sort
     * @param newDays
     * @param catId
     * @param q
     * @param startPrice
     * @param endPrice
     * @param pageSize
     * @param currentPage
     * @param listener
     */
    public void getShopItemList(String uid, String shopId, String sort, String newDays, String catId, String q,
                                String startPrice, String endPrice, String pageSize, String currentPage,
                                final RequestListener<GoodsList> listener) {
        final GetShopItemListRequest request = new GetShopItemListRequest(uid, shopId, sort, newDays, catId, q,
                startPrice, endPrice, pageSize, currentPage);
        baseRequest(request, listener, false);
    }

    /**
     * 查询店铺基本信息，包括店铺id，标题，信用，收藏人气，DSR等
     *
     * @param sellerId
     * @param shopId
     * @param listener
     */
    public void getWapShopInfo(String sellerId, String shopId, final RequestListener<SellerInfo> listener) {
        baseRequest(new GetWapShopInfoRequest(sellerId, shopId), listener, false);
    }

    /**
     * 获取店铺内类目（包括二级）
     *
     * @param sellerId
     * @param listener
     */
    public void getCatInfoInShop(String sellerId, String shopId, final RequestListener<List<Cat>> listener) {
        baseRequest(new GetShopCatInfoRequest(sellerId, shopId), listener, false);
    }

    /**
     * 获取优惠券列表
     */
    public void getCouponList(final int page, final int pageSize, final String tag,
                              final RequestListener<CouponList> listener) {
        baseRequest(new GetCouponListRequest(page, pageSize, tag), listener, true, true);
    }

    /**
     * 检查商品积分黑名单
     *
     * @param catergoryId
     * @param itemId
     * @param listener
     */
    public void getTaobaoPointValidateblackfilter(final String catergoryId, final String itemId, final RequestListener<Boolean> listener) {
        baseRequest(new GetTaobaoPointValidateblackfilter(catergoryId, itemId), listener, false);
    }

    public void getstdcats(final String catergoryId, final RequestListener<String> listener) {
        baseRequest(new GetStdCats(catergoryId), listener, false);
    }

    /**
     * 获取我的网店优惠券或生活优惠券
     *
     * @param bizType    必填，默认为2，优惠券类型，0-全部，1-线上，2-生活
     * @param couponType 选填，券类型，多个券之间以_隔开
     */
    public void requestMyCouponsList(String bizType, String couponType, final RequestListener<MyCouponsList> listener) {
        baseRequest(new GetMyCouponsListRequest(bizType, couponType), listener, true);
    }

    /**
     * 获取我的积分
     *
     * @param listener
     */
    public void requestTaobaoPoint(final RequestListener<String> listener) {
        baseRequest(new GetTaobaoPoint(), listener, false);
    }

    /**
     * 获取我的支付宝红包列表
     *
     * @param currentPage 页码，选填
     */
    public void requestMyAlipayHongbaoList(String currentPage, final RequestListener<MyAlipayHongbaoList> listener) {
        baseRequest(new GetMyAlipayHongbaoList(currentPage), listener, true);
    }

    /**
     * 获取商品优惠券推荐商品列表
     *
     * @param sellerId 卖家ID
     * @param couponId 优惠券ID
     */
    public void requestCouponRecommendList(String sellerId, String couponId,
                                           final RequestListener<CouponRecommendList> listener) {
        baseRequest(new GetCouponRecommendList(sellerId, couponId), listener, true);
    }

    /**
     * 获取淘宝SDK中专题主会场模板数据
     *
     * @param url TMS url
     * @return
     */
    public void topicsTmsRequest(final String url, final RequestListener<TopicsEntity> listener) {
        baseRequest(new TopicsTmsRequest(url), listener, false);
    }

    /**
     * 获取热门词推荐列表
     *
     * @param type     1：电视淘宝 2：天猫超市。默认为1
     * @param listener
     */
    public void getHotWordsList(String type, final RequestListener<ArrayList<String>> listener) {
        baseRequest(new GetHotWordsRequest(type), listener, false);
    }


    /**
     * 获取cps红包
     *
     * @param refpid
     * @param e
     * @param wua
     * @param asac
     * @param listener
     */
    public void getBonusData(String refpid, String e, String wua, String asac, RequestListener<BonusBean> listener) {
        baseRequest(new GetCPSBonus(refpid, e, wua, asac), listener, true);
    }

    /**
     * 获取抽奖条件
     *
     * @param uuid
     * @param listener
     */
    public void getValidateLottery(String uuid, RequestListener<ValidateLotteryBean> listener) {
        baseRequest(new ValidateLotteryRequest(uuid), listener, true);
    }


    /**
     * 2018.8.27获取直播的发放权益的时间
     *
     * @param listener
     */
    public void getLiveBonusTime(RequestListener<LiveBonusTimeResult> listener) {
        baseRequest(new GetLiveBonusTimeRequest(), listener, false);
    }


    /**
     * 2018.8.27获取直播的发放权益的结果
     *
     * @param bizId
     * @param type
     * @param asac
     * @param listener
     */
    public void getLiveBonusResult(String bizId, String type, String asac, RequestListener<LiveBonusResult> listener) {
        baseRequest(new GetLiveBonusResultRequest(bizId, type, asac), listener, true);
    }

    /**
     * 2018.8.28  获取直播关注主播的结果
     *
     * @param followedId
     * @param listener
     */
    public void getLiveFollowResult(String followedId, RequestListener<LiveFollowResult> listener) {
        baseRequest(new GetLiveFollowResultRequest(followedId), listener, true);
    }

    /**
     * 2018.8.28  获取直播取消关注主播的结果
     *
     * @param followedId
     * @param listener
     */
    public void getLiveCancelFollowResult(String followedId, RequestListener<String> listener) {
        baseRequest(new GetLiveCancelFollowResultRequest(followedId), listener, true);
    }

    /**
     * 2018.8.29  切换直播，首次进入获取是否有关注主播的状态
     *
     * @param followedId
     * @param listener
     */
    public void getLiveIsFollowStatus(String followedId, RequestListener<LiveIsFollowStatus> listener) {
        baseRequest(new GetLiveIsFollowStatusRequest(followedId), listener, true);
    }

    /**
     * 通知已经发了多少红包
     *
     * @param amount
     * @param uuid
     */
    public void addLotteryRecord(String amount, String uuid) {
        baseRequest(new AddLotteryRecordRequest(amount, uuid), null, true);
    }

    /**
     * 获取推广商品
     *
     * @param listener
     */
    public void getPromotionForready(final RequestListener<PromotionBean> listener) {
        baseRequest(new GetPromotionForready(), listener, false);
    }

    /**
     * 获取搜索分词
     * mtop.taobao.tvtao.segmentationservice.tagging
     *
     * @param origin
     * @param listener
     */
    public void getTypeWordsMtops(String origin, final RequestListener<String> listener) {
        baseRequest(new TypeWordsRequestMtop(origin), listener, false);
    }

    /**
     * 上传路由MAC和事件
     * mtop.taobao.tvtao.tvtaoshakeservice.uploadrouterinfo
     *
     * @param listener
     */
    public void appearMACAndEvent(String appKey, String o2oShopId, String metaType, String currentMeta, String duration, String routerMAC, String timestamp, String signature, String signVersion, final RequestListener<String> listener) {
        baseRequest(new appearMACAndEventRequest(appKey, o2oShopId, metaType, currentMeta, duration, routerMAC, timestamp, signature, signVersion), listener, false);
    }


    /**
     * 淘宝统一的搜索关键词结果
     *
     * @param q
     * @param area     area=tctv是天猫超市的，area=tmtv是天猫的 area=tbtv是整个淘宝的
     * @param code     utf-8
     * @param n
     * @param listener
     */
    public void requestGetTbSearchResultList(final String q, final String area, final String code, final int n,
                                             final RequestListener<ArrayList<String>> listener) {
        baseRequest(new GetSearchResultRequest(q, area, code, n), listener, false);
    }

    /**
     * 新版手淘联想词接口请求
     */

    public void requestGetSearhRelationRecommend(String key,final RequestListener<ArrayList<String>> listener){
        baseRequest(new GetSearhRelationRecommendRequest(key),listener,false);
    }

    /**
     * 添加商品进购物车
     *
     * @param itemId
     * @param quantity
     * @param skuId
     * @param extParams
     * @param listener
     */
    public void addBag(final String itemId, final int quantity, final String skuId, final String extParams,
                       final RequestListener<ArrayList<SearchResult>> listener) {
        baseRequest(new AddBagRequest(itemId, quantity, skuId, extParams), listener, true, true);
    }

    /**
     * 获取找相似数据Bean
     *
     * @param pageSize    一页扥数据大小
     * @param currentPage 当前页
     * @param catid
     * @param nid
     * @param listener
     */
    public void findSame(int pageSize, int currentPage, String catid, String nid, final RequestListener<FindSameContainerBean> listener) {
        baseRequest(new GetFindSameRequest(pageSize, currentPage, catid, nid), listener, true, true);
    }


    public void getDistanceFromGaode(String org, String dest, final RequestListener<String> listener) {
        baseRequest(new TakeOutGetGaodeDistanceRequest(org, dest), listener, false, true);
    }

    /**
     * 获取外卖的订单列表
     *
     * @param pageNo
     * @param listener
     */
    public void getTakeOutOrderList(int pageNo, final RequestListener<TakeOutOrderListData> listener) {
        baseRequest(new TakeOutGetOrderListRequest(pageNo), listener, true, true);
    }

    public void cancelTakeOutOrder(String mainOrderId, final RequestListener<TakeOutOrderCancelData> listener) {
        baseRequest(new TakeOutCancelOrderRequest(mainOrderId), listener, true, true);
    }

    /**
     * 获取外卖订单的配送骑手信息
     *
     * @param mainOrderId
     * @param listener
     */
    public void getTakeOutOrderDelivery(String mainOrderId, final RequestListener<TakeOutOrderDeliveryData> listener) {
        baseRequest(new TakeOutGetOrderDeliveryRequest(mainOrderId), listener, true, true);
    }

    /**
     * 获取外卖的订单详情
     *
     * @param mainOrderId
     * @param listener
     */
    public void getTakeOutOrderDetail(String mainOrderId, final RequestListener<TakeOutOrderInfoDetails> listener) {
        baseRequest(new TakeOutGetOrderDetailRequest(mainOrderId), listener, true, true);
    }

    /**
     * 获取购物车列表
     *
     * @param queryBagRequestBo
     * @param listener
     */
    public void queryBag(final QueryBagRequestBo queryBagRequestBo, final RequestListener<String> listener) {
        baseRequest(new QueryBagRequest(queryBagRequestBo), listener, true, true);
    }

    /**
     * 获取购物车猜你喜欢列表
     *
     * @param channel
     * @param listener
     */
    public void guessLike(final String channel, final RequestListener<GuessLikeGoodsBean> listener) {
        baseRequest(new GetGuessLikeRequest(channel), listener, false, true);
    }

    /**
     *
     * */
    public void getCartStyle(final RequestListener<CartStyleBean> listener) {
        baseRequest(new GetCartStyleRequest(), listener, false, true);
    }

    /**
     * 获取首页猜你喜欢列表
     *
     * @param channel
     * @param listener
     */
    public void homeGuessLike(final String channel, final RequestListener<GuessLikeGoodsBean> listener) {
        baseRequest(new GetHomeGuessLikeRequest(channel), listener, false, true);
    }

    /**
     * 获取支付结果页猜你喜欢
     *
     * @param listener
     */
    public void realTimeRecommond(final RequestListener<GuessLikeGoodsBean> listener) {
        baseRequest(new GetRealTimeRecommondRequest(), listener, true, true);
    }


    /**
     * 获取支付结果页运营位
     */
    public void getDynamicRecommendData(final RequestListener<DynamicRecommend> listener) {
        baseRequest(new GetDynamicRecommendRequest(), listener, true, true);
    }

    /**
     * 更新购物车
     *
     * @param params
     * @param listener
     */
    public void updateBag(final String params, final String cartFrom, final RequestListener<String> listener) {
        baseRequest(new UpdateBagRequest(params, cartFrom), listener, true, true);
    }

    /**
     * 获取相关推荐商品列表,可获取量子推荐宝贝或卖家设置的全店关联宝贝信息
     *
     * @param itemId
     * @param sellerId
     * @param listener
     */
    public void getWapRelatedItems(String itemId, String sellerId, final RequestListener<List<RelatedItem>> listener) {
        baseRequest(new GetWapRelatedItems(itemId, sellerId), listener, false);
    }

    /**
     * 获取指定店铺下的效优惠券列表
     *
     * @param sellerId
     * @param listener
     */
    public void getShopCoupon(String sellerId, final RequestListener<List<ShopCoupon>> listener) {
        baseRequest(new GetShopCoupon(sellerId), listener, false);
    }

    /**
     * 领取优惠券
     *
     * @param sellerId
     * @param activityId
     * @param listener
     */
    public void applyShopCoupon(String sellerId, String activityId, final RequestListener<JSONObject> listener) {
        baseRequest(new ApplyShopCoupon(sellerId, activityId), listener, true);
    }


    /**
     * 淘客登录打点
     *
     * @param nickName
     * @param listener
     */
    public void requestTaokeLoginAnalysis(String nickName, final RequestListener<JSONObject> listener) {
        baseRequest(new TaokeLoginAnalysisRequest(nickName), listener, false);
    }

    public void requestTaokeLoginAnalysis(String nickName, String bizSource, final RequestListener<JSONObject> listener) {
        baseRequest(new TaokeLoginAnalysisRequest(nickName, bizSource), listener, false);
    }


    /**
     * 淘客商品详情打点
     *
     * @param nickName
     * @param tid
     * @param sourceType
     * @param sellerId
     * @param listener
     */
    public void requestTaokeDetailAnalysis(String stbId, String nickName, String tid, String sourceType, String sellerId, final RequestListener<JSONObject> listener) {
        baseRequest(new TaokeDetailAnalysisRequest(stbId, nickName, tid, sourceType, sellerId), listener, false);
    }

    public void requestTaokeDetailAnalysis(String stbId, String nickName, String tid, String sourceType, String sellerId, String bizSource, final RequestListener<JSONObject> listener) {
        baseRequest(new TaokeDetailAnalysisRequest(stbId, nickName, tid, sourceType, sellerId, bizSource), listener, false);
    }

    /**
     * 淘客聚划算列表打点
     *
     * @param nickName
     * @param listener
     */
    public void requestTaokeJHSListAnalysis(String stbId, String nickName, final RequestListener<JSONObject> listener) {
        baseRequest(new TaokeJHSListAnalysisRequest(stbId, nickName), listener, false);
    }

    /**
     * 淘客聚划算列表打点
     *
     * @param nickName
     * @param listener
     */
    public void requestTaokeJHSListAnalysis(String stbId, String nickName, String bizSource, final RequestListener<JSONObject> listener) {
        baseRequest(new TaokeJHSListAnalysisRequest(stbId, nickName, bizSource), listener, false);
    }

    public void requestAlimamaApplyCoupon(String itemId, String pid, String couponKey, String userId, final RequestListener<String> listener) {
        baseRequest(new ApplyCoupon(itemId, pid, couponKey, userId), listener, false, true);
    }

    /**
     * 升级接口
     */
    public void requestUpGrade(String version, String uuid, String channelId, String code, String versionCode, String versionName, String systemInfo, String umtoken, String modelInfo, String extParams, final RequestListener<String> listener) {
        baseRequest(new UpgradeApp(version, uuid, channelId, code, versionCode, versionName, systemInfo, umtoken, modelInfo, extParams), listener, false, true);
    }

    /**
     * 升级接口
     */
    public void requestNewFeature(String version, String uuid, String channelId, String code, String versionCode, String versionName, String systemInfo, String umtoken, String modelInfo, final RequestListener<String> listener) {
        baseRequest(new UpgradeNewFeature(version, uuid, channelId, code, versionCode, versionName, systemInfo, umtoken, modelInfo), listener, false, true);
    }

    /**
     * 上传升级日志接口
     */
    public void logReceive(String osVersion, String uuid, String channelId, String code, String versionCode, String versionName, String systemInfo, String log, final RequestListener<String> listener) {
        baseRequest(new LogReceive(osVersion, uuid, channelId, code, versionCode, versionName, systemInfo, log), listener, false, true);
    }

    /**
     * 处理数据请求
     *
     * @param request
     * @param post
     * @return
     */
    public <T> ServiceResponse<T> normalLoad(BaseMtopRequest request, boolean post, int timeout) {
        ServiceResponse<T> serviceResponse = null;
        request.setParamsData();
        AppDebug.i(TAG, TAG + ".normalLoad.request = " + request + ", post = " + post);
        MtopBuilder builder = AppInitializer.getMtopInstance().build(request, request.getTTid()).useWua();
        builder.setSocketTimeoutMilliSecond(timeout);
        if (post) {// post方法
            builder.reqMethod(MethodEnum.POST);
        }
        builder.useWua();//临时添加
        MtopResponse mtopResponse = builder.syncRequest();
        if (Config.isDebug()) {
            AppDebug.i(TAG, TAG + ".normalLoad, mtopResponse = " + mtopResponse);
        }

        if (mtopResponse != null) {
            AppDebug.v(
                    TAG,
                    TAG + ".normalLoad.isApiSuccess = " + mtopResponse.isApiSuccess() + ",isNetworkError = "
                            + mtopResponse.isNetworkError() + ", isSessionInvalid = "
                            + mtopResponse.isSessionInvalid() + ", isSystemError = " + mtopResponse.isSystemError()
                            + ", isExpiredRequest = " + mtopResponse.isExpiredRequest() + ", is41XResult = "
                            + mtopResponse.is41XResult() + ", isApiLockedResult = "
                            + mtopResponse.isApiLockedResult() + ", isMtopSdkError = "
                            + mtopResponse.isMtopSdkError());

            AppDebug.i(TAG, TAG + ".normalLoad.getResponseCode = " + mtopResponse.getResponseCode());
            AppDebug.i(TAG, TAG + ".normalLoad.getRetCode = " + mtopResponse.getRetCode());
            AppDebug.i(TAG, TAG + ".normalLoad.getRetMsg = " + mtopResponse.getRetMsg());
            AppDebug.i(TAG, TAG + ".normalLoad.getFullKey = " + mtopResponse.getFullKey());
            AppDebug.i(TAG, TAG + ".normalLoad, getBytedata = " + mtopResponse.getBytedata());
            AppDebug.i(TAG, TAG + ".normalLoad.getApi = " + mtopResponse.getApi());

            try {
                if (mtopResponse.getBytedata() != null) {
                    serviceResponse = request.resolveResponse(new String(mtopResponse.getBytedata()));
                } else {
                    String retCode = mtopResponse.getRetCode();
                    AppDebug.i(TAG, TAG + ".normalLoad.retCode = " + retCode);
                    if (!TextUtils.isEmpty(retCode)) {
                        ServiceCode serviceCode = ServiceCode.valueOf(retCode);
                        if (serviceCode != null) {
                            serviceResponse = new ServiceResponse<T>();
                            serviceResponse.update(serviceCode.getCode(), retCode, serviceCode.getMsg());
                        }
                    }
                }

            } catch (Exception e1) {
                serviceResponse = new ServiceResponse<T>();
                serviceResponse.update(ServiceCode.DATA_PARSE_ERROR);
                e1.printStackTrace();
            }
        } else {
            serviceResponse = new ServiceResponse<T>();
            serviceResponse.update(ServiceCode.HTTP_ERROR);
        }

        // 如果serviceResponse仍然为null
        if (serviceResponse == null) {
            AppDebug.i(TAG, TAG + ".normalLoad.custom serviceResponse");
            serviceResponse = new ServiceResponse<T>();
            if (mtopResponse.getResponseCode() == 419
                    && (mtopResponse.getApi().equals("mtop.trade.buildOrder") || mtopResponse.getApi().equals("mtop.trade.buildOrder"))) {
                serviceResponse.update(ServiceCode.API_ERROR_HTTP);
            } else {
                serviceResponse.update(ServiceCode.API_ERROR);
            }
        }
        AppDebug.i(TAG, TAG + ".normalLoad, serviceResponse = " + serviceResponse);
        if (mtopResponse.getResponseCode() == 419
                && (mtopResponse.getApi().equals("mtop.trade.buildOrder") || mtopResponse.getApi().equals("mtop.trade.createOrder"))) {
            serviceResponse.setStatusCode(419);
        }

        return serviceResponse;
    }


    /**
     * 返回结果的默认处理
     *
     * @param result
     * @param listener
     */
    private <T> void normalPostExecute(ServiceResponse<T> result, final RequestListener<T> listener) {
        if (listener != null) {
            listener.onRequestDone(result.getData(), result.getStatusCode(), result.getErrorMsg());
        }
    }

    /**
     * 处理http请求,post方法暂时不可用，因为接口还不完善，所以先这一样接入，带接口完善后再更新
     *
     * @param theRequest
     * @return
     */
    public <T> ServiceResponse<T> processHttpRequest(BaseHttpRequest theRequest, boolean post) {
        Network network = new DegradableNetwork(CoreApplication.getApplication());
        String url = null;
        if (post) {
            url = theRequest.getPostUrl();
        } else {
            url = theRequest.getUrl();
        }
        //url = theRequest.getUrl();
        AppDebug.v(TAG, TAG + ".processHttpRequest.url = " + url + ", post = " + post);
        Response response = null;
        if (!TextUtils.isEmpty(url)) {
            RequestImpl request = new RequestImpl(url);
            if (theRequest != null && theRequest.getHttpHeader() != null && !theRequest.getHttpHeader().isEmpty()) {
                List<Header> headers = theRequest.getHttpHeader();
                List<anetwork.channel.Header> aHeaders = new ArrayList<anetwork.channel.Header>();
                for (Header header : headers) {
                    aHeaders.add(new HeaderWrapper(header));
                }
                request.setHeaders(aHeaders);
            }
            if (post) {
                request.setMethod("POST");
                //post参数
                request.setParams(theRequest.getPostParametersForRequestImpl());
            }
            try {
                response = network.syncSend(request, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            AppDebug.v(TAG, TAG + ".processHttpRequest.request = " + request);
            AppDebug.v(TAG, TAG + ".processHttpRequest.response = " + response);
        }

        //获取当前请求body数据
        byte[] data = null;
        if (response != null) {
            data = response.getBytedata();
        }
        ServiceResponse<T> resolveResponse = null;
        if (data != null) {
            try {

                BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data),
                        theRequest.getResponseEncode()));

                StringBuilder sb = new StringBuilder();
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    sb.append(inputLine);
                }

                resolveResponse = theRequest.resolveResponse(sb.toString());
                //resolveResponse = theRequest.resolveResponse(new String(data));

            } catch (Exception e) {
                resolveResponse = new ServiceResponse<T>();
                resolveResponse.update(ServiceCode.DATA_PARSE_ERROR);
                e.printStackTrace();
            }
        } else {
            resolveResponse = new ServiceResponse<T>();
            resolveResponse.update(ServiceCode.HTTP_ERROR);
        }

        AppDebug.v(TAG, TAG + ".processHttpRequest.resolveResponse = " + resolveResponse);
        return resolveResponse;
    }


    /**
     * 是否已经关注
     *
     * @param accountType  账号类型 1店铺 2达人
     * @param pubAccountId 被关注公众号的淘宝id
     * @param listener
     */
    public void requestWeitaoFollow(String accountType, String pubAccountId, final RequestListener<WeitaoFollowBean> listener) {
        baseRequest(new WeitaoFollowRequest(accountType, pubAccountId), listener, true);
    }


    /**
     * 取消关注
     *
     * @param pubAccountId 被关注公众号的淘宝id
     * @param originPage   关注来源页面或功能，业务方自定
     * @param originFlag   关注来源标识，业务方自定
     * @param listener
     */

    public void requestWeitaoRemove(String pubAccountId, String originPage, String originFlag, final RequestListener<JSONObject> listener) {
        baseRequest(new WeitaoRemoveRequest(pubAccountId, originPage, originFlag), listener, true);
    }


    /**
     * 加关注
     *
     * @param accountType  账号类型 1店铺 2达人
     * @param pubAccountId 被关注公众号的淘宝id
     * @param originPage
     * @param originFlag
     * @param listener
     */

    public void requestWeitaoAdd(String accountType, String pubAccountId, String originPage, String originFlag, final RequestListener<JSONObject> listener) {
        baseRequest(new WeitaoAddRequest(accountType, pubAccountId, originPage, originFlag), listener, true);
    }

    /**
     * 屏蔽微淘动态
     *
     * @param pubAccountId
     * @param status       是否屏蔽
     * @param listener
     */
    public void requestWeitaoSetDynamic(String pubAccountId, boolean status, final RequestListener<JSONObject> listener) {
        baseRequest(new WeitaoSetDynamic(pubAccountId, status), listener, true);
    }


    /**
     * 获取外卖--店铺页-店铺详情信息
     *
     * @param shopId
     * @param serviceId 暂不登录，后期再改  2017.12.15
     */
    public void requestShopHomeDetail(String shopId, String serviceId, String longitude, String latitude, String extFeature, String pageNo, String genreIds, final RequestListener<ShopDetailData> listener) {
        baseRequest(new ShopDetailDataRequest(shopId, serviceId, longitude, latitude, extFeature, pageNo, genreIds), listener, true);
    }


    //获取搜索结果
    public void requestSearchResult(String shopId, String keyword, String orderType, int pageSize, int pageNo, final RequestListener<ShopSearchResultBean> listener) {
        baseRequest(new ShopSearchRequest(shopId, keyword, orderType, pageSize, pageNo), listener, true);
    }

    /**
     * 外卖加入购物车接口
     *
     * @param itemId
     * @param skuId
     * @param quantity
     * @param exParams
     * @param cartFrom
     * @param listener
     */
    public void requestTakeOutAddBag(String itemId, String skuId, String quantity, String exParams, String cartFrom, RequestListener<AddBagBo> listener) {
        baseRequest(new TakeOutAddBagRequest(itemId, skuId, quantity, exParams, cartFrom), listener, false, true, 0);
    }

    /**
     * 再来一单
     *
     * @param storeId
     * @param orderItems
     * @param listener
     */
    public void requestTakeOutAgain(String storeId, String orderItems, RequestListener<TakeOutBagAgain> listener) {
        baseRequest(new TakeOutAgainRequest(storeId, orderItems), listener, true, false, 0);
    }

//    /**
//     * 清空购物车
//     * @param storeId
//     * @param orderItems
//     * @param listener
//     */
//    public void requestTakeOutClearBag(String storeId,String orderItems, RequestListener<TakeOutBagClear> listener){
//        baseRequest(new TakeOutAgainRequest(storeId,orderItems), listener, true, false, 0);
//    }

    /**
     * 外卖修改 购物车item
     *
     * @param storeId
     * @param latitude
     * @param longitude
     * @param operateType 1 表示修改数量（数量加或者减）  2 表示删除（数量为1时扔执行减操作）  3 表示清空
     * @param paramList
     * @param listener
     */
    public void requestTakeOutUpdate(String storeId, String latitude, String longitude, String operateType, String paramList, RequestListener<TakeOutBag> listener) {
        baseRequest(new TakeOutUpdateBagRequest(storeId, latitude, longitude, operateType, paramList), listener, true, false, 0);
    }

    /**
     * 外卖获取购物车列表
     *
     * @param storeId
     * @param longitude
     * @param latitude
     * @param extFeature
     * @param listener
     */
    public void getTakeOutBag(String storeId, String longitude, String latitude, String extFeature, RequestListener<TakeOutBag> listener) {
        baseRequest(new TakeOutGetBagRequest(storeId, longitude, latitude, extFeature), listener, true, false, 0);
    }


    /**
     * 外卖--优惠券
     *
     * @param storeId  店铺id
     * @param listener
     */
    public void requestTakeOutVouchers(String storeId, RequestListener<VouchersMO> listener) {
        baseRequest(new TakeOutVouchersRequest(storeId), listener, false);
    }


    /**
     * 外卖--店铺详情页，列出超级会员代金券+店铺代金券信息
     *
     * @param storeId 店铺id
     */
    public void requestTakeOutVouchersList(String storeId, RequestListener<VouchersList> listener) {
        baseRequest(new TakeOutVouchersListRequest(storeId), listener, true);
    }

    /**
     * 领取代金券（含超级会员代金券+店铺代金券
     *
     * @param storeId      餐厅 id
     * @param activityId   券活动 id
     * @param exchangeType 兑换类型 -1:年终奖 0:免费 1:权益 2:红包 3:奖励金
     * @param storeIdType  storeId 类型，0 为淘宝店铺id（默认），1 为饿了么中后台店铺id）
     */
    public void requestGetTakeOutVouchers(String storeId, String activityId, String exchangeType, String storeIdType, RequestListener<TakeVouchers> listener) {
        baseRequest(new TakeOutGetVouchersRequest(storeId, activityId, exchangeType, storeIdType), listener, true);
    }

    /**
     * 店铺详情页，调用饿了么接口返回店铺的优惠券+超会券信息。
     * 注意，如果返回了超级会员代金券，客户端需要在优惠列表动态插入一条优惠信息，因为设计加码逻辑（和店铺+人有关），所以必须要饿了么返回。
     */
    public void requestTakeOutVouchersSummary(String storeId, RequestListener<VouchersSummary> listener) {
        baseRequest(new TakeOutVouchersSummaryRequest(storeId), listener, false);
    }

    /**
     * 外卖天降红包领取接口
     */
    public void requestTakeOutApplyCoupon(String asac, String channel,RequestListener<TakeoutApplyCoupon> listener) {
        baseRequest(new TakeoutApplyCouponRequest(asac,channel), listener, false);
    }

    /**
     * 外卖天降红包配置接口
     */
    public void requestTakeOutCouponStyle(RequestListener<TakeOutCouponStyle> listener) {
        baseRequest(new TakeOutCouponStyleRequest(), listener, false);
    }

    /**
     * 网络加载
     *
     * @author shengzhi.rensz
     * @data 2015-2-9 上午10:58:13
     */
    public interface RequestLoadListener<T> {

        ServiceResponse<T> load();
    }


    private class HeaderWrapper implements anetwork.channel.Header {
        private Header mHeader;

        HeaderWrapper(Header header) {
            this.mHeader = header;
        }

        @Override
        public String getName() {
            return mHeader.getName();
        }

        @Override
        public String getValue() {
            return mHeader.getValue();
        }
    }

}
