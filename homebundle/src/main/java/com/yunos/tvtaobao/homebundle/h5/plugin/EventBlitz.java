package com.yunos.tvtaobao.homebundle.h5.plugin;

import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunos.tv.blitz.BlitzPlugin;
import com.yunos.tv.blitz.data.BzResult;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.homebundle.activity.HomeActivity;

import java.lang.ref.WeakReference;

/**
 * Created by pan on 16/10/22.
 */

public class EventBlitz {
    private static final String TAG = "EventBlitz";
    private WeakReference<HomeActivity> mEventReference;
    private EventBlitz.BlitzEvent mBlitzCallback;

    private final String RESULT = "result";
    private final int HAVE_SUPERNATANT = 0;

    public EventBlitz(WeakReference<HomeActivity> videoForJsReference) {
        AppDebug.i(TAG, "init --> param_final");
        mEventReference = videoForJsReference;
        onInitPayPlugin();
    }

    private void onInitPayPlugin() {
        mBlitzCallback = new EventBlitz.BlitzEvent(new WeakReference<EventBlitz>(this));
        BlitzPlugin.bindingJs("haveSupernatant", mBlitzCallback);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HAVE_SUPERNATANT:
                    HomeActivity activity = mEventReference.get();
                    if (activity != null) {
                        JSONObject object = JSON.parseObject(msg.obj.toString());
                        boolean isIntercept = object.getBoolean("isIntercept");
                        activity.interceptBack(isIntercept);
                    }
                    break;
            }
        }
    };

    private boolean onHandleCallPay(final String param, long cbData) {

        final String param_final = param;
        final long cbData_final = cbData;

        AppDebug.i(TAG, "onHandleCallPay --> param_final  =" + param_final + ";  cbData_final = " + cbData_final);

        HomeActivity homeActivity = null;
        if (mEventReference != null && mEventReference.get() != null) {
            homeActivity = mEventReference.get();
        }

        if (homeActivity == null) {
            BzResult result = new BzResult();
            result.addData(RESULT, "false");
            result.setSuccess();
            String res = result.toJsonString();
            BlitzPlugin.responseJs(false, res, cbData_final);
            return true;
        } else {
            BzResult result = new BzResult();
            result.setSuccess();
            String res = result.toJsonString();
            BlitzPlugin.responseJs(true, res, cbData_final);
        }

        JSONObject object = JSON.parseObject(param);
        final String methodName = object.getString("methodName");

        if (param != null && methodName != null) {
            AppDebug.e(TAG, "param : " + param + "     methodName : " + methodName);
            Message msg = new Message();
            msg.obj = param;

            if (methodName.equals("haveSupernatant")) {
                msg.what = HAVE_SUPERNATANT;
            }
            mHandler.sendMessage(msg);
        }

        return true;
    }


    private class BlitzEvent implements BlitzPlugin.JsCallback {

        private WeakReference<EventBlitz> mReference;

        public BlitzEvent(WeakReference<EventBlitz> reference) {
            mReference = reference;
        }

        @Override
        public void onCall(String param, long cbData) {
            AppDebug.i(TAG, "onCall --> param  =" + param + ";  cbData = " + cbData);
            if (mReference != null && mReference.get() != null) {
                EventBlitz plugin = mReference.get();
                plugin.onHandleCallPay(param, cbData);
            }
        }

    }
}
