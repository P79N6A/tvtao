/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.tvlife.imageloader.core;


import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.graphics.Bitmap;

import com.tvlife.imageloader.cache.disc.DiscCacheAware;
import com.tvlife.imageloader.cache.disc.impl.FileCountLimitedDiscCache;
import com.tvlife.imageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.tvlife.imageloader.cache.disc.impl.UnlimitedDiscCache;
import com.tvlife.imageloader.cache.disc.naming.FileNameGenerator;
import com.tvlife.imageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.tvlife.imageloader.cache.memory.MemoryCacheAware;
import com.tvlife.imageloader.cache.memory.impl.LruMemoryCache;
import com.tvlife.imageloader.core.assist.QueueProcessingType;
import com.tvlife.imageloader.core.assist.deque.LIFOLinkedBlockingDeque;
import com.tvlife.imageloader.core.decode.BaseImageDecoder;
import com.tvlife.imageloader.core.decode.ImageDecoder;
import com.tvlife.imageloader.core.display.BitmapDisplayer;
import com.tvlife.imageloader.core.display.SimpleBitmapDisplayer;
import com.tvlife.imageloader.core.download.BaseImageDownloader;
import com.tvlife.imageloader.core.download.ImageDownloader;
import com.tvlife.imageloader.utils.StorageUtils;

/**
 * Factory for providing of default options for
 * {@linkplain ImageLoaderConfiguration configuration}
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.5.6
 */
public class DefaultConfigurationFactory {
    
    // DiscCacheAware
    
    static final String CREAT_DISCCACHEAWARE = "create  cache  file  [%s]";

    /** Creates default implementation of task executor */
    public static Executor createExecutor(
            String name, int threadPoolSize, int threadPriority,
            QueueProcessingType tasksProcessingType) {
        boolean lifo = tasksProcessingType == QueueProcessingType.LIFO;
        
        BlockingQueue<Runnable> taskQueue = lifo ? new LIFOLinkedBlockingDeque<Runnable>()
                : new LinkedBlockingQueue<Runnable>();

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS,
                taskQueue, createThreadFactory(name, threadPriority));
        
        threadPoolExecutor
                .setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

        return threadPoolExecutor;
    }

    /**
     * Creates {@linkplain HashCodeFileNameGenerator default implementation} of
     * FileNameGenerator
     */
    public static FileNameGenerator createFileNameGenerator() {
        return new HashCodeFileNameGenerator();
    }

    /**
     * Creates default implementation of {@link DiscCacheAware} depends on
     * incoming parameters
     */
    public static DiscCacheAware createDiscCache(
            Context context, FileNameGenerator discCacheFileNameGenerator,
            int discCacheSize, int discCacheFileCount,  String filename) {
        
   
        if (discCacheSize > 0) {
            File individualCacheDir = StorageUtils
                    .getIndividualCacheDirectory(context, filename); 
            
            return new TotalSizeLimitedDiscCache(individualCacheDir,
                    discCacheFileNameGenerator, discCacheSize);
        }
        else if (discCacheFileCount > 0) {
            File individualCacheDir = StorageUtils
                    .getIndividualCacheDirectory(context, filename); 
            
            return new FileCountLimitedDiscCache(individualCacheDir,
                    discCacheFileNameGenerator, discCacheFileCount);
        }
        else {
            File cacheDir = StorageUtils.getCacheDirectory(context); 
            
            return new UnlimitedDiscCache(cacheDir, discCacheFileNameGenerator);
        }
    }

    /**
     * Creates reserve disc cache which will be used if primary disc cache
     * becomes unavailable
     */
    public static DiscCacheAware createReserveDiscCache(File cacheDir) {
        File individualDir = new File(cacheDir, "uil-images");
        if (individualDir.exists() || individualDir.mkdir()) {
            cacheDir = individualDir;
        }
        return new TotalSizeLimitedDiscCache(cacheDir, 2 * 1024 * 1024); // limit
                                                                         // - 2
                                                                         // Mb
    }

    /**
     * Creates default implementation of {@link MemoryCacheAware} -
     * {@link LruMemoryCache}<br />
     * Default cache size = 1/8 of available app memory.
     */
    public static MemoryCacheAware<String, Bitmap> createMemoryCache(
            int memoryCacheSize) {
        if (memoryCacheSize == 0) {
            memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);
        }
        return new LruMemoryCache(memoryCacheSize);
    }

    /**
     * Creates default implementation of {@link ImageDownloader} -
     * {@link BaseImageDownloader}
     */
    public static ImageDownloader createImageDownloader(Context context) {
        return new BaseImageDownloader(context);
    }

    /**
     * Creates default implementation of {@link ImageDecoder} -
     * {@link BaseImageDecoder}
     */
    public static ImageDecoder createImageDecoder(boolean loggingEnabled) {
        return new BaseImageDecoder(loggingEnabled);
    }

    /**
     * Creates default implementation of {@link BitmapDisplayer} -
     * {@link SimpleBitmapDisplayer}
     */
    public static BitmapDisplayer createBitmapDisplayer() {
        return new SimpleBitmapDisplayer();
    }

    /**
     * Creates default implementation of {@linkplain ThreadFactory thread
     * factory} for task executor
     */
    private static ThreadFactory createThreadFactory(
            String name, int threadPriority) {
        return new DefaultThreadFactory(name, threadPriority);
    }

    private static class DefaultThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);

        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private final int threadPriority;

        DefaultThreadFactory(String name, int threadPriority) {
            this.threadPriority = threadPriority;
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
                    .getThreadGroup();
            namePrefix = "Load image from " + name + ": pool-"
                    + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix
                    + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            t.setPriority(threadPriority);
            return t;
        }
    }

    /**
     * 创建一个提交任意任务的处理工厂
     * 
     * @author yunzhong.qyz
     */
    public static class CachedThreadPoolThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        CachedThreadPoolThreadFactory(String name) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
                    .getThreadGroup();
            namePrefix = name + "：pool-" + poolNumber.getAndIncrement()
                    + "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix
                    + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
