package com.tvtaobao.voice.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.bftv.fui.thirdparty.IUserStatusNotice;
import com.bftv.fui.thirdparty.InterceptionData;
import com.bftv.fui.thirdparty.RecyclingData;
import com.tvtaobao.voice.VoiceUtils;
import com.yunos.voice.utils.LogPrint;

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

    private VoiceUtils mVoice;
    @Override
    public void onCreate() {
        super.onCreate();
        mVoice = VoiceUtils.getInstance();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    private IUserStatusNotice.Stub stub = new IUserStatusNotice.Stub() {
        @Override
        public void onInterception(InterceptionData interceptionData) throws RemoteException {
            LogPrint.e("Less", "onInterception:" + interceptionData.toString());
            mVoice.handleInput(interceptionData.needValue, null, null);
        }

        @Override
        public void onRecyclingNotice(RecyclingData recyclingData) throws RemoteException {
            LogPrint.e("Less", "onRecyclingNotice 回收数据通知" + recyclingData.toString());
        }

        @Override
        public void onAsr(final String s, int i, int i1) throws RemoteException {
            LogPrint.e("Less", "onAsr 用户说完话了:" + s + " ,i : " + i + " ,i1 : " + i1);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BftvService.this,"用户说完话了" + s, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onShow(boolean b) throws RemoteException {
            LogPrint.e("Less", "onShow b : " + b);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BftvService.this,"onShow", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
}
