package com.yunos.tvtaobao.payment.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.yunos.tvtaobao.payment.analytics.Utils;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * <pre>
 *     author : xutingting
 *     e-mail : xutingting@zhiping.tech
 *     time   : 2017/12/27
 *     desc   :
 *     version: 1.0
 * </pre>
 */
abstract public  class BaseActivity extends Activity {
    //页面名称
    protected String mPageName;
    // 外部来源
    private String mFrom;
    // 内部来源
    private String mHuoDong;
    // 来源应用
    private String mApp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        enterUT();

    }

    @Override
    protected void onPause() {
        super.onPause();
        exitUT();
    }
    /**
     * 页面名称
     *
     * @return
     */
    public String getFullPageName() {
        if (TextUtils.isEmpty(mPageName)) {
//            mPageName = getAppTag() + getPageName();
            mPageName = getPageName();
        }

        return mPageName;
    }


    public String getPageName() {
        return Pattern.compile("(activity|view|null|page|layout)$", Pattern.CASE_INSENSITIVE)
                .matcher(getClass().getSimpleName()).replaceAll("");
    }

//    /**
//     * 定义应用关键字，和pageName合并拼成一个页面名称
//     *
//     * @return
//     */
//    abstract protected String getAppTag();
//


    /**
     * 进入UT 埋点
     */
    protected void enterUT() {
//        if (isTbs()) {
        mPageName = getFullPageName();
        //如果这应页面需要做代码统计，就不能不设置名称
        if (TextUtils.isEmpty(mPageName)) {
            throw new IllegalArgumentException("The PageName was null and TBS is open");
        }
        Utils.utPageAppear(mPageName, mPageName);
//        AppDebug.i(TAGS, TAGS + ".enterUT end mPageName=" + mPageName);
//        }
    }

    /**
     * 退出UT
     */
    protected void exitUT() {
        if (!TextUtils.isEmpty(mPageName)) {
            Map<String, String> p = getPageProperties();
//            AppDebug.i(TAGS, TAGS + ".exitUI TBS=updatePageProperties(" + p + ")");
            Utils.utUpdatePageProperties(mPageName, p);
            Utils.utPageDisAppear(mPageName);
        }
    }

    public Map<String, String> getPageProperties() {
        return Utils.getProperties(mFrom, mHuoDong, mApp);
    }


}
