package com.yunos.tvtaobao.biz.h5.plugin;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;

import com.ut.mini.UTAnalytics;
import com.yunos.tv.blitz.BlitzPlugin;
import com.yunos.tv.blitz.data.BzResult;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.util.ActivityPathRecorder;
import com.yunos.tv.core.util.DeviceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TvTaoBaoBlitzPlugin {

    private static String TAG = "CommonPlugin";
    private static String RESULT = "result";
    private static String ERROR = "error";
    private static String KEY = "key";
    private static String VALUE = "value";

    private Activity currActivity;
    private static TvTaoBaoBlitzPlugin instance;
    private TvTaoBaoBlitzPlugin(){

        AppDebug.i(TAG, "mStbIDGetCallback");
        BlitzPlugin.bindingJs("tvtaobao_stbId_get", mStbIDGetCallback);

        AppDebug.i(TAG, "onInitupdateNextPageProperties");
        BlitzPlugin.bindingJs("tvtaobao_spm_update_next_page_properties", updateNextPageProperties);

        AppDebug.i(TAG, "mGetAppkeyCallback");
        BlitzPlugin.bindingJs("tvtaobao_appkey_get", mGetAppkeyCallback);

        AppDebug.i(TAG, "mGetActivityPathCallback");
        BlitzPlugin.bindingJs("tvtaobao_activity_path_get", mGetActivityPathCallback);

    }
    public static TvTaoBaoBlitzPlugin getInstance(){
        if (instance==null){
            synchronized (TvTaoBaoBlitzPlugin.class){
                if (instance==null){
                    instance = new TvTaoBaoBlitzPlugin();
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

    private static Handler handler = new Handler();




    private static BlitzPlugin.JsCallback mStbIDGetCallback = new BlitzPlugin.JsCallback() {
        @Override
        public void onCall(String param, long cdData) {
            String stbId = DeviceUtil.initMacAddress(getInstance().currActivity);
            AppDebug.i(TAG, "mStbIDGetCallback  stb == "+stbId);
            if (stbId != null) {
                BzResult result = new BzResult();
                result.addData(RESULT, "true");
                result.addData("stbID", stbId);
                result.setSuccess();
                String res = result.toJsonString();
                BlitzPlugin.responseJs(true, res, cdData);
            } else {
                responseFalse(cdData);
            }
        }
    };


    private static BlitzPlugin.JsCallback updateNextPageProperties = new BlitzPlugin.JsCallback() {

        @Override
        public void onCall(String param, long cdData) {
            String spm_url = null;
            try {
                JSONObject jsonObject = new JSONObject(param);
                spm_url = jsonObject.getString("spm_url");
                if (!TextUtils.isEmpty(spm_url)) {
                    Map<String, String> nextparam = new HashMap<>();
                    AppDebug.i(TAG, spm_url);
                    nextparam.put("spm-url", spm_url);
                    UTAnalytics.getInstance().getDefaultTracker().updateNextPageProperties(nextparam);
                    BzResult result = new BzResult();
                    result.addData(RESULT, "true");
                    result.setSuccess();
                    String res = result.toJsonString();
                    BlitzPlugin.responseJs(true, res, cdData);
                } else {
                    responseFalse(cdData);
                }
            } catch (Exception e) {
                responseFalse(cdData);
                e.printStackTrace();
            }
        }
    };


    private static BlitzPlugin.JsCallback mGetAppkeyCallback = new BlitzPlugin.JsCallback() {
        @Override
        public void onCall(String param, long cdData) {
            String appKey = Config.getChannel();
            AppDebug.i(TAG, "mGetAppkeyCallback  appKey == "+appKey);
            if (appKey != null) {
                BzResult result = new BzResult();
                result.addData(RESULT, "true");
                result.addData("appKey", appKey);
                result.setSuccess();
                String res = result.toJsonString();
                BlitzPlugin.responseJs(true, res, cdData);
            } else {
                responseFalse(cdData);
            }
        }
    };


    private  BlitzPlugin.JsCallback mGetActivityPathCallback = new BlitzPlugin.JsCallback() {
        @Override
        public void onCall(String param, long cdData) {
            if(getInstance().currActivity instanceof ActivityPathRecorder.PathNode){
                List<String> pathList = ActivityPathRecorder.getInstance().
                        getCurrentPath((ActivityPathRecorder.PathNode) getInstance().currActivity);
                if (pathList != null) {
                    BzResult result = new BzResult();
                    result.addData(RESULT, "true");
                    JSONArray array = new JSONArray();
                    for (int i = 0 ; i < pathList.size() ; i++) {
                        array.put(pathList.get(i));
                    }
                    result.addData("activityPath", array != null? array.toString():"");
                    AppDebug.i(TAG,"mGetActivityPathCallback activityPath == "+ array != null? array.toString():"");
                    result.setSuccess();
                    String res = result.toJsonString();
                    BlitzPlugin.responseJs(true, res, cdData);
                } else {
                    responseFalse(cdData);
                }
            }
        }
    };
}
