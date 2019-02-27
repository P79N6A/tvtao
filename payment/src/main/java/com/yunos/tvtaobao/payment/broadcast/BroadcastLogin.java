package com.yunos.tvtaobao.payment.broadcast;

import android.content.Context;
import android.content.Intent;

/**
 * <pre>
 *     author : xutingting
 *     e-mail : xutingting@zhiping.tech
 *     time   : 2017/12/26
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class BroadcastLogin {
    private static final String MEMBER_LOGIN_BROADCAST_ACTION = "com.membersSDK.login";
    private static final String MEMBER_LOGOUT_BROADCAST_ACTION = "com.membersSDK.loginOut";

    public static void sendBroadcastLogin(Context context, boolean islogin) {
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

}
