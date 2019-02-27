/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yunos.tvtaobao.newcart.ui.contract;

import android.app.Activity;

import com.taobao.wireless.trade.mcart.sdk.co.Component;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ActionType;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeGoodsBean;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.biz.base.IModel;
import com.yunos.tvtaobao.biz.base.IView;
import com.yunos.tvtaobao.newcart.entity.CartBuilder;
import com.yunos.tvtaobao.newcart.entity.CartGoodsComponent;
import com.yunos.tvtaobao.biz.request.bo.CartStyleBean;
import com.yunos.tvtaobao.newcart.ui.presenter.ShopCartPresenter;

import java.util.List;
import java.util.Map;


/**
 */
public interface ShopCartContract {
    //对于经常使用的关于UI的方法可以定义到IView中
    interface View extends IView {

//        void setShopCartData(Map<String, List<CartGoodsComponent>> data);

        void updateGoodsQuanitiy(int mNextActionPostion, String mNextActionCartId, Integer mNextActionObj);

        void requestDataDone(boolean b);

        void requestNewDataDone(boolean b);

        void showCartEmpty();

        void requestNextData();

        void updateShopCartList(boolean b);

        void rebuildView();

        void setGuessLikeGoodsData(GuessLikeGoodsBean data);

        void setCartStyleData(CartStyleBean data);

        void showFindsameRebateResult(List<GuessLikeGoodsBean.ResultVO.RecommendVO> recommemdList, List<RebateBo> data);

    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,如是否使用缓存
    interface Model extends IModel {
        void getCartGoods(String cartfrom, BizRequestListener<String> listener);

        void updateBagRequest(List<Component> items, ActionType actionType, String cartFrom,
                              BizRequestListener<String> listener);

        CartBuilder getCartBuilder(CartBuilder cartBuilder);

        void handleAnaylisysTaoke(Activity activity, Map<String, List<CartGoodsComponent>> data, ShopCartPresenter.TaokeDetailListener taokeDetailListener);

        void getGuessLIkeGoods(String cartFrom, BizRequestListener<GuessLikeGoodsBean> listener);

        void getCartStyle(BizRequestListener<CartStyleBean> listener);

        void getGuessLikeGoodsRebate( String itemIdArray, List<String> list,String extParams, RequestListener<List<RebateBo>> listener);

    }
}
