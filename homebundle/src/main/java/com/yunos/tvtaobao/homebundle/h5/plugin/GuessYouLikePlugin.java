package com.yunos.tvtaobao.homebundle.h5.plugin;

import android.content.Intent;

import com.yunos.tv.blitz.BlitzPlugin;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.activity.TaoBaoBlitzActivity;
import com.yunos.tvtaobao.homebundle.activity.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by LJY on 18/7/16.
 */

public class GuessYouLikePlugin {

    private GuessYouLikePlugin.GuessYouLikeJsCallback mJsCallback;

    private WeakReference<HomeActivity> mRefActivity;

    private boolean toGuessYouLike = false;

    public GuessYouLikePlugin(WeakReference<HomeActivity> refActivity) {
        mRefActivity = refActivity;
        mJsCallback = new GuessYouLikePlugin.GuessYouLikeJsCallback(new WeakReference<>(this));
        BlitzPlugin.bindingJs("tvtaobao_home_to_like", mJsCallback);
    }


    private static class GuessYouLikeJsCallback implements BlitzPlugin.JsCallback {
        private WeakReference<GuessYouLikePlugin> refPlugin;

        GuessYouLikeJsCallback(WeakReference<GuessYouLikePlugin> plugin) {
            refPlugin = plugin;
        }


        @Override
        public void onCall(String param, long cbData) {
            AppDebug.d("GuessYouLikePlugin", "param:" + param + ", cbData:" + cbData);
//            if(!refPlugin.get().toGuessYouLike){
            String bg = "";
            try {
                JSONObject data = new JSONObject(param);
                bg = data.getString("bg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            refPlugin.get().mRefActivity.get().homeToGuessYouLike(bg);
//                refPlugin.get().toGuessYouLike=true;
//            }
        }
    }


}
