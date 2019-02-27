package com.yunos.tvtaobao.search.contract;

import android.content.Context;

import com.yunos.tvtaobao.biz.base.IModel;
import com.yunos.tvtaobao.biz.base.IView;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.search.sqlite.SearchHistoryDao;

import java.util.ArrayList;


/**
 * Created by xtt
 * on 2018/12/6
 */
public interface KeySearchContract {
    //对于经常使用的关于UI的方法可以定义到IView中
    interface View extends IView {
        void setSearchHistoryData(ArrayList<String> listHistory);
        void setSearchHistoryNoData();
        void setSearchDiscoveryData(ArrayList<String> listDiscovery);
        void setSearchDiscoveryNoData();
        void setAssociateWord(ArrayList<String> words);

    }
    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,如是否使用缓存
    interface Model extends IModel {
        void deleteSearchHistoryData(SearchHistoryDao searchHistoryDao, OnGetSearchHistoryDataListener onGetSearchHistoryDataListener);
        void getSearchHistoryData(SearchHistoryDao searchHistoryDao, OnGetSearchHistoryDataListener onGetSearchHistoryDataListener);
        void getSearchAssociatedWord(String key, BizRequestListener<ArrayList<String>> listener);
        void getSearchDiscovery(String mType, BizRequestListener<ArrayList<String>> listener);
        void saveHistory(String word,SearchHistoryDao searchHistoryDao);
        void closeCursor();

    }

    interface   OnGetSearchHistoryDataListener{
        void onSuccess(ArrayList<String> listHistory);
        void onFailure();
    }



}
