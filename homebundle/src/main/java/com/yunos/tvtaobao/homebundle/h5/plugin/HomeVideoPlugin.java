package com.yunos.tvtaobao.homebundle.h5.plugin;

import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunos.tv.blitz.BlitzPlugin;
import com.yunos.tv.blitz.data.BzResult;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tvtaobao.biz.activity.TaoBaoBlitzActivity;
import com.yunos.tvtaobao.homebundle.activity.HomeActivity;

import java.lang.ref.WeakReference;

/**
 * Created by huangdaju on 17/7/19.
 */

public class HomeVideoPlugin {

    private static final String TAG = "HomeVideoPlugin";
    private WeakReference<HomeActivity> mTaoBaoBlitzActivityReference;
    private HomeVideoPlugin.BlitzEvent mBlitzCallback;

    private static final int SHOW_VIDEO_JS = 1;
    private static final int HIDE_VIDEO_JS = 2;
    private static final int SET_VIDEO_AREA_SIZE_JS = 3;
    private static final int ON_SCALE_CHANGE_JS = 4;
    private static final int NOTIFY_LIVE_DATA = 5;
    private Handler mHandler;

    public HomeVideoPlugin(WeakReference<HomeActivity> taoBaoBlitzActivityReference) {
        mTaoBaoBlitzActivityReference = taoBaoBlitzActivityReference;
        mHandler = new JsHandler(mTaoBaoBlitzActivityReference);
        onInitPayPlugin();
    }

    private void onInitPayPlugin() {
        mBlitzCallback = new BlitzEvent(new WeakReference<>(this));
        BlitzPlugin.bindingJs("loadVideo", mBlitzCallback);
        BlitzPlugin.bindingJs("hideVideo", mBlitzCallback);
        BlitzPlugin.bindingJs("setFullScreen", mBlitzCallback);
        BlitzPlugin.bindingJs("setVideoArea", mBlitzCallback);
        BlitzPlugin.bindingJs("notifyLiveData", mBlitzCallback);
        BlitzPlugin.bindingJs("getLiveData", mBlitzCallback);
    }


    private boolean onHandleCall(final String param, long cbData) {

        final String param_final = param;
        final long cbData_final = cbData;

        AppDebug.i(TAG, "onHandleCallPay --> param_final  =" + param_final + ";  cbData_final = " + cbData_final);

        HomeActivity homeActivity = null;
        if (mTaoBaoBlitzActivityReference != null && mTaoBaoBlitzActivityReference.get() != null) {
            homeActivity = mTaoBaoBlitzActivityReference.get();
        }
        if (homeActivity.isFinishing()) {
            return false;
        }

        JSONObject object = JSON.parseObject(param);
        final String methodName = object.getString("methodName");

        BzResult result = new BzResult();
        if (methodName != null && methodName.equals("getLiveData"))
            result.addData("liveType", 3);
        result.addData("version", AppInfo.getAppVersionNum());
        result.setSuccess();
        String res = result.toJsonString();
        BlitzPlugin.responseJs(true, res, cbData_final);


        if (param != null && methodName != null) {
            AppDebug.e("----->", "param : " + param + "     methodName : " + methodName);
            Message msg = new Message();
            msg.obj = param;
            if (methodName.equals("loadVideo")) {
                msg.what = SHOW_VIDEO_JS;
            } else if (methodName.equals("hideVideo")) {
                msg.what = HIDE_VIDEO_JS;
            } else if (methodName.equals("setVideoArea")) {
                msg.what = SET_VIDEO_AREA_SIZE_JS;
            } else if (methodName.equals("setFullScreen")) {
                msg.what = ON_SCALE_CHANGE_JS;
            } else if (methodName.equals("notifyLiveData")) {
                msg.what = NOTIFY_LIVE_DATA;
            }
            mHandler.sendMessage(msg);
        }

        return true;
    }


    private static class JsHandler extends Handler {
        private WeakReference<HomeActivity> mActivityWeakReference;

        private JsHandler(WeakReference<HomeActivity> androidVideoForJsReference) {
            mActivityWeakReference = androidVideoForJsReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mActivityWeakReference == null || mActivityWeakReference.get() == null)
                return;
            HomeActivity homeActivity = mActivityWeakReference.get();

            switch (msg.what) {
                case SHOW_VIDEO_JS:
                    homeActivity.showVideoForJs();
                    break;
                case HIDE_VIDEO_JS:
                    homeActivity.dismissForJs();
                    break;
                case SET_VIDEO_AREA_SIZE_JS:
                    JSONObject object3 = JSON.parseObject(msg.obj.toString());
                    int width = object3.getInteger("width");
                    int height = object3.getInteger("height");
                    int marginTop = object3.getInteger("marginTop");
                    int marginLeft = object3.getInteger("marginLeft");
                    homeActivity.changeVideoSizeForJS(width, height, marginTop, marginLeft);
                    break;
                case ON_SCALE_CHANGE_JS:
                    int liveType = 1;
                    if (msg.obj.toString().contains("liveType")) { //防止双11版本没有更新,出现错误
                        JSONObject object4 = JSON.parseObject(msg.obj.toString());
                        liveType = object4.getInteger("liveType");
                    }
                    homeActivity.fullScreenForJs(liveType);
                    break;
                case NOTIFY_LIVE_DATA:
                    JSONObject object5 = JSON.parseObject(msg.obj.toString());
                    int liveTypes = object5.getInteger("liveType");
                    homeActivity.changeLiveType(liveTypes);
                    break;
            }
        }
    }

    private class BlitzEvent implements BlitzPlugin.JsCallback {

        private WeakReference<HomeVideoPlugin> mReference;

        public BlitzEvent(WeakReference<HomeVideoPlugin> reference) {
            mReference = reference;
        }

        @Override
        public void onCall(String param, long cbData) {
            AppDebug.i(TAG, "onCall --> param  =" + param + ";  cbData = " + cbData);
            if (mReference != null && mReference.get() != null) {
                HomeVideoPlugin plugin = mReference.get();
                plugin.onHandleCall(param, cbData);
            }
        }

    }

}
