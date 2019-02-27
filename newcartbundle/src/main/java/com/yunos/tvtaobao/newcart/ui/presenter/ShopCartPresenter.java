package com.yunos.tvtaobao.newcart.ui.presenter;

import android.app.Activity;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.taobao.wireless.trade.mcart.sdk.co.Component;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ActionType;
import com.taobao.wireless.trade.mcart.sdk.constant.CartFrom;
import com.taobao.wireless.trade.mcart.sdk.engine.CartEngine;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeGoodsBean;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.biz.request.core.ServiceCode;
import com.yunos.tvtaobao.biz.base.BasePresenter;
import com.yunos.tvtaobao.newcart.entity.CartBuilder;
import com.yunos.tvtaobao.newcart.entity.CartGoodsComponent;
import com.yunos.tvtaobao.biz.request.bo.CartStyleBean;
import com.yunos.tvtaobao.newcart.entity.QueryBagType;
import com.yunos.tvtaobao.newcart.ui.activity.NewShopCartListActivity;
import com.yunos.tvtaobao.newcart.ui.contract.ShopCartContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

/**
 * Created by yuanqihui on 2018/6/7.
 */

public class ShopCartPresenter extends BasePresenter<ShopCartContract.Model, ShopCartContract.View> {


    private CartBuilder cartBuilder;
    private String cartFrom;

    public ShopCartPresenter(ShopCartContract.Model model, ShopCartContract.View rootView) {
        super(model, rootView);
    }


    public void getShopCartList(Activity activity, CartBuilder cartBuilder, String cartFrom, QueryBagType queryBagType) {
        mModel.getCartBuilder(cartBuilder);
        this.cartBuilder = cartBuilder;
        this.cartFrom = cartFrom;
        mModel.getCartGoods(cartFrom, new GetShopCartListener(new WeakReference<BaseActivity>((BaseActivity) activity),
                queryBagType));
    }

    public void updateBagRequest(Activity activity, List<Component> items, ActionType updateQuantity, String cartFrom, QueryBagType requestUpdateQuantity) {
        mModel.updateBagRequest(items, updateQuantity, cartFrom, new GetShopCartListener(new WeakReference<BaseActivity>((BaseActivity) activity),
                requestUpdateQuantity));
    }

    public void onHandleAnaylisysTaoke(Activity activity, Map<String, List<CartGoodsComponent>> data) {
        mModel.handleAnaylisysTaoke(activity, data, new TaokeDetailListener(new WeakReference<BaseActivity>((BaseActivity) activity)));
    }

    private int mNextActionPostion; // 下次需要处理的商品位置
    private String mNextActionCartId; // 下次需要处理的商品购物车的ID
    private ActionType mNextActionType; // 下次处理类型
    private Object mNextActionObj; // 数据类型

    /**
     * 设置下次需要请求更新数据的相关信息
     *
     * @param cartId
     * @param listPosition
     * @param cartId
     * @param obj
     */
    public void setNextActionType(ActionType nextActionType, int listPosition, String cartId, Object obj) {
        mNextActionType = nextActionType;
        mNextActionPostion = listPosition;
        mNextActionCartId = cartId;
        mNextActionObj = obj;
    }

    public void getGuessLikeGoods(NewShopCartListActivity newShopCartListActivity, String cartFrom) {
        mModel.getGuessLIkeGoods(cartFrom, new GetGuessLikeListener(new WeakReference<BaseActivity>((BaseActivity) newShopCartListActivity)));
    }

    public void getCartStyle(NewShopCartListActivity newShopCartListActivity){
        mModel.getCartStyle(new GetCartStyleListener(new WeakReference<BaseActivity>((BaseActivity) newShopCartListActivity)));
    }

    public void getGuessLikeRebate( String itemIdArray, List<String> list, List<GuessLikeGoodsBean.ResultVO.RecommendVO> recommemdList , BaseActivity baseActivity){
        try{
            JSONObject object = new JSONObject();
            object.put("umToken", Config.getUmtoken(baseActivity));
            object.put("wua", Config.getWua(baseActivity));
            object.put("isSimulator", Config.isSimulator(baseActivity));
            object.put("userAgent", Config.getAndroidSystem(baseActivity));
            String extParams = object.toString();
            mModel.getGuessLikeGoodsRebate(itemIdArray,list,extParams,new GetRebateBusinessRequestListener(recommemdList,new WeakReference<BaseActivity>(baseActivity)));

        }catch (JSONException e){
            e.printStackTrace();
        }

      }

    public class GetShopCartListener extends BizRequestListener<String> {


        private QueryBagType mQueryBagType; // 数据请求的类型

        public GetShopCartListener(WeakReference<BaseActivity> mBaseActivityRef, QueryBagType requestType) {
            super(mBaseActivityRef);
            mQueryBagType = requestType;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            nextAction(false);
            mRootView.requestDataDone(false);
            AppDebug.i(TAG, "QueryBagListener onError mQueryBagType=" + mQueryBagType + " resultCode=" + resultCode);
            if (mQueryBagType == QueryBagType.REQUEST_NEW_DATA || mQueryBagType == QueryBagType.REQUEST_NEXT_DATA) {
                if (resultCode == ServiceCode.API_NO_DATA.getCode()) {
                    mRootView.requestNewDataDone(true);
                    return true;
                }
            }

            // 只有请求首次数据失败才会清空列表，其它的只给出提示框
            if (mQueryBagType == QueryBagType.REQUEST_NEW_DATA) {
                mRootView.showCartEmpty();
            }
            return false;
        }

        @Override
        public void onSuccess(String data) {
//            mRootView.showProgressDialog(false);
            mRootView.requestDataDone(true);
            AppDebug.i(TAG, "QueryBagListener onSuccess mQueryBagType=" + mQueryBagType + ", data = " + data);
            try {
                List<Component> list = CartEngine.getInstance(getCartFrom(cartFrom)).parseByStructure(JSON.parseObject(data));

                // 新数据请求完成
                if (mQueryBagType == QueryBagType.REQUEST_NEW_DATA
                        || mQueryBagType == QueryBagType.REQUEST_NEXT_DATA) {
                    mRootView.requestNewDataDone(!cartBuilder.haveNextData());
                }
                if (mQueryBagType != QueryBagType.REQUEST_NEW_DATA) {
                    mRootView.rebuildView();
                    //如果不是最后一页,且都是失效商品时,断续请求下一页
                    if (!CartEngine.getInstance(getCartFrom(cartFrom)).isEndPage()
                            && cartBuilder.getCartData().isEmpty()) {
                        mRootView.showProgressDialog(true);
                        mRootView.requestNextData();
                        return;
                    }
                    // 如果只是更新sku、数量、来至于购物车，则不更新购物车数量
                    if (mQueryBagType == QueryBagType.REQUEST_UPDATE_SKU
                            || mQueryBagType == QueryBagType.REQUEST_UPDATE_QUANTITY
                            || !TextUtils.isEmpty(cartFrom)) {
                        mRootView.updateShopCartList(false);
                    } else {
                        mRootView.updateShopCartList(true);
                    }
                } else {

                    boolean result = cartBuilder.buildView();
                    if (!result) {
//                        finish();
                    } else {
                        //如果不是最后一页,且都是失效商品时,断续请求下一页
                        if (!CartEngine.getInstance(getCartFrom(cartFrom)).isEndPage()
                                && cartBuilder.getCartData().isEmpty()) {
                            mRootView.showProgressDialog(true);
                            mRootView.requestNextData();
                            return;
                        }
                        mRootView.updateShopCartList(true);
                    }
                }
                // 完成后处理后续的请求
                nextAction(true);
            } catch (Exception e) {
                e.printStackTrace();
                // 出错显示空购物车
                mRootView.showCartEmpty();
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            // 如果是数据下次请求弹出错误提示框不做退出
            if (mQueryBagType == QueryBagType.REQUEST_NEW_DATA) {
                return true;
            } else {
                return false;
            }

        }

        /**
         * 处理下次请求（有时候处理需要依赖上次处理的结果，如果下次处理的数据为空，就不会进行下次处理）
         *
         * @param preRequestSuccess
         */
        private void nextAction(boolean preRequestSuccess) {
            if (preRequestSuccess && !TextUtils.isEmpty(mNextActionCartId)) {
                if (mNextActionType.compareTo(ActionType.UPDATE_QUANTITY) == 0) {
                    mRootView.updateGoodsQuanitiy(mNextActionPostion, mNextActionCartId,
                            (Integer) mNextActionObj);
                }
            }
        }
    }


    /**
     * 淘客详情打点
     */
    public static class TaokeDetailListener extends BizRequestListener<JSONObject> {

        public TaokeDetailListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return true;
        }

        @Override
        public void onSuccess(JSONObject data) {
//            AppDebug.d(TAG,data.toString());
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    private class GetGuessLikeListener extends BizRequestListener<GuessLikeGoodsBean> {

        public GetGuessLikeListener(WeakReference<BaseActivity> baseActivityWeakReference) {
            super(baseActivityWeakReference);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(GuessLikeGoodsBean data) {
            mRootView.setGuessLikeGoodsData(data);
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }




    private class GetRebateBusinessRequestListener extends BizRequestListener<List<RebateBo>> {

        List<GuessLikeGoodsBean.ResultVO.RecommendVO> recommemdList;
        public GetRebateBusinessRequestListener( List<GuessLikeGoodsBean.ResultVO.RecommendVO> recommemdList ,WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
            this.recommemdList = recommemdList;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(List<RebateBo> data) {
            mRootView.showProgressDialog(false);
            mRootView.showFindsameRebateResult(recommemdList,data);
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
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


    /**
     * 获取运营配置的购物车背景图以及标签位
     * */
    private class GetCartStyleListener extends BizRequestListener<CartStyleBean>{

        public GetCartStyleListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(CartStyleBean data) {
            mRootView.setCartStyleData(data);
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }
}
