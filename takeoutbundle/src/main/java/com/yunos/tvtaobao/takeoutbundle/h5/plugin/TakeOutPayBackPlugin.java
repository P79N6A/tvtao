package com.yunos.tvtaobao.takeoutbundle.h5.plugin;

import android.os.Bundle;

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
 * Created by haoxiang on 2018/1/3
 */

public abstract class TakeOutPayBackPlugin {
    private static final String TAG = TakeOutPayBackPlugin.class.getSimpleName();
    private static final String jsFuncName = "takeOut_pay_callBack";
    private AppInfoJsCallback mAppInfoJsCallback;


    public void onCreate(Bundle bundle){
        mAppInfoJsCallback = new AppInfoJsCallback(new WeakReference<>(this));
        BlitzPlugin.bindingJs(jsFuncName, mAppInfoJsCallback);
    }

    public void onDestroy(){
        BlitzPlugin.removeBinding(jsFuncName);
    }

    public abstract void onCall(String param, long cbData);

    private static class AppInfoJsCallback implements BlitzPlugin.JsCallback {
        private WeakReference<TakeOutPayBackPlugin> mReference;
        private AppInfoJsCallback(WeakReference<TakeOutPayBackPlugin> reference) {
            mReference = reference;
        }
        @Override
        public void onCall(String param, long cbData) {
            AppDebug.v(TAG, "onCall --> param  =" + param + ";  cbData = " + cbData);
            if (mReference != null && mReference.get() != null) {
                TakeOutPayBackPlugin plugin = mReference.get();
                plugin.onCall(param, cbData);
            }
        }
    }
}
