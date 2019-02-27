package com.yunos.tvtaobao.blitz;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.yunos.tv.blitz.BlitzContextWrapper;
import com.yunos.tv.blitz.account.BzDebugLog;
import com.yunos.tv.blitz.data.BzResult;
import com.yunos.tv.blitz.listener.BzMiscListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.activity.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class TaobaoBzMiscListener implements BzMiscListener {

    private static final String TAG = "TaobaoBzMiscListener";
//    private BusinessRequest mBusinessRequest;

    public TaobaoBzMiscListener() {
//        mBusinessRequest = BusinessRequest.getBusinessRequest();
    }


    @Override
    public void onStartActivity(Context context, String param, int callback) {
        AppDebug.i(TAG, "onStartActivity -->  param = " + param);

        WeakReference<Context> mReferenceContext = new WeakReference<Context>(context);
        int retcode = 0;
        try {
            JSONObject start_param = new JSONObject(param);
            final String blitzOpenType = start_param.optString("blitzOpenType");
            final String uri = start_param.optString("uri");
            final JSONObject data = start_param.optJSONObject("data");

            String action_temp = start_param.optString("action");

            AppDebug.i(TAG, "onStartActivity -->  blitzOpenType  = " + blitzOpenType + "; action_temp = " + action_temp
                    + "; data = " + data + ";  uri = " + uri);

            if (TextUtils.isEmpty(action_temp)) {
                action_temp = Intent.ACTION_VIEW;
            }
            final String action = action_temp;

            AppDebug.i(TAG, "onStartActivity -->  action = " + action + "; uri = " + uri);

            Context ctx = mReferenceContext.get();
            if (ctx != null && ctx instanceof BaseActivity) {
                final BaseActivity taoBaoBlitzActivity = (BaseActivity) ctx;
                if (taoBaoBlitzActivity != null) {
                    taoBaoBlitzActivity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Intent intent = new Intent(action);
                            intent.setAction(action);
                            if (!TextUtils.isEmpty(uri)) {
                                intent.setData(Uri.parse(uri));
                            }
                            taoBaoBlitzActivity.startActivity(intent);
                        }
                    });
                }
            }
        } catch (JSONException e) {
            AppDebug.i(TAG, "onStartActivity -->  e = " + e.toString());
            retcode = -1;
        }


        BzResult result =new BzResult();
        result.addData("retcode",retcode);
        if(retcode == -1){
            BzDebugLog.d(TAG,"startactivity fail");
            Context ctx = mReferenceContext.get();
            if (ctx != null && ctx instanceof BaseActivity) {
                final BaseActivity taoBaoBlitzActivity = (BaseActivity) ctx;
                if (taoBaoBlitzActivity != null) {
                    taoBaoBlitzActivity.getBlitzContext().replyCallBack(callback, false, result.toJsonString());
                }
            }
        }else{
            BzDebugLog.d(TAG,"startactivity successful");
            Context ctx = mReferenceContext.get();
            if (ctx != null && ctx instanceof BaseActivity) {
                final BaseActivity taoBaoBlitzActivity = (BaseActivity) ctx;
                if (taoBaoBlitzActivity != null) {
                    taoBaoBlitzActivity.getBlitzContext().replyCallBack(callback,true,result.toJsonString());
                }
            }
            result.setSuccess();
        }

    }

    @Override
    public void onGetMtopResponse(String s, int i, String s1, String s2) {

    }

    @Override
    public String onGetMtopRequest(Context context, String param, int callback) {

        WeakReference<Context> mReferenceContext = new WeakReference<Context>(context);

        final String final_param = param;
        final int final_callback = callback;
        AppDebug.i(TAG, "onGetMtopRequest -->  final_callback = " + final_callback + ";  final_param = " + final_param);

        Context ctx = mReferenceContext.get();
        if (ctx != null && ctx instanceof BaseActivity) {
            final BaseActivity taoBaoBlitzActivity = (BaseActivity) ctx;
            if (taoBaoBlitzActivity != null) {
                taoBaoBlitzActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
//                        BlitzMtopRequest mtopRequest = new BlitzMtopRequest();
//                        mtopRequest.resolveBlitzRequest(final_param);
//                        if (mBusinessRequest != null) {
//                            mBusinessRequest.baseRequest(mtopRequest, new BlitzMtopListener(
//                                    new WeakReference<BaseActivity>(taoBaoBlitzActivity), final_callback), mtopRequest
//                                    .getBlitzMtopNeedLogin(), mtopRequest.getBlitzMtopPost());
//                        }
                    }
                });
            }
        }
        return null;
    }

    public static class BlitzMtopListener implements RequestListener<JSONObject> {

        private int mAddrCallback;
        private WeakReference<BaseActivity> mTaoBaoBlitzActivityRef = null;

        public BlitzMtopListener(WeakReference<BaseActivity> baseActivityRef, int addr_callback) {

            mAddrCallback = addr_callback;
            mTaoBaoBlitzActivityRef = baseActivityRef;
        }

        public boolean onError(int resultCode, String msg) {

            AppDebug.i(TAG, "BlitzMtopListener --> onError -->  resultCode = " + resultCode + ";  msg = " + msg);
            BzResult result = new BzResult();

//            result.addData(BlitzRequestConfig.MTOP_RETURN_CODE, resultCode);
//            result.addData(BlitzRequestConfig.MTOP_RETURN_MSG, msg);

            replyCallBack(result, false);

            return false;
        }

        public void onSuccess(JSONObject data) {
            AppDebug.i(TAG, "BlitzMtopListener --> onSuccess --> data = " + data);
            BzResult result = new BzResult();

            result.setSuccess();
//            result.addData(BlitzRequestConfig.MTOP_RETURN_DATA, data);

            replyCallBack(result, true);
        }

        boolean replyCallBack(BzResult bzResult, boolean success) {
            boolean result_handle = false;
            if (mTaoBaoBlitzActivityRef != null && mTaoBaoBlitzActivityRef.get() != null) {
                BaseActivity taoBaoBlitzActivity = mTaoBaoBlitzActivityRef.get();
                if (taoBaoBlitzActivity != null) {
                    BlitzContextWrapper blitzContextWrapper = taoBaoBlitzActivity.getBlitzContext();
                    if (blitzContextWrapper != null) {
                        blitzContextWrapper.replyCallBack(mAddrCallback, success, bzResult.toJsonString());
                        result_handle = true;
                    }
                }
            }
            return result_handle;
        }

        @Override
        public void onRequestDone(JSONObject data, int resultCode, String msg) {
            if (resultCode == 200) {
                onSuccess(data);
            } else {
                onError(resultCode, msg);
            }
        }
    }

    @Override
    public void onStartActivityForResult(Context arg0, String arg1) {
    }
}
