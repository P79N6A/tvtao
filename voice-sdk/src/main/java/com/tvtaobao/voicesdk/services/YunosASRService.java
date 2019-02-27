package com.tvtaobao.voicesdk.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.tvtaobao.voicesdk.ASRInput;
import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.bo.CommandReturn;
import com.tvtaobao.voicesdk.interfaces.VoiceListener;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.tv.alitvasrsdk.ASRCommandReturn;
import com.yunos.tv.alitvasrsdk.AliTVASRManager;
import com.yunos.tv.alitvasrsdk.AppContextData;
import com.yunos.tv.alitvasrsdk.OnASRCommandListener;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/06/20
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class YunosASRService extends Service {

    private static final String TAG = "YunosASRService";

    private AliTVASRManager aliTVASRManager;
    private ASRInput asrInput;
    private Listener listener;
    private boolean isAction = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        try{
            asrInput = ASRInput.getInstance();
            asrInput.setContext(this);
            listener = new Listener();
            ASRNotify.getInstance().setFeedBack(listener);
            initAliTvASR(getBaseContext(), false, 0, 0);
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        asrInput.destroy();
        aliTVASRManager.release();
        super.onDestroy();
    }


    /**
     * @param context
     * @param showUI     //初始化  shouUI:是否显示默认识别 UI
     * @param serverMode 设置云端 ASR 模型 0
     * @param ResultMode 设置返回结果类型:0:返回ASR和NLP ,1:仅返回ASR
     */
    public void initAliTvASR(Context context, boolean showUI, int serverMode, int ResultMode) {

        aliTVASRManager = new AliTVASRManager();

        aliTVASRManager.init(context, showUI);//初始化  shouUI:是否显示默认识别 UI

        aliTVASRManager.setOnASRCommandListener(onASRCommandListener).setASRListenerType(OnASRCommandListener.ASRListenerType.DEFAULT_LISTENER);

        aliTVASRManager.setASRServerMode(serverMode);//设置云端 ASR 模型 0

        aliTVASRManager.setASRResultMode(ResultMode);//设置返回结果类型:0:返回ASR和NLP ,1:仅返回ASR

        aliTVASRManager.showASRUI(true);

    }

    /**
     * 注意：
     * 此处返回的数据都是直接在子线程，操作处理请返回到主线程
     */
    private OnASRCommandListener onASRCommandListener = new OnASRCommandListener() {
        @Override
        public void onASRStatusUpdated(ASRStatus asrStatus, Bundle bundle) {
//            LogPrint.e(TAG, TAG + ".onASRStatusUpdated status : " + asrStatus);
        }

        @Override
        public void onASRServiceStatusUpdated(ASRServiceStatus asrServiceStatus) {

            LogPrint.e(TAG, TAG + ".onASRServiceStatusUpdated status : " + asrServiceStatus);
            String msg = null;
            if (asrServiceStatus == ASRServiceStatus.ASR_SERVICE_STATUS_CONNECTED) {
                msg = "语音服务注册成功...(" + Thread.currentThread().getId() + ")";
            } else {
                msg = "语音服务注册失败...(" + Thread.currentThread().getId() + ")";
            }
            LogPrint.e(TAG, "onASRServiceStatusUpdated : " + msg);
        }

        @Override
        public ASRCommandReturn onASRResult(String s, boolean b) {
            return null;
        }

        @Override
        public ASRCommandReturn onNLUResult(String s, String s1, String s2, Bundle bundle) {
            LogPrint.e(TAG, "onNLUResult s: " + s + " ,s1 : " + s1 + " ,s2 : " + s2 + " ,bundle : " + bundle);
            String mAsr = bundle.getString("asr");
//            mAsr = bundle.getString("asr");

            ASRCommandReturn result = new ASRCommandReturn();

            isAction = false;

            synchronized (onASRCommandListener) {
                try {
                    asrInput.handleInput(mAsr, null, listener);
                    LogPrint.e(TAG, "onNLUResult isAction : " + isAction + "  ,time : " + System.currentTimeMillis());

                    onASRCommandListener.wait();

                    LogPrint.e(TAG, "onNLUResult isAction : " + isAction + "  ,time : " + System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            result.mIsHandled = isAction;
            return result;
        }

        @Override
        public void getAppContextData(AppContextData appContextData) {

        }

        @Override
        public Bundle getSceneInfo(Bundle bundle) {
            return null;
        }

        @Override
        public Bundle asrToClient(Bundle bundle) {
            return null;
        }
    };

    /**
     * 数据处理好之后，通过VoiceListener返回。
     * 然后通过IRemoteVoice，将我们转换好的数据格式，返回给暴风。
     */
    class Listener implements VoiceListener {

        @Override
        public void callback(CommandReturn command) {
            LogPrint.i(TAG, "VoiceListener callback action : " + command.mAction);
            isAction = command.mIsHandled;
            synchronized (onASRCommandListener) {
                try {
                    onASRCommandListener.notify();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            switch (command.mAction) {
                case CommandReturn.TYPE_TTS_PLAY:
                    LogPrint.i(TAG, "VoiceListener callback Message : " + command.mMessage);
                    if (!TextUtils.isEmpty(command.mMessage)) {
                        playTTS(command.mMessage);
                    }
                    break;
            }
        }

        @Override
        public void searchResult(String data) {
            LogPrint.i(TAG, "VoiceListener searchResult");
            isAction = true;
            synchronized (onASRCommandListener) {
                try {
                    onASRCommandListener.notify();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean playTTS(String s) {
        boolean b = false;
        try {
            b = aliTVASRManager.playTTS(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogPrint.i(TAG, "playTTS b : " + b);
        return b;
    }


    private void stopTTS() {
        try {
            aliTVASRManager.stopTTS();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
