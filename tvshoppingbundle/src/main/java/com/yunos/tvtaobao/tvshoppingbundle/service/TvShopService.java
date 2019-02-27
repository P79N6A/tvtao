/**
 * $
 * PROJECT NAME: MovieBuy
 * PACKAGE NAME: com.example.moviebuy
 * FILE NAME: TestServece.java
 * CREATED TIME: 2014年12月29日
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.tvshoppingbundle.service;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.assist.FailReason;
import com.tvlife.imageloader.core.display.RoundedBitmapDisplayer;
import com.tvlife.imageloader.core.listener.SimpleImageLoadingListener;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.request.bo.TbTvShoppingItemBo;
import com.yunos.tvtaobao.biz.request.bo.TbTvShoppingItemBo.ShopType;
import com.yunos.tvtaobao.tvshoppingbundle.bean.TbTvShoppingReceiverData;
import com.yunos.tvtaobao.tvshoppingbundle.bean.TbTvShoppingReceiverData.VideoPlayType;
import com.yunos.tvtaobao.tvshoppingbundle.dialog.TvShopAllCollectDialog;
import com.yunos.tvtaobao.tvshoppingbundle.dialog.TvShopDialog;
import com.yunos.tvtaobao.tvshoppingbundle.dialog.TvShopMyCollectDialog;
import com.yunos.tvtaobao.tvshoppingbundle.manager.TbTvShoppingManager;
import com.yunos.tvtaobao.tvshoppingbundle.manager.TbTvShoppingManager.NEED_RESUME_PAGE;
import com.yunos.tvtaobao.tvshoppingbundle.manager.TbTvShoppingManager.TbTvShopNeedResumeInfo;
import com.yunos.tvtaobao.tvshoppingbundle.manager.TbTvShoppingManager.TbTvShoppingActionListener;
import com.yunos.tvtaobao.tvshoppingbundle.manager.TbTvShoppingManager.TbTvShoppingItemData;
import com.yunos.tvtaobao.tvshoppingbundle.request.TvShopBusinessRequest;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2014年12月29日 上午10:28:44
 */
public class TvShopService extends Service {

    final private String TAG = "TvShopServece";

    private static final String ACTION_FLOAT_HOME_VISIBILITY_CHANGED = "com.yunos.tv.homeshell.FloatHomeVisibilityChanged";
    private static final String PROPERTY_VISIBILITY = "visibility";
    private static final int HANDLER_REQUEST_PROGRAM_IDS = 100;// 请求所以视频列表的id的handler参数

    private TbTvShoppingManager mTbTvShoppingManager;
    private TbTvShoppingActionListener mTbTvShoppingActionListener;
    // 正在请求数据
    private boolean mRequesting;
    // 边看边买使用Dialog的显示方式
    private boolean mShowDialog;
    // 影视acitivty状态onresume/onpause
    private String mActivityState;
    // 是否显示桌面的浮层
    private boolean mShowHomeView;
    private BroadcastReceiver mBroadcastReceiver;
    private ArrayList<String> mProgramIdList;
    private MyHandler mMyHandler;
    private TbTvShoppingReceiverData mTVShopRequestData;

    @Override
    public void onCreate() {
        AppDebug.i("test", "test.onCreate");
        mRequesting = false;
        mActivityState = "";
        mShowHomeView = false;
        mShowDialog = true;
        mMyHandler = new MyHandler(this);

        mBroadcastReceiver = new BroadcastReceiver() {

            private String SYSTEM_DIALOG_REASON_KEY = "reason";
            private String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                AppDebug.i(TAG, TAG + ".onReceive.intent = " + intent);
                if (action.equals(ACTION_FLOAT_HOME_VISIBILITY_CHANGED)) {
                    mShowHomeView = intent.getBooleanExtra(PROPERTY_VISIBILITY, false);
                    AppDebug.i(TAG, TAG + ".onReceive.mShowHomeView = " + mShowHomeView);
                    if (mShowHomeView) {
                        TbTvShoppingManager.getIntance().clearShowingDialog();
                    }
                } else {
                    String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                    AppDebug.i(TAG, TAG + ".onReceive.reason = " + reason);
                    if (!TextUtils.isEmpty(reason) && SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                        AppDebug.i(TAG, TAG + ".onReceive.click homeKey");//按Home键后清除所以数据(现在不清除)
                        //                        TbTvShoppingManager.getIntance().clearShowingDialog();
                        //                        TbTvShoppingManager.getIntance().reset();
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter(ACTION_FLOAT_HOME_VISIBILITY_CHANGED);
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);//监听home键
        registerReceiver(mBroadcastReceiver, filter);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        AppDebug.i(TAG, TAG + ". onDestroy");
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }

        if (mTbTvShoppingManager != null) {
            mTbTvShoppingManager.unregisterTbTvShoppingListener(mTbTvShoppingActionListener);
            mTbTvShoppingManager = null;
        }

        if (mMyHandler != null) {
            mMyHandler.removeCallbacksAndMessages(null);
            mMyHandler = null;
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        start(intent, startId);
        return START_NOT_STICKY;
    }

    private void start(final Intent intent, int startId) {
        AppDebug.i(TAG, TAG + ".onStart.intent = " + intent + ".mShowHomeView = " + mShowHomeView);
        if (intent == null || mShowHomeView) {
            return;
        }

        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            AppDebug.e(TAG, TAG + ".onStart.bundle == null");
            return;
        }

        if (CloudUUIDWrapper.getCloudUUID() != null) {
            AppDebug.i(TAG, TAG + ".onStart.uuid exist");
        } else {
            return;
        }

        boolean isExit = TbTvShoppingManager.isExit();
        AppDebug.i(TAG, TAG + ".onStart isExit=" + isExit);
        if (isExit) {
            TbTvShoppingManager.destoryInstance();
            mTbTvShoppingManager = TbTvShoppingManager.getIntance();
            mTbTvShoppingActionListener = new TbTvShoppingActionListener() {

                @Override
                public void onShowShop(long id, long itemId, ShopType shopType) {
                    if (mShowDialog) {
                        onShowShopItem(id, itemId, shopType);
                    } else {
                        onShowShopItemActivity(id, itemId, shopType);
                    }
                }

                @Override
                public void onResumeShop(TbTvShopNeedResumeInfo tbTvShopNeedResumeInfo) {
                    if (tbTvShopNeedResumeInfo != null) {// 只有3.0之后才会执行该函数
                        // 判断并启动界面
                        boolean activityLaunchState = mTbTvShoppingManager.getActivityLaunchState();
                        AppDebug.i(TAG, TAG + ".onStart.onResumeShop, activityLaunchState = " + activityLaunchState
                                + ".mNeedResumePage = " + tbTvShopNeedResumeInfo.mNeedResumePage);
                        if (activityLaunchState) {// 如果正在启动，则返回，否则设置启动状态为true;
                            return;
                        } else {
                            mTbTvShoppingManager.setActivityLaunchState(true);
                        }

                        if (tbTvShopNeedResumeInfo.mNeedResumePage == NEED_RESUME_PAGE.TVSHOP_DETAIL_PAGE) {// 详情
                            TvShopDialog tvShopDialog = new TvShopDialog.Builder(TvShopService.this)
                                    .setTvShopId(tbTvShopNeedResumeInfo.mId)
                                    .setTvShopItemId(tbTvShopNeedResumeInfo.mItemId).setShowDetailPage(true)
                                    .setTBDetailResultVO(tbTvShopNeedResumeInfo.mTBDetailResultVO)
                                    .setShopIsFavor(tbTvShopNeedResumeInfo.mShopIsFavor).create();
                            tvShopDialog.show();
                        } else if (tbTvShopNeedResumeInfo.mNeedResumePage == NEED_RESUME_PAGE.TVSHOP_ALL_COLLECT_PAGE) { // 显示所有商品列表
                            TvShopAllCollectDialog tvShopAllCollectDialog = new TvShopAllCollectDialog.Builder(
                                    TvShopService.this).setAllCollectId(tbTvShopNeedResumeInfo.mId)
                                    .setAllCollectDefaultIndex(tbTvShopNeedResumeInfo.mAllCollectDefaultIndex).create();
                            tvShopAllCollectDialog.show();
                        } else {
                            mTbTvShoppingManager.setActivityLaunchState(false);// 恢复显示状态
                        }

                        mTbTvShoppingManager.setTbTvShopNeedResumeInfo(null);// 清除需要重启的信息
                    }
                }

                @Override
                public void onShowExitNotify(List<TbTvShoppingItemData> favorItemList,
                        TbTvShoppingReceiverData receiverData) {
                    AppDebug.i("test", "test.onShowExitNotify");
                    if (mShowDialog) {
                        onShowExitPage(receiverData);
                    } else {
                        onShowExitPageActivity(receiverData);
                    }
                }

                @Override
                public void onRequestData(TbTvShoppingReceiverData receiverData) {
                    AppDebug.i("test", "test.onRequestData, receiverData = " + receiverData + ", mProgramIdList = "
                            + mProgramIdList);
                    mTVShopRequestData = receiverData;
                    // 请求数据
                    if (mTVShopRequestData != null) {
                        if (mProgramIdList != null && mProgramIdList.size() > 0) {
                            // 存在影视列表，则请求
                            getTvShopData();
                        } else {
                            getAllProgramIds(true);
                        }
                    }
                }

                @Override
                public void onHideShop(long id, long itemId) {
                }
            };
            mTbTvShoppingManager.registerTbTvShoppingListener(mTbTvShoppingActionListener);
        }

        if (mTbTvShoppingManager != null) {
            boolean resume = false;// 从onPause到onResume，界面需要恢复
            String activityState = bundle.getString(TbTvShoppingReceiverData.TAG_ACTIVITY_STATE, "");
            if (mActivityState.equals(TbTvShoppingReceiverData.STATE_ACTIVITY_RESUME)) {
                if (activityState.equals(TbTvShoppingReceiverData.STATE_ACTIVITY_PAUSE)) {
                    mActivityState = TbTvShoppingReceiverData.STATE_ACTIVITY_PAUSE;
                }
            } else if (mActivityState.equals(TbTvShoppingReceiverData.STATE_ACTIVITY_PAUSE)) {
                if (activityState.equals(TbTvShoppingReceiverData.STATE_ACTIVITY_RESUME)) {
                    mActivityState = TbTvShoppingReceiverData.STATE_ACTIVITY_RESUME;
                    resume = true;
                }
            } else {
                mActivityState = activityState;
                if (activityState.equals(TbTvShoppingReceiverData.STATE_ACTIVITY_RESUME)) {
                    resume = true;
                }
            }

            String from_app = bundle.getString(TbTvShoppingReceiverData.TAG_APP_FROM, "");
            // 如果有包名传过来，不管是我们影视还是第三方，均使用dialog的实现方式。
            // 如果后续影视2.x或3.0版本有任何更新，如果逻辑与2.7一样就不发包名，如果与3.0一致，就发包名
            if (!TextUtils.isEmpty(from_app)) {
                mShowDialog = true;
            } else {// 如果没有包名发过来，说明是老版本的影视。
                mShowDialog = false;
            }

            if (Config.isDebug()) {
                AppDebug.i(TAG, TAG + ".onStart.mShowDialog = " + mShowDialog + ".from_app = " + from_app);
            }

            if (mShowDialog) {// 3.0的影视，使用Dialog界面
                if (Config.isDebug()) {
                    AppDebug.i(TAG, TAG + ".onStart.mActivityState = " + mActivityState + ",curactivityState = "
                            + activityState + ".resume = " + resume);
                }
                // 如果界面处于onpause状态，则清楚所有对话框，否则显示界面。
                if (mActivityState.equals(TbTvShoppingReceiverData.STATE_ACTIVITY_PAUSE)) {
                    mTbTvShoppingManager.clearShowingDialog();
                } else {
                    mTbTvShoppingManager.receiverNewData(intent.getExtras(), resume);
                }
            } else {// 2.x的影视
                mTbTvShoppingManager.receiverNewData(intent.getExtras(), false);
            }
        }

        super.onStart(intent, startId);
    }

    /**
     * 当需要显示推荐商品时
     * @param itemId
     * @param shopType
     */
    private void onShowShopItem(final long id, final long itemId, ShopType shopType) {
        if (mTbTvShoppingManager == null) {
            return;
        }

        AppDebug.i("test", "onShowShopItem test.shopType = " + shopType);

        if (shopType.equals(ShopType.LIST)) {
            boolean needHideShop = mTbTvShoppingManager.getNeedHideShop();
            AppDebug.i(TAG, TAG + ".onStart.onShowShopItem, needHideShop = " + needHideShop);
            if (!needHideShop) {
                boolean activityLaunchState = mTbTvShoppingManager.getActivityLaunchState();
                AppDebug.i(TAG, TAG + ".onStart.onShowShopItem, activityLaunchState = " + activityLaunchState);
                if (activityLaunchState) {// 如果正在启动，则返回，否则设置启动状态为true;
                    return;
                } else {
                    mTbTvShoppingManager.setActivityLaunchState(true);
                }
                TvShopAllCollectDialog tvShopAllCollectDialog = new TvShopAllCollectDialog.Builder(this)
                        .setAllCollectId(id).setAllCollectDefaultIndex(0).create();
                tvShopAllCollectDialog.show();
            }
        } else {
            final TbTvShoppingItemBo itemBo = mTbTvShoppingManager.getTbTvShoppingItem(id);
            if (Config.isDebug()) {
                AppDebug.i(TAG, TAG + ". onStart.onShowShopItem itemId=" + itemId + ", id = " + id + " shopType="
                        + shopType + ", imageUrl = " + itemBo.getItemImage());
            }
            ImageLoaderManager imageLoaderManager = ImageLoaderManager.getImageLoaderManager(TvShopService.this);
            DisplayImageOptions options = new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(0))
                    .cacheInMemory(true).cacheOnDisc(false).bitmapConfig(Bitmap.Config.RGB_565).build();
            imageLoaderManager.loadImage(itemBo.getItemImage(), options, new SimpleImageLoadingListener() {

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (Config.isDebug()) {
                        AppDebug.i(TAG,
                                TAG + ".onStart.onShowShopItem onLoadingComplete, imageUrl = " + itemBo.getItemImage());
                    }

                    if (mTbTvShoppingManager != null) {
                        boolean needHideShop = mTbTvShoppingManager.getNeedHideShop();
                        AppDebug.i(TAG, TAG + ".onStart.onShowShopItem onLoadingComplete, needHideShop = "
                                + needHideShop);
                        if (!needHideShop) {
                            boolean activityLaunchState = mTbTvShoppingManager.getActivityLaunchState();
                            AppDebug.i(TAG, TAG + ".onStart.onShowShopItem onLoadingComplete, activityLaunchState = "
                                    + activityLaunchState);
                            if (activityLaunchState) {// 如果正在启动，则返回，否则设置启动状态为true;
                                return;
                            } else {
                                mTbTvShoppingManager.setActivityLaunchState(true);
                            }
                            TvShopDialog tvShopDialog = new TvShopDialog.Builder(TvShopService.this).setTvShopId(id)
                                    .setTvShopItemId(itemId).create();
                            tvShopDialog.show();
                        }
                    }

                    super.onLoadingComplete(imageUri, view, loadedImage);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    if (Config.isDebug()) {
                        AppDebug.i(TAG,
                                TAG + ".onStart.onShowShopItem onLoadingFailed, imageUrl = " + itemBo.getItemImage());
                    }
                    super.onLoadingFailed(imageUri, view, failReason);
                }
            });
        }
    }

    /**
     * 当需要显示推荐商品时(影视3.0之前的版本)
     * @param itemId
     * @param shopType
     */
    private void onShowShopItemActivity(long id, final long itemId, ShopType shopType) {
//        if (mTbTvShoppingManager == null) {
//            return;
//        }
//        final Intent intent = new Intent();
//        if (shopType.equals(ShopType.SINGLE)) {
//            intent.setClass(getApplicationContext(), TvShopActivity.class);
//        } else {
//            intent.setClass(getApplicationContext(), TvShopAllCollectActivity.class);
//        }
//        intent.putExtra(BaseConfig.INTENT_KEY_TVSHOP_ID, id);
//        intent.putExtra(BaseConfig.INTENT_KEY_ITEM_ID, itemId);
//        final TbTvShoppingItemBo itemBo = mTbTvShoppingManager.getTbTvShoppingItem(id);
//
//        AppDebug.i("test", "onShowShopItemActivity test.show");
//        if (Config.isDebug()) {
//            AppDebug.i(TAG, TAG + ". onStart.onShowShopItemActivity itemId=" + itemId + ", id = " + id + " shopType="
//                    + shopType + ", imageUrl = " + itemBo.getItemImage());
//        }
//
//        ImageLoaderManager imageLoaderManager = ImageLoaderManager.getImageLoaderManager(TvShopService.this);
//        DisplayImageOptions options = new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(0))
//                .cacheInMemory(true).cacheOnDisc(false).bitmapConfig(Bitmap.Config.RGB_565).build();
//        imageLoaderManager.loadImage(itemBo.getItemImage(), options, new SimpleImageLoadingListener() {
//
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                AppDebug.i(TAG,
//                        TAG + ".onStart.onShowShopItemActivity onLoadingComplete, imageUrl = " + itemBo.getItemImage());
//                if (mTbTvShoppingManager != null) {
//                    boolean activityLaunchState = mTbTvShoppingManager.getActivityLaunchState();
//                    AppDebug.i(TAG, TAG + ".onStart.onShowShopItemActivity onLoadingComplete, activityLaunchState = "
//                            + activityLaunchState);
//                    if (activityLaunchState) {// 如果正在启动，则返回，否则设置启动状态为true;
//                        return;
//                    } else {
//                        mTbTvShoppingManager.setActivityLaunchState(true);
//                    }
//                }
//                try {// 只有图片下载成功才进入界面
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                } catch (ActivityNotFoundException e) {
//                    mTbTvShoppingManager.setActivityLaunchState(false);
//                    e.printStackTrace();
//                }
//                super.onLoadingComplete(imageUri, view, loadedImage);
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                AppDebug.i(TAG,
//                        TAG + ".onStart.onShowShopItemActivity onLoadingFailed, imageUrl = " + itemBo.getItemImage());
//                super.onLoadingFailed(imageUri, view, failReason);
//            }
//        });
    }

    /**
     * 影视退出全屏时显示我的收藏界面,影视3.0之后的版本
     * @param receiverData
     */
    private void onShowExitPage(TbTvShoppingReceiverData receiverData) {
        if (mTbTvShoppingManager == null) {
            return;
        }

        boolean activityLaunchState = mTbTvShoppingManager.getActivityLaunchState();
        AppDebug.i(TAG, TAG + ".onShowExitPage, activityLaunchState = " + activityLaunchState);
        if (activityLaunchState) {// 如果正在启动，则返回，否则设置启动状态为true;
            return;
        } else {
            mTbTvShoppingManager.setActivityLaunchState(true);
        }

        TvShopMyCollectDialog tvShopMyCollectDialog = new TvShopMyCollectDialog.Builder(TvShopService.this)
                .setCurVideoData(receiverData).create();
        tvShopMyCollectDialog.show();
    }

    /**
     * 影视退出全屏时显示我的收藏界面,影视3.0之前的版本
     * @param receiverData
     */
    private void onShowExitPageActivity(TbTvShoppingReceiverData receiverData) {
//        if (mTbTvShoppingManager == null) {
//            return;
//        }
//
//        Intent intent = new Intent();
//        boolean activityLaunchState = mTbTvShoppingManager.getActivityLaunchState();
//        AppDebug.i(TAG, TAG + ".onShowExitPageActivity, activityLaunchState = " + activityLaunchState);
//        if (activityLaunchState) {// 如果正在启动，则返回，否则设置启动状态为true;
//            return;
//        } else {
//            mTbTvShoppingManager.setActivityLaunchState(true);
//        }
//
//        try {
//            intent.setClass(getApplicationContext(), TvShopMyCollectActivity.class);
//            if (receiverData != null) {
//                intent.putExtra("videoData", receiverData);
//            }
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//        } catch (ActivityNotFoundException e) {
//            mTbTvShoppingManager.setActivityLaunchState(false);
//            e.printStackTrace();
//        }
    }

    /**
     * 获取影视边看边买数据
     */
    private void getTvShopData() {
        AppDebug.i("test", "test.getTvShopData, mTVShopRequestData = " + mTVShopRequestData);
        if (mTVShopRequestData == null) {
            return;
        }

        String videoId = mTVShopRequestData.getVideoId();
        String videoSubId = mTVShopRequestData.getVideoSubid();
        VideoPlayType videoType = mTVShopRequestData.getType();
        String from_app = mTVShopRequestData.getFromApp();

        boolean isLive = false;
        boolean isNews = false;
        if (videoType == VideoPlayType.LIVE) {
            isLive = true;// 直播
        } else if (videoType == VideoPlayType.ZIXUN || videoType == VideoPlayType.PLAYBACK) {
            isNews = true;
        }

        AppDebug.i("test", "test.getTvShopData videoId=" + videoId + ".videoSubId = " + videoSubId + ", videoType = "
                + videoType + ", isLive = " + isLive + ", isNews = " + isNews + ", from_app = " + from_app);

        boolean have = getHaveTvShopData(videoId);
        AppDebug.i("test", "test.getTvShopData, have = " + have);
        if (!have) {// 如果播放的影视不在影视列表中，则返回
            return;
        }

        if (mRequesting) {
            return;
        }
        mRequesting = true;
        if (!TextUtils.isEmpty(videoId)) {
            TvShopBusinessRequest.getBusinessRequest().requestGetTvShopTimeItemList(videoId, videoSubId, isLive,
                    isNews, from_app,
                    new GetTvShopTimeItemListRequestListener(new WeakReference<TvShopService>(TvShopService.this)));
        }
    }

    /**
     * 获取所有具有边看边买数据的VideoId
     */
    private void getAllProgramIds(boolean needRequestTvshopData) {
        AppDebug.i(TAG, TAG + ".getAllProgramIds.needRequestTvshopData = " + needRequestTvshopData);
        TvShopBusinessRequest.getBusinessRequest().requestGetTvShopAllProgramIds(
                new GetTvShopAllProgramIdsRequestListener(this, needRequestTvshopData));
    }

    /**
     * 获取该影视中是否存在边看边买数据
     */
    private boolean getHaveTvShopData(String vidioId) {
        if (mProgramIdList == null || mProgramIdList.size() == 0 || TextUtils.isEmpty(vidioId)) {
            return false;
        }

        for (int i = 0; i < mProgramIdList.size(); i++) {
            String id = mProgramIdList.get(i);
            if (!TextUtils.isEmpty(id) && vidioId.equals(id)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 设置网络获取的数据
     * @param tbTvShoppingItemBoList
     */
    private void setShoppingData(List<TbTvShoppingItemBo> tbTvShoppingItemBoList) {
        if (mTbTvShoppingManager != null) {
            mTbTvShoppingManager.initTvShoppingData(tbTvShoppingItemBoList);
        }
    }

    /**
     * handler处理类
     * Class Descripton.
     * @author mi.cao
     * @data 2015年9月7日 下午6:56:06
     */
    private static class MyHandler extends Handler {

        private WeakReference<TvShopService> ref;

        public MyHandler(TvShopService servece) {
            ref = new WeakReference<TvShopService>(servece);
        }

        @Override
        public void handleMessage(Message msg) {
            TvShopService service = ref.get();
            if (service == null) {
                return;
            }

            service.getAllProgramIds(false);// 请求含有边看边买所有影视的id

            super.handleMessage(msg);
        }
    }

    /**
     * 获取视频时间点
     * Class Descripton.
     * @version
     * @author mi.cao
     * @data 2015年1月13日 下午5:54:30
     */
    private static class GetTvShopTimeItemListRequestListener implements RequestListener<ArrayList<TbTvShoppingItemBo>> {

        private WeakReference<TvShopService> ref;

        public GetTvShopTimeItemListRequestListener(WeakReference<TvShopService> servece) {
            ref = servece;
        }

        @Override
        public void onRequestDone(ArrayList<TbTvShoppingItemBo> data, int resultCode, String msg) {
            TvShopService service = ref.get();
            if (service != null) {
                AppDebug.i(service.TAG, service.TAG + ",GetTvShopTimeItemListRequestListener resultCode = "
                        + resultCode + ", msg = " + msg + " ,data = " + data);
                service.mRequesting = false;
                if (resultCode == 200) {
                    service.setShoppingData(data);
                    if (service.mTbTvShoppingManager != null) {
                        service.mTbTvShoppingManager.tbsVideoPlayAndPause();
                    }
                }
            }
        }
    }

    /**
     * 获取所有配置有边看边买视频id
     * Class Descripton.
     * @version
     * @author mi.cao
     * @data 2015年9月7日 下午4:48:59
     */
    private static class GetTvShopAllProgramIdsRequestListener implements RequestListener<ArrayList<String>> {

        private WeakReference<TvShopService> ref;
        private boolean mNeedRequestTvshopData;

        public GetTvShopAllProgramIdsRequestListener(TvShopService servece, boolean needRequestTvshopData) {
            ref = new WeakReference<TvShopService>(servece);
            mNeedRequestTvshopData = needRequestTvshopData;
        }

        @Override
        public void onRequestDone(ArrayList<String> data, int resultCode, String msg) {
            TvShopService service = ref.get();
            if (service != null) {
                AppDebug.i(service.TAG, service.TAG + ",GetTvShopAllProgramIdsRequestListener resultCode = "
                        + resultCode + ", msg = " + msg + "data = " + data + ", mNeedRequestTvshopData = "
                        + mNeedRequestTvshopData);
                if (service.mMyHandler != null && service.mMyHandler.hasMessages(HANDLER_REQUEST_PROGRAM_IDS)) {
                    service.mMyHandler.removeMessages(HANDLER_REQUEST_PROGRAM_IDS);
                }

                if (resultCode == 200) {// 请求成功 则赋值
                    service.mProgramIdList = data;
                }

                if (resultCode == 1) {// 如果是网络无连接的错误，10分钟去请求一次
                    if (service.mMyHandler != null) {
                        service.mMyHandler.sendEmptyMessageDelayed(HANDLER_REQUEST_PROGRAM_IDS, 10 * 60 * 1000);
                    }
                } else {// 其它的一个小时一次
                    if (service.mMyHandler != null) {
                        service.mMyHandler.sendEmptyMessageDelayed(HANDLER_REQUEST_PROGRAM_IDS, 1 * 3600 * 1000);
                    }
                }

                if (mNeedRequestTvshopData) {
                    service.getTvShopData();
                }
            }
        }
    }



}
