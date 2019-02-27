package com.yunos.tvtaobao.tvshoppingbundle.manager;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.taobao.detail.clientDomain.TBDetailResultVO;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.bo.TbTvShoppingItemBo;
import com.yunos.tvtaobao.biz.request.bo.TbTvShoppingItemBo.ShopType;
import com.yunos.tvtaobao.tvshoppingbundle.bean.TbTvShoppingReceiverData;
import com.yunos.tvtaobao.tvshoppingbundle.dialog.TvShopAllCollectDialog;
import com.yunos.tvtaobao.tvshoppingbundle.dialog.TvShopBaseDialog;
import com.yunos.tvtaobao.tvshoppingbundle.dialog.TvShopDialog;
import com.yunos.tvtaobao.tvshoppingbundle.manager.TvShoppingShow.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TbTvShoppingManager {

    private static final String TAG = "TbTvShoppingManager";
    private final int DEFAULT_SHOW_SHOP_ID = -1; // 默认的显示商品ID

    private static boolean mIsExit = true; // 是否已经退出
    private static TbTvShoppingManager mTbTvShoppingManager; // 单例

    private TbTvShoppingReceiverData mCurrTvShopReceiverData; // 当前收到的广播数据
    private TvShoppingShow mTbTvShopping; // 时间轴显示控制类
    private List<TbTvShoppingItemData> mTbTvShoppingItemDataList; // 边看边购的数据类
    private List<TbTvShoppingItemData> mTbTvShoppingFavorItemList;// 边看边购收藏数据
    private TbTvShoppingActionListener mTbTvShoppingActionListener; // 边看边购的监听列表
    private List<OnTbTvShoppingHideListener> mOnTbTvShoppingHideListenerList; // 边看边购的页面的隐藏监听列表
    private long mCurrShowId; // 当前显示着的商品唯一ID
    private boolean mLockCheckTime; // 是否锁住时间轴的检查
    private int mLastState; //上次数据传回时的播放状态
    private boolean mShopListShowed; // 列表是否已经显示过
    private boolean mPlay; // 是否开始播放
    private boolean mPause; // 是否开始暂停
    private boolean mEnterfull; // 是否进入全屏
    private boolean mExitfull; // 是否退出全屏
    private boolean mSequeceChange; // 是否切换集数
    private boolean mActivityLaunching; // activity正在启动
    private static List<TvShopBaseDialog> mTvShopBaseDialogList;// 保存打开界面的对话框,全局变量，在任何时候manager对象销毁时，保证不受manager销毁的限制
    private TbTvShopNeedResumeInfo mTbTvShopNeedResumeInfo;// 需要重新显示的界面信息
    private TvShoppingData mTvShoppingData;// 时间轴商品的相关数据
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Handler handler;

    /**
     * 取得单例（有可能已经存在）
     *
     * @return
     */
    public static TbTvShoppingManager getIntance() {
        if (mTbTvShoppingManager == null) {
            mTbTvShoppingManager = new TbTvShoppingManager();
        }
        return mTbTvShoppingManager;
    }

    /**
     * 是否已经退出
     * (判断上次是否已经退出了需要重新下状态，在变成非全屏的时候只是记一个状态因为后续需要用到相关的信息，
     * 在下次重新接收新的广播的时候再重新生成单例)
     *
     * @return
     */
    public static boolean isExit() {
        AppDebug.i(TAG, "isExit =" + mIsExit);
        return mIsExit;
    }

    /**
     * 单例置空
     */
    public static void destoryInstance() {
        mTbTvShoppingManager = null;
    }

    private TbTvShoppingManager() {
        // 新进入
        mIsExit = false;
        mActivityLaunching = false;
        mLastState = TbTvShoppingReceiverData.STATE_IDLE;
        mOnTbTvShoppingHideListenerList = new ArrayList<OnTbTvShoppingHideListener>();
        mCurrShowId = DEFAULT_SHOW_SHOP_ID;
        mTbTvShoppingItemDataList = new ArrayList<TbTvShoppingItemData>();
        mTbTvShoppingFavorItemList = new ArrayList<TbTvShoppingItemData>();
        mTbTvShopping = new TvShoppingShow();
        mTbTvShopping.setOnRequestTvShoppingChangedListener(new OnRequestTvShoppingChangedListener() {

            @Override
            public void onRequestTvShoppingChanged(TvShoppingData tvShoppingData, boolean show) {
                mTvShoppingData = tvShoppingData;
                long id = tvShoppingData.mId;
                long itemId = getItemId(id);
                AppDebug.i("test", "onTvShoppingChanged id=" + id + ", itemId = " + itemId + " show=" + show);
                // 显示跟隐藏的动作
                TbTvShoppingItemData itemData = getShoppingItem(id);
                // 修改隐藏的状态
                if (!show) {
                    setTVshoppingItemStatus(id, false);
                }
                // 更新显示状态
                if (itemData != null && itemData.mTbTvShoppingItemBo != null) {
                    changedShopStatus(id, show, itemData.mTbTvShoppingItemBo.getShopType());
                }
            }
        });
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 注册监听方法
     *
     * @param listener
     */
    public void registerTbTvShoppingListener(TbTvShoppingActionListener listener) {
        mTbTvShoppingActionListener = listener;
        AppDebug.i(TAG, "registerTbTvShoppingListener listener=" + listener);
    }

    /**
     * 反注册监听方法
     */
    public void unregisterTbTvShoppingListener(TbTvShoppingActionListener listener) {
        mTbTvShoppingActionListener = null;
        AppDebug.i(TAG, "unregisterTbTvShoppingListener listener=" + listener);
    }

    /**
     * 注册页面隐藏监听方法
     *
     * @param listener
     */
    public void registerTbTvShoppingHideListener(OnTbTvShoppingHideListener listener) {
        if (mOnTbTvShoppingHideListenerList == null) {
            mOnTbTvShoppingHideListenerList = new ArrayList<OnTbTvShoppingHideListener>();
        }
        mOnTbTvShoppingHideListenerList.add(listener);
        AppDebug.i(TAG, "registerTbTvShoppingHideListener count=" + mOnTbTvShoppingHideListenerList.size()
                + " listener=" + listener);
    }

    /**
     * 反注册页面隐藏监听方法
     */
    public void unregisterTbTvShoppingHideListener(OnTbTvShoppingHideListener listener) {
        if (mOnTbTvShoppingHideListenerList != null) {
            mOnTbTvShoppingHideListenerList.remove(listener);
            AppDebug.i(TAG, "unregisterTbTvShoppingHideListener count=" + mOnTbTvShoppingHideListenerList.size()
                    + " listener=" + listener);
        }
    }

    /**
     * 保存正在显示的对话框，在show()之后调用
     *
     * @param dialog
     */
    public void addShowingDialog(TvShopBaseDialog dialog) {
        if (mTvShopBaseDialogList == null) {
            mTvShopBaseDialogList = Collections.synchronizedList(new ArrayList<TvShopBaseDialog>());
        }
        mTvShopBaseDialogList.add(dialog);
    }

    /**
     * 删除将要关闭的对话框,在delete之后调用
     *
     * @param dialog
     */
    public void deleteShowingDailog(TvShopBaseDialog dialog) {
        if (mTvShopBaseDialogList != null) {
            for (int i = mTvShopBaseDialogList.size() - 1; i >= 0 && i < mTvShopBaseDialogList.size(); i--) {
                TvShopBaseDialog theDialog = mTvShopBaseDialogList.get(i);
                if (theDialog == dialog) {
                    try {
                        if (theDialog.isShowing()) {
                            theDialog.setDismissImmediately(true);
                            theDialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mTvShopBaseDialogList.remove(i);// 此方法执行删除操作后必须跳出循环
                    break;
                }
            }
        }
    }

    /**
     * 清楚正在显示的对话框，在show()之前调用
     */
    public void clearShowingDialog() {
        AppDebug.v(TAG, TAG + ".clearShowingDialog.mTvShopBaseDialogList = " + mTvShopBaseDialogList);
        if (mTvShopBaseDialogList != null) {
            AppDebug.v(TAG, TAG + ".clearShowingDialog.mTvShopBaseDialogList.size = " + mTvShopBaseDialogList.size());
            for (int i = mTvShopBaseDialogList.size() - 1; i >= 0; i--) {
                if (i >= mTvShopBaseDialogList.size())
                    continue;//impossible, unless list modified by other threads or methods
                TvShopBaseDialog dialog = mTvShopBaseDialogList.remove(i);
                AppDebug.v(TAG, TAG + ".clearShowingDialog.dialog = " + dialog);
                if (dialog != null && dialog.isShowing()) {
                    dialog.setDismissImmediately(true);
                    if (dialog instanceof TvShopDialog) {
                        TvShopDialog dlg = (TvShopDialog) dialog;
                        long id = dlg.getId();
                        AppDebug.v(TAG, TAG + ".clearShowingDialog.TvShopDialog.id = " + id + ".mCurrShowId = "
                                + mCurrShowId);
                    } else if (dialog instanceof TvShopAllCollectDialog) {
                        TvShopAllCollectDialog dlg = (TvShopAllCollectDialog) dialog;
                        long id = dlg.getId();
                        AppDebug.v(TAG, TAG + ".clearShowingDialog.TvShopAllCollectDialog.id = " + id
                                + ".mCurrShowId = " + mCurrShowId);
                    }
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }
        }
    }

    /**
     * 设置商品显示的状态(这里的显示隐藏的意思是表示一个较长时间的状态)
     *
     * @param id
     * @param show
     */
    public void setTVshoppingItemStatus(long id, boolean show) {
        AppDebug.i(TAG, "setTVshoppingItemStatus id=" + id + " show=" + show);
        // 显示跟隐藏的动作
        TbTvShoppingItemData itemData = getShoppingItem(id);
        if (itemData != null && itemData.mTbTvShoppingItemBo != null) {
            itemData.mIsShowed = show;
            TbTvShoppingItemBo bo = itemData.mTbTvShoppingItemBo;
            // 最后的商品列表是否显示过
            if (bo != null && bo.isList()) {
                mShopListShowed = show;
            }
            if (mTbTvShopping != null) {
                mTbTvShopping.setShopState(id, show);
            }
        }
    }

    /**
     * 设置浮层关闭倒计时
     */
    public void setCountDownClearShowingDialogTimer() {
        try {
            if (mTvShoppingData == null) {
                return;
            }
            long startTime = mTvShoppingData.mStartTime;
            long endTime = mTvShoppingData.mEndTime;
            // 倒计时时间，为最多显示的时间。确保在没有收到广播的情况下关闭浮层
            long countDownTime = endTime - startTime;
            if (mTimer == null) {
                mTimer = new Timer();
            }
            if (mTimerTask == null) {
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                clearShowingDialog();//换到主线程做
                            }
                        });

                    }
                };
            }
            mTimer.schedule(mTimerTask, countDownTime);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    /**
     * 取消浮层关闭倒计时
     */
    public void cancelCountDownClearShowingDialogTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    /**
     * 锁住时间轴的计算跟检查
     *
     * @param lock
     */
    public void setLockCheckTime(boolean lock) {
        boolean empty = shopIsEmpty();
        AppDebug.i(TAG, "setLockCheckTime lock=" + lock + " shopIsEmpty = " + empty);
        if (!empty) {
            mLockCheckTime = lock;
        }
    }

    /**
     * 设置商品详情页图片的url地址
     *
     * @param id
     * @param imageUrl
     */
    public void setShopDetailImageUrl(long id, String imageUrl) {
        AppDebug.i(TAG, "setShopDetailImageUrl id=" + id + " imageUrl=" + imageUrl);
        if (imageUrl != null) {
            TbTvShoppingItemData itemData = getShoppingItem(id);
            if (itemData != null) {
                itemData.mShopDetailImageUrl = imageUrl;
            }
        }
    }

    /**
     * 取得商品详情页图片的url下载地址
     *
     * @param id
     * @return
     */
    public String getShopDetailImageUrl(long id) {
        AppDebug.i(TAG, "getShopDetailImageUrl id=" + id);
        TbTvShoppingItemData itemData = getShoppingItem(id);
        if (itemData != null) {
            return itemData.getShopDetailImageUrl();
        }
        return null;
    }

    /**
     * 取得指定商品的原始数据
     *
     * @param id
     * @return
     */
    public TbTvShoppingItemBo getTbTvShoppingItem(long id) {
        AppDebug.i(TAG, "getTbTvShoppingItem id=" + id);
        TbTvShoppingItemData itemData = getShoppingItem(id);
        if (itemData != null) {
            return itemData.mTbTvShoppingItemBo;
        }
        return null;
    }

    /**
     * 设置商品是否在显示
     *
     * @param id
     * @param visible
     */
    public void setShopItemVisible(long id, boolean visible) {
        AppDebug.i(TAG, "setShopItemVisible id=" + id + " visible=" + visible);
        TbTvShoppingItemData itemData = getShoppingItem(id);
        if (itemData != null) {
            itemData.mIsVisible = visible;
        }
        // 这里只记录显示的状态，因为隐藏的意思不同，这里只是界面的隐藏，而这个状态的意思是处理隐藏的状态
        if (visible) {
            setTVshoppingItemStatus(id, true);
            mCurrShowId = id;
        }
    }

    /**
     * 获取该商品是否正在显示
     *
     * @param id
     * @return
     */
    public boolean getShopItemVisible(long id) {
        TbTvShoppingItemData itemData = getShoppingItem(id);
        if (itemData != null) {
            return itemData.mIsVisible;
        }

        return false;
    }

    /**
     * 判断是否有界面正在显示
     *
     * @return
     */
    public boolean getHasShopItemVisible() {
        if (mTbTvShoppingItemDataList != null) {
            for (TbTvShoppingItemData itemData : mTbTvShoppingItemDataList) {
                if (itemData.mIsVisible) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 加入收藏
     *
     * @param id
     * @param favor
     */
    public void setShopItemFavor(long id, boolean favor) {
        if (mTbTvShoppingFavorItemList == null) {
            mTbTvShoppingFavorItemList = new ArrayList<TbTvShoppingItemData>();
        }

        long itemId = getShoppingItemId(id);// 找出id对应的itemId
        AppDebug.i(TAG, "setShopItemFavor id=" + id + ", itemId = " + itemId + " favor=" + favor);

        // 找出与该itemId相同的所有列表。
        List<TbTvShoppingItemData> theItemDataList = getTheItemDataListFromItemId(itemId);
        if (theItemDataList != null && theItemDataList.size() > 0) {
            for (int i = 0; i < theItemDataList.size(); i++) {// 更新这些相同商品的状态
                TbTvShoppingItemData itemData = theItemDataList.get(i);
                if (itemData != null) {
                    itemData.mIsFavor = favor;
                }
            }

            TbTvShoppingItemData theItemData = null;
            // 判断该商品已经在收藏列表中
            for (TbTvShoppingItemData itemData : mTbTvShoppingFavorItemList) {
                if (itemData != null && itemData.mItemId == itemId) {
                    theItemData = itemData;
                    break;
                }
            }

            if (favor) {
                if (theItemData == null) {// 如果收藏， 并且列表中不存在,增加到列表中
                    for (TbTvShoppingItemData itemData : theItemDataList) {
                        if (itemData != null && itemData.mId == id) {
                            AppDebug.v(TAG, TAG + ".setShopItemFavor addFavor itemData.id = " + itemData.mId
                                    + ".itemData.mItemId = " + itemData.mItemId);
                            mTbTvShoppingFavorItemList.add(itemData);
                            break;
                        }
                    }
                }
            } else {
                if (theItemData != null) {// 如果取消收藏，并且列表中存在，则删除含有该itemId的商品
                    Iterator<TbTvShoppingItemData> iter = mTbTvShoppingFavorItemList.iterator();
                    while (iter.hasNext()) {
                        TbTvShoppingItemData itemData = iter.next();
                        if (itemData != null && itemData.mItemId == itemId) {
                            AppDebug.v(TAG, TAG + ".setShopItemFavor deleteFavor itemData.id = " + itemData.mId
                                    + ".itemData.mItemId = " + itemData.mItemId);
                            iter.remove();
                        }
                    }
                }
            }
        }

        AppDebug.i(TAG,
                TAG + ".setShopItemFavor.mTbTvShoppingFavorItemList.size = " + mTbTvShoppingFavorItemList.size());
    }

    /**
     * 取得收藏商品相关信息
     *
     * @return
     */
    public List<TbTvShoppingItemData> getFavorItemList() {
        return mTbTvShoppingFavorItemList;
    }

    /**
     * 取得所有商品的相关信息
     *
     * @return
     */
    public List<TbTvShoppingItemData> getShoppingItemDataList() {
        return mTbTvShoppingItemDataList;
    }

    /**
     * 片尾是否有推荐商品
     *
     * @return
     */
    private boolean hasEndItems() {
        boolean has = false;
        if (null != mTbTvShoppingItemDataList && mTbTvShoppingItemDataList.size() > 0) {
            for (TbTvShoppingItemData item : mTbTvShoppingItemDataList) {
                if (null != item.getTbTvShoppingItemBo() && null != item.getTbTvShoppingItemBo().getShopType()
                        && item.getTbTvShoppingItemBo().getShopType() == ShopType.LIST) {
                    has = true;
                    break;
                }
            }
        }
        if (Config.isDebug()) {
            AppDebug.v(TAG, TAG + ".hasEndItems. has = " + has);
        }

        return has;
    }

    /**
     * 返回商品数
     *
     * @return
     */
    public int getSingleItemNum() {
        int num = 0;
        if (null != mTbTvShoppingItemDataList && mTbTvShoppingItemDataList.size() > 0) {
            for (TbTvShoppingItemData item : mTbTvShoppingItemDataList) {
                if (null != item.getTbTvShoppingItemBo() && null != item.getTbTvShoppingItemBo().getShopType()
                        && item.getTbTvShoppingItemBo().getShopType() == ShopType.SINGLE) {
                    num++;
                }
            }
        }
        if (Config.isDebug()) {
            AppDebug.v(TAG, TAG + ".getSingleItemNum. num = " + num);
        }

        return num;
    }

    /**
     * 获取对应商品在列表中的位置
     *
     * @param id
     * @return
     */
    public int getPosition(long id) {
        int position = -1;
        if (null != mTbTvShoppingItemDataList && mTbTvShoppingItemDataList.size() > 0) {
            for (int i = 0; i < mTbTvShoppingItemDataList.size(); i++) {
                TbTvShoppingItemData item = mTbTvShoppingItemDataList.get(i);
                if (null != item && item.getId() == id) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    /**
     * 接收到新的数据做出相应的处理
     *
     * @param bundle
     */
    public void receiverNewData(Bundle bundle, boolean resume) {
        if (bundle == null || mTbTvShopping == null) {
            return;
        }
        TbTvShoppingReceiverData receiverData = TbTvShoppingReceiverData.buildReceiverData(bundle);
        if (receiverData == null) {
            return;
        }

        // 埋点
        mPlay = isPlay(receiverData); // 是否开始播放
        mPause = isPause(receiverData);// 是否暂停播放
        mEnterfull = isEnterFullScreen(receiverData);// 是否进入全屏
        mExitfull = isExitFullScreen(receiverData);// 是否退出全屏
        mSequeceChange = isVideoSequeceChange(receiverData);// 是否切换集数

        if (mCurrTvShopReceiverData != null) {
            mLastState = mCurrTvShopReceiverData.getState();
        }

        if (Config.isDebug()) {
            AppDebug.v(TAG, TAG + ".receiverNewData.mPlay = " + mPlay + ".mPause = " + mPause + ".mEnterfull = "
                    + mEnterfull + ".mExitfull = " + mExitfull + ".mSequeceChange = " + mSequeceChange);
        }

        if (mPlay || mPause || mEnterfull || mExitfull || mSequeceChange) {
            tbsVideoPlayAndPause(receiverData);
        }

        // 如果影视界面显示 判断是否有可恢复的界面
        if (resume) {
            boolean needHideShop = needHideShop(receiverData);
            if (Config.isDebug()) {
                AppDebug.i(TAG, TAG + ".receiverNewData.needHideShop = " + needHideShop + ".mTbTvShopNeedResumeInfo = "
                        + mTbTvShopNeedResumeInfo + ".mTbTvShoppingActionListener = " + mTbTvShoppingActionListener
                        + ",position = " + receiverData.getPositon());
            }
            if (mTbTvShopNeedResumeInfo != null && mTbTvShoppingActionListener != null) {
                if (mTbTvShopNeedResumeInfo.mNeedResumePage != NEED_RESUME_PAGE.TVSHOP_MY_COLLECT_PAGE) {
                    if (!needHideShop) {// 不是我的收藏界面时，需要检查是否需要隐藏的条件
                        mTbTvShoppingActionListener.onResumeShop(mTbTvShopNeedResumeInfo);
                    }
                } else {
                    mTbTvShoppingActionListener.onResumeShop(mTbTvShopNeedResumeInfo);
                }
            }

            return;
        }

        if (receiverData.isFullScreen()) {
            boolean needRequestData = needRequestData(mCurrTvShopReceiverData, receiverData);
            // 请求新的数据
            if (needRequestData) {
                requestShoppingData(receiverData);
            }

            boolean needHideShop = needHideShop(receiverData);
            boolean needWeakHideShop = needWeakHideShop(receiverData);
            if (!needHideShop && needWeakHideShop && !getHasShopItemVisible()) {// 如果没有界面存在，则判断弱条件
                needHideShop = true;
            }

            boolean isCtrlViewDismiss = isControlViewDismiss(mCurrTvShopReceiverData, receiverData);
            mCurrTvShopReceiverData = receiverData;
            AppDebug.i(TAG, TAG + ".receiverNewData needHideShop=" + needHideShop + " needRequestData="
                    + needRequestData + ", isCtrlViewDismiss = " + isCtrlViewDismiss);
            if (needRequestData || needHideShop) {
                // 隐藏商品
                changedShopStatus(mCurrShowId, false, ShopType.UNKNOWN);
                if (needHideShop && !mLockCheckTime && receiverData.getPositon() > 0 && !isCtrlViewDismiss) {
                    // 只隐藏显示的商品
                    mTbTvShopping.checkTimeShop(receiverData.getPositon(), true);
                }
            } else {
                AppDebug.v(TAG, TAG + ".receiverNewData.mLockCheckTime = " + mLockCheckTime);
                if (!mLockCheckTime && receiverData.getPositon() > 0 && !isCtrlViewDismiss) {
                    // 检查是否有新的商品需要显示
                    mTbTvShopping.checkTimeShop(receiverData.getPositon());
                }
            }
        } else {
            changedShopStatus(mCurrShowId, false, ShopType.UNKNOWN);

            // 手动退出才会给出提示
            boolean isManualExit = receiverData.isManualUnFullscreen();
            AppDebug.v(TAG, TAG + ".receiverNewData.isManualExit = " + isManualExit + ", mShopListShowed = "
                    + mShopListShowed);
            // 手动退出，全部列表没有显示
            if (isManualExit && !mShopListShowed) {
                exitVideo();
            }

            reset();
        }
    }

    /**
     * 获取是否需要隐藏界面
     *
     * @return
     */
    public boolean getNeedHideShop() {
        if (mCurrTvShopReceiverData == null) {
            return true;
        }

        boolean needHideShop = needHideShop(mCurrTvShopReceiverData);
        boolean needWeakHideShop = needWeakHideShop(mCurrTvShopReceiverData);
        if (!needHideShop && needWeakHideShop && !getHasShopItemVisible()) {// 如果没有界面存在，则判断弱条件
            needHideShop = true;
        }

        return needHideShop;
    }

    /**
     * 统计：视频开始播放/停止播放时，采集信息
     */
    public void tbsVideoPlayAndPause() {
        tbsVideoPlayAndPause(mCurrTvShopReceiverData);
    }

    /**
     * 统计：视频开始播放/停止播放时，采集信息
     *
     * @param receiverData
     */
    private void tbsVideoPlayAndPause(TbTvShoppingReceiverData receiverData) {
        if (null == receiverData) {
            return;
        }

        Map<String, String> p = Utils.getProperties();
        if (null != receiverData) {
            p.put("video_id", receiverData.getVideoId());
        }
        if (null != receiverData.getVideoName()) {
            p.put("video_name", receiverData.getVideoName());
        }
        if (null != receiverData.getType()) {
            p.put("video_type", receiverData.getType().getName());
        }

        int itemNum = getSingleItemNum();//全部推荐商品
        if (itemNum > 0) {
            p.put("has_item", "true"); //是否会推荐商品
            p.put("item_num", String.valueOf(itemNum)); //推荐商品的数量
        } else {
            p.put("has_item", "false");
        }
        List<TbTvShoppingItemData> favorItems = getFavorItemList(); //收藏
        if (null != favorItems && favorItems.size() > 0) {
            p.put("has_pause_item", "true"); //中途退出视频是否有推荐
        } else {
            p.put("has_pause_item", "false");
        }
        p.put("has_end_item", String.valueOf(hasEndItems()));// 片尾时是否有推荐
        if (mPlay) {
            p.put("video_control", "play");//操作类型（play开始播放/pause退出播放）
        } else if (mPause) {
            p.put("video_control", "pause");//操作类型（play开始播放/pause退出播放）
        }

        if (mEnterfull) {
            p.put("video_full", "full");//全屏播放
        } else if (mExitfull) {
            p.put("video_full", "quit_full");//退出播放
        }

        if (mSequeceChange && !TextUtils.isEmpty(receiverData.getVideoSubid())) {
            p.put("sequece_change", "true");//集数切换
        } else {
            p.put("sequece_change", "false");//集数切换
        }

        if (!TextUtils.isEmpty(receiverData.getVideoSubid())) {
            p.put("video_subid", receiverData.getVideoSubid());//视频子集
        } else {
            p.put("video_subid", "");//视频子集
        }

        String controlName = Utils.getControlName("VideoBuy_Video", null);
        Utils.utCustomHit("tvshopservice", controlName, p);
        if (Config.isDebug()) {
            AppDebug.i(TAG, TAG + ".tbsVideoPlayAndPause.controlName = " + controlName + ",p = " + p);
        }
    }

    /**
     * 是否开始播放
     *
     * @param receiverData
     * @return
     */
    private boolean isPlay(TbTvShoppingReceiverData receiverData) {
        if (receiverData == null) {
            return false;
        }

        if (receiverData.getState() == TbTvShoppingReceiverData.STATE_PLAYING
                && mLastState != TbTvShoppingReceiverData.STATE_PLAYING) {
            return true;
        }

        return false;
    }

    /**
     * 是否暂停播放
     *
     * @param receiverData
     * @return
     */
    private boolean isPause(TbTvShoppingReceiverData receiverData) {
        if (receiverData == null) {
            return false;
        }

        if (receiverData.getState() == TbTvShoppingReceiverData.STATE_PAUSED
                && mLastState != TbTvShoppingReceiverData.STATE_PAUSED) {
            return true;
        }

        return false;
    }

    /**
     * 是否进入全屏
     *
     * @return
     */
    public boolean isEnterFullScreen(TbTvShoppingReceiverData receiverData) {
        if (receiverData == null) {
            return false;
        }

        if (mCurrTvShopReceiverData == null) {
            if (receiverData.isFullScreen()) {// 如果当前没有数据，且新接收的数据为全屏
                return true;
            } else {
                return false;
            }
        } else if (!mCurrTvShopReceiverData.isFullScreen() && receiverData.isFullScreen()) {
            return true;
        }

        return false;
    }

    /**
     * 是否退出全屏
     *
     * @return
     */
    public boolean isExitFullScreen(TbTvShoppingReceiverData receiverData) {
        if (receiverData == null || mCurrTvShopReceiverData == null) {
            return false;
        }

        if (mCurrTvShopReceiverData.isFullScreen() && !receiverData.isFullScreen()) {
            return true;
        }

        return false;
    }

    /**
     * 是否切换集数
     *
     * @return
     */
    public boolean isVideoSequeceChange(TbTvShoppingReceiverData receiverData) {
        if (receiverData == null) {
            return false;
        }

        if (mCurrTvShopReceiverData == null) {
            String videoValue = receiverData.getVideoSubid();
            if (!TextUtils.isEmpty(videoValue)) {// 如果是第一条数据，且当前集数不为空
                return true;
            } else {
                return false;
            }
        } else if (mCurrTvShopReceiverData != null) {
            String preVideoValue = mCurrTvShopReceiverData.getVideoSubid();
            String curVideoValue = receiverData.getVideoSubid();
            // 如果两都为空，或者不为空并且两值相同，说明源相同
            if ((TextUtils.isEmpty(preVideoValue) && TextUtils.isEmpty(curVideoValue))
                    || (!TextUtils.isEmpty(preVideoValue) && !TextUtils.isEmpty(curVideoValue) && preVideoValue
                    .compareTo(curVideoValue) == 0)) {
                return false;
            } else {
                return true;
            }
        }

        return false;
    }

    /**
     * 初始化商品的数据
     *
     * @param tbTvShoppingItemBoList
     */
    public void initTvShoppingData(List<TbTvShoppingItemBo> tbTvShoppingItemBoList) {
        if (mTbTvShoppingItemDataList == null) {
            mTbTvShoppingItemDataList = new ArrayList<TbTvShoppingItemData>();
        }
        mTbTvShoppingItemDataList.clear();

        if (Config.isDebug()) {
            AppDebug.v(TAG, TAG + ".initTvShoppingData.tbTvShoppingItemBoList = " + tbTvShoppingItemBoList
                    + ", mTbTvShopping = " + mTbTvShopping);
        }

        AppDebug.i(TAG, "list.size = " + tbTvShoppingItemBoList.size());

        if (tbTvShoppingItemBoList != null && mTbTvShopping != null) {
            mTbTvShopping.clearTimeShopItemDataList();
            for (TbTvShoppingItemBo itemBo : tbTvShoppingItemBoList) {
                AppDebug.d(TAG, itemBo.toString());
                // 保存边看边购的数据信息
                TbTvShoppingItemData itemData = new TbTvShoppingItemData();
                itemData.mId = itemBo.getStartTime();
                itemData.mItemId = itemBo.getItemId();
                itemData.itemActionUri = itemBo.getItemActionUri();
                itemData.mTbTvShoppingItemBo = itemBo;
                itemData.mIsShowed = false;
                mTbTvShoppingItemDataList.add(itemData);

                // 将数据加入到时间轴控制类里面
                mTbTvShopping.addTime(itemData.mId, itemBo.getItemId(), itemBo.getStartTime(), itemBo.getEndTime());
            }
        }
    }

    /**
     * 返回控制面板是否从可见到不可见
     *
     * @param preData
     * @param curData
     * @return
     */
    private boolean isControlViewDismiss(TbTvShoppingReceiverData preData, TbTvShoppingReceiverData curData) {
        if (preData != null && curData != null) {
            if (preData.getIsShowView() && !curData.getIsShowView()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 是否强制需要隐藏商品
     *
     * @param currReceiverData
     * @return true 隐藏
     */
    private boolean needHideShop(TbTvShoppingReceiverData currReceiverData) {
        boolean need = true;
        if (currReceiverData != null) {
            need = currReceiverData.needHideShop();
        }
        AppDebug.v(TAG, TAG + ".needHideShop need = " + need);
        return need;
    }

    /**
     * 是否非强制隐藏商品，当商品界面已经出来，则不关闭
     *
     * @param currReceiverData
     * @return true 隐藏
     */
    private boolean needWeakHideShop(TbTvShoppingReceiverData currReceiverData) {
        boolean need = true;
        if (currReceiverData != null) {
            need = currReceiverData.needWeakHideShop();
        }
        AppDebug.v(TAG, TAG + ".needWeakHideShop need = " + need);
        return need;
    }

    /**
     * 是否需要请求新的数据（判断视频源是否是相同）
     *
     * @param preReceiverData
     * @param currReceiverData
     * @return
     */
    private boolean needRequestData(TbTvShoppingReceiverData preReceiverData, TbTvShoppingReceiverData currReceiverData) {
        // 首次进入先请求数据
        if (preReceiverData == null && currReceiverData != null) {
            return true;
        }

        if (preReceiverData != null && currReceiverData != null) {
            String preVideoId = preReceiverData.getVideoId();
            String currVideoId = currReceiverData.getVideoId();
            AppDebug.i(TAG, TAG + ".needRequestData preVideoId=" + preVideoId + " currVideoId=" + currVideoId);
            // 如果上次数据是空的，本次的为非空，更新数据
            if (TextUtils.isEmpty(preVideoId) && !TextUtils.isEmpty(currVideoId)) {
                return true;
            } else if (!TextUtils.isEmpty(preVideoId) && !TextUtils.isEmpty(currVideoId)
                    && preVideoId.compareTo(currVideoId) != 0) {
                // 如果上次和本次数据都为非空，并且不相同，更新数据
                return true;
            } else {
                String preVideoValue = preReceiverData.getVideoSubid();
                String currVideoValue = currReceiverData.getVideoSubid();
                AppDebug.i(TAG, TAG + ".needRequestData preVideoValue=" + preVideoValue + " currVideoValue="
                        + currVideoValue);
                // 如果两都为空，或者不为空并且两值相同，说明源相同
                if ((TextUtils.isEmpty(preVideoValue) && TextUtils.isEmpty(currVideoValue))
                        || (!TextUtils.isEmpty(preVideoValue) && !TextUtils.isEmpty(currVideoValue) && preVideoValue
                        .compareTo(currVideoValue) == 0)) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 取得商品相关信息
     *
     * @param id
     * @return
     */
    private TbTvShoppingItemData getShoppingItem(long id) {
        if (mTbTvShoppingItemDataList != null) {
            for (TbTvShoppingItemData itemData : mTbTvShoppingItemDataList) {
                if (itemData.mId == id) {
                    return itemData;
                }
            }
        }
        return null;
    }

    /**
     * 得到商品id
     *
     * @param id
     * @return
     */
    private long getShoppingItemId(long id) {
        if (mTbTvShoppingItemDataList != null) {
            for (TbTvShoppingItemData itemData : mTbTvShoppingItemDataList) {
                if (itemData != null && itemData.mId == id) {
                    return itemData.mItemId;
                }
            }
        }
        return -1;
    }

    /**
     * 从所有商品列表中得到同一商品的列表
     *
     * @param itemId
     * @return
     */
    private List<TbTvShoppingItemData> getTheItemDataListFromItemId(long itemId) {
        List<TbTvShoppingItemData> theItemDataList = new ArrayList<TbTvShoppingItemData>();
        if (itemId > 0 && mTbTvShoppingItemDataList != null) {
            for (TbTvShoppingItemData itemData : mTbTvShoppingItemDataList) {
                if (itemData != null && itemData.mItemId == itemId) {
                    theItemDataList.add(itemData);
                }
            }
        }

        return theItemDataList;
    }

    /**
     * 改变商品的显示状态
     *
     * @param id
     * @param show
     * @param shopType
     */
    private void changedShopStatus(long id, boolean show, ShopType shopType) {
        if (mOnTbTvShoppingHideListenerList == null) {
            AppDebug.i(TAG, TAG + ".changedShopStatus.mOnTbTvShoppingHideListenerList == null");
            return;
        }
        long itemId = getItemId(id);

        AppDebug.i(TAG, TAG + ".changedShopStatus show=" + show + " id = " + id + ", itemId = " + itemId
                + " mCurrShowId=" + mCurrShowId);

        if (itemId == DEFAULT_SHOW_SHOP_ID) {
            return;
        }

        if (show) {
            // 只有不相同的才处理
            if (mCurrShowId != id) {
                if (mTbTvShoppingActionListener != null) {
                    mTbTvShoppingActionListener.onShowShop(id, itemId, shopType);
                }
            }
        } else {
            // 只有当前显示着的才会隐藏
            if (mCurrShowId != DEFAULT_SHOW_SHOP_ID && mCurrShowId == id) {
                mCurrShowId = DEFAULT_SHOW_SHOP_ID;
                if (mTbTvShoppingActionListener != null) {
                    mTbTvShoppingActionListener.onHideShop(id, itemId);
                }
                for (OnTbTvShoppingHideListener listener : mOnTbTvShoppingHideListenerList) {
                    listener.onHideShop(id);
                }
            }
        }
    }

    /**
     * 根据id获取itemId
     *
     * @param id
     * @return
     */
    private long getItemId(long id) {
        if (mTbTvShoppingItemDataList != null && mTbTvShoppingItemDataList.size() > 0) {
            for (TbTvShoppingItemData itemData : mTbTvShoppingItemDataList) {
                if (itemData.mId == id) {
                    return itemData.mItemId;
                }
            }
        }
        return DEFAULT_SHOW_SHOP_ID;
    }

    /**
     * 需要请求新的数据
     *
     * @param receiverData
     */
    private void requestShoppingData(TbTvShoppingReceiverData receiverData) {
        if (mTbTvShoppingActionListener != null) {
            mTbTvShoppingActionListener.onRequestData(receiverData);
        }
    }

    /**
     * 退出边看边购
     */
    private void exitVideo() {
        AppDebug.i(TAG, TAG + ".exitVideo");
        List<TbTvShoppingItemData> favorItemList = getFavorItemList();
        // 只有没显示过最后的商品列表并且有收藏数就给提示框
        if (favorItemList != null && favorItemList.size() > 0) {
            // 退出时显示提示框
            if (mTbTvShoppingActionListener != null) {
                mTbTvShoppingActionListener.onShowExitNotify(favorItemList, mCurrTvShopReceiverData);
            }
        }
    }

    /**
     * 重置（只清除一部分数据，相关信息不清除，后续可以会再次使用）
     */
    public void reset() {
        AppDebug.i(TAG, TAG + ".reset");
        if (mOnTbTvShoppingHideListenerList != null) {
            mOnTbTvShoppingHideListenerList.clear();
            mOnTbTvShoppingHideListenerList = null;
        }
        mTbTvShoppingActionListener = null;
        mCurrTvShopReceiverData = null;
        mTbTvShopping = null;
        mTbTvShopNeedResumeInfo = null;
        mIsExit = true;
    }

    /**
     * 商品是否为空
     *
     * @return
     */
    public boolean shopIsEmpty() {
        if (mTbTvShoppingItemDataList == null || mTbTvShoppingItemDataList.size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * activity正在启动
     *
     * @param launching
     */
    public void setActivityLaunchState(boolean launching) {
        mActivityLaunching = launching;
    }

    /**
     * 返回activity启动状态
     *
     * @return
     */
    public boolean getActivityLaunchState() {
        return mActivityLaunching;
    }

    /**
     * 测试数据
     */
    public List<TbTvShoppingItemBo> testTvShoppingData() {
        String imageList[] = {
                "http://image.tv.yunos.com/develop/attachment/tv2d0/program/advert/e48dc2f2277bd0e88b5436edad0ecf3a.JPG",
                "http://image.tv.yunos.com/develop/attachment/tv2d0/program/advert/3aca4213bba0d28092217769bf71953f.png",
                "http://image.tv.yunos.com/develop/attachment/tv2d0/program/advert/e48dc2f2277bd0e88b5436edad0ecf3a.JPG",
                "http://image.tv.yunos.com/develop/attachment/tv2d0/program/advert/3e43ed627a7b6d535a772d3b6c12d0a4.png",
                "http://image.tv.yunos.com/develop/attachment/tv2d0/program/advert/e48dc2f2277bd0e88b5436edad0ecf3a.JPG",
                ""};
        int count = imageList.length;
        List<TbTvShoppingItemBo> itemList = new ArrayList<TbTvShoppingItemBo>();
        int diff = 12000;
        int time = diff;
        int itemId = 1;
        for (int i = 0; i < count; i++) {
            TbTvShoppingItemBo itemBo = new TbTvShoppingItemBo();
            itemBo.setStartTime(time);
            time += diff;
            itemBo.setEndTime(time);
            if (i == count - 1) {
                itemBo.setItemId(0);
                itemBo.setType("LIST");
            } else {
                itemBo.setItemId(itemId);
                itemBo.setType("SINGLE");
            }
            itemBo.setItemImage(imageList[i]);
            time += diff;
            itemId++;
            itemList.add(itemBo);
            AppDebug.i(TAG, "testTvShoppingData itemId=" + itemBo.getItemId() + " type=" + itemBo.getShopType()
                    + " startTime=" + itemBo.getStartTime() + " endTime=" + itemBo.getEndTime());
        }
        return itemList;
    }

    /**
     * @return the mCurrTvShopReceiverData
     */
    public TbTvShoppingReceiverData getmCurrTvShopReceiverData() {
        return mCurrTvShopReceiverData;
    }

    /**
     * @param mCurrTvShopReceiverData the mCurrTvShopReceiverData to set
     */
    public void setmCurrTvShopReceiverData(TbTvShoppingReceiverData mCurrTvShopReceiverData) {
        this.mCurrTvShopReceiverData = mCurrTvShopReceiverData;
    }

    /**
     * @param mLastState the mLastState to set
     */
    public void setmLastState(int mLastState) {
        this.mLastState = mLastState;
    }

    /**
     * 设置需要重新启动的界面信息
     *
     * @param tbTvShopNeedResumeInfo
     */
    public void setTbTvShopNeedResumeInfo(TbTvShopNeedResumeInfo tbTvShopNeedResumeInfo) {
        mTbTvShopNeedResumeInfo = tbTvShopNeedResumeInfo;
    }

    /**
     * 获取需要重新启动的界面信息
     *
     * @return
     */
    public TbTvShopNeedResumeInfo getTbTvShopNeedResumeInfo() {
        return mTbTvShopNeedResumeInfo;
    }

    /**
     * 边看边购的动作监听方法
     *
     * @author tingmeng.ytm
     */
    public interface TbTvShoppingActionListener {

        public void onRequestData(TbTvShoppingReceiverData receiverData);

        public void onShowShop(long id, long itemId, ShopType shopType);

        public void onResumeShop(TbTvShopNeedResumeInfo tbTvShopNeedResumeInfo);

        public void onHideShop(long id, long itemId);

        public void onShowExitNotify(List<TbTvShoppingItemData> favorDateList, TbTvShoppingReceiverData receiverData);
    }

    public interface OnTbTvShoppingHideListener {

        public void onHideShop(long id);
    }

    /**
     * 重新恢复界面名称
     * Class Descripton.
     *
     * @author mi.cao
     * @data 2015年6月27日 下午9:15:10
     */
    public enum NEED_RESUME_PAGE {
        TVSHOP_DETAIL_PAGE, TVSHOP_ALL_COLLECT_PAGE, TVSHOP_MY_COLLECT_PAGE
    }

    /**
     * 当影视界面onResume时，将要重新恢复的界面信息
     * Class Descripton.
     *
     * @author mi.cao
     * @data 2015年6月27日 下午9:02:27
     */
    public static class TbTvShopNeedResumeInfo {

        public NEED_RESUME_PAGE mNeedResumePage;// 需要请求的页面
        public long mId; // 卡片id
        public long mItemId;// 商品id
        public int mAllCollectDefaultIndex;// 所有商品列表默认索引
        public TBDetailResultVO mTBDetailResultVO;// 详情界面需要该数据
        public boolean mShopIsFavor;// 商品是否收藏，详情需要
        public TbTvShoppingReceiverData mCurVideoData;
    }

    /**
     * 边看边购的每个商品相关信息
     *
     * @author tingmeng.ytm
     */
    public static class TbTvShoppingItemData {

        private long mId; // 唯一的id
        private long mItemId; // 商品的ID
        private String itemActionUri;
        private String mShopDetailImageUrl; // 详情页的图片url
        private TbTvShoppingItemBo mTbTvShoppingItemBo;
        private boolean mIsShowed; // 是否在此区域内显示过了
        private boolean mIsVisible; // 商品是否在显示状态
        private boolean mIsFavor; // 商品是否被收藏关注过

        /**
         * 设置商品信息的唯一ID
         *
         * @param id
         */
        public void setId(long id) {
            mId = id;
        }

        /**
         * 获取商品信息的唯一ID
         *
         * @return
         */
        public long getId() {
            return mId;
        }

        /**
         * 取得商品的ID
         *
         * @return
         */
        public long getItemId() {
            return mItemId;
        }

        /**
         * 取得配置跳转链接
         *
         * @return
         */
        public String getItemActionUri() {
            return itemActionUri;
        }

        /**
         * 取得详情页的图片url地址
         *
         * @return
         */
        public String getShopDetailImageUrl() {
            return mShopDetailImageUrl;
        }

        /**
         * 取得商品的原始数据
         *
         * @return
         */
        public TbTvShoppingItemBo getTbTvShoppingItemBo() {
            return mTbTvShoppingItemBo;
        }

        /**
         * 商品是否曾经显示过
         *
         * @return
         */
        public boolean isShowed() {
            return mIsShowed;
        }

        /**
         * 是否显示中
         *
         * @return
         */
        public boolean isVisible() {
            return mIsVisible;
        }

        /**
         * 是否已经加入收藏并且关注
         *
         * @return
         */
        public boolean isFavor() {
            return mIsFavor;
        }
    }
}
