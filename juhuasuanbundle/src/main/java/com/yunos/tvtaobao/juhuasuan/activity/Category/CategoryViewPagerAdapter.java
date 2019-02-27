package com.yunos.tvtaobao.juhuasuan.activity.Category;


import android.app.Fragment;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvlife.app.widget.FocusedBasePositionManager;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CategoryMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;
import com.yunos.tvtaobao.juhuasuan.config.SystemConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * 分类页面的Page adapter.
 * @author tim
 */
public class CategoryViewPagerAdapter extends PagerAdapter {

    private final String TAG = "ItemListViewPagerAdapter";
    private Context mContext; //上下文
    private List<ItemMO> mListData;//商品的信息列表
    private CategoryMO mCate;//商品的分类ID
    private int mPreLoadPageViewCount = 2;//提前加载页面View的个数
    private HashMap<Integer, PageItemView> mHashViewMap;//page页面的map
    private View mCurrView;//当前pageView
    private View mPreView;//前页的pageView
    private Fragment mFragment;//pageView的fragment
    private boolean mIsOnlyOnePage;//是否只有一页商品信息
    private boolean mIsFirstLoad = true;//是否是首次加载页面

    //    private int mChildCount = 0;

    public CategoryViewPagerAdapter(Context context, List<ItemMO> listData, Fragment fragment) {
        mContext = context;
        mFragment = fragment;
        // 不能直接使用赋值，因为列表会请求中做出改变
        if (listData != null && listData.size() > 0) {
            List<ItemMO> newListData = new ArrayList<ItemMO>();
            newListData.addAll(listData);
            mListData = newListData;
        }
        mListData = new ArrayList<ItemMO>();
        mHashViewMap = new HashMap<Integer, PageItemView>();
        for (int i = 0; i < mPreLoadPageViewCount; i++) {
            View layout = LayoutInflater.from(mContext).inflate(R.layout.jhs_category_page_item, null);
            PageItemView itemView = (PageItemView) layout.findViewById(R.id.page_layout);
            mHashViewMap.put(i, itemView);
        }
    }

    /**
     * 设置是否只有一页商品信息
     * @param onlyOnePage
     */
    public void setIsOnlyOnePage(boolean onlyOnePage) {
        mIsOnlyOnePage = onlyOnePage;
    }

    /**
     * 设置分类ID
     * @param cate
     */
    public void setCategory(CategoryMO cate) {
        mCate = cate;
    }

    /**
     * 设置商品列表
     * @param listData
     */
    public void setListData(List<ItemMO> listData) {
        if (listData == null) {
            AppDebug.e(TAG, TAG + ".setListData.listData == null");
            return;
        }
        // 不能直接使用赋值，因为列表会请求中做出改变
        List<ItemMO> newListData = new ArrayList<ItemMO>();
        newListData.addAll(listData);
        mListData = newListData;
    }

    /**
     * 增加商品列表
     * @param listData
     */
    public void addListData(List<ItemMO> listData) {
        mListData.addAll(listData);
    }

    /**
     * 取得商品页的map
     * @return
     */
    public HashMap<Integer, PageItemView> getPageItemViewMap() {
        return mHashViewMap;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        AppDebug.i(TAG, "destroyItem ItemListPageItemView position=" + position);
        PageItemView item = (PageItemView) object;
        item.recycleGoodsInfoBitmap();
        item.release();
        ((ViewPager) container).removeView(item);
        mHashViewMap.remove(position);
    }

    @Override
    public void finishUpdate(View view) {

    }

    @Override
    public int getCount() {
        if (mListData != null) {
            int size = mListData.size();
            if (size > 0) {
                return 1 + ((size - 1) / PageItemView.ITEM_COUNT_EACH_PAGE);
            }
            return 0;
        }
        return 0;
    }

    //    public int getItemPosition(Object object) {
    //        AppDebug.i(TAG, TAG + ".getItemPosition mChildCount=" + mChildCount);
    //        if (mChildCount > 0) {
    //            mChildCount--;
    //            return POSITION_NONE;
    //        }
    //        return super.getItemPosition(object);
    //    }

    //这里就是初始化ViewPagerItemView.如果ViewPagerItemView已经存在,  
    //重新reload，不存在new一个并且填充数据.  
    @Override
    public Object instantiateItem(View container, int position) {
        AppDebug.i(TAG, "========================position=================" + position);
        PageItemView itemView = mHashViewMap.get(position);
        if (itemView == null) {
            AppDebug.i(TAG, "new ItemListPageItemView position=" + position);
            View layout = LayoutInflater.from(mContext).inflate(R.layout.jhs_category_page_item, null);
            itemView = (PageItemView) layout.findViewById(R.id.page_layout);
            mHashViewMap.put(position, itemView);
        }
        itemView.setTag(mFragment.getId() + "_" + position);

        /* add by leiming.yanlm, BEGEIN */
        FocusedBasePositionManager.FocusParams params = new FocusedBasePositionManager.FocusParams();
        params.setFocusMode(FocusedBasePositionManager.FOCUS_ASYNC_DRAW);
        params.setFrameRate(6);
        params.setScale(true);
        params.setFocusDrawable(mContext.getResources().getDrawable(R.drawable.jhs_tui_grid_focus));
        params.setFocusShadowDrawable(mContext.getResources().getDrawable(R.drawable.jhs_tui_grid_focus));
        if (!SystemConfig.DIPEI_BOX) {
            params.setItemScaleValue(1.06f, 1.06f);
        } else {
            params.setFrameRate(1, 1); //干掉动画
            params.setScale(true);//干掉动画
        }
        itemView.createPositionManager(params);
        //// FIXME: 20/07/2017
        /* add by leiming.yanlm, END */

        /*
         * if (!SystemConfig.DIPEI_BOX) {
         * itemView.setItemScaleValue(1.06f, 1.06f);
         * }
         * itemView.setFocusResId(R.drawable.tui_grid_focus);
         * itemView.setFocusShadowResId(R.drawable.tui_grid_focus);
         * // itemView.setSelectedDefalutIndex(0);
         * if (SystemConfig.DIPEI_BOX) {
         * itemView.setFrameRate(1); //干掉动画
         * itemView.setScale(false);//干掉动画
         * }
         */

        List<ItemMO> pageItem = getPageItemData(position);
        itemView.setCategory(mCate);
        itemView.setIsFirstPage(false);
        itemView.setIsLastPage(false);
        if (position == 0) {
            itemView.setIsFirstPage(true);
        }
        if (position == getCount() - 1) {
            itemView.setIsLastPage(true);
        }

        //强制设置为非最后一页。因为一定条件下强制只更新首页的数据
        if (position == 0 && !mIsOnlyOnePage) {
            itemView.setIsLastPage(false);
        }

        //只有首次显示的时候需要立刻刷新图片
        if (mIsFirstLoad) {
            mIsFirstLoad = false;
            itemView.setRefreshImageAtTime(true);
        } else {
            itemView.setRefreshImageAtTime(false);
        }
        itemView.setPageData(pageItem, position, mFragment);
        CategoryViewPager viewPager = (CategoryViewPager) container;
        viewPager.setNeedLayout(true);
        viewPager.addView(itemView);
        return itemView;
    }

    /**
     * 取得当前的页面的View
     * @return
     */
    public View getCurrItem() {
        return mCurrView;
    }

    /**
     * 取得前页面的View
     * @return
     */
    public View getPreItem() {
        return mPreView;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        if (mCurrView == null || !view.equals(mCurrView)) {
            mPreView = mCurrView;
            mCurrView = (View) object;
        }
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {

    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View view) {
    }

    /**
     * 取得Page页的商品的数据，不够整页数的商品取最前面的补齐
     * @param currPage
     * @return List<ItemMO>
     */
    private List<ItemMO> getPageItemData(int currPage) {
        AppDebug.i(TAG, TAG + ".getPageItemData currPage=" + currPage);
        if (currPage < 0 || currPage >= getCount()) {
            return null;
        }
        int itemCount = PageItemView.ITEM_COUNT_EACH_PAGE;
        int startIndex = currPage * itemCount;
        int endIndex = startIndex + itemCount;
        int dataLastIndex = mListData.size();
        if (dataLastIndex < endIndex) {
            endIndex = dataLastIndex;
        }
        List<ItemMO> pageItem = new ArrayList<ItemMO>(mListData.subList(startIndex, endIndex));
        //最后一页不足5个，那从前面取，直到填满5个
        //        if (pageItem.size() < PageItemView.ITEM_COUNT_EACH_PAGE && mListData.size() > PageItemView.ITEM_COUNT_EACH_PAGE) {
        //            int lackCount = PageItemView.ITEM_COUNT_EACH_PAGE - pageItem.size();
        //            List<ItemMO> lackItem = new ArrayList<ItemMO>(mListData.subList(0, lackCount));
        //            pageItem.addAll(lackItem);
        //            AppDebug.i(TAG, "getPageItemData lackCount=" + lackItem.size());
        //        }
        return pageItem;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.view.PagerAdapter#notifyDataSetChanged()
     */
    @Override
    public void notifyDataSetChanged() {
        //        mChildCount = getCount();
        //        AppDebug.i(TAG, TAG + ".notifyDataSetChanged mChildCount=" + mChildCount);
        super.notifyDataSetChanged();
        notifyDataSetChangedView();
    }

    public void notifyDataSetChangedView() {
        for (Entry<Integer, PageItemView> entry : mHashViewMap.entrySet()) {
            Integer position = entry.getKey();
            if (null != position) {
                List<ItemMO> listData = getPageItemData(position);
                if (null != listData) {
                    PageItemView view = entry.getValue();
                    if (null != view) {
                        view.setPageData(listData, position, mFragment);
                        if (position == getCount() - 1) {
                            view.setIsLastPage(true);
                        }
                    }
                }
            }
        }
    }

}
