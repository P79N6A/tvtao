package com.yunos.tvtaobao.h5.commonbundle.h5.plugin;

import android.text.TextUtils;

import com.yunos.tv.blitz.BlitzPlugin;
import com.yunos.tv.blitz.data.BzResult;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.activity.TaoBaoBlitzActivity;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.ztc.KMRefluxRequest;
import com.yunos.tvtaobao.biz.request.ztc.RedirectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by zhujun on 19/04/2017.
 * blitz plugin for alimama advertisement
 */

public class TvTaoBaoZtcPlugin {

    private ZtcJsCallback mJsCallback;

    private WeakReference<TaoBaoBlitzActivity> mRefActivity;

    private RedirectRequest redirectRequest;

    public TvTaoBaoZtcPlugin(WeakReference<TaoBaoBlitzActivity> refActivity) {
        mRefActivity = refActivity;
        mJsCallback = new ZtcJsCallback(new WeakReference<>(this));
        BlitzPlugin.bindingJs("ztc", mJsCallback);
    }


    private static class ZtcJsCallback implements BlitzPlugin.JsCallback, RedirectRequest.RedirectRequestListener {
        private WeakReference<TvTaoBaoZtcPlugin> refPlugin;

        ZtcJsCallback(WeakReference<TvTaoBaoZtcPlugin> plugin) {
            refPlugin = plugin;
        }

        private long callbackData;

        private String title;

        private String picUrl;

        @Override
        public void onCall(String param, long cbData) {
            callbackData = cbData;
            AppDebug.d("test", "param:" + param + ", cbData:" + cbData);
            AppDebug.d("test", "refPlugin:" + refPlugin + ", refPlugin.get:" + refPlugin.get());
            if (refPlugin == null || refPlugin.get() == null) {
                return;
            }
            AppDebug.d("test", "mRefActivity:" + refPlugin.get().mRefActivity + ", mRefActivity.get:" + refPlugin.get().mRefActivity.get());
            if (refPlugin.get().mRefActivity == null || refPlugin.get().mRefActivity.get() == null) {
                return;
            }
            try {
                final TaoBaoBlitzActivity activity = refPlugin.get().mRefActivity.get();

                JSONObject params = new JSONObject(param);
                final String eurl = params.optString("url", "");
                final String userAgent = params.optString("userAgent");
                title = params.optString("title", "");
                picUrl = params.optString("picUrl", "");
                final boolean isLoadUrl = params.optBoolean("isLoadUrl", true);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if (!TextUtils.isEmpty(eurl)) {
                                if (refPlugin.get().redirectRequest != null) {
                                    refPlugin.get().redirectRequest.destroy();//todo: 目前不对webview做destroy操作，避免过早销毁，不完成直通车页面加载
                                }
                                refPlugin.get().redirectRequest = new RedirectRequest(activity, userAgent, isLoadUrl, TvTaoBaoZtcPlugin.ZtcJsCallback.this);
                                refPlugin.get().redirectRequest.requestParams(eurl);
                            }
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onItemIdRetrieveResult(boolean success, String id) {
            AppDebug.d("test", "onItemidRetrieveResult, success:" + success + ",id:" + id);
            if (refPlugin == null || refPlugin.get() == null) {
                return;
            }
            if (refPlugin.get().mRefActivity == null || refPlugin.get().mRefActivity.get() == null) {
                return;
            }
//                String url = "tvtaobao://home?app=taobaosdk&module=detail&itemId=" + id;
//                AppDebug.i("test", "url = " + url);
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse(url));
//                intent.putExtra(CoreIntentKey.URI_FROM_APP_BUNDLE, AppHolder.getAppName());
//                activity.startActivity(intent);
            BzResult result = new BzResult();
            boolean checkResult = success && !TextUtils.isEmpty(id);
            result.addData("success", checkResult);
            result.setResult(checkResult ? BzResult.SUCCESS : BzResult.FAIL);
            if (!TextUtils.isEmpty(id))
                result.addData("id", id);
            if (!checkResult)
                result.addData("msg", "ID_RETRIEVE_FAILURE");
            BlitzPlugin.responseJs(checkResult, result.toJsonString(), callbackData);
            KMRefluxRequest request = new KMRefluxRequest(id, title, picUrl);
            BusinessRequest.getBusinessRequest().baseRequest(request, null, false);
        }
    }

    public void destroy() {
        if (redirectRequest != null) {
            redirectRequest.destroy();
            redirectRequest = null;
        }
    }
}
