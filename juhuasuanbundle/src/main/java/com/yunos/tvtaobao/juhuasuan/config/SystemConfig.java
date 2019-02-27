package com.yunos.tvtaobao.juhuasuan.config;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import com.yunos.tvtaobao.juhuasuan.util.SDCardUtil;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.DeviceJudge;
import com.yunos.tv.core.common.DeviceJudge.MemoryType;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.Config;

/**
 * 系统配置
 * @author hanqi
 * @date 2013-12-19
 */
public class SystemConfig {

    public static final String HTTP_PARAMS_ENCODING = "UTF-8";

    public static Float DENSITY;// 屏幕密度比例 如0.75 1.0

    public static Integer DENSITY_DPI;// 屏幕密度 如120 160 240

    public static Integer SCREEN_WIDTH;// 屏幕宽度 单位为像素

    public static Integer SCREEN_HEIGHT;// 屏幕高度 单位为像素

    private static Boolean hasInited = false;

    // 渠道id
    public static String CHANNEL_ID = Config.getChannel();

    // 版本数字
    public static Integer APP_VERSION_NUMBER;

    // 版本字符
    public static String APP_VERSION;

    // 终端标识 默认值
    public static String TTID = CHANNEL_ID + "@tvjuhuasuan_yunos";
    //下单专用TTID,当用户开通过快捷支付，就不会再发短信
    public static String SECUREPAY_TTID = CHANNEL_ID + "@tvjuhuasuan_alitv";
    //    public static String SECUREPAY_TTID = "961528@juhuasuan_alios";

    // 终端类型
    public static final String TERMINAL_TYPE = "YUNOS";

    // 老版支付最终版本号，大于这个版本的支付就走新版支付流程
    public static final int OLD_SECURE_PAY_FINAL_VERSION = 30;

    // 是否启用积分抵用功能
    public static final boolean USEMALLPOINTS_MODULE = false;
    //是否是低配机，低配机将会禁用大部分动画功能
    public static boolean DIPEI_BOX = false;
    //是否启用TV购功能，低配时关闭
    public static final boolean TVGOU_ENABLED = false;
    //是否启用推荐功能，需要系统支持OPENGL,低配时请需要关闭
    public static final boolean RECOMMENT_ENABLED = false;
    //是否启用全部功能
    public static final boolean QUANBU_ENABLED = false;

    //多久之后数据自动刷新
    public static final Long RElOADDATA_TIME = 10800000L; //3小时：3*3600*1000 单位:毫秒

    /**
     * 程序启动时动态获取，优先选择sd卡，然后是应用文件目录。
     */
    private static String FILE_ROOT_PATH = "/sdcard";
    /**
     * 根目录下聚划算文件夹
     */
    private static final String FILE_JU_PATH = "/juhuasuan";
    /**
     * 缓存
     */
    private static final String FILE_JU_CACHE_PATH = "/cache";
    /**
     * 图片缓存
     */
    private static final String FILE_JU_CACHE_IMG_PATH = "/img";
    /**
     * 用户数据缓存
     */
    private static final String FILE_JU_CACHE_USER_PATH = "/user";

    private static final String FILE_JU_UPDATE_PATH = "/update";

    private static Context appContext;

    public static void init(Context context) {
        AppDebug.i("SystemConfig", "SystemConfig.init");
        if (hasInited) {
            return;
        }
        if (context == null) {
            throw new IllegalArgumentException("The context was null");
        }
        appContext = context.getApplicationContext();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        DENSITY = dm.density;
        DENSITY_DPI = dm.densityDpi;
        SCREEN_WIDTH = dm.widthPixels;
        SCREEN_HEIGHT = dm.heightPixels;

        // 读取版本号
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            APP_VERSION_NUMBER = info.versionCode;
            APP_VERSION = info.versionName;
        } catch (Exception e) {
            Log.e("SysConfig-getAppVersion", "读取版本号异常: " + e.toString());
        }
        TTID += "_" + APP_VERSION;
        SECUREPAY_TTID += "_" + APP_VERSION;
        //        if (SystemConfig.DIPEI_BOX) {
        //            TTID += "L";
        //        }
        hasInited = true;

        MemoryType memoryType = DeviceJudge.getMemoryType();
        if (memoryType == MemoryType.LowMemoryDevice) {
            DIPEI_BOX = true;
        }
    }

    // public static void main(String[] args) {
    // String r = URLDecoder
    // .decode("http://api.waptest.taobao.com/rest/api3.do?t=1349660769&sign=e7ee331b13325f9d7d1bc93fb2cc285f&appVersion=1.2.1&data=%7B%22checkCode%22%3A%22%22%2C%22checkCodeId%22%3A%22%22%2C%22password%22%3A%22180d76c362b79e0db62e0754f6880fbce93d5e00f6be144545b512ae3540%22%2C%22appKey%22%3A%224272%22%2C%22token%22%3A%22c91790f0408e9ba5d2c7dcd132de61dc%22%2C%22topToken%22%3A%22d8382ff73e8daa4ce0a1439ad199ae73%22%2C%22username%22%3A%22%E9%9C%8D%E6%81%A9%E6%B1%89%E5%AD%97%22%7D&ttid=201200%40juhuasuan_iphone_1.2.1&v=v2&api=com.taobao.client.sys.login&imei=C468B739F7EEAE4847BFB9BBE4CEE425&imsi=C468B739F7EEAE4847BFB9BBE4CEE425&appKey=4272&authType=md5");
    // System.out.println(r);
    // }

    /**
     * 形如sdcard/juhuasuan 末尾没有斜杠
     * @return
     */
    private static String getJuHuaSuanRootPath() {
        if (SDCardUtil.isSdCardWritable()) {
            FILE_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            FILE_ROOT_PATH = appContext.getFilesDir().getAbsolutePath();
        }
        return FILE_ROOT_PATH + FILE_JU_PATH;
    }

    /**
     * 形如sdcard/juhuasuan/cache 末尾没有斜杠
     * @return
     */
    public static String getJuCacheFilePath() {
        return getJuHuaSuanRootPath() + FILE_JU_CACHE_PATH;
    }

    /**
     * 形如：sdcard/juhuasuan/cache/img 末尾没有斜杠
     * @return
     */
    public static String getJuCacheImgFilePath() {
        return getJuCacheFilePath() + FILE_JU_CACHE_IMG_PATH;
    }

    /**
     * 形如：sdcard/juhuasuan/cache/user 末尾没有斜杠
     * @return
     */
    public static String getJuCacheUserFilePath() {
        return getJuCacheFilePath() + FILE_JU_CACHE_USER_PATH;
    }

    public static String getJuUpdatePath() {
        return getJuCacheFilePath() + FILE_JU_UPDATE_PATH;
    }

    public static String getJuSysInfo() {
        return Build.BRAND + " " + Build.MODEL + " " + Build.VERSION.RELEASE + ",app:" + APP_VERSION + " " + CHANNEL_ID;
    }
}
