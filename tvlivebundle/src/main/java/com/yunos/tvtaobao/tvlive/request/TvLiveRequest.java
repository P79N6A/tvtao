package com.yunos.tvtaobao.tvlive.request;

import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.LiveBlackList;
import com.yunos.tvtaobao.biz.request.bo.LiveDetailBean;
import com.yunos.tvtaobao.biz.request.bo.TBaoLiveListBean;
import com.yunos.tvtaobao.biz.request.bo.TBaoShopBean;
import com.yunos.tvtaobao.biz.request.bo.TMallLiveBean;
import com.yunos.tvtaobao.biz.request.bo.TMallLiveCommentBean;
import com.yunos.tvtaobao.biz.request.bo.TMallLiveDetailBean;
import com.yunos.tvtaobao.biz.request.bo.TMallLiveShopList;
import com.yunos.tvtaobao.biz.request.bo.YGAcrVideoItem;
import com.yunos.tvtaobao.biz.request.bo.YGAttachInfo;
import com.yunos.tvtaobao.biz.request.item.GetLiveDetailRequest;
import com.yunos.tvtaobao.biz.request.item.GetLiveHotItemListRequest;
import com.yunos.tvtaobao.biz.request.item.GetLiveListRequest;
import com.yunos.tvtaobao.biz.request.item.GetTMallCommentRequest;
import com.yunos.tvtaobao.biz.request.item.GetTMallDetailRequest;
import com.yunos.tvtaobao.biz.request.item.GetTMallShopRequest;
import com.yunos.tvtaobao.biz.request.item.GetYGVideoItemsRequest;
import com.yunos.tvtaobao.biz.request.item.LiveBlackListRequest;
import com.yunos.tvtaobao.biz.request.item.SendCommentRequest;
import com.yunos.tvtaobao.biz.request.item.TMallLiveListRequest;
import com.yunos.tvtaobao.biz.request.item.YGAttachInfoRequest;
import com.yunos.tvtaobao.tvlive.bo.model.bean.YGVideoInfo;
import com.yunos.tvtaobao.tvlive.request.item.YGVideoListRequest;

import java.util.List;

public class TvLiveRequest extends BusinessRequest {
    private static TvLiveRequest mBusinessRequest;

    /**
     * 单例
     */
    private TvLiveRequest() {
    }

    public static TvLiveRequest getBusinessRequest() {
        if (mBusinessRequest == null) {
            mBusinessRequest = new TvLiveRequest();
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
     * 发送天猫直播评论
     *
     * @param cid
     * @param parentId 传入评论id,对该评论进行回复
     * @param text
     * @param source
     * @param type     评论类型,0表示普通评论
     * @param itemId   商品id
     * @param listener
     */
    public void sendComment(String cid, String parentId, String text, String source, String type, String itemId, final RequestListener<?> listener) {
        baseRequest(new SendCommentRequest(cid, parentId, text, source, type, itemId), listener, true);
    }

    /**
     * 获取直播详情
     * mtop.mediaplatform.live.livedetail
     *
     * @param liveId    直播id或topic
     * @param creatorId 视频创建者id
     * @param listener
     */
    public void getLiveDetail(String liveId, String creatorId, final RequestListener<LiveDetailBean> listener) {
        baseRequest(new GetLiveDetailRequest(liveId, creatorId), listener, false);
    }

    /**
     * 获取直播列表
     * mtop.mediaplatform.live.videolist
     *
     * @param listener
     */
    public void getLiveList(int s, int n, String tag, final RequestListener<List<TBaoLiveListBean>> listener) {
        baseRequest(new GetLiveListRequest(s, n, tag), listener, false);
    }

    /**
     * 获取直播宝贝列表
     * mtop.mediaplatform.video.livedetail.itemlist
     *
     * @param liveId
     * @param creatorId
     * @param listener
     */
    public void getLiveHotItemList(String type, String liveId, String creatorId, final RequestListener<TBaoShopBean> listener) {
        baseRequest(new GetLiveHotItemListRequest(type, liveId, creatorId), listener, false);
    }

    /**
     * 获取直播黑名单
     */
    public void getLiveBlackList(final RequestListener<LiveBlackList> listener) {
        baseRequest(new LiveBlackListRequest(), listener, false);
    }

    /**
     * 获取天猫直播列表
     */
    public void getTMallLiveList(final RequestListener<List<TMallLiveBean>> listener) {
        baseRequest(new TMallLiveListRequest(), listener, false);
    }

    /**
     * 获取央广购物视频列表
     */
    public void getYGVideoList(final RequestListener<List<YGVideoInfo>> listener) {
        baseRequest(new YGVideoListRequest(), listener, false, 1000);
    }

    /**
     * 获取央广购物点播打点信息
     *
     * @param episodeId
     * @param listener
     */
    public void getYGVideoItems(String episodeId, RequestListener<List<YGAcrVideoItem>> listener) {
        baseRequest(new GetYGVideoItemsRequest(episodeId), listener, false);
    }

    /**
     * 获取央广直播商品详情
     *
     * @param liveId
     * @param listener
     */
    public void getYGLiveAttachInfo(String liveId, RequestListener<YGAttachInfo> listener) {
        baseRequest(new YGAttachInfoRequest(liveId), listener, false);
    }

    /**
     * 获取天猫直播详情
     *
     * @param cid
     * @param listener
     */
    public void getTMallLiveDetail(String cid, final RequestListener<TMallLiveDetailBean> listener) {
        baseRequest(new GetTMallDetailRequest(cid), listener, false);
    }

    /**
     * 获取天猫直播评论
     *
     * @param app
     * @param sourceId            直播cid
     * @param type                0表示全部,1表示主播,2表示排除主播
     * @param direction           -1表示下拉刷新,0表示上拉
     * @param id                  评论id
     * @param count               查询的数目
     * @param includeCommentCount 是否查询评论数
     * @param listener
     */
    public void getTMallLiveComment(String app, String sourceId, int type, int direction, String timeStamp, String id, int count, boolean includeCommentCount, final RequestListener<TMallLiveCommentBean> listener) {
        baseRequest(new GetTMallCommentRequest(app, sourceId, type, direction, timeStamp, id, count, includeCommentCount), listener, false, 5 * 1000);
    }

    /**
     * 获取天猫直播商品列表
     *
     * @param app
     * @param sourceId
     * @param direction
     * @param count
     * @param listener
     */
    public void getTMallLiveShop(String app, String sourceId, int direction, int count, final RequestListener<TMallLiveShopList> listener) {
        baseRequest(new GetTMallShopRequest(app, sourceId, direction, count), listener, false);
    }



}
