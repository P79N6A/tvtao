package com.yunos.tvtaobao.h5.commonbundle.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.yunos.alitvcompliance.TVCompliance;
import com.yunos.alitvcompliance.types.RetCode;
import com.yunos.alitvcompliance.types.RetData;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tv.core.util.SystemUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.CoreActivity;
import com.yunos.tvtaobao.biz.activity.TaoBaoBlitzActivity;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.info.GlobalConfigInfo;
import com.yunos.tvtaobao.biz.util.StringUtil;
import com.yunos.tvtaobao.h5.commonbundle.R;
import com.yunos.tvtaobao.h5.commonbundle.h5.plugin.GuardPlugin;
import com.yunos.tvtaobao.h5.commonbundle.h5.plugin.TakeOutPlugin;
import com.yunos.tvtaobao.h5.commonbundle.h5.plugin.TvTaoBaoZtcPlugin;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.Map;

public class CommonActivity extends TaoBaoBlitzActivity {


    private String page;
    private boolean mIsBackHome;
    private GlobalConfig globalConfig;
    private String CLICK_ID = "click_id";
    private TvTaoBaoZtcPlugin mZtcPlugin;
    private GuardPlugin guardPlugin;

    public String getPage() {
        return page;
    }

    protected final void setPage(String page) {
        this.page = page;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            page = processPageUrl();
            if (TextUtils.isEmpty(page)) {
                toastPageError(getString(com.yunos.tvtaobao.businessview.R.string.ytbv_not_found_page), true);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            toastPageError(getString(R.string.ytbv_invalid_h5_url), true);
            return;
        }

        if (!Config.isDebug() && !StringUtil.isInnerUrl(page)) {
            toastPageError(getString(R.string.ytbv_invalid_h5_url), true);
            return;
        }

        AppDebug.v(TAG, TAG + ".onCreate.page = " + page);
        globalConfig = GlobalConfigInfo.getInstance().getGlobalConfig();
        if (GlobalConfig.instance == null || !GlobalConfig.instance.isBeta()) {
            mZtcPlugin = new TvTaoBaoZtcPlugin(new WeakReference<TaoBaoBlitzActivity>(this));
        }
        guardPlugin= new GuardPlugin(new WeakReference<TaoBaoBlitzActivity>(this));
        onInitH5View(page);
    }




    protected String processPageUrl() throws Exception {
        String page = IntentDataUtil.getString(getIntent(), "page", null);

        if (TextUtils.isEmpty(page)) {
            return page;
        }

        mIsBackHome = isBackHome();

        page = URLDecoder.decode(page);
        page = PageUtil.processPageUrl(page);

        //构建语音路径
        page = buildVoiceUrl(page, getIntent());

        return page;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // js 桥
        TakeOutPlugin.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mZtcPlugin != null) {
            mZtcPlugin.destroy();
        }

        TakeOutPlugin.unregister(this);
    }

    private String addClickIdInUrl(String pageUrl, String click_id) {

        String newUrl = pageUrl;
        try {
            newUrl = naiveAppendQueryParam(pageUrl, CLICK_ID, click_id);
        } catch (UnsupportedEncodingException exception) {
            exception.printStackTrace();
        } finally {
            return newUrl;
        }

    }

    @Override
    public String getPageName() {
        String pageName = "Common";
        String urlName = SystemUtil.getUrlFileName(page);
        if (!TextUtils.isEmpty(urlName)) {
            pageName = pageName + "_" + urlName;
        }
        AppDebug.v(TAG, TAG + ".getPageName.pageName = " + pageName);
        return pageName;
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        String name = SystemUtil.getUrlFileName(page);
        if (!TextUtils.isEmpty(name)) {
            p.put("name", name);
        }
        return p;
    }

    @Override
    public void handleBackPress() {
        if (mIsBackHome) {
            String homeUri = "tvtaobao://home?module=main&" + CoreIntentKey.URI_FROM_APP + "=" + getAppName();
            Intent intent = new Intent();
            intent.putExtra(CoreActivity.INTENT_KEY_INHERIT_FLAGS, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setData(Uri.parse(homeUri));
            startActivity(intent);
            AppDebug.i(TAG, "onBackPressed mIsBackHome  = " + mIsBackHome + "; homeUri = " + homeUri + "; intent = "
                    + intent);
            return;
        }
        super.handleBackPress();
    }

    /**
     * 是否返回首页
     *
     * @return
     */
    protected boolean isBackHome() {
        boolean isbackhome = false;
        String isBackHomeValue = IntentDataUtil.getString(getIntent(), "isbackhome", null);
        String isJoin = IntentDataUtil.getString(getIntent(), CoreIntentKey.URI_JOIN, null);

        AppDebug.i(TAG, "isBackHome isBackHomeValue  = " + isBackHomeValue + " ,isJoin= " + isJoin);
        if (!TextUtils.isEmpty(isBackHomeValue)) {
            isbackhome = isBackHomeValue.toLowerCase().equals("true");
        }
        /**
         * 后面换成可以配置
         */
        if (!TextUtils.isEmpty(isJoin)) {
            if (globalConfig != null) {
                isbackhome = globalConfig.getAfp().isIsbackhome();
                AppDebug.e("TAG", "isBackHome===111" + isbackhome);
            } else {
                isbackhome = true;
                AppDebug.e("TAG", "isBackHome===222");
            }

            AppDebug.e("TAG", "isBackHome===");
        }
        AppDebug.i(TAG, "isBackHome isbackhome = " + isbackhome);
        return isbackhome;
    }

    @Override
    public void interceptBack(boolean isIntercept) {
    }

    @Override
    public void onWebviewPageDone(String url) {
        super.onWebviewPageDone(url);

    }

    protected final void toastPageError(String errorMsg, boolean finishActivity) {
        Toast.makeText(this.getApplicationContext(), errorMsg, Toast.LENGTH_SHORT)
                .show();
        if (finishActivity) {
            finish();
        }
    }
}