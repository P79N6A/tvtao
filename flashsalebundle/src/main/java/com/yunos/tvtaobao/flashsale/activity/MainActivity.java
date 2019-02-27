/**
 * Copyright (C) 2015 The ALI OS Project
 * Version Date Author
 * 2015-4-19 lizhi.ywp
 */
package com.yunos.tvtaobao.flashsale.activity;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.config.TvOptionsChannel;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.flashsale.AppManager;
import com.yunos.tvtaobao.flashsale.R;
import com.yunos.tvtaobao.flashsale.adapter.GoodsAdapter;
import com.yunos.tvtaobao.flashsale.bo.CategoryItem;
import com.yunos.tvtaobao.flashsale.bo.GoodsInfo;
import com.yunos.tvtaobao.flashsale.listener.DatabaseListener;
import com.yunos.tvtaobao.flashsale.listener.FlipperItemListener;
import com.yunos.tvtaobao.flashsale.utils.AppConfig;
import com.yunos.tvtaobao.flashsale.utils.DateUtils;
import com.yunos.tvtaobao.flashsale.utils.TbsUtil;
import com.yunos.tvtaobao.flashsale.view.AnimatorView;
import com.yunos.tvtaobao.flashsale.view.FinallyBuyView;
import com.yunos.tvtaobao.flashsale.view.FocusFlipperView;
import com.yunos.tvtaobao.flashsale.view.MyConcernView;
import com.yunos.tvtaobao.flashsale.view.PeriodBuyView;

import java.util.Map;
import java.util.Set;

public class MainActivity extends FlashSaleBaseActivity {

    private FocusPositionManager mFocusPositionManager;
    // private RequestManager mRequestManager;
    private FocusFlipperView mFocuseViewFlipper;
    private int mScreenWidth;
    private int mArrowWidth;
    private Animation mLeftIn;
    private Animation mLeftOut;
    private Animation mRightIn;
    private Animation mRightOut;

    /** 进来的时候最先跳到的页面数 */
    private byte mPageType = FlipperItemListener.TYPE_PERIOD_BUY;
    private AppManager mAppManager;
    private String mEntryTypeDesc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageName = TbsUtil.PAGE_TaoQiangGou_Home;
        Uri uri = getIntent().getData();
        if (uri == null) {
            openFail("NoUri");
            return;
        }

        // 解析Uri
        Bundle bundle = decodeUri(uri);
        if (bundle == null) {
            openFail("NoBundle");
            return;
        }

        String pageTypeStr = bundle.getString("pageType");

        TvOptionsConfig.setTvOptionVoiceSystem(bundle.getString(CoreIntentKey.URI_FROM));
        TvOptionsConfig.setTvOptionsChannel(TvOptionsChannel.TAO_QG);

        if (pageTypeStr != null) {
            mPageType = Byte.parseByte(pageTypeStr);
        }
        if (mPageType == FlipperItemListener.TYPE_MYCONCERN) {
            mEntryTypeDesc = "Favorites";
        } else if (mPageType == FlipperItemListener.TYPE_FINALLY_BUY) {
            mEntryTypeDesc = "Remainder";
        } else {
            mEntryTypeDesc = "home";
        }
        setContentView(R.layout.fs_activity_home);
        mAppManager = AppManager.getInstance(this);

        getFlashSaleContextListener().OnWaitProgressDialog(true);

        mAppManager.getMyConcernCache().queryMyconcernList(new DatabaseListener() {

            @Override
            public void onQueryDone(byte queryState) {
                if (!MainActivity.this.isFinishing()) {
                    initView();
                }
            }
        });

        // initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mFocuseViewFlipper) {
            mFocuseViewFlipper.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        // 设置账号界面隐藏
        super.onResume();
        if (null != mFocuseViewFlipper) {
            mFocuseViewFlipper.onResume();
        }
        /** 更新埋点数据 */
        if (!TextUtils.isEmpty(mEntryTypeDesc)) {
            Map<String, String> property = Utils.getProperties();
            property.put("from", mEntryTypeDesc);
            Utils.utUpdatePageProperties(mPageName, property);
        }

        if (mFocusPositionManager != null) {
            mFocusPositionManager.getPositionManager().forceDrawFocus();
        }
    }

    private Animation createAnimation(long duration, float fromAlpha, float toAlpha, int fromXDelta, int toXDelta) {
        // AnimationSet anim = new AnimationSet(true);
        TranslateAnimation translate = new TranslateAnimation(Animation.ABSOLUTE, fromXDelta, Animation.ABSOLUTE,
                toXDelta, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        translate.setDuration(duration);
        return translate;

        // anim.setDuration(duration);
        // anim.addAnimation(translate);
        // AlphaAnimation alpha = new AlphaAnimation(fromAlpha, toAlpha);
        // alpha.setDuration(duration);
        // anim.addAnimation(alpha);
        // return anim;
    }

    private void initView() {
        // mRequestManager = RequestManager.getRequestManager();
        mFocusPositionManager = (FocusPositionManager) super.findViewById(R.id.fs_home_rootview);
        mFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(R.drawable.comm_focus)));

        mFocuseViewFlipper = (FocusFlipperView) super.findViewById(R.id.fs_home_content);
        mFocuseViewFlipper.setSwitchMode(AnimatorView.SWITCH_MODE_NORMAL);

        int pageCount = 0;
        // 如果我的关注列表不为空
        if (mAppManager.getMyConcernCache().size() > 0 || mPageType == FlipperItemListener.TYPE_MYCONCERN) {
            mFocuseViewFlipper.addView(new MyConcernView(mFocuseViewFlipper, this));
            pageCount++;
        }
        PeriodBuyView periodBuyView = new PeriodBuyView(mFocuseViewFlipper, this);
        mFocuseViewFlipper.addView(periodBuyView);
        pageCount++;
        FinallyBuyView finallyBuyView = new FinallyBuyView(mFocuseViewFlipper, this);
        mFocuseViewFlipper.addView(finallyBuyView);
        pageCount++;
        // mFocuseViewFlipper.setDisplayedChild(mPageType, true);
        if (mPageType == FlipperItemListener.TYPE_MYCONCERN || pageCount < FlipperItemListener.TYPE_MAX) {
            mFocuseViewFlipper.setDisplayedChild(0, true);
        } else {
            mFocuseViewFlipper.setDisplayedChild(1, true);
        }

        mScreenWidth = super.getResources().getDisplayMetrics().widthPixels;
        mArrowWidth = AppConfig.ARROWBAR_WIDTH;
        mLeftIn = createAnimation(AppConfig.ANIM_DURATION_TIMEOUT, 0.1f, 1.0f, mArrowWidth - mScreenWidth, 0);
        mLeftOut = createAnimation(AppConfig.ANIM_DURATION_TIMEOUT, 1.0f, 0.1f, 0, mArrowWidth - mScreenWidth);
        mRightIn = createAnimation(AppConfig.ANIM_DURATION_TIMEOUT, 0.1f, 1.0f, mScreenWidth - mArrowWidth, 0);

        mRightOut = createAnimation(AppConfig.ANIM_DURATION_TIMEOUT, 1.0f, 0.1f, 0, mScreenWidth - mArrowWidth);
    }

    protected boolean OnSwitch(int keyCode) {
        View v = mFocuseViewFlipper.getCurrentView();

        if (null != v && v instanceof FlipperItemListener) {
            FlipperItemListener itemListener = (FlipperItemListener) v;
            return itemListener.OnSwitch(keyCode);
        }
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return super.dispatchKeyEvent(event);
        }

        if (null == mFocuseViewFlipper) {
            return true;
        }

        if (mFocuseViewFlipper.isAnim()) {
            AppDebug.i(TAG,"dispatchKeyEvent: the viewflipper is excuting animation");
            return true;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int code = event.getKeyCode();
            if (code == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (mFocuseViewFlipper.hasNext() && OnSwitch(code)) {
                    mFocusPositionManager.getPositionManager().release();

                    mFocuseViewFlipper.setInAnimation(mRightIn);
                    mFocuseViewFlipper.setOutAnimation(mLeftOut);
                    mFocuseViewFlipper.showNext();
                    AppDebug.i(TAG,"dispatchKeyEvent: the viewflipper switch next view");
                    return true;
                }
            } else if (code == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (mFocuseViewFlipper.hasPrevious() && OnSwitch(code)) {
                    mFocusPositionManager.getPositionManager().release();

                    mFocuseViewFlipper.setInAnimation(mLeftIn);
                    mFocuseViewFlipper.setOutAnimation(mRightOut);
                    mFocuseViewFlipper.showPrevious();
                    AppDebug.i(TAG,"dispatchKeyEvent: the viewflipper switch previous view");
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        p.put(SPMConfig.SPM_CNT, SPMConfig.TAOQIANGGOU_MAIN+".0.0");

        return p;
    }

    /**
     * 打开activity失败的提示
     */
    private void startActivityFail() {
        Toast.makeText(this, this.getResources().getString(R.string.str_start_activity_error), Toast.LENGTH_SHORT)
                .show();
    }

    private void openFail(String failReason) {
        // 统计URI启动应用失败次数
        Map<String, String> p = Utils.getProperties();
        p.put("failReason", failReason);
        Utils.utCustomHit(mPageName, "OpenFail", p);

        startActivityFail();
        this.finish();
    }

    /**
     * 解析url地址
     * @param uri
     * @return
     */
    private Bundle decodeUri(Uri uri) {
        AppDebug.d(TAG,".decodeUri uri=" + uri.toString());
        String query = uri.getEncodedQuery();
        if (query == null) {
            return null;
        }
        try {
            Bundle bundle = new Bundle();
            Set<String> params = uri.getQueryParameterNames();
            for (String key : params) {
                String value = uri.getQueryParameter(key);
                bundle.putString(key, value);
            }
            return bundle;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public CategoryItem getQianggou() {
        PeriodBuyView v = (PeriodBuyView) mFocuseViewFlipper.getFliperItemView(FlipperItemListener.TYPE_PERIOD_BUY);
        if (null != v) {
            return v.getQianggou();
        }
        return null;
    }

    @Override
    public void enterDetail(GoodsInfo info) {
        boolean needToast = true;

        if (null != info) {
            int type = info.getType();
            String itemId = info.getItemId();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(CoreIntentKey.URI_FROM_APP_BUNDLE, getAppName());
            intent.putExtra(CoreIntentKey.URI_FROM, getAppName());
            intent.putExtra("frominner", true);
            StringBuilder sb = new StringBuilder(100);

            if (type == GoodsInfo.ITEM_TYPE_SECKILL_INFO) {
                sb.append("tvtaobao://home?app=seckill&module=detail");
                sb.append("&itemId=");
                sb.append(itemId);

                //				sb.append("&stock=");
                //				int count = info.getRemainingNum();
                //				if( !info.isFuture() ){
                //					count += info.getSoldNum();
                //				}
                //				sb.append(count);
            } else {
                sb.append("tvtaobao://home?app=taobaosdk&module=detail");

                byte status = getDetailStatus(info);
                if (status == 1 && !TextUtils.isEmpty(info.getEndTime())) {
                    sb.append("&time=");
                    sb.append(info.getEndTime());
                } else if (!TextUtils.isEmpty(info.getStartTime())) {
                    sb.append("&time=");
                    sb.append(info.getStartTime());
                }
                if (!TextUtils.isEmpty(info.getSeckillId())) {
                    sb.append("&status=");
                    sb.append(status);
                    sb.append("&qianggouId=");
                    sb.append(info.getSeckillId());
                }
                if (info.getSalePrice() > 0) {
                    sb.append("&price=");
                    sb.append(info.getSalePrice());
                }
                sb.append("&itemId=");
                sb.append(itemId);
            }

            try {
                // /**将goodsInfo数据也传递过去*/
                // Bundle bundle = new Bundle();
                // bundle.putSerializable("goodsInfo", info);
                // intent.putExtras(bundle);
                String url = sb.toString();
                AppDebug.i(TAG, "enterDetail" + "url = " + url);
                intent.setData(Uri.parse(url));
                intent.putExtra("extParams", "{\"umpChannel\":\"qianggou\",\"u_channel\":\"qianggou\"}");
                this.startActivity(intent);
                needToast = false;
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }

        }

        if (needToast) {
            String name = this.getResources().getString(R.string.ytbv_not_open);
            Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 传递给详情页面的状态信息
     * @param info
     *            商品信息
     *            上下文
     * @return 0：过去 1：现在 2：将来
     */
    public byte getDetailStatus(GoodsInfo info) {
        if (null != info) {
            if (info.isFuture()) {
                return 2;
            } else {
                long endTime = DateUtils.string2Timestamp(info.getEndTime());
                long curTime = AppManager.getInstance(this).getTimerManager().getCurTime();

                return (byte) ((curTime >= endTime) ? 0 : 1);
            }
        }
        return (byte) 0;
    }

    @Override
    protected FocusPositionManager getFocusPositionManager() {
        return mFocusPositionManager;
    }
}
