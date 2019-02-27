package com.yunos.tvtaobao.detailbundle.activity;


import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.TradeBaseActivity;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.ItemRates;
import com.yunos.tvtaobao.biz.request.bo.PaginationItemRates;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.detailbundle.R;
import com.yunos.tvtaobao.detailbundle.adapter.DetailEvaluateAdapter;
import com.yunos.tvtaobao.detailbundle.bean.ItemRateInfo;
import com.yunos.tvtaobao.detailbundle.resconfig.IResConfig;
import com.yunos.tvtaobao.detailbundle.view.CommentPageView;
import com.yunos.tvtaobao.detailbundle.view.DetailBuilder;
import com.yunos.tvtaobao.detailbundle.view.DetailEvaluateView;
import com.yunos.tvtaobao.detailbundle.view.DetailFocusPositionManager;
import com.yunos.tvtaobao.detailbundle.view.DetailListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class DetailEvaluateActivity extends TradeBaseActivity {

    private final String TAG = "Page_TbDetail_Evaluate";

    private final int PAGESIZE = 20;

    //评价内容预留
    private final int KEEPSIZE = 10;

    // 商品ID号
    private String mItemID = null;

    private TBDetailResultV6 mTBDetailResultVO;

    // 评价内容
    private ArrayList<ItemRateInfo> mItemRateList;

    private BusinessRequest mBusinessRequest;

    private DetailFocusPositionManager mFocusPositionManager;

    private DetailEvaluateView mDetailEvaluateView;

    // 详情页面的数据处理
    private DetailBuilder mDetailBuilder;

    // 资源配置[天猫，或者淘宝]
    private IResConfig resConfig;

    public boolean isFirstRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onKeepActivityOnlyOne(DetailEvaluateActivity.class.getName());

        setContentView(R.layout.ytm_activity_detail_evaluate);

        mItemID = getIntent().getStringExtra(BaseConfig.INTENT_KEY_ITEMID);
        mTBDetailResultVO = (TBDetailResultV6) getIntent().getExtras().get("mTBDetailResultVO");
        if (TextUtils.isEmpty(mItemID)) {
            finish();
            return;
        }

        onInitDetailValue();
        setDetailViewListen();

        // 重新调整详情页面相关的变量
        reAdjustInitDetailValue();

        // 先获取全部评论，主要是计算各评论类型的数量
        getAllRatesData();
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        if (!TextUtils.isEmpty(mItemID)) {
            p.put("item_id", mItemID);
        }
        if(!TextUtils.isEmpty(getAppName())&&!TextUtils.isEmpty(AppInfo.getAppVersionName())){
            p.put("from_app", getAppName()+AppInfo.getAppVersionName());
        }
        if (mTBDetailResultVO!=null&&mTBDetailResultVO.getItem()!=null&&!TextUtils.isEmpty(mTBDetailResultVO.getItem().getTitle())) {
            p.put("item_name", mTBDetailResultVO.getItem().getTitle());
        }
        if (CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
            p.put("is_login", "1");
        } else {
            p.put("is_login", "0");
        }
        p.put(SPMConfig.SPM_CNT, "a2o0j.11292282.0.0");
        return p;
    }

    @Override
    public String getPageName() {
        return TAG;
    }

    @Override
    protected void refreshData() {

    }

    /**
     * 初始化详情页面的变量值
     */
    private void onInitDetailValue() {
        mBusinessRequest = BusinessRequest.getBusinessRequest();
        mDetailEvaluateView = new DetailEvaluateView(new WeakReference<Activity>(this));
        mFocusPositionManager = (DetailFocusPositionManager) findViewById(R.id.detail_evaluate_main);
        mFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                R.drawable.ytbv_common_focus)));

        mDetailBuilder = new DetailBuilder(this);
        // 检查mTBDetailResultVO数据
        mDetailBuilder.onCheckResultVO(mTBDetailResultVO,null);

        // 获取资源配置
        resConfig = mDetailBuilder.getResConfig();
    }

    /**
     * 设置详情页面的监听
     */
    private void setDetailViewListen() {

        // 设置评价切换TAB的监听
        mDetailEvaluateView.setChangeTabListen(new DetailEvaluateView.onChangeTabListen() {

            @Override
            public void onChangeTab(int tabPosition, boolean select) {
                ItemRateInfo itemRateInfo = getItemRateInfo(tabPosition);
                if (itemRateInfo != null) {
                    itemRateInfo.isShow = select;
                    AppDebug.i(TAG, "setChangeTabListen --> tabPosition = " + tabPosition + "; select = " + select);
                    if (select) {
                        handlerEvaluateItemSelected(tabPosition, 0);
                    }
                }
            }
        });

        // 设置评价内容点击的监听
        mDetailEvaluateView.setEvaluateItemClickListener(new DetailEvaluateView.OnEvaluateItemClickListener() {

            @Override
            public void onEvaluateItemClick(AdapterView<?> parent, View view, int tabnumber, int position, long id) {
                AppDebug.i(TAG, "setEvaluateItemClickListener --> tabnumber = " + tabnumber + "; position = "
                        + position + "; view = " + view);
                if (mItemRateList != null && position >= 0) {
                    ItemRateInfo itemRateInfo = getItemRateInfo(tabnumber);
                    if (itemRateInfo != null && itemRateInfo.mRatesList != null && !itemRateInfo.mRatesList.isEmpty()
                            && position < itemRateInfo.mRatesList.size()) {
                        AppDebug.i(TAG, "setEvaluateItemClickListener --> itemRateInfo = " + itemRateInfo);
                        ItemRates itemRates = itemRateInfo.mRatesList.get(position);
                        if (itemRates != null && itemRates.getPicUrlList() != null
                                && !itemRates.getPicUrlList().isEmpty()) {
                            // 点击的项中如果有图片地址，那么就调起图片展示浮层   
                            if (mDetailEvaluateView != null) {
                                int totalType = 0;
                                if (mDetailBuilder != null) {
                                    totalType = mDetailBuilder.getRateTypeCount();
                                }

                                AppDebug.i(TAG, "setEvaluateItemClickListener --> tabnumber = " + tabnumber
                                        + "; position = " + position + "; itemRateInfo = " + itemRateInfo
                                        + "; totalType = " + totalType);

                                mDetailEvaluateView.setCommentPageViewData(itemRateInfo.mRatesList, tabnumber,
                                        position, totalType);
                                mDetailEvaluateView.setCommentPageViewVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        });

        // 评价内容选择的监听
        mDetailEvaluateView.setEvaluateItemSelectedListener(new DetailEvaluateView.OnEvaluateItemSelectedListener() {

            @Override
            public void onEvaluateItemSelected(AdapterView<?> parent, View view, int tabnumber, int position, long id) {
                handlerEvaluateItemSelected(tabnumber, position);
            }
        });

        // 设置浮层监听
        mDetailEvaluateView.setCommentPageViewListListener(new CommentPageView.OnItemRatesListListener() {

            @Override
            public boolean onReloadNextPageRates(int tabnumber, int selectposition) {
                AppDebug.i(TAG, "onReloadNextPageRates --> tabnumber = " + tabnumber + "; selectposition = "
                        + selectposition);
                return handlerEvaluateItemSelected(tabnumber, selectposition);
            }

            @Override
            public void onItemRatesIndexChanged(ArrayList<ItemRates> itemRatesAllList, int curIndex) {

            }
        });

    }

    /**
     * 处理评价内容选择
     * @param tabnumber
     * @param selectpos
     */
    private boolean handlerEvaluateItemSelected(int tabnumber, int selectpos) {
        ItemRateInfo itemRateInfo = getItemRateInfo(tabnumber);
        AppDebug.i(TAG, "handlerEvaluateItemSelected --> getRatesData --> tabnumber = " + tabnumber + "; selectpos = "
                + selectpos + "; itemRateInfo = " + itemRateInfo);
        if (!checkNetwork()) {
            return false;
        }
        AppDebug.i(TAG, "handlerEvaluateItemSelected --> network ok!");

        if (itemRateInfo != null && !itemRateInfo.request_loading) {
            ArrayList<ItemRates> itemRates = itemRateInfo.mRatesList;
            if (itemRates != null) {
                //  内容保存在list中的条数
                int saveSize = itemRates.size();

                //  预留的评价内容条数
                int needSize = (selectpos + 1) + KEEPSIZE;

                AppDebug.i(TAG, "handlerEvaluateItemSelected --> getRatesData --> saveSize = " + saveSize
                        + "; needSize = " + needSize);
                if (saveSize < needSize && saveSize < itemRateInfo.total_count) {
                    // 如果保存的条数，比需要预留的条数要少，那么请求
                    itemRateInfo.request_loading = true;
                    getRatesData(itemRateInfo.currentPageNum, itemRateInfo.pageSize, tabnumber);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检查网络状态
     * @return
     */
    private boolean checkNetwork() {
        boolean result = false;
        if (!NetWorkUtil.isNetWorkAvailable()) {
            result = false;
            showNetworkErrorDialog(false);
        } else {
            removeNetworkOkDoListener();
            result = true;
        }
        return result;
    }

    /**
     * 初始化埋点信息 TODO 这个有用吗
     * @return
     */
    private Map<String, String> initTBSProperty() {
        Map<String, String> p = Utils.getProperties();
        if (!TextUtils.isEmpty(mItemID)) {
            p.put("item_id", mItemID);
        }

        if (CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
            p.put("is_login", "1");
        } else {
            p.put("is_login", "0");
        }

        return p;
    }

    /**
     * 获取 ItemRateInfo
     * @param tabnumber
     * @return
     */
    private ItemRateInfo getItemRateInfo(int tabnumber) {
        if (mItemRateList != null) {
            int size = mItemRateList.size();
            if (tabnumber >= 0 && tabnumber < size) {
                ItemRateInfo itemRateInfo = mItemRateList.get(tabnumber);
                return itemRateInfo;
            }
        }
        return null;
    }

    /**
     * 获取全部的评价
     */
    private void getAllRatesData() {
        AppDebug.i(TAG, "getAllRatesData -->");
        isFirstRequest = true;
        // 主要目的是得到各评价的总数; 所以 pagesize 只需一个； tabnumber = -1
        getRatesData(1, 2, DetailBuilder.LAYOUT_INDEX_F1);
    }

    /**
     * 请求评论的数据
     * @param pageNo 第几页
     * @param tabnumber tab的位置
     */
    private void getRatesData(int pageNo, final int pagesize, final int tabnumber) {
        String rateType = null;
        if (mDetailBuilder != null) {
            rateType = mDetailBuilder.getRateType(tabnumber);
        }

        AppDebug.i(TAG, "getRatesData --> tabnumber = " + tabnumber + "; pageNo = " + pageNo + "; pagesize = "
                + pagesize + "; rateType = " + rateType);

        mBusinessRequest.requestGetItemRates(mItemID, pageNo, pagesize, rateType,
                new GetItemRatesBusinessRequestListener(new WeakReference<BaseActivity>(this), tabnumber));

    }

    /**
     * 评论请求的监听类
     */
    private static class GetItemRatesBusinessRequestListener extends BizRequestListener<PaginationItemRates> {

        private final int mTabPosition;

        public GetItemRatesBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef, final int position) {
            super(mBaseActivityRef);
            mTabPosition = position;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            DetailEvaluateActivity mDetailEvaluateActivity = (DetailEvaluateActivity) mBaseActivityRef.get();
            if (mDetailEvaluateActivity != null) {
                mDetailEvaluateActivity.OnWaitProgressDialog(false);
                AppDebug.i(mDetailEvaluateActivity.TAG,
                        "GetItemRatesBusinessRequestListener --> onError --> resultCode = " + resultCode + "; msg = "
                                + msg);
                mDetailEvaluateActivity.onHandleRequestGetItemRatesError(mTabPosition);

                if (mDetailEvaluateActivity.isFirstRequest) {
                    return false;
                }
                mDetailEvaluateActivity.isFirstRequest = false;
            }
            return true;
        }

        @Override
        public void onSuccess(PaginationItemRates data) {
            DetailEvaluateActivity mDetailEvaluateActivity = (DetailEvaluateActivity) mBaseActivityRef.get();
            if (mDetailEvaluateActivity != null) {
                AppDebug.i(mDetailEvaluateActivity.TAG, "GetItemRatesBusinessRequestListener --> onSuccess --> data = "
                        + data);
                mDetailEvaluateActivity.OnWaitProgressDialog(false);
                if (mTabPosition == DetailBuilder.LAYOUT_INDEX_F1) {
                    mDetailEvaluateActivity.handlerGetAllRatesData(data, mTabPosition);
                } else {
                    mDetailEvaluateActivity.onHandleRequestGetItemRatesSuccess(data, mTabPosition);
                }

                mDetailEvaluateActivity.isFirstRequest = false;
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            DetailEvaluateActivity mDetailEvaluateActivity = (DetailEvaluateActivity) mBaseActivityRef.get();
            if (mDetailEvaluateActivity != null && mDetailEvaluateActivity.isFirstRequest) {
                return true;
            }
            return false;
        }
    }

    /**
     * 重新调整相关的变量
     */
    private void reAdjustInitDetailValue() {
        AppDebug.i(TAG, "reAdjustInitDetailValue --> resConfig = " + resConfig);
        if (resConfig != null) {

            if (mItemRateList == null) {
                mItemRateList = new ArrayList<ItemRateInfo>();
            }
            mItemRateList.clear();

            int itemRateListLen = 4;
            if (mDetailBuilder != null) {
                itemRateListLen = mDetailBuilder.getRateTypeCount();
            }

            for (int index = 0; index < itemRateListLen; index++) {
                ItemRateInfo itemRateInfo = new ItemRateInfo();
                itemRateInfo.mRatesList = new ArrayList<ItemRates>();
                itemRateInfo.mRatesList.clear();
                itemRateInfo.mEvaluateAdapter = new DetailEvaluateAdapter(this);
                itemRateInfo.mEvaluateListView = new DetailListView(this);
                itemRateInfo.mEvaluateListView.setAdapter(itemRateInfo.mEvaluateAdapter);
                itemRateInfo.isPerformClick = false;
                if (index == 0) {
                    itemRateInfo.isGainFoucs = true;
                } else {
                    itemRateInfo.isGainFoucs = false;
                }
                itemRateInfo.isShow = false;
                itemRateInfo.isFatherViewShow = true;
                itemRateInfo.tabNumber = index;
                itemRateInfo.request_loading = false;
                itemRateInfo.request_first = true;

                // 以下参数跟请求有关
                itemRateInfo.currentPageNum = 1;
                itemRateInfo.end_request = false;
                itemRateInfo.pageSize = PAGESIZE;
                // 默认为无限大
                itemRateInfo.total_count = Integer.MAX_VALUE;

                mItemRateList.add(itemRateInfo);

            }
        }
    }

    /**
     * 初始化评价卡片，填充各类型的总数
     */
    public void handlerGetAllRatesData(PaginationItemRates data, int tabPosition) {
        if (mDetailEvaluateView != null) {
            mDetailEvaluateView.initEvaluateTab(resConfig, mItemRateList);
            mDetailEvaluateView.setEvaluateContext(resConfig, data, tabPosition, null);
            mDetailEvaluateView.changeEvaluateTab(0, true, mItemRateList);
        }
    }

    /**
     * 处理获取评价请求返回的结果
     */
    private void onHandleRequestGetItemRatesSuccess(PaginationItemRates data, int tabPosition) {
        if (mItemRateList != null) {
            ItemRateInfo itemRateInfo = getItemRateInfo(tabPosition);
            if (itemRateInfo != null) {
                itemRateInfo.request_loading = false;
                itemRateInfo.request_first = false;

                ArrayList<ItemRates> itemrates = itemRateInfo.mRatesList;
                if (itemrates != null) {
                    ItemRates[] rates = null;
                    if (data != null) {
                        rates = data.getItemRates();
                    }
                    if (rates == null || rates.length <= 0) {
                    } else {
                        // 把评价内容放入队列中
                        itemrates.addAll(Arrays.asList(data.getItemRates()));
                        // 为下一页的请求做准备
                        itemRateInfo.currentPageNum++;
                    }
                }
            }

            if (mDetailEvaluateView != null) {
                mDetailEvaluateView.setEvaluateContext(resConfig, data, tabPosition, itemRateInfo);
                mDetailEvaluateView.updateCommentPageView(tabPosition, true);
            }
        }
    }

    /**
     * 处理获取评价请求返回的结果, 错误
     * @param tabPosition
     */
    public void onHandleRequestGetItemRatesError(int tabPosition) {
        if (mDetailEvaluateView != null) {
            mDetailEvaluateView.updateCommentPageView(tabPosition, false);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDetailEvaluateView != null && mDetailEvaluateView.isCommentPageViewShow()) {
            mDetailEvaluateView.setCommentPageViewVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        onRemoveKeepedActivity(DetailEvaluateActivity.class.getName());
        super.onDestroy();
    }
}
