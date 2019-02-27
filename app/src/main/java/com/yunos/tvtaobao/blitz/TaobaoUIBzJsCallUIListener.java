package com.yunos.tvtaobao.blitz;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.yunos.tv.blitz.data.BzResult;
import com.yunos.tv.blitz.listener.BzJsCallUIListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.activity.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class TaobaoUIBzJsCallUIListener implements BzJsCallUIListener {

    private final String TAG = "TaobaoUIBzJsCallUIListener";

    public TaobaoUIBzJsCallUIListener() {
    }

    @Override
    public String onUILoading(Context context, String param) {
        AppDebug.i(TAG, "onUILoading , param  = " + param);

        WeakReference<Context> mReferenceContext = new WeakReference<Context>(context);
        Context ctx = mReferenceContext.get();

        BzResult result = new BzResult();
        try {
            JSONObject jsObj = new JSONObject(param);
            final boolean isShow = jsObj.optBoolean("show");
            if (ctx != null && ctx instanceof BaseActivity) {
                final BaseActivity taoBaoBlitzActivity = (BaseActivity) ctx;
                result.setSuccess();
                taoBaoBlitzActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        AppDebug.d(TAG, "thread id:" + Thread.currentThread().getId() + ",thread name:"
                                + Thread.currentThread().getName() + ",isShow:" + isShow);

                        taoBaoBlitzActivity.OnWaitProgressDialog(isShow);
                    }
                });
            } else {
                result.setResult(BzResult.FAIL);
            }

        } catch (JSONException e) {
            AppDebug.e(TAG, "TaobaoUI: param parse to JSON error, param=" + param);
            result.setResult(BzResult.FAIL);
        }
        return result.toJsonString();
    }

    @Override
    public String onUIDialog(Context context, String param) {
        AppDebug.i(TAG, "onUIDialog , param  = " + param);

        WeakReference<Context> mReferenceContext = new WeakReference<Context>(context);
        Context ctx = mReferenceContext.get();

        BzResult result = new BzResult();
        try {
            JSONObject jsObj = new JSONObject(param);
            final boolean isFinishActivity = jsObj.optBoolean("isFinishActivity");
            final String message = jsObj.optString("message");
            if (!TextUtils.isEmpty(message)) {
                if (ctx != null && ctx instanceof BaseActivity) {
                    final BaseActivity taoBaoBlitzActivity = (BaseActivity) ctx;
                    result.setSuccess();
                    taoBaoBlitzActivity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            taoBaoBlitzActivity.showErrorDialog(message, isFinishActivity);
                        }
                    });
                }
            }

        } catch (JSONException e) {
            AppDebug.e(TAG, "TaobaoUI: param parse to JSON error, param=" + param);
        }
        return result.toJsonString();
    }

    @Override
    public String onUINetworkDialog(Context context, String param) {

        AppDebug.i(TAG, "onUINetworkDialog , param  = " + param);

        WeakReference<Context> mReferenceContext = new WeakReference<Context>(context);
        Context ctx = mReferenceContext.get();

        BzResult result = new BzResult();
        try {
            JSONObject jsObj = new JSONObject(param);
            final boolean isFinishActivity = jsObj.optBoolean("isFinishActivity");
            if (ctx != null && ctx instanceof BaseActivity) {
                final BaseActivity taoBaoBlitzActivity = (BaseActivity) ctx;
                result.setSuccess();
                taoBaoBlitzActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        taoBaoBlitzActivity.showNetworkErrorDialog(isFinishActivity);
                    }
                });
            }
        } catch (JSONException e) {
            AppDebug.e(TAG, "TaobaoUI: param parse to JSON error, param=" + param);
        }
        return result.toJsonString();
    }

    @Override
    public String onUIStopLoading(Context context, String s) {
        Log.i(TAG, "onUIStopLoading");
        return null;
    }
}
