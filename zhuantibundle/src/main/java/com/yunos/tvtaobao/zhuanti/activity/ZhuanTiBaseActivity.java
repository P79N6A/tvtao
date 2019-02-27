package com.yunos.tvtaobao.zhuanti.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.yunos.tvtaobao.biz.activity.CoreActivity;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.request.core.AsyncDataLoader;
import com.yunos.tvtaobao.zhuanti.constant.IntentKey;


/**
 * 基础Activity，用户页面统计
 * @author hanqi
 */
public abstract class ZhuanTiBaseActivity extends BaseActivity {

    protected final String TAG = "zhuanti_" + getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
    private final String TAGS = "ZtBaseActivity[" + TAG + "]";
    //是否已经执行onCreate
    protected boolean isCreate = false;

    private boolean mIsForeground = false;

    private boolean mIsBackHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isCreate = true;
        mIsBackHome = isBackHome();
    }


    @Override
    protected void onDestroy() {
        AppDebug.i(TAGS, TAGS + ".onDestroy ");

        // 清理网络加载数据的线程池
        AsyncDataLoader.purge();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsForeground = true;
    }

    @Override
    protected void onPause() {
        mIsForeground = false;
        removeNetworkOkDoListener();
        super.onPause();
    }

    @Override
    protected String getAppTag() {
        return "Zt";
    }

    @Override
    protected String getAppName() {
        return "zhuanti";
    }

    @Override
    public void handleBackPress() {
        if (mIsBackHome) {
            String homeUri = "tvtaobao://home?module=main&" + CoreIntentKey.URI_FROM_APP + "=" + getAppName();
            Intent intent = new Intent();
            intent.putExtra(CoreActivity.INTENT_KEY_INHERIT_FLAGS, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setData(Uri.parse(homeUri));
            startActivity(intent);
            finish();
            AppDebug.i(TAG, "onBackPressed mIsBackHome  = " + mIsBackHome + "; homeUri = " + homeUri + "; intent = "
                    + intent);
            return;
        }
        super.handleBackPress();
    }

    protected void requestUpdate() {
//        final UpdateController controller = UpdateController.getInstance();
//        AppDebug.e(TAG, TAG + ".requestUpdate isAlreadyShow : " + controller.isAlreadyShow);
//        if (!controller.isAlreadyShow) {
//            controller.setUpdateListener(new UpdateController.UpdateListener() {
//                @Override
//                public void onApkExit(Bundle bundle) {
//                    AppDebug.e(TAG, TAG + ".onApkExit");
//                    UpdateDialog updateDialog = new UpdateDialog(ZhuanTiBaseActivity.this);
//                    updateDialog.setBundle(bundle);
//                    updateDialog.show();
//
//                    controller.addCurrentToSharedPreferences();
//                }
//            });
//            controller.requestUpdate();
//        }
    }
    /**
     * 是否返回首页
     * @return
     */
    protected boolean isBackHome() {
        boolean isbackhome = false;
        String isBackHomeValue = IntentDataUtil.getString(getIntent(), IntentKey.IS_BACK_HOME, null);
        AppDebug.i(TAG, "isBackHome isBackHomeValue  = " + isBackHomeValue);
        if (!TextUtils.isEmpty(isBackHomeValue)) {
            isbackhome = isBackHomeValue.toLowerCase().equals("true");
        }
        AppDebug.i(TAG, "isBackHome isbackhome = " + isbackhome);
        return isbackhome;
    }

    /**
     * 显示设置对话框
     * @param okfinish 按设置后是否关闭当前界面
     */
    public void onShowNetDialog(final boolean okfinish) {
        showNetworkErrorDialog(okfinish);
    }

    /**
     * 返回当前的界面是否在前台
     * @return
     */
    public boolean isForeground() {
        return mIsForeground;
    }

    /**
     * @param url
     */
    protected void onInitH5View(String url) {
        loadWithUrl(url);
        setH5BackGroud();
        OnWaitProgressDialog(true);
    }
    
}
