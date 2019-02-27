package com.yunos.tvtaobao.search.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.base.BaseModel;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.search.activity.SearchActivity;
import com.yunos.tvtaobao.search.contract.KeySearchContract;
import com.yunos.tvtaobao.search.sqlite.SearchHistoryDao;
import com.yunos.tvtaobao.search.sqlite.SearchHistoryDbHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by xtt
 * on 2018/12/6
 */
public class KeySearchModel extends BaseModel implements KeySearchContract.Model {
    private Cursor cursor;
    @Override
    public void deleteSearchHistoryData(SearchHistoryDao searchHistoryDao, KeySearchContract.OnGetSearchHistoryDataListener listener) {
        searchHistoryDao.delete(SearchHistoryDbHelper.DB_NAME,null,null);
         cursor = searchHistoryDao.findAll(SearchHistoryDbHelper.DB_NAME, new String[]{"_id", "history"},
                null, null, null, null, "_id desc");
        if(cursor!=null&&cursor.getCount()<=0){
            listener.onSuccess(null);
        }else {
            listener.onFailure();
        }

    }

    @Override
    public void getSearchHistoryData(SearchHistoryDao searchHistoryDao,KeySearchContract.OnGetSearchHistoryDataListener onGetSearchHistoryDataListener) {
         cursor = searchHistoryDao.findAll(SearchHistoryDbHelper.DB_NAME, new String[]{"_id", "history"},
                null, null, null, null, "_id desc");
        if(cursor.getCount()<=0){
            onGetSearchHistoryDataListener.onFailure();
            return;
        }
        ArrayList<String> data = new ArrayList<String>();
        while (cursor.moveToNext()) {
            data.add(cursor.getString(cursor.getColumnIndex("history")));
        }
        if(data.size()>0){
            onGetSearchHistoryDataListener.onSuccess(data);
        }else {
            onGetSearchHistoryDataListener.onFailure();
        }

    }

    @Override
    public void getSearchAssociatedWord(String key, BizRequestListener<ArrayList<String>> listener) {
        mBusinessRequest.requestGetSearhRelationRecommend(key,  listener);
    }

    @Override
    public void getSearchDiscovery(String mType, BizRequestListener<ArrayList<String>> listener) {
        mBusinessRequest.getHotWordsList(mType, listener);

    }

    @Override
    public void saveHistory(String word,SearchHistoryDao searchHistoryDao) {
        //        存储搜索key
        ContentValues values = new ContentValues();
        values.put("history", word);
        searchHistoryDao.insert(SearchHistoryDbHelper.DB_NAME, values);

    }

    @Override
    public void closeCursor() {
        if(cursor!=null){
            cursor.close();
        }
    }


}
