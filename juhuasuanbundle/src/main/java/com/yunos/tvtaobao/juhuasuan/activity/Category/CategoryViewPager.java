package com.yunos.tvtaobao.juhuasuan.activity.Category;


import android.app.Fragment;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.juhuasuan.activity.HomeActivity;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CategoryMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CountList;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;
import com.yunos.tvtaobao.juhuasuan.common.NetWorkCheck;
import com.yunos.tvtaobao.juhuasuan.common.NetWorkCheck.*;
import com.yunos.tvtaobao.juhuasuan.config.SystemConfig;
import com.yunos.tvtaobao.juhuasuan.request.MyBusinessRequest;
import com.yunos.tvtaobao.juhuasuan.request.listener.JuHomeItemsFilterRetryRequestListener;
import com.yunos.tvtaobao.juhuasuan.util.StringUtil;
import com.yunos.tvtaobao.juhuasuan.view.ViewScroller;
import com.yunos.tvtaobao.juhuasuan.view.ViewScroller.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * 商品列表的页面
 * 1.替换系统默认ViewPager的Scroller,并监听Scroll，为后续使用动画效果做准备
 * 2.首次加载只加载一页，当焦点聚焦过来的时候再更新第二页，为加快分类商品的快速切换
 * 3.里面的onLayout需要手动设置
 * 4.监听pager页并提前分批加载后面的商品数据
 * @author tim
 * @later hanqi
 */
public class CategoryViewPager extends ViewPager implements ScrollListener {

    private static final String TAG = "CategoryViewPager";
    public static final int EACH_REQUEST_GOODS_LIST_COUNT = 31; //每次网络读取商品的个数(注：最小为4页的个数即4*5=20，最大为50)
    private final int REQUEST_LIST_ADVANCE_PAGE_COUNT = 5; //提前多长时间发起下个网络请求
    private MyBusinessRequest mBusinessRequest; //数据请求
    private CategoryViewPagerAdapter mItemListViewPagerAdapter; //Page页适配器
    private int needRequestIndex = 0;
    private boolean mNoMoreGoods; //没有更多的商品数据
    private HomeActivity mActivity; //上下文件
    private CategoryMO mCategoryMO; //当前分类的信息
    private ViewScroller mScroller; //自定义用来替换默认ViewPager的scrolloer
    private boolean mNeedRefreshAllList; //是否需要刷新全部数据
    private boolean mNeedLayout; //是否需要加载
    private CategoryViewPagerListener mCategoryViewPagerListener;
    private boolean hide = false;

    private NetWorkConnectedCallBack mNetWorkConnectedCallBack = new NetWorkCheck.NetWorkConnectedCallBack() {

        @Override
        public void connected() {
            AppDebug.i(TAG, TAG + ".mNetWorkConnectedCallBack.connected mCategoryMo=" + mCategoryMO);
            List<ItemMO> itemList = mBusinessRequest.getCategoryItemList(mCategoryMO.getCid());
            boolean onlyRefreshFirstPage = true;
            if (itemList != null && itemList.size() > 0) {
                onlyRefreshFirstPage = false;
            }
            //            if (mCategoryMO.getType() == 0) { //普通商品分类
            requestGoodsList(onlyRefreshFirstPage, mCategoryMO, mCategoryViewPagerListener);
            //            }
        }
    };

    public CategoryViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
        AppDebug.i(TAG, "ItemListViewPager onLayout change=" + arg0 + " mNeedLayout=" + mNeedLayout);
        if (arg0 || mNeedLayout) {
            mNeedLayout = false;
            super.onLayout(arg0, arg1, arg2, arg3, arg4);
        }
    }

    /**
     * 这里是用android4.0版本的插件代码
     * 发现android4.2.2版本的代码会导致bug
     * 发现当按向右键，如果当前focus对象右侧还有对象，但被隐藏了，用4.2.2版本的代码会导致return true，但实际应该return false
     * 好大一个坑啊
     */
    @Override
    public boolean arrowScroll(int direction) {
        View currentFocused = findFocus();
        if (currentFocused == this)
            currentFocused = null;

        boolean handled = false;

        AppDebug.i(TAG, TAG + ".dispatchKeyEvent.arrowScroll currentFocused=" + currentFocused);
        View nextFocusedView = FocusFinder.getInstance().findNextFocus(this, currentFocused, direction);
        //        currentFocused.

        boolean nextFocusedViewInCurrentFocusedView = false;
        if (null != nextFocusedView) {

            if (null != currentFocused && currentFocused instanceof ViewGroup) {
                ViewGroup currentFocusedViewGroup = ((ViewGroup) currentFocused);
                if (currentFocusedViewGroup.getChildCount() > 0) {
                    for (int i = 0; i < currentFocusedViewGroup.getChildCount(); i++) {
                        View childView = currentFocusedViewGroup.getChildAt(i);
                        if (childView == nextFocusedView) {
                            AppDebug.i(
                                    TAG,
                                    TAG
                                            + ".dispatchKeyEvent.arrowScroll has find the nextFocused View in currentFocusedView");
                            nextFocusedViewInCurrentFocusedView = true;
                            break;
                        }
                    }
                }
            }

        }
        View nextFocused = nextFocusedViewInCurrentFocusedView ? null : nextFocusedView;

        AppDebug.i(TAG, TAG + ".dispatchKeyEvent.arrowScroll nextFocused=" + nextFocused);
        if (nextFocused != null && nextFocused != currentFocused) {
            if (direction == View.FOCUS_LEFT) {
                // If there is nothing to the left, or this is causing us to
                // jump to the right, then what we really want to do is page left.
                if (currentFocused != null && nextFocused.getLeft() >= currentFocused.getLeft()) {
                    handled = pageLeft();
                } else {
                    handled = nextFocused.requestFocus();
                }
            } else if (direction == View.FOCUS_RIGHT) {
                // If there is nothing to the right, or this is causing us to
                // jump to the left, then what we really want to do is page right.
                if (currentFocused != null && nextFocused.getLeft() <= currentFocused.getLeft()) {
                    handled = pageRight();
                } else {
                    handled = nextFocused.requestFocus();
                }
            }
        } else if (direction == FOCUS_LEFT || direction == FOCUS_BACKWARD) {
            // Trying to move left and nothing there; try to page.
            handled = pageLeft();
        } else if (direction == FOCUS_RIGHT || direction == FOCUS_FORWARD) {
            // Trying to move right and nothing there; try to page.
            handled = pageRight();
        }
        if (handled) {
            playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
        }
        AppDebug.i(TAG, TAG + ".arrowScroll handled=" + handled);
        return handled;
    }

    boolean pageLeft() {
        if (getCurrentItem() > 0) {
            setCurrentItem(getCurrentItem() - 1, true);
            return true;
        }
        return false;
    }

    boolean pageRight() {
        if (getAdapter() != null && getCurrentItem() < (getAdapter().getCount() - 1)) {
            setCurrentItem(getCurrentItem() + 1, true);
            return true;
        }
        return false;
    }

    /**
     * 设置当前分类是否为隐藏
     * @param isHide
     */
    public void setHide(boolean isHide) {
        hide = isHide;
        if (mItemListViewPagerAdapter != null) {
            HashMap<Integer, PageItemView> pagetViewList = mItemListViewPagerAdapter.getPageItemViewMap();
            for (Entry<Integer, PageItemView> entry : pagetViewList.entrySet()) {
                PageItemView page = entry.getValue();
                page.setHide(isHide);
            }
        }
    }

    /**
     * 设置上下文
     * @param activity
     * @param fragment
     */
    public void setContext(HomeActivity activity, Fragment fragment) {
        mActivity = activity;
        setDrawingCacheEnabled(true);
        mBusinessRequest = MyBusinessRequest.getInstance();
        mItemListViewPagerAdapter = new CategoryViewPagerAdapter(mActivity, null, fragment);
        setAdapter(mItemListViewPagerAdapter);
        setOnPageChangeListener(new GoodsPageChangeListener());
        /* 主要代码段 */
        if (!SystemConfig.DIPEI_BOX) {
            try {
                Field mField = ViewPager.class.getDeclaredField("mScroller");
                mField.setAccessible(true);
                //设置加速度 ，通过改变FixedSpeedScroller这个类中的mDuration来改变动画时间（如mScroller.setmDuration(mMyDuration);） 
                mScroller = new ViewScroller(getContext(), new DecelerateInterpolator());
                mScroller.setmDuration(500);
                mScroller.setScrollListener(this);
                mField.set(this, mScroller);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //        JuImageLoader.getJuImageLoader().setJuCustomerLoadInterface(new MyJuCustomerLoadInterface(this));
    }

    /**
     * 设置监听ViewPage的方法
     * @param listener
     */
    public void setCategoryViewPagerListener(CategoryViewPagerListener listener) {
        mCategoryViewPagerListener = listener;
    }

    /**
     * 取得当前数据的总页数(是通过数据计算所得，而非UI显示的页数)
     * @return
     */
    public int getViewPagerCount() {
        int goodsCount = getGoodsCount();
        int pageCount = 1 + ((goodsCount - 1) / PageItemView.ITEM_COUNT_EACH_PAGE);
        return pageCount;
    }

    /**
     * 是否需要重新布局
     * @param needLayout
     */
    public void setNeedLayout(boolean needLayout) {
        mNeedLayout = needLayout;
    }

    /**
     * 取得显示在viewPager里面的View
     * @return
     */
    public HashMap<Integer, PageItemView> getPageItemViewMap() {
        return mItemListViewPagerAdapter.getPageItemViewMap();
    }

    /**
     * 是否需要修改全部列表
     * @return
     */
    public boolean getNeedRefreshAllList() {
        return mNeedRefreshAllList;
    }

    /**
     * 刷新刷新所有列表
     */
    public void refreshList() {
        mNeedRefreshAllList = false;
        mItemListViewPagerAdapter.setListData(mBusinessRequest.getCategoryItemList(mCategoryMO.getCid()));
        mItemListViewPagerAdapter.notifyDataSetChanged();
        refreshAllNeedRefreshGoods();
    }

    /**
     * 刷新首页的商品列表
     * @return boolean 是否刷新成功
     */
    public boolean refreshFirsPageList() {
        List<ItemMO> itemList = mBusinessRequest.getCategoryItemList(mCategoryMO.getCid());
        if (itemList != null && itemList.size() > 0) {
            List<ItemMO> refreshList;
            AppDebug.i(TAG, "refreshFirsPageList size=" + itemList.size());
            if (itemList.size() > PageItemView.ITEM_COUNT_EACH_PAGE) {
                AppDebug.i(TAG, "refreshFirsPageList need refresh all list");
                mNeedRefreshAllList = true;
                mItemListViewPagerAdapter.setIsOnlyOnePage(false);
                refreshList = itemList.subList(0, PageItemView.ITEM_COUNT_EACH_PAGE);
            } else {
                refreshList = itemList;
                mItemListViewPagerAdapter.setIsOnlyOnePage(true);
            }
            mItemListViewPagerAdapter.setListData(refreshList);
            mItemListViewPagerAdapter.notifyDataSetChanged();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 取得商品的个数
     * @return int
     */
    public int getGoodsCount() {
        if (null == mBusinessRequest || null == mCategoryMO) {
            return 0;
        }
        List<ItemMO> itemList = mBusinessRequest.getCategoryItemList(mCategoryMO.getCid());
        if (itemList != null) {
            return itemList.size();
        } else {
            return 0;
        }
    }

    @Override
    public void endScroll() {
        HashMap<Integer, PageItemView> pageViewMap = getPageItemViewMap();
        if (pageViewMap != null) {
            for (Entry<Integer, PageItemView> entry : pageViewMap.entrySet()) {
                PageItemView value = entry.getValue();
                //                value.setOutsideSroll(false);
//                value.setScrolling(false);
                value.setSlide(false);
            }
        }
    }

    @Override
    public void scrolling() {
    }

    @Override
    public void startScroll() {
        HashMap<Integer, PageItemView> pageViewMap = getPageItemViewMap();
        if (pageViewMap != null) {
            for (Entry<Integer, PageItemView> entry : pageViewMap.entrySet()) {
                PageItemView value = entry.getValue();
                //                value.setOutsideSroll(true);
//                value.setScrolling(true);
                value.setSlide(true);
            }
        }
    }

    /**
     * 请求分类的数据
     */
    public void requestCategoryGoodsData(CategoryMO categoryData) {
        AppDebug.i(TAG, TAG + ".requestCategoryGoodsData categoryData=" + categoryData
                + ", mCategoryViewPagerListener=" + mCategoryViewPagerListener);
        mCategoryMO = categoryData;
        if (mCategoryMO != null && !StringUtil.isEmpty(mCategoryMO.getCid())) {
            //解决异常问题 update by wangsc
            mItemListViewPagerAdapter.setCategory(mCategoryMO);
            //如果数据为非空说明预加载已完成，直接显示
            if (refreshFirsPageList()) {
                if (mCategoryViewPagerListener != null) {
                    mCategoryViewPagerListener.onRequestGetGoodsDone(true);
                }
            } else {
                requestGoodsList(true, mCategoryMO, mCategoryViewPagerListener);
            }
        }
    }

    /**
     * 网络读取普通类目商品数据的请求
     * @param onlyRefreshFirstPage
     *            是否只刷新首页
     * @param listener
     */
    private void requestGoodsList(final boolean onlyRefreshFirstPage, final CategoryMO cate,
            final CategoryViewPagerListener listener) {
        AppDebug.i(TAG, TAG + ".requestGoodsList onlyRefreshFirstPage=" + onlyRefreshFirstPage + ", cate=" + cate);
        mBusinessRequest.requestGetCategoryItemList(mActivity, cate, EACH_REQUEST_GOODS_LIST_COUNT,
                new JuHomeItemsFilterRetryRequestListener<CategoryViewPager>(mActivity, cate, this) {

                    @Override
                    public void onSuccess(BaseActivity baseActivity, CountList<ItemMO> itemList) {
                        CategoryViewPager viewPager = mT.get();
                        if (null == viewPager) {
                            return;
                        }
                        if (listener != null) {
                            listener.onRequestGetGoodsDone(true);
                        }
                        if (itemList != null && itemList.size() > 0) {
                            if (itemList.getTotalPage() <= itemList.getCurrentPage()) {
                                viewPager.setmNoMoreGoods(true);
                            } else {
                                viewPager.needRequestIndex += itemList.size();
                                if (itemList.getCurrentPage() <= 1) {
                                    viewPager.needRequestIndex -= EACH_REQUEST_GOODS_LIST_COUNT;
                                }
                            }
                            AppDebug.i(TAG, TAG + ".requestGoodsList needRequestIndex=" + viewPager.needRequestIndex
                                    + ", itemList=" + itemList.size());
                            //是否只刷新第一页
                            if (onlyRefreshFirstPage) {
                                viewPager.refreshFirsPageList();
                            } else {
                                viewPager.refreshList();
                            }
                        } else if (null != listener) {
                            int page = viewPager.mBusinessRequest.getCurrCateogryPageNo(cate.getCid());
                            if (1 >= page) {
                                listener.onRequestGetGoodsNone();
                            }
                        }
                    }

                    @Override
                    public boolean onUserError(BaseActivity baseActivity, int resultCode, String msg) {
                        CategoryViewPager viewPager = mT.get();
                        if (null == viewPager) {
                            return true;
                        }
                        int pageNo = viewPager.mBusinessRequest.getCurrCateogryPageNo(viewPager.mCategoryMO.getCid());
                        if (listener != null && pageNo <= 1) {
                            listener.onRequestGetGoodsDone(false);
                        }
                        if (viewPager.hide) {
                            return true;
                        }
                        return false;
                    }

                });
    }

    /**
     * Page页面监听器
     * @author tim
     */
    private class GoodsPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            //滑动完成后再显示商品
            if (arg0 == 0) {
                setSlide(false);
                refreshAllNeedRefreshGoods();
                endScroll();
            } else {
                setSlide(true);
                if (!SystemConfig.DIPEI_BOX) {
                    startScroll();
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            cancelLoadImage(arg0);

            //            int currPage = mBusinessRequest.getCurrCateogryPageNo(mCategoryMO.getCid());
            //            int needRequestIndex = currPage * EACH_REQUEST_GOODS_LIST_COUNT - REQUEST_LIST_ADVANCE_PAGE_COUNT
            //                    * PageItemView.ITEM_COUNT_EACH_PAGE;
            int currRequestIndex = arg0 * PageItemView.ITEM_COUNT_EACH_PAGE;
            AppDebug.i(TAG, "onPageSelected needRequestIndex=" + needRequestIndex + " currRequestIndex="
                    + currRequestIndex + ", mNoMoreGoods=" + mNoMoreGoods);
            if (needRequestIndex <= currRequestIndex && !mNoMoreGoods && mCategoryMO != null) {
                requestGoodsList(false, mCategoryMO, mCategoryViewPagerListener);
            }
            if (mCategoryViewPagerListener != null) {
                mCategoryViewPagerListener.onViewPagerChanged(arg0);
            }

        }
    }

    /**
     * 取消商品图片加载
     */
    protected void cancelLoadImage(int position) {
        HashMap<Integer, PageItemView> pageViewMap = getPageItemViewMap();
        if (pageViewMap != null) {
            for (Entry<Integer, PageItemView> entry : pageViewMap.entrySet()) {
                PageItemView value = entry.getValue();
                if (entry.getKey() != position) { //当前页不能取消加载
                    value.cancelLoadImage();
                }
            }
        }
    }

    /**
     * 更新所有需要更新的商品
     */
    public void refreshAllNeedRefreshGoods() {
        HashMap<Integer, PageItemView> itemMap = mItemListViewPagerAdapter.getPageItemViewMap();
        if (itemMap != null) {
            for (Entry<Integer, PageItemView> entry : itemMap.entrySet()) {
                PageItemView pageItem = entry.getValue();
                pageItem.refreshGoodsView();
            }
        }
    }

    /**
     * 设置是否正在滑动
     * @param isSlide
     */
    private void setSlide(boolean isSlide) {
        HashMap<Integer, PageItemView> itemMap = mItemListViewPagerAdapter.getPageItemViewMap();
        if (itemMap != null) {
            for (Entry<Integer, PageItemView> entry : itemMap.entrySet()) {
                PageItemView pageItem = entry.getValue();
                pageItem.setSlide(isSlide);
            }
        }
    }

    /**
     * @return the mNoMoreGoods
     */
    public boolean ismNoMoreGoods() {
        return mNoMoreGoods;
    }

    /**
     * @param mNoMoreGoods the mNoMoreGoods to set
     */
    public void setmNoMoreGoods(boolean mNoMoreGoods) {
        this.mNoMoreGoods = mNoMoreGoods;
    }

    /**
     * 商品数据请求完成后监听回调
     * @author tim
     */
    public interface CategoryViewPagerListener {

        /**
         * 数据加载完成的监听方法
         * @param isSuccess
         */
        public void onRequestGetGoodsDone(boolean isSuccess);

        /**
         * 页面发生切换，是在切换动画之前
         * 注：这里不能有影响UI速度操作
         * @param currPage
         */
        public void onViewPagerChanged(int currPage);

        /**
         * 当没有商品时回调
         */
        public void onRequestGetGoodsNone();
    }

    //    private static class MyJuCustomerLoadInterface implements JuCustomerLoadInterface {
    //
    //        private final WeakReference<CategoryViewPager> ref;
    //
    //        public MyJuCustomerLoadInterface(CategoryViewPager that) {
    //            super();
    //
    //            this.ref = new WeakReference<CategoryViewPager>(that);
    //        }
    //
    //        @Override
    //        public Bitmap customerLoad(Object obj) {
    //            CategoryViewPager that = this.ref.get();
    //            if (that != null) {
    //
    //                if (obj != null && obj instanceof GoodsNormalItemViewInfoRequestData) {
    //                    GoodsNormalItemViewInfoRequestData data = (GoodsNormalItemViewInfoRequestData) obj;
    //                    if (data.mItemData != null) {
    //                        AppDebug.i(TAG, "GoodsNormalItemViewInfoRequest name=" + data.mItemData.getLongName()
    //                                + " width=" + data.mWith + " height=" + data.mHeight);
    //                    }
    //                    if (data.mWith <= 0 || data.mHeight < 0) {
    //                        return null;
    //                    }
    //                    GoodsNormalItemViewInfo infoView = new GoodsNormalItemViewInfo(that.mContext, null);
    //                    Bitmap bitmap = Bitmap.createBitmap(data.mWith, data.mHeight, Bitmap.Config.ARGB_8888);
    //                    Canvas canvas = new Canvas(bitmap);
    //                    infoView.setGoodsItemData(data.mItemData, 0);
    //                    infoView.measure(MeasureSpec.makeMeasureSpec(data.mWith, MeasureSpec.EXACTLY),
    //                            MeasureSpec.makeMeasureSpec(data.mHeight, MeasureSpec.EXACTLY));
    //                    infoView.layout(0, 0, data.mWith, data.mHeight);
    //                    infoView.draw(canvas);
    //                    return bitmap;
    //                }
    //            }
    //            return null;
    //        }
    //    }
}
