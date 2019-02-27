/**
 * $
 * PROJECT NAME: juhuasuan
 * PACKAGE NAME: com.yunos.tvtaobao.juhuasuan.request
 * FILE NAME: MyBusinessRequest.java
 * CREATED TIME: 2015-2-12
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.juhuasuan.request;


import android.content.Context;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CategoryMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CountList;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.HomeCatesResponse;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.Option;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.PaginationItemRate;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.enumeration.OptionType;
import com.yunos.tvtaobao.juhuasuan.request.item.GetItemRateRequest;
import com.yunos.tvtaobao.juhuasuan.request.item.GetJuItemDetailRequest;
import com.yunos.tvtaobao.juhuasuan.request.item.GetOptionItemRequest;
import com.yunos.tvtaobao.juhuasuan.request.item.GetOptionRequest;
import com.yunos.tvtaobao.juhuasuan.request.item.GetTmsHomeCatesRequest;
import com.yunos.tvtaobao.juhuasuan.request.listener.JuItemRetryRequestListener;
import com.yunos.tvtaobao.juhuasuan.request.listener.JuItemsFilterRetryRequestListener;
import com.yunos.tvtaobao.juhuasuan.request.listener.JuRetryRequestListener;
import com.yunos.tvtaobao.juhuasuan.util.StringUtil;

import java.util.HashMap;
import java.util.List;

/**
 * 数据请求接口
 * @version
 * @author hanqi
 * @data 2015-2-12 下午6:54:49
 */
public class MyBusinessRequest extends BusinessRequest {

    public static final String VIDEO_CATEGORY_ID = "0";
    private static final String TAG = "BusinessRequest";
    private static MyBusinessRequest mBusinessRequest;
    private HashMap<String, CategoryRequestData> mCategoryItem = new HashMap<String, CategoryRequestData>();
    private HashMap<String, List<ItemMO>> mCachePreItemList = new HashMap<String, List<ItemMO>>();

    /**
     * 单例
     * @return
     */
    public static MyBusinessRequest getInstance() {
        if (mBusinessRequest == null) {
            mBusinessRequest = new MyBusinessRequest();
        }
        return mBusinessRequest;
    }

    //------------数据存储处理开始---------------------------------------------------------------------------------------------------//

    /**
     * 删除数据
     * @param cid
     */
    public void removeMCategoryItemData(String cid) {
        mCategoryItem.remove(cid);
        mCachePreItemList.remove(cid);
        AppDebug.i(TAG, TAG + ".removeMCategoryItemData cid=" + cid);
    }

    /**
     * 是否有某个类目的数据信息
     * @param cid
     * @return
     */
    public boolean hasCategoryItem(String cid) {
        return mCategoryItem.containsKey(cid);
    }

    /**
     * 取某个类目的数据信息
     * @param cid
     * @return
     */
    public CategoryRequestData getCategoryRequestData(String cid) {
        return mCategoryItem.get(cid);
    }

    /**
     * 存放某个类目的数据信息
     * @param cid
     * @param categoryRequestData
     */
    public void putCategoryRequestData(String cid, CategoryRequestData categoryRequestData) {
        mCategoryItem.put(cid, categoryRequestData);
    }

    /**
     * 取得分类所有的商品列表
     * @param categoryId
     * @return
     */
    public CountList<ItemMO> getCategoryItemList(String categoryId) {
        CategoryRequestData category = mCategoryItem.get(categoryId);
        if (category != null) {
            return category.mItemList;
        } else {
            return null;
        }
    }

    /**
     * 取得当前分类数据请求的页数
     * @param categoryId
     * @return
     */
    public int getCurrCateogryPageNo(String categoryId) {
        CategoryRequestData category = mCategoryItem.get(categoryId);
        if (category != null) {
            return category.mCurrPageNo;
        } else {
            return 0;
        }
    }

    /**
     * 更新商品分类列表里面指定的信息
     * @param categoryId
     * @param data
     */
    public void updateCategoryListGoodsInfo(String categoryId, ItemMO data) {
        if (categoryId == null || data == null) {
            return;
        }
        CategoryRequestData result = mCategoryItem.get(categoryId);
        if (result != null && result.mItemList != null) {
            for (ItemMO item : result.mItemList) {
                if (item.getItemId().compareTo(data.getItemId()) == 0) {
                    AppDebug.i(TAG, "updateCategoryListGoodsInfo item=" + item.getItemId());
                    item.updateItemData(data);
                }
            }
        }

    }

    //-----------------数据存储处理结束--------------------------------------------------------------------------------------//

    /**
     * 销毁
     */
    public void destory() {
        mBusinessRequest = null;
    }

    @Override
    public <T> void baseRequest(BaseMtopRequest request, RequestListener<T> listener, boolean needLogin, boolean post) {
        if (listener instanceof JuRetryRequestListener) {
            ((JuRetryRequestListener<T>) listener).setDataRequest(request);
        }
        super.baseRequest(request, listener, needLogin, post);
    }

    /**
     * 获取首页tms配置
     * @param listener
     */
    public void requestGetTmsHomeCates(JuRetryRequestListener<HomeCatesResponse> listener) {
        baseRequest(new GetTmsHomeCatesRequest(), listener, false);
    }

    /**
     * 查询品牌团商品
     * @param brandCode
     * @param page
     * @param pageSize
     * @param listener
     */
    public void requestGetBrandItems(String brandCode, int page, int pageSize,
                                     JuRetryRequestListener<CountList<ItemMO>> listener) {
        requestGetOptionItems(OptionType.Brand.getplatformId(), brandCode, page, 100, listener);
    }

    /**
     * 通过option获取商品列表
     * @param platformId
     * @param optStr
     * @param page
     * @param pageSize
     * @param listener
     */
    public void requestGetOptionItems(String platformId, String optStr, Integer page, final Integer pageSize,
                                      JuRetryRequestListener<CountList<ItemMO>> listener) {
        baseRequest(new GetOptionItemRequest(platformId, optStr, page, 100), listener, false, false);
    }

    /**
     * 取得普通分类的商品列表
     * @param context
     * @param cate
     * @param pageSize
     * @param listener
     */
    public void requestGetCategoryItemList(Context context, final CategoryMO cate, final Integer pageSize,
                                           final JuItemsFilterRetryRequestListener listener) {
        //加入到分类列表里面
        final CategoryRequestData category;
        if (!mCategoryItem.containsKey(cate.getCid())) {
            category = new CategoryRequestData();
            category.mItemList = new CountList<ItemMO>();
            category.mCurrPageNo = -1;
            mCategoryItem.put(cate.getCid(), category);
        } else {
            category = mCategoryItem.get(cate.getCid());
        }
        AppDebug.i(TAG, "requestGetCategoryItemList isLoading=" + category.mIsLoading);
        //如果当前分类正在加载就不加载，只更新监听方法
        if (category.mIsLoading) {
            return;
        }
        String platFormId = OptionType.Today.getplatformId();

        listener.setCateRequestData(category);
        baseRequest(new GetOptionItemRequest(platFormId, cate.getOptStr(), category.mRequestPageNo, pageSize, null,
                null, null, null), listener, false, false);
    }

    /**
     * 获取商品评价(目前没有用到cm)
     * @param itemId
     * @param pageNo
     * @param pageSize
     * @param listener
     */
    public void requestItemRates(long itemId, int pageNo, int pageSize,
            JuRetryRequestListener<PaginationItemRate> listener) {
        baseRequest(new GetItemRateRequest(itemId, pageNo, pageSize), listener, false, false);
    }

    /**
     * 通过ID取得商品的信息(取得比获取列表更加详细的信息)(目前没有用到cm)
     * @param juId
     *            聚划算中的商品id
     * @param listener
     */
    public void requestGetItemById(final Long juId, JuItemRetryRequestListener listener) {
        baseRequest(new GetJuItemDetailRequest(juId), listener, false, false);
    }

    /**
     * 获取option：分类 品牌团 平拍团分类
     * @param platformId @see OptionType
     * @param optStr
     *            platformId是品牌团，optStr==null时去品牌团，optStr=="frontCatId:-1;"时去品牌团分类
     * @param page
     * @param pageSize
     * @param listener
     */
    public void requestGetOption(final String platformId, final String optStr, final Integer page,
                                 final Integer pageSize, JuRetryRequestListener<CountList<Option>> listener) {
        baseRequest(new GetOptionRequest(platformId, optStr, page, pageSize), listener, false, false);
    }

    /**
     * 取得产品的分类信息
     * @param categoryType
     *            普通商品0 本地化商品1
     * @param listener
     */
    public void requestCategory(Integer categoryType, JuRetryRequestListener<CountList<Option>> listener) {
        baseRequest(new GetOptionRequest(OptionType.Today.getplatformId(), null, 0, 200), listener, false, false);
    }

    synchronized public CountList<ItemMO> filterSameItem(String cid, CountList<ItemMO> itemList) {
        if (StringUtil.isEmpty(cid) || cid.equals("0") || itemList == null) {
            return null;
        }
        List<ItemMO> preItemList = mCachePreItemList.get(cid);
        if (preItemList != null && preItemList.size() > 0) {
            //            itemList.add(preItemList.get(0));
            CountList<ItemMO> vaildItemList = new CountList<ItemMO>();
            for (ItemMO currItem : itemList) {
                boolean isSame = false;
                for (ItemMO preItem : preItemList) {
                    if (currItem.getJuId().compareTo(preItem.getJuId()) == 0) {
                        AppDebug.i(TAG,
                                "filterSameItem same goods juId=" + currItem.getJuId() + " | " + preItem.getJuId()
                                        + "name=" + currItem.getLongName());
                        isSame = true;
                        break;
                    }
                }
                if (!isSame) {
                    vaildItemList.add(currItem);
                    AppDebug.i(TAG, "filterSameItem not same goods vaildItemList=" + vaildItemList);
                }
            }
            vaildItemList.setCurrentPage(itemList.getCurrentPage());
            vaildItemList.setItemCount(itemList.getItemCount());
            vaildItemList.setPageSize(itemList.getPageSize());
            vaildItemList.setTotalPage(itemList.getTotalPage());
            preItemList = vaildItemList;
        } else {
            preItemList = itemList;
        }
        mCachePreItemList.put(cid, preItemList);
        AppDebug.i(TAG, TAG + ".filterSameItem preItemList" + preItemList.size());
        return new CountList<ItemMO>(preItemList);
    }

    /**
     * 加载分类信息的数据
     * @author tim
     */
    public class CategoryRequestData {

        public CountList<ItemMO> mItemList;//列表
        public int mCurrPageNo;//已经请求取得数据的page数
        public int mRequestPageNo;//当前请求page数
        public boolean mIsLoading;//当前是否在请求数据中
    }

}
