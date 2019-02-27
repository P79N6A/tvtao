package com.tvtaobao.voicesdk.base;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.tvtaobao.voicesdk.bo.CommandReturn;
import com.tvtaobao.voicesdk.interfaces.VoiceListener;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.tvtaobao.blitz.account.AccountActivityHelper;

import java.lang.ref.WeakReference;

/**
 * Created by panbeixing on 2018/4/16.
 */

public abstract class BaseDeal {
    private final String TAG = "BaseDeal";

    protected WeakReference<Service> mWeakService;
    protected WeakReference<VoiceListener> mWeakListener;

    private AccountActivityHelper mAccountActivityHelper;
    private AccountActivityHelper.OnAccountStateChangedListener mOnAccountStateChangedListener;

    protected BaseDeal() {
        mAccountActivityHelper = new AccountActivityHelper();
    }

    /**
     * 设置一个service对象，用来进行intent
     *
     * @param service
     */
    public void setContext(Service service) {
        mWeakService = new WeakReference<>(service);
    }

    /**
     * 启动登录页面
     */
    public void startLoginActivity() {
        mAccountActivityHelper.startAccountActivity("tvtaobao_voice", true);
        onTTS("请先打开手机淘宝扫码登陆，登陆后再试一下");
    }

    /**
     * 注册登录的状态监听
     */
    protected void registerLoginListener() {
        if (mOnAccountStateChangedListener == null) {
            mOnAccountStateChangedListener = new AccountActivityHelper.OnAccountStateChangedListener() {

                @Override
                public void onAccountStateChanged(AccountActivityHelper.AccountLoginState state) {
                    switch (state) {
                        case LOGIN:
                            loginSuccess();
                            LogPrint.e(TAG, "登录=========");
                            break;
                        case LOGOUT:
                            LogPrint.e(TAG, "登出=========");
                            break;
                        case CANCEL:
                            LogPrint.e(TAG, "取消登录=========");
                            break;
                        default:
                            break;
                    }
                }
            };
            mAccountActivityHelper.registerAccountActivity(mOnAccountStateChangedListener);
        }
    }

    public void loginSuccess() {

    }

    /**
     * 删除注册登录的状态监听
     */
    protected void unRegisterLoginListener() {
        if (mOnAccountStateChangedListener != null) {
            mAccountActivityHelper.unRegisterAccountActivity(mOnAccountStateChangedListener);
        }
    }

    /**
     * 跳转页面
     *
     * @param uri
     */
    public void gotoActivity(String uri) {
        if (mWeakService != null && mWeakService.get() != null) {
            Intent intent = new Intent();
            intent.setData(Uri.parse(uri));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mWeakService.get().startActivity(intent);

            alreadyDeal("正在为您跳转");
        } else {
            notDeal();
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
        if (mWeakListener != null && mWeakListener.get() != null) {
            CommandReturn commandReturn = new CommandReturn();
            commandReturn.mIsHandled = false;
            mWeakListener.get().callback(commandReturn);
        }
    }

    protected void alreadyDeal() {
        alreadyDeal(null);
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


}
