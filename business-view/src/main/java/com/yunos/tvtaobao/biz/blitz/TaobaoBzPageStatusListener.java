package com.yunos.tvtaobao.biz.blitz;


import android.content.Context;
import android.text.TextUtils;

import com.yunos.tv.blitz.listener.BzPageStatusListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Map;

public class TaobaoBzPageStatusListener {

//    private static final String TAG = "TaobaoBzPageStatusListener";
//    private static final String ERROR_CODE = "errorcode";
//
//    private LOAD_MODE mLoadMode = LOAD_MODE.URL_MODE;
//
//    public TaobaoBzPageStatusListener() {
//    }
//
//    @Override
//    public void onPageLoadError(Context context, String url, String param) {
//
//        AppDebug.i(TAG, "onPageLoadError -->  mLoadMode = " + mLoadMode + ";  url = " + url + "; param = " + param);
//
//        String errorcode = "";
//        if (!TextUtils.isEmpty(param)) {
//            try {
//                JSONObject error_param = new JSONObject(param);
//                if (error_param != null) {
//                    errorcode = error_param.optString(ERROR_CODE);
//                }
//            } catch (JSONException e) {
//            }
//        }
//
//        WeakReference<Context> mReferenceContext = new WeakReference<Context>(context);
//
//        Context ctx = mReferenceContext.get();
//        if (ctx != null && ctx instanceof BaseActivity) {
//
//            final BaseActivity taoBaoBlitzActivity = (BaseActivity) ctx;
//            if (!TextUtils.isEmpty(url)) {
//                Map<String, String> params = Utils.getProperties();
//                params.put("url", url);
//                if (!TextUtils.isEmpty(errorcode)) {
//                    params.put(ERROR_CODE, errorcode);
//                }
//                Utils.utCustomHit(taoBaoBlitzActivity.getFullPageName(), "load_h5_error", params);
//            }
//
//            taoBaoBlitzActivity.runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    taoBaoBlitzActivity.OnWaitProgressDialog(false);
//                    taoBaoBlitzActivity.showErrorDialog("加载页面失败,请稍后重试", true);
//                }
//            });
//
//        }
//    }
//
//    @Override
//    public void onPageLoadFinished(Context context, final String url) {
//        AppDebug.i(TAG, "onPageLoadFinished -->  mLoadMode = " + mLoadMode + ";  url = " + url + "; context = "
//                + context);
//        WeakReference<Context> mReferenceContext = new WeakReference<Context>(context);
//        Context ctx = mReferenceContext.get();
//        if (ctx != null && ctx instanceof BaseActivity) {
//
//            final BaseActivity taoBaoBlitzActivity = (BaseActivity) ctx;
//
//            Map<String, String> p = Utils.getProperties();
//            p.put("url", url);
//            Utils.utCustomHit(taoBaoBlitzActivity.getFullPageName(), "load_h5_finish", p);
//
//            taoBaoBlitzActivity.runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    taoBaoBlitzActivity.OnWaitProgressDialog(false);
//                    if (!TextUtils.isEmpty(url)) {
//                        taoBaoBlitzActivity.onWebviewPageDone(url);
//                    }
//                }
//            });
//        }
//    }
//
//    @Override
//    public void onPageLoadStart(Context context, String url) {
//        AppDebug.i(TAG, "onPageLoadStart -->  mLoadMode = " + mLoadMode + ";  url = " + url);
//
//        WeakReference<Context> mReferenceContext = new WeakReference<Context>(context);
//        Context ctx = mReferenceContext.get();
//
//        if (!TextUtils.isEmpty(url)) {
//            Map<String, String> p = Utils.getProperties();
//            p.put("url", url);
//
//            if (ctx != null && ctx instanceof BaseActivity) {
//                final BaseActivity taoBaoBlitzActivity = (BaseActivity) ctx;
//                Utils.utCustomHit(taoBaoBlitzActivity.getFullPageName(), "load_h5_start", p);
//            }
//        }
//
//    }

    public static enum LOAD_MODE {
        URL_MODE(0), DATA_MODE(1);

        private int value;

        LOAD_MODE(int num) {
            value = num;
        }

        public int getValue() {
            return value;
        }
    }

}
