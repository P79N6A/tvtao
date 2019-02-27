package com.yunos.tvtaobao.payment.alipay;

import com.ali.auth.third.core.model.Session;
import com.yunos.tvtaobao.payment.CustomLoginFragment;

import java.lang.ref.WeakReference;

/**
 * Created by rca on 20/12/2017.
 */

public class AlipayTaskListener {
    public interface LoginCallback {
        void onLoginSuccess(Session session);

        void onLoginFailure(int code, String message);
    }

    private static WeakReference<LoginCallback> loginCallbackWeakReference;

    public static void registerLoginCallback(LoginCallback callback) {
        loginCallbackWeakReference = callback == null ? null : new WeakReference<LoginCallback>(callback);
    }

    public static void unregisterLoginCallback() {
        registerLoginCallback(null);
    }

    public static void notifyLoginSuccess(Session session) {
        if (loginCallbackWeakReference != null && loginCallbackWeakReference.get() != null)
            loginCallbackWeakReference.get().onLoginSuccess(session);
    }

    public static void notifyLoginFailure(int code, String msg) {
        if (loginCallbackWeakReference != null && loginCallbackWeakReference.get() != null)
            loginCallbackWeakReference.get().onLoginFailure(code, msg);
    }
}
