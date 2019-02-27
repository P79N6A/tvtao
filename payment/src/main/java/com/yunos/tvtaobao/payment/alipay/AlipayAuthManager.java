package com.yunos.tvtaobao.payment.alipay;


/**
 * Created by rca on 20/12/2017.
 * Tasks to manage: Alipay authorizing; Alipay authorizing and logging in; Taobao QR logging in;
 */

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.ali.auth.third.core.MemberSDK;
import com.ali.auth.third.core.callback.LoginCallback;
import com.ali.auth.third.core.model.Session;
import com.ali.auth.third.offline.login.LoginService;
import com.yunos.tvtaobao.payment.alipay.task.AlipayAuthCheckTask;
import com.yunos.tvtaobao.payment.alipay.task.AlipayAuthLoginTask;
import com.yunos.tvtaobao.payment.alipay.task.AlipayAuthTask;

import java.util.concurrent.Executor;

public class AlipayAuthManager {

    private static Context sContext;

    public interface AuthListener {
        void onAuthQrGenerated(String qrUrl);

        void onAuthSuccess();

        void onAuthFailure();
    }

    public interface AuthLoginListener {
        void onAuthQrGenerated(String qrUrl);

        void onAuthSuccess();

        void onAuthFailure();

        void onAuthLoginResult(boolean success, Session session);
    }

    public interface AuthCheckListener {
        void onAuthCheckResult(boolean isAuth, String alipayId);
    }


    /**
     * init
     *
     * @param context
     */
    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    private static AlipayAuthTask authTask;

    private static AlipayAuthCheckTask authCheckTask;

    private static AlipayAuthLoginTask authLoginTask;

    public static void authCheck(final AuthCheckListener listener) {
        if (authCheckTask != null) {
            authCheckTask.cancel(true);
        }
        authCheckTask = new AlipayAuthCheckTask() {
            @Override
            protected void onPostExecute(AlipayAuthCheckResult result) {
                super.onPostExecute(result);
                if (listener != null) {
                    listener.onAuthCheckResult(result.auth, result.alipayId);
                }
            }
        };
        authCheckTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void doAuth(String alipayId, final AuthListener listener) {
        if (authTask != null) {
            authTask.cancel(true);
        }
        authTask = new AlipayAuthTask(sContext);
        authTask.setAlipayUserId(alipayId);
        authTask.setListener(new AlipayAuthTask.AlipayAuthTaskListener() {
            @Override
            public void onReceivedAlipayAuthStateNotify(AlipayAuthTask.AlipayAuthTaskResult result) {
                if (result.getStep() == AlipayAuthTask.STEP_GEN) {
                    if (listener != null) {
                        String qrCode = (result.getObject() instanceof String) ? (String) result.getObject() : null;
                        listener.onAuthQrGenerated(qrCode);
                    }
                } else if (result.getStep() == AlipayAuthTask.STEP_QUERY) {
                    if (listener != null) {
                        listener.onAuthSuccess();
                    }
                }
            }

        });
        authTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void doAuthLogin(final AuthLoginListener listener) {
        if (authLoginTask != null) {
            authLoginTask.cancel(true);
        }
        authLoginTask = new AlipayAuthLoginTask(sContext);
        authLoginTask.setListener(new AlipayAuthLoginTask.AlipayAuthTaskListener() {
            @Override
            public void onReceivedAlipayAuthStateNotify(AlipayAuthLoginTask.AlipayAuthTaskResult result) {
                if (result.getStep() == AlipayAuthLoginTask.STEP_GEN) {
                    if (listener != null) {
                        String qrCode = (result.getObject() instanceof String) ? (String) result.getObject() : null;
                        listener.onAuthQrGenerated(qrCode);
                    }
                } else if (result.getStep() == AlipayAuthLoginTask.STEP_QUERY) {
                    if (listener != null) {
                        listener.onAuthSuccess();
                    }
                } else if (result.getStep() == AlipayAuthLoginTask.STEP_LOGIN) {
                    if (listener != null) {
                        if (result.getStatus() == AlipayAuthLoginTask.STATUS_SUCCESS && result.getObject() instanceof Session) {
                            listener.onAuthLoginResult(true, (Session) result.getObject());
                        } else {
                            listener.onAuthLoginResult(false, null);
                        }
                    }
                }
            }
        });
        authLoginTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void dispose() {
        if (authTask != null) {
            authTask.setListener(null);
            authTask.cancel(true);
        }
        authTask = null;

        if (authCheckTask != null) {
            authCheckTask.cancel(true);
        }
        authCheckTask = null;

        if (authLoginTask != null) {
            authLoginTask.cancel(true);
        }
        authLoginTask = null;
    }
}
