package com.yunos.tvtaobao.payresult;

import android.text.TextUtils;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.util.ActivityPathRecorder;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.DynamicRecommend;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeFieldsVO;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeGoodsBean;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeGoodsBean.ResultVO.RecommendVO;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.newcart.ui.adapter.GuessLikeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuhaoteng on 2018/8/6.
 */

public class GetGuessLikeDataListener extends BizRequestListener<GuessLikeGoodsBean> {
    private static final String TAG = "GetGuessLikeDataListener";

    private ArrayList<RecommendVO> mRecommemdList;
    private GuessLikeAdapter mGuessLikeAdapter;
    private boolean mHasDynamicRecommend;
    private DynamicRecommend mDynamicRecommend;
    private PayResultDataFetcher.OnItemDataFetchListener mOnItemDataFetchListener;
    private BusinessRequest mBusinessRequest;
    WeakReference<BaseActivity> baseActivityRef;

    public GetGuessLikeDataListener(WeakReference<BaseActivity> baseActivityRef, GuessLikeAdapter guessLikeAdapter,
                                    DynamicRecommend dynamicRecommend, PayResultDataFetcher.OnItemDataFetchListener listener) {
        super(baseActivityRef);
        this.baseActivityRef = baseActivityRef;
        mGuessLikeAdapter = guessLikeAdapter;
        mHasDynamicRecommend = (dynamicRecommend != null && dynamicRecommend.isValid()) ? true : false;
        mDynamicRecommend = dynamicRecommend;
        mOnItemDataFetchListener = listener;
    }

    @Override
    public boolean onError(int resultCode, String msg) {
        if (mOnItemDataFetchListener != null) {
            mOnItemDataFetchListener.onComplete();
        }
        return false;
    }

    @Override
    public void onSuccess(GuessLikeGoodsBean data) {
        setGuessLikeGoodsData(data);
        if (mOnItemDataFetchListener != null) {
            mOnItemDataFetchListener.onComplete();
        }
    }

    @Override
    public boolean ifFinishWhenCloseErrorDialog() {
        return false;
    }


    public void setGuessLikeGoodsData(final GuessLikeGoodsBean guessLikeGoodsData) {

        if (guessLikeGoodsData != null) {
            mRecommemdList = new ArrayList<>();
            if (guessLikeGoodsData.getResult() != null && guessLikeGoodsData.getResult().getRecommedResult() != null) {
                int guessLikeNum = mHasDynamicRecommend ? 4 : 5;
                int size = guessLikeGoodsData.getResult().getRecommedResult().size() > guessLikeNum ? guessLikeNum : guessLikeGoodsData.getResult().getRecommedResult().size();
                if (mHasDynamicRecommend) {
                    RecommendVO recommendVO = new RecommendVO();
                    recommendVO.setRecommend(mDynamicRecommend);
                    mRecommemdList.add(recommendVO);
                    mGuessLikeAdapter.setHasDynamicRecommend(true);
                }
                for (int i = 0; i < size; i++) {
                    if (guessLikeGoodsData.getResult().getRecommedResult().get(i).getType().equals("item")) {
                        mRecommemdList.add(guessLikeGoodsData.getResult().getRecommedResult().get(i));
                    }
                }
//                返利
                if (mRecommemdList != null) {
                    try {
                        JSONArray jsonArray = new JSONArray();
                        for (int i = 0; i < mRecommemdList.size(); i++) {
                            GuessLikeGoodsBean.ResultVO.RecommendVO recommendGoods = mRecommemdList.get(i);
                            if(recommendGoods!=null){
                                GuessLikeFieldsVO fields = recommendGoods.getFields();
                                if(fields!=null){
                                    String itemId = fields.getItemId();
                                    String price = null;
                                    GuessLikeFieldsVO.PriceVO priceVo = fields.getPrice();
                                    if(priceVo!=null){
                                        if (priceVo != null && (priceVo.getCent() != null) || priceVo.getYuan() != null) {
                                            if (!TextUtils.isEmpty(priceVo.getCent())) {
                                                price = priceVo.getYuan() + "." + priceVo.getCent();
                                            } else {
                                                price = priceVo.getYuan();
                                            }
                                        }
//                            String itemS11Pre = goods.getS11Pre();
                                        AppDebug.e(TAG, "Rebate itemId = " + itemId + ";itemS11Pre = false" + ";price =" + price);
                                        JSONObject object = new JSONObject();
                                        object.put("itemId", itemId);
//                        object.put("isPre", itemS11Pre);
                                        object.put("price", price);
                                        jsonArray.put(object);
                                    }

                                }
                            }
                        }
                        AppDebug.e(TAG, "Rebate" + jsonArray.toString());
                        if(baseActivityRef!=null&&baseActivityRef.get()!=null) {
                            PayResultActivity payResultActivity = (PayResultActivity) baseActivityRef.get();

                                JSONObject object = new JSONObject();
                                object.put("umToken", Config.getUmtoken(payResultActivity));
                                object.put("wua", Config.getWua(payResultActivity));
                                object.put("isSimulator", Config.isSimulator(payResultActivity));
                                object.put("userAgent", Config.getAndroidSystem(payResultActivity));
                                String extParams = object.toString();


                            getGuessLikeGoodsRebate( jsonArray.toString(),
                                    ActivityPathRecorder.getInstance().getCurrentPath(payResultActivity),extParams, new GetRebateBusinessRequestListener(mRecommemdList, mBaseActivityRef));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }



//                mGuessLikeAdapter.setData(mRecommemdList);
            }
        }
    }

//    返利
    public void getGuessLikeGoodsRebate( String itemIdArray, List<String> list,String extParams, RequestListener<List<RebateBo>> listener){
        mBusinessRequest = new BusinessRequest();
        mBusinessRequest.requestRebateMoney(itemIdArray,list,false,false,true,extParams,listener);
    }
//    返利监听
    private class GetRebateBusinessRequestListener extends BizRequestListener<List<RebateBo>> {

        ArrayList<RecommendVO> recommemdList;
        public GetRebateBusinessRequestListener( ArrayList<RecommendVO> recommemdList ,WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
            this.recommemdList = recommemdList;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(List<RebateBo> data) {
            showFindsameRebateResult(recommemdList,data);
        }


        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    private void showFindsameRebateResult(ArrayList<RecommendVO> recommemdList, List<RebateBo> data) {
        if (data != null && data.size() > 0) {
            for (RebateBo rebateBo : data) {
                for (GuessLikeGoodsBean.ResultVO.RecommendVO recommendGoods : recommemdList) {
                    if (rebateBo != null&&recommendGoods != null) {
                        String rebateBoId = rebateBo.getItemId();
                        GuessLikeFieldsVO fields = recommendGoods.getFields();
                        if(fields != null){
                            String recommendGoodsId = fields.getItemId();
                            if(rebateBoId != null){
                                if(rebateBoId.equals(recommendGoodsId)){
                                    recommendGoods.setRebateBo(rebateBo);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            mGuessLikeAdapter.setData(recommemdList);
        }else {
            mGuessLikeAdapter.setData(recommemdList);
        }


    }

}
