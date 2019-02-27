package com.yunos.tvtaobao.splashscreen;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.tvtaobao.voicesdk.request.ASRUTRequest;
import com.yunos.tvtaobao.biz.activity.CoreActivity;

import com.ut.mini.UTAnalytics;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.SystemConfig;
import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.util.StringUtil;
import com.yunos.tvtaobao.businessview.R;
import com.yunos.tvtaobao.splashscreen.config.ModeType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 分发activity
 * 1.解析uri，根据INTENT_KEY_APP转发到指定的app
 * 2.分为外部跟内部启动
 * 外部：先启动加载页面，再清空内部标签的intent key，然后走1的流程再分发到指定的app
 * 内部：同1流程
 *
 * @author tingmeng.ytm
 */
public class ProcessActivity {

    private static final String TAG = "ProcessActivity";
    /* 内部 是否外部调用欢迎页从分发里面来 */
    private static final String INTENT_KEY_FROM_PROCESS = "fromprocess";
    /* 内部 添加的uri */
    private static final String INTENT_KEY_INNER_URI = "inneruri";
    /* 是否不显示欢迎页面 */
    private static final String INTENT_KEY_NOT_SHOW_LOADING = "notshowloading";
    /* 判断应用的关键字 */
    private static final String INTENT_KEY_APP = "app";
    /* 判断应用的指定模块的关键字 */
    private static final String INTENT_KEY_MODULE = "module";
    /* 分会场类型关键字 */
    private static final String INTENT_KEY_TYPE = "type";
    /* spm埋点H5传递的key */
    public static final String INTENT_KEY_SPM = "spm";
    /* 对应每个应用的查找表 */
    private static HashMap<String, String> mAppHostMap;
    /* 外部传入的参数，这个要原封不动的传给后续的activity */
    private Bundle mParamsExtrasBundle;


    private Activity mActivity;
    private HashMap<String, String> mSelfActivityMap; // 主模块的页面map
    private HashMap<String, String> mHostPageModuleList;
    private ModeType mode = null;

    private static ProcessActivity mProcessActivity;

    private boolean isFirstActivity = false;

    private ProcessActivity() {

    }

    public static ProcessActivity getInstance() {
        if (null == mProcessActivity) {
            mProcessActivity = new ProcessActivity();
        }
        return mProcessActivity;
    }

    public void init(Activity activity) {
        mActivity = activity;
    }


    public void destroy() {
        mActivity = null;
    }

    /**
     * 解析分发activity
     *
     * @return
     */
    public boolean processActivity() {
        if (mActivity == null)
            return false;
        // 初始化应用查找表
        initAppHostMap();
        mParamsExtrasBundle = mActivity.getIntent().getExtras();
        AppDebug.v(TAG, TAG + ".processActivity.mParamsExtrasBundle = " + mParamsExtrasBundle);
        //从intent的getData中获取uri
        Uri uri = mActivity.getIntent().getData();
        AppDebug.v(TAG, TAG + ".processActivity.uri = " + uri);

        if (uri == null) { //这时如果uri为空，则获取intent的extras中的uri
            if (mParamsExtrasBundle != null) {
                // 提取内部的uri
                String uriString = mParamsExtrasBundle.getString(INTENT_KEY_INNER_URI);
                if (uriString != null) {
                    uri = Uri.parse(uriString);
                    AppDebug.i(TAG, TAG + ".INTENT_KEY_INNER_URI uriString=" + uriString + ".uri = " + uri);
                }
            }
        }
        // 无URI直接报错返回
        if (uri == null) {
            openFail("NoUri");
            return false;
        }
        // 解析Uri，无参数就报错
        Bundle bundle = decodeUri(uri);
        if (bundle == null) {
            openFail("NoBundle");
            return false;
        }

        boolean inner = false;
        boolean inheritFlags = false;

        String spm_url = null;

        // 取得外部的bundle信息
        if (mParamsExtrasBundle != null) {
            bundle.putAll(mParamsExtrasBundle);
            inner = bundle.getBoolean(CoreActivity.INTENT_KEY_INNER, false);
            inheritFlags = bundle.getBoolean(CoreActivity.INTENT_KEY_INHERIT_FLAGS, false);
            // 删除内部手动添加的关键字
            mParamsExtrasBundle.remove(CoreActivity.INTENT_KEY_INNER);
            mParamsExtrasBundle.remove(INTENT_KEY_FROM_PROCESS);
            mParamsExtrasBundle.remove(INTENT_KEY_NOT_SHOW_LOADING);
            mParamsExtrasBundle.remove(CoreActivity.INTENT_KEY_INHERIT_FLAGS);

            spm_url = bundle.getString(INTENT_KEY_SPM);
            AppDebug.i(TAG, TAG + ".processActivity.spm_url=" + spm_url);
//           Utils.updateNextPageProperties(spm_url);

        }

        /*
         * TODO 当from_app不为空时，说明有可能是合作的系统厂商的语音跳转进来的，
         * 然后进行from判断，为voice_system需要记录一条数据
         */
        String fromApp = bundle.getString(CoreIntentKey.URI_FROM_APP);
        if (fromApp != null) {
            String from = bundle.getString(CoreIntentKey.URI_FROM);
            if ("voice_system".equals(from)) {
                BusinessRequest.getBusinessRequest().baseRequest(new ASRUTRequest("open_page", "system"), null, false);
            }
        }

        boolean result = false;
        AppDebug.i(TAG, TAG + ".processActivity.inner=" + inner + ".inheritFlags = " + inheritFlags);
        int intentFlag = 0;
        /*
         * 继承IntentFlags，
         * 因为通过URI启动目标页面时往往中间会有个负责转发的页面
         * 有些Flag需要透传到针对目标页面的操作
         */
        if (inheritFlags) {
            intentFlag = mActivity.getIntent().getFlags();
        }
        String app = bundle.getString(INTENT_KEY_APP);
        AppDebug.i(TAG, TAG + ".processActivity.bundle=" + bundle + ".SYSTEM_YUNOS_4_0 = "
                + SystemConfig.SYSTEM_YUNOS_4_0);
        String module = bundle.getString(INTENT_KEY_MODULE);
        // 如果为空就是自己本身的页面
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (StringUtil.isEmpty(app)) {
                result = gotoSelfAppActivity(bundle, intentFlag);
            } else if (app.equals("tvtaobao") || app.equals("taobaosdk") || app.equals("takeout")) {
                result = gotoSelfAppActivity(bundle, intentFlag);
            } else if (app.equals("chongzhi")) {
                bundle.putString(INTENT_KEY_MODULE, BaseConfig.INTENT_KEY_MODULE_CHONGZHI);
                result = gotoSelfAppActivity(bundle, intentFlag);
            } else if (app.equals("zhuanti")) {
                String mModule = IntentDataUtil.getString(mActivity.getIntent(), INTENT_KEY_MODULE, null);
                isFirstActivity = mActivity.getIntent().getBooleanExtra(CoreIntentKey.URI_IS_FIRST_ACTIVITY, false);
                bundle.putString(INTENT_KEY_MODULE, mModule);
                result = gotoSelfAppActivity(bundle, intentFlag);
            } else if (app.equals("juhuasuan")) {
                bundle.putString(INTENT_KEY_MODULE, BaseConfig.INTENT_KEY_MODULE_JUHUASUAN);
                result = gotoSelfAppActivity(bundle, intentFlag);
            } else {
                result = gotoOtherAppActivity(app, uri.getEncodedQuery(), intentFlag);
            }
        }
        mActivity = null;
        return result;
    }

    /**
     * 是否不显示加载页面
     *
     * @return
     */
    public boolean isNotShowLoading() {
        Bundle bundle = mActivity.getIntent().getExtras();
        boolean notShowLoading = false;
        if (bundle != null) {
            String notShowLoadingValue = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                notShowLoadingValue = bundle.getString(INTENT_KEY_NOT_SHOW_LOADING, "");
            }
            if (!TextUtils.isEmpty(notShowLoadingValue)) {
                notShowLoading = notShowLoadingValue.toLowerCase().equals("true");
            }
        }
        AppDebug.i(TAG, "isNotShowLoading notShowLoading=" + notShowLoading);
        return notShowLoading;
    }

    /**
     * 是否从分发的外部调用欢迎页activity里面来的（只有外部调用的时候才返回true）
     *
     * @return
     */
    public boolean isFromProcess() {
        if (mActivity == null)
            return false;
        Intent intent = mActivity.getIntent();
        Bundle bundle = null;
        if (intent != null) {
            bundle = mActivity.getIntent().getExtras();
        }
        boolean fromProcess = false;
        if (bundle != null) {
            fromProcess = bundle.getBoolean(INTENT_KEY_FROM_PROCESS, false);
        }
        AppDebug.i(TAG, "isFromProcess fromProcess=" + fromProcess);
        return fromProcess;
    }

    /**
     * 启动自己的本身应用的activity
     *
     * @param bundle
     * @param flags  上层传递下来的flags参数
     * @return boolean
     */
    private boolean gotoSelfAppActivity(Bundle bundle, int flags) {

        initSelfActivityMap();
        String selfModule = bundle.getString(INTENT_KEY_MODULE);
        if (Config.isDebug()) {
            AppDebug.i(TAG, TAG + ".gotoSelfAppActivity bundle=" + bundle + ".flags = " + flags + ".selfModule " + selfModule);
        }

        if (selfModule != null) {
            Intent intent = null;
            String activityClass = mSelfActivityMap.get(selfModule);
            if (activityClass != null) {
                intent = new Intent();
                intent.setClassName(mActivity, activityClass);
            }
            if ("cart".equalsIgnoreCase(selfModule)) {
                String cartFrom = bundle.getString(BaseConfig.INTENT_KEY_CARTFROM);
                if ("tsm_client_native".equalsIgnoreCase(cartFrom)) {
                    intent.setClassName(mActivity, BaseConfig.SWITCH_TO_MAOCHAO_CART_LIST_ACTIVITY);
                }
            }
            if (intent != null) {
                intent.addFlags(flags);
                intent.putExtras(bundle);
                boolean fromSystem =false;
                String from = bundle.getString(CoreIntentKey.URI_FROM);
                if("voice_system".equals(from)){
                    fromSystem = true;
                }
                intent.putExtra(CoreIntentKey.URI_FROM_SYSTEM,fromSystem);
                mActivity.startActivity(intent);
//                mActivity.finish();
                gotoFinishActivity();
                return true;
            } else {
                openFail("NoIntent");
                return false;
            }
        } else {
            openFail("NoSelfModule");
            return false;
        }
    }

    /**
     * 启动其它应用的activity
     *
     * @param app
     * @param query URI相关参数信息
     * @param flags 上层传递下来的flags参数
     * @return boolean
     */
    private boolean gotoOtherAppActivity(String app, String query, int flags) {
        if (Config.isDebug()) {
            AppDebug.i(TAG, TAG + ".gotoOtherAppActivity.app=" + app);
        }
        if (query == null) {
            openFail("NoQuery");
            return false;
        }
        String appHost = mAppHostMap.get(app);
        if (appHost == null) {
            openFail("NoAppHost");
            return false;
        }
        try {
            String uri = appHost + query;
            Uri theUri = Uri.parse(uri);
            AppDebug.i(TAG, TAG + ".gotoOtherAppActivity.uri=" + uri + ", flags = " + flags + ", theUri = "
                    + theUri);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(flags);
            intent.setData(theUri);
            if (mParamsExtrasBundle != null)

                intent.putExtras(mParamsExtrasBundle);
            mActivity.startActivity(intent);
//            mActivity.finish();
            gotoFinishActivity();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            openFail("StartActivityError");
            return false;
        }
    }

    /**
     * 控制activity的关闭，因为当吊起page页面时，如果page页面还没有起来先finish，可能会出现界面黑屏或者闪现前一个activity的界面
     */
    private void gotoFinishActivity() {
        if (mActivity instanceof LoadingActivity) {
            LoadingActivity activity = (LoadingActivity) mActivity;
            if (activity != null) {
                activity.needFinish = true;
            }
        } else {
            mActivity.finish();
        }
    }

    /**
     * 解析url地址
     *
     * @param uri
     * @return
     */
    public static Bundle decodeUri(Uri uri) {
        if (Config.isDebug()) {
            AppDebug.i(TAG, TAG + ".decodeUri uri=" + uri.toString());
        }
        String query = uri.getEncodedQuery();
        if (query == null) {
            return null;
        }
        try {
            Bundle bundle = new Bundle();
            Set<String> params = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                params = uri.getQueryParameterNames();
            }
            for (String key : params) {
                String value = uri.getQueryParameter(key);
                bundle.putString(key, value);
                if (Config.isDebug()) {
                    AppDebug.i(TAG, TAG + ".decodeUri key=" + key + " value=" + value);
                }
            }
            return bundle;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 初始化应用的查找表
     */
    private void initAppHostMap() {
        if (mAppHostMap == null) {
            mAppHostMap = new HashMap<String, String>();
            mAppHostMap.put("zhuanti", "tvtaobao://zhuanti?"); // 专题活动native
            mAppHostMap.put("browser", "tvtaobao://browser?"); // 专题活动H5
            mAppHostMap.put("taobaosdk", "tvtaobao://taobaosdk?"); // 淘宝sdk
            mAppHostMap.put("juhuasuan", "tvtaobao://juhuasuan?"); // 聚划算
            mAppHostMap.put("seckill", "tvtaobao://seckill?"); // 秒杀
            mAppHostMap.put("chaoshi", "tvtaobao://chaoshi?"); // 超市
            mAppHostMap.put("caipiao", "tvtaobao://caipiao?"); // 彩票
            mAppHostMap.put("tvshopping", "tvtaobao://tvshopping?"); // 边看边购
            mAppHostMap.put("flashsale", "tvtaobao://flashsale?"); // 淘抢购
            mAppHostMap.put("voice", "tvtaobao://voice?");
            mAppHostMap.put("takeout", "tvtaobao://takeout?"); //淘宝外卖
        }
    }

    /**
     * 打开activity失败的提示
     *
     * @param context
     */
    private void startActivityFail(Context context) {
        Toast.makeText(context, context.getResources().getString(R.string.ytbv_start_activity_error),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * 打开失败
     *
     * @param failReason
     */
    private void openFail(String failReason) {
        //统计URI启动应用失败次数
        Map<String, String> p = Utils.getProperties();
        p.put("failReason", failReason);
        CoreActivity activity = (CoreActivity) mActivity;
        if (activity != null) {
            Utils.utCustomHit(activity.getFullPageName(), "OpenFail", p);
        } else {
            Utils.utCustomHit("OpenFail", p);
        }

        startActivityFail(mActivity);
        mActivity.finish();
    }

    /**
     * 初始化自己主模块的activity
     */
    private void initSelfActivityMap() {
        if (mSelfActivityMap == null) {
            mSelfActivityMap = new HashMap<String, String>();
            boolean isBlitzShop = SharePreferences.getBoolean("isBlitzShop", false);

            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_TAKEOUT_ORDER_LIST, BaseConfig.SWITCH_TO_TAKEOUT_ORDER_LIST_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_TAKEOUT_ORDER_DETAIL, BaseConfig.SWITCH_TO_TAKEOUT_ORDER_DETAIL_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_SHOP_SEARCH, BaseConfig.SWITCH_TO_TAKEOUT_SHOP_SEARCH_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_DETAIL, BaseConfig.SWITCH_TO_DETAIL_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_GOODSLIST, BaseConfig.SWITCH_TO_GOODLIST_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_MYTAOBAO, BaseConfig.SWITCH_TO_MYTAOBAO_ACTIVITY);
//            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_ADDRESS, AddressActivity.class);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_ORDERLIST, BaseConfig.SWITCH_TO_ORDERLIST_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_NEW_TVBUY, BaseConfig.SWITCH_TO_NEW_TVBUY_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_TVBUY_SHOPPING, BaseConfig.SWITCH_TO_TVBUY_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_COLLECTS, BaseConfig.SWITCH_TO_COLLECTS_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_COUPON, BaseConfig.SWITCH_TO_COUPON_ACTIVITY);
            if (!isBlitzShop) {
                mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_SHOP, BaseConfig.SWITCH_TO_SHOP_ACTIVITY);
            } else {
                mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_SHOP, BaseConfig.SWITCH_TO_SHOP_BLIZ_ACTIVITY);
            }
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_SUREJOIN, BaseConfig.SWITCH_TO_SKU_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_SEARCH, BaseConfig.SWITCH_TO_SEARCH_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_RECOMMEND, BaseConfig.SWITCH_TO_RECOMMEND_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_MAIN, BaseConfig.SWITCH_TO_HOME_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_CART, BaseConfig.SWITCH_TO_SHOPCART_LIST_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_MENU, BaseConfig.SWITCH_TO_MENU_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_COMMON, BaseConfig.SWITCH_TO_COMMON_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_TAKEOUT_WEB, BaseConfig.SWITCH_TO_TAKEOUT_WEB_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_ADDRESS_WEB, BaseConfig.SWITCH_TO_ADDRESS_WEB_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_RELATIVE_RECOMMEND, BaseConfig.SWITCH_TO_RELATIVERECOMMEND_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_CHAOSHI, BaseConfig.SWITCH_TO_CHAOSHI_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_TODAYGOODS, BaseConfig.SWITCH_TO_TODAYGOODS_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_CHONGZHI, BaseConfig.SWITCH_TO_CHONGZHI_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_FLASHSALE_MAIN, BaseConfig.SWITCH_TO_FLASHSALE_MAIN_ACTIVITY);

            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_MYTAOBAO, BaseConfig.SWITCH_TO_MYTAOBAO_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_JUHUASUAN, BaseConfig.SWITCH_TO_JUHUASUAN_ACTIVITY);

            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_PAYRESULT, BaseConfig.SWITCH_TO_PAYRESULT_ACTIVITY);

            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_GRAPHICDETAILS, BaseConfig.SWITCH_TO_SKILL_GRAPHICDETAILS_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_TAKEOUT_HOME, BaseConfig.SWITCH_TO_TAKEOUT_SHOP_HOME);

            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_ANSWER, BaseConfig.SWITCH_TO_ANSWER_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_HQLIVE, BaseConfig.SWITCH_TO_HQLIVE_ACTIVITY);
//            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_LOGIN, BaseConfig.SWITCH_TO_LOGIN_ACTIVITY);
//            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_ALIPAY, BaseConfig.SWITCH_TO_ALIPAY_ACTIVITY);
//
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_LIKE, BaseConfig.SWITCH_TO_GUESS_YOU_LIKE_ACTIVITY);

            if (Config.isDebug())
                mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_TESTORDER, BaseConfig.SWITCH_TO_BUILDORDER_ACTIVITY);
//            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_TVBUY, TVBuyActivity.class);
//            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_VIDEO, LiveJumpActivity.class);

            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_CREATE_ORDER, BaseConfig.SWITCH_TO_VOICE_CREATE_ORDER_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_LOGOUT, BaseConfig.SWITCH_TO_LOGINOUT_ACTIVITY);
            mSelfActivityMap.put(BaseConfig.INTENT_KEY_MODULE_TAOBAOLIVE,BaseConfig.SWITCH_TO_TABPBAO_LIVE_ACTIVITY);

        }
    }

}
