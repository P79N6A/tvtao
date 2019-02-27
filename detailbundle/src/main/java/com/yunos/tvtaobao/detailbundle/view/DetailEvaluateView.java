package com.yunos.tvtaobao.detailbundle.view;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.AdapterView.OnItemClickListener;
import com.yunos.tv.app.widget.AdapterView.OnItemSelectedListener;
import com.yunos.tv.app.widget.FrameLayout.LayoutParams;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.dialog.util.SnapshotUtil;
import com.yunos.tvtaobao.biz.dialog.util.SnapshotUtil.OnFronstedGlassSreenDoneListener;
import com.yunos.tvtaobao.biz.request.bo.ItemRates;
import com.yunos.tvtaobao.biz.request.bo.PaginationItemRates;
import com.yunos.tvtaobao.detailbundle.R;
import com.yunos.tvtaobao.detailbundle.activity.DetailEvaluateActivity;
import com.yunos.tvtaobao.detailbundle.adapter.DetailEvaluateAdapter;
import com.yunos.tvtaobao.detailbundle.bean.ItemRateInfo;
import com.yunos.tvtaobao.detailbundle.resconfig.IResConfig;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 评价的UI
 */
public class DetailEvaluateView {

    private final String TAG = "DetailEvaluateView";

    private final int[] mTabLayoutId = { R.id.ytm_detail_goods_evalute_tab_layout1,
            R.id.ytm_detail_goods_evalute_tab_layout2, R.id.ytm_detail_goods_evalute_tab_layout3,
            R.id.ytm_detail_goods_evalute_tab_layout4 };

    private WeakReference<Activity> mDetailActivityReference;

    // 评价模块的父VIEW
    private InnerGroupFocusRelativeLayout mGoodsEvaluateLayout;

    // 评价TAB项
    private DetailCommentTabView mDetailCommentTabView;

    // 评价内容
    private InnerFocusFrameLayout mInnerFocusFrameLayout;

    // 评价
    private String mEvaluateTitleDocuments;

    // 好评
    private String mFeedGoodDocuments;

    // 中评
    private String mFeedNormalDocuments;

    // 差评
    private String mFeedBadDocuments;

    // 有图
    private String mFeedHasPicDocuments;

    // 全部
    private String mFeedAllDocuments;

    // 分割线的高度
    private int mDividerhight;

    // TAB 切换的监听
    private onChangeTabListen mOnChangeTabListen;

    // 评价内容的点击
    private OnEvaluateItemClickListener mOnEvaluateItemClickListener;

    // 评价内容的选择监听
    private OnEvaluateItemSelectedListener mOnEvaluateItemSelectedListener;

    private Rect mTabBackgroudRect;

    // 评价的浮层
    private CommentPageView mCommentPageView;

    // 是否设置过评价总数
    private boolean mSetEvaluateTab;

    // 评价内容获得焦点
    private boolean mInnerGainFocus;

    // 毛玻璃处理完成
    private OnFronstedGlassSreenDoneListener screenShotListener;

    private int mListViewHeight;

    private DetailFocusPositionManager mFocusPositionManager;

    public DetailEvaluateView(WeakReference<Activity> mBaseActivityRef) {
        mDetailActivityReference = mBaseActivityRef;
        mSetEvaluateTab = false;
        mInnerGainFocus = false;
        onInitEvaluateView();
        screenShotListener = new OnFronstedGlassSreenDoneListener() {

            @Override
            public void onFronstedGlassSreenDone(Bitmap bmp) {
                if (mFocusPositionManager != null) {
                    if ((bmp != null) && (!bmp.isRecycled())) {
                        mFocusPositionManager.setBackgroundDrawable(new BitmapDrawable(bmp));
                    }
                }
            }
        };
        SnapshotUtil.getFronstedSreenShot(mDetailActivityReference, 5, 0, screenShotListener);

    }

    /**
     * 初始化DetailEvaluateView
     */
    private void onInitEvaluateView() {
        if (mDetailActivityReference != null && mDetailActivityReference.get() != null) {
            DetailEvaluateActivity mDetailEvaluateActivity = (DetailEvaluateActivity) mDetailActivityReference.get();

            mListViewHeight = mDetailEvaluateActivity.getResources().getDimensionPixelSize(R.dimen.dp_585);

            mFocusPositionManager = (DetailFocusPositionManager) mDetailEvaluateActivity
                    .findViewById(R.id.detail_evaluate_main);
            mGoodsEvaluateLayout = (InnerGroupFocusRelativeLayout) mDetailEvaluateActivity
                    .findViewById(R.id.detail_evaluate_layout);
            mGoodsEvaluateLayout.setInnerSelectedListener(new ItemSelectedListener() {

                @Override
                public void onItemSelected(View v, int position, boolean isSelected, View view) {
                    if (v != null && v instanceof InnerFocusFrameLayout) {
                        mInnerGainFocus = isSelected;
                        innerFocusFrameLayoutFocusChange(isSelected);
                    } else {
                        mInnerGainFocus = false;
                    }
                }
            });
            mGoodsEvaluateLayout.setAutoSearchFocus(false);

            mCommentPageView = new CommentPageView(mDetailEvaluateActivity);
            mDetailEvaluateActivity.addContentView(mCommentPageView, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
            mCommentPageView.setVisibility(View.GONE);

            mDividerhight = mDetailEvaluateActivity.getResources().getDimensionPixelSize(R.dimen.dp_2);

            mEvaluateTitleDocuments = mDetailEvaluateActivity.getString(R.string.ytsdk_detail_evaluate_name);
            mFeedGoodDocuments = mDetailEvaluateActivity.getString(R.string.ytsdk_good_comment);
            mFeedNormalDocuments = mDetailEvaluateActivity.getString(R.string.ytsdk_normal_comment);
            mFeedBadDocuments = mDetailEvaluateActivity.getString(R.string.ytsdk_bad_comment);
            mFeedHasPicDocuments = mDetailEvaluateActivity.getString(R.string.ytsdk_haspic_comment);
            mFeedAllDocuments = mDetailEvaluateActivity.getString(R.string.ytsdk_all_comment);

            // 评价模块的父VIEW
            mDetailCommentTabView = (DetailCommentTabView) mDetailEvaluateActivity.findViewById(R.id.evaluate_tab);

            // 评价内容
            mInnerFocusFrameLayout = (InnerFocusFrameLayout) mDetailEvaluateActivity
                    .findViewById(R.id.evaluate_context_framelayout);
            mTabBackgroudRect = new Rect();
            mTabBackgroudRect.setEmpty();
            mTabBackgroudRect.right = mDetailEvaluateActivity.getResources().getDimensionPixelSize(R.dimen.dp_12);
            mTabBackgroudRect.bottom = -1;
        }
    }

    /**
     * 当评价内容区获得焦点和失去焦点后的操作
     * @param hasFocus
     */
    private void innerFocusFrameLayoutFocusChange(boolean hasFocus) {
        if (mInnerFocusFrameLayout != null) {
            int count = mInnerFocusFrameLayout.getChildCount();
            for (int index = 0; index < count; index++) {
                View view = mInnerFocusFrameLayout.getChildAt(index);
                if (view != null) {
                    if (view.getVisibility() != View.VISIBLE) {
                        continue;
                    }
                    if (view instanceof DetailListView) {
                        DetailListView listView = (DetailListView) view;
                        int selectpos = -1;
                        if (hasFocus) {
                            selectpos = listView.getSelectedItemPosition();
                        }
                        changeListViewBackgroud(listView, selectpos);
                    }
                }
            }
        }
    }

    /**
     * 改变listview的背景色
     * @param listView
     * @param position
     */
    private void changeListViewBackgroud(DetailListView listView, int position) {
        if (listView != null && mInnerFocusFrameLayout != null) {
            int firstVisiblePosition = listView.getFirstVisiblePosition();
            int lastVisiblePosition = listView.getLastVisiblePosition() + 1;
            for (int index = firstVisiblePosition; index < lastVisiblePosition; index++) {
                View view = listView.getChildAt(index - firstVisiblePosition);
                if (view != null && view instanceof DetailEvaluatecItemView) {
                    DetailEvaluatecItemView detailEvaluatecItemView = (DetailEvaluatecItemView) view;
                    detailEvaluatecItemView.showMark(true);
                    if (position == index && mInnerGainFocus) {
                        detailEvaluatecItemView.showMark(false);
                    }
                    AppDebug.i(TAG, "changeListViewBackgroud --> setSelect--> view = " + view + "; index = " + index
                            + "; mInnerGainFocus = " + mInnerGainFocus + "; position = " + position
                            + "; getShowMark = " + detailEvaluatecItemView.getShowMark());
                }
            }
        }
    }

    /**
     * 初始化评价的有关VIEW
     * @param resConfig
     */
    public void initEvaluateTab(IResConfig resConfig, final ArrayList<ItemRateInfo> itemRatelist) {
        int tabtotalCount = 4;
        ArrayList<String> documents = new ArrayList<String>();// 评论tab项文本列表
        documents.clear();
        tabtotalCount = itemRatelist.size();
        if (tabtotalCount == 2) {
            documents.add(mFeedAllDocuments);
            documents.add(mFeedHasPicDocuments);
        } else if (tabtotalCount == 4) {
            documents.add(mFeedGoodDocuments);
            documents.add(mFeedNormalDocuments);
            documents.add(mFeedBadDocuments);
            documents.add(mFeedHasPicDocuments);
        } else {
            // 如果不是2种类型或者4种类型的评价，那么直接退出
            return;
        }

        int size = Math.min(mTabLayoutId.length, tabtotalCount);

        AppDebug.i(TAG, "initEvaluateTab --> tabtotalCount = " + tabtotalCount + "; documents = " + documents
                + "; size = " + size + ";mDetailCommentTabView = " + mDetailCommentTabView);

        if (mDetailCommentTabView != null && documents != null) {// 画tab view
            for (int index = 0; index < size; index++) {
                DetailEvaluatecItemView view = (DetailEvaluatecItemView) mDetailCommentTabView
                        .findViewById(mTabLayoutId[index]);
                AppDebug.i(TAG, "initEvaluateTab --> view = " + view);
                if (view != null) {
                    TextView tabevaluatedes = (TextView) view.findViewById(R.id.evaluate_tab_evaluatedes);
                    String document = documents.get(index);
                    if (!TextUtils.isEmpty(document)) {
                        tabevaluatedes.setText(document);
                    }
                    view.setVisibility(View.VISIBLE);
                    view.setFocusable(true);
                    if (index < size - 1) {
                        // 最后一个不画分割线
                        view.setDividerLeftRightSpace(0);
                        view.setDividerResId(R.color.ytsdk_detail_divider_color, mDividerhight);
                    }
                    TextView tabevaluatecount = (TextView) view.findViewById(R.id.evaluate_tab_evaluatecount);
                    if (tabevaluatecount != null) {
                        tabevaluatecount.setText("(" + 0 + ")");
                    }

                    AppDebug.i(TAG, "initEvaluateTab --> tabevaluatedes = " + tabevaluatedes
                            + "; documents.get(index) = " + documents.get(index));
                }
                ItemRateInfo itemRateInfo = itemRatelist.get(index);
                if (mInnerFocusFrameLayout != null && itemRateInfo != null) {

                    // 初始化监听
                    initListViewListen(itemRateInfo);

                    DetailEvaluateAdapter detailEvaluateAdapter = itemRateInfo.mEvaluateAdapter;
                    if (detailEvaluateAdapter != null) {
                        detailEvaluateAdapter.setItemRatesList(itemRateInfo.mRatesList);
                        if (index >= size - 1) {
                            // 最后一项为有图的类型
                            detailEvaluateAdapter.setPicType(true);
                        } else {
                            detailEvaluateAdapter.setPicType(false);
                        }
                    }

                    FrameLayout.LayoutParams contanerLp = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT, mListViewHeight);
                    contanerLp.setMargins(0, 0, 0, 0);
                    // 居中 
                    contanerLp.gravity = Gravity.CENTER_HORIZONTAL;

                    DetailListView listView = itemRateInfo.mEvaluateListView;

                    if (listView != null) {
                        // 把listview添加到mInnerFocusFrameLayout中 
                        mInnerFocusFrameLayout.addView(listView, contanerLp);
                        listView.setVisibility(View.GONE);
                        AppDebug.i(TAG, "initEvaluateTab --> addVIEW = " + mInnerFocusFrameLayout + "; listView = "
                                + listView);
                    }
                }

            }

            // 设置第一个获得焦点的view
            mDetailCommentTabView.setFirstFocusView(mDetailCommentTabView.findViewById(mTabLayoutId[0]));
            //            mDetailCommentTabView.findViewById(mTabLayoutId[0]).requestFocus();
            // 设置选中监听
            mDetailCommentTabView.setOnInnerItemSelectedListener(new DetailCommentTabView.OnInnerItemSelectedListener() {

                @Override
                public void onInnerItemSelected(View view, boolean isSelected, boolean fatherViewselected,
                                                View parentView) {

                    AppDebug.i(TAG, "initEvaluateTab --> onInnerItemSelected -->  view = " + view + "; isSelected = "
                            + isSelected);

                    if (view != null) {
                        int resId = view.getId();
                        int tabPos = 0;
                        for (int index = 0; index < mTabLayoutId.length; index++) {
                            if (resId == mTabLayoutId[index]) {
                                tabPos = index;
                                break;
                            }
                        }
                        AppDebug.i(TAG, "initEvaluateTab --> onInnerItemSelected -->  tabPos = " + tabPos
                                + "; isSelected = " + isSelected);

                        if (tabPos < mTabLayoutId.length) {

                            ItemRateInfo itemRateInfo = getItemRateInfo(tabPos, itemRatelist);
                            if (itemRateInfo != null) {
                                itemRateInfo.isGainFoucs = fatherViewselected;
                            }
                            // 改变背景
                            changeEvaluateTab(tabPos, isSelected, itemRatelist);
                        }

                        if (!fatherViewselected) {
                            // 如果父类失去焦点
                            evaluateTabLoseFoucs(tabPos);
                        } else {
                            // 父类得到焦点
                            evaluateTabGainFoucs(tabPos);
                        }
                    }
                }
            });
        }
    }

    /**
     * 设置评价的TAB内容
     * @param data
     * @param tabPosition
     */
    public void setEvaluateContext(IResConfig resConfig, PaginationItemRates data, int tabPosition,
                                   ItemRateInfo itemRateInfo) {
        AppDebug.i(TAG, "setEvaluateContext --> data = " + data + "; tabPosition = " + tabPosition
                + "; itemRateInfo = " + itemRateInfo);

        // 获得评价的类型种类数
        int totalCount = getRateTypeCount(resConfig);

        if (data != null && mGoodsEvaluateLayout != null) {

            if (!mSetEvaluateTab && tabPosition == DetailBuilder.LAYOUT_INDEX_F1) {
                // 设置总的评价数
                TextView title = (TextView) mGoodsEvaluateLayout.findViewById(R.id.detail_goods_evaluate_title);
                if (title != null && !TextUtils.isEmpty(data.getFeedAllCount())) {
                    title.setText(mEvaluateTitleDocuments + "(" + data.getFeedAllCount() + ")");
                }

                // 设置更类型评价的数量
                int size = Math.min(mTabLayoutId.length, totalCount);
                for (int index = 0; index < size; index++) {
                    View view = mDetailCommentTabView.findViewById(mTabLayoutId[index]);
                    if (view != null) {
                        TextView tabevaluatecount = (TextView) view.findViewById(R.id.evaluate_tab_evaluatecount);
                        if (tabevaluatecount != null) {
                            String count = getFeedCount(data, index, totalCount);
                            if (!TextUtils.isEmpty(count)) {
                                tabevaluatecount.setText("(" + count + ")");
                                view.setVisibility(View.VISIBLE);
                            } else {
                                view.setVisibility(View.GONE);
                            }
                        }
                    }
                }

                mSetEvaluateTab = true;
            }

            if (itemRateInfo != null) {
                String count = getFeedCount(data, itemRateInfo.tabNumber, totalCount);
                if (!TextUtils.isEmpty(count)) {
                    try {
                        int feed_count = Integer.parseInt(count);

                        itemRateInfo.total_count = feed_count;

                    } catch (Exception e) {
                        AppDebug.i(TAG, "setEvaluateContext --> count = " + count);
                    }
                }
            }

        }

        if (itemRateInfo != null) {
            // 如果当前的listView 是显示，那么通知适配器，并显示VIEW  
            if (itemRateInfo.mEvaluateAdapter != null) {
                itemRateInfo.mEvaluateAdapter.notifyDataSetChanged();
            }
            if (itemRateInfo.mEvaluateListView != null && itemRateInfo.isShow) {
                itemRateInfo.mEvaluateListView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 切换评价类型adap
     * @param tabPosition
     */
    public void changeEvaluateTab(int tabPosition, boolean isSelected, ArrayList<ItemRateInfo> itemRateList) {
        AppDebug.i(TAG, "changeEvaluateTab --> tabPosition = " + tabPosition + "; itemRateList = " + itemRateList);

        if (isSelected) {
            checkListViewShow(tabPosition, itemRateList);
        }
        if (mOnChangeTabListen != null) {
            mOnChangeTabListen.onChangeTab(tabPosition, isSelected);
        }
        ItemRateInfo itemRateInfo = getItemRateInfo(tabPosition, itemRateList);
        if (itemRateInfo != null && !itemRateInfo.isGainFoucs) {
            evaluateTabLoseFoucs(tabPosition);
        } else {
            changeEvaluateTabBackgroud(tabPosition, isSelected);
        }
    }

    /**
     * 获取 ItemRateInfo 信息
     * @param tabPosition
     * @param itemRateList
     * @return
     */
    private ItemRateInfo getItemRateInfo(int tabPosition, ArrayList<ItemRateInfo> itemRateList) {
        if (itemRateList != null) {
            int size = itemRateList.size();
            if (tabPosition >= 0 && tabPosition < size) {
                return itemRateList.get(tabPosition);
            }
        }
        return null;
    }

    /**
     * 改变评价TAB的背景颜色
     * @param resPos
     */
    private void changeEvaluateTabBackgroud(int resPos, boolean isSelected) {
        int length = mTabLayoutId.length;
        if (resPos >= length || resPos < 0) {
            return;
        }
        if (mDetailCommentTabView != null) {
            View view = mDetailCommentTabView.findViewById(mTabLayoutId[resPos]);
            AppDebug.i(TAG, "changeEvaluateTabBackgroud --> resPos = " + resPos + "; isSelected = " + isSelected
                    + "; view = " + view);
            if (view != null) {
                DetailEvaluatecItemView detailEvaluatecItemView = (DetailEvaluatecItemView) view;
                detailEvaluatecItemView.setShowDrawBackgroud(false);
                TextView tabevaluatedes = (TextView) detailEvaluatecItemView
                        .findViewById(R.id.evaluate_tab_evaluatedes);
                TextView tabevaluatecount = (TextView) detailEvaluatecItemView
                        .findViewById(R.id.evaluate_tab_evaluatecount);
                int colorValueResId = R.color.ytsdk_detail_evaluate_tab_bg_color;
                int color = detailEvaluatecItemView.getResources().getColor(R.color.ytsdk_detail_evaluate_font_color);
                if (isSelected) {
                    colorValueResId = R.color.ytsdk_detail_tab_gainfocus_color;
                    color = detailEvaluatecItemView.getResources().getColor(R.color.ytm_white);
                }
                view.setBackgroundResource(colorValueResId);
                tabevaluatedes.setTextColor(color);
                tabevaluatecount.setTextColor(color);
            }
        }
    }

    /**
     * 改变评价TAB的整个失去焦点
     * @param tabPos
     */
    private void evaluateTabLoseFoucs(int tabPos) {
        int length = mTabLayoutId.length;
        if (tabPos >= length || tabPos < 0) {
            return;
        }
        if (mDetailCommentTabView != null) {
            for (int index = 0; index < length; index++) {
                changeEvaluateTabBackgroud(index, false);
            }
            View view = mDetailCommentTabView.findViewById(mTabLayoutId[tabPos]);
            if (view != null) {
                DetailEvaluatecItemView detailEvaluatecItemView = (DetailEvaluatecItemView) view;
                int colorValue = detailEvaluatecItemView.getResources().getColor(
                        R.color.ytsdk_detail_tab_notselect_color);
                TextView tabevaluatedes = (TextView) view.findViewById(R.id.evaluate_tab_evaluatedes);
                if (tabevaluatedes != null) {
                    tabevaluatedes.setTextColor(colorValue);
                }
                TextView tabevaluatecount = (TextView) view.findViewById(R.id.evaluate_tab_evaluatecount);
                if (tabevaluatecount != null) {
                    tabevaluatecount.setTextColor(colorValue);
                }
                detailEvaluatecItemView.setBackgroudColor(colorValue, mTabBackgroudRect);
                detailEvaluatecItemView.setShowDrawBackgroud(true);
            }
        }
    }

    /**
     * TAB的父VIEW 得到焦点
     * @param tabPos
     */
    private void evaluateTabGainFoucs(int tabPos) {
        int length = mTabLayoutId.length;
        if (tabPos >= length || tabPos < 0) {
            return;
        }
        if (mDetailCommentTabView != null) {
            for (int index = 0; index < length; index++) {
                View view = mDetailCommentTabView.findViewById(mTabLayoutId[index]);
                if (view != null) {
                    DetailEvaluatecItemView detailEvaluatecItemView = (DetailEvaluatecItemView) view;
                    int colorValue = detailEvaluatecItemView.getResources().getColor(
                            R.color.ytsdk_detail_evaluate_font_color);
                    if (tabPos == index) {
                        colorValue = detailEvaluatecItemView.getResources().getColor(R.color.ytm_white);
                    }
                    TextView tabevaluatedes = (TextView) view.findViewById(R.id.evaluate_tab_evaluatedes);
                    if (tabevaluatedes != null) {
                        tabevaluatedes.setTextColor(colorValue);
                    }
                    TextView tabevaluatecount = (TextView) view.findViewById(R.id.evaluate_tab_evaluatecount);
                    if (tabevaluatecount != null) {
                        tabevaluatecount.setTextColor(colorValue);
                    }
                    detailEvaluatecItemView.setShowDrawBackgroud(false);
                }
            }
        }
    }

    /**
     * @param tabPosition
     * @param itemRateList
     */
    private void checkListViewShow(int tabPosition, ArrayList<ItemRateInfo> itemRateList) {
        if (itemRateList == null) {
            return;
        }
        int size = itemRateList.size();
        AppDebug.i(TAG, "checkListViewShow --> tabPosition = " + tabPosition + "; itemRateList = " + itemRateList);
        if (tabPosition < 0 || tabPosition >= size) {
            return;
        }
        for (int index = 0; index < size; index++) {
            ItemRateInfo itemRateInfo = itemRateList.get(index);
            if (itemRateInfo != null && itemRateInfo.mEvaluateListView != null) {
                itemRateInfo.mEvaluateListView.setVisibility(View.GONE);
            }
        }
        ItemRateInfo itemRateInfo = itemRateList.get(tabPosition);
        if (itemRateInfo != null && itemRateInfo.mEvaluateListView != null) {
            itemRateInfo.mEvaluateListView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置ListView的监听
     * @param itemRateInfo
     */
    private void initListViewListen(final ItemRateInfo itemRateInfo) {
        if (itemRateInfo != null && itemRateInfo.mEvaluateListView != null) {
            final DetailListView listView = itemRateInfo.mEvaluateListView;
            // 设置选中监听
            listView.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    AppDebug.i(TAG, "initListViewListen --> onItemSelected --> position = " + position + "; view = "
                            + view);
                    if (mOnEvaluateItemSelectedListener != null) {
                        mOnEvaluateItemSelectedListener.onEvaluateItemSelected(parent, view, itemRateInfo.tabNumber,
                                position, id);
                        changeListViewBackgroud(listView, position);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    AppDebug.i(TAG, "initListViewListen --> onNothingSelected --> parent = " + parent);
                }
            });

            // 如果允许点击，那么执行点击事件
            final int tabNumBer = itemRateInfo.tabNumber;
            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AppDebug.i(TAG, "initListViewListen --> onItemClick  view  = " + view + "; position = " + position);
                    if (mOnEvaluateItemClickListener != null) {
                        mOnEvaluateItemClickListener.onEvaluateItemClick(parent, view, tabNumBer, position, id);
                    }
                }
            });
        }
    }

    /**
     * 获取评价数
     * @param data
     * @param totalCount
     * @return
     */
    private String getFeedCount(PaginationItemRates data, int index, int totalCount) {
        String count = "";
        if (totalCount == 2) {
            // 这是天猫商品
            switch (index) {
                case 0:
                    count = data.getFeedAllCount();
                    break;
                case 1:
                    count = data.getFeedPicCount();
                    try {
                        int count_int = Integer.parseInt(count);
                        if (count_int <= 0) {
                            // 如果有图的评价为0；呢么不显示评价的TAB
                            count = "";
                        }
                    } catch (Exception e) {
                    }

                    break;
                default:
                    count = data.getFeedAllCount();
                    break;
            }
        } else {
            // 淘宝的商品
            switch (index) {
                case 0:
                    count = data.getFeedGoodCount();
                    break;
                case 1:
                    count = data.getFeedNormalCount();
                    break;
                case 2:
                    count = data.getFeedBadCount();
                    break;
                case 3:
                    count = data.getFeedPicCount();
                    try {
                        int count_int = Integer.parseInt(count);
                        if (count_int <= 0) {
                            // 如果有图的评价为0；呢么不显示评价的TAB
                            count = "";
                        }
                    } catch (Exception e) {
                    }
                    break;

                default:
                    count = data.getFeedAllCount();
                    break;
            }
        }

        return count;
    }

    /**
     * 获得评价的类型种类数
     * @return
     */
    private int getRateTypeCount(IResConfig resConfig) {
        int totalCount = 4;
        if (IResConfig.GoodsType.TMALL == resConfig.getGoodsType()) {
            totalCount = 2;
        } else {
            totalCount = 4;
        }
        return totalCount;
    }

    /**
     * 设置浮层的显示状态
     * @param visibility
     */
    public void setCommentPageViewVisibility(int visibility) {
        if (mCommentPageView != null) {
            mCommentPageView.setVisibility(visibility);
            Drawable d = mFocusPositionManager.getBackground();
            if (d != null) {
                mCommentPageView.setBackgroundDrawable(d);
            }
            if (visibility == View.VISIBLE) {
                mCommentPageView.requestFocus();
            }
        }
    }

    /**
     * 浮层是否显示
     * @return
     */
    public boolean isCommentPageViewShow() {
        if (mCommentPageView != null && mCommentPageView.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    /**
     * 浮层的数据
     * @param list
     * @param tabnumber
     * @param position
     */
    public void setCommentPageViewData(ArrayList<ItemRates> list, int tabnumber, int position, int totalType) {
        if (mCommentPageView != null) {
            mCommentPageView.initItemRatesList(list, position, tabnumber, totalType);
        }
    }

    /**
     * 设置评价页面的显示状态
     * @param visibility
     */
    public void setDetailEvaluateViewVisibility(int visibility) {
        if (mGoodsEvaluateLayout != null) {
            mGoodsEvaluateLayout.setVisibility(visibility);
        }
    }

    /**
     * 设置评论区域聚焦监听
     * @param l
     */
    public void setOnEvaluateFocusListener(OnFocusChangeListener l) {
        if (mGoodsEvaluateLayout != null && l != null) {
            mGoodsEvaluateLayout.setOnFocusChangeListener(l);
        }
    }

    /**
     * TAB 切换的监听
     * @param l
     */
    public void setChangeTabListen(onChangeTabListen l) {
        mOnChangeTabListen = l;
    }

    /**
     * 评价内容单击的监听
     * @param l
     */
    public void setEvaluateItemClickListener(OnEvaluateItemClickListener l) {
        mOnEvaluateItemClickListener = l;
    }

    /**
     * 评价内容选择的监听
     * @param l
     */
    public void setEvaluateItemSelectedListener(OnEvaluateItemSelectedListener l) {
        mOnEvaluateItemSelectedListener = l;
    }

    /**
     * 设置浮层的监听
     * @param l
     */
    public void setCommentPageViewListListener(CommentPageView.OnItemRatesListListener l) {
        if (mCommentPageView != null) {
            mCommentPageView.setOnItemRatesListListener(l);
        }
    }

    /**
     * 更新浮层界面
     * @param tabnumber
     * @param success
     */
    public void updateCommentPageView(int tabnumber, boolean success) {
        if (mCommentPageView != null && isCommentPageViewShow()) {
            AppDebug.i(TAG, "updateCommentPageView --> tabnumber = " + tabnumber + "; success = " + success);
            mCommentPageView.updateView(tabnumber, success);
        }
    }

    public interface onChangeTabListen {

        public void onChangeTab(int tabPosition, boolean select);
    }

    public interface OnEvaluateItemClickListener {

        public void onEvaluateItemClick(AdapterView<?> parent, View view, int tabnumber, int position, long id);
    }

    public interface OnEvaluateItemSelectedListener {

        public void onEvaluateItemSelected(AdapterView<?> parent, View view, int tabnumber, int position, long id);
    }
}
