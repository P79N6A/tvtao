package com.tvtaobao.voicesdk.register;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.tvtaobao.voicesdk.base.SDKInitConfig;
import com.tvtaobao.voicesdk.register.interfaces.ITVTaoRegister;
import com.tvtaobao.voicesdk.register.bo.Register;
import com.tvtaobao.voicesdk.register.type.RegisterType;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     author : pan
 *     QQ : 1065475118
 *     time   : 2018/03/26
 *     desc   : Language Points Registration
 *              语点注册
 *     version: 1.0
 * </pre>
 */

public class LPR {
    private static String TAG = "LPR";

    private static LPR lpr;

    private Register register;

    private ServiceConnection mRegistedConn;
    private ITVTaoRegister mTVTaoRegister;
    private Context context;
    private String packageName;
    private boolean needRegister = false;
    private KDXFRegister kdxfRegister;

    private RegisterListener registerListener;

    private LPR() {
        kdxfRegister = new KDXFRegister();
    }

    public static LPR getInstance() {
        if (lpr == null) {
            synchronized (LPR.class) {
                if (lpr == null) {
                    lpr = new LPR();
                }
            }
        }
        return lpr;
    }

    /**
     * @param packageName 语音助手的包名
     */
    public void init(String packageName) {
        this.packageName = packageName;
        this.needRegister = SDKInitConfig.needRegister();
        LogPrint.i(TAG, "packageName " + packageName + " ,needRegister : " + needRegister);

        bindService(null, true);
    }

    /**
     * 页面注册
     *
     * @param context
     */
    public void registed(Context context) {
        this.context = context;
        register = new Register();
        register.className = context.getClass().getName();
        registerRequest(register);
    }

    /**
     * 动态注册。
     *
     * @param register
     */
    public void registed(Register register) {
        if (register == null) {
            return;
        }

        registerRequest(register);
    }

    public void unregistered() {
        if(register!=null){
            register.resgistedType = RegisterType.RELIEVE;
        }
        if (needRegister&&register!=null) {
            sendRegister(register, false);
        }
        if(kdxfRegister!=null){
            kdxfRegister.release();
        }
        register = null;
    }

    /**
     * 将语点注册
     *
     * @param register
     * @param isRegister 是否注册，true 注册；false 解注册
     */
    public void sendRegister(Register register, boolean isRegister) {
        LogPrint.i(TAG, TAG + ".sendRegister register : " + register + " ,isRegister : " + isRegister);
        //如果mTVTaoRegister为空，先去绑定一下
        if (mTVTaoRegister == null) {
            bindService(register, isRegister);
        } else {
            try {
                if (isRegister) {
                    mTVTaoRegister.onRegister(register);
                } else {
                    mTVTaoRegister.unRegister(register);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 语点注册请求，获取页面语点数据，发送给SDK。
     *
     * @param register
     */
    public void registerRequest(Register register) {
        if (registerListener == null) {
            registerListener = new RegisterListener();
        }
        registerListener.setRegister(register);
        BusinessRequest.getBusinessRequest().requestVoiceRegister(SDKInitConfig.getCurrentPage(),
                register.className, register.resgistedType, register.getParams(), registerListener);
    }


   public void release() {
        context = null;
   }

    /**
     * 绑定SDK语点注册服务。
     */
    private void bindService(final Register register, final boolean isRegister) {
        if (!needRegister) {
            return;
        }

        //mTVTaoRegister不为空，说明已经绑定了，不需要再去绑定一下。
        if (mTVTaoRegister != null) {
            return;
        }

        LogPrint.i(TAG, TAG + ".bindService mRegistedConn : " + mRegistedConn);
        //初始化连接
        if (mRegistedConn == null) {
            mRegistedConn = new ServiceConnection() {

                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    LogPrint.i(TAG, "onServiceConnected");
                    mTVTaoRegister = ITVTaoRegister.Stub.asInterface(iBinder);

                    if (register == null) {
                        return;
                    }

                    try {
                        if (isRegister) {
                            mTVTaoRegister.onRegister(register);
                        } else {
                            mTVTaoRegister.unRegister(register);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    LogPrint.i(TAG, "onServiceDisconnected");
                    mTVTaoRegister = null;
                }
            };
        }

        //绑定服务
        Intent serviceIntent = new Intent();
        serviceIntent.setPackage(packageName);
        serviceIntent.setAction("ACTION.TVTAO.AIDL.LPR");
        Context appContext = CoreApplication.getApplication();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext.startForegroundService(serviceIntent);
        }
        appContext.bindService(serviceIntent, mRegistedConn, Context.BIND_AUTO_CREATE);
    }

    private class RegisterListener implements RequestListener<JSONObject> {
        private Register register;

        public void setRegister(Register register) {
            this.register = register;
        }

        @Override
        public void onRequestDone(JSONObject data, int resultCode, String msg) {
            LogPrint.i(TAG, "RegisterListener data : " + data);
            if (resultCode == 200) {
                try {
                    JSONArray keys = data.getJSONArray("asrs");
                    if (keys == null || keys.length() == 0) {
                        return;
                    }

                    //执行科大讯飞的语点注册操作
                    kdxfRegister.onRegister(register.className, keys);

                    if (!needRegister) {
                        return;
                    }

//                    String[] ss = keys.toString().split(",");
                    String[] registedArray = new String[keys.length()];
                    for (int i = 0; i < keys.length(); i++) {
                        registedArray[i] = keys.getString(i);
                    }

                    register.registedArray = registedArray.clone();
                    sendRegister(register, true);
                    LogPrint.i(TAG, "RegisterListener registedArray : " + Arrays.toString(registedArray));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                LogPrint.e(TAG, "RegisterListener resultCode : " + resultCode + " ,msg : " + msg);
            }
        }
    }
}
