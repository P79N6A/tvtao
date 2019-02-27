package com.yunos.tvtaobao.newcart.ui.model;

import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeGoodsBean;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.biz.base.BaseModel;
import com.yunos.tvtaobao.newcart.ui.contract.GuessLikeContract;

import java.util.List;

/**
 * Created by yuanqihui on 2018/6/28.
 */

public class GuessLikeModel extends BaseModel implements GuessLikeContract.Model {

    @Override
    public void getGuessLIkeGoods(String channel, BizRequestListener<GuessLikeGoodsBean> listener) {
        mBusinessRequest.guessLike(channel, listener);
    }

    @Override
    public void getHomeGuessLIkeGoods(String channel, BizRequestListener<GuessLikeGoodsBean> listener) {
        mBusinessRequest.homeGuessLike(channel, listener);
    }

    @Override
    public void getGuessLikeGoodsRebate( String itemIdArray, List<String> list,String extParams, RequestListener<List<RebateBo>> listener) {
        mBusinessRequest.requestRebateMoney(itemIdArray,list,false,false,true,extParams,listener);
    }

}
