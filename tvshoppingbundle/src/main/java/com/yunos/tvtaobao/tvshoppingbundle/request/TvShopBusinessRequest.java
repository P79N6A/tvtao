package com.yunos.tvtaobao.tvshoppingbundle.request;


import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.TbTvShoppingItemBo;
import com.yunos.tvtaobao.tvshoppingbundle.request.item.GetTvShopAdvertTime;
import com.yunos.tvtaobao.tvshoppingbundle.request.item.GetTvShopAllProgramIds;

import java.util.ArrayList;

public class TvShopBusinessRequest extends BusinessRequest {

    private static TvShopBusinessRequest mTvShopBusinessRequest;

    private TvShopBusinessRequest() {
        super();
    }

    public static TvShopBusinessRequest getBusinessRequest() {
        if (mTvShopBusinessRequest == null) {
            mTvShopBusinessRequest = new TvShopBusinessRequest();
        }
        return mTvShopBusinessRequest;
    }

    /**
     * 销毁
     */
    public void destory() {
        mTvShopBusinessRequest = null;
    }

    /**
     * 获取边看边购弹出广告的信息列表
     * @param programId
     * @param sequenceId
     * @param listener
     */
    public void requestGetTvShopTimeItemList(final String programId, final String sequenceId, final boolean isLive,
                                             final boolean isNews, final String fromApp, final RequestListener<ArrayList<TbTvShoppingItemBo>> listener) {
        baseRequest(new GetTvShopAdvertTime(programId, sequenceId, isLive, isNews, fromApp), listener, false);
    }
    
    /**
     * 获取所有配置有边看边买视频id
     * @param listener
     */
    public void requestGetTvShopAllProgramIds(final RequestListener<ArrayList<String>> listener){
        baseRequest(new GetTvShopAllProgramIds(), listener, false);
    }

}
