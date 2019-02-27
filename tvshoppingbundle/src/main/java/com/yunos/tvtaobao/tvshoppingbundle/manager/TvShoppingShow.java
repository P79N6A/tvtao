package com.yunos.tvtaobao.tvshoppingbundle.manager;


import com.yunos.tv.core.common.AppDebug;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据时间轴上的区域时间显示相关商品的控制类
 * (这里只会做每次状态变化的请求，状态是否真正改变需要上层主动处理设置)
 *
 * @author tingmeng.ytm
 */
public class TvShoppingShow {

    private final String TAG = "TvShopping";
    private List<TvShoppingData> mTvShoppingDataList;
    private OnRequestTvShoppingChangedListener mOnRequestTvShoppingChangedListener;

    public TvShoppingShow() {
        mTvShoppingDataList = new ArrayList<TvShoppingData>();
    }

    /**
     * 设置监听方法
     *
     * @param listener
     */
    public void setOnRequestTvShoppingChangedListener(OnRequestTvShoppingChangedListener listener) {
        mOnRequestTvShoppingChangedListener = listener;
    }

    /**
     * 是否需要显示时间轴上的商品
     *
     * @param currTime
     */
    public void checkTimeShop(long currTime) {
        checkTimeShop(currTime, false);
    }

    /**
     * 是否需要显示时间轴上的商品
     *
     * @param currTime      当前时间
     * @param onlyCheckHide 是否只是检查当前是否要隐藏商品，不做显示商品的检查
     */
    public void checkTimeShop(long currTime, boolean onlyCheckHide) {
        if (mTvShoppingDataList == null || mTvShoppingDataList.size() == 0) {
            AppDebug.e(TAG, TAG + ".checkTimeShop mTvShoppingDataList = " + mTvShoppingDataList);
            return;
        }

        int count = mTvShoppingDataList.size();
        AppDebug.v(TAG, TAG + ".checkTimeShop mTvShoppingDataList.size() = " + count + ", onlyCheckHide = "
                + onlyCheckHide);

        for (int i = 0; i < count; i++) {
            TvShoppingData itemData = mTvShoppingDataList.get(i);
            // 如果已经显示了就判断是否在区间外，如果未显示就判断是否在区间内
            boolean contain = containTime(itemData, currTime);
            AppDebug.i(TAG, "checkTimeShop index = " + i + " id=" + itemData.mId + ", itemId = " + itemData.mItemId
                    + " mShowed=" + itemData.mShowed + " contain=" + contain);
            if (itemData.mShowed) {
                // 如果不在区间内，说明已经离开了上个商品的区间
                if (!contain) {
                    changeShopState(i, false);
                }
            } else {
                // 如果在区间内，说明已经进入区间, 如果只是检查是否隐藏的话那就过滤这个处理等待下次再处理
                if (contain && !onlyCheckHide) {
                    changeShopState(i, true);
                }
            }

            if (!contain) {// 不在区间，主动关闭标志设为false
                itemData.mShowed = false;
            }
        }
    }

    /**
     * 加入数据
     *
     * @param itemId
     * @param startTime
     * @param endTime
     */
    public void addTime(long id, long itemId, long startTime, long endTime) {
        if (mTvShoppingDataList == null) {
            mTvShoppingDataList = new ArrayList<TvShoppingData>();
        }
        AppDebug.d(TAG, "checkValidTime " + checkValidTime(startTime, endTime));
        // 判断数据的有效性
        if (checkValidTime(startTime, endTime)) {
            TvShoppingData itemData = new TvShoppingData();
            itemData.mId = id;
            itemData.mItemId = itemId;
            itemData.mStartTime = startTime;
            itemData.mEndTime = endTime;
            mTvShoppingDataList.add(itemData);
        }
    }

    /**
     * 删除指定item的数据
     *
     * @param id
     */
    public void removeTime(long id) {
        int removeIndex = -1;
        int count = mTvShoppingDataList.size();
        for (int i = 0; i < count; i++) {
            TvShoppingData itemData = mTvShoppingDataList.get(i);
            if (itemData.mId == id) {
                removeIndex = i;
            }
        }
        if (removeIndex >= 0) {
            mTvShoppingDataList.remove(removeIndex);
        }
    }

    /**
     * 显示时间的有效性
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public boolean checkValidTime(long startTime, long endTime) {
        if (startTime < 0 || endTime <= 0) {
            return false;
        }

        if (startTime >= endTime) {
            return false;
        }
        AppDebug.d(TAG, "startTime " + startTime + " endTime " + endTime);
        return true;
    }

    /**
     * 设置shop的显示状态
     *
     * @param id
     * @param show
     */
    public void setShopState(long id, boolean show) {
        AppDebug.v(TAG, TAG + ".setShopState.id = " + id);
        if (mTvShoppingDataList == null || mTvShoppingDataList.size() == 0) {
            return;
        }
        for (TvShoppingData itemData : mTvShoppingDataList) {
            if (itemData.mId == id) {
                itemData.mShowed = show;
            }
        }
    }

    /**
     * 返回 mTimeShopItemDataList
     *
     * @return
     */
    public List<TvShoppingData> getTimeShopItemDataList() {
        return mTvShoppingDataList;
    }

    /**
     * 销毁 mTimeShopItemDataList
     */
    public void clearTimeShopItemDataList() {
        if (mTvShoppingDataList != null) {
            mTvShoppingDataList.clear();
            mTvShoppingDataList = null;
        }
    }

    /**
     * 当前的时候是否在这个item之内
     *
     * @param itemData
     * @param currTime
     * @return
     */
    private boolean containTime(TvShoppingData itemData, long currTime) {
        AppDebug.i(TAG, "currTime = " + currTime + " startTime = " + itemData.mStartTime + " endTime=" + itemData.mEndTime);
        if (currTime >= itemData.mStartTime && currTime <= itemData.mEndTime) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 商品的状态变化
     *
     * @param
     * @param show
     */
    private void changeShopState(int i, boolean show) {
        if (mTvShoppingDataList == null || mTvShoppingDataList.size() == 0) {
            return;
        }
        TvShoppingData itemData = mTvShoppingDataList.get(i);
        if (itemData.mShowed != show) {
            // 这里不做对状态的设置只是发送请求进行变化的设置
            if (mOnRequestTvShoppingChangedListener != null) {
                mOnRequestTvShoppingChangedListener.onRequestTvShoppingChanged(itemData, show);
            }
        }
    }

    /**
     * 时间轴商品状态的变化
     *
     * @author tingmeng.ytm
     */
    public interface OnRequestTvShoppingChangedListener {

        /**
         * 指定的时间轴请求的商品变化
         *
         * @param tvShoppingData 发生变化的商品
         * @param show   是将要变成显示还是隐藏
         */
        public void onRequestTvShoppingChanged(TvShoppingData tvShoppingData, boolean show);
    }

    /**
     * 时间轴商品的相关数据
     *
     * @author tingmeng.ytm
     */
    public static class TvShoppingData {

        public long mId; // 推荐商品唯一Id
        public long mItemId; // 商品的ID
        public boolean mShowed; // 是否已经在区间曾经显示过了
        public long mStartTime; // 显示的开始时间等于这时间也会显示（毫秒）
        public long mEndTime; // 显示的结束时间等于这时间也还是会显示，只有大于这个时间会不会显示（毫秒）
    }
}
