package com.yunos.tvtaobao.newcart.ui.model;

import android.app.Activity;

import com.alibaba.fastjson.JSONObject;
import com.taobao.wireless.trade.mcart.sdk.co.Component;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ActionType;
import com.taobao.wireless.trade.mcart.sdk.constant.CartFrom;
import com.taobao.wireless.trade.mcart.sdk.engine.CartEngine;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeGoodsBean;
import com.yunos.tvtaobao.biz.request.bo.QueryBagRequestBo;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.biz.request.core.ServiceCode;
import com.yunos.tvtaobao.biz.base.BaseModel;
import com.yunos.tvtaobao.newcart.entity.CartBuilder;
import com.yunos.tvtaobao.newcart.entity.CartGoodsComponent;
import com.yunos.tvtaobao.biz.request.bo.CartStyleBean;
import com.yunos.tvtaobao.newcart.ui.contract.ShopCartContract;
import com.yunos.tvtaobao.newcart.ui.presenter.ShopCartPresenter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by yuanqihui on 2018/6/8.
 */

public class ShopCartModel extends BaseModel implements ShopCartContract.Model {
    private CartBuilder mCartBuilder;

//    public ShopCartModel(BusinessRequest businessRequest) {
//        super(businessRequest);
//    }

    @Override
    public void getCartGoods(String cartFrom, BizRequestListener<String> listener) {
        if (!mCartBuilder.haveNextData()) {
            listener.onError(ServiceCode.API_NO_DATA.getCode(), ServiceCode.API_NO_DATA.getMsg());
            return;
        }

        QueryBagRequestBo queryBagRequestBo = new QueryBagRequestBo();
        queryBagRequestBo.setPage(true);
        queryBagRequestBo.setCartFrom(cartFrom);
        JSONObject params = mCartBuilder.generatePageData();
        if (params != null) {
            queryBagRequestBo.setP(mCartBuilder.proccessRequestParameter(JSONObject.toJSONString(params)));
            queryBagRequestBo.setFeature(mCartBuilder.getFeature());
        }
        mBusinessRequest.queryBag(queryBagRequestBo, listener);
    }

    @Override
    public CartBuilder getCartBuilder(CartBuilder cartBuilder) {
        this.mCartBuilder = cartBuilder;
        return mCartBuilder;
    }

    private CartFrom getCartFrom(String cartFromString) {
        CartFrom cartFrom;
        if (cartFromString == null) {
            cartFrom = CartFrom.DEFAULT_CLIENT;
        } else if ("tsm_client_native".equalsIgnoreCase(cartFromString)) {
            cartFrom = CartFrom.TSM_NATIVE_TAOBAO;
        } else {
            cartFrom = CartFrom.parseCartFrom(cartFromString);
        }
        return cartFrom;
    }

    @Override
    public void handleAnaylisysTaoke(Activity activity, Map<String, List<CartGoodsComponent>> data, ShopCartPresenter.TaokeDetailListener taokeDetailListener) {
        if (CoreApplication.getLoginHelper(activity).isLogin()) {
            StringBuilder sellerIds = new StringBuilder();
            StringBuilder shopTypes = new StringBuilder();
            StringBuilder itemIds = new StringBuilder();
            for (Iterator<?> it = data.keySet().iterator(); it.hasNext(); ) {
                String key = (String) it.next();
                List<CartGoodsComponent> goodsList = data.get(key);
                if (goodsList != null) {
                    for (int i = 0; i < goodsList.size(); i++) {
                        CartGoodsComponent goods = goodsList.get(i);

                        sellerIds.append(goods.getItemComponent().getSellerId()).append(",");
                        itemIds.append(goods.getItemComponent().getItemId()).append(",");
                        String toBuy = goods.getItemComponent().getToBuy();
                        // 淘宝天猫识别
                        if (toBuy != null && toBuy.equalsIgnoreCase("tmall")) {
                            shopTypes.append("B").append(",");
                        } else {
                            shopTypes.append("C").append(",");
                        }
                    }
                }
            }
            String stbId = DeviceUtil.initMacAddress(activity);
            mBusinessRequest.requestTaokeDetailAnalysis(stbId, User.getNick(), itemIds.toString(), shopTypes.toString(), sellerIds.toString(), taokeDetailListener);
        }
    }

    @Override
    public void updateBagRequest(List<Component> items, ActionType actionType, String cartFrom,
                                 BizRequestListener<String> listener) {
        JSONObject mJSONObject = CartEngine.getInstance(getCartFrom(cartFrom)).getSubmitModule().generateAsyncRequestData(items, actionType, true);
        if (mJSONObject == null) {
            return;
        }

        String params = mCartBuilder.proccessRequestParameter(JSONObject.toJSONString(mJSONObject));
        mBusinessRequest.updateBag(params, cartFrom, listener);
    }

    @Override
    public void getGuessLIkeGoods(String cartFrom, BizRequestListener<GuessLikeGoodsBean> listener) {
        mBusinessRequest.guessLike("tsm_client_native".equals(cartFrom) ? "MAO_CHAO" : "TRADE_CART", listener);
    }

    @Override
    public void getCartStyle(BizRequestListener<CartStyleBean> listener) {
        mBusinessRequest.getCartStyle(listener);
    }

    @Override
    public void getGuessLikeGoodsRebate(String itemIdArray, List<String> list,String extParams, RequestListener<List<RebateBo>> listener) {
        mBusinessRequest.requestRebateMoney(itemIdArray,list,false,false,true,extParams,listener);

    }
}
