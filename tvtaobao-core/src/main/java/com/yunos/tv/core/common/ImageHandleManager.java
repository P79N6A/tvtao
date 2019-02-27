package com.yunos.tv.core.common;


import android.content.Context;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageHandleManager {

    private final String TAG = "ImageHandleManager";

    private static ImageHandleManager mImageHandleManager = null;
    private ThreadPoolExecutor mThreadPoolExecutor = null;

    /**
     * 获取图片处理管理
     * @param context
     * @return
     */
    public static ImageHandleManager getImageHandleManager(Context context) {
        if (mImageHandleManager == null) {
            mImageHandleManager = new ImageHandleManager(context.getApplicationContext());
        }
        return mImageHandleManager;
    }

    /**
     * 清空线程池的任务
     */
    public synchronized void purge() {
        if (mThreadPoolExecutor != null) {
            mThreadPoolExecutor.purge();
        }
    }

    /**
     * 停掉线程池
     */
    public synchronized void shutdown() {
        if (mThreadPoolExecutor != null) {
            mThreadPoolExecutor.shutdownNow();
        }
    }

    /**
     * 提交任务
     * @param runnable
     */
    public synchronized void executeTask(Runnable runnable) {

        if (runnable == null) {
            return;
        }
        if (mThreadPoolExecutor == null) {
            onInitThreadPoolExecutor();
        }
        mThreadPoolExecutor.execute(runnable);
    }

    private ImageHandleManager(Context context) {
        onInitThreadPoolExecutor();
    }

    private synchronized void onInitThreadPoolExecutor() {
        AppDebug.d(TAG, "onInitThreadPoolExecutor ;  mThreadPoolExecutor = " + mThreadPoolExecutor);
        if (mThreadPoolExecutor != null) {
            return;
        }

        mThreadPoolExecutor = new ThreadPoolExecutor(0, 2, 30L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(), createThreadFactory());
        mThreadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());

        AppDebug.i(TAG, "onInitThreadPoolExecutor ： run initialize ok");
    }

    // 创建工厂
    private ThreadFactory createThreadFactory() {
        return new ImageHandleManagerThreadFactory();
    }

    private class ImageHandleManagerThreadFactory implements ThreadFactory {

        private final AtomicInteger poolNumber = new AtomicInteger(1);

        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private final int threadPriority;

        ImageHandleManagerThreadFactory() {
            this.threadPriority = Thread.NORM_PRIORITY - 2;
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "ImageHandleManager: pool-" + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            t.setPriority(threadPriority);
            return t;
        }
    }
}
