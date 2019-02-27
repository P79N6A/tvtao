/**
 * $
 * PROJECT NAME: TvShopping
 * PACKAGE NAME: com.yunos.tvshopping.dialog
 * FILE NAME: TvShopDialog.java
 * CREATED TIME: 2015年6月25日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.tvshoppingbundle.dialog;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taobao.detail.clientDomain.TBDetailResultVO;
import com.taobao.detail.domain.base.PriceUnit;
import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.assist.FailReason;
import com.tvlife.imageloader.core.display.RoundedBitmapDisplayer;
import com.tvlife.imageloader.core.listener.SimpleImageLoadingListener;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.FocusTextView;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.AppHandler;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.TbTvShoppingItemBo;
import com.yunos.tvtaobao.biz.request.core.ServiceCode;
//import com.yunos.tvtaobao.blitz.account.LoginHelper;
import com.yunos.tvtaobao.tvshoppingbundle.R;
import com.yunos.tvtaobao.tvshoppingbundle.manager.TbTvShoppingManager;
import com.yunos.tvtaobao.tvshoppingbundle.manager.TbTvShoppingManager.*;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2015年6月25日 下午3:49:53
 */
public class TvShopDialog extends TvShopBaseDialog implements OnClickListener {

    private String TAG = "TvShopDialog";
    private Context mContext;
    private final static int SHOW_PROGRESS = 0;// 显示progressbar
    private final static int SHOW_DETAIL = 1; // 显示详情界面
    private final static int SHOW_FAILED = 2; // 显示加载失败界面

    private final static int GOODS_IMAGE_VIEW_ID = 0;//  推荐商品ImageView ID
    private final static int GOODS_DETAIL_LAYOUT_ID = 1;// 详情界面layout id

    private RelativeLayout mTvshopRootLayout;
    // 详情界面layout根控件
    private FocusPositionManager mFocusDetailManager;
    // 推荐页面
    private RelativeLayout mGoodsImageLayout;
    // 推荐页面图片view
    private ImageView mGoodsImageView;

    // 详情界面
    private LinearLayout mGoodsDetailLayout;
    // 详情加载失败界面
    private LinearLayout mGoodsDetailLoadFail;
    // 详情加载中界面
    private LinearLayout mProgressBarLayout;

    // 商品主图
    private ImageView mGoodsDetailImage;
    // 商品名称
    private TextView mGoodsTitle;
    // 商品价格
    private TextView mGoodsPrice;
    // 商品原始价格
    private TextView mGoodsOriginPrice;
    // focus框背景
    private FocusTextView mCollectGoodsFocusLayout;
    // 加入收藏star
    private ImageView mGoodsCollectStar;
    // 加入收藏文本
    private TextView mGoodsCollectText;
    // 收藏状态layout
    private LinearLayout mGoodsCollectStatusLayout;
    // 收藏状态图片
    private ImageView mGoodsCollectStatusImage;
    // 收藏或取消收藏失败提示
    private TextView mGoodsCollectStatusText;
    // 界面销毁监听器
    private OnTbTvShoppingHideListener mOnTbTvShoppingHideListener;
    // 是否已收藏了
    private boolean mShopIsFavor;
    // 推荐商品唯一ID
    private long mId;
    // 商品ID
    private long mItemId;
    // 图片下载器
    private ImageLoaderManager mImageLoaderManager;
    private DisplayImageOptions options;

    // 是否正在请求详情数据
    private boolean mRequestingDetail;
    // 是否正在请求商品的收藏状态
    private boolean mRequestingCollectStatus;
    // 是否正请求增加到收藏
    private boolean mRequestingAddCollect;
    // 加载商品详情失败(包括获取是否收藏失败)
    private boolean mRequestDetailFailed;
    // 是否可以相应点击时间
    private boolean mCanClick;
    private BusinessRequest mBusinessRequest;
    // 商品详情数据
    private TBDetailResultVO mTBDetailResultVO;
    private AppHandler<TvShopDialog> mHandler = new TvShopDialogAppHandler(this);
    private String mTvShopPageName;

    private final String PAGE_NAME = "TsTvShop";
    private final String PAGE_DETAIL_NAME = PAGE_NAME + "_Detail";

    private Animation mAnimationIn; // 进入时的动画
    private Animation mAnimationOut; // 消失时的动画
    private boolean mIsDismissing; // 消失动画正在进行
    private boolean mShowDetailPage; // 是否显示详情界面

    public TvShopDialog(Context context, int theme, long id, long itemId, boolean showDetailPage,
                        TBDetailResultVO tBDetailResultVO, boolean shopIsFavor) {
        super(context, theme);
        mContext = context;
        mId = id;
        mItemId = itemId;
        mShowDetailPage = showDetailPage;
        mShopIsFavor = shopIsFavor;
        mTBDetailResultVO = tBDetailResultVO;

        init();
    }

    /**
     * 初始化参数
     */
    private void init() {
        setContentView(R.layout.ytshop_activity);

        mIsDismissing = false;
        mAnimationIn = AnimationUtils.loadAnimation(mWindow.getContext(), R.anim.ytshop_slide_in_right);
        mAnimationOut = AnimationUtils.loadAnimation(mWindow.getContext(), R.anim.ytshop_slide_out_right);

        initView();
        initParamter();
        registerLoginListener();// 需要实现
    }

    /**
     * 初始化控件变量
     */
    private void initView() {
        mTvshopRootLayout = (RelativeLayout) findViewById(R.id.tvshop_root_layout);
        mGoodsImageLayout = (RelativeLayout) findViewById(R.id.goods_image_layout);
        mGoodsImageView = (ImageView) findViewById(R.id.goods_image);
        mGoodsImageView.setId(GOODS_IMAGE_VIEW_ID);
        mGoodsImageView.setOnClickListener(this);

        mFocusDetailManager = (FocusPositionManager) findViewById(R.id.tvshopping_detail_manager);
        mFocusDetailManager.setSelector(new StaticFocusDrawable(mContext.getResources().getDrawable(
                R.drawable.ytshop_common_focus)));
        mGoodsDetailLoadFail = (LinearLayout) findViewById(R.id.goods_detail_load_fail);
        mProgressBarLayout = (LinearLayout) findViewById(R.id.goods_detail_loading_bar_layout);
        mGoodsDetailLayout = (LinearLayout) findViewById(R.id.goods_detail_layout);

        mGoodsDetailImage = (ImageView) findViewById(R.id.goods_detail_image);
        mGoodsTitle = (TextView) findViewById(R.id.goods_title);
        mGoodsPrice = (TextView) findViewById(R.id.goods_price);
        mGoodsOriginPrice = (TextView) findViewById(R.id.goods_origin_price);
        mGoodsOriginPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        mCollectGoodsFocusLayout = (FocusTextView) findViewById(R.id.collect_goods_focus_layout);
        mCollectGoodsFocusLayout.setId(GOODS_DETAIL_LAYOUT_ID);
        mCollectGoodsFocusLayout.setOnClickListener(this);

        mGoodsCollectStar = (ImageView) findViewById(R.id.collect_goods_star);
        mGoodsCollectText = (TextView) findViewById(R.id.collect_goods_text);
        mGoodsCollectStatusLayout = (LinearLayout) findViewById(R.id.collect_goods_status_layout);
        mGoodsCollectStatusImage = (ImageView) findViewById(R.id.collect_goods_status_image);
        mGoodsCollectStatusText = (TextView) findViewById(R.id.collect_goods_status_text);
    }

    /**
     * 初始化参数
     */
    private void initParamter() {
        mRequestingDetail = false;
        mRequestingCollectStatus = false;
        mRequestingAddCollect = false;
        mRequestDetailFailed = false;
        mCanClick = true;

        mTbTvShoppingManager.setShopItemVisible(mId, true);
        boolean needHideShop = mTbTvShoppingManager.getNeedHideShop();
        AppDebug.i(TAG, TAG + ".initParamter needHideShop = " + needHideShop + ", mId = " + mId + ", mItemId = "
                + mItemId + ".mShowDetailPage = " + mShowDetailPage + ".mTBDetailResultVO = " + mTBDetailResultVO);
        if (needHideShop) {
            mDismissImmediately = true;
            dismiss();
            return;
        }

        mOnTbTvShoppingHideListener = new OnTbTvShoppingHideListener() {

            @Override
            public void onHideShop(long id) {
                AppDebug.i(TAG, TAG + ".initParamter.onHideShop.id = " + id + ".mTvShopPageName = " + mTvShopPageName);
                Map<String, String> p = getPageProperties();
                p.put("quit_type", "auto");//自动退出mTvShopPageName
                Utils.utUpdatePageProperties(mTvShopPageName, p);
                dismiss();
                if (mNetworkDialog != null) {
                    mNetworkDialog.dismiss();
                    mNetworkDialog = null;
                }
            }
        };
        mTbTvShoppingManager.registerTbTvShoppingHideListener(mOnTbTvShoppingHideListener);

        mBusinessRequest = BusinessRequest.getBusinessRequest();
        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(mContext);
        options = new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(0)).cacheInMemory(true)
                .cacheOnDisc(false).bitmapConfig(Bitmap.Config.RGB_565).build();

        if (!mShowDetailPage) { // 显示卡片时
            mTvShopPageName = PAGE_NAME;
            TbTvShoppingItemBo itemBo = mTbTvShoppingManager.getTbTvShoppingItem(mId);
            AppDebug.i(TAG, TAG + ".initParamter.itemBo = " + itemBo);
            if (itemBo != null) {
                mImageLoaderManager.displayImage(itemBo.getItemImage(), mGoodsImageView, options,
                        new SimpleImageLoadingListener() {

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                AppDebug.i(TAG, TAG + ".initParamter.onLoadingComplete.imageUri = " + imageUri
                                        + ".view = " + view + ".loadedImage = " + loadedImage);
                                if (mGoodsImageLayout != null) {
                                    mGoodsImageLayout.setVisibility(View.VISIBLE);
                                    mGoodsImageView.requestFocus();
                                } else {
                                    mDismissImmediately = true;
                                    dismiss();
                                }

                                super.onLoadingComplete(imageUri, view, loadedImage);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                mDismissImmediately = true;
                                dismiss();
                                super.onLoadingFailed(imageUri, view, failReason);
                            }
                        });
            } else {
                mDismissImmediately = true;
                dismiss();
            }
        } else {// 显示详情页
            mTvShopPageName = PAGE_DETAIL_NAME;
            // 通知时间轴锁住
            setShopTimeLock(true);

            String itemId = String.valueOf(mItemId);
            AppDebug.v(TAG, TAG + ".initParamter.itemId = " + itemId);
            mGoodsImageLayout.setVisibility(View.GONE);
            mFocusDetailManager.setVisibility(View.VISIBLE);
            mFocusDetailManager.requestFocus();
            changeDetailLayout(SHOW_PROGRESS);

            Map<String, String> p = Utils.getProperties();
            p = initProperties(p);

            if (mTBDetailResultVO != null) {
                updateView();// 此时的收藏状态可能不准确，需重新获取收藏状态
                refreshData();
            } else {
                if (!mRequestingDetail) {
                    mRequestingDetail = true;
                    mBusinessRequest.requestGetItemDetailV5(itemId, null, new GetTvShopGoodsDetailRequest(this));
                }

                AppDebug.v(TAG, TAG + ".initParamter.judge isLogin start");
                // 如果没有登入，不去请求是否收藏接口
                if (!CoreApplication.getLoginHelper(mContext).isLogin()) {
                    AppDebug.v(TAG, TAG + ".initParamter, not login");
                    updataGoodsCollectStatus(false);

                    p.put("is_login", "false");
                    Utils.utUpdatePageProperties(mTvShopPageName, p);
                    return;
                }
                AppDebug.v(TAG, TAG + ".initParamter, logined");

                p.put("is_login", "true");
                if (null != User.getUserId()) {
                    p.put("user_id", User.getUserId());
                }

                Utils.utUpdatePageProperties(mTvShopPageName, p);

                AppDebug.v(TAG, TAG + ".onClick, mRequestingCollectStatus = " + mRequestingCollectStatus);
                if (!mRequestingCollectStatus) {
                    mRequestingCollectStatus = true;
                    mBusinessRequest.checkFav(itemId, new GetTvShopGoodsCollectStatusRequest(this));
                }
            }
        }
    }

    /**
     * 更新控件
     */
    private void updateView() {
        // 只有当详情数据和收藏状态请求结束时才显示界面
        if (mRequestingDetail || mRequestingCollectStatus) {
            AppDebug.v(TAG, TAG + ".updateView.mRequestingDetail = " + mRequestingDetail
                    + ", mRequestingCollectStatus = " + mRequestingCollectStatus);
            return;
        }

        // 如果加载失败
        if (mRequestDetailFailed || mTBDetailResultVO == null || mTBDetailResultVO.itemInfoModel == null) {
            changeDetailLayout(SHOW_FAILED);
            mCanClick = false;// 界面消失前，禁止点击事件再次发生
            autoFinishAcitivty(1000);
            return;
        }

        Map<String, String> p = getPageProperties();
        Utils.utUpdatePageProperties(mTvShopPageName, p);

        // 隐藏广告view,显示详情的view
        changeDetailLayout(SHOW_DETAIL);
        mCollectGoodsFocusLayout.setVisibility(View.VISIBLE);// 必须在此显示,否则focus框不上

        // 详情图片
        if (mTBDetailResultVO.itemInfoModel.picsPath != null && mTBDetailResultVO.itemInfoModel.picsPath.size() > 0
                && mGoodsDetailImage != null) {
            if (mTbTvShoppingManager != null) {
                mTbTvShoppingManager.setShopDetailImageUrl(mId, mTBDetailResultVO.itemInfoModel.picsPath.get(0));
            }
            mImageLoaderManager.displayImage(mTBDetailResultVO.itemInfoModel.picsPath.get(0), mGoodsDetailImage,
                    options);
        }

        // 详情标题
        if (!TextUtils.isEmpty(mTBDetailResultVO.itemInfoModel.title) && mGoodsTitle != null) {
            mGoodsTitle.setText(mTBDetailResultVO.itemInfoModel.title);
        }

        // 当前价格
        if (mTBDetailResultVO.itemInfoModel.priceUnits != null && mTBDetailResultVO.itemInfoModel.priceUnits.size() > 0
                && mGoodsPrice != null && mGoodsOriginPrice != null) {
            // 取当前价格
            String curPrice = "";
            String originPrice = "";
            for (int i = 0; i < mTBDetailResultVO.itemInfoModel.priceUnits.size(); i++) {
                PriceUnit unit = mTBDetailResultVO.itemInfoModel.priceUnits.get(i);
                if (unit.display != null && unit.display == 1) {
                    curPrice = unit.price;
                }
            }
            String text = mContext.getString(R.string.ytshop_price_symbol) + curPrice;
            SpannableString ss = new SpannableString(text);
            ss.setSpan(new AbsoluteSizeSpan((int) mContext.getResources().getDimension(R.dimen.sp_20)), 0, 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mGoodsPrice.setText(ss);

            // 取原始价格
            for (int i = 0; i < mTBDetailResultVO.itemInfoModel.priceUnits.size(); i++) {
                PriceUnit unit = mTBDetailResultVO.itemInfoModel.priceUnits.get(i);
                if (unit.display != null) {
                    if (unit.display == 2) {
                        originPrice = unit.price;
                    } else if (unit.display == 3) {
                        originPrice = unit.price;
                        break;
                    }
                }
            }

            AppDebug.v(TAG, TAG + ".updateView.curPrice = " + curPrice + ".originPrice = " + originPrice);
            if (!TextUtils.isEmpty(originPrice)) {
                text = mContext.getString(R.string.ytshop_price_symbol) + originPrice;
            } else {
                text = mContext.getString(R.string.ytshop_price_symbol) + curPrice;
            }
            mGoodsOriginPrice.setText(text);
        }

        updataGoodsCollectStatus(mShopIsFavor);
    }

    /**
     * 获取该卡片的id
     * @return
     */
    public long getId() {
        return mId;
    }

    @Override
    protected void onLogin() {
        AppDebug.v(TAG, TAG + ".onLogin");
        super.onLogin();
    }

    /*
     * 登入成功后刷新界面
     */
    @Override
    protected void refreshData() {
        AppDebug.v(TAG, TAG + ".refreshData");
        if (mItemId < 0) {
            return;
        }
        String itemId = String.valueOf(mItemId);
        AppDebug.v(TAG, TAG + ".refreshData.itemId = " + itemId);

        mBusinessRequest.checkFav(itemId, new UpdateTvShopGoodsCollectStatusRequest(this));
    }

    /**
     * 更新收藏状态
     * @param collected
     */
    private void updataGoodsCollectStatus(boolean collected) {
        if (collected) {// 若已收藏
            mGoodsCollectStar.setImageResource(R.drawable.ytshop_star_collected);
            mGoodsCollectText.setText(R.string.ytshop_collected_biaozhu);
        } else {
            mGoodsCollectStar.setImageResource(R.drawable.ytshop_star_collect);
            mGoodsCollectText.setText(R.string.ytshop_add_collect);
        }
    }

    /**
     * 显示Detail界面
     * @param type
     */
    private void changeDetailLayout(int type) {
        switch (type) {
            case SHOW_PROGRESS:// 加载中
                mProgressBarLayout.setVisibility(View.VISIBLE);
                mGoodsDetailLoadFail.setVisibility(View.GONE);
                mGoodsDetailLayout.setVisibility(View.GONE);
                break;
            case SHOW_DETAIL:// 显示界面
                mProgressBarLayout.setVisibility(View.GONE);
                mGoodsDetailLoadFail.setVisibility(View.GONE);
                mGoodsDetailLayout.setVisibility(View.VISIBLE);
                break;
            case SHOW_FAILED:// 加载失败
                mProgressBarLayout.setVisibility(View.GONE);
                mGoodsDetailLoadFail.setVisibility(View.VISIBLE);
                mGoodsDetailLayout.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 自动关闭该Activity
     */
    private void autoFinishAcitivty(int delay) {
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(0, delay);
        }
    }

    /**
     * 设置关注跟收藏
     * @param favor
     */
    private void setShopFavor(boolean favor) {
        if (mTbTvShoppingManager != null) {
            mTbTvShoppingManager.setShopItemFavor(mId, favor);
        }
    }

    /**
     * 进入TBS统计
     * @param pageName
     */
    private void enterPageTBS(String pageName) {
        AppDebug.v(TAG, TAG + ".enterPageTBS.pageName=" + pageName);
        if (TextUtils.isEmpty(pageName)) {
            return;
        }
        Utils.utPageAppear(pageName, pageName);
        Map<String, String> p = getPageProperties();
        Utils.utUpdatePageProperties(pageName, p);
    }

    /**
     * 退出TBS统计
     * @param pageName
     */
    private void exitPageTBS(String pageName) {
        AppDebug.v(TAG, TAG + ".exitPageTBS.pageName=" + pageName);
        if (TextUtils.isEmpty(pageName)) {
            return;
        }

        Utils.utPageDisAppear(pageName);
    }

    /**
     * 当商品收藏/取消收藏失败时
     */
    private void onGoodsManageFailed() {
        mGoodsCollectStatusLayout.setVisibility(View.VISIBLE);
        mGoodsCollectStatusImage.setVisibility(View.VISIBLE);
        if (mShopIsFavor) { // 取消收藏失败
            mGoodsCollectStatusText.setText(R.string.ytshop_del_collect_failed);
        } else { // 加入收藏失败
            mGoodsCollectStatusText.setText(R.string.ytshop_add_collect_failed);
        }
    }

    /**
     * 当商品收藏/取消收藏成功时
     */
    private void onGoodsManageSuccess() {
        updataGoodsCollectStatus(mShopIsFavor);
        setShopFavor(mShopIsFavor);
        if (mShopIsFavor) {
            mCanClick = false;// 界面消失前，禁止点击事件再次发生
            autoFinishAcitivty(1500);
            mGoodsCollectStatusLayout.setVisibility(View.VISIBLE);
            mGoodsCollectStatusImage.setVisibility(View.GONE);
            mGoodsCollectStatusText.setText(R.string.ytshop_add_collect_success_tips);
        } else {
            mGoodsCollectStatusLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 设置是否锁住时间检测
     * @param lock
     */
    private void setShopTimeLock(boolean lock) {
        if (mTbTvShoppingManager != null) {
            mTbTvShoppingManager.setLockCheckTime(lock);
        }
    }

    @Override
    public void show() {
        AppDebug.v(TAG, TAG + ".show.dialog = " + this + ".mTvShopPageName = " + mTvShopPageName);
        if (mAnimationIn != null) {// 显示详情时无动画
            mTvshopRootLayout.startAnimation(mAnimationIn);
        }

        enterPageTBS(mTvShopPageName);
        mTbTvShoppingManager.clearShowingDialog();
        super.show();
        // 显示商品后开启关闭倒计时
        TbTvShoppingManager.getIntance().setCountDownClearShowingDialogTimer();
        mTbTvShoppingManager.addShowingDialog(this);
    }

    @Override
    public void dismiss() {
        AppDebug.v(TAG, TAG + ".dismiss.dialog = " + this + ".mTvShopPageName = " + mTvShopPageName
                + ".mDismissImmediately = " + mDismissImmediately);
        if (mAnimationOut != null && !mDismissImmediately) {
            mAnimationOut.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    mIsDismissing = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mIsDismissing = false;
                    TvShopDialog.super.dismiss();
                    onDestroy();
                }
            });
            mTvshopRootLayout.startAnimation(mAnimationOut);
        } else {
            mDismissImmediately = false;
            mTvshopRootLayout.setVisibility(View.GONE);// 让界面快速消失
            super.dismiss();
            TbTvShoppingManager.getIntance().cancelCountDownClearShowingDialogTimer();
            onDestroy();
        }

        exitPageTBS(mTvShopPageName);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        AppDebug.v(TAG, TAG + ".dispatchKeyEvent.event = " + event);

        if (mIsDismissing) {// 正在消失
            return true;
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
            if (keyCode != KeyEvent.KEYCODE_DPAD_CENTER && keyCode != KeyEvent.KEYCODE_ENTER) {
                // 在这屏蔽返回键和escape键
                if (keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_ESCAPE) {
                    if (mGoodsImageLayout != null && mGoodsImageLayout.getVisibility() == View.VISIBLE) {
                        onBackPressed();
                    }
                }
            } else {
                if (mCollectGoodsFocusLayout != null && mCollectGoodsFocusLayout.getVisibility() == View.VISIBLE) {
                    if (!mCollectGoodsFocusLayout.isFocused()) {
                        mCollectGoodsFocusLayout.requestFocus();
                    }
                }
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        AppDebug.v(TAG, TAG + ".onBackPressed.mTvShopPageName = " + mTvShopPageName);
        if (mTbTvShoppingManager != null) {
            mTbTvShoppingManager.setShopItemVisible(mId, false);
        }

        Map<String, String> p = getPageProperties();
        p.put("quit_type", "manual");
        Utils.utUpdatePageProperties(mTvShopPageName, p);

        super.onBackPressed();
    }

    /**
     * 销毁参数
     */
    private void onDestroy() {
        unRegisterLoginListener();
        if (mTbTvShoppingManager != null) {
            mTbTvShoppingManager.deleteShowingDailog(this);
            mTbTvShoppingManager.unregisterTbTvShoppingHideListener(mOnTbTvShoppingHideListener);
            mTbTvShoppingManager.setShopItemVisible(mId, false);
            mOnTbTvShoppingHideListener = null;
        }
        setShopTimeLock(false);
    }

    @Override
    protected boolean isTbs() {
        return false;
    }

    @Override
    public Map<String, String> initProperties(Map<String, String> p) {
        p = super.initProperties(p);
        p.put("item_id", String.valueOf(mItemId));
        if (null != mTBDetailResultVO && null != mTBDetailResultVO.itemInfoModel
                && null != mTBDetailResultVO.itemInfoModel.title) {
            p.put("item_name", mTBDetailResultVO.itemInfoModel.title);
        }

        int image_num = -1;
        if (mGoodsImageLayout != null && mGoodsImageLayout.getVisibility() == View.VISIBLE) {
            image_num = 1;// 当商品推荐界面， 本期只支持单张图片
        } else {
            image_num = 1;// 默认商品详情主图
        }
        p.put("image_num", String.valueOf(image_num));

        int position = mTbTvShoppingManager.getPosition(mId);
        p.put("position", String.valueOf(position));
        return p;
    }

    /**
     * Handler 类
     * Class Descripton.
     * @version
     * @author mi.cao
     * @data 2015年1月14日 下午8:11:12
     */
    private static class TvShopDialogAppHandler extends AppHandler<TvShopDialog> {

        public TvShopDialogAppHandler(TvShopDialog dialog) {
            super(dialog);
        }

        @Override
        public void handleMessage(Message msg) {
            TvShopDialog dialog = getT();
            if (null == dialog) {
                return;
            }
            dialog.setShopTimeLock(false);
            dialog.mCanClick = true; // 复原是否可以响应点击事件标志
            Map<String, String> p = dialog.getPageProperties();
            p.put("quit_type", "auto");//自动退出mTvShopPageName
            Utils.utUpdatePageProperties(dialog.mTvShopPageName, p);
            dialog.dismiss();
        }
    }

    public static class Builder {

        private Context mContext;
        // 推荐商品唯一ID
        private long mId;
        // 商品ID
        private long mItemId;
        private boolean mShowDetailPage;
        private TBDetailResultVO mTBDetailResultVO;
        private boolean mShopIsFavor;

        public Builder(Context context) {
            mContext = context;
            mId = -1;
            mItemId = -1;
            mShowDetailPage = false;
            mTBDetailResultVO = null;
            mShopIsFavor = false;
        }

        public Builder setTvShopId(long id) {
            mId = id;
            return this;
        }

        public Builder setTvShopItemId(long itemId) {
            mItemId = itemId;
            return this;
        }

        public Builder setShowDetailPage(boolean showDetailPage) {
            mShowDetailPage = showDetailPage;
            return this;
        }

        public Builder setTBDetailResultVO(TBDetailResultVO tBDetailResultVO) {
            mTBDetailResultVO = tBDetailResultVO;
            return this;
        }

        public Builder setShopIsFavor(boolean shopIsFavor) {
            mShopIsFavor = shopIsFavor;
            return this;
        }

        public TvShopDialog create() {
            TvShopDialog dialog = new TvShopDialog(mContext, R.style.ytbv_CustomDialog, mId, mItemId, mShowDetailPage,
                    mTBDetailResultVO, mShopIsFavor);

            return dialog;
        }
    }

    /**
     * 当账号登入无效时
     * @param forceLogin
     */
    private void onInvalidLoginState(boolean forceLogin) {
        startYunosAccountActivity(mContext, forceLogin);
        // 当进入登入界面时，需要关闭当前界面，此时需要记录该界面信息
        TbTvShopNeedResumeInfo tbTvShopNeedResumeInfo = new TbTvShopNeedResumeInfo();
        tbTvShopNeedResumeInfo.mNeedResumePage = NEED_RESUME_PAGE.TVSHOP_DETAIL_PAGE;
        tbTvShopNeedResumeInfo.mId = mId;
        tbTvShopNeedResumeInfo.mItemId = mItemId;
        tbTvShopNeedResumeInfo.mShopIsFavor = mShopIsFavor;
        tbTvShopNeedResumeInfo.mTBDetailResultVO = mTBDetailResultVO;
        if (mTbTvShoppingManager != null) {
            mTbTvShoppingManager.setTbTvShopNeedResumeInfo(tbTvShopNeedResumeInfo);
        }
        mDismissImmediately = true;
        dismiss();// 关闭dialog
    }

    @Override
    public void onClick(View v) {
        AppDebug.v(TAG, TAG + ".onClick.v = " + v + "mItemId = " + mItemId + ".mCanClick = " + mCanClick);
        if (mItemId < 0 || !mCanClick) {
            return;
        }
        AppDebug.v(TAG, TAG + ".onClick.mAnimationing = " + mIsDismissing);
        if (mIsDismissing) {
            return;
        }

        String itemId = String.valueOf(mItemId);
        AppDebug.v(TAG, TAG + ".onClick.itemId = " + itemId);

        Map<String, String> p = Utils.getProperties();
        p = initProperties(p);
        String controlName = null;

        switch (v.getId()) {
            case GOODS_IMAGE_VIEW_ID:
                AppDebug.v(TAG, TAG + ".onClick.judge to detail");
                // 通知时间轴锁住
                setShopTimeLock(true);

                controlName = Utils.getControlName(mTvShopPageName, "Summary", null, "Click");//推荐商品简介页面点击事件（第一个页面）
                mGoodsImageLayout.setVisibility(View.GONE);
                mFocusDetailManager.setVisibility(View.VISIBLE);
                mFocusDetailManager.requestFocus();
                changeDetailLayout(SHOW_PROGRESS);

                if (!mRequestingDetail) {
                    mRequestingDetail = true;
                    mBusinessRequest.requestGetItemDetailV5(itemId, null, new GetTvShopGoodsDetailRequest(this));
                }

                AppDebug.v(TAG, TAG + ".onClick.judge isLogin start");
                // 如果没有登入，不去请求是否收藏接口
                if (!CoreApplication.getLoginHelper(mContext).isLogin()) {
                    AppDebug.v(TAG, TAG + ".onClick, not login");
                    updataGoodsCollectStatus(false);

                    p.put("is_login", "false");
                    TBS.Adv.ctrlClicked(CT.Button, controlName, Utils.getKvs(p));
                    exitPageTBS(mTvShopPageName);// 离开推荐窗口时，埋点该界面
                    mTvShopPageName = PAGE_DETAIL_NAME;
                    enterPageTBS(mTvShopPageName);// 进入详情时 埋点该界面
                    return;
                }
                AppDebug.v(TAG, TAG + ".onClick, logined");

                p.put("is_login", "true");
                if (null != User.getUserId()) {
                    p.put("user_id", User.getUserId());
                }

                AppDebug.v(TAG, TAG + ".onClick, mRequestingCollectStatus = " + mRequestingCollectStatus);
                if (!mRequestingCollectStatus) {
                    mRequestingCollectStatus = true;
                    mBusinessRequest.checkFav(itemId, new GetTvShopGoodsCollectStatusRequest(this));
                }

                TBS.Adv.ctrlClicked(CT.Button, controlName, Utils.getKvs(p));
                exitPageTBS(mTvShopPageName);// 离开推荐窗口时，埋点该界面
                mTvShopPageName = PAGE_DETAIL_NAME;
                enterPageTBS(mTvShopPageName);// 进入详情时 埋点该界面
                break;

            case GOODS_DETAIL_LAYOUT_ID:
                AppDebug.v(TAG, TAG + ".onClick.prepare to collect");
                controlName = Utils.getControlName(mTvShopPageName, "", null, "Click");
                // 如果没有登入,去登入界面
                if (!CoreApplication.getLoginHelper(mContext).isLogin()) {
                    onInvalidLoginState(false);
                    p.put("is_login", "false");
                    TBS.Adv.ctrlClicked(CT.Button, controlName, Utils.getKvs(p));
                    return;
                }

                p.put("is_login", "true");
                if (null != User.getUserId()) {
                    p.put("user_id", User.getUserId());
                }

                if (!mRequestingAddCollect) {
                    mRequestingAddCollect = true;
                    String func = "addAuction"; //addAuction增加收藏, delAuction表示删除收藏
                    if (mShopIsFavor) {// 如果已经收藏，则取消收藏
                        func = "delAuction";
                    }
                    AppDebug.v(TAG, TAG + ".onClick.func = " + func + ".mShopIsFavor = " + mShopIsFavor);
                    p.put("collect_opt", func);//收藏还是取消收藏
                    mGoodsCollectStatusLayout.setVisibility(View.GONE);
                    mBusinessRequest.manageFav(itemId, func, new GetTvShopGoodsCollectManageRequest(this));
                }

                TBS.Adv.ctrlClicked(CT.Button, controlName, Utils.getKvs(p));
                break;
            default:
                break;
        }
    }

    /**
     * 获取商品详情数据
     * Class Descripton.
     * @version
     * @author mi.cao
     * @data 2015年6月25日 下午6:06:26
     */
    private static class GetTvShopGoodsDetailRequest implements RequestListener<TBDetailResultVO> {

        private WeakReference<TvShopDialog> mTvShopDialog;

        public GetTvShopGoodsDetailRequest(TvShopDialog dialog) {
            mTvShopDialog = new WeakReference<TvShopDialog>(dialog);
        }

        @Override
        public void onRequestDone(TBDetailResultVO data, int resultCode, String msg) {
            TvShopDialog dialog = mTvShopDialog.get();
            if (dialog != null) {
                AppDebug.v(dialog.TAG, dialog.TAG + ".GetTvShopGoodsDetailRequest.data = " + data + ", resultCode = "
                        + resultCode + ", msg = " + msg);
                if (resultCode == 200) {
                    dialog.mTBDetailResultVO = data;
                } else {
                    dialog.mRequestDetailFailed = true;
                }
                dialog.mRequestingDetail = false;
                dialog.updateView();
            }
        }
    }

    /**
     * 获取商品的收藏状态
     * Class Descripton.
     * @version
     * @author mi.cao
     * @data 2015年6月25日 下午7:07:49
     */
    private static class GetTvShopGoodsCollectStatusRequest implements RequestListener<String> {

        private WeakReference<TvShopDialog> mTvShopDialog;

        public GetTvShopGoodsCollectStatusRequest(TvShopDialog dialog) {
            mTvShopDialog = new WeakReference<TvShopDialog>(dialog);
        }

        @Override
        public void onRequestDone(String data, int resultCode, String msg) {
            TvShopDialog dialog = mTvShopDialog.get();
            if (dialog != null) {
                AppDebug.v(dialog.TAG, dialog.TAG + ".GetTvShopGoodsCollectStatusRequest.data = " + data
                        + ", resultCode = " + resultCode + ", msg = " + msg);
                if (resultCode == 200) {
                    if (!TextUtils.isEmpty(data)) {// 已收藏
                        if (data.equals("true")) {
                            dialog.mShopIsFavor = true;
                        } else {
                            dialog.mShopIsFavor = false;
                        }
                    }
                } else {
                    dialog.mShopIsFavor = false;
                }
                dialog.mRequestingCollectStatus = false;
                dialog.updateView();
            }
        }
    }

    /**
     * 收藏或取消收藏
     * Class Descripton.
     * @version
     * @author mi.cao
     * @data 2015年6月25日 下午7:10:05
     */
    private static class GetTvShopGoodsCollectManageRequest implements RequestListener<String> {

        private WeakReference<TvShopDialog> mTvShopDialog;

        public GetTvShopGoodsCollectManageRequest(TvShopDialog dialog) {
            mTvShopDialog = new WeakReference<TvShopDialog>(dialog);
        }

        @Override
        public void onRequestDone(String data, int resultCode, String msg) {
            TvShopDialog dialog = mTvShopDialog.get();
            if (dialog != null) {
                AppDebug.v(dialog.TAG, dialog.TAG + ".GetTvShopGoodsCollectManageRequest.data = " + data
                        + ", resultCode = " + resultCode + ", msg = " + msg);
                if (resultCode == 200) {
                    dialog.mRequestingAddCollect = false;

                    if (dialog.mShopIsFavor) { // 如果是已经收藏的，则取消收藏
                        dialog.mShopIsFavor = false;
                    } else {
                        dialog.mShopIsFavor = true;
                    }

                    dialog.onGoodsManageSuccess();
                } else {
                    if (resultCode == ServiceCode.API_NOT_LOGIN.getCode()) {// 没有登入 则进行登入
                        dialog.mRequestingAddCollect = false;
                        dialog.onInvalidLoginState(false);
                    } else if (resultCode == ServiceCode.API_SID_INVALID.getCode()) { // SID失效，则进行强制登入
                        dialog.mRequestingAddCollect = false;
                        dialog.onInvalidLoginState(true);
                    } else {
                        String itemId = String.valueOf(dialog.mItemId);
                        dialog.mBusinessRequest.checkFav(itemId, new ReGetTvShopGoodsCollectStatusRequest(dialog));
                    }
                }
            }
        }
    }

    /**
     * 重新获取商品的收藏状态
     * Class Descripton.
     * @version
     * @author mi.cao
     * @data 2015年6月25日 下午7:40:13
     */
    private static class ReGetTvShopGoodsCollectStatusRequest implements RequestListener<String> {

        private WeakReference<TvShopDialog> mTvShopDialog;

        public ReGetTvShopGoodsCollectStatusRequest(TvShopDialog dialog) {
            mTvShopDialog = new WeakReference<TvShopDialog>(dialog);
        }

        @Override
        public void onRequestDone(String data, int resultCode, String msg) {
            TvShopDialog dialog = mTvShopDialog.get();
            if (dialog != null) {
                AppDebug.v(dialog.TAG, dialog.TAG + ".ReGetTvShopGoodsCollectStatusRequest.data = " + data
                        + ", resultCode = " + resultCode + ", msg = " + msg);
                if (resultCode == 200) {
                    if (!TextUtils.isEmpty(data)) {// 已收藏
                        boolean favorStatus = data.equals("true");
                        if (dialog.mShopIsFavor && !favorStatus) {// 去取消收藏，状态为未收藏，置为未收藏状态
                            dialog.mShopIsFavor = false;
                            dialog.onGoodsManageSuccess();
                        } else if (dialog.mShopIsFavor && favorStatus) { // 去取消收藏，状态为收藏，表示取消收藏失败
                            dialog.onGoodsManageFailed();
                        } else if (!dialog.mShopIsFavor && !favorStatus) {// 去收藏，状态为未收藏，表示收藏失败
                            dialog.onGoodsManageFailed();
                        } else if (!dialog.mShopIsFavor && favorStatus) { // 去收藏，状态为已收藏，置为收藏状态
                            dialog.mShopIsFavor = true;
                            dialog.onGoodsManageSuccess();
                        }
                    } else {
                        dialog.onGoodsManageFailed();
                    }
                } else {
                    dialog.onGoodsManageFailed();
                }
                dialog.mRequestingAddCollect = false;
            }
        }
    }

    /**
     * 更新商品的收藏状态
     * Class Descripton.
     * @version
     * @author mi.cao
     * @data 2015年6月26日 上午10:21:36
     */
    private static class UpdateTvShopGoodsCollectStatusRequest implements RequestListener<String> {

        private WeakReference<TvShopDialog> mTvShopDialog;

        public UpdateTvShopGoodsCollectStatusRequest(TvShopDialog dialog) {
            mTvShopDialog = new WeakReference<TvShopDialog>(dialog);
        }

        @Override
        public void onRequestDone(String data, int resultCode, String msg) {
            TvShopDialog dialog = mTvShopDialog.get();
            if (dialog != null) {
                AppDebug.v(dialog.TAG, dialog.TAG + ".UpdateTvShopGoodsCollectStatusRequest.data = " + data
                        + ", resultCode = " + resultCode + ", msg = " + msg);
                if (resultCode == 200) {
                    if (!TextUtils.isEmpty(data)) {// 已收藏
                        if (data.equals("true")) {
                            dialog.mShopIsFavor = true;
                        } else {
                            dialog.mShopIsFavor = false;
                        }
                    }
                } else {
                    dialog.mShopIsFavor = false;
                }
                dialog.updataGoodsCollectStatus(dialog.mShopIsFavor);
            }
        }
    }
}
