/**
 * $
 * PROJECT NAME: TvShopping
 * PACKAGE NAME: com.yunos.tvshopping.dialog
 * FILE NAME: TvShopMyCollectDialog.java
 * CREATED TIME: 2015年6月26日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.tvshoppingbundle.dialog;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.display.RoundedBitmapDisplayer;
import com.tvlife.imageloader.core.listener.SimpleImageLoadingListener;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.widget.FocusNoDeepRelativeLayout;
//import com.yunos.tvtaobao.blitz.account.LoginHelper;
import com.yunos.tvtaobao.tvshoppingbundle.R;
import com.yunos.tvtaobao.tvshoppingbundle.bean.TbTvShoppingReceiverData;
import com.yunos.tvtaobao.tvshoppingbundle.manager.TbTvShoppingManager;
import com.yunos.tvtaobao.tvshoppingbundle.manager.TbTvShoppingManager.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2015年6月26日 上午10:29:32
 */
public class TvShopMyCollectDialog extends TvShopBaseDialog {

    private Context mContext;

    private FocusPositionManager mFocusPositionManager;
    // 我的收藏界面title
    private TextView mMyCollectTitle;
    // 我的收藏列表
    private FocusNoDeepRelativeLayout mCollectFocusLayout;
    private LinearLayout mCollectGoodsLayout;
    // 图片下载器
    private ImageLoaderManager mImageLoaderManager;
    // 图片下载参数配置
    private DisplayImageOptions options;
    // 已收藏的数据列表
    private List<TbTvShoppingItemData> mFavorItemList;
    // 当前播放的视频相关数据
    private TbTvShoppingReceiverData mCurVideoData;

    private Animation mAnimationIn; // 进入时的动画
    private Animation mAnimationOut; // 消失时的动画
    private boolean mIsDismissing; // 消失动画正在进行

    public TvShopMyCollectDialog(Context context, int theme, TbTvShoppingReceiverData videoData) {
        super(context, theme);
        mContext = context;
        mCurVideoData = videoData;
        init();

    }

    /**
     * 初始化
     */
    private void init() {
        setContentView(R.layout.ytshop_my_collect_activity);

        mIsDismissing = false;

        //        mAnimationIn = AnimationUtils.loadAnimation(mWindow.getContext(), android.R.anim.fade_in);
        //        mAnimationOut = AnimationUtils.loadAnimation(mWindow.getContext(), android.R.anim.fade_out);

        mFavorItemList = mTbTvShoppingManager.getFavorItemList();
        if (mFavorItemList == null || mFavorItemList.size() == 0) {
            AppDebug.i(TAG, "mFavorItemList null");
            mDismissImmediately = true;
            dismiss();
            return;
        }

        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(mContext);
        options = new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(0)).cacheInMemory(true)
                .cacheOnDisc(false).showImageOnFail(R.drawable.ytshop_my_collect_default)
                .showImageForEmptyUri(R.drawable.ytshop_my_collect_default).bitmapConfig(Bitmap.Config.RGB_565).build();

        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mFocusPositionManager = (FocusPositionManager) findViewById(R.id.my_collect_root_layout);
        mFocusPositionManager.setSelector(new StaticFocusDrawable(mContext.getResources().getDrawable(
                R.drawable.ytshop_common_focus)));
        mFocusPositionManager.requestFocus();

        initTitle();

        mCollectFocusLayout = (FocusNoDeepRelativeLayout) findViewById(R.id.my_collect_focus_layout);
        mCollectGoodsLayout = (LinearLayout) findViewById(R.id.my_collect_layout);
        mCollectFocusLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AppDebug.v(TAG, TAG + ".mCollectFocusLayout.onClick.mAnimationing = " + mIsDismissing);
                if (mIsDismissing) {
                    return;
                }

                //                if (!NetWorkUtil.isNetWorkAvailable()) {
                //                    onStartActivityNetWorkError(new OnClickListener() {
                //
                //                        @Override
                //                        public void onClick(DialogInterface dialog, int which) {
                //                            startNetWorkSettingActivity(mContext,
                //                                    mContext.getString(R.string.ytbv_open_setting_activity_error));
                //                            mNetworkDialog.dismiss();
                //                            onNeedHideDialog();
                //                        }
                //                    }, new OnKeyListener() {
                //
                //                        @Override
                //                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                //                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //                                mNetworkDialog.dismiss();
                //                                if (mCloseDialogOfNetworkDialog && mContext != null) {
                //                                    dismiss();
                //                                }
                //                                return true;
                //                            }
                //                            return false;
                //                        }
                //                    }, false);
                //                    return;
                //                }

                Map<String, String> p = Utils.getProperties();
                p = initProperties(p);
                String controlName = Utils.getControlName(getFullPageName(), "FavBtn", null);

                if (!CoreApplication.getLoginHelper(mContext).isLogin()) {
                    p.put("is_login", "false");
                } else {
                    p.put("is_login", "true");
                }

                TBS.Adv.ctrlClicked(CT.Button, controlName, Utils.getKvs(p));
                // 打开我的收藏
                try {
                    String url = "tvtaobao://home?module=collects&" + CoreIntentKey.URI_FROM_APP + "="
                            + getAppName();
                    AppDebug.i(TAG, TAG + ".onItemClick.url = " + url);
                    Intent intent = new Intent();
                    setHuodong("biankanbianmai_collect");
                    intent.setData(Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    mDismissImmediately = true;
                    dismiss();
                }
            }
        });

        if (mFavorItemList == null || mFavorItemList.size() == 0) {
            return;
        }
        int size = mFavorItemList.size();
        AppDebug.v(TAG, TAG + ".initView.size = " + size);
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(mContext);
            imageView.setBackgroundColor(mContext.getResources().getColor(R.color.ytshop_gray));
            imageView.setScaleType(ScaleType.CENTER_CROP);
            int width = (int) mContext.getResources().getDimension(R.dimen.dp_89);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
            if (i > 0) {
                params.leftMargin = (int) mContext.getResources().getDimension(R.dimen.dp_2);
            }

            mCollectGoodsLayout.addView(imageView, params);

            if (i > 2) {// 超过三个时,显示扩展图片
                imageView.setImageResource(R.drawable.ytshop_my_collect_more);
                break;
            } else {
                imageView.setImageResource(R.drawable.ytshop_my_collect_default);// 默认图
                TbTvShoppingItemData itemData = mFavorItemList.get(i);
                if (itemData == null || TextUtils.isEmpty(itemData.getShopDetailImageUrl())) {
                    AppDebug.v(TAG, TAG + ".initView imageUrl = null");
                    continue;
                }
                AppDebug.v(TAG, TAG + ".initView imageUrl = " + itemData.getShopDetailImageUrl());
                mImageLoaderManager.displayImage(itemData.getShopDetailImageUrl(), imageView, options,
                        new SimpleImageLoadingListener() {

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                ImageView imageView = (ImageView) view;
                                if (imageView != null && loadedImage != null && !loadedImage.isRecycled()) {
                                    imageView.setImageBitmap(loadedImage);
                                }
                                super.onLoadingComplete(imageUri, view, loadedImage);
                            }
                        });
            }
        }
        AppDebug.v(TAG, TAG + ".initView.child = " + mCollectGoodsLayout.getChildCount());
    }

    /**
     * 当需要隐藏对话框时，记录对话框信息
     */
    private void onNeedHideDialog() {
        TbTvShopNeedResumeInfo tbTvShopNeedResumeInfo = new TbTvShopNeedResumeInfo();
        tbTvShopNeedResumeInfo.mNeedResumePage = NEED_RESUME_PAGE.TVSHOP_MY_COLLECT_PAGE;
        tbTvShopNeedResumeInfo.mCurVideoData = mCurVideoData;

        if (mTbTvShoppingManager != null) {
            mTbTvShoppingManager.setTbTvShopNeedResumeInfo(tbTvShopNeedResumeInfo);
        }
        mDismissImmediately = true;
        dismiss();
    }

    @Override
    protected void changedNetworkStatus(boolean available) {
        if (available) {
            if (mNetworkDialog != null && mNetworkDialog.isShowing()) {
                mNetworkDialog.dismiss();
                mNetworkDialog = null;
            }
        }
        super.changedNetworkStatus(available);
    }

    private void initTitle() {
        if (mFavorItemList == null || mFavorItemList.size() == 0) {
            return;
        }
        mMyCollectTitle = (TextView) findViewById(R.id.my_collect_title);
        mMyCollectTitle.getPaint().setAntiAlias(true);
        String title = String.format(mContext.getResources().getString(R.string.ytshop_my_collected_title),
                mFavorItemList.size());
        Pattern p = Pattern.compile("\\d+");//在这里，编译 成一个正则。;
        Matcher m = p.matcher(title);//获得匹配;
        String strDigit = "";
        int start = 0;
        int end = 0;
        if (m.find()) { //如果字符中有多段含有数字，可以用while找出所以数字，如abc55dc77,找出55和77
            strDigit = m.group();
            if (!TextUtils.isEmpty(strDigit)) {
                start = title.indexOf(strDigit);
                end = start + strDigit.length();
            }
        }
        AppDebug.v(TAG, TAG + ".strDigit = " + strDigit + ", start = " + start + ", end" + end);
        SpannableString ss = new SpannableString(title);
        ss.setSpan(new AbsoluteSizeSpan((int) mContext.getResources().getDimension(R.dimen.sp_44)), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.ytshop_my_collect_title_num)),
                start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mMyCollectTitle.setText(ss);
    }

    @Override
    public void show() {
        AppDebug.i(TAG, TAG + ".show.dialog = " + this);
        if (mAnimationIn != null) {
            mFocusPositionManager.startAnimation(mAnimationIn);
        }
        mTbTvShoppingManager.clearShowingDialog();
        super.show();
        // 显示商品后开启关闭倒计时
        TbTvShoppingManager.getIntance().setCountDownClearShowingDialogTimer();
        mTbTvShoppingManager.addShowingDialog(this);
    }

    @Override
    public void dismiss() {
        AppDebug.i(TAG, TAG + ".dismiss.dialog = " + this + ".mDismissImmediately = " + mDismissImmediately);
        if (mAnimationOut != null && !mDismissImmediately) {// 动画消失 
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
                    TvShopMyCollectDialog.super.dismiss();
                    onDestroy();
                }
            });
            mFocusPositionManager.startAnimation(mAnimationOut);
        } else {// 立即消失
            mDismissImmediately = false;
            super.dismiss();
            TbTvShoppingManager.getIntance().cancelCountDownClearShowingDialogTimer();
            onDestroy();
        }
    }

    private void onDestroy() {
        if (mFavorItemList != null) {
            mFavorItemList.clear();
            mFavorItemList = null;
        }
        if (mTbTvShoppingManager != null) {
            mTbTvShoppingManager.deleteShowingDailog(this);
            mTbTvShoppingManager = null;
        }

        mImageLoaderManager = null;
        options = null;
        mCollectFocusLayout = null;
        mCollectGoodsLayout = null;
        mMyCollectTitle = null;
        mFocusPositionManager = null;
    }

    @Override
    protected void refreshData() {
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mIsDismissing) {// 正在消失
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public Map<String, String> initProperties(Map<String, String> p) {
        if (null != mCurVideoData) {
            if (null != mCurVideoData.getVideoId()) {
                p.put("video_id", mCurVideoData.getVideoId());
            }
            if (null != mCurVideoData.getVideoName()) {
                p.put("video_name", mCurVideoData.getVideoName());
            }
            if (null != mCurVideoData.getType()) {
                p.put("video_type", mCurVideoData.getType().getName());
            }
        }

        if (null != mFavorItemList) {
            p.put("fav_item_num", String.valueOf(mFavorItemList.size()));//
        }
        if (null != mTbTvShoppingManager) {
            p.put("item_num", String.valueOf(mTbTvShoppingManager.getSingleItemNum()));
        }
        return p;
    }

    public static class Builder {

        private Context mContext;

        private TbTvShoppingReceiverData mCurVideoData;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setCurVideoData(TbTvShoppingReceiverData videoData) {
            mCurVideoData = videoData;
            return this;
        }

        public TvShopMyCollectDialog create() {
            TvShopMyCollectDialog dialog = new TvShopMyCollectDialog(mContext, R.style.ytbv_CustomDialog, mCurVideoData);

            return dialog;
        }

    }
}
