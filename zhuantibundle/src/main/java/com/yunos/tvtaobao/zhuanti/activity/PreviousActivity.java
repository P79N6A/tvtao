package com.yunos.tvtaobao.zhuanti.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.AppHandler;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tvtaobao.biz.listener.NetworkOkDoListener;
import com.yunos.tvtaobao.zhuanti.R;
import com.yunos.tvtaobao.zhuanti.bo.enumration.HandleWhat;
import com.yunos.tvtaobao.zhuanti.bo.enumration.ZhuanTiType;
import com.yunos.tvtaobao.zhuanti.constant.IntentKey;

/**
 * 首页 外部调用Url：yunoszhuanti://home?id=1111&modul=bbb&from=aaa
 * 资源名称: UI+版本号（大版本号）+模块名称+资源名称
 * 过渡activity 不用作显示
 */
public class PreviousActivity extends ZhuanTiBaseActivity {

    private final String TAG = "PreviousActivity";

    private ZhuanTiType mode = null;
    private boolean isFirstActivity = false;

    private AppHandler<PreviousActivity> mHomeHandler = new AppHandler<PreviousActivity>(this) {

        @Override
        public void handleMessage(Message msg) {
            PreviousActivity activity = getT();
            AppDebug.v(TAG, TAG + ".handleMessage    activity = " + activity + "; msg.what = " + msg.what);
            if (null == activity) {
                return;
            }
            switch (msg.what) {
                case HandleWhat.MSG_GO_TO_HUABAO_ACTIVITY:
                    activity.gotoHuabaoActivity();
                    break;
                case HandleWhat.MSG_GO_TO_OLD_TV_SHOPPING_ACTIVITY:
                    activity.gotoOldTvShoppingActivity();
                    break;
                case HandleWhat.MSG_GO_TO_NEW_TV_SHOPPING_ACTIVITY:
                    activity.gotoNewTvShoppingActivity();
                    break;

                case HandleWhat.MSG_GO_TO_TV_SHOPPING_ACTIVITY:
                    activity.gotoTvShoppingActivity();
                    break;
                case HandleWhat.MSG_GO_TO_TEJIA_ACTIVITY:
                    activity.gotoZhuHuiChangActivity();
                    break;
                case HandleWhat.MSG_GO_TO_TIANTIAN_ACTIVITY:
                    activity.gotoTianTianActivity();
                    break;

                case HandleWhat.MSG_GO_TO_QINGCANG_ACTIVITY:
                    activity.gotoQingcangTopicActivity();
                    break;

                case HandleWhat.MSG_GO_TO_TVCOMMEND_SINGLE_ACTIVITY:
                    activity.gotoTvCommendSinActivity();
                    break;

                case HandleWhat.MSG_GO_TO_FENLEI_ACTIVITY:
                    activity.gotoFenLeiActivity();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        String mModule = IntentDataUtil.getString(getIntent(), IntentKey.URI_MODULE, null);
        isFirstActivity = getIntent().getBooleanExtra(CoreIntentKey.URI_IS_FIRST_ACTIVITY, false);

        AppDebug.v(TAG, TAG + ".onCreate mModule   = " + mModule + ", isFirstActivity = " + isFirstActivity
                + ", uri = " + uri);

        try {
            mode = ZhuanTiType.valueOf(ZhuanTiType.class, mModule);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        AppDebug.v(TAG, TAG + ".onCreate RunMode mode = " + mode);
        if (mode == null) {
            showErrorDialog(getString(R.string.ytm_invalid_parameter), true);
            return;
        }

        if (NetWorkUtil.isNetWorkAvailable()) {
            dispatchModle(mode);
        } else {
            setNetworkOkDoListener(new NetworkOkDoListener() {

                @Override
                public void todo() {
                    dispatchModle(mode);
                }
            });
            showNetworkErrorDialog(true);
        }
    }

    public void dispatchModle(ZhuanTiType mode) {
        switch (mode) {
            case huabao:
                mHomeHandler.sendEmptyMessage(HandleWhat.MSG_GO_TO_HUABAO_ACTIVITY);
                break;
            case oldtvshop:
                mHomeHandler.sendEmptyMessage(HandleWhat.MSG_GO_TO_OLD_TV_SHOPPING_ACTIVITY);
                break;
            case newtvshop:
                mHomeHandler.sendEmptyMessage(HandleWhat.MSG_GO_TO_NEW_TV_SHOPPING_ACTIVITY);
                break;
            case tvshopping:
                mHomeHandler.sendEmptyMessage(HandleWhat.MSG_GO_TO_TV_SHOPPING_ACTIVITY);
                break;
            case zhuhuichang:
                mHomeHandler.sendEmptyMessage(HandleWhat.MSG_GO_TO_TEJIA_ACTIVITY);
                break;
            case tiantiantejia:
                mHomeHandler.sendEmptyMessage(HandleWhat.MSG_GO_TO_TIANTIAN_ACTIVITY);
                break;
            case taobaoqingcang:
                mHomeHandler.sendEmptyMessage(HandleWhat.MSG_GO_TO_QINGCANG_ACTIVITY);
                break;

            case tvcommendsingle:
                mHomeHandler.sendEmptyMessage(HandleWhat.MSG_GO_TO_TVCOMMEND_SINGLE_ACTIVITY);
                break;

            case goodsshopping:
            case fenlei:
                mHomeHandler.sendEmptyMessage(HandleWhat.MSG_GO_TO_FENLEI_ACTIVITY);
                break;

            default:
                break;
        }
    }

    /**
     * 进入画报型专题界面
     */
    private void gotoHuabaoActivity() {
//        AppDebug.v(TAG, TAG + ".gotoHuabaoActivity");
//        Intent intent = new Intent();
//        intent.setData(getIntent().getData());
//        intent.putExtra(CoreIntentKey.URI_IS_FIRST_ACTIVITY, isFirstActivity);
//        intent.setClass(this, HuabaoActivity.class);
//        onStartActivity(intent);
    }

    /**
     * 进入视频专题界面 2.0
     */
    private void gotoTvShoppingActivity() {
//        AppDebug.v(TAG, TAG + ".gotoTvShoppingActivity");
//        Intent intent = new Intent();
//        intent.setData(getIntent().getData());
//        intent.putExtra(CoreIntentKey.URI_IS_FIRST_ACTIVITY, isFirstActivity);
//        intent.setClass(this, TvBuyActivity.class);
//        onStartActivity(intent);
    }
    /**
     * 进入新版视频达人页面 3.0
     */
    private void gotoNewTvShoppingActivity() {
        AppDebug.v(TAG, TAG + ".gotoNewTvShoppingActivity");
        Intent intent = new Intent();
        intent.setData(getIntent().getData());
        intent.putExtra(CoreIntentKey.URI_IS_FIRST_ACTIVITY, isFirstActivity);
        intent.setClass(this, NewTvBuyActivity.class);
        onStartActivity(intent);
    }

    /**
     * 进入最老版视频页面 1.0
     */
    private void gotoOldTvShoppingActivity() {
//        AppDebug.v(TAG, TAG + ".gotoNewTvShoppingActivity");
//        Intent intent = new Intent();
//        intent.setData(getIntent().getData());
//        intent.putExtra(CoreIntentKey.URI_IS_FIRST_ACTIVITY, isFirstActivity);
//        intent.setClass(this, TvShoppingActivity.class);
//        onStartActivity(intent);
    }


    /**
     * 进入特价型专题界面
     */
    private void gotoZhuHuiChangActivity() {

//        AppDebug.v(TAG, TAG + ".gotoTejiaTopicActivity");
//        Intent intent = new Intent();
//        intent.setData(getIntent().getData());
//        intent.putExtra(CoreIntentKey.URI_IS_FIRST_ACTIVITY, isFirstActivity);
//        intent.setClass(this, TeJiaActivity.class);
//        onStartActivity(intent);
    }

    /**
     * 进入特价型专题界面
     */
    private void gotoTianTianActivity() {

//        AppDebug.v(TAG, TAG + ".gotoTejiaTopicActivity");
//        Intent intent = new Intent();
//        intent.putExtra(IntentKey.URI_MODULE, "tiantiantejia");
//        intent.setData(getIntent().getData());
//        intent.putExtra(CoreIntentKey.URI_IS_FIRST_ACTIVITY, isFirstActivity);
//        intent.setClass(this, TeJiaActivity.class);
//        onStartActivity(intent);
    }

    /**
     * 进入清仓型专题界面
     */
    private void gotoQingcangTopicActivity() {
        // do something...
//        AppDebug.v(TAG, TAG + ".gotoGoodsShoppingActivity");
//        Intent intent = new Intent();
//        intent.putExtra(IntentKey.URI_MODULE, "taobaoqingcang");
//        intent.setData(getIntent().getData());
//        intent.putExtra(CoreIntentKey.URI_IS_FIRST_ACTIVITY, isFirstActivity);
//        intent.setClass(this, TeJiaActivity.class);
//        onStartActivity(intent);
    }

    /**
     * 
     */
    private void gotoFenLeiActivity() {
//        AppDebug.v(TAG, TAG + ".gotoFenLeiActivity");
//        Intent intent = new Intent();
//        intent.setData(getIntent().getData());
//        intent.putExtra(CoreIntentKey.URI_IS_FIRST_ACTIVITY, isFirstActivity);
//        intent.setClass(this, FenLeiActivity.class);
//        onStartActivity(intent);
    }

    /**
     * 进入影视推荐界面
     */
    private void gotoTvCommendSinActivity() {
//        AppDebug.v(TAG, TAG + ".gotoTvCommendSinActivity");
//        Intent intent = new Intent();
//        intent.setData(getIntent().getData());
//        intent.putExtra(CoreIntentKey.URI_IS_FIRST_ACTIVITY, isFirstActivity);
//        intent.setClass(this, TvCommendSinActivity.class);
//        onStartActivity(intent);
    }

    private void onStartActivity(Intent intent) {
        // 传入是否返回首页的标志
        String isBackHomeValue = IntentDataUtil.getString(getIntent(), IntentKey.IS_BACK_HOME, null);
        intent.putExtra(IntentKey.IS_BACK_HOME, isBackHomeValue);

        if (!NetWorkUtil.isNetWorkAvailable()) {
            // 如果网络不可用，那么弹出提示框
            onShowNetDialog(true);
            return;
        }

        // 跳转
        startActivity(intent);

        // 结束本Activity
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mHomeHandler != null) {
            mHomeHandler.removeCallbacksAndMessages(null);
            mHomeHandler = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onStartActivityNetWorkError() {
        onShowNetDialog(true);
    }

    @Override
    public boolean isIgnored() {
        return true;
    }
}
