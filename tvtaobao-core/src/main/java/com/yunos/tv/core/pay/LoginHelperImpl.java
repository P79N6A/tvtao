package com.yunos.tv.core.pay;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.ali.auth.third.core.MemberSDK;
import com.ali.auth.third.core.callback.LoginCallback;
import com.ali.auth.third.core.config.ConfigManager;
import com.ali.auth.third.core.model.Session;
import com.ali.auth.third.core.service.impl.CredentialManager;
import com.ali.auth.third.offline.login.LoginService;
import com.ali.auth.third.offline.login.callback.LogoutCallback;
import com.ali.auth.third.offline.login.util.LoginStatus;
import com.ali.auth.third.offline.webview.AuthWebView;
import com.yunos.tv.blitz.account.LoginHelper;
import com.yunos.tv.blitz.global.BzAppConfig;
import com.yunos.tv.blitz.global.BzAppMain;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.common.User;
import com.yunos.tvtaobao.payment.MemberSDKLoginHelper;
import com.yunos.tvtaobao.payment.MemberSDKLoginStatus;
import com.yunos.tvtaobao.payment.alipay.AlipayTaskListener;
import com.yunos.tvtaobao.payment.alipay.request.ReleaseContractRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import mtopsdk.mtop.common.ApiID;
import mtopsdk.mtop.common.DefaultMtopCallback;
import mtopsdk.mtop.common.MtopFinishEvent;
import mtopsdk.mtop.global.SDKUtils;
import mtopsdk.mtop.intf.Mtop;

public class LoginHelperImpl implements LoginHelper, MemberSDKLoginHelper, AlipayTaskListener.LoginCallback {

    private static final String TAG = "LoginHelper";

    private boolean forceLogout = false;

    private FrequentLock mFrequentLock;
    private static LoginHelperImpl mJuLoginHelper;
    public static Context mRegistedContext;

    public static final String MEMBER_LOGIN_BROADCAST_ACTION = "com.membersSDK.login";
    public static final String MEMBER_LOGOUT_BROADCAST_ACTION = "com.membersSDK.loginOut";

    private SparseArray<LoginHelper.SyncLoginListener> mSyncLoginListenerMap;

    private SparseArray<MemberSDKLoginHelper.SyncLoginListener> mMemberSyncLoginListenerMap;

    private Handler mHandler;
    private SparseArray<LoginHelper.SyncLoginListener> mReceiveLoginListenerMap;
    private SparseArray<MemberSDKLoginHelper.SyncLoginListener> mMemberReceiveLoginListenerMap;
    private String mNewestToken = "";//

    private String nick;
    private BroadcastReceiver mLoginListenerReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MEMBER_LOGIN_BROADCAST_ACTION)) {
//                userLoginout();
//                //切换账号时候只发登录广播，所以先清除用户信息
//                User.clearUser();
                // 遍历所有注册登录的监听方法
                int listenerSize = mReceiveLoginListenerMap.size();
                for (int i = 0; i < listenerSize; i++) {
                    int key = mReceiveLoginListenerMap.keyAt(i);
                    LoginHelper.SyncLoginListener listener = mReceiveLoginListenerMap.get(key);
                    if (listener != null) {
                        listener.onLogin(true);
                    }
                }

                int memberListenerSize = mMemberReceiveLoginListenerMap.size();
                for (int i = 0; i < memberListenerSize; i++) {
                    int key = mMemberReceiveLoginListenerMap.keyAt(i);
                    MemberSDKLoginHelper.SyncLoginListener listener = mMemberReceiveLoginListenerMap.get(key);
                    if (listener != null) {
                        listener.onLogin(true);
                    }
                }

            } else if (intent.getAction().equals(MEMBER_LOGOUT_BROADCAST_ACTION)) {
                userLoginout();
                //清除用户信息
                User.clearUser();
                // 遍历所有注册登录的监听方法
                int listenerSize = mReceiveLoginListenerMap.size();
                for (int i = 0; i < listenerSize; i++) {
                    int key = mReceiveLoginListenerMap.keyAt(i);
                    LoginHelper.SyncLoginListener listener = mReceiveLoginListenerMap.get(key);
                    if (listener != null) {
                        listener.onLogin(false);
                    }
                }

                int memberListenerSize = mMemberReceiveLoginListenerMap.size();
                for (int i = 0; i < memberListenerSize; i++) {
                    int key = mMemberReceiveLoginListenerMap.keyAt(i);
                    MemberSDKLoginHelper.SyncLoginListener listener = mMemberReceiveLoginListenerMap.get(key);
                    if (listener != null) {
                        listener.onLogin(false);
                    }
                }
            }
        }
    };

    // 注销已经登录的会话信息，包括登录的sid和ecode信息和UserId
    private void userLoginout() {
        BzAppMain.mMtopInstance.logout();
        SDKUtils.logOut();
    }

    /**
     * 构造方法
     *
     * @param context
     */
    private LoginHelperImpl(Context context) {
        mSyncLoginListenerMap = new SparseArray<LoginHelper.SyncLoginListener>();
        mReceiveLoginListenerMap = new SparseArray<LoginHelper.SyncLoginListener>();
        mMemberReceiveLoginListenerMap = new SparseArray<>();
        mMemberSyncLoginListenerMap = new SparseArray<>();
        mFrequentLock = new FrequentLock();
        mHandler = new Handler(Looper.getMainLooper());
        int sharedPref = SharePreferences.getInt("wv_support", 0);
        AppDebug.d("LoginHelper", "wvsupport: " + sharedPref);
//        if (sharedPref != 1) {
        try {
//                SharePreferences.put("wv_support", 1);
            Class.forName(AuthWebView.class.getName());
//                AuthWebView webView = new AuthWebView(context);
//                webView.destroy();
//                SharePreferences.put("wv_support", 2);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        }
        boolean transf = SharePreferences.getBoolean("account_tr", false);
        if (!transf) {
            AppDebug.d(TAG, "Loginhelperimpl trytotransfer");
            tryToTransferAccount();
        }
        AlipayTaskListener.registerLoginCallback(this);
        registerLoginListener(context);
    }

    private void tryToTransferAccount() {
//        final TYIDManagerCallback<Bundle> callback = new TYIDManagerCallback<Bundle>() {
//            @Override
//            public void run(TYIDManagerFuture<Bundle> tyidManagerFuture) {
//                Bundle result;
//                try {
//                    result = tyidManagerFuture.getResult();
//                    int retCode = result.getInt(TYIDConstants.KEY_CODE);
//                    AppDebug.d(TAG, "retCode=" + retCode);
//                    if (retCode == 200) {
//                        //回调得到token
//                        String data = result.getString(TYIDConstants.YUNOS_RAW_DATA);
//                        AppDebug.i(TAG, TAG + ".mTYIDManagerCallback.data:" + data);
//                        JSONObject constant = new JSONObject(data);
//                        String sid = constant.optString("sid");
//                        AppDebug.d(TAG, "get sid:" + sid);
//                        ConfigManager.getInstance().setSsoToken(sid);
//                        mHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                ssoLogin();
//                            }
//                        }, 2000);
//                    } else {
//                        SharePreferences.put("account_tr", true);
//                    }
//                } catch (Exception e) {
//                    AppDebug.d(TAG, "yunosApplyMtopToken error e=" + e);
//                    SharePreferences.put("account_tr", true);
//                }
//            }
//        };
//
//        AppDebug.d(TAG, "Loginhelperimpl applynewmtop");
//        TYIDManagerWrapper.get(CoreApplication.getApplication(), new TYIDManagerWrapper.IServiceConnectStatus() {
//            @Override
//            public void onServiceConnectStatus(int status) {
//                if (status == 1) {
//                    try {
//                        TYIDManagerWrapper.get(CoreApplication.getApplication()).yunosApplyNewMtopToken(Config.getTTid(), Config.getAppKey(), "yunostvtaobao",
//                                CoreApplication.getDeviceId(), callback, mHandler);
//
//                    } catch (Exception e1) {
//                        AppDebug.e(TAG, "yunosApplyNewMtopToken TYIDException error e1=" + e1);
//                        SharePreferences.put("account_tr", true);
//                    }
//                }
//            }
//        });

    }

    /**
     * 取得引用单例
     *
     * @param context (可以为null，前提是mJuLoginHelper != null)
     * @return JuLoginHelper
     */
    public static LoginHelperImpl getJuLoginHelper() {
        if (mJuLoginHelper == null) {
            synchronized (LoginHelperImpl.class) {
                if (mJuLoginHelper == null) {
                    mJuLoginHelper = new LoginHelperImpl(CoreApplication.getApplication());
                }
            }
        }
        return mJuLoginHelper;
    }

    /**
     * 销毁
     */
    public void destroy() {
        // 清除
        mSyncLoginListenerMap.clear();
        mReceiveLoginListenerMap.clear();
        mMemberSyncLoginListenerMap.clear();
        mMemberReceiveLoginListenerMap.clear();
        try {
            CredentialManager.INSTANCE.logout();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        unregisterLoginListener();
    }

    /**
     * 添加监听登录方法
     *
     * @param listener
     */
    public void addSyncLoginListener(LoginHelper.SyncLoginListener listener) {
        if (listener != null) {
            mSyncLoginListenerMap.put(listener.hashCode(), listener);
        }
    }

    public void addSyncLoginListener(MemberSDKLoginHelper.SyncLoginListener listener) {
        if (listener != null) {
            mMemberSyncLoginListenerMap.put(listener.hashCode(), listener);
        }
    }

    /**
     * 移除监听登录监听方法
     *
     * @param listener
     */
    public void removeSyncLoginListener(LoginHelper.SyncLoginListener listener) {
        if (listener != null) {
            mSyncLoginListenerMap.remove(listener.hashCode());
        }
    }

    public void removeSyncLoginListener(MemberSDKLoginHelper.SyncLoginListener listener) {
        if (listener != null) {
            mMemberSyncLoginListenerMap.remove(listener.hashCode());
        }
    }

    /**
     * 注册登录的监听方法需要在主线程里面注册
     *
     * @param context
     */
    public void registerLoginListener(Context context) {
        unregisterLoginListener();
        mRegistedContext = context;
        IntentFilter intentFilter = new IntentFilter(MEMBER_LOGIN_BROADCAST_ACTION);
        intentFilter.addAction(MEMBER_LOGOUT_BROADCAST_ACTION);
        mRegistedContext.registerReceiver(mLoginListenerReceiver, intentFilter);
    }

    public Context getRegistedContext() {
        return mRegistedContext;
    }

    /**
     * 取消注册的登录监听方法
     */
    public void unregisterLoginListener() {
        if (mRegistedContext != null) {
            mRegistedContext.unregisterReceiver(mLoginListenerReceiver);
            mRegistedContext = null;
        }
    }

    /**
     * 增加注册监听登录方法
     *
     * @param listener
     */
    public void addReceiveLoginListener(LoginHelper.SyncLoginListener listener) {
        if (listener != null) {
            mReceiveLoginListenerMap.put(listener.hashCode(), listener);
        }
    }

    public void addReceiveLoginListener(MemberSDKLoginHelper.SyncLoginListener listener) {
        if (listener != null) {
            mMemberReceiveLoginListenerMap.put(listener.hashCode(), listener);
        }
    }

    /**
     * 删除注册监听登录方法
     *
     * @param listener
     */
    public void removeReceiveLoginListener(LoginHelper.SyncLoginListener listener) {
        if (listener != null) {
            mReceiveLoginListenerMap.remove(listener.hashCode());
        }
    }

    public void removeReceiveLoginListener(MemberSDKLoginHelper.SyncLoginListener listener) {
        if (listener != null) {
            mMemberReceiveLoginListenerMap.remove(listener.hashCode());
        }
    }

    /**
     * 判断用户是否登陆
     *
     * @return
     */
    public boolean isLogin() {
//        try {
//            TYIDManager mTYIDManager = TYIDManager.get(BzAppConfig.context.getContext());
//            if(mTYIDManager == null){
//            	return false;
//            }
//            int loginState = mTYIDManager.yunosGetLoginState();
//            if (loginState == 200) {
//                return true;
//            }
//        } catch (TYIDException e) {
//            e.printStackTrace();
//            BzDebugLog.e(TAG, TAG + ".isLogin e = " + e);
//        }

        try {
            LoginService service = MemberSDK.getService(LoginService.class);
            return service == null ? false : service.checkSessionValid();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private LoginCallback autoLoginCallback = new LoginCallback() {
        @Override
        public void onSuccess(Session session) {
            mFrequentLock.clearLoack();
            SharePreferences.put("account_tr", true);
            ConfigManager.getInstance().setSsoToken((String) null);
            User.updateUser(session);
            nick = session.nick;
            if (mSyncLoginListenerMap != null) {
                int listenerSize = mSyncLoginListenerMap.size();
                for (int i = 0; i < listenerSize; i++) {
                    int key = mSyncLoginListenerMap.keyAt(i);
                    LoginHelper.SyncLoginListener listener = mSyncLoginListenerMap.get(key);
                    if (listener != null) {
                        listener.onLogin(true);
                    }
                }
            }

            if (mMemberSyncLoginListenerMap != null) {
                int listenerSize = mMemberSyncLoginListenerMap.size();
                for (int i = 0; i < listenerSize; i++) {
                    int key = mMemberSyncLoginListenerMap.keyAt(i);
                    MemberSDKLoginHelper.SyncLoginListener listener = mMemberSyncLoginListenerMap.get(key);
                    if (listener != null) {
                        listener.onLogin(true);
                    }
                }
            }
        }

        @Override
        public void onFailure(int code, String msg) {
            mFrequentLock.clearLoack();
            if (mSyncLoginListenerMap != null) {
                int listenerSize = mSyncLoginListenerMap.size();
                for (int i = 0; i < listenerSize; i++) {
                    int key = mSyncLoginListenerMap.keyAt(i);
                    LoginHelper.SyncLoginListener listener = mSyncLoginListenerMap.get(key);
                    if (listener != null) {
                        listener.onLogin(false);
                    }
                }
            }
            if (mMemberSyncLoginListenerMap != null) {
                int listenerSize = mMemberSyncLoginListenerMap.size();
                for (int i = 0; i < listenerSize; i++) {
                    int key = mMemberSyncLoginListenerMap.keyAt(i);
                    MemberSDKLoginHelper.SyncLoginListener listener = mMemberSyncLoginListenerMap.get(key);
                    if (listener != null) {
                        listener.onLogin(false);
                    }
                }
            }
        }
    };

    /**
     * 登录
     */
    public void login(final Context context) {//todo 此处逻辑需要修改与原先逻辑一致，原先逻辑仅做尝试刷行用户登录状态，不作登录操作
//        TYIDManager mTYIDManager = null;
        // 避免在一定时间内频繁调用

        if (mFrequentLock != null && mFrequentLock.isLock()) {
            Log.i(TAG, "login isLock = " + mFrequentLock.isLock());
            return;
        }

        Log.i(TAG, "login islogginout " + MemberSDKLoginStatus.isLoggingOut());
        if (MemberSDKLoginStatus.isLoggingOut()) {
            return;
        }

        final LoginService service = MemberSDK.getService(LoginService.class);
        if (service == null)
            return;
        service.autoLogin(autoLoginCallback);
//        try {
//            Method autoLogin = clz.getDeclaredMethod("a", LoginCallback.class);
//            autoLogin.setAccessible(true);
//            autoLogin.invoke(service, autoLoginCallback);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        service.auth(new LoginCallback() {
//            @Override
//            public void onSuccess(Session session) {
//                onLoginSuccess(session);
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//                onLoginFailure(i, s);
//            }
//        });
    }

    /**
     * 帐号迁移登录
     */
    public void ssoLogin() {//todo 此处逻辑需要修改与原先逻辑一致，原先逻辑仅做尝试刷行用户登录状态，不作登录操作

        if (mFrequentLock != null && mFrequentLock.isLock()) {
        }
        final LoginService service = MemberSDK.getService(LoginService.class);
        if (service == null)
            return;
        Class clz = service.getClass();

        try {//fixme
            Method ssoTokenLogin = clz.getDeclaredMethod("ssoTokenLogin", LoginCallback.class);
            ssoTokenLogin.setAccessible(true);
            ssoTokenLogin.invoke(service, autoLoginCallback);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    /**
     * 启动账号登录界面
     */
    public void startYunosAccountActivity(final Context context, boolean ifChangeAccount) {

        //如果是更换账号不设置回跳地址,停留在账号页面
//        Intent intent = new Intent();
//        intent.setData(Uri.parse("tvtaobao://home?module=login"));
////        intent.setClassName("com.yunos.account", "com.yunos.account.AccountLoginIndex");
//        if (!ifChangeAccount) {
//            intent.putExtra("from", context.getApplicationInfo().packageName);
//        }
//        context.startActivity(intent);
        if (MemberSDKLoginStatus.compareAndSetLogin(false, true)) {
            LoginService service = MemberSDK.getService(LoginService.class);
            if(service==null){
                return;
            }
            service.auth(new LoginCallback() {
                @Override
                public void onSuccess(Session session) {
                    onLoginSuccess(session);
                    sendBroadcastLogin(context, true);
                    MemberSDKLoginStatus.compareAndSetLogin(true, false);
                }

                @Override
                public void onFailure(int i, String s) {
                    onLoginFailure(i, s);
                    MemberSDKLoginStatus.compareAndSetLogin(true, false);
                }
            });
        }


    }


    public void logout(final Context context) {
        LoginService service = MemberSDK.getService(LoginService.class);
        service.logout(new LogoutCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, " memberSDK loginOut succsss");
                Toast.makeText(context, "成功登出", Toast.LENGTH_SHORT).show();
                sendBroadcastLogin(context, false);
            }

            @Override
            public void onFailure(int i, String s) {
                Log.d(TAG, " memberSDK loginOut failure");
            }
        });

        ReleaseContractRequest request = new ReleaseContractRequest();
        ApiID id = Mtop.instance(context).build(request, null).useWua().addListener(new DefaultMtopCallback() {
            @Override
            public void onFinished(MtopFinishEvent event, Object context) {
                Log.d("test", "releaseContract response: " + event.getMtopResponse().getDataJsonObject());
                super.onFinished(event, context);
            }
        }).asyncRequest();

    }

    @Override
    public String getNick() {
        return User.getNick();
    }

    /**
     * 登录请求
     *
     * @param context
     */
    public void loginRequest(final Context context) {
        login(context);
    }

    public void sendBroadcastLogin(Context context, boolean islogin) {
        if (islogin) {
            //把要发送的广播值传入Intent对象
            Intent intent = new Intent(MEMBER_LOGIN_BROADCAST_ACTION);
            //调用Context的 sendBroadcast()方法发送广播
            context.sendBroadcast(intent);
        } else {
            //把要发送的广播值传入Intent对象
            Intent intent = new Intent(MEMBER_LOGOUT_BROADCAST_ACTION);
            //调用Context的 sendBroadcast()方法发送广播
            context.sendBroadcast(intent);
        }
    }

    @Override
    public void onLoginSuccess(final Session session) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BzAppConfig.context.getContext(), "欢迎回来，" + session.nick, Toast.LENGTH_LONG).show();
            }
        });
        User.updateUser(session);
        nick = session.nick;
        int listenerSize = mSyncLoginListenerMap.size();
        for (int i = 0; i < listenerSize; i++) {
            int key = mSyncLoginListenerMap.keyAt(i);
            LoginHelper.SyncLoginListener listener = mSyncLoginListenerMap.get(key);
            if (listener != null) {
                listener.onLogin(true);
            }
        }

        int memberListenerSize = mMemberSyncLoginListenerMap.size();
        for (int i = 0; i < memberListenerSize; i++) {
            int key = mMemberSyncLoginListenerMap.keyAt(i);
            MemberSDKLoginHelper.SyncLoginListener listener = mMemberSyncLoginListenerMap.get(key);
            if (listener != null) {
                listener.onLogin(!TextUtils.isEmpty(session.openSid));
            }
        }
        sendBroadcastLogin(BzAppConfig.context.getContext(), true);
    }

    @Override
    public void onLoginFailure(int code, String message) {
        int listenerSize = mSyncLoginListenerMap.size();
        for (int j = 0; j < listenerSize; j++) {
            int key = mSyncLoginListenerMap.keyAt(j);
            LoginHelper.SyncLoginListener listener = mSyncLoginListenerMap.get(key);
            if (listener != null) {
                listener.onLogin(false);
            }
        }

        int memberListenerSize = mMemberSyncLoginListenerMap.size();
        for (int i = 0; i < memberListenerSize; i++) {
            int key = mMemberSyncLoginListenerMap.keyAt(i);
            MemberSDKLoginHelper.SyncLoginListener listener = mMemberSyncLoginListenerMap.get(key);
            if (listener != null) {
                listener.onLogin(false);
            }
        }

    }

    class LoginBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MEMBER_LOGIN_BROADCAST_ACTION)) {
                mHandler.sendEmptyMessage(10001);
            } else if (intent.getAction().equals(MEMBER_LOGOUT_BROADCAST_ACTION)) {
                mHandler.sendEmptyMessage(10002);
            }
        }
    }


    class FrequentLock {

        private final int DELAY_TIME = 1000;
        private boolean haveLock = false;
        private long lastTimeLock = 0L;

        public boolean isLock() {
            long current = System.currentTimeMillis();
            if (haveLock && lastTimeLock > 0L) {
                if (current - lastTimeLock > DELAY_TIME) {
                    // 如果是超过DELAY_TIME这个时间，那么废弃这个标志，认为没有上锁
                    clearLoack();
                }
            }
            if (haveLock) {
                lastTimeLock = System.currentTimeMillis();
                return true;
            }

            haveLock = true;
            lastTimeLock = System.currentTimeMillis();
            return false;
        }

        public void clearLoack() {
            haveLock = false;
            lastTimeLock = 0L;
        }

    }

    @Override
    public String getSessionId() {
        return User.getSessionId();
    }

    @Override
    public String getUserId() {
        return User.getUserId();
    }
}
