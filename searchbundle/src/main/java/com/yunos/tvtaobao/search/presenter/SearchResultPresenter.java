package com.yunos.tvtaobao.search.presenter;

import android.app.Activity;

import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.base.BasePresenter;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.bo.GoodsSearchResultDo;
import com.yunos.tvtaobao.biz.request.bo.GoodsSearchZtcResult;
import com.yunos.tvtaobao.search.contract.SearchResultContract;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by xtt
 * on 2018/12/6
 * desc
 */
public class SearchResultPresenter extends BasePresenter<SearchResultContract.Model, SearchResultContract.View> {

    public SearchResultPresenter(SearchResultContract.Model model, SearchResultContract.View rootView) {
        super(model, rootView);
    }

    public void requestSearchProduct(Activity activity, String q, Integer page_size, Integer page_no,
                                     int per, String sort, String cat, String flag,  List<String> list,
                                     boolean isFromCartToBuildOrder, boolean isFromBuildOrder,boolean isTmail ) {
        mModel.getSearchGoodsResult(q, page_size, page_no, per, sort, cat, flag, list,
                isFromCartToBuildOrder, isFromBuildOrder, isTmail,new SearchRequestListener(new WeakReference<BaseActivity>((BaseActivity) activity)));

    }

    private class SearchRequestListener extends BizRequestListener<GoodsSearchResultDo> {

        public SearchRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(GoodsSearchResultDo data) {
            mRootView.setSearchResultData(data);
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return true;
        }

    }
}
