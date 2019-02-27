package com.yunos.tvtaobao.payresult;

import android.os.AsyncTask;

import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.DynamicRecommend;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.newcart.ui.adapter.GuessLikeAdapter;

import java.lang.ref.WeakReference;
import java.util.List;


/**
 * Created by wuhaoteng on 2018/8/7.
 * 获取支付结果界面数据
 */

public class PayResultDataFetcher {

    private BaseActivity mContext;
    private BusinessRequest mBusinessRequest;
    private GuessLikeAdapter mGuessLikeAdapter;
    private PayResultActivity.OnAllDataFetchListener mOnAllDataFetchListener;
    private AsyncTask<String, Integer, String> mAccountAsyncTask;
    //动态运营位
    private DynamicRecommend dynamicRecommend;

    private int mStep = 0;

    public PayResultDataFetcher(BaseActivity context, GuessLikeAdapter guessLikeAdapter) {
        mContext = context;
        mGuessLikeAdapter = guessLikeAdapter;
        mBusinessRequest = new BusinessRequest();
    }

    public void doRequest(PayResultActivity.OnAllDataFetchListener listener) {
        mOnAllDataFetchListener = listener;
        doNext();
    }


    private void doNext() {
        switch (mStep) {
            case 0:
                //获取支付宝账号
                fetchAliPayAccount(new OnItemDataFetchListener() {
                    @Override
                    public void onComplete() {
                        mStep++;
                        doNext();
                    }
                });
                break;
            case 1:
                //获取运营位信息
                fetchDynamicRecommendData(new OnDynamicRecommendFetchListener() {
                    @Override
                    public void onComplete(DynamicRecommend dynamicRecommend) {
                        PayResultDataFetcher.this.dynamicRecommend = dynamicRecommend;
                        mStep++;
                        doNext();
                    }

                });
                break;
            case 2:
                //获取支付结果推荐
                fetchGuessLikeData(new OnItemDataFetchListener() {
                    @Override
                    public void onComplete() {
                        if (mOnAllDataFetchListener != null) {
                            mOnAllDataFetchListener.onAllComplete();
                        }
                    }

                });
                break;
        }
    }

    public void fetchAliPayAccount(OnItemDataFetchListener listener) {
        mAccountAsyncTask  = new AliPayAccountTask(mOnAllDataFetchListener, listener).execute();
    }

    public void fetchDynamicRecommendData(OnDynamicRecommendFetchListener listener) {
        mBusinessRequest.getDynamicRecommendData(new GetDynamicRecommendListener(new WeakReference<BaseActivity>(mContext),listener));

    }


    public void fetchGuessLikeData(OnItemDataFetchListener listener) {
        mBusinessRequest.realTimeRecommond(new GetGuessLikeDataListener(new WeakReference<BaseActivity>(mContext),
                mGuessLikeAdapter, dynamicRecommend, listener));


    }




    public void close() {
        mBusinessRequest = null;
        if (mAccountAsyncTask != null) {
            mAccountAsyncTask.cancel(true);
            mAccountAsyncTask = null;
        }
    }

    interface OnDynamicRecommendFetchListener{
        void onComplete(DynamicRecommend dynamicRecommend);
    }

    interface OnItemDataFetchListener {
        void onComplete();
    }

}
