/**
 * $
 * PROJECT NAME: CommentImagePage
 * PACKAGE NAME: com.example.commentimagepage
 * FILE NAME: CommentPageView.java
 * CREATED TIME: 2015年5月12日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.detailbundle.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.display.RoundedBitmapDisplayer;
import com.tvlife.imageloader.core.listener.SimpleImageLoadingListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.SystemConfig;
import com.yunos.tvtaobao.biz.common.DepthPageTransformer;
import com.yunos.tvtaobao.biz.request.bo.AppendedFeed;
import com.yunos.tvtaobao.biz.request.bo.ItemRates;
import com.yunos.tvtaobao.detailbundle.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2015年5月12日 上午10:50:04
 */
public class CommentPageView extends RelativeLayout {

    private static String TAG = "CommentPageView";
    private Context mContext;
    private ArrayList<ItemRates> mItemRatesAllList;// 所有的评论数据
    private ArrayList<ItemRates> mItemRatesValidList;// 含有图片的评论数据
    private ArrayList<String> mItemUrlList;// 所有图片的列表

    private ViewPager mViewPager;// 图片显示控件
    private ViewPageAdapter mViewPageAdapter; // 适配器
    private ImageView mLeftArray;// 左箭头
    private ImageView mRightArray;// 右箭头
    private ItemInfoView mItemInfoView;// 用户及商品信息
    private TextView mTextView; // 评论
    private ProgressBar mProgressBar;

    private int mViewPageMarginTop; // viewPage的上边距
    private int mItemInfoMarginTop; // 用户信息的上边距
    private int mCommentViewMarginTop; // 评论的上边距

    private long mLastTime; // 记录keyEvent上一次时间
    private long mInterval; // 时间间隔
    private int mCommentType; // 评价类型（评论中tab的位置0.1.2...）
    private int mCommentTypeSize;// 评论类型数量
    private boolean mItemListReLoading;
    private int mItemRatesAllListSize; // 所有评论列表size
    private int mValidListIndex; // 有图片评论数据的当前索引 
    private int mCurSelected; // 当前选择的图片索引
    private boolean mDataUpdating; // 是否正在更新列表
    private boolean mAllDataLoaded; // 所以数据已经加载完毕
    private boolean mReloadCanceled;

    public CommentPageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public CommentPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public CommentPageView(Context context) {
        super(context);
        mContext = context;
    }

    /**
     * 设置所有评论的数据，以及当前选择的评论
     * @param itemRatesList
     * @param curIndex
     */
    public void initItemRatesList(ArrayList<ItemRates> itemRatesList, int curIndex, int commentType, int commentTypeSize) {
        if (itemRatesList == null || itemRatesList.size() == 0) {
            setVisibility(View.GONE);
            return;
        }

        if (curIndex < 0 || curIndex >= itemRatesList.size()) {
            setVisibility(View.GONE);
            return;
        }

        mCommentType = commentType;// 评价类型
        mCommentTypeSize = commentTypeSize;

        // 当前选择是否有效
        boolean curValid = false;
        ItemRates curItemRates = itemRatesList.get(curIndex);
        if (curItemRates != null) {
            if (isHaveImageTab()) {// 只有有图tab情况下，才取追加图片
                AppendedFeed appdendedFeed = curItemRates.getAppendedFeed();
                if (curItemRates.getPicUrlList() != null && curItemRates.getPicUrlList().size() > 0) {
                    curValid = true;
                } else if (appdendedFeed != null && appdendedFeed.getAppendFeedPicPathList() != null
                        && appdendedFeed.getAppendFeedPicPathList().size() > 0) {
                    curValid = true;
                }
            } else {// 全部tab，只取评论，不取追加评论 
                if (curItemRates.getPicUrlList() != null && curItemRates.getPicUrlList().size() > 0) {
                    curValid = true;
                }
            }
        }

        if (!curValid) {
            AppDebug.e(TAG, TAG + ".initItemRatesList.curValid = false");
            setVisibility(View.GONE);
            return;
        }

        mCurSelected = 0;
        mAllDataLoaded = false;
        mReloadCanceled = false;
        mItemRatesAllList = itemRatesList;
        mItemRatesAllListSize = mItemRatesAllList.size();
        mValidListIndex = -1;

        AppDebug.v(TAG, TAG + ".initItemRatesList.mItemRatesAllList.size = " + mItemRatesAllListSize);
        // 取得有效的数据列表
        mItemRatesValidList = getItemRatesValidList(mItemRatesAllList);
        if (mItemRatesValidList == null || mItemRatesValidList.size() == 0) {
            setVisibility(View.GONE);
            return;
        }

        // 取得有效列表中默认的当前索引
        for (int i = 0; i < mItemRatesValidList.size(); i++) {
            if (mItemRatesValidList.get(i) == curItemRates) {
                mValidListIndex = i;
                break;
            }
        }

        AppDebug.v(TAG, TAG + ".initItemRatesList.mValidListIndex = " + mValidListIndex);
        if (mValidListIndex < 0) {
            setVisibility(View.GONE);
            return;
        }

        setImageUrlList();
        if (mItemUrlList != null) {
            AppDebug.v(TAG, TAG + ".initItemRatesList.mItemUrlList.size = " + mItemUrlList.size());
        }

        if (mItemUrlList == null || mItemUrlList.size() == 0) {
            AppDebug.e(TAG, TAG + ".initItemRatesList.mItemUrlList.size() == 0");
            setVisibility(View.GONE);
            return;
        }

        initView();
    }

    /**
     * 更新评论数据
     * @param commentType
     * @param success
     */
    public void updateView(int commentType, boolean success) {
        if (!success) {//数据更新失败
            mItemListReLoading = false;// 重新加载结束
            setProgressBarStatus(false);
            AppDebug.i(TAG, TAG + ".updateView.success == false ");
            return;
        }
        if (mItemRatesAllList == null || mItemRatesAllList.size() == 0) {
            mItemListReLoading = false;// 重新加载结束
            setProgressBarStatus(false);
            AppDebug.i(TAG, TAG + ".updateView.mItemRatesAllList == null ");
            return;
        }

        AppDebug.i(TAG, TAG + ".updateView.mItemRatesAllList.curSize = " + mItemRatesAllListSize + ",newSize = "
                + mItemRatesAllList.size());
        // 如果mItemRatesAllList数量没有增加，说明加载完毕了
        if (mItemRatesAllList.size() <= mItemRatesAllListSize) {
            mAllDataLoaded = true; // 所有数据均加载完毕
            mItemListReLoading = false;// 重新加载结束
            setProgressBarStatus(false);
            return;
        }
        // 更新所有列表数量
        mItemRatesAllListSize = mItemRatesAllList.size();

        if (mItemRatesValidList == null) {// 已经被初始化过，不应该为null
            mItemListReLoading = false;// 重新加载结束
            setProgressBarStatus(false);
            AppDebug.i(TAG, TAG + ".updateView.mItemRatesValidList == null ");
            return;
        }

        // 开始更新数据，此时列表左右键不能操作
        mDataUpdating = true;

        ArrayList<ItemRates> newItemRatesValidList = getItemRatesValidList(mItemRatesAllList);
        if (newItemRatesValidList == null || newItemRatesValidList.size() == 0) {
            mItemListReLoading = false;// 重新加载结束
            setProgressBarStatus(false);
            AppDebug.i(TAG, TAG + ".updateView.newItemRatesValidList == null ");
            return;
        }

        if (Config.isDebug()) {
            AppDebug.i(TAG, TAG + ".updateView.mItemRatesAllListSize = " + mItemRatesAllListSize
                    + ".newItemRatesValidList.size = " + newItemRatesValidList.size() + ".mItemRatesValidList.size = "
                    + mItemRatesValidList.size());
        }

        // 如果带图片的评论没有增加，并且重新请求没取消，则再次请求
        if (newItemRatesValidList.size() <= mItemRatesValidList.size() && !mReloadCanceled) {
            reloadNextPagerates();
            mDataUpdating = false;
            return;
        }

        // 添加新的图片数据到列表中
        mItemRatesValidList.clear();
        mItemRatesValidList.addAll(newItemRatesValidList);

        // 重新更新图片列表
        setImageUrlList();
        if (mItemUrlList != null) {
            AppDebug.v(TAG, TAG + ".initItemRatesList.mItemUrlList.size = " + mItemUrlList.size());
        }
        // 更新箭头状态
        setArrayStatus(mCurSelected);
        if (mViewPageAdapter != null) {
            mViewPageAdapter.notifyDataSetChanged();
        }

        mDataUpdating = false;
        mItemListReLoading = false;// 重新加载结束
        setProgressBarStatus(false);

        mTextView.requestFocus();
    }

    /**
     * 获取有图片商品列表
     * @param itemRatesList
     */
    private ArrayList<ItemRates> getItemRatesValidList(ArrayList<ItemRates> itemRatesList) {
        ArrayList<ItemRates> newItemRatesValidList = new ArrayList<ItemRates>();
        for (int i = 0; i < itemRatesList.size(); i++) {
            ItemRates itemRates = itemRatesList.get(i);
            if (itemRates != null) {
                boolean bValid = false;
                if (isHaveImageTab()) {// 有图tab
                    AppendedFeed appdendedFeed = itemRates.getAppendedFeed();
                    if (itemRates.getPicUrlList() != null && itemRates.getPicUrlList().size() > 0) {
                        bValid = true;
                    } else if (appdendedFeed != null && appdendedFeed.getAppendFeedPicPathList() != null
                            && appdendedFeed.getAppendFeedPicPathList().size() > 0) {// 如果评论没有，就去追加找
                        bValid = true;
                    }
                } else {// 全部tab
                    if (itemRates.getPicUrlList() != null && itemRates.getPicUrlList().size() > 0) {
                        bValid = true;
                    }
                }

                if (bValid) {
                    newItemRatesValidList.add(itemRates);
                }
            }
        }

        return newItemRatesValidList;
    }

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int curSelected) {
            if (mItemRatesValidList == null) {
                return;
            }

            int validIndex = getValidItemListIndex(curSelected);
            AppDebug.v(TAG, TAG + ".onPageSelected.curSelected = " + curSelected + ".validIndex = " + validIndex
                    + ".mItemRatesValidList.size() = " + mItemRatesValidList.size());
            if (validIndex < 0 || validIndex > mItemRatesValidList.size() - 1) {
                return;
            }

            if (validIndex != mValidListIndex) {
                mValidListIndex = validIndex;
                // 当评论项发生改变时，更新评论数据
                ItemRates itemRates = mItemRatesValidList.get(validIndex);
                mItemInfoView.setItemRates(itemRates);
                setFeedBack(getFeedBackContent(itemRates));
                mTextView.requestFocus();

                // 获取所有评论列表中的索引
                int allListIndex = getAllItemListIndex(itemRates);
                if (mOnItemRatesListListener != null) {
                    mOnItemRatesListListener.onItemRatesIndexChanged(mItemRatesAllList, allListIndex);
                }

                if (Config.isDebug()) {
                    AppDebug.i(TAG, TAG + ".onPageSelected.mValidListIndex = " + mValidListIndex
                            + ".mItemRatesValidList.size() = " + mItemRatesValidList.size() + ".curSelected = "
                            + curSelected);
                }
            }

            mCurSelected = curSelected;
            setArrayStatus(curSelected);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    /**
     * 重新加载下一页数据
     */
    private void reloadNextPagerates() {
        AppDebug.v(TAG, TAG + ".reloadNextPagerates.mOnItemRatesListListener = " + mOnItemRatesListListener
                + ".mItemListReLoading = " + mItemListReLoading + ".mAllDataLoaded = " + mAllDataLoaded
                + ".mCommentTypeSize = " + mCommentTypeSize + ".mCommentType = " + mCommentType);
        // 只有在有图片的tab上，没有正在请求数据，以及所有数据没有请求完成情况下，才会继续请求下一页
        if (mOnItemRatesListListener != null && !mItemListReLoading && !mAllDataLoaded
                && mCommentTypeSize - 1 == mCommentType) {
            boolean ret = mOnItemRatesListListener.onReloadNextPageRates(mCommentType, mItemRatesAllListSize);
            if (ret) {
                mItemListReLoading = true;// 开始重新加载数据
                setProgressBarStatus(true);
            }
        }
    }

    /**
     * 初始化界面
     */
    private void initView() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        mLastTime = 0; // 记录keyEvent上一次时间
        mInterval = 400; // 时间间隔ms
        mViewPageMarginTop = (int) getResources().getDimension(R.dimen.dp_83);
        mItemInfoMarginTop = (int) getResources().getDimension(R.dimen.dp_27);
        mCommentViewMarginTop = (int) getResources().getDimension(R.dimen.dp_16);
        removeAllViews();

        mViewPager = new ViewPager(mContext);
        mViewPager.setFocusable(false);
        mViewPager.setId(R.id.comment_page_id);
        int imageHeight = (int) getResources().getDimension(R.dimen.dp_522);
        LayoutParams pagerParams = new LayoutParams(LayoutParams.MATCH_PARENT, imageHeight);
        pagerParams.leftMargin = (int) getResources().getDimension(R.dimen.dp_15);
        pagerParams.rightMargin = (int) getResources().getDimension(R.dimen.dp_15);
        pagerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        pagerParams.topMargin = mViewPageMarginTop;
        addView(mViewPager, pagerParams);
        // 自定义scroller对象
        FixedSpeedScroller mScroller = null;
        try {// 反射机制
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            mScroller = new FixedSpeedScroller(mViewPager.getContext(), new AccelerateInterpolator());
            mField.set(mViewPager, mScroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        mViewPageAdapter = new ViewPageAdapter(mContext, mItemUrlList);
        mViewPager.setAdapter(mViewPageAdapter);
        // 通过当前评论的索引，获取初始阶段图片的索引
        int curImageIndex = 0;
        for (int i = 0; i < mValidListIndex; i++) {
            ItemRates itemRates = mItemRatesValidList.get(i);
            if (itemRates != null) {
                if (isHaveImageTab()) {// 有图tab
                    AppendedFeed appdendedFeed = itemRates.getAppendedFeed();
                    if (itemRates.getPicUrlList() != null && itemRates.getPicUrlList().size() != 0) {
                        curImageIndex += itemRates.getPicUrlList().size();
                    } else if (appdendedFeed != null && appdendedFeed.getAppendFeedPicPathList() != null) {
                        curImageIndex += appdendedFeed.getAppendFeedPicPathList().size();
                    }
                } else {// 全部tab
                    if (itemRates.getPicUrlList() != null && itemRates.getPicUrlList().size() != 0) {
                        curImageIndex += itemRates.getPicUrlList().size();
                    }
                }
            }
        }
        AppDebug.v(TAG, TAG + ".initView.curImageIndex = " + curImageIndex);
        mViewPager.setCurrentItem(curImageIndex);
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        // 左箭头
        mLeftArray = new ImageView(mContext);
        LayoutParams leftParams = new LayoutParams((int) getResources().getDimension(R.dimen.dp_15),
                (int) getResources().getDimension(R.dimen.dp_22));
        leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        leftParams.addRule(RelativeLayout.CENTER_VERTICAL);
        leftParams.leftMargin = (int) getResources().getDimension(R.dimen.dp_7);
        addView(mLeftArray, leftParams);
        mLeftArray.setImageResource(R.drawable.ytm_array_left);
        mLeftArray.setFocusable(false);

        // 右箭头
        mRightArray = new ImageView(mContext);
        LayoutParams rightParams = new LayoutParams((int) getResources().getDimension(R.dimen.dp_15),
                (int) getResources().getDimension(R.dimen.dp_22));
        rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rightParams.addRule(RelativeLayout.CENTER_VERTICAL);
        rightParams.rightMargin = (int) getResources().getDimension(R.dimen.dp_7);
        addView(mRightArray, rightParams);
        mRightArray.setImageResource(R.drawable.ytm_array_right);
        mRightArray.setFocusable(false);
        // 设置箭头状态
        setArrayStatus(curImageIndex);

        // 用户和商品信息
        mItemInfoView = new ItemInfoView(mContext);
        mItemInfoView.setItemRates(mItemRatesValidList.get(mValidListIndex));
        mItemInfoView.setId(R.id.item_info_id);
        mItemInfoView.setFocusable(false);
        LayoutParams itemInfoViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        itemInfoViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        itemInfoViewParams.addRule(RelativeLayout.BELOW, mViewPager.getId());
        itemInfoViewParams.topMargin = mItemInfoMarginTop;
        addView(mItemInfoView, itemInfoViewParams);

        // 评论
        mTextView = new TextView(mContext);
        mTextView.setGravity(Gravity.CENTER);
        LayoutParams textParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        textParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textParams.addRule(RelativeLayout.BELOW, mItemInfoView.getId());
        textParams.topMargin = mCommentViewMarginTop;
        textParams.leftMargin = mCommentViewMarginTop;
        textParams.rightMargin = mCommentViewMarginTop;
        mTextView.setTextColor(getResources().getColor(android.R.color.white));
        mTextView.setTextSize((int) getResources().getDimension(R.dimen.sp_20));
        mTextView.setSingleLine();
        mTextView.setFocusable(true);
        mTextView.setEllipsize(TruncateAt.MARQUEE);
        mTextView.setMarqueeRepeatLimit(-1);// 无线循环
        addView(mTextView, textParams);
        setFeedBack(getFeedBackContent(mItemRatesValidList.get(mValidListIndex)));

        //loading界面        
        mProgressBar = new ProgressBar(mContext);
        LayoutParams progressParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        progressParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(mProgressBar, progressParams);
        setProgressBarStatus(false);

        mTextView.requestFocus();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mTextView != null) {
            mTextView.requestFocus();
        }
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * 是否处在有图的tab上
     * @return
     */
    private boolean isHaveImageTab() {
        if (mCommentType == mCommentTypeSize - 1) {
            return true;
        }
        return false;
    }

    /**
     * 获取评论内容
     * @param itemRates
     * @return
     */
    private String getFeedBackContent(ItemRates itemRates) {
        String content = null;

        if (itemRates != null) {
            if (isHaveImageTab()) {// 有图tab
                AppendedFeed appendedFeed = itemRates.getAppendedFeed();
                if (itemRates.getPicUrlList() != null && itemRates.getPicUrlList().size() > 0) {// 存在评论图片
                    content = itemRates.getFeedback();// 先取评论
                    if (TextUtils.isEmpty(content)) {// 如果评论为null，去取追加评论
                        if (appendedFeed != null) {
                            content = appendedFeed.getAppendedFeedback();
                        }
                    }
                } else if (appendedFeed != null && appendedFeed.getAppendFeedPicPathList() != null
                        && appendedFeed.getAppendFeedPicPathList().size() > 0) {// 存在追加图片
                    content = appendedFeed.getAppendedFeedback();// 先取追加评论 
                    if (TextUtils.isEmpty(content)) {
                        content = itemRates.getFeedback();// 如果追加评论为空，再去取原始评论
                    }
                }
            } else {
                if (itemRates.getPicUrlList() != null && itemRates.getPicUrlList().size() > 0) {// 存在评论图片
                    content = itemRates.getFeedback();
                }
            }
        }

        return content;
    }

    /**
     * 设置评论内容
     * @param content
     */
    private void setFeedBack(String content) {
        if (!TextUtils.isEmpty(content)) {
            mTextView.setText(content);
        } else {
            mTextView.setText("");
        }
    }

    /**
     * 设置加载框状态
     * @param show
     */
    private void setProgressBarStatus(boolean show) {
        if (mProgressBar != null) {
            if (show) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else {
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 获取加载框显示状态
     * @return
     */
    private boolean getProgressBarStatus() {
        if (mProgressBar != null) {
            if (mProgressBar.getVisibility() == View.VISIBLE) {
                return true;
            }
        }

        return false;
    }

    /**
     * 箭头状态
     * @param curSelected
     */
    private void setArrayStatus(int curSelected) {
        if (mItemUrlList == null) {
            return;
        }

        AppDebug.v(TAG,
                TAG + ".setArrayStatus.curSelected = " + curSelected + ".mItemUrlList.size() = " + mItemUrlList.size());
        if (curSelected == 0) {
            mLeftArray.setVisibility(View.INVISIBLE);
        } else {
            mLeftArray.setVisibility(View.VISIBLE);
        }

        if (curSelected == mItemUrlList.size() - 1) {
            mRightArray.setVisibility(View.INVISIBLE);
        } else {
            mRightArray.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置所有图片的链接列表
     * @return
     */
    private void setImageUrlList() {
        if (mItemRatesValidList == null || mItemRatesValidList.size() == 0) {
            return;
        }

        if (mItemUrlList == null) {
            mItemUrlList = new ArrayList<String>();
        }

        // 先将图片列表放在临时变量中
        ArrayList<String> itemUrlList = new ArrayList<String>();
        for (int i = 0; i < mItemRatesValidList.size(); i++) {
            ItemRates itemRates = mItemRatesValidList.get(i);
            if (itemRates != null) {
                if (isHaveImageTab()) {// 有图tab
                    AppendedFeed appendedFeed = itemRates.getAppendedFeed();
                    if (itemRates.getPicUrlList() != null && itemRates.getPicUrlList().size() > 0) {// 评论中没有图片，则从追加中查找图片
                        for (int j = 0; j < itemRates.getPicUrlList().size(); j++) {
                            itemUrlList.add(itemRates.getPicUrlList().get(j));
                        }
                    } else if (appendedFeed != null && appendedFeed.getAppendFeedPicPathList() != null) {
                        for (int j = 0; j < appendedFeed.getAppendFeedPicPathList().size(); j++) {
                            itemUrlList.add(appendedFeed.getAppendFeedPicPathList().get(j));
                        }
                    }
                } else {// 全部tab
                    if (itemRates.getPicUrlList() != null && itemRates.getPicUrlList().size() > 0) {
                        for (int j = 0; j < itemRates.getPicUrlList().size(); j++) {
                            itemUrlList.add(itemRates.getPicUrlList().get(j));
                        }
                    }
                }
            }
        }

        // 如果图片存在
        if (itemUrlList.size() > 0) {
            mItemUrlList.clear();
            mItemUrlList.addAll(itemUrlList);
        }
    }

    /**
     * 根据图片索引，获取有效数据列表的索引
     * @param pos
     * @return
     */
    private int getValidItemListIndex(int pos) {
        if (mItemUrlList == null || pos < 0 || pos > mItemUrlList.size() - 1) {
            return -1;
        }

        int position = pos;
        int size = 0;
        for (int i = 0; i < mItemRatesValidList.size(); i++) {
            ItemRates itemRates = mItemRatesValidList.get(i);
            if (itemRates != null) {
                AppendedFeed appendedFeed = itemRates.getAppendedFeed();
                if (isHaveImageTab()) {// 有图tab
                    if (itemRates.getPicUrlList() != null && itemRates.getPicUrlList().size() > 0) {
                        size += itemRates.getPicUrlList().size();
                    } else if (appendedFeed != null && appendedFeed.getAppendFeedPicPathList() != null) {
                        size += appendedFeed.getAppendFeedPicPathList().size();
                    }
                } else {// 全部tab
                    if (itemRates.getPicUrlList() != null && itemRates.getPicUrlList().size() > 0) {
                        size += itemRates.getPicUrlList().size();
                    }
                }

                if (size > position) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * 获取所有评论列表的索引
     * @param itemRates
     * @return
     */
    private int getAllItemListIndex(ItemRates itemRates) {
        int index = -1;
        if (itemRates != null && mItemRatesAllList != null) {
            index = mItemRatesAllList.indexOf(itemRates);
        }

        return index;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (mDataUpdating) {
                    return true;
                }
                if (mLastTime != 0) {// 限制快速滑动的速度
                    long curTime = System.currentTimeMillis();
                    AppDebug.v(TAG, TAG + ".dispatchKeyEvent.interval = " + (curTime - mLastTime));
                    if (curTime - mLastTime < mInterval) {//限制长按时 按键消息的速度，当下一个按键想个小于interval时屏蔽消息
                        return true;
                    } else {
                        mLastTime = curTime;
                    }
                } else {
                    mLastTime = System.currentTimeMillis();
                }

                mViewPager.dispatchKeyEvent(event);
                // 向右按键，且倒数第二项时，加载后面页
                if (mValidListIndex >= mItemRatesValidList.size() - 3
                        && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    AppDebug.v(TAG, TAG + ".dispatchKeyEvent.start.reloadNextPagerates");
                    mReloadCanceled = false;
                    reloadNextPagerates();
                }
            } else if ((event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_ESCAPE)
                    && getVisibility() == View.VISIBLE) {
                if (getProgressBarStatus()) {//如果加载框显示着，则显示隐藏加载框，停止继续加载
                    mReloadCanceled = true;// 重新请求取消
                    mItemListReLoading = false;
                    setProgressBarStatus(false);
                    return true;
                }

                setVisibility(View.GONE);
                return true;
            } else {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // 画背景颜色
        canvas.drawColor(getResources().getColor(R.color.ytbv_shadow_color_50));

        super.dispatchDraw(canvas);
    }

    private static class ViewPageAdapter extends PagerAdapter {

        private Context mContext;
        private ArrayList<ImageView> mViewList; // 可重用view的列表
        private ArrayList<String> mItemUrlList; // 所有图片的列表

        private ImageLoaderManager mImageLoaderManager;// 下载管理器
        private DisplayImageOptions mOptions; // 下载参数
        private Bitmap mDefaultBitmap; // 默认图片

        public ViewPageAdapter(Context context, ArrayList<String> itemUrlList) {
            mContext = context;
            mViewList = new ArrayList<ImageView>();
            mItemUrlList = itemUrlList;// 当外部List发生变化时，这里的变量会随之变化。
            mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(context);
            mOptions = new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(0)).cacheInMemory(true)
                    .cacheOnDisc(false).showImageForEmptyUri(R.drawable.ytm_comment_view_image_default)
                    .showImageOnFail(R.drawable.ytm_comment_view_image_default).bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            mDefaultBitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.ytm_comment_view_image_default);
        }

        @Override
        public int getCount() {
            if (mItemUrlList == null || mItemUrlList.size() == 0) {
                return 0;
            }
            return mItemUrlList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView view = (ImageView) object;
            if (view != null) {
                ((ViewPager) container).removeView(view);
                mViewList.add(view);
            }
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = null;
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            if (mViewList.size() > 0) {
                imageView = mViewList.remove(0);
                mViewList.clear();
            } else {
                imageView = new ImageView(mContext);
            }

            String theImageUrl = mItemUrlList.get(position);

            if (imageView != null && !TextUtils.isEmpty(theImageUrl)) {
                imageView.setScaleType(ScaleType.CENTER_INSIDE);
                String imageUrl = theImageUrl;
                if (SystemConfig.SCREEN_WIDTH > 1280) {// 1080p
                    imageUrl += "_760x760.jpg";
                } else {
                    imageUrl += "_560x560.jpg";
                }
                if (Config.isDebug()) {
                    AppDebug.i(TAG, TAG + ".ViewPageAdapter.instantiateItem.imageUrl = " + imageUrl + ". position = "
                            + position + ", mItemUrlList.size = " + mItemUrlList.size());
                }

                final ImageView theImageView = imageView;
                if (mDefaultBitmap != null) {
                    theImageView.setImageBitmap(mDefaultBitmap);
                }

                mImageLoaderManager.displayImage(imageUrl, theImageView, mOptions, new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (theImageView != null && loadedImage != null && !loadedImage.isRecycled()) {
                            theImageView.setImageBitmap(loadedImage);
                        }
                    }
                });
                container.addView(imageView, 0, params);
            }

            return imageView;
        }

        @Override
        public int getItemPosition(Object object) {
            //return POSITION_NONE;
            return super.getItemPosition(object);
        }
    }

    /**
     * 用户和商品相关的信息
     * Class Descripton.
     * @version
     * @author mi.cao
     * @data 2015年5月19日 下午8:34:04
     */
    private static class ItemInfoView extends View {

        private ItemRates mItemRates;// 用户信息

        private Bitmap mStarBitmap;// 星型图片
        private Paint mPaint;// 画笔
        private Rect mStarSrcRect;// 星型 原图的矩形范围
        private Rect mStarDstRect;// 星型绘制的位置
        private FontMetrics mFontMetrics;// 字体属性

        private String mUserNick;// 用户昵称
        private int mStarCount;// 星的数量
        private int mNickStarInterval;// 昵称与星之间的间距
        private int mStarInterval;// 星之间的间距
        private int mStarSkuInterval;// 星与sku之间的间距
        private int mSkuInterval; // sku之间的间距
        private int mSkuDateInterval; // sku与时间之间的间距

        private float mMarginleft;// 绘制的起始位置
        private float mNickTextWidth = 0;// 昵称字符的宽度
        private float mTimeTextWidth = 0;// 时间字符的宽度
        private float mSkuTextWidth = 0; // sku字符的宽度
        private float mTextBaseLine = 0; // text绘制的基线

        private int mVisibleSkuCount; //可见的sku个数，因为

        public ItemInfoView(Context context) {
            super(context);

            mPaint = new Paint();
            mPaint.setTextSize(getResources().getDimension(R.dimen.dp_20));
            mPaint.setColor(getResources().getColor(android.R.color.white));
            mFontMetrics = mPaint.getFontMetrics();

            mNickStarInterval = (int) getResources().getDimension(R.dimen.dp_7);
            mStarInterval = (int) getResources().getDimension(R.dimen.dp_1);
            mStarSkuInterval = (int) getResources().getDimension(R.dimen.dp_49);
            mSkuInterval = (int) getResources().getDimension(R.dimen.dp_21);
            mSkuDateInterval = (int) getResources().getDimension(R.dimen.dp_42);
            mNickTextWidth = 0;
            mTimeTextWidth = 0;
            mSkuTextWidth = 0;
            mTextBaseLine = 0;
        }

        public void setItemRates(ItemRates itemRates) {
            if (itemRates == null) {
                return;
            }

            mItemRates = itemRates;
            mUserNick = mItemRates.getUserNick();
            int userLevel = 1;

            if (!TextUtils.isEmpty(mItemRates.getUserStar())) {
                try {
                    userLevel = Integer.valueOf(mItemRates.getUserStar());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            //            if (Config.isDebug()) {
            //                AppDebug.v(TAG,
            //                        TAG + ".setItemRates.userLevel = " + userLevel + ".userStar = " + mItemRates.getUserStar());
            //            }

            if (userLevel <= 5) {
                mStarCount = userLevel;
                mStarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ytm_comment_view_red);
            } else if (userLevel <= 10) {
                mStarCount = userLevel - 5;
                mStarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ytm_comment_view_blue);
            } else if (userLevel <= 15) {
                mStarCount = userLevel - 10;
                mStarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ytm_comment_view_cap);
            } else {
                mStarCount = userLevel > 20 ? 5 : (userLevel - 15);
                mStarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ytm_comment_view_crown);
            }

            mStarSrcRect = new Rect(0, 0, mStarBitmap.getWidth(), mStarBitmap.getHeight());
            mStarDstRect = new Rect();
            invalidate();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            //            int height = MeasureSpec.getSize(heightMeasureSpec);
            //            if (Config.isDebug()) {
            //                AppDebug.v(TAG, TAG + ".onMeasure.width = " + width + ". height = " + height + ", width1 = "
            //                        + getWidth() + ", height1 = " + getHeight() + ", width2 = " + getMeasuredWidth()
            //                        + ", height2 = " + getMeasuredHeight());
            //            }

            // 设定view的大小
            setMeasuredDimension(width, (int) Math.ceil(mFontMetrics.bottom - mFontMetrics.top));
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            //            if (Config.isDebug()) {
            //                AppDebug.v(TAG, TAG + ".onLayout.changed = " + changed + ".left = " + left + ".top = " + top
            //                        + ".right = " + right + ".bottom = " + bottom);
            //            }

            // 技术文本基线位置
            mTextBaseLine = (float) ((getHeight() + Math.ceil(mFontMetrics.descent - mFontMetrics.ascent)) / 2 - getResources()
                    .getDimension(R.dimen.dp_6));
            super.onLayout(changed, left, top, right, bottom);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            setDrawMargin();

            //            if (Config.isDebug()) {
            //                AppDebug.v(TAG, TAG + ".onDraw. mTextBaseLine = " + mTextBaseLine + "height = " + getHeight()
            //                        + ".bitmap.width = " + mStarBitmap.getWidth() + ".bitmap.height = " + mStarBitmap.getHeight()
            //                        + ".mMarginleft = " + mMarginleft + ". getWidth() = " + getWidth() + ". mNickTextWidth = "
            //                        + mNickTextWidth + ". mSkuTextWidth = " + mSkuTextWidth + ".mTimeTextWidth = " + mTimeTextWidth);
            //            }

            // 画昵称
            if (!TextUtils.isEmpty(mUserNick)) {
                canvas.drawText(mUserNick, mMarginleft, mTextBaseLine, mPaint);
            }

            // 画星型
            mMarginleft += (mNickTextWidth + mNickStarInterval);
            mStarDstRect.left = (int) mMarginleft;
            mStarDstRect.right = mStarDstRect.left + mStarBitmap.getWidth();
            mStarDstRect.top = (getHeight() - mStarBitmap.getHeight()) / 2;
            mStarDstRect.bottom = mStarDstRect.top + mStarBitmap.getHeight();
            for (int i = 0; i < mStarCount; i++) {
                mMarginleft = mStarDstRect.right;
                canvas.drawBitmap(mStarBitmap, mStarSrcRect, mStarDstRect, mPaint);
                mStarDstRect.left += (mStarBitmap.getWidth() + mStarInterval);
                mStarDstRect.right = mStarDstRect.left + mStarBitmap.getWidth();
            }

            // 画sku
            int skuCount = 0;
            String ellipsis = "...";
            Map<String, String> skuMap = mItemRates.getSkuMap();
            if (skuMap != null && skuMap.size() > 0) {
                mMarginleft += mStarSkuInterval;
                float left = mMarginleft;
                Iterator<Entry<String, String>> iter = skuMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, String> entry = iter.next();
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                        skuCount++;
                        if (skuCount > mVisibleSkuCount) {// 当大于可显示的sku数量时，退出
                            mMarginleft = left + mPaint.measureText(ellipsis);// 画一个省略符
                            canvas.drawText(ellipsis, left, mTextBaseLine, mPaint);
                            break;
                        }

                        String skuText = key + ":" + value;
                        if (Config.isDebug()) {
                            AppDebug.v(TAG, TAG + ".onDraw.mMarginleft = " + mMarginleft + ". left = " + left);
                        }

                        mMarginleft = left + mPaint.measureText(skuText);// 向右偏移一个sku
                        canvas.drawText(skuText, left, mTextBaseLine, mPaint);
                        left = mMarginleft + mSkuInterval;// 偏移一个sku间距
                    }
                }
            }

            if (Config.isDebug()) {
                AppDebug.v(TAG, TAG + ".onDraw.skuCount = " + skuCount + ". mVisibleSkuCount = " + mVisibleSkuCount
                        + ".skuMap.size() = " + skuMap.size());
            }

            // 画时间
            if (!TextUtils.isEmpty(mItemRates.getFeedbackDate())) {
                mMarginleft += mSkuDateInterval;
                canvas.drawText(mItemRates.getFeedbackDate(), mMarginleft, mTextBaseLine, mPaint);
            }

            super.onDraw(canvas);
        }

        /**
         * 设置尺寸参数
         */
        private void setDrawMargin() {
            if (!TextUtils.isEmpty(mUserNick)) {
                mNickTextWidth = mPaint.measureText(mUserNick);
            }

            if (!TextUtils.isEmpty(mItemRates.getFeedbackDate())) {
                mTimeTextWidth = mPaint.measureText(mItemRates.getFeedbackDate());
            }

            //            if (Config.isDebug()) {
            //                AppDebug.v(TAG, TAG + ".setDrawMargin.getWidth = " + getWidth() + ".mNickTextWidth = " + mNickTextWidth
            //                        + ".mTimeTextWidth = " + mTimeTextWidth + ".mStarBitmap.getWidth() = " + mStarBitmap.getWidth()
            //                        + ".mStarCount = " + mStarCount + ".mNickStarInterval = " + mNickStarInterval
            //                        + ".mStarSkuInterval = " + mStarSkuInterval + ".mSkuDateInterval = " + mSkuDateInterval
            //                        + ".mSkuInterval = " + mSkuInterval);
            //            }

            float skuLength = getWidth()
                    - (mNickTextWidth + mTimeTextWidth + mStarBitmap.getWidth() * mStarCount + (mStarCount - 1)
                            + mNickStarInterval + mStarSkuInterval + mSkuDateInterval);

            mSkuTextWidth = 0;
            mVisibleSkuCount = 0;
            if (skuLength > 0) {
                int skuTextWidth = 0;
                Map<String, String> skuMap = mItemRates.getSkuMap();
                if (skuMap != null && skuMap.size() > 0) {// 先计算每个sku的宽度，再加上sku之间的间距
                    Iterator<Entry<String, String>> iter = skuMap.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<String, String> entry = iter.next();
                        String key = entry.getKey();
                        String value = entry.getValue();
                        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                            String skuText = key + ":" + value;
                            skuTextWidth += mPaint.measureText(skuText);
                            //                            AppDebug.v(TAG, TAG + ".setDrawMargin.skuTextWidth = " + skuTextWidth);
                            if (skuTextWidth > skuLength) {// 当超过规定长度时，舍弃该sku
                                break;
                            }
                            skuTextWidth += mSkuInterval;
                            mSkuTextWidth = skuTextWidth;
                            mVisibleSkuCount++;
                        }
                    }
                    mSkuTextWidth -= mSkuInterval;
                }

                if (Config.isDebug()) {
                    AppDebug.v(TAG, TAG + ".setDrawMargin.skuLength = " + skuLength + ".mVisibleSkuCount = "
                            + mVisibleSkuCount + "skuMap.size() = " + skuMap.size());
                }
            }

            mMarginleft = (getWidth() - (mNickTextWidth + mSkuTextWidth + mTimeTextWidth + mStarBitmap.getWidth()
                    * mStarCount + (mStarCount - 1) * mStarInterval + mNickStarInterval + mStarSkuInterval + mSkuDateInterval)) / 2;
        }
    }

    private OnItemRatesListListener mOnItemRatesListListener;

    /**
     * 设置当前查看的评论数据的监听
     * @param l
     */
    public void setOnItemRatesListListener(OnItemRatesListListener l) {
        mOnItemRatesListListener = l;
    }

    /**
     * 当前评论数据的监听接口
     * Class Descripton.
     * @version
     * @author mi.cao
     * @data 2015年5月20日 下午5:52:10
     */
    public interface OnItemRatesListListener {

        /**
         * 当前查看的评论项改变时
         * @param itemRatesAllList
         * @param curIndex
         */
        public void onItemRatesIndexChanged(ArrayList<ItemRates> itemRatesAllList, int curIndex);

        /**
         * 当需要重新加载下一页评论数据时
         */
        public boolean onReloadNextPageRates(int commentType, int curSize);
    }
}
