package com.yunos.tvtaobao.newcart.ui.presenter;

import android.app.Activity;

import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeGoodsBean;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.biz.base.BasePresenter;
import com.yunos.tvtaobao.newcart.ui.contract.GuessLikeContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by yuanqihui on 2018/6/27.
 */

public class GuessLikePresenter extends BasePresenter<GuessLikeContract.Model, GuessLikeContract.View> {
    public GuessLikePresenter(GuessLikeContract.Model model, GuessLikeContract.View rootView) {
        super(model, rootView);
    }

    public void getGuessLikeGoods(String channel, Activity activity) {
        mModel.getGuessLIkeGoods(channel, new GetGuessLikeListener(new WeakReference<BaseActivity>((BaseActivity) activity)));
    }

    public void getHomeGuessLikeGoods(String channel, Activity activity) {
        mModel.getHomeGuessLIkeGoods(channel, new GetGuessLikeListener(new WeakReference<BaseActivity>((BaseActivity) activity)));
    }


    public void getGuessLikeRebate( String itemIdArray, List<String> list, List<GuessLikeGoodsBean.ResultVO.RecommendVO> recommemdList , BaseActivity baseActivity){
        try{
            JSONObject object = new JSONObject();
            object.put("umToken", Config.getUmtoken(baseActivity));
            object.put("wua", Config.getWua(baseActivity));
            object.put("isSimulator", Config.isSimulator(baseActivity));
            object.put("userAgent", Config.getAndroidSystem(baseActivity));
            String extParams = object.toString();
            mModel.getGuessLikeGoodsRebate(itemIdArray,list,extParams,new GuessLikePresenter.GetRebateBusinessRequestListener(recommemdList,new WeakReference<BaseActivity>(baseActivity)));

        }catch (JSONException e){
            e.printStackTrace();
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
}
