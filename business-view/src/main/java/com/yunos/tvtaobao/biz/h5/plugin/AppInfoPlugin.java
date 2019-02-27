package com.yunos.tvtaobao.biz.h5.plugin;

import com.yunos.tv.blitz.BlitzPlugin;
import com.yunos.tv.blitz.data.BzResult;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tvtaobao.biz.activity.TaoBaoBlitzActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by vincent on 2/6/17.
 */

public class AppInfoPlugin {
    private static final String TAG = AppInfoPlugin.class.getSimpleName();
    private static final String jsFuncName = "getExAppInfo";
    private static final String DATA = "data";
    private static final String STBID = "stbid";
    private static final String CHANNEL = "channel";
    private static final String PACKAGE_NAME = "packageName";
    private static final String VERSION_NAME = "version_name";

    private WeakReference<TaoBaoBlitzActivity> mTaoBaoBlitzActivityWeakReference;
    private AppInfoJsCallback mAppInfoJsCallback;

    public AppInfoPlugin(WeakReference<TaoBaoBlitzActivity> taoBaoBlitzActivityWeakReference){
        mTaoBaoBlitzActivityWeakReference = taoBaoBlitzActivityWeakReference;
        onInitPlugin();
    }

    private void onInitPlugin() {
        mAppInfoJsCallback = new AppInfoJsCallback(new WeakReference<>(this));
        BlitzPlugin.bindingJs(jsFuncName, mAppInfoJsCallback);
    }

    private void onHandleCall(String param, final long cbData) {
        //TODO: 目前提供stbid的信息，以后可以提供更多
        String stbid = DeviceUtil.getStbID();
        String channelId = Config.getChannel();
        String packageName = AppInfo.getPackageName();
        String versionName = AppInfo.getAppVersionName();
        JSONObject data = new JSONObject();
        try {
            data.put(STBID, stbid);
            data.put(CHANNEL, channelId);
            data.put(PACKAGE_NAME, packageName);
            data.put(VERSION_NAME, versionName);
        } catch (JSONException e) {
            AppDebug.e(TAG, e.getMessage());
        }
        successResponseJs(data, cbData);
    }

    private static void successResponseJs(JSONObject data, final long cbData){
        BzResult result = new BzResult(BzResult.SUCCESS);
        if(data != null){
            result.addData(DATA, data);
        }
        String res = result.toJsonString();
        BlitzPlugin.responseJs(true, res, cbData);
    }

    private static class AppInfoJsCallback implements BlitzPlugin.JsCallback {

        private WeakReference<AppInfoPlugin> mReference;

        private AppInfoJsCallback(WeakReference<AppInfoPlugin> reference) {
            mReference = reference;
        }

        @Override
        public void onCall(String param, long cbData) {
            AppDebug.v(TAG, "onCall --> param  =" + param + ";  cbData = " + cbData);
            if (mReference != null && mReference.get() != null) {
                AppInfoPlugin plugin = mReference.get();
                plugin.onHandleCall(param, cbData);
            }
        }
    }
}
