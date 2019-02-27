package com.tvtaobao.voicesdk.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.bftv.fui.tell.TTS;
import com.bftv.fui.tell.TellManager;
import com.bftv.fui.thirdparty.Feedback;
import com.bftv.fui.thirdparty.IRemoteFeedback;
import com.bftv.fui.thirdparty.IUserStatusNotice;
import com.bftv.fui.thirdparty.InterceptionData;
import com.bftv.fui.thirdparty.RecyclingData;
import com.tvtaobao.voicesdk.ASRInput;
import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.bo.CommandReturn;
import com.tvtaobao.voicesdk.interfaces.VoiceListener;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.config.AppInfo;

/**
 * <pre>
 *     author : pan
 *     QQ : 1065475118
 *     time   : 2018/03/21
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class BftvService extends Service {

    private String TAG = "BftvService";
    private ASRNotify asrNotify;
    private ASRInput asrInput;
    private Listener mListener;

    @Override
    public void onCreate() {
        super.onCreate();
        asrNotify = ASRNotify.getInstance();
        asrInput = ASRInput.getInstance();
        asrInput.setContext(this);
        mListener = new Listener();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    private IUserStatusNotice.Stub stub = new IUserStatusNotice.Stub() {
        @Override
        public void onInterception(InterceptionData interceptionData) throws RemoteException {
            LogPrint.e(TAG, "onInterception:" + interceptionData.toString());
            asrInput.handleInput(interceptionData.needValue, null, null);
        }

        @Override
        public void onRecyclingNotice(RecyclingData recyclingData) throws RemoteException {
            LogPrint.e(TAG, "onRecyclingNotice 回收数据通知" + recyclingData.toString());
        }

        @Override
        public void onAsr(String s, int i, int i1, IRemoteFeedback iRemoteFeedback) throws RemoteException {
            LogPrint.e(TAG, "onAsr s : " + s + " ,i : " + i + " ,i1 : " + i1);
            asrNotify.setFeedBack(mListener);
            mListener.destroy();
            mListener.setIRemoteVoice(iRemoteFeedback, s);
            asrInput.handleInput(s, null, mListener);
        }

        @Override
        public void onShow(boolean b) throws RemoteException {
            LogPrint.e(TAG, "onShow b : " + b);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BftvService.this, "onShow", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };


    /**
     * 数据处理好之后，通过VoiceListener返回。
     * 然后通过IRemoteVoice，将我们转换好的数据格式，返回给暴风。
     */
    class Listener implements VoiceListener {

        private IRemoteFeedback iRemoteVoice;
        private String asr_text;

        private void setIRemoteVoice(IRemoteFeedback iRemoteVoice, String asr) {
            this.iRemoteVoice = iRemoteVoice;
            this.asr_text = asr;
        }

        public void destroy() {
            this.iRemoteVoice = null;
        }

        @Override
        public void callback(CommandReturn command) {
            LogPrint.e(TAG, TAG + "Listener callback mIsHandled : " + command.mIsHandled + " ,Action : " + command.mAction);
            try {
                Feedback feed = new Feedback();
                feed.isHasResult = command.mIsHandled;
                switch (command.mAction) {
                    case CommandReturn.TYPE_TTS_PLAY:
                        if (!TextUtils.isEmpty(command.mMessage)) {
                            TTS tts = new TTS();
                            tts.pck = AppInfo.getPackageName();
                            tts.tts = command.mMessage;
                            tts.userTxt = asr_text;
                            tts.isDisplayLayout = command.showUI;
                            TellManager.getInstance().tts(CoreApplication.getApplication(), tts);
                        }
                        break;
                    default:
                        break;
                }

                if (iRemoteVoice != null) {
                    iRemoteVoice.feedback(feed);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void searchResult(String data) {
            LogPrint.e(TAG, TAG + ".Listener.searchResult");
            try {
                Feedback feed = new Feedback();
                feed.isHasResult = false;
                if (iRemoteVoice != null) {
                    iRemoteVoice.feedback(feed);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }
}
