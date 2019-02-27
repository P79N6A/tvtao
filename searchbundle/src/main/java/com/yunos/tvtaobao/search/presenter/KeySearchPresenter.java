package com.yunos.tvtaobao.search.presenter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.base.BasePresenter;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.search.contract.KeySearchContract;
import com.yunos.tvtaobao.search.sqlite.SearchHistoryDao;
import com.yunos.tvtaobao.search.sqlite.SearchHistoryDbHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by xtt
 * on 2018/12/6
 */
public class KeySearchPresenter extends BasePresenter<KeySearchContract.Model, KeySearchContract.View> {
    private SearchHistoryDao searchHistoryDao;

    public KeySearchPresenter(Context context, KeySearchContract.Model model, KeySearchContract.View rootView) {
        super(model, rootView);
        searchHistoryDao = new SearchHistoryDao(context);
    }

    /**
     * 请求搜索联想词
     *
     * @param key
     */
    public void requestAssociateWord(Activity activity, String key) {
        mModel.getSearchAssociatedWord(key, new SearchAssociatedWordListener(new WeakReference<BaseActivity>((BaseActivity) activity)));
    }

    /**
     * 存储搜索key
     *
     * @param word
     */
    public void saveHistoryDao(String word) {
        mModel.saveHistory(word,searchHistoryDao);
    }

    private class SearchAssociatedWordListener extends BizRequestListener<ArrayList<String>> {
        public SearchAssociatedWordListener(WeakReference<BaseActivity> weakReference) {
            super(weakReference);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(ArrayList<String> data) {
            mRootView.setAssociateWord(data);
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    /**
     * 获取搜索历史
     */
    public void getSearchHistory() {
        mModel.getSearchHistoryData(searchHistoryDao, new KeySearchContract.OnGetSearchHistoryDataListener() {
            @Override
            public void onSuccess(ArrayList<String> listHistory) {
                mRootView.setSearchHistoryData(listHistory);
            }

            @Override
            public void onFailure() {
                mRootView.setSearchHistoryNoData();
            }
        });
    }

    /**
     * 删除搜索历史
     */
    public void deleteSearchHistory() {
        mModel.deleteSearchHistoryData(searchHistoryDao, new KeySearchContract.OnGetSearchHistoryDataListener() {
            @Override
            public void onSuccess(ArrayList<String> listHistory) {
                mRootView.setSearchHistoryNoData();
            }

            @Override
            public void onFailure() {

            }
        });
    }

    public void requestSearchDiscovery(String mType,Activity activity){
        mModel.getSearchDiscovery(mType,new SearchDiscoveryListener(new WeakReference<BaseActivity>((BaseActivity) activity)) );

    }

    private class SearchDiscoveryListener extends BizRequestListener<ArrayList<String>> {
        public SearchDiscoveryListener(WeakReference<BaseActivity> weakReference) {
            super(weakReference);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            mRootView.setSearchDiscoveryNoData();
            return false;
        }

        @Override
        public void onSuccess(ArrayList<String> data) {
            mRootView.setSearchDiscoveryData(data);
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }
    //关闭游标
    public void setCursorClose(){
        if(mModel!=null) {
            mModel.closeCursor();
        }
    }


}
