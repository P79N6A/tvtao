/**
 * 
 */
package com.yunos.tv.core.common;


import android.content.Context;
import android.widget.ImageView;

import com.tvlife.imageloader.cache.disc.naming.Md5FileNameGenerator;
import com.tvlife.imageloader.cache.memory.impl.LruMemoryCache;
import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.ImageLoader;
import com.tvlife.imageloader.core.ImageLoaderConfiguration;
import com.tvlife.imageloader.core.listener.ImageLoadingListener;
import com.yunos.tv.core.config.Config;

/**
 * 图片加载管理器 :[请不要修改此类,包括添加或删除接口]
 * 【 此 类 配对的 图片下载JAR 包版本是：1.1.1, jar包文件名是 TVLifeImageLoader-ver1.1.1.jar 】
 * 注意： 在使用TVLifeImageLoader 加载 图片时， 如果 滚动 的情况下
 * A：建立队列，用来保存URL。
 * B：在屏幕中需要显示的图片[指的是 在界面可见区域内]URL添加到队列中
 * C：如果滚出界面可见范围，那么从队列中移除此URL
 * D：当界面处于停止时，检查队列，并请求图片。
 * 这样做，可避免网络图片下载的阻塞
 * @author yunzhong.qyz
 */
public class ImageLoaderManager {

    private static ImageLoaderManager mImageLoaderManager;
    private ImageLoader mImageLoaderWorker;
    private boolean mEnableDebugLogs = Config.isDebug();

    /**
     * 图片下载器显示配置
     * @author yunzhong.qyz
     */
    public static class OptionConfig {

        // 在SD卡缓冲,
        // 此版本的ImageLoad 已经关闭了SD卡的加载，所以此设置的值 true 或者 false 都是无效的。
        // 关闭SD卡缓冲的原因主要是有时加载图片不可靠，容易报错，产生空白的数据
        // 此问题会在后期版本中进行修复，如果确实要使用SD卡缓冲，请开发者自行实现
        public static boolean CACHE_ONDISC = false;

        // 在内存中缓冲，如果设为 true , 建议 采用 LruMemoryCache 进行内存缓冲
        // 如果设为 false， 那么 开发者自己去实现图片在内存中的缓冲
        // [或者说： 如果想自己实现图片在内存中的缓冲管理，那么把此值设为false]
        public static boolean CACHE_INMEMORY = true;

    }

    /**
     * 图片下载器全局下载配置
     * @author yunzhong.qyz
     */

    public static class ImageLoaderConfig {

        // 缓冲在内存中的大小，此处最好是根据分配率进行配置
        public static int MEMORYCACHE_LIMIT_SIZE = 10 * 1024 * 1024;
        public static int MEMORYCACHE_LIMIT_SIZE_LOW = 2 * 1024 * 1024;

        // 缓冲在硬盘中的大小
        public static int DISCCACHE_MAX_SIZE = 50 * 1024 * 1024;

        public static void setMemorycacheLimitSize(int size) {

        }

    }

    /**
     * 图片下载的管理类
     * @param context
     * @return
     */
    public static ImageLoaderManager getImageLoaderManager(Context context) {
        if (mImageLoaderManager == null) {
            mImageLoaderManager = new ImageLoaderManager(context.getApplicationContext());
        }
        return mImageLoaderManager;
    }

    /**
     * 显示图片
     * @param url
     * @param imageView
     *            此接口应尽量少使用，后期版本中会以
     *            displayImage(String url, ImageView imageView, DisplayImageOptions option,
     *            ImageLoadingListener listener)
     *            替换
     */
    public void displayImage(String url, ImageView imageView) {
        checkImageLoaderInstance();
        mImageLoaderWorker.displayImage(url, imageView);

    }

    /**
     * 显示图片
     * @param url
     * @param imageView
     * @param option
     *            此接口应尽量少使用，后期版本中会以
     *            displayImage(String url, ImageView imageView, DisplayImageOptions option,
     *            ImageLoadingListener listener)
     *            替换
     */
    public void displayImage(String url, ImageView imageView, DisplayImageOptions option) {
        checkImageLoaderInstance();
        mImageLoaderWorker.displayImage(url, imageView, option);
    }

    /**
     * @param url
     * @param imageView
     * @param listener
     *            此接口应尽量少使用，后期版本中会以
     *            displayImage(String url, ImageView imageView, DisplayImageOptions option,
     *            ImageLoadingListener listener)
     *            替换
     */
    public void displayImage(String url, ImageView imageView, ImageLoadingListener listener) {
        checkImageLoaderInstance();
        mImageLoaderWorker.displayImage(url, imageView, listener);
    }

    /**
     * 显示图片
     * @param url
     * @param imageView
     * @param option
     * @param listener
     */
    public void displayImage(String url, ImageView imageView, DisplayImageOptions option, ImageLoadingListener listener) {
        checkImageLoaderInstance();
        mImageLoaderWorker.displayImage(url, imageView, option, listener);
    }

    /**
     * 加载图片
     * @param url
     * @param listener
     */
    public void loadImage(String url, ImageLoadingListener listener) {
        checkImageLoaderInstance();

        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder().cacheOnDisc(OptionConfig.CACHE_ONDISC)
                .cacheInMemory(OptionConfig.CACHE_INMEMORY).build();

        mImageLoaderWorker.loadImage(url, imageOptions, listener);
    }

    /**
     * 加载图片
     * @param url
     * @param imageView
     * @param listener
     */
    public void loadImage(String url, ImageView imageView, ImageLoadingListener listener) {
        checkImageLoaderInstance();
        mImageLoaderWorker.displayImage(url, imageView, listener);
    }

    /**
     * @param url
     * @param options
     * @param listener
     */
    public void loadImage(String url, DisplayImageOptions options, ImageLoadingListener listener) {

        if (options == null) {
            throw new NullPointerException("options is null!");
        }

        checkImageLoaderInstance();
        mImageLoaderWorker.loadImage(url, options, listener);
    }

    /**
     * 加载图片,并传入文件名
     * @param url
     * @param name
     * @param option
     * @param listener
     */
    public void loadImage(String url, String name, DisplayImageOptions option, ImageLoadingListener listener) {
        checkImageLoaderInstance();
        mImageLoaderWorker.loadImage(url, name, option, listener);
    }

    /**
     * 停止图片加载
     */
    public void stop() {
        checkImageLoaderInstance();
        mImageLoaderWorker.stop();
    }

    /**
     * 清空硬盘
     * 请谨慎使用，因为在清空硬盘，可能会出现卡死，或者其他的现象
     * 此问题同样会在后期版本中修复
     */
    public void clearDiscCache() {
        checkImageLoaderInstance();
        mImageLoaderWorker.clearDiscCache();
    }

    /**
     * 清空内存
     */
    public void clearMemoryCache() {
        checkImageLoaderInstance();
        mImageLoaderWorker.clearMemoryCache();
    }

    /**
     * 取消 在 imageView 中显示图片
     * @param imageView
     */
    public void cancelDisplayTask(ImageView imageView) {
        checkImageLoaderInstance();
        mImageLoaderWorker.cancelDisplayTask(imageView);
    }

    /**
     * 取消图片下载
     * 此方法可以结合队列使用
     * 注意： 当 调用 图片 显示方法时，再调用此方法可能会无效；
     * 原因： 当执行显示方法，或加载方法时，图片下载可能立即就执行了，执行之后再去调用，必然会无效
     * @param url
     */
    public void cancelLoadTask(String url) {
        checkImageLoaderInstance();
        mImageLoaderWorker.cancelLoadTaskFor(url);
    }

    /**
     * 取消全部的图片下载
     */
    public void cancelLoadAllTaskFor() {
        checkImageLoaderInstance();
        mImageLoaderWorker.cancelLoadAllTaskFor();
    }

    /**
     * 设置是否需要开启 LOG
     * @param enable
     */
    public void setWriteDebugLogs(boolean enable) {
        mEnableDebugLogs = enable;
    }

    /*****************************************************************************************************************
     * 定义的私有方法，请不要改动********************************************
     ****************************************************************************************************************/

    /**
     * 构造方法
     * @param context
     */
    private ImageLoaderManager(Context context) {

        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder().cacheOnDisc(OptionConfig.CACHE_ONDISC)
                .cacheInMemory(OptionConfig.CACHE_INMEMORY).build();

        //低内存设备使用低配置
        int memory = ImageLoaderConfig.MEMORYCACHE_LIMIT_SIZE;
        if (! DeviceJudge.MemoryType.HighMemoryDevice.equals(DeviceJudge.getMemoryType())) {
            memory = ImageLoaderConfig.MEMORYCACHE_LIMIT_SIZE_LOW;
        }
        int cacheSize = Math.min(memory, (int)Runtime.getRuntime().maxMemory() / 10);

        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context.getApplicationContext())
                .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory().discCacheFileNameGenerator(new Md5FileNameGenerator())
                .memoryCache(new LruMemoryCache(cacheSize))
                .discCacheSize(ImageLoaderConfig.DISCCACHE_MAX_SIZE)
                .discCacheFileFileName("main")
                .defaultDisplayImageOptions(imageOptions);

        if (mEnableDebugLogs) {
            builder.writeDebugLogs();
            AppDebug.i("ImageLoaderManager", "cacheSize = " + cacheSize);
        }

        ImageLoaderConfiguration config = builder.build();

        getLoaderInstance().init(config);

    }

    /**
     * 检查 图片下载器的实例
     */
    private void checkImageLoaderInstance() {
        getLoaderInstance();
    }

    /**
     * 取得图片载器
     * @return
     */
    public ImageLoader getLoaderInstance() {
        if (mImageLoaderWorker == null) {
            mImageLoaderWorker = ImageLoader.getInstance();
        }
        return mImageLoaderWorker;
    }

}
