package com.yunos.tvtaobao.h5.commonbundle.h5.plugin;

import com.yunos.tv.blitz.BlitzPlugin;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.activity.TaoBaoBlitzActivity;
import com.yunos.tvtaobao.h5.commonbundle.activity.CommonActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class GuardPlugin {

    private GuardPlugin.GuardJsCallback mJsCallback;

    private WeakReference<TaoBaoBlitzActivity> mRefActivity;

    public GuardPlugin(WeakReference<TaoBaoBlitzActivity> refActivity) {
        mRefActivity = refActivity;
        mJsCallback = new GuardPlugin.GuardJsCallback(new WeakReference<>(this),mRefActivity);
        BlitzPlugin.bindingJs("tvtaobao_guard", mJsCallback);
    }


    private static class GuardJsCallback implements BlitzPlugin.JsCallback {
        private WeakReference<GuardPlugin> refPlugin;
        private WeakReference<TaoBaoBlitzActivity> mRefActivity;
        GuardJsCallback(WeakReference<GuardPlugin> plugin,WeakReference<TaoBaoBlitzActivity> refActivity) {
            refPlugin = plugin;
            mRefActivity = refActivity;
        }


        @Override
        public void onCall(String param, long cbData) {
            AppDebug.d("GuardPlugin", "param:" + param + ", cbData:" + cbData);
            try{
                JSONObject object = new JSONObject();
                object.put("umToken", Config.getUmtoken(mRefActivity.get()));
                object.put("wua", Config.getWua(mRefActivity.get()));
                object.put("isSimulator", Config.isSimulator(mRefActivity.get()));
                object.put("userAgent", Config.getAndroidSystem(mRefActivity.get()));
                String extParams = object.toString();
                BlitzPlugin.responseJs(true, extParams, cbData);
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    }


}

