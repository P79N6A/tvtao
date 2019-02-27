package com.yunos.tvtaobao.payment;

import android.content.Context;

/**
 * Created by rca on 08/12/2017.
 */

public interface MemberSDKLoginHelper {
    public static final String MEMBER_LOGIN_BROADCAST_ACTION = "com.membersSDK.login";
    public static final String MEMBER_LOGOUT_BROADCAST_ACTION = "com.membersSDK.loginOut";

    interface SyncLoginListener {

        void onLogin(boolean isSuccess);
    }

    void login(final Context context);

    void startYunosAccountActivity(Context context, boolean ifChangeAccount);

    void destroy();

    Context getRegistedContext();

    /**
     * 添加监听登录方法
     *
     * @param listener
     */
    void addSyncLoginListener(SyncLoginListener listener);

    /**
     * 移除监听登录监听方法
     *
     * @param listener
     */
    public void removeSyncLoginListener(SyncLoginListener listener);

    /**
     * 注册登录的监听方法需要在主线程里面注册
     *
     * @param context
     */
    void registerLoginListener(Context context);

    /**
     * 取消注册的登录监听方法
     */
    void unregisterLoginListener();

    /**
     * 增加注册监听登录方法
     *
     * @param listener
     */
    void addReceiveLoginListener(SyncLoginListener listener);

    /**
     * 删除注册监听登录方法
     *
     * @param listener
     */
    void removeReceiveLoginListener(SyncLoginListener listener);

    /**
     * 判断用户是否登陆
     *
     * @return
     */

    boolean isLogin();

    void logout(final Context context);

    String getNick();

    String getSessionId();
}
