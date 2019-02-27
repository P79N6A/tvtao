package com.yunos.tvtaobao.biz.request.core;


import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.ali.auth.third.offline.login.util.LoginStatus;
import com.yunos.tv.blitz.account.LoginHelper;
import com.yunos.tv.core.AppInitializer;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.pay.LoginHelperImpl;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tvtaobao.payment.MemberSDKLoginStatus;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncDataLoader {

    private static final String TAG = "AsyncDataLoader";

    protected static ThreadPoolExecutor threadPool;

    /**
     * 延迟初始化，同时，静态变量声明跟随app，线程池关闭后便不可用，须再次初始化。
     */
    protected static synchronized void initialize() {
        if (threadPool != null) {
            return;
        }
        threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
        // 一旦线程数大于核心池大小且处于闲置状态,会被立即回收掉
        //        threadPool = new ThreadPoolExecutor(1, 5, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        //        
        //        // 设置保护策略;当队列任务已经满了,或者任务提交到一个已经关闭的executor,则丢弃该任务;<br>
        //        // 默认的保护策略是AbortPolicy,会抛出RejectedExecutionException
        //        threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
    }

    /**
     * 停用池(只要app还在threadPool就不会)
     */
    public static void shutdown() {
        if (threadPool != null) {
            threadPool.shutdownNow();
            threadPool = null;
        }
    }

    /**
     * 清理线程池中的任务
     */
    public static void purge() {
        if (threadPool != null) {
            threadPool.purge();
        }
    }

    /**
     * 退出
     *
     * @throws InterruptedException
     */
    public static void waitTerminationAndExit() throws InterruptedException {
        if (threadPool != null) {
            if (!threadPool.isShutdown()) {
                threadPool.shutdownNow();
            }
            threadPool.awaitTermination(3, TimeUnit.SECONDS);
            threadPool = null;
        }
    }

    /**
     * 加载数据（需要自动登录）
     */
    public static <T> void execute(final DataLoadCallback<ServiceResponse<T>> dataLoadCallback) {
        AsyncDataLoader.execute(null, dataLoadCallback, null, null);
    }

    protected static <T> void execute(final Context context, final DataLoadCallback<ServiceResponse<T>> dataLoadCallback,
                                      final ServiceResponse.RequestErrorListener networkErrorListener,
                                      final ServiceResponse.RequestErrorListener loginErrorListener) {
        AppDebug.d(TAG, " execute ;  threadPool = " + threadPool);
        try {

            dataLoadCallback.preExecute();
            // 网络无连接直接报错
            if (!NetWorkUtil.isNetWorkAvailable()) {
                AppDebug.d(TAG, " execute ;  network not available ");
                ServiceResponse<T> response = new ServiceResponse<T>();
                if (networkErrorListener != null) {
                    response.addErrorListener(networkErrorListener);
                }
                response.update(ServiceCode.NET_WORK_ERROR);
                dataLoadCallback.postExecute(response);
                return;
            }

            if (threadPool == null) {
                initialize();
            }
            final Handler handler = new MyHandler<T>(dataLoadCallback, loginErrorListener);

            AppDebug.v(TAG, handler.toString());
            if (!threadPool.isShutdown()) {
                threadPool.execute(new MyRunnable<T>(handler, dataLoadCallback));

            }
        } catch (Exception e1) {
            e1.printStackTrace();
            AppDebug.e(TAG, e1.getMessage());
        }
    }

    /**
     * 加载数据(不需要自动登录)
     *
     * @author tianxiang
     * @date 2012-10-11下午5:06:24
     */
    public static <T> void executeWithNoAutoLogin(final DataLoadCallback<ServiceResponse<T>> dataLoadCallback) {
        AppDebug.d(TAG, " executeWithNoAutoLogin ;  threadPool = " + threadPool);
        try {
            dataLoadCallback.preExecute();
            if (!NetWorkUtil.isNetWorkAvailable()) {
                ServiceResponse<T> response = new ServiceResponse<T>();
                response.update(ServiceCode.NET_WORK_ERROR);
                dataLoadCallback.postExecute(response);
                return;
            }

            if (threadPool == null) {
                initialize();
            }

            final Handler handler = new MyHandler<T>(dataLoadCallback);

            if (threadPool != null && !threadPool.isShutdown()) {
                threadPool.execute(new MyRunnableNoLogin(handler, dataLoadCallback));
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            AppDebug.e(TAG, e1.getMessage());
        }
    }

    /**
     * 使用线程池处理
     *
     * @param runnable
     */
    public static void executeProcess(Runnable runnable) {
        if (threadPool == null) {
            initialize();
        }

        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.execute(runnable);
        }
    }

    /**
     * 回调接口定义
     *
     * @author tianxiang
     * @date 2012-10-11 下午5:12:52
     */
    public interface DataLoadCallback<T> {

        /**
         * 加载数据
         *
         * @date 2012-10-12下午2:09:59
         */
        T load();

        /**
         * 在load前执行
         *
         * @date 2012-11-22上午8:36:08
         */
        void preExecute();

        /**
         * 数据加载完成后的回调，一般用于界面更新
         *
         * @date 2012-11-22上午8:36:20
         */
        void postExecute(T t);

        /**
         * 当调起登录时回调，用于accountActivityHelper状态切换
         */
        void onStartLogin();

    }

    private static class MyHandler<T> extends Handler {

        private DataLoadCallback<ServiceResponse<T>> callback;
        private final ServiceResponse.RequestErrorListener requestErrorListener;

        public MyHandler(DataLoadCallback<ServiceResponse<T>> callback,
                         ServiceResponse.RequestErrorListener loginErrorRef) {
            this.callback = callback;
            this.requestErrorListener = loginErrorRef;
        }

        public MyHandler(DataLoadCallback<ServiceResponse<T>> callbackRef) {
            this.callback = callbackRef;
            this.requestErrorListener = null;
        }

        @SuppressWarnings("unchecked")
        public void handleMessage(Message message) {

            switch (message.what) {
                // 处理完成
                case 0:
                    try {
                        ServiceResponse<T> sr = (ServiceResponse<T>) message.obj;
                        callback.postExecute(sr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    callback = null; // 这里所有的处理已经完成， callback已经没有用了。 下一个请求会new一个handler
                    break;
                // 重新登录
                case 1:
                    AppDebug.i(TAG, TAG + ".relogin");
                    final Handler loginHandler = this;
                    final LoginHelper helper = CoreApplication.getLoginHelper(CoreApplication.getApplication());
                    callback.onStartLogin();
                    LoginHelper.SyncLoginListener synLoginListener = new LoginHelper.SyncLoginListener() {

                        @Override
                        public void onLogin(boolean isSuccess) {
                            CoreApplication.getLoginHelper(CoreApplication.getApplication()).removeSyncLoginListener(this);
                            AppDebug.i(TAG,
                                    "onLogin isSuccess=" + isSuccess + ", getSessionId = " + helper.getSessionId());

                            if (isSuccess) {
                                if (!threadPool.isShutdown()) {
                                    threadPool.execute(new Runnable() {

                                        @Override
                                        public void run() {
                                            // 登录成功后再次请求数据
                                            AppDebug.v(TAG,
                                                    TAG + ".registerSessionInfo, getSessionId = " + helper.getSessionId());
//                                            AsyncDataLoader.clearWebkitCookie();
                                            AppInitializer.getMtopInstance().registerSessionInfo(helper.getSessionId(),
                                                    helper.getUserId());
                                            ServiceResponse<T> sr = callback.load();
                                            loginHandler.sendMessage(loginHandler.obtainMessage(0, sr));
                                        }
                                    });
                                }
                            } else {
                                // 登录失败返回错误
                                ServiceResponse<T> sr = new ServiceResponse<T>();
                                sr.update(ServiceCode.CLIENT_LOGIN_ERROR);

                                if (requestErrorListener != null)
                                    sr.addErrorListener(requestErrorListener);
                                callback.postExecute(sr);
                            }
                        }
                    };
                    CoreApplication.getLoginHelper(CoreApplication.getApplication()).addSyncLoginListener(synLoginListener);
                    if(helper instanceof LoginHelperImpl)
                        helper.startYunosAccountActivity(CoreApplication.getApplication(), true);
                    else
                        helper.login(CoreApplication.getApplication());
//                    CoreApplication.getLoginHelper(CoreApplication.getApplication()).login(CoreApplication.getApplication());
                    break;
                default:
                    break;
            }
        }
    }

    private static class MyRunnable<T> implements Runnable {

        private final Handler handler;
        private final DataLoadCallback<ServiceResponse<T>> callback;

        public MyRunnable(Handler handler, DataLoadCallback<ServiceResponse<T>> callback) {
            this.handler = handler;
            this.callback = callback;
        }

        @Override
        public void run() {
            //增加系统登录态判断，读取的是本地数据，无需网络请求,防止广播异常
            while (Config.isAgreementPay() && MemberSDKLoginStatus.isLoggingOut()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            try {
//                TYIDManager mTYIDManager = TYIDManager.get(CoreApplication.getApplication());
//                loginState = mTYIDManager.yunosGetLoginState();
//            } catch (Exception e) {
//                AppDebug.e(TAG, "asyncDataLoader get login state exception:" + e);
//            }
//
//            AppDebug.i(TAG, TAG + ".isLogined = " + User.isLogined() + ".loginState = " + loginState);
            try {
                if (!CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
                    if (!Config.isAgreementPay() || !MemberSDKLoginStatus.isLoggingOut()) {
                        handler.sendEmptyMessage(1);
                        return;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            if (callback != null) {
                ServiceResponse<T> sr = new ServiceResponse<T>();
                try {
                    sr = callback.load();
                } catch (Exception e) {
                    e.printStackTrace();
                    AppDebug.e(TAG, "asyncDataLoader callback.load exception:" + e);
                    sr.update(ServiceCode.DATA_PARSE_ERROR);
                }

                // 如果登录失败马上请求登录
                if (sr != null) {
                    if (!sr.isSucess()) {
                        AppDebug.i(TAG,
                                TAG + ".isNotLogin = " + sr.isNotLogin() + ".isSessionTimeout = "
                                        + sr.isSessionTimeout());
                        if (sr.isNotLogin() || sr.isSessionTimeout()) {
                            handler.sendEmptyMessage(1);
                            return;
                        }
                    }
                }
                handler.sendMessage(handler.obtainMessage(0, sr));
            }

        }
    }

    private static class MyHandlerNoLogin<T> extends Handler {

        private final DataLoadCallback<ServiceResponse<T>> callback;

        public MyHandlerNoLogin(DataLoadCallback<ServiceResponse<T>> callback) {
            this.callback = callback;
        }

        @SuppressWarnings("unchecked")
        public void handleMessage(Message message) {
            try {
                callback.postExecute((ServiceResponse<T>) message.obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class MyRunnableNoLogin<T> implements Runnable {

        private final Handler handler;
        private DataLoadCallback<ServiceResponse<T>> callback;

        public MyRunnableNoLogin(Handler handler, DataLoadCallback<ServiceResponse<T>> callbackRef) {
            this.handler = handler;
            this.callback = callbackRef;
        }

        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage(0, callback.load()));
            // Runnable will contains by a Queue, it will save the reference until flush out.
            // so nullfiy the the callback
            this.callback = null;
        }
    }

//    /**
//     * 清除webkit的缓存
//     */
//    public static void clearWebkitCookie() {
//        CookieManager.setup(CoreApplication.mContext);
//        if (CookieManager.webkitCookMgr != null) {// 请求这个接口前清除所有的cookie，因为该接口会先验证cookie中的sid，导致验证不通过
//            CookieManager.webkitCookMgr.removeSessionCookie();
//            CookieManager.webkitCookMgr.removeAllCookie();
//        }
//    }

}
