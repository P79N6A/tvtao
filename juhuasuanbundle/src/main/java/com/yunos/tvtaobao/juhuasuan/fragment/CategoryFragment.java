package com.yunos.tvtaobao.juhuasuan.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.juhuasuan.activity.Category.CategoryFragmentLayout;
import com.yunos.tvtaobao.juhuasuan.activity.Category.CategoryViewPager;
import com.yunos.tvtaobao.juhuasuan.activity.Category.GoodsNormalItemView;
import com.yunos.tvtaobao.juhuasuan.activity.Category.PageItemView;
import com.yunos.tvtaobao.juhuasuan.activity.HomeActivity;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CategoryMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;
import com.yunos.tvtaobao.juhuasuan.common.NetWorkCheck;
import com.yunos.tvtaobao.juhuasuan.config.SystemConfig;
import com.yunos.tvtaobao.juhuasuan.request.MyBusinessRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

@SuppressLint("ValidFragment")
public class CategoryFragment extends BaseFragment {

    public static final String TAG = "CategoryFragment";

    private CategoryMO mCatagory;
    private View mLoadProgressLayout;
    private CategoryViewPager mItemListViewPager;
    private CategoryFragmentLayout mCategoryFragmentLayout;
    private View mNoGoodsLayout;
    private boolean mIsFirstShow = true;

    //    private boolean mLoadSurfaceView = true;
    private Long dataLoadTime = System.currentTimeMillis();

    public CategoryFragment() {
    }

    public CategoryFragment(int containerViewId, String tag, HomeActivity activity) {
        super(R.layout.jhs_category_fragment, containerViewId, tag, activity);
    }

    public static CategoryFragment newInstance(Object object, int containerViewId, String tag, HomeActivity activity) {
        CategoryFragment fragment = new CategoryFragment(containerViewId, tag, activity);
        fragment.mCatagory = (CategoryMO) object;
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDebug.i(TAG, TAG + ".onCreate mCatagory=" + mCatagory);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppDebug.i(TAG, TAG + ".onDestroy recyleMemory is doing");
        recyleMemory();
    }

    @Override
    public void recyleMemory() {
        AppDebug.i(TAG, TAG + ".recyleMemory is running! mCatagory=" + mCatagory);
        if (mItemListViewPager != null) {
            HashMap<Integer, PageItemView> viewList = mItemListViewPager.getPageItemViewMap();
            if (viewList != null) {
                for (Entry<Integer, PageItemView> entry : viewList.entrySet()) {
                    PageItemView value = entry.getValue();
                    value.release();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //进入详情更新成功后需要重新刷新相关信息
        if (resultCode == Activity.RESULT_OK && requestCode == HomeActivity.ACTIVITY_REQUEST_REFRESH_GOODS_INFO_CODE) {
            if (data != null) {
                String categoryId = data.getStringExtra("categoryId");
                if (categoryId.equals(mCatagory.getCid())) {
                    int[] positions = data.getIntArrayExtra("positions");
                    for (Integer pos : positions) {
                        HashMap<Integer, PageItemView> viewList = mItemListViewPager.getPageItemViewMap();
                        if (viewList != null) {
                            for (Entry<Integer, PageItemView> entry : viewList.entrySet()) {
                                PageItemView value = entry.getValue();
                                GoodsNormalItemView goodsView = value.findChildViewForPosition(pos);
                                if (goodsView != null) {
                                    MyBusinessRequest request = MyBusinessRequest.getInstance();
                                    List<ItemMO> itemList = request.getCategoryItemList(mCatagory.getCid());
                                    //解决最后一页补齐用的商品更新问题
                                    int dataPos = pos;
                                    if (null != itemList) {
                                        if (dataPos >= itemList.size()) {
                                            dataPos = dataPos - itemList.size();
                                        } else {
                                            goodsView.enforceRefreshGoodsInfo(itemList.get(dataPos), pos);
                                        }
                                    }
                                }
                            }
                        }
                        AppDebug.i(TAG, "onActivityResult position=" + pos);
                    }
                }
            }
        }
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        AppDebug.i(TAG, TAG + ".onViewCreated mCatagory=" + mCatagory);
        mCategoryFragmentLayout = (CategoryFragmentLayout) rootView.findViewById(R.id.category_layout);
        mItemListViewPager = (CategoryViewPager) rootView.findViewById(R.id.goods_page);
        mItemListViewPager.setContext(getHomeActivity(), this);
        mLoadProgressLayout = rootView.findViewById(R.id.load_progress);
        mNoGoodsLayout = rootView.findViewById(R.id.no_goods);

        mItemListViewPager.setCategoryViewPagerListener(new CategoryViewPager.CategoryViewPagerListener() {

            @Override
            public void onViewPagerChanged(int currPage) {
                if (currPage <= 0) {
                    mCategoryFragmentLayout.setLeftEnableHint(false);
                } else {
                    mCategoryFragmentLayout.setLeftEnableHint(true);
                }

                int pageCount = mItemListViewPager.getViewPagerCount();
                if ((pageCount - 1) <= currPage) {
                    mCategoryFragmentLayout.setRightEnableHint(false);
                } else {
                    mCategoryFragmentLayout.setRightEnableHint(true);
                }
            }

            @Override
            public void onRequestGetGoodsDone(boolean isSuccess) {
                mLoadProgressLayout.setVisibility(View.GONE);
                mNoGoodsLayout.setVisibility(View.GONE);
                mItemListViewPager.setVisibility(View.VISIBLE);
                if (isDoRequestFocus()) {
                    mItemListViewPager.requestFocus();
                    setDoRequestFocus(false);
                }
                int pageCount = mItemListViewPager.getViewPagerCount();
                if (pageCount > 0) {
                    //初始化页面标识
                    mCategoryFragmentLayout.setShowHintEnable(true);
                    // 判断当前页是否为首页
                    if (mItemListViewPager.getCurrentItem() == 0) {
                        mCategoryFragmentLayout.setLeftEnableHint(false);
                    } else {
                        mCategoryFragmentLayout.setLeftEnableHint(true);
                    }
                    if (pageCount > 1) {
                        mCategoryFragmentLayout.setRightEnableHint(true);
                    } else {
                        mCategoryFragmentLayout.setRightEnableHint(false);
                    }
                }
            }

            @Override
            public void onRequestGetGoodsNone() {
                mLoadProgressLayout.setVisibility(View.GONE);
                mNoGoodsLayout.setVisibility(View.VISIBLE);
            }
        });
        requestData();
        //        mItemListViewPager.requestCategoryGoodsData(mCatagory);
        dataLoadTime = System.currentTimeMillis();
    }

    public void requestData() {
        if (!NetWorkUtil.isNetWorkAvailable()) {
            AppDebug.i(TAG, TAG + ".requestData NetWorkCheck.netWorkError mCatagory=" + mCatagory);
            NetWorkCheck.netWorkError(getActivity(), new NetWorkCheck.NetWorkConnectedCallBack() {

                @Override
                public void connected() {
                    requestData();
                }
            });
            return;
        }
        AppDebug.i(TAG, TAG + ".requestData mCatagory=" + mCatagory);
        mItemListViewPager.requestCategoryGoodsData(mCatagory);
    }

    @Override
    public void onHide(FragmentTransaction ft) {
        AppDebug.i(TAG, TAG + ".onHide mItemListViewPager=" + mItemListViewPager + ", fragment.id=" + getId());
        if (mItemListViewPager != null) {
            mItemListViewPager.setHide(true);
        }
    }

    @Override
    public void onShow(FragmentTransaction ft) {
        AppDebug.i(TAG, TAG + ".onShow mItemListViewPager=" + mItemListViewPager + ", fragment.id=" + getId()
                + ", mCatagory=" + mCatagory);
        reLoadData();

        if (mItemListViewPager != null) {
            mItemListViewPager.setHide(false);
            //如果商品为空重新请求数据
            if (mItemListViewPager.getGoodsCount() <= 0) {
                mCategoryFragmentLayout.setShowHintEnable(false);
                mItemListViewPager.setVisibility(View.GONE);
                mLoadProgressLayout.setVisibility(View.VISIBLE);
                //                mItemListViewPager.requestCategoryGoodsData(mCatagory);
                requestData();
            } else {
                if (mIsFirstShow == false) {
                    //重新刷新由于种种原因未显示的商品
                    mItemListViewPager.refreshAllNeedRefreshGoods();
                }
            }
        }
        mIsFirstShow = false;
    }

    /**
     * 是否要重新获取数据
     */
    public void reLoadData() {
        if (System.currentTimeMillis() - dataLoadTime < SystemConfig.RElOADDATA_TIME) {
            return;
        }
        AppDebug.i(TAG, TAG + ".reLoadData System.currentTimeMillis()=" + System.currentTimeMillis()
                + ", dataLoadTime=" + dataLoadTime + ", time=" + (System.currentTimeMillis() - dataLoadTime)
                + ", RElOADDATA_TIME=" + SystemConfig.RElOADDATA_TIME + ", category=" + mCatagory.getName());
        MyBusinessRequest.getInstance().removeMCategoryItemData(mCatagory.getCid());
        if (null != mItemListViewPager) {
            mItemListViewPager.setmNoMoreGoods(false);
        }
        dataLoadTime = System.currentTimeMillis();
    }

}
