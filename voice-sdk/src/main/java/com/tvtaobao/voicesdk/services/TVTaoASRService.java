package com.tvtaobao.voicesdk.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.tvtao.sdk.voice.ITVTaoCallBack;
import com.tvtao.sdk.voice.ITVTaoInterface;
import com.tvtaobao.voicesdk.ASRInput;
import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.bo.CommandReturn;
import com.tvtaobao.voicesdk.interfaces.VoiceListener;
import com.tvtaobao.voicesdk.utils.LogPrint;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2017/09/18
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class TVTaoASRService extends Service {
    private String TAG = "TVTaoASRService";
    private ASRInput asrInput;

    private Listener mListener;

    @Override
    public void onCreate() {
        super.onCreate();
        LogPrint.i(TAG, "onCreate");
        asrInput = ASRInput.getInstance();
        asrInput.setContext(this);
        mListener = new Listener();
        ASRNotify.getInstance().setFeedBack(mListener);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LogPrint.i(TAG, "Android Version -> " + android.os.Build.VERSION.SDK_INT);
            NotificationChannel channel = new NotificationChannel("tvtaobao", "TVTAOBAO",
                    NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);

            Notification notification = new Notification.Builder(getApplicationContext(), "tvtaobao").build();
            startForeground(1, notification);

//            startForeground(0, new Notification());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogPrint.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        asrInput.destroy();
    }

    private IBinder mBinder = new ITVTaoInterface.Stub() {


        @Override
        public void nlpRequest(String asr, String searchConfig, String json, ITVTaoCallBack callback) throws RemoteException {
            LogPrint.i(TAG, "nlpRequest asr : " + asr + " ,searchConfig : " + searchConfig + " ,json : " + json);
            mListener.destroy();
            mListener.setITVTaoCallBack(asr, callback);
            asrInput.setMessage(asr, searchConfig, json, mListener);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    class Listener implements VoiceListener {

        private String _asr;
        private ITVTaoCallBack mCallBack;

        public void setITVTaoCallBack(String asr, ITVTaoCallBack callBack) {
            this._asr = asr;
            this.mCallBack = callBack;
        }

        public void destroy() {
            this._asr = null;
            this.mCallBack = null;
        }

        @Override
        public void callback(CommandReturn command) {
            LogPrint.d(TAG, "VoiceListener.callback");
            try {
                LogPrint.d(TAG, "VoiceListener.callback : " + mCallBack);
                command.mASRMessage = _asr;

                if (mCallBack != null)
                    mCallBack.callback(command.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void searchResult(String data) {
            LogPrint.d(TAG, "VoiceListener.searchResult");
            try {
                if (mCallBack != null)
                    mCallBack.searchResult(_asr, data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
