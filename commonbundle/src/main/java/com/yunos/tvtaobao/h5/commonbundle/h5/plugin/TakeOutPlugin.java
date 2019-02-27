package com.yunos.tvtaobao.h5.commonbundle.h5.plugin;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.tvtaobao.voicesdk.tts.TTSUtils;
import com.ut.mini.UTAnalytics;
import com.yunos.tv.blitz.BlitzPlugin;
import com.yunos.tv.blitz.data.BzResult;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.common.ActivityQueueManager;
import com.yunos.tvtaobao.h5.commonbundle.activity.CommonActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by haoxiang on 2018/1/3
 */

public class TakeOutPlugin {

    private static String TAG = "TakeOutPlugin";
    private static String RESULT = "result";
    private static String ERROR = "error";
    private static String KEY = "key";
    private static String VALUE = "value";
    private Activity currActivity;
    private static TakeOutPlugin instance;
    private TakeOutPlugin(){
        AppDebug.i(TAG, "onInitSearchSetPlugin");
        BlitzPlugin.bindingJs("tvtaobao_takeout_search_set", mTakeoutSearchSetCallback);
        AppDebug.i(TAG, "onInitSpmGetPlugin");
        BlitzPlugin.bindingJs("tvtaobao_spm_get", mSpmGetCallback);

        AppDebug.i(TAG, "onInitSpmSetPlugin");
        BlitzPlugin.bindingJs("tvtaobao_spm_set", mSpmSetCallback);

        AppDebug.i(TAG, "onInitCloseActivityPlugin");
        BlitzPlugin.bindingJs("finish_h5_until_url", mCloseActivityPlugin);

    }
    public static TakeOutPlugin getInstance(){
        if (instance==null){
            synchronized (TakeOutPlugin.class){
                if (instance==null){
                    instance = new TakeOutPlugin();
                }
            }
        }
        return instance;
    }


    public static void register(Activity activity){
        getInstance().currActivity = activity;
    }

    public static void unregister(Activity activity){
        if (getInstance().currActivity == activity){
            getInstance().currActivity = null;
        }
    }

    private static void responseFalse(long cdData) {
        BzResult result = new BzResult();
        result.addData(RESULT, "false");
        String res = result.toJsonString();
        BlitzPlugin.responseJs(false, res, cdData);
    }


    private static BlitzPlugin.JsCallback mTakeoutSearchSetCallback = new BlitzPlugin.JsCallback() {
        @Override
        public void onCall(String param, long cdData) {
            AppDebug.i(TAG, "onCall  SearchSetCallback -->param = " + param + " ; cdData = " + cdData);
            JSONObject json;
            try {
                json = new JSONObject(param);
                boolean searchStatus = json.getBoolean("list-searchStatus");

                if (searchStatus) {
                    if (getInstance().currActivity != null) {
                        getInstance().currActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TTSUtils.getInstance().showDialog(getInstance().currActivity, 1);
                            }
                        });

                    }
                } else {
                    if (getInstance().currActivity != null) {
                        getInstance().currActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TTSUtils.getInstance().showDialog(getInstance().currActivity, 0);
                            }
                        });

                    }
                }
                BzResult result = new BzResult();
                result.setSuccess();
                result.addData(RESULT, "true");
                String res = result.toJsonString();
                BlitzPlugin.responseJs(true, res, cdData);
            } catch (JSONException e) {
                BzResult result = new BzResult();
                result.addData(RESULT, "false");
                String res = result.toJsonString();
                BlitzPlugin.responseJs(false, res, cdData);
                e.printStackTrace();
            }

        }
    };

    private static BlitzPlugin.JsCallback mSpmGetCallback = new BlitzPlugin.JsCallback() {
        @Override
        public void onCall(String param, long cdData) {
            AppDebug.i(TAG, "onCall SpmGetCallback -->param  = " + param + " cdData = " + cdData);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(param);
                String key = jsonObject.getString(KEY);
                String value = SharePreferences.getString(key);
                if (!TextUtils.isEmpty(value)) {
                    AppDebug.i(TAG, " onHandleSpmGet key = " + key + " value = " + value);
                    BzResult result = new BzResult();
                    result.addData(RESULT, value);
                    result.setSuccess();
                    String res = result.toJsonString();
                    BlitzPlugin.responseJs(true, res, cdData);
                } else {
                    responseFalse(cdData);
                }
            } catch (JSONException e) {
                responseFalse(cdData);
                e.printStackTrace();
            }
        }
    };

    private static BlitzPlugin.JsCallback mSpmSetCallback = new BlitzPlugin.JsCallback() {
        @Override
        public void onCall(String param, long cdData) {
            AppDebug.i(TAG, "onCall  SpmSetCallback -->param = " + param + " ; cdData = " + cdData);

            JSONObject json;
            try {
                json = new JSONObject(param);
                String key = json.getString(KEY);
                String value = json.getString(VALUE);
                AppDebug.i(TAG, "onHandleSpmSet key = " + key);
                SharePreferences.put(key, value);
                if (getInstance().currActivity != null) {
                    Intent intent = new Intent();
                    intent.setAction("com.yunos.tvtaobao.address");
                    getInstance().currActivity.sendOrderedBroadcast(intent, null);
                }
                BzResult result = new BzResult();
                result.setSuccess();
                result.addData(RESULT, "true");
                String res = result.toJsonString();
                BlitzPlugin.responseJs(true, res, cdData);
            } catch (JSONException e) {
                BzResult result = new BzResult();
                result.addData(RESULT, "false");
                String res = result.toJsonString();
                BlitzPlugin.responseJs(false, res, cdData);
                e.printStackTrace();
            }

        }
    };

    private static BlitzPlugin.JsCallback mCloseActivityPlugin = new BlitzPlugin.JsCallback() {
        /**
         * 关闭h5 target url 后面到所有activity 包括代表target url 的commonActivity
         *
         * @param param
         */
        public void onCall(String param, long cbData) {
            AppDebug.i(TAG, "onCall CloseActivityPlugin -->param  = " + param + " cdData = " + cbData);

            String target = null;
            try {
                JSONObject jsonObject = new JSONObject(param);
                target = jsonObject.getString("url");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (target == null) return;

            List<WeakReference<Activity>> list = ActivityQueueManager.getInstance().getCacheActivityByTag(BaseActivity.LOW_MEM_STACK_TAG);

            int targetIndex = -1;
            for (int i = 0; list != null && i < list.size(); i++) {
                Activity activity = list.get(i).get();
                AppDebug.i(TAG, "onCall CloseActivityPlugin  ac  = " + activity);
                if (activity == null || !(activity instanceof CommonActivity)) {
                    continue;
                }
                CommonActivity commonActivity = (CommonActivity) activity;
                String page = commonActivity.getPage();
                AppDebug.i(TAG, "onCall CloseActivityPlugin  target  = " + page);
                if (page != null && page.equals(target)) {
                    targetIndex = i;
                    break;
                }
            }


            if (targetIndex >= 0) {
                AppDebug.i(TAG, "onCall CloseActivityPlugin -->param  = " + "find target");
            } else {
                AppDebug.i(TAG, "onCall CloseActivityPlugin -->param  = " + "not find target");
            }

            for (int i = (targetIndex >= 0 ? targetIndex + 1 : 0); list != null && i < list.size(); i++) {
                Activity activity = list.get(i).get();
                if (activity != null) {
                    activity.finish();
                }
            }

            BzResult result = new BzResult();
            result.addData(RESULT, true);
            result.setSuccess();
            String res = result.toJsonString();
            AppDebug.i(TAG, "onCall CloseActivityPlugin --> back  = " + res);
            BlitzPlugin.responseJs(true, res, cbData);

        }
    };

}
