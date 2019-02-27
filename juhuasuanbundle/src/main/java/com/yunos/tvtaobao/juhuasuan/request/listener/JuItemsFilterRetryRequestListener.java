/**
 * $
 * PROJECT NAME: juhuasuan
 * PACKAGE NAME: com.yunos.tvtaobao.juhuasuan.request.listener
 * FILE NAME: JuFilterRetryRequestListener.java
 * CREATED TIME: 2015-2-13
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.juhuasuan.request.listener;


import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CategoryMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CountList;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;
import com.yunos.tvtaobao.juhuasuan.request.MyBusinessRequest;
import com.yunos.tvtaobao.juhuasuan.request.MyBusinessRequest.*;

import java.lang.ref.WeakReference;

/**
 * 与JuRetryRequestListener区别是对结果有重处理
 * @version
 * @author hanqi
 * @data 2015-2-13 下午5:07:19
 */
public abstract class JuItemsFilterRetryRequestListener extends JuRetryRequestListener<CountList<ItemMO>> {

    private final String TAG = "JuItemsFilterRetryRequestListener";

    private WeakReference<CategoryMO> mCate;
    private WeakReference<CategoryRequestData> mCateRequestData;

    public JuItemsFilterRetryRequestListener(BaseActivity activity, CategoryMO cate) {
        super(activity);
        mCate = new WeakReference<CategoryMO>(cate);
    }

    public JuItemsFilterRetryRequestListener(BaseActivity activity, CategoryMO cate, boolean finish) {
        super(activity, finish);
        mCate = new WeakReference<CategoryMO>(cate);
    }

    public void setCateRequestData(CategoryRequestData cateRequestData) {
        // 设置加载标识跟记入加入页数
        cateRequestData.mIsLoading = true;
        cateRequestData.mRequestPageNo = cateRequestData.mCurrPageNo + 1;
        if (cateRequestData.mRequestPageNo < 1) {
            cateRequestData.mRequestPageNo = 1;
        }
        mCateRequestData = new WeakReference<MyBusinessRequest.CategoryRequestData>(cateRequestData);
    }

    @Override
    public void onRequestDone(CountList<ItemMO> data, int resultCode, String msg) {
        // 加载完成清除加载标识
        MyBusinessRequest.CategoryRequestData requestData = mCateRequestData.get();
        if (requestData != null) {
            requestData.mIsLoading = false;
        }
        super.onRequestDone(data, resultCode, msg);
    }
    
    @Override
    public CountList<ItemMO> initData(CountList<ItemMO> data) {
        CategoryMO cate = mCate.get();
        if (null == cate) {
            return data;
        }
        MyBusinessRequest businessRequest = MyBusinessRequest.getInstance();
        CategoryRequestData currCategory = null;
        if (businessRequest.hasCategoryItem(cate.getCid())) {
            currCategory = businessRequest.getCategoryRequestData(cate.getCid());
            AppDebug.i(TAG, TAG + ".requestGetCategoryItemList postExecute 1 currCategory=" + currCategory);
        } else {
            currCategory = mCateRequestData.get();
        }
        if (null == currCategory) {
            return data;
        }

        if (data != null) {
            if (data.size() > 0) {
                //去重处理
                CountList<ItemMO> validItemList = null;
                validItemList = businessRequest.filterSameItem(cate.getCid(), data);
                AppDebug.i(TAG, TAG + ".requestGetCategoryItemList.load validItemList=" + validItemList + ", data=" + data);
                validItemList.setCurrentPage(data.getCurrentPage());
                validItemList.setItemCount(data.getItemCount());
                validItemList.setPageSize(data.getPageSize());
                validItemList.setTotalPage(data.getTotalPage());
                data = validItemList;

                //只有取得有效数据才将当前页数据更新
                currCategory.mCurrPageNo = currCategory.mRequestPageNo;
                currCategory.mItemList.addAll(data);
                businessRequest.putCategoryRequestData(cate.getCid(), currCategory);
                AppDebug.i(TAG, TAG + ".requestGetCategoryItemList postExecute category=" + currCategory);
            } else {
                AppDebug.i(TAG, TAG + ".requestGetCategoryItemList mRequestPageNo=" + currCategory.mRequestPageNo+
                        " CurrentPage="+data.getCurrentPage()+" TotalPage="+data.getTotalPage());  
                // 当没有新的数据时，使用之前的数据
                if (currCategory.mRequestPageNo > data.getCurrentPage() && data.getCurrentPage() <= data.getTotalPage()) {
                    CountList<ItemMO> preItemList = currCategory.mItemList;
                    if (preItemList != null && preItemList.size() > 0) {
                        preItemList.setCurrentPage(data.getCurrentPage());
                        preItemList.setItemCount(data.getItemCount());
                        preItemList.setPageSize(data.getPageSize());
                        preItemList.setTotalPage(data.getTotalPage());
                        data = preItemList;
                    }
                }
            }
        }
        return data;
    }

}
