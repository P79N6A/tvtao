package com.tvtaobao.voicesdk.control.base;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.text.TextUtils;

import com.tvtaobao.voicesdk.bo.CommandReturn;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.control.bo.ConfigVO;
import com.tvtaobao.voicesdk.interfaces.VoiceListener;
import com.tvtaobao.voicesdk.utils.DialogManager;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.blitz.account.AccountActivityHelper;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/4/18
 *     desc :
 *     version : 1.0
 * </pre>
 */

public abstract class BizBaseControl {
    protected final String TAG = BizBaseControl.class.getSimpleName();

    protected static WeakReference<Service> mWeakService;
    protected WeakReference<VoiceListener> mWeakListener;

    private AccountActivityHelper mAccountActivityHelper;
    private LoginAuthReceived loginAuthReceived;

    protected ConfigVO configVO;

    protected BizBaseControl() {
        LogPrint.e(TAG, "BizBaseControl");
        mAccountActivityHelper = new AccountActivityHelper();
    }

    public abstract void execute(DomainResultVo domainResultVO);

    /**
     * 设置一个service对象，用来进行intent
     *
     * @param service
     */
    public void init(WeakReference<Service> service, WeakReference<VoiceListener> listener) {
        this.mWeakService = service;
        this.mWeakListener = listener;
    }

    public void setConfig(ConfigVO config) {
        this.configVO = config;
    }

    /**
     * 启动登录页面
     */
    public void startLoginActivity() {
        DialogManager.getManager().dismissAllDialog();

        Intent intent = new Intent();
        intent.setClassName(AppInfo.getPackageName(), "com.yunos.voice.activity.LoginAuthActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("QRCodeType", "login");
        mWeakService.get().startActivity(intent);
        registerLoginListener();

    }

    /**
     * 注册登录的状态监听
     */
    private boolean isRegisterReceived = false;

    protected void registerLoginListener() {
        if (!isRegisterReceived) {
            isRegisterReceived = true;
            if (loginAuthReceived == null) {
                loginAuthReceived = new LoginAuthReceived();
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.yunos.tvtaobao.VOICE.ALIPAY.LOGINAUTH");
            mWeakService.get().registerReceiver(loginAuthReceived, intentFilter);
        }
    }

    public void loginSuccess() {
    }

    public void loginCancel() {
    }

    /**
     * 删除注册登录的状态监听
     */
    protected void unRegisterLoginListener() {
        mWeakService.get().unregisterReceiver(loginAuthReceived);
        isRegisterReceived = false;
    }

    public class LoginAuthReceived extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogPrint.e(TAG, "LoginAuthReceived " + intent.getAction());
            if ("com.yunos.tvtaobao.VOICE.ALIPAY.LOGINAUTH".equals(intent.getAction())
                    || "com.yunos.tvtaobao.VOICE.ALIPAY.AUTH".equals(intent.getAction())) {
                String status = intent.getStringExtra("status");
                if ("success".equals(status)) {
                    loginSuccess();
                } else {
                    loginCancel();
                }
                unRegisterLoginListener();
            }
        }
    }

    protected void onTTS(String s) {
        if (mWeakListener != null && mWeakListener.get() != null) {
            CommandReturn commandReturn = new CommandReturn();
            commandReturn.mIsHandled = true;
            commandReturn.mAction = CommandReturn.TYPE_TTS_PLAY;
            commandReturn.mMessage = s;
            mWeakListener.get().callback(commandReturn);
        } else {
            notDeal();
        }
    }

    protected void notDeal() {
        LogPrint.d(TAG, TAG + ".notDeal mWeakListener : " + mWeakListener);
        if (mWeakListener != null && mWeakListener.get() != null) {
            CommandReturn commandReturn = new CommandReturn();
            commandReturn.mIsHandled = false;
            mWeakListener.get().callback(commandReturn);

            DialogManager.getManager().dismissAllDialog();
        } else {
            LogPrint.d(TAG, TAG + ".notDeal");
        }
    }

    protected void alreadyDeal(String feedback) {
        if (mWeakListener != null && mWeakListener.get() != null) {
            CommandReturn commandReturn = new CommandReturn();
            commandReturn.mIsHandled = true;
            if (!TextUtils.isEmpty(feedback)) {
                commandReturn.mAction = CommandReturn.TYPE_FEEDBACK_INFO;
                commandReturn.mMessage = feedback;
            }
            mWeakListener.get().callback(commandReturn);
        }
    }

    /**
     * 跳转页面
     *
     * @param uri
     */
    protected void gotoActivity(String uri) {
        if (mWeakService != null && mWeakService.get() != null) {
            Intent intent = new Intent();
            intent.setData(Uri.parse(uri));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mWeakService.get().startActivity(intent);

            DialogManager.getManager().dismissAllDialog();
            alreadyDeal("正在为您跳转页面");
        } else {
            notDeal();
        }
    }

    /**
     * 获取统计最简的Properties
     *
     * @return
     */
    protected Map<String, String> getProperties() {
        return getProperties(configVO.asr_text);
    }

    protected Map<String, String> getProperties(String asr) {
        Map<String, String> p = new HashMap<String, String>();
        String uuid = CloudUUIDWrapper.getCloudUUID();
        if (!TextUtils.isEmpty(uuid)) {
            p.put("uuid", uuid);
        }
        p.put("channel", Config.getChannelName());
        if (!TextUtils.isEmpty(asr)) {
            p.put("asr", asr);
        }
        return p;
    }
}
