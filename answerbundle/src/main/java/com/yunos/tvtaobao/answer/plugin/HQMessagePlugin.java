package com.yunos.tvtaobao.answer.plugin;

import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunos.tvtaobao.answer.activity.HQLiveActivity;
import com.yunos.tv.blitz.BlitzPlugin;
import com.yunos.tv.blitz.data.BzResult;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.AppInfo;

import java.lang.ref.WeakReference;

/**
 * <pre>
 *     author : pan
 *     QQ : 1065475118
 *     time   : 2018/01/10
 *     desc   : Plugin通信工具
 *     version: 1.0
 * </pre>
 */

public class HQMessagePlugin {
    private static String TAG = "HQMessagePlugin";
    private static final String RESULT = "result";

    private JsHandler jsHandler;
    private WeakReference<HQLiveActivity> hqLiveActivityWR;
    private InitCallback initCallback;
    private PlayCallback playCallback;

    private final static int TYPE_VIDEO_INIT = 0x0001;
    private final static int TYPE_VIDEO_PLAY = 0x0002;

    public HQMessagePlugin(WeakReference<HQLiveActivity> activityWR) {
        hqLiveActivityWR = activityWR;
        jsHandler = new JsHandler(activityWR);
        onInitPayPlugin();
    }

    private void onInitPayPlugin() {
        initCallback = new InitCallback(new WeakReference<>(this));
        playCallback = new PlayCallback(new WeakReference<>(this));
        BlitzPlugin.bindingJs("tvtaobao-hq-init", initCallback);
        BlitzPlugin.bindingJs("tvtaobao-hq-play", playCallback);
    }

    private void onHandleMessage(int type, String param, long cbData) {
        AppDebug.i(TAG, "onHandleMessage param = " + param + "; cbData = " + cbData);
        final String param_final = param;
        final long cbData_final = cbData;

        AppDebug.i(TAG, "onHandleCallPay --> param_final  =" + param_final + ";  cbData_final = " + cbData_final);

        BzResult result = new BzResult();
        result.addData("version", AppInfo.getAppVersionNum());
        if (hqLiveActivityWR == null || hqLiveActivityWR.get() == null) {
            result.addData(RESULT, "false");
            String res = result.toJsonString();
            BlitzPlugin.responseJs(false, res, cbData_final);
            return;
        } else {
            result.setSuccess();
            String res = result.toJsonString();
            BlitzPlugin.responseJs(true, res, cbData_final);
        }

        switch (type) {
            case TYPE_VIDEO_INIT:
                Message msg1 = new Message();
                msg1.what = TYPE_VIDEO_INIT;
                msg1.obj = param;
                jsHandler.sendMessage(msg1);
                break;
            case TYPE_VIDEO_PLAY:
                Message msg2 = new Message();
                msg2.what = TYPE_VIDEO_PLAY;
                msg2.obj = param;
                jsHandler.sendMessage(msg2);
                break;
        }

    }

    private static class InitCallback implements BlitzPlugin.JsCallback {

        private WeakReference<HQMessagePlugin> mReference;

        private InitCallback(WeakReference<HQMessagePlugin> reference) {
            mReference = reference;
        }

        @Override
        public void onCall(String param, long cbData) {
            AppDebug.i(TAG, "onCall --> param  =" + param + ";  cbData = " + cbData);
            if (mReference != null && mReference.get() != null) {
                HQMessagePlugin plugin = mReference.get();
                plugin.onHandleMessage(TYPE_VIDEO_INIT, param, cbData);
            }
        }
    }

    private static class PlayCallback implements BlitzPlugin.JsCallback {
        private WeakReference<HQMessagePlugin> mReference;

        private PlayCallback(WeakReference<HQMessagePlugin> reference) {
            mReference = reference;
        }

        @Override
        public void onCall(String param, long cbData) {
            AppDebug.i(TAG, "onCall --> param  =" + param + ";  cbData = " + cbData);
            if (mReference != null && mReference.get() != null) {
                HQMessagePlugin plugin = mReference.get();
                plugin.onHandleMessage(TYPE_VIDEO_PLAY, param, cbData);
            }
        }
    }

    private static class JsHandler extends Handler {
        private WeakReference<HQLiveActivity> liveActivityWR;
        public JsHandler(WeakReference<HQLiveActivity> liveActivityWR) {
            this.liveActivityWR = liveActivityWR;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (liveActivityWR == null || liveActivityWR.get() == null)
                return;

            HQLiveActivity hqLiveActivity = liveActivityWR.get();
            JSONObject object = JSON.parseObject(msg.obj.toString());
            switch (msg.what) {
                case TYPE_VIDEO_INIT:
                    int x = object.getInteger("x");
                    int y = object.getInteger("y");
                    int width = object.getInteger("width");
                    int height = object.getInteger("height");
                    hqLiveActivity.initVideoArea(width, height, y, x);
                    break;
                case TYPE_VIDEO_PLAY:
                    String url = object.getString("url");
                    hqLiveActivity.playVideo(url);
                    break;
            }
        }
    }
}
